/*
 * @(#)2016-10-25 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.web;

import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlow;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.security.acl.service.AclConstants;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.bean.WorkProcessBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import com.wellsoft.pt.workflow.work.web.WorkController;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
 * 2016-10-25.1	zhulh		2016-10-25		Create
 * </pre>
 * @date 2016-10-25
 */
@Controller
@RequestMapping("/workflow/work/v53")
public class WorkV53Controller extends WorkController {

    protected String workViewV53 = "/workflow/work/work_view_v53";
    private String defaultWorkView = "/workflow/work/work_view";
    @Autowired
    private WorkService workService;

    @Autowired
    private ReadMarkerService readMarkerService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private AclService aclService;

    @Autowired
    private FlowPermissionEvaluator flowPermissionEvaluator;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.BaseController#forward(java.lang.String)
     */
    @Override
    protected String forward(String viewName) {
        if (StringUtils.equals(viewName, defaultWorkView)) {
            return super.forward(workViewV53);
        }
        return super.forward(viewName);
    }

    @Override
    @RequestMapping(value = "/view/flow/process", method = RequestMethod.GET)
    public String viewFlowProcess(@RequestParam(value = "flowInstUuid", required = true) String flowInstUuid,
                                  Model model) {
        List<WorkProcessBean> workProcesses = workService.getWorkProcess(flowInstUuid);
        model.addAttribute("flowInstUuid", flowInstUuid);
        model.addAttribute("workProcesses", workProcesses);
        return forward("/workflow/work/work_view_process_v53");
    }

    @RequestMapping(value = "/view/flow/mobile/process", method = RequestMethod.GET)
    public String viewFlowProcessExt(@RequestParam(value = "flowInstUuid", required = true) String flowInstUuid,
                                     @RequestParam(value = "isMobileApp", required = false) Boolean isMobileApp, Model model) {
        List<WorkProcessBean> workProcesses = workService.getWorkProcess(flowInstUuid);
        model.addAttribute("isMobileApp", isMobileApp);
        model.addAttribute("flowInstUuid", flowInstUuid);
        model.addAttribute("workProcesses", workProcesses);
        return forward("/workflow/work/work_view_process_mobile_v53");
    }

    /**
     * 查看待办的工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/todo", method = RequestMethod.GET)
    public String viewTodo(
            HttpServletRequest request,
            @RequestParam(value = "taskUuid", defaultValue = StringUtils.EMPTY, required = false) String taskUuid,
            @RequestParam(value = "taskInstUuid", defaultValue = StringUtils.EMPTY, required = false) String taskInstUuid,
            @RequestParam(value = "taskIdentityUuid", defaultValue = StringUtils.EMPTY, required = false) String taskIdentityUuid,
            @RequestParam(value = "flowInstUuid", defaultValue = StringUtils.EMPTY, required = false) String flowInstUuid,
            @RequestParam(value = "openToRead", defaultValue = "true") boolean openToRead,
            @RequestParam(value = "custom_script_url", defaultValue = StringUtils.EMPTY, required = false) String customScriptUrl,
            @RequestParam(value = "auto_submit", defaultValue = StringUtils.EMPTY, required = false) String auto_submit,
            @RequestParam(value = "sameUserSubmitTaskOperationUuid", required = false) String sameUserSubmitTaskOperationUuid,
            Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
                return redirect("/workflow/work/v53/view/unread?taskInstUuid=" + taskInstUuid + "&openToRead="
                        + openToRead);
            } else if (taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                return redirect("/workflow/work/v53/view/read?taskInstUuid=" + taskInstUuid);
            }
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        // 打开流程单据时，标记为已读
        if (StringUtils.isNotBlank(taskInstUuid)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
                taskService.markRead(taskInstUuid);
            }
            readMarkerService.markRead(taskInstUuid, userId);
        }

        WorkBean workBean = workService.getTodo(taskInstUuid, flowInstUuid);
        // 当前任务办理人标识
        workBean.setTaskIdentityUuid(taskIdentityUuid);

        // 加载自定义扩展JS
        if (StringUtils.isNotBlank(customScriptUrl)) {
            workBean.addExtraParam("custom_script_url", customScriptUrl);
        }
        Map<String, String[]> paramMap = request.getParameterMap();
        // 附加参数
        for (String paramName : paramMap.keySet()) {
            if (paramName.startsWith("ep_")) {
                String paramValue = request.getParameter(paramName);
                workBean.addExtraParam(paramName, paramValue);
            }
        }
        workBean.addExtraParam("auto_submit", auto_submit);

        // 与前环节相同，获取办理意见
        if (StringUtils.isNotBlank(sameUserSubmitTaskOperationUuid)) {
            TaskOperation taskOperation = workService.getTaskOperation(sameUserSubmitTaskOperationUuid);
            if (taskOperation != null) {
                workBean.setOpinionLabel(taskOperation.getOpinionLabel());
                workBean.setOpinionValue(taskOperation.getOpinionValue());
                workBean.setOpinionText(taskOperation.getOpinionText());
            }
        }

        model.addAttribute(workBean);
        return forward(workViewV53);
    }

    /**
     * 查看未阅的工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/unread", method = RequestMethod.GET)
    public String viewUnread(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                             @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                             @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid,
                             @RequestParam(value = "openToRead", defaultValue = "true") boolean openToRead, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                return redirect("/workflow/work/v53/view/read?taskInstUuid=" + taskInstUuid);
            }
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getUnread(taskInstUuid, flowInstUuid, openToRead);
        // 打开流程单据时，标记为已读
        if (StringUtils.isNotBlank(taskInstUuid)) {
            readMarkerService.markRead(taskInstUuid, user.getUserId());
        }
        model.addAttribute(workBean);
        return forward(workViewV53);
    }

    /**
     * 查看已读的工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/read", method = RequestMethod.GET)
    public String viewRead(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                           @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                           @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)) {
            if (!taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                if (taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
                    return redirect("/workflow/work/v53/view/unread?taskInstUuid=" + taskInstUuid);
                }
                return forward(AclConstants.DEFAULT_ERROR_VIEW);
            } else {
                aclService.removePermission(TaskInstance.class, taskInstUuid, AclPermission.UNREAD, userId);
                aclService.addPermission(TaskInstance.class, taskInstUuid, AclPermission.FLAG_READ, userId);
                readMarkerService.markRead(taskInstUuid, userId);
            }
        }

        WorkBean workBean = workService.getRead(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward(workViewV53);
    }

    /**
     * 任务关注
     *
     * @param workBean
     * @param result
     * @return
     */
    @RequestMapping(value = "/view/attention", method = RequestMethod.GET)
    public String viewAttention(
            @RequestParam(value = "taskUuid", required = false) String taskUuid,
            @RequestParam(value = "taskInstUuid", defaultValue = StringUtils.EMPTY, required = false) String taskInstUuid,
            @RequestParam(value = "flowInstUuid", defaultValue = StringUtils.EMPTY, required = false) String flowInstUuid,
            Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.ATTENTION)) {
            return redirect("/workflow/work/v53/view/work?taskInstUuid=" + taskInstUuid + "&flowInstUuid="
                    + flowInstUuid);
        }

        WorkBean workBean = workService.getAttention(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/work_view");
    }

    /**
     * 查看工作包含从草稿、待办、已办中查看
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/work", method = RequestMethod.GET)
    public String viewWork(
            HttpServletRequest request,
            @RequestParam(value = "taskInstUuid", defaultValue = StringUtils.EMPTY, required = false) String taskInstUuid,
            @RequestParam(value = "taskIdentityUuid", defaultValue = StringUtils.EMPTY, required = false) String taskIdentityUuid,
            @RequestParam(value = "flowInstUuid", defaultValue = StringUtils.EMPTY, required = false) String flowInstUuid,
            @RequestParam(value = "approveFlowInstUuid", defaultValue = StringUtils.EMPTY, required = false) String approveFlowInstUuid,
            @RequestParam(value = "openToRead", defaultValue = "true") boolean openToRead,
            @RequestParam(value = "custom_script_url", defaultValue = StringUtils.EMPTY, required = false) String customScriptUrl,
            @RequestParam(value = "allowOperate", required = false, defaultValue = "true") boolean allowOperate,
            @RequestParam(value = "auto_submit", defaultValue = StringUtils.EMPTY, required = false) String auto_submit,
            @RequestParam(value = "_requestCode", defaultValue = StringUtils.EMPTY, required = false) String requestCode,
            Model model, final RedirectAttributes redirectAttributes) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        String requestCodeParamUri = "&_requestCode=" + requestCode;
        if (StringUtils.isNotBlank(taskInstUuid)) {
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            if (taskInstance != null && taskInstance.getEndTime() != null) {
                taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
                if (taskInstance != null) {
                    taskInstUuid = taskInstance.getUuid();
                } else {
                    String redirectUrl = "/workflow/work/v53/view/draft?flowInstUuid=" + flowInstUuid
                            + requestCodeParamUri;
                    addEpRedirectAttributes(request, redirectAttributes);
                    return redirect(redirectUrl);
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
                String redirectUrl = "/workflow/work/v53/view/draft?flowInstUuid=" + flowInstUuid + requestCodeParamUri;
                addEpRedirectAttributes(request, redirectAttributes);
                return redirect(redirectUrl);
            }
        }
        // 标识为已阅
        if (taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            String redirectUrl = "/workflow/work/v53/view/todo?taskInstUuid=" + taskInstUuid + "&flowInstUuid="
                    + flowInstUuid + "&taskIdentityUuid=" + taskIdentityUuid + "&custom_script_url=" + customScriptUrl
                    + "&allowOperate=" + allowOperate + requestCodeParamUri;
            addEpRedirectAttributes(request, redirectAttributes);
            return redirect(redirectUrl);
        }

        WorkBean workBean = null;
        TaskInstance entity = new TaskInstance();
        entity.setUuid(taskInstUuid);
        if (taskService.hasSupervisePermission(user, taskInstUuid)) {
            workBean = workService.getSupervise(taskInstUuid, flowInstUuid);
        } else if (taskService.hasMonitorPermission(user, taskInstUuid)) {
            workBean = workService.getMonitor(taskInstUuid, flowInstUuid);
        } else if (taskService.hasViewPermission(user, taskInstUuid)) {
            workBean = workService.getDone(taskInstUuid, taskIdentityUuid, flowInstUuid);
        } else {
            // 查阅送审批的流程
            if (StringUtils.isNotBlank(approveFlowInstUuid)) {
                FlowInstanceParameter flowInstanceParameter = new FlowInstanceParameter();
                flowInstanceParameter.setFlowInstUuid(approveFlowInstUuid);
                flowInstanceParameter.setName("custom_rt_sentContent");
                List<FlowInstanceParameter> flowInstanceParameters = FlowEngine.getInstance().getFlowService()
                        .findFlowInstanceParameter(flowInstanceParameter);
                if (flowInstanceParameters.size() == 1) {
                    String sendContent = flowInstanceParameters.get(0).getValue();
                    // 判断送审批的内容包含查阅的流程实例UUID
                    if (StringUtils.contains(sendContent, flowInstUuid)) {
                        // 草稿权限判断
                        if (flowService.hasDraftPermission(userId, approveFlowInstUuid)) {
                            workBean = workService.getRead(taskInstUuid, flowInstUuid);
                        } else {
                            // 最新环节权限判断
                            TaskInstance taskInstance = taskService
                                    .getLastTaskInstanceByFlowInstUuid(approveFlowInstUuid);
                            if (taskService.hasViewPermission(user, taskInstance.getUuid())) {
                                workBean = workService.getRead(taskInstUuid, flowInstUuid);
                            }
                        }
                    }
                }
            }
            if (workBean == null) {
                return forward(AclConstants.DEFAULT_ERROR_VIEW);
            }
        }
        workBean.setTaskIdentityUuid(taskIdentityUuid);
        model.addAttribute(workBean);

        readMarkerService.markRead(taskInstUuid, userId);

        // 加载自定义扩展JS
        if (StringUtils.isNotBlank(customScriptUrl)) {
            workBean.addExtraParam("custom_script_url", customScriptUrl);
        }
        Map<String, String[]> paramMap = request.getParameterMap();
        // 附加参数
        for (String paramName : paramMap.keySet()) {
            if (paramName.startsWith("ep_")) {
                String paramValue = request.getParameter(paramName);
                workBean.addExtraParam(paramName, paramValue);
            }
        }
        // 不允许操作清空默认具有的操作按钮
        if (!allowOperate) {
            workBean.setButtons(new ArrayList<CustomDynamicButton>(0));
        }
        return forward(workViewV53);
    }

    @RequestMapping(value = "/view/viewReadLog", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> viewReadLog(@RequestParam(value = "taskInstUuid") final String taskInstUuid) {
        return workService.viewReadLog(taskInstUuid);
    }

    /**
     * @param request
     * @param taskInstUuid
     * @param flowInstUuid
     * @param parentFlowInstUuid
     * @param model
     * @param redirectAttributes
     * @return
     */
    @RequestMapping(value = "/view/subflow/share", method = RequestMethod.GET)
    public String subflowShare(
            HttpServletRequest request,
            @RequestParam(value = "taskInstUuid", defaultValue = StringUtils.EMPTY, required = false) String taskInstUuid,
            @RequestParam(value = "flowInstUuid", defaultValue = StringUtils.EMPTY, required = false) String flowInstUuid,
            @RequestParam(value = "belongToFlowInstUuid", defaultValue = StringUtils.EMPTY, required = false) String belongToFlowInstUuid,
            Model model, final RedirectAttributes redirectAttributes) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
        String tmpTaskInstUuid = taskInstance.getUuid();
        // 流程权限判断
        if (taskService.hasViewPermission(user, tmpTaskInstUuid)) {
            String redirectUrl = "/workflow/work/v53/view/work?taskInstUuid=" + tmpTaskInstUuid + "&flowInstUuid="
                    + flowInstUuid;
            addEpRedirectAttributes(request, redirectAttributes);
            return redirect(redirectUrl);
        }

        // 判断子流程是否共享
        TaskSubFlow taskSubFlow = taskSubFlowService.get(belongToFlowInstUuid, flowInstUuid);
        if (taskSubFlow == null || !Boolean.TRUE.equals(taskSubFlow.getIsShare())) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getRead(tmpTaskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward(workViewV53);
    }

}
