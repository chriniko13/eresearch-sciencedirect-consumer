package com.eresearch.elsevier.sciencedirect.consumer.worker;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.eresearch.elsevier.sciencedirect.consumer.worker.flattener.ResultsFlattener;
import com.eresearch.elsevier.sciencedirect.consumer.worker.generator.WorkerGenerator;
import com.eresearch.elsevier.sciencedirect.consumer.worker.properties.ResourcesProperties;
import com.eresearch.elsevier.sciencedirect.consumer.worker.splitter.ListToSublistsSplitter;

@Component
public class WorkerDispatcher {

    @Autowired
    private ListToSublistsSplitter splitter;

    @Autowired
    private WorkerGenerator generator;

    @Autowired
    private ResultsFlattener resultsFlattener;

    @Autowired
    private ResourcesProperties resourcesProperties;

    public <I, R> List<R> performTask(List<I> listToChunk, Function<I, R> function) {

        List<List<I>> chunkedList = splitter.chopped(listToChunk, Integer.valueOf(resourcesProperties.getListChoppedSize()));

        List<CompletableFuture<List<R>>> workers = chunkedList
                .stream()
                .map(dataToProcess -> generator.createAsyncWorker(function).apply(dataToProcess))
                .collect(Collectors.toList());

        return resultsFlattener.flattenResultsBlocking(workers);
    }

    public Boolean shouldDispatch(Integer dataSize) {
        return dataSize >= Integer.valueOf(resourcesProperties.getSplitterThreshold());
    }

}
