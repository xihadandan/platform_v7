/*
 * @(#)2/4/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.UserOptionElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.exception.MultiJobNotSelectedException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.wellsoft.pt.bpm.engine.enums.Participant.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2/4/25.1	    zhulh		2/4/25		    Create
 * </pre>
 * @date 2/4/25
 */
@Service
public class FlowUserJobIdentityServiceImpl implements FlowUserJobIdentityService {

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private IdentityService identityService;

    private List<Participant> filterPriorParticipants = Lists.newArrayList(SameDeptAsPrior, SameRootDeptAsPrior, SameRootNodeAsPrior,
            SameBizUnitAsPrior, SameDirectLeaderAsPrior, SameDeptLeaderAsPrior, SameLeaderAsPrior, SameBranchLeaderAsPrior,
            SameAllLeaderAsPrior, SameSubordinateOfPrior, SameAllSubordinateOfPrior,
            SameBizItemAsPrior, SameRoleUserOfBizItemAsPrior, SameDeptAsPrior, SameDeptAndBizRoleAsPrior,
            SameRoleUserOfDeptAsPrior, SameParentDeptAsPrior, SameRoleUserOfParentDeptAsPrior, SameRootDeptAsPrior,
            SameRoleUserOfRootDeptAsPrior, SameRootNodeAsPrior, SameRoleUserOfRootNodeAsPrior, SameBizRoleAsPrior);

    private List<Participant> optionPriorParticipants = Lists.newArrayList(PriorUser, DirectLeaderOfPriorUser, DeptLeaderOfPriorUser, LeaderOfPriorUser,
            BranchedLeaderOfPriorUser, AllLeaderOfPriorUser, DeptOfPriorUser, ParentDeptOfPriorUser,
            RootDeptOfPriorUser, RootNodeOfPriorUser, BizUnitOfPriorUser, SubordinateOfPriorUser, AllSubordinateOfPriorUser,
            BizItemOfPriorUser, RoleUserOfBizItemOfPriorUser, DeptOfPriorUser, DeptAndBizRoleOfPriorUser,
            RoleUserOfDeptOfPriorUser, ParentDeptOfPriorUser, RoleUserOfParentDeptOfPriorUser, RootDeptOfPriorUser,
            RoleUserOfRootDeptOfPriorUser, RootNodeOfPriorUser, RoleUserOfRootNodeOfPriorUser, BizRoleOfPriorUser);

    @Override
    public FlowUserSid getStartUserSid(String userId, TaskData taskData, Node node, FlowDelegate flowDelegate, Token token) {
        JobIdentity jobIdentity = flowDelegate.getJobIdentity(node.getId());
        List<OrgUserJobDto> userJobDtos = null;
        // 选择具体身份流转
        if (jobIdentity.isUserSelectJob()) {
            userJobDtos = listUserJobIdentity(userId, token);
            String jobField = jobIdentity.getJobField();
            if (jobIdentity.isSelectJobField() && StringUtils.isNotBlank(jobField)) {
                // 职位根据职位字段获取
                String jobFieldValue = (String) (taskData.getDyFormData(taskData.getDataUuid()).getFieldValue(jobField));
                String jobId = StringUtils.isNotBlank(jobFieldValue) ? jobFieldValue : null;
                OrgUserJobDto orgUserJobDto = userJobDtos.stream().filter(orgUserJob -> StringUtils.equals(orgUserJob.getJobId(), jobId))
                        .findFirst().orElse(null);
                if (orgUserJobDto != null) {
                    userJobDtos = Lists.newArrayList(orgUserJobDto);
                } else {
                    // 获取不到身份时，以选择身份流转
                    userJobDtos = getJobSelected(taskData, userJobDtos, jobIdentity);
                }
            } else {
                userJobDtos = getJobSelected(taskData, userJobDtos, jobIdentity);
            }
        } else if (jobIdentity.isUserMainJob()) {
            userJobDtos = selectUserMainJob(userId, jobIdentity, taskData, token);
        } else {
            // 以全部身份流转
            userJobDtos = Lists.newArrayList();
        }
        return new FlowUserSid(userId, taskData.getUserName(), userJobDtos);
    }

    private List<OrgUserJobDto> selectUserMainJob(String userId, JobIdentity jobIdentity, TaskData taskData, Token token) {
        List<OrgUserJobDto> userJobDtos = listUserJobIdentity(userId, token);
        // 以主身份流转
        OrgUserJobDto orgUserJobDto = userJobDtos.stream().filter(orgUserJob -> orgUserJob.isPrimary()).findFirst().orElse(null);
        if (orgUserJobDto != null) {
            userJobDtos = Lists.newArrayList(orgUserJobDto);
        } else if (jobIdentity.isUserSelectJobWhileMainJobNotFound()) {
            // 获取不到主身份时，选择具体身份
            if (token.getTask() != null) {
                // 委托人委托时找不到主身份，返回空
                Integer actionCode = taskData.getActionCode(token.getTask().getUuid());
                if (ActionCode.DELEGATION.getCode().equals(actionCode)
                        || ActionCode.TAKE_BACK_TODO_DELEGATION.getCode().equals(actionCode)) {
                    return Lists.newArrayList();
                }
            }
            userJobDtos = getJobSelected(taskData, userJobDtos, jobIdentity);
        } else {
            // 获取不到主身份时，以全部身份流转
            userJobDtos = Lists.newArrayList();
        }
        return userJobDtos;
    }

    /**
     * @param userId
     * @param token
     * @return
     */
    private List<OrgUserJobDto> listUserJobIdentity(String userId, Token token) {
        TaskData taskData = token.getTaskData();
        List<OrgUserJobDto> orgUserJobDtos = (List<OrgUserJobDto>) taskData.get("userJobIdentities_" + userId);
        if (orgUserJobDtos == null) {
            orgUserJobDtos = workflowOrgService.listUserJobIdentity(userId, token);
            taskData.put("userJobIdentities_" + userId, orgUserJobDtos);
        }
        return orgUserJobDtos;
    }

    /**
     * @param taskData
     * @param userJobDtos
     * @param jobIdentity
     * @return
     */
    private List<OrgUserJobDto> getJobSelected(TaskData taskData, List<OrgUserJobDto> userJobDtos, JobIdentity jobIdentity) {
        List<OrgUserJobDto> retUserJobDtos = userJobDtos;
        String jobSelected = taskData.getJobSelected(taskData.getUserId());
        if (StringUtils.isNotBlank(jobSelected)) {
            List<String> selectedJobIds = Arrays.asList(StringUtils.split(jobSelected, Separator.SEMICOLON.getValue()));
            retUserJobDtos = userJobDtos.stream().filter(orgUserJob -> selectedJobIds.contains(orgUserJob.getJobId()))
                    .collect(Collectors.toList());
        } else if (CollectionUtils.size(userJobDtos) > 1) {
            Map<String, Object> data = Maps.newHashMap();
            data.put("taskInstUuid", taskData.getTaskInstUuid());
            data.put("jobs", userJobDtos);
            data.put("multiselect", jobIdentity.isMultiSelectJob());
            throw new MultiJobNotSelectedException(data);
        } else {
            // 仅一个职位
        }
        return retUserJobDtos;
    }

    @Override
    public void addUnitUserJobIdentity(List<FlowUserSid> userSids, String idPath, String taskId, Token token, ParticipantType participantType) {
        if (CollectionUtils.isEmpty(userSids) || StringUtils.isBlank(idPath) || !ParticipantType.TodoUser.equals(participantType)) {
            return;
        }

        List<String> idPaths = Lists.newArrayList(StringUtils.split(idPath, Separator.SEMICOLON.getValue()));

        addUnitUserJobIdentity(userSids, idPaths, false, taskId, token, participantType);
    }

    @Override
    public void addUnitUserJobIdentity(List<FlowUserSid> userSids, List<String> idPaths, boolean switchPath, String taskId, Token token, ParticipantType participantType) {
        JobIdentity jobIdentity = token.getFlowDelegate().getJobIdentity(taskId);
        if (jobIdentity.isUserSelectJob()) {
            Set<String> jobIdentityPaths = idPaths.stream().map(idPath -> getJobIdentityPath(idPath)).collect(Collectors.toSet());
            Map<String, String> userPaths = getUserPathMap(idPaths);

            String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
            for (FlowUserSid userSid : userSids) {
                if (!StringUtils.startsWith(userSid.getId(), IdPrefix.USER.getValue())) {
                    continue;
                }

                List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobIdentity(userSid.getId(), orgVersionIds);
                Set<OrgUserJobDto> jobIdentityDtos = Sets.newLinkedHashSet();
                String userPath = userPaths.get(userSid.getId());
                if (StringUtils.isNotBlank(userPath)) {
                    addFlowUserJobIdentityWithPath(userSid, userPath, true, switchPath, orgUserJobDtos, jobIdentityDtos, token);
                } else {
                    for (String path : jobIdentityPaths) {
                        addFlowUserJobIdentityWithPath(userSid, path, false, switchPath, orgUserJobDtos, jobIdentityDtos, token);
                    }
                }
                // userSid.setOrgUserJobDtos(Lists.newArrayList(jobIdentityDtos));
                userSid.addOrgUserJobDtos(jobIdentityDtos);
            }
        } else if (jobIdentity.isUserMainJob()) {
            String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
            for (FlowUserSid userSid : userSids) {
                if (!StringUtils.startsWith(userSid.getId(), IdPrefix.USER.getValue())) {
                    continue;
                }
                List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobs(userSid.getId(), orgVersionIds);
                if (CollectionUtils.isNotEmpty(orgUserJobDtos)) {
                    userSid.addOrgUserJobDtos(orgUserJobDtos.stream().filter(orgUserJobDto -> orgUserJobDto.isPrimary()).collect(Collectors.toList()));
                }
            }
        }
    }

    /**
     * @param userSid
     * @param path
     * @param orgUserJobDtos
     * @param jobIdentityDtos
     * @param token
     */
    private void addFlowUserJobIdentityWithPath(FlowUserSid userSid, String path, boolean isUserPath, boolean switchPath,
                                                List<OrgUserJobDto> orgUserJobDtos, Set<OrgUserJobDto> jobIdentityDtos, Token token) {
        List<OrgUserJobDto> jobDtos = null;
        // 处理群组路径
        if (isGroupPath(path)) {
            Set<OrgUserJobDto> groupJobDtos = Sets.newLinkedHashSet();
            String groupId = getGroupId(path);
            List<OrgGroupMemberEntity> groupMemberEntities = getUserGroupMember(userSid.getId(), groupId, token);
            for (OrgGroupMemberEntity groupMemberEntity : groupMemberEntities) {
                if (groupMemberEntity == null && StringUtils.isBlank(groupMemberEntity.getMemberIdPath())) {
                    continue;
                }
                String memberIdPath = groupMemberEntity.getMemberIdPath();
                String jobIdentityPath = getJobIdentityPath(memberIdPath);
                groupJobDtos.addAll(orgUserJobDtos.stream().filter(orgUserJobDto -> FlowUserJobIdentityService.isMatchJobPath(orgUserJobDto.getJobIdPath(), jobIdentityPath, switchPath)).collect(Collectors.toList()));
            }
            jobDtos = Lists.newArrayList(groupJobDtos);
        } else {
            // 组织路径
            String jobIdentityPath = isUserPath ? getJobIdentityPath(path) : path;
            jobDtos = orgUserJobDtos.stream().filter(orgUserJobDto -> FlowUserJobIdentityService.isMatchJobPath(orgUserJobDto.getJobIdPath(), jobIdentityPath, switchPath)).collect(Collectors.toList());
        }
        if (CollectionUtils.isNotEmpty(jobDtos)) {
            jobIdentityDtos.addAll(jobDtos);
        }
    }

    @Override
    public void addUserJobIdentity(Map<FlowUserSid, String> flowUserIdentityMap) {
        if (MapUtils.isEmpty(flowUserIdentityMap)) {
            return;
        }
        List<String> identityUuids = Lists.newArrayList(flowUserIdentityMap.values());
        List<TaskIdentity> taskIdentities = identityService.listByUuids(identityUuids);
        Map<String, TaskIdentity> taskIdentityMap = taskIdentities.stream().collect(Collectors.toMap(TaskIdentity::getUuid, taskIdentity -> taskIdentity));
        for (Map.Entry<FlowUserSid, String> entry : flowUserIdentityMap.entrySet()) {
            FlowUserSid flowUserSid = entry.getKey();
            String identityUuid = entry.getValue();
            TaskIdentity taskIdentity = taskIdentityMap.get(identityUuid);
            if (taskIdentity != null) {
                addFlowUserJobIdentity(flowUserSid, taskIdentity);
            }
        }
    }

    @Override
    public void addUserJobIdentityByJobIds(Set<FlowUserSid> userSidSet, List<String> jobIds, Token token, ParticipantType participantType) {
        if (CollectionUtils.isEmpty(userSidSet) || CollectionUtils.isEmpty(jobIds) || !ParticipantType.TodoUser.equals(participantType)) {
            return;
        }

        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        for (FlowUserSid userSid : userSidSet) {
            if (!StringUtils.startsWith(userSid.getId(), IdPrefix.USER.getValue())) {
                continue;
            }

            List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobs(userSid.getId(), orgVersionIds);
            orgUserJobDtos = orgUserJobDtos.stream().filter(orgUserJobDto -> jobIds.contains(orgUserJobDto.getJobId())).collect(Collectors.toList());
            userSid.addOrgUserJobDtos(orgUserJobDtos);
        }
    }

    @Override
    public void addUserMainJobIdentity(Set<FlowUserSid> userSidSet, Token token, ParticipantType participantType) {
        if (CollectionUtils.isEmpty(userSidSet) || !ParticipantType.TodoUser.equals(participantType)) {
            return;
        }

        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        for (FlowUserSid userSid : userSidSet) {
            if (!StringUtils.startsWith(userSid.getId(), IdPrefix.USER.getValue())) {
                continue;
            }
            List<OrgUserJobDto> orgUserJobDtos = workflowOrgService.listUserJobIdentity(userSid.getId(), orgVersionIds);
            if (CollectionUtils.isNotEmpty(orgUserJobDtos)) {
                userSid.addOrgUserJobDtos(Lists.newArrayList(orgUserJobDtos.get(0)));
            }
        }
    }

    private List<OrgGroupMemberEntity> getUserGroupMember(String userId, String groupId, Token token) {
        List<OrgGroupMemberEntity> groupMemberEntities = workflowOrgService.listMemberByGroupId(groupId);
        return groupMemberEntities.stream().filter(orgGroupMemberEntity -> {
            String memberId = orgGroupMemberEntity.getMemberId();
            if (StringUtils.equals(memberId, userId)) {
                return true;
            }
            if (!StringUtils.startsWith(memberId, IdPrefix.USER.getValue())) {
                return workflowOrgService.isMemberOf(userId, memberId, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
            }
            return false;
        }).collect(Collectors.toList());
    }

    private Map<String, String> getUserPathMap(List<String> idPaths) {
        Map<String, String> pathMap = Maps.newHashMap();
        for (String idPath : idPaths) {
            List<String> ids = Lists.newArrayList(StringUtils.split(idPath, Separator.SLASH.getValue()));
            if (CollectionUtils.size(ids) > 0) {
                pathMap.put(ids.get(ids.size() - 1), idPath);
            } else {
                pathMap.put(idPath, idPath);
            }
        }
        return pathMap;
    }

    private String getJobIdentityPath(String path) {
        List<String> ids = Lists.newArrayList(StringUtils.split(path, Separator.SLASH.getValue()));
        if (StringUtils.startsWith(ids.get(ids.size() - 1), IdPrefix.USER.getValue())) {
            ids.remove(ids.size() - 1);
        }
        String jobIdPath = StringUtils.join(ids, Separator.SLASH.getValue());
        return jobIdPath;
    }

    private boolean isGroupPath(String path) {
        String[] ids = StringUtils.split(path, Separator.SLASH.getValue());
        return StringUtils.startsWith(ids[ids.length - 1], IdPrefix.GROUP.getValue());
    }

    private String getGroupId(String path) {
        String[] ids = StringUtils.split(path, Separator.SLASH.getValue());
        return ids[ids.length - 1];
    }

    @Override
    public JobIdentity getPriorUserJobIdentity(String priorUserId, List<UserOptionElement> userOptionElements,
                                               boolean isFilter, Node node, Token token) {
        String preTaskId = token.getTask() != null ? token.getTask().getId() : node.getId();
        if (StringUtils.isBlank(preTaskId)) {
            preTaskId = node.getId();
        }
        JobIdentity jobIdentity = token.getFlowDelegate().getJobIdentity(preTaskId);
        String jobId = null;
        // 获取多职流转的职位选择
        if (jobIdentity.isUserSelectJob()) {
            List<Participant> priorParticipants = isFilter ? filterPriorParticipants : optionPriorParticipants;
            for (UserOptionElement userOptionElement : userOptionElements) {
                if (StringUtils.isBlank(userOptionElement.getValue())) {
                    continue;
                }
                Participant participant = Enum.valueOf(Participant.class, userOptionElement.getValue().trim());
                // 只有计算“前办理人的部门/领导”等选项才需要职位ID
                if (priorParticipants.contains(participant)) {
                    // 获取多职流转的职位选择;
                    jobId = getUserSelectJobId(priorUserId, node, token);
                    break;
                }
            }
        } else if (jobIdentity.isUserMainJob()) {
            // 以主身份流转
            List<OrgUserJobDto> userJobDtos = listUserJobIdentity(priorUserId, token);
            OrgUserJobDto orgUserJobDto = userJobDtos.stream().filter(orgUserJob -> orgUserJob.isPrimary()).findFirst().orElse(null);
            if (orgUserJobDto != null) {
                jobId = orgUserJobDto.getJobId();
            } else if (jobIdentity.isUserSelectJobWhileMainJobNotFound()) {
                // 获取不到主身份时，选择具体身份
                jobId = getJobSelected(token.getTaskData(), userJobDtos, jobIdentity).stream().map(OrgUserJobDto::getJobId)
                        .collect(Collectors.joining(Separator.SEMICOLON.getValue()));
                jobIdentity.setMultiJobFlowType(FlowDefConstants.FLOW_BY_USER_SELECT_JOB);
            } else {
                // 获取不到主身份时，以全部身份流转
                jobIdentity.setMultiJobFlowType(FlowDefConstants.FLOW_BY_USER_ALL_JOBS);
            }
        }
        jobIdentity.setJobId(jobId);
        return jobIdentity;
    }

    @Override
    public List<FlowUserSid> getFlowUserSids(List<TaskIdentity> taskIdentities) {
        List<FlowUserSid> flowUserSids = Lists.newArrayList();
        List<String> userIds = taskIdentities.stream().map(TaskIdentity::getUserId).distinct().collect(Collectors.toList());
        Map<String, String> userNames = workflowOrgService.getNamesByIds(userIds);
        for (TaskIdentity taskIdentity : taskIdentities) {
            FlowUserSid flowUserSid = new FlowUserSid(taskIdentity.getUserId(), userNames.get(taskIdentity.getUserId()));
            addFlowUserJobIdentity(flowUserSid, taskIdentity);
            flowUserSids.add(flowUserSid);
        }
        return flowUserSids;
    }

    /**
     * @param flowUserSid
     * @param taskIdentity
     */
    private void addFlowUserJobIdentity(FlowUserSid flowUserSid, TaskIdentity taskIdentity) {
        String identityId = taskIdentity.getIdentityId();
        String identityIdPath = taskIdentity.getIdentityIdPath();
        if (StringUtils.isNotBlank(identityId) && StringUtils.isNotBlank(identityIdPath)) {
            List<OrgUserJobDto> orgUserJobDtos = Lists.newArrayList();
            String[] identityIds = StringUtils.split(identityId, Separator.SEMICOLON.getValue());
            String[] identityIdPaths = StringUtils.split(identityIdPath, Separator.SEMICOLON.getValue());
            if (identityIds.length == identityIdPaths.length) {
                for (int i = 0; i < identityIds.length; i++) {
                    String jobId = identityIds[i];
                    String jobIdPath = identityIdPaths[i];
                    OrgUserJobDto orgUserJobDto = new OrgUserJobDto();
                    orgUserJobDto.setJobId(jobId);
                    orgUserJobDto.setJobIdPath(jobIdPath);
                    orgUserJobDtos.add(orgUserJobDto);
                }
            }
            flowUserSid.setOrgUserJobDtos(orgUserJobDtos);
        }
    }

    /**
     * @param userId
     * @param taskIdentityUuid
     * @param taskData
     * @return
     */
    public List<OrgUserJobDto> getUserJobIdentities(String userId, String taskInstUuid, String taskIdentityUuid, boolean defaultEmpty,
                                                    boolean fetchAll, TaskData taskData) {
        List<OrgUserJobDto> userJobDtos = Lists.newArrayList();
        Token token = taskData.getToken();
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            // TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
            List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuidAndUserIds(taskInstUuid, PermissionGranularityUtils.getUserSids(userId));
            TaskIdentity taskIdentity = taskIdentities.stream().filter(identity -> StringUtils.equals(identity.getUuid(), taskIdentityUuid)).findFirst().orElse(null);
            if (taskIdentity == null) {
                taskIdentity = identityService.get(taskIdentityUuid);
                if (taskIdentity != null) {
                    taskIdentities = Lists.newArrayList(taskIdentity);
                }
            }
            if (taskIdentity != null && IdPrefix.startsUser(taskIdentity.getUserId())) {
                String jobId = taskIdentity.getIdentityId();
                if (StringUtils.isNotBlank(jobId)) {
                    List<String> jobIds = Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue()));
                    userJobDtos.addAll(listUserJobIdentity(userId, token).stream().filter(orgUserJobDto -> jobIds.contains(orgUserJobDto.getJobId()))
                            .collect(Collectors.toList()));
                } else if (fetchAll) {
                    userJobDtos.addAll(listUserJobIdentity(userId, token));
                }
            }

            List<TaskIdentity> sidTaskIdentities = taskIdentities.stream().filter(identity -> !IdPrefix.startsUser(identity.getUserId())).collect(Collectors.toList());
            sidTaskIdentities.forEach(sidIdentity -> {
                if (StringUtils.startsWith(sidIdentity.getUserId(), IdPrefix.GROUP.getValue())) {
                    // 群组权限粒度的人员身份
                    String groupId = sidIdentity.getUserId();
                    List<OrgGroupMemberEntity> groupMemberEntities = getUserGroupMember(userId, groupId, token);
                    for (OrgGroupMemberEntity groupMemberEntity : groupMemberEntities) {
                        if (groupMemberEntity == null && StringUtils.isBlank(groupMemberEntity.getMemberIdPath())) {
                            continue;
                        }
                        String memberIdPath = groupMemberEntity.getMemberIdPath();
                        String jobIdentityPath = getJobIdentityPath(memberIdPath);
                        List<OrgUserJobDto> orgUserJobDtos = listUserJobIdentity(userId, token);
                        userJobDtos.addAll(orgUserJobDtos.stream().filter(orgUserJobDto -> StringUtils.startsWith(orgUserJobDto.getJobIdPath(), jobIdentityPath))
                                .collect(Collectors.toList()));
                    }
                } else {
                    // 部门、职位等权限粒度的人员身份
                    if (!defaultEmpty) {
                        List<OrgUserJobDto> orgUserJobDtos = listUserJobIdentity(userId, token);
                        String deptId = sidIdentity.getUserId();
                        userJobDtos.addAll(orgUserJobDtos.stream().filter(orgUserJobDto -> StringUtils.contains(orgUserJobDto.getJobIdPath(), deptId))
                                .collect(Collectors.toList()));
                    }
                }
            });
        }
        return userJobDtos;
    }

    @Override
    public List<OrgUserJobDto> getUserOperateJobIdentity(String userId, TaskInstance taskInstance, String taskIdentityUuid, TaskData taskData) {
        List<OrgUserJobDto> orgUserJobDtos = null;
        String taskInstUuid = taskInstance.getUuid();
        Token token = taskData.getToken();
        String jobId = taskData.getJobSelected(userId);
        if (StringUtils.isBlank(jobId)) {
            jobId = taskData.getUserJobIdentityId(userId, taskInstUuid);
        }

        if (token == null) {
            token = new Token(taskInstance, taskData);
        }

        String jobUserId = userId;
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
            if (taskIdentity != null && WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType())) {
                jobUserId = taskIdentity.getOwnerId();
            }
        }

        if (StringUtils.isBlank(jobId)) {
            JobIdentity jobIdentity = token.getFlowDelegate().getJobIdentity(token.getTask().getId());
            if (jobIdentity.isUserSelectJob()) {
                orgUserJobDtos = getUserJobIdentities(jobUserId, taskInstUuid, taskIdentityUuid, false, true, taskData);
                if (CollectionUtils.isEmpty(orgUserJobDtos)) {
                    orgUserJobDtos = listUserJobIdentity(jobUserId, token);
                }
            } else if (jobIdentity.isUserMainJob()) {
                orgUserJobDtos = selectUserMainJob(jobUserId, jobIdentity, taskData, token);
            } else {
                orgUserJobDtos = listUserJobIdentity(jobUserId, token);
            }
        } else {
            List<String> jobIds = Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue()));
            orgUserJobDtos = listUserJobIdentity(jobUserId, token);
            orgUserJobDtos = orgUserJobDtos.stream().filter(orgUserJobDto -> jobIds.contains(orgUserJobDto.getJobId())).collect(Collectors.toList());
        }

        return orgUserJobDtos;
    }

    @Override
    public void selectSubmtJobIdentity(TaskInstance taskInstance, TaskIdentity taskIdentity, TaskData taskData, FlowInstance flowInstance) {
        String jobId = null;
        String userId = taskData.getUserId();
        String consignorUserId = userId;
        TaskIdentity sourceTaskIdentity = taskIdentity;
        if (sourceTaskIdentity != null && WorkFlowTodoType.Delegation.equals(sourceTaskIdentity.getTodoType())
                && StringUtils.equals(userId, sourceTaskIdentity.getUserId())) {
            sourceTaskIdentity = getConsignorTaskIdentity(sourceTaskIdentity);
            consignorUserId = sourceTaskIdentity.getUserId();
        }

        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        JobIdentity jobIdentity = flowDelegate.getJobIdentity(taskInstance.getId());
        if (jobIdentity.isUserSelectJob()) {
            jobId = getCompleteActionJobId(userId, taskInstance, taskData);
            if (StringUtils.isBlank(jobId)) {
                jobId = taskData.getJobSelected(userId);
            }
            if (StringUtils.isBlank(jobId)) {
                jobId = taskData.getUserJobIdentityId(userId, taskInstance.getUuid());
            }
            if (StringUtils.isBlank(jobId) && sourceTaskIdentity != null) {
                jobId = sourceTaskIdentity.getIdentityId();
            }
            if (StringUtils.isBlank(jobId)) {
                jobId = getUserSelectJobId(consignorUserId, flowDelegate.getTaskNode(taskInstance.getId()), new Token(taskInstance, taskData));
            }
        } else if (jobIdentity.isUserMainJob()) {
            String todoUserId = sourceTaskIdentity != null ? sourceTaskIdentity.getUserId() : userId;
            if (IdPrefix.startsUser(todoUserId)) {
                List<OrgUserJobDto> orgUserJobDtos = selectUserMainJob(userId, jobIdentity, taskData, new Token(taskInstance, taskData));
                jobId = orgUserJobDtos.stream().map(OrgUserJobDto::getJobId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            } else {
                // 部门、职位等权限粒度的人员身份
                List<OrgUserJobDto> orgUserJobDtos = listUserJobIdentity(userId, new Token(taskInstance, taskData));
                String deptId = sourceTaskIdentity.getUserId();
                jobId = orgUserJobDtos.stream().filter(orgUserJobDto -> StringUtils.contains(orgUserJobDto.getJobIdPath(), deptId))
                        .map(OrgUserJobDto::getJobId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            }
        }
        taskData.setUserJobIdentityId(userId, taskInstance.getUuid(), jobId);
    }

    /**
     * @param userId
     * @param taskInstance
     * @param taskData
     * @return
     */
    private String getCompleteActionJobId(String userId, TaskInstance taskInstance, TaskData taskData) {
        String actionType = taskData.getActionType(taskInstance.getUuid() + userId);
        if (WorkFlowOperation.COMPLETE.equals(actionType)) {
            String decisionMakers = (String) taskData.getCustomData("decisionMakers_" + taskInstance.getId() + "_" + taskInstance.getUuid());
            if (StringUtils.isNotBlank(decisionMakers)) {
                if (StringUtils.startsWith(decisionMakers, "{")) {
                    Map<String, String> valueMap = JsonUtils.json2Object(decisionMakers, Map.class);
                    return valueMap != null ? valueMap.get(userId) : null;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    private TaskIdentity getConsignorTaskIdentity(TaskIdentity taskIdentity) {
        TaskIdentity consignorTaskIdentity = taskIdentity;
        String sourceTaskIdentityUuid = consignorTaskIdentity.getSourceTaskIdentityUuid();
        while (StringUtils.isNotBlank(sourceTaskIdentityUuid)) {
            consignorTaskIdentity = identityService.get(sourceTaskIdentityUuid);
            if (!WorkFlowTodoType.Delegation.equals(consignorTaskIdentity.getTodoType())) {
                break;
            }
            sourceTaskIdentityUuid = consignorTaskIdentity.getSourceTaskIdentityUuid();
        }
        return consignorTaskIdentity;
    }

    /**
     * 多职流转按职位字段选择，获取职位值
     * 1.表单不存在职位字段的情况：
     * 1). 用户多职的情况下，抛异常到前端，让用户选择哪个职位，再提交上来
     * 2). 用户只有一个职位的情况下，直接使用该职位
     * 2.表单存在职位字段，使用职位字段的值
     *
     * @param token
     * @return
     */
    @Override
    public String getUserSelectJobId(String userId, Node node, Token token) {
        TaskData taskData = token.getTaskData();
        String taskInstUuid = taskData.getTaskInstUuid();
        String jobId = taskData.getUserJobIdentityId(userId, taskInstUuid);
        if (StringUtils.isNotBlank(jobId)) {
            return jobId;
        }

        String currentUserId = taskData.getUserId();
        FlowDelegate flowDelegate = token.getFlowDelegate();
        boolean startNewWork = StringUtils.equals(flowDelegate.getStartNode().getToID(), node.getId()) ||
                (token.getFlowInstance() != null ? taskData.getStartNewFlow(token.getFlowInstance().getUuid()) : true);
        JobIdentity jobIdentity = flowDelegate.getJobIdentity(node.getId());
        String jobField = jobIdentity.getJobField();
        // 获取当前用户提交的身份
        if (!startNewWork && !jobIdentity.isTaskScope()) {
            String taskIdentityUuid = taskData.getTaskIdentityUuid(token.getTask().getUuid() + currentUserId);
            // 当前用户进行委托提交，取受托人的身份
            if (!StringUtils.equals(userId, currentUserId)) {
                TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
                if (taskIdentity != null && StringUtils.isNotBlank(taskIdentity.getSourceTaskIdentityUuid())) {
                    taskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
                }
            }
            List<OrgUserJobDto> orgUserJobDtos = getUserJobIdentities(userId, taskInstUuid, taskIdentityUuid, false, false, taskData);
            if (CollectionUtils.size(orgUserJobDtos) > 1) {
                jobId = selectUserJob(userId, orgUserJobDtos, jobIdentity, taskData, token);
                taskData.setUserJobIdentityId(userId, taskInstUuid, jobId);
            } else {
                jobId = orgUserJobDtos.stream().map(OrgUserJobDto::getJobId).collect(Collectors.joining(Separator.SEMICOLON.getValue()));
            }
            if (StringUtils.isBlank(jobId)) {
                orgUserJobDtos = listUserJobIdentity(userId, token);
                jobId = selectUserJob(userId, orgUserJobDtos, jobIdentity, taskData, token);
                taskData.setUserJobIdentityId(userId, taskInstUuid, jobId);
            } else {
                taskData.setUserJobIdentityId(userId, taskInstUuid, jobId);
            }
        } else if (jobIdentity.isUserSelectJob()) {
            // 多职流转按职位字段选择流转的情况
            // 无选择职位字段的情况下，判断多职选择
            if (startNewWork && jobIdentity.isSelectJobField() && StringUtils.isNotBlank(jobField)) {
                // 职位根据职位字段获取
                String jobFieldValue = (String) (taskData.getDyFormData(taskData.getDataUuid()).getFieldValue(jobField));
                jobId = StringUtils.isNotBlank(jobFieldValue) ? jobFieldValue : null;
            } else {
                // 前端选择了职位
                jobId = selectUserJob(userId, null, jobIdentity, taskData, token);
            }
        }
        return jobId;
    }

    /**
     * @param userId
     * @param orgUserJobDtos
     * @param jobIdentity
     * @param taskData
     * @param token
     * @return
     */
    private String selectUserJob(String userId, List<OrgUserJobDto> orgUserJobDtos, JobIdentity jobIdentity,
                                 TaskData taskData, Token token) {
        String jobId = null;
        List<OrgUserJobDto> userJobDtos = orgUserJobDtos;
        // 前端选择了职位
        if (StringUtils.isNotBlank(taskData.getJobSelected(userId))) {
            jobId = taskData.getJobSelected(userId);
        } else {
            // 如果没有配置职位字段，且用户是多职的情况下，则抛出异常，前端进行多职选择提交
            if (userJobDtos == null) {
                userJobDtos = listUserJobIdentity(userId, token);
            }
            // 多职情况下
            if (CollectionUtils.size(userJobDtos) > 1) {
                // 前办理人
                // String priorUser = getPriorUserId(token, userId);
                Map<String, Object> data = Maps.newHashMap();
                //data.put("userId", priorUser);
                data.put("taskInstUuid", taskData.getTaskInstUuid());
                data.put("jobs", userJobDtos);
                data.put("multiselect", jobIdentity.isMultiSelectJob());
                throw new MultiJobNotSelectedException(data);
            } else if (CollectionUtils.size(userJobDtos) == 1) {
                // 仅一个职位
                jobId = userJobDtos.get(0).getJobId();
            }
        }
        return jobId;
    }

}
