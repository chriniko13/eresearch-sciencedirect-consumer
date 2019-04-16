package com.eresearch.elsevier.sciencedirect.consumer.dto;


import java.util.Collection;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectConsumerSearchViewDto {

    @JsonProperty("opensearch:totalResults")
    private String totalResults;

    @JsonProperty("opensearch:startIndex")
    private String startIndex;

    @JsonProperty("opensearch:itemsPerPage")
    private String itemsPerPage;

    @JsonProperty("opensearch:Query")
    private ScienceDirectSearchViewQuery query;

    @JsonProperty("link")
    private Collection<ScienceDirectSearchViewLink> links;

    @JsonProperty("entry")
    private Collection<ScienceDirectSearchViewEntry> entries;
}
