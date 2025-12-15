/*
 * @(#)Apr 13, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.dms.entity.DmsTagEntity;
import com.wellsoft.pt.dms.model.DmsTagData;

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
 * Apr 13, 2018.1	zhulh		Apr 13, 2018		Create
 * </pre>
 * @date Apr 13, 2018
 */
public interface DmsTagFacadeService extends BaseService {

    /**
     * 根据名称、颜色创建标签，返回标签UUID
     *
     * @param name
     * @param color
     * @return
     */
    String createTag(String name, String color);

    /**
     * 创建标签并标记数据
     *
     * @param name
     * @param color
     * @param dataUuids
     * @return
     */
    String createTagAndTagData(String name, String color, List<String> dataUuids);

    /**
     * 根据标签UUID更新标签颜色
     *
     * @param uuid
     * @param name
     * @param color
     * @return
     */
    void updateTagColor(String uuid, String color);

    /**
     * 重命名标签
     *
     * @param uuid
     * @param name
     */
    void renameTag(String uuid, String name);

    /**
     * 删除标签
     *
     * @param tagUuids
     */
    void deleteAll(List<String> tagUuids);

    /**
     * 标记数据
     *
     * @param dataUuids
     * @param tagUuid
     */
    void tagData(List<String> dataUuids, String tagUuid);

    /**
     * 取消数据标记
     *
     * @param dataUuids
     * @param tagUuid
     */
    void untagData(List<String> dataUuids, String tagUuid);

    /**
     * 取消数据所有标记
     *
     * @param dataUuids
     * @param tagUuid
     */
    void untagDataAll(List<String> dataUuids);

    /**
     * 根据用户ID查询用户的标签
     *
     * @param userId
     * @return
     */
    List<DmsTagEntity> queryUserTags(String userId);

    /**
     * 查询当前用户的标签
     *
     * @param userId
     * @return
     */
    List<DmsTagEntity> queryCurrentUserTags();

    /**
     * 查询数据的标签
     *
     * @param dataUuids
     * @return
     */
    Map<String, List<DmsTagData>> queryDataTags(List<String> dataUuids);

}
