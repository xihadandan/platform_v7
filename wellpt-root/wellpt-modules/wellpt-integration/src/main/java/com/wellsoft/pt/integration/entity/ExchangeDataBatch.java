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
import java.util.Set;

/**
 * Description: 交换数据数据批次
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
@Table(name = "is_exchange_data_batch")
@DynamicUpdate
@DynamicInsert
public class ExchangeDataBatch extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1421443865750598538L;

    private String id;//终端请求的批次id

    private String fromId;//发送单位

    private String fromUser;//发送用户id

    private String toId;//接收单位

    private String cc;//抄送

    private String bcc;//密送

    private String typeId;//数据类型

    private Integer operateSource;//操作来源 终端1/平台非1(20发送，22转发，23回复，24出证，25上传,26提交审核(个人,需审核)

    @UnCloneable
    private Set<ExchangeData> exchangeDatas;

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

    public Integer getOperateSource() {
        return operateSource;
    }

    public void setOperateSource(Integer operateSource) {
        this.operateSource = operateSource;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "exchangeDataBatch")
    @Cascade(value = {CascadeType.ALL})
    public Set<ExchangeData> getExchangeDatas() {
        return exchangeDatas;
    }

    public void setExchangeDatas(Set<ExchangeData> exchangeDatas) {
        this.exchangeDatas = exchangeDatas;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

}
