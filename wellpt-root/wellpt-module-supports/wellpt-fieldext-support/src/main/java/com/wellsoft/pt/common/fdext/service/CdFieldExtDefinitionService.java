/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.common.fdext.bean.CdFieldExtDefinitionBean;
import com.wellsoft.pt.common.fdext.entity.CdFieldExtDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
public interface CdFieldExtDefinitionService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    CdFieldExtDefinition get(String uuid);

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    CdFieldExtDefinitionBean getBean(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<CdFieldExtDefinition> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<CdFieldExtDefinition> findByExample(CdFieldExtDefinition example);

    /**
     * 根据实例查询列表
     *
     * @param hql
     * @return
     */
    List<CdFieldExtDefinition> find(String hql, Map<String, Object> values);

    /**
     * 保存
     *
     * @param entity
     */
    void save(CdFieldExtDefinition entity);

    /**
     * 保存
     *
     * @param entity
     */
    void saveBean(CdFieldExtDefinitionBean bean);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<CdFieldExtDefinition> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(CdFieldExtDefinition entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<CdFieldExtDefinition> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void removeByPk(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

}
