/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.element;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: ButtonElement.java
 *
 * @author zhulh
 * @date 2012-11-17
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-17.1	zhulh		2012-11-17		Create
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ButtonElement extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -388906097316516279L;

    // 按钮来源
    private String btnSource;
    // 按钮角色
    private String btnRole;
    // 按钮ID
    private String btnId;
    // 产品集成UUID
    private String piUuid;
    // 产品集成名称
    private String piName;
    // 锚点类型
    private String hashType;
    // 锚点
    private String hash;
    // 事件处理
    private String eventHandler;
    // 事件参数
    private String eventParams;
    // 目标位置
    private String targetPosition;
    // 按钮ID
    private String id;
    // 按钮名称
    private String name;
    // 复制按钮的code
    private String btnValue;
    // 按钮新名称
    private String newName;
    // 按钮新权限代码
    private String newCode;
    // 使用方式，0(替换名称)、1(全部替换)、2(新增操作)
    private String useWay;
    // 操作按钮提交的环节
    private String btnArgument;
    // 操作按钮使用人
    private String btnOwners;
    // 操作按钮使用人ID
    private String btnOwnerIds;
    // 提交的参与者名称
    private String btnUsers;
    // 提交的参与者ID
    private String btnUserIds;
    // 提交的抄送人ID
    private String btnCopyUsers;
    // 提交的抄送人ID
    private String btnCopyUserIds;
    // 样式名称
    private String btnClassName;
    // 图标
    private String btnIcon;
    // 样式
    private String btnStyle;
    // 备注
    private String btnRemark;
    // 排序号
    private String sortOrder;

    private String uuid;
    // 扩展配置信息
    private String configuration;

    /**
     * @return the btnSource
     */
    public String getBtnSource() {
        return btnSource;
    }

    /**
     * @param btnSource 要设置的btnSource
     */
    public void setBtnSource(String btnSource) {
        this.btnSource = btnSource;
    }

    /**
     * @return the btnRole
     */
    public String getBtnRole() {
        return btnRole;
    }

    /**
     * @param btnRole 要设置的btnRole
     */
    public void setBtnRole(String btnRole) {
        this.btnRole = btnRole;
    }

    /**
     * @return the btnId
     */
    public String getBtnId() {
        return btnId;
    }

    /**
     * @param btnId 要设置的btnId
     */
    public void setBtnId(String btnId) {
        this.btnId = btnId;
    }

    /**
     * @return the piUuid
     */
    public String getPiUuid() {
        return piUuid;
    }

    /**
     * @param piUuid 要设置的piUuid
     */
    public void setPiUuid(String piUuid) {
        this.piUuid = piUuid;
    }

    /**
     * @return the piName
     */
    public String getPiName() {
        return piName;
    }

    /**
     * @param piName 要设置的piName
     */
    public void setPiName(String piName) {
        this.piName = piName;
    }

    /**
     * @return the hashType
     */
    public String getHashType() {
        return hashType;
    }

    /**
     * @param hashType 要设置的hashType
     */
    public void setHashType(String hashType) {
        this.hashType = hashType;
    }

    /**
     * @return the hash
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param hash 要设置的hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return the eventHandler
     */
    public String getEventHandler() {
        return eventHandler;
    }

    /**
     * @param eventHandler 要设置的eventHandler
     */
    public void setEventHandler(String eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * @return the eventParams
     */
    public String getEventParams() {
        return eventParams;
    }

    /**
     * @param eventParams 要设置的eventParams
     */
    public void setEventParams(String eventParams) {
        this.eventParams = eventParams;
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
     * @return the btnValue
     */
    public String getBtnValue() {
        return btnValue;
    }

    /**
     * @param btnValue 要设置的btnValue
     */
    public void setBtnValue(String btnValue) {
        this.btnValue = btnValue;
    }

    /**
     * @return the newName
     */
    public String getNewName() {
        return newName;
    }

    /**
     * @param newName 要设置的newName
     */
    public void setNewName(String newName) {
        this.newName = newName;
    }

    /**
     * @return the newCode
     */
    public String getNewCode() {
        return newCode;
    }

    /**
     * @param newCode 要设置的newCode
     */
    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    /**
     * @return the useWay
     */
    public String getUseWay() {
        return useWay;
    }

    /**
     * @param useWay 要设置的useWay
     */
    public void setUseWay(String useWay) {
        this.useWay = useWay;
    }

    /**
     * @return the btnArgument
     */
    public String getBtnArgument() {
        return btnArgument;
    }

    /**
     * @param btnArgument 要设置的btnArgument
     */
    public void setBtnArgument(String btnArgument) {
        this.btnArgument = btnArgument;
    }

    /**
     * @return the btnOwners
     */
    public String getBtnOwners() {
        return btnOwners;
    }

    /**
     * @param btnOwners 要设置的btnOwners
     */
    public void setBtnOwners(String btnOwners) {
        this.btnOwners = btnOwners;
    }

    /**
     * @return the btnOwnerIds
     */
    public String getBtnOwnerIds() {
        return btnOwnerIds;
    }

    /**
     * @param btnOwnerIds 要设置的btnOwnerIds
     */
    public void setBtnOwnerIds(String btnOwnerIds) {
        this.btnOwnerIds = btnOwnerIds;
    }

    /**
     * @return the btnUsers
     */
    public String getBtnUsers() {
        return btnUsers;
    }

    /**
     * @param btnUsers 要设置的btnUsers
     */
    public void setBtnUsers(String btnUsers) {
        this.btnUsers = btnUsers;
    }

    /**
     * @return the btnUserIds
     */
    public String getBtnUserIds() {
        return btnUserIds;
    }

    /**
     * @param btnUserIds 要设置的btnUserIds
     */
    public void setBtnUserIds(String btnUserIds) {
        this.btnUserIds = btnUserIds;
    }

    /**
     * @return the btnCopyUsers
     */
    public String getBtnCopyUsers() {
        return btnCopyUsers;
    }

    /**
     * @param btnCopyUsers 要设置的btnCopyUsers
     */
    public void setBtnCopyUsers(String btnCopyUsers) {
        this.btnCopyUsers = btnCopyUsers;
    }

    /**
     * @return the btnCopyUserIds
     */
    public String getBtnCopyUserIds() {
        return btnCopyUserIds;
    }

    /**
     * @param btnCopyUserIds 要设置的btnCopyUserIds
     */
    public void setBtnCopyUserIds(String btnCopyUserIds) {
        this.btnCopyUserIds = btnCopyUserIds;
    }

    /**
     * @return the btnClassName
     */
    public String getBtnClassName() {
        return btnClassName;
    }

    /**
     * @param btnClassName 要设置的btnClassName
     */
    public void setBtnClassName(String btnClassName) {
        this.btnClassName = btnClassName;
    }

    /**
     * @return the btnIcon
     */
    public String getBtnIcon() {
        return btnIcon;
    }

    /**
     * @param btnIcon 要设置的btnIcon
     */
    public void setBtnIcon(String btnIcon) {
        this.btnIcon = btnIcon;
    }

    /**
     * @return the btnStyle
     */
    public String getBtnStyle() {
        return btnStyle;
    }

    /**
     * @param btnStyle 要设置的btnStyle
     */
    public void setBtnStyle(String btnStyle) {
        this.btnStyle = btnStyle;
    }

    /**
     * @return the btnRemark
     */
    public String getBtnRemark() {
        return btnRemark;
    }

    /**
     * @param btnRemark 要设置的btnRemark
     */
    public void setBtnRemark(String btnRemark) {
        this.btnRemark = btnRemark;
    }

    /**
     * @return the sortOrder
     */
    public String getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the configuration
     */
    public String getConfiguration() {
        return configuration;
    }

    /**
     * @param configuration 要设置的configuration
     */
    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }
}
