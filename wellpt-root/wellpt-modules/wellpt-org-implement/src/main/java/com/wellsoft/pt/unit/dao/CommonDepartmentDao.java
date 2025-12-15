package com.wellsoft.pt.unit.dao;

import com.wellsoft.pt.org.dao.OrgHibernateDao;
import com.wellsoft.pt.unit.entity.CommonDepartment;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: CommonDepartmentDao.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Repository
public class CommonDepartmentDao extends OrgHibernateDao<CommonDepartment, String> {
    private static final String QUERY_DEPARTMENT_BY_ID = "from CommonDepartment dept where dept.unit.id = :commonUnitId and dept.id = :departmentId";

    private static final String QUERY_DEPARTMENT_BY_PARENTID = "from CommonDepartment dept where dept.parent.uuid = :parentUuid";

    private static final String QUERY_DEPARTMENT_BY_TENANT_ID = "from CommonDepartment dept where dept.unit.tenantId = :tenantId and dept.id = :departmentId";

    private static final String QUERY_DEPARTMENT_BY_UNIT = "from CommonDepartment dept where (dept.parent.uuid is null or dept.parent.uuid='') and dept.unit.uuid=:unitUuid and dept.isVisible=1";

    private static final String QUERY_DEPARTMENT_BY_UNIT_UUID = "from CommonDepartment dept where  dept.unit.uuid=:unitUuid";

    /**
     * 根据单位和部门ID获取唯一的公共库部门信息
     *
     * @param paramMap
     * @return
     */
    public List<CommonDepartment> getListByParamMap(Map<String, Object> paramMap) {
        List<CommonDepartment> list = new ArrayList<CommonDepartment>();
        if (paramMap.get("commonUnitId") != null && StringUtils.isNotBlank(
                (String) paramMap.get("commonUnitId"))) {
            list = this.find(QUERY_DEPARTMENT_BY_ID, paramMap);
        }
        if (paramMap.get("tenantId") != null && StringUtils.isNotBlank(
                (String) paramMap.get("tenantId"))) {
            list = this.find(QUERY_DEPARTMENT_BY_TENANT_ID, paramMap);
        }
        return list;
    }

    /**
     * 根据公共库部门父节点，获取公共库部门所有直接子节点
     *
     * @param paramMap
     * @return
     */
    public List<CommonDepartment> getListByParent(Map<String, Object> paramMap) {
        return this.find(QUERY_DEPARTMENT_BY_PARENTID, paramMap);
    }

    /**
     * 根据单位获得直接部门节点
     *
     * @param paramMap
     * @return
     */
    public List<CommonDepartment> getListByUnit(Map<String, Object> paramMap) {
        return this.find(QUERY_DEPARTMENT_BY_UNIT, paramMap);
    }

    /**
     * 根据单位获得直接部门节点
     *
     * @param paramMap
     * @return
     */
    public List<CommonDepartment> getListByUnitUuid(String uuid) {
        HashMap<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("unitUuid", uuid);
        return this.find(QUERY_DEPARTMENT_BY_UNIT_UUID, paramMap);
    }
}
