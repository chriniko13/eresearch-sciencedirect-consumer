package com.eresearch.elsevier.sciencedirect.consumer.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@EqualsAndHashCode(of = {"dcIdentifier", "dcTitle"})
@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectSearchViewEntry {

    @JsonProperty("@force-array")
    private String forceArray;

    @JsonProperty("error")
    private String error;

    @JsonProperty("@_fa")
    private String fa;

    @JsonProperty("load-date")
    private String loadDate;

    @JsonProperty("link")
    private Collection<ScienceDirectSearchViewLink> links;

    @JsonProperty("prism:url")
    private String prismUrl;

    @JsonProperty("dc:identifier")
    private String dcIdentifier;

    @JsonProperty("openaccess")
    private String openAccess;

    @JsonProperty("openaccessFlag")
    private String openAccessFlag;

    @JsonProperty("dc:title")
    private String dcTitle;

    @JsonProperty("prism:publicationName")
    private String prismPublicationName;

    @JsonProperty("prism:isbn")
    private String prismIsbn;

    @JsonProperty("prism:issn")
    private String prismIssn;

    @JsonProperty("prism:volume")
    private String prismVolume;

    @JsonProperty("prism:issueIdentifier")
    private String prismIssueIdentifier;

    @JsonProperty("prism:issueName")
    private String prismIssueName;

    @JsonProperty("prism:edition")
    private String prismEdition;

    @JsonProperty("prism:startingPage")
    private String prismStartingPage;

    @JsonProperty("prism:endingPage")
    private String prismEndingPage;

    @JsonProperty("prism:coverDate")
    private String prismCoverDate;

    @JsonProperty("prism:coverDisplayDate")
    private String coverDisplayDate;

    @JsonProperty("dc:creator")
    private String dcCreator;

    @JsonProperty("authors")
    private ScienceDirectSearchViewEntryAuthors authors;

    @JsonProperty("prism:doi")
    private String prismDoi;

    @JsonProperty("pii")
    private String pii;

    @JsonProperty("pubType")
    private String pubType;

    @JsonProperty("prism:teaser")
    private String prismTeaser;

    @JsonProperty("dc:description")
    private String dcDescription;

    @JsonProperty("authkeywords")
    private String authKeywords;

    @JsonProperty("prism:aggregationType")
    private String prismAggregationType;

    @JsonProperty("prism:copyright")
    private String prismCopyright;

    @JsonProperty("scopus-id")
    private String scopusId;

    @JsonProperty("eid")
    private String eid;

    @JsonProperty("scopus-eid")
    private String scopusEid;

    @JsonProperty("pubmed-id")
    private String pubmedId;

    @JsonProperty("openaccessArticle")
    private String openAccessArticle;

    @JsonProperty("openArchiveArticle")
    private String openArchiveArticle;

    @JsonProperty("openaccessUserLicense")
    private String openAccessUserLicense;
}
