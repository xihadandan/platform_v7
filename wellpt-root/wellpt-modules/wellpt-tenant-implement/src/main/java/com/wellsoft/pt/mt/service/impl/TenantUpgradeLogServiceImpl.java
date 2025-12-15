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
import com.wellsoft.pt.mt.entity.TenantUpgradeLog;
import com.wellsoft.pt.mt.service.TenantUpgradeLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/**
 * Description: 升级日记类
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
public class TenantUpgradeLogServiceImpl extends BaseServiceImpl implements TenantUpgradeLogService {
    @Override
    public TenantUpgradeLog get(String uuid) {
        // TODO Auto-generated method stub
        return this.getCommonDao().get(TenantUpgradeLog.class, uuid);
    }

    @Override
    public List<TenantUpgradeLog> getAll() {
        // TODO Auto-generated method stub
        return this.getCommonDao().getAll(TenantUpgradeLog.class);
    }

    @Override
    public List<TenantUpgradeLog> findByExample(TenantUpgradeLog example) {
        // TODO Auto-generated method stub
        return this.getCommonDao().findByExample(example);
    }

    @Override
    public void save(TenantUpgradeLog entity) {
        // TODO Auto-generated method stub
        this.getCommonDao().save(entity);
    }

    @Override
    public void saveAll(Collection<TenantUpgradeLog> entities) {
        // TODO Auto-generated method stub
        this.getCommonDao().saveAll(entities);
    }

    @Override
    public void remove(TenantUpgradeLog entity) {
        // TODO Auto-generated method stub
        this.getCommonDao().delete(entity);
    }

    @Override
    public void removeAll(Collection<TenantUpgradeLog> entities) {
        // TODO Auto-generated method stub
        this.getCommonDao().deleteAll(entities);
    }

    @Override
    public QueryData queryDataGrid(String searchValue, JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // TODO Auto-generated method stub
        return null;
    }
}
