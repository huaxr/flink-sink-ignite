package org.apache.flink_sink.sink;

import java.util.Map;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink_sink.utils.Env;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteDataStreamer;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteIllegalStateException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.internal.A;
import org.apache.ignite.spi.discovery.zk.ZookeeperDiscoverySpi;

/**
 * Apache Flink Ignite sink implemented as a RichSinkFunction.
 */
public class Sinker<IN> extends RichSinkFunction<IN> {
    /** Default flush frequency. */
    private static final long DFLT_FLUSH_FREQ = 10000L;

    /** Logger. */
    private transient IgniteLogger log;

    /** Automatic flush frequency. */
    private long autoFlushFrequency = DFLT_FLUSH_FREQ;

    /** Enables overwriting existing values in cache. */
    private boolean allowOverwrite = false;

    /** Flag for stopped state. */
    private volatile boolean stopped = true;

    /** Ignite instance. */
    protected transient Ignite ignite;

    /** Ignite Data streamer instance. */
    protected transient IgniteDataStreamer streamer;

    /** Ignite grid configuration file. */
    protected final String igniteCfgFile;

    /** Cache name. */
    protected final String cacheName;

    /**
     * Gets the cache name.
     *
     * @return Cache name.
     */
    public String getCacheName() {
        return cacheName;
    }

    /**
     * Gets Ignite configuration file.
     *
     * @return Configuration file.
     */
    public String getIgniteConfigFile() {
        return igniteCfgFile;
    }

    /**
     * Gets the Ignite instance.
     *
     * @return Ignite instance.
     */
    public Ignite getIgnite() {
        return ignite;
    }

    /**
     * Obtains data flush frequency.
     *
     * @return Flush frequency.
     */
    public long getAutoFlushFrequency() {
        return autoFlushFrequency;
    }

    /**
     * Specifies data flush frequency into the grid.
     *
     * @param autoFlushFrequency Flush frequency.
     */
    public void setAutoFlushFrequency(long autoFlushFrequency) {
        this.autoFlushFrequency = autoFlushFrequency;
    }

    /**
     * Obtains flag for enabling overwriting existing values in cache.
     *
     * @return True if overwriting is allowed, false otherwise.
     */
    public boolean getAllowOverwrite() {
        return allowOverwrite;
    }

    /**
     * Enables overwriting existing values in cache.
     *
     * @param allowOverwrite Flag value.
     */
    public void setAllowOverwrite(boolean allowOverwrite) {
        this.allowOverwrite = allowOverwrite;
    }

    /**
     * Default IgniteSink constructor.
     *
     * @param cacheName Cache name.
     */
    public Sinker(String cacheName, String igniteCfgFile) {
        this.cacheName = cacheName;
        this.igniteCfgFile = igniteCfgFile;
    }

    /**
     * Starts streamer.
     *
     * @throws IgniteException If failed.
     */
    @Override
    public void open(Configuration parameter) {
        A.notNull(igniteCfgFile, "Ignite config file");
        A.notNull(cacheName, "Cache name");

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

        this.ignite.getOrCreateCache(cacheName);

        this.log = this.ignite.log();

        this.streamer = this.ignite.dataStreamer(cacheName);
        this.streamer.autoFlushFrequency(autoFlushFrequency);
        this.streamer.allowOverwrite(allowOverwrite);

        stopped = false;
    }

    /**
     * Stops streamer.
     *
     * @throws IgniteException If failed.
     */
    @Override
    public void close() {
        if (stopped)
            return;

        stopped = true;

        this.streamer.close();
    }

    /**
     * Transfers data into grid. It is called when new data
     * arrives to the sink, and forwards it to {@link IgniteDataStreamer}.
     *
     * @param in IN.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void invoke(IN in) {
        try {
            if (!(in instanceof Map))
                throw new IgniteException("Map as a streamer input is expected!");

            this.streamer.addData((Map)in);
        }
        catch (Exception e) {
            log.error("Error while processing IN of " + cacheName, e);
        }
    }
}