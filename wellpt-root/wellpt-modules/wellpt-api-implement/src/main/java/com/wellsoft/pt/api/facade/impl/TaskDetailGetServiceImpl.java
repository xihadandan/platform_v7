/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.google.common.collect.Lists;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.Task;
import com.wellsoft.pt.api.domain.TaskDetail;
import com.wellsoft.pt.api.domain.TaskDetail.Button;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskDetailGetRequest;
import com.wellsoft.pt.api.response.TaskDetailGetResponse;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperationComparator;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
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
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_DETAIL_GET)
@Transactional
public class TaskDetailGetServiceImpl implements WellptService<TaskDetailGetRequest> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskService taskService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private AclService aclService;

    @Autowired
    private TaskGetServiceImpl taskGetService;

    @Autowired
    private UserDetailsServiceProvider userDetailsServiceProvider;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskDetailGetRequest taskDetailGetRequest) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        TaskDetail taskDetail = new TaskDetail();
        // 以用户名方式登录加载授权信息并退出虚拟登录
        UserDetails userDetails = null;
        try {
            userDetails = userNameLoginIfRequire();

            String taskInstUuid = taskDetailGetRequest.getUuid();
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);

            // 环节信息
            Task task = taskGetService.getTask(taskInstUuid);

            // 动态表单数据
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(taskInstance.getFormUuid(),
                    taskInstance.getDataUuid());

            // 操作按钮
            List<Button> buttons = getButtons(userId, taskInstance);

            BeanUtils.copyProperties(task, taskDetail);
            taskDetail.setFormData(dyFormData.getFormDataOfMainform());
            taskDetail.setButtons(buttons);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            userNameLogoutIfRequire(userDetails);
        }

        TaskDetailGetResponse response = new TaskDetailGetResponse();
        response.setData(taskDetail);

        return response;
    }

    /**
     * 以用户名方式登录加载授权信息并退出虚拟登录
     */
    private UserDetails userNameLoginIfRequire() {
        if (!IgnoreLoginUtils.isIgnoreLogin()) {
            return null;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && (authentication.getPrincipal() instanceof UserDetails)) {
            return null;
        }

        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String loginName = userDetails.getLoginName();
        userDetails = userDetailsServiceProvider.getUserDetails(loginName);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails,
                StringUtils.EMPTY, userDetails.getAuthorities());
        auth.setDetails(userDetails.getPassword());
        IgnoreLoginUtils.logout();
        SecurityContextHolder.getContext().setAuthentication(auth);
        return userDetails;
    }

    /**
     * @param userNameLogin
     */
    private void userNameLogoutIfRequire(UserDetails userDetails) {
        if (userDetails == null) {
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(null);
        try {
            IgnoreLoginUtils.login(userDetails);
        } catch (Exception e) {
        }
    }

    /**
     * @param userId
     * @param taskInstance
     */
    private List<Button> getButtons(String userId, TaskInstance taskInstance) {
        String taskInstUuid = taskInstance.getUuid();
        List<Button> buttons = Lists.newArrayList();
        List<Permission> permissions = aclService.getPermission(taskInstance, userId);
        // 待办
        if (hasPermission(AclPermission.TODO, permissions)) {
            // 保存
            buttons.add(getButton(WorkFlowPrivilege.Save));
            // 提交
            buttons.add(getButton(WorkFlowPrivilege.Submit));
            // 直接退回
            if (taskService.isAllowedDirectRollbackToTask(userId, taskInstUuid)) {
                buttons.add(getButton(WorkFlowPrivilege.DirectRollback));
            }
            // 退回
            if (taskService.isAllowedRollbackToTask(userId, taskInstUuid)) {
                buttons.add(getButton(WorkFlowPrivilege.Rollback));
            }
            // 转办
            if (taskService.isAllowedTransfer(userId, taskInstUuid)) {
                buttons.add(getButton(WorkFlowPrivilege.Transfer));
            }
            // 会签
            if (taskService.isAllowedCounterSign(userId, taskInstUuid)) {
                buttons.add(getButton(WorkFlowPrivilege.CounterSign));
            }
        } else if (hasPermission(AclPermission.TODO, permissions)) {
            // 已办
            // 撤回
            if (taskService.isAllowedCancel(userId, taskInstUuid)) {
                buttons.add(getButton(WorkFlowPrivilege.Cancel));
            }
        }
        // 抄送
        if (taskService.isAllowedCopyTo(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.CopyTo));
        }
        // 关注
        if (taskService.isAllowedAttention(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.Attention));
        }
        // 取消关注
        if (taskService.isAllowedUnfollow(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.Unfollow));
        }
        // 催办
        if (taskService.isAllowedRemind(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.Remind));
        }
        // 特送个人
        if (taskService.isAllowedHandOver(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.HandOver));
        }
        // 特送环节
        if (taskService.isAllowedGotoTask(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.GotoTask));
        }
        // 挂起
        if (taskService.isAllowedSuspend(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.Suspend));
        }
        // 恢复
        if (taskService.isAllowedResume(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.Resume));
        }
        // 删除
        if (taskService.isAllowedDelete(userId, taskInstUuid)) {
            buttons.add(getButton(WorkFlowPrivilege.Delete));
        }
        Collections.sort(buttons, new Comparator<Button>() {

            @Override
            public int compare(Button o1, Button o2) {
                return Integer.valueOf(o1.getOrder()).compareTo(o2.getOrder());
            }

        });
        return buttons;
    }

    /**
     * @param workFlowPrivilege
     * @return
     */
    private Button getButton(WorkFlowPrivilege workFlowPrivilege) {
        return new Button(workFlowPrivilege.getCode(), workFlowPrivilege.getName(),
                WorkFlowOperationComparator.getOrder(workFlowPrivilege.getCode()));
    }

    /**
     * @param checkPermission
     * @param permissions
     * @return
     */
    private boolean hasPermission(Permission checkPermission, List<Permission> permissions) {
        for (Permission permission : permissions) {
            if (checkPermission.getMask() == permission.getMask()) {
                return true;
            }
        }
        return false;
    }

}
