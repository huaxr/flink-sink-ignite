package org.apache.flink_sink.config;

import org.apache.flink_sink.anno.Config;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

@Config
public class Bmq {
    public static String BmqSourceTopic = "event_queue";
    public static Properties getKafkaProperties() {
        Properties properties = new Properties();

        Bmq bmq = new Bmq();
        Class bmqClass = bmq.getClass();
        Config conf = (Config) bmqClass.getAnnotation(Config.class);
        System.out.println("配置加载:" +conf.BmqClusterName);
        properties.put(ConsumerConfig.CLUSTER_NAME_CONFIG, conf.BmqClusterName);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, conf.BmqConsumerGroup);
        properties.put(ConsumerConfig.TOPIC_NAME_CONFIG, conf.BmqSourceTopic);
        properties.put(ConsumerConfig.PSM_CONFIG, conf.PSM);
        properties.put(ConsumerConfig.TEAM_CONFIG, conf.Team);
        properties.put(ConsumerConfig.OWNER_CONFIG, conf.Owner);
        return properties;
    }
}