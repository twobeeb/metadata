package io.conduktor.metadata.repositories;

import io.conduktor.metadata.model.TopicConfig;
import io.conduktor.metadata.model.TopicConfigId;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
@Repository
public interface TopicConfigRepo extends CrudRepository<TopicConfig, TopicConfigId> {
    void deleteAllByCluster(String cluster);
}
