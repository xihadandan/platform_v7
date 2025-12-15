/*
 * @(#)2013-1-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.audit.dao.NestedRoleDao;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-21.1	zhulh		2013-1-21		Create
 * </pre>
 * @date 2013-1-21
 */
@Repository
public class NestedRoleDaoImpl extends AbstractJpaDaoImpl<NestedRole, String> implements NestedRoleDao {
    private static final String DELETE_BY_ROLE_UUID = "delete from NestedRole nr where nr.roleUuid = :roleUuid";
    private static final String GET_BY_ROLE_UUID = "from NestedRole nr where nr.roleUuid = :roleUuid";

    /**
     * @param uuid
     */
    @Transactional
    public void deleteByRold(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("roleUuid", uuid);
        deleteByHQL(DELETE_BY_ROLE_UUID, values);
    }

    /**
     * Description how to use this method
     *
     * @param uuid
     * @return
     */
    public List<NestedRole> getByRole(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("roleUuid", uuid);
        return this.listByHQL(GET_BY_ROLE_UUID, values);
    }

}
