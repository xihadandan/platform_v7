/*
 * @(#)2014-1-5 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.entity;

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
 * 2014-1-5.1	zhulh		2014-1-5		Create
 * </pre>
 * @date 2014-1-5
 */
@Entity
@Table(name = "repo_file_upload")
@DynamicUpdate
@DynamicInsert
public class FileUpload extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6900733021848313583L;

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
    private String moduleName;
    // 结点名称
    private String nodeName;
    //文件夹ID
    private String folderID;
    // 文件名称
    private String filename;
    //文件ID
    private String fileID;

    //文件用途
    private String purpose;

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

    private String origUuid;

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
     * @return the moduleName
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * @param moduleName 要设置的moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName 要设置的nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /*	*//**
     * @return the filename
     */
	/*
	public String getFileName() {
	return this.getFilename();
	}*/

    /**
     * @param filename 要设置的filename
     */
    public void setFilename(String filename) {
        this.filename = filename;
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

    public String getFolderID() {
        return folderID;
    }

    public void setFolderID(String folderID) {
        this.folderID = folderID;
    }

    public String getFileID() {
        return fileID;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getOrigUuid() {
        return origUuid;
    }

    public void setOrigUuid(String origUuid) {
        this.origUuid = origUuid;
    }

}
