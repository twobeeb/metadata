package io.conduktor.metadata.model;

import java.io.Serializable;

public class TopicId implements Serializable {
    private String cluster;
    private String topic;


    public String getCluster() {
        return cluster;
    }

    public String getTopic() {
        return topic;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
