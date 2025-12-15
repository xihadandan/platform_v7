/*
 * @(#)2017-12-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.entity.DmsRoleActionEntity;

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
 * 2017-12-29.1	zhulh		2017-12-29		Create
 * </pre>
 * @date 2017-12-29
 */
public interface DmsRoleActionService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsRoleActionEntity get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsRoleActionEntity> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsRoleActionEntity> findByExample(DmsRoleActionEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsRoleActionEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsRoleActionEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsRoleActionEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsRoleActionEntity> entities);

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
     * @param roleUuid
     */
    void removeByRoleUuid(String roleUuid);

    /**
     * 如何描述该方法
     *
     * @param roleUuids
     * @return
     */
    List<String> getActionByRoleUuids(List<String> roleUuids);

}
