/*
 * @(#)2012-11-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.web;

import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.security.acl.service.AclConstants;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 手机端工作操作控制器类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-26.1	zhulh		2012-11-26		Create
 * </pre>
 * @date 2012-11-26
 */
@Controller
@RequestMapping("/workflow/mobile/work")
public class MobileWorkController extends BaseController {

    @Autowired
    private AclService aclService;

    @Autowired
    private WorkService workService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private ReadMarkerService readMarkerService;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private TaskOperationService taskOperationService;

    @RequestMapping("/flow/list")
    public String openList(Model model) {
        model.addAttribute(new WorkBean());
        return forward("/workflow/work/flow_list");
    }

    /**
     * 跳转到新建工作界面
     *
     * @param flowDefUid
     * @param model
     * @return
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String newWork(HttpServletRequest request, @RequestParam("flowDefUuid") String flowDefUuid,
                          @RequestParam(value = "formUuid", defaultValue = "") String formUuid,
                          @RequestParam(value = "dataUuid", defaultValue = "") String dataUuid,
                          @RequestParam(value = "custom_script_url", required = false) String customScriptUrl, Model model) {
        WorkBean workBean = workService.newWork(flowDefUuid);
        if (StringUtils.isNotBlank(formUuid)) {
            workBean.setFormUuid(formUuid);

            if (StringUtils.isNotBlank(dataUuid)) {
                workBean.setDataUuid(dataUuid);
            }
        }

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

        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 跳转到新建工作界面
     *
     * @param flowDefUid
     * @param model
     * @return
     */
    @RequestMapping(value = "/new/{id}", method = RequestMethod.GET)
    public String newWorkById(HttpServletRequest request, @PathVariable("id") String id,
                              @RequestParam(value = "formUuid", defaultValue = "") String formUuid,
                              @RequestParam(value = "dataUuid", defaultValue = "") String dataUuid,
                              @RequestParam(value = "custom_script_url", required = false) String customScriptUrl, Model model) {
        WorkBean workBean = workService.newWorkById(id);
        if (StringUtils.isNotBlank(formUuid)) {
            workBean.setFormUuid(formUuid);

            if (StringUtils.isNotBlank(dataUuid)) {
                workBean.setDataUuid(dataUuid);
            }
        }

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

        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 保存工作
     *
     * @param workBean
     * @param result
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public String save(@Valid @ModelAttribute WorkBean workBean, BindingResult result) {
        workService.save(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看草稿工作
     *
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/draft", method = RequestMethod.GET)
    public String viewDraft(HttpServletRequest request, @RequestParam("flowInstUuid") String flowInstUuid, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        // 权限检查
        if (StringUtils.isNotBlank(flowInstUuid) && !flowService.hasDraftPermission(userId, flowInstUuid)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getDraft(flowInstUuid);

        // 附加参数
        Map<String, String[]> paramMap = request.getParameterMap();
        for (String paramName : paramMap.keySet()) {
            if (paramName.startsWith("ep_")) {
                String paramValue = request.getParameter(paramName);
                workBean.addExtraParam(paramName, paramValue);
            }
        }

        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/task/{taskInstUuid}", method = RequestMethod.GET)
    public String viewTask(HttpServletRequest request, @PathVariable("taskInstUuid") String taskInstUuid,
                           @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid,
                           @RequestParam(value = "custom_script_url", required = false) String customScriptUrl,
                           @RequestParam(value = "allowOperate", required = false, defaultValue = "true") boolean allowOperate,
                           Model model) {
        WorkBean workBean = workService.getDone(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);

        // 标识为已阅
        String userId = SpringSecurityUtils.getCurrentUserId();
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
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看工作包含从草稿、待办、已办中查看
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/todo/flow", method = RequestMethod.GET)
    public String viewTodoFlow(HttpServletRequest request,
                               @RequestParam(value = "flowInstUuid", required = true) String flowInstUuid,
                               @RequestParam(value = "openToRead", defaultValue = "true") boolean openToRead,
                               @RequestParam(value = "custom_script_url", required = false) String customScriptUrl,
                               @RequestParam(value = "auto_submit", required = false) String auto_submit, Model model) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<TaskInstance> taskInstances = taskService.getTodoTasks(userId, flowInstUuid);
        WorkBean workBean = null;
        if (taskInstances.isEmpty()) {
            workBean = workService.getDraft(flowInstUuid);
        } else {
            String taskInstUuid = taskInstances.get(0).getUuid();
            workBean = workService.getTodo(taskInstUuid, flowInstUuid);

            // 打开流程单据时，标记为已读
            if (StringUtils.isNotBlank(taskInstUuid)) {
                readMarkerService.markRead(taskInstUuid, userId);
            }
        }

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

        model.addAttribute(workBean);

        return forward("/workflow/work/mobile_work_view");
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
    public String viewWork(HttpServletRequest request,
                           @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                           @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid,
                           @RequestParam(value = "openToRead", defaultValue = "true") boolean openToRead,
                           @RequestParam(value = "custom_script_url", required = false) String customScriptUrl,
                           @RequestParam(value = "allowOperate", required = false, defaultValue = "true") boolean allowOperate,
                           @RequestParam(value = "auto_submit", required = false) String auto_submit, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isBlank(taskInstUuid) && StringUtils.isNotBlank(flowInstUuid)) {
            TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
            if (taskInstance != null) {
                taskInstUuid = taskInstance.getUuid();
            }
        }
        // 标识为已阅
        if (taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            String redirectUrl = "/workflow/work/view/todo?taskInstUuid=" + taskInstUuid + "&flowInstUuid="
                    + flowInstUuid + "&custom_script_url=" + customScriptUrl + "&allowOperate=" + allowOperate;
            String ep_qztj = request.getParameter("ep_qztj");
            if (StringUtils.isNotBlank(ep_qztj)) {
                redirectUrl += "&ep_qztj=" + ep_qztj;
            }
            return redirect(redirectUrl);
        }

        logger.error("view taskInstUuid " + taskInstUuid);
        WorkBean workBean = null;
        if (taskService.hasMonitorPermission(user, taskInstUuid)) {
            workBean = workService.getMonitor(taskInstUuid, flowInstUuid);
        } else {
            workBean = workService.getDone(taskInstUuid, flowInstUuid);
        }
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
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看工作包含从草稿、待办、已办中查看
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/todo", method = RequestMethod.GET)
    public String viewTodo(HttpServletRequest request,
                           @RequestParam(value = "taskUuid", required = false) String taskUuid,
                           @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                           @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid,
                           @RequestParam(value = "openToRead", defaultValue = "true") boolean openToRead,
                           @RequestParam(value = "custom_script_url", required = false) String customScriptUrl,
                           @RequestParam(value = "auto_submit", required = false) String auto_submit, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
                return redirect("/workflow/work/view/unread?taskInstUuid=" + taskInstUuid + "&openToRead=" + openToRead);
            } else if (taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                return redirect("/workflow/work/view/read?taskInstUuid=" + taskInstUuid);
            }
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getTodo(taskInstUuid, flowInstUuid);
        // 打开流程单据时，标记为已读
        if (StringUtils.isNotBlank(taskInstUuid)) {
            readMarkerService.markRead(taskInstUuid, userId);
        }

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

        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看已办工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/done", method = RequestMethod.GET)
    public String viewDone(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                           @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                           @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }

        // 标识为已阅
        readMarkerService.markRead(taskInstUuid, userId);

        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.DONE)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getDone(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看已完成的工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/over", method = RequestMethod.GET)
    public String viewOver(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                           @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                           @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid) && !taskService.hasPermission(user, taskInstUuid, AclPermission.DONE)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getOver(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
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
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.UNREAD)) {
            if (taskService.hasPermission(user, taskInstUuid, AclPermission.FLAG_READ)) {
                return redirect("/workflow/work/view/read?taskInstUuid=" + taskInstUuid);
            }
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getUnread(taskInstUuid, flowInstUuid, openToRead);
        // 打开流程单据时，标记为已读
        if (StringUtils.isNotBlank(taskInstUuid)) {
            readMarkerService.markRead(taskInstUuid, userId);
        }
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
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
                    return redirect("/workflow/work/view/unread?taskInstUuid=" + taskInstUuid);
                }
                return forward(AclConstants.DEFAULT_ERROR_VIEW);
            } else {
                aclService.removePermission(TaskInstance.class, taskInstUuid, AclPermission.UNREAD, userId);
            }
        }

        WorkBean workBean = workService.getRead(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 任务关注
     *
     * @param workBean
     * @param result
     * @return
     */
    @RequestMapping(value = "/view/attention", method = RequestMethod.GET)
    public String viewAttention(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                                @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                                @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.ATTENTION)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getAttention(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看督办的工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/supervise", method = RequestMethod.GET)
    public String viewSupervise(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                                @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                                @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.SUPERVISE)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getSupervise(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /**
     * 查看监控的工作
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/monitor", method = RequestMethod.GET)
    public String viewMonitor(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                              @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                              @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)
                && !taskService.hasPermission(user, taskInstUuid, AclPermission.MONITOR)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getMonitor(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward("/workflow/work/mobile_work_view");
    }

    /*add by huanglinchuan 2014.10.31 begin*/
    @RequestMapping(value = "/view/colligate", method = RequestMethod.GET)
    public String view(@RequestParam(value = "taskUuid", required = false) String taskUuid,
                       @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                       @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        if (StringUtils.isNotBlank(taskUuid)) {
            taskInstUuid = taskUuid;
        }
        // 权限检查
        if (StringUtils.isNotBlank(taskInstUuid)) {
            List<Permission> allPermissions = new ArrayList<Permission>();
            TaskInstance taskInstance = taskService.get(taskInstUuid);
            List<Permission> permissions = aclService.getPermission(taskInstance, userId);
            List<Permission> draftPermissions = aclService.getPermission(taskInstance.getFlowInstance(), userId);
            /*modified by huanglinchuan 2014.12.13 begin*/
            List<Permission> readPermissions = aclService.getAllPermissionByUserId(taskInstance.getFlowDefinition(),
                    userId);
            /*modified by huanglinchuan 2014.12.13 end*/
            if (permissions != null) {
                allPermissions.addAll(permissions);
            }
            if (draftPermissions != null) {
                allPermissions.addAll(draftPermissions);
            }
            if (readPermissions != null) {
                allPermissions.addAll(readPermissions);
            }
            if (allPermissions == null || allPermissions.isEmpty()) {
                return forward(AclConstants.DEFAULT_ERROR_VIEW);
            }

            WorkBean workBean = workService.getByPermissions(taskInstUuid, flowInstUuid, allPermissions);
            model.addAttribute(workBean);
        }

        return forward("/workflow/work/mobile_work_view");
    }

    /*add by huanglinchuan 2014.10.31 end*/

    @RequestMapping(value = "/view/flow/process", method = RequestMethod.GET)
    public String viewFlowProcess(@RequestParam(value = "flowInstUuid", required = true) String flowInstUuid,
                                  Model model) {
        model.addAttribute("flowInstUuid", flowInstUuid);
        return forward("/workflow/work/mobile_work_view_process");
    }

    @RequestMapping(value = "/get/flow/process", method = RequestMethod.GET)
    public @ResponseBody
    String getFlowProcess(
            @RequestParam(value = "flowInstUuid", required = true) String flowInstUuid, Model model) {
        model.addAttribute("flowInstUuid", flowInstUuid);
        String process = workService.getProcess(flowInstUuid, true, true);
        return process;
    }

    @RequestMapping(value = "/view/timeline", method = RequestMethod.GET)
    public String viewTimeline(@RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                               @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        return forward("/workflow/work/work_timeline");
    }

    @RequestMapping(value = "/view/timeline2", method = RequestMethod.GET)
    public String viewTimeline2(@RequestParam("taskInstUuid") String taskInstUuid,
                                @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model) {
        return forward("/workflow/work/work_timeline2");
    }

    /**
     * 新版待办工作查看
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/view/todo2", method = RequestMethod.GET)
    public String viewTodo2(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                            @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model)
            throws IOException {
        // String contextPath = request.getContextPath();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String urlParams = "&taskInstUuid=" + taskInstUuid + "&flowInstUuid=" + flowInstUuid;
        if (taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            return redirect("/web/app/pt-mobile/mobile-workflow/mobile-worktodo.html?dotype=done2" + urlParams);
        } else if (taskService.hasPermission(user, taskInstUuid, AclPermission.DONE)) {
            return redirect("/web/app/pt-mobile/mobile-workflow/mobile-workdone.html?dotype=todo2" + urlParams);
        } else {
            return redirect("/web/app/pt-mobile/mobile-workflow/mobile-workshare.html?dotype=todo2" + urlParams);
        }
    }

    /**
     * 新版分享工作查看
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param model
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/view/share", method = RequestMethod.GET)
    public String viewShare(HttpServletRequest request, HttpServletResponse response,
                            @RequestParam(value = "taskInstUuid", required = false) String taskInstUuid,
                            @RequestParam(value = "flowInstUuid", required = false) String flowInstUuid, Model model)
            throws IOException {
        // String contextPath = request.getContextPath();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String urlParams = "&taskInstUuid=" + taskInstUuid + "&flowInstUuid=" + flowInstUuid;
        if (taskService.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            return redirect("/web/app/pt-mobile/mobile-workflow/mobile-worktodo.html?dotype=done2" + urlParams);
        } else if (taskService.hasPermission(user, taskInstUuid, AclPermission.DONE)) {
            return redirect("/web/app/pt-mobile/mobile-workflow/mobile-workdone.html?dotype=todo2" + urlParams);
        } else {
            return redirect("/web/app/pt-mobile/mobile-workflow/mobile-workshare.html?dotype=todo2" + urlParams);
        }
    }

}
