package io.conduktor.metadata.model;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.annotation.DateCreated;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.util.Date;

@Entity
@IdClass(TopicId.class)
public class Topic {
    @Id
    private String cluster;
    @Id
    private String topic;
    private int partitionCount;
    private int replicationFactor;
    private long messageCount;
    private long size;

    @DateCreated
    @Nullable
    private Date dateCreated;


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

    public int getPartitionCount() {
        return partitionCount;
    }

    public void setPartitionCount(int partitionCount) {
        this.partitionCount = partitionCount;
    }

    public int getReplicationFactor() {
        return replicationFactor;
    }

    public void setReplicationFactor(int replicationFactor) {
        this.replicationFactor = replicationFactor;
    }

    public long getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(long messageCount) {
        this.messageCount = messageCount;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Nullable
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(@Nullable Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
