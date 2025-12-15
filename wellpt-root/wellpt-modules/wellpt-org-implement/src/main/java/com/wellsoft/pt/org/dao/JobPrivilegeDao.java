package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.JobPrivilege;
import com.wellsoft.pt.org.entity.JobPrivilegeId;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
@Repository
public class JobPrivilegeDao extends OrgHibernateDao<JobPrivilege, String> {
    private static final String QUERY_JOB_P_BY_JOBUUID =
            "select jobPrivilege.jobPrivilegeId.jobUuid as jobUuid,"
                    + "jobPrivilege.jobPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "jobPrivilege.jobPrivilegeId.tenantId  as tenantId  "
                    + "from JobPrivilege jobPrivilege  "
                    + "where "
                    + "jobPrivilege.jobPrivilegeId.jobUuid=:jobUuid";
    private static final String QUERY_JOB_P_BY_PUUID =
            "select jobPrivilege.jobPrivilegeId.jobUuid as jobUuid,"
                    + "jobPrivilege.jobPrivilegeId.privilegeUuid as privilegeUuid,"
                    + "jobPrivilege.jobPrivilegeId.tenantId  as tenantId  "
                    + "from JobPrivilege jobPrivilege  "
                    + "where "
                    + "jobPrivilege.jobPrivilegeId.privilegeUuid=:privilegeUuid";


    public List<JobPrivilege> getJobPrivilegeByJobUuid(String jobUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jobUuid", jobUuid);
        List<JobPrivilegeId> jobPrivilegeIds = this.query(QUERY_JOB_P_BY_JOBUUID, queryMap, JobPrivilegeId.class);
        List<JobPrivilege> jobPrivileges = new ArrayList<JobPrivilege>();
        JobPrivilege jobPrivilege;
        for (JobPrivilegeId jobPrivilegeId : jobPrivilegeIds) {
            jobPrivilege = new JobPrivilege();
            jobPrivilege.setJobPrivilegeId(jobPrivilegeId);
            jobPrivileges.add(jobPrivilege);
        }
        return jobPrivileges;
    }

    public List<JobPrivilege> getJobPrivilegeByPrivilegeUuid(String privilegeUuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("privilegeUuid", privilegeUuid);
        List<JobPrivilegeId> jobPrivilegeIds = this.query(QUERY_JOB_P_BY_PUUID, queryMap, JobPrivilegeId.class);
        List<JobPrivilege> jobPrivileges = new ArrayList<JobPrivilege>();
        JobPrivilege jobPrivilege;
        for (JobPrivilegeId jobPrivilegeId : jobPrivilegeIds) {
            jobPrivilege = new JobPrivilege();
            jobPrivilege.setJobPrivilegeId(jobPrivilegeId);
            jobPrivileges.add(jobPrivilege);
        }
        return jobPrivileges;
    }
}
