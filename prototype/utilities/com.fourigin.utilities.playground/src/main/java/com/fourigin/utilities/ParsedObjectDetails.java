package com.fourigin.utilities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ParsedObjectDetails {
    private String id;
    private OfferType offerType;
    private String code;
    private LocalizedText headline;
    private LocalizedText shortDescription;
    private LocalizedText longDescription;
    private Map<String, String> properties;
    private List<ImageDetails> images;
    private double latitude;
    private double longitude;

    public ParsedObjectDetails(){}

    public ParsedObjectDetails(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public OfferType getOfferType() {
        return offerType;
    }

    public void setOfferType(OfferType offerType) {
        this.offerType = offerType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public LocalizedText getHeadline() {
        return headline;
    }

    public void setHeadline(LocalizedText headline) {
        this.headline = headline;
    }

    public void setHeadline(String language, String headline) {
        if(this.headline == null){
            this.headline = new LocalizedText();
        }
        this.headline.setValue(language, headline);
    }

    public LocalizedText getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(LocalizedText shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setShortDescription(String language, String shortDescription) {
        if(this.shortDescription == null){
            this.shortDescription = new LocalizedText();
        }
        this.shortDescription.setValue(language, shortDescription);
    }

    public LocalizedText getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(LocalizedText longDescription) {
        this.longDescription = longDescription;
    }

    public void setLongDescription(String language, String longDescription) {
        if(this.longDescription == null){
            this.longDescription = new LocalizedText();
        }
        this.longDescription.setValue(language, longDescription);
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public void setProperty(String key, String value){
        if(this.properties == null){
            this.properties = new HashMap<>();
        }

        this.properties.put(key, value);
    }

    public List<ImageDetails> getImages() {
        return images;
    }

    public void setImages(List<ImageDetails> images) {
        this.images = images;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParsedObjectDetails)) return false;
        ParsedObjectDetails that = (ParsedObjectDetails) o;
        return Double.compare(that.latitude, latitude) == 0 &&
            Double.compare(that.longitude, longitude) == 0 &&
            Objects.equals(id, that.id) &&
            offerType == that.offerType &&
            Objects.equals(code, that.code) &&
            Objects.equals(headline, that.headline) &&
            Objects.equals(shortDescription, that.shortDescription) &&
            Objects.equals(longDescription, that.longDescription) &&
            Objects.equals(properties, that.properties) &&
            Objects.equals(images, that.images);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, offerType, code, headline, shortDescription, longDescription, properties, images, latitude, longitude);
    }

    @Override
    public String toString() {
        return "ParsedObjectDetails{" +
            "id='" + id + '\'' +
            ", offerType='" + offerType + '\'' +
            ", code='" + code + '\'' +
            ", headline='" + headline + '\'' +
            ", shortDescription='" + shortDescription + '\'' +
            ", longDescription='" + (longDescription == null? "<null>" : "<not null>") + '\'' +
            ", properties=" + properties +
            ", images=" + images +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
