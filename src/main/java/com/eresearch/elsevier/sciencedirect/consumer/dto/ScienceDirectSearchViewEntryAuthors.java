package com.eresearch.elsevier.sciencedirect.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
@NoArgsConstructor
public class ScienceDirectSearchViewEntryAuthors {

    @JsonProperty("author")
    private Collection<ScienceDirectSearchViewEntryAuthor> authors;

}
