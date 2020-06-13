package com.bytedance.flink;

import org.apache.flink_sink.model.ScanEvent;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestQueryIgnite {
    @Test
    public void testCase() throws Exception {
        Ignite ignite = Ignition.start("loc_ignite.xml");
        List<String> list = new ArrayList<String>(Arrays.asList("LoginCache","ScanCache", "WindowCache"));
        for(String value:list) {
            System.out.println("获取cache:"+ value);
            IgniteCache<String, String> cache =  ignite.getOrCreateCache(value);
            cache.iterator().forEachRemaining(System.out::println);
        }
    }

    @Test
    public void testCase2() throws Exception {
        Ignite ignite = Ignition.start("loc_ignite.xml");
        IgniteCache<String, ScanEvent> cache =  ignite.getOrCreateCache("ScanCache");
        System.out.println(cache.get("1.1.1.33"));
    }
}



