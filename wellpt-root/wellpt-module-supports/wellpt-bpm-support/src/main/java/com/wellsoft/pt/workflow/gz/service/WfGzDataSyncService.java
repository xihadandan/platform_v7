/*
 * @(#)2015-07-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.gz.entity.WfGzDataSync;

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
 * 2015-07-16.1	zhulh		2015-07-16		Create
 * </pre>
 * @date 2015-07-16
 */
public interface WfGzDataSyncService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    WfGzDataSync get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<WfGzDataSync> getAll();

    /**
     * 获取所有数据
     *
     * @return
     */
    List<WfGzDataSync> getAll(String orderBy);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<WfGzDataSync> findByExample(WfGzDataSync example);

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<WfGzDataSync> findByExampleByModifyTimeAsc(WfGzDataSync example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(WfGzDataSync entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<WfGzDataSync> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(WfGzDataSync entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<WfGzDataSync> entities);

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

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param taskInstUuid
     * @param currentTenantId
     * @param tenantId
     */
    void copyTo(String userId, String taskInstUuid, String sourceTenantId, String targetTenantId);

}
