package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.config.RuntimeConfiguration;
import com.fourigin.argo.models.content.config.RuntimeConfigurations;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentElementsContainer;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.ContentList;
import com.fourigin.argo.models.content.elements.ListElement;
import com.fourigin.argo.models.content.elements.ContentListElementsContainer;
import com.fourigin.argo.models.content.elements.NamedElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

public final class ContentPageManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentPageManager.class);

    @SuppressWarnings("RegExpRedundantEscape")
    private static final String LIST_REFERENCE_PATTERN = "(.*)\\[(\\d)\\]$";

    private ContentPageManager() {
    }

    public static Map<String, Object> resolveConfig(ContentPage contentPage, String configName) {
        if (contentPage == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve config based on null-ContentPage!");
            return null;
        }

        if (configName == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve config based on null-name!");
            return null;
        }

        RuntimeConfigurations container = contentPage.getConfigurations();
        if (container == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("No configurations available!");
            return null;
        }

        Set<RuntimeConfiguration> configs = container.getConfigurations();
        if (configs == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("No configurations available!");
            return null;
        }

        for (RuntimeConfiguration configuration : configs) {
            if (configName.equals(configuration.getName())) {
                return configuration.getSettings();
            }
        }

        if (LOGGER.isInfoEnabled()) LOGGER.info("No configuration available for name '{}'!", configName);
        return null;
    }

    public static ContentElement resolve(ContentPage contentPage, String contentPath) {
        return resolve(contentPage, contentPath, true);
    }

    public static ContentElement resolve(ContentPage contentPage, String contentPath, boolean detach) {
        if (contentPage == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve ContentElement based on null-ContentPage!");
            return null;
        }

        if (contentPath == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve ContentElement based on null-path!");
            return null;
        }

        String path = normalizeContentPath(contentPath);

        if ("/".equals(path)) {
            // special case, return all root level elements, packed in a new pseudo group
            if (detach) {
                return new ContentGroup.Builder()
                    .withName("")
                    .withElements(contentPage.getContent())
                    .build();
            } else {
                if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to return the whole ContentPage without detaching!");
                return null;
            }
        }

        if (path.startsWith("@")) {
            if (detach) {
                Collection<DataSourceContent> dataSourceContents = contentPage.getDataSourceContents();
                if (dataSourceContents != null) {
                    int pos = path.indexOf(":");
                    String dataSourceName = path.substring(1, pos);

                    DataSourceContent match = null;
                    for (DataSourceContent dataSourceContent : dataSourceContents) {
                        if (dataSourceName.equals(dataSourceContent.getName())) {
                            match = dataSourceContent;
                            break;
                        }
                    }

                    if (match == null) {
                        if (LOGGER.isErrorEnabled())
                            LOGGER.error("No data source found for name '{}'!", dataSourceName);
                        return null;
                    }

                    path = path.substring(pos + 1);

                    if ("/".equals(path)) {
                        // special case, return all root level elements, packed in a new pseudo group
                        return new ContentGroup.Builder()
                            .withName("")
                            .withElements(match.getContent())
                            .build();
                    }

                    return resolveInternal(match.getContent(), path);
                }
            } else {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Unable to return DataSource related content without detaching!");
                return null;
            }
        }

        List<ContentElement> elements = contentPage.getContent();
        return resolveInternal(elements, path);
    }

    public static ContentElement resolve(ContentElementsContainer container, String contentPath) {
        if (container == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve ContentElement based on null-container!");
            return null;
        }

        if (contentPath == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve ContentElement based on null-path!");
            return null;
        }

        String path = contentPath.replace("//", "/").trim();

        List<ContentElement> elements = container.getElements();
        return resolveInternal(elements, path);
    }

    public static ContentElement resolveOptional(String path, List<ContentElement> elements) {
        try {
            return resolveInternal(elements, path);
        } catch (UnresolvableContentPathException ex) {
            return null;
        }
    }

    public static void update(ContentPage contentPage, String contentPath, ContentElement element) {
        Logger logger = LoggerFactory.getLogger(ContentPageManager.class);

        if (logger.isDebugEnabled())
            logger.debug("Updating element {} at path {} of page {}", element, contentPath, contentPage.getId());

        List<ContentElement> elements;

        String parentPath = normalizeContentPath(contentPath + "/..");
        if ("/".equals(parentPath)) {
            elements = contentPage.getContent();

            if (elements == null) {
                // new element
                elements = new ArrayList<>();
                contentPage.setContent(elements);
                elements.add(element);
                return;
            }
        } else {
            ContentElement parentElement = resolve(contentPage, parentPath, false);
            if (parentElement == null) {
                throw new IllegalArgumentException("Unable to find parent element for path '" + contentPath + "'!");
            }

            if (!ContentElementsContainer.class.isAssignableFrom(parentElement.getClass())) {
                throw new IllegalArgumentException("Parent element is not a container for path '" + contentPath + "'!");
            }

            ContentElementsContainer parent = ContentElementsContainer.class.cast(parentElement);
            if (logger.isDebugEnabled()) logger.debug("parent: {}", parent);

            elements = parent.getElements();
            if (elements == null) {
                // new element
                elements = new ArrayList<>();
                parent.setElements(elements);
                elements.add(element);
                return;
            }
        }

        ContentElement previousElement = null;
        int previousPosition = 0;

        if (NamedElement.class.isAssignableFrom(element.getClass())) {
            // names content element reference
            String name = ((NamedElement) element).getName();

            for (ContentElement contentElement : elements) {
                if (!NamedElement.class.isAssignableFrom(contentElement.getClass())) {
                    throw new IllegalArgumentException("Required a NamedContentElement, but found '" + contentElement.getClass().getName() + "' at content path '" + contentPath + "'");
                }
                if (name.equals(((NamedElement) contentElement).getName())) {
                    previousElement = contentElement;
                    break;
                }

                previousPosition++;
            }
        } else {
            // list content element reference

            // TODO: implement reference by number (".../blah[3]") as a last part of the content path

//            String elementReference = contentPath.substring(contentPath.lastIndexOf('/'));
//            if (logger.isDebugEnabled()) logger.debug("Element reference: {}", elementReference);

            throw new UnsupportedOperationException("Not implemented yet!");
        }

        // TODO: check if only list elements may be applied here!

        if (previousElement != null) {
            // replace the existing element
            if (logger.isDebugEnabled()) logger.debug("Replacing existing element {}", previousElement);
            elements.remove(previousElement);
        }

        if (logger.isDebugEnabled()) logger.debug("Adding a (new/modified) element {}", element);
        elements.add(previousPosition, element);
    }

    private static String normalizeContentPath(String path) {
        if (path == null) {
            return null;
        }

        path = path.replace("//", "/").trim();
        return compressContentPath(path);
    }

    private static String compressContentPath(String path) {
        if (!path.contains("/..")) {
            return path;
        }

        String[] parts = path.split("/");
        List<String> resultParts = new ArrayList<>();
        int count = 0;
        for (String part : parts) {
            if (!"..".equals(part)) {
                resultParts.add(count, part);
                count++;
                continue;
            }

            if (count == 0) {
                // invalid! ".." shouldn't stay at the begin of the path!
                continue;
            }

            // remove the last part
            String previousPart = resultParts.remove(count - 1);

            // check for index reference (for lists)
            if (previousPart.matches(LIST_REFERENCE_PATTERN)) {
                previousPart = previousPart.replaceFirst(LIST_REFERENCE_PATTERN, "$1");
                resultParts.add(count - 1, previousPart);
            } else {
                // step back
                count--;
            }
        }

        StringBuilder builder = new StringBuilder();
        for (String resultPart : resultParts) {
            if (resultPart != null && !resultPart.isEmpty()) {
                builder.append('/').append(resultPart);
            }
        }

        if (builder.length() == 0) {
            return "/";
        }

        return builder.toString();
    }

    private static ContentElement resolveInternal(List<ContentElement> elements, String path) {
        ContentElement current = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resolving path '{}' in {}", path, elements);
        }

        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();

            current = null;

            String indexReference = null;
            if (token.matches(LIST_REFERENCE_PATTERN)) {
                indexReference = token.replaceFirst(LIST_REFERENCE_PATTERN, "$2");
                token = token.replaceFirst(LIST_REFERENCE_PATTERN, "$1");
            }

            for (ContentElement element : elements) {
                if (!NamedElement.class.isAssignableFrom(element.getClass())) {
                    continue;
                }

                NamedElement namedElement = (NamedElement) element;
                if (token.equals(namedElement.getName())) {
                    if (indexReference != null) {
                        // get referenced sub-element
                        int index = Integer.parseInt(indexReference);

                        if (!ContentListElementsContainer.class.isAssignableFrom(element.getClass())) {
                            throw new UnresolvableContentPathException("Unable to resolve list reference '" + index + "'!", path);
                        }

                        List<ListElement> listElements = ((ContentListElementsContainer) element).getElements();
                        if (listElements.size() <= index) {
                            throw new UnresolvableContentPathException("Index out of range for reference '" + index + "', only " + listElements.size() + " elements available!", path);
                        }

                        current = listElements.get(index);
                    } else {
                        current = element;
                    }

                    break;
                }
            }

            if (current == null) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Unable to resolve content path '{}'! No element found for part '{}'!", path, token);
                throw new UnresolvableContentPathException("No element found for part '" + token + "'!", path);
            }

            if (!ContentElementsContainer.class.isAssignableFrom(current.getClass())) {
                if (tok.hasMoreTokens()) {
                    if (LOGGER.isInfoEnabled())
                        LOGGER.info("Unable to resolve content path '{}'! Reached the end of the element hierarchy at '{}'!", path, token);
                    throw new UnresolvableContentPathException("Reached the end of the element hierarchy at '" + token + "'!", path);
                }

                return current;
            }

            ContentElementsContainer currentContainer = ContentElementsContainer.class.cast(current);
            elements = currentContainer.getElements();
        }

        return current;
    }

    public static <T> List<T> collect(ContentPage page, java.lang.Class<T> iface) {
        List<T> result = collect(page.getContent(), iface);

        Collection<DataSourceContent> dataSourceContents = page.getDataSourceContents();
        if (dataSourceContents != null) {
            List<T> dataSourceElements = new ArrayList<>();

            for (DataSourceContent dataSourceContent : dataSourceContents) {
                List<T> dataSourceResult = collect(dataSourceContent.getContent(), iface);
                if (dataSourceResult != null) {
                    dataSourceElements.addAll(dataSourceResult);
                }
            }

            if (result == null) {
                result = dataSourceElements;
            } else {
                result.addAll(dataSourceElements);
            }
        }

        return result;
    }

    public static <T> List<T> collect(List<ContentElement> elements, java.lang.Class<T> iface) {
        List<T> result = new ArrayList<>();

        collectChildren(iface, elements, result);

        return result;
    }

    private static <T> void collectChildren(Class<T> iface, List<? extends ContentElement> children, List<T> result) {
        if (children != null) {
            for (ContentElement element : children) {
                if (iface.isInstance(element)) {
                    result.add(iface.cast(element));
                }

                checkSubContainer(iface, element, result);
            }
        }
    }

    private static <T> void collectListChildren(Class<T> iface, List<? extends ListElement> children, List<T> result) {
        if (children != null) {
            for (ListElement element : children) {
                if (iface.isInstance(element)) {
                    result.add(iface.cast(element));
                }

                checkSubContainer(iface, element, result);
            }
        }
    }

    private static <T> void checkSubContainer(Class<T> iface, Object element, List<T> result) {
        if (element instanceof ContentList) {
            //noinspection unchecked
            List<? extends ListElement> subChildren = ((ContentList) element).getElements();
            collectListChildren(iface, subChildren, result);
        } else if (element instanceof ContentElementsContainer) {
            //noinspection unchecked
            List<? extends ContentElement> subChildren = ((ContentElementsContainer) element).getElements();
            collectChildren(iface, subChildren, result);
        }
    }
}
