package com.bytedance.flink;

import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class TestLoadProperties {
    @Test
    public void testProper() throws Exception {
        Properties props = new Properties();
        InputStream in = new BufferedInputStream(new FileInputStream("resources/loc.properties"));
        props.load(in);
        props.getProperty("a");
    }
}
