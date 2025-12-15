/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: 解析组织选择框选中的数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-15.1	zhulh		2013-3-15		Create
 * </pre>
 * @date 2013-3-15
 */
@Component
public class UnitIdentityResolver extends AbstractIdentityResolver {

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    @Autowired
    private OptionIdentityResolver optionIdentityResolver;

    @Autowired
    private JobDutyIdentityResolver jobDutyIdentityResolver;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.IdentityResolver#resolve(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.core.Token, java.util.List)
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> rawUsers, ParticipantType participantType,
                                     String sidGranularity) {
        List<FlowUserSid> userIds = new ArrayList<FlowUserSid>(0);
        if (CollectionUtils.isEmpty(rawUsers)) {
            return userIds;
        }

        // 解析用户、部门、群组的ID为userId
        // userIds.addAll(IdentityResolverStrategy.resolveUserIds(rawUsers));
        userIds.addAll(sidGranularityResolver.resolve(node, token, rawUsers, sidGranularity));

        return userIds;
    }

    /**
     * @param node
     * @param token
     * @param userUnitElement
     * @param participantType
     * @param sidGranularity
     * @return
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, UserUnitElement userUnitElement, ParticipantType participantType, String sidGranularity) {
        List<FlowUserSid> userIds = Lists.newArrayList();

        // 行政组织用户
        String value = userUnitElement.getValue();
        if (StringUtils.isNotBlank(value)) {
            List<String> rawUsers = Arrays.asList(StringUtils.split(value, Separator.SEMICOLON.getValue()));
            String orgId = userUnitElement.getOrgId();
            if (CollectionUtils.isNotEmpty(rawUsers)) {
                List<FlowUserSid> unitUserSids = sidGranularityResolver.resolve(node, token, rawUsers, sidGranularity, orgId);
                flowUserJobIdentityService.addUnitUserJobIdentity(unitUserSids, userUnitElement.getValuePath(), node.getId(), token, participantType);
                userIds.addAll(unitUserSids);
            }
        }

        // 人员选项
        if (CollectionUtils.isNotEmpty(userUnitElement.getUserOptions())) {
            userIds.addAll(optionIdentityResolver.resolve(node, token, userUnitElement, participantType, sidGranularity));
        }

        // 职等职级
        if (userUnitElement.getIsEnabledJobRank() || userUnitElement.getIsEnabledJobGrade()) {
            userIds.addAll(jobDutyIdentityResolver.resolve(node, token, userUnitElement, participantType, sidGranularity));
        }
        return userIds;
    }
}
