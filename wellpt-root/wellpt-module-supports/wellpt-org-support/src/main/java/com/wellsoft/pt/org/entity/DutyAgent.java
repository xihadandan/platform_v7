/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Description: 职务代理人
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-15.1	zhulh		2013-4-15		Create
 * </pre>
 * @date 2013-4-15
 */
//@Entity
//@Table(name = "org_duty_agent")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class DutyAgent extends IdEntity {
    public static final Integer STATUS_DEACTIVE = 0;
    public static final Integer STATUS_ACTIVE = 1;
    public static final Integer STATUS_CONSULT = 2;
    private static final long serialVersionUID = -3957399221418758844L;
    // 委托人
    @NotBlank
    private String consignorName;
    // 委托人ID，只能是一个用户ID
    private String consignor;
    // 受托人
    @NotBlank
    private String trusteeName;
    // 受托人ID，一个或多个用户ID，多个以分号隔开
    private String trustee;
    // 编号
    private String code;
    // 业务类型
    private String businessType;
    // 委托内容
    private String content;
    // 委托内容显示值
    private String contentName;
    // 包括当前待办工作
    private Boolean includeCurrentWork;
    // 到期自动收回受委托人在委托期间还未处理的待办工作
    private Boolean dueToTakeBackWork;
    // 委托条件JSON信息
    private String conditionJson;
    // 委托条件
    private String condition;
    // 状态： 0终止 1激活 2征求受托人意见
    @NotNull
    private Integer status;
    // 开始时间
    private Date fromTime;
    // 结束时间
    private Date toTime;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

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
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * @param businessType 要设置的businessType
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    /**
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }

    /**
     * @param condition 要设置的condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
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

}
