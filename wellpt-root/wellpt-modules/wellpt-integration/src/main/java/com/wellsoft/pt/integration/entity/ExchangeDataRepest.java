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
import java.util.Date;

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
@Table(name = "is_exchange_data_repest")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataRepest extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4275489138927104374L;
    //批次
    private String batchId;
    //数据uuid
    private String dataId;
    //版本号
    private Integer dataRecVer;
    //数据监控uuid
    private String exchangeDataMonitorUuid;

    private Integer code;

    private String msg;

    private String systemUuid;

    private String unitId;

    private String fromId;

    private Date opTime;

    //ing 发送中 fail失败 success成功
    private String status;
    //重发次数
    private Integer retransmissionNum;
    //间隔秒数
    private Integer interval;
    //已经发的次数
    private Integer sendNum;
    //重发的事件1、 callbackClient 2、receiveClient 3、routeCallBackClient 4、replyClient 5、cancel 6、cancelClient
    private String sendMethod;

    private String userId;

    private String tenantId;

    private String matterId;

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getExchangeDataMonitorUuid() {
        return exchangeDataMonitorUuid;
    }

    public void setExchangeDataMonitorUuid(String exchangeDataMonitorUuid) {
        this.exchangeDataMonitorUuid = exchangeDataMonitorUuid;
    }

    public Integer getRetransmissionNum() {
        return retransmissionNum;
    }

    public void setRetransmissionNum(Integer retransmissionNum) {
        this.retransmissionNum = retransmissionNum;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSendMethod() {
        return sendMethod;
    }

    public void setSendMethod(String sendMethod) {
        this.sendMethod = sendMethod;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getDataRecVer() {
        return dataRecVer;
    }

    public void setDataRecVer(Integer dataRecVer) {
        this.dataRecVer = dataRecVer;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public Date getOpTime() {
        return opTime;
    }

    public void setOpTime(Date opTime) {
        this.opTime = opTime;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

}
