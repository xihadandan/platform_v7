/*
 * @(#)10/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 用户待办临时数据表
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/9/25.1	    zhulh		10/9/25		    Create
 * </pre>
 * @date 10/9/25
 */
@Entity
@Table(name = "WF_TASK_TODO_TEMP")
@DynamicUpdate
@DynamicInsert
@ApiModel("用户待办临时数据")
public class WfTaskTodoTempEntity extends SysEntity {
    private static final long serialVersionUID = 4475646588991509732L;

    @ApiModelProperty("流程标题")
    private String title;

    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;

    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;

    @ApiModelProperty("环节实例版本号")
    private Integer taskInstRecVer;

    @ApiModelProperty("用户ID")
    private String userId;

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单数据UUID")
    private String dataUuid;

    @ApiModelProperty("表单数据")
    private String formData;

    @ApiModelProperty("意见立场文本")
    private String opinionLabel;

    @ApiModelProperty("意见立场值")
    private String opinionValue;

    @ApiModelProperty("意见内容")
    private String opinionText;

    @ApiModelProperty("意见附件ID")
    private String opinionFileIds;

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the taskInstRecVer
     */
    public Integer getTaskInstRecVer() {
        return taskInstRecVer;
    }

    /**
     * @param taskInstRecVer 要设置的taskInstRecVer
     */
    public void setTaskInstRecVer(Integer taskInstRecVer) {
        this.taskInstRecVer = taskInstRecVer;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

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
     * @return the formData
     */
    public String getFormData() {
        return formData;
    }

    /**
     * @param formData 要设置的formData
     */
    public void setFormData(String formData) {
        this.formData = formData;
    }

    /**
     * @return the opinionLabel
     */
    public String getOpinionLabel() {
        return opinionLabel;
    }

    /**
     * @param opinionLabel 要设置的opinionLabel
     */
    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

    /**
     * @return the opinionValue
     */
    public String getOpinionValue() {
        return opinionValue;
    }

    /**
     * @param opinionValue 要设置的opinionValue
     */
    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
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
     * @return the opinionFileIds
     */
    public String getOpinionFileIds() {
        return opinionFileIds;
    }

    /**
     * @param opinionFileIds 要设置的opinionFileIds
     */
    public void setOpinionFileIds(String opinionFileIds) {
        this.opinionFileIds = opinionFileIds;
    }

}
