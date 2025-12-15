/*
 * @(#)12/13/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.bpm.engine.access.BizOrgSidGranularityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/13/24.1	    zhulh		12/13/24		    Create
 * </pre>
 * @date 12/13/24
 */
@Component
public class BizOrgSidGranularityResolverImpl implements BizOrgSidGranularityResolver {

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * @param node
     * @param token
     * @param rawUsers
     * @param granularity
     * @param bizOrgId
     * @return
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> rawUsers, String granularity, String bizOrgId) {
//        String sidGranularity = granularity;
//        if (StringUtils.isBlank(sidGranularity)) {
//            if (node != null && token != null) {
//                sidGranularity = token.getFlowDelegate().getGranularity(node.getId(), token.getTaskData());
//            } else {
//                sidGranularity = SidGranularity.USER;
//            }
//        }

        Set<FlowUserSid> sids = new LinkedHashSet<>();
        switch (granularity) {
            // 用户
            case SidGranularity.USER:
                sids.addAll(resolveUserSidGranularity(node, token, rawUsers, bizOrgId));
                break;
            default:
                // 默认动态
                sids.addAll(resolveActivitySidGranularity(node, token, rawUsers, bizOrgId));
                break;
        }
        return Arrays.asList(sids.toArray(new FlowUserSid[0]));
    }

    /**
     * @param node
     * @param token
     * @param rawUsers
     * @param bizOrgId
     * @return
     */
    private Collection<FlowUserSid> resolveUserSidGranularity(Node node, Token token, List<String> rawUsers, String bizOrgId) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        // 用户、部门、群组ID、职务、职位
        // key=用户id，value=用户名字
        List<String> rawUserIds = Lists.newArrayList();
        rawUserIds.addAll(rawUsers);
        Map<String, String> users = workflowOrgService.getBizOrgUsersByIds(rawUserIds, bizOrgId);
        Set<FlowUserSid> sidSet = new LinkedHashSet<FlowUserSid>();
        for (Map.Entry<String, String> entry : users.entrySet()) {
            String userId = entry.getKey();
            if (StringUtils.isNotBlank(userId)) {
                sidSet.add(new FlowUserSid(userId, entry.getValue(), SidGranularity.USER));
            }
        }
        return sidSet;
    }

    /**
     * @param node
     * @param token
     * @param rawUsers
     * @param bizOrgId
     * @return
     */
    private Collection<FlowUserSid> resolveGroupSidGranularity(Node node, Token token, List<String> rawUsers, String bizOrgId) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        Map<String, String> groups = workflowOrgService.getNamesByIds(new ArrayList<>(rawUsers));
        Set<FlowUserSid> sidSet = new LinkedHashSet<>(rawUsers.size());
        for (Map.Entry<String, String> entry : groups.entrySet()) {
            String groupId = entry.getKey();
            if (StringUtils.isNotBlank(groupId)) {
                sidSet.add(new FlowUserSid(groupId, entry.getValue(), SidGranularity.GROUP));
            }
        }
        return sidSet;
    }

    private Collection<FlowUserSid> resolveActivitySidGranularity(Node node, Token token, Collection<String> rawUsers, String bizOrgId) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> sidSet = new LinkedHashSet<FlowUserSid>();
        List<String> userIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();
        List<String> rawDeptIds = new ArrayList<String>();
        rawDeptIds.addAll(rawUsers);
        for (String rawJobId : rawDeptIds) {
            if (StringUtils.startsWith(rawJobId, SidGranularity.USER)) {
                userIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.GROUP)) {
                groupIds.add(rawJobId);
            }
        }
        rawDeptIds.removeAll(userIds);
        rawDeptIds.removeAll(groupIds);
        // 解析群组ID
        sidSet.addAll(resolveGroupSidGranularity(node, token, groupIds, bizOrgId));
        // 解析用户ID
        sidSet.addAll(resolveUserSidGranularity(node, token, userIds, bizOrgId));
        // 解析部门ID
        Map<String, String> depts = workflowOrgService.getNamesByIds(Lists.newArrayList(rawUsers));
        for (Map.Entry<String, String> entry : depts.entrySet()) {
            String jobId = entry.getKey();
            if (StringUtils.isNotBlank(jobId)) {
                sidSet.add(new FlowUserSid(jobId, entry.getValue()));
            }
        }
        return sidSet;
    }
}
