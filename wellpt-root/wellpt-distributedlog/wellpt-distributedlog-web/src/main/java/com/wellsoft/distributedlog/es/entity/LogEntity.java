package com.wellsoft.distributedlog.es.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
@TypeAlias("log")
public class LogEntity implements Serializable {

    private static final long serialVersionUID = -3599760740169106519L;
    @Id
    private String id;
    @Field(name = "log_level", type = FieldType.Keyword)
    private String logLevel;
    @Field(name = "content", type = FieldType.Text)
    private String content;
    @Field(name = "trace_id", type = FieldType.Keyword)
    private String traceId;
    @Field(name = "span_id", type = FieldType.Keyword)
    private String spanId;
    @Field(name = "pre_app", type = FieldType.Keyword)
    private String preApp;
    @Field(name = "pre_ip", type = FieldType.Ip)
    private String preIp;
    @Field(name = "class_name", type = FieldType.Text)
    private String className;
    @Field(name = "log_time", type = FieldType.Date)
    private Date logTime;
    @Field(name = "app", type = FieldType.Keyword)
    private String app;
    @Field(name = "ip", type = FieldType.Ip)
    private String ip;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getPreApp() {
        return preApp;
    }

    public void setPreApp(String preApp) {
        this.preApp = preApp;
    }

    public String getPreIp() {
        return preIp;
    }

    public void setPreIp(String preIp) {
        this.preIp = preIp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Date getLogTime() {
        return logTime;
    }

    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
