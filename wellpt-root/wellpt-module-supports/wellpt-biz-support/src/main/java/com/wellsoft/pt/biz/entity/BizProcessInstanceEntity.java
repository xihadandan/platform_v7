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
 * Description: 业务流程定义实体类
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
@ApiModel("业务流程实例")
@Entity
@Table(name = "BIZ_PROCESS_INSTANCE")
@DynamicUpdate
@DynamicInsert
public class BizProcessInstanceEntity extends IdEntity {

    @ApiModelProperty("业务流程名称")
    private String name;

    @ApiModelProperty("业务流程ID")
    private String id;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("业务主体名称")
    private String entityName;

    @ApiModelProperty("业务主体ID")
    private String entityId;

    @ApiModelProperty("业务主体表单ID")
    private String entityFormId;

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单数据UUID")
    private String dataUuid;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("当前状态，10运行中，20暂停，30已结束")
    private String state;

    @ApiModelProperty("业务流程定义UUID")
    private String processDefUuid;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName 要设置的entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
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
     * @return the entityFormId
     */
    public String getEntityFormId() {
        return entityFormId;
    }

    /**
     * @param entityFormId 要设置的entityFormId
     */
    public void setEntityFormId(String entityFormId) {
        this.entityFormId = entityFormId;
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
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the processDefUuid
     */
    public String getProcessDefUuid() {
        return processDefUuid;
    }

    /**
     * @param processDefUuid 要设置的processDefUuid
     */
    public void setProcessDefUuid(String processDefUuid) {
        this.processDefUuid = processDefUuid;
    }

}
