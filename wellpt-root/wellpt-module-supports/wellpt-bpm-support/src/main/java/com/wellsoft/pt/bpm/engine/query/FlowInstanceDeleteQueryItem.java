/*
 * @(#)2015-6-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.jdbc.entity.IdEntity;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-19.1	zhulh		2015-6-19		Create
 * </pre>
 * @date 2015-6-19
 */
public class FlowInstanceDeleteQueryItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5734688730118280091L;

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
     * 是否逾期
     */
    private Boolean isOverDue;

    /**
     * 预警时间
     */
    private Date alarmTime;

    /**
     * 到期时间、逾期时间、承诺时间
     */
    private Date dueTime;

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

    /**
     * 流程定义
     */
    private String flowDefUuid;

    /**
     * 父结点
     */
    private String parentFlowInstUuid;

    // 使用的组织版本ID
    private String orgVersionId;

    private String system;
    private String tenant;

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
     * @return the isTiming
     */
    public Boolean getIsTiming() {
        return isTiming;
    }

    /**
     * @param isTiming 要设置的isTiming
     */
    public void setIsTiming(Boolean isTiming) {
        this.isTiming = isTiming;
    }

    /**
     * @return the isOverDue
     */
    public Boolean getIsOverDue() {
        return isOverDue;
    }

    /**
     * @param isOverDue 要设置的isOverDue
     */
    public void setIsOverDue(Boolean isOverDue) {
        this.isOverDue = isOverDue;
    }

    /**
     * @return the alarmTime
     */
    public Date getAlarmTime() {
        return alarmTime;
    }

    /**
     * @param alarmTime 要设置的alarmTime
     */
    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
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
     * @return the duration
     */
    public Long getDuration() {
        return duration;
    }

    /**
     * @param duration 要设置的duration
     */
    public void setDuration(Long duration) {
        this.duration = duration;
    }

    /**
     * @return the startUserId
     */
    public String getStartUserId() {
        return startUserId;
    }

    /**
     * @param startUserId 要设置的startUserId
     */
    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the startDepartmentId
     */
    public String getStartDepartmentId() {
        return startDepartmentId;
    }

    /**
     * @param startDepartmentId 要设置的startDepartmentId
     */
    public void setStartDepartmentId(String startDepartmentId) {
        this.startDepartmentId = startDepartmentId;
    }

    /**
     * @return the ownerDepartmentId
     */
    public String getOwnerDepartmentId() {
        return ownerDepartmentId;
    }

    /**
     * @param ownerDepartmentId 要设置的ownerDepartmentId
     */
    public void setOwnerDepartmentId(String ownerDepartmentId) {
        this.ownerDepartmentId = ownerDepartmentId;
    }

    /**
     * @return the startUnitId
     */
    public String getStartUnitId() {
        return startUnitId;
    }

    /**
     * @param startUnitId 要设置的startUnitId
     */
    public void setStartUnitId(String startUnitId) {
        this.startUnitId = startUnitId;
    }

    /**
     * @return the ownerUnitId
     */
    public String getOwnerUnitId() {
        return ownerUnitId;
    }

    /**
     * @param ownerUnitId 要设置的ownerUnitId
     */
    public void setOwnerUnitId(String ownerUnitId) {
        this.ownerUnitId = ownerUnitId;
    }

    /**
     * @return the isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive 要设置的isActive
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return the reservedText1
     */
    public String getReservedText1() {
        return reservedText1;
    }

    /**
     * @param reservedText1 要设置的reservedText1
     */
    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    /**
     * @return the reservedText2
     */
    public String getReservedText2() {
        return reservedText2;
    }

    /**
     * @param reservedText2 要设置的reservedText2
     */
    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    /**
     * @return the reservedText3
     */
    public String getReservedText3() {
        return reservedText3;
    }

    /**
     * @param reservedText3 要设置的reservedText3
     */
    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    /**
     * @return the reservedText4
     */
    public String getReservedText4() {
        return reservedText4;
    }

    /**
     * @param reservedText4 要设置的reservedText4
     */
    public void setReservedText4(String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    /**
     * @return the reservedText5
     */
    public String getReservedText5() {
        return reservedText5;
    }

    /**
     * @param reservedText5 要设置的reservedText5
     */
    public void setReservedText5(String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    /**
     * @return the reservedText6
     */
    public String getReservedText6() {
        return reservedText6;
    }

    /**
     * @param reservedText6 要设置的reservedText6
     */
    public void setReservedText6(String reservedText6) {
        this.reservedText6 = reservedText6;
    }

    /**
     * @return the reservedText7
     */
    public String getReservedText7() {
        return reservedText7;
    }

    /**
     * @param reservedText7 要设置的reservedText7
     */
    public void setReservedText7(String reservedText7) {
        this.reservedText7 = reservedText7;
    }

    /**
     * @return the reservedText8
     */
    public String getReservedText8() {
        return reservedText8;
    }

    /**
     * @param reservedText8 要设置的reservedText8
     */
    public void setReservedText8(String reservedText8) {
        this.reservedText8 = reservedText8;
    }

    /**
     * @return the reservedText9
     */
    public String getReservedText9() {
        return reservedText9;
    }

    /**
     * @param reservedText9 要设置的reservedText9
     */
    public void setReservedText9(String reservedText9) {
        this.reservedText9 = reservedText9;
    }

    /**
     * @return the reservedText10
     */
    public String getReservedText10() {
        return reservedText10;
    }

    /**
     * @param reservedText10 要设置的reservedText10
     */
    public void setReservedText10(String reservedText10) {
        this.reservedText10 = reservedText10;
    }

    /**
     * @return the reservedText11
     */
    public String getReservedText11() {
        return reservedText11;
    }

    /**
     * @param reservedText11 要设置的reservedText11
     */
    public void setReservedText11(String reservedText11) {
        this.reservedText11 = reservedText11;
    }

    /**
     * @return the reservedText12
     */
    public String getReservedText12() {
        return reservedText12;
    }

    /**
     * @param reservedText12 要设置的reservedText12
     */
    public void setReservedText12(String reservedText12) {
        this.reservedText12 = reservedText12;
    }

    /**
     * @return the reservedNumber1
     */
    public Integer getReservedNumber1() {
        return reservedNumber1;
    }

    /**
     * @param reservedNumber1 要设置的reservedNumber1
     */
    public void setReservedNumber1(Integer reservedNumber1) {
        this.reservedNumber1 = reservedNumber1;
    }

    /**
     * @return the reservedNumber2
     */
    public Double getReservedNumber2() {
        return reservedNumber2;
    }

    /**
     * @param reservedNumber2 要设置的reservedNumber2
     */
    public void setReservedNumber2(Double reservedNumber2) {
        this.reservedNumber2 = reservedNumber2;
    }

    /**
     * @return the reservedNumber3
     */
    public Double getReservedNumber3() {
        return reservedNumber3;
    }

    /**
     * @param reservedNumber3 要设置的reservedNumber3
     */
    public void setReservedNumber3(Double reservedNumber3) {
        this.reservedNumber3 = reservedNumber3;
    }

    /**
     * @return the reservedDate1
     */
    public Date getReservedDate1() {
        return reservedDate1;
    }

    /**
     * @param reservedDate1 要设置的reservedDate1
     */
    public void setReservedDate1(Date reservedDate1) {
        this.reservedDate1 = reservedDate1;
    }

    /**
     * @return the reservedDate2
     */
    public Date getReservedDate2() {
        return reservedDate2;
    }

    /**
     * @param reservedDate2 要设置的reservedDate2
     */
    public void setReservedDate2(Date reservedDate2) {
        this.reservedDate2 = reservedDate2;
    }

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the parentFlowInstUuid
     */
    public String getParentFlowInstUuid() {
        return parentFlowInstUuid;
    }

    /**
     * @param parentFlowInstUuid 要设置的parentFlowInstUuid
     */
    public void setParentFlowInstUuid(String parentFlowInstUuid) {
        this.parentFlowInstUuid = parentFlowInstUuid;
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

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
