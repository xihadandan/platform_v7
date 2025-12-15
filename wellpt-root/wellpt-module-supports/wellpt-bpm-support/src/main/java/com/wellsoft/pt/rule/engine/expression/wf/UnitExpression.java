/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.expression.wf;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.UnitIdentityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.rule.engine.Context;
import com.wellsoft.pt.rule.engine.Param;
import com.wellsoft.pt.rule.engine.suport.CommandName;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

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
 * 2015-6-25.1	zhulh		2015-6-25		Create
 * </pre>
 * @date 2015-6-25
 */
public class UnitExpression extends WorkFlowExpression {

    private String expression;

    /**
     * @param expression
     */
    public UnitExpression(String expression) {
        this.expression = expression;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.rule.engine.expression.Expression#evaluate(com.wellsoft.pt.rule.engine.Param)
     */
    @Override
    public Object evaluate(Param param) {
        Context context = param.getContext();
        UnitIdentityResolver unitIdentityResolver = ApplicationContextHolder.getBean(UnitIdentityResolver.class);

        Node node = (Node) context.getValue(KEY_NODE);
        Token token = (Token) context.getValue(KEY_TOKEN);
        ParticipantType participantType = (ParticipantType) context.getValue("participantType");
        participantType = participantType == null ? ParticipantType.TodoUser : participantType;

        List<String> orgIds = getParamAsList(this.expression, CommandName.Unit);
        List<String> orgIdPaths = null;
        if (isOrgIdPath(orgIds)) {
            orgIdPaths = Lists.newArrayList(orgIds);
            orgIds = extractOrgIds(orgIds);
        }

        List<FlowUserSid> userIds = unitIdentityResolver.resolve(node, token, orgIds, participantType);
        // 添加审批路径
        if (ParticipantType.TodoUser.equals(participantType) && CollectionUtils.isNotEmpty(userIds) && CollectionUtils.isNotEmpty(orgIdPaths)) {
            FlowUserJobIdentityService flowUserJobIdentityService = ApplicationContextHolder.getBean(FlowUserJobIdentityService.class);
            flowUserJobIdentityService.addUnitUserJobIdentity(userIds, orgIdPaths, false, node.getId(), token, participantType);
        }

        return userIds;
    }

    private List<String> extractOrgIds(List<String> orgIdPaths) {
        List<String> orgIds = Lists.newArrayList();
        for (String orgIdPath : orgIdPaths) {
            if (StringUtils.contains(orgIdPath, Separator.SLASH.getValue())) {
                String[] ids = StringUtils.split(orgIdPath, Separator.SLASH.getValue());
                int length = ids.length;
                // 业务角色ID
                if (length >= 2 && (StringUtils.startsWith(ids[length - 2], IdPrefix.BIZ_PREFIX.getValue())
                        || StringUtils.startsWith(ids[length - 2], IdPrefix.BIZ_ORG_DIM.getValue()))
                        && !IdPrefix.hasPrefix(ids[length - 1])) {
                    orgIds.add(ids[length - 2] + Separator.SLASH.getValue() + ids[length - 1]);
                } else {
                    orgIds.add(ids[length - 1]);
                }
            } else {
                orgIds.add(orgIdPath);
            }
        }
        return orgIds;
    }

    /**
     * @param orgIds
     * @return
     */
    private boolean isOrgIdPath(List<String> orgIds) {
        for (String orgId : orgIds) {
            if (StringUtils.contains(orgId, Separator.SLASH.getValue())) {
                String[] ids = StringUtils.split(orgId, Separator.SLASH.getValue());
                // 业务角色不是组织路径
                if (ids.length == 2 && (StringUtils.startsWith(ids[0], IdPrefix.BIZ_PREFIX.getValue())
                        || StringUtils.startsWith(ids[0], IdPrefix.BIZ_ORG_DIM.getValue()))
                        && !IdPrefix.hasPrefix(ids[1])) {
                    continue;
                }
                return true;
            }
        }
        return false;
    }

}
