package com.eresearch.elsevier.sciencedirect.consumer.service;


import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;

public interface ElsevierScienceDirectConsumerService {

    ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerOperation(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto) throws BusinessProcessingException;

    void sciDirNonBlockConsumption(String transactionId, ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto);
}
