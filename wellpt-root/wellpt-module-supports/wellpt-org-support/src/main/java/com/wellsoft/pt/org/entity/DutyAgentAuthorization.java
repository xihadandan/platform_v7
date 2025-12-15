/*
 * @(#)2013-4-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

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
//@Table(name = "org_duty_agent_auth")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class DutyAgentAuthorization extends IdEntity {
    private static final long serialVersionUID = -3957399221418758844L;

    // 授权的委托人名字
    private String authorisedConsignorName;
    // 授权的委托人ID，只能是一个用户ID
    private String authorisedConsignor;
    // 委托人字
    private String consignorName;
    // 委托人ID，可多个
    private String consignor;
    // 编号
    private String code;
    // 业务类型
    private String businessType;
    // 委托内容显示值
    private String contentName;
    // 委托内容
    private String content;
    // 备注
    private String remark;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the authorisedConsignorName
     */
    public String getAuthorisedConsignorName() {
        return authorisedConsignorName;
    }

    /**
     * @param authorisedConsignorName 要设置的authorisedConsignorName
     */
    public void setAuthorisedConsignorName(String authorisedConsignorName) {
        this.authorisedConsignorName = authorisedConsignorName;
    }

    /**
     * @return the authorisedConsignor
     */
    public String getAuthorisedConsignor() {
        return authorisedConsignor;
    }

    /**
     * @param authorisedConsignor 要设置的authorisedConsignor
     */
    public void setAuthorisedConsignor(String authorisedConsignor) {
        this.authorisedConsignor = authorisedConsignor;
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
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
