/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.ext.dms.file.web;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.workflow.ext.dms.file.viewer.WorkflowFileViewer;
import com.wellsoft.pt.app.workflow.web.WorkV53Controller;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.file.action.FileActions;
import com.wellsoft.pt.security.acl.service.AclConstants;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
@Controller
@RequestMapping(WorkflowFileViewer.WORKFLOW_FILE_VIEWER_PREFIX)
public class WorkflowFileViewerController extends WorkV53Controller {

    @Autowired
    private WorkService workService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    /**
     * 查看归档的流程数据
     *
     * @param flowInstUuid
     * @param fileUuid
     * @param folderUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/archive", method = RequestMethod.GET)
    public String viewArchive(
            @RequestParam(value = "flowInstUuid") String flowInstUuid,
            @RequestParam(value = "fileUuid") String fileUuid,
            @RequestParam(value = "folderUuid") String folderUuid,
            @RequestParam(value = "_requestCode", defaultValue = StringUtils.EMPTY, required = false) String requestCode,
            HttpServletRequest request, HttpServletResponse response, Model model,
            final RedirectAttributes redirectAttributes) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String requestCodeParamUri = "&_requestCode=" + requestCode;
        TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
        String taskInstUuid = taskInstance.getUuid();
        // 流程权限判断
        if (taskService.hasViewPermission(user, taskInstUuid)) {
            String redirectUrl = "/workflow/work/v53/view/work?taskInstUuid=" + taskInstUuid + "&flowInstUuid="
                    + flowInstUuid + requestCodeParamUri;
            addEpRedirectAttributes(request, redirectAttributes);
            return redirect(redirectUrl);
        }

        // 文件读取权限判断
        if (!dmsFileServiceFacade.hasFilePermission(fileUuid, FileActions.READ_FILE)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getRead(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward(workViewV53);
    }

    /**
     * 查看归档的流程表单数据
     *
     * @param flowInstUuid
     * @param fileUuid
     * @param folderUuid
     * @param model
     * @return
     */
    @RequestMapping(value = "/view/dyform/archive", method = RequestMethod.GET)
    public String viewDyformArchive(
            @RequestParam(value = "dataUuid") String dataUuid,
            @RequestParam(value = "fileUuid") String fileUuid,
            @RequestParam(value = "folderUuid") String folderUuid,
            @RequestParam(value = "_requestCode", defaultValue = StringUtils.EMPTY, required = false) String requestCode,
            HttpServletRequest request, HttpServletResponse response, Model model,
            final RedirectAttributes redirectAttributes) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        // 获取规档的流程
        String flowInstUuid = getArchiveFlowInstUuid(fileUuid, folderUuid);
        if (StringUtils.isBlank(flowInstUuid)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        if (!StringUtils.equals(flowInstance.getDataUuid(), dataUuid)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        String requestCodeParamUri = "&_requestCode=" + requestCode;
        TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
        String taskInstUuid = taskInstance.getUuid();
        // 流程权限判断
        if (taskService.hasViewPermission(user, taskInstUuid)) {
            String redirectUrl = "/workflow/work/v53/view/work?taskInstUuid=" + taskInstUuid + "&flowInstUuid="
                    + flowInstUuid + requestCodeParamUri;
            addEpRedirectAttributes(request, redirectAttributes);
            return redirect(redirectUrl);
        }

        // 文件读取权限判断
        if (!dmsFileServiceFacade.hasFilePermission(fileUuid, FileActions.READ_FILE)) {
            return forward(AclConstants.DEFAULT_ERROR_VIEW);
        }

        WorkBean workBean = workService.getRead(taskInstUuid, flowInstUuid);
        model.addAttribute(workBean);
        return forward(workViewV53);
    }

    /**
     * @param folderUuid
     * @return
     */
    private String getArchiveFlowInstUuid(String fileUuid, String folderUuid) {
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setName(folderUuid + Separator.SLASH.getValue() + fileUuid);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isEmpty(parameters)) {
            return null;
        }
        return parameters.get(0).getValue();
    }

}
