package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 多部门人员审核表
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年8月12日.1	liuyz		2021年8月12日		Create
 * </pre>
 * @date 2021年8月12日
 */
@Entity
@Table(name = "MULTI_DEPT_USER_AUDIT")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiDeptUserAudit extends TenantEntity {

    /**
     *
     */
    private static final long serialVersionUID = -7154646995062875692L;

    // 平台用户id
    private String userId;
    // 钉钉用户id
    private String dingUserId;
    // 钉钉用户唯一标识
    private String unionId;
    // 员工名称
    private String name;
    // 登录名
    private String loginName;
    // 员工编号
    private String employeeNumber;
    // 部门id多个用;分隔
    private String deptIds;
    // 部门名称，多个用;隔开
    private String deptNames;
    // 钉钉职位
    private String jobName;
    // 审核前的OA主职位
    private String beforeAuditMainJob;
    // 审核后的OA主职位
    private String afterAuditMainJob;
    // 同步时间
    private Date syncTime;
    // 审核时间
    private Date auditTime;
    // 审核人
    private String auditUser;
    // 审核状态  1：已审核  0：待审核
    private Integer auditStatus;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDingUserId() {
        return dingUserId;
    }

    public void setDingUserId(String dingUserId) {
        this.dingUserId = dingUserId;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDeptIds() {
        return deptIds;
    }

    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

    public String getDeptNames() {
        return deptNames;
    }

    public void setDeptNames(String deptNames) {
        this.deptNames = deptNames;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
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

    public Date getSyncTime() {
        return syncTime;
    }

    public void setSyncTime(Date syncTime) {
        this.syncTime = syncTime;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(String auditUser) {
        this.auditUser = auditUser;
    }

    public Integer getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(Integer auditStatus) {
        this.auditStatus = auditStatus;
    }

}
