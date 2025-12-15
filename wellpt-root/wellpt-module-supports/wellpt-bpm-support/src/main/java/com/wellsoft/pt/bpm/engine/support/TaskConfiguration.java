/*
 * @(#)7/5/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.RightConfigElement;
import com.wellsoft.pt.bpm.engine.form.Button;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: 环节配置相关信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/5/24.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 7/5/24
 */
public class TaskConfiguration extends BaseObject {
    private static final long serialVersionUID = 1481788565172027151L;

    private String taskId;

    private String taskName;

    private FlowDelegate flowDelegate;

    private String formUuid;

    private String printTemplateId;

    private String printTemplateUuid;

    private String serialNoDefId;

    // 发起操作按钮权限
    private List<String> startRights = Collections.emptyList();
    private List<Button> startButtons = Collections.emptyList();
    private RightConfigElement startRightConfig;

    // 待办操作按钮权限
    private List<String> todoRights = Collections.emptyList();
    private List<Button> todoButtons = Collections.emptyList();
    private RightConfigElement todoRightConfig;

    // 已办操作按钮权限
    private List<String> doneRights = Collections.emptyList();
    private List<Button> doneButtons = Collections.emptyList();
    private RightConfigElement doneRightConfig;

    // 督办操作按钮权限
    private List<String> monitorRights = Collections.emptyList();
    private List<Button> monitorButtons = Collections.emptyList();
    private RightConfigElement monitorRightConfig;

    // 监控操作按钮权限
    private List<String> adminRights = Collections.emptyList();
    private List<Button> adminButtons = Collections.emptyList();
    private RightConfigElement adminRightConfig;

    // 抄送操作按钮权限
    private List<String> copyToRights = Collections.emptyList();
    private List<Button> copyToButtons = Collections.emptyList();
    private RightConfigElement copyToRightConfig;

    // 查阅操作按钮权限
    private List<String> viewerRights = Collections.emptyList();
    private List<Button> viewerButtons = Collections.emptyList();
    private RightConfigElement viewerRightConfig;

    private List<CustomDynamicButton> customDynamicButtons;

    private List<Map<String, String>> opinions;

    /**
     *
     */
    public TaskConfiguration() {
    }

    /**
     * @param taskId
     * @param flowDelegate
     */
    public TaskConfiguration(String taskId, FlowDelegate flowDelegate) {
        this.taskId = taskId;
        this.flowDelegate = flowDelegate;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public Boolean isFirstTaskNode() {
        return flowDelegate.isFirstTaskNode(taskId);
    }

    /**
     * @return the flowDelegate
     */
    public FlowDelegate getFlowDelegate() {
        return flowDelegate;
    }

    /**
     * @param flowDelegate 要设置的flowDelegate
     */
    public void setFlowDelegate(FlowDelegate flowDelegate) {
        this.flowDelegate = flowDelegate;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the printTemplateId
     */
    public String getPrintTemplateId() {
        return printTemplateId;
    }

    /**
     * @param printTemplateId 要设置的printTemplateId
     */
    public void setPrintTemplateId(String printTemplateId) {
        this.printTemplateId = printTemplateId;
    }

    /**
     * @return the printTemplateUuid
     */
    public String getPrintTemplateUuid() {
        return printTemplateUuid;
    }

    /**
     * @param printTemplateUuid 要设置的printTemplateUuid
     */
    public void setPrintTemplateUuid(String printTemplateUuid) {
        this.printTemplateUuid = printTemplateUuid;
    }

    /**
     * @return the serialNoDefId
     */
    public String getSerialNoDefId() {
        return serialNoDefId;
    }

    /**
     * @param serialNoDefId 要设置的serialNoDefId
     */
    public void setSerialNoDefId(String serialNoDefId) {
        this.serialNoDefId = serialNoDefId;
    }

    /**
     * @return the startRights
     */
    public List<String> getStartRights() {
        return startRights;
    }

    /**
     * @param startRights 要设置的startRights
     */
    public void setStartRights(List<String> startRights) {
        this.startRights = startRights;
    }

    /**
     * @return the startButtons
     */
    public List<Button> getStartButtons() {
        return startButtons;
    }

    /**
     * @param startButtons 要设置的startButtons
     */
    public void setStartButtons(List<Button> startButtons) {
        this.startButtons = startButtons;
    }

    /**
     * @return the startRightConfig
     */
    public RightConfigElement getStartRightConfig() {
        return startRightConfig;
    }

    /**
     * @param startRightConfig 要设置的startRightConfig
     */
    public void setStartRightConfig(RightConfigElement startRightConfig) {
        this.startRightConfig = startRightConfig;
    }

    /**
     * @return the todoRights
     */
    public List<String> getTodoRights() {
        return todoRights;
    }

    /**
     * @param todoRights 要设置的todoRights
     */
    public void setTodoRights(List<String> todoRights) {
        this.todoRights = todoRights;
    }

    /**
     * @return the todoButtons
     */
    public List<Button> getTodoButtons() {
        return todoButtons;
    }

    /**
     * @param todoButtons 要设置的todoButtons
     */
    public void setTodoButtons(List<Button> todoButtons) {
        this.todoButtons = todoButtons;
    }

    /**
     * @return the todoRightConfig
     */
    public RightConfigElement getTodoRightConfig() {
        return todoRightConfig;
    }

    /**
     * @param todoRightConfig 要设置的todoRightConfig
     */
    public void setTodoRightConfig(RightConfigElement todoRightConfig) {
        this.todoRightConfig = todoRightConfig;
    }

    /**
     * @return the doneRights
     */
    public List<String> getDoneRights() {
        return doneRights;
    }

    /**
     * @param doneRights 要设置的doneRights
     */
    public void setDoneRights(List<String> doneRights) {
        this.doneRights = doneRights;
    }

    /**
     * @return the doneButtons
     */
    public List<Button> getDoneButtons() {
        return doneButtons;
    }

    /**
     * @param doneButtons 要设置的doneButtons
     */
    public void setDoneButtons(List<Button> doneButtons) {
        this.doneButtons = doneButtons;
    }

    /**
     * @return the doneRightConfig
     */
    public RightConfigElement getDoneRightConfig() {
        return doneRightConfig;
    }

    /**
     * @param doneRightConfig 要设置的doneRightConfig
     */
    public void setDoneRightConfig(RightConfigElement doneRightConfig) {
        this.doneRightConfig = doneRightConfig;
    }

    /**
     * @return the monitorRights
     */
    public List<String> getMonitorRights() {
        return monitorRights;
    }

    /**
     * @param monitorRights 要设置的monitorRights
     */
    public void setMonitorRights(List<String> monitorRights) {
        this.monitorRights = monitorRights;
    }

    /**
     * @return the monitorButtons
     */
    public List<Button> getMonitorButtons() {
        return monitorButtons;
    }

    /**
     * @param monitorButtons 要设置的monitorButtons
     */
    public void setMonitorButtons(List<Button> monitorButtons) {
        this.monitorButtons = monitorButtons;
    }

    /**
     * @return the monitorRightConfig
     */
    public RightConfigElement getMonitorRightConfig() {
        return monitorRightConfig;
    }

    /**
     * @param monitorRightConfig 要设置的monitorRightConfig
     */
    public void setMonitorRightConfig(RightConfigElement monitorRightConfig) {
        this.monitorRightConfig = monitorRightConfig;
    }

    /**
     * @return the adminRights
     */
    public List<String> getAdminRights() {
        return adminRights;
    }

    /**
     * @param adminRights 要设置的adminRights
     */
    public void setAdminRights(List<String> adminRights) {
        this.adminRights = adminRights;
    }

    /**
     * @return the adminButtons
     */
    public List<Button> getAdminButtons() {
        return adminButtons;
    }

    /**
     * @param adminButtons 要设置的adminButtons
     */
    public void setAdminButtons(List<Button> adminButtons) {
        this.adminButtons = adminButtons;
    }

    /**
     * @return the adminRightConfig
     */
    public RightConfigElement getAdminRightConfig() {
        return adminRightConfig;
    }

    /**
     * @param adminRightConfig 要设置的adminRightConfig
     */
    public void setAdminRightConfig(RightConfigElement adminRightConfig) {
        this.adminRightConfig = adminRightConfig;
    }

    /**
     * @return the copyToRights
     */
    public List<String> getCopyToRights() {
        return copyToRights;
    }

    /**
     * @param copyToRights 要设置的copyToRights
     */
    public void setCopyToRights(List<String> copyToRights) {
        this.copyToRights = copyToRights;
    }

    /**
     * @return the copyToButtons
     */
    public List<Button> getCopyToButtons() {
        return copyToButtons;
    }

    /**
     * @param copyToButtons 要设置的copyToButtons
     */
    public void setCopyToButtons(List<Button> copyToButtons) {
        this.copyToButtons = copyToButtons;
    }

    /**
     * @return the copyToRightConfig
     */
    public RightConfigElement getCopyToRightConfig() {
        return copyToRightConfig;
    }

    /**
     * @param copyToRightConfig 要设置的copyToRightConfig
     */
    public void setCopyToRightConfig(RightConfigElement copyToRightConfig) {
        this.copyToRightConfig = copyToRightConfig;
    }

    /**
     * @return the viewerRights
     */
    public List<String> getViewerRights() {
        return viewerRights;
    }

    /**
     * @param viewerRights 要设置的viewerRights
     */
    public void setViewerRights(List<String> viewerRights) {
        this.viewerRights = viewerRights;
    }

    /**
     * @return the viewerButtons
     */
    public List<Button> getViewerButtons() {
        return viewerButtons;
    }

    /**
     * @param viewerButtons 要设置的viewerButtons
     */
    public void setViewerButtons(List<Button> viewerButtons) {
        this.viewerButtons = viewerButtons;
    }

    /**
     * @return the viewerRightConfig
     */
    public RightConfigElement getViewerRightConfig() {
        return viewerRightConfig;
    }

    /**
     * @param viewerRightConfig 要设置的viewerRightConfig
     */
    public void setViewerRightConfig(RightConfigElement viewerRightConfig) {
        this.viewerRightConfig = viewerRightConfig;
    }

    /**
     * @return the customDynamicButtons
     */
    public List<CustomDynamicButton> getCustomDynamicButtons() {
        return customDynamicButtons;
    }

    /**
     * @param customDynamicButtons 要设置的customDynamicButtons
     */
    public void setCustomDynamicButtons(List<CustomDynamicButton> customDynamicButtons) {
        this.customDynamicButtons = customDynamicButtons;
    }

    /**
     * @return the opinions
     */
    public List<Map<String, String>> getOpinions() {
        return opinions;
    }

    /**
     * @param opinions 要设置的opinions
     */
    public void setOpinions(List<Map<String, String>> opinions) {
        this.opinions = opinions;
    }

    /**
     * @param privileges
     * @return
     */
    public List<Button> filterTodoButtons(WorkFlowPrivilege... privileges) {
        List<Button> todoButtons = getTodoButtons();
        List<String> privilegeCodes = Arrays.stream(privileges).map(WorkFlowPrivilege::getCode).collect(Collectors.toList());
        return todoButtons.stream().filter(button -> privilegeCodes.contains(button.getCode())).collect(Collectors.toList());
    }

}
