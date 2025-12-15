/*
 * @(#)2013-3-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.marker.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.common.marker.entity.ReadMarker;

import java.util.Collection;
import java.util.List;

/**
 * Description: 未读，已读标记服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1.1	zhulh		2013-3-1		Create
 * </pre>
 * @date 2013-3-1
 */
public interface ReadMarkerService {

    /**
     * 根据对象UUID，将对象设置为对所有人未读
     *
     * @param uuid 对象的唯一标识
     */
    void markNew(String uuid);

    /**
     * 将对象设置为对所有人未读
     *
     * @param entity 要标识的对象
     */
    <ENTITY extends IdEntity> void markNew(ENTITY entity);

    /**
     * 根据对象UUID，将对象设置为对所有人未读
     *
     * @param uuid 对象的唯一标识
     */
    void markNew(String uuid, String userId);

    void markNewList(List<String> uuidList, String userId);

    /**
     * 将对象设置为对所有人未读
     *
     * @param entity 要标识的对象
     */
    <ENTITY extends IdEntity> void markNew(ENTITY entity, String userId);

    /**
     * 根据UUID将对象对某人设置为已读
     *
     * @param entityUuid 对象唯一标识
     * @param userId     用户ID
     */
    void markRead(String entityUuid, String userId);

    void markReadList(List<String> entityUuidList, String userId);

    /**
     * 将对象对某人设置为已读
     *
     * @param entity 要标识的对象
     * @param userId 用户ID
     */
    <ENTITY extends IdEntity> void markRead(ENTITY entity, String userId);

    /**
     * 判断指定UUID的实体是否已阅
     *
     * @param taskInstUuid
     * @param copyUserId
     * @return
     */
    ReadMarker get(String uuid, String userId);

    /**
     * 根据指定UUID，获取阅读记录
     *
     * @param uuid
     * @return
     */
    List<ReadMarker> getReadMarkers(String uuid);

    /**
     * 根据传入的对象列表UUID返回指定用户已读的UUID列表
     *
     * @param entities 传入的实体UUID列表
     * @param userId   用户ID
     * @return 返回标记已读的实体
     */
    <ENTITY extends IdEntity> List<String> getReadList(List<String> uuids, String userId);

    /**
     * 根据传入的对象列表返回指定用户已读的列表
     *
     * @param entities 传入的实体列表
     * @param userId   用户ID
     * @return 返回标记已读的实体
     */
    <ENTITY extends IdEntity> Collection<ENTITY> getReadList(Collection<ENTITY> entities, String userId);

    /**
     * 根据传入的对象列表UUID返回指定用户未读的UUID列表
     *
     * @param entities 传入的实体UUID列表
     * @param userId   用户ID
     * @return 返回标记已读的实体
     */
    <ENTITY extends IdEntity> List<String> getUnReadList(List<String> uuids, String userId);

    /**
     * 根据传入的列表返回指定用户未读的列表
     *
     * @param entities 传入的实体
     * @param userId   用户ID
     * @return 返回标记未读的实体
     */
    <ENTITY extends IdEntity> Collection<ENTITY> getUnReadList(Collection<ENTITY> entities, String userId);

    /**
     * 标记对象实体列表的每一个对象针对用户是未读或已读
     *
     * @param entities  传入的实体列表
     * @param userId    用户ID
     * @param flagField 要设置的对象标志值的字段属性，boolean类型(true/false),字符串("true"/"false"),整形(1/0)
     */
    <ENTITY extends IdEntity> void markList(List<ENTITY> entities, String userId, String flagField);

    /**
     * 标记查询对象列表的每一个对象针对用户是未读或已读
     *
     * @param items     传入的查询对象列表
     * @param userId    用户ID
     * @param keyField  查询对象的唯一标识
     * @param flagField 要设置的查询标志值的键值	boolean类型(true/false)
     */
    void markList(List<QueryItem> items, String userId, String keyField, String flagField);

    /**
     * 根据用户ID和流程实例 变更数据状态
     *
     * @param userId        用户ID
     * @param flowInstUuids 流程实例UUID
     * @param dataStatuses  数据状态(已阅变更未阅，未阅变更已阅)已阅：true;未阅：false
     * @return java.lang.Boolean
     **/
    public Boolean readStatusUpdate(String userId, String[] flowInstUuids, String[] dataStatuses);
}
