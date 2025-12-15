/*
 * @(#)12/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.itemflow.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/13/23.1	zhulh		12/13/23		Create
 * </pre>
 * @date 12/13/23
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ItemReturnInfo extends BaseObject {
    private static final long serialVersionUID = -6749103659095890299L;

    private String sourceFormUuid;
    private String sourceDataUuid;
    private String targetFormUuid;
    private String targetDataUuid;
    // 事项完成时反馈
    private boolean returnWithOver;
    // 事项发生事件时反馈
    private boolean returnWithEvent;
    // 反馈事件
    private List<String> returnEvents;
    // 反馈规则
    private String returnBotRuleId;

    /**
     * @return the sourceFormUuid
     */
    public String getSourceFormUuid() {
        return sourceFormUuid;
    }

    /**
     * @param sourceFormUuid 要设置的sourceFormUuid
     */
    public void setSourceFormUuid(String sourceFormUuid) {
        this.sourceFormUuid = sourceFormUuid;
    }

    /**
     * @return the sourceDataUuid
     */
    public String getSourceDataUuid() {
        return sourceDataUuid;
    }

    /**
     * @param sourceDataUuid 要设置的sourceDataUuid
     */
    public void setSourceDataUuid(String sourceDataUuid) {
        this.sourceDataUuid = sourceDataUuid;
    }

    /**
     * @return the targetFormUuid
     */
    public String getTargetFormUuid() {
        return targetFormUuid;
    }

    /**
     * @param targetFormUuid 要设置的targetFormUuid
     */
    public void setTargetFormUuid(String targetFormUuid) {
        this.targetFormUuid = targetFormUuid;
    }

    /**
     * @return the targetDataUuid
     */
    public String getTargetDataUuid() {
        return targetDataUuid;
    }

    /**
     * @param targetDataUuid 要设置的targetDataUuid
     */
    public void setTargetDataUuid(String targetDataUuid) {
        this.targetDataUuid = targetDataUuid;
    }

    /**
     * @return the returnWithOver
     */
    public boolean getReturnWithOver() {
        return returnWithOver;
    }

    /**
     * @param returnWithOver 要设置的returnWithOver
     */
    public void setReturnWithOver(boolean returnWithOver) {
        this.returnWithOver = returnWithOver;
    }

    /**
     * @return the returnWithEvent
     */
    public boolean getReturnWithEvent() {
        return returnWithEvent;
    }

    /**
     * @param returnWithEvent 要设置的returnWithEvent
     */
    public void setReturnWithEvent(boolean returnWithEvent) {
        this.returnWithEvent = returnWithEvent;
    }

    /**
     * @return the returnEvents
     */
    public List<String> getReturnEvents() {
        return returnEvents;
    }

    /**
     * @param returnEvents 要设置的returnEvents
     */
    public void setReturnEvents(List<String> returnEvents) {
        this.returnEvents = returnEvents;
    }

    /**
     * @return the returnBotRuleId
     */
    public String getReturnBotRuleId() {
        return returnBotRuleId;
    }

    /**
     * @param returnBotRuleId 要设置的returnBotRuleId
     */
    public void setReturnBotRuleId(String returnBotRuleId) {
        this.returnBotRuleId = returnBotRuleId;
    }
}
