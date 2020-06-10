package org.apache.flink_sink.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class Kafka {
    static String kafkaClusterName = "kafka_system_security_lf";
    static String kafkaConsumerGroup = "hxr_test";
    static String kafkaSourceTopic = "event_queue";

    public static Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put(ConsumerConfig.CLUSTER_NAME_CONFIG, kafkaClusterName);
        properties.put(ConsumerConfig.TOPIC_NAME_CONFIG, kafkaSourceTopic);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerGroup);
//        properties.put(ConsumerConfig.PSM_CONFIG, psmSting);
//        properties.put(ConsumerConfig.TEAM_CONFIG, "team1");
//        properties.put(ConsumerConfig.OWNER_CONFIG, "bytedance");
        return properties;
    }
}
