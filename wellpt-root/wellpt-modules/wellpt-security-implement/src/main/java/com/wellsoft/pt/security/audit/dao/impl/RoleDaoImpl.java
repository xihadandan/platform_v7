package com.wellsoft.pt.security.audit.dao.impl;

// Generated 2011-4-26 9:37:16 by Hibernate Tools 3.4.0.CR1

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.audit.dao.RoleDao;
import com.wellsoft.pt.security.audit.entity.Role;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
@Repository
public class RoleDaoImpl extends AbstractJpaDaoImpl<Role, String> implements RoleDao {
    private static final String QUERY_BY_UUID_ID = "from Role role where role.uuid != :uuid and role.id = :id";

    /**
     * 查询是否已经存在ID的实体列表
     *
     * @param paramMap
     * @return
     */
    public List<Role> getListByUuidId(Map<String, Object> paramMap) {
        return this.listByHQL(QUERY_BY_UUID_ID, paramMap);
    }

    public Role getById(String id) {
        List<Role> roleList = this.listByFieldEqValue("id", id);
        return CollectionUtils.isNotEmpty(roleList) ? roleList.get(0) : null;
    }

    // 获取平台角色和本单位的角色
    public List<Role> queryRoleListOfUnitIdAndPTRoleList(String unitId, String... appIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("unitId", MultiOrgSystemUnit.PT_ID);
        values.put("appIds", appIds);
        List<Role> ptRoles = this.listByNameHQLQuery("queryRoleList", values);
        if (!unitId.equals(MultiOrgSystemUnit.PT_ID)) {
            values.put("unitId", unitId);
            ptRoles.addAll(this.listByNameHQLQuery("queryRoleList", values));
        }
        return ptRoles;
    }

    @Override
    public List<Role> queryRoleList(String unitId, String... appIds) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("unitId", unitId);
        values.put("appIds", appIds);
        return this.listByNameHQLQuery("queryRoleList", values);
    }
}
