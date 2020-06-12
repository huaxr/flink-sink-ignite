package org.apache.flink_sink.stream;

import org.apache.flink_sink.anno.Cache;

import java.lang.reflect.Field;


public class CacheName {
    @Cache(description = "登录类型事件缓存", value = "LoginCache", type = "login")
    private static String loginCache;

    @Cache(description = "登录窗口聚合事件缓存", value = "LoginCacheWindows", type = "login_window")
    private static String loginCacheWindow;

    public static String getCacheName(String key) {
        Class c = CacheName.class;
        for(Field f : c.getDeclaredFields()){
            if(f.isAnnotationPresent(Cache.class)){
                Cache annotation = f.getAnnotation(Cache.class);
                if (annotation.type().equals(key)) {  // can not use ==
                    return annotation.value();
                }
            }
        }
        return null;
    }
}
