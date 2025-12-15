/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.trigger.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.ClassUtils;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
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
 * 2014-12-4.1	ruanhg		2014-12-4		Create
 * </pre>
 * @date 2014-12-4
 */
@Entity
@Table(name = "tig_table_data")
@DynamicUpdate
@DynamicInsert
public class TigTableData implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2553037356477986505L;

    private String uuid;

    private Date createTime;

    private Integer orderId;

    private String stableName;//表名

    private String suuid;//数据uuid

    private String compositeKey;//联合主键 key1:key2...

    private Date synTime;

    private Integer status;//1未同步/2同步中/3同步失败/4同步成功/5免同步/6反馈/7大字段的反馈

    private Integer direction;//1内/2外

    private Integer action;//1添加/2更新/3删除

    private Integer feedback;//3成功/4失败

    private Integer cloumNum;//对应的字段数量

    private String remark;

    public String getStableName() {
        return stableName;
    }

    public void setStableName(String stableName) {
        this.stableName = stableName;
    }

    public String getSuuid() {
        return suuid;
    }

    public void setSuuid(String suuid) {
        this.suuid = suuid;
    }

    public Date getSynTime() {
        return synTime;
    }

    public void setSynTime(Date synTime) {
        this.synTime = synTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Integer getAction() {
        return action;
    }

    public void setAction(Integer action) {
        this.action = action;
    }

    public Integer getFeedback() {
        return feedback;
    }

    public void setFeedback(Integer feedback) {
        this.feedback = feedback;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Id
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getCompositeKey() {
        return compositeKey;
    }

    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    public Integer getCloumNum() {
        return cloumNum;
    }

    public void setCloumNum(Integer cloumNum) {
        this.cloumNum = cloumNum;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != ClassUtils.getUserClass(obj.getClass()))
            return false;
        TigTableData other = (TigTableData) obj;
        if (uuid == null) {
            if (other.uuid != null)
                return false;
        } else if (!uuid.equals(other.getUuid()))
            return false;
        return true;
    }

}
