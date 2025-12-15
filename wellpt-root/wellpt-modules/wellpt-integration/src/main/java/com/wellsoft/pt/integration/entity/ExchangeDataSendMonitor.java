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
import java.util.Date;
import java.util.Set;

/**
 * Description: 数据交换发送监控
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-20.1	ruanhg		2014-1-20		Create
 * </pre>
 * @date 2014-1-20
 */
@Entity
@Table(name = "is_exchange_send_monitor")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataSendMonitor extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2867102001769435710L;

    @UnCloneable
    private ExchangeData exchangeData;//数据表

    @UnCloneable
    private Set<ExchangeDataMonitor> exchangeDataMonitors;//接收监控

    private String fromId;//发送单位

    private String sendUser;//发送人

    private Date sendTime;//发送时间

    private Integer sendLimitNum;//逾期天数
    //  8个发送环节
    //	verificaFail验证失败
    //	ing发送中
    //	end已到达
    //	sign已签收完
    //	abnormal未送达
    //	back退回件
    //	examineIng 待审核
    //	examineFail 未通过审核
    //	examineClose 审核关闭
    private String sendNode;

    private String sendType;//发送类型（首发first、补发reapply、分发distribution）

    private String sendMsg;//补发/分发说明

    private Date limitTime;//接收到期时间

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_uuid")
    public ExchangeData getExchangeData() {
        return exchangeData;
    }

    public void setExchangeData(ExchangeData exchangeData) {
        this.exchangeData = exchangeData;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exchangeDataSendMonitor")
    @Cascade(value = {CascadeType.ALL})
    @OrderBy("createTime asc,receiveTime asc")
    public Set<ExchangeDataMonitor> getExchangeDataMonitors() {
        return exchangeDataMonitors;
    }

    public void setExchangeDataMonitors(Set<ExchangeDataMonitor> exchangeDataMonitors) {
        this.exchangeDataMonitors = exchangeDataMonitors;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getSendUser() {
        return sendUser;
    }

    public void setSendUser(String sendUser) {
        this.sendUser = sendUser;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getSendLimitNum() {
        return sendLimitNum;
    }

    public void setSendLimitNum(Integer sendLimitNum) {
        this.sendLimitNum = sendLimitNum;
    }

    public String getSendNode() {
        return sendNode;
    }

    public void setSendNode(String sendNode) {
        this.sendNode = sendNode;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getSendMsg() {
        return sendMsg;
    }

    public void setSendMsg(String sendMsg) {
        this.sendMsg = sendMsg;
    }

    public Date getLimitTime() {
        return limitTime;
    }

    public void setLimitTime(Date limitTime) {
        this.limitTime = limitTime;
    }

}
