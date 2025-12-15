package com.wellsoft.pt.integration.bean;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;

/**
 * Description: ExchangeDataBean
 *
 * @author wangbx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-26.1	wangbx		2013-11-26		Create
 * </pre>
 * @date 2013-11-26
 */
public class ExchangeDataBean {

    private DyFormData dyFormData;

    private String cc;

    private String bcc;

    private String toId;

    private String typeId;

    private Integer rel;

    private String exchangeDataUuid;

    private String sendMonitorUuid;

    // 关联统一查询号
    private String correlationDataId;
    // 关联统一查询号的版本
    private Integer correlationRecver;

    private String matterId;

    public DyFormData getDyFormData() {
        return dyFormData;
    }

    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
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

    public Integer getRel() {
        return rel;
    }

    public void setRel(Integer rel) {
        this.rel = rel;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public String getExchangeDataUuid() {
        return exchangeDataUuid;
    }

    public void setExchangeDataUuid(String exchangeDataUuid) {
        this.exchangeDataUuid = exchangeDataUuid;
    }

    public String getSendMonitorUuid() {
        return sendMonitorUuid;
    }

    public void setSendMonitorUuid(String sendMonitorUuid) {
        this.sendMonitorUuid = sendMonitorUuid;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

}
