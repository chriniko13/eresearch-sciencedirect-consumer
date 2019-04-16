package com.eresearch.elsevier.sciencedirect.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SciDirQueueResultDto {

    private String transactionId;
    private String exceptionMessage;
    private ElsevierScienceDirectConsumerResultsDto sciDirResultsDto;
}
