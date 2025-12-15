/*
 * @(#)2018年9月11日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 授权实体颗粒度解析
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月11日.1	zhulh		2018年9月11日		Create
 * </pre>
 * @date 2018年9月11日
 */
@Component
public class SidGranularityResolverImpl implements SidGranularityResolver {

    // 颗粒度优先级
    private static Map<String, Integer> GRANULARITY_PRIORITY_MAP = new HashMap<String, Integer>();

    static {
        GRANULARITY_PRIORITY_MAP.put(SidGranularity.USER, 1);
        GRANULARITY_PRIORITY_MAP.put(SidGranularity.JOB, 2);
        GRANULARITY_PRIORITY_MAP.put(SidGranularity.DEPARTMENT, 3);
        GRANULARITY_PRIORITY_MAP.put(SidGranularity.SYSTEM_UNIT, 4);
        GRANULARITY_PRIORITY_MAP.put(SidGranularity.GROUP, 5);
        GRANULARITY_PRIORITY_MAP.put(SidGranularity.ACTIVITY, 6);
    }

    @Autowired
    private WorkflowOrgService workflowOrgService;


    /**
     * @param node
     * @param token
     * @param rawUsers
     * @return
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, Collection<String> rawUsers) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        String granularity = token.getFlowDelegate().getGranularity(node.getId(), token.getTaskData(), Collections.emptyList());
        return resolve(node, token, rawUsers, granularity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.SidGranularityResolver#resolve(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.core.Token, java.util.Collection, java.lang.String)
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, Collection<String> rawUsers, String granularity) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }
        return resolve(node, token, rawUsers, granularity, StringUtils.EMPTY);
    }

    @Override
    public List<FlowUserSid> resolve(Node node, Token token, Collection<String> rawUsers, String granularity, String orgId) {
        String sidGranularity = granularity;
        if (StringUtils.isBlank(sidGranularity)) {
            if (node != null && token != null) {
                sidGranularity = token.getFlowDelegate().getGranularity(node.getId(), token.getTaskData(), Collections.emptyList());
            } else {
                sidGranularity = SidGranularity.USER;
            }
        }
        String[] orgVersionIds = getOrgVersionIdsAsArray(orgId, token);

        Set<FlowUserSid> sids = new LinkedHashSet<FlowUserSid>();
        switch (sidGranularity) {
            // 用户
            case SidGranularity.USER:
                sids.addAll(resolveUserSidGranularity(node, token, rawUsers, orgVersionIds));
                break;
            case SidGranularity.JOB:
                // 职位
                sids.addAll(resolveJobSidGranularity(node, token, rawUsers, orgVersionIds));
                break;
            case SidGranularity.DEPARTMENT:
                sids.addAll(resolveDepartmentSidGranularity(node, token, rawUsers, orgVersionIds));
                // 部门
                break;
            case SidGranularity.SYSTEM_UNIT:
                // 系统单位
                sids.addAll(resolveSystemUnitSidGranularity(node, token, rawUsers, orgVersionIds));
                break;
            case SidGranularity.GROUP:
                // 系统单位
                sids.addAll(resolveGroupSidGranularity(node, token, rawUsers, orgVersionIds));
                break;
            case SidGranularity.ACTIVITY:
                // 系统单位
                sids.addAll(resolveActivitySidGranularity(node, token, rawUsers, orgVersionIds));
                break;
            default:
                // 默认用户
                sids.addAll(resolveUserSidGranularity(node, token, rawUsers, orgVersionIds));
                break;
        }
        return Arrays.asList(sids.toArray(new FlowUserSid[0]));
    }

    /**
     * @param orgId
     * @param token
     * @return
     */
    private String[] getOrgVersionIdsAsArray(String orgId, Token token) {
        return OrgVersionUtils.getFlowOrgVersionIdsAsArray(orgId, token);
    }

    /**
     * @param node
     * @param token
     * @param rawSids
     * @return
     */
    @Override
    public List<FlowUserSid> resolveWithSid(Node node, Token token, Collection<FlowUserSid> rawSids) {
        if (CollectionUtils.isEmpty(rawSids)) {
            return Collections.emptyList();
        }

        String configGranularity = token.getFlowDelegate().getGranularity(node.getId());
        return resolveWithSid(node, token, rawSids, configGranularity);
    }

    /**
     * @param node
     * @param token
     * @param rawSids
     * @param granularity
     * @return
     */
    @Override
    public List<FlowUserSid> resolveWithSid(Node node, Token token, Collection<FlowUserSid> rawSids, String granularity) {
        if (CollectionUtils.isEmpty(rawSids)) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> sids = new LinkedHashSet<FlowUserSid>();
        List<String> rawUsers = new ArrayList<String>();
        for (FlowUserSid sid : rawSids) {
            String sidGranularity = sid.getGranularity();
            if (isContains(granularity, sidGranularity)) {
                sids.add(sid);
            } else {
                rawUsers.add(sid.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(rawUsers)) {
            sids.addAll(resolve(node, token, rawUsers, granularity));
        }
        return Arrays.asList(sids.toArray(new FlowUserSid[0]));
    }

    /**
     * @param configGranularity
     * @param sidGranularity
     * @return
     */
    private boolean isContains(String configGranularity, String sidGranularity) {
        Integer configPriority = GRANULARITY_PRIORITY_MAP.get(configGranularity);
        Integer sidPriority = GRANULARITY_PRIORITY_MAP.get(sidGranularity);
        if (configPriority == null || sidPriority == null) {
            return false;
        }
        return configPriority >= sidPriority;
    }

    /**
     * @param node
     * @param token
     * @param rawUsers
     * @return
     */
    private Collection<FlowUserSid> resolveUserSidGranularity(Node node, Token token, Collection<String> rawUsers, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        // 用户、部门、群组ID、职务、职位
        // key=用户id，value=用户名字
        List<String> rawUserIds = Lists.newArrayList();
        rawUserIds.addAll(rawUsers);
        Map<String, String> users = workflowOrgService.getUsersByIds(rawUserIds, orgVersionIds);
        // Map<String, UserDto> users = orgApiFacade.getUsersByOrgIds(rawUserIds);
        Set<FlowUserSid> sidSet = new LinkedHashSet<FlowUserSid>();
        for (Entry<String, String> entry : users.entrySet()) {
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
     * @return
     */
    private Collection<FlowUserSid> resolveJobSidGranularity(Node node, Token token, Collection<String> rawUsers, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> sidSet = new LinkedHashSet<FlowUserSid>();
        List<String> userIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();
        List<String> rawJobIds = new ArrayList<String>();
        rawJobIds.addAll(rawUsers);
        for (String rawJobId : rawJobIds) {
            if (StringUtils.startsWith(rawJobId, SidGranularity.USER)
                    || StringUtils.startsWith(rawJobId, IdPrefix.BIZ_PREFIX.getValue())
                    || StringUtils.startsWith(rawJobId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                userIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.GROUP)) {
                groupIds.add(rawJobId);
            }
        }
        rawJobIds.removeAll(userIds);
        rawJobIds.removeAll(groupIds);

        // 解析群组ID
        if (CollectionUtils.isNotEmpty(groupIds)) {
            Set<String> memberIds = workflowOrgService.listMemberIdByGroupIds(groupIds);
            memberIds.forEach(memberId -> {
                if (StringUtils.startsWith(memberId, SidGranularity.USER)
                        || StringUtils.startsWith(memberId, IdPrefix.BIZ_PREFIX.getValue())
                        || StringUtils.startsWith(memberId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                    userIds.add(memberId);
                } else {
                    rawJobIds.add(memberId);
                }
            });
        }

        // 解析用户ID
        sidSet.addAll(resolveUserSidGranularity(node, token, userIds, orgVersionIds));

        // 解析职位ID
        Map<String, String> jobs = workflowOrgService.getJobsByIds(rawJobIds, orgVersionIds);
        // Map<String, String> jobs = orgApiFacade.getJobsByOrgIds(rawJobIds);
        for (Entry<String, String> entry : jobs.entrySet()) {
            String jobId = entry.getKey();
            if (StringUtils.isNotBlank(jobId)) {
                sidSet.add(new FlowUserSid(jobId, entry.getValue(), SidGranularity.JOB));
            }
        }
        return sidSet;
    }


    private Collection<FlowUserSid> resolveActivitySidGranularity(Node node, Token token, Collection<String> rawUsers, String... orgVersionIds) {
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
        sidSet.addAll(resolveGroupSidGranularity(node, token, groupIds, orgVersionIds));
        // 解析用户ID
        sidSet.addAll(resolveUserSidGranularity(node, token, userIds, orgVersionIds));
        // 解析部门ID
        Map<String, String> depts = workflowOrgService.getNamesByIds(Lists.newArrayList(rawUsers));
        for (Entry<String, String> entry : depts.entrySet()) {
            String jobId = entry.getKey();
            if (StringUtils.isNotBlank(jobId)) {
                sidSet.add(new FlowUserSid(jobId, entry.getValue()));
            }
        }
        return sidSet;
    }

    /**
     * @param node
     * @param token
     * @param rawUsers
     * @return
     */
    private Collection<FlowUserSid> resolveDepartmentSidGranularity(Node node, Token token, Collection<String> rawUsers, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> sidSet = new LinkedHashSet<FlowUserSid>();
        List<String> userIds = new ArrayList<String>();
        List<String> jobIds = new ArrayList<String>();
        List<String> unitIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();
        List<String> rawDeptIds = new ArrayList<String>();
        rawDeptIds.addAll(rawUsers);
        for (String rawJobId : rawDeptIds) {
            if (StringUtils.startsWith(rawJobId, SidGranularity.USER)
                    || StringUtils.startsWith(rawJobId, IdPrefix.BIZ_PREFIX.getValue())
                    || StringUtils.startsWith(rawJobId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                userIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.JOB)) {
                jobIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.SYSTEM_UNIT)) {
                unitIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.GROUP)) {
                groupIds.add(rawJobId);
            }
        }
        rawDeptIds.removeAll(userIds);
        rawDeptIds.removeAll(jobIds);
        rawDeptIds.removeAll(unitIds);
        rawDeptIds.removeAll(groupIds);

        // 解析群组ID
        if (CollectionUtils.isNotEmpty(groupIds)) {
            Set<String> memberIds = workflowOrgService.listMemberIdByGroupIds(groupIds);
            memberIds.forEach(memberId -> {
                if (StringUtils.startsWith(memberId, SidGranularity.USER)
                        || StringUtils.startsWith(memberId, IdPrefix.BIZ_PREFIX.getValue())
                        || StringUtils.startsWith(memberId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                    userIds.add(memberId);
                } else if (StringUtils.startsWith(memberId, SidGranularity.JOB)) {
                    jobIds.add(memberId);
                } else if (StringUtils.startsWith(memberId, SidGranularity.SYSTEM_UNIT)) {
                    unitIds.add(memberId);
                } else {
                    rawDeptIds.add(memberId);
                }
            });
        }

        // 解析用户ID
        sidSet.addAll(resolveUserSidGranularity(node, token, userIds, orgVersionIds));
        // 解析职位ID
        sidSet.addAll(resolveJobSidGranularity(node, token, jobIds, orgVersionIds));
        // 解析单位ID，部门可放入单位，单位可放入部门
        sidSet.addAll(resolveSystemUnitSidGranularity(node, token, unitIds, orgVersionIds));

        // 解析部门ID
        Map<String, String> depts = workflowOrgService.getDepartmentsByIds(rawDeptIds, orgVersionIds);
        for (Entry<String, String> entry : depts.entrySet()) {
            String jobId = entry.getKey();
            if (StringUtils.isNotBlank(jobId)) {
                sidSet.add(new FlowUserSid(jobId, entry.getValue(), SidGranularity.DEPARTMENT));
            }
        }
        return sidSet;
    }

    private Collection<FlowUserSid> resolveSystemUnitSidGranularity(Node node, Token token, Collection<String> rawUsers, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> sidSet = new LinkedHashSet<FlowUserSid>();
        List<String> userIds = new ArrayList<String>();
        List<String> jobIds = new ArrayList<String>();
        List<String> deptIds = new ArrayList<String>();
        List<String> groupIds = new ArrayList<String>();
        List<String> rawUnitIds = new ArrayList<String>();
        rawUnitIds.addAll(rawUsers);
        for (String rawJobId : rawUnitIds) {
            if (StringUtils.startsWith(rawJobId, SidGranularity.USER)
                    || StringUtils.startsWith(rawJobId, IdPrefix.BIZ_PREFIX.getValue())
                    || StringUtils.startsWith(rawJobId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                userIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.JOB)) {
                jobIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.DEPARTMENT)) {
                deptIds.add(rawJobId);
            } else if (StringUtils.startsWith(rawJobId, SidGranularity.GROUP)) {
                groupIds.add(rawJobId);
            }
        }
        rawUnitIds.removeAll(userIds);
        rawUnitIds.removeAll(jobIds);
        rawUnitIds.removeAll(deptIds);
        rawUnitIds.removeAll(groupIds);

        // 解析群组ID
        if (CollectionUtils.isNotEmpty(groupIds)) {
            Set<String> memberIds = workflowOrgService.listMemberIdByGroupIds(groupIds);
            memberIds.forEach(memberId -> {
                if (StringUtils.startsWith(memberId, SidGranularity.USER)
                        || StringUtils.startsWith(memberId, IdPrefix.BIZ_PREFIX.getValue())
                        || StringUtils.startsWith(memberId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                    userIds.add(memberId);
                } else if (StringUtils.startsWith(memberId, SidGranularity.JOB)) {
                    jobIds.add(memberId);
                } else if (StringUtils.startsWith(memberId, SidGranularity.DEPARTMENT)) {
                    deptIds.add(memberId);
                } else {
                    rawUnitIds.add(memberId);
                }
            });
        }

        // 解析用户ID
        sidSet.addAll(resolveUserSidGranularity(node, token, userIds, orgVersionIds));
        // 解析职位ID
        sidSet.addAll(resolveJobSidGranularity(node, token, jobIds, orgVersionIds));
        // 解析部门ID，部门可放入单位，单位可放入部门
        sidSet.addAll(resolveDepartmentSidGranularity(node, token, deptIds, orgVersionIds));

        // 解析单位ID
        Map<String, String> units = workflowOrgService.getUnitsByIds(rawUnitIds, orgVersionIds);
        for (Entry<String, String> entry : units.entrySet()) {
            String jobId = entry.getKey();
            if (StringUtils.isNotBlank(jobId)) {
                sidSet.add(new FlowUserSid(jobId, entry.getValue(), SidGranularity.SYSTEM_UNIT));
            }
        }
        return sidSet;
    }

    /**
     * @param node
     * @param token
     * @param rawUsers
     * @return
     */
    private Collection<FlowUserSid> resolveGroupSidGranularity(Node node, Token token, Collection<String> rawUsers, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(rawUsers)) {
            return Collections.emptyList();
        }

        Map<String, String> groups = workflowOrgService.getNamesByIds(new ArrayList<>(rawUsers));
        Set<FlowUserSid> sidSet = new LinkedHashSet<>(rawUsers.size());
        for (Entry<String, String> entry : groups.entrySet()) {
            String groupId = entry.getKey();
            if (StringUtils.isNotBlank(groupId)) {
                sidSet.add(new FlowUserSid(groupId, entry.getValue(), SidGranularity.GROUP));
            }
        }
        return sidSet;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.SidGranularityResolver#resolveAsUserIds(java.util.Collection)
     */
    @Override
    public Collection<String> resolveAsUserIds(Collection<FlowUserSid> sids) {
        List<String> rawOrgIds = new ArrayList<String>();
        for (FlowUserSid sid : sids) {
            rawOrgIds.add(sid.getId());
        }

        Collection<FlowUserSid> userSids = resolveUserSidGranularity(null, null, rawOrgIds);
        Set<String> returnSids = new LinkedHashSet<String>();
        for (FlowUserSid userSid : userSids) {
            returnSids.add(userSid.getId());
        }
        return returnSids;
    }

}
