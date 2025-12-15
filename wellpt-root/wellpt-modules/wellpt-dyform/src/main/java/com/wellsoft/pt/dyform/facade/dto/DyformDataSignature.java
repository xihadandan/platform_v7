/*
 * @(#)2014-1-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.facade.dto;

import com.wellsoft.pt.repository.entity.FileUpload;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 表单数据签名
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-10.1	zhulh		2014-1-10		Create
 *
 *
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-3.10	 hunt		2014-4-3		添加了fileID字段
 * </pre>
 * @date 2014-1-10
 */
public class DyformDataSignature implements Serializable {

    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILURE = -1;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 978277335246260603L;
    // 文件ID
    private String fileID;

    // 被签名的内容
    private String signedData;
    // 消息摘要
    private String digestValue;
    // 消息摘要算法
    private String digestAlgorithm;
    // 证书
    private String certificate;
    // 签名值
    private String signatureValue;
    // 状态(0签名失败, 1签名成功)
    private int status;
    // 备注
    private String remark;

    // 附件签名信息
    private List<FileUpload> files;

    /**
     * 获取被签名的内容
     *
     * @return
     */
    public String getSignedData() {
        return signedData;
    }

    /**
     * 设置被签名的内容
     *
     * @param signedData
     */
    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }

    /**
     * 获取消息摘要
     *
     * @return the digestValue
     */
    public String getDigestValue() {
        return digestValue;
    }

    /**
     * 设置消息摘要
     *
     * @param digestValue 要设置的digestValue
     */
    public void setDigestValue(String digestValue) {
        this.digestValue = digestValue;
    }

    /**
     * 获取消息摘要算法
     *
     * @return the digestAlgorithm
     */
    public String getDigestAlgorithm() {
        return digestAlgorithm;
    }

    /**
     * 设置消息摘要算法
     *
     * @param digestAlgorithm 要设置的digestAlgorithm
     */
    public void setDigestAlgorithm(String digestAlgorithm) {
        this.digestAlgorithm = digestAlgorithm;
    }

    /**
     * 获取证书
     *
     * @return the certificate
     */
    public String getCertificate() {
        return certificate;
    }

    /**
     * 设置证书
     *
     * @param certificate 要设置的certificate
     */
    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    /**
     * 获取签名值
     *
     * @return the signatureValue
     */
    public String getSignatureValue() {
        return signatureValue;
    }

    /**
     * 设置签名值
     *
     * @param signatureValue 要设置的signatureValue
     */
    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    /**
     * 获取状态(0签名失败, 1签名成功)
     *
     * @return the status
     */
    public int getStatus() {
        return status;
    }

    /**
     * 设置状态(0签名失败, 1签名成功)
     *
     * @param status 要设置的status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 获取备注
     *
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取附件签名信息
     *
     * @return the files
     */
    public List<FileUpload> getFiles() {
        return files;
    }

    /**
     * 设置附件签名信息
     *
     * @param files 要设置的files
     */
    public void setFiles(List<FileUpload> files) {
        this.files = files;
    }

    /**
     * 获取文件ID
     *
     * @return
     */
    public String getFileID() {
        return fileID;
    }

    /**
     * 设置文件ID
     *
     * @param fileID
     */
    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

}
