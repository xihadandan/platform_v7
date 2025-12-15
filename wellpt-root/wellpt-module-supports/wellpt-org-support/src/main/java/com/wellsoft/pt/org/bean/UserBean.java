/*
 * @(#)2013-1-11 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-11.1	zhulh		2013-1-11		Create
 * 2013-9-25    liuzq       2013-9-25       Update
 * </pre>
 * @date 2013-1-11
 */
public class UserBean extends User {

    private static final long serialVersionUID = 5523235521786861303L;

    // 所属部门ID
    private String departmentId;
    // 岗位
    private String jobName;
    // 岗位编码
    private String jobCode;
    // 上级领导
    private String leaderNames;

    //所有领导姓名
    private String allLeaderNames;
    //职位汇报对象
    private String reportToNames;

    // 上级领导ID
    private String leaderIds;
    // B岗人员
    private String deputyNames;
    // B岗人员ID
    private String deputyIds;
    // 所属群组
    private String groupNames;
    // 所属群组ID
    private String groupIds;
    // 公共库的用户ID
    private String commonUserId;
    // 证书主体
    private String subjectDN;
    // 接收短信消息
    private boolean receiveSmsMessage;
    //联系人姓名
    private String contactName;
    //邮件
    private String email;
    //主职位id
    private String majorJobId;
    //其他职位名称
    private String otherJobIds;

    //bo用户
    private String boUser;

    //bo密码
    private String boPwd;
    @UnCloneable
    private Set<Role> roles = new HashSet<Role>();
    @UnCloneable
    private Set<Privilege> privileges = new HashSet<Privilege>();

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getMajorJobId() {
        return majorJobId;
    }

    public void setMajorJobId(String majorJobId) {
        this.majorJobId = majorJobId;
    }

    public String getOtherJobIds() {
        return otherJobIds;
    }

    public void setOtherJobIds(String otherJobIds) {
        this.otherJobIds = otherJobIds;
    }

    public String getCommonUserId() {
        return commonUserId;
    }

    public void setCommonUserId(String commonUserId) {
        this.commonUserId = commonUserId;
    }

    /**
     * @return the departmentId
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId 要设置的departmentId
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName 要设置的jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return the jobCode
     */
    public String getJobCode() {
        return jobCode;
    }

    /**
     * @param jobCode 要设置的jobCode
     */
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

    /**
     * @return the leaderNames
     */
    public String getLeaderNames() {
        return leaderNames;
    }

    /**
     * @param leaderNames 要设置的leaderNames
     */
    public void setLeaderNames(String leaderNames) {
        this.leaderNames = leaderNames;
    }

    /**
     * @return the leaderIds
     */
    public String getLeaderIds() {
        return leaderIds;
    }

    /**
     * @param leaderIds 要设置的leaderIds
     */
    public void setLeaderIds(String leaderIds) {
        this.leaderIds = leaderIds;
    }

    /**
     * @return the deputyNames
     */
    public String getDeputyNames() {
        return deputyNames;
    }

    /**
     * @param deputyNames 要设置的deputyNames
     */
    public void setDeputyNames(String deputyNames) {
        this.deputyNames = deputyNames;
    }

    /**
     * @return the deputyIds
     */
    public String getDeputyIds() {
        return deputyIds;
    }

    /**
     * @param deputyIds 要设置的deputyIds
     */
    public void setDeputyIds(String deputyIds) {
        this.deputyIds = deputyIds;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
    }

    public String getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(String groupIds) {
        this.groupIds = groupIds;
    }

    /**
     * @return the subjectDN
     */
    public String getSubjectDN() {
        return subjectDN;
    }

    /**
     * @param subjectDN 要设置的subjectDN
     */
    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    /**
     * @return the receiveSmsMessage
     */
    public boolean isReceiveSmsMessage() {
        return receiveSmsMessage;
    }

    /**
     * @param receiveSmsMessage 要设置的receiveSmsMessage
     */
    public void setReceiveSmsMessage(boolean receiveSmsMessage) {
        this.receiveSmsMessage = receiveSmsMessage;
    }

    /**
     * @return the contactName
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName 要设置的contactName
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
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

    public String getBoUser() {
        return boUser;
    }

    /**
     * @param boUser 要设置的 boUser
     */

    public void setBoUser(String boUser) {
        this.boUser = boUser;
    }

    public String getBoPwd() {
        return boPwd;
    }

    /**
     * @param boPwd 要设置的 boPwd
     */

    public void setBoPwd(String boPwd) {
        this.boPwd = boPwd;
    }

    /*
     * 获取所有领导姓名
     */
    public String getAllLeaderNames() {
        return allLeaderNames;
    }

    public void setAllLeaderNames(String allLeaderNames) {
        this.allLeaderNames = allLeaderNames;
    }

    public String getReportToNames() {
        return reportToNames;
    }

    public void setReportToNames(String reportToNames) {
        this.reportToNames = reportToNames;
    }
}
