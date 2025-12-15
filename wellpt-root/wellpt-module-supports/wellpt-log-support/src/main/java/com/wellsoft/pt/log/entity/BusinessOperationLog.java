/*
 * @(#)2021-01-08 V1.0
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
 * Description: 数据库表LOG_BUSINESS_OPERATION的实体类
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
@Entity
@Table(name = "LOG_BUSINESS_OPERATION")
@DynamicUpdate
@DynamicInsert
public class BusinessOperationLog extends TenantEntity {

    private static final long serialVersionUID = 1610088946452L;

    // 数据id
    private String dataId;
    // 模块名称
    private String moduleName;
    // 数据定义名称
    private String dataDefName;
    // 操作类型
    private String operation;
    private String operation2;
    // 数据定义id
    private String dataDefId;
    // 数据类型
    private String dataDefType;
    // 操作人id
    private String userId;
    // 操作人
    private String userName;
    // 模块id
    private String moduleId;
    // 数据名称
    private String dataName;

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

    public String getOperation2() {
        return operation2;
    }

    public void setOperation2(String operation2) {
        this.operation2 = operation2;
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

}
