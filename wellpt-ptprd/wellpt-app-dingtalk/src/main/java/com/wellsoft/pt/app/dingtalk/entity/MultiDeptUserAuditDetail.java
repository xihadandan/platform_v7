package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 多部门人员审核详情表
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/11/15.1	liuyz		2021/11/15		Create
 * </pre>
 * @date 2021/11/15
 */
@Entity
@Table(name = "MULTI_DEPT_USER_AUDIT_DETAIL")
@DynamicUpdate
@DynamicInsert
@Deprecated
public class MultiDeptUserAuditDetail extends IdEntity {

    private String auditUuid;
    private Integer jobType; // 1 钉钉职位；2：OA职位
    private Integer isMain;
    private String deptId;
    private String deptName;
    private String jobId;
    private String jobName;
    private String jobIdPath;
    private String jobNamePath;

    public MultiDeptUserAuditDetail() {

    }

    // 钉钉职位
    public MultiDeptUserAuditDetail(String auditUuid, Integer isMain, String deptId, String deptName, String jobName) {
        this.auditUuid = auditUuid;
        this.jobType = 1;
        this.isMain = isMain;
        this.deptId = deptId;
        this.deptName = deptName;
        this.jobName = jobName;
    }

    // OA职位
    public MultiDeptUserAuditDetail(String auditUuid, Integer isMain, String jobId, String jobIdPath, String jobName, String jobNamePath) {
        this.auditUuid = auditUuid;
        this.jobType = 2;
        this.isMain = isMain;
        this.jobId = jobId;
        this.jobIdPath = jobIdPath;
        this.jobName = jobName;
        this.jobNamePath = jobNamePath;
    }

    public String getAuditUuid() {
        return auditUuid;
    }

    public void setAuditUuid(String auditUuid) {
        this.auditUuid = auditUuid;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }

    public Integer getIsMain() {
        return isMain;
    }

    public void setIsMain(Integer isMain) {
        this.isMain = isMain;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobIdPath() {
        return jobIdPath;
    }

    public void setJobIdPath(String jobIdPath) {
        this.jobIdPath = jobIdPath;
    }

    public String getJobNamePath() {
        return jobNamePath;
    }

    public void setJobNamePath(String jobNamePath) {
        this.jobNamePath = jobNamePath;
    }
}
