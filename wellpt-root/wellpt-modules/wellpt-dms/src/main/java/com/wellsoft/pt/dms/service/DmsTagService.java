/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.pt.dms.dao.DmsTagDao;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
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
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
public interface DmsTagService extends JpaService<DmsTagEntity, DmsTagDao, String> {
    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsTagEntity get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsTagEntity> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsTagEntity> findByExample(DmsTagEntity example);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsTagEntity> findByExample(DmsTagEntity example, String orderBy);

    /**
     * 根据实例查询总数
     *
     * @param example
     * @return
     */
    Long countByExample(DmsTagEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsTagEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsTagEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsTagEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsTagEntity> entities);

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
     * 根据系统获取根标签
     *
     * @param system
     * @return
     */
    List<DmsTagEntity> listRootTagBySystem(String system);
}
