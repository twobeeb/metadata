package io.conduktor.metadata.services;

import io.conduktor.metadata.model.Topic;
import io.conduktor.metadata.model.TopicConfig;
import io.conduktor.metadata.model.TopicPartition;
import io.conduktor.metadata.repositories.TopicConfigRepo;
import io.conduktor.metadata.repositories.TopicPartitionRepo;
import io.conduktor.metadata.repositories.TopicRepo;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicCollection;
import org.apache.kafka.common.config.ConfigResource;
import org.jobrunr.jobs.annotations.Job;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Singleton
public class TopicStateService {

    @Inject
    public TopicRepo topicRepo;
    @Inject
    public TopicConfigRepo topicConfigRepo;
    @Inject
    public TopicPartitionRepo topicPartitionRepo;

    public TopicStateService(TopicRepo topicRepo, TopicConfigRepo topicConfigRepo, TopicPartitionRepo topicPartitionRepo){
        this.topicPartitionRepo = topicPartitionRepo;
        this.topicConfigRepo = topicConfigRepo;
        this.topicRepo = topicRepo;
    }
    @Job(name = "Sync cluster")
    public void doWork(String cluster) throws ExecutionException, InterruptedException {
        doWorkTransactionnal(cluster);
    }

    @Transactional
    public void doWorkTransactionnal(String cluster) throws ExecutionException, InterruptedException {
        Instant t0 = Instant.now();

        // Prepare AdminClient data
        Admin admin = Admin.create(Map.of("bootstrap.servers","34.140.204.135:12092"));
        Set<String> topics_names = admin.listTopics().names()
                .get();
        var describeResponse = admin.describeTopics(TopicCollection.ofTopicNames(topics_names))
                        .allTopicNames().get();

        var configResponse = admin.describeConfigs(topics_names.stream()
                .map(s -> new ConfigResource(ConfigResource.Type.TOPIC, s))
                .collect(Collectors.toList())
        ).all().get().entrySet().stream().map(entry -> Map.entry(entry.getKey().name(), entry.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));

        var partitionBegin = admin.listOffsets(describeResponse.values().stream()
                .flatMap(td -> td.partitions()
                        .stream()
                        .map(tpi -> new org.apache.kafka.common.TopicPartition(td.name(), tpi.partition()))
                )
                .map(t -> Map.entry(t, OffsetSpec.earliest()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).all().get();
        var partitionEnd = admin.listOffsets(describeResponse.values().stream()
                .flatMap(td -> td.partitions()
                        .stream()
                        .map(tpi -> new org.apache.kafka.common.TopicPartition(td.name(), tpi.partition()))
                )
                .map(t -> Map.entry(t, OffsetSpec.latest()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).all().get();

        var brokers = admin.describeCluster().nodes().get().stream().map(Node::id).toList();
        var logsDirResponse = admin.describeLogDirs(brokers).allDescriptions().get()
                .values().stream().flatMap(t->t.values().stream())
                .collect(Collectors.toList());
        var partitionsSizes = logsDirResponse.stream()
                .flatMap(logDirDescription -> logDirDescription.replicaInfos().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (replicaInfo, replicaInfo2) -> replicaInfo));

        var allPartitions = describeResponse.values().stream()
                .flatMap(td -> td.partitions()
                        .stream()
                        .map(tpi -> {
                            org.apache.kafka.common.TopicPartition tp = new org.apache.kafka.common.TopicPartition(td.name(), tpi.partition());
                            long begin = partitionBegin.get(tp).offset();
                            long end = partitionEnd.get(tp).offset();
                            long size = partitionsSizes.get(tp).size();
                            TopicPartition t = new TopicPartition();
                            t.setCluster(cluster);
                            t.setTopic(td.name());
                            t.setPartition(tpi.partition());
                            t.setBeginOffset(begin);
                            t.setEndOffset(end);
                            t.setMessageCount(end-begin);
                            t.setSize(size);
                            return t;
                        })).toList();





        // convert to model
        var allConfigs = configResponse.entrySet().stream()
                .flatMap(kv -> kv.getValue()
                        .entries()
                        .stream()
                        .map(configEntry -> new TopicConfig(cluster, kv.getKey(), configEntry.name(), configEntry.value()))
                )
                .collect(Collectors.toList());
        var  alltopics = describeResponse.values()
                .stream()
                .map(t -> {
                    Topic topic = new Topic();
                    topic.setCluster(cluster);
                    topic.setTopic(t.name());
                    topic.setPartitionCount(t.partitions().size());
                    topic.setReplicationFactor(t.partitions().get(0).replicas().size());
                    topic.setMessageCount(0);
                    topic.setSize(0);
                    return topic;
                })
                .toList();

        Instant t1 = Instant.now();

        topicRepo.deleteAllByCluster(cluster);
        topicRepo.saveAll(alltopics);

        topicConfigRepo.deleteAllByCluster(cluster);
        topicConfigRepo.saveAll(allConfigs);

        topicPartitionRepo.deleteAllByCluster(cluster);
        topicPartitionRepo.saveAll(allPartitions);

        Instant t2 = Instant.now();
        var totalTime = Duration.between(t2, t0);
        var adminTime = Duration.between(t1, t0);
        var dbTime = Duration.between(t2, t1);

        //context.logger().info("Total:"+ totalTime + " ("+alltopics.size()+" topics, "+allConfigs.size()+" configs)");
        System.out.println("Total:"+ totalTime + " ("+alltopics.size()+" topics, "+allConfigs.size()+" configs)");
        System.out.println("  AdminTime:"+ adminTime);
        System.out.println("  DataBTime:"+ dbTime);

    }
}
