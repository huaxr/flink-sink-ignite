package org.apache.flink_sink.model;

public interface Event {
     String getCacheName();
     String getKey();
}
