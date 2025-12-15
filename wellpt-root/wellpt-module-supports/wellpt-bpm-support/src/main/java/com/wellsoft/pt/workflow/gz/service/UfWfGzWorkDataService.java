/*
 * @(#)2015-08-12 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.workflow.gz.entity.UfWfGzWorkData;

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
 * 2015-08-12.1	zhulh		2015-08-12		Create
 * </pre>
 * @date 2015-08-12
 */
public interface UfWfGzWorkDataService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    UfWfGzWorkData get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<UfWfGzWorkData> getAll();

    /**
     * 获取所有UUID数据
     *
     * @param uuids
     * @return
     */
    List<UfWfGzWorkData> getAllByPk(Collection<String> uuids);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<UfWfGzWorkData> findByExample(UfWfGzWorkData example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(UfWfGzWorkData entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<UfWfGzWorkData> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(UfWfGzWorkData entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<UfWfGzWorkData> entities);

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
