package org.apache.flink_sink.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginEvent implements Event{
    @JsonProperty("type")
    private String type;
    @JsonProperty("source_ip")
    private String sourceIp;
    @JsonProperty("target_ip")
    private String targetIp;
    @JsonProperty("source_port")
    private int sourcePort;
    @JsonProperty("target_ip")
    private int targetPort;
    @JsonProperty("alarm_type")
    private String alarmType;
    @JsonProperty("alarm_detail")
    private String alarmDetail;

    @Override
    public String getCacheName() {
        return "LoginCache";
    }

    @Override
    public String getKey() {
        return sourceIp;
    }

    public LoginEvent() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LoginEvent{" +
                "type='" + type + '\'' +
                ", sourceIp='" + sourceIp + '\'' +
                ", targetIp='" + targetIp + '\'' +
                ", sourcePort=" + sourcePort +
                ", targetPort=" + targetPort +
                ", alarmType='" + alarmType + '\'' +
                ", alarmDetail='" + alarmDetail + '\'' +
                '}';
    }
}
