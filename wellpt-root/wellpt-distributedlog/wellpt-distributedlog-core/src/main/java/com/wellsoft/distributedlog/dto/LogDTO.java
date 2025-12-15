package com.wellsoft.distributedlog.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月01日   chenq	 Create
 * </pre>
 */
public class LogDTO implements Serializable {
    private static final long serialVersionUID = 8341033788715321422L;

    private String id;
    private String logLevel;
    private String content;
    private String traceId;
    private String spanId;
    private String preApp;
    private String preIp;
    private String className;
    private Date logTime;
    private String app;
    private String ip;

    public static long getSerialVersionUID() {
        return serialVersionUID;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
