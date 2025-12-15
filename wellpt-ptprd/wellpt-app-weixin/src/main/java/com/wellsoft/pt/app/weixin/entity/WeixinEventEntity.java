/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
@javax.persistence.Entity
@Table(name = "weixin_event")
@DynamicUpdate
@DynamicInsert
public class WeixinEventEntity extends SysEntity {

    // 企业ID
    private String corpId;

    // 处理状态：处理状态：0:待分发，1：处理成功，2：处理失败
    private HandleStatus handleStatus;

    // 处理结果
    private String handleResult;

    // 事件类型
    private String eventType;

    // 事件类型名称
    private String eventTypeName;

    // 事件数据
    private String eventData;

    /**
     * @return the corpId
     */
    public String getCorpId() {
        return corpId;
    }

    /**
     * @param corpId 要设置的corpId
     */
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    /**
     * @return the handleStatus
     */
    public HandleStatus getHandleStatus() {
        return handleStatus;
    }

    /**
     * @param handleStatus 要设置的handleStatus
     */
    public void setHandleStatus(HandleStatus handleStatus) {
        this.handleStatus = handleStatus;
    }

    /**
     * @return the handleResult
     */
    public String getHandleResult() {
        return handleResult;
    }

    /**
     * @param handleResult 要设置的handleResult
     */
    public void setHandleResult(String handleResult) {
        this.handleResult = handleResult;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * @param eventType 要设置的eventType
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the eventTypeName
     */
    public String getEventTypeName() {
        return eventTypeName;
    }

    /**
     * @param eventTypeName 要设置的eventTypeName
     */
    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    /**
     * @return the eventData
     */
    public String getEventData() {
        return eventData;
    }

    /**
     * @param eventData 要设置的eventData
     */
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }

    public enum HandleStatus {
        WAIT,
        SUCCESS,
        FAILED
    }

}
