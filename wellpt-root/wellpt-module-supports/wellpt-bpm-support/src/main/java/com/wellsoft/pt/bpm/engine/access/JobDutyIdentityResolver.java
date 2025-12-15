/*
 * @(#)12/12/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description: 职等职级人员解析
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
public class JobDutyIdentityResolver extends AbstractIdentityResolver {

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

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
        return Collections.emptyList();
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
        WorkFlowSettings flowSettings = flowSettingService.getWorkFlowSettings();
        if (BooleanUtils.isFalse(flowSettings.isEnabledJobDuty())) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> sidSet = Sets.newLinkedHashSet();
        String jobGrade = userUnitElement.getJobGrade();
        JobIdentity jobIdentity = token.getFlowDelegate().getJobIdentity(node.getId());
        // 职等
        if (userUnitElement.getIsEnabledJobGrade() && StringUtils.isNotBlank(jobGrade)) {
            if (SidGranularity.USER.equals(sidGranularity)) {
                Set<FlowUserSid> userSidSet = Sets.newLinkedHashSet();
                Map<String, String> userMap = workflowOrgService.getUsersByJobGrades(Arrays.asList(StringUtils.split(jobGrade, Separator.SEMICOLON.getValue())),
                        OrgVersionUtils.getFlowOrgVersionIdsAsArray(userUnitElement.getOrgId(), token));
                addUserSids(userMap, SidGranularity.USER, userSidSet);
                sidSet.addAll(userSidSet);
                if (ParticipantType.TodoUser.equals(participantType) && CollectionUtils.isNotEmpty(userSidSet)) {
                    if (jobIdentity.isUserSelectJob()) {
                        List<String> jobIds = workflowOrgService.getJobIdsByJobGrades(Arrays.asList(StringUtils.split(jobGrade, Separator.SEMICOLON.getValue())),
                                OrgVersionUtils.getFlowOrgVersionIdsAsArray(userUnitElement.getOrgId(), token));
                        flowUserJobIdentityService.addUserJobIdentityByJobIds(userSidSet, jobIds, token, participantType);
                    } else if (jobIdentity.isUserMainJob()) {
                        flowUserJobIdentityService.addUserMainJobIdentity(userSidSet, token, participantType);
                    }
                }
            } else {
                Map<String, String> jobMap = workflowOrgService.getJobsByJobGrades(Arrays.asList(StringUtils.split(jobGrade, Separator.SEMICOLON.getValue())),
                        OrgVersionUtils.getFlowOrgVersionIdsAsArray(userUnitElement.getOrgId(), token));
                addUserSids(jobMap, SidGranularity.JOB, sidSet);
            }
        }

        // 职级
        String jobRankId = userUnitElement.getJobRankId();
        if (userUnitElement.getIsEnabledJobRank() && StringUtils.isNotBlank(jobRankId)) {
            if (SidGranularity.USER.equals(sidGranularity)) {
                Set<FlowUserSid> userSidSet = Sets.newLinkedHashSet();
                Map<String, String> userMap = workflowOrgService.getUsersByJobRankIds(Arrays.asList(StringUtils.split(jobRankId, Separator.SEMICOLON.getValue())),
                        OrgVersionUtils.getFlowOrgVersionIdsAsArray(userUnitElement.getOrgId(), token));
                addUserSids(userMap, SidGranularity.USER, userSidSet);
                sidSet.addAll(userSidSet);
                if (ParticipantType.TodoUser.equals(participantType) && CollectionUtils.isNotEmpty(userSidSet)) {
                    if (jobIdentity.isUserSelectJob()) {
                        List<String> jobIds = workflowOrgService.getJobIdsByJobRankIds(Arrays.asList(StringUtils.split(jobRankId, Separator.SEMICOLON.getValue())),
                                OrgVersionUtils.getFlowOrgVersionIdsAsArray(userUnitElement.getOrgId(), token));
                        flowUserJobIdentityService.addUserJobIdentityByJobIds(userSidSet, jobIds, token, participantType);
                    } else if (jobIdentity.isUserMainJob()) {
                        flowUserJobIdentityService.addUserMainJobIdentity(userSidSet, token, participantType);
                    }
                }
            } else {
                Map<String, String> jobMap = workflowOrgService.getJobsByJobRankIds(Arrays.asList(StringUtils.split(jobRankId, Separator.SEMICOLON.getValue())),
                        OrgVersionUtils.getFlowOrgVersionIdsAsArray(userUnitElement.getOrgId(), token));
                addUserSids(jobMap, SidGranularity.JOB, sidSet);
            }
        }

        return Arrays.asList(sidSet.toArray(new FlowUserSid[0]));
    }

    private void addUserSids(Map<String, String> userMap, String sidGranularity, Set<FlowUserSid> sidSet) {
        for (Map.Entry<String, String> entry : userMap.entrySet()) {
            String jobId = entry.getKey();
            if (StringUtils.isNotBlank(jobId)) {
                sidSet.add(new FlowUserSid(jobId, entry.getValue(), sidGranularity));
            }
        }
    }

}
