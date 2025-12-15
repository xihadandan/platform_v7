/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.sql.Clob;
import java.util.Date;

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
@Table(name = "is_exchange_data_monitor")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataMonitor extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2205030337433662047L;
    // 发送监控
    @UnCloneable
    private ExchangeDataSendMonitor exchangeDataSendMonitor;

    private String unitId;// 接收单位id

    private String unitType;// 接收单位类型（收件、抄送、密送）

    private String receiveStatus;// 到达情况 default success fail

    private Date receiveTime;// 到达时间

    private String replyStatus;// 接收情况 default success fail

    private Date replyTime;// 接收时间

    private String replyMsg;// 拒收的原因

    private String replyUser;// 接收用户id

    private String systemId;// 系统id

    private String routeMsg;// 发送的路由

    private Integer retransmissionNum;// 重发次数

    private Integer interval;// 间隔秒数

    private Integer sendNum;// 已经发的次数

    private String formId;// 表单id

    private String formDataUuid;// 接收的表单数据

    private Clob xml;// 接收的xml

    private String matter;// 事项

    private String matterId;// 事项Id

    private String cancelStatus;// 撤回状态 default success fail

    private String cancelMsg;// 撤回原因

    private Date cancelTime;// 撤回时间

    private String cancelUser;// 撤回用户信息 id

    private String cancelRequest;// 被要求撤回 yes、no

    private Integer replyLimitNum;// 接收逾期天数

    private String receiveNode;// 接收环节 1、wait待收 2、sign已签收完 3、back退回件 4、reply 出证
    // 5、transfer 分发件

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "send_id")
    public ExchangeDataSendMonitor getExchangeDataSendMonitor() {
        return exchangeDataSendMonitor;
    }

    public void setExchangeDataSendMonitor(ExchangeDataSendMonitor exchangeDataSendMonitor) {
        this.exchangeDataSendMonitor = exchangeDataSendMonitor;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getReceiveStatus() {
        return receiveStatus;
    }

    public void setReceiveStatus(String receiveStatus) {
        this.receiveStatus = receiveStatus;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public Date getReplyTime() {
        return replyTime;
    }

    public void setReplyTime(Date replyTime) {
        this.replyTime = replyTime;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getReplyUser() {
        return replyUser;
    }

    public void setReplyUser(String replyUser) {
        this.replyUser = replyUser;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getRouteMsg() {
        return routeMsg;
    }

    public void setRouteMsg(String routeMsg) {
        this.routeMsg = routeMsg;
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

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getFormDataUuid() {
        return formDataUuid;
    }

    public void setFormDataUuid(String formDataUuid) {
        this.formDataUuid = formDataUuid;
    }

    public Clob getXml() {
        return xml;
    }

    public void setXml(Clob xml) {
        this.xml = xml;
    }

    public String getMatter() {
        return matter;
    }

    public void setMatter(String matter) {
        this.matter = matter;
    }

    public String getMatterId() {
        return matterId;
    }

    public void setMatterId(String matterId) {
        this.matterId = matterId;
    }

    public String getCancelStatus() {
        return cancelStatus;
    }

    public void setCancelStatus(String cancelStatus) {
        this.cancelStatus = cancelStatus;
    }

    public String getCancelMsg() {
        return cancelMsg;
    }

    public void setCancelMsg(String cancelMsg) {
        this.cancelMsg = cancelMsg;
    }

    public Date getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getCancelUser() {
        return cancelUser;
    }

    public void setCancelUser(String cancelUser) {
        this.cancelUser = cancelUser;
    }

    public String getCancelRequest() {
        return cancelRequest;
    }

    public void setCancelRequest(String cancelRequest) {
        this.cancelRequest = cancelRequest;
    }

    public Integer getReplyLimitNum() {
        return replyLimitNum;
    }

    public void setReplyLimitNum(Integer replyLimitNum) {
        this.replyLimitNum = replyLimitNum;
    }

    public String getReceiveNode() {
        return receiveNode;
    }

    public void setReceiveNode(String receiveNode) {
        this.receiveNode = receiveNode;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

}
