/*
 * @(#)2018年8月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
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
 * 2018年8月14日.1	zhulh		2018年8月14日		Create
 * </pre>
 * @date 2018年8月14日
 */
@Entity
@Table(name = "wf_flow_delegation_settings")
@DynamicUpdate
@DynamicInsert
@ApiModel("流程工作委托设置")
public class FlowDelegationSettings extends TenantEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -507983697245177753L;

    public static final Integer STATUS_DEACTIVE = 0;
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_CONSULT = 2;
    public static final Integer STATUS_REFUSE = 3;

    // 委托人名称
    @ApiModelProperty("委托人名称")
    private String consignorName;
    // 委托人ID，只能是一个用户ID
    @ApiModelProperty("委托人ID，只能是一个用户ID")
    private String consignor;
    // 受托人名称
    @ApiModelProperty("受托人名称")
    private String trusteeName;
    // 受托人ID，一个或多个用户ID，多个以分号隔开
    @ApiModelProperty("受托人ID，一个或多个用户ID，多个以分号隔开")
    private String trustee;
    // 委托内容
    @ApiModelProperty("委托内容")
    private String content;
    // 委托内容显示值
    @ApiModelProperty("委托内容显示值")
    private String contentName;
    //    // 委托工作身份的流程
//    @ApiModelProperty("委托工作身份的流程")
//    private String jobIdentity;
    // 委托条件JSON信息
    @ApiModelProperty("委托条件JSON信息")
    private String conditionJson;
    // 包括当前待办工作
    @ApiModelProperty("包括当前待办工作")
    private Boolean includeCurrentWork;
    // 到期自动收回受委托人在委托期间还未处理的待办工作
    @ApiModelProperty("到期自动收回受委托人在委托期间还未处理的待办工作")
    private Boolean dueToTakeBackWork;
    // 手动终止时自动收回受委托人在委托期间还未处理的待办工作
    @ApiModelProperty("手动终止时自动收回受委托人在委托期间还未处理的待办工作")
    private Boolean deactiveToTakeBackWork;
    // 是否允许二次委托
    @ApiModelProperty("是否允许二次委托")
    private Boolean allowSecondaryDelegation;
    // 是否可见委托待办
    @ApiModelProperty("是否可见委托待办")
    private Boolean delegationTaskVisible;
    // 状态0终止，1激活，2征求受托人意见
    @NotNull
    @ApiModelProperty("状态0终止，1激活，2征求受托人意见")
    private Integer status;
    // 开始时间
    @ApiModelProperty("开始时间")
    private Date fromTime;
    // 结束时间
    @ApiModelProperty("结束时间")
    private Date toTime;
    // 征求意见发送的消息ID
    @ApiModelProperty("征求意见发送的消息ID")
    private String consultMessageId;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    /**
     * @return the consignorName
     */
    public String getConsignorName() {
        return consignorName;
    }

    /**
     * @param consignorName 要设置的consignorName
     */
    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    /**
     * @return the consignor
     */
    public String getConsignor() {
        return consignor;
    }

    /**
     * @param consignor 要设置的consignor
     */
    public void setConsignor(String consignor) {
        this.consignor = consignor;
    }

    /**
     * @return the trusteeName
     */
    public String getTrusteeName() {
        return trusteeName;
    }

    /**
     * @param trusteeName 要设置的trusteeName
     */
    public void setTrusteeName(String trusteeName) {
        this.trusteeName = trusteeName;
    }

    /**
     * @return the trustee
     */
    public String getTrustee() {
        return trustee;
    }

    /**
     * @param trustee 要设置的trustee
     */
    public void setTrustee(String trustee) {
        this.trustee = trustee;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the contentName
     */
    public String getContentName() {
        return contentName;
    }

    /**
     * @param contentName 要设置的contentName
     */
    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    /**
     * @return the conditionJson
     */
    public String getConditionJson() {
        return conditionJson;
    }

    /**
     * @param conditionJson 要设置的conditionJson
     */
    public void setConditionJson(String conditionJson) {
        this.conditionJson = conditionJson;
    }

//    /**
//     * @return the jobIdentity
//     */
//    public String getJobIdentity() {
//        return jobIdentity;
//    }
//
//    /**
//     * @param jobIdentity 要设置的jobIdentity
//     */
//    public void setJobIdentity(String jobIdentity) {
//        this.jobIdentity = jobIdentity;
//    }

    /**
     * @return the includeCurrentWork
     */
    public Boolean getIncludeCurrentWork() {
        return includeCurrentWork;
    }

    /**
     * @param includeCurrentWork 要设置的includeCurrentWork
     */
    public void setIncludeCurrentWork(Boolean includeCurrentWork) {
        this.includeCurrentWork = includeCurrentWork;
    }

    /**
     * @return the dueToTakeBackWork
     */
    public Boolean getDueToTakeBackWork() {
        return dueToTakeBackWork;
    }

    /**
     * @param dueToTakeBackWork 要设置的dueToTakeBackWork
     */
    public void setDueToTakeBackWork(Boolean dueToTakeBackWork) {
        this.dueToTakeBackWork = dueToTakeBackWork;
    }

    /**
     * @return the deactiveToTakeBackWork
     */
    public Boolean getDeactiveToTakeBackWork() {
        return deactiveToTakeBackWork;
    }

    /**
     * @param deactiveToTakeBackWork 要设置的deactiveToTakeBackWork
     */
    public void setDeactiveToTakeBackWork(Boolean deactiveToTakeBackWork) {
        this.deactiveToTakeBackWork = deactiveToTakeBackWork;
    }

    /**
     * @return the allowSecondaryDelegation
     */
    public Boolean getAllowSecondaryDelegation() {
        return allowSecondaryDelegation;
    }

    /**
     * @param allowSecondaryDelegation 要设置的allowSecondaryDelegation
     */
    public void setAllowSecondaryDelegation(Boolean allowSecondaryDelegation) {
        this.allowSecondaryDelegation = allowSecondaryDelegation;
    }

    public Boolean getDelegationTaskVisible() {
        return delegationTaskVisible;
    }

    public void setDelegationTaskVisible(Boolean delegationTaskVisible) {
        this.delegationTaskVisible = delegationTaskVisible;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the fromTime
     */
    public Date getFromTime() {
        return fromTime;
    }

    /**
     * @param fromTime 要设置的fromTime
     */
    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    /**
     * @return the toTime
     */
    public Date getToTime() {
        return toTime;
    }

    /**
     * @param toTime 要设置的toTime
     */
    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

    /**
     * @return the consultMessageId
     */
    public String getConsultMessageId() {
        return consultMessageId;
    }

    /**
     * @param consultMessageId 要设置的consultMessageId
     */
    public void setConsultMessageId(String consultMessageId) {
        this.consultMessageId = consultMessageId;
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
