package com.fourigin.utilities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fourigin.argo.models.content.ContentPage;
import com.fourigin.argo.models.content.ContentPageMetaData;
import com.fourigin.argo.models.content.DataSourceContent;
import com.fourigin.argo.models.content.elements.ContentElement;
import com.fourigin.argo.models.content.elements.ContentGroup;
import com.fourigin.argo.models.content.elements.ContentList;
import com.fourigin.argo.models.content.elements.ContentListElement;
import com.fourigin.argo.models.content.elements.ObjectContentListElement;
import com.fourigin.argo.models.content.elements.TextContentElement;
import com.fourigin.argo.models.content.elements.mapping.ContentPageModule;
import com.fourigin.argo.models.datasource.DataSourceIdentifier;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.fourigin.utilities.OfferType.SALE;

public final class JsoupParser {

    private static final ObjectMapper OBJECT_MAPPER;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        OBJECT_MAPPER.registerModule(new ContentPageModule());
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
    }

    private static final boolean READ_ORIGINAL_CONTENT = false;

    private static final List<String> SEARCH_INDEX = Arrays.asList(
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=1",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=2",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=3",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=4",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=5",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=6",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=7",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=8",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=9",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=10",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=11",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=12",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=13",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=14",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=15",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=16",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk?page=17"
    );

    private static final List<String> ORIGINAL_LINKS = Arrays.asList(
        "http://www.greekestate.gr/de",
        "http://www.greekestate.gr/de/Immobilien/%20Suche",
        "http://www.greekestate.gr/de/Profil",
        "http://www.greekestate.gr/de/Vermarktungsanfrage/erstellen",
        "http://www.greekestate.gr/de/Suchanfrage/%20erstellen",
        "http://www.greekestate.gr/de/Kontakt",
        "http://www.greekestate.gr/en",
        "http://www.greekestate.gr/en/property/search",
        "http://www.greekestate.gr/en/profile",
        "http://www.greekestate.gr/en/entrustmentEnquiry/create",
        "http://www.greekestate.gr/en/searchEnquiry/create",
        "http://www.greekestate.gr/en/contact",
        "http://www.greekestate.gr/ru",
        "http://www.greekestate.gr/ru/nedvizhimost/poisk",
        "http://www.greekestate.gr/ru/profil",
        "http://www.greekestate.gr/ru/peredatNedvizhimost",
        "http://www.greekestate.gr/ru/poiskNedvizhimosti",
        "http://www.greekestate.gr/ru/sviazatsya"
    );

    private static final String OBJECT_LINK_PREFIX_DE = "http://www.greekestate.gr/de/ImmobilienDetails/";
    private static final String OBJECT_LINK_PREFIX_RU = "http://www.greekestate.gr/ru/nedvizhimost/";
    private static final String OBJECT_LINK_PREFIX_EN = "http://www.greekestate.gr/en/propertyDetails/";

    private JsoupParser(){}

    public static void main(String[] args) {
        if (READ_ORIGINAL_CONTENT) {
            createBackup();
        }

        List<String> objectIds = readObjects();

        Map<String, ParsedObjectDetails> parsedObjects = parseObjects(objectIds);

        Map<String, ParsedObjectDetails> fixedObjects = fixObjects(parsedObjects);

        convertObjects(objectIds, fixedObjects);
    }

    private static List<String> readObjects() {
        List<String> objectIds;
        try {
            File objectIdsFile = new File("storage/1-original/ids.json");

            //noinspection unchecked
            objectIds = OBJECT_MAPPER.readValue(objectIdsFile, List.class);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to read object IDs!", ex);
        }

        System.out.println("Reading " + objectIds.size() + " object files ...");

        for (String id : objectIds) {
            // reading pages
            try {
                File ruFile = new File("storage/1-original/ru_object_" + id + ".html");
                if (!ruFile.exists()) {
                    Document ruDoc = getDocument(OBJECT_LINK_PREFIX_RU + id);
                    FileUtils.writeStringToFile(ruFile, ruDoc.outerHtml(), "UTF-8");
                }

                File deFile = new File("storage/1-original/de_object_" + id + ".html");
                if (!deFile.exists()) {
                    Document deDoc = getDocument(OBJECT_LINK_PREFIX_DE + id);
                    FileUtils.writeStringToFile(deFile, deDoc.outerHtml(), "UTF-8");
                }

                File enFile = new File("storage/1-original/en_object_" + id + ".html");
                if (!enFile.exists()) {
                    Document enDoc = getDocument(OBJECT_LINK_PREFIX_EN + id);
                    FileUtils.writeStringToFile(enFile, enDoc.outerHtml(), "UTF-8");
                }

                System.out.print('.');

            } catch (IOException ex) {
                throw new IllegalStateException("Unable to write object file!", ex);
            }
        }

        System.out.println();

        return objectIds;
    }

    private static Map<String, ParsedObjectDetails> parseObjects(List<String> objectIds) {
        Map<String, ParsedObjectDetails> parsedObjects = new HashMap<>();

        int left = objectIds.size();
        for (String id : objectIds) {
            File file = new File("storage/2-parsed/" + id + ".json");
            if (file.exists()) {
                left--;

                try {
                    ParsedObjectDetails details = OBJECT_MAPPER.readValue(file, ParsedObjectDetails.class);
                    parsedObjects.put(id, details);
                } catch (IOException ex) {
                    throw new IllegalArgumentException("Unable to store parsed object " + id + " to file '" + file.getAbsolutePath() + "'!", ex);
                }
                continue;
            }

            // reading pages

            Document ruDoc;
            Document deDoc;
            Document enDoc;

            try {
                File ruFile = new File("storage/1-original/ru_object_" + id + ".html");
                ruDoc = Jsoup.parse(ruFile, "UTF-8");
                File deFile = new File("storage/1-original/de_object_" + id + ".html");
                deDoc = Jsoup.parse(deFile, "UTF-8");
                File enFile = new File("storage/1-original/en_object_" + id + ".html");
                enDoc = Jsoup.parse(enFile, "UTF-8");
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to read local object file!", ex);
            }

            System.out.print(id);

            ParsedObjectDetails details = new ParsedObjectDetails(id);

            // parsing headlines
            Element ruH1 = ruDoc.select("h1").first();
            Element deH1 = deDoc.select("h1").first();
            Element enH1 = enDoc.select("h1").first();

            String ruHeader = parseHeadline(details, "ru", ruH1); // NOPMD
            String deHeader = parseHeadline(details, "de", deH1); // NOPMD
            String enHeader = parseHeadline(details, "en", enH1);

            System.out.print('.');

            // parsing code
            Element codeDiv = enDoc.select("div.property-id").first();
            String code = codeDiv.text().trim();
            if (code.startsWith("Code ")) {
                code = code.substring("Code ".length());
            }
            details.setCode(code);

            System.out.print('.');

            // parsing property type
            if (enHeader.startsWith("Apartment")) {
                details.setProperty("type", "apartment");
            } else if (enHeader.startsWith("Land Plot")) {
                details.setProperty("type", "land-plot");
            } else if (enHeader.startsWith("Building")) {
                details.setProperty("type", "building");
            } else if (enHeader.startsWith("Hotel")) {
                details.setProperty("type", "hotel");
            } else if (enHeader.startsWith("Villa")) {
                details.setProperty("type", "villa");
            } else if (enHeader.startsWith("Detached House")) {
                details.setProperty("type", "detached-house");
            } else if (enHeader.startsWith("Office")) {
                details.setProperty("type", "office");
            } else if (enHeader.startsWith("Farm parcel")) {
                details.setProperty("type", "farm-parcel");
            } else if (enHeader.startsWith("Store")) {
                details.setProperty("type", "store");
            } else if (enHeader.startsWith("Business building")) {
                details.setProperty("type", "business-building");
            } else if (enHeader.startsWith("Various Other Property")) {
                details.setProperty("type", "various-other-property");
            } else if (enHeader.startsWith("Parking")) {
                details.setProperty("type", "parking");
            } else if (enHeader.startsWith("Other Land property")) {
                details.setProperty("type", "other-land-property");
            } else if (enHeader.startsWith("Maisonette")) {
                details.setProperty("type", "maisonette");
            } else {
                throw new IllegalArgumentException("Unsupported header '" + enHeader + "'! Unable to parse property type!");
            }

            System.out.print('.');

            // Parsing offer type
            if (enHeader.contains(" to rent ")) {
                details.setOfferType(OfferType.RENT);
            } else if (enHeader.contains(" for sale ")) {
                details.setOfferType(SALE);
            } else {
                throw new IllegalArgumentException("Unsupported header '" + enHeader + "'! Unable to parse offer type!");
            }

            System.out.print('.');

            // Parsing region
            if (enHeader.contains(" Loutraki")) {
                details.setProperty("region", "loutraki");
            } else if (enHeader.contains(" Assos")) {
                details.setProperty("region", "assos");
            } else if (enHeader.contains(" Patisia")) {
                details.setProperty("region", "patisia");
            } else if (enHeader.contains(" Lechaio")) {
                details.setProperty("region", "lechaio");
            } else if (enHeader.contains(" Kalamia")) {
                details.setProperty("region", "kalamia");
            } else if (enHeader.contains(" Center")) {
                details.setProperty("region", "center");
            } else if (enHeader.contains(" Korinthos")) {
                details.setProperty("region", "korinthos");
            } else if (enHeader.contains(" Sikiona")) {
                details.setProperty("region", "sikiona");
            } else if (enHeader.contains(" Limni Vouliagmenis")) {
                details.setProperty("region", "limni vouliagmenis");
            } else if (enHeader.contains(" Soligeia")) {
                details.setProperty("region", "soligeia");
            } else if (enHeader.contains(" Nea Penteli")) {
                details.setProperty("region", "nea penteli");
            } else if (enHeader.contains(" Glika Nera")) {
                details.setProperty("region", "glika nera");
            } else if (enHeader.contains(" Marousi")) {
                details.setProperty("region", "marousi");
            } else if (enHeader.contains(" Velo")) {
                details.setProperty("region", "velo");
            } else if (enHeader.contains(" Xilokastro")) {
                details.setProperty("region", "xilokastro");
            } else if (enHeader.contains(" Vocha")) {
                details.setProperty("region", "vocha");
            } else if (enHeader.contains(" Poseidonia")) {
                details.setProperty("region", "poseidonia");
            } else if (enHeader.contains(" Agia Paraskevi")) {
                details.setProperty("region", "agia paraskevi");
            } else if (enHeader.contains(" Agioi Theodoroi")) {
                details.setProperty("region", "agioi theodoroi");
            } else if (enHeader.contains(" Xilokeriza")) {
                details.setProperty("region", "xilokeriza");
            } else if (enHeader.contains(" Voula")) {
                details.setProperty("region", "voula");
            } else if (enHeader.contains(" Saronikos")) {
                details.setProperty("region", "saronikos");
            } else if (enHeader.contains(" Evrostini")) {
                details.setProperty("region", "evrostini");
            } else if (enHeader.contains(" Kato Almiri")) {
                details.setProperty("region", "kato almiri");
            } else if (enHeader.contains(" Batharistra")) {
                details.setProperty("region", "batharistra");
            } else if (enHeader.contains(" Archaio Limani")) {
                details.setProperty("region", "archaio limani");
            } else if (enHeader.contains(" Akadimia")) {
                details.setProperty("region", "akadimia");
            } else if (enHeader.contains(" Agios Georgios")) {
                details.setProperty("region", "agios georgios");
            } else {
                throw new IllegalArgumentException("Unsupported header '" + enHeader + "'! Unable to parse region!");
            }

            System.out.print('.');

            // Parsing short description
            Elements ruDescriptionList = ruDoc.select("ul.property-amenities li");
            Elements deDescriptionList = deDoc.select("ul.property-amenities li");
            Elements enDescriptionList = enDoc.select("ul.property-amenities li");
            if (ruDescriptionList != null) {
                StringBuilder builder = new StringBuilder();
                for (Element element : ruDescriptionList) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(element.html());
                }
                details.setShortDescription("ru", builder.toString());
            }
            if (deDescriptionList != null) {
                StringBuilder builder = new StringBuilder();
                for (Element element : deDescriptionList) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(element.html());
                }
                details.setShortDescription("de", builder.toString());
            }
            if (enDescriptionList != null) {
                StringBuilder builder = new StringBuilder();
                for (Element element : enDescriptionList) {
                    if (builder.length() > 0) {
                        builder.append(", ");
                    }
                    builder.append(element.html());
                }
                details.setShortDescription("en", builder.toString());
            }

            // Parsing long description
            Element ruDescriptionDiv = ruDoc.select("div.entry div.panel-body").first();
            Element deDescriptionDiv = deDoc.select("div.entry div.panel-body").first();
            Element enDescriptionDiv = enDoc.select("div.entry div.panel-body").first();
            if (ruDescriptionDiv != null) {
                details.setLongDescription("ru", ruDescriptionDiv.html());
            }
            if (deDescriptionDiv != null) {
                details.setLongDescription("de", deDescriptionDiv.html());
            }
            if (enDescriptionDiv != null) {
                details.setLongDescription("en", enDescriptionDiv.html());
            }

            System.out.print('.');

            // Parsing geo-position
            Element geoPositionDiv = enDoc.select("div#map div.marker").first();
            if (geoPositionDiv != null) {
                details.setLatitude(Double.parseDouble(geoPositionDiv.attr("data-lat")));
                details.setLongitude(Double.parseDouble(geoPositionDiv.attr("data-lng")));
            }

            System.out.print('.');

            // Parsing images
            Elements imageDivs = enDoc.select("div#property-gallery div.item img");
            if (imageDivs != null) {
                List<ImageDetails> images = new ArrayList<>();
                for (Element imageTag : imageDivs) {
                    String imageUrl = imageTag.attr("src");
                    String altName = imageTag.attr("alt");

                    String assetId;
                    try {
                        assetId = uploadImage(imageUrl);
                    } catch (Throwable ex) {
                        throw new IllegalArgumentException("Unable to upload image '" + imageUrl + "'!", ex);
                    }

                    ImageDetails image = new ImageDetails(assetId);
                    image.setAlternateText(altName);
                    images.add(image);
                }

                details.setImages(images);
            }

            System.out.print('.');

            // Storing parsed object
            try {
                OBJECT_MAPPER.writeValue(file, details);
            } catch (IOException ex) {
                throw new IllegalArgumentException("Unable to store parsed object " + id + " to file '" + file.getAbsolutePath() + "'!", ex);
            }

            parsedObjects.put(id, details);

            left--;
            System.out.println(" (" + left + " left)");
        }

        return parsedObjects;
    }

    private static Map<String, ParsedObjectDetails> fixObjects(Map<String, ParsedObjectDetails> parsedObjects) {
        System.out.println("Validating & fixing " + parsedObjects.size() + " objects ...");

        Map<String, ParsedObjectDetails> fixedObjects = new HashMap<>();

        for (Map.Entry<String, ParsedObjectDetails> entry : parsedObjects.entrySet()) {
            String id = entry.getKey();
            ParsedObjectDetails object = entry.getValue();

            Map<String, String> properties = object.getProperties();
            if (properties.get("price") == null) {
                throw new IllegalStateException("Object '" + id + "' doesn't have a price!");
            }

            {
                String value = properties.get("area");
                if (value != null) {
                    value = value.replaceAll("m2", "");
                    value = value.replaceAll("\\.", "");
                    value = value.replaceAll(",", "");
                    value = value.trim();
                    properties.put("area", value);
                }
            }
            {
                String value = properties.get("price");
                if (value != null) {
                    value = value.replaceAll("€", "");
                    value = value.replaceAll("\\.", "");
                    value = value.replaceAll(",", "");
                    value = value.trim();
                    properties.put("price", value);
                }
            }
            {
                String value = properties.get("type");
                if (value != null) {
                    value = value.replaceAll("\\ ", "-");
                    value = value.trim();
                    properties.put("type", value);
                }
            }

            object.setProperties(properties);

            fixLocalizedText(object.getHeadline());
            fixLocalizedText(object.getShortDescription());
            fixLocalizedText(object.getLongDescription());

            fixHeadline(object.getHeadline());

            System.out.print('.');

            fixedObjects.put(id, object);
        }

        System.out.println();

        return fixedObjects;
    }

    private static void convertObjects(List<String> objectIds, Map<String, ParsedObjectDetails> fixedObjects) {
        System.out.println("Converting fixed objects ...");

        List<ContentPage> rentPages = new ArrayList<>();
        List<ContentPage> salePages = new ArrayList<>();

        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        for (String id : objectIds) {
            ParsedObjectDetails object = fixedObjects.get(id);

            System.out.print(id + ":");

            OfferType offerType = object.getOfferType();

            String objectCode = object.getCode();

            String enTitle;
            String deTitle;
            String ruTitle;
            switch (offerType) {
                case SALE:
                    enTitle = "Sale object #" + objectCode + " - BestGreekEstate";
                    deTitle = "Verkaufsobjekt #" + objectCode + " - BestGreekEstate";
                    ruTitle = "Объект #" + objectCode + " на продажу - BestGreekEstate";
                    break;
                case RENT:
                    enTitle = "Rent object #" + objectCode + " - BestGreekEstate";
                    deTitle = "Mietobjekt #" + objectCode + " - BestGreekEstate";
                    ruTitle = "Объект #" + objectCode + " для аренды - BestGreekEstate";
                    break;
                default:
                    throw new IllegalStateException("Unsupported offer type " + offerType + "!");
            }

            ContentPageMetaData metaData = new ContentPageMetaData.Builder()
                .withTitle(enTitle)
                .withContextSpecificTitle("de", deTitle)
                .withContextSpecificTitle("ru", ruTitle)
                .withAttribute("converted on", dateFormat.format(now))
                .build();
            System.out.print(" meta");

            List<ContentElement> propertyElements = new ArrayList<>();
            propertyElements.add(new TextContentElement.Builder()
                .withName("code")
                .withContent(objectCode)
                .build()
            );
            for (Map.Entry<String, String> entry : object.getProperties().entrySet()) {
                String name = entry.getKey();
                String value = entry.getValue();
                propertyElements.add(new TextContentElement.Builder()
                    .withName(name)
                    .withContent(value)
                    .build()
                );
            }
            System.out.print(" properties");

            List<ContentListElement> imageElements = new ArrayList<>();
            for (ImageDetails imageDetails : object.getImages()) {
                imageElements.add(new ObjectContentListElement.Builder()
                    .withReferenceId(imageDetails.getId())
                    .withAlternateText(imageDetails.getAlternateText())
                    .build());
            }
            System.out.print(" images");

            Map<String, String> headline = extractLocalizedValues(object.getHeadline());
            Map<String, String> shortDescription = extractLocalizedValues(object.getShortDescription());
            Map<String, String> longDescription = extractLocalizedValues(object.getLongDescription());

            List<ContentElement> content = Arrays.asList(
                // headline
                new TextContentElement.Builder()
                    .withName("headline")
                    .withContent(headline.get("en"))
                    .withContextSpecificContent("de", headline.get("de"))
                    .withContextSpecificContent("ru", headline.get("ru"))
                    .build(),
                // short-description
                new TextContentElement.Builder()
                    .withName("short-description")
                    .withContent(shortDescription.get("en"))
                    .withContextSpecificContent("de", shortDescription.get("de"))
                    .withContextSpecificContent("ru", shortDescription.get("ru"))
                    .build(),
                // properties
                new ContentGroup.Builder()
                    .withName("properties")
                    .withElements(propertyElements)
                    .build(),
                // short-description
                new TextContentElement.Builder()
                    .withName("long-description")
                    .withContent(longDescription.get("en"))
                    .withContextSpecificContent("de", longDescription.get("de"))
                    .withContextSpecificContent("ru", longDescription.get("ru"))
                    .build(),
                // images
                new ContentList.Builder()
                    .withName("images")
                    .withElements(imageElements)
                    .build(),
                // geo-position
                new ContentGroup.Builder()
                    .withName("geo-position")
                    .withElements(
                        new TextContentElement.Builder()
                            .withName("latitude")
                            .withContent(String.valueOf(object.getLatitude()))
                            .build(),
                        new TextContentElement.Builder()
                            .withName("longitude")
                            .withContent(String.valueOf(object.getLongitude()))
                            .build()
                    )
                    .build()
            );

            Collection<DataSourceContent> dataSources = Arrays.asList(
                new DataSourceContent.Builder()
                    .withName("timestamp")
                    .withIdentifier(new DataSourceIdentifier.Builder()
                        .withType("TIMESTAMP")
                        .build())
                    .build(),
                new DataSourceContent.Builder()
                    .withName("top-level-navigation")
                    .withIdentifier(new DataSourceIdentifier.Builder()
                        .withType("SITE")
                        .withQueryProperty("INFO_PATH", "/")
                        .withQueryProperty("NON_RECURSIVE", true)
                        .withQueryProperty("VERBOSE", true)
                        .build())
                    .build(),
                new DataSourceContent.Builder()
                    .withName("common-content")
                    .withIdentifier(new DataSourceIdentifier.Builder()
                        .withType("COMMON-CONTENT")
                        .withQueryProperty("INCLUDE_CONTENT", "/general-headlines, /links-and-ctas, /properties")
                        .build())
                    .build()
            );

            ContentPage page = new ContentPage.Builder()
                .withId(id)
                .withMetaData(metaData)
                .withContent(content)
                .withDataSourceContents(dataSources)
                .build();
            System.out.print(" page");

            switch (object.getOfferType()) {
                case RENT:
                    rentPages.add(page);
                    System.out.println(" --> rent");
                    break;
                case SALE:
                    salePages.add(page);
                    System.out.println(" --> sale");
                    break;
                default:
                    throw new IllegalStateException("Unsupported offer type '" + object.getOfferType() + "'!");
            }
        }

        // Storing pages
        System.out.print("Writing sale files ...");

        StringBuilder saleInfoBuilder = new StringBuilder();
        saleInfoBuilder.append("{\n");
        saleInfoBuilder.append("\t\"path\": \"/objects/sale\",\n");
        saleInfoBuilder.append("\t\"children\": [\n");
        saleInfoBuilder.append("\t\t{\n");
        saleInfoBuilder.append("\t\t\t\"type\": \"file\",\n");
        saleInfoBuilder.append("\t\t\t\"name\": \"search\",\n");
        saleInfoBuilder.append("\t\t\t\"localizedName\": {\n");
        saleInfoBuilder.append("\t\t\t\t\"\": \"search\",\n");
        saleInfoBuilder.append("\t\t\t\t\"de\": \"suche\",\n");
        saleInfoBuilder.append("\t\t\t\t\"ru\": \"poisk\"\n");
        saleInfoBuilder.append("\t\t\t},\n");
        saleInfoBuilder.append("\t\t\t\"templateReference\": {\n");
        saleInfoBuilder.append("\t\t\t\t\"templateId\": \"greekestate.object-search\",\n");
        saleInfoBuilder.append("\t\t\t\t\"variationId\": \"default\",\n");
        saleInfoBuilder.append("\t\t\t\t\"revision\": \"\"\n");
        saleInfoBuilder.append("\t\t\t}\n");
        saleInfoBuilder.append("\t\t}");

        for (ContentPage page : salePages) {
            String id = page.getId();
            File file = new File("storage/4-converted/sale/object_" + id + ".json");
            try {
                OBJECT_MAPPER.writeValue(file, page);
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to write converted object to file '" + file.getAbsolutePath() + "'!", ex);
            }

            saleInfoBuilder.append(",\n\t\t{\n");
            saleInfoBuilder.append("\t\t\t\"type\": \"file\",\n");
            saleInfoBuilder.append("\t\t\t\"name\": \"object_").append(id).append("\",\n");
            saleInfoBuilder.append("\t\t\t\"localizedName\": {\n");
            saleInfoBuilder.append("\t\t\t\t\"\": \"object_").append(id).append("\",\n");
            saleInfoBuilder.append("\t\t\t\t\"de\": \"objekt_").append(id).append("\",\n");
            saleInfoBuilder.append("\t\t\t\t\"ru\": \"objekt_").append(id).append("\"\n");
            saleInfoBuilder.append("\t\t\t},\n");
            saleInfoBuilder.append("\t\t\t\"templateReference\": {\n");
            saleInfoBuilder.append("\t\t\t\t\"templateId\": \"greekestate.object-details\",\n");
            saleInfoBuilder.append("\t\t\t\t\"variationId\": \"default\",\n");
            saleInfoBuilder.append("\t\t\t\t\"revision\": \"\"\n");
            saleInfoBuilder.append("\t\t\t}\n");
            saleInfoBuilder.append("\t\t}");
        }
        saleInfoBuilder.append("\n\t]\n");
        saleInfoBuilder.append("}\n");

        File saleInfoFile = new File("storage/4-converted/sale/.cms/.info");
        try (OutputStream os = new FileOutputStream(saleInfoFile)) {
            IOUtils.write(saleInfoBuilder.toString(), os, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write sales info file '" + saleInfoFile.getAbsolutePath() + "'!", ex);
        }

        System.out.print("\nWriting rent files ...");

        StringBuilder rentInfoBuilder = new StringBuilder();
        rentInfoBuilder.append("{\n");
        rentInfoBuilder.append("\t\"path\": \"/objects/rent\",\n");
        rentInfoBuilder.append("\t\"children\": [\n");
        rentInfoBuilder.append("\t\t{\n");
        rentInfoBuilder.append("\t\t\t\"type\": \"file\",\n");
        rentInfoBuilder.append("\t\t\t\"name\": \"search\",\n");
        rentInfoBuilder.append("\t\t\t\"localizedName\": {\n");
        rentInfoBuilder.append("\t\t\t\t\"\": \"search\",\n");
        rentInfoBuilder.append("\t\t\t\t\"de\": \"suche\",\n");
        rentInfoBuilder.append("\t\t\t\t\"ru\": \"poisk\"\n");
        rentInfoBuilder.append("\t\t\t},\n");
        rentInfoBuilder.append("\t\t\t\"templateReference\": {\n");
        rentInfoBuilder.append("\t\t\t\t\"templateId\": \"greekestate.object-search\",\n");
        rentInfoBuilder.append("\t\t\t\t\"variationId\": \"default\",\n");
        rentInfoBuilder.append("\t\t\t\t\"revision\": \"\"\n");
        rentInfoBuilder.append("\t\t\t}\n");
        rentInfoBuilder.append("\t\t}");

        for (ContentPage page : rentPages) {
            String id = page.getId();
            File file = new File("storage/4-converted/rent/object_" + id + ".json");
            try {
                OBJECT_MAPPER.writeValue(file, page);
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to write converted object to file '" + file.getAbsolutePath() + "'!", ex);
            }

            rentInfoBuilder.append(",\n\t\t{\n");
            rentInfoBuilder.append("\t\t\t\"type\": \"file\",\n");
            rentInfoBuilder.append("\t\t\t\"name\": \"object_").append(id).append("\",\n");
            rentInfoBuilder.append("\t\t\t\"localizedName\": {\n");
            rentInfoBuilder.append("\t\t\t\t\"\": \"object_").append(id).append("\",\n");
            rentInfoBuilder.append("\t\t\t\t\"de\": \"objekt_").append(id).append("\",\n");
            rentInfoBuilder.append("\t\t\t\t\"ru\": \"objekt_").append(id).append("\"\n");
            rentInfoBuilder.append("\t\t\t},\n");
            rentInfoBuilder.append("\t\t\t\"templateReference\": {\n");
            rentInfoBuilder.append("\t\t\t\t\"templateId\": \"greekestate.object-details\",\n");
            rentInfoBuilder.append("\t\t\t\t\"variationId\": \"default\",\n");
            rentInfoBuilder.append("\t\t\t\t\"revision\": \"\"\n");
            rentInfoBuilder.append("\t\t\t}\n");
            rentInfoBuilder.append("\t\t}");
        }
        rentInfoBuilder.append("\n\t]\n");
        rentInfoBuilder.append("}\n");

        File rentInfoFile = new File("storage/4-converted/rent/.cms/.info");
        try (OutputStream os = new FileOutputStream(rentInfoFile)) {
            IOUtils.write(rentInfoBuilder.toString(), os, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write rent info file '" + rentInfoFile.getAbsolutePath() + "'!", ex);
        }

        System.out.print("\nDONE");
    }

    private static void createBackup() {
        System.out.println("Creating backup of static pages ...");
        for (String link : ORIGINAL_LINKS) {
            try {
                Document doc = getDocument(link);
                String name = extractFilename(link);
                System.out.println("\t'" + name + "'");
                File file = new File("storage/1-original/" + name + ".html");
                FileUtils.writeStringToFile(file, doc.outerHtml(), "UTF-8");
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to write original file!", ex);
            }
        }

        System.out.println("Reading object IDs ...");
        List<String> objectIds = new ArrayList<>();
        for (String page : SEARCH_INDEX) {
            Document doc = getDocument(page);

            Elements list = doc.select("a.listing-item-body");
            if (list != null && !list.isEmpty()) {
                for (Element element : list) {
                    String href = element.attr("href");
                    String id = href.substring(href.lastIndexOf('/') + 1);
                    objectIds.add(id);
                }
            }
        }

        System.out.println("Found " + objectIds.size() + " objects:\n" + objectIds);

        try {
            File objectIdsFile = new File("storage/1-original/ids.json");
            OBJECT_MAPPER.writeValue(objectIdsFile, objectIds);
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to write object IDs!", ex);
        }
    }

    private static void fixLocalizedText(LocalizedText text) {
        if (text == null) {
            return;
        }

        Map<String, String> values = text.getValues();
        if (values == null) {
            return;
        }

        {
            String value = values.get("de");
            if (value == null) {
                return;
            }

            String result = value.replaceAll("\n", "");
            result = result.replaceAll("&auml;", "ä");
            result = result.replaceAll("&ouml;", "ö");
            result = result.replaceAll("&uuml;", "ü");
            result = result.replaceAll("&Auml;", "Ä");
            result = result.replaceAll("&Ouml;", "Ö");
            result = result.replaceAll("&Uuml;", "Ü");
            result = result.replaceAll("&szlig;", "ß");
            result = result.replaceAll("qm", "m<sup>2</sup>");

            values.put("de", result);
        }

        {
            String value = values.get("ru");
            if (value == null) {
                return;
            }

            String result = value.replaceAll("\n", "");
            result = result.replaceAll("кв\\.м\\.", "м<sup>2</sup>");

            values.put("ru", result);
        }

        {
            String value = values.get("en");
            if (value == null) {
                return;
            }

            String result = value.replaceAll("\n", "");
            result = result.replaceAll("sq m", "m<sup>2</sup>");

            values.put("en", result);
        }
    }

    private static void fixHeadline(LocalizedText headline) {
        if (headline == null) {
            return;
        }

        Map<String, String> values = headline.getValues();
        if (values == null) {
            return;
        }

        {
            String value = values.get("de");
            if (value == null) {
                return;
            }

            String result = value.replaceAll(" zu verkaufen ", " in ");
            result = result.replaceAll(" zu vermieten ", " in ");
            result = result.trim();

            values.put("de", result);
        }

        {
            String value = values.get("ru");
            if (value == null) {
                return;
            }

            String result = value.replaceAll(" Продажа ", " в ");
            result = result.replaceAll(" Аренда ", " в ");
            result = result.trim();

            values.put("ru", result);
        }

        {
            String value = values.get("en");
            if (value == null) {
                return;
            }

            String result = value.replaceAll(" for sale ", " in ");
            result = result.replaceAll(" to rent ", " in ");
            result = result.trim();

            values.put("en", result);
        }
    }

    private static Map<String, String> extractLocalizedValues(LocalizedText localizedText) {
        if (localizedText == null) {
            return Collections.emptyMap();
        }

        return localizedText.getValues();
    }

    private static String extractFilename(String link) {
        String result;
        if (link.startsWith("http://")) {
            result = link.substring("http://".length());
        } else {
            result = link;
        }

        if (result.startsWith("www.greekestate.gr/")) {
            result = result.substring("www.greekestate.gr/".length());
        }

        result = result.replaceAll("/", "_");
        return result;
    }

    private static String parseHeadline(ParsedObjectDetails details, String language, Element h1) {
        String header = h1.text();
        String[] parts = header.split(", ");
        String headline = parts[0].trim();
        String price = parts[1].trim();
        String area = parts[2].trim();

        details.setHeadline(language, headline);
        details.setProperty("price", price);
        details.setProperty("area", area);

        return header;
    }

    private static Document getDocument(String url) {
        try {
            final Connection.Response response = Jsoup.connect(url).execute();
            return response.parse();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load page '" + url + "'!", ex);
        }
    }

    private static String uploadImage(String imageUrl) throws IOException {
        int filenameStart = imageUrl.lastIndexOf('/');
        int filenameEnd = imageUrl.indexOf("?v=");

        String filename;
        if (filenameEnd > filenameStart) {
            filename = imageUrl.substring(filenameStart + 1, filenameEnd);
        } else {
            filename = imageUrl.substring(filenameStart + 1);
        }

        String uploadUrl = "http://argo.greekestate.fourigin.com/cms/assets/uploadUrl?";
        uploadUrl += "base=DE";
        uploadUrl += "&filename=" + filename;
        uploadUrl += "&contentType=image/jpeg";
        uploadUrl += "&url=" + imageUrl;

//        System.out.println("upload-url: " + uploadUrl);

        URL url = new URL(uploadUrl);
        UploadResponse response = OBJECT_MAPPER.readValue(url, UploadResponse.class);

        Map<String, Throwable> failed = response.getFailed();
        if (failed == null || failed.isEmpty()) {
            String key = filename.substring(0, filename.lastIndexOf("."));
            return response.getSuccessful().get(key);
        }

        for (Map.Entry<String, Throwable> entry : failed.entrySet()) {
            System.err.println("Error uploading asset '" + entry.getKey() + "': " + entry.getValue());
        }

        return null;
    }
}
