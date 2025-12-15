/*
 * @(#)12/4/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query;

import com.wellsoft.context.base.BaseObject;

import java.util.Date;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/4/24.1	    zhulh		12/4/24		    Create
 * </pre>
 * @date 12/4/24
 */
public class FlowDefinitionUserQueryItem extends BaseObject {
    private static final long serialVersionUID = -6864656671962552401L;

    private String uuid;
    private String rowId;
    private Date createTime;
    private Date modifyTime;
    private String category;
    private String categoryName;
    private String name;
    private String id;
    private String code;
    private Double version;
    private String formUuid;
    private String formName;
    //    private Boolean enabled;
//    private Boolean freeed;
    private String remark;
    private Integer deleteStatus;
    private Date deleteTime;
    private String nodeUuids;
    private String nodeType;
    private String nodeName;
    private String nodeId;
    private String nodeUserAttribute;
    private String userValue;
    private String userArgValue;
    private String userOrgId;
    private String creators;
    private String users;
    private String monitors;
    private String admins;
    private String viewers;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the rowId
     */
    public String getRowId() {
        return rowId;
    }

    /**
     * @param rowId 要设置的rowId
     */
    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the modifyTime
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime 要设置的modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName 要设置的formName
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

//    /**
//     * @return the enabled
//     */
//    public Boolean getEnabled() {
//        return enabled;
//    }
//
//    /**
//     * @param enabled 要设置的enabled
//     */
//    public void setEnabled(Boolean enabled) {
//        this.enabled = enabled;
//    }
//
//    /**
//     * @return the freeed
//     */
//    public Boolean getFreeed() {
//        return freeed;
//    }
//
//    /**
//     * @param freeed 要设置的freeed
//     */
//    public void setFreeed(Boolean freeed) {
//        this.freeed = freeed;
//    }

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

    /**
     * @return the deleteStatus
     */
    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * @param deleteStatus 要设置的deleteStatus
     */
    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    /**
     * @return the deleteTime
     */
    public Date getDeleteTime() {
        return deleteTime;
    }

    /**
     * @param deleteTime 要设置的deleteTime
     */
    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    /**
     * @return the nodeUuids
     */
    public String getNodeUuids() {
        return nodeUuids;
    }

    /**
     * @param nodeUuids 要设置的nodeUuids
     */
    public void setNodeUuids(String nodeUuids) {
        this.nodeUuids = nodeUuids;
    }

    /**
     * @return the nodeType
     */
    public String getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType 要设置的nodeType
     */
    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return the nodeName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName 要设置的nodeName
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the nodeId
     */
    public String getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId 要设置的nodeId
     */
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the nodeUserAttribute
     */
    public String getNodeUserAttribute() {
        return nodeUserAttribute;
    }

    /**
     * @param nodeUserAttribute 要设置的nodeUserAttribute
     */
    public void setNodeUserAttribute(String nodeUserAttribute) {
        this.nodeUserAttribute = nodeUserAttribute;
    }

    /**
     * @return the userValue
     */
    public String getUserValue() {
        return userValue;
    }

    /**
     * @param userValue 要设置的userValue
     */
    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    /**
     * @return the userArgValue
     */
    public String getUserArgValue() {
        return userArgValue;
    }

    /**
     * @param userArgValue 要设置的userArgValue
     */
    public void setUserArgValue(String userArgValue) {
        this.userArgValue = userArgValue;
    }

    /**
     * @return the userOrgId
     */
    public String getUserOrgId() {
        return userOrgId;
    }

    /**
     * @param userOrgId 要设置的userOrgId
     */
    public void setUserOrgId(String userOrgId) {
        this.userOrgId = userOrgId;
    }

    /**
     * @return the creators
     */
    public String getCreators() {
        return creators;
    }

    /**
     * @param creators 要设置的creators
     */
    public void setCreators(String creators) {
        this.creators = creators;
    }

    /**
     * @return the users
     */
    public String getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(String users) {
        this.users = users;
    }

    /**
     * @return the monitors
     */
    public String getMonitors() {
        return monitors;
    }

    /**
     * @param monitors 要设置的monitors
     */
    public void setMonitors(String monitors) {
        this.monitors = monitors;
    }

    /**
     * @return the admins
     */
    public String getAdmins() {
        return admins;
    }

    /**
     * @param admins 要设置的admins
     */
    public void setAdmins(String admins) {
        this.admins = admins;
    }

    /**
     * @return the viewers
     */
    public String getViewers() {
        return viewers;
    }

    /**
     * @param viewers 要设置的viewers
     */
    public void setViewers(String viewers) {
        this.viewers = viewers;
    }
}
