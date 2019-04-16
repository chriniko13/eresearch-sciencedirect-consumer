package com.eresearch.elsevier.sciencedirect.consumer.connector.guard;

import java.util.Collection;

import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewEntry;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ScienceDirectSearchViewQuery;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(of = {"totalUniqueResults", "scienceDirectSearchViewQuery"})
@Getter
@Setter
class MemoGuard {

    private Integer totalUniqueResults;
    private ScienceDirectSearchViewQuery scienceDirectSearchViewQuery;
    private Collection<ScienceDirectSearchViewEntry> scienceDirectSearchViewEntries;

    MemoGuard(Integer totalUniqueResults, ScienceDirectSearchViewQuery scienceDirectSearchViewQuery, Collection<ScienceDirectSearchViewEntry> scienceDirectSearchViewEntries) {
        this.totalUniqueResults = totalUniqueResults;
        this.scienceDirectSearchViewQuery = scienceDirectSearchViewQuery;
        this.scienceDirectSearchViewEntries = scienceDirectSearchViewEntries;
    }
}