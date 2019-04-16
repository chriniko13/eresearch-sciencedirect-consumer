package com.eresearch.elsevier.sciencedirect.consumer.resource;


import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.SciDirImmediateResultDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import com.eresearch.elsevier.sciencedirect.consumer.exception.DataValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface ElsevierScienceDirectConsumerResource {

    DeferredResult<ElsevierScienceDirectConsumerResultsDto> elsevierScienceDirectConsumerOperation(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto)
            throws BusinessProcessingException;

    ResponseEntity<SciDirImmediateResultDto> sciDirNonBlockConsumption(String transactionId, ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto)
            throws DataValidationException;
}
