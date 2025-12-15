/*
 * @(#)2014年7月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.favorite.service.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.favorite.dao.FavoriteDao;
import com.wellsoft.pt.basicdata.favorite.entity.FavoriteItem;
import com.wellsoft.pt.basicdata.favorite.service.FavoriteService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
@org.springframework.stereotype.Service
@org.springframework.transaction.annotation.Transactional
public class FavoriteServiceImpl extends BaseServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteDao favoriteDao;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#markFavorite(java.lang.String)
     */
    @Override
    public void markFavorite(String entityUuid) {
        favorite(entityUuid, SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#favorite(com.wellsoft.context.jdbc.entity.IdEntity)
     */
    @Override
    public <ENTITY extends IdEntity> void favorite(ENTITY entity) {
        favorite(entity, SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#favorite(java.lang.String, java.lang.String)
     */
    @Override
    public void favorite(String entityUuid, String userId) {
        favoriteDao.save(get(entityUuid, userId));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#favorite(com.wellsoft.context.jdbc.entity.IdEntity, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void favorite(ENTITY entity, String userId) {
        favoriteDao.save(get(entity.getUuid(), userId));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#markUnFavorite(java.lang.String)
     */
    @Override
    public void markUnFavorite(String entityUuid) {
        unFavorite(entityUuid, SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#unFavorite(com.wellsoft.context.jdbc.entity.IdEntity)
     */
    @Override
    public <ENTITY extends IdEntity> void unFavorite(ENTITY entity) {
        unFavorite(entity, SpringSecurityUtils.getCurrentUserId());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#unFavorite(java.lang.String, java.lang.String)
     */
    @Override
    public void unFavorite(String entityUuid, String userId) {
        favoriteDao.delete(entityUuid, userId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#unFavorite(com.wellsoft.context.jdbc.entity.IdEntity, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void unFavorite(ENTITY entity, String userId) {
        favoriteDao.delete(entity.getUuid(), userId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#get(java.lang.String, java.lang.String)
     */
    @Override
    public FavoriteItem get(String entityUuid, String userId) {
        FavoriteItem example = new FavoriteItem();
        example.setUserId(userId);
        example.setEntityUuid(entityUuid);
        List<FavoriteItem> examples = favoriteDao.findByExample(example);
        if (examples != null && !examples.isEmpty()) {
            example = examples.get(0);
        }
        return example;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#getFavoriteItems(java.lang.String)
     */
    @Override
    public List<FavoriteItem> getFavoriteItems(String entityUuid) {
        FavoriteItem example = new FavoriteItem();
        example.setEntityUuid(entityUuid);
        List<FavoriteItem> examples = favoriteDao.findByExample(example);
        return examples;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#getFavoriteList(java.util.List, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> List<String> getFavoriteList(List<String> uuids, String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#getFavoriteList(java.util.Collection, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> Collection<ENTITY> getFavoriteList(Collection<ENTITY> entities, String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#getUnFavoriteList(java.util.List, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> List<String> getUnFavoriteList(List<String> uuids, String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#getUnFavoriteList(java.util.Collection, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> Collection<ENTITY> getUnFavoriteList(Collection<ENTITY> entities, String userId) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#favoritList(java.util.List, java.lang.String, java.lang.String)
     */
    @Override
    public <ENTITY extends IdEntity> void favoritList(List<ENTITY> entities, String userId, String flagField) {
        // TODO Auto-generated method stub
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#favoritList(java.util.List, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public void favoritList(List<QueryItem> items, String userId, String keyField, String flagField) {
        // TODO Auto-generated method stub
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#isFavorite(java.lang.String)
     */
    @Override
    public boolean isFavorite(String entityUuid) {
        return isFavorite(entityUuid, SpringSecurityUtils.getCurrentUserId());
    }

    public boolean isFavorite(String entityUuid, String userId) {
        return favoriteDao.isExist(entityUuid, userId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.favorite.service.FavoriteService#isFavorite(com.wellsoft.context.jdbc.entity.IdEntity)
     */
    @Override
    public <ENTITY extends IdEntity> boolean isFavorite(ENTITY entity) {
        return isFavorite(entity, SpringSecurityUtils.getCurrentUserId());
    }

    public <ENTITY extends IdEntity> boolean isFavorite(ENTITY entity, String userId) {
        return favoriteDao.isExist(entity.getUuid(), userId);
    }

    public <ITEM extends IdEntity> List<ITEM> query(Class<ITEM> entityClass, String whereHql,
                                                    Map<String, Object> values, Map<String, String> orderBys, PagingInfo pagingInfo) {
        return favoriteDao.query(entityClass, whereHql, values, orderBys, pagingInfo);
    }
}
