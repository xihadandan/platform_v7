/*
 * @(#)2013-4-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.security.superadmin.dao.DatabaseConfigDao;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import com.wellsoft.pt.security.superadmin.provider.DatabaseConfigProviderFactory;
import com.wellsoft.pt.security.superadmin.service.DatabaseConfigService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static com.wellsoft.pt.security.superadmin.web.DatabaseConfigController.databaseTypes;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	rzhu		2013-4-24		Create
 * </pre>
 * @date 2013-4-24
 */
@Service
public class DatabaseConfigServiceImpl extends AbstractJpaServiceImpl<DatabaseConfig, DatabaseConfigDao, String>
        implements DatabaseConfigService {

    @Autowired
    private TenantFacadeService tenantFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.DatabaseConfigService#getAll()
     */
    @Override
    public List<DatabaseConfig> getAll() {
        return this.dao.listAllByOrderPage(null, "code asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.DatabaseConfigService#save(com.wellsoft.pt.security.superadmin.entity.DatabaseConfig)
     */
    @Override
    @Transactional
    public DatabaseConfig saveDatabaseConfig(DatabaseConfig databaseAdmin) {
        DatabaseConfig model = new DatabaseConfig();
        if (StringUtils.isNotBlank(databaseAdmin.getUuid())) {
            model = this.dao.getOne(databaseAdmin.getUuid());
        }
        BeanUtils.copyProperties(databaseAdmin, model);
        // 设置数据库类型显示名称
        model.setTypeName(databaseTypes.get(model.getType()));
        this.dao.save(model);

        // 更新租户数据库
        Tenant example = new Tenant();
        example.setDatabaseConfigUuid(model.getUuid());
        List<Tenant> tenants = tenantFacadeService.listByExample(example);
        for (Tenant tenant : tenants) {
            String type = model.getType();
            String server = model.getHost();
            String port = model.getPort();

            if (!type.equals(model.getType())) {
                throw new RuntimeException("租户[" + tenant.getAccount() + "]已经使用类型为" + type + "的数据库，不能更改成为"
                        + model.getType() + "的数据库!");
            }
            tenant.setJdbcType(type);
            tenant.setJdbcServer(server);
            tenant.setJdbcPort(port);

            this.tenantFacadeService.saveTenant(tenant);
        }

        return this.dao.getOne(model.getUuid());
    }

    /**
     * 测试数据库的连接性
     * <p>
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.security.superadmin.service.DatabaseConfigService#checkConnectionStatus()
     */
    @Override
    public boolean checkConnectionStatus(String uuid) {
        DatabaseConfig databaseConfig = this.dao.getOne(uuid);
        if (databaseConfig == null) {
            throw new RuntimeException();
        }
        return DatabaseConfigProviderFactory.getConfigProvider(databaseConfig.getType()).checkConnectionStatus(
                databaseConfig);
    }

    /**
     * 根据UUID获取数据库配置
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.DatabaseConfigService#get(java.lang.String)
     */
    @Override
    public DatabaseConfig get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * 删除数据库配置方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.DatabaseConfigService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        long count = tenantFacadeService.countByDatabaseConfigUuid(uuid);
        if (count > 0) {
            throw new RuntimeException("数据库配置已经被使用，无法删除!");
        }

        this.dao.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.DatabaseConfigService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

}
