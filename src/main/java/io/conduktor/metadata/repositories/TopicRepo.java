package io.conduktor.metadata.repositories;

import io.conduktor.metadata.model.Topic;
import io.conduktor.metadata.model.TopicId;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

@Repository
public interface TopicRepo extends CrudRepository<Topic, TopicId> {
    void deleteAllByCluster(String cluster);
}
