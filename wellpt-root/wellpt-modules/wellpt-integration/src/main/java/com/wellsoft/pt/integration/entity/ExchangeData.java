/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Clob;
import java.util.Date;
import java.util.Set;

/**
 * Description: 交换数据
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
@Table(name = "is_exchange_data")
@DynamicUpdate
@DynamicInsert
public class ExchangeData extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -923794404927579358L;

    @UnCloneable
    private ExchangeDataBatch exchangeDataBatch;//批次号

    @UnCloneable
    private Set<ExchangeDataSendMonitor> exchangeDataSendMonitors;//发送监控

    private String dataId;//统一查询号

    private Integer dataRecVer;//版本号

    private Clob text;//XML数据

    private String correlationDataId;//关联统一查询号

    private Integer correlationRecver;//关联统一查询号的版本

    private String formId;//发送的表单id

    private String formDataId;//发送的表单数据id

    private String signDigest;//签名摘要

    private String reservedText1;//商事主体名称

    private String reservedText2;//标题

    private String reservedText3;//登记时间

    private String reservedNumber1;//成立时间/更新时间

    private String reservedNumber2;//注册号

    private String reservedText4;//企业类型/证件号

    private String reservedText5;//法定代表人

    private String reservedText6;//主体状态

    private String reservedText7;//统一社会信用代码

    private String reservedText8;//

    private String newestData;//yes no

    private String validData;//有效数据

    private String drafter;//起草人

    private Date draftTime;//起草时间

    private String releaser;//发布人

    private Date releaseTime;//发布时间

    private Integer uploadLimitNum;//上报逾期天数

    private String examineFailMsg;//审核被退回原因

    private String contentStatus;//主体状态，登记状态（行政许可）

    private String matterId;

    private Boolean historyData;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exchangeData")
    @Cascade(value = {CascadeType.ALL})
    public Set<ExchangeDataSendMonitor> getExchangeDataSendMonitors() {
        return exchangeDataSendMonitors;
    }

    public void setExchangeDataSendMonitors(Set<ExchangeDataSendMonitor> exchangeDataSendMonitors) {
        this.exchangeDataSendMonitors = exchangeDataSendMonitors;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    public ExchangeDataBatch getExchangeDataBatch() {
        return exchangeDataBatch;
    }

    public void setExchangeDataBatch(ExchangeDataBatch exchangeDataBatch) {
        this.exchangeDataBatch = exchangeDataBatch;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public Clob getText() {
        return text;
    }

    public void setText(Clob text) {
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

    public String getFormDataId() {
        return formDataId;
    }

    public void setFormDataId(String formDataId) {
        this.formDataId = formDataId;
    }

    public String getSignDigest() {
        return signDigest;
    }

    public void setSignDigest(String signDigest) {
        this.signDigest = signDigest;
    }

    public String getReservedText1() {
        return reservedText1;
    }

    public void setReservedText1(String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    public String getReservedText2() {
        return reservedText2;
    }

    public void setReservedText2(String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    public String getReservedText3() {
        return reservedText3;
    }

    public void setReservedText3(String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    public String getReservedNumber1() {
        return reservedNumber1;
    }

    public void setReservedNumber1(String reservedNumber1) {
        this.reservedNumber1 = reservedNumber1;
    }

    public String getReservedNumber2() {
        return reservedNumber2;
    }

    public void setReservedNumber2(String reservedNumber2) {
        this.reservedNumber2 = reservedNumber2;
    }

    public String getNewestData() {
        return newestData;
    }

    public void setNewestData(String newestData) {
        this.newestData = newestData;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getReservedText4() {
        return reservedText4;
    }

    public void setReservedText4(String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    public String getReservedText5() {
        return reservedText5;
    }

    public void setReservedText5(String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    public String getReservedText6() {
        return reservedText6;
    }

    public void setReservedText6(String reservedText6) {
        this.reservedText6 = reservedText6;
    }

    public String getDrafter() {
        return drafter;
    }

    public void setDrafter(String drafter) {
        this.drafter = drafter;
    }

    public Date getDraftTime() {
        return draftTime;
    }

    public void setDraftTime(Date draftTime) {
        this.draftTime = draftTime;
    }

    public String getReleaser() {
        return releaser;
    }

    public void setReleaser(String releaser) {
        this.releaser = releaser;
    }

    public Integer getUploadLimitNum() {
        return uploadLimitNum;
    }

    public void setUploadLimitNum(Integer uploadLimitNum) {
        this.uploadLimitNum = uploadLimitNum;
    }

    public String getContentStatus() {
        return contentStatus;
    }

    public void setContentStatus(String contentStatus) {
        this.contentStatus = contentStatus;
    }

    public Integer getDataRecVer() {
        return dataRecVer;
    }

    public void setDataRecVer(Integer dataRecVer) {
        this.dataRecVer = dataRecVer;
    }

    public String getExamineFailMsg() {
        return examineFailMsg;
    }

    public void setExamineFailMsg(String examineFailMsg) {
        this.examineFailMsg = examineFailMsg;
    }

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getValidData() {
        return validData;
    }

    public void setValidData(String validData) {
        this.validData = validData;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

    public Boolean getHistoryData() {
        return historyData;
    }

    public void setHistoryData(Boolean historyData) {
        this.historyData = historyData;
    }

    /**
     * @return the reservedText7
     */
    public String getReservedText7() {
        return reservedText7;
    }

    /**
     * @param reservedText7 要设置的reservedText7
     */
    public void setReservedText7(String reservedText7) {
        this.reservedText7 = reservedText7;
    }

    /**
     * @return the reservedText8
     */
    public String getReservedText8() {
        return reservedText8;
    }

    /**
     * @param reservedText8 要设置的reservedText8
     */
    public void setReservedText8(String reservedText8) {
        this.reservedText8 = reservedText8;
    }

}
