/*
 * @(#)2015-2-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service.impl;

import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.facade.service.MobileWorkService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-27.1	zhulh		2015-2-27		Create
 * </pre>
 * @date 2015-2-27
 */
@Service
@Transactional(readOnly = true)
public class MobileWorkServiceImpl extends WorkServiceImpl implements MobileWorkService {

    // @Autowired
    // private WorkService workService;

    /**
     * (non-Javadoc)
     *
     * @see com.com.wellsoft.pt.workflow.work.facade.service.MobileWorkService#getTodoWorkData(java.lang.String,
     * java.lang.String)
     */
    @Override
    public WorkBean getTodoWorkData(String taskInstUuid, String flowInstUuid) {
        WorkBean workBean = super.getTodo(taskInstUuid, flowInstUuid);
        workBean.setLoadDyFormData(false);
        workBean.setDaemon(true);
        workBean = super.getWorkData(workBean);
        return workBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.com.wellsoft.pt.workflow.work.facade.service.MobileWorkService#getDoneWorkData(java.lang.String,
     * java.lang.String)
     */
    @Override
    public WorkBean getDoneWorkData(String taskInstUuid, String flowInstUuid) {
        WorkBean workBean = super.getDone(taskInstUuid, flowInstUuid);
        workBean.setLoadDyFormData(false);
        workBean = super.getWorkData(workBean);
        return workBean;
    }

    @Override
    @Transactional
    public WorkBean getTodo(String taskInstUuid, String flowInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
                return super.getUnread(taskInstUuid, flowInstUuid, true);
            } else if (taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                return super.getRead(taskInstUuid, flowInstUuid);
            }
            throw new WorkFlowException("没有待办权限");
        }
        WorkBean workBean = super.getTodo(taskInstUuid, flowInstUuid);
        if (StringUtils.isNotBlank(taskInstUuid) && user != null) {
            readMarkerService.markRead(taskInstUuid, user.getUserId());
        }
        return workBean;
    }

    @Override
    @Transactional
    public WorkBean getUnread(String taskInstUuid, String flowInstUuid, boolean openToRead) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                return super.getRead(taskInstUuid, flowInstUuid);
            }
            throw new WorkFlowException("没有权限");
        }
        // 打开流程单据时，标记为已读
        if (StringUtils.isNotBlank(taskInstUuid)) {
            readMarkerService.markRead(taskInstUuid, userId);
        }
        return super.getUnread(taskInstUuid, flowInstUuid, openToRead);
    }

    @Override
    @Transactional
    public WorkBean getRead(String taskInstUuid, String flowInstUuid, boolean openToRead) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)) {
            if (!taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                if (taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
                    return super.getUnread(taskInstUuid, flowInstUuid, true);
                }
                throw new WorkFlowException("没有权限");
            } else {
                aclTaskService.removePermission(taskInstUuid, AclPermission.UNREAD, userId);
            }
        }
        return super.getRead(taskInstUuid, flowInstUuid);
    }

    @Override
    public WorkBean getAttention(String taskInstUuid, String flowInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.ATTENTION)) {
            WorkBean workBean = null;
            TaskInstance entity = new TaskInstance();
            entity.setUuid(taskInstUuid);
            if (taskService.hasSupervisePermission(user, taskInstUuid)) {
                workBean = super.getSupervise(taskInstUuid, flowInstUuid);
            } else if (taskService.hasMonitorPermission(user, taskInstUuid)) {
                workBean = super.getMonitor(taskInstUuid, flowInstUuid);
            } else if (taskService.hasViewPermission(user, taskInstUuid)) {
                workBean = super.getDone(taskInstUuid, flowInstUuid);
            } else {
                throw new WorkFlowException("没有权限");
            }
            return workBean;
        }
        return super.getAttention(taskInstUuid, flowInstUuid);
    }

    @Override
    public WorkBean getOver(String taskInstUuid, String flowInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.DONE)) {
            throw new WorkFlowException("没有权限");
        }
        return super.getOver(taskInstUuid, flowInstUuid);
    }

    @Override
    public WorkBean getSupervise(String taskInstUuid, String flowInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasSupervisePermission(user, taskInstUuid)) {
            throw new WorkFlowException("没有权限");
        }
        return super.getSupervise(taskInstUuid, flowInstUuid);
    }

    @Override
    public WorkBean getMonitor(String taskInstUuid, String flowInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasMonitorPermission(user, taskInstUuid)) {
            throw new WorkFlowException("没有权限");
        }
        return super.getMonitor(taskInstUuid, flowInstUuid);
    }

    @Override
    @Transactional
    public WorkBean getDone(String taskInstUuid, String flowInstUuid) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.DONE)) {
            throw new WorkFlowException("没有权限");
        }
        return super.getDone(taskInstUuid, flowInstUuid);
    }

    @Override
    @Transactional
    public WorkBean getWork(String taskInstUuid, String flowInstUuid) {
        Boolean allowOperate = true;
        String taskIdentityUuid = null, customScriptUrl = null;
        HttpServletRequest request = null;
        if ((request = JsonDataServicesContextHolder.getRequest()) != null) {
            String allowOpe = request.getParameter("allowOperate");
            allowOperate = Boolean.valueOf(allowOpe);
            taskIdentityUuid = request.getParameter("taskIdentityUuid");
            customScriptUrl = request.getParameter("customScriptUrl");
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskInstUuid)) {
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            if (taskInstance != null && taskInstance.getEndTime() != null) {
                taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
                if (taskInstance != null) {
                    taskInstUuid = taskInstance.getUuid();
                } else {
                    WorkBean workBean = super.getDraft(flowInstUuid);
                    addEpRedirectAttributes(request, workBean);
                    return workBean;
                }
            }
        }
        if (StringUtils.isBlank(taskInstUuid) && StringUtils.isNotBlank(flowInstUuid)) {
            List<TaskInstance> unfinishedTasks = taskService.getUnfinishedTasks(flowInstUuid);
            TaskInstance taskInstance = null;
            if (!unfinishedTasks.isEmpty()) {
                taskInstance = unfinishedTasks.get(0);
            } else {
                taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
            }
            if (taskInstance != null) {
                taskInstUuid = taskInstance.getUuid();
            } else {
                WorkBean workBean = super.getDraft(flowInstUuid);
                addEpRedirectAttributes(request, workBean);
                return workBean;
            }
        }
        // 标识为已阅
        if (taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            WorkBean workBean = super.getTodo(taskInstUuid, flowInstUuid);
            addEpRedirectAttributes(request, workBean);
            return workBean;
        }

        WorkBean workBean = null;
        TaskInstance entity = new TaskInstance();
        entity.setUuid(taskInstUuid);
        if (taskService.hasSupervisePermission(user, taskInstUuid)) {
            workBean = super.getSupervise(taskInstUuid, flowInstUuid);
        } else if (taskService.hasMonitorPermission(user, taskInstUuid)) {
            workBean = super.getMonitor(taskInstUuid, flowInstUuid);
        } else if (taskService.hasViewPermission(user, taskInstUuid)) {
            workBean = super.getDone(taskInstUuid, flowInstUuid);
        } else {
            throw new WorkFlowException("没有权限");
        }
        workBean.setTaskIdentityUuid(taskIdentityUuid);
        readMarkerService.markRead(taskInstUuid, userId);

        // 加载自定义扩展JS
        if (StringUtils.isNotBlank(customScriptUrl)) {
            workBean.addExtraParam("custom_script_url", customScriptUrl);
        }
        // 附加参数
        addEpRedirectAttributes(request, workBean);
        // 不允许操作清空默认具有的操作按钮
        if (!allowOperate) {
            workBean.setButtons(new ArrayList<CustomDynamicButton>(0));
        }
        return workBean;
    }

    /**
     * 如何描述该方法
     *
     * @param request
     * @param redirectAttributes
     */
    protected void addEpRedirectAttributes(HttpServletRequest request, WorkBean workBean) {
        if (request == null || request.getParameterMap() == null) {
            return;
        }
        Map<String, String[]> paramMap = request.getParameterMap();
        // 附加参数
        for (String paramName : paramMap.keySet()) {
            if (paramName.startsWith("ep_")) {
                String paramValue = request.getParameter(paramName);
                workBean.addExtraParam(paramName, paramValue);
            }
        }
    }

}
