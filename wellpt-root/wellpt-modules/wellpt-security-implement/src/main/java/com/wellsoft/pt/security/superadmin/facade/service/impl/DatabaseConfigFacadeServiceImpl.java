/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.facade.service.impl;

import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService;
import com.wellsoft.pt.security.superadmin.service.DatabaseConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;

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
public class DatabaseConfigFacadeServiceImpl implements DatabaseConfigFacadeService {

    @Resource
    DatabaseConfigService databaseConfigService;

    @Override
    public DatabaseConfig getDatabaseConfigByUuid(String uuid) {
        return databaseConfigService.getOne(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService#saveDatabaseConfig(com.wellsoft.pt.security.superadmin.entity.DatabaseConfig)
     */
    @Override
    public DatabaseConfig saveDatabaseConfig(DatabaseConfig databaseAdmin) {
        return databaseConfigService.saveDatabaseConfig(databaseAdmin);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService#checkConnectionStatus(java.lang.String)
     */
    @Override
    public boolean checkConnectionStatus(String uuid) throws Exception {
        return databaseConfigService.checkConnectionStatus(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService#getAll()
     */
    @Override
    public Object getAll() {
        return databaseConfigService.listAll();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        databaseConfigService.remove(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        databaseConfigService.removeAll(uuids);
    }

}
