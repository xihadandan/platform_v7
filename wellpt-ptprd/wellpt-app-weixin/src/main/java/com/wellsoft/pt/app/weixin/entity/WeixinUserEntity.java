/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@javax.persistence.Entity
@Table(name = "weixin_user")
@DynamicUpdate
@DynamicInsert
public class WeixinUserEntity extends Entity {
    private static final long serialVersionUID = 5276667368407195075L;

    // 微信配置信息Uuid
    private Long configUuid;

    // 行政组织UUID
    private Long orgUuid;

    // 行政组织版本UUID
    private Long orgVersionUuid;

    // 企业ID
    private String corpId;

    // oa系统的用户ID
    private String oaUserId;

    // 成员UserID。对应管理端的账号
    private String userId;

    // 成员名称
    private String name;

    // 成员所属部门id列表
    private String departmentIds;

    // 职务信息
    private String position;

    // 激活状态: 1=已激活，2=已禁用，4=未激活，5=退出企业。
    private Integer status;

    // 是否启用
    private Integer enable;

    // 是否为部门负责人：0-否；1-是。
    private Integer isLeader;

    // 扩展属性
    private String extAttr;

    // 手机号码隐藏
    private Integer hideMobile;

    // 座机
    private String telephone;

    // 部门内的排序值，默认为0。数量必须和department一致，数值越大排序越前面
    private String orders;

    // 主部门，仅当应用对主部门有查看权限时返回
    private Long mainDepartment;

    // 别名
    private String alias;

    // 表示在所在的部门内是否为部门负责人。0-否；1-是。是一个列表，数量必须与department一致
    private String isLeaderInDepts;

    // 直属上级UserID
    private String directLeaders;

    // 成员对外属性
    private String externalProfile;

    /**
     * @return the configUuid
     */
    public Long getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(Long configUuid) {
        this.configUuid = configUuid;
    }

    /**
     * @return the orgUuid
     */
    public Long getOrgUuid() {
        return orgUuid;
    }

    /**
     * @param orgUuid 要设置的orgUuid
     */
    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    /**
     * @return the orgVersionUuid
     */
    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    /**
     * @param orgVersionUuid 要设置的orgVersionUuid
     */
    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    /**
     * @return the corpId
     */
    public String getCorpId() {
        return corpId;
    }

    /**
     * @param corpId 要设置的corpId
     */
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    /**
     * @return the userId
     */
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
     * @return the oaUserId
     */
    public String getOaUserId() {
        return oaUserId;
    }

    /**
     * @param oaUserId 要设置的oaUserId
     */
    public void setOaUserId(String oaUserId) {
        this.oaUserId = oaUserId;
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
     * @return the departmentIds
     */
    public String getDepartmentIds() {
        return departmentIds;
    }

    /**
     * @param departmentIds 要设置的departmentIds
     */
    public void setDepartmentIds(String departmentIds) {
        this.departmentIds = departmentIds;
    }

    /**
     * @return the position
     */
    public String getPosition() {
        return position;
    }

    /**
     * @param position 要设置的position
     */
    public void setPosition(String position) {
        this.position = position;
    }

    /**
     * @return the status
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * @return the enable
     */
    public Integer getEnable() {
        return enable;
    }

    /**
     * @param enable 要设置的enable
     */
    public void setEnable(Integer enable) {
        this.enable = enable;
    }

    /**
     * @return the isLeader
     */
    public Integer getIsLeader() {
        return isLeader;
    }

    /**
     * @param isLeader 要设置的isLeader
     */
    public void setIsLeader(Integer isLeader) {
        this.isLeader = isLeader;
    }

    /**
     * @return the extAttr
     */
    public String getExtAttr() {
        return extAttr;
    }

    /**
     * @param extAttr 要设置的extAttr
     */
    public void setExtAttr(String extAttr) {
        this.extAttr = extAttr;
    }

    /**
     * @return the hideMobile
     */
    public Integer getHideMobile() {
        return hideMobile;
    }

    /**
     * @param hideMobile 要设置的hideMobile
     */
    public void setHideMobile(Integer hideMobile) {
        this.hideMobile = hideMobile;
    }

    /**
     * @return the telephone
     */
    public String getTelephone() {
        return telephone;
    }

    /**
     * @param telephone 要设置的telephone
     */
    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    /**
     * @return the orders
     */
    public String getOrders() {
        return orders;
    }

    /**
     * @param orders 要设置的orders
     */
    public void setOrders(String orders) {
        this.orders = orders;
    }

    /**
     * @return the mainDepartment
     */
    public Long getMainDepartment() {
        return mainDepartment;
    }

    /**
     * @param mainDepartment 要设置的mainDepartment
     */
    public void setMainDepartment(Long mainDepartment) {
        this.mainDepartment = mainDepartment;
    }

    /**
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias 要设置的alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return the isLeaderInDepts
     */
    public String getIsLeaderInDepts() {
        return isLeaderInDepts;
    }

    /**
     * @param isLeaderInDepts 要设置的isLeaderInDepts
     */
    public void setIsLeaderInDepts(String isLeaderInDepts) {
        this.isLeaderInDepts = isLeaderInDepts;
    }

    /**
     * @return the directLeaders
     */
    public String getDirectLeaders() {
        return directLeaders;
    }

    /**
     * @param directLeaders 要设置的directLeaders
     */
    public void setDirectLeaders(String directLeaders) {
        this.directLeaders = directLeaders;
    }

    /**
     * @return the externalProfile
     */
    public String getExternalProfile() {
        return externalProfile;
    }

    /**
     * @param externalProfile 要设置的externalProfile
     */
    public void setExternalProfile(String externalProfile) {
        this.externalProfile = externalProfile;
    }
    
}
