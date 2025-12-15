/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.weixin.dao.WeixinUserDao;
import com.wellsoft.pt.app.weixin.entity.WeixinUserEntity;
import com.wellsoft.pt.app.weixin.service.WeixinUserService;
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
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@Service
public class WeixinUserServiceImpl extends AbstractJpaServiceImpl<WeixinUserEntity, WeixinUserDao, Long> implements WeixinUserService {

    @Override
    public WeixinUserEntity getByUserIdAndCorpId(String userId, String corpId) {
        Assert.hasLength(userId, "userId cannot be null");
        Assert.notNull(corpId, "corpId cannot be null");

        String hql = "from WeixinUserEntity where userId = :userId and corpId = :corpId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("corpId", corpId);
        List<WeixinUserEntity> userEntities = this.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0) : null;
    }

    @Override
    public WeixinUserEntity getByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid) {
        Assert.hasLength(userId, "userId cannot be null");
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from WeixinUserEntity where userId = :userId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<WeixinUserEntity> userEntities = this.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0) : null;
    }

    @Override
    public List<String> listOaUserIdByUserIdAndOrgVersionUuid(List<String> userIds, Long orgVersionUuid) {
        String hql = "select oaUserId as oaUserId from WeixinUserEntity where userId in :userIds and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userIds", userIds);
        params.put("orgVersionUuid", orgVersionUuid);
        List<String> oaUserIds = this.dao.listCharSequenceByHQL(hql, params);
        return oaUserIds;
    }

    @Override
    public List<WeixinUserEntity> listLeaderUserByOrgVersionUuid(Long orgVersionUuid) {
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from WeixinUserEntity where isLeader = :isLeader and orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("isLeader", 1);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.listByHQL(hql, params);
    }

    @Override
    public List<WeixinUserEntity> listWithDirectLeaderByOrgVersionUuid(Long orgVersionUuid) {
        Assert.notNull(orgVersionUuid, "orgVersionUuid cannot be null");

        String hql = "from WeixinUserEntity where directLeaders is not null and orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        return this.listByHQL(hql, params);
    }

    @Override
    public Map<String, String> listUserIdMapByOaUserIds(List<String> oaUserIds, String corpId) {
        if (CollectionUtils.isEmpty(oaUserIds)) {
            return Collections.emptyMap();
        }

        String hql = "select userId as userId, oaUserId as oaUserId from WeixinUserEntity where oaUserId in :oaUserIds and corpId = :corpId order by createTime desc";
        Map<String, String> userIdMap = Maps.newHashMap();
        Map<String, String> oaUserIdMap = Maps.newHashMap();
        ListUtils.handleSubList(Arrays.asList(oaUserIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("oaUserIds", list);
            params.put("corpId", corpId);
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
    public String getUserIdByOaUserIdAndCorpId(String oaUserId, String corpId) {
        Assert.hasLength(oaUserId, "oaUserId cannot be null");
        Assert.notNull(corpId, "corpId cannot be null");

        String hql = "select userId as userId from WeixinUserEntity where oaUserId = :oaUserId and corpId = :corpId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("oaUserId", oaUserId);
        params.put("corpId", corpId);
        List<WeixinUserEntity> userEntities = this.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0).getUserId() : null;
    }

    @Override
    public String getOaUserIdByUserIdAndCorpId(String userId, String corpId) {
        String hql = "select oaUserId as oaUserId from WeixinUserEntity where userId = :userId and corpId = :corpId order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("corpId", corpId);
        List<WeixinUserEntity> userEntities = this.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(userEntities) ? userEntities.get(0).getOaUserId() : null;
    }

}
