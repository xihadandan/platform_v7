/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.List;

/**
 * Description: 组件的定义信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-18.1	zhulh		2016-09-18		Create
 * </pre>
 * @date 2016-09-18
 */
@Entity
@Table(name = "APP_WIDGET_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class AppWidgetDefinition extends IdEntity implements UUIDGeneratorIndicate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1474182004522L;

    // 标题
    private String title;
    // 名称
    private String name;
    // ID
    private String id;
    // 组件类型
    private String wtype;
    // 定义JSON信息
    private String definitionJson;
    // 定义HTML信息
    private String html;
    // 引用的组件定义UUID, 为空表示没引用
    private String refWidgetDefUuid;
    // 组件定义所在的定义UUID
    private String appPageUuid;

    private String appPageId;  //应用页面ID

    private BigDecimal version; // 版本

    private String appId;// 产品ID 或者 模块ID , 用于快速查询模块或者产品下的组件

    private Boolean main; // 标记为主要


    /**
     * 页面组件功能资源未授权集合
     * 页面组件功能资源权限只有被设置为保护的情况下（默认是不保护的），才需要判断是否有权限，所以通过未授权的逆向方式能够判断是否开启保护以及是否有权限
     */
    private List<String> unauthorizedResource = Lists.newArrayList();

    private List<AppDefElementI18nEntity> i18ns;

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
     * @return the wtype
     */
    @Column(name = "WTYPE")
    public String getWtype() {
        return this.wtype;
    }

    /**
     * @param wtype
     */
    public void setWtype(String wtype) {
        this.wtype = wtype;
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
     * @return the refWidgetDefUuid
     */
    @Column(name = "REF_WIDGET_DEF_UUID")
    public String getRefWidgetDefUuid() {
        return this.refWidgetDefUuid;
    }

    /**
     * @param refWidgetDefUuid
     */
    public void setRefWidgetDefUuid(String refWidgetDefUuid) {
        this.refWidgetDefUuid = refWidgetDefUuid;
    }

    /**
     * @return the appPageUuid
     */
    @Column(name = "APP_PAGE_UUID")
    public String getAppPageUuid() {
        return this.appPageUuid;
    }

    /**
     * @param appPageUuid
     */
    public void setAppPageUuid(String appPageUuid) {
        this.appPageUuid = appPageUuid;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Boolean getMain() {
        return main;
    }

    public void setMain(Boolean main) {
        this.main = main;
    }

    @Column(name = "APP_PAGE_ID")
    public String getAppPageId() {
        return appPageId;
    }

    public void setAppPageId(String appPageId) {
        this.appPageId = appPageId;
    }

    @Column(name = "VERSION")
    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    @Transient
    public List<String> getUnauthorizedResource() {
        return unauthorizedResource;
    }

    public void setUnauthorizedResource(List<String> unauthorizedResource) {
        this.unauthorizedResource = unauthorizedResource;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
