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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 业务流程事项实例实体类
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
@ApiModel("业务流程事项实例")
@Entity
@Table(name = "BIZ_PROCESS_ITEM_INSTANCE")
@DynamicUpdate
@DynamicInsert
public class BizProcessItemInstanceEntity extends IdEntity {

    @ApiModelProperty("事项定义名称")
    private String itemDefName;

    @ApiModelProperty("事项定义ID")
    private String itemDefId;

    @ApiModelProperty("事项名称")
    private String itemName;

    @ApiModelProperty("事项编码")
    private String itemCode;

    @ApiModelProperty("事项ID，业务流程事项配置自动生成的ID")
    private String itemId;

    @ApiModelProperty("事项类型")
    private String itemType;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("业务主体名称")
    private String entityName;

    @ApiModelProperty("业务主体ID")
    private String entityId;

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单数据UUID")
    private String dataUuid;

    @ApiModelProperty("开始时间")
    private Date startTime;

    @ApiModelProperty("结束时间")
    private Date endTime;

    @ApiModelProperty("时限类型1、工作日，2、自然日")
    private Integer timeLimitType;

    @ApiModelProperty("时限")
    private Integer timeLimit;

    @ApiModelProperty("总用时")
    private Double totalTime;

    @ApiModelProperty("是否里程碑")
    private Boolean milestone;

    @ApiModelProperty("是否多事项")
    private Boolean multiple;

    @ApiModelProperty("当前状态，00草稿，10运行中，20暂停，30已结束")
    private String state;

    @ApiModelProperty("计时器UUID")
    private String timerUuid;

    @ApiModelProperty("计时器状态，0未启动、1计时中、2暂停、3结束")
    private Integer timerState;

    @ApiModelProperty("计时状态， 0正常、1预警、2到期、3逾期")
    private Integer timingState;

    @ApiModelProperty("到期时间")
    private Date dueTime;

    @ApiModelProperty("上级事项实例UUID")
    private String parentItemInstUuid;

    @ApiModelProperty("归属事项实例UUID，事项拆分时归属的事项实例")
    private String belongItemInstUuid;

    @ApiModelProperty("事项定义UUID")
    private String itemDefUuid;

    @ApiModelProperty("过程节点实例UUID")
    private String processNodeInstUuid;

    @ApiModelProperty("业务流程实例UUID")
    private String processInstUuid;

    @ApiModelProperty("业务流程定义UUID")
    private String processDefUuid;

    @ApiModelProperty("业务流程定义ID")
    private String processDefId;

    @ApiModelProperty("事项流定义ID，事项流定义ID为事项ID")
    private String itemFlowDefId;

    @ApiModelProperty("事项流实例UUID，事项流实例UUID为发起的事项实例UUID")
    private String itemFlowInstUuid;

    /**
     * @return the itemDefName
     */
    public String getItemDefName() {
        return itemDefName;
    }

    /**
     * @param itemDefName 要设置的itemDefName
     */
    public void setItemDefName(String itemDefName) {
        this.itemDefName = itemDefName;
    }

    /**
     * @return the itemDefId
     */
    public String getItemDefId() {
        return itemDefId;
    }

    /**
     * @param itemDefId 要设置的itemDefId
     */
    public void setItemDefId(String itemDefId) {
        this.itemDefId = itemDefId;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName 要设置的itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @param itemCode 要设置的itemCode
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType 要设置的itemType
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @param itemId 要设置的itemId
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
     * @return the timeLimitType
     */
    public Integer getTimeLimitType() {
        return timeLimitType;
    }

    /**
     * @param timeLimitType 要设置的timeLimitType
     */
    public void setTimeLimitType(Integer timeLimitType) {
        this.timeLimitType = timeLimitType;
    }

    /**
     * @return the timeLimit
     */
    public Integer getTimeLimit() {
        return timeLimit;
    }

    /**
     * @param timeLimit 要设置的timeLimit
     */
    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
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
     * @return the milestone
     */
    @Column(name = "IS_MILESTONE")
    public Boolean getMilestone() {
        return milestone;
    }

    /**
     * @param milestone 要设置的milestone
     */
    public void setMilestone(Boolean milestone) {
        this.milestone = milestone;
    }

    /**
     * @return the multiple
     */
    @Column(name = "IS_MULTIPLE")
    public Boolean getMultiple() {
        return multiple;
    }

    /**
     * @param multiple 要设置的multiple
     */
    public void setMultiple(Boolean multiple) {
        this.multiple = multiple;
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
     * @return the parentItemInstUuid
     */
    public String getParentItemInstUuid() {
        return parentItemInstUuid;
    }

    /**
     * @param parentItemInstUuid 要设置的parentItemInstUuid
     */
    public void setParentItemInstUuid(String parentItemInstUuid) {
        this.parentItemInstUuid = parentItemInstUuid;
    }

    /**
     * @return the belongItemInstUuid
     */
    public String getBelongItemInstUuid() {
        return belongItemInstUuid;
    }

    /**
     * @param belongItemInstUuid 要设置的belongItemInstUuid
     */
    public void setBelongItemInstUuid(String belongItemInstUuid) {
        this.belongItemInstUuid = belongItemInstUuid;
    }

    /**
     * @return the itemDefUuid
     */
    public String getItemDefUuid() {
        return itemDefUuid;
    }

    /**
     * @param itemDefUuid 要设置的itemDefUuid
     */
    public void setItemDefUuid(String itemDefUuid) {
        this.itemDefUuid = itemDefUuid;
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

    /**
     * @return the processDefId
     */
    public String getProcessDefId() {
        return processDefId;
    }

    /**
     * @param processDefId 要设置的processDefId
     */
    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    /**
     * @return the itemFlowDefId
     */
    public String getItemFlowDefId() {
        return itemFlowDefId;
    }

    /**
     * @param itemFlowDefId 要设置的itemFlowDefId
     */
    public void setItemFlowDefId(String itemFlowDefId) {
        this.itemFlowDefId = itemFlowDefId;
    }

    /**
     * @return the itemFlowInstUuid
     */
    public String getItemFlowInstUuid() {
        return itemFlowInstUuid;
    }

    /**
     * @param itemFlowInstUuid 要设置的itemFlowInstUuid
     */
    public void setItemFlowInstUuid(String itemFlowInstUuid) {
        this.itemFlowInstUuid = itemFlowInstUuid;
    }

}
