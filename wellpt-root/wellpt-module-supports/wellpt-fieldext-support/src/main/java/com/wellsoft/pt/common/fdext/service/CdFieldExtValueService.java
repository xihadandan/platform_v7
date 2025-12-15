/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.common.fdext.bean.CdFieldExtensionValue;
import com.wellsoft.pt.common.fdext.entity.CdFieldExtValue;
import com.wellsoft.pt.common.fdext.support.BeanDataValue;
import com.wellsoft.pt.common.fdext.support.MapDataValue;

import java.util.Collection;
import java.util.List;

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
public interface CdFieldExtValueService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    CdFieldExtValue get(String uuid);

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    CdFieldExtensionValue getBean(String uuid);

    /**
     * 根据dataUuid,groupCode获取
     *
     * @param dataUuid
     * @param groupCode
     * @return
     */
    CdFieldExtensionValue getBean(String dataUuid, String groupCode);

    BeanDataValue getBeanValue(String dataUuid, String groupCode);

    MapDataValue getMapValue(String dataUuid, String groupCode);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<CdFieldExtValue> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<CdFieldExtValue> findByExample(CdFieldExtValue example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(CdFieldExtValue entity);

    /**
     * 保存
     *
     * @param entity
     */
    void saveBean(CdFieldExtensionValue entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<CdFieldExtValue> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(CdFieldExtValue entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<CdFieldExtValue> entities);

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
