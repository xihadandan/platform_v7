/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.dao.NestedRoleDao;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.service.NestedRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class NestedRoleServiceImpl extends AbstractJpaServiceImpl<NestedRole, NestedRoleDao, String> implements
        NestedRoleService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.NestedRoleService#deleteByRold(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteByRold(String uuid) {
        dao.deleteByRold(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.NestedRoleService#getByRole(java.lang.String)
     */
    @Override
    public List<NestedRole> getByRole(String uuid) {
        return dao.getByRole(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.NestedRoleService#getAllParentRolesByRoleUuid(java.lang.String)
     */
    @Override
    public List<String> getAllParentRoleUuidsByRoleUuid(String roleUuid) {
        Set<String> parentRoleUuidSet = Sets.newLinkedHashSet();
        addParentRoleUuidsByRoleUuid(roleUuid, parentRoleUuidSet);
        return Arrays.asList(parentRoleUuidSet.toArray(new String[0]));
    }

    /**
     * 如何描述该方法
     *
     * @param roleUuid
     * @param parentRoleUuids
     */
    private void addParentRoleUuidsByRoleUuid(String roleUuid, Set<String> parentRoleUuidSet) {
        List<NestedRole> nestedRoles = dao.getByRole(roleUuid);
        List<String> nestedRoleUuids = Lists.newArrayList();
        for (NestedRole nestedRole : nestedRoles) {
            nestedRoleUuids.add(nestedRole.getUuid());
        }
        if (CollectionUtils.isEmpty(nestedRoleUuids)) {
            return;
        }

        String sql = "select t.role_uuid from audit_role_nested_role t where t.nested_role_uuid in (:nestedRoleUuids)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("nestedRoleUuids", nestedRoleUuids);
        List<String> parentRoleUuids = dao.listCharSequenceBySQL(sql, values);
        for (String parentRoleUuid : parentRoleUuids) {
            addParentRoleUuidsByRoleUuid(parentRoleUuid, parentRoleUuidSet);
        }
        parentRoleUuidSet.addAll(parentRoleUuids);
    }

}
