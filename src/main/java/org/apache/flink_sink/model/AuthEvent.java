package org.apache.flink_sink.model;


import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthEvent implements Event{
    @JsonProperty("addr")
    private String addr;  // 被登录ip地址

    @JsonProperty("host")
    private String host;

    @JsonProperty("data_type")
    private String dataType;

    @JsonProperty("date_time")
    private String dateTime;

    @JsonProperty("data")
    private String data;

    @JsonProperty("extra")
    private String extra;

    @Override
    public String getCacheName() {
        return "AuthCache";
    }

    @Override
    public String getKey() {
        return host + "-" + addr + "-" + dateTime;
    }

    public AuthEvent() {
        super();
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    @Override
    public String toString() {
        return "AuthEvent{" +
                "addr='" + addr + '\'' +
                ", host='" + host + '\'' +
                ", dataType='" + dataType + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", data='" + data + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}
