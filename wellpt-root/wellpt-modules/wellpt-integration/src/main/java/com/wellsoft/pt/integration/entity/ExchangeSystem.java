/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 交换数据接入系统
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-15.1	ruanhg		2013-11-15		Create
 * </pre>
 * @date 2013-11-15
 */
@Entity
@Table(name = "is_exchange_system")
@DynamicUpdate
@DynamicInsert
public class ExchangeSystem extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4708149117344033499L;
    //唯一识别号
    private String id;
    //编号用于排序
    private String code;
    //系统名称
    private String name;
    //服务器Ip
    private String serverIp;
    //所属单位（单位uuid）
    private String unitId;
    //数据接收接口地址
    private String receiveUrl;
    //上传结果回调接口地址
    private String sendCallbackUrl;
    //路由回调接口地址
    private String routeCallbackUrl;
    //回复消息告知接口地址
    private String replyMsgUrl;
    //撤销回调地址
    private String cancelCallbackUrl;
    //撤销地址
    private String cancelUrl;
    //新版webService的路径
    private String webServiceUrl;
    //FTP服务器地址
    private String ftpServerUrl;
    //FTP用户名
    private String ftpUserName;
    //FTP密码
    private String ftpUserPassword;
    //FTP路径
    private String ftpFilePath;
    //备注
    private String remark;
    //上报类型
    private String typeId;
    //接收类型
    private String typeId1;
    //是否禁用回调
    private Boolean isCallBack;
    //数字证书主体
    private String subjectDN;
    //对接方式
    private String exchangeType;
    //发送位置   →  in:内网，out:外网
    private String sendPosition;
    //是否启用CA
    private Boolean isEnableCa;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getReceiveUrl() {
        return receiveUrl;
    }

    public void setReceiveUrl(String receiveUrl) {
        this.receiveUrl = receiveUrl;
    }

    public String getSendCallbackUrl() {
        return sendCallbackUrl;
    }

    public void setSendCallbackUrl(String sendCallbackUrl) {
        this.sendCallbackUrl = sendCallbackUrl;
    }

    public String getRouteCallbackUrl() {
        return routeCallbackUrl;
    }

    public void setRouteCallbackUrl(String routeCallbackUrl) {
        this.routeCallbackUrl = routeCallbackUrl;
    }

    public String getReplyMsgUrl() {
        return replyMsgUrl;
    }

    public void setReplyMsgUrl(String replyMsgUrl) {
        this.replyMsgUrl = replyMsgUrl;
    }

    public String getFtpServerUrl() {
        return ftpServerUrl;
    }

    public void setFtpServerUrl(String ftpServerUrl) {
        this.ftpServerUrl = ftpServerUrl;
    }

    public String getFtpUserName() {
        return ftpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        this.ftpUserName = ftpUserName;
    }

    public String getFtpUserPassword() {
        return ftpUserPassword;
    }

    public void setFtpUserPassword(String ftpUserPassword) {
        this.ftpUserPassword = ftpUserPassword;
    }

    public String getFtpFilePath() {
        return ftpFilePath;
    }

    public void setFtpFilePath(String ftpFilePath) {
        this.ftpFilePath = ftpFilePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    /**
     * @return the subjectDN
     */
    public String getSubjectDN() {
        return subjectDN;
    }

    /**
     * @param subjectDN 要设置的subjectDN
     */
    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public String getTypeId1() {
        return typeId1;
    }

    public void setTypeId1(String typeId1) {
        this.typeId1 = typeId1;
    }

    public String getCancelCallbackUrl() {
        return cancelCallbackUrl;
    }

    public void setCancelCallbackUrl(String cancelCallbackUrl) {
        this.cancelCallbackUrl = cancelCallbackUrl;
    }

    public String getCancelUrl() {
        return cancelUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }

    public String getWebServiceUrl() {
        return webServiceUrl;
    }

    public void setWebServiceUrl(String webServiceUrl) {
        this.webServiceUrl = webServiceUrl;
    }

    public Boolean getIsCallBack() {
        return isCallBack;
    }

    public void setIsCallBack(Boolean isCallBack) {
        this.isCallBack = isCallBack;
    }

    public String getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {
        this.exchangeType = exchangeType;
    }

    public String getSendPosition() {
        return sendPosition;
    }

    public void setSendPosition(String sendPosition) {
        this.sendPosition = sendPosition;
    }

    public Boolean getIsEnableCa() {
        return isEnableCa;
    }

    public void setIsEnableCa(Boolean isEnableCa) {
        this.isEnableCa = isEnableCa;
    }

}
