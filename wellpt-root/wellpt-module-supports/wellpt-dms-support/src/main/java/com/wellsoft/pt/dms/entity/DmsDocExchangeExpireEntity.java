/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;


/**
 * Description: 数据库表DMS_DOC_EXCHANGE_EXPIRE的实体类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@Entity
@Table(name = "DMS_DOC_EXCHANGE_EXPIRE")
@DynamicUpdate
@DynamicInsert
public class DmsDocExchangeExpireEntity extends TenantEntity {

    private static final long serialVersionUID = 1626145242896L;

    // 提醒类型 1：签收，2：反馈
    private Integer type;
    // 消息格式uuid
    private String msgTemplateUuid;
    // 文档交换-记录uuid
    private String docExchangeRecordUuid;
    // 发送时间（小于等于当前时间的，根据 文档交换记录的相关状态为来确定是发送，或删除该条数据）
    private Date sendTime;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the msgTemplateUuid
     */
    public String getMsgTemplateUuid() {
        return this.msgTemplateUuid;
    }

    /**
     * @param msgTemplateUuid
     */
    public void setMsgTemplateUuid(String msgTemplateUuid) {
        this.msgTemplateUuid = msgTemplateUuid;
    }

    /**
     * @return the docExchangeRecordUuid
     */
    public String getDocExchangeRecordUuid() {
        return this.docExchangeRecordUuid;
    }

    /**
     * @param docExchangeRecordUuid
     */
    public void setDocExchangeRecordUuid(String docExchangeRecordUuid) {
        this.docExchangeRecordUuid = docExchangeRecordUuid;
    }

    /**
     * @return the sendTime
     */
    public Date getSendTime() {
        return this.sendTime;
    }

    /**
     * @param sendTime
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

}
