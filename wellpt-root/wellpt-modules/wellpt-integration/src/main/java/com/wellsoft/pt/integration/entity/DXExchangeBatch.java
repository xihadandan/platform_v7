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
import java.util.HashSet;
import java.util.Set;

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
@Table(name = "is_dx_exchange_batch")
@DynamicUpdate
@DynamicInsert
public class DXExchangeBatch extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8417268540290646895L;

    private String id;//终端请求的批次id

    private String fromId;//发送单位

    private String fromUser;//发送用户id

    private String toId;//接收单位

    private String cc;//抄送

    private String bcc;//密送

    private String typeId;//数据类型

    private String params;

    private Integer arrow;//平台收/发（0/1）

    private String sourceBatchId;

    @UnCloneable
    private Set<DXExchangeRouteDate> dXExchangeRouteDates;//发送
    @UnCloneable
    private Set<DXExchangeData> dXExchangeDatas = new HashSet<DXExchangeData>(0);

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dXExchangeBatch")
    @Cascade(value = {CascadeType.ALL})
    public Set<DXExchangeRouteDate> getdXExchangeRouteDates() {
        return dXExchangeRouteDates;
    }

    public void setdXExchangeRouteDates(Set<DXExchangeRouteDate> dXExchangeRouteDates) {
        this.dXExchangeRouteDates = dXExchangeRouteDates;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    @Column(name = "to_id", length = 2500)
    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    @Column(name = "cc", length = 1000)
    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    @Column(name = "bcc", length = 1000)
    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "dXExchangeBatch")
    @Cascade(value = {CascadeType.ALL})
    public Set<DXExchangeData> getdXExchangeDatas() {
        return dXExchangeDatas;
    }

    public void setdXExchangeDatas(Set<DXExchangeData> dXExchangeDatas) {
        this.dXExchangeDatas = dXExchangeDatas;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public Integer getArrow() {
        return arrow;
    }

    public void setArrow(Integer arrow) {
        this.arrow = arrow;
    }

    public String getSourceBatchId() {
        return sourceBatchId;
    }

    public void setSourceBatchId(String sourceBatchId) {
        this.sourceBatchId = sourceBatchId;
    }

}
