/*
 * @(#)2015年8月28日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.dispatcher.DefaultDispatcherFlowResolver.NewFlowTaskUsers;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.expression.Expression;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月28日.1	zhulh		2015年8月28日		Create
 * </pre>
 * @date 2015年8月28日
 */
public class NewFlowMemberOfConditionExpression implements Expression {

    private String type;
    private String member;

    /**
     * @param expression
     * @param paramList
     */
    public NewFlowMemberOfConditionExpression(String type, String member) {
        this.type = type;
        this.member = member;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        // 分发的办理人在指定人员范围内
        if (StringUtils.equals("1", type)) {
            return evaluateMemberOfUser(param);
        }
        // 分发的组织节点在指定业务通讯单位内
        if (StringUtils.equals("0", type)) {
            return evaluateMemberOfBusinessUnit(param);
        }
        return false;
    }

    /**
     * @param param
     * @return
     */
    private boolean evaluateMemberOfUser(Param param) {
        Context context = param.getContext();
        Token token = (Token) context.getValue("token");
        Node node = (Node) context.getValue("node");
        String createInstanceWay = (String) context.getValue("createInstanceWay");
        NewFlowTaskUsers newFlowTaskUsers = (NewFlowTaskUsers) context.getValue("newFlowTaskUsers");
        List<String> taskUsers = (List<String>) context.getValue("taskUsers");
        // 人员范围
        List<FlowUserSid> limitUserSids = getLimitUserSids(node, token);
        if (CollectionUtils.isEmpty(limitUserSids)) {
            return false;
        }

        // 要比较人员
        List<FlowUserSid> taskUserSids = IdentityResolverStrategy.resolveFlowUserSids(node, token, taskUsers);
        if (CollectionUtils.isEmpty(taskUserSids)) {
            return false;
        }

        // 单一实例
        if (FlowConstant.CREATE_INSTANCE_WAY.SINGLETON.equals(createInstanceWay)) {
            for (FlowUserSid flowUserSid : taskUserSids) {
                if (!limitUserSids.contains(flowUserSid)) {
                    return false;
                }
            }
            return true;
        } else {
            // 按办理人生成
            for (FlowUserSid flowUserSid : taskUserSids) {
                if (limitUserSids.contains(flowUserSid)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @return
     */
    private List<FlowUserSid> getLimitUserSids(Node node, Token token) {
        List<FlowUserSid> limitUserIds = Lists.newArrayList();
        if (StringUtils.isBlank(member)) {
            return limitUserIds;
        }
        // 获取用户范围
        String[] rawUserIds = StringUtils.split(member.replaceAll(",", " , "), Separator.COMMA.getValue());
        for (int i = 0; i < rawUserIds.length; i++) {
            rawUserIds[i] = StringUtils.trim(rawUserIds[i]);
        }
        List<String> rawLimitUserIds = Lists.newArrayList();
        if (rawUserIds.length == 5) {
            for (int i = 1; i < rawUserIds.length; i++) {
                rawLimitUserIds.add(rawUserIds[i]);
            }
            limitUserIds.addAll(IdentityResolverStrategy.resolveCustomBtnUsers(token, node, rawLimitUserIds,
                    ParticipantType.TodoUser));
        }
        return limitUserIds;
    }

    /**
     * @param param
     * @return
     */
    private boolean evaluateMemberOfBusinessUnit(Param param) {
        Context context = param.getContext();
        String createInstanceWay = (String) context.getValue("createInstanceWay");
        String orgId = (String) context.getValue("businessType");
        List<String> taskUserSids = (List<String>) context.getValue("taskOrgIds");
        // 要比较人员
        if (CollectionUtils.isEmpty(taskUserSids)) {
            return false;
        }

        // 业务单位范围
        List<String> limitUserSids = Arrays.asList(StringUtils.split(member, Separator.SEMICOLON.getValue()));
        if (CollectionUtils.isEmpty(limitUserSids)) {
            return false;
        }

        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        String orgVersionId = null;
        if (StringUtils.startsWith(orgId, IdPrefix.ORG.getValue())) {
            orgVersionId = workflowOrgService.getOrgVersionIdByOrgId(orgId);
        }
        // 单一实例
        if (FlowConstant.CREATE_INSTANCE_WAY.SINGLETON.equals(createInstanceWay)) {
            for (String flowUserSid : taskUserSids) {
                // 行政组织
                if (StringUtils.startsWith(orgId, IdPrefix.ORG.getValue())) {
                    if (!(limitUserSids.contains(flowUserSid) || workflowOrgService.isMemberOf(flowUserSid, limitUserSids, orgVersionId))) {
                        return false;
                    }
                } else {
                    // 业务组织
                    if (!(limitUserSids.contains(flowUserSid) || workflowOrgService.isMemberOfBizOrg(flowUserSid, limitUserSids, orgId))) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            // 按办理人生成
            for (String flowUserSid : taskUserSids) {
                // 行政组织
                if (StringUtils.startsWith(orgId, IdPrefix.ORG.getValue())) {
                    if (limitUserSids.contains(flowUserSid) || workflowOrgService.isMemberOf(flowUserSid, limitUserSids, orgVersionId)) {
                        return true;
                    }
                } else {
                    if (limitUserSids.contains(flowUserSid) || workflowOrgService.isMemberOfBizOrg(flowUserSid, limitUserSids, orgId)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

}
