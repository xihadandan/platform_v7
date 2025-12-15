/*
 * @(#)8/9/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import com.wellsoft.pt.biz.enums.EnumStateHistoryType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
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
 * 8/9/24.1	    zhulh		8/9/24		    Create
 * </pre>
 * @date 8/9/24
 */
@ApiModel("业务流程表单状态历史记录")
@Entity
@Table(name = "BIZ_FORM_STATE_HISTORY")
@DynamicUpdate
@DynamicInsert
public class BizFormStateHistoryEntity extends com.wellsoft.context.jdbc.entity.Entity {

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单数据UUID")
    private String dataUuid;

    @ApiModelProperty("表单数据类型")
    private EnumStateHistoryType type;

    @ApiModelProperty("状态字段")
    private String stateField;

    @ApiModelProperty("旧状态值")
    private String oldState;

    @ApiModelProperty("新状态值")
    private String newState;

    @ApiModelProperty("变更触发类型")
    private String triggerType;

    @ApiModelProperty("变更信息")
    private String triggerInfo;

    @ApiModelProperty("业务主体ID")
    private String entityId;

    @ApiModelProperty("事项实例UUID，类型为事项时有值")
    private String itemInstUuid;

    @ApiModelProperty("过程节点实例UUID，类型为过程节点时有值")
    private String processNodeInstUuid;

    @ApiModelProperty("业务流程实例UUID")
    private String processInstUuid;

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the type
     */
    public EnumStateHistoryType getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(EnumStateHistoryType type) {
        this.type = type;
    }

    /**
     * @return the stateField
     */
    public String getStateField() {
        return stateField;
    }

    /**
     * @param stateField 要设置的stateField
     */
    public void setStateField(String stateField) {
        this.stateField = stateField;
    }

    /**
     * @return the oldState
     */
    public String getOldState() {
        return oldState;
    }

    /**
     * @param oldState 要设置的oldState
     */
    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    /**
     * @return the newState
     */
    public String getNewState() {
        return newState;
    }

    /**
     * @param newState 要设置的newState
     */
    public void setNewState(String newState) {
        this.newState = newState;
    }

    /**
     * @return the triggerType
     */
    public String getTriggerType() {
        return triggerType;
    }

    /**
     * @param triggerType 要设置的triggerType
     */
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    /**
     * @return the triggerInfo
     */
    public String getTriggerInfo() {
        return triggerInfo;
    }

    /**
     * @param triggerInfo 要设置的triggerInfo
     */
    public void setTriggerInfo(String triggerInfo) {
        this.triggerInfo = triggerInfo;
    }

    /**
     * @return the entityId
     */
    public String getEntityId() {
        return entityId;
    }

    /**
     * @param entityId 要设置的entityId
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
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
