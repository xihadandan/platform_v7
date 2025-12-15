package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.Job;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 岗位维护DAO
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-7-29.1  zhengky	2014-7-29	  Create
 * </pre>
 * @date 2014-7-29
 */

@Repository
public class JobDao extends OrgHibernateDao<Job, String> {

    private static final String GET_JOB_BY_NAME_AND_DEPTNAME = " from Job job where job.name = :jobName  and job.departmentName = :departmentName";
    private static final String GET_JOB_BY_DEPTUUID = " select  j from Job j ,Duty d  where j.duty=d and j.departmentUuid = :departmentUuid order by d.ilevel desc,j.code,j.name";
    private static final String GET_JOB_BY_DUTYUUID = " select  j from Job j ,Duty d  where j.duty=d and d.uuid = :dutyUuid order by d.ilevel desc,j.code,j.name  ";

    /**
     * @param id
     * @return
     */
    public Job getById(String id) {
        return this.findUniqueBy("id", id);
    }

    /**
     * @param id
     * @return
     */
    public Job getByUuId(String uuid) {
        return this.findUniqueBy("uuid", uuid);
    }

    /**
     * 通过部门名称和岗位名称定位岗位
     *
     * @param jobname
     * @param deptname
     * @return
     */
    public List<Job> getJobByNameAndDeptName(String jobname, String deptname) {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("jobName", StringUtils.trimToEmpty(jobname));
        values.put("departmentName", StringUtils.trimToEmpty(deptname));
        return this.find(GET_JOB_BY_NAME_AND_DEPTNAME, values);
    }

    /**
     * 通过部门ID获得岗位
     *
     * @param jobname
     * @param deptname
     * @return
     */
    public List<Job> getJobByDeptUuid(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.find(GET_JOB_BY_DEPTUUID, values);
    }

    /**
     * 通过职务UUID获得职位
     *
     * @param deptUuid
     * @return
     */
    public List<Job> getJobByDutyUuid(String dutyUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dutyUuid", dutyUuid);
        return this.find(GET_JOB_BY_DUTYUUID, values);
    }

}
