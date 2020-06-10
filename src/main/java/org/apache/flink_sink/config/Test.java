package org.apache.flink_sink.config;

import java.util.Properties;

public class Test {
    public static String TestSourceTopic = "mykafka";

    public static Properties getKafkaProperties() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", "group");
        properties.put("key.serializer","org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer","org.apache.kafka.cpmmon.serialization.StringSerializer");
        return properties;
    }
}
