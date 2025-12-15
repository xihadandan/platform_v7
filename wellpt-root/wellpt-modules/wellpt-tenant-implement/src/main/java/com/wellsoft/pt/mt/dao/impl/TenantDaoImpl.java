/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.mt.dao.TenantDao;
import com.wellsoft.pt.mt.entity.Tenant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-21.1	lilin		2012-10-21		Create
 * </pre>
 * @date 2012-10-21
 */
@Repository
public class TenantDaoImpl extends AbstractJpaDaoImpl<Tenant, String> implements TenantDao {
    // 获取所有可用的租户，租户可用且不在审核中
    private static final String QUERY_ACTIVE_TENANT = "from Tenant tenant where tenant.status = :status";

    // 获取所有待审核的租户
    private static final String QUERY_REVIEW_TENANT = "from Tenant tenant where tenant.status = :status";

    private static final String COUNT_BY_DATABASE_CONFIG = "select count(*) from Tenant tenant where tenant.databaseConfigUuid = :databaseConfigUuid";

    private static final String QUERY_TENANT_BY_USERID = "select unit.tenant from CommonUnit unit left join unit.users user where user.id=:userId";

    /**
     * 更加账号名称获取该租户信息
     *
     * @param account
     * @return
     */
    @Override
    public Tenant getByAccount(String account) {
        return getOneByHQL("from Tenant where account='" + account + "'", null);
    }

    /**
     * 根据用户ID获取租户信息
     *
     * @param id
     * @return
     */
    @Override
    public Tenant getById(String id) {
        return getOneByHQL("from Tenant where id='" + id + "'", null);
    }

    /**
     * 获取所有可用的租户，租户可用且不在审核中
     *
     * @return
     */
    @Override
    public List<Tenant> getActiveTenants() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", Tenant.STATUS_ENABLED);
        return listByHQL(QUERY_ACTIVE_TENANT, values);
    }

    /**
     * 获取审核通过的租户，包括激活与未激活的
     *
     * @return
     */
    @Override
    public List<Tenant> getNormalTenants() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", Tenant.STATUS_ENABLED);
        return listByHQL(QUERY_REVIEW_TENANT, values);
    }

    /**
     * 获取所有待审核的租户
     *
     * @return
     */
    @Override
    public List<Tenant> getReviewTenants() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", Tenant.STATUS_TO_REVIEW);
        return listByHQL(QUERY_REVIEW_TENANT, values);
    }

    /**
     * @param uuid
     * @return
     */
    @Override
    public Long countByDatabaseConfigUuid(String databaseConfigUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("databaseConfigUuid", databaseConfigUuid);
        return getNumberByHQL(COUNT_BY_DATABASE_CONFIG, values);
    }

    /**
     * 根据用户ID获取单位信息
     *
     * @param userId
     * @return
     */
    @Override
    public List<Tenant> getByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("userId", userId);
        List<Tenant> tenants = listByHQL(QUERY_TENANT_BY_USERID, paramMap);
        return tenants;
    }
}
