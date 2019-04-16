package com.eresearch.elsevier.sciencedirect.consumer.dao;

import org.springframework.stereotype.Component;

@Component
public class ScienceDirectConsumerDaoImpl implements ScienceDirectConsumerDao {

    @Override
    public String getInsertQueryForSearchResultsTable() {
        return "INSERT scidir_consumer.search_results(author_name, author_results, links_consumed, first_link, last_link, creation_timestamp) VALUES (?, ?, ?, ?, ?, ?)";
    }

    @Override
    public String getDeleteQueryForSearchResultsTable() {
        return "DELETE FROM scidir_consumer.search_results";
    }

    @Override
    public String getResetAutoIncrementForSearchResultsTable() {
        return "ALTER TABLE scidir_consumer.search_results AUTO_INCREMENT = 1";
    }

    @Override
    public String getSelectQueryForSearchResultsTable() {
        return "SELECT * FROM scidir_consumer.search_results";
    }

    @Override
    public String getCreationQueryForSearchResultsTable() {
        return "CREATE TABLE IF NOT EXISTS scidir_consumer.search_results(id BIGINT(20) UNSIGNED NOT NULL AUTO_INCREMENT, author_name VARCHAR(255) DEFAULT NULL, author_results LONGTEXT, links_consumed MEDIUMTEXT, first_link MEDIUMTEXT, last_link MEDIUMTEXT, creation_timestamp TIMESTAMP NULL DEFAULT NULL, PRIMARY KEY (id), KEY au_id_idx (author_name) ) ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }

    @Override
    public String getDropQueryForSearchResultsTable() {
        return "DROP TABLE IF EXISTS scidir_consumer.search_results";
    }
}
