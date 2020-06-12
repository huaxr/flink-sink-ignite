package org.apache.flink_sink.stream;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink_sink.config.Bmq;
import org.apache.flink_sink.sink.IgniteSinker;
import org.apache.flink_sink.utils.Env;
import org.apache.ignite.sink.flink.IgniteSink;

import java.util.Properties;

public class Stream {
    public static String configFile = getFile();
    private static StreamExecutionEnvironment env;

    public static StreamExecutionEnvironment initEnv() {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        if (Env.isLocal()) {
            //env.setMaxParallelism(1).setParallelism(1);
            return env;
        }
        //checkpoint配置
        //为了能够使用支持容错的kafka Consumer，开启checkpoint机制，支持容错，保存某个状态信息
        env.enableCheckpointing(5000);
        env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
        env.getCheckpointConfig().setCheckpointTimeout(60000);
        env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
        //env.setMaxParallelism(1).setParallelism(1);
        return env;
    }

    public static StreamExecutionEnvironment getEnv() {
        if (env == null) {
            env = initEnv();
        }
        return env;
    }

    public static DataStream getStream() {
        DataStream<String> appStream;
        if (Env.isLocal()) {
            // 本地socket source
            appStream = getEnv().socketTextStream("localhost", 9999);
        } else {
            // 线上source从kafka
            FlinkKafkaConsumer010<String> kafkaSource = getSource();
            appStream = getEnv().addSource(kafkaSource).name("kafka_source");
        }
        return appStream;
    }

    public static FlinkKafkaConsumer010<String> getSource() {
        Properties properties = Bmq.getKafkaProperties();
        FlinkKafkaConsumer010<String> kafkaSource = new FlinkKafkaConsumer010<String>(Bmq.BmqSourceTopic, new SimpleStringSchema(), properties);
        return kafkaSource;
    }

    public static String getFile() {
        String configFile;
        if (Env.isLocal()) {
            configFile = "loc_ignite.xml";
        } else {
            configFile = "tce_ignite.xml";
        }
        return configFile;
    }

    public static IgniteSinker getSinker() {
        IgniteSinker igniteSink = new IgniteSinker(configFile);
        igniteSink.setAllowOverwrite(true);
        igniteSink.setAutoFlushFrequency(10);
        return igniteSink;
    }

    public static IgniteSink getSink(String cacheName) {
        IgniteSink igniteSink = new IgniteSink(cacheName, configFile);
        igniteSink.setAllowOverwrite(true);
        igniteSink.setAutoFlushFrequency(10);
        return igniteSink;
    }
}
