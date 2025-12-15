/*
 * @(#)2018-02-02 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.entity.DmsShareInfoEntity;

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
 * 2018-02-02.1	zhulh		2018-02-02		Create
 * </pre>
 * @date 2018-02-02
 */
public interface DmsShareInfoService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsShareInfoEntity get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsShareInfoEntity> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsShareInfoEntity> findByExample(DmsShareInfoEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsShareInfoEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsShareInfoEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsShareInfoEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsShareInfoEntity> entities);

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
