package com.eresearch.elsevier.sciencedirect.consumer.worker.flattener;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

@Component
public class ResultsFlattener {

	public <R> List<R> flattenResultsBlocking(List<CompletableFuture<List<R>>> workers) {

		return workers.stream().map(CompletableFuture::join).flatMap(List::stream).collect(Collectors.toList());

	}
}
