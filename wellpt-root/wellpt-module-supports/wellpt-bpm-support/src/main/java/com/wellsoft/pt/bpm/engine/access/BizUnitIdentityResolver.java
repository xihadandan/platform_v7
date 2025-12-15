/*
 * @(#)12/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Description: 业务组织人员解析
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/12/24.1	    zhulh		12/12/24		    Create
 * </pre>
 * @date 12/12/24
 */
@Component
public class BizUnitIdentityResolver extends AbstractIdentityResolver {

    @Autowired
    private BizOrgSidGranularityResolver bizOrgSidGranularityResolver;

    @Autowired
    private BizOrgOptionIdentityResolver bizOrgOptionIdentityResolver;

    /**
     * @param node
     * @param token
     * @param raws
     * @param participantType
     * @param sidGranularity
     * @return
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType, String sidGranularity) {
        throw new UnsupportedOperationException();
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

        // 业务组织用户
        String value = userUnitElement.getValue();
        if (StringUtils.isNotBlank(value)) {
            List<String> rawUsers = Arrays.asList(StringUtils.split(value, Separator.SEMICOLON.getValue()));
            String bizOrgId = OrgVersionUtils.getAvailableBizOrgId(userUnitElement.getBizOrgId(), token);
            if (CollectionUtils.isNotEmpty(rawUsers)) {
                userIds.addAll(bizOrgSidGranularityResolver.resolve(node, token, rawUsers, sidGranularity, bizOrgId));
            }
        }

        // 人员选项
        if (CollectionUtils.isNotEmpty(userUnitElement.getUserOptions())) {
            userIds.addAll(bizOrgOptionIdentityResolver.resolve(node, token, userUnitElement, participantType, sidGranularity));
        }

        return userIds;
    }

}
