/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.mt.dao.TenantTemplateModuleDao;
import com.wellsoft.pt.mt.entity.TenantTemplateModule;

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
 * 2016-03-01.1	linz		2016-03-01		Create
 * </pre>
 * @date 2016-03-01
 */
public interface TenantTemplateModuleService extends JpaService<TenantTemplateModule, TenantTemplateModuleDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    TenantTemplateModule get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<TenantTemplateModule> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<TenantTemplateModule> findByExample(TenantTemplateModule example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(TenantTemplateModule entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<TenantTemplateModule> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    QueryData queryDataGrid(String searchValue, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo);

}
