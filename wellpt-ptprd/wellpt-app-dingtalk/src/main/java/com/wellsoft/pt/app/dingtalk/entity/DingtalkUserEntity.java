/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;
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
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
@javax.persistence.Entity
@Table(name = "dingtalk_user")
@DynamicUpdate
@DynamicInsert
public class DingtalkUserEntity extends Entity {
    private static final long serialVersionUID = -1106064223468259881L;

    // 钉钉配置信息Uuid
    private Long configUuid;

    // 行政组织UUID
    private Long orgUuid;

    // 行政组织版本UUID
    private Long orgVersionUuid;

    // 应用的App ID
    private String appId;

    // oa系统的用户ID
    private String oaUserId;

    // 用户在当前开发者企业账号范围内的唯一标识
    private String unionId;

    // 用户的userId
    private String userId;

    // 用户名
    private String name;

    // 头像地址
    private String avatar;

    // 手机号
    private String mobile;

    // 是否号码隐藏
    private Boolean hideMobile;

    // 分机号
    private String telephone;

    // 员工工号
    private String jobNumber;

    // 职位
    private String title;

    // 邮箱
    private String email;

    // 是否是部门的主管
    private Boolean leader;

    // 主管部门的部门ID
    private Long leaderDeptId;

    // 主管部门的组织元素UUID
    private Long leaderOrgElementUuid;

    // 所属部门id列表，多个以分号隔开
    private String deptIdList;

    // 员工在部门中的排序
    private Long deptOrder;

    // 入职时间
    private Date hiredDate;

    // 是否激活了钉钉
    private Boolean active;

    // 备注
    private String remark;

    // 扩展属性
    private String extension;

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
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
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
     * @return the unionId
     */
    public String getUnionId() {
        return unionId;
    }

    /**
     * @param unionId 要设置的unionId
     */
    public void setUnionId(String unionId) {
        this.unionId = unionId;
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
     * @return the avatar
     */
    public String getAvatar() {
        return avatar;
    }

    /**
     * @param avatar 要设置的avatar
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * @return the mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile 要设置的mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return the hideMobile
     */
    public Boolean getHideMobile() {
        return hideMobile;
    }

    /**
     * @param hideMobile 要设置的hideMobile
     */
    public void setHideMobile(Boolean hideMobile) {
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
     * @return the jobNumber
     */
    public String getJobNumber() {
        return jobNumber;
    }

    /**
     * @param jobNumber 要设置的jobNumber
     */
    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    /**
     * @return the title
     */
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
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email 要设置的email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the leader
     */
    public Boolean getLeader() {
        return leader;
    }

    /**
     * @param leader 要设置的leader
     */
    public void setLeader(Boolean leader) {
        this.leader = leader;
    }

    /**
     * @return the leaderDeptId
     */
    public Long getLeaderDeptId() {
        return leaderDeptId;
    }

    /**
     * @param leaderDeptId 要设置的leaderDeptId
     */
    public void setLeaderDeptId(Long leaderDeptId) {
        this.leaderDeptId = leaderDeptId;
    }

    /**
     * @return the leaderOrgElementUuid
     */
    public Long getLeaderOrgElementUuid() {
        return leaderOrgElementUuid;
    }

    /**
     * @param leaderOrgElementUuid 要设置的leaderOrgElementUuid
     */
    public void setLeaderOrgElementUuid(Long leaderOrgElementUuid) {
        this.leaderOrgElementUuid = leaderOrgElementUuid;
    }

    /**
     * @return the deptIdList
     */
    public String getDeptIdList() {
        return deptIdList;
    }

    /**
     * @param deptIdList 要设置的deptIdList
     */
    public void setDeptIdList(String deptIdList) {
        this.deptIdList = deptIdList;
    }

    /**
     * @return the deptOrder
     */
    public Long getDeptOrder() {
        return deptOrder;
    }

    /**
     * @param deptOrder 要设置的deptOrder
     */
    public void setDeptOrder(Long deptOrder) {
        this.deptOrder = deptOrder;
    }

    /**
     * @return the hiredDate
     */
    public Date getHiredDate() {
        return hiredDate;
    }

    /**
     * @param hiredDate 要设置的hiredDate
     */
    public void setHiredDate(Date hiredDate) {
        this.hiredDate = hiredDate;
    }

    /**
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * @param active 要设置的active
     */
    public void setActive(Boolean active) {
        this.active = active;
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

    /**
     * @return the extension
     */
    public String getExtension() {
        return extension;
    }

    /**
     * @param extension 要设置的extension
     */
    public void setExtension(String extension) {
        this.extension = extension;
    }

}
