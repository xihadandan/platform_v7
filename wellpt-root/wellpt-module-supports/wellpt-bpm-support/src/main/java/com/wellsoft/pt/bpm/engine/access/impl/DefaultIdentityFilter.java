/*
 * @(#)2014-5-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.access.IdentityFilter;
import com.wellsoft.pt.bpm.engine.access.JobDutyIdentityResolver;
import com.wellsoft.pt.bpm.engine.access.SidGranularityResolver;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserOptionElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

import static com.wellsoft.pt.bpm.engine.enums.Participant.*;

/**
 * Description: 默认的人员过滤实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-5-27.1	zhulh		2014-5-27		Create
 * </pre>
 * @date 2014-5-27
 */
@Component
public class DefaultIdentityFilter implements IdentityFilter {

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private JobDutyIdentityResolver jobDutyIdentityResolver;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * (non-Javadoc)
     */
    public static boolean isDoFilter(List<String> raws) {
        for (String user : raws) {
            if (StringUtils.isBlank(user)) {
                continue;
            }
            Participant participant = Enum.valueOf(Participant.class, user.trim());
            switch (participant) {// 人员过滤
                case SameDeptAsPrior:
                    return true;
                // 2、限于前办理人的同一根部门人员
                case SameRootDeptAsPrior:
                    return true;
                // 2、限于前办理人的同一业务单位人员
                case SameBizUnitAsPrior:
                    return true;
                // 3、限于前办理人的上级领导
                case SameLeaderAsPrior:
                    return true;
                // 3、限于前办理人的部门领导
                case SameDeptLeaderAsPrior:
                    return true;
                // 3、限于前办理人的分管领导
                case SameBranchLeaderAsPrior:
                    return true;
                // 3、限于前办理人的所有上级领导
                case SameAllLeaderAsPrior:
                    return true;
                // 限于前办理人的同业务角色人员
                case SameBizRoleAsPrior:
                    return true;
                // 4、限于申请人的部门人员
                case SameDeptAsCreator:
                    return true;
                // 5、限于申请人的同一根部门人员
                case SameRootDeptAsCreator:
                    return true;
                // 5、限于申请人的同一业务单位人员
                case SameBizUnitAsCreator:
                    return true;
                // 6、限于申请人的上级领导
                case SameLeaderAsCreator:
                    return true;
                // 6、限于申请人的部门领导
                case SameDeptLeaderAsCreator:
                    return true;
                // 6、限于申请人的分管领导
                case SameBranchLeaderAsCreator:
                    return true;
                // 6、限于申请人的所有上级领导
                case SameAllLeaderAsCreator:
                    return true;
                // 限于申请人的同业务角色人员
                case SameBizRoleAsCreator:
                    return true;
                default:
                    break;
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Set<FlowUserSid> doFilter(List<FlowUserSid> userSids, Node node, Token token, List<String> raws) {
        // 过滤后要返回的用户ID
        Set<FlowUserSid> returnUserIds = new LinkedHashSet<>();
        if (CollectionUtils.isEmpty(raws)) {
            returnUserIds.addAll(userSids);
            return returnUserIds;
        }

        List<UserOptionElement> userOptionElements = Lists.newArrayList();
        raws.forEach(userOption -> {
            UserOptionElement element = new UserOptionElement();
            element.setValue(userOption);
            userOptionElements.add(element);
        });

        return filterUserOptions(userSids, node, token, userOptionElements, null, null);
    }

    @Override
    public Set<FlowUserSid> doFilter(List<FlowUserSid> userIds, Node node, Token token, UserUnitElement userUnitElement) {
        // 人员选项过滤
        Set<FlowUserSid> returnUserIds = filterUserOptions(userIds, node, token, userUnitElement.getUserOptions(), userUnitElement.getOrgId(), userUnitElement.getBizOrgId());

        // 职等职级过滤
        if (CollectionUtils.isNotEmpty(returnUserIds) && (userUnitElement.getIsEnabledJobRank() || userUnitElement.getIsEnabledJobGrade())) {
            List<FlowUserSid> filterUserIds = jobDutyIdentityResolver.resolve(node, token, userUnitElement, ParticipantType.ViewerUser, SidGranularity.USER);
            returnUserIds = Sets.newLinkedHashSet(CollectionUtils.intersection(returnUserIds, filterUserIds));
        }
        return returnUserIds;
    }

    /**
     * @param userSids
     * @param node
     * @param token
     * @param userOptionElements
     * @param orgId
     * @param bizOrgId
     * @return
     */
    private Set<FlowUserSid> filterUserOptions(List<FlowUserSid> userSids, Node node, Token token,
                                               List<UserOptionElement> userOptionElements, String orgId, String bizOrgId) {
        if (CollectionUtils.isEmpty(userOptionElements)) {
            return Sets.newLinkedHashSet(userSids);
        }

        // 申请人
        String creator = token.getFlowInstance().getStartUserId();
        // 当前办理人
        String userId = token.getTaskData().getUserId();
        // 前办理人ID
        String priorUserId = getPriorUserId(token, userId);
        JobIdentity jobIdentity = flowUserJobIdentityService.getPriorUserJobIdentity(priorUserId, userOptionElements, true, node, token);
        String multiJobFlowType = jobIdentity.getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        // 获取多职流转的职位选择
        String jobId = jobIdentity.getJobId();

        // 申请人多职流转配置
        JobIdentity creatorJobIdentity = token.getFlowDelegate().getJobIdentity(token.getFlowDelegate().getStartNode().getToID());
        String creatorJobIdentityId = token.getFlowInstance().getStartJobId();

        // 要过滤的用户ID
        List<String> filterUserIds = Lists.newArrayList();
        // 获取流程的组织版本
        String[] orgVersionIds = OrgVersionUtils.getFlowOrgVersionIdsAsArray(orgId, token);

        boolean isSetFilter = true;
        // 人员过滤
        for (UserOptionElement userOptionElement : userOptionElements) {
            String userOption = userOptionElement.getValue();
            if (StringUtils.isBlank(userOption)) {
                continue;
            }
            Participant participant = Enum.valueOf(Participant.class, userOption.trim());
            if (StringUtils.isNotBlank(bizOrgId)) {
                // 业务组织
                filterUserIds.addAll(getBizOrgUsers(creator, creatorJobIdentityId, priorUserId, jobId, participant, userOptionElement, bizOrgId));
                if (CollectionUtils.isEmpty(filterUserIds)) {
                    isSetFilter = isSetFilterBizOrgUsers(participant);
                }
                continue;
            }

            switch (participant) {
                // 限于前办理人的同一部门人员
                case SameDeptAsPrior:
                    Set<String> deptAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        deptAsPriorUserIds = workflowOrgService.listUserAllJobDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptAsPriorUserIds = workflowOrgService.listUserMainJobDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptAsPriorUserIds = workflowOrgService.listUserJobDepartmentUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptAsPriorUserIds)) {
                        filterUserIds.addAll(deptAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的同一根部门人员
                case SameRootDeptAsPrior:
                    Set<String> rootDeptAsPriorUserIds = null;
                    // 前办理人
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        rootDeptAsPriorUserIds = workflowOrgService.listUserAllJobRootDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootDeptAsPriorUserIds = workflowOrgService.listUserMainJobRootDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootDeptAsPriorUserIds = workflowOrgService.listUserJobRootDepartmentUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(rootDeptAsPriorUserIds)) {
                        filterUserIds.addAll(rootDeptAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的同一根节点人员
                case SameRootNodeAsPrior:
                    Set<String> rootNodeAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        rootNodeAsPriorUserIds = workflowOrgService.listUserAllJobRootNodeUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootNodeAsPriorUserIds = workflowOrgService.listUserMainJobRootNodeUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootNodeAsPriorUserIds = workflowOrgService.listUserJobRootNodeUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(rootNodeAsPriorUserIds)) {
                        filterUserIds.addAll(rootNodeAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的同一单位人员
                case SameBizUnitAsPrior:
                    Set<String> bizUnitAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        bizUnitAsPriorUserIds = workflowOrgService.listUserAllJobUnitUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        bizUnitAsPriorUserIds = workflowOrgService.listUserMainJobUnitUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        bizUnitAsPriorUserIds = workflowOrgService.listUserJobUnitUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(bizUnitAsPriorUserIds)) {
                        filterUserIds.addAll(bizUnitAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的直接汇报人
                case SameDirectLeaderAsPrior:
                    Set<String> directLeaderAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        directLeaderAsPriorUserIds = workflowOrgService.listUserAllJobDirectLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        directLeaderAsPriorUserIds = workflowOrgService.listUserMainJobDirectLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        directLeaderAsPriorUserIds = workflowOrgService.listUserJobDirectLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(directLeaderAsPriorUserIds)) {
                        filterUserIds.addAll(directLeaderAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的部门领导
                case SameDeptLeaderAsPrior:
                    Set<String> deptLeaderAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        deptLeaderAsPriorUserIds = workflowOrgService.listUserAllJobDepartmentLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptLeaderAsPriorUserIds = workflowOrgService.listUserMainJobDepartmentLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptLeaderAsPriorUserIds = workflowOrgService.listUserJobDepartmentLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptLeaderAsPriorUserIds)) {
                        filterUserIds.addAll(deptLeaderAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的上级领导
                case SameLeaderAsPrior:
                    Set<String> leaderAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        leaderAsPriorUserIds = workflowOrgService.listUserAllJobSuperiorLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        leaderAsPriorUserIds = workflowOrgService.listUserMainJobSuperiorLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        leaderAsPriorUserIds = workflowOrgService.listUserJobSuperiorLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(leaderAsPriorUserIds)) {
                        filterUserIds.addAll(leaderAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的分管领导
                case SameBranchLeaderAsPrior:
                    Set<String> branchLeaderAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        branchLeaderAsPriorUserIds = workflowOrgService.listUserAllJobBranchLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        branchLeaderAsPriorUserIds = workflowOrgService.listUserMainJobBranchLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        branchLeaderAsPriorUserIds = workflowOrgService.listUserJobBranchLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(branchLeaderAsPriorUserIds)) {
                        filterUserIds.addAll(branchLeaderAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的所有上级领导
                case SameAllLeaderAsPrior:
                    Set<String> allLeaderAsPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        allLeaderAsPriorUserIds = workflowOrgService.listUserAllJobLeaderOfAll(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        allLeaderAsPriorUserIds = workflowOrgService.listUserMainJobLeaderOfAll(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        allLeaderAsPriorUserIds = workflowOrgService.listUserJobLeaderOfAll(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(allLeaderAsPriorUserIds)) {
                        filterUserIds.addAll(allLeaderAsPriorUserIds);
                    }
                    break;
                // 限于前办理人的直接下属
                case SameSubordinateOfPrior:
                    Set<String> subordinnateOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> allJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER);
                        subordinnateOfPriorUserIds = workflowOrgService.listUserSubordinateUserIds(priorUserId, allJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> mainJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER);
                        subordinnateOfPriorUserIds = workflowOrgService.listUserSubordinateUserIds(priorUserId, mainJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        subordinnateOfPriorUserIds = workflowOrgService.listUserSubordinateUserIds(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(subordinnateOfPriorUserIds)) {
                        filterUserIds.addAll(subordinnateOfPriorUserIds);
                    }
                    break;
                // 限于前办理人的所有下属
                case SameAllSubordinateOfPrior:
                    Set<String> allSubordinateOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> allJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER);
                        allSubordinateOfPriorUserIds = workflowOrgService.listUserAllSubordinateUserIds(priorUserId, allJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> mainJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER);
                        allSubordinateOfPriorUserIds = workflowOrgService.listUserAllSubordinateUserIds(priorUserId, mainJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        allSubordinateOfPriorUserIds = workflowOrgService.listUserAllSubordinateUserIds(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(allSubordinateOfPriorUserIds)) {
                        filterUserIds.addAll(allSubordinateOfPriorUserIds);
                    }
                    break;
                // 限于申请人的同一部门人员
                case SameDeptAsCreator:
                    Set<String> deptAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        deptAsCreatorUserIds = workflowOrgService.listUserAllJobDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        deptAsCreatorUserIds = workflowOrgService.listUserMainJobDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        deptAsCreatorUserIds = workflowOrgService.listUserJobDepartmentUserId(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptAsCreatorUserIds)) {
                        filterUserIds.addAll(deptAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的同一根部门人员
                case SameRootDeptAsCreator:
                    Set<String> rootDeptAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        rootDeptAsCreatorUserIds = workflowOrgService.listUserAllJobRootDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        rootDeptAsCreatorUserIds = workflowOrgService.listUserMainJobRootDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        rootDeptAsCreatorUserIds = workflowOrgService.listUserJobRootDepartmentUserId(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(rootDeptAsCreatorUserIds)) {
                        filterUserIds.addAll(rootDeptAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的同一根节点人员
                case SameRootNodeAsCreator:
                    Set<String> rootNodeAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        rootNodeAsCreatorUserIds = workflowOrgService.listUserAllJobRootNodeUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        rootNodeAsCreatorUserIds = workflowOrgService.listUserMainJobRootNodeUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        rootNodeAsCreatorUserIds = workflowOrgService.listUserJobRootNodeUserId(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(rootNodeAsCreatorUserIds)) {
                        filterUserIds.addAll(rootNodeAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的同一单位人员
                case SameBizUnitAsCreator:
                    Set<String> bizUnitAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        bizUnitAsCreatorUserIds = workflowOrgService.listUserAllJobUnitUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        bizUnitAsCreatorUserIds = workflowOrgService.listUserMainJobUnitUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        bizUnitAsCreatorUserIds = workflowOrgService.listUserJobUnitUserId(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(bizUnitAsCreatorUserIds)) {
                        filterUserIds.addAll(bizUnitAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的直接汇报人
                case SameDirectLeaderAsCreator:
                    Set<String> directLeaderAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        directLeaderAsCreatorUserIds = workflowOrgService.listUserAllJobDirectLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        directLeaderAsCreatorUserIds = workflowOrgService.listUserMainJobDirectLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        directLeaderAsCreatorUserIds = workflowOrgService.listUserJobDirectLeader(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(directLeaderAsCreatorUserIds)) {
                        filterUserIds.addAll(directLeaderAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的部门领导
                case SameDeptLeaderAsCreator:
                    Set<String> deptLeaderAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        deptLeaderAsCreatorUserIds = workflowOrgService.listUserAllJobDepartmentLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        deptLeaderAsCreatorUserIds = workflowOrgService.listUserMainJobDepartmentLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        deptLeaderAsCreatorUserIds = workflowOrgService.listUserJobDepartmentLeader(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptLeaderAsCreatorUserIds)) {
                        filterUserIds.addAll(deptLeaderAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的上级领导
                case SameLeaderAsCreator:
                    Set<String> leaderAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        leaderAsCreatorUserIds = workflowOrgService.listUserAllJobSuperiorLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        leaderAsCreatorUserIds = workflowOrgService.listUserMainJobSuperiorLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        leaderAsCreatorUserIds = workflowOrgService.listUserJobSuperiorLeader(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(leaderAsCreatorUserIds)) {
                        filterUserIds.addAll(leaderAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的分管领导
                case SameBranchLeaderAsCreator:
                    Set<String> branchLeaderAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        branchLeaderAsCreatorUserIds = workflowOrgService.listUserAllJobBranchLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        branchLeaderAsCreatorUserIds = workflowOrgService.listUserMainJobBranchLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        branchLeaderAsCreatorUserIds = workflowOrgService.listUserJobBranchLeader(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(branchLeaderAsCreatorUserIds)) {
                        filterUserIds.addAll(branchLeaderAsCreatorUserIds);
                    }
                    break;
                // 限于申请人的所有上级领导
                case SameAllLeaderAsCreator:
                    Set<String> allLeaderAsCreatorUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        allLeaderAsCreatorUserIds = workflowOrgService.listUserAllJobLeaderOfAll(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        allLeaderAsCreatorUserIds = workflowOrgService.listUserMainJobLeaderOfAll(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        allLeaderAsCreatorUserIds = workflowOrgService.listUserJobLeaderOfAll(creator, getCreatorSelectJobValue(token), orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(allLeaderAsCreatorUserIds)) {
                        filterUserIds.addAll(allLeaderAsCreatorUserIds);
                    }
                    break;
                default:
                    isSetFilter = false;
                    break;
            }
        }

        Set<FlowUserSid> returnUserIds = new LinkedHashSet<>();
        // 没有进行人员过滤，直接返回原有人员，否则返回包含的过滤人员
        if (isSetFilter || CollectionUtils.isNotEmpty(filterUserIds)) {
            List<String> userIds = Lists.newArrayList();
            for (FlowUserSid userSid : userSids) {
                userIds.add(userSid.getId());
            }
            // 用户取交集
            Collection<String> retainUsers = CollectionUtils.retainAll(userIds, filterUserIds);
            returnUserIds.addAll(userSids.stream().filter(userSid -> retainUsers.contains(userSid.getId()))
                    .collect(Collectors.toList()));
        } else {
            returnUserIds.addAll(userSids);
        }
        return returnUserIds;
    }

    /**
     * @param participant
     * @return
     */
    private boolean isSetFilterBizOrgUsers(Participant participant) {
        List<Participant> participants = Lists.newArrayList(SameBizItemAsPrior, SameRoleUserOfBizItemAsPrior,
                SameDeptAsPrior, SameDeptAndBizRoleAsPrior, SameRoleUserOfDeptAsPrior, SameParentDeptAsPrior,
                SameRoleUserOfParentDeptAsPrior, SameRootDeptAsPrior, SameRoleUserOfRootDeptAsPrior, SameRootNodeAsPrior,
                SameRoleUserOfRootNodeAsPrior, SameBizRoleAsPrior, SameBizItemAsCreator, SameRoleUserOfBizItemAsCreator,
                SameDeptAsCreator, SameDeptAndBizRoleAsCreator, SameRoleUserOfDeptAsCreator, SameParentDeptAsCreator,
                SameRoleUserOfParentDeptAsCreator, SameRootDeptAsCreator, SameRoleUserOfRootDeptAsCreator, SameRootNodeAsCreator,
                SameRoleUserOfRootNodeAsCreator, SameBizRoleAsCreator);
        return participants.contains(participant);
    }

    /**
     * @param creator
     * @param creatorJobIdentityId
     * @param priorUserId
     * @param priorUserJobIdentityId
     * @param participant
     * @param userOptionElement
     * @param bizOrgId
     * @return
     */
    private Set<String> getBizOrgUsers(String creator, String creatorJobIdentityId, String priorUserId, String priorUserJobIdentityId,
                                       Participant participant, UserOptionElement userOptionElement, String bizOrgId) {
        Set<String> userIds = new LinkedHashSet<>();
        // 业务组织
        switch (participant) {
            // 前办理人
            // 限于前办理人的同业务项人员
            case SameBizItemAsPrior:
                userIds.addAll(workflowOrgService.listUserBizItemAndBizRoleUserId(priorUserId, null, bizOrgId));
                break;
            // 限于前办理人同业务项的指定角色人员
            case SameRoleUserOfBizItemAsPrior:
                List<String> bizItemAsPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizItemAndBizRoleUserId(priorUserId, bizItemAsPriorRoleIds, bizOrgId));
                break;
            // 限于前办理人的同部门人员
            case SameDeptAsPrior:
                userIds.addAll(workflowOrgService.listUserBizDepartmentUserId(priorUserId, priorUserJobIdentityId, false, bizOrgId));
                break;
            // 限于前办理人的同部门同角色人员
            case SameDeptAndBizRoleAsPrior:
                userIds.addAll(workflowOrgService.listUserBizDepartmentUserId(priorUserId, priorUserJobIdentityId, true, bizOrgId));
                break;
            // 限于前办理人同部门的指定角色人员
            case SameRoleUserOfDeptAsPrior:
                List<String> deptAsPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizDepartmentAndBizRoleUserId(priorUserId, deptAsPriorRoleIds, bizOrgId));
                break;
            // 限于前办理人的上级部门人员
            case SameParentDeptAsPrior:
                userIds.addAll(workflowOrgService.listUserBizParentDepartmentUserId(priorUserId, bizOrgId));
                break;
            // 限于前办理人上级部门的指定角色人员
            case SameRoleUserOfParentDeptAsPrior:
                List<String> parentDeptAsPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizParentDepartmentAndBizRoleUserId(priorUserId, priorUserJobIdentityId, parentDeptAsPriorRoleIds, bizOrgId));
                break;
            // 限于前办理人的根部门人员
            case SameRootDeptAsPrior:
                userIds.addAll(workflowOrgService.listUserBizRootDepartmentUserId(priorUserId, priorUserJobIdentityId, bizOrgId));
                break;
            // 限于前办理人根部门的指定角色人员
            case SameRoleUserOfRootDeptAsPrior:
                List<String> rootDeptAsPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizRootDepartmentAndBizRoleUserId(priorUserId, priorUserJobIdentityId, rootDeptAsPriorRoleIds, bizOrgId));
                break;
            // 限于前办理人的根节点人员
            case SameRootNodeAsPrior:
                userIds.addAll(workflowOrgService.listUserBizRootNodeUserId(priorUserId, bizOrgId));
                break;
            // 限于前办理人根节点的指定角色人员
            case SameRoleUserOfRootNodeAsPrior:
                List<String> rootNodeAsPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizRootNodeAndBizRoleUserId(priorUserId, rootNodeAsPriorRoleIds, bizOrgId));
                break;
            // 限于前办理人的同角色人员
            case SameBizRoleAsPrior:
                userIds.addAll(workflowOrgService.listUserBizRoleUserId(priorUserId, bizOrgId));
                break;
            // 申请人
            // 限于申请人的同业务项人员
            case SameBizItemAsCreator:
                userIds.addAll(workflowOrgService.listUserBizItemAndBizRoleUserId(creator, null, bizOrgId));
                break;
            // 限于申请人同业务项的指定角色人员
            case SameRoleUserOfBizItemAsCreator:
                List<String> bizItemAsCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizItemAndBizRoleUserId(creator, bizItemAsCreatorRoleIds, bizOrgId));
                break;
            // 限于申请人的同部门人员
            case SameDeptAsCreator:
                userIds.addAll(workflowOrgService.listUserBizDepartmentUserId(creator, creatorJobIdentityId, false, bizOrgId));
                break;
            // 限于申请人的同部门同角色人员
            case SameDeptAndBizRoleAsCreator:
                userIds.addAll(workflowOrgService.listUserBizDepartmentUserId(creator, creatorJobIdentityId, true, bizOrgId));
                break;
            // 限于申请人同部门的指定角色人员
            case SameRoleUserOfDeptAsCreator:
                List<String> deptAsCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizDepartmentAndBizRoleUserId(creator, deptAsCreatorRoleIds, bizOrgId));
                break;
            // 限于申请人的上级部门人员
            case SameParentDeptAsCreator:
                userIds.addAll(workflowOrgService.listUserBizParentDepartmentUserId(creator, bizOrgId));
                break;
            // 限于申请人上级部门的指定角色人员
            case SameRoleUserOfParentDeptAsCreator:
                List<String> parentDeptAsCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizParentDepartmentAndBizRoleUserId(creator, creatorJobIdentityId, parentDeptAsCreatorRoleIds, bizOrgId));
                break;
            // 限于申请人的根部门人员
            case SameRootDeptAsCreator:
                userIds.addAll(workflowOrgService.listUserBizRootDepartmentUserId(creator, creatorJobIdentityId, bizOrgId));
                break;
            // 限于申请人根部门的指定角色人员
            case SameRoleUserOfRootDeptAsCreator:
                List<String> rootDeptAsCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizRootDepartmentAndBizRoleUserId(creator, creatorJobIdentityId, rootDeptAsCreatorRoleIds, bizOrgId));
                break;
            // 限于申请人的根节点人员
            case SameRootNodeAsCreator:
                userIds.addAll(workflowOrgService.listUserBizRootNodeUserId(creator, bizOrgId));
                break;
            // 限于申请人根节点的指定角色人员
            case SameRoleUserOfRootNodeAsCreator:
                List<String> rootNodeAsCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                userIds.addAll(workflowOrgService.listUserBizRootNodeAndBizRoleUserId(creator, rootNodeAsCreatorRoleIds, bizOrgId));
                break;
            // 限于申请人的同角色人员
            case SameBizRoleAsCreator:
                userIds.addAll(workflowOrgService.listUserBizRoleUserId(creator, bizOrgId));
                break;
            default:
                break;
        }
        return userIds;
    }

    /**
     * @param token
     * @return
     */
    private String getCreatorSelectJobValue(Token token) {
        return token.getFlowInstance().getStartJobId();
//        // 申请人
//        String creator = token.getFlowInstance().getStartUserId();
//        String jobField = token.getFlowInstance().getFlowDefinition().getJobField();
//        String jobValue = null;
//        DyFormData dyFormData = token.getTaskData().getDyFormData(token.getTaskData().getDataUuid());
//        if (StringUtils.isNotBlank(jobField)) {
//            jobValue = ObjectUtils.toString(dyFormData.getFieldValue(jobField), StringUtils.EMPTY);
//        } else {
//            jobValue = token.getFlowInstance().getStartJobId();
//        }
//
//        if (StringUtils.isBlank(jobValue)) {
//            List<com.wellsoft.pt.org.dto.OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(creator, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
//            if (CollectionUtils.isNotEmpty(userJobDtos)) {
//                jobValue = userJobDtos.get(0).getJobId();
//            }
//        }
//
//        return jobValue;
    }

    /**
     * 获取前办理人用户ID，如果是委托待办则返回委托人，否则返回当前用户
     *
     * @param token
     * @param currentUserId
     * @return
     */
    private String getPriorUserId(Token token, String currentUserId) {
        TaskInstance taskInstance = token.getTask();
        if (taskInstance == null) {
            return currentUserId;
        }
        String key = taskInstance.getUuid() + currentUserId;
        String taskIdentityUuid = token.getTaskData().getTaskIdentityUuid(key);
        if (StringUtils.isBlank(taskIdentityUuid)) {
            return currentUserId;
        }
        TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
        if (taskIdentity == null) {
            return currentUserId;
        }
        // 判断当前用户是否进行委托提交
        Integer todoType = taskIdentity.getTodoType();
        if (!WorkFlowTodoType.Delegation.equals(todoType)) {
            return currentUserId;
        }
        String ownerId = taskIdentity.getOwnerId();
        if (StringUtils.isNotBlank(ownerId)) {
            return ownerId;
        }
        return currentUserId;
    }

}
