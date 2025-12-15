/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:互联网用户
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-04-08.1	zenghw		2021-04-08		Create
 * </pre>
 * @date 2021-04-08
 */
@Entity
@Table(name = "INTERNET_USER_INFO")
@DynamicUpdate
@DynamicInsert
public class InternetUserInfoEntity extends IdEntity {

    private static final long serialVersionUID = 1617867222046L;

    /**
     * // 证照号码由业务决定，自己存自已取
     **/
    private String licenseNumber;
    /**
     * // 单位名称
     **/
    private String unitName;
    /**
     * // 证照类型由业务决定，自己存自已取
     **/
    private String licenseType;

    /**
     * // //用户类型：个人/法人
     * // 0 个人
     * // 1法人
     **/
    private Integer type;
    /**
     * // 对应USER_ACCOUNT的uuid
     **/
    private String accountUuid;

    /**
     * @return the licenseNumber
     */
    public String getLicenseNumber() {
        return this.licenseNumber;
    }

    /**
     * @param licenseNumber
     */
    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return this.unitName;
    }

    /**
     * @param unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    /**
     * @return the licenseType
     */
    public String getLicenseType() {
        return this.licenseType;
    }

    /**
     * @param licenseType
     */
    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the accountUuid
     */
    public String getAccountUuid() {
        return this.accountUuid;
    }

    /**
     * @param accountUuid
     */
    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

}
