/*
 * @(#)2018年9月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月13日.1	zhulh		2018年9月13日		Create
 * </pre>
 * @date 2018年9月13日
 */
public abstract class AbstractIdentityResolver implements IdentityResolver {

    /**
     * 解析出指定节点的用户标识
     *
     * @param node
     * @param token
     * @param raws
     * @param participantType
     * @return
     */
    public List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType) {
        String sidGranularity = SidGranularity.USER;
        if (node != null && token != null) {
            sidGranularity = token.getFlowDelegate().getGranularity(node.getId(), token.getTaskData(), Collections.emptyList());
        }
        return this.resolve(node, token, raws, participantType, sidGranularity);
    }

    /**
     * 解析出指定节点的用户标识
     *
     * @param node
     * @param token
     * @param userUnitElement
     * @param participantType
     * @param sidGranularity
     * @return
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, UserUnitElement userUnitElement, ParticipantType participantType, String sidGranularity) {
        String value = userUnitElement.getValue();
        if (StringUtils.isBlank(value)) {
            return Collections.emptyList();
        }
        List<String> raws = Arrays.asList(StringUtils.split(value, Separator.SEMICOLON.getValue()));
        return this.resolve(node, token, raws, participantType, sidGranularity);
    }

    /**
     * 获取前办理人用户ID，如果是委托待办则返回委托人，否则返回当前用户
     *
     * @param token
     * @param currentUserId
     * @return
     */
    protected String getPriorUserId(Token token, String currentUserId) {
        TaskInstance taskInstance = token.getTask();
        if (taskInstance == null) {
            return currentUserId;
        }
        String key = taskInstance.getUuid() + currentUserId;
        String taskIdentityUuid = token.getTaskData().getTaskIdentityUuid(key);
        if (StringUtils.isBlank(taskIdentityUuid)) {
            return currentUserId;
        }
        IdentityService identityService = ApplicationContextHolder.getBean(IdentityService.class);
        TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
        if (taskIdentity == null) {
            return currentUserId;
        }
        // 委托提交
        if (IdPrefix.startsUser(taskIdentity.getUserId()) && !StringUtils.equals(currentUserId, taskIdentity.getUserId())) {
            String currentUserTaskIdentityUuid = token.getTaskData().getTaskIdentityUuid(taskIdentityUuid);
            if (StringUtils.isNotBlank(currentUserTaskIdentityUuid)) {
                taskIdentity = identityService.get(currentUserTaskIdentityUuid);
            }
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
