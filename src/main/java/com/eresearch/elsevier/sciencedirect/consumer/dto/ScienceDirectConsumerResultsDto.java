package com.eresearch.elsevier.sciencedirect.consumer.dto;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectConsumerResultsDto {

    @JsonProperty("search-results")
    private ScienceDirectConsumerSearchViewDto scienceDirectConsumerSearchViewDto;

}
