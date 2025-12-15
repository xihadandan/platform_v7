/*
 * @(#)2014-1-18 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
 * 2014-1-18.1	zhulh		2014-1-18		Create
 * </pre>
 * @date 2014-1-18
 */
@Entity
@Table(name = "is_exchange_data_file_upload")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataFileUpload extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4242743934603993957L;

    // 数据类型ID
    private String typeId;
    // 数据类型名称
    private String typeName;
    // 批次ID
    private String batchId;
    // 单位ID
    private String unitId;
    // 单位名称
    private String unitName;

    // 用户ID
    private String userId;
    // 用户名
    private String userName;
    // 部门ID
    private String departmentId;
    // 部门名称
    private String departmentName;
    // 业务类型ID
    private String businessTypeId;
    // 业务类型名称
    private String businessTypeName;
    // 模块名称
    private String fileId;
    // 文件名称
    private String fileName;
    // 文件类型
    private String contentType;
    // 文件大小
    private long fileSize;
    // 上传时间
    private Date uploadTime;
    // 对文件进行签名
    private boolean signUploadFile;
    // 文件消息摘要
    private String digestValue;
    // 消息摘要算法
    private String digestAlgorithm;
    // 证书内容
    private String certificate;
    // 文件消息摘要签名数据
    private String signatureValue;

    /**
     * @return the typeId
     */
    public String getTypeId() {
        return typeId;
    }

    /**
     * @param typeId 要设置的typeId
     */
    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName 要设置的typeName
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the batchId
     */
    public String getBatchId() {
        return batchId;
    }

    /**
     * @param batchId 要设置的batchId
     */
    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    /**
     * @return the unitId
     */
    public String getUnitId() {
        return unitId;
    }

    /**
     * @param unitId 要设置的unitId
     */
    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @param unitName 要设置的unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
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

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the departmentId
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId 要设置的departmentId
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return the departmentName
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * @param departmentName 要设置的departmentName
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * @return the businessTypeId
     */
    public String getBusinessTypeId() {
        return businessTypeId;
    }

    /**
     * @param businessTypeId 要设置的businessTypeId
     */
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    /**
     * @return the businessTypeName
     */
    public String getBusinessTypeName() {
        return businessTypeName;
    }

    /**
     * @param businessTypeName 要设置的businessTypeName
     */
    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
    }

    /**
     * @return the contentType
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * @param contentType 要设置的contentType
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * @return the fileSize
     */
    public long getFileSize() {
        return fileSize;
    }

    /**
     * @param fileSize 要设置的fileSize
     */
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * @return the uploadTime
     */
    public Date getUploadTime() {
        return uploadTime;
    }

    /**
     * @param uploadTime 要设置的uploadTime
     */
    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    /**
     * @return the signUploadFile
     */
    public boolean isSignUploadFile() {
        return signUploadFile;
    }

    /**
     * @param signUploadFile 要设置的signUploadFile
     */
    public void setSignUploadFile(boolean signUploadFile) {
        this.signUploadFile = signUploadFile;
    }

    /**
     * @return the digestValue
     */
    public String getDigestValue() {
        return digestValue;
    }

    /**
     * @param digestValue 要设置的digestValue
     */
    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    /**
     * @return the digestAlgorithm
     */
    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * @param digestAlgorithm 要设置的digestAlgorithm
     */
    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    /**
     * @return the certificate
     */
    @Column(length = 2000)
    public String getCertificate() {
        return certificate;
    }

    /**
     * @param certificate 要设置的certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * @return the signatureValue
     */
    public String getSignatureValue() {
        return signatureValue;
    }

    /**
     * @param signatureValue 要设置的signatureValue
     */
    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
