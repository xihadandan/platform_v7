/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.element.UserOptionElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityItem;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStack;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStackFactary;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 解析参与者中的数据
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
public class OptionIdentityResolver extends AbstractIdentityResolver {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;
//    @Autowired
//    private WfTaskInstanceTodoUserService wfTaskInstanceTodoUserService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * 获取前一环节所有办理人ID
     *
     * @param node
     * @param token
     * @return
     */
    private List<String> getPriorTaskUsers(Node node, Token token) {
        List<String> priorIds = new ArrayList<String>();
        Transition transition = token.getTransition();
        if (transition != null && transition.getFrom() != null && node.equals(transition.getRuntimeToNode())) {
            String flowInstUuid = token.getFlowInstance().getUuid();
            List<TaskActivityQueryItem> taskActivities = taskActivityService.getAllActivityByFlowInstUuid(flowInstUuid);
            TaskActivityStack stack = TaskActivityStackFactary.build(null, taskActivities);

            Iterator<TaskActivityItem> it = stack.iterator();
            String preTaskId = transition.getFrom().getId();
            while (it.hasNext()) {
                TaskActivityItem item = it.next();
                String taskId = item.getTaskId();
                String taskInstUuid = item.getTaskInstUuid();
                if (taskId.equals(preTaskId)) {
                    priorIds.addAll(taskService.getTaskOwners(taskInstUuid));
                    break;
                }
            }
        }
        return priorIds;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType,
                                     String sidGranularity) {
        if (CollectionUtils.isEmpty(raws)) {
            return Collections.emptyList();
        }

        List<UserOptionElement> userOptionElements = Lists.newArrayList();
        raws.forEach(userOption -> {
            UserOptionElement element = new UserOptionElement();
            element.setValue(userOption);
            userOptionElements.add(element);
        });

        return resolve(node, token, userOptionElements, participantType, sidGranularity, null, null);
    }

//    /**
//     * @param token
//     * @return
//     */
//    private String getCreatorSelectJobValue(Token token) {
//        return token.getFlowInstance().getStartJobId();
////        // 申请人
////        String creator = token.getFlowInstance().getStartUserId();
////        String jobField = token.getFlowInstance().getFlowDefinition().getJobField();
////        String jobValue = null;
////        DyFormData dyFormData = token.getTaskData().getDyFormData(token.getTaskData().getDataUuid());
////        if (StringUtils.isNotBlank(jobField)) {
////            jobValue = ObjectUtils.toString(dyFormData.getFieldValue(jobField), StringUtils.EMPTY);
////        } else {
////            jobValue = token.getFlowInstance().getStartJobId();
////        }
////
////        if (StringUtils.isBlank(jobValue)) {
////            List<com.wellsoft.pt.org.dto.OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(creator, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
////            if (CollectionUtils.isNotEmpty(userJobDtos)) {
////                jobValue = userJobDtos.get(0).getJobId();
////            }
////        }
////
////        return jobValue;
//    }

//    /**
//     * 多职流转按职位字段选择，获取职位值
//     * 1.表单不存在职位字段的情况：
//     * 1). 用户多职的情况下，抛异常到前端，让用户选择哪个职位，再提交上来
//     * 2). 用户只有一个职位的情况下，直接使用该职位
//     * 2.表单存在职位字段，使用职位字段的值
//     *
//     * @param token
//     * @return
//     */
//    private String getPriorUserSelectJobValue(Node node, Token token) {
//        JobIdentity jobIdentity = token.getFlowDelegate().getJobIdentity(node.getId());
//        String jobId = null;
//        String jobField = jobIdentity.getJobField();
//        TaskData taskData = token.getTaskData();
//        String userId = taskData.getUserId();
//        // 获取当前用户提交的身份
//        if (!taskData.getStartNewFlow(token.getFlowInstance().getUuid())) {
//            String taskIdentityUuid = taskData.getTaskIdentityUuid(token.getTask().getUuid() + userId);
//            if (StringUtils.isNotBlank(taskIdentityUuid)) {
//                TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
//                if (StringUtils.startsWith(taskIdentity.getUserId(), IdPrefix.USER.getValue())) {
//                    jobId = taskIdentity.getIdentityId();
//                } else {
//                    List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobIdentity(userId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
//                    jobId = orgUserJobDtos.stream().filter(orgUserJobDto -> StringUtils.contains(orgUserJobDto.getJobIdPath(), taskIdentity.getUserId()))
//                            .map(OrgUserJobDto::getJobId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
//                }
//            }
//        } else if (jobIdentity.isUserSelectJob()) {
//            // 多职流转按职位字段选择流转的情况
//            // 无选择职位字段的情况下，判断多职选择
//            if (jobIdentity.isSelectJobField() && StringUtils.isNotBlank(jobField)) {
//                // 职位根据职位字段获取
//                String jobFieldValue = (String) (taskData.getDyFormData(taskData.getDataUuid()).getFieldValue(jobField));
//                jobId = StringUtils.isNotBlank(jobFieldValue) ? jobFieldValue : null;
//            } else {
//                // 前端选择了职位
//                if (StringUtils.isNotBlank(taskData.getJobSelected())) {
//                    jobId = taskData.getJobSelected();
//                } else {
//                    // 如果没有配置职位字段，且用户是多职的情况下，则抛出异常，前端进行多职选择提交
//                    List<com.wellsoft.pt.org.dto.OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobIdentity(userId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
//                    // 多职情况下
//                    if (CollectionUtils.size(userJobDtos) > 1) {
//                        // 前办理人
//                        String priorUser = getPriorUserId(token, userId);
//                        Map<String, Object> data = Maps.newHashMap();
//                        data.put("userId", priorUser);
//                        data.put("jobs", userJobDtos);
//                        data.put("multiselect", jobIdentity.isMultiSelectJob());
//                        throw new MultiJobNotSelectedException(data);
//                    } else if (CollectionUtils.size(userJobDtos) == 1) {
//                        // 仅一个职位
//                        jobId = userJobDtos.get(0).getJobId();
//                    }
//                }
//            }
//        }
//
//        return jobId;
//    }

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
        List<UserOptionElement> userOptionElements = userUnitElement.getUserOptions();
        if (CollectionUtils.isEmpty(userOptionElements)) {
            return Collections.emptyList();
        }

        return resolve(node, token, userOptionElements, participantType, sidGranularity, userUnitElement.getOrgId(), userUnitElement.getBizOrgId());
    }

    /**
     * @param node
     * @param token
     * @param userOptionElements
     * @param participantType
     * @param sidGranularity
     * @param orgId
     * @param bizOrgId
     * @return
     */
    private List<FlowUserSid> resolve(Node node, Token token, List<UserOptionElement> userOptionElements, ParticipantType participantType,
                                      String sidGranularity, String orgId, String bizOrgId) {
        if (CollectionUtils.isEmpty(userOptionElements)) {
            return Collections.emptyList();
        }

        // 申请人
        String creator = token.getFlowInstance().getStartUserId();
        // 申请人身份
        String creatorJobIdentityId = token.getFlowInstance().getStartJobId();
        // 当前办理人
        String userId = token.getTaskData().getUserId();
        // 前办理人ID
        String priorUserId = getPriorUserId(token, userId);

        JobIdentity jobIdentity = flowUserJobIdentityService.getPriorUserJobIdentity(priorUserId, userOptionElements, false, node, token);
        String multiJobFlowType = jobIdentity.getMultiJobFlowType();
        if (StringUtils.isBlank(multiJobFlowType)) {
            multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
        }
        // 获取多职流转的职位选择
        String jobId = jobIdentity.getJobId();

        // 申请人多职流转配置
        JobIdentity creatorJobIdentity = token.getFlowDelegate().getJobIdentity(token.getFlowDelegate().getStartNode().getToID());

        // 办理人多职流转配置
        JobIdentity todoUserJobIdentity = token.getFlowDelegate().getJobIdentity(node.getId());

        // 参与人
        List<String> userIds = new ArrayList<>(0);
        // 获取流程的组织版本
        String[] orgVersionIds = OrgVersionUtils.getFlowOrgVersionIdsAsArray(orgId, token);
        Map<String, List<OrgUserJobDto>> userJobIdentityMap = Maps.newHashMap();

        for (UserOptionElement userOptionElement : userOptionElements) {
            String userOption = userOptionElement.getValue();
            if (StringUtils.isBlank(userOption)) {
                continue;
            }
            Participant participant = Enum.valueOf(Participant.class, userOption.trim());
            switch (participant) {
                // 人员选择
                // 前办理人
                case PriorUser:
                    userIds.add(priorUserId);
                    addUserJobIdentityOfPriorUser(priorUserId, jobId, Sets.newHashSet(priorUserId), todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    break;
                // 前办理人的直接汇报人
                case DirectLeaderOfPriorUser:
                    Set<String> directLeaderListOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        directLeaderListOfPriorUserIds = workflowOrgService.listUserAllJobDirectLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        directLeaderListOfPriorUserIds = workflowOrgService.listUserMainJobDirectLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        directLeaderListOfPriorUserIds = workflowOrgService.listUserJobDirectLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(directLeaderListOfPriorUserIds)) {
                        userIds.addAll(directLeaderListOfPriorUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, directLeaderListOfPriorUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的部门领导
                case DeptLeaderOfPriorUser:
                    Set<String> deptLeaderOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        deptLeaderOfPriorUserIds = workflowOrgService.listUserAllJobDepartmentLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptLeaderOfPriorUserIds = workflowOrgService.listUserMainJobDepartmentLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptLeaderOfPriorUserIds = workflowOrgService.listUserJobDepartmentLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptLeaderOfPriorUserIds)) {
                        userIds.addAll(deptLeaderOfPriorUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, deptLeaderOfPriorUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的上级领导
                case LeaderOfPriorUser:
                    Set<String> leaderOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        leaderOfPriorUserIds = workflowOrgService.listUserAllJobSuperiorLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        leaderOfPriorUserIds = workflowOrgService.listUserMainJobSuperiorLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        leaderOfPriorUserIds = workflowOrgService.listUserJobSuperiorLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(leaderOfPriorUserIds)) {
                        userIds.addAll(leaderOfPriorUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, leaderOfPriorUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的分管领导
                case BranchedLeaderOfPriorUser:
                    Set<String> branchedLeaderOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        branchedLeaderOfPriorUserIds = workflowOrgService.listUserAllJobBranchLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        branchedLeaderOfPriorUserIds = workflowOrgService.listUserMainJobBranchLeader(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        branchedLeaderOfPriorUserIds = workflowOrgService.listUserJobBranchLeader(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(branchedLeaderOfPriorUserIds)) {
                        userIds.addAll(branchedLeaderOfPriorUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, branchedLeaderOfPriorUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的所有上级领导
                case AllLeaderOfPriorUser:
                    Set<String> allLeaderOfPriorUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        allLeaderOfPriorUserIds = workflowOrgService.listUserAllJobLeaderOfAll(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        allLeaderOfPriorUserIds = workflowOrgService.listUserMainJobLeaderOfAll(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        allLeaderOfPriorUserIds = workflowOrgService.listUserJobLeaderOfAll(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(allLeaderOfPriorUserIds)) {
                        userIds.addAll(allLeaderOfPriorUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, allLeaderOfPriorUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的部门人员
                case DeptOfPriorUser:
                    Set<String> deptUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        deptUserIds = workflowOrgService.listUserAllJobDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptUserIds = workflowOrgService.listUserMainJobDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        deptUserIds = workflowOrgService.listUserJobDepartmentUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptUserIds)) {
                        userIds.addAll(deptUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, deptUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的上级部门人员
                case ParentDeptOfPriorUser:
                    Set<String> parentDeptUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        parentDeptUserIds = workflowOrgService.listUserAllJobParentDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        parentDeptUserIds = workflowOrgService.listUserMainJobParentDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        parentDeptUserIds = workflowOrgService.listUserJobParentDepartmentUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(parentDeptUserIds)) {
                        userIds.addAll(parentDeptUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, parentDeptUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的根部门人员
                case RootDeptOfPriorUser:
                    Set<String> rootDeptUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        rootDeptUserIds = workflowOrgService.listUserAllJobRootDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootDeptUserIds = workflowOrgService.listUserMainJobRootDepartmentUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootDeptUserIds = workflowOrgService.listUserJobRootDepartmentUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(rootDeptUserIds)) {
                        userIds.addAll(rootDeptUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, rootDeptUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的根节点人员
                case RootNodeOfPriorUser:
                    Set<String> rootNodeUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        rootNodeUserIds = workflowOrgService.listUserAllJobRootNodeUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootNodeUserIds = workflowOrgService.listUserMainJobRootNodeUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        rootNodeUserIds = workflowOrgService.listUserJobRootNodeUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(rootNodeUserIds)) {
                        userIds.addAll(rootNodeUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, rootNodeUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的单位人员
                case BizUnitOfPriorUser:
                    Set<String> bizUnitUserIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        bizUnitUserIds = workflowOrgService.listUserAllJobUnitUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        bizUnitUserIds = workflowOrgService.listUserMainJobUnitUserId(priorUserId, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        bizUnitUserIds = workflowOrgService.listUserJobUnitUserId(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(bizUnitUserIds)) {
                        userIds.addAll(bizUnitUserIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, bizUnitUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人直接下属
                case SubordinateOfPriorUser:
                    Set<String> subordinateIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> allJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER);
                        subordinateIds = workflowOrgService.listUserSubordinateUserIds(priorUserId, allJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> mainJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER);
                        subordinateIds = workflowOrgService.listUserSubordinateUserIds(priorUserId, mainJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        subordinateIds = workflowOrgService.listUserSubordinateUserIds(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(subordinateIds)) {
                        userIds.addAll(subordinateIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, subordinateIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的所有下属
                case AllSubordinateOfPriorUser:
                    Set<String> allSubordinateIds = null;
                    if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> allJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER);
                        allSubordinateIds = workflowOrgService.listUserAllSubordinateUserIds(priorUserId, allJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        List<OrgUserEntity.Type> mainJobTypes = Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER);
                        allSubordinateIds = workflowOrgService.listUserAllSubordinateUserIds(priorUserId, mainJobTypes, orgVersionIds);
                    } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
                        allSubordinateIds = workflowOrgService.listUserAllSubordinateUserIds(priorUserId, jobId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(allSubordinateIds)) {
                        userIds.addAll(allSubordinateIds);
                        addUserJobIdentityOfPriorUser(priorUserId, jobId, allSubordinateIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前一个环节办理人
                case PriorTaskUser:
                    List<String> priorTaskUserIds = getPriorTaskUsers(node, token);
                    userIds.addAll(priorTaskUserIds);
                    addUserJobIdentityOfPriorUser(priorUserId, jobId, priorTaskUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    break;
                // 申请人
                case Creator:
                    userIds.add(creator);
                    addUserJobIdentityOfCreator(creator, creatorJobIdentityId, Sets.newHashSet(creator), todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    break;
                // 申请人的直接汇报人
                case DirectLeaderOfCreator:
                    Set<String> directLeaderListOfCreatorIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        directLeaderListOfCreatorIds = workflowOrgService.listUserAllJobDirectLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        directLeaderListOfCreatorIds = workflowOrgService.listUserMainJobDirectLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        directLeaderListOfCreatorIds = workflowOrgService.listUserJobDirectLeader(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(directLeaderListOfCreatorIds)) {
                        userIds.addAll(directLeaderListOfCreatorIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, directLeaderListOfCreatorIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的部门领导
                case DeptLeaderOfCreator:
                    Set<String> deptLeaderOfCreatorIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        deptLeaderOfCreatorIds = workflowOrgService.listUserAllJobDepartmentLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        deptLeaderOfCreatorIds = workflowOrgService.listUserMainJobDepartmentLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        deptLeaderOfCreatorIds = workflowOrgService.listUserJobDepartmentLeader(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(deptLeaderOfCreatorIds)) {
                        userIds.addAll(deptLeaderOfCreatorIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, deptLeaderOfCreatorIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的上级领导
                case LeaderOfCreator:
                    Set<String> leaderOfCreatorIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        leaderOfCreatorIds = workflowOrgService.listUserAllJobSuperiorLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        leaderOfCreatorIds = workflowOrgService.listUserMainJobSuperiorLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        leaderOfCreatorIds = workflowOrgService.listUserJobSuperiorLeader(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(leaderOfCreatorIds)) {
                        userIds.addAll(leaderOfCreatorIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, leaderOfCreatorIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的分管领导
                case BranchedLeaderOfCreator:
                    Set<String> branchedLeaderOfCreatorIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        branchedLeaderOfCreatorIds = workflowOrgService.listUserAllJobBranchLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        branchedLeaderOfCreatorIds = workflowOrgService.listUserMainJobBranchLeader(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        branchedLeaderOfCreatorIds = workflowOrgService.listUserJobBranchLeader(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(branchedLeaderOfCreatorIds)) {
                        userIds.addAll(branchedLeaderOfCreatorIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, branchedLeaderOfCreatorIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的所有上级领导
                case AllLeaderOfCreator:
                    Set<String> allLeaderOfCreatorIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        allLeaderOfCreatorIds = workflowOrgService.listUserAllJobLeaderOfAll(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        allLeaderOfCreatorIds = workflowOrgService.listUserMainJobLeaderOfAll(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        allLeaderOfCreatorIds = workflowOrgService.listUserJobLeaderOfAll(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(allLeaderOfCreatorIds)) {
                        userIds.addAll(allLeaderOfCreatorIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, allLeaderOfCreatorIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的部门人员
                case DeptOfCreator:
                    // 获取申请人的部门ID
                    Set<String> creatorDeptUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        creatorDeptUserIds = workflowOrgService.listUserAllJobDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        creatorDeptUserIds = workflowOrgService.listUserMainJobDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        creatorDeptUserIds = workflowOrgService.listUserJobDepartmentUserId(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(creatorDeptUserIds)) {
                        userIds.addAll(creatorDeptUserIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, creatorDeptUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的上级部门人员
                case ParentDeptOfCreator:
                    Set<String> creatorParentDeptUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        creatorParentDeptUserIds = workflowOrgService.listUserAllJobParentDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        creatorParentDeptUserIds = workflowOrgService.listUserMainJobParentDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        creatorParentDeptUserIds = workflowOrgService.listUserJobParentDepartmentUserId(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(creatorParentDeptUserIds)) {
                        userIds.addAll(creatorParentDeptUserIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, creatorParentDeptUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的根部门人员
                case RootDeptOfCreator:
                    Set<String> creatorRootDeptUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        creatorRootDeptUserIds = workflowOrgService.listUserAllJobRootDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        creatorRootDeptUserIds = workflowOrgService.listUserMainJobRootDepartmentUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        creatorRootDeptUserIds = workflowOrgService.listUserJobRootDepartmentUserId(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(creatorRootDeptUserIds)) {
                        userIds.addAll(creatorRootDeptUserIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, creatorRootDeptUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的根节点人员
                case RootNodeOfCreator:
                    Set<String> creatorRootNodeUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        creatorRootNodeUserIds = workflowOrgService.listUserAllJobRootNodeUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        creatorRootNodeUserIds = workflowOrgService.listUserMainJobRootNodeUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        creatorRootNodeUserIds = workflowOrgService.listUserJobRootNodeUserId(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(creatorRootNodeUserIds)) {
                        userIds.addAll(creatorRootNodeUserIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, creatorRootNodeUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的单位人员
                case BizUnitOfCreator:
                    Set<String> creatorBizUnitUserIds = null;
                    if (creatorJobIdentity.isUserAllJob()) {
                        creatorBizUnitUserIds = workflowOrgService.listUserAllJobUnitUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserMainJob()) {
                        creatorBizUnitUserIds = workflowOrgService.listUserMainJobUnitUserId(creator, orgVersionIds);
                    } else if (creatorJobIdentity.isUserSelectJob()) {
                        creatorBizUnitUserIds = workflowOrgService.listUserJobUnitUserId(creator, creatorJobIdentityId, orgVersionIds);
                    }
                    if (CollectionUtils.isNotEmpty(creatorBizUnitUserIds)) {
                        userIds.addAll(creatorBizUnitUserIds);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, creatorBizUnitUserIds, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
//                // 业务组织
//                // 前办理人的同业务角色人员
//                case BizRoleOfPriorUser:
//                    Set<String> sameBizRoleUserId = workflowOrgService.listUserSameBizRoleUserId(priorUserId, orgVersionIds);
//                    if (CollectionUtils.isNotEmpty(sameBizRoleUserId)) {
//                        userIds.addAll(sameBizRoleUserId);
//                    }
//                    break;
//                // 申请人的同业务角色人员
//                case BizRoleOfCreator:
//                    Set<String> creatorSameBizRoleUserId = workflowOrgService.listUserSameBizRoleUserId(creator, orgVersionIds);
//                    if (CollectionUtils.isNotEmpty(creatorSameBizRoleUserId)) {
//                        userIds.addAll(creatorSameBizRoleUserId);
//                    }
//                    break;
                default:
                    break;
            }
        }

        // token.getTaskData().setTaskOptionUnitUserIds(node.getId(), optionUnitUsers);
        List<FlowUserSid> userSids = sidGranularityResolver.resolve(node, token, userIds, sidGranularity);
        if (ParticipantType.TodoUser.equals(participantType)) {
            setUserJobIdentity(userSids, userJobIdentityMap);
        }

        return userSids;
    }


    /**
     * @param userSids
     * @param userJobIdentityMap
     */
    private void setUserJobIdentity(List<FlowUserSid> userSids, Map<String, List<OrgUserJobDto>> userJobIdentityMap) {
        for (FlowUserSid userSid : userSids) {
            if (userJobIdentityMap.containsKey(userSid.getId())) {
                userSid.setOrgUserJobDtos(userJobIdentityMap.get(userSid.getId()));
            }
        }
    }

    /**
     * @param priorUserId
     * @param jobId
     * @param userIds
     * @param userJobIdentityMap
     * @param token
     * @param participantType
     * @param orgVersionIds
     */
    public void addUserJobIdentityOfPriorUser(String priorUserId, String jobId, Collection<String> userIds, JobIdentity jobIdentity,
                                              Map<String, List<OrgUserJobDto>> userJobIdentityMap, Token token, ParticipantType participantType, String... orgVersionIds) {
        if (!ParticipantType.TodoUser.equals(participantType) || CollectionUtils.isEmpty(userIds) || jobIdentity.isUserAllJob()) {
            return;
        }

        if (StringUtils.isBlank(jobId) || StringUtils.contains(jobId, "all")) {
            return;
        }

        addUserJobIdentity(priorUserId, jobId, userIds, userJobIdentityMap, token, orgVersionIds);
    }

    /**
     * @param creator
     * @param userIds
     * @param userJobIdentityMap
     * @param token
     * @param participantType
     * @param orgVersionIds
     */
    public void addUserJobIdentityOfCreator(String creator, String creatorJobId, Collection<String> userIds, JobIdentity jobIdentity, Map<String, List<OrgUserJobDto>> userJobIdentityMap,
                                            Token token, ParticipantType participantType, String... orgVersionIds) {
        if (!ParticipantType.TodoUser.equals(participantType) || CollectionUtils.isEmpty(userIds) || !jobIdentity.isUserSelectJob()) {
            return;
        }

        if (StringUtils.isBlank(creatorJobId) || StringUtils.contains(creatorJobId, "all")) {
            return;
        }

        addUserJobIdentity(creator, creatorJobId, userIds, userJobIdentityMap, token, orgVersionIds);
    }

    /**
     * @param filterUserId
     * @param jobId
     * @param userIds
     * @param userJobIdentityMap
     * @param token
     * @param orgVersionIds
     */
    private void addUserJobIdentity(String filterUserId, String jobId, Collection<String> userIds, Map<String,
            List<OrgUserJobDto>> userJobIdentityMap, Token token, String[] orgVersionIds) {
        List<String> jobIds = Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue()));

        List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobIdentity(filterUserId, orgVersionIds);
        Set<String> bizRoleMatches = Sets.newHashSet();
        List<OrgUserJobDto> filterJobDtos = userJobDtos.stream().filter(orgUserJobDto -> {
                    String filterJobId = orgUserJobDto.getJobId();
                    if (jobIds.contains(filterJobId)) {
                        return true;
                    }
                    // 业务角色匹配
                    for (String identityId : jobIds) {
                        if (StringUtils.endsWith(identityId, "/*") && StringUtils.contains(filterJobId, "/")) {
                            if (StringUtils.startsWith(identityId, StringUtils.substringBefore(filterJobId, "/"))) {
                                bizRoleMatches.add(StringUtils.substringBefore(identityId, "/"));
                                return true;
                            }
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(filterJobDtos)) {
            return;
        }

        for (String userId : userIds) {
            if (userJobIdentityMap.containsKey(userId)) {
                continue;
            }
            if (StringUtils.equals(filterUserId, userId)) {
                userJobIdentityMap.put(userId, filterJobDtos);
                continue;
            }

            List<OrgUserJobDto> matchUserJobDtos = workflowOrgService.listUserJobIdentity(userId, orgVersionIds);
            matchUserJobDtos = matchUserJobDtos.stream().filter(orgUserJobDto -> {
                for (OrgUserJobDto filterJobDto : filterJobDtos) {
                    if (isMatchUserJobIdentity(orgUserJobDto, filterJobDto, bizRoleMatches)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());
            userJobIdentityMap.put(userId, matchUserJobDtos);
        }
    }

    /**
     * @param orgUserJobDto
     * @param filterJobDto
     * @return
     */
    private boolean isMatchUserJobIdentity(OrgUserJobDto orgUserJobDto, OrgUserJobDto filterJobDto, Set<String> bizRoleMatches) {
        String jobIdPath = orgUserJobDto.getJobIdPath();
        String filterIdPath = filterJobDto.getJobIdPath();

        // 身份路径匹配
        boolean match = FlowUserJobIdentityService.isMatchJobPath(jobIdPath, filterIdPath, true);
        if (match) {
            return true;
        }

        // 业务角色模糊匹配
        if (CollectionUtils.isNotEmpty(bizRoleMatches) && StringUtils.contains(orgUserJobDto.getJobId(), "/")) {
            return bizRoleMatches.contains(StringUtils.substringBefore(orgUserJobDto.getJobId(), "/"));
        }

        return false;
    }

}
