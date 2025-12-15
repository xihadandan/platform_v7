/*
 * @(#)2018年4月18日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.dao.TenantDao;
import com.wellsoft.pt.mt.entity.Tenant;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月18日.1	chenqiong		2018年4月18日		Create
 * </pre>
 * @date 2018年4月18日
 */
public interface TenantService extends JpaService<Tenant, TenantDao, String> {
    /**
     * 分页查询
     *
     * @param queryInfo
     * @return
     */
    List<TenantBean> query(QueryInfo queryInfo);

    /**
     * @param newTenantId
     * @return
     */
    Tenant getById(String newTenantId);

    /**
     * @return
     */
    List<Tenant> getNormalTenants();

    /**
     * @return
     */
    List<Tenant> getReviewTenants();

    /**
     * @param account
     * @return
     */
    Tenant getByAccount(String account);

    /**
     * @param userId
     * @return
     */
    List<Tenant> getByUserId(String userId);

    /**
     * @param example
     * @return
     */
    List<Tenant> findByExample(Tenant example);

    /**
     * @param uuid
     * @return
     */
    long countByDatabaseConfigUuid(String uuid);

    /**
     * @return
     */
    List<Tenant> getActiveTenants();

    /**
     * @param uuid
     * @return
     */
    Tenant get(String uuid);

    /**
     * @param uuid
     * @return
     */
    Tenant getTenantByUUID(String uuid);

    /**
     * @param tenant
     */
    void deleteTenant(Tenant tenant);

    /**
     * @param uuid
     */
    void deleteTenant(String uuid);

    /**
     * @param tenant
     * @return
     */
    List<Tenant> listByExample(Tenant tenant);

}
