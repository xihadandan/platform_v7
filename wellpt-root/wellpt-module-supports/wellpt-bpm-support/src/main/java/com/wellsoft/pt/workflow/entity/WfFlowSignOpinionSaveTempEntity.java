/*
 * @(#)2021-09-10 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表WF_FLOW_SIGN_OPINION_SAVE_TEMP的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-09-10.1	zenghw		2021-09-10		Create
 * </pre>
 * @date 2021-09-10
 */
@Entity
@Table(name = "WF_FLOW_SIGN_OPINION_SAVE_TEMP")
@DynamicUpdate
@DynamicInsert
public class WfFlowSignOpinionSaveTempEntity extends IdEntity {

    private static final long serialVersionUID = 1631240082875L;

    // 办理意见立场
    private String opinionValue;
    // 流程实例UUID
    private String flowInstUuid;
    // 操作的用户ID
    private String userId;
    // 办理意见内容
    private String opinionText;
    // 办理意见立场文本
    private String opinionLabel;

    /**
     * @return the opinionValue
     */
    public String getOpinionValue() {
        return this.opinionValue;
    }

    /**
     * @param opinionValue
     */
    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return this.flowInstUuid;
    }

    /**
     * @param flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the opinionText
     */
    public String getOpinionText() {
        return this.opinionText;
    }

    /**
     * @param opinionText
     */
    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    /**
     * @return the opinionLabel
     */
    public String getOpinionLabel() {
        return this.opinionLabel;
    }

    /**
     * @param opinionLabel
     */
    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

}
