/*
 * @(#)2017-02-13 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.DmsDataManagementDefinitionDao;
import com.wellsoft.pt.dms.entity.DmsDataManagementDefinition;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2017-02-13.1	zhulh		2017-02-13		Create
 * </pre>
 * @date 2017-02-13
 */
public interface DmsDataManagementDefinitionService extends
        JpaService<DmsDataManagementDefinition, DmsDataManagementDefinitionDao, String> {
    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsDataManagementDefinition get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsDataManagementDefinition> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsDataManagementDefinition> findByExample(DmsDataManagementDefinition example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsDataManagementDefinition entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsDataManagementDefinition> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsDataManagementDefinition entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsDataManagementDefinition> entities);

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
