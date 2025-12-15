/*
 * @(#)2015-6-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-27.1	zhulh		2015-6-27		Create
 * </pre>
 * @date 2015-6-27
 */
@Component
public class OptionOfIdentityResolver extends AbstractIdentityResolver {

    public static final String USER_IDS_FOR_OPTION_OF = "UserIdsForOptionOf";
    public static final String ORG_ID_FOR_OPTION_OF = "OrgIdForOptionOf";

    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.IdentityResolver#resolve(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.core.Token, java.util.List, com.wellsoft.pt.bpm.engine.enums.ParticipantType)
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType,
                                     String sidGranularity) {
        if (CollectionUtils.isEmpty(raws)) {
            return Collections.emptyList();
        }

        // 输入用户ID列表
        List<FlowUserSid> userSidsForOptionOf = (List<FlowUserSid>) token.getTaskData().get(USER_IDS_FOR_OPTION_OF);
        if (CollectionUtils.isEmpty(userSidsForOptionOf)) {
            return Collections.emptyList();
        }

        String jobId = userSidsForOptionOf.stream().filter(sid -> StringUtils.isNotBlank(sid.getIdentityIdPath()))
                .map(FlowUserSid::getIdentityId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));

        // 组织类型
        String orgVersionId = (String) token.getTaskData().get(ORG_ID_FOR_OPTION_OF);
        if (StringUtils.isBlank(orgVersionId)) {
            // 申请人
            String creator = token.getFlowInstance().getStartUserId();
            orgVersionId = OrgVersionUtils.getFlowOrgVersionId(token, creator);
        }

        // 参与人
        List<String> userIds = new ArrayList<String>(0);
        Participant participant = Enum.valueOf(Participant.class, raws.get(0));
        for (FlowUserSid userSid : userSidsForOptionOf) {
            String userId = userSid.getId();
            switch (participant) {
                // 人员选择
                // 1、直接领导
                case LeaderOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobSuperiorLeader(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobSuperiorLeader(userId, orgVersionId));
                    }
                    break;
                // 2、部门领导
                case DeptLeaderOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobDepartmentLeader(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobDepartmentLeader(userId, orgVersionId));
                    }
                    break;
                // 3、分管领导
                case BranchedLeaderOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobBranchLeader(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobBranchLeader(userId, orgVersionId));
                    }
                    break;
                // 4、所有领导
                case AllLeaderOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobLeaderOfAll(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobLeaderOfAll(userId, orgVersionId));
                    }
                    break;
                // 5、部门
                case DeptOf:
                    // 获取部门ID
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobDepartmentUserId(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobDepartmentUserId(userId, orgVersionId));
                    }
                    break;
                // 6、上级部门
                case ParentDeptOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobParentDepartmentUserId(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobParentDepartmentUserId(userId, orgVersionId));
                    }
                    break;
                // 7、根部门
                case RootDeptOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobRootDepartmentUserId(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserAllJobRootDepartmentUserId(userId, orgVersionId));
                    }
                    break;
                // 8、直系部门人员
                case SameDeptOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobDepartmentUserId(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserMainJobDepartmentUserId(userId, orgVersionId));
                    }
                    break;
                // 9、同一根部门人员
                case SameRootDeptOf:
                    if (StringUtils.isNotBlank(jobId)) {
                        userIds.addAll(workflowOrgService.listUserJobRootDepartmentUserId(userId, jobId, orgVersionId));
                    } else {
                        userIds.addAll(workflowOrgService.listUserMainJobRootDepartmentUserId(userId, orgVersionId));
                    }
                    break;
                default:
                    break;
            }
        }

        List<FlowUserSid> userSids = sidGranularityResolver.resolve(node, token, userIds, sidGranularity);
        if (ParticipantType.TodoUser.equals(participantType)) {
            List<String> idPaths = userSidsForOptionOf.stream().filter(sid -> StringUtils.isNotBlank(sid.getIdentityIdPath()))
                    .map(FlowUserSid::getIdentityIdPath).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(idPaths)) {
                flowUserJobIdentityService.addUnitUserJobIdentity(userSids, idPaths, true, node.getId(), token, participantType);
            }
        }
        return userSids;
    }

}
