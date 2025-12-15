/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
@Entity
@Table(name = "APP_PAGE_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class AppPageDefinition extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1462783385437L;

    @ApiModelProperty("标题")
    private String title;
    @ApiModelProperty("名称")
    private String name;
    @ApiModelProperty("ID")
    private String id;
    @ApiModelProperty("编号")
    private String code;
    @ApiModelProperty("页面容器类型")
    private String wtype;
    @ApiModelProperty("页面主题")
    private String theme;
    @ApiModelProperty("页面归属用户ID")
    private String userId;
    @ApiModelProperty("归属用户页面关联的页面UUID")
    private String correlativePageUuid;
    @ApiModelProperty("是否共享的页面")
    private Boolean shared;
    @ApiModelProperty("是否默认页面")
    private Boolean isDefault;
    @ApiModelProperty("是否启用")
    private Boolean enabled;
    private Boolean designable; // 是否可设计
    @ApiModelProperty("定义JSON信息")
    private String definitionJson;
    @ApiModelProperty("定义HTML信息")
    private String html;
    @ApiModelProperty("uni-app定义JSON信息")
    private String uniAppDefinitionJson;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("归属产品集成UUID")
    private String appPiUuid;
    @ApiModelProperty(value = "版本号")
    private String version;
    @ApiModelProperty("pc端状态")
    private String isPc;
    @ApiModelProperty("归属应用")
    private String appId; // 系统 / 模块 id
    @ApiModelProperty("是否固定布局")
    private Boolean layoutFixed;
    @ApiModelProperty("归属租户")
    private String tenant;
    @ApiModelProperty("是否匿名")
    private Boolean isAnonymous; // 是否匿名访问页面


    /**
     * @return the title
     */
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the name
     */
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    @Column(name = "ID")
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    @Column(name = "CODE")
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the wtype
     */
    @Column(name = "WTYPE")
    public String getWtype() {
        return wtype;
    }

    /**
     * @param wtype 要设置的wtype
     */
    public void setWtype(String wtype) {
        this.wtype = wtype;
    }

    /**
     * @return the userId
     */
    @Column(name = "USER_ID")
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the theme
     */
    @Column(name = "THEME")
    public String getTheme() {
        return theme;
    }

    /**
     * @param theme 要设置的theme
     */
    public void setTheme(String theme) {
        this.theme = theme;
    }

    /**
     * @return the correlativeAppPageUuid
     */
    @Column(name = "CORRELATIVE_PAGE_UUID")
    public String getCorrelativePageUuid() {
        return correlativePageUuid;
    }

    /**
     * @param correlativeAppPageUuid 要设置的correlativeAppPageUuid
     */
    public void setCorrelativePageUuid(String correlativePageUuid) {
        this.correlativePageUuid = correlativePageUuid;
    }

    /**
     * @return the shared
     */
    @Column(name = "SHARED")
    public Boolean getShared() {
        return this.shared;
    }

    /**
     * @param shared
     */
    public void setShared(Boolean shared) {
        this.shared = shared;
    }

    /**
     * @return the isDefault
     */
    @Column(name = "IS_DEFAULT")
    public Boolean getIsDefault() {
        return isDefault;
    }

    /**
     * @param isDefault 要设置的isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    /**
     * @return the definitionJson
     */
    @Column(name = "DEFINITION_JSON")
    public String getDefinitionJson() {
        return this.definitionJson;
    }

    /**
     * @param definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

    /**
     * @return the html
     */
    @Column(name = "HTML")
    public String getHtml() {
        return html;
    }

    /**
     * @param html 要设置的html
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * @return the uniAppDefinitionJson
     */
    @Column(name = "UNI_APP_DEFINITION_JSON")
    public String getUniAppDefinitionJson() {
        return uniAppDefinitionJson;
    }

    /**
     * @param uniAppDefinitionJson 要设置的uniAppDefinitionJson
     */
    public void setUniAppDefinitionJson(String uniAppDefinitionJson) {
        this.uniAppDefinitionJson = uniAppDefinitionJson;
    }

    /**
     * @return the remark
     */
    @Column(name = "REMARK")
    public String getRemark() {
        return this.remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    public String getIsPc() {
        return isPc;
    }

    public void setIsPc(String isPc) {
        this.isPc = isPc;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getDesignable() {
        return designable;
    }

    public void setDesignable(Boolean designable) {
        this.designable = designable;
    }

    public Boolean getLayoutFixed() {
        return layoutFixed;
    }

    public void setLayoutFixed(Boolean layoutFixed) {
        this.layoutFixed = layoutFixed;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean anonymous) {
        isAnonymous = anonymous;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
