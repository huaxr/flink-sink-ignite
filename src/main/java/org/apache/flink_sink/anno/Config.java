package org.apache.flink_sink.anno;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Config {
    String BmqClusterName = "kafka_system_security_lf";
    String BmqConsumerGroup = "hxr_test";
    String BmqSourceTopic = "event_queue";
    String BmqSinkTopic = "event_queue";
    String PSM = "security.center.flink_tran_sink";
    String Team = "security_team";
    String Owner = "security_hua";
}