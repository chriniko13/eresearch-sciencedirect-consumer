package com.eresearch.elsevier.sciencedirect.consumer.resource;

import com.codahale.metrics.Timer;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.SciDirImmediateResultDto;
import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;
import com.eresearch.elsevier.sciencedirect.consumer.exception.DataValidationException;
import com.eresearch.elsevier.sciencedirect.consumer.metrics.entries.ResourceLayerMetricEntry;
import com.eresearch.elsevier.sciencedirect.consumer.service.ElsevierScienceDirectConsumerService;
import com.eresearch.elsevier.sciencedirect.consumer.validator.Validator;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j
@RestController
@RequestMapping("/sciencedirect-consumer")
public class ElsevierScienceDirectConsumerResourceImpl implements ElsevierScienceDirectConsumerResource {


    private static final Long DEFERRED_RESULT_TIMEOUT = TimeUnit.MILLISECONDS.toMinutes(7);

    @Qualifier("scidirConsumerExecutor")
    @Autowired
    private ExecutorService sciDirConsumerExecutor;

    @Autowired
    private ElsevierScienceDirectConsumerService elsevierScienceDirectConsumerService;

    @Autowired
    private Validator<ElsevierScienceDirectConsumerDto> elsevierScienceDirectConsumerDtoValidator;

    @Autowired
    private ResourceLayerMetricEntry resourceLayerMetricEntry;

    @RequestMapping(method = RequestMethod.POST, path = "/find", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public
    @ResponseBody
    DeferredResult<ElsevierScienceDirectConsumerResultsDto> elsevierScienceDirectConsumerOperation(
            @RequestBody ElsevierScienceDirectConsumerDto elsevierScopusConsumerDto)
            throws BusinessProcessingException {


        DeferredResult<ElsevierScienceDirectConsumerResultsDto> deferredResult = new DeferredResult<>(DEFERRED_RESULT_TIMEOUT);

        Runnable task = elsevierScienceDirectConsumerOperation(elsevierScopusConsumerDto, deferredResult);
        sciDirConsumerExecutor.execute(task);

        return deferredResult;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/find-q", consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE}, produces = {MediaType.APPLICATION_JSON_UTF8_VALUE})
    @Override
    public @ResponseBody
    ResponseEntity<SciDirImmediateResultDto> sciDirNonBlockConsumption(
            @RequestHeader(value = "Transaction-Id") String transactionId,
            @RequestBody ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto) throws DataValidationException {

        elsevierScienceDirectConsumerDtoValidator.validate(elsevierScienceDirectConsumerDto);

        Runnable task = () -> elsevierScienceDirectConsumerService.sciDirNonBlockConsumption(transactionId, elsevierScienceDirectConsumerDto);
        sciDirConsumerExecutor.execute(task);

        return ResponseEntity.ok(new SciDirImmediateResultDto("Response will be written in queue."));
    }

    private Runnable elsevierScienceDirectConsumerOperation(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto,
                                                            DeferredResult<ElsevierScienceDirectConsumerResultsDto> deferredResult) {

        return () -> {

            final Timer.Context context = resourceLayerMetricEntry.getResourceLayerTimer().time();
            try {

                elsevierScienceDirectConsumerDtoValidator.validate(elsevierScienceDirectConsumerDto);

                ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerResultsDto
                        = elsevierScienceDirectConsumerService.elsevierScienceDirectConsumerOperation(elsevierScienceDirectConsumerDto);

                resourceLayerMetricEntry.getSuccessResourceLayerCounter().inc();

                deferredResult.setResult(elsevierScienceDirectConsumerResultsDto);

            } catch (DataValidationException | BusinessProcessingException e) {

                log.error("ElsevierScienceDirectConsumerResourceImpl#elsevierScienceDirectConsumerOperation --- error occurred.", e);

                resourceLayerMetricEntry.getFailureResourceLayerCounter().inc();

                deferredResult.setErrorResult(e);

            } finally {
                context.stop();
            }
        };
    }
}
