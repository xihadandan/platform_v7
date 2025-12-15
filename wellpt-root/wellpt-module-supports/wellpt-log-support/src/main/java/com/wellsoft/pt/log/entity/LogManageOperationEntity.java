/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表LOG_MANAGE_OPERATION的实体类
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
@Table(name = "LOG_MANAGE_OPERATION")
@DynamicUpdate
@DynamicInsert
public class LogManageOperationEntity extends TenantEntity {

    private static final long serialVersionUID = 1624869183271L;

    // 报文操作前的数据值（不涉及搜索）
    private String beforeMessageValue;
    // 数据名称简介
    private String dataNameInfo;
    // 数据id
    private String dataId;
    // 模块名称
    private String moduleName;
    // 操作人id
    private String userId;
    // 操作人
    private String userName;
    // 操作前的数据名称
    private String beforeDataName;
    // 操作后的数据名称
    private String afterDataName;
    // 数据解析类型：entity;xml;json等
    private String dataParseType;
    // 报文操作后的数据值（不涉及搜索）
    private String afterMessageValue;
    // 操作类型
    private String operation;
    // 操作类型ID
    private String operationId;
    // 模块id
    private String moduleId;
    // 数据类型：流程分类，流程定义
    private String dataType;

    // 数据类型：1:流程分类，2:流程定义
    private String dataTypeId;

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getDataTypeId() {
        return dataTypeId;
    }

    public void setDataTypeId(String dataTypeId) {
        this.dataTypeId = dataTypeId;
    }

    /**
     * @return the beforeMessageValue
     */
    public String getBeforeMessageValue() {
        return this.beforeMessageValue;
    }

    /**
     * @param beforeMessageValue
     */
    public void setBeforeMessageValue(String beforeMessageValue) {
        this.beforeMessageValue = beforeMessageValue;
    }

    /**
     * @return the dataNameInfo
     */
    public String getDataNameInfo() {
        return this.dataNameInfo;
    }

    /**
     * @param dataNameInfo
     */
    public void setDataNameInfo(String dataNameInfo) {
        this.dataNameInfo = dataNameInfo;
    }

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
     * @return the moduleName
     */
    public String getModuleName() {
        return this.moduleName;
    }

    /**
     * @param moduleName
     */
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the beforeDataName
     */
    public String getBeforeDataName() {
        return this.beforeDataName;
    }

    /**
     * @param beforeDataName
     */
    public void setBeforeDataName(String beforeDataName) {
        this.beforeDataName = beforeDataName;
    }

    /**
     * @return the afterDataName
     */
    public String getAfterDataName() {
        return this.afterDataName;
    }

    /**
     * @param afterDataName
     */
    public void setAfterDataName(String afterDataName) {
        this.afterDataName = afterDataName;
    }

    /**
     * @return the dataParseType
     */
    public String getDataParseType() {
        return this.dataParseType;
    }

    /**
     * @param dataParseType
     */
    public void setDataParseType(String dataParseType) {
        this.dataParseType = dataParseType;
    }

    /**
     * @return the afterMessageValue
     */
    public String getAfterMessageValue() {
        return this.afterMessageValue;
    }

    /**
     * @param afterMessageValue
     */
    public void setAfterMessageValue(String afterMessageValue) {
        this.afterMessageValue = afterMessageValue;
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
     * @return the moduleId
     */
    public String getModuleId() {
        return this.moduleId;
    }

    /**
     * @param moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return this.dataType;
    }

    /**
     * @param dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

}
