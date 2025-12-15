/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.pt.integration.entity.ExchangeDataRepest;

import java.io.Serializable;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-9.1	zhulh		2012-11-9		Create
 * </pre>
 * @date 2012-11-9
 */
public class WebServiceMessage implements Serializable {
    private static final long serialVersionUID = -4904847581839790594L;
    public String way;
    public String dataId;
    public Integer recVer;
    public String batchId;
    public String unitId;
    public String typeId;
    public Integer code;
    public String msg;
    public Date date;
    public String userId;
    public String tenantId;
    public Integer operateSource;
    public String fromId;
    public String cc;
    public String bcc;
    public String toId;
    public String text;
    //关联统一查询号的版本
    public Integer correlationRecver;
    public String systemUuid;
    public String restrain;
    public String formDataUuid;
    public String monitorUuid;
    public ExchangeDataRepest exchangeDataRepest;
    public String matterId;
    public String source;
    private long delay;
    //关联统一查询号
    private String correlationDataId;

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Integer getRecVer() {
        return recVer;
    }

    public void setRecVer(Integer recVer) {
        this.recVer = recVer;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public Integer getOperateSource() {
        return operateSource;
    }

    public void setOperateSource(Integer operateSource) {
        this.operateSource = operateSource;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCorrelationDataId() {
        return correlationDataId;
    }

    public void setCorrelationDataId(String correlationDataId) {
        this.correlationDataId = correlationDataId;
    }

    public Integer getCorrelationRecver() {
        return correlationRecver;
    }

    public void setCorrelationRecver(Integer correlationRecver) {
        this.correlationRecver = correlationRecver;
    }

    public String getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }

    public String getRestrain() {
        return restrain;
    }

    public void setRestrain(String restrain) {
        this.restrain = restrain;
    }

    public String getFormDataUuid() {
        return formDataUuid;
    }

    public void setFormDataUuid(String formDataUuid) {
        this.formDataUuid = formDataUuid;
    }

    public String getMonitorUuid() {
        return monitorUuid;
    }

    public void setMonitorUuid(String monitorUuid) {
        this.monitorUuid = monitorUuid;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public ExchangeDataRepest getExchangeDataRepest() {
        return exchangeDataRepest;
    }

    public void setExchangeDataRepest(ExchangeDataRepest exchangeDataRepest) {
        this.exchangeDataRepest = exchangeDataRepest;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

}
