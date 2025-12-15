/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.dto;

import java.io.Serializable;


/**
 * Description: 数据库表LOG_BUSINESS_DETAILS的对应的DTO类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-08.1	zhongzh		2021-01-08		Create
 * </pre>
 * @date 2021-01-08
 */
public class BusinessDetailsLogDto implements Serializable {

    private static final long serialVersionUID = 1610088946384L;

    // 数据id
    private String dataId;
    // 属性名称
    private String attrName;
    // 数据定义id
    private String dataDefId;
    // 操作后的数据值
    private String afterValue;
    // 父级数据id
    private String parentDataId;
    // 数据名称
    private String dataName;
    // 数据定义名称
    private String dataDefName;
    // 操作类型
    private String operation;
    // 属性类型
    private String attrType;
    // 操作前的数据值
    private String beforeValue;
    // 数据类型
    private String dataDefType;
    // 属性id
    private String attrId;
    // 明细日志id
    private String logId;

    /**
     * @return the dataId
     */
    public String getDataId() {
        return this.dataId;
    }

    /**
     * @param dataId
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the attrName
     */
    public String getAttrName() {
        return this.attrName;
    }

    /**
     * @param attrName
     */
    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    /**
     * @return the dataDefId
     */
    public String getDataDefId() {
        return this.dataDefId;
    }

    /**
     * @param dataDefId
     */
    public void setDataDefId(String dataDefId) {
        this.dataDefId = dataDefId;
    }

    /**
     * @return the afterValue
     */
    public String getAfterValue() {
        return this.afterValue;
    }

    /**
     * @param afterValue
     */
    public void setAfterValue(String afterValue) {
        this.afterValue = afterValue;
    }

    /**
     * @return the parentDataId
     */
    public String getParentDataId() {
        return this.parentDataId;
    }

    /**
     * @param parentDataId
     */
    public void setParentDataId(String parentDataId) {
        this.parentDataId = parentDataId;
    }

    /**
     * @return the dataName
     */
    public String getDataName() {
        return this.dataName;
    }

    /**
     * @param dataName
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * @return the dataDefName
     */
    public String getDataDefName() {
        return this.dataDefName;
    }

    /**
     * @param dataDefName
     */
    public void setDataDefName(String dataDefName) {
        this.dataDefName = dataDefName;
    }

    /**
     * @return the operation
     */
    public String getOperation() {
        return this.operation;
    }

    /**
     * @param operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * @return the attrType
     */
    public String getAttrType() {
        return this.attrType;
    }

    /**
     * @param attrType
     */
    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    /**
     * @return the beforeValue
     */
    public String getBeforeValue() {
        return this.beforeValue;
    }

    /**
     * @param beforeValue
     */
    public void setBeforeValue(String beforeValue) {
        this.beforeValue = beforeValue;
    }

    /**
     * @return the dataDefType
     */
    public String getDataDefType() {
        return this.dataDefType;
    }

    /**
     * @param dataDefType
     */
    public void setDataDefType(String dataDefType) {
        this.dataDefType = dataDefType;
    }

    /**
     * @return the attrId
     */
    public String getAttrId() {
        return this.attrId;
    }

    /**
     * @param attrId
     */
    public void setAttrId(String attrId) {
        this.attrId = attrId;
    }

    /**
     * @return the logId
     */
    public String getLogId() {
        return this.logId;
    }

    /**
     * @param logId
     */
    public void setLogId(String logId) {
        this.logId = logId;
    }

}
