package com.bytedance.flink;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink_sink.model.AuthEvent;
import org.apache.flink_sink.stream.CacheName;
import org.junit.Test;

public class TestCacheName {
    @Test
    public void testCase() throws Exception {
        String cache = CacheName.getCacheName("login");
        System.out.println("获取cache:"+ cache);
    }

    @Test
    public void testJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        AuthEvent ev = mapper.readValue("{\"type\":\"login\",\"aa\":\"AA\",\"source_ip\":\"1.1.1.\"}", AuthEvent.class);
        System.out.println(ev);
    }
}
