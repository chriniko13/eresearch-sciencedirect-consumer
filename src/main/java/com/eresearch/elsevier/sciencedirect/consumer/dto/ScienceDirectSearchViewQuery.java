package com.eresearch.elsevier.sciencedirect.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(of = {"role", "searchTerms"})
@EqualsAndHashCode(of = {"role", "searchTerms"})
@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectSearchViewQuery {

    @JsonProperty("@role")
    private String role;

    @JsonProperty("@searchTerms")
    private String searchTerms;

    @JsonProperty("@startPage")
    private String startPage;
}
