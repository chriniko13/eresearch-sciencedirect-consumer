package com.eresearch.elsevier.sciencedirect.consumer.connector.pagination;


import com.eresearch.elsevier.sciencedirect.consumer.exception.BusinessProcessingException;

import java.util.List;

public interface ScienceDirectSearchPaginationResourcesCalculator {

    List<String> calculateStartPageQueryParams(String firstResourcePage,
                                               String lastResourcePage,
                                               Integer resourcePageCount) throws BusinessProcessingException;
}
