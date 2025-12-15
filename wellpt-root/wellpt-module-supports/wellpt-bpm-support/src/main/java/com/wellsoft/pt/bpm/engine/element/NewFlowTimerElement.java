/*
 * @(#)2020年9月23日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年9月23日.1	zhulh		2020年9月23日		Create
 * </pre>
 * @date 2020年9月23日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewFlowTimerElement extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8497853341941048098L;

    // 子流程ID
    private String newFlowId;

    // 计时器名称
    private String newFlowTimerName;

    // 计时器ID
    private String newFlowTimerId;

    /**
     * @return the newFlowId
     */
    public String getNewFlowId() {
        return newFlowId;
    }

    /**
     * @param newFlowId 要设置的newFlowId
     */
    public void setNewFlowId(String newFlowId) {
        this.newFlowId = newFlowId;
    }

    /**
     * @return the newFlowTimerName
     */
    public String getNewFlowTimerName() {
        return newFlowTimerName;
    }

    /**
     * @param newFlowTimerName 要设置的newFlowTimerName
     */
    public void setNewFlowTimerName(String newFlowTimerName) {
        this.newFlowTimerName = newFlowTimerName;
    }

    /**
     * @return the newFlowTimerId
     */
    public String getNewFlowTimerId() {
        return newFlowTimerId;
    }

    /**
     * @param newFlowTimerId 要设置的newFlowTimerId
     */
    public void setNewFlowTimerId(String newFlowTimerId) {
        this.newFlowTimerId = newFlowTimerId;
    }

}
