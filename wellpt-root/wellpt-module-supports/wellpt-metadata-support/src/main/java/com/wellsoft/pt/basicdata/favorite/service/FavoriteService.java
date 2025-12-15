/*
 * @(#)2014年7月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.favorite.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.favorite.entity.FavoriteItem;

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
 * 2014年7月18日.1	zhongzh		2014年7月18日		Create
 * </pre>
 * @date 2014年7月18日
 */
public interface FavoriteService {
    /**
     * 根据对象UUID，将对象设置为对当前登录人收藏
     *
     * @param entityUuid 对象的唯一标识
     */
    void markFavorite(String entityUuid);

    /**
     * 将对象设置为对当前登录人收藏
     *
     * @param entity 要标识的对象
     */
    <ENTITY extends IdEntity> void favorite(ENTITY entity);

    /**
     * 根据对象UUID，将对象设置为对某个人收藏
     *
     * @param entityuuid 对象的唯一标识
     */
    void favorite(String entityuuid, String userId);

    /**
     * 将对象设置为对对某个人收藏
     *
     * @param entity 要标识的对象
     */
    <ENTITY extends IdEntity> void favorite(ENTITY entity, String userId);

    /**
     * 根据对象UUID，判断当前登录人是否有收藏
     *
     * @param entityUuid 对象的唯一标识
     */
    boolean isFavorite(String entityUuid);

    /**
     * 判断当前登录人是否有收藏
     *
     * @param entity 要标识的对象
     */
    <ENTITY extends IdEntity> boolean isFavorite(ENTITY entity);

    /**
     * 根据UUID将对象对当前用户为未收藏
     *
     * @param entityUuid 对象唯一标识
     * @param userId     用户ID
     */
    void markUnFavorite(String entityUuid);

    /**
     * 将对象对当前用户为未收藏
     *
     * @param entity 要标识的对象
     * @param userId 用户ID
     */
    <ENTITY extends IdEntity> void unFavorite(ENTITY entity);

    /**
     * 根据UUID将对象对某人设置为未收藏
     *
     * @param entityUuid 对象唯一标识
     * @param userId     用户ID
     */
    void unFavorite(String entityUuid, String userId);

    /**
     * 将对象对某人设置为未收藏
     *
     * @param entity 要标识的对象
     * @param userId 用户ID
     */
    <ENTITY extends IdEntity> void unFavorite(ENTITY entity, String userId);

    /**
     * 判断指定UUID的实体是否收藏
     *
     * @param entityUuid
     * @param userId
     * @return
     */
    FavoriteItem get(String entityUuid, String userId);

    /**
     * 根据指定UUID，获取收藏记录
     *
     * @param entityUuid
     * @return
     */
    List<FavoriteItem> getFavoriteItems(String entityUuid);

    /**
     * 根据传入的对象列表UUID返回指定用户收藏的UUID列表
     *
     * @param entities 传入的实体UUID列表
     * @param userId   用户ID
     * @return 返回标记已读的实体
     */
    <ENTITY extends IdEntity> List<String> getFavoriteList(List<String> entityUuids, String userId);

    /**
     * 根据传入的对象列表返回指定用户收藏的列表
     *
     * @param entities 传入的实体列表
     * @param userId   用户ID
     * @return 返回标记已读的实体
     */
    <ENTITY extends IdEntity> Collection<ENTITY> getFavoriteList(Collection<ENTITY> entities, String userId);

    /**
     * 根据传入的对象列表UUID返回指定用户未收藏的UUID列表
     *
     * @param entities 传入的实体UUID列表
     * @param userId   用户ID
     * @return 返回标记已读的实体
     */
    <ENTITY extends IdEntity> List<String> getUnFavoriteList(List<String> entityUuids, String userId);

    /**
     * 根据传入的列表返回指定用户未收藏的列表
     *
     * @param entities 传入的实体
     * @param userId   用户ID
     * @return 返回标记未读的实体
     */
    <ENTITY extends IdEntity> Collection<ENTITY> getUnFavoriteList(Collection<ENTITY> entities, String userId);

    /**
     * 标记对象实体列表的每一个对象针对用户是未收藏或已收藏
     *
     * @param entities  传入的实体列表
     * @param userId    用户ID
     * @param flagField 要设置的对象标志值的字段属性，boolean类型(true/false),字符串("true"/"false"),整形(1/0)
     */
    <ENTITY extends IdEntity> void favoritList(List<ENTITY> entities, String userId, String flagField);

    /**
     * 标记查询对象列表的每一个对象针对用户是未收藏或已收藏
     *
     * @param items     传入的查询对象列表
     * @param userId    用户ID
     * @param keyField  查询对象的唯一标识
     * @param flagField 要设置的查询标志值的键值	boolean类型(true/false)
     */
    void favoritList(List<QueryItem> items, String userId, String keyField, String flagField);

    /**
     * 查询当前用户的收藏记录
     *
     * @param entityClass
     * @param whereHql
     * @param values
     * @param orderBys
     * @param pagingInfo
     * @return
     */
    public <ITEM extends IdEntity> List<ITEM> query(Class<ITEM> entityClass, String whereHql,
                                                    Map<String, Object> values, Map<String, String> orderBys, PagingInfo pagingInfo);
}
