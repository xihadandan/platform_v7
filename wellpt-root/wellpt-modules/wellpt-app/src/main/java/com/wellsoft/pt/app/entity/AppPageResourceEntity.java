/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 页面引用的资源
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月10日.1	zhulh		2019年6月10日		Create
 * </pre>
 * @date 2019年6月10日
 */
@Entity
@Table(name = "APP_PAGE_RESOURCE")
@DynamicUpdate
@DynamicInsert
public class AppPageResourceEntity extends IdEntity implements UUIDGeneratorIndicate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6161122906162971443L;

    //    // 名称
    private String name;
    //    // ID
    private String id;
    //    // 编号
    //    private String code;
    //    // 类型
    //    private String type;
    //    // 功能定义的JSON详细信息
    //    @MaxLength(max = 4000)
    //    private String definitionJson;
    //    // 功能信息是否可导出
    //    private Boolean exportable;
    //    // 功能导出类型
    //    private String exportType;
    @ApiModelProperty("资源是否受保护")
    private Boolean isProtected;
    //    // 备注
    //    private String remark;
    @ApiModelProperty("配置类型1、配置资源，2、开发资源")
    private String configType;
    @ApiModelProperty("资源归属产品集成的UUID")
    private String appPiUuid;
    @ApiModelProperty("资源归属页面的UUID")
    private String appPageUuid;
    @ApiModelProperty("资源引用的功能UUID")
    private String appFunctionUuid;

    private String resourceId;
    private String resourceType;


    /**
     * @return the isProtected
     */
    public Boolean getIsProtected() {
        return isProtected;
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
     * @return the appPiUuid
     */
    public String getAppPiUuid() {
        return appPiUuid;
    }

    /**
     * @param appPiUuid 要设置的appPiUuid
     */
    public void setAppPiUuid(String appPiUuid) {
        this.appPiUuid = appPiUuid;
    }

    /**
     * @return the appPageUuid
     */
    public String getAppPageUuid() {
        return appPageUuid;
    }

    /**
     * @param appPageUuid 要设置的appPageUuid
     */
    public void setAppPageUuid(String appPageUuid) {
        this.appPageUuid = appPageUuid;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取页面资源参与权限控制的标识ID
     *
     * @return
     */
    @ApiModelProperty("权限控制的标识ID")
    @Transient
    public String getPrivilegeResouceId() {
        return AppConstants.FUNCTIONREF_OF_PAGE_PREFIX + "_" + this.getAppPiUuid() + "_" + this.getAppPageUuid()
                + "_" + this.getAppFunctionUuid();
    }


    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }
}
