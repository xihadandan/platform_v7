/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表LOG_MANAGE_DETAILS的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-06-28.1	zenghw		2021-06-28		Create
 * </pre>
 * @date 2021-06-28
 */
@Entity
@Table(name = "LOG_MANAGE_DETAILS")
@DynamicUpdate
@DynamicInsert
public class LogManageDetailsEntity extends IdEntity {

    private static final long serialVersionUID = 1624869183588L;

    // 属性名称
    private String attrName;
    // 前端数据展示类型：eg:文本，图片
    private String dataShowType;
    // 属性类型
    private String attrType;
    // 操作前的数据值
    private String beforeValue;
    // 属性id
    private String attrId;
    // 明细日志id 对应管理操作日志的uuid
    private String logId;
    // 操作后的数据值
    private String afterValue;

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
     * @return the dataShowType
     */
    public String getDataShowType() {
        return this.dataShowType;
    }

    /**
     * @param dataShowType
     */
    public void setDataShowType(String dataShowType) {
        this.dataShowType = dataShowType;
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

}
