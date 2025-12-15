/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Date;
import java.util.Set;

/**
 * Description: 流程实例
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
@Entity
@Table(name = "wf_flow_instance")
@DynamicUpdate
@DynamicInsert
public class FlowInstance extends IdEntity {

    private static final long serialVersionUID = -359450656538345401L;

    /**
     * 流程定义id
     */
    private String id;

    /**
     * 流程实例名称
     */
    private String name;

    /**
     * 动态表单定义UUID
     */
    private String formUuid;

    /**
     * 动态表单数据UUID
     */
    private String dataUuid;

    /**
     * 流程定义
     */
    @UnCloneable
    private FlowDefinition flowDefinition;

    /**
     * 标题
     */
    private String title;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 是否计时
     */
    private Boolean isTiming;

    /**
     * 计时状态0正常、1预警、2到期、3逾期
     */
    private Integer timingState;

    /**
     * 是否逾期
     */
    private Boolean isOverDue;

    /**
     * 预警时间
     */
    private Date alarmTime;

    /**
     * 到期时间、承诺时间
     */
    private Date dueTime;

    /**
     * 逾期时间
     */
    private Date overdueTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 流程经历的总时间，以毫秒为单位
     */
    private Long duration;

    /**
     * 流程启动者
     */
    private String startUserId;

    /**
     * 流程实例所有者ID
     */
    private String ownerId;

    /**
     * 流程发起部门ID
     */
    private String startDepartmentId;

    private String startJobId;

    /**
     * 流程所属部门ID
     */
    private String ownerDepartmentId;

    /**
     * 流程发起单位ID
     */
    private String startUnitId;

    /**
     * 流程所属单位ID
     */
    private String ownerUnitId;

    /**
     * 当前流程是否处理活动状态
     */
    private Boolean isActive;

    /**
     * 归属系统ID
     */
    private String systemUnitId;

    /**
     * 当前流程包含的所有环节
     */
    @UnCloneable
    private Set<TaskInstance> taskInstances;

    /**
     * 父结点
     */
    @UnCloneable
    private FlowInstance parent;

    /**
     * 自关联
     */
    @UnCloneable
    private Set<FlowInstance> childrens;

    /**
     * 是否可查看主流程 会存在多个子流程
     * key:子流程Id
     * val:是否可查看主流程
     * 示例格式：{S808:0,S408:1}
     */
    private String viewMainFlowJson;

    /**
     * 预留字段
     **/
    // 255字符长度
    private String reservedText1;
    // 255字符长度
    private String reservedText2;
    // 255字符长度
    private String reservedText3;
    // 255字符长度
    private String reservedText4;
    // 255字符长度
    private String reservedText5;
    // 255字符长度
    private String reservedText6;
    // 255字符长度
    private String reservedText7;
    // 255字符长度
    private String reservedText8;
    // 255字符长度
    private String reservedText9;
    // 255字符长度
    private String reservedText10;
    // 255字符长度
    private String reservedText11;
    // 255字符长度
    private String reservedText12;

    private Integer reservedNumber1;
    private Double reservedNumber2;
    private Double reservedNumber3;

    private Date reservedDate1;
    private Date reservedDate2;

    // 使用的组织版本ID
    private String orgVersionId;

    private String system;
    private String tenant;

    /**
     * 获取流程定义id
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置流程定义id
     *
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取流程实例名称
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置流程实例名称
     *
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取动态表单定义UUID
     *
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * 设置动态表单定义UUID
     *
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * 获取动态表单数据UUID
     *
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * 设置动态表单数据UUID
     *
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * 获取流程定义
     *
     * @return the flowDefinition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flow_def_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public FlowDefinition getFlowDefinition() {
        return flowDefinition;
    }

    /**
     * 设置流程定义
     *
     * @param flowDefinition 要设置的flowDefinition
     */
    public void setFlowDefinition(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    /**
     * 获取标题
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取开始时间
     *
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * 设置开始时间
     *
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    /**
     * 获取是否计时
     *
     * @return the isTiming
     */
    public Boolean getIsTiming() {
        return isTiming;
    }

    /**
     * 设置是否计时
     *
     * @param isTiming 要设置的isTiming
     */
    public void setIsTiming(Boolean isTiming) {
        this.isTiming = isTiming;
    }

    /**
     * 获取计时状态0正常、1预警、2到期、3逾期
     *
     * @return the timingState
     */
    public Integer getTimingState() {
        return timingState;
    }

    /**
     * 设置计时状态0正常、1预警、2到期、3逾期
     *
     * @param timingState 要设置的timingState
     */
    public void setTimingState(Integer timingState) {
        this.timingState = timingState;
    }

    /**
     * 获取是否逾期
     *
     * @return the isOverDue
     */
    public Boolean getIsOverDue() {
        return isOverDue;
    }

    /**
     * 设置是否逾期
     *
     * @param isOverDue 要设置的isOverDue
     */
    public void setIsOverDue(Boolean isOverDue) {
        this.isOverDue = isOverDue;
    }

    /**
     * 获取预警时间
     *
     * @return the alarmTime
     */
    public Date getAlarmTime() {
        return alarmTime;
    }

    /**
     * 设置预警时间
     *
     * @param alarmTime 要设置的alarmTime
     */
    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    /**
     * 获取到期时间、逾期时间、承诺时间
     *
     * @return the dueTime
     */
    public Date getDueTime() {
        return dueTime;
    }

    /**
     * 设置到期时间、逾期时间、承诺时间
     *
     * @param dueTime 要设置的dueTime
     */
    public void setDueTime(Date dueTime) {
        this.dueTime = dueTime;
    }

    /**
     * @return the overdueTime
     */
    public Date getOverdueTime() {
        return overdueTime;
    }

    /**
     * @param overdueTime 要设置的overdueTime
     */
    public void setOverdueTime(Date overdueTime) {
        this.overdueTime = overdueTime;
    }

    /**
     * 获取结束时间
     *
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * 设置结束时间
     *
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * 获取流程经历的总时间，以毫秒为单位
     *
     * @return the duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * 设置流程经历的总时间，以毫秒为单位
     *
     * @param duration 要设置的duration
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * 获取流程启动者
     *
     * @return the startUserId
     */
    public String getStartUserId() {
        return startUserId;
    }

    /**
     * 设置流程启动者
     *
     * @param startUserId 要设置的startUserId
     */
    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    /**
     * 获取流程实例所有者ID
     *
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * 设置流程实例所有者ID
     *
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * 获取流程发起部门ID
     *
     * @return the startDepartmentId
     */
    public String getStartDepartmentId() {
        return startDepartmentId;
    }

    /**
     * 设置流程发起部门ID
     *
     * @param startDepartmentId 要设置的startDepartmentId
     */
    public void setStartDepartmentId(String startDepartmentId) {
        this.startDepartmentId = startDepartmentId;
    }

    /**
     * 获取流程所属部门ID
     *
     * @return the ownerDepartmentId
     */
    public String getOwnerDepartmentId() {
        return ownerDepartmentId;
    }

    /**
     * 设置流程所属部门ID
     *
     * @param ownerDepartmentId 要设置的ownerDepartmentId
     */
    public void setOwnerDepartmentId(String ownerDepartmentId) {
        this.ownerDepartmentId = ownerDepartmentId;
    }

    /**
     * 获取流程发起单位ID
     *
     * @return the startUnitId
     */
    public String getStartUnitId() {
        return startUnitId;
    }

    /**
     * 设置流程发起单位ID
     *
     * @param startUnitId 要设置的startUnitId
     */
    public void setStartUnitId(String startUnitId) {
        this.startUnitId = startUnitId;
    }

    /**
     * 获取流程所属单位ID
     *
     * @return the ownerUnitId
     */
    public String getOwnerUnitId() {
        return ownerUnitId;
    }

    /**
     * 设置流程所属单位ID
     *
     * @param ownerUnitId 要设置的ownerUnitId
     */
    public void setOwnerUnitId(String ownerUnitId) {
        this.ownerUnitId = ownerUnitId;
    }

    /**
     * 获取当前流程是否处理活动状态
     *
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * 设置当前流程是否处理活动状态
     *
     * @param isActive 要设置的isActive
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * 获取归属系统ID
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置归属系统ID
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    //	/**
    //	 * @return the currentTaskInstance
    //	 */
    //	@OneToOne(fetch = FetchType.LAZY, optional = true)
    //	@JoinColumn(name = "current_task_inst_uuid")
    //	public TaskInstance getCurrentTaskInstance() {
    //		return currentTaskInstance;
    //	}
    //
    //	/**
    //	 * @param currentTaskInstance 要设置的currentTaskInstance
    //	 */
    //	public void setCurrentTaskInstance(TaskInstance currentTaskInstance) {
    //		this.currentTaskInstance = currentTaskInstance;
    //	}

    /**
     * 获取当前流程包含的所有环节
     *
     * @return the taskInstances
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flowInstance")
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    @OrderBy("startTime asc")
    public Set<TaskInstance> getTaskInstances() {
        return taskInstances;
    }

    /**
     * 设置当前流程包含的所有环节
     *
     * @param taskInstances 要设置的taskInstances
     */
    public void setTaskInstances(Set<TaskInstance> taskInstances) {
        this.taskInstances = taskInstances;
    }

    /**
     * 获取父流程
     *
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_flow_inst_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public FlowInstance getParent() {
        return parent;
    }

    /**
     * 设置父流程
     *
     * @param parent 要设置的parent
     */
    public void setParent(FlowInstance parent) {
        this.parent = parent;
    }

    /**
     * 获取子流程
     *
     * @return the childrens
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<FlowInstance> getChildrens() {
        return childrens;
    }

    /**
     * 设置子流程
     *
     * @param childrens 要设置的childrens
     */
    public void setChildrens(Set<FlowInstance> childrens) {
        this.childrens = childrens;
    }

    /**
     * 获取预留字段1
     *
     * @return the reservedText1
     */
    public String getReservedText1() {
        return reservedText1;
    }

    /**
     * 设置预留字段1
     *
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    /**
     * 获取预留字段2
     *
     * @return the reservedText2
     */
    public String getReservedText2() {
        return reservedText2;
    }

    /**
     * 设置预留字段2
     *
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    /**
     * 获取预留字段3
     *
     * @return the reservedText3
     */
    public String getReservedText3() {
        return reservedText3;
    }

    /**
     * 设置预留字段3
     *
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    /**
     * 获取预留字段4
     *
     * @return the reservedText4
     */
    public String getReservedText4() {
        return reservedText4;
    }

    /**
     * 设置预留字段4
     *
     * @param reservedText4 要设置的reservedText4
     */
    public void setReservedText4(String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    /**
     * 获取预留字段5
     *
     * @return the reservedText5
     */
    public String getReservedText5() {
        return reservedText5;
    }

    /**
     * 设置预留字段5
     *
     * @param reservedText5 要设置的reservedText5
     */
    public void setReservedText5(String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    /**
     * 获取预留字段6
     *
     * @return the reservedText6
     */
    public String getReservedText6() {
        return reservedText6;
    }

    /**
     * 设置预留字段6
     *
     * @param reservedText6 要设置的reservedText6
     */
    public void setReservedText6(String reservedText6) {
        this.reservedText6 = reservedText6;
    }

    /**
     * 获取预留字段7
     *
     * @return the reservedText7
     */
    public String getReservedText7() {
        return reservedText7;
    }

    /**
     * 设置预留字段7
     *
     * @param reservedText7 要设置的reservedText7
     */
    public void setReservedText7(String reservedText7) {
        this.reservedText7 = reservedText7;
    }

    /**
     * 获取预留字段8
     *
     * @return the reservedText8
     */
    public String getReservedText8() {
        return reservedText8;
    }

    /**
     * 设置预留字段8
     *
     * @param reservedText8 要设置的reservedText8
     */
    public void setReservedText8(String reservedText8) {
        this.reservedText8 = reservedText8;
    }

    /**
     * 获取预留字段9
     *
     * @return the reservedText9
     */
    public String getReservedText9() {
        return reservedText9;
    }

    /**
     * 设置预留字段9
     *
     * @param reservedText9 要设置的reservedText9
     */
    public void setReservedText9(String reservedText9) {
        this.reservedText9 = reservedText9;
    }

    /**
     * 获取预留字段10
     *
     * @return the reservedText10
     */
    public String getReservedText10() {
        return reservedText10;
    }

    /**
     * 设置预留字段10
     *
     * @param reservedText10 要设置的reservedText10
     */
    public void setReservedText10(String reservedText10) {
        this.reservedText10 = reservedText10;
    }

    /**
     * 获取预留字段11
     *
     * @return the reservedText11
     */
    public String getReservedText11() {
        return reservedText11;
    }

    /**
     * 设置预留字段11
     *
     * @param reservedText11 要设置的reservedText11
     */
    public void setReservedText11(String reservedText11) {
        this.reservedText11 = reservedText11;
    }

    /**
     * 获取预留字段12
     *
     * @return the reservedText12
     */
    public String getReservedText12() {
        return reservedText12;
    }

    /**
     * 设置预留字段12
     *
     * @param reservedText12 要设置的reservedText12
     */
    public void setReservedText12(String reservedText12) {
        this.reservedText12 = reservedText12;
    }

    /**
     * 获取预留字段数字1
     *
     * @return the reservedNumber1
     */
    public Integer getReservedNumber1() {
        return reservedNumber1;
    }

    /**
     * 设置预留字段数字1
     *
     * @param reservedNumber1 要设置的reservedNumber1
     */
    public void setReservedNumber1(Integer reservedNumber1) {
        this.reservedNumber1 = reservedNumber1;
    }

    /**
     * 获取预留字段数字2
     *
     * @return the reservedNumber2
     */
    public Double getReservedNumber2() {
        return reservedNumber2;
    }

    /**
     * 设置预留字段数字2
     *
     * @param reservedNumber2 要设置的reservedNumber2
     */
    public void setReservedNumber2(Double reservedNumber2) {
        this.reservedNumber2 = reservedNumber2;
    }

    /**
     * 获取预留字段数字3
     *
     * @return the reservedNumber3
     */
    public Double getReservedNumber3() {
        return reservedNumber3;
    }

    /**
     * 设置预留字段数字3
     *
     * @param reservedNumber3 要设置的reservedNumber3
     */
    public void setReservedNumber3(Double reservedNumber3) {
        this.reservedNumber3 = reservedNumber3;
    }

    /**
     * 获取预留字段日期1
     *
     * @return the reservedDate1
     */
    public Date getReservedDate1() {
        return reservedDate1;
    }

    /**
     * 设置预留字段日期1
     *
     * @param reservedDate1 要设置的reservedDate1
     */
    public void setReservedDate1(Date reservedDate1) {
        this.reservedDate1 = reservedDate1;
    }

    /**
     * 获取预留字段日期2
     *
     * @return the reservedDate2
     */
    public Date getReservedDate2() {
        return reservedDate2;
    }

    /**
     * 设置预留字段日期2
     *
     * @param reservedDate2 要设置的reservedDate2
     */
    public void setReservedDate2(Date reservedDate2) {
        this.reservedDate2 = reservedDate2;
    }

    /**
     * 获取开始职位ID
     *
     * @return
     */
    public String getStartJobId() {
        return startJobId;
    }

    /**
     * 设置开始职位ID
     *
     * @param startJobId
     */
    public void setStartJobId(String startJobId) {
        this.startJobId = startJobId;
    }

    /**
     * 获取是否可查看主流程 会存在多个子流程
     *
     * @return
     */
    public String getViewMainFlowJson() {
        return viewMainFlowJson;
    }

    /**
     * 设置是否可查看主流程 会存在多个子流程
     *
     * @param viewMainFlowJson
     */
    public void setViewMainFlowJson(String viewMainFlowJson) {
        this.viewMainFlowJson = viewMainFlowJson;
    }

    /**
     * @return the orgVersionId
     */
    public String getOrgVersionId() {
        return orgVersionId;
    }

    /**
     * @param orgVersionId 要设置的orgVersionId
     */
    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
