/*
 * @(#)2017-04-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.service;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.entity.DmsDataVersion;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-04-26.1	zhulh		2017-04-26		Create
 * </pre>
 * @date 2017-04-26
 */
public interface DmsDataVersionService extends BaseService {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    DmsDataVersion get(String uuid);

    /**
     * 根据数据UUID获取版本
     *
     * @param dataUuid
     * @return
     */
    DmsDataVersion getByDataUuid(String dataUuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<DmsDataVersion> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<DmsDataVersion> findByExample(DmsDataVersion example);

    /**
     * 保存
     *
     * @param entity
     */
    void save(DmsDataVersion entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(Collection<DmsDataVersion> entities);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(DmsDataVersion entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<DmsDataVersion> entities);

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
     * @param dataType
     * @param title
     * @param dataDefUuid
     * @param sourceDefId
     * @param dataUuid
     * @param initVerNumber
     * @param verIncrement
     * @param remark
     */
    void saveVersion(String dataType, String title, String dataDefUuid, String sourceDefId, String dataUuid,
                     int initVerNumber, double verIncrement, String remark);

    /**
     * @param dataType
     * @param sourceDefUuid
     * @param sourceDefId
     * @param sourceDataUuid
     * @param dataDefUuid
     * @param dataDefId
     * @param dataUuid
     * @param initVerNumber
     * @param verIncrement
     */
    void saveNewVersion(String dataType, String title, String sourceDefUuid, String sourceDefId, String sourceDataUuid,
                        String dataDefUuid, String dataDefId, String dataUuid, int initVerNumber, double verIncrement, String remark);

    /**
     * @param dataType
     * @param dataDefUuid
     * @param dataDefId
     * @param dataUuid
     */
    void deleteVersion(String dataType, String dataDefUuid, String dataDefId, String dataUuid);

    /**
     * @param dataType
     * @param dataDefUuid
     * @param dataDefId
     * @param dataUuid
     * @return
     */
    List<DmsDataVersion> getAllVersions(String dataType, String dataDefUuid, String dataDefId, String dataUuid);

    List<QueryItem> listLatestVersionFormDataByFormId(String interfaceParam, PagingInfo pagingInfo);

    List<QueryItem> listLatestVersionFormDataByParams(Map<String, Object> params, PagingInfo pagingInfo);

}
