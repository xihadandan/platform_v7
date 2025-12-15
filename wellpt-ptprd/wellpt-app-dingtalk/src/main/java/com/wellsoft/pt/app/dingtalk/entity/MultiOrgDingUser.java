package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 钉钉部门用户信息表
 *
 * @author Well
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月29日.1	Well		2020年5月29日		Create
 * </pre>
 * @date 2020年5月29日
 */
@Entity
@Table(name = "MULTI_ORG_DING_USER_INFO")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiOrgDingUser extends TenantEntity {

    /**
     *
     */
    private static final long serialVersionUID = -7154646885062875692L;

    private String userId; // 平台用户id
    private String ding_userId; // 钉钉用户id
    private String unionId; // 钉钉用户唯一标识
    private int ptIsActive; // 平台是否确认
    private String orderInDepts; // 对应部门的排序
    private String openId; // 同unionid
    private String roles; // 角色信息
    private String mobile; // 电话信息
    private String officePhone; // 办公电话
    private int active; // 是否激活
    private String avatar; // 头像url
    private int isAdmin; // 是否是管理员
    private String tags; // 标签
    private int isHide; // 是否号码影藏
    private String isLeaderInDepts; // 在对应部门是否为主管
    private int isBoss; // 是否为boss
    private int isSenior; // 是否为高管
    private String name; // 员工名称
    private String stateCode; // 国家地区码
    private String department; // 所属部门
    private String email; // 邮箱
    private String employeeNumber; // 员工编号
    private String remark; // 备注
    private String position; // 职位
    private String ptOriginPwd; // 职位
    private String idNumber; // 证件号码

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDing_userId() {
        return ding_userId;
    }

    public void setDing_userId(String ding_userId) {
        this.ding_userId = ding_userId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public int getPtIsActive() {
        return ptIsActive;
    }

    public void setPtIsActive(int ptIsActive) {
        this.ptIsActive = ptIsActive;
    }

    public String getOrderInDepts() {
        return orderInDepts;
    }

    public void setOrderInDepts(String orderInDepts) {
        this.orderInDepts = orderInDepts;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(int isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getIsHide() {
        return isHide;
    }

    public void setIsHide(int isHide) {
        this.isHide = isHide;
    }

    public String getIsLeaderInDepts() {
        return isLeaderInDepts;
    }

    public void setIsLeaderInDepts(String isLeaderInDepts) {
        this.isLeaderInDepts = isLeaderInDepts;
    }

    public int getIsBoss() {
        return isBoss;
    }

    public void setIsBoss(int isBoss) {
        this.isBoss = isBoss;
    }

    public int getIsSenior() {
        return isSenior;
    }

    public void setIsSenior(int isSenior) {
        this.isSenior = isSenior;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPtOriginPwd() {
        return ptOriginPwd;
    }

    public void setPtOriginPwd(String ptOriginPwd) {
        this.ptOriginPwd = ptOriginPwd;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }
}
