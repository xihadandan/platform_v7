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
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-28.1	ruanhg		2014-4-28		Create
 * </pre>
 * @date 2014-4-28
 */
@Entity
@Table(name = "is_dx_exchange_route")
@DynamicUpdate
@DynamicInsert
public class DXExchangeRouteDate extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7136611462157177540L;

    //发送监控
    @UnCloneable
    private DXExchangeBatch dXExchangeBatch;

    private String unitId;//接收单位id

    private String type;//接收单位类型（收件to、抄送cc、密送bcc）

    private String receiveStatus;//到达情况  default success fail

    private Date receiveTime;//到达时间

    private String systemId;//系统id

    private String routeMsg;//发送的路由

    private Integer retransmissionNum;//重发次数

    private Integer interval;//间隔秒数

    private Integer sendNum;//已经发的次数

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_uuid")
    public DXExchangeBatch getdXExchangeBatch() {
        return dXExchangeBatch;
    }

    public void setdXExchangeBatch(DXExchangeBatch dXExchangeBatch) {
        this.dXExchangeBatch = dXExchangeBatch;
    }

    public String getUnitId() {
        return unitId;
    }

    public void setUnitId(String unitId) {
        this.unitId = unitId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

}
