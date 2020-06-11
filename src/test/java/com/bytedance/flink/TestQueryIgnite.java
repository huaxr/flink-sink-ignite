package com.bytedance.flink;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.junit.Test;

public class TestQueryIgnite {
    @Test
    public void testCase() throws Exception {
        Ignite ignite = Ignition.start("loc_ignite.xml");
        IgniteCache<String, String> cache =  ignite.getOrCreateCache("HIDS_SOAR");
        cache.iterator().forEachRemaining(System.out::println);
    }
}



