package com.eresearch.elsevier.sciencedirect.consumer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString
public class ElsevierScienceDirectConsumerDto {

    @JsonProperty("firstname")
    private String firstname;

    @JsonProperty("initials")
    private String initials;

    @JsonProperty("surname")
    private String surname;
}
