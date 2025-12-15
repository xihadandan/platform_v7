/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.entity.DmsTagDataEntity;
import com.wellsoft.pt.dms.model.DmsTagData;

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
public interface DmsTagDataService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsTagDataEntity get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsTagDataEntity> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsTagDataEntity> findByExample(DmsTagDataEntity example);

    /**
     * @param dataUuids
     * @return
     */
    List<DmsTagData> findByDataUuids(List<String> dataUuids);

    /**
     * 根据实例查询总数
     *
     * @param example
     * @return
     */
    Long countByExample(DmsTagDataEntity example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsTagDataEntity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsTagDataEntity> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsTagDataEntity entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsTagDataEntity> entities);

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
     * 根据标签数据UUID批量删除
     *
     * @param dataUuid
     * @param tagUuid
     */
    void removeByDataUuidAndTagUuid(String dataUuid, String tagUuid);

    /**
     * 根据标签数据UUID批量删除
     *
     * @param dataUuids
     */
    void removeAllByDataUuid(List<String> dataUuids);

    /**
     * 根据标签UUID删除数据
     *
     * @param tagUuid
     */
    void removeByTagUuid(String tagUuid);

}
