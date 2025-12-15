/*
 * @(#)8/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 业务主体计时器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/6/24.1	    zhulh		8/6/24		    Create
 * </pre>
 * @date 8/6/24
 */
@ApiModel("业务主体计时器")
@Entity
@Table(name = "BIZ_PROCESS_ENTITY_TIMER")
@DynamicUpdate
@DynamicInsert
public class BizProcessEntityTimerEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private static final long serialVersionUID = 3157993922158742823L;

    @ApiModelProperty("计时器名称")
    private String name;

    @ApiModelProperty("计时器ID")
    private String id;

    @ApiModelProperty("业务主体ID")
    private String entityId;

    @ApiModelProperty("状态字段")
    private String stateField;

    @ApiModelProperty("状态代码")
    private String stateCode;

    @ApiModelProperty("计时器UUID")
    private String timerUuid;

    @ApiModelProperty("计时器状态，0未启动、1计时中、2暂停、3结束")
    private Integer timerState;

    @ApiModelProperty("计时状态， 0正常、1预警、2到期、3逾期")
    private Integer timingState;

    @ApiModelProperty("到期时间")
    private Date dueTime;

    @ApiModelProperty("逾期时间")
    private Date overDueTime;

    @ApiModelProperty("总用时")
    private Double totalTime;

    @ApiModelProperty("业务流程实例UUID")
    private String processInstUuid;

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
     * @return the stateCode
     */
    public String getStateCode() {
        return stateCode;
    }

    /**
     * @param stateCode 要设置的stateCode
     */
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    /**
     * @return the timerUuid
     */
    public String getTimerUuid() {
        return timerUuid;
    }

    /**
     * @param timerUuid 要设置的timerUuid
     */
    public void setTimerUuid(String timerUuid) {
        this.timerUuid = timerUuid;
    }

    /**
     * @return the timerState
     */
    public Integer getTimerState() {
        return timerState;
    }

    /**
     * @param timerState 要设置的timerState
     */
    public void setTimerState(Integer timerState) {
        this.timerState = timerState;
    }

    /**
     * @return the timingState
     */
    public Integer getTimingState() {
        return timingState;
    }

    /**
     * @param timingState 要设置的timingState
     */
    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the overDueTime
     */
    public Date getOverDueTime() {
        return overDueTime;
    }

    /**
     * @param overDueTime 要设置的overDueTime
     */
    public void setOverDueTime(Date overDueTime) {
        this.overDueTime = overDueTime;
    }

    /**
     * @return the totalTime
     */
    public Double getTotalTime() {
        return totalTime;
    }

    /**
     * @param totalTime 要设置的totalTime
     */
    public void setTotalTime(Double totalTime) {
        this.totalTime = totalTime;
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
