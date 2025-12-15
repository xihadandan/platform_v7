/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppApplicationDao;
import com.wellsoft.pt.app.entity.AppApplication;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
public interface AppApplicationService extends JpaService<AppApplication, AppApplicationDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppApplication get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppApplication> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppApplication> findByExample(AppApplication example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppApplication entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppApplication> entities);

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
     * @param ids
     * @return
     */
    List<AppApplication> getByIds(String[] ids);

    /**
     * 根据系统单位Id统计数量
     *
     * @param systemUnitId
     * @return
     */
    long countBySystemUnitId(String systemUnitId);
}
