/*
 * @(#)2016年8月12日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
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
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月12日.1	zhongzh		2016年8月12日		Create
 * </pre>
 * @date 2016年8月12日
 */
@Entity
@Table(name = "TIG_COLUMN_CLOB")
@DynamicUpdate
@DynamicInsert
public class TigColumnClob implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private String tigOwnerUuid;
    private String tigColumnName;
    private String dataUuid;
    private String compositeKey;
    private Integer dataStatus;
    private String stableName;
    private String dataClob;
    private Integer direction;
    private Date createTime;
    private Date synTime;
    private Date backupTime;
    private Date feedbackTime;

    /**
     * @return the tigOwnerUuid
     */
    @Id
    public String getTigOwnerUuid() {
        return tigOwnerUuid;
    }

    /**
     * @param tigOwnerUuid 要设置的tigOwnerUuid
     */
    public void setTigOwnerUuid(String tigOwnerUuid) {
        this.tigOwnerUuid = tigOwnerUuid;
    }

    /**
     * @return the tigColumnName
     */
    @Id
    public String getTigColumnName() {
        return tigColumnName;
    }

    /**
     * @param tigColumnName 要设置的tigColumnName
     */
    public void setTigColumnName(String tigColumnName) {
        this.tigColumnName = tigColumnName;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the compositeKey
     */
    public String getCompositeKey() {
        return compositeKey;
    }

    /**
     * @param compositeKey 要设置的compositeKey
     */
    public void setCompositeKey(String compositeKey) {
        this.compositeKey = compositeKey;
    }

    /**
     * @return the dataStatus
     */
    public Integer getDataStatus() {
        return dataStatus;
    }

    /**
     * @param dataStatus 要设置的dataStatus
     */
    public void setDataStatus(Integer dataStatus) {
        this.dataStatus = dataStatus;
    }

    /**
     * @return the stableName
     */
    public String getStableName() {
        return stableName;
    }

    /**
     * @param stableName 要设置的stableName
     */
    public void setStableName(String stableName) {
        this.stableName = stableName;
    }

    /**
     * @return the dataClob
     */
    public String getDataClob() {
        return dataClob;
    }

    /**
     * @param dataClob 要设置的dataClob
     */
    public void setDataClob(String dataClob) {
        this.dataClob = dataClob;
    }

    /**
     * @return the direction
     */
    public Integer getDirection() {
        return direction;
    }

    /**
     * @param direction 要设置的direction
     */
    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the synTime
     */
    public Date getSynTime() {
        return synTime;
    }

    /**
     * @param synTime 要设置的synTime
     */
    public void setSynTime(Date synTime) {
        this.synTime = synTime;
    }

    /**
     * @return the backupTime
     */
    public Date getBackupTime() {
        return backupTime;
    }

    /**
     * @param backupTime 要设置的backupTime
     */
    public void setBackupTime(Date backupTime) {
        this.backupTime = backupTime;
    }

    /**
     * @return the feedbackTime
     */
    public Date getFeedbackTime() {
        return feedbackTime;
    }

    /**
     * @param feedbackTime 要设置的feedbackTime
     */
    public void setFeedbackTime(Date feedbackTime) {
        this.feedbackTime = feedbackTime;
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
        result = prime * result + ((tigColumnName == null) ? 0 : tigColumnName.hashCode());
        result = prime * result + ((tigOwnerUuid == null) ? 0 : tigOwnerUuid.hashCode());
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
        TigColumnClob other = (TigColumnClob) obj;
        if (tigColumnName == null) {
            if (other.tigColumnName != null)
                return false;
        } else if (!tigColumnName.equals(other.getTigColumnName()))
            return false;
        if (tigOwnerUuid == null) {
            if (other.tigOwnerUuid != null)
                return false;
        } else if (!tigOwnerUuid.equals(other.getTigOwnerUuid()))
            return false;
        return true;
    }

}