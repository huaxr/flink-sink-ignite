package org.apache.flink_sink.stream;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.util.Collector;
import org.apache.flink_sink.model.Event;
import org.apache.flink_sink.model.AuthEvent;
import org.apache.flink_sink.model.ScanEvent;
import org.apache.flink_sink.model.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class Transaction {
    private static final Logger LOGGER = LoggerFactory.getLogger(Transaction.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    // 数据的Transformations可以将一个或多个DataStream转换为一个新的DataStream。程序可以将多种Transformations组合成复杂的拓扑结构。
    // flatMap 读入一个元素，返回转换后的0个、1个或者多个元素
    // keyBy 逻辑上将流分区为不相交的分区，每个分区包含相同key的元素。
    // timeWindow Windows可定义在已分区的KeyedStreams上。Windows会在每个key对应的数据上根据一些特征（例如，在最近5秒内到达的数据）进行分组。
    // reduce 在一个KeyedStream上不断进行reduce操作。将当前元素与上一个reduce后的值进行合并，再返回新合并的值。
    // map 读入一个元素，返回转换后的一个元素。
    public static class Splitter implements FlatMapFunction<String, Tuple2<String, Integer>> {
        @Override
        public void flatMap(String s, Collector<Tuple2<String, Integer>> out) {
            String[] strings = s.split(" ");
            for (String str : strings) {
                out.collect(new Tuple2<String, Integer>(str, 1));
            }
        }
    }

    public static class Reducer implements ReduceFunction<Tuple2<String, Integer>> {
        @Override
        public Tuple2<String, Integer> reduce(Tuple2<String, Integer> t1, Tuple2<String, Integer> t2) {
            return new Tuple2<String, Integer>(t1.f0, t1.f1 + t2.f1);
        }
    }

    public static class Mapper implements MapFunction<Tuple2<String, Integer>, String> {
        @Override
        public String map(Tuple2<String, Integer> value) throws Exception {
            System.out.println(value);
            return value.f0 + ", " + value.f1;
        }
    }

    public static class Mapper2 implements MapFunction<String, Event> {
        @Override
        public Event map(String value) throws Exception {
            try {
                mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                Map<String, Object> data = mapper.readValue(value, new TypeReference<Map<String, Object>>(){});
                String alarmType = data.getOrDefault("data_type", "").toString();

                Event raw = null;

                if (alarmType.equals("TAG-AUTH")) {
                    raw = mapper.readValue(value, AuthEvent.class);
                }
                else if (alarmType.equals("scan")) {
                    raw = mapper.readValue(value, ScanEvent.class);
                }
                else if (alarmType.equals("window")) {
                    raw = mapper.readValue(value, WindowEvent.class);
                }
                else {
                    System.out.println("该类型暂时没有相关实现:" + alarmType);
                }
                String cache = raw.getCacheName();
                System.out.println("获取type:"+ alarmType);
                System.out.println("获取cache:"+ cache);
                return raw;
            } catch (Exception e){
                LOGGER.error("[*] transaction序列化错误", e);
                return null;
            }
        }
    }
}
