/*
 * @(#)2017-12-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-12-11.1	zyguo		2017-12-11		Create
 * </pre>
 * @date 2017-12-11
 */
@Entity
@Table(name = "MULTI_ORG_USER_INFO")
@DynamicUpdate
@DynamicInsert
@ApiModel("组织用户对象")
public class MultiOrgUserInfo extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1512981137416L;

    // 对应的USER_ID
    @ApiModelProperty("对应的USER_ID")
    private String userId;
    // EMPLOYEE_NUMBER
    @ApiModelProperty("EMPLOYEE_NUMBER")
    private String employeeNumber;
    // 传真号
    @ApiModelProperty("传真号")
    private String fax;
    // 身份证号
    @ApiModelProperty("身份证号")
    private String idNumber;
    // 手机号
    @ApiModelProperty("手机号")
    private String mobilePhone;
    // 办公电话
    @ApiModelProperty("办公电话")
    private String officePhone;
    // 头像UUID
    @ApiModelProperty("头像UUID")
    private String photoUuid;
    // 性别
    @ApiModelProperty("性别")
    private String sex;
    // 家电电话
    @ApiModelProperty("家电电话")
    private String homePhone;
    // 英文名
    @ApiModelProperty("英文名")
    private String englishName;
    // MAIN_EMAIL
    @ApiModelProperty("MAIN_EMAIL")
    private String mainEmail;
    // 小头像UUID
    @ApiModelProperty("小头像UUID")
    private String smallPhotoUuid;
    // 证书主体
    @MaxLength(max = 2000)
    @ApiModelProperty("证书主体")
    private String certificateSubject;

    /**
     * @return the employeeNumber
     */
    @Column(name = "EMPLOYEE_NUMBER")
    public String getEmployeeNumber() {
        return this.employeeNumber;
    }

    /**
     * @param employeeNumber
     */
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
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
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the idNumber
     */
    @Column(name = "ID_NUMBER")
    public String getIdNumber() {
        return this.idNumber;
    }

    /**
     * @param idNumber
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    /**
     * @return the mobilePhone
     */
    @Column(name = "MOBILE_PHONE")
    public String getMobilePhone() {
        return this.mobilePhone;
    }

    /**
     * @param mobilePhone
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * @return the officePhone
     */
    @Column(name = "OFFICE_PHONE")
    public String getOfficePhone() {
        return this.officePhone;
    }

    /**
     * @param officePhone
     */
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    /**
     * @return the photoUuid
     */
    @Column(name = "PHOTO_UUID")
    public String getPhotoUuid() {
        return this.photoUuid;
    }

    /**
     * @param photoUuid
     */
    public void setPhotoUuid(String photoUuid) {
        this.photoUuid = photoUuid;
    }

    /**
     * @return the sex
     */
    @Column(name = "SEX")
    public String getSex() {
        return this.sex;
    }

    /**
     * @param sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the homePhone
     */
    @Column(name = "HOME_PHONE")
    public String getHomePhone() {
        return this.homePhone;
    }

    /**
     * @param homePhone
     */
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    /**
     * @return the englishName
     */
    @Column(name = "ENGLISH_NAME")
    public String getEnglishName() {
        return this.englishName;
    }

    /**
     * @param englishName
     */
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    /**
     * @return the mainEmail
     */
    @Column(name = "MAIN_EMAIL")
    public String getMainEmail() {
        return this.mainEmail;
    }

    /**
     * @param mainEmail
     */
    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    /**
     * @return the smallPhotoUuid
     */
    @Column(name = "SMALL_PHOTO_UUID")
    public String getSmallPhotoUuid() {
        return this.smallPhotoUuid;
    }

    /**
     * @param smallPhotoUuid
     */
    public void setSmallPhotoUuid(String smallPhotoUuid) {
        this.smallPhotoUuid = smallPhotoUuid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCertificateSubject() {
        return certificateSubject;
    }

    public void setCertificateSubject(String certificateSubject) {
        this.certificateSubject = certificateSubject;
    }

}
