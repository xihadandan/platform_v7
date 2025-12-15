/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;

/**
 * Description: 多租户的租户信息类
 *
 * @author ll
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-21.1	ll		2012-10-21		Create
 * </pre>
 * @date 2012-10-21
 */
@Entity
@CommonEntity
@Table(name = "MT_TENANT")
@DynamicUpdate
@DynamicInsert
public class Tenant extends IdEntity {
    // 租户状态(0禁用、1启用、2待审核、3审核失败、4删除)
    public static final Integer STATUS_DISENABLED = 0;
    public static final Integer STATUS_ENABLED = 1;
    public static final Integer STATUS_TO_REVIEW = 2;
    public static final Integer STATUS_REJECT = 3;
    public static final Integer STATUS_DELETED = 4;
    private static final long serialVersionUID = 2058469369113091726L;
    /**
     * 租户名称
     */
    @NotBlank
    private String name;
    /**
     * 租户ID
     */
    @NotBlank
    private String id;
    /**
     * 租户编号
     */
    @NotBlank
    private String code;
    /**
     * 租户账号
     */
    @NotBlank
    private String account;
    /**
     * 租户密码
     */
    @NotBlank
    private String password;
    /**
     * 租户邮箱
     */
    @Email
    private String email;
    /**
     * 租户状态(0禁用、1启用、2待审核、3审核失败、4删除)
     */
    private Integer status;
    /**
     * 描述
     */
    private String remark;
    /**
     * 数据库类型
     */
    private String jdbcType;
    /**
     * 数据库服务地址
     */
    private String jdbcServer;
    /**
     * 数据库服务端口
     */
    private String jdbcPort;
    /**
     * 数据库名
     */
    private String jdbcDatabaseName;
    /**
     * 数据库连接用户名
     */
    private String jdbcUsername;
    /**
     * 数据库连接用户名
     */
    private String jdbcPassword;

    /**
     * 数据库链接URL格式
     */
    private String jdbcUrlFormat;

    /**
     * 数据库配置外键
     */
    private String databaseConfigUuid;

    // /** 单位类别 */
    // private String depatermentType;
    // /** 企业地址 */
    // private String address;
    // /** 邮编 */
    // private String postcode;
    // /** 营业执照注册号 */
    // private String businessLicenseNum;
    // /** 营业执照所在地 */
    // private String businessLicenseAddress;
    // /** 营业期限 */
    // private String businessDeadline;
    // /** 营业期限（是否长期） */
    // private Boolean businessDeadlineCheck;
    // /** 经营范围 */
    // private String businessScope;
    // /** 营业执照副本扫描 */
    // private String businessLicensePic;
    // /** 注册资本 */
    // private String registerFigure;
    // /** 组织机构代码 */
    // private String institutionalCode;
    // /** 运营者身份证姓名 */
    // private String businessIdCardName;
    // /** 运营者身份证号码 */
    // private String businessIdCardNum;
    // /** 手机号码 */
    // @MobilePhone
    // private String mobileNum;
    // /** 职务 */
    // private String position;

    /**身份证照片uuid**/
    /*
     * private String idCardUuid;
     */
    /**
     * 授权运营书uuid
     **/
    /*
     * private String bussinessUuid;
     */
    @UnCloneable
    private TenantTemplate tenantTemplate;

    /**
     * 租户机构名称
     */
    private String orgName;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "tenant_template_uuid")
    public TenantTemplate getTenantTemplate() {
        return tenantTemplate;
    }

    public void setTenantTemplate(TenantTemplate tenantTemplate) {
        this.tenantTemplate = tenantTemplate;
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
     * @return the account
     */
    @Column(nullable = false, unique = true)
    public String getAccount() {
        return account;
    }

    /**
     * @param account 要设置的account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 要设置的password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email 要设置的email
     */
    public void setEmail(String email) {
        this.email = email;
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

    /**
     * @return the jdbcType
     */
    public String getJdbcType() {
        return jdbcType;
    }

    /**
     * @param jdbcType 要设置的jdbcType
     */
    public void setJdbcType(String jdbcType) {
        this.jdbcType = jdbcType;
    }

    /**
     * @return the jdbcServer
     */
    public String getJdbcServer() {
        return jdbcServer;
    }

    /**
     * @param jdbcServer 要设置的jdbcServer
     */
    public void setJdbcServer(String jdbcServer) {
        this.jdbcServer = jdbcServer;
    }

    /**
     * @return the jdbcPort
     */
    public String getJdbcPort() {
        return jdbcPort;
    }

    /**
     * @param jdbcPort 要设置的jdbcPort
     */
    public void setJdbcPort(String jdbcPort) {
        this.jdbcPort = jdbcPort;
    }

    /**
     * @return the jdbcDatabaseName
     */
    public String getJdbcDatabaseName() {
        return jdbcDatabaseName;
    }

    /**
     * @param jdbcDatabaseName 要设置的jdbcDatabaseName
     */
    public void setJdbcDatabaseName(String jdbcDatabaseName) {
        this.jdbcDatabaseName = jdbcDatabaseName;
    }

    /**
     * @return the jdbcUsername
     */
    public String getJdbcUsername() {
        return jdbcUsername;
    }

    /**
     * @param jdbcUsername 要设置的jdbcUsername
     */
    public void setJdbcUsername(String jdbcUsername) {
        this.jdbcUsername = jdbcUsername;
    }

    /**
     * @return the jdbcPassword
     */
    public String getJdbcPassword() {
        return jdbcPassword;
    }

    /**
     * @param jdbcPassword 要设置的jdbcPassword
     */
    public void setJdbcPassword(String jdbcPassword) {
        this.jdbcPassword = jdbcPassword;
    }

    /**
     * @return the jdbcUrlFormat
     */
    public String getJdbcUrlFormat() {
        return jdbcUrlFormat;
    }

    /**
     * @param jdbcUrlFormat 要设置的jdbcUrlFormat
     */
    public void setJdbcUrlFormat(String jdbcUrlFormat) {
        this.jdbcUrlFormat = jdbcUrlFormat;
    }

    /**
     * @return the databaseConfigUuid
     */
    public String getDatabaseConfigUuid() {
        return databaseConfigUuid;
    }

    /**
     * @param databaseConfigUuid 要设置的databaseConfigUuid
     */
    public void setDatabaseConfigUuid(String databaseConfigUuid) {
        this.databaseConfigUuid = databaseConfigUuid;
    }

    //
    // public String getDepatermentType() {
    // return depatermentType;
    // }
    //
    // public void setDepatermentType(String depatermentType) {
    // this.depatermentType = depatermentType;
    // }
    //
    // public String getAddress() {
    // return address;
    // }
    //
    // public void setAddress(String address) {
    // this.address = address;
    // }
    //
    // public String getPostcode() {
    // return postcode;
    // }
    //
    // public void setPostcode(String postcode) {
    // this.postcode = postcode;
    // }
    //
    // public String getBusinessLicenseNum() {
    // return businessLicenseNum;
    // }
    //
    // public void setBusinessLicenseNum(String businessLicenseNum) {
    // this.businessLicenseNum = businessLicenseNum;
    // }
    //
    // public String getBusinessLicenseAddress() {
    // return businessLicenseAddress;
    // }
    //
    // public void setBusinessLicenseAddress(String businessLicenseAddress) {
    // this.businessLicenseAddress = businessLicenseAddress;
    // }
    //
    // public String getBusinessDeadline() {
    // return businessDeadline;
    // }
    //
    // public void setBusinessDeadline(String businessDeadline) {
    // this.businessDeadline = businessDeadline;
    // }
    //
    // public Boolean getBusinessDeadlineCheck() {
    // return businessDeadlineCheck;
    // }
    //
    // public void setBusinessDeadlineCheck(Boolean businessDeadlineCheck) {
    // this.businessDeadlineCheck = businessDeadlineCheck;
    // }
    //
    // public String getBusinessScope() {
    // return businessScope;
    // }
    //
    // public void setBusinessScope(String businessScope) {
    // this.businessScope = businessScope;
    // }
    //
    // public String getBusinessLicensePic() {
    // return businessLicensePic;
    // }
    //
    // public void setBusinessLicensePic(String businessLicensePic) {
    // this.businessLicensePic = businessLicensePic;
    // }
    //
    // public String getRegisterFigure() {
    // return registerFigure;
    // }
    //
    // public void setRegisterFigure(String registerFigure) {
    // this.registerFigure = registerFigure;
    // }
    //
    // public String getInstitutionalCode() {
    // return institutionalCode;
    // }
    //
    // public void setInstitutionalCode(String institutionalCode) {
    // this.institutionalCode = institutionalCode;
    // }
    //
    // public String getBusinessIdCardName() {
    // return businessIdCardName;
    // }
    //
    // public void setBusinessIdCardName(String businessIdCardName) {
    // this.businessIdCardName = businessIdCardName;
    // }
    //
    // public String getBusinessIdCardNum() {
    // return businessIdCardNum;
    // }
    //
    // public void setBusinessIdCardNum(String businessIdCardNum) {
    // this.businessIdCardNum = businessIdCardNum;
    // }
    //
    // public String getMobileNum() {
    // return mobileNum;
    // }
    //
    // public void setMobileNum(String mobileNum) {
    // this.mobileNum = mobileNum;
    // }
    //
    // public String getPosition() {
    // return position;
    // }
    //
    // public void setPosition(String position) {
    // this.position = position;
    // }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    /*
     * public String getIdCardUuid() { return idCardUuid; }
     *
     * public void setIdCardUuid(String idCardUuid) { this.idCardUuid =
     * idCardUuid; }
     *
     * public String getBussinessUuid() { return bussinessUuid; }
     *
     * public void setBussinessUuid(String bussinessUuid) { this.bussinessUuid =
     * bussinessUuid; }
     */

}
