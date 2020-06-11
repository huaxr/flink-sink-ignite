package org.apache.flink_sink.utils;

public class Env {
    private static String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isLocal() {
        if (OS.startsWith("mac")) {
            System.out.println("local env");
            return true;
        }
        return false;
    }
}
