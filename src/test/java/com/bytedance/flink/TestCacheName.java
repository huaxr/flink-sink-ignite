package com.bytedance.flink;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink_sink.model.LoginEvent;
import org.apache.flink_sink.stream.CacheName;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
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
        LoginEvent ev = mapper.readValue("{\"type\":\"login\",\"aa\":\"AA\",\"source_ip\":\"1.1.1.\"}", LoginEvent.class);
        System.out.println(ev);
    }
}
