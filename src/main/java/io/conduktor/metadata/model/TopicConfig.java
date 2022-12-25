package io.conduktor.metadata.model;

import io.micronaut.core.annotation.Nullable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

@Entity
@IdClass(TopicConfigId.class)
public class TopicConfig {
    @Id
    private String cluster;
    @Id
    private String topic;
    @Id
    private String key;

    @Nullable
    private String value;

    public TopicConfig(){}
    public TopicConfig(String cluster, String topic, String key, String value) {
        this.cluster = cluster;
        this.topic = topic;
        this.key = key;
        this.value = value;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
