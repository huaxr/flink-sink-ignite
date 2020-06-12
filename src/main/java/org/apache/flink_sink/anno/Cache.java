package org.apache.flink_sink.anno;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
    String description();
    String value();
    String type();
}
