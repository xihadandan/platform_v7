/*
 * @(#)8/18/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description: 数据定义引用功能资源
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/18/23.1	zhulh		8/18/23		Create
 * </pre>
 * @date 8/18/23
 */
@javax.persistence.Entity
@Table(name = "APP_DATA_DEF_REF_RESOURCE")
@DynamicInsert
@DynamicUpdate
public class AppDataDefinitionRefResourceEntity extends Entity {

    @ApiModelProperty("数据定义UUID")
    private String dataDefUuid;

    @ApiModelProperty("数据定义名称")
    private String dataDefName;

    @ApiModelProperty("数据定义类型")
    private String dataDefType;

    @ApiModelProperty("数据定义项ID")
    private String itemId;

    @ApiModelProperty("数据定义项名称")
    private String itemName;

    @ApiModelProperty("资源引用的功能UUID")
    private String appFunctionUuid;

    @ApiModelProperty("资源是否受保护")
    private Boolean isProtected;

    @ApiModelProperty("配置类型1、配置资源，2、开发资源")
    private String configType;

    @ApiModelProperty("模块ID")
    private String moduleId;

    /**
     * @return the dataDefUuid
     */
    public String getDataDefUuid() {
        return dataDefUuid;
    }

    /**
     * @param dataDefUuid 要设置的dataDefUuid
     */
    public void setDataDefUuid(String dataDefUuid) {
        this.dataDefUuid = dataDefUuid;
    }

    /**
     * @return the dataDefName
     */
    public String getDataDefName() {
        return dataDefName;
    }

    /**
     * @param dataDefName 要设置的dataDefName
     */
    public void setDataDefName(String dataDefName) {
        this.dataDefName = dataDefName;
    }

    /**
     * @return the dataDefType
     */
    public String getDataDefType() {
        return dataDefType;
    }

    /**
     * @param dataDefType 要设置的dataDefType
     */
    public void setDataDefType(String dataDefType) {
        this.dataDefType = dataDefType;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId 要设置的itemId
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName 要设置的itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the appFunctionUuid
     */
    public String getAppFunctionUuid() {
        return appFunctionUuid;
    }

    /**
     * @param appFunctionUuid 要设置的appFunctionUuid
     */
    public void setAppFunctionUuid(String appFunctionUuid) {
        this.appFunctionUuid = appFunctionUuid;
    }

    /**
     * @return the isProtected
     */
    public Boolean getIsProtected() {
        return this.isProtected;
    }

    /**
     * @param isProtected 要设置的isProtected
     */
    public void setIsProtected(Boolean isProtected) {
        this.isProtected = isProtected;
    }

    /**
     * @return the configType
     */
    public String getConfigType() {
        return configType;
    }

    /**
     * @param configType 要设置的configType
     */
    public void setConfigType(String configType) {
        this.configType = configType;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
