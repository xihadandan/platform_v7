package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.DeptPrincipal;
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
public class DeptPrincipalDao extends OrgHibernateDao<DeptPrincipal, String> {

    private static final String GET_MANAGER_BY_DEPARTMENT_UUID = "from DeptPrincipal dp where dp.type = '2' and dp.departmentUuid = :departmentUuid ";
    private static final String DELETE_MANAGER_BY_DEPARTMENT_UUID = "delete from DeptPrincipal dp where dp.type = '2' and dp.departmentUuid = :departmentUuid";

    private static final String GET_BRANCHED_BY_DEPARTMENT_UUID = "from DeptPrincipal dp where dp.type = '1' and dp.departmentUuid = :departmentUuid ";
    private static final String DELETE_BRANCHED_BY_DEPARTMENT_UUID = "delete from DeptPrincipal dp where dp.type = '1' and dp.departmentUuid = :departmentUuid";

    private static final String GET_PRINCIPAL_BY_DEPARTMENT_UUID = "from DeptPrincipal dp where dp.type = '0' and dp.departmentUuid = :departmentUuid ";
    private static final String DELETE_PRINCIPAL_BY_DEPARTMENT_UUID = "delete from DeptPrincipal dp where dp.type = '0' and dp.departmentUuid = :departmentUuid";

    //通过职位和用户id查找部门
    private static final String GET_PRINCIPAL_BY_ORG_ID = "from DeptPrincipal dp where dp.type = '0' and (dp.orgId = :userId or dp.orgId = :jobId) ";


    private static final String GET_ALL_PRINCIPAL_BY_DEPT_UUID = "from DeptPrincipal dp where dp.departmentUuid = :departmentUuid   ";

    /**
     * 根据部门UUID，获取部门管理员
     *
     * @param uuid
     */
    public List<DeptPrincipal> getManager(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.find(GET_MANAGER_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，删除部门管理员
     *
     * @param uuid
     */
    public void deleteManager(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        this.batchExecute(DELETE_MANAGER_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，获取部门分管领导
     *
     * @param uuid
     */
    public List<DeptPrincipal> getBranched(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.find(GET_BRANCHED_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，删除部门分管领导
     *
     * @param uuid
     */
    public void deleteBranched(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        this.batchExecute(DELETE_BRANCHED_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<DeptPrincipal> getPrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.find(GET_PRINCIPAL_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        this.batchExecute(DELETE_PRINCIPAL_BY_DEPARTMENT_UUID, values);
    }

    /**
     * 根据组织ID获得对应的部门
     */
    public List<DeptPrincipal> getPrincipalByOrgId(String userId, String jobId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("jobId", jobId);
        return this.find(GET_PRINCIPAL_BY_ORG_ID, values);
    }

    /**
     * 根据部门uuid,取得部门的所有领导人  createtime 2016-3-11
     *
     * @param deptUuid
     * @return
     * @author hongjz
     */
    public List<DeptPrincipal> getAllPrincipals(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentUuid", deptUuid);
        return this.find(GET_ALL_PRINCIPAL_BY_DEPT_UUID, values);
    }
}
