/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 业务流程事项实例操作实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
@ApiModel("业务流程事项实例操作")
@Entity
@Table(name = "BIZ_PROCESS_ITEM_OPERATION")
@DynamicUpdate
@DynamicInsert
public class BizProcessItemOperationEntity extends IdEntity {

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("操作人ID")
    private String operatorId;

    @ApiModelProperty("操作类型")
    private String operateType;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("办理意见")
    private String opinionText;

    @ApiModelProperty("办理附件UUID列表，多个以分号隔开")
    private String repoFileUuids;

    @ApiModelProperty("事项实例UUID")
    private String itemInstUuid;

    @ApiModelProperty("过程节点实例UUID")
    private String processNodeInstUuid;

    @ApiModelProperty("业务流程实例UUID")
    private String processInstUuid;

    /**
     * @return the operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName 要设置的operatorName
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * @return the operatorId
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId 要设置的operatorId
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return the operateType
     */
    public String getOperateType() {
        return operateType;
    }

    /**
     * @param operateType 要设置的operateType
     */
    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    /**
     * @return the operateTime
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * @param operateTime 要设置的operateTime
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return the opinionText
     */
    public String getOpinionText() {
        return opinionText;
    }

    /**
     * @param opinionText 要设置的opinionText
     */
    public void setOpinionText(String opinionText) {
        this.opinionText = opinionText;
    }

    /**
     * @return the repoFileUuids
     */
    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    /**
     * @param repoFileUuids 要设置的repoFileUuids
     */
    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }

    /**
     * @return the itemInstUuid
     */
    public String getItemInstUuid() {
        return itemInstUuid;
    }

    /**
     * @param itemInstUuid 要设置的itemInstUuid
     */
    public void setItemInstUuid(String itemInstUuid) {
        this.itemInstUuid = itemInstUuid;
    }

    /**
     * @return the processNodeInstUuid
     */
    public String getProcessNodeInstUuid() {
        return processNodeInstUuid;
    }

    /**
     * @param processNodeInstUuid 要设置的processNodeInstUuid
     */
    public void setProcessNodeInstUuid(String processNodeInstUuid) {
        this.processNodeInstUuid = processNodeInstUuid;
    }

    /**
     * @return the processInstUuid
     */
    public String getProcessInstUuid() {
        return processInstUuid;
    }

    /**
     * @param processInstUuid 要设置的processInstUuid
     */
    public void setProcessInstUuid(String processInstUuid) {
        this.processInstUuid = processInstUuid;
    }

}
