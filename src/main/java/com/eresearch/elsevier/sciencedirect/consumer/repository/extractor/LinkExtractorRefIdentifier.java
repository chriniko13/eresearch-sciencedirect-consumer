package com.eresearch.elsevier.sciencedirect.consumer.repository.extractor;


public enum LinkExtractorRefIdentifier {

    FIRST("first"),
    LAST("last"),
    SELF("self"),;

    private final String value;

    LinkExtractorRefIdentifier(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
