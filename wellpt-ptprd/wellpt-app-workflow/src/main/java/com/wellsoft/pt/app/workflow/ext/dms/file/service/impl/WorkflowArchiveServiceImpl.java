/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.ext.dms.file.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.workflow.ext.dms.file.service.WorkflowArchiveService;
import com.wellsoft.pt.app.workflow.ext.dms.file.viewer.WorkflowMediaType;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotParam.BotFromParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.core.Archive;
import com.wellsoft.pt.bpm.engine.core.Script;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.exception.ChooseArchiveFolderException;
import com.wellsoft.pt.bpm.engine.service.FlowArchiveService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.ArchiveStrategy;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowScriptHelper;
import com.wellsoft.pt.dms.facade.api.DmsFileServiceFacade;
import com.wellsoft.pt.dms.model.DmsFile;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

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
@Service
@Transactional
public class WorkflowArchiveServiceImpl implements WorkflowArchiveService, FlowArchiveService {

    @Autowired
    private DmsFileServiceFacade dmsFileServiceFacade;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DyFormFacade dyFormFacade;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.ext.dms.file.service.WorkflowArchiveService#archive(java.lang.String, com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void archive(String folderUuid, Event event) {
        DmsFile dmsFile = new DmsFile();
        dmsFile.setName(event.getTitle());
        dmsFile.setContentType(WorkflowMediaType.APPLICATION_WORKFLOW_VALUE);
        dmsFile.setDataDefUuid(event.getFlowDefUuid());
        dmsFile.setDataUuid(event.getFlowInstUuid());
        // 归档到夹folderUuid
        dmsFile.setFolderUuid(folderUuid);
        String fileUuid = dmsFileServiceFacade.archive(dmsFile, taskService.getDoneUserIds(event.getTaskInstUuid()));
        saveArchiveFlowInstanceParameter(event.getFlowInstUuid(), fileUuid, folderUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.ext.dms.file.service.WorkflowArchiveService#archive(com.wellsoft.pt.bpm.engine.context.event.Event, com.wellsoft.pt.bpm.engine.core.Archive)
     */
    @Override
    public void archive(Event event, Archive archive) {
        String archiveWay = archive.getArchiveWay();
        if (StringUtils.isBlank(archiveWay)) {
            return;
        }

        switch (Integer.parseInt(archiveWay)) {
            case 1:
                // 流程数据归档
                archiveWorkData(event, archive);
                break;
            case 2:
                // 表单数据归档
                archiveDyformOfWorkData(event, archive);
                break;
            case 3:
                // 数据转换后归档
                archiveAfterBot(event, archive);
                break;
            case 4:
                // 脚本归档
                archiveByScript(event, archive);
                break;
            case 5:
                // 弹窗由用户确认归档范围
                archiveByUserConfirm(event, archive);
                break;
            default:
                break;
        }

    }

    /**
     * @param event
     * @param archive
     * @return
     */
    private Set<String> getDestFolderUuid(Event event, Archive archive) {
        return getDestFolderUuid(event, archive, false);
    }

    /**
     * @param event
     * @param archive
     * @param allowEmptyFolder
     * @return
     */
    private Set<String> getDestFolderUuid(Event event, Archive archive, boolean allowEmptyFolder) {
        String destFolderUuid = archive.getDestFolderUuid();
        List<String> destFolderUuids = Arrays.asList(StringUtils.split(destFolderUuid, Separator.SEMICOLON.getValue()));
        Set<String> returnFolderSet = new HashSet<String>();
        String subFolderPath = resolveSubFolder(event, archive);
        if (StringUtils.isNotBlank(subFolderPath)) {
            for (String parentFolderUuid : destFolderUuids) {
                returnFolderSet.add(dmsFileServiceFacade.createFolderIfNotExists(subFolderPath, parentFolderUuid));
            }
        } else {
            returnFolderSet.addAll(destFolderUuids);
        }
        if (!allowEmptyFolder) {
            Assert.notEmpty(returnFolderSet, "归档夹不能为空！");
        }
        return returnFolderSet;
    }

    /**
     * @param event
     * @param archive
     * @return
     */
    @SuppressWarnings("rawtypes")
    private String resolveSubFolder(Event event, Archive archive) {
        String subFolderRule = archive.getSubFolderRule();
        Boolean fillDateTime = archive.getFillDateTime();
        if (StringUtils.isBlank(subFolderRule)) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> extraData = new HashMap<String, Object>();
        addExtraData(event, extraData);
        try {
            Map dataMap = event.getDyFormData().getFormDataOfMainform();
            TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
            Map<Object, Object> data = templateEngine.mergeDataAsMap(null, dataMap, extraData, false, false);
            if (fillDateTime) {
                templateEngine.addDefaultConstantFillFormat(data);
            } else {
                templateEngine.addDefaultConstant(data);
            }
            subFolderRule = TemplateEngineFactory.getDefaultTemplateEngine().process(subFolderRule, data);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return StringUtils.trim(subFolderRule);
    }

    /**
     * @param extraData
     */
    private void addExtraData(Event event, Map<String, Object> extraData) {
        // 流程数据
        extraData.put("流程名称", event.getFlowName());
        extraData.put("流程ID", event.getFlowId());
        // 当前用户数据
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        Date sysdate = Calendar.getInstance().getTime();
        extraData.put("currentUserName", userDetails.getUserName());
        extraData.put("currentLoginName", userDetails.getLoginName());
        extraData.put("currentUserId", userDetails.getUserId());
        extraData.put("currentUserDepartmentId", userDetails.getMainDepartmentId());
        extraData.put("currentUserDepartmentName", userDetails.getMainDepartmentName());
        extraData.put("currentUserUnitId", userDetails.getSystemUnitId());
        extraData.put("sysdate", sysdate);
        extraData.put("当前用户名", userDetails.getUserName());
        extraData.put("当前用户登录名", userDetails.getLoginName());
        extraData.put("当前用户ID", userDetails.getUserId());
        extraData.put("当前用户主部门ID", userDetails.getMainDepartmentId());
        extraData.put("当前用户主部门名称", userDetails.getMainDepartmentName());
        extraData.put("当前用户归属单位ID", userDetails.getSystemUnitId());
        extraData.put("系统当前时间", sysdate);
    }

    /**
     * 流程数据归档
     *
     * @param event
     * @param archive
     */
    private void archiveWorkData(Event event, Archive archive) {
        Set<String> folderUuidSet = getDestFolderUuid(event, archive);
        for (String folderUuid : folderUuidSet) {
            archive(folderUuid, event);
        }
    }

    /**
     * 表单数据归档
     *
     * @param event
     * @param archive
     */
    private void archiveDyformOfWorkData(Event event, Archive archive) {
        Set<String> folderUuidSet = getDestFolderUuid(event, archive);
        List<String> readerIds = taskService.getDoneUserIds(event.getTaskInstUuid());
        for (String folderUuid : folderUuidSet) {
            String fileUuid = dmsFileServiceFacade.archive(event.getTitle(),
                    WorkflowMediaType.APPLICATION_WORKFLOW_DYFORM_VALUE, event.getDyFormData(), folderUuid, readerIds);
            saveArchiveFlowInstanceParameter(event.getFlowInstUuid(), fileUuid, folderUuid);
        }
    }

    /**
     * @param flowInstUuid
     * @param fileUuid
     * @param folderUuid
     */
    private void saveArchiveFlowInstanceParameter(String flowInstUuid, String fileUuid, String folderUuid) {
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName("archiveFileUuids");
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isNotEmpty(parameters)) {
            example = parameters.get(0);
            if (StringUtils.isNotBlank(example.getValue())) {
                Set<String> values = Sets.newLinkedHashSet(Arrays.asList(StringUtils.split(example.getValue(), Separator.SEMICOLON.getValue())));
                values.add(fileUuid);
                example.setValue(StringUtils.join(values, Separator.SEMICOLON.getValue()));
            }
        } else {
            example.setValue(fileUuid);
        }
        flowService.saveFlowInstanceParameter(example);
    }

    /**
     * 数据转换后归档
     *
     * @param event
     * @param archive
     */
    private void archiveAfterBot(Event event, Archive archive) {
        // 获取已转换的信息
        FlowInstanceParameter dataUuidArchiveParameter = getArchiveBotDataUuidFlowInstanceParameter(event, archive);
        String archiveStrategy = archive.getArchiveStrategy();
        switch (archiveStrategy) {
            case ArchiveStrategy.ADD:
                // 新增
                addArchiveAfterBot(event, archive);
                break;
            case ArchiveStrategy.REPLACE:
                // 替换
                replaceArchiveAfterBot(event, archive, dataUuidArchiveParameter);
                break;
            case ArchiveStrategy.IGNORE:
                // 忽略
                if (dataUuidArchiveParameter == null) {
                    addArchiveAfterBot(event, archive);
                }
                break;
            default:
                addArchiveAfterBot(event, archive);
                break;
        }
    }

    /**
     * 单据转换后增加归档
     *
     * @param event
     * @param archive
     */
    @SuppressWarnings("unchecked")
    private void addArchiveAfterBot(Event event, Archive archive) {
        BotResult botResult = getBotResult(event, archive);
        Object data = botResult.getData();
        DyFormData dyFormData = (DyFormData) botResult.getDyformData();
        if (data instanceof Map) {
            Map<String, Object> formData = (Map<String, Object>) data;
            String formUuid = (String) formData.get("form_uuid");
            String dataUuid = botResult.getDataUuid();

            if (StringUtils.isBlank(formUuid)) {
                for (Map.Entry<String, Object> entry : ((Map<String, Object>) data).entrySet()) {
                    Object value = entry.getValue();
                    if (null != value && value instanceof List) {
                        formData = (Map<String, Object>) ((ArrayList) value).get(0);
                        formUuid = (String) formData.get("form_uuid");
                        break;
                    }
                }
            }

            List<String> readerIds = taskService.getDoneUserIds(event.getTaskInstUuid());
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            Set<String> folderUuidSet = getDestFolderUuid(event, archive, true);
            for (String folderUuid : folderUuidSet) {
                String fileUuid = dmsFileServiceFacade.archive(dyFormData, folderUuid, readerIds);
                saveArchiveFlowInstanceParameter(event.getFlowInstUuid(), fileUuid, folderUuid);
            }
            // 保存归档单据转换的信息
            saveArchiveBotFormDataFlowInstanceParameter(event, archive, formUuid, dataUuid);
        } else {
            throw new RuntimeException("归档失败，无法取到流程数据转换后的数据！");
        }
    }

    /**
     * 单据转换后替换原有归档
     *
     * @param event
     * @param archive
     * @param dataUuidArchiveParameter
     */
    @SuppressWarnings("unchecked")
    private void replaceArchiveAfterBot(Event event, Archive archive, FlowInstanceParameter dataUuidArchiveParameter) {
        BotResult botResult = getBotResult(event, archive);
        Object data = botResult.getData();
        DyFormData dyFormData = (DyFormData) botResult.getDyformData();
        if (data instanceof Map) {
            Map<String, Object> formData = (Map<String, Object>) data;
            String formUuid = (String) formData.get("form_uuid");
            String dataUuid = botResult.getDataUuid();
            if (dyFormData == null) {
                dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            Set<String> folderUuidSet = getDestFolderUuid(event, archive, true);
            // 归档夹不为空
            if (CollectionUtils.isNotEmpty(folderUuidSet)) {
                List<String> readerIds = taskService.getDoneUserIds(event.getTaskInstUuid());
                for (String folderUuid : folderUuidSet) {
                    String fileUuid = null;
                    if (dataUuidArchiveParameter != null) {
                        DmsFile oldDmsFile = dmsFileServiceFacade.getFileWithoutPermission(folderUuid,
                                dataUuidArchiveParameter.getValue());
                        // 新增归档
                        if (oldDmsFile == null) {
                            fileUuid = dmsFileServiceFacade.archive(dyFormData, folderUuid, readerIds);
                        } else {
                            // 删除旧归档
                            dmsFileServiceFacade.physicalDeleteFileWithoutPermission(oldDmsFile.getUuid());
                            // 删除旧归档流程参数数据
                            deleteArchiveBotFormDataFlowInstanceParameter(dataUuidArchiveParameter);
                            // 新增归档
                            fileUuid = dmsFileServiceFacade.archive(dyFormData, folderUuid, readerIds);
                        }
                    } else {
                        // 新增归档
                        fileUuid = dmsFileServiceFacade.archive(dyFormData, folderUuid, readerIds);
                    }
                    saveArchiveFlowInstanceParameter(event.getFlowInstUuid(), fileUuid, folderUuid);
                }
            } else {
                // 归档夹为空，删除旧的表单数据
                if (dataUuidArchiveParameter != null) {
                    FlowInstanceParameter formUuidArchiveParameter = getArchiveBotFormUuidFlowInstanceParameter(
                            event, archive);
                    if (formUuidArchiveParameter != null) {
                        dyFormFacade.delFullFormData(formUuidArchiveParameter.getValue(),
                                dataUuidArchiveParameter.getValue());
                    }
                    // 删除旧归档流程参数数据
                    deleteArchiveBotFormDataFlowInstanceParameter(dataUuidArchiveParameter);
                }
            }
            // 保存归档单据转换的信息
            saveArchiveBotFormDataFlowInstanceParameter(event, archive, formUuid, dataUuid);
        } else {
            throw new RuntimeException("归档失败，无法取到流程数据转换后的数据！");
        }
    }

    /**
     * @param event
     * @param archive
     * @param formUuid
     * @param dataUuid
     */
    private void saveArchiveBotFormDataFlowInstanceParameter(Event event, Archive archive, String formUuid, String dataUuid) {
        // 表单定义UUID
        FlowInstanceParameter formUuidParameter = new FlowInstanceParameter();
        formUuidParameter.setFlowInstUuid(event.getFlowInstUuid());
        formUuidParameter.setName("archiveId_" + archive.getArchiveId() + "_formUuid");
        List<FlowInstanceParameter> formUuidParameters = flowService.findFlowInstanceParameter(formUuidParameter);
        if (CollectionUtils.isNotEmpty(formUuidParameters)) {
            formUuidParameter = formUuidParameters.get(0);
        }
        formUuidParameter.setValue(formUuid);
        flowService.saveFlowInstanceParameter(formUuidParameter);

        // 表单数据UUID
        FlowInstanceParameter dataUuidParameter = new FlowInstanceParameter();
        dataUuidParameter.setFlowInstUuid(event.getFlowInstUuid());
        dataUuidParameter.setName("archiveId_" + archive.getArchiveId() + "_dataUuid");
        List<FlowInstanceParameter> dataUuidparameters = flowService.findFlowInstanceParameter(dataUuidParameter);
        if (CollectionUtils.isNotEmpty(dataUuidparameters)) {
            dataUuidParameter = dataUuidparameters.get(0);
        }
        dataUuidParameter.setValue(dataUuid);
        flowService.saveFlowInstanceParameter(dataUuidParameter);
    }

    /**
     * @param event
     * @param archive
     * @return
     */
    private FlowInstanceParameter getArchiveBotFormUuidFlowInstanceParameter(Event event, Archive archive) {
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(event.getFlowInstUuid());
        example.setName("archiveId_" + archive.getArchiveId() + "_formUuid");
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isNotEmpty(parameters)) {
            return parameters.get(0);
        }
        return null;
    }

    /**
     * @param event
     * @param archive
     * @return
     */
    private FlowInstanceParameter getArchiveBotDataUuidFlowInstanceParameter(Event event, Archive archive) {
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(event.getFlowInstUuid());
        example.setName("archiveId_" + archive.getArchiveId() + "_dataUuid");
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        if (CollectionUtils.isNotEmpty(parameters)) {
            return parameters.get(0);
        }
        return null;
    }

    /**
     * @param archiveParameter
     */
    private void deleteArchiveBotFormDataFlowInstanceParameter(FlowInstanceParameter archiveParameter) {
        flowService.deleteFlowInstanceParameter(archiveParameter);
    }

    /**
     * 获取规则转换后的数据
     *
     * @param event
     * @param archive
     * @return
     */
    private BotResult getBotResult(Event event, Archive archive) {
        String botRuleId = archive.getBotRuleId();
        Set<BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotFromParam botFromParam = new BotFromParam(event.getDataUuid(), dyFormFacade.getFormIdByFormUuid(event
                .getFormUuid()));
        froms.add(botFromParam);
        BotParam botParam = new BotParam(botRuleId, froms);
        botParam.setFroms(froms);
        HashMap<String, Object> jsonBody = Maps.newHashMap();
        jsonBody.put("event", event);
        jsonBody.put("flowInstUuid", event.getFlowInstUuid());
        jsonBody.put("taskInstUuid", event.getTaskInstUuid());
        jsonBody.put("flowDefUuid", event.getFlowDefUuid());
        jsonBody.put("flowId", event.getFlowId());
        jsonBody.put("title", event.getTitle());
        jsonBody.put("taskId", event.getTaskId());
        jsonBody.put("taskName", event.getTaskName());
        jsonBody.put("formUuid", event.getFormUuid());
        jsonBody.put("dataUuid", event.getDataUuid());
        jsonBody.put("actionType", event.getActionType());
        jsonBody.put("actionCode", event.getActionCode());
        jsonBody.put("actionName", event.getAction());
        botParam.setJsonBody(jsonBody);
        BotResult botResult = botFacadeService.startBot(botParam);
        return botResult;
    }

    /**
     * 脚本归档
     *
     * @param event
     * @param archive
     */
    private void archiveByScript(Event event, Archive archive) {
        Script script = new Script();
        script.setType(archive.getArchiveScriptType());
        script.setContent(archive.getArchiveScript());
        Map<String, Object> variables = new HashMap<String, Object>();
        WorkFlowScriptHelper.executeEventScript(event, script, variables);
    }

    /**
     * 弹窗由用户确认归档范围
     *
     * @param event
     * @param archive
     */
    private void archiveByUserConfirm(Event event, Archive archive) {
        Set<String> folderUuids = getDestFolderUuidByUserConfirm(event, archive);
        // 规则转换后的表单数据
        DyFormData dyFormData = getBotDyformDataByUserConfirm(event, archive);
        List<String> doneUserIds = taskService.getDoneUserIds(event.getTaskInstUuid());
        for (String folderUuid : folderUuids) {
            String fileUuid = dmsFileServiceFacade.archive(dyFormData, folderUuid, doneUserIds);
            saveArchiveFlowInstanceParameter(event.getFlowInstUuid(), fileUuid, folderUuid);
        }
    }

    /**
     * @param event
     * @param archive
     * @return
     */
    private Set<String> getDestFolderUuidByUserConfirm(Event event, Archive archive) {
        TaskData taskData = event.getTaskData();
        String archiveFolderUuid = taskData.getArchiveFolderUuid(event.getPreTaskId());
        if (StringUtils.isBlank(archiveFolderUuid)) {
            List<String> rootFolderUuids = Arrays.asList(StringUtils.split(archive.getDestFolderUuid(),
                    Separator.SEMICOLON.getValue()));
            throw new ChooseArchiveFolderException(event.getPreTaskId(), event.getPreTaskId(), rootFolderUuids);
        }
        Set<String> folderUuuiSet = Sets.newHashSet();
        folderUuuiSet.addAll(Arrays.asList(StringUtils.split(archiveFolderUuid, Separator.SEMICOLON.getValue())));
        return folderUuuiSet;
    }

    /**
     * @param event
     * @param archive
     * @return
     */
    @SuppressWarnings("unchecked")
    private DyFormData getBotDyformDataByUserConfirm(Event event, Archive archive) {
        DyFormData dyFormData = event.getDyFormData();
        if (StringUtils.isNotBlank(archive.getBotRuleId())) {
            BotResult botResult = getBotResult(event, archive);
            Object data = botResult.getData();
            dyFormData = (DyFormData) botResult.getDyformData();
            if (data instanceof Map) {
                Map<String, Object> formData = (Map<String, Object>) data;
                String formUuid = (String) formData.get("form_uuid");
                String dataUuid = botResult.getDataUuid();
                if (dyFormData == null) {
                    dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
                }
                // 保存归档单据转换的信息
                saveArchiveBotFormDataFlowInstanceParameter(event, archive, formUuid, dataUuid);
            } else {
                throw new RuntimeException("归档失败，无法取到流程数据转换后的数据！");
            }
        }
        return dyFormData;
    }

}
