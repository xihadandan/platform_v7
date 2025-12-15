package com.wellsoft.distributedlog.request;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年07月05日   chenq	 Create
 * </pre>
 */
public class LogRequestParams implements Serializable {

    private String content;

    private String traceId;

    private String app;

    private String preApp;

    private String className;

    private String ip;

    private String preIp;

    private String[] logLevel;

    private Date beginTime;

    private Date endTime;

    private int pageIndex;

    private int pageSize;

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

    public String[] getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String[] logLevel) {
        this.logLevel = logLevel;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getPreApp() {
        return preApp;
    }

    public void setPreApp(String preApp) {
        this.preApp = preApp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPreIp() {
        return preIp;
    }

    public void setPreIp(String preIp) {
        this.preIp = preIp;
    }
}
