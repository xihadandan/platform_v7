/*
 * @(#)2013-5-31 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.mgr.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.hibernate.MultiTenantDataSourceHelper;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.support.SQLServer2008TenantDatabaseBuilder;
import com.wellsoft.pt.jpa.support.TenantDatabaseBuilder;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.biz.TenantBusinessSerivce;
import com.wellsoft.pt.mt.dbmigrate.*;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantCreateProcessLog;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;
import com.wellsoft.pt.mt.mgr.TenantManagerService;
import com.wellsoft.pt.mt.service.TenantIdGeneratorService;
import com.wellsoft.pt.mt.service.TenantService;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import com.wellsoft.pt.security.superadmin.facade.service.DatabaseConfigFacadeService;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-31.1	zhulh		2013-5-31		Create
 * </pre>
 * @date 2013-5-31
 */
@Service
@Transactional
public class TenantManagerServiceImpl extends BaseServiceImpl implements TenantManagerService {
    // 用于格式化租户编号
    // private DecimalFormat decimalFormat = new DecimalFormat("0000");

    private static final String TENANT_ID_PATTERN = "T000";

    @Autowired
    private TenantService tenantService;

    @Autowired
    private DatabaseConfigFacadeService databaseConfigFacadeService;

    @Autowired
    private TenantIdGeneratorService tenantIdGeneratorService;

    @Autowired
    private TenantBusinessSerivce tenantBusinessSerivce;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.TenantManagerService#get(java.lang.String)
     */
    @Override
    public Tenant get(String tenantUuid) {
        return tenantService.getOne(tenantUuid);
    }

    /**
     * 如何描述该方法
     *
     * @param onlyTeanantModule
     * @return 模块代码, 模块名称前端解释
     */
    public List<String> getBuildableModules(Boolean onlyTeanantModule) {
        List<String> modules = new ArrayList<String>();
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        Map<String, IMigrateModule> moduleMap = applicationContext.getBeansOfType(IMigrateModule.class);
        for (IMigrateModule module : MigratorUtils.sortedModules(moduleMap.values())) {
            if (onlyTeanantModule && module instanceof IMigrateCommon == true) {
                continue;
            }
            modules.add(module.getCode());
        }
        return modules;
    }

    public boolean isDbExist(String tenantUuid, String dbaUser, String dbaPassword, ExecuteCallback executeCallback) {
        Tenant tenant = new Tenant(), tn;
        if (StringUtils.isBlank(tenantUuid) || (tn = tenantService.getOne(tenantUuid)) == null) {
            throw new RuntimeException("invalidate tenant,try again");
        }
        BeanUtils.copyProperties(tn, tenant);
        Properties dbaProp = MigratorUtils.getJdbcProperties(dbaUser, dbaPassword);
        return DbMigrateFactory.createDbMigrator(tenant.getJdbcType()).isDbExist(tenant, dbaProp, executeCallback);
    }

    @Override
    public void createTenantDb(String tenantUuid, String dbaUser, String dbaPassword, ExecuteCallback executeCallback) {
        Tenant tenant = new Tenant(), tn;
        if (StringUtils.isBlank(tenantUuid) || (tn = tenantService.getOne(tenantUuid)) == null) {
            throw new RuntimeException("invalidate tenant,try again");
        }
        BeanUtils.copyProperties(tn, tenant);
        Properties dbaProp = MigratorUtils.getJdbcProperties(dbaUser, dbaPassword);
        DbMigrateFactory.createDbMigrator(tenant.getJdbcType()).buildDb(tenant, dbaProp, executeCallback);
    }

    /**
     * 如何描述该方法
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.mgr.TenantManagerService#createTenantDb(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void migrateTenantDb(String tenantUuid, String dbaUser, String dbaPassword, ExecuteCallback executeCallback) {
        Tenant tenant = new Tenant(), tn;
        if (StringUtils.isBlank(tenantUuid) || (tn = tenantService.getOne(tenantUuid)) == null) {
            throw new RuntimeException("invalidate tenant,try again");
        }
        BeanUtils.copyProperties(tn, tenant);
        Properties dbaProp = MigratorUtils.getJdbcProperties(dbaUser, dbaPassword);
        DbMigrateFactory.createDbMigrator(tenant.getJdbcType()).migrateDb(tenant, dbaProp, executeCallback);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.mgr.TenantManagerService#createTenantDb(java.lang.String, java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public void migrateTenantDb(String tenantUuid, List<String> modules, String dbaUser, String dbaPassword,
                                ExecuteCallback executeCallback) {
        Tenant tenant = new Tenant(), tn;
        if (StringUtils.isBlank(tenantUuid) || (tn = tenantService.getOne(tenantUuid)) == null) {
            throw new RuntimeException("invalidate tenant,try again");
        }
        BeanUtils.copyProperties(tn, tenant);
        Properties dbaProp = MigratorUtils.getJdbcProperties(dbaUser, dbaPassword);
        DbMigrateFactory.createDbMigrator(tenant.getJdbcType()).migrateDb(tenant, modules, dbaProp, executeCallback);
    }

    @Override
    public void executeTenantSql(String tenantUuid, String sqlScript, ExecuteCallback executeCallback) {
        Tenant tenant = new Tenant(), tn;
        if (StringUtils.isBlank(tenantUuid) || (tn = tenantService.getOne(tenantUuid)) == null) {
            throw new RuntimeException("invalidate tenant,try again");
        }
        BeanUtils.copyProperties(tn, tenant);
        DbMigrateFactory.createDbMigrator(tenant.getJdbcType()).execute(tenant, sqlScript, executeCallback, tenant);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#saveTenant(com.wellsoft.pt.core.mt.entity.Tenant)
     */
    @Override
    public void saveTenant(TenantBean bean) {
        if (bean.getStatus() == null) {
            bean.setStatus(Tenant.STATUS_DISENABLED);
        }
        Tenant tenant = new Tenant();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            tenant = tenantService.getOne(bean.getUuid());
        } else {
            // 生成并设置租户ID
            String newTenantId = generateTenantId();
            while (tenantService.getById(newTenantId) != null) {
                newTenantId = generateTenantId();
            }
            bean.setId(newTenantId);
        }
        BeanUtils.copyProperties(bean, tenant);

        // 1、设置租户数据库信息
        if (bean.isCreateDatabase()) {
            tenant.setJdbcDatabaseName(IdPrefix.TENANT.getValue() + tenant.getId());
            tenant.setJdbcUsername(IdPrefix.TENANT.getValue() + tenant.getId());
            tenant.setJdbcPassword(tenant.getPassword());
        }

        // 2、从数据库配置中获取配置信息
        if (StringUtils.isNotBlank(tenant.getDatabaseConfigUuid())) {
            DatabaseConfig databaseConfig = databaseConfigFacadeService.getDatabaseConfigByUuid(tenant
                    .getDatabaseConfigUuid());
            String type = databaseConfig.getType();
            String server = databaseConfig.getHost();
            String port = databaseConfig.getPort();
            tenant.setJdbcType(type);
            tenant.setJdbcServer(server);
            tenant.setJdbcPort(port);
        }

        this.tenantService.save(tenant);
        this.tenantService.flushSession();

        // 3、创建数据库
        if (bean.isCreateDatabase()) {
            tenantBusinessSerivce.createTenantDatabase(tenant);
        }

        // 4、根据租户创建租户库内的管理员
        if (bean.isCreateDatabase() || StringUtils.isNotBlank(bean.getUuid())) {
            tenantBusinessSerivce.createTenantAdminUser(tenant);
        }
    }

    /**
     * 获取审核通过的租户，包括激活与未激活的
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#getNormalTenants()
     */
    @Override
    public List<Tenant> getNormalTenants() {
        return tenantService.getNormalTenants();
    }

    /**
     * 获取所有审核中的租户列表
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#getReviewTenants()
     */
    @Override
    public List<Tenant> getReviewTenants() {
        return tenantService.getReviewTenants();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#register(com.wellsoft.pt.core.mt.entity.Tenant)
     */
    @Override
    public boolean register(Tenant tenant) {
        String account = tenant.getAccount();
        if (StringUtils.isEmpty(account)) {
            return false;
        }
        Tenant example = new Tenant();
        example.setAccount(account);
        if (tenantService.getByAccount(account) != null) {
            throw new RuntimeException("租户[" + "]已经存在!");
        }
        // 设置未审核
        tenant.setStatus(Tenant.STATUS_TO_REVIEW);
        // 生成并设置租户ID
        tenant.setId(generateTenantId());
        tenant.setJdbcDatabaseName(IdPrefix.TENANT.getValue() + tenant.getId());
        tenant.setJdbcUsername(IdPrefix.TENANT.getValue() + tenant.getId());
        tenant.setJdbcPassword(tenant.getPassword());
        try {
            // 使用公共库作为tenant作虚拟登录
            IgnoreLoginUtils.login(Config.DEFAULT_TENANT, Config.DEFAULT_TENANT);
            tenantService.save(tenant);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            IgnoreLoginUtils.logout();
        }

        return true;
    }

    /**
     * 提交审核
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#review(com.wellsoft.pt.core.mt.entity.Tenant)
     */
    @Override
    public boolean review(Tenant tenant) {
        Tenant t = this.tenantService.getOne(tenant.getUuid());
        BeanUtils.copyProperties(tenant, t);
        // 设置审核成功，即可激活
        t.setStatus(Tenant.STATUS_ENABLED);
        // 从数据库配置中获取配置信息
        if (StringUtils.isNotBlank(tenant.getDatabaseConfigUuid())) {
            DatabaseConfig databaseConfig = databaseConfigFacadeService.getDatabaseConfigByUuid(tenant
                    .getDatabaseConfigUuid());
            String type = databaseConfig.getType();
            t.setJdbcType(type);
            t.setJdbcServer(databaseConfig.getHost());
            t.setJdbcPort(databaseConfig.getPort());
        }

        this.tenantService.save(t);

        // 创建租户库
        TenantDatabaseBuilder builder = new SQLServer2008TenantDatabaseBuilder();
        builder.build(tenant);

        if (!checkDatasourceConnectionStatus(t.getUuid())) {
            throw new RuntimeException("数据库连接失败!");
        }

        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#checkDatasourceConnectionStatus(java.lang.String)
     */
    @Override
    public boolean checkDatasourceConnectionStatus(String uuid) {
        return tenantBusinessSerivce.checkDatasourceConnectionStatus(uuid);
    }

    /**
     * 生成租户唯一的ID
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.mt.service.TenantService#generateTenantId()
     */
    @Override
    public String generateTenantId() {
        // return IdPrefix.TENANT.getValue() +
        // decimalFormat.format(this.tenantService.count());
        return tenantIdGeneratorService.generate(Tenant.class, TENANT_ID_PATTERN);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.TenantManagerService#deleteTenant(java.lang.String)
     */
    @Override
    public void deleteTenant(String uuid) {
        Tenant tenant = this.tenantService.getOne(uuid);
        Integer status = tenant.getStatus();
        tenant.setStatus(Tenant.STATUS_DELETED);
        this.tenantService.save(tenant);

        // 删除数据源
        MultiTenantDataSourceHelper.removeDataSource(tenant.getId());

        // 若租户原本可用则卸载数据库
        if (Tenant.STATUS_ENABLED.equals(status)) {
            TenantDatabaseBuilder builder = new SQLServer2008TenantDatabaseBuilder();
            builder.drop(tenant);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.TenantManagerService#deleteAllTenant(java.util.Collection)
     */
    @Override
    public void deleteAllTenant(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.deleteTenant(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.TenantManagerService#rejectTenant(java.lang.String)
     */
    @Override
    public void rejectTenant(String uuid) {
        Tenant tenant = this.tenantService.getOne(uuid);
        tenant.setStatus(Tenant.STATUS_REJECT);
        this.tenantService.save(tenant);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.superadmin.service.TenantManagerService#rejectAllTenant(java.util.Collection)
     */
    @Override
    public void rejectAllTenant(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.rejectTenant(uuid);
        }
    }

    public List<Tenant> getByUserId(String userId) {
        if (StringUtils.isBlank(userId)) {
            return null;
        }
        return this.tenantService.getByUserId(userId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.mgr.TenantManagerService#createTenantDb(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void createTenantDbTest(String tenantUuid, String dbaUser, String dbaPassword) {
        ExecuteCallback executeCallback = new ExecuteCallback() {

            @Override
            public void onExecuting(IMigrateModule module, Tenant tenant) {

            }

            @Override
            public void onExecuted(IMigrateModule module, Tenant tenant, Integer status, String message) {
                System.out.println("callback : status[" + status + "]message[" + message + "]");
            }
        };
        this.createTenantDb(tenantUuid, dbaUser, dbaPassword, executeCallback);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.mt.mgr.TenantManagerService#migrateTenantDb(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void migrateTenantDbTest(String tenantUuid, String dbaUser, String dbaPassword) {
        ExecuteCallback executeCallback = new ExecuteCallback() {

            @Override
            public void onExecuting(IMigrateModule module, Tenant tenant) {

            }

            @Override
            public void onExecuted(IMigrateModule module, Tenant tenant, Integer status, String message) {
                System.out.println("callback : status[" + status + "]message[" + message + "]");
            }
        };
        this.migrateTenantDb(tenantUuid, dbaUser, dbaPassword, executeCallback);
    }

    @Override
    public void disableActive(String uuids) {
        // TODO Auto-generated method stub
        String uuidList[] = uuids.split(";");
        for (String uuid : uuidList) {
            Tenant tenant = this.get(uuid);
            if (!Tenant.STATUS_ENABLED.equals(tenant.getStatus())) {
                throw new RuntimeException("租户【" + tenant.getName() + "】非启用租户无法停用!");
            }
            tenant.setStatus(Tenant.STATUS_DISENABLED);
            this.tenantService.save(tenant);
        }
    }

    @Override
    public void enableActive(String uuids) {
        // TODO Auto-generated method stub
        String uuidList[] = uuids.split(";");
        for (String uuid : uuidList) {
            Tenant tenant = this.get(uuid);
            if (!Tenant.STATUS_DISENABLED.equals(tenant.getStatus())) {
                throw new RuntimeException("租户【" + tenant.getName() + "】非禁用租户无法启用!");
            }
            tenant.setStatus(Tenant.STATUS_ENABLED);
            this.tenantService.save(tenant);
        }
    }

    @Override
    public List<TenantCreateProcessLog> getSetupLogByTenantCode(int setup, int batchNo, String tenantCode) {
        Tenant tenant = this.tenantService.getById("T" + tenantCode);
        if (tenant != null) {
            return this.getSetupLogByTenantUuid(setup, batchNo, tenant.getUuid());
        } else {
            return new ArrayList<TenantCreateProcessLog>();
        }
    }

    @Override
    public List<TenantCreateProcessLog> getSetupLogByTenantUuid(int setup, int batchNo, String tenantUuid) {
        String hql = "from TenantCreateProcessLog w where w.sortOrder=:sortOrder and w.batchNo = :batchNo and w.tenantUuid=:tenantUuid";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("sortOrder", setup);
        queryMap.put("batchNo", batchNo);
        queryMap.put("tenantUuid", tenantUuid);
        return this.getCommonDao().query(hql, queryMap);
    }

    @Override
    public int getCreateBatchNo(String tenantUuid) {

        String sql = " select MAX(W.BATCH_NO) from mt_tenant_create_process_log W WHERE W.TENANT_UUID='" + tenantUuid
                + "'";
        List<Object> obj = this.getCommonDao().getSession().createSQLQuery(sql).list();
        return Integer.parseInt(obj.get(0).toString()) + 1;
    }

    @Override
    public List<TenantUpgradeProcessLog> getUpdateSetupLogByTenantUuid(int setup, String upgradeBatchUuid,
                                                                       String tenantUuid) {
        String hql = "from TenantUpgradeProcessLog w where w.sortOrder=:sortOrder and w.tenantUpgradeBatch.uuid = :upgradeBatchUuid and w.tenantUuid=:tenantUuid";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("sortOrder", setup);
        queryMap.put("upgradeBatchUuid", upgradeBatchUuid);
        queryMap.put("tenantUuid", tenantUuid);
        return this.getCommonDao().query(hql, queryMap);
    }

    @Override
    public int getTenantCode() {
        String sql = " select MAX(W.code) from mt_tenant W ";
        List<Object> obj = this.getCommonDao().getSession().createSQLQuery(sql).list();
        return Integer.parseInt(obj.get(0).toString()) + 1;
    }

    @Override
    public Tenant checkTenantOrgName(String orgName, String uuid) {
        String hql = "from Tenant w where w.orgName=:orgName";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("orgName", orgName);
        if (StringUtils.isNotBlank(uuid)) {
            queryMap.put("uuid", uuid);
            hql += " and w.uuid<>:uuid";
        }

        List<Tenant> tenants = this.getCommonDao().query(hql, queryMap);
        if (tenants.size() > 0) {
            return tenants.get(0);
        } else {
            return null;
        }

    }

    @Override
    public Tenant checkTenantName(String name, String uuid) {
        String hql = "from Tenant w where w.name=:name";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("name", name);
        if (StringUtils.isNotBlank(uuid)) {
            queryMap.put("uuid", uuid);
            hql += " and w.uuid<>:uuid";
        }
        List<Tenant> tenants = this.getCommonDao().query(hql, queryMap);
        if (tenants.size() > 0) {
            return tenants.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Tenant checkJdbcUsername(String jdbcUsername) {
        String hql = "from Tenant w where w.jdbcUsername=:jdbcUsername";
        Map<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("jdbcUsername", jdbcUsername);
        List<Tenant> tenants = this.getCommonDao().query(hql, queryMap);
        if (tenants.size() > 0) {
            return tenants.get(0);
        } else {
            return null;
        }
    }

}
