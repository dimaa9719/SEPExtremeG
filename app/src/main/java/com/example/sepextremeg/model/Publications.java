package com.example.sepextremeg.model;

public class Publications {
    String isbnNumber;
    String pubTitle;
    String pubType;
    String AuthorName;
    String organisation;
    String language;
    String citynCountry;
    String yearPublished;
    String permLink;

    public Publications(String isbnNumber, String pubTitle, String pubType, String authorName,
                        String organisation, String language, String citynCountry, String yearPublished) {
        this.isbnNumber = isbnNumber;
        this.pubTitle = pubTitle;
        this.pubType = pubType;
        AuthorName = authorName;
        this.organisation = organisation;
        this.language = language;
        this.citynCountry = citynCountry;
        this.yearPublished = yearPublished;
    }

    public Publications() {

    }

    public String getIsbnNumber() {
        return isbnNumber;
    }

    public void setIsbnNumber(String isbnNumber) {
        this.isbnNumber = isbnNumber;
    }

    public String getPubTitle() {
        return pubTitle;
    }

    public void setPubTitle(String pubTitle) {
        this.pubTitle = pubTitle;
    }

    public String getPubType() {
        return pubType;
    }

    public void setPubType(String pubType) {
        this.pubType = pubType;
    }

    public String getAuthorName() {
        return AuthorName;
    }

    public void setAuthorName(String authorName) {
        AuthorName = authorName;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCitynCountry() {
        return citynCountry;
    }

    public void setCitynCountry(String citynCountry) {
        this.citynCountry = citynCountry;
    }

    public String getYearPublished() {
        return yearPublished;
    }

    public void setYearPublished(String yearPublished) {
        this.yearPublished = yearPublished;
    }

    public String getPermLink() {
        return permLink;
    }

    public void setPermLink(String permLink) {
        this.permLink = permLink;
    }
}
