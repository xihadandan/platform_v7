package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.JobRole;
import com.wellsoft.pt.org.entity.JobRoleId;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Repository
public class JobRoleDao extends OrgHibernateDao<JobRole, String> {

    private static final String QUERY_JOB_ROLE_BY_JOBUUID = "select jobRole.jobRoleId.jobUuid as jobUuid,"
            + "jobRole.jobRoleId.roleUuid as roleUuid," + "jobRole.jobRoleId.tenantId  as tenantId  "
            + "from JobRole jobRole  " + "where " + "jobRole.jobRoleId.jobUuid=:jobUuid";
    private static final String QUERY_JOB_ROLE_ROLE_UUID = "select jobRole.jobRoleId.jobUuid as jobUuid,"
            + "jobRole.jobRoleId.roleUuid as roleUuid," + "jobRole.jobRoleId.tenantId  as tenantId  "
            + "from JobRole jobRole  " + "where "
            + "jobRole.jobRoleId.roleUuid=:roleUuid and jobRole.jobRoleId.tenantId=:tenantId";
    private static final String DELETE_JOB_ROLE_BY_JOBUUID_ROLE_UUID = "delete JobRole u " + "where"
            + " u.jobRoleId.jobUuid=:jobUuid and " + " u.jobRoleId.roleUuid=:roleUuid";

    public List<JobRole> getJobRoleByJobUuid(String jobUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobUuid", jobUuid);
        List<JobRoleId> jobRoleIds = this.query(QUERY_JOB_ROLE_BY_JOBUUID, queryMap, JobRoleId.class);
        List<JobRole> jobRoles = new ArrayList<JobRole>();
        JobRole jobRole = null;
        for (JobRoleId jobRoleId : jobRoleIds) {
            jobRole = new JobRole();
            jobRole.setJobRoleId(jobRoleId);
            jobRoles.add(jobRole);
        }
        return jobRoles;
    }

    public List<JobRole> getJobRoleByRoleUuid(String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("roleUuid", roleUuid);
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        List<JobRoleId> jobRoleIds = this.query(QUERY_JOB_ROLE_ROLE_UUID, queryMap, JobRoleId.class);
        List<JobRole> jobRoles = new ArrayList<JobRole>();
        JobRole jobRole = null;
        for (JobRoleId jobRoleId : jobRoleIds) {
            jobRole = new JobRole();
            jobRole.setJobRoleId(jobRoleId);
            jobRoles.add(jobRole);
        }
        return jobRoles;
    }

    public void deleteJobRoleByJobUuidAndRoleUuid(String jobUuid, String roleUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobUuid", jobUuid);
        queryMap.put("roleUuid", roleUuid);
        this.batchExecute(DELETE_JOB_ROLE_BY_JOBUUID_ROLE_UUID, queryMap);
    }

    public void saveJobRole(JobRole jobRole) {
        this.save(jobRole);
    }

    public void deleteJobRole(JobRole jobRole) {
        this.delete(jobRole);
    }

}
