/*
 * @(#)2015-08-14 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.mt.entity.TenantGzUser;

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
 * 2015-08-14.1	zhulh		2015-08-14		Create
 * </pre>
 * @date 2015-08-14
 */
public interface TenantGzUserService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    TenantGzUser get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<TenantGzUser> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<TenantGzUser> findByExample(TenantGzUser example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(TenantGzUser entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<TenantGzUser> entities);

    /**
     * 获取挂职租户信息
     *
     * @param userId
     * @param tenantId
     * @return
     */
    List<TenantGzUser> getTenantGzUser(String userId, String tenantId);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param tenantId
     */
    void deleteById(String userId, String tenantId);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(TenantGzUser entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<TenantGzUser> entities);

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

}
