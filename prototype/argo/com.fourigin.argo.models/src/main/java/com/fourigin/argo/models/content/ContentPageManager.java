package com.fourigin.argo.models.content;

import com.fourigin.argo.models.content.config.RuntimeConfiguration;
import com.fourigin.argo.models.content.config.RuntimeConfigurations;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentElementsContainer;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.ContentList;
import com.fourigin.argo.models.content.elements.ContentListElement;
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
        if (contentPage == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve ContentElement based on null-ContentPage!");
            return null;
        }

        if (contentPath == null) {
            if (LOGGER.isInfoEnabled()) LOGGER.info("Unable to resolve ContentElement based on null-path!");
            return null;
        }

        String path = contentPath.replace("//", "/").trim();

        // compress path (e.g. "..")
        path = compressContentPath(path);

        if ("/".equals(path)) {
            // special case, return all root level elements, packed in a new pseudo group
            return new ContentGroup.Builder()
                .withName("")
                .withElements(contentPage.getContent())
                .build();
        }

        if (path.startsWith("@")) {
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
                    if (LOGGER.isErrorEnabled()) LOGGER.error("No data source found for name '{}'!", dataSourceName);
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

                return resolve(path, match.getContent());
            }
        }

        List<ContentElement> elements = contentPage.getContent();
        return resolve(path, elements);
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
        return resolve(path, elements);
    }

    public static void update(ContentPage contentPage, String contentPath, ContentElement element) {
        ContentElement parentElement = resolve(contentPage, contentPath + "/..");
        if (parentElement == null) {
            throw new IllegalArgumentException("Unable to find parent element for path '" + contentPath + "'!");
        }

        if (!ContentElementsContainer.class.isAssignableFrom(parentElement.getClass())) {
            throw new IllegalArgumentException("Parent element is not a container for path '" + contentPath + "'!");
        }

        ContentElementsContainer parent = ContentElementsContainer.class.cast(parentElement);
        List<ContentElement> elements = parent.getElements();
        if (elements == null) {
            elements = new ArrayList<>();
            elements.add(element);
            return;
        }

        String name = element.getName();

        ContentElement previousElement = null;
        for (ContentElement contentElement : elements) {
            if (name.equals(contentElement.getName())) {
                previousElement = contentElement;
                break;
            }
        }

        if (previousElement != null) {
            // replace the existing element
            elements.remove(previousElement);
        }

        elements.add(element);
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
            resultParts.remove(count - 1);

            // step back
            count--;
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

    public static ContentElement resolveOptional(String path, List<ContentElement> elements) {
        try {
            return resolve(path, elements);
        } catch (UnresolvableContentPathException ex) {
            return null;
        }
    }

    public static ContentElement resolve(String path, List<ContentElement> elements) {
        ContentElement current = null;

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Resolving path '{}' in {}", path, elements);
        }

        StringTokenizer tok = new StringTokenizer(path, "/");
        while (tok.hasMoreTokens()) {
            String token = tok.nextToken().trim();

            current = null;

            for (ContentElement element : elements) {
                if (token.equals(element.getName())) {
                    current = element;
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

    private static <T> void collectListChildren(Class<T> iface, List<? extends ContentListElement> children, List<T> result) {
        if (children != null) {
            for (ContentListElement element : children) {
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
            List<? extends ContentListElement> subChildren = ((ContentList) element).getElements();
            collectListChildren(iface, subChildren, result);
        } else if (element instanceof ContentElementsContainer) {
            //noinspection unchecked
            List<? extends ContentElement> subChildren = ((ContentElementsContainer) element).getElements();
            collectChildren(iface, subChildren, result);
        }
    }
}
