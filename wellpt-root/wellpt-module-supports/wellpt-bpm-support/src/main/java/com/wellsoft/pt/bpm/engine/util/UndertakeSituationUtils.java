/*
 * @(#)2019年3月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.google.common.base.CaseFormat;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.context.util.tree.TreeUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.element.ButtonElement;
import com.wellsoft.pt.bpm.engine.element.ColumnElement;
import com.wellsoft.pt.bpm.engine.element.ParallelGatewayElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumn;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicColumnValue;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.query.FlowShareDataQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService;
import com.wellsoft.pt.bpm.engine.support.FlowShareItem;
import com.wellsoft.pt.bpm.engine.support.FlowShareRowData;
import com.wellsoft.pt.bpm.engine.support.NewFlow;
import com.wellsoft.pt.bpm.engine.timer.support.TimerHelper;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;

import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月12日.1	zhulh		2019年3月12日		Create
 * </pre>
 * @date 2019年3月12日
 */
public class UndertakeSituationUtils {

    private static final String COLUMN_TYPE_ATTACH = "attach";

    private static List<String> NO_MAJOR_ACTIONS = Lists.newArrayList();

    static {
        NO_MAJOR_ACTIONS.add("add-subflow");
        NO_MAJOR_ACTIONS.add("add-major-flow");
        NO_MAJOR_ACTIONS.add("add-minor-flow");
    }

    /**
     * 判断是否主办人员
     *
     * @param userDetails
     * @param taskInstUuid
     * @param queryItems
     * @return
     */
    public static boolean isMajorUser(UserDetails userDetails, String taskInstUuid,
                                      Collection<FlowShareDataQueryItem> queryItems) {
        Map<String, FlowShareDataQueryItem> shareDataMap = ConvertUtils.convertElementToMap(queryItems, "taskInstUuid");
        FlowShareDataQueryItem flowShareDataQueryItem = shareDataMap.get(taskInstUuid);
        if (flowShareDataQueryItem == null || !Boolean.TRUE.equals(flowShareDataQueryItem.getIsMajor())) {
            return false;
        }
        return isMajorUser(userDetails, taskInstUuid);
    }

    /**
     * 判断是否主办人员
     *
     * @param userDetails
     * @param taskInstUuid
     * @return
     */
    public static boolean isMajorUser(UserDetails userDetails, String taskInstUuid) {
        FlowPermissionEvaluator flowPermissionEvaluator = ApplicationContextHolder
                .getBean(FlowPermissionEvaluator.class);
        return flowPermissionEvaluator.hasPermission(userDetails, taskInstUuid, AclPermission.TODO);
    }

    /**
     * 判断是否跟踪人员
     *
     * @param userDetails
     * @param taskId
     * @param flowInstUuid
     * @return
     */
    public static boolean isFollowUpUser(UserDetails userDetails, String taskId, String flowInstUuid) {
        String userId = userDetails.getUserId();
        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
        String name = FlowConstant.SUB_FLOW.KEY_FOLLOW_UP_USERS + Separator.UNDERLINE.getValue() + taskId;
        FlowInstanceParameter example = new FlowInstanceParameter();
        example.setFlowInstUuid(flowInstUuid);
        example.setName(name);
        List<FlowInstanceParameter> parameters = flowService.findFlowInstanceParameter(example);
        for (FlowInstanceParameter flowInstanceParameter : parameters) {
            List<String> userIds = Arrays
                    .asList(StringUtils.split(flowInstanceParameter.getValue(), Separator.SEMICOLON.getValue()));
            if (userIds.contains(userId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param parallelGatewayElement
     * @param isMajor
     * @param isSupervise
     * @return
     */
    public static List<CustomDynamicButton> getUndertakeSituationButtons(ParallelGatewayElement parallelGatewayElement,
                                                                         boolean isMajor, boolean isSupervise) {
        List<CustomDynamicButton> undertakeSituationButtons = Lists.newArrayList();
        List<ButtonElement> buttonElements = parallelGatewayElement.getUndertakeSituationButtons();
        for (ButtonElement buttonElement : buttonElements) {
            String id = buttonElement.getId();
            if (!(isMajor || isSupervise) && NO_MAJOR_ACTIONS.contains(id)) {
                continue;
            }
            CustomDynamicButton button = new CustomDynamicButton();
            button.setId(id);
            button.setName(StringUtils.isNotBlank(buttonElement.getNewName()) ? buttonElement.getNewName()
                    : buttonElement.getName());
            button.setClassName("btn-" + StringUtils.replace(id, "_", "-"));
            undertakeSituationButtons.add(button);
        }
        return undertakeSituationButtons;
    }

    /**
     * @param subTaskNode
     * @param isMajor
     * @param isSupervise
     * @return
     */
    public static List<CustomDynamicButton> getUndertakeSituationButtons(FlowInstance parentFlowInstance,
                                                                         SubTaskNode subTaskNode, boolean isMajor, boolean isSupervise, boolean isMainProcess) {
        List<CustomDynamicButton> undertakeSituationButtons = Lists.newArrayList();
        List<ButtonElement> buttonElements = subTaskNode.getUndertakeSituationButtons();
        CustomDynamicButton closeSubViewBtn = null;
        CustomDynamicButton allowSubViewBtn = null;
        for (ButtonElement buttonElement : buttonElements) {
            String id = buttonElement.getId();
            if (!(isMajor || isSupervise) && NO_MAJOR_ACTIONS.contains(id)) {
                continue;
            }

            CustomDynamicButton button = new CustomDynamicButton();
            button.setId(id);
            button.setUuid(buttonElement.getUuid());
            button.setName(StringUtils.isNotBlank(buttonElement.getNewName()) ? buttonElement.getNewName()
                    : buttonElement.getName());
            button.setClassName("btn-" + StringUtils.replace(id, "_", "-"));
            /**
             * 关闭子流程查看本流程 允许子流程查看本流程
             * 按钮定义文件 subflowProperties.js
             */
            if ("closeSubView".equals(id)) {
                closeSubViewBtn = button;
                continue;
            }
            if ("allowSubView".equals(id)) {
                allowSubViewBtn = button;
                continue;
            }
            undertakeSituationButtons.add(button);
        }

        JSONObject viewMainFlowJson = JSONObject.fromObject(parentFlowInstance.getViewMainFlowJson());
        String childLookParent = "0";
        if (viewMainFlowJson.isEmpty()) {
            childLookParent = subTaskNode.getChildLookParent();
        } else {
            childLookParent = viewMainFlowJson.getString(subTaskNode.getId());
        }
        // 是主流程，允许主流程更改查看权限
        if (isMainProcess && "1".equals(subTaskNode.getParentSetChild())) {
            if (closeSubViewBtn != null) {
                if (!"1".equals(childLookParent)) {
                    closeSubViewBtn.setClassName(closeSubViewBtn.getClassName() + " hide");
                }
                undertakeSituationButtons.add(closeSubViewBtn);
            }
            if (allowSubViewBtn != null) {
                undertakeSituationButtons.add(allowSubViewBtn);
                if ("1".equals(childLookParent)) {
                    allowSubViewBtn.setClassName(allowSubViewBtn.getClassName() + " hide");
                }
            }
        }
        return undertakeSituationButtons;
    }

    /**
     * @param parallelGatewayElement
     * @return
     */
    public static List<CustomDynamicColumn> getUndertakeSituationColumns(
            ParallelGatewayElement parallelGatewayElement) {
        List<CustomDynamicColumn> undertakeSituationColumns = Lists.newArrayList();
        List<ColumnElement> columnElements = parallelGatewayElement.getUndertakeSituationColumns();
        int extendColumnIndex = 0;
        for (ColumnElement columnElement : columnElements) {
            CustomDynamicColumn column = new CustomDynamicColumn();
            column.setType(columnElement.getType());
            column.setIndex(columnElement.getIndex());
            column.setName(columnElement.getName());
            String className = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnElement.getIndex());
            if (StringUtils.equals(columnElement.getType(), ColumnElement.TYPE_EXTEND)) {
                extendColumnIndex++;
                column.setClassName("col-custom col-custom-" + extendColumnIndex + " col-"
                        + StringUtils.replace(className, "_", "-"));
            } else {
                column.setClassName("col-" + StringUtils.replace(className, "_", "-"));
            }
            column.setConfiguration(columnElement.getConfiguration());
            undertakeSituationColumns.add(column);
        }
        return undertakeSituationColumns;
    }

    /**
     * @param subTaskNode
     * @return
     */
    public static List<CustomDynamicColumn> getUndertakeSituationColumns(SubTaskNode subTaskNode) {
        List<CustomDynamicColumn> undertakeSituationColumns = Lists.newArrayList();
        List<ColumnElement> columnElements = subTaskNode.getUndertakeSituationColumns();
        getIndexEqualExtraColumn(columnElements);
        int extendColumnIndex = 0;
        for (ColumnElement columnElement : columnElements) {
            CustomDynamicColumn column = new CustomDynamicColumn();
            column.setType(columnElement.getType());
            column.setIndex(columnElement.getIndex());
            column.setName(columnElement.getName());
            column.setUuid(columnElement.getUuid());
            String className = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnElement.getIndex());
            if (StringUtils.equals(columnElement.getType(), ColumnElement.TYPE_EXTEND)) {
                extendColumnIndex++;
                column.setClassName("col-custom col-custom-" + extendColumnIndex + " col-"
                        + StringUtils.replace(className, "_", "-"));
            } else {
                column.setClassName("col-" + StringUtils.replace(className, "_", "-"));
            }
            column.setConfiguration(columnElement.getConfiguration());
            undertakeSituationColumns.add(column);
        }
        return undertakeSituationColumns;
    }

    /**
     * @param parallelGatewayElement
     * @return
     */
    public static List<ColumnElement> getExtendColumns(ParallelGatewayElement parallelGatewayElement) {
        List<ColumnElement> extendColumns = Lists.newArrayList();
        List<ColumnElement> columnElements = parallelGatewayElement.getUndertakeSituationColumns();
        for (ColumnElement columnElement : columnElements) {
            if (ColumnElement.TYPE_EXTEND.equals(columnElement.getType())) {
                extendColumns.add(columnElement);
            }
        }
        return extendColumns;
    }

    /**
     * @param subTaskNode
     * @return
     */
    public static List<ColumnElement> getExtendColumns(SubTaskNode subTaskNode) {
        List<ColumnElement> extendColumns = Lists.newArrayList();
        List<ColumnElement> columnElements = subTaskNode.getUndertakeSituationColumns();

        getIndexEqualExtraColumn(columnElements);

        for (ColumnElement columnElement : columnElements) {
            if (ColumnElement.TYPE_EXTEND.equals(columnElement.getType())) {
                extendColumns.add(columnElement);
            }
        }
        return extendColumns;
    }

    /**
     * @param fieldName
     * @param flowShareItem
     * @param subflowDyformDataMap
     * @return
     */
    public static Object getFieldValue(String fieldName, FlowShareItem flowShareItem,
                                       Map<String, DyFormData> subflowDyformDataMap) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        String formUuid = flowShareItem.getFormUuid();
        String dataUuid = flowShareItem.getDataUuid();
        // 分发中的子流程没有表单数据
        if (StringUtils.isBlank(formUuid) || StringUtils.isBlank(dataUuid)) {
            return null;
        }
        DyFormData dyFormData = subflowDyformDataMap.get(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            subflowDyformDataMap.put(dataUuid, dyFormData);
        }

        // 列值
        CustomDynamicColumnValue columnValue = new CustomDynamicColumnValue();
        if (dyFormData.isFieldExist(fieldName)) {
            List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
            Map<String, DyformFieldDefinition> fieldDefinitionMap = ConvertUtils.convertElementToMap(fieldDefinitions,
                    "fieldName");
            DyformFieldDefinition dyformFieldDefinition = fieldDefinitionMap.get(fieldName);
            if (dyformFieldDefinition != null) {
                if (DyFormConfig.InputModeUtils.isInputModeEqAttach(dyformFieldDefinition.getInputMode())) {
                    columnValue.setType(COLUMN_TYPE_ATTACH);
                }
            }
            Object fieldValue = dyFormData.getFieldValue(fieldName);
            columnValue.setValue(fieldValue != null ? fieldValue : StringUtils.EMPTY);
        }
        return columnValue;
    }

    /**
     * @param flowShareItem
     * @param columnElements
     * @return
     */
    public static FlowShareRowData convertRowData(FlowShareItem flowShareItem, List<CustomDynamicColumn> columns) {
        FlowShareRowData rowData = new FlowShareRowData();
        BeanUtils.copyProperties(flowShareItem, rowData);
        rowData.setBelongToTaskId(flowShareItem.getParentTaskId());
        rowData.setBelongToTaskInstUuid(flowShareItem.getParentTaskInstUuid());
        rowData.setBelongToFlowInstUuid(flowShareItem.getParentFlowInstUuid());
        rowData.setTimingState(flowShareItem.getTimingState());
        List<CustomDynamicColumnValue> columnValues = Lists.newArrayList();
        columnValues.add(new CustomDynamicColumnValue("", "currentTaskId", flowShareItem.getCurrentTaskId()));
        AppDefElementI18nService appDefElementI18nService = ApplicationContextHolder.getBean(AppDefElementI18nService.class);
        List<AppDefElementI18nEntity> i18ns = appDefElementI18nService.getI18ns(flowShareItem.getFlowDefId(), "", null, IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
        if (CollectionUtils.isNotEmpty(i18ns)) {
            for (AppDefElementI18nEntity i : i18ns) {
                rowData.getI18ns().put(i.getCode(), i.getContent());
            }
        }
        for (CustomDynamicColumn column : columns) {
            String index = column.getIndex();
            Field field = ReflectionUtils.getAccessibleField(flowShareItem, index);
            if (field != null) {
                Object object = ReflectionUtils.getFieldValue(flowShareItem, index);
                // 反馈时限
                if ("dueTime".equals(index)) {
                    object = ReflectionUtils.getFieldValue(flowShareItem, "dueTimeFormatString");
                    columnValues.add(new CustomDynamicColumnValue(StringUtils.EMPTY, index, object));
                } else if ("resultFiles".equals(index)) {
                    // 办理附件
                    columnValues.add(new CustomDynamicColumnValue(COLUMN_TYPE_ATTACH, index, object));
                } else {
                    columnValues.add(new CustomDynamicColumnValue(StringUtils.EMPTY, index, object));
                }
            } else if (flowShareItem.getExtras().containsKey(index)) {
                CustomDynamicColumnValue columnValue = null;
                if (flowShareItem.getExtras().get(index) instanceof String) {
                    columnValue = new CustomDynamicColumnValue();
                    columnValue.setValue("");
                } else {
                    columnValue = (CustomDynamicColumnValue) flowShareItem.getExtras().get(index);
                }

                columnValue.setType(column.getType());
                columnValue.setIndex(index);

                columnValues.add(columnValue);
            }
        }
        rowData.setColumnValues(columnValues);
        return rowData;
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     */
    public static void setTodoName(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem) {
        String branchType = shareDataQueryItem.getBranchType();
        if (FlowConstant.BRANCH_TYPE.MAJOR.equals(branchType)) {
            flowShareItem.setTodoName("主办 " + shareDataQueryItem.getTodoName());
            flowShareItem.setFlowLabelId("flowLabel_major");
        } else if (FlowConstant.BRANCH_TYPE.MINOR.equals(branchType)) {
            // flowShareItem.setTodoName("协办 " + shareDataQueryItem.getTodoName());
        }
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     * @param newFlow
     */
    public static void setTodoName(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem,
                                   NewFlow newFlow) {
        if (newFlow != null && StringUtils.isNotBlank(newFlow.getLabel())) {
            flowShareItem.setFlowLabel(newFlow.getLabel());
            if (StringUtils.isNotBlank(newFlow.getLabel())) {
                flowShareItem.setFlowLabelId("flowLabel_" + newFlow.getId());
            }
//            flowShareItem.setTodoName(label + " " + shareDataQueryItem.getTodoName());
        }
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     */
    public static void setCurrentTaskName(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem) {
        Integer completionState = shareDataQueryItem.getCompletionState();
        if (completionState == null) {
            completionState = 0;
        }
        switch (completionState) {
            case 0:
                if (StringUtils.isBlank(flowShareItem.getCurrentTaskName())) {
                    flowShareItem.setCurrentTodoUserName(StringUtils.EMPTY);
                    flowShareItem.setCurrentTaskName(StringUtils.EMPTY);
                }
                break;
            case 1:
                if (StringUtils.isBlank(flowShareItem.getCurrentTodoUserName())) {
                    flowShareItem.setCurrentTodoUserName(StringUtils.EMPTY);
                    flowShareItem.setCurrentTaskName("结束");
                }
                break;
            case 2:
                if (StringUtils.isBlank(flowShareItem.getCurrentTodoUserName())) {
                    flowShareItem.setCurrentTodoUserName(StringUtils.EMPTY);
                    flowShareItem.setCurrentTaskName("终止");
                }
                break;
            case 3:
                if (StringUtils.isBlank(flowShareItem.getCurrentTodoUserName())) {
                    flowShareItem.setCurrentTodoUserName(StringUtils.EMPTY);
                    flowShareItem.setCurrentTaskName("主流程撤销");
                }
                break;
            case 4:
                if (StringUtils.isBlank(flowShareItem.getCurrentTodoUserName())) {
                    flowShareItem.setCurrentTodoUserName(StringUtils.EMPTY);
                    flowShareItem.setCurrentTaskName("退回主流程");
                }
                break;
            default:
                if (StringUtils.isBlank(flowShareItem.getCurrentTodoUserName())) {
                    flowShareItem.setCurrentTodoUserName(StringUtils.EMPTY);
                    flowShareItem.setCurrentTaskName("结束");
                }
                break;
        }
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     * @param taskActivities
     * @param taskFormAttachmentLogs
     */
    @SuppressWarnings("unchecked")
    public static void setResultFiles(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem,
                                      List<TaskActivity> taskActivities, List<TaskFormAttachmentLog> taskFormAttachmentLogs) {
        String belongToTaskInstUuid = shareDataQueryItem.getBelongToTaskInstUuid();
        String branchTaskInstUuid = shareDataQueryItem.getTaskInstUuid();
        List<TaskActivity> branchTaskActivities = filterBranchTaskActivity(belongToTaskInstUuid, branchTaskInstUuid,
                taskActivities);
        List<TaskFormAttachmentLog> taskActivityAttachmentLogs = filterTskActivityAttachmentLogs(branchTaskActivities,
                taskFormAttachmentLogs);
        Map<String, Object> resultFileMap = Maps.newLinkedHashMap();
        // 从附件日志提取提交的附件
        for (TaskFormAttachmentLog taskFormAttachmentLog : taskActivityAttachmentLogs) {
            String operateType = taskFormAttachmentLog.getOperateType();
            Map<String, Object> values = JsonUtils.json2Object(taskFormAttachmentLog.getContent(), LinkedHashMap.class);
            // 新增
            if (EnumOperateType.NEW.getValue().equals(operateType)) {
                resultFileMap.putAll(values);
            } else {
                // 删除
                for (String key : values.keySet()) {
                    resultFileMap.remove(key);
                }
            }
        }

        // 附件信息组装成LogicFileInfo列表
        List<LogicFileInfo> resultFiles = Lists.newArrayList();
        for (Entry<String, Object> entry : resultFileMap.entrySet()) {
            LogicFileInfo logicFileInfo = new LogicFileInfo();
            logicFileInfo.setFileID(entry.getKey());
            logicFileInfo.setFileName(ObjectUtils.toString(entry.getValue()));
            resultFiles.add(logicFileInfo);
        }
        flowShareItem.setResultFiles(resultFiles);
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     * @param taskFormAttachmentMap 环节表单附件集合
     */
    @SuppressWarnings("unchecked")
    public static void setResultFiles(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem,
                                      Map<String, List<TaskFormAttachment>> taskFormAttachmentMap) {
        List<TaskFormAttachment> taskFormAttachments = taskFormAttachmentMap.get(shareDataQueryItem.getFlowInstUuid());
        if (taskFormAttachments == null) {
            flowShareItem.setResultFiles(Lists.newArrayList());
            return;
        }
        Collections.sort(taskFormAttachments, IdEntityComparators.CREATE_TIME_ASC);

        // 附件信息组装成LogicFileInfo列表
        List<LogicFileInfo> resultFiles = Lists.newArrayList();
        for (TaskFormAttachment taskFormAttachment : taskFormAttachments) {
            Map<String, Object> resultFileMap = JsonUtils.json2Object(taskFormAttachment.getContent(),
                    LinkedHashMap.class);
            for (Entry<String, Object> entry : resultFileMap.entrySet()) {
                LogicFileInfo logicFileInfo = new LogicFileInfo();
                logicFileInfo.setFileID(entry.getKey());
                logicFileInfo.setFileName(ObjectUtils.toString(entry.getValue()));
                resultFiles.add(logicFileInfo);
            }
        }
        flowShareItem.setResultFiles(resultFiles);
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     */
    @SuppressWarnings("unchecked")
    public static void setResultFiles(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem) {
        TaskFormAttachmentService taskFormAttachmentService = ApplicationContextHolder
                .getBean(TaskFormAttachmentService.class);
        List<TaskFormAttachment> taskFormAttachments = taskFormAttachmentService
                .getByFlowInstUuid(shareDataQueryItem.getFlowInstUuid());
        Collections.sort(taskFormAttachments, IdEntityComparators.CREATE_TIME_ASC);

        // 附件信息组装成LogicFileInfo列表
        List<LogicFileInfo> resultFiles = Lists.newArrayList();
        for (TaskFormAttachment taskFormAttachment : taskFormAttachments) {
            Map<String, Object> resultFileMap = JsonUtils.json2Object(taskFormAttachment.getContent(),
                    LinkedHashMap.class);
            for (Entry<String, Object> entry : resultFileMap.entrySet()) {
                LogicFileInfo logicFileInfo = new LogicFileInfo();
                logicFileInfo.setFileID(entry.getKey());
                logicFileInfo.setFileName(ObjectUtils.toString(entry.getValue()));
                resultFiles.add(logicFileInfo);
            }
        }
        flowShareItem.setResultFiles(resultFiles);

        // MongoFileService mongoFileService =
        // ApplicationContextHolder.getBean(MongoFileService.class);
        // List<MongoFileEntity> mongoFileEntities =
        // mongoFileService.getFilesFromFolder(shareDataQueryItem.getDataUuid(),
        // null);
        // if (CollectionUtils.isNotEmpty(mongoFileEntities)) {
        // List<LogicFileInfo> resultFiles = Lists.newArrayList();
        // for (MongoFileEntity mongoFileEntity : mongoFileEntities) {
        // resultFiles.add(mongoFileEntity.getLogicFileInfo());
        // }
        // flowShareItem.setResultFiles(resultFiles);
        // }
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     */
    public static void setDueTimeFormatString(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem) {
        Date dueTime = shareDataQueryItem.getDueTime();
        if (dueTime == null) {
            flowShareItem.setDueTimeFormatString("—");
        } else {
            flowShareItem.setDueTimeFormatString(DateUtils.formatDate(flowShareItem.getDueTime()));
        }
    }

    /**
     * @param shareDataQueryItem
     * @param flowShareItem
     */
    public static void setRemainingTime(FlowShareDataQueryItem shareDataQueryItem, FlowShareItem flowShareItem,
                                        boolean isLatest) {
        Integer completionState = shareDataQueryItem.getCompletionState();
        if (completionState == null) {
            completionState = 0;
        }
        if (isLatest && completionState == 0) {
            Date dueTime = shareDataQueryItem.getDueTime();
            Integer limitUnit = shareDataQueryItem.getLimitUnit();
            if (limitUnit == null) {
                limitUnit = TimerUnit.WORKING_DAY;
            }
            if (dueTime == null) {
                flowShareItem.setRemainingTime("—");
            } else {
                String timerUuid = shareDataQueryItem.getTimerUuid();
                if (StringUtils.isNotBlank(timerUuid)) {
                    TsTimerFacadeService timerFacadeService = ApplicationContextHolder
                            .getBean(TsTimerFacadeService.class);
                    int remainingTaskLimitTime = Double.valueOf(timerFacadeService.getRemainingTimeLimit(timerUuid))
                            .intValue();
                    String timerUnitName = timerFacadeService.getTimeLimitNameInMinute(timerUuid);
                    if (remainingTaskLimitTime <= 0) {
                        flowShareItem.setRemainingTime("0" + timerUnitName);
                    } else {
                        flowShareItem.setRemainingTime(remainingTaskLimitTime + timerUnitName);
                    }
                } else {
                    int remainingTaskLimitTime = TimerHelper.calculateRemainingTaskLimitTime(dueTime, limitUnit);
                    String timerUnitName = TimerHelper.getTimerUnitName(limitUnit);
                    if (remainingTaskLimitTime <= 0) {
                        flowShareItem.setRemainingTime("0" + timerUnitName);
                    } else {
                        flowShareItem.setRemainingTime(remainingTaskLimitTime + timerUnitName);
                    }
                }
            }
        } else {
            flowShareItem.setRemainingTime("—");
        }
    }

    /**
     * @param subTaskNode
     * @return
     */
    public static Comparator<? super FlowShareDataQueryItem> getFlowShareDataQueryItemComparator(
            SubTaskNode subTaskNode) {
        Map<String, Integer> flowIdOrderMap = Maps.newHashMap();
        if (CollectionUtils.isEmpty(subTaskNode.getNewFlows())) {
            return new FlowShareDataQueryItemComparator();
        }
        int order = 1;
        for (NewFlow newFlow : subTaskNode.getNewFlows()) {
            flowIdOrderMap.put(newFlow.getFlowId(), order++);
        }
        return new FlowShareDataQueryItemComparator(flowIdOrderMap);
    }

    /**
     * @return
     */
    public static FlowShareDataQueryItemComparator getFlowShareDataQueryItemComparator() {
        return new FlowShareDataQueryItemComparator();
    }

    /**
     * @param parallelTaskInstUuid
     * @param currentTaskInstUuid
     * @param taskActivities
     * @return
     */
    public static List<TaskActivity> filterBranchTaskActivity(String parallelTaskInstUuid, String currentTaskInstUuid,
                                                              List<TaskActivity> taskActivities) {
        TreeNode activityTree = TreeUtils.buildTree(taskActivities, "taskInstUuid", "preTaskInstUuid",
                new Function<TaskActivity, TreeNode>() {

                    @Override
                    public TreeNode apply(TaskActivity input) {
                        TreeNode treeNode = new TreeNode();
                        treeNode.setId(input.getTaskInstUuid());
                        treeNode.setData(input);
                        return treeNode;
                    }

                });
        Map<String, TaskActivity> taskActivityMap = ConvertUtils.convertElementToMap(taskActivities, "taskInstUuid");

        // 提取分支的活动记录
        List<TaskActivity> branchTaskActivities = Lists.newArrayList();
        TreeNode branchNode = TreeUtils.getTreeNode(activityTree, currentTaskInstUuid);
        while (branchNode != null) {
            String nodeId = branchNode.getId();
            // 到达分发结点，停止提取
            if (StringUtils.equals(parallelTaskInstUuid, nodeId)) {
                break;
            }

            branchTaskActivities.add((TaskActivity) branchNode.getData());

            TaskActivity taskActivity = taskActivityMap.get(nodeId);
            String preTaskInstUuid = taskActivity.getPreTaskInstUuid();
            if (StringUtils.isBlank(preTaskInstUuid)) {
                break;
            }
            branchNode = TreeUtils.getTreeNode(activityTree, preTaskInstUuid);
        }

        // 倒序处理
        Collections.reverse(branchTaskActivities);
        return branchTaskActivities;
    }

    /**
     * @param taskActivities
     * @param taskFormAttachmentLogs
     * @return
     */
    private static List<TaskFormAttachmentLog> filterTskActivityAttachmentLogs(List<TaskActivity> taskActivities,
                                                                               List<TaskFormAttachmentLog> taskFormAttachmentLogs) {
        Map<String, TaskActivity> taskActivityMap = ConvertUtils.convertElementToMap(taskActivities, "taskInstUuid");
        List<TaskFormAttachmentLog> returnTaskFormAttachmentLogs = Lists.newArrayList();
        for (TaskFormAttachmentLog taskFormAttachmentLog : taskFormAttachmentLogs) {
            if (taskActivityMap.containsKey(taskFormAttachmentLog.getTaskInstUuid())) {
                returnTaskFormAttachmentLogs.add(taskFormAttachmentLog);
            }
        }
        return returnTaskFormAttachmentLogs;
    }

    /**
     * //index索引列对应ExtraColumn
     *
     * @param
     * @return void
     **/
    public static void getIndexEqualExtraColumn(List<ColumnElement> columnElements) {
        if (CollectionUtils.isNotEmpty(columnElements)) {
            for (ColumnElement extendColumn : columnElements) {
                if (StringUtils.isNotBlank(extendColumn.getIndex())
                        && StringUtils.isNotBlank(extendColumn.getExtraColumn())
                        && !extendColumn.getIndex().equals(extendColumn.getExtraColumn())) {
                    extendColumn.setIndex(extendColumn.getExtraColumn());
                }
            }
        }
    }

    private static class FlowShareDataQueryItemComparator implements Comparator<FlowShareDataQueryItem> {

        private Map<String, Integer> flowIdOrderMap = Maps.newHashMap();

        /**
         * @param flowIdOrderMap
         */
        public FlowShareDataQueryItemComparator() {
        }

        /**
         * @param flowIdOrderMap
         */
        public FlowShareDataQueryItemComparator(Map<String, Integer> flowIdOrderMap) {
            this.flowIdOrderMap.putAll(flowIdOrderMap);
        }

        /**
         * (non-Javadoc)
         *
         * @see Comparator#compare(Object, Object)
         */
        @Override
        public int compare(FlowShareDataQueryItem o1, FlowShareDataQueryItem o2) {
            // 分发状态比较
            Integer dispatchState1 = o1.getDispatchState();
            Integer dispatchState2 = o2.getDispatchState();
            if (dispatchState1 != null && dispatchState2 != null) {
                if (dispatchState1 == 1 && dispatchState2 == 1) {
                } else if (dispatchState1 == 1 && dispatchState2 != 1) {
                    return -1;
                } else if (dispatchState1 != 1 && dispatchState2 == 1) {
                    return 1;
                } else if (dispatchState1 > dispatchState2) {
                    return -1;
                } else {
                    return 1;
                }
            }
            // 流程定义ID比较
            if (!StringUtils.equals(o1.getFlowDefId(), o2.getFlowDefId())
                    && flowIdOrderMap.containsKey(o1.getFlowDefId()) && flowIdOrderMap.containsKey(o2.getFlowDefId())) {
                return flowIdOrderMap.get(o1.getFlowDefId()).compareTo(flowIdOrderMap.get(o2.getFlowDefId()));
            }
            // 主协、协办比较
            Boolean isMajor1 = o1.getIsMajor();
            Boolean isMajor2 = o2.getIsMajor();
            if (isMajor1 == null || isMajor2 == null) {
                return 0;
            }
            if (isMajor1.equals(isMajor2)) {
                // 完成状态比较
                Integer completionState1 = o1.getCompletionState();
                Integer completionState2 = o2.getCompletionState();
                if (completionState1 == null || completionState2 == null) {
                    return 0;
                }
                return completionState1.compareTo(completionState2);
            }
            return -isMajor1.compareTo(isMajor2);
        }

    }

}
