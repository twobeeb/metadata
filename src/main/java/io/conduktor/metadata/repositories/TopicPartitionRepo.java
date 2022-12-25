package io.conduktor.metadata.repositories;

import io.conduktor.metadata.model.TopicPartition;
import io.conduktor.metadata.model.TopicPartitionId;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
@Repository
public interface TopicPartitionRepo extends CrudRepository<TopicPartition, TopicPartitionId> {

    void deleteAllByCluster(String cluster);
}
