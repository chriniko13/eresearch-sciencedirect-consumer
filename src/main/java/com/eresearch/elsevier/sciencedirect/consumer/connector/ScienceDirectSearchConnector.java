package com.eresearch.elsevier.sciencedirect.consumer.connector;


import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;

import java.util.List;

public interface ScienceDirectSearchConnector {

    List<ScienceDirectConsumerResultsDto> searchScienceDirectExhaustive(String query) throws BusinessProcessingException;

}
