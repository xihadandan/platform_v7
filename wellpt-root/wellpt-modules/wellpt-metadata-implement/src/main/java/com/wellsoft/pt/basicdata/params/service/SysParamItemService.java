/*
 * @(#)2015-07-20 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.service;

import com.wellsoft.pt.basicdata.params.dao.SysParamItemDao;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
public interface SysParamItemService extends JpaService<SysParamItem, SysParamItemDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    SysParamItem get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<SysParamItem> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<SysParamItem> findByExample(SysParamItem example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(SysParamItem entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<SysParamItem> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(SysParamItem entity);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<SysParamItem> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 跟新或新增记录
     *
     * @param uuids
     */
    void saveValue(SysParamItem entity);
}
