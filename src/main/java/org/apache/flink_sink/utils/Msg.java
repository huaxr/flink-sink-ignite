package org.apache.flink_sink.utils;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Msg {
    String Default="MSG";
    String msg() default Default;
}

@Msg(msg = "OK")
class Test{

}

@Msg
class Main{
    protected static String a;
    public Main() {
        Class tClass = this.getClass();
        Msg msg = (Msg) tClass.getAnnotation(Msg.class);
        this.a = msg.Default;
        System.out.println("aaaaaaaaaaaa");
    }
    public static void main(String[] args) {

    }
}