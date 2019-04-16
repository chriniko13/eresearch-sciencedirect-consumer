package com.eresearch.elsevier.sciencedirect.consumer.worker.splitter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class ListToSublistsSplitter {

	public <T> List<List<T>> chopped(List<T> list, final int L) {

		List<List<T>> parts = new ArrayList<>();
		final int N = list.size();

		for (int i = 0; i < N; i += L) {
			parts.add(new ArrayList<>(list.subList(i, Math.min(N, i + L))));
		}

		return parts;
	}

}
