package org.apache.flink_sink.stream;

import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import org.apache.flink_sink.sink.IgniteSinker;
import org.apache.flink_sink.model.Event;
import java.util.Map;
import java.util.Objects;

public class App extends Stream{
	public static void main(String[] args) {
		StreamExecutionEnvironment env = getEnv();
		DataStream<String> appStream = getStream();

		DataStream<Event> out = appStream.filter(s -> !s.isEmpty())
				.map(new Transaction.Mapper2())
				.filter(Objects::nonNull)
				.setMaxParallelism(10);
		out.print();

		IgniteSinker igniteSink = getSinker();
		out.addSink(igniteSink).name("ignite_sink");
		try {
			env.execute("flink::kafka_to_ignite");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
