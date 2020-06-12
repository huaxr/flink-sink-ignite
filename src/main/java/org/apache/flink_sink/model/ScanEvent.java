package org.apache.flink_sink.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScanEvent implements Event{

    private String type;
//    private String cacheName; // 写入指定的缓存

    @JsonProperty("source_ip")
    private String sourceIp;
    private String targetIp;
    private int sourcePort;
    private int targetPort;
    private String alarmType;
    private String alarmDetail;
    private int threatIndex; // 威胁指数

    @Override
    public String getCacheName() {
        return "ScanCache";
    }

    @Override
    public String getKey() {
        return sourceIp;
    }

    public ScanEvent() {
        super();
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public int getTargetPort() {
        return targetPort;
    }

    public void setTargetPort(int targetPort) {
        this.targetPort = targetPort;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public String getAlarmDetail() {
        return alarmDetail;
    }

    public void setAlarmDetail(String alarmDetail) {
        this.alarmDetail = alarmDetail;
    }

    public int getThreatIndex() {
        return threatIndex;
    }

    public void setThreatIndex(int threatIndex) {
        this.threatIndex = threatIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ScanEvent{" +
                "type='" + type + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", targetIp='" + targetIp + '\'' +
                ", sourcePort=" + sourcePort +
                ", targetPort=" + targetPort +
                ", alarmType='" + alarmType + '\'' +
                ", alarmDetail='" + alarmDetail + '\'' +
                ", threatIndex=" + threatIndex +
                '}';
    }
}
