package org.apache.flink_sink.stream;

import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink_sink.anno.Config;
import org.apache.flink_sink.config.Bmq;
import org.apache.ignite.sink.flink.IgniteSink;

import java.util.Map;
import java.util.Properties;

public class App {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static StreamExecutionEnvironment getEnv() {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		if (OS.startsWith("mac")) {
			System.out.println("local env");
			env.setMaxParallelism(1).setParallelism(1);
			return env;
		}
		//checkpoint配置
		//为了能够使用支持容错的kafka Consumer，开启checkpoint机制，支持容错，保存某个状态信息
		env.enableCheckpointing(5000);
		env.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
		env.getCheckpointConfig().setMinPauseBetweenCheckpoints(500);
		env.getCheckpointConfig().setCheckpointTimeout(60000);
		env.getCheckpointConfig().setMaxConcurrentCheckpoints(1);
		env.setMaxParallelism(1).setParallelism(1);
		return env;
	}
	public static FlinkKafkaConsumer010<String> getSource() {
		Properties properties = Bmq.getKafkaProperties();
		FlinkKafkaConsumer010<String> kafkaSource = new FlinkKafkaConsumer010<String>(Bmq.BmqSourceTopic, new SimpleStringSchema(), properties);
		return kafkaSource;
	}

	public static IgniteSink getSink() {
		IgniteSink igniteSink = new IgniteSink("HIDS_SOAR", "default-config.xml");
		igniteSink.setAllowOverwrite(true);
		igniteSink.setAutoFlushFrequency(10);
		return igniteSink;
	}

	public static void main(String[] args) {
		StreamExecutionEnvironment env = getEnv();
		FlinkKafkaConsumer010<String> kafkaSource = getSource();
		DataStream<String> appStream;
		if (OS.startsWith("mac")) {
			appStream = env.socketTextStream("localhost", 9999);
		} else {
			appStream = env.addSource(kafkaSource).setParallelism(10).name("kafka_source");
		}
		DataStream<Map<String,Object>> out = appStream.map(new Transaction.Mapper2());
		out.print();

		IgniteSink igniteSink = getSink();
		out.addSink(igniteSink).name("ignite_sink");

		try {
			env.execute("flink::kafka_to_ignite");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
