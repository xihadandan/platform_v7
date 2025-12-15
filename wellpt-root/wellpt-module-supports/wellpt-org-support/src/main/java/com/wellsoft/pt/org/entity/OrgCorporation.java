/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.RemoteUnique;
import com.wellsoft.context.validator.Telephone;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
//@Entity
//@Table(name = "ORG_CORPORATION")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class OrgCorporation extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1457678034015L;

    // 机构名称
    @NotBlank
    private String name;
    // 机构简称
    @NotBlank
    private String shortName;
    // ID, 由C+10位递增数字组成
    @NotBlank
    @RemoteUnique
    private String id;
    // 机构编码，营业执照代码
    @NotBlank
    private String code;
    // 上级机构UUID
    private String parentUuid;
    // 上级机构名称，冗余字段
    private String parentName;
    // 机构所在的租户ID
    private String tenantId;
    // 成立日期，格式(YYYY-MM-DD)
    private String registerDate;
    // 注册资本，以万为单位
    private Integer registeredCapital;
    // 法人代表
    private String representative;
    // 机构邮件
    @Email
    private String email;
    // 机构传真
    private String fax;
    // 联系电话
    @Telephone
    private String telephone;
    // 联系地址
    private String address;
    // 备注
    private String remark;

    /**
     * @return the name
     */
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public String setName(String name) {
        return this.name = name;
    }

    /**
     * @return the shortName
     */
    @Column(name = "SHORT_NAME")
    public String getShortName() {
        return this.shortName;
    }

    /**
     * @param shortName
     */
    public String setShortName(String shortName) {
        return this.shortName = shortName;
    }

    /**
     * @return the id
     */
    @Column(name = "ID")
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public String setId(String id) {
        return this.id = id;
    }

    /**
     * @return the code
     */
    @Column(name = "CODE")
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public String setCode(String code) {
        return this.code = code;
    }

    /**
     * @return the parentUuid
     */
    @Column(name = "PARENT_UUID")
    public String getParentUuid() {
        return this.parentUuid;
    }

    /**
     * @param parentUuid
     */
    public String setParentUuid(String parentUuid) {
        return this.parentUuid = parentUuid;
    }

    /**
     * @return the parentName
     */
    @Column(name = "PARENT_NAME")
    public String getParentName() {
        return this.parentName;
    }

    /**
     * @param parentName
     */
    public String setParentName(String parentName) {
        return this.parentName = parentName;
    }

    /**
     * @return the tenantId
     */
    @Column(name = "TENANT_ID")
    public String getTenantId() {
        return this.tenantId;
    }

    /**
     * @param tenantId
     */
    public String setTenantId(String tenantId) {
        return this.tenantId = tenantId;
    }

    /**
     * @return the registerDate
     */
    @Column(name = "REGISTER_DATE")
    public String getRegisterDate() {
        return this.registerDate;
    }

    /**
     * @param registerDate
     */
    public String setRegisterDate(String registerDate) {
        return this.registerDate = registerDate;
    }

    /**
     * @return the registeredCapital
     */
    @Column(name = "REGISTERED_CAPITAL")
    public Integer getRegisteredCapital() {
        return this.registeredCapital;
    }

    /**
     * @param registeredCapital
     */
    public Integer setRegisteredCapital(Integer registeredCapital) {
        return this.registeredCapital = registeredCapital;
    }

    /**
     * @return the representative
     */
    @Column(name = "REPRESENTATIVE")
    public String getRepresentative() {
        return this.representative;
    }

    /**
     * @param representative
     */
    public String setRepresentative(String representative) {
        return this.representative = representative;
    }

    /**
     * @return the email
     */
    @Column(name = "EMAIL")
    public String getEmail() {
        return this.email;
    }

    /**
     * @param email
     */
    public String setEmail(String email) {
        return this.email = email;
    }

    /**
     * @return the fax
     */
    @Column(name = "FAX")
    public String getFax() {
        return this.fax;
    }

    /**
     * @param fax
     */
    public String setFax(String fax) {
        return this.fax = fax;
    }

    /**
     * @return the telephone
     */
    @Column(name = "TELEPHONE")
    public String getTelephone() {
        return this.telephone;
    }

    /**
     * @param telephone
     */
    public String setTelephone(String telephone) {
        return this.telephone = telephone;
    }

    /**
     * @return the address
     */
    @Column(name = "ADDRESS")
    public String getAddress() {
        return this.address;
    }

    /**
     * @param address
     */
    public String setAddress(String address) {
        return this.address = address;
    }

    /**
     * @return the remark
     */
    @Column(name = "REMARK")
    public String getRemark() {
        return this.remark;
    }

    /**
     * @param remark
     */
    public String setRemark(String remark) {
        return this.remark = remark;
    }

}
