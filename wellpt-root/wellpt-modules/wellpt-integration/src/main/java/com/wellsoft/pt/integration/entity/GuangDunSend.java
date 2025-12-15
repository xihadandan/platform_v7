/*
 * @(#)2014-4-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;
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
 * 2017年5月10日.1	ruanhg		2017年5月10日		Create
 * </pre>
 * @date 2017年5月10日
 */
@Entity
@Table(name = "is_guangdun_send")
@DynamicUpdate
@DynamicInsert
public class GuangDunSend extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4481460199986229850L;

    // 光盾任务ID
    private String taskId;

    // 主体
    private Blob subject;

    // 数据ID
    private String dataId;

    // 上一个数据ID
    private String preDataId;

    private String toId;
    // 是否接收反馈 1：接收反馈 0：未接收反馈
    private Integer isBack;
    // 上次重发的时间
    private Date preSendDate;

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public Blob getSubject() {
        return subject;
    }

    public void setSubject(Blob subject) {
        this.subject = subject;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getPreDataId() {
        return preDataId;
    }

    public void setPreDataId(String preDataId) {
        this.preDataId = preDataId;
    }

    /**
     * @return the isBlack
     */
    public Integer getIsBack() {
        return isBack;
    }

    /**
     * @param isBlack 要设置的isBlack
     */
    public void setIsBack(Integer isBack) {
        this.isBack = isBack;
    }

    /**
     * @return the preSendDate
     */
    public Date getPreSendDate() {
        return preSendDate;
    }

    /**
     * @param preSendDate 要设置的preSendDate
     */
    public void setPreSendDate(Date preSendDate) {
        this.preSendDate = preSendDate;
    }

}
