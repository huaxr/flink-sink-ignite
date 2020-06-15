package org.apache.flink_sink.sink;

import java.util.*;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink_sink.model.Event;
import org.apache.flink_sink.utils.Env;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteIllegalStateException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;

public class IgniteSinker extends RichSinkFunction<Event> {
    // 定义全局 stream map 缓存
    protected transient Map<String, IgniteDataStreamer> load;
    private static final long DFLT_FLUSH_FREQ = 10000L;
    private transient IgniteLogger log;
    private long autoFlushFrequency = DFLT_FLUSH_FREQ;
    private boolean allowOverwrite = false;
    private volatile boolean stopped = true;
    protected transient Ignite ignite;
    protected final String igniteCfgFile;

    public Ignite getIgnite() {
        return ignite;
    }
    public void setAutoFlushFrequency(long autoFlushFrequency) {
        this.autoFlushFrequency = autoFlushFrequency;
    }
    public void setAllowOverwrite(boolean allowOverwrite) {
        this.allowOverwrite = allowOverwrite;
    }

    // 自定义的 无需传入cacheName参数
    public IgniteSinker(String igniteCfgFile) {
        this.igniteCfgFile = igniteCfgFile;
    }
    @Override
    public void open(Configuration parameter) {
        A.notNull(igniteCfgFile, "Ignite config file");
        try {
            // if an ignite instance is already started in same JVM then use it.
            this.ignite = Ignition.ignite();
        } catch (IgniteIllegalStateException e) {
            // this.ignite = Ignition.start(igniteCfgFile);
            System.out.println("JVM 尝试启动多client, Ignite发生异常:" + e.toString());
            System.out.println("正在使用 getOrStart 避免冲突");
            IgniteConfiguration obj = new IgniteConfiguration();
            obj.setClientMode(true);
            ZookeeperDiscoverySpi spi = new ZookeeperDiscoverySpi();
            String clusterIps;
            if (Env.isLocal()) {
                clusterIps = "10.224.31.148:2181";
            } else {
                clusterIps = "10.224.174.155:2181,10.224.179.150:2181,10.224.181.135:2181,10.224.185.154:2181,10.224.190.155:2181";
            }
            spi.setZkConnectionString(clusterIps);
            spi.setZkRootPath("/apacheIgnite");
            spi.setJoinTimeout(100000);
            spi.setSessionTimeout(100000);
            obj.setDiscoverySpi(spi);
            this.ignite = Ignition.getOrStart(obj);
        }
        // 初始化多个stream
        initStream();
        this.log = this.ignite.log();
        stopped = false;
    }

    @Override
    public void close() {
        if (stopped)
            return;

        stopped = true;
        // close 掉其它stream
        for(IgniteDataStreamer streamer: load.values()) {
            streamer.close();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void invoke(Event in) {
        try {
            String cacheName = in.getCacheName();
            IgniteDataStreamer streamer = load.get(cacheName);
            Map<String, Event> map = new HashMap<>();
            map.put(in.getKey(), in);
            System.out.println(in);
            streamer.addData(map);
        }
        catch (Exception e) {
            log.error("Error while processing IN of " , e);
        }
    }
    // 初始化几个缓存
    public void initStream() {
        load = new HashMap<>();
        List<String> list = new ArrayList<String>(Arrays.asList("AuthCache","ScanCache", "WindowCache"));
        for(String value:list) {
            this.ignite.getOrCreateCache(value);
            IgniteDataStreamer LoginCacheStream = this.ignite.dataStreamer(value);
            LoginCacheStream.autoFlushFrequency(autoFlushFrequency);
            LoginCacheStream.allowOverwrite(allowOverwrite);
            load.put(value, LoginCacheStream);
        }
    }
}
