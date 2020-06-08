package org.apache.flink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

/**
 * 如有需要，可以替换代码中的 Kafka 相关配置信息.
 */
public class KafkaWordCount {

	private final String kafkaClusterName = "kafka_system_security_lf";

	private final String kafkaConsumerGroup = "hxr_test";

	private final String kafkaSourceTopic = "event_queue";

	private final String kafkaSinkTopic = "event_queue";

	public void consumerFromKafka() {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		Properties properties = createKafkaConsumerConfig(kafkaClusterName, kafkaConsumerGroup, kafkaSourceTopic);
		FlinkKafkaConsumer010<String> flinkKafkaConsumer010 =
			new FlinkKafkaConsumer010<>(kafkaSourceTopic, new SimpleStringSchema(), properties);
		DataStream<String> kafkaStream = env.addSource(flinkKafkaConsumer010).setParallelism(10).name("mySource");

		DataStream<String> wordcount = kafkaStream
			.flatMap(new FlatMapFunction<String, Tuple2<String, Integer>>() {
				@Override
				public void flatMap(String s, Collector<Tuple2<String, Integer>> collector) {

					String[] strs = s.split("\\s+");
					for (String str : strs) {
						collector.collect(new Tuple2<String, Integer>(str, 1));
					}
				}
			}).name("flatmap")
			.keyBy(0)
			.timeWindow(Time.seconds(3))
			.reduce(new ReduceFunction<Tuple2<String, Integer>>() {
				@Override
				public Tuple2<String, Integer> reduce(Tuple2<String, Integer> t1, Tuple2<String, Integer> t2) {
					return new Tuple2<String, Integer>(t1.f0, t1.f1 + t2.f1);
				}
			}).name("reduce")
			.map(new MapFunction<Tuple2<String, Integer>, String>() {
				@Override
				public String map(Tuple2<String, Integer> value) throws Exception {
					return value.f0 + ", " + value.f1;
				}
			});
		wordcount.print();
		wordcount.addSink(new FlinkKafkaProducer010<>(kafkaSinkTopic,
			new SimpleStringSchema(), createKafkaProducerConfig(kafkaClusterName)));

		kafkaStream.map(new RichMapFunction<String, Object>() {
			@Override
			public Object map(String s) throws Exception {
				this.getRuntimeContext().getIndexOfThisSubtask();
				return null;
			}
		});

		try {
			env.execute("flink_1.9_test");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a kafka consumer configuration for a given kafka cluster.
	 *
	 * @param kafkaClusterName The name of kafka cluster.
	 * @param consumerGroup    The consumer group that the consumer belongs to.
	 */
	private static Properties createKafkaConsumerConfig(String kafkaClusterName,
														String consumerGroup,
														String topic) {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.CLUSTER_NAME_CONFIG, kafkaClusterName);
		properties.put(ConsumerConfig.TOPIC_NAME_CONFIG, topic);
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroup);
		properties.put(ConsumerConfig.PSM_CONFIG, "security.center.flink_tran_sink");
		properties.put(ConsumerConfig.TEAM_CONFIG, "team1");
		properties.put(ConsumerConfig.OWNER_CONFIG, "bytedance");
		return properties;
	}

	/**
	 * Creates a kafka producer configuration for a given kafka cluster.
	 *
	 * @param kafkaClusterName The name of kafka cluster.
	 */
	public static Properties createKafkaProducerConfig(String kafkaClusterName) {
		Properties properties = new Properties();
		properties.put(ConsumerConfig.CLUSTER_NAME_CONFIG, kafkaClusterName);
		properties.put(ConsumerConfig.PSM_CONFIG, "security.center.flink_tran_sink");
		properties.put(ConsumerConfig.TEAM_CONFIG, "bytedance");
		properties.put(ConsumerConfig.OWNER_CONFIG, "huaxinrui");
		return properties;
	}

	public static void main(String[] args) {
		KafkaWordCount wordCount = new KafkaWordCount();
		wordCount.consumerFromKafka();
	}
}
