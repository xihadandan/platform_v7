package com.wellsoft.pt.security.audit.dao;

// Generated 2011-4-26 9:37:16 by Hibernate Tools 3.4.0.CR1

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.List;
import java.util.Map;

/**
 *
 */
public interface RoleDao extends JpaDao<Role, String> {

    /**
     * 查询是否已经存在ID的实体列表
     *
     * @param paramMap
     * @return
     */
    List<Role> getListByUuidId(Map<String, Object> paramMap);

    Role getById(String id);

    // 获取平台角色和本单位的角色
    List<Role> queryRoleListOfUnitIdAndPTRoleList(String unitId, String... appIds);

    List<Role> queryRoleList(String unitId, String[] toArray);
}
