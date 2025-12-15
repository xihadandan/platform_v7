/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;
import com.wellsoft.pt.mt.service.TenantUpgradeProcessLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Description: 升级过程类
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
@Service
@Transactional
public class TenantUpgradeProcessLogServiceImpl extends BaseServiceImpl implements TenantUpgradeProcessLogService {
    @Override
    public TenantUpgradeProcessLog get(String uuid) {
        // TODO Auto-generated method stub
        return this.getCommonDao().get(TenantUpgradeProcessLog.class, uuid);
    }

    @Override
    public List<TenantUpgradeProcessLog> getAll() {
        // TODO Auto-generated method stub
        return this.getCommonDao().getAll(TenantUpgradeProcessLog.class);
    }

    @Override
    public List<TenantUpgradeProcessLog> findByExample(TenantUpgradeProcessLog example) {
        // TODO Auto-generated method stub
        return this.getCommonDao().findByExample(example);
    }

    @Override
    public void save(TenantUpgradeProcessLog entity) {
        // TODO Auto-generated method stub
        this.getCommonDao().save(entity);
    }

    @Override
    public void saveAll(Collection<TenantUpgradeProcessLog> entities) {
        // TODO Auto-generated method stub
        this.getCommonDao().saveAll(entities);
    }

    @Override
    public void remove(TenantUpgradeProcessLog entity) {
        // TODO Auto-generated method stub
        this.getCommonDao().delete(entity);
    }

    @Override
    public void removeAll(Collection<TenantUpgradeProcessLog> entities) {
        // TODO Auto-generated method stub
        this.getCommonDao().deleteAll(entities);
    }

    @Override
    public QueryData queryDataGrid(String searchValue, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // TODO Auto-generated method stub
        return null;
    }
}
