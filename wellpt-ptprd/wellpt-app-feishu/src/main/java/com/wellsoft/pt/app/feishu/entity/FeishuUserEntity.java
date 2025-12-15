package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;
import java.util.Date;

/**
 * 飞书用户信息实体类
 */
@javax.persistence.Entity
@Table(name = "feishu_user")
@DynamicUpdate
@DynamicInsert
public class FeishuUserEntity extends Entity {

    /**
     * 飞书配置信息Uuid
     */
    private Long configUuid;

    /**
     * 行政组织UUID
     */
    private Long orgUuid;

    /**
     * 行政组织版本UUID
     */
    private Long orgVersionUuid;

    /**
     * 飞书应用的App ID
     */
    private String appId;

    /**
     * oa系统的用户ID
     */
    private String oaUserId;

    /**
     * 飞书用户的union_id，应用开发商发布的不同应用中同一用户的标识
     */
    private String unionId;

    /**
     * 飞书用户的user_id，租户内用户的唯一标识
     */
    private String userId;

    /**
     * 飞书用户的open_id，应用内用户的唯一标识
     */
    private String openId;

    /**
     * 用户名
     */
    private String name;

    /**
     * 英文名
     */
    private String enName;

    /**
     * 别名
     */
    private String nickname;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 手机号码可见性
     */
    private Boolean mobileVisible;

    /**
     * 性别(0：未知1：男2：女3：其他)
     */
    private Integer gender;

    /**
     * 用户头像信息
     */
    private String avatar;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 用户所属部门的ID列表
     */
    private String departmentIds;

    /**
     * 用户的直接主管的用户ID
     */
    private String leaderUserId;

    /**
     * 入职时间，时间戳格式
     */
    private Date joinTime;

    /**
     * 工号
     */
    private String employeeNo;

    /**
     * 职务
     */
    private String jobTitle;

    /**
     * 部门路径
     */
    private String departmentPath;

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

    public Long getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOaUserId() {
        return oaUserId;
    }

    public void setOaUserId(String oaUserId) {
        this.oaUserId = oaUserId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Boolean getMobileVisible() {
        return mobileVisible;
    }

    public void setMobileVisible(Boolean mobileVisible) {
        this.mobileVisible = mobileVisible;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepartmentIds() {
        return departmentIds;
    }

    public void setDepartmentIds(String departmentIds) {
        this.departmentIds = departmentIds;
    }

    public String getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(String leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public Date getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(Date joinTime) {
        this.joinTime = joinTime;
    }

    public String getEmployeeNo() {
        return employeeNo;
    }

    public void setEmployeeNo(String employeeNo) {
        this.employeeNo = employeeNo;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getDepartmentPath() {
        return departmentPath;
    }

    public void setDepartmentPath(String departmentPath) {
        this.departmentPath = departmentPath;
    }
}
