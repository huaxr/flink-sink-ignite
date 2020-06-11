package org.apache.flink_sink.stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink_sink.config.Bmq;
import org.apache.flink_sink.sink.IgniteSinker;
import org.apache.flink_sink.utils.Env;
import org.apache.ignite.sink.flink.IgniteSink;

import java.util.Map;
import java.util.Objects;
import java.util.Properties;

public class App {
	public static String configFile = getFile();

	public static StreamExecutionEnvironment getEnv() {
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
		IgniteSinker igniteSink = new IgniteSinker("HIDS_SOAR", configFile);
		igniteSink.setAllowOverwrite(true);
		igniteSink.setAutoFlushFrequency(10);
		return igniteSink;
	}

	public static IgniteSink getSink() {
		IgniteSink igniteSink = new IgniteSink("HIDS_SOAR", configFile);
		igniteSink.setAllowOverwrite(true);
		igniteSink.setAutoFlushFrequency(10);
		return igniteSink;
	}

	public static void main(String[] args) {
		StreamExecutionEnvironment env = getEnv();
		DataStream<String> appStream;

		if (Env.isLocal()) {
			// 本地socket source
			appStream = env.socketTextStream("localhost", 9999);
		} else {
			// 线上source从kafka
			FlinkKafkaConsumer010<String> kafkaSource = getSource();
			appStream = env.addSource(kafkaSource).name("kafka_source");
		}

		IgniteSinker igniteSink = getSinker();
		DataStream<Map<String,Object>> out = appStream.filter(s -> !s.isEmpty()).map(new Transaction.Mapper2()).filter(Objects::nonNull).setMaxParallelism(10);
		out.print();
		out.filter(s -> !s.isEmpty()).addSink(igniteSink).name("ignite_sink");

		try {
			env.execute("flink::kafka_to_ignite");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
