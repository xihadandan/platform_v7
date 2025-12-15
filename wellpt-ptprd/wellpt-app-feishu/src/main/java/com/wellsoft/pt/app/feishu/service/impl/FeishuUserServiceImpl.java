package com.wellsoft.pt.app.feishu.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.feishu.dao.FeishuUserDao;
import com.wellsoft.pt.app.feishu.entity.FeishuUserEntity;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class FeishuUserServiceImpl extends AbstractJpaServiceImpl<FeishuUserEntity, FeishuUserDao, Long> implements FeishuUserService {

    @Override
    public List<FeishuUserEntity> listByOpenIdsAndOrgVersionUuid(Set<String> openIds, Long orgVersionUuid) {
        String hql = "from FeishuUserEntity where openId in :openIds and orgVersionUuid = :orgVersionUuid";
        List<FeishuUserEntity> userEntities = Lists.newArrayList();
        ListUtils.handleSubList(Arrays.asList(openIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("openIds", list);
            params.put("orgVersionUuid", orgVersionUuid);
            userEntities.addAll(this.dao.listByHQL(hql, params));
        });
        return userEntities;
    }

    @Override
    public Map<String, String> listOpenIdMapByOaUserId(Set<String> oaUserIds, String appId) {
        if (CollectionUtils.isEmpty(oaUserIds)) {
            return Collections.emptyMap();
        }

        String hql = "select openId as openId, oaUserId as oaUserId from FeishuUserEntity where oaUserId in :oaUserIds and appId = :appId and status in('0', '1') order by createTime desc";
        // Set<String> openIds = Sets.newLinkedHashSet();
        Map<String, String> openIdMap = Maps.newHashMap();
        Map<String, String> oaUserIdMap = Maps.newHashMap();
        ListUtils.handleSubList(Arrays.asList(oaUserIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("oaUserIds", list);
            params.put("appId", appId);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, Integer.MAX_VALUE));
            queryItems.forEach(item -> {
                String openId = item.getString("openId");
                String oaUserId = item.getString("oaUserId");
                if (oaUserIdMap.containsKey(oaUserId)) {
                    return;
                }
                openIdMap.put(openId, oaUserId);
                oaUserIdMap.put(oaUserId, openId);
            });
            // openIds.addAll(this.dao.listCharSequenceByHQL(hql, params));
        });
        return openIdMap;
    }

    @Override
    public Map<String, String> listOaUserIdMapByOpenUserId(Set<String> openIds, String appId) {
        if (CollectionUtils.isEmpty(openIds)) {
            return Collections.emptyMap();
        }

        String hql = "select openId as openId, oaUserId as oaUserId from FeishuUserEntity where openId in :openIds and appId = :appId and status in('0', '1') order by createTime desc";
        // Set<String> openIds = Sets.newLinkedHashSet();
        Map<String, String> openIdMap = Maps.newHashMap();
        Map<String, String> oaUserIdMap = Maps.newHashMap();
        ListUtils.handleSubList(Arrays.asList(openIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("openIds", list);
            params.put("appId", appId);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, Integer.MAX_VALUE));
            queryItems.forEach(item -> {
                String openId = item.getString("openId");
                String oaUserId = item.getString("oaUserId");
                if (oaUserIdMap.containsKey(oaUserId)) {
                    return;
                }
                openIdMap.put(openId, oaUserId);
                oaUserIdMap.put(oaUserId, openId);
            });
            // openIds.addAll(this.dao.listCharSequenceByHQL(hql, params));
        });
        return oaUserIdMap;
    }

    @Override
    public Map<String, String> getUserNamesByOpenIds(Set<String> openIds, String appId) {
        if (CollectionUtils.isEmpty(openIds) || StringUtils.isBlank(appId)) {
            return Collections.emptyMap();
        }

        String hql = "select openId as openId, name as userName from FeishuUserEntity where openId in :openIds and appId = :appId order by createTime desc";
        Map<String, String> userNameMap = Maps.newHashMap();
        openIds.forEach(openId -> userNameMap.put(openId, openId));
        ListUtils.handleSubList(Arrays.asList(openIds.toArray(new String[0])), 1000, list -> {
            Map<String, Object> params = Maps.newHashMap();
            params.put("openIds", list);
            params.put("appId", appId);
            List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, Integer.MAX_VALUE));
            queryItems.forEach(item -> {
                userNameMap.put(item.getString("openId"), item.getString("userName"));
            });
        });
        return userNameMap;
    }

    @Override
    public String getOaUserIdByOpenIdAndConfigUuid(String openId, Long configUuid) {
        String hql = "select oaUserId as oaUserId from FeishuUserEntity where openId = :openId and configUuid = :configUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openId", openId);
        params.put("configUuid", configUuid);
        List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getString("oaUserId") : null;
    }

    @Override
    public String getOaUserIdByOpenIdAndOrgVersionUuid(String openId, Long orgVersionUuid) {
        String hql = "select oaUserId as oaUserId from FeishuUserEntity where openId = :openId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openId", openId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<QueryItem> queryItems = this.dao.listQueryItemByHQL(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(queryItems) ? queryItems.get(0).getString("oaUserId") : null;
    }

    @Override
    public List<String> listOaUserIdByOpenIdAndOrgVersionUuid(List<String> openIds, Long orgVersionUuid) {
        String hql = "select oaUserId as oaUserId from FeishuUserEntity where openId in :openIds and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openIds", openIds);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    @Override
    public FeishuUserEntity getByOpenIdAndOrgVersionUuid(String openId, Long orgVersionUuid) {
        String hql = "from FeishuUserEntity where openId = :openId and orgVersionUuid = :orgVersionUuid order by createTime desc";
        Map<String, Object> params = Maps.newHashMap();
        params.put("openId", openId);
        params.put("orgVersionUuid", orgVersionUuid);
        List<FeishuUserEntity> entities = this.dao.listByHQLAndPage(hql, params, new PagingInfo(1, 1));
        return CollectionUtils.isNotEmpty(entities) ? entities.get(0) : null;
    }

    @Override
    @Transactional
    public void updateStatusByOpenIdAndOrgVersionUuid(String status, String openId, Long orgVersionUuid) {
        String hql = "update FeishuUserEntity set status = :status where openId = :openId and orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("status", status);
        params.put("openId", openId);
        params.put("orgVersionUuid", orgVersionUuid);
        this.dao.updateByHQL(hql, params);
    }

}
