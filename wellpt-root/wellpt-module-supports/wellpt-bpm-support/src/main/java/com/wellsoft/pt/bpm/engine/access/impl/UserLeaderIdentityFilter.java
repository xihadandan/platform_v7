/*
 * @(#)2014-10-22 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.access.IdentityFilter;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-22.1	zhulh		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
@Component
public class UserLeaderIdentityFilter implements IdentityFilter {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.impl.DefaultIdentityFilter#doFilter(java.util.List, com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.core.Token, java.util.List)
     */
    @Override
    public Set<FlowUserSid> doFilter(List<FlowUserSid> userSids, Node node, Token token, List<String> raws) {
        // 过滤后要返回的用户ID
        Set<FlowUserSid> returnUserIds = new LinkedHashSet<FlowUserSid>();
        if (raws.isEmpty()) {
            returnUserIds.addAll(userSids);
            return returnUserIds;
        }

        // 要过滤的用户ID
        List<String> filterUserIds = new ArrayList<String>(0);
        for (String user : raws) {
            // 前办理人ID
            if (StringUtils.isBlank(user)) {
                continue;
            }
            Participant participant = Enum.valueOf(Participant.class, user.trim());
            switch (participant) {// 人员过滤
                // 7、办理人的领导
                // case LeaderOfUser:
                // for (String id : userIds) {
                // returnUserIds.addAll(orgApiFacade.getUserLeaderIds(id));
                // }
                // break;
                // // 7、办理人的部门领导
                // case DeptLeaderOfUser:
                // for (String id : userIds) {
                // returnUserIds.addAll(orgApiFacade.getUserDepartmentLeaderIds(id));
                // }
                // break;
                // // 7、办理人的分管领导
                // case BranchedLeaderOfUser:
                // for (String id : userIds) {
                // returnUserIds.addAll(orgApiFacade.getUserDepartmentBranchedLeaders(id));
                // }
                // break;
                // // 8、办理人的所有领导
                // case AllLeaderOfUser:
                // for (String id : userIds) {
                // returnUserIds.addAll(orgApiFacade.getAllUserLeaderIds(id));
                // }
                // break;
                default:
                    break;
            }
        }
        // 如果要过滤的人员不为空则过滤人员
        if (CollectionUtils.isNotEmpty(filterUserIds)) {
            String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
            List<String> userIds = new ArrayList<String>();
            for (FlowUserSid userSid : userSids) {
                userIds.add(userSid.getId());
            }
            String memberOf = StringUtils.join(userIds, Separator.SEMICOLON.getValue());
            Set<String> filterIds = new LinkedHashSet<String>();
            for (String filterId : filterUserIds) {
                if (userIds.contains(filterId) || workflowOrgService.isMemberOf(filterId, memberOf, orgVersionIds)) {
                    filterIds.add(filterId);
                }
            }
            returnUserIds.addAll(sidGranularityResolver.resolve(node, token, filterIds));
        } else if (returnUserIds.isEmpty()) {
            returnUserIds.addAll(userSids);
        }
        return returnUserIds;
    }

    @Override
    public Set<FlowUserSid> doFilter(List<FlowUserSid> userIds, Node node, Token token, UserUnitElement userUnitElement) {
        return Collections.emptySet();
    }

}
