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
@Table(name = "is_guangdun_receive")
@DynamicUpdate
@DynamicInsert
public class GuangDunReceive extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3655590905220951791L;

    // 光盾任务ID
    private String taskId;

    // 主体
    private Blob subject;

    // 数据ID
    private String dataId;

    // 上一个数据ID
    private String preDataId;
    // 目标ID
    private String toId;
    // 1：已还原 0：未还原
    private Integer status;
    // 是否已反馈 1:已反馈 0:未反馈
    private Integer isBack;

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the subject
     */
    public Blob getSubject() {
        return subject;
    }

    /**
     * @param subject 要设置的subject
     */
    public void setSubject(Blob subject) {
        this.subject = subject;
    }

    /**
     * @return the dataId
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * @param dataId 要设置的dataId
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the preDataId
     */
    public String getPreDataId() {
        return preDataId;
    }

    /**
     * @param preDataId 要设置的preDataId
     */
    public void setPreDataId(String preDataId) {
        this.preDataId = preDataId;
    }

    /**
     * @return the toId
     */
    public String getToId() {
        return toId;
    }

    /**
     * @param toId 要设置的toId
     */
    public void setToId(String toId) {
        this.toId = toId;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the isBack
     */
    public Integer getIsBack() {
        return isBack;
    }

    /**
     * @param isBack 要设置的isBack
     */
    public void setIsBack(Integer isBack) {
        this.isBack = isBack;
    }

}
