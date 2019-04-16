package com.eresearch.elsevier.sciencedirect.consumer.repository;


import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerDto;
import com.eresearch.elsevier.sciencedirect.consumer.dto.ElsevierScienceDirectConsumerResultsDto;

public interface ScienceDirectConsumerRepository {


    void save(ElsevierScienceDirectConsumerDto elsevierScienceDirectConsumerDto,
              ElsevierScienceDirectConsumerResultsDto elsevierScienceDirectConsumerResultsDto);
}
