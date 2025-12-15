package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * 飞书事件表实体类
 */
@javax.persistence.Entity
@Table(name = "feishu_event")
@DynamicUpdate
@DynamicInsert
public class FeishuEventEntity extends SysEntity {

    /**
     * 飞书appId
     */
    private String appId;
    /**
     * 处理状态：处理状态：0:待分发，1：处理成功，2：处理失败
     */
    private HandleStatus handleStatus;
    /**
     * 处理结果
     */
    private String handleResult;

    /**
     * 事件id
     */
    private String eventId;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 事件类型名称
     */
    private String eventTypeName;

    /**
     * 事件数据
     */
    private String eventData;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public HandleStatus getHandleStatus() {
        return handleStatus;
    }

    public void setHandleStatus(HandleStatus handleStatus) {
        this.handleStatus = handleStatus;
    }

    public String getHandleResult() {
        return handleResult;
    }

    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getEventData() {
        return eventData;
    }

    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public enum HandleStatus {
        WAIT,
        SUCCESS,
        FAILED
    }
}
