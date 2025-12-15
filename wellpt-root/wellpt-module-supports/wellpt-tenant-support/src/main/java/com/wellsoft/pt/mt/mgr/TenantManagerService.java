/*
 * @(#)2013-5-31 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.mgr;

import com.wellsoft.pt.mt.bean.TenantBean;
import com.wellsoft.pt.mt.dbmigrate.ExecuteCallback;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantCreateProcessLog;
import com.wellsoft.pt.mt.entity.TenantUpgradeProcessLog;

import java.util.Collection;
import java.util.List;

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
public interface TenantManagerService {
    /**
     * 如何描述该方法
     *
     * @param tenantUuid
     * @return
     */
    public Tenant get(String tenantUuid);

    /**
     * 获取平台可构建数据库模块
     *
     * @param onlyTenantModule
     * @return
     */
    public List<String> getBuildableModules(Boolean onlyTenantModule);

    /**
     * 如何描述该方法
     *
     * @param tenantUuid
     * @param dbaUser
     * @param dbaPassword
     * @param executeCallback
     * @return
     */
    public boolean isDbExist(String tenantUuid, String dbaUser, String dbaPassword, ExecuteCallback executeCallback);

    public void createTenantDbTest(String tenantUuid, String dbaUser, String dbaPassword);

    /**
     * 创建租户库
     *
     * @param tenantUuid
     * @param dbaUser
     * @param dbaPassword
     * @param executeCallback
     */
    public void createTenantDb(String tenantUuid, String dbaUser, String dbaPassword, ExecuteCallback executeCallback);

    public void migrateTenantDbTest(String tenantUuid, String dbaUser, String dbaPassword);

    /**
     * 初始化租户库所有可用模块
     *
     * @param tenantUuid
     */
    public void migrateTenantDb(String tenantUuid, String dbaUser, String dbaPassword, ExecuteCallback executeCallback);

    /**
     * 初始化租户库指定可用模块
     *
     * @param tenantUuid
     * @param modules
     */
    public void migrateTenantDb(String tenantUuid, List<String> modules, String dbaUser, String dbaPassword,
                                ExecuteCallback executeCallback);

    /**
     * 在指定租户执行SQL脚本
     *
     * @param tenantUuid
     * @param modules
     */
    public void executeTenantSql(String tenantUuid, String sqlScript, ExecuteCallback executeCallback);

    public void saveTenant(TenantBean bean);

    public void deleteTenant(String uuid);

    public void deleteAllTenant(Collection<String> uuids);

    public void rejectTenant(String uuid);

    public void rejectAllTenant(Collection<String> uuids);

    /**
     * 获取审核通过的租户，包括激活与未激活的
     *
     * @return
     */
    public List<Tenant> getNormalTenants();

    /**
     * 获取所有审核中的租户列表
     *
     * @return
     */
    public List<Tenant> getReviewTenants();

    /**
     * 租户注册
     *
     * @param tenant
     */
    public boolean register(Tenant tenant);

    /**
     * 提交审核
     *
     * @param tenant
     */
    public boolean review(Tenant tenant);

    /**
     * 检查租户数据库的连接性
     *
     * @param uuid
     * @return
     */
    public boolean checkDatasourceConnectionStatus(String uuid);

    /**
     * 生成租户唯一的ID
     *
     * @return
     */
    public String generateTenantId();

    /**
     * 根据用户ID获取租户列表
     *
     * @param userId
     * @return
     */
    public List<Tenant> getByUserId(String userId);

    /**
     * 租户禁用
     *
     * @param uuid
     */
    public void disableActive(String uuid);

    /**
     * 启用租户
     *
     * @param uuid
     */
    public void enableActive(String uuid);

    /**
     * 根据步骤获取日记信息
     *
     * @param setup      当前步骤
     * @param tenantCode 租户编码
     * @param batchNo    创建批次号
     * @return
     */
    public List<TenantCreateProcessLog> getSetupLogByTenantCode(int setup, int batchNo, String tenantCode);

    /**
     * 根据租户获取步骤信息
     *
     * @param setup
     * @param tenantUuid
     * @param batchNo    创建批次号
     * @return
     */
    public List<TenantCreateProcessLog> getSetupLogByTenantUuid(int setup, int batchNo, String tenantUuid);

    /**
     * 获取创建批次号
     *
     * @param tenantUuid
     * @return
     */
    public int getCreateBatchNo(String tenantUuid);

    /**
     * 获取更新日记
     *
     * @param setup
     * @param upgradeBatchUuid
     * @param tenantUuid
     * @return
     */
    public List<TenantUpgradeProcessLog> getUpdateSetupLogByTenantUuid(int setup, String upgradeBatchUuid,
                                                                       String tenantUuid);

    /**
     * 如何描述该方法
     * 获取租户CODE
     *
     * @return
     */
    public int getTenantCode();

    /**
     * 校验组织名称
     *
     * @param orgName
     * @return
     */
    public Tenant checkTenantOrgName(String orgName, String uuid);

    /**
     * 校验租户名称
     *
     * @param name
     * @return
     */
    public Tenant checkTenantName(String name, String uuid);

    /**
     * 校验数据库登录名
     *
     * @param jdbcUsername
     * @return
     */
    public Tenant checkJdbcUsername(String jdbcUsername);
}
