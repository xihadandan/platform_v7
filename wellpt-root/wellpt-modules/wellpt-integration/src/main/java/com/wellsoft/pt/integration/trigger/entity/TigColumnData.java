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
@Table(name = "TIG_COLUMN_DATA")
@DynamicUpdate
@DynamicInsert
public class TigColumnData implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private String tigOwnerUuid;
    private String tigColumnName;
    private String tigDataType;
    private Date dataTime;
    private Float dataFloat;
    private Double dataNumber;
    private String dataChar;
    private Date dataDate;
    private String dataVarchar_2;
    private String isSynBack;
    private String dataClob;

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
    @Id
    public void setTigOwnerUuid(String tigOwnerUuid) {
        this.tigOwnerUuid = tigOwnerUuid;
    }

    /**
     * @return the tigColumnName
     */
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
     * @return the tigDataType
     */
    public String getTigDataType() {
        return tigDataType;
    }

    /**
     * @param tigDataType 要设置的tigDataType
     */
    public void setTigDataType(String tigDataType) {
        this.tigDataType = tigDataType;
    }

    /**
     * @return the dataTime
     */
    public Date getDataTime() {
        return dataTime;
    }

    /**
     * @param dataTime 要设置的dataTime
     */
    public void setDataTime(Date dataTime) {
        this.dataTime = dataTime;
    }

    /**
     * @return the dataFloat
     */
    public Float getDataFloat() {
        return dataFloat;
    }

    /**
     * @param dataFloat 要设置的dataFloat
     */
    public void setDataFloat(Float dataFloat) {
        this.dataFloat = dataFloat;
    }

    /**
     * @return the dataNumber
     */
    public Double getDataNumber() {
        return dataNumber;
    }

    /**
     * @param dataNumber 要设置的dataNumber
     */
    public void setDataNumber(Double dataNumber) {
        this.dataNumber = dataNumber;
    }

    /**
     * @return the dataChar
     */
    public String getDataChar() {
        return dataChar;
    }

    /**
     * @param dataChar 要设置的dataChar
     */
    public void setDataChar(String dataChar) {
        this.dataChar = dataChar;
    }

    /**
     * @return the dataDate
     */
    public Date getDataDate() {
        return dataDate;
    }

    /**
     * @param dataDate 要设置的dataDate
     */
    public void setDataDate(Date dataDate) {
        this.dataDate = dataDate;
    }

    /**
     * @return the dataVarchar_2
     */
    public String getDataVarchar_2() {
        return dataVarchar_2;
    }

    /**
     * @param dataVarchar_2 要设置的dataVarchar_2
     */
    public void setDataVarchar_2(String dataVarchar_2) {
        this.dataVarchar_2 = dataVarchar_2;
    }

    /**
     * @return the isSynBack
     */
    public String getIsSynBack() {
        return isSynBack;
    }

    /**
     * @param isSynBack 要设置的isSynBack
     */
    public void setIsSynBack(String isSynBack) {
        this.isSynBack = isSynBack;
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
        TigColumnData other = (TigColumnData) obj;
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
