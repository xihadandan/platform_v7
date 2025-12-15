/*
 * @(#)2017年4月12日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月12日.1	zhulh		2017年4月12日		Create
 * </pre>
 * @date 2017年4月12日
 */
@Entity
@Table(name = "APP_NAVIGATION")
@DynamicUpdate
@DynamicInsert
public class AppNavigation extends IdEntity implements ConfigurableIdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6740686802027606425L;

    // 名称
    @NotBlank
    private String name;

    // 显示名称
    private String text;

    // ID
    @NotBlank
    private String id;

    // 编号
    @NotBlank
    private String code;

    // 图标
    private String icon;

    // 是否隐藏
    private Boolean hidden;

    // 徽章
    // 获取视图数量模块
    private String countJsModule;
    // 获取视图数量模块名称
    private String countJsModuleName;
    // 获取数量的组件定义ID
    private String widgetDefId;
    // 获取数量的组件定义名称
    private String widgetDefName;

    // 事件类型
    private String eventType;

    // 目标位置
    private String targetPosition;
    // 目标组件所在的页面
    private String targetPageUuid;
    // 目标组件名称
    private String targetWidgetName;
    // 目标组件ID
    private String targetWidgetId;
    // 目标组件存在时是否刷新
    private String refreshIfExists;

    // 事件处理
    private String eventHanlderId;
    private String eventHanlderName;
    private Integer eventHanlderType;
    private String eventHanlderPath;

    // 上级导航UUID
    private String parentUuid;

    // 描述
    private String remark;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text 要设置的text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon 要设置的icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the hidden
     */
    public Boolean getHidden() {
        return hidden;
    }

    /**
     * @param hidden 要设置的hidden
     */
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return the countJsModule
     */
    public String getCountJsModule() {
        return countJsModule;
    }

    /**
     * @param countJsModule 要设置的countJsModule
     */
    public void setCountJsModule(String countJsModule) {
        this.countJsModule = countJsModule;
    }

    /**
     * @return the countJsModuleName
     */
    public String getCountJsModuleName() {
        return countJsModuleName;
    }

    /**
     * @param countJsModuleName 要设置的countJsModuleName
     */
    public void setCountJsModuleName(String countJsModuleName) {
        this.countJsModuleName = countJsModuleName;
    }

    /**
     * @return the widgetDefId
     */
    public String getWidgetDefId() {
        return widgetDefId;
    }

    /**
     * @param widgetDefId 要设置的widgetDefId
     */
    public void setWidgetDefId(String widgetDefId) {
        this.widgetDefId = widgetDefId;
    }

    /**
     * @return the widgetDefName
     */
    public String getWidgetDefName() {
        return widgetDefName;
    }

    /**
     * @param widgetDefName 要设置的widgetDefName
     */
    public void setWidgetDefName(String widgetDefName) {
        this.widgetDefName = widgetDefName;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * @param eventType 要设置的eventType
     */
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    /**
     * @return the targetPosition
     */
    public String getTargetPosition() {
        return targetPosition;
    }

    /**
     * @param targetPosition 要设置的targetPosition
     */
    public void setTargetPosition(String targetPosition) {
        this.targetPosition = targetPosition;
    }

    /**
     * @return the targetPageUuid
     */
    public String getTargetPageUuid() {
        return targetPageUuid;
    }

    /**
     * @param targetPageUuid 要设置的targetPageUuid
     */
    public void setTargetPageUuid(String targetPageUuid) {
        this.targetPageUuid = targetPageUuid;
    }

    /**
     * @return the targetWidgetName
     */
    public String getTargetWidgetName() {
        return targetWidgetName;
    }

    /**
     * @param targetWidgetName 要设置的targetWidgetName
     */
    public void setTargetWidgetName(String targetWidgetName) {
        this.targetWidgetName = targetWidgetName;
    }

    /**
     * @return the targetWidgetId
     */
    public String getTargetWidgetId() {
        return targetWidgetId;
    }

    /**
     * @param targetWidgetId 要设置的targetWidgetId
     */
    public void setTargetWidgetId(String targetWidgetId) {
        this.targetWidgetId = targetWidgetId;
    }

    /**
     * @return the refreshIfExists
     */
    public String getRefreshIfExists() {
        return refreshIfExists;
    }

    /**
     * @param refreshIfExists 要设置的refreshIfExists
     */
    public void setRefreshIfExists(String refreshIfExists) {
        this.refreshIfExists = refreshIfExists;
    }

    /**
     * @return the eventHanlderId
     */
    public String getEventHanlderId() {
        return eventHanlderId;
    }

    /**
     * @param eventHanlderId 要设置的eventHanlderId
     */
    public void setEventHanlderId(String eventHanlderId) {
        this.eventHanlderId = eventHanlderId;
    }

    /**
     * @return the eventHanlderName
     */
    public String getEventHanlderName() {
        return eventHanlderName;
    }

    /**
     * @param eventHanlderName 要设置的eventHanlderName
     */
    public void setEventHanlderName(String eventHanlderName) {
        this.eventHanlderName = eventHanlderName;
    }

    /**
     * @return the eventHanlderType
     */
    public Integer getEventHanlderType() {
        return eventHanlderType;
    }

    /**
     * @param eventHanlderType 要设置的eventHanlderType
     */
    public void setEventHanlderType(Integer eventHanlderType) {
        this.eventHanlderType = eventHanlderType;
    }

    /**
     * @return the eventHanlderPath
     */
    public String getEventHanlderPath() {
        return eventHanlderPath;
    }

    /**
     * @param eventHanlderPath 要设置的eventHanlderPath
     */
    public void setEventHanlderPath(String eventHanlderPath) {
        this.eventHanlderPath = eventHanlderPath;
    }

    /**
     * @return the parentUuid
     */
    public String getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
