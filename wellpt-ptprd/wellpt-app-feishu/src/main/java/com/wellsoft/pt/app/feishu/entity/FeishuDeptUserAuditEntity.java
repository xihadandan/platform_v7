package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;
import java.util.Date;

/**
 * 飞书多部门人员审核表
 */
@javax.persistence.Entity
@Table(name = "feishu_dept_user_audit")
@DynamicUpdate
@DynamicInsert
public class FeishuDeptUserAuditEntity extends Entity {

    /**
     * 飞书用户表uuid
     */
    private Long feishuUserUuid;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * oa账号名
     */
    private String loginName;

    /**
     * 飞书所属部门
     */
    private String departments;

    /**
     * 飞书职位
     */
    private String jobTitle;

    /**
     * 审核前的OA主职位
     */
    private String beforeAuditMainJob;

    /**
     * 审核后的OA主职位
     */
    private String afterAuditMainJob;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核用户ID
     */
    private String auditUserId;

    /**
     * 审核用户名称
     */
    private String auditUserName;

    /**
     * 审核状态：0：待审核，1：已审核
     */
    private String auditStatus;

    /**
     * 飞书职位json
     */
    private String jobJson;

    /**
     * oa职位json
     */
    private String oaJobJson;

    public Long getFeishuUserUuid() {
        return feishuUserUuid;
    }

    public void setFeishuUserUuid(Long feishuUserUuid) {
        this.feishuUserUuid = feishuUserUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getDepartments() {
        return departments;
    }

    public void setDepartments(String departments) {
        this.departments = departments;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getBeforeAuditMainJob() {
        return beforeAuditMainJob;
    }

    public void setBeforeAuditMainJob(String beforeAuditMainJob) {
        this.beforeAuditMainJob = beforeAuditMainJob;
    }

    public String getAfterAuditMainJob() {
        return afterAuditMainJob;
    }

    public void setAfterAuditMainJob(String afterAuditMainJob) {
        this.afterAuditMainJob = afterAuditMainJob;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditUserId() {
        return auditUserId;
    }

    public void setAuditUserId(String auditUserId) {
        this.auditUserId = auditUserId;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getJobJson() {
        return jobJson;
    }

    public void setJobJson(String jobJson) {
        this.jobJson = jobJson;
    }

    public String getOaJobJson() {
        return oaJobJson;
    }

    public void setOaJobJson(String oaJobJson) {
        this.oaJobJson = oaJobJson;
    }
}
