/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.dingtalk.dao.DingtalkUserDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkUserEntity;
import com.wellsoft.pt.app.dingtalk.service.DingtalkUserService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
@Service
public class DingtalkUserServiceImpl extends AbstractJpaServiceImpl<DingtalkUserEntity, DingtalkUserDao, Long> implements
        DingtalkUserService {

    @Override
    public DingtalkUserEntity getByUnionIdAndOrgVersionUuid(String unionId, Long orgVersionUuid) {
        Assert.notNull(unionId, "unionId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from DingtalkUserEntity where unionId = :unionId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("unionId", unionId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<DingtalkUserEntity> userEntities = this.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0) : null;
    }

    @Override
    public List<DingtalkUserEntity> listLeaderUserByOrgVersionUuid(Long orgVersionUuid) {
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from DingtalkUserEntity where leader = :leader and orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("leader", true);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<String> listOaUserIdByUserIdAndOrgVersionUuid(List<String> userIds, Long orgVersionUuid) {
        String hql = "select oaUserId as oaUserId from DingtalkUserEntity where userId in :userIds and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userIds", userIds);
        params.put("orgVersionUuid", orgVersionUuid);
        List<String> oaUserIds = this.dao.listCharSequenceByHQL(hql, params);
        return oaUserIds;
    }

    @Override
    public DingtalkUserEntity getByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid) {
        Assert.notNull(userId, "userId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from DingtalkUserEntity where userId = :userId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<DingtalkUserEntity> userEntities = this.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0) : null;
    }

    @Override
    public Map<String, String> listUserIdMapByOaUserIds(List<String> oaUserIds, String appId) {
        if (CollectionUtils.isEmpty(oaUserIds)) {
            return Collections.emptyMap();
        }

        String hql = "select userId as userId, oaUserId as oaUserId, unionId as unionId from DingtalkUserEntity where oaUserId in :oaUserIds and appId = :appId order by createTime desc";
        Map<String, String> userIdMap = Maps.newHashMap();
        Map<String, String> oaUserIdMap = Maps.newHashMap();
        ListUtils.handleSubList(Arrays.asList(oaUserIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("oaUserIds", list);
            params.put("appId", appId);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, Integer.MAX_VALUE));
            queryItems.forEach(item -> {
                String userId = item.getString("userId");
                String oaUserId = item.getString("oaUserId");
                if (oaUserIdMap.containsKey(oaUserId)) {
                    return;
                }
                userIdMap.put(userId, oaUserId);
                oaUserIdMap.put(oaUserId, userId);
            });
        });
        return userIdMap;
    }

    @Override
    public Map<String, String> listUnionIdMapByOaUserIds(List<String> oaUserIds, String appId) {
        if (CollectionUtils.isEmpty(oaUserIds)) {
            return Collections.emptyMap();
        }

        String hql = "select userId as userId, oaUserId as oaUserId, unionId as unionId from DingtalkUserEntity where oaUserId in :oaUserIds and appId = :appId order by createTime desc";
        Map<String, String> unionIdMap = Maps.newHashMap();
        Map<String, String> oaUserIdMap = Maps.newHashMap();
        ListUtils.handleSubList(Arrays.asList(oaUserIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("oaUserIds", list);
            params.put("appId", appId);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, Integer.MAX_VALUE));
            queryItems.forEach(item -> {
                String unionId = item.getString("unionId");
                String oaUserId = item.getString("oaUserId");
                if (oaUserIdMap.containsKey(oaUserId)) {
                    return;
                }
                unionIdMap.put(unionId, oaUserId);
                oaUserIdMap.put(oaUserId, unionId);
            });
        });
        return unionIdMap;
    }

    @Override
    public String getUserIdByOaUserId(String oaUserId, String appId) {
        Assert.notNull(oaUserId, "oaUserId cannot be null");
        Assert.notNull(appId, "appId cannot be null");

        String hql = "select userId as userId, oaUserId as oaUserId, unionId as unionId from DingtalkUserEntity where oaUserId = :oaUserId and appId = :appId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("oaUserId", oaUserId);
        params.put("appId", appId);
        List<DingtalkUserEntity> userEntities = this.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0).getUserId() : null;
    }

    @Override
    public String getUnionIdByOaUserId(String oaUserId, String appId) {
        Assert.notNull(oaUserId, "oaUserId cannot be null");
        Assert.notNull(appId, "appId cannot be null");

        String hql = "select userId as userId, oaUserId as oaUserId, unionId as unionId from DingtalkUserEntity where oaUserId = :oaUserId and appId = :appId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("oaUserId", oaUserId);
        params.put("appId", appId);
        List<DingtalkUserEntity> userEntities = this.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0).getUnionId() : null;
    }

    @Override
    public String getOaUserIdByUnionIdAndAppId(String unionId, String appId) {
        String hql = "select userId as userId, oaUserId as oaUserId, unionId as unionId from DingtalkUserEntity where unionId = :unionId and appId = :appId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("unionId", unionId);
        params.put("appId", appId);
        List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getString("oaUserId") : null;
    }

}
