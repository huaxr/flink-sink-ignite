package org.apache.flink_sink.config;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink_sink.stream.Transaction;
import org.apache.ignite.sink.flink.IgniteSink;


public class Tmp {

	public static void main(String[] args) {
		StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
		env.setMaxParallelism(1).setParallelism(1);
//		Properties properties = Test.getKafkaProperties();
//		FlinkKafkaConsumer010<String> kafkaSource = new FlinkKafkaConsumer010<>(Test.TestSourceTopic, new SimpleStringSchema(), properties);

		// 生成
//		DataStream<String> kafkaStream = env.addSource(kafkaSource).setParallelism(10).name("kafka_topic_event_queue");
		DataStream<String> appStream = env.socketTextStream("localhost", 9999);
		// todo add Transaction
		SingleOutputStreamOperator<String> out = appStream.flatMap(new Transaction.Splitter()).name("flatmap")
				.keyBy(0)
				.timeWindow(Time.seconds(3))
				.reduce(new Transaction.Reducer()).name("reduce")
				.map(new Transaction.Mapper()).name("map");
		out.print();
//		FlinkKafkaProducer010<String> kafkaSink = new FlinkKafkaProducer010<>(Bmq.BmqSinkTopic, new SimpleStringSchema(), properties);
		IgniteSink igniteSink = new IgniteSink("HIDS_SOAR", "default-config.xml");
		igniteSink.setAllowOverwrite(true);
		igniteSink.setAutoFlushFrequency(10);
//		igniteSink.start();
		out.forceNonParallel().addSink(igniteSink);
//		out//.name("soar_sink");

		try {
			env.execute("__main__");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
