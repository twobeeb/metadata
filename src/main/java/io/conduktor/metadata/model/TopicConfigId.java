package io.conduktor.metadata.model;

import java.io.Serializable;

public class TopicConfigId implements Serializable {
    private String cluster;
    private String topic;
    private String key;

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
}
