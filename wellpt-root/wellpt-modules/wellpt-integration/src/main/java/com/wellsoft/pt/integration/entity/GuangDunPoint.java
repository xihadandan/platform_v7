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
@Table(name = "is_guangdun_point")
@DynamicUpdate
@DynamicInsert
public class GuangDunPoint extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8177999214700487222L;

    // 数据ID
    private String sendDataId;

    private String receiveDataId;

    // 异常数据ID,还原数据时
    private String errDataId;
    // 保存数据ID
    private String saveDataId;

    // 数据是否堵塞，当dataId作为条件找不到receive的preDataId的数据时,1:阻塞
    private Integer status;

    public String getSendDataId() {
        return sendDataId;
    }

    public void setSendDataId(String sendDataId) {
        this.sendDataId = sendDataId;
    }

    public String getReceiveDataId() {
        return receiveDataId;
    }

    public void setReceiveDataId(String receiveDataId) {
        this.receiveDataId = receiveDataId;
    }

    public String getErrDataId() {
        return errDataId;
    }

    public void setErrDataId(String errDataId) {
        this.errDataId = errDataId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the saveDataId
     */
    public String getSaveDataId() {
        return saveDataId;
    }

    /**
     * @param saveDataId 要设置的saveDataId
     */
    public void setSaveDataId(String saveDataId) {
        this.saveDataId = saveDataId;
    }

}
