package org.apache.flink_sink.anno;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Config {
    String BmqClusterName = "bmq_security_riskctrl";
    String BmqConsumerGroup = "flink_source_consumer_hxr";
    String BmqSourceTopic = "bytesecurity_ignite";
    String PSM = "security.center.flink_tran_sink";
    String Team = "security_team";
    String Owner = "security_hua";
}