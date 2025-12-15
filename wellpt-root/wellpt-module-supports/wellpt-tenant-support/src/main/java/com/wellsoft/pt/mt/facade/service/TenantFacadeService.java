/*
 * @(#)2018年1月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.facade.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantPinyin;

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
 * 2018年1月4日.1	chenqiong		2018年1月4日		Create
 * </pre>
 * @date 2018年1月4日
 */
public interface TenantFacadeService extends Facade {

    /**
     * 分页查询
     *
     * @param queryInfo
     * @return
     */
    List<TenantBean> query(QueryInfo queryInfo);

    public Tenant getByAccount(String account);

    public void deleteTenant(String uuid);

    public void deleteTenant(Tenant tenant);

    public Tenant getTenantByUUID(String uuid);

    /**
     * 根据租户UUID，获取租户
     *
     * @param tenantUuid
     * @return
     */
    public Tenant get(String uuid);

    /**
     * 获取所有有效的租户
     *
     * @return
     */
    public List<Tenant> getActiveTenants();

    /**
     * 根据用户ID获取租户信息
     *
     * @param id
     * @return
     */
    public Tenant getById(String id);

    public void saveTenantPinyin(TenantPinyin pinyin);

    void saveTenant(Tenant tenant);

    void deleteTenantPinyinByUuid(String uuid);

    List<Tenant> listByExample(Tenant tenant);

    /**
     * @param uuid
     * @return
     */
    long countByDatabaseConfigUuid(String uuid);

}
