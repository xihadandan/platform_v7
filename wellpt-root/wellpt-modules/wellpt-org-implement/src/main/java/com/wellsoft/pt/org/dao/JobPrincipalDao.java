package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.JobPrincipal;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 部门负责人dao
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-26.1  zhengky	2014-9-26	  Create
 * </pre>
 * @date 2014-9-26
 */
@Repository
public class JobPrincipalDao extends OrgHibernateDao<JobPrincipal, String> {

    private static final String GET_PRINCIPAL_BY_JOB_UUID = "from JobPrincipal jp where jp.type = '0' and jp.jobUuid = :jobUuid ";
    private static final String DELETE_PRINCIPAL_BY_JOB_UUID = "delete from JobPrincipal jp where jp.type = '0' and jp.jobUuid = :jobUuid";

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<JobPrincipal> getPrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("jobUuid", deptUuid);
        return this.find(GET_PRINCIPAL_BY_JOB_UUID, values);
    }

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("jobUuid", deptUuid);
        this.batchExecute(DELETE_PRINCIPAL_BY_JOB_UUID, values);
    }
}
