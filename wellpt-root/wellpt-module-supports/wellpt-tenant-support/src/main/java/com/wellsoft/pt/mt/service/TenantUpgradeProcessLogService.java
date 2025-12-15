/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-15.1	linz		2016-03-15		Create
 * </pre>
 * @date 2016-03-15
 */
public interface TenantUpgradeProcessLogService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    TenantUpgradeProcessLog get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<TenantUpgradeProcessLog> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<TenantUpgradeProcessLog> findByExample(TenantUpgradeProcessLog example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(TenantUpgradeProcessLog entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<TenantUpgradeProcessLog> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(TenantUpgradeProcessLog entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<TenantUpgradeProcessLog> entities);

    public QueryData queryDataGrid(String searchValue, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);
}
