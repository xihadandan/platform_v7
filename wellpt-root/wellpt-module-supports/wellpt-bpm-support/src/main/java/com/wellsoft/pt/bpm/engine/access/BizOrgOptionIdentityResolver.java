/*
 * @(#)12/13/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserOptionElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 12/13/24.1	    zhulh		12/13/24		    Create
 * </pre>
 * @date 12/13/24
 */
@Component
public class BizOrgOptionIdentityResolver extends AbstractIdentityResolver {

    @Autowired
    private BizOrgSidGranularityResolver bizOrgSidGranularityResolver;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    @Autowired
    private OptionIdentityResolver optionIdentityResolver;

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

    @Override
    public List<FlowUserSid> resolve(Node node, Token token, UserUnitElement userUnitElement, ParticipantType participantType, String sidGranularity) {
        List<UserOptionElement> userOptionElements = userUnitElement.getUserOptions();
        if (CollectionUtils.isEmpty(userOptionElements)) {
            return Collections.emptyList();
        }

        String bizOrgId = OrgVersionUtils.getAvailableBizOrgId(userUnitElement.getBizOrgId(), token);
        // 申请人
        String creator = token.getFlowInstance().getStartUserId();
        // 申请人身份
        String creatorJobIdentityId = token.getFlowInstance().getStartJobId();
        // 当前办理人
        String userId = token.getTaskData().getUserId();
        // 参与人
        List<String> userIds = new ArrayList<>(0);
        // 前办理人ID
        String priorUserId = getPriorUserId(token, userId);
        // 前办理人多职流转信息
        JobIdentity jobIdentity = flowUserJobIdentityService.getPriorUserJobIdentity(priorUserId, userOptionElements, false, node, token);
        String priorUserJobIdentityId = jobIdentity.getJobId();

        // 办理人多职流转配置
        JobIdentity todoUserJobIdentity = token.getFlowDelegate().getJobIdentity(node.getId());
        // 获取流程的组织版本
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        Map<String, List<OrgUserJobDto>> userJobIdentityMap = Maps.newHashMap();

        // 处理参与人选项
        for (UserOptionElement userOptionElement : userOptionElements) {
            String userOption = userOptionElement.getValue();
            if (StringUtils.isBlank(userOption)) {
                continue;
            }

            Participant participant = Enum.valueOf(Participant.class, userOption.trim());
            switch (participant) {
                // 前办理人
                case PriorUser:
                    userIds.add(priorUserId);
                    break;
                // 前办理人的同业务项人员
                case BizItemOfPriorUser:
                    Set<String> bizItemOfPriorUsers = workflowOrgService.listUserBizItemAndBizRoleUserId(priorUserId, null, bizOrgId);
                    userIds.addAll(bizItemOfPriorUsers);
                    break;
                // 前办理人同业务项的指定角色人员
                case RoleUserOfBizItemOfPriorUser:
                    List<String> bizItemOfPriorUserRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfBizItemOfPriorUsers = workflowOrgService.listUserBizItemAndBizRoleUserId(priorUserId, bizItemOfPriorUserRoleIds, bizOrgId);
                    userIds.addAll(roleUserOfBizItemOfPriorUsers);
                    break;
                // 前办理人的同部门人员
                case DeptOfPriorUser:
                    Set<String> deptOfPriorUsers = workflowOrgService.listUserBizDepartmentUserId(priorUserId, priorUserJobIdentityId, false, bizOrgId);
                    if (CollectionUtils.isNotEmpty(deptOfPriorUsers)) {
                        userIds.addAll(deptOfPriorUsers);
                        String deptJobIdentity = extractDeptJobIdentity(priorUserJobIdentityId);
                        addUserJobIdentityOfPriorUser(priorUserId, deptJobIdentity, Sets.newHashSet(priorUserId), jobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的同部门同角色人员
                case DeptAndBizRoleOfPriorUser:
                    Set<String> deptAndBizRoleOfPriorUsers = workflowOrgService.listUserBizDepartmentUserId(priorUserId, priorUserJobIdentityId, true, bizOrgId);
                    if (CollectionUtils.isNotEmpty(deptAndBizRoleOfPriorUsers)) {
                        userIds.addAll(deptAndBizRoleOfPriorUsers);
                        addUserJobIdentityOfPriorUser(priorUserId, priorUserJobIdentityId, deptAndBizRoleOfPriorUsers, jobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人同部门的指定角色人员
                case RoleUserOfDeptOfPriorUser:
                    List<String> deptAndBizRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfDeptOfPriorUsers = workflowOrgService.listUserBizDepartmentAndBizRoleUserId(priorUserId, deptAndBizRoleIds, bizOrgId);
                    userIds.addAll(roleUserOfDeptOfPriorUsers);
                    break;
                // 前办理人的上级部门人员
                case ParentDeptOfPriorUser:
                    Set<String> parentDeptOfPriorUsers = workflowOrgService.listUserBizParentDepartmentUserId(priorUserId, priorUserJobIdentityId, bizOrgId);
                    userIds.addAll(parentDeptOfPriorUsers);
                    break;
                // 前办理人上级部门的指定角色人员
                case RoleUserOfParentDeptOfPriorUser:
                    List<String> parentDeptOfPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfParentDeptOfPriorUsers = workflowOrgService.listUserBizParentDepartmentAndBizRoleUserId(priorUserId, priorUserJobIdentityId, parentDeptOfPriorRoleIds, bizOrgId);
                    if (CollectionUtils.isNotEmpty(roleUserOfParentDeptOfPriorUsers)) {
                        userIds.addAll(roleUserOfParentDeptOfPriorUsers);
                        addUserJobIdentityOfPriorUser(priorUserId, priorUserJobIdentityId, roleUserOfParentDeptOfPriorUsers, jobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的根部门人员
                case RootDeptOfPriorUser:
                    Set<String> rootDeptOfPriorUsers = workflowOrgService.listUserBizRootDepartmentUserId(priorUserId, priorUserJobIdentityId, bizOrgId);
                    userIds.addAll(rootDeptOfPriorUsers);
                    break;
                // 前办理人根部门的指定角色人员
                case RoleUserOfRootDeptOfPriorUser:
                    List<String> rootDeptOfPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfRootDeptOfPriorUsers = workflowOrgService.listUserBizRootDepartmentAndBizRoleUserId(priorUserId, priorUserJobIdentityId, rootDeptOfPriorRoleIds, bizOrgId);
                    if (CollectionUtils.isNotEmpty(roleUserOfRootDeptOfPriorUsers)) {
                        userIds.addAll(roleUserOfRootDeptOfPriorUsers);
                        addUserJobIdentityOfPriorUser(priorUserId, priorUserJobIdentityId, roleUserOfRootDeptOfPriorUsers, jobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 前办理人的根节点人员
                case RootNodeOfPriorUser:
                    Set<String> rootNodeOfPriorUsers = workflowOrgService.listUserBizRootNodeUserId(priorUserId, bizOrgId);
                    userIds.addAll(rootNodeOfPriorUsers);
                    break;
                // 前办理人根节点的指定角色人员
                case RoleUserOfRootNodeOfPriorUser:
                    List<String> rootNodeOfPriorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfRootNodeOfPriorUsers = workflowOrgService.listUserBizRootNodeAndBizRoleUserId(priorUserId, rootNodeOfPriorRoleIds, bizOrgId);
                    userIds.addAll(roleUserOfRootNodeOfPriorUsers);
                    break;
                // 前办理人的同角色人员
                case BizRoleOfPriorUser:
                    Set<String> bizRoleOfPriorUsers = workflowOrgService.listUserBizRoleUserId(priorUserId, bizOrgId);
                    userIds.addAll(bizRoleOfPriorUsers);
                    break;
                // 申请人
                case Creator:
                    userIds.add(creator);
                    break;
                // 申请人的同业务项人员
                case BizItemOfCreator:
                    Set<String> bizItemOfCreator = workflowOrgService.listUserBizItemAndBizRoleUserId(creator, null, bizOrgId);
                    userIds.addAll(bizItemOfCreator);
                    break;
                // 申请人同业务项的指定角色人员
                case RoleUserOfBizItemOfCreator:
                    List<String> bizItemOfCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfBizItemOfCreators = workflowOrgService.listUserBizItemAndBizRoleUserId(creator, bizItemOfCreatorRoleIds, bizOrgId);
                    userIds.addAll(roleUserOfBizItemOfCreators);
                    break;
                // 申请人的同部门人员
                case DeptOfCreator:
                    Set<String> deptOfCreators = workflowOrgService.listUserBizDepartmentUserId(creator, creatorJobIdentityId, false, bizOrgId);
                    if (CollectionUtils.isNotEmpty(deptOfCreators)) {
                        userIds.addAll(deptOfCreators);
                        String deptJobIdentity = extractDeptJobIdentity(creatorJobIdentityId);
                        addUserJobIdentityOfCreator(creator, deptJobIdentity, deptOfCreators, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的同部门同角色人员
                case DeptAndBizRoleOfCreator:
                    Set<String> deptAndBizRoleOfCreators = workflowOrgService.listUserBizDepartmentUserId(creator, creatorJobIdentityId, true, bizOrgId);
                    if (CollectionUtils.isNotEmpty(deptAndBizRoleOfCreators)) {
                        userIds.addAll(deptAndBizRoleOfCreators);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, deptAndBizRoleOfCreators, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人同部门的指定角色人员
                case RoleUserOfDeptOfCreator:
                    List<String> deptAndBizOfCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfDeptOfCreators = workflowOrgService.listUserBizDepartmentAndBizRoleUserId(creator, deptAndBizOfCreatorRoleIds, bizOrgId);
                    userIds.addAll(roleUserOfDeptOfCreators);
                    break;
                // 申请人的上级部门人员
                case ParentDeptOfCreator:
                    Set<String> parentDeptOfCreators = workflowOrgService.listUserBizParentDepartmentUserId(creator, creatorJobIdentityId, bizOrgId);
                    userIds.addAll(parentDeptOfCreators);
                    break;
                // 申请人上级部门的指定角色人员
                case RoleUserOfParentDeptOfCreator:
                    List<String> parentDeptOfCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfParentDeptOfCreators = workflowOrgService.listUserBizParentDepartmentAndBizRoleUserId(creator, creatorJobIdentityId, parentDeptOfCreatorRoleIds, bizOrgId);
                    if (CollectionUtils.isNotEmpty(roleUserOfParentDeptOfCreators)) {
                        userIds.addAll(roleUserOfParentDeptOfCreators);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, roleUserOfParentDeptOfCreators, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的根部门人员
                case RootDeptOfCreator:
                    Set<String> rootDeptOfCreators = workflowOrgService.listUserBizRootDepartmentUserId(creator, creatorJobIdentityId, bizOrgId);
                    if (CollectionUtils.isNotEmpty(rootDeptOfCreators)) {
                        userIds.addAll(rootDeptOfCreators);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, rootDeptOfCreators, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人根部门的指定角色人员
                case RoleUserOfRootDeptOfCreator:
                    List<String> rootDeptOfCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> rootDeptOfCreatorRoleUsers = workflowOrgService.listUserBizRootDepartmentAndBizRoleUserId(creator, creatorJobIdentityId, rootDeptOfCreatorRoleIds, bizOrgId);
                    if (CollectionUtils.isNotEmpty(rootDeptOfCreatorRoleUsers)) {
                        userIds.addAll(rootDeptOfCreatorRoleUsers);
                        addUserJobIdentityOfCreator(creator, creatorJobIdentityId, rootDeptOfCreatorRoleUsers, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
                    }
                    break;
                // 申请人的根节点人员
                case RootNodeOfCreator:
                    Set<String> rootNodeOfCreators = workflowOrgService.listUserBizRootNodeUserId(creator, bizOrgId);
                    userIds.addAll(rootNodeOfCreators);
                    break;
                // 申请人根节点的指定角色人员
                case RoleUserOfRootNodeOfCreator:
                    List<String> rootNodeOfCreatorRoleIds = Arrays.asList(StringUtils.split(userOptionElement.getBizRoleId(), Separator.SEMICOLON.getValue()));
                    Set<String> roleUserOfRootNodeOfCreators = workflowOrgService.listUserBizRootNodeAndBizRoleUserId(creator, rootNodeOfCreatorRoleIds, bizOrgId);
                    userIds.addAll(roleUserOfRootNodeOfCreators);
                    break;
                // 申请人的同角色人员
                case BizRoleOfCreator:
                    Set<String> bizRoleOfCreators = workflowOrgService.listUserBizRoleUserId(creator, bizOrgId);
                    userIds.addAll(bizRoleOfCreators);
                    break;
            }
        }

        List<FlowUserSid> userSids = bizOrgSidGranularityResolver.resolve(node, token, userIds, sidGranularity, bizOrgId);
        if (ParticipantType.TodoUser.equals(participantType)) {
            setUserJobIdentity(userSids, userJobIdentityMap);
        }
        return userSids;
    }

    private String extractDeptJobIdentity(String creatorJobIdentityId) {
        if (StringUtils.isBlank(creatorJobIdentityId)) {
            return creatorJobIdentityId;
        }

        List<String> deptJobIdentities = Lists.newArrayList();
        String[] identities = StringUtils.split(creatorJobIdentityId, Separator.SEMICOLON.getValue());
        for (String identity : identities) {
            if (StringUtils.contains(identity, Separator.SLASH.getValue())) {
                deptJobIdentities.add(StringUtils.split(identity, Separator.SLASH.getValue())[0] + Separator.SLASH.getValue() + Separator.ASTERISK.getValue());
            } else {
                deptJobIdentities.add(identity);
            }
        }
        return StringUtils.join(deptJobIdentities, Separator.SEMICOLON.getValue());
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
     * @param creator
     * @param deptOfCreators
     * @param todoUserJobIdentity
     * @param userJobIdentityMap
     * @param token
     * @param participantType
     * @param orgVersionIds
     */
    private void addUserJobIdentityOfCreator(String creator, String creatorJobIdentityId, Set<String> deptOfCreators, JobIdentity todoUserJobIdentity, Map<String, List<OrgUserJobDto>> userJobIdentityMap, Token token, ParticipantType participantType, String[] orgVersionIds) {
        optionIdentityResolver.addUserJobIdentityOfCreator(creator, creatorJobIdentityId, deptOfCreators, todoUserJobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
    }

    /**
     * @param priorUserId
     * @param priorJobId
     * @param userIds
     * @param jobIdentity
     * @param userJobIdentityMap
     * @param token
     * @param participantType
     * @param orgVersionIds
     */
    private void addUserJobIdentityOfPriorUser(String priorUserId, String priorJobId, Collection<String> userIds, JobIdentity jobIdentity,
                                               Map<String, List<OrgUserJobDto>> userJobIdentityMap, Token token, ParticipantType participantType, String... orgVersionIds) {
        optionIdentityResolver.addUserJobIdentityOfPriorUser(priorUserId, priorJobId, userIds, jobIdentity, userJobIdentityMap, token, participantType, orgVersionIds);
    }

}
