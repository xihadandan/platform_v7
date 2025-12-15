/*
 * @(#)2/4/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.UserOptionElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface FlowUserJobIdentityService {
    FlowUserSid getStartUserSid(String userId, TaskData taskData, Node node, FlowDelegate flowDelegate, Token token);

    void addUnitUserJobIdentity(List<FlowUserSid> userSids, String idPath, String taskId, Token token, ParticipantType participantType);

    void addUnitUserJobIdentity(List<FlowUserSid> userSids, List<String> idPaths, boolean switchPath, String taskId, Token token, ParticipantType participantType);

    void addUserJobIdentity(Map<FlowUserSid, String> flowUserIdentityMap);

    void addUserJobIdentityByJobIds(Set<FlowUserSid> userSidSet, List<String> jobIds, Token token, ParticipantType participantType);

    void addUserMainJobIdentity(Set<FlowUserSid> userSidSet, Token token, ParticipantType participantType);

    JobIdentity getPriorUserJobIdentity(String priorUserId, List<UserOptionElement> userOptionElements, boolean isFilter, Node node, Token token);

    List<FlowUserSid> getFlowUserSids(List<TaskIdentity> taskIdentities);

    List<OrgUserJobDto> getUserOperateJobIdentity(String userId, TaskInstance taskInstance, String taskIdentityUuid, TaskData taskData);

    void selectSubmtJobIdentity(TaskInstance taskInstance, TaskIdentity taskIdentity, TaskData taskData, FlowInstance flowInstance);

    String getUserSelectJobId(String userId, Node node, Token token);

    /**
     * @param jobIdPath
     * @param filterIdPath
     * @param switchPath
     * @return
     */
    static boolean isMatchJobPath(String jobIdPath, String filterIdPath, boolean switchPath) {
        if (StringUtils.equals(jobIdPath, filterIdPath) || StringUtils.startsWith(jobIdPath, filterIdPath)
                || (switchPath && StringUtils.startsWith(filterIdPath, jobIdPath))) {
            return true;
        }

        boolean isJobPath = isJobPath(jobIdPath);
        boolean isFilterJobPath = isJobPath(filterIdPath);

        // 存在职位信息
        if (isJobPath && isFilterJobPath) {
            String deptPath = StringUtils.substringBeforeLast(jobIdPath, Separator.SLASH.getValue());
            String deptFilterPath = StringUtils.substringBeforeLast(filterIdPath, Separator.SLASH.getValue());
            if (StringUtils.startsWith(deptPath, deptFilterPath) || (switchPath && StringUtils.startsWith(deptFilterPath, deptPath))) {
                return true;
            }
        } else if (isJobPath) {
            String deptPath = StringUtils.substringBeforeLast(jobIdPath, Separator.SLASH.getValue());
            if (StringUtils.startsWith(deptPath, filterIdPath) || (switchPath && StringUtils.startsWith(filterIdPath, deptPath))) {
                return true;
            }
        } else if (isFilterJobPath) {
            String deptFilterPath = StringUtils.substringBeforeLast(filterIdPath, Separator.SLASH.getValue());
            if (StringUtils.startsWith(jobIdPath, deptFilterPath) || (switchPath && StringUtils.startsWith(deptFilterPath, jobIdPath))) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param jobIdPath
     * @return
     */
    static boolean isJobPath(String jobIdPath) {
        if (StringUtils.contains(jobIdPath, Separator.SLASH.getValue())) {
            return StringUtils.startsWith(StringUtils.substringAfterLast(jobIdPath, Separator.SLASH.getValue()), IdPrefix.JOB.getValue());
        }
        return false;
    }
}
