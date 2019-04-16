package com.eresearch.elsevier.sciencedirect.consumer.worker.generator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class WorkerGenerator {

	@Autowired
    @Qualifier("workerOperationsExecutor")
	private ExecutorService workerOperationsExecutor;

	public <I, R> Function<List<I>, CompletableFuture<List<R>>> createAsyncWorker(Function<I, R> function) {
		
		return dataToProcess -> CompletableFuture.supplyAsync(() -> {

			return dataToProcess.stream()
					            .map(function)
					            .collect(Collectors.toList());

		}, workerOperationsExecutor);
		
	}

}
