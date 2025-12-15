/*
 * @(#)2021年3月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.RightUnitElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.element.UnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.parser.FlowDefinitionParser;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.bpm.engine.timer.listener.TaskTimerListener;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.DyShowType;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.timer.dto.TsTimerCategoryDto;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanDto;
import com.wellsoft.pt.timer.enums.EnumTimeLimitType;
import com.wellsoft.pt.timer.enums.EnumTimeLimitUnit;
import com.wellsoft.pt.timer.enums.EnumTimingMode;
import com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2021年3月1日.1	zhulh		2021年3月1日		Create
 * </pre>
 * @date 2021年3月1日
 */
@Service
public class FlowDefinitionUpgradeServiceImpl implements FlowDefinitionUpgradeService {

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private FlowSchemeService flowSchemeService;

    @Autowired
    private FlowSchemaService flowSchemaService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    @Autowired
    private TsTimerConfigFacadeService timerConfigFacadeService;

    @Autowired
    private TsTimerCategoryFacadeService timerCategoryFacadeService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#upgrade2v6_2_3()
     */
    @Override
    public List<String> upgrade2v6_2_3(String flowDefUuid) {
        List<String> updatedFlowInfos = Lists.newArrayList();
        // 获取要升级的流程定义XML
        Map<String, String> toUpgradeFlowDefXmlMap = getUpgradeFlowDefinitionXml(flowDefUuid);

        // 保存要升级的流程定义XML
        for (String toUpgradeFlowDefUuid : toUpgradeFlowDefXmlMap.keySet()) {
            FlowDefinition flowDefinition = saveUpgradeFlowDefinitionXml(toUpgradeFlowDefUuid,
                    toUpgradeFlowDefXmlMap.get(toUpgradeFlowDefUuid));
            updatedFlowInfos.add(flowDefinition.getName() + ":" + flowDefinition.getVersion());
        }
        return updatedFlowInfos;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#getUpgradeFlowDefinitionXml()
     */
    @Transactional(readOnly = true)
    public Map<String, String> getUpgradeFlowDefinitionXml(String flowDefUuid) {
        Map<String, String> toUpgradeFlowDefXmlMap = Maps.newHashMap();
        String hql = null;
        if (StringUtils.isBlank(flowDefUuid)) {
            hql = "from FlowDefinition t1 where t1.flowSchemaUuid in( select t2.uuid from FlowSchema t2 where t2.content not like '%allFormFieldBtns%')";
        } else {
            hql = "from FlowDefinition t1 where t1.uuid='" + flowDefUuid + "'";
        }
        List<FlowDefinition> flowDefinitions = flowDefinitionService.listByHQL(hql, null);
        for (FlowDefinition flowDefinition : flowDefinitions) {
            try {
                upgrade2v6_2_3IfRequired(flowDefinition, toUpgradeFlowDefXmlMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toUpgradeFlowDefXmlMap;
    }

    /**
     * @param flowDefinition
     * @throws Exception
     */
    private void upgrade2v6_2_3IfRequired(FlowDefinition flowDefinition, Map<String, String> toUpgradeFlowDefXmlMap)
            throws Exception {
        boolean hasChanged = false;
        String flowXml = flowSchemeService.getFlowXml(flowDefinition.getUuid());
        FlowDelegate flowDelegate = new FlowDelegate(FlowDefinitionParser.parseFlow(flowXml));
        Document document = FlowDefinitionParser.createDocument(flowXml);
        if (StringUtils.isBlank(flowDefinition.getFormUuid())) {
            return;
        }
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(flowDefinition.getFormUuid());
        if (formDefinition == null) {
            return;
        }
        FormDefinitionHandler formDefinitionHandler = new FormDefinitionHandler(formDefinition.getDefinitionJson(),
                formDefinition.getFormType(), formDefinition.getName(), formDefinition.getpFormUuid());
        // 1、添加canEditForm结点
        if (addCanEditFormIfRequired(flowDelegate, document)) {
            hasChanged = true;
        }
        // 2、添加allFormField结点
        if (allFormFieldIfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 3、添加allFormFieldBtns结点
        if (allFormFieldBtnIfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 4、表单隐藏字段设置到hideFields属性
        if (addHideFieldIfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 5、旧数据从表editFields：formUuid:code_formUuid
        if (addEditFieldsIfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 6、必填域
        if (addNotNullFieldsIfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 7、旧数据附件fileRights
        if (addFileRightsIfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 流程定义变更，加入待升级
        if (hasChanged) {
            toUpgradeFlowDefXmlMap.put(flowDefinition.getUuid(), document.asXML());
        }
    }

    /**
     * @param flowDelegate
     * @param document
     * @return
     */
    private boolean addCanEditFormIfRequired(FlowDelegate flowDelegate, Document document) {
        boolean hasChanged = false;
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            String firstTaskId = flowDelegate.getStartNode().getToID();
            TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
            List<RightUnitElement> rights = taskElement.getRights();
            Element taskNode = (Element) document.selectSingleNode("/flow/tasks/task[@id='" + taskId + "']");
            Element canEditForm = (Element) taskNode.selectSingleNode("canEditForm");
            if (canEditForm == null) {
                for (UnitElement unitElement : rights) {
                    if (StringUtils.equals(unitElement.getValue(), WorkFlowPrivilege.RequiredSignOpinion.getCode())) {
                        if (StringUtils.equals(firstTaskId, node.getId())) {
                            taskNode.add(createCanEditFormElement());
                            taskElement.setCanEditForm("1");
                        } else {
                            taskNode.add(createNotCanEditFormElement());
                        }
                        hasChanged = true;
                    }
                }
            } else if (StringUtils.isBlank(taskElement.getCanEditForm())) {
                if (StringUtils.equals(firstTaskId, node.getId())) {
                    canEditForm.setText("1");
                    taskElement.setCanEditForm("1");
                } else {
                    canEditForm.setText("0");
                }
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    /**
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param document
     * @return
     * @throws Exception
     */
    private boolean allFormFieldIfRequired(FormDefinitionHandler formDefinitionHandler, FlowDelegate flowDelegate,
                                           Document document) throws Exception {

        boolean hasChanged = false;
        // 主表字段
        List<String> formFields = getFormFields(formDefinitionHandler);
        // 从表附件操作按钮
        Map<String, List<String>> subformFieldMap = getSubformFields(formDefinitionHandler);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
            Element taskNode = (Element) document.selectSingleNode("/flow/tasks/task[@id='" + taskId + "']");
            Element allFormFields = (Element) taskNode.selectSingleNode("allFormField");
            if (allFormFields == null) {
                Element element = createAllFormFieldElement();
                taskNode.add(element);
                for (String formField : formFields) {
                    element.add(createFieldElement(formField));
                    UnitElement unit = new UnitElement();
                    unit.setValue(formField);
                    taskElement.getAllFormFields().add(unit);
                    hasChanged = true;
                }
                for (String subformUuid : subformFieldMap.keySet()) {
                    String subformId = formDefinitionHandler.getSubformDefinition(subformUuid).getName();
                    element.add(createFieldElement(subformId));
                    UnitElement unit = new UnitElement();
                    unit.setValue(subformId);
                    taskElement.getAllFormFields().add(unit);
                    hasChanged = true;
                }
                for (String subformUuid : subformFieldMap.keySet()) {
                    List<String> fields = subformFieldMap.get(subformUuid);
                    for (String fieldName : fields) {
                        element.add(createFieldElement(subformUuid + ":" + fieldName));
                        UnitElement unit = new UnitElement();
                        unit.setValue(subformUuid + ":" + fieldName);
                        taskElement.getAllFormFields().add(unit);
                        hasChanged = true;
                    }
                }
            }
        }
        return hasChanged;
    }

    /**
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param document
     * @return
     * @throws Exception
     */
    private boolean allFormFieldBtnIfRequired(FormDefinitionHandler formDefinitionHandler, FlowDelegate flowDelegate,
                                              Document document) throws Exception {
        boolean hasChanged = false;
        // 主表附件操作按钮
        List<String> formFieldBtns = getFormFileFieldBtns(formDefinitionHandler);
        // 从表操作按钮
        Map<String, List<String>> subformBtnCodeMap = getSubformBtnCodes(formDefinitionHandler);
        // 从表附件操作按钮
        Map<String, List<String>> subformFileFieldBtns = getSubformFileFieldBtns(formDefinitionHandler);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            Element taskNode = (Element) document.selectSingleNode("/flow/tasks/task[@id='" + taskId + "']");
            Element allFormFieldBtns = (Element) taskNode.selectSingleNode("allFormFieldBtns");
            if (allFormFieldBtns == null) {
                Element element = createAllFormFieldBtnsElement();
                taskNode.add(element);
                for (String formFieldBtn : formFieldBtns) {
                    element.add(createFieldElement(formFieldBtn));
                    hasChanged = true;
                }
                for (String subformUuid : subformBtnCodeMap.keySet()) {
                    String subformId = formDefinitionHandler.getSubformDefinition(subformUuid).getName();
                    element.add(createFieldElement(
                            subformId + ":" + StringUtils.join(subformBtnCodeMap.get(subformUuid), ";")));
                    hasChanged = true;
                }
                for (String subformUuid : subformFileFieldBtns.keySet()) {
                    String subformId = formDefinitionHandler.getSubformDefinition(subformUuid).getName();
                    List<String> fields = subformFileFieldBtns.get(subformUuid);
                    for (String fieldName : fields) {
                        element.add(createFieldElement(subformId + ":" + fieldName));
                        hasChanged = true;
                    }
                }
            }
        }
        return hasChanged;
    }

    /**
     * @param formHideFields
     * @param subformHideFields
     * @param flowDelegate
     * @param document
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private boolean addEditFieldsIfRequired(FormDefinitionHandler formDefinitionHandler, FlowDelegate flowDelegate,
                                            Document document) throws Exception {
        // 从表操作按钮
        List<String> formEditFields = getFormEditFields(formDefinitionHandler);
        Map<String, List<String>> subformEditFieldMap = getSubformEditFields(formDefinitionHandler);
        Map<String, List<String>> subformBtnFieldMap = getSubformBtnFields(formDefinitionHandler);
        boolean hasChanged = false;
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            TaskForm taskForm = flowDelegate.getTaskForm(taskId);
            TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
            List<Element> editFieldNodes = document.selectNodes("/flow/tasks/task[@id='" + taskId + "']/editFields");
            if (StringUtils.isBlank(taskElement.getCanEditForm())
                    || ("1".equals(taskElement.getCanEditForm()) && MapUtils.isEmpty(taskForm.getEditFieldMap()))) {
                for (String editField : formEditFields) {
                    editFieldNodes.get(0).add(createFieldElement(editField));
                    hasChanged = true;
                }
                for (String subformUuid : subformEditFieldMap.keySet()) {
                    List<String> subformEditFields = subformEditFieldMap.get(subformUuid);
                    for (String subformEditField : subformEditFields) {
                        editFieldNodes.get(0).add(createFieldElement(subformUuid + ":" + subformEditField));
                        hasChanged = true;
                    }
                }
            }

            Map<String, List<String>> editFieldMap = getEditFieldMap(taskElement, formDefinitionHandler, taskForm);
            for (String subformUuid : subformBtnFieldMap.keySet()) {
                List<String> subformBtnFields = subformBtnFieldMap.get(subformUuid);
                List<String> editFields = editFieldMap.get(subformUuid);
                if (editFields == null) {
                    editFields = Lists.newArrayListWithExpectedSize(0);
                }
                subformBtnFields.remove(editFields);
                if (CollectionUtils.isNotEmpty(editFieldNodes)) {
                    for (String subformBtnField : subformBtnFields) {
                        editFieldNodes.get(0).add(createFieldElement(subformUuid + ":" + subformBtnField));
                        hasChanged = true;
                    }
                }
            }
        }
        return hasChanged;
    }

    /**
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param document
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private boolean addNotNullFieldsIfRequired(FormDefinitionHandler formDefinitionHandler, FlowDelegate flowDelegate,
                                               Document document) throws Exception {
        boolean hasChanged = false;
        List<String> formRequiredFields = getFormRequiredFields(formDefinitionHandler);
        Map<String, List<String>> subformRequiredFieldMap = getSubformRequiredFields(formDefinitionHandler);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            TaskForm taskForm = flowDelegate.getTaskForm(taskId);
            TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
            List<Element> notNullFieldsNodes = document
                    .selectNodes("/flow/tasks/task[@id='" + taskId + "']/notNullFields");
            if (StringUtils.isBlank(taskElement.getCanEditForm()) || ("1".equals(taskElement.getCanEditForm()))) {
                Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
                List<String> requiredFields = Lists.newArrayList(formRequiredFields);
                if (notNullFieldMap != null && notNullFieldMap.containsKey(formDefinitionHandler.getFormUuid())) {
                    requiredFields.removeAll(notNullFieldMap.get(formDefinitionHandler.getFormUuid()));
                }
                for (String editField : requiredFields) {
                    notNullFieldsNodes.get(0).add(createFieldElement(editField));
                    hasChanged = true;
                }
                for (String subformUuid : subformRequiredFieldMap.keySet()) {
                    List<String> subformRequiredFields = Lists.newArrayList(subformRequiredFieldMap.get(subformUuid));
                    if (notNullFieldMap != null && notNullFieldMap.containsKey(subformUuid)) {
                        requiredFields.removeAll(notNullFieldMap.get(subformUuid));
                    }
                    for (String subformRequiredField : subformRequiredFields) {
                        notNullFieldsNodes.get(0).add(createFieldElement(subformUuid + ":" + subformRequiredField));
                        hasChanged = true;
                    }
                }
            }
        }
        return hasChanged;
    }

    /**
     * @param taskId
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param taskForm
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getEditFieldMap(TaskElement taskElement,
                                                      FormDefinitionHandler formDefinitionHandler, TaskForm taskForm) throws Exception {
        if (StringUtils.isNotBlank(taskElement.getCanEditForm())) {
            if (!("1".equals(taskElement.getCanEditForm()) && MapUtils.isEmpty(taskForm.getEditFieldMap()))) {
                return taskForm.getEditFieldMap();
            }
        }
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> formEditFields = getFormEditFields(formDefinitionHandler);
        Map<String, List<String>> subformEditFieldMap = getSubformEditFields(formDefinitionHandler);
        Map<String, List<String>> subformBtnFieldMap = getSubformBtnFields(formDefinitionHandler);
        map.put(formDefinitionHandler.getFormUuid(), formEditFields);
        map.putAll(subformEditFieldMap);
        for (String key : subformBtnFieldMap.keySet()) {
            if (map.containsKey(key)) {
                map.get(key).addAll(subformBtnFieldMap.get(key));
            } else {
                map.put(key, subformBtnFieldMap.get(key));
            }
        }
        return map;
    }

    /**
     * @param taskId
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param taskForm
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getHideFieldMap(TaskElement taskElement,
                                                      FormDefinitionHandler formDefinitionHandler, TaskForm taskForm) throws Exception {
        if (StringUtils.isNotBlank(taskElement.getCanEditForm())) {
            if (!("1".equals(taskElement.getCanEditForm()) && MapUtils.isEmpty(taskForm.getHideFieldMap()))) {
                return taskForm.getHideFieldMap();
            }
        }
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> formHideFields = getFormHideFields(formDefinitionHandler);
        Map<String, List<String>> subformHideFieldMap = getSubformHideFields(formDefinitionHandler);
        map.put(formDefinitionHandler.getFormUuid(), formHideFields);
        map.putAll(subformHideFieldMap);
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param document
     * @return
     * @throws Exception
     */
    private boolean addFileRightsIfRequired(FormDefinitionHandler formDefinitionHandler, FlowDelegate flowDelegate,
                                            Document document) throws Exception {
        boolean hasChanged = false;
        // 从表操作按钮
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            Element taskNode = (Element) document.selectSingleNode("/flow/tasks/task[@id='" + taskId + "']");
            Element fileRightNode = (Element) taskNode.selectSingleNode("fileRights");
            if (fileRightNode != null) {
                fileRightNode.clearContent();
            } else if (fileRightNode == null) {
                fileRightNode = createFileRightsElement();
                taskNode.add(fileRightNode);
            }

            TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
            TaskForm taskForm = flowDelegate.getTaskForm(taskId);
            // List<String> fileRights = taskForm.getFileRights();
            // 主表字段
            List<String> formFileRights = getFormFileRightBtns(formDefinitionHandler, taskElement, taskForm);
            // formFileRights = extractAddFileRight(fileRights, formFileRights);
            for (String formFileRight : formFileRights) {
                fileRightNode.add(createFieldElement(formFileRight));
                hasChanged = true;
            }
            // 从表字段
            Map<String, List<String>> subformFileRightBtns = getSubformFileRightBtns(formDefinitionHandler, taskElement,
                    taskForm);
            for (String subformUuid : subformFileRightBtns.keySet()) {
                List<String> fields = subformFileRightBtns.get(subformUuid);
                // fields = extractSubformAddFileRight(subformUuid, fileRights, fields);
                for (String fieldName : fields) {
                    fileRightNode.add(createFieldElement(subformUuid + ":" + fieldName));
                    hasChanged = true;
                }
            }
        }
        return hasChanged;
    }

    /**
     * @param fileRights
     * @param formFileRights
     * @return
     */
    private List<String> extractAddFileRight(List<String> fileRights, List<String> formFileRights) {
        if (CollectionUtils.isEmpty(fileRights)) {
            return formFileRights;
        }
        List<String> retFormFileRights = Lists.newArrayList();
        Set<String> fileRightFields = Sets.newHashSet();
        for (String fileRight : fileRights) {
            String fielName = StringUtils.split(fileRight, ":")[0];
            fileRightFields.add(fielName);
        }
        for (String formFileField : formFileRights) {
            String fielName = StringUtils.split(formFileField, ":")[0];
            if (!fileRightFields.contains(fielName)) {
                retFormFileRights.add(formFileField);
            }
        }
        return retFormFileRights;
    }

    /**
     * @param subformUuid
     * @param fileRights
     * @param fields
     * @return
     */
    private List<String> extractSubformAddFileRight(String subformUuid, List<String> fileRights,
                                                    List<String> formFileRights) {
        if (CollectionUtils.isEmpty(fileRights)) {
            return formFileRights;
        }
        List<String> retFormFileRights = Lists.newArrayList();
        Set<String> fileRightFields = Sets.newHashSet();
        for (String fileRight : fileRights) {
            String[] parts = StringUtils.split(fileRight, ":");
            if (StringUtils.equals(subformUuid, parts[0])) {
                fileRightFields.add(parts[1]);
            }
        }
        for (String formFileField : formFileRights) {
            String fielName = StringUtils.split(formFileField, ":")[0];
            if (!fileRightFields.contains(fielName)) {
                retFormFileRights.add(formFileField);
            }
        }
        return retFormFileRights;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getSubformBtnFields(FormDefinitionHandler formDefinitionHandler)
            throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                map.put(subformUuid, Lists.<String>newArrayList());
                JSONObject jsonObject = formDefinitionHandler.getSubformDefinitionJson(subformUuid);
                if (jsonObject.has("tableButtonInfo")) {
                    JSONArray tableButtonInfos = jsonObject.getJSONArray("tableButtonInfo");
                    for (int index = 0; index < tableButtonInfos.length(); index++) {
                        JSONObject tableButtonInfo = tableButtonInfos.getJSONObject(index);
                        if (tableButtonInfo.has("position") && tableButtonInfo.getJSONArray("position").length() <= 0) {
                            continue;
                        }
                        String btnCode = tableButtonInfo.getString("code");
                        map.get(subformUuid).add(btnCode + "_" + subformUuid);
                    }
                }
            }
        }
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getSubformBtnCodes(FormDefinitionHandler formDefinitionHandler) throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                map.put(subformUuid, Lists.<String>newArrayList());
                JSONObject jsonObject = formDefinitionHandler.getSubformDefinitionJson(subformUuid);
                if (jsonObject.has("tableButtonInfo")) {
                    JSONArray tableButtonInfos = jsonObject.getJSONArray("tableButtonInfo");
                    for (int index = 0; index < tableButtonInfos.length(); index++) {
                        JSONObject tableButtonInfo = tableButtonInfos.getJSONObject(index);
                        String btnCode = tableButtonInfo.getString("code");
                        map.get(subformUuid).add(btnCode);
                    }
                }
            }
        }
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getSubformFileFieldBtns(FormDefinitionHandler formDefinitionHandler)
            throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        for (String subformUuid : subformUuids) {
            DyFormFormDefinition subformDefinition = dyFormFacade.getFormDefinition(subformUuid);
            if (subformDefinition == null) {
                continue;
            }
            FormDefinitionHandler subformDefinitionHandler = new FormDefinitionHandler(
                    subformDefinition.getDefinitionJson(), subformDefinition.getFormType(), subformDefinition.getName(),
                    subformDefinition.getpFormUuid());
            map.put(subformUuid, getFormFileFieldBtns(subformDefinitionHandler));
        }
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @throws Exception
     */
    private Map<String, List<String>> getSubformFileRightBtns(FormDefinitionHandler formDefinitionHandler,
                                                              TaskElement taskElement, TaskForm taskForm) throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        for (String subformUuid : subformUuids) {
            DyFormFormDefinition subformDefinition = dyFormFacade.getFormDefinition(subformUuid);
            if (subformDefinition == null) {
                continue;
            }
            FormDefinitionHandler subformDefinitionHandler = new FormDefinitionHandler(
                    subformDefinition.getDefinitionJson(), subformDefinition.getFormType(), subformDefinition.getName(),
                    subformDefinition.getpFormUuid());
            map.put(subformUuid, getFormFileRightBtns(subformDefinitionHandler, taskElement, taskForm));
        }
        return map;
    }

    /**
     * @param formHideFields
     * @param subformHideFields
     * @param flowDelegate
     * @param document
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private boolean addHideFieldIfRequired(FormDefinitionHandler formDefinitionHandler, FlowDelegate flowDelegate,
                                           Document document) throws Exception {
        boolean hasChanged = false;
        List<String> formHideFields = getFormHideFields(formDefinitionHandler);
        Map<String, List<String>> subformHideFields = getSubformHideFields(formDefinitionHandler);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            Element allFormFieldNode = (Element) document
                    .selectSingleNode("/flow/tasks/task[@id='" + taskId + "']/allFormField");
            if (allFormFieldNode == null) {
                allFormFieldNode = createAllFormFieldElement();
            }
            List<Element> hideFieldNodes = document.selectNodes("/flow/tasks/task[@id='" + taskId + "']/hideFields");
            TaskForm taskForm = flowDelegate.getTaskForm(taskId);
            List<String> allFormFields = taskForm.getAllFormFields();
            Map<String, List<String>> hideFieldMap = taskForm.getHideFieldMap();
            for (String key : hideFieldMap.keySet()) {
                List<String> hideFields = hideFieldMap.get(key);
                if (taskForm.isMainForm(key)) {
                    List<String> taskFormHideFields = Lists.newArrayList(formHideFields);
                    taskFormHideFields.removeAll(hideFields);
                    if (CollectionUtils.isNotEmpty(hideFieldNodes)) {
                        for (String mainFormHideField : taskFormHideFields) {
                            hideFieldNodes.get(0).add(createFieldElement(mainFormHideField));
                            if (!allFormFields.contains(mainFormHideField)) {
                                allFormFieldNode.add(createFieldElement(mainFormHideField));
                            }
                            hasChanged = true;
                        }
                    }
                } else if (subformHideFields.containsKey(key)) {
                    List<String> subformHideFieldList = Lists.newArrayList(subformHideFields.get(key));
                    subformHideFieldList.removeAll(hideFields);
                    if (CollectionUtils.isNotEmpty(hideFieldNodes)) {
                        for (String subformHideField : subformHideFieldList) {
                            String subformField = key + ":" + subformHideField;
                            hideFieldNodes.get(0).add(createFieldElement(subformField));
                            if (!allFormFields.contains(subformField)) {
                                allFormFieldNode.add(createFieldElement(subformField));
                            }
                            hasChanged = true;
                        }
                    }
                }
            }
        }
        return hasChanged;
    }

    /**
     * @return
     */
    private Element createCanEditFormElement() {
        Element canEditForm = DocumentFactory.getInstance().createElement("canEditForm");
        canEditForm.addAttribute("type", "32");
        canEditForm.setText("1");
        return canEditForm;
    }

    /**
     * @return
     */
    private Element createNotCanEditFormElement() {
        Element canEditForm = DocumentFactory.getInstance().createElement("canEditForm");
        canEditForm.addAttribute("type", "32");
        canEditForm.setText("0");
        return canEditForm;
    }

    /**
     * @return
     */
    private Element createAllFormFieldBtnsElement() {
        Element allFormFieldBtns = DocumentFactory.getInstance().createElement("allFormFieldBtns");
        return allFormFieldBtns;
    }

    /**
     * @return
     */
    private Element createAllFormFieldElement() {
        return DocumentFactory.getInstance().createElement("allFormField");
    }

    /**
     * @return
     */
    private Element createFileRightsElement() {
        return DocumentFactory.getInstance().createElement("fileRights");
    }

    /**
     * @param hideField
     * @return
     */
    private Element createFieldElement(String hideField) {
        Element unit = DocumentFactory.getInstance().createElement("unit");
        unit.addAttribute("type", "32");
        unit.setText(hideField);
        return unit;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private List<String> getFormFileFieldBtns(FormDefinitionHandler formDefinitionHandler) throws Exception {
        List<String> fieldBtns = Lists.newArrayList();
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return fieldBtns;
        }
        for (String fieldName : fieldNames) {
            JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null && jsonObject.has("inputMode")) {
                String inputMode = jsonObject.getString("inputMode");
                // 列表附件
                if (DyFormConfig.INPUTMODE_ACCESSORY3.equals(inputMode) && jsonObject.has("secDevBtnIdStr")) {
                    String secDevBtnIdStr = jsonObject.getString("secDevBtnIdStr");
                    if (StringUtils.isNotBlank(secDevBtnIdStr)) {
                        fieldBtns.add(fieldName + ":" + secDevBtnIdStr);
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORY1.equals(inputMode)) {
                    // 图标附件
                    if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                        fieldBtns.add(fieldName + ":15;7;2;13;12;1;4;5;14;8;21;9;3;10;11");
                    } else {
                        fieldBtns.add(fieldName + ":6;15;7;2;13;12;1;4;14;8;21;9;3;10;11");
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORYIMG.equals(inputMode)) {
                    // 图片附件
                    fieldBtns.add(fieldName + ":allowDelete;allowDownload;allowPreview;allowUpload");
                }
            }
        }
        return fieldBtns;
    }

    /**
     * @param formDefinitionHandler
     * @param taskForm
     * @throws Exception
     */
    private List<String> getFormFileRightBtns(FormDefinitionHandler formDefinitionHandler, TaskElement taskElement,
                                              TaskForm taskForm) throws Exception {
        List<String> fileRights = Lists.newArrayList();
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return fileRights;
        }
        boolean canEditForm = StringUtils.equals("1", taskElement.getCanEditForm());
        List<String> hideFields = getHideFieldMap(taskElement, formDefinitionHandler, taskForm)
                .get(formDefinitionHandler.getFormUuid());
        List<String> editFields = getEditFieldMap(taskElement, formDefinitionHandler, taskForm)
                .get(formDefinitionHandler.getFormUuid());
        if (hideFields == null) {
            hideFields = Lists.newArrayList();
        }
        if (editFields == null) {
            editFields = Lists.newArrayList();
        }
        for (String fieldName : fieldNames) {
            JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null && jsonObject.has("inputMode")) {
                String inputMode = jsonObject.getString("inputMode");
                // 列表附件
                if (DyFormConfig.INPUTMODE_ACCESSORY3.equals(inputMode) && jsonObject.has("secDevBtnIdStr")) {
                    String secDevBtnIdStr = jsonObject.getString("secDevBtnIdStr");
                    if (StringUtils.isBlank(secDevBtnIdStr)) {
                        continue;
                    }
                    if (canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":" + secDevBtnIdStr);
                    } else if (canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName
                                + ":954dac1c-8ae8-4bd2-86a6-e3283325989c;5f82f10a-9450-4a18-8c8d-e38e3767b466;4ee25050-f1e5-49d2-a635-9c19ef1785dc");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName
                                + ":954dac1c-8ae8-4bd2-86a6-e3283325989c;5f82f10a-9450-4a18-8c8d-e38e3767b466;4ee25050-f1e5-49d2-a635-9c19ef1785dc");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":" + secDevBtnIdStr);
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORY1.equals(inputMode)) {
                    // 图标附件
                    if (canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        if (jsonObject.has("operateBtns")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("operateBtns");
                            List<String> operateBtns = Lists.newArrayList();
                            for (int index = 0; index < jsonArray.length(); index++) {
                                operateBtns.add(jsonArray.getString(index));
                            }
                            if (!operateBtns.contains("15")) {
                                operateBtns.add("15");
                            }
                            if (!operateBtns.contains("4")) {
                                operateBtns.add("4");
                            }
                            if (!operateBtns.contains("12")) {
                                operateBtns.add("12");
                            }
                            fileRights.add(fieldName + ":" + StringUtils.join(operateBtns, ";"));
                        } else {
                            if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                                fileRights.add(fieldName + ":7;2;1;4;5;14;3;15;4;12");
                            } else {
                                fileRights.add(fieldName + ":7;2;6;1;4;14;3;15;4;12");
                            }
                        }
                    } else if (canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                            fileRights.add(fieldName + ":7;15;2;13;15;12");
                        } else {
                            fileRights.add(fieldName + ":7;15;6;2;13;15;12");
                        }
                    } else if (!canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                            fileRights.add(fieldName + ":7;15;2;13;15;12");
                        } else {
                            fileRights.add(fieldName + ":7;15;6;2;13;15;12");
                        }
                    } else if (!canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        if (jsonObject.has("operateBtns")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("operateBtns");
                            List<String> operateBtns = Lists.newArrayList();
                            for (int index = 0; index < jsonArray.length(); index++) {
                                operateBtns.add(jsonArray.getString(index));
                            }
                            if (!operateBtns.contains("15")) {
                                operateBtns.add("15");
                            }
                            if (!operateBtns.contains("4")) {
                                operateBtns.add("4");
                            }
                            if (!operateBtns.contains("12")) {
                                operateBtns.add("12");
                            }
                            fileRights.add(fieldName + ":" + StringUtils.join(operateBtns, ";"));
                        } else {
                            if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                                fileRights.add(fieldName + ":7;2;1;4;5;14;3;15;4;12");
                            } else {
                                fileRights.add(fieldName + ":7;2;6;1;4;14;3;15;4;12");
                            }
                        }
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORYIMG.equals(inputMode)) {
                    // 图片附件
                    if (canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowDelete;allowDownload;allowPreview;allowUpload");
                    } else if (canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowPreview;allowDownload");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowPreview;allowDownload");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowDelete;allowDownload;allowPreview;allowUpload");
                    }
                }
            }
        }
        return fileRights;
    }

    /**
     * @param formDefinitionHandler
     * @return
     */
    private List<String> getFormFields(FormDefinitionHandler formDefinitionHandler) {
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        return Lists.newArrayList(fieldNames);
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private List<String> getFormRequiredFields(FormDefinitionHandler formDefinitionHandler) throws Exception {
        List<String> requiredFields = Lists.newArrayList();
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return requiredFields;
        }
        for (String fieldName : fieldNames) {
            JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null && jsonObject.has("fieldCheckRules")) {
                JSONArray fieldCheckRules = jsonObject.getJSONArray("fieldCheckRules");
                for (int index = 0; index < fieldCheckRules.length(); index++) {
                    JSONObject fieldCheckRule = fieldCheckRules.getJSONObject(index);
                    if (fieldCheckRule.has("value") && "1".equals(fieldCheckRule.get("value"))) {
                        requiredFields.add(fieldName);
                        break;
                    }
                }
            }
        }
        return requiredFields;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private Map<String, List<String>> getSubformRequiredFields(FormDefinitionHandler formDefinitionHandler)
            throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        for (String subformUuid : subformUuids) {
            DyFormFormDefinition subformDefinition = dyFormFacade.getFormDefinition(subformUuid);
            if (subformDefinition == null) {
                continue;
            }
            FormDefinitionHandler subformDefinitionHandler = new FormDefinitionHandler(
                    subformDefinition.getDefinitionJson(), subformDefinition.getFormType(), subformDefinition.getName(),
                    subformDefinition.getpFormUuid());
            map.put(subformUuid, getFormRequiredFields(subformDefinitionHandler));
        }
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private List<String> getFormEditFields(FormDefinitionHandler formDefinitionHandler) throws Exception {
        List<String> editFields = Lists.newArrayList();
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return editFields;
        }
        for (String fieldName : fieldNames) {
            JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null && jsonObject.has("showType")) {
                if (DyShowType.edit.equals(jsonObject.getString("showType"))) {
                    editFields.add(fieldName);
                }
            }
        }
        return editFields;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    private List<String> getFormHideFields(FormDefinitionHandler formDefinitionHandler) throws Exception {
        List<String> hideFields = Lists.newArrayList();
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return hideFields;
        }
        for (String fieldName : fieldNames) {
            JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null && jsonObject.has("showType")) {
                if (DyShowType.hide.equals(jsonObject.getString("showType"))) {
                    hideFields.add(fieldName);
                }
            }
        }
        return hideFields;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws JSONException
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getSubformFields(FormDefinitionHandler formDefinitionHandler)
            throws JSONException {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                map.put(subformUuid, Lists.<String>newArrayList());
                JSONObject jsonObject = formDefinitionHandler.getSubformDefinitionJson(subformUuid);
                JSONObject fields = jsonObject.getJSONObject("fields");
                Iterator<String> it = fields.keys();
                while (it.hasNext()) {
                    String fieldName = it.next();
                    map.get(subformUuid).add(fieldName);
                }
            }
        }
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getSubformEditFields(FormDefinitionHandler formDefinitionHandler)
            throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                map.put(subformUuid, Lists.<String>newArrayList());
                JSONObject jsonObject = formDefinitionHandler.getSubformDefinitionJson(subformUuid);
                JSONObject fields = jsonObject.getJSONObject("fields");
                Iterator<String> it = fields.keys();
                while (it.hasNext()) {
                    String fieldName = it.next();
                    JSONObject field = fields.getJSONObject(fieldName);
                    String hidden = field.getString("editable");
                    if (StringUtils.equals("1", hidden)) {
                        map.get(subformUuid).add(fieldName);
                    }
                }
            }
        }
        return map;
    }

    /**
     * @param formDefinitionHandler
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private Map<String, List<String>> getSubformHideFields(FormDefinitionHandler formDefinitionHandler)
            throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        if (CollectionUtils.isNotEmpty(subformUuids)) {
            for (String subformUuid : subformUuids) {
                map.put(subformUuid, Lists.<String>newArrayList());
                JSONObject jsonObject = formDefinitionHandler.getSubformDefinitionJson(subformUuid);
                JSONObject fields = jsonObject.getJSONObject("fields");
                Iterator<String> it = fields.keys();
                while (it.hasNext()) {
                    String fieldName = it.next();
                    JSONObject field = fields.getJSONObject(fieldName);
                    String hidden = field.getString("hidden");
                    if (StringUtils.equals("2", hidden)) {
                        map.get(subformUuid).add(fieldName);
                    }
                }
            }
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#saveUpgradeFlowDefinitionXml(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public FlowDefinition saveUpgradeFlowDefinitionXml(String flowDefUuid, String flowDefXml) {
        try {
            // 更新流程规划
            FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
            FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
            // Clob content = flowDefinitionService.convertString2Clob(flowDefXml);
            flowSchema.setContent(Hibernate.getLobCreator(flowSchemaService.getDao().getSession()).createClob(flowDefXml));
            flowSchemaService.save(flowSchema);
            flowSchemaService.flushSession();
            flowSchemaService.clearSession();
            return flowDefinition;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#upgrade2v6_2_5(java.lang.String)
     */
    @Override
    public List<String> upgrade2v6_2_5(String flowDefUuid) {
        List<String> updatedFlowInfos = Lists.newArrayList();
        // 获取要升级的流程定义XML
        Map<String, String> toUpgradeFlowDefXmlMap = getUpgradeFlowDefinitionXml2v6_2_5(flowDefUuid);

        // 保存要升级的流程定义XML
        for (String toUpgradeFlowDefUuid : toUpgradeFlowDefXmlMap.keySet()) {
            FlowDefinition flowDefinition = saveUpgradeFlowDefinitionXml(toUpgradeFlowDefUuid,
                    toUpgradeFlowDefXmlMap.get(toUpgradeFlowDefUuid));
            updatedFlowInfos.add(flowDefinition.getName() + ":" + flowDefinition.getVersion());
        }
        return updatedFlowInfos;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#getUpgradeFlowDefinitionXml2v6_2_5(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, String> getUpgradeFlowDefinitionXml2v6_2_5(String flowDefUuid) {
        Map<String, String> toUpgradeFlowDefXmlMap = Maps.newHashMap();
        String hql = null;
        if (StringUtils.isBlank(flowDefUuid)) {
            hql = "select t1.uuid from FlowDefinition t1 where t1.flowSchemaUuid in( select t2.uuid from FlowSchema t2 where t2.content like '%4ee25050-f1e5-49d2-a635-9c19ef1785dc%')";
        } else {
            hql = "select t1.uuid from FlowDefinition t1 where t1.uuid='" + flowDefUuid + "'";
        }
        List<FlowDefinition> flowDefinitionUuids = flowDefinitionService.listByHQL(hql, null);
        for (Object flowDefinitionUuid : flowDefinitionUuids) {
            try {
                FlowDefinition flowDefinition = flowDefinitionService.getOne(ObjectUtils.toString(flowDefinitionUuid));
                upgrade2v6_2_5IfRequired(flowDefinition, toUpgradeFlowDefXmlMap);
                flowDefinitionService.clearSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toUpgradeFlowDefXmlMap;
    }

    /**
     * @param flowDefinition
     * @param toUpgradeFlowDefXmlMap
     */
    private void upgrade2v6_2_5IfRequired(FlowDefinition flowDefinition, Map<String, String> toUpgradeFlowDefXmlMap)
            throws Exception {
        boolean hasChanged = false;
        String flowXml = flowSchemeService.getFlowXml(flowDefinition.getUuid());
        FlowDelegate flowDelegate = new FlowDelegate(FlowDefinitionParser.parseFlow(flowXml));
        Document document = FlowDefinitionParser.createDocument(flowXml);
        if (StringUtils.isBlank(flowDefinition.getFormUuid())) {
            return;
        }
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(flowDefinition.getFormUuid());
        if (formDefinition == null) {
            return;
        }
        FormDefinitionHandler formDefinitionHandler = new FormDefinitionHandler(formDefinition.getDefinitionJson(),
                formDefinition.getFormType(), formDefinition.getName(), formDefinition.getpFormUuid());
        // 7、旧数据附件fileRights
        if (addFileRights2v6_2_5IfRequired(formDefinitionHandler, flowDelegate, document)) {
            hasChanged = true;
        }
        // 流程定义变更，加入待升级
        if (hasChanged) {
            toUpgradeFlowDefXmlMap.put(flowDefinition.getUuid(), document.asXML());
        }
    }

    /**
     * @param formDefinitionHandler
     * @param flowDelegate
     * @param document
     * @return
     */
    private boolean addFileRights2v6_2_5IfRequired(FormDefinitionHandler formDefinitionHandler,
                                                   FlowDelegate flowDelegate, Document document) throws Exception {
        boolean hasChanged = false;
        // 从表操作按钮
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            String taskId = node.getId();
            if (StringUtils.equals(FlowDelegate.START_FLOW_ID, taskId)
                    || StringUtils.equals(FlowDelegate.END_FLOW_ID, taskId)) {
                continue;
            }
            Element taskNode = (Element) document.selectSingleNode("/flow/tasks/task[@id='" + taskId + "']");
            Element fileRightNode = (Element) taskNode.selectSingleNode("fileRights");
            if (fileRightNode != null) {
                fileRightNode.clearContent();
            } else if (fileRightNode == null) {
                fileRightNode = createFileRightsElement();
                taskNode.add(fileRightNode);
            }

            TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
            TaskForm taskForm = flowDelegate.getTaskForm(taskId);
            // List<String> fileRights = taskForm.getFileRights();
            // 主表字段
            List<String> formFileRights = getFormFileRightBtns2v6_2_5(formDefinitionHandler, taskElement, taskForm);
            // formFileRights = extractAddFileRight(fileRights, formFileRights);
            for (String formFileRight : formFileRights) {
                fileRightNode.add(createFieldElement(formFileRight));
                hasChanged = true;
            }
            // 从表字段
            Map<String, List<String>> subformFileRightBtns = getSubformFileRightBtns2v6_2_5(formDefinitionHandler,
                    taskElement, taskForm);
            for (String subformUuid : subformFileRightBtns.keySet()) {
                List<String> fields = subformFileRightBtns.get(subformUuid);
                // fields = extractSubformAddFileRight(subformUuid, fileRights, fields);
                for (String fieldName : fields) {
                    fileRightNode.add(createFieldElement(subformUuid + ":" + fieldName));
                    hasChanged = true;
                }
            }
        }
        return hasChanged;
    }

    /**
     * @param formDefinitionHandler
     * @param taskElement
     * @param taskForm
     * @return
     */
    private List<String> getFormFileRightBtns2v6_2_5(FormDefinitionHandler formDefinitionHandler,
                                                     TaskElement taskElement, TaskForm taskForm) throws Exception {
        List<String> fileRights = Lists.newArrayList();
        List<String> fieldNames = formDefinitionHandler.getFieldNames();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return fileRights;
        }
        boolean canEditForm = StringUtils.equals("1", taskElement.getCanEditForm());
        List<String> hideFields = getHideFieldMap(taskElement, formDefinitionHandler, taskForm)
                .get(formDefinitionHandler.getFormUuid());
        List<String> editFields = getEditFieldMap(taskElement, formDefinitionHandler, taskForm)
                .get(formDefinitionHandler.getFormUuid());
        if (hideFields == null) {
            hideFields = Lists.newArrayList();
        }
        if (editFields == null) {
            editFields = Lists.newArrayList();
        }
        for (String fieldName : fieldNames) {
            JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldName);
            if (jsonObject != null && jsonObject.has("inputMode")) {
                String inputMode = jsonObject.getString("inputMode");
                // 列表附件
                if (DyFormConfig.INPUTMODE_ACCESSORY3.equals(inputMode) && jsonObject.has("secDevBtnIdStr")) {
                    String secDevBtnIdStr = jsonObject.getString("secDevBtnIdStr");
                    if (StringUtils.isBlank(secDevBtnIdStr)) {
                        continue;
                    }
                    if (canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":" + secDevBtnIdStr);
                    } else if (canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName
                                + ":954dac1c-8ae8-4bd2-86a6-e3283325989c;5f82f10a-9450-4a18-8c8d-e38e3767b466;4ee25050-f1e5-49d2-a635-9c19ef1785dc;ac764fe8-3cab-4100-b128-9f514be135c0");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName
                                + ":954dac1c-8ae8-4bd2-86a6-e3283325989c;5f82f10a-9450-4a18-8c8d-e38e3767b466;4ee25050-f1e5-49d2-a635-9c19ef1785dc;ac764fe8-3cab-4100-b128-9f514be135c0");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":" + secDevBtnIdStr);
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORY1.equals(inputMode)) {
                    // 图标附件
                    if (canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        if (jsonObject.has("operateBtns")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("operateBtns");
                            List<String> operateBtns = Lists.newArrayList();
                            for (int index = 0; index < jsonArray.length(); index++) {
                                operateBtns.add(jsonArray.getString(index));
                            }
                            if (!operateBtns.contains("15")) {
                                operateBtns.add("15");
                            }
                            if (!operateBtns.contains("4")) {
                                operateBtns.add("4");
                            }
                            if (!operateBtns.contains("12")) {
                                operateBtns.add("12");
                            }
                            fileRights.add(fieldName + ":" + StringUtils.join(operateBtns, ";"));
                        } else {
                            if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                                fileRights.add(fieldName + ":7;2;1;4;5;14;3;15;4;12");
                            } else {
                                fileRights.add(fieldName + ":7;2;6;1;4;14;3;15;4;12");
                            }
                        }
                    } else if (canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                            fileRights.add(fieldName + ":7;15;2;13;15;12");
                        } else {
                            fileRights.add(fieldName + ":7;15;6;2;13;15;12");
                        }
                    } else if (!canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                            fileRights.add(fieldName + ":7;15;2;13;15;12");
                        } else {
                            fileRights.add(fieldName + ":7;15;6;2;13;15;12");
                        }
                    } else if (!canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        if (jsonObject.has("operateBtns")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("operateBtns");
                            List<String> operateBtns = Lists.newArrayList();
                            for (int index = 0; index < jsonArray.length(); index++) {
                                operateBtns.add(jsonArray.getString(index));
                            }
                            if (!operateBtns.contains("15")) {
                                operateBtns.add("15");
                            }
                            if (!operateBtns.contains("4")) {
                                operateBtns.add("4");
                            }
                            if (!operateBtns.contains("12")) {
                                operateBtns.add("12");
                            }
                            fileRights.add(fieldName + ":" + StringUtils.join(operateBtns, ";"));
                        } else {
                            if (jsonObject.has("keepOpLog") && "1".equals(jsonObject.getString("keepOpLog"))) {
                                fileRights.add(fieldName + ":7;2;1;4;5;14;3;15;4;12");
                            } else {
                                fileRights.add(fieldName + ":7;2;6;1;4;14;3;15;4;12");
                            }
                        }
                    }
                } else if (DyFormConfig.INPUTMODE_ACCESSORYIMG.equals(inputMode)) {
                    // 图片附件
                    if (canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowDelete;allowDownload;allowPreview;allowUpload");
                    } else if (canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowPreview;allowDownload");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && !editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowPreview;allowDownload");
                    } else if (!canEditForm && !hideFields.contains(fieldName) && editFields.contains(fieldName)) {
                        fileRights.add(fieldName + ":allowDelete;allowDownload;allowPreview;allowUpload");
                    }
                }
            }
        }
        return fileRights;
    }

    /**
     * @param formDefinitionHandler
     * @param taskElement
     * @param taskForm
     * @return
     */
    private Map<String, List<String>> getSubformFileRightBtns2v6_2_5(FormDefinitionHandler formDefinitionHandler,
                                                                     TaskElement taskElement, TaskForm taskForm) throws Exception {
        Map<String, List<String>> map = Maps.newHashMap();
        List<String> subformUuids = formDefinitionHandler.getFormUuidsOfSubform();
        for (String subformUuid : subformUuids) {
            DyFormFormDefinition subformDefinition = dyFormFacade.getFormDefinition(subformUuid);
            if (subformDefinition == null) {
                continue;
            }
            FormDefinitionHandler subformDefinitionHandler = new FormDefinitionHandler(
                    subformDefinition.getDefinitionJson(), subformDefinition.getFormType(), subformDefinition.getName(),
                    subformDefinition.getpFormUuid());
            map.put(subformUuid, getFormFileRightBtns2v6_2_5(subformDefinitionHandler, taskElement, taskForm));
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#upgrade2v6_2_7(java.lang.String)
     */
    @Override
    public List<String> upgrade2v6_2_7(String flowDefUuid) {
        List<String> updatedFlowInfos = Lists.newArrayList();
        // 获取要升级的流程定义XML
        Map<String, String> toUpgradeFlowDefXmlMap = getUpgradeFlowDefinitionXml2v6_2_7(flowDefUuid);

        // 保存要升级的流程定义XML
        for (String toUpgradeFlowDefUuid : toUpgradeFlowDefXmlMap.keySet()) {
            FlowDefinition flowDefinition = saveUpgradeFlowDefinitionXml(toUpgradeFlowDefUuid,
                    toUpgradeFlowDefXmlMap.get(toUpgradeFlowDefUuid));
            updatedFlowInfos.add(flowDefinition.getName() + ":" + flowDefinition.getVersion());
        }
        return updatedFlowInfos;
    }

    /**
     * @param flowDefUuid
     * @return
     */
    private Map<String, String> getUpgradeFlowDefinitionXml2v6_2_7(String flowDefUuid) {
        Map<String, String> toUpgradeFlowDefXmlMap = Maps.newHashMap();
        String hql = null;
        if (StringUtils.isBlank(flowDefUuid)) {
            hql = "select t1.uuid from FlowDefinition t1 where t1.flowSchemaUuid in( select t2.uuid from FlowSchema t2 where t2.content like '%limitTimeType%')";
        } else {
            hql = "select t1.uuid from FlowDefinition t1 where t1.uuid='" + flowDefUuid + "'";
        }
        List<FlowDefinition> flowDefinitionUuids = flowDefinitionService.listByHQL(hql, null);
        for (Object flowDefinitionUuid : flowDefinitionUuids) {
            try {
                FlowDefinition flowDefinition = flowDefinitionService.getOne(ObjectUtils.toString(flowDefinitionUuid));
                upgrade2v6_2_7IfRequired(flowDefinition, toUpgradeFlowDefXmlMap);
                flowDefinitionService.clearSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toUpgradeFlowDefXmlMap;
    }

    /**
     * @param flowDefinition
     * @param toUpgradeFlowDefXmlMap
     * @throws Exception
     */
    private void upgrade2v6_2_7IfRequired(FlowDefinition flowDefinition, Map<String, String> toUpgradeFlowDefXmlMap)
            throws Exception {
        boolean hasChanged = false;
        String flowXml = flowSchemeService.getFlowXml(flowDefinition.getUuid());
        FlowDelegate flowDelegate = new FlowDelegate(FlowDefinitionParser.parseFlow(flowXml));
        Document document = FlowDefinitionParser.createDocument(flowXml);
        if (StringUtils.isBlank(flowDefinition.getFormUuid())) {
            return;
        }
        // 生成计时器配置
        if (addTimerConfig2v6_2_7IfRequired(flowDelegate, document)) {
            hasChanged = true;
        }
        // 流程定义变更，加入待升级
        if (hasChanged) {
            toUpgradeFlowDefXmlMap.put(flowDefinition.getUuid(), document.asXML());
        }
    }

    /**
     * @param flowDelegate
     * @param document
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean addTimerConfig2v6_2_7IfRequired(FlowDelegate flowDelegate, Document document) {
        boolean hasChanged = false;
        List<Element> timerElements = (List<Element>) document.selectNodes("/flow/timers/timer");
        // 获取默认的工作时间方案
        TsWorkTimePlanDto workTimePlanDto = workTimePlanFacadeService.getDefaultWorkTimePlan();
        TsTimerCategoryDto timerCategoryDto = timerCategoryFacadeService.getById("flowTiming");
        for (Element timerElement : timerElements) {
            // 计时器配置UUID
            Element timerConfigUuidElement = (Element) timerElement.selectSingleNode("timerConfigUuid");
            if (timerConfigUuidElement != null && StringUtils.isNotBlank(timerConfigUuidElement.getText())) {
                continue;
            }
            // 计时包含启动时间点，1是0否，默认0
            addIncludeStartTimePointElement(timerElement);
            // 自动推迟到下一工作时间的起始点前结点
            addAutoDelayElement(timerElement);
            // 工作时间方案
            addWorkTimePlanElements(timerElement, workTimePlanDto);
            // 时限单位
            addTimeLimitUnitElement(timerElement);
            // 增加计时器配置UUID
            addTimerConfigUuid(timerElement, timerCategoryDto);
            hasChanged = true;
        }
        return hasChanged;
    }

    /**
     * @param timerElement
     */
    private void addIncludeStartTimePointElement(Element timerElement) {
        Element includeStartTimePoint = DocumentFactory.getInstance().createElement("includeStartTimePoint");
        includeStartTimePoint.setText("0");
        timerElement.add(includeStartTimePoint);
    }

    /**
     * @param timerElement
     */
    private void addAutoDelayElement(Element timerElement) {
        Element autoDelay = DocumentFactory.getInstance().createElement("autoDelay");
        autoDelay.setText("0");
        timerElement.add(autoDelay);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private void addWorkTimePlanElements(Element timerElement, TsWorkTimePlanDto workTimePlanDto) {
        // 工作时间方案UUID
        Element workTimePlanUuid = DocumentFactory.getInstance().createElement("workTimePlanUuid");
        workTimePlanUuid.setText(workTimePlanDto.getUuid());
        timerElement.add(workTimePlanUuid);
        // 工作时间方案ID
        Element workTimePlanId = DocumentFactory.getInstance().createElement("workTimePlanId");
        workTimePlanId.setText(workTimePlanDto.getId());
        timerElement.add(workTimePlanId);
        // 工作时间方案名称
        Element workTimePlanName = DocumentFactory.getInstance().createElement("workTimePlanName");
        workTimePlanName.setText(workTimePlanDto.getName());
        timerElement.add(workTimePlanName);
    }

    /**
     * @param timerElement
     */
    private void addTimeLimitUnitElement(Element timerElement) {
        // 计时方式
        int limitUnit = Integer.valueOf(timerElement.selectSingleNode("limitUnit").getText());
        EnumTimeLimitUnit timeLimitUnit = EnumTimeLimitUnit.Day;
        switch (limitUnit) {
            case TimerUnit.DATE_WORKING_DAY:
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                timeLimitUnit = EnumTimeLimitUnit.Day;
                break;
            case TimerUnit.DATE_WORKING_HOUR:
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                timeLimitUnit = EnumTimeLimitUnit.Hour;
                break;
            case TimerUnit.DATE_WORKING_MINUTE:
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                timeLimitUnit = EnumTimeLimitUnit.Minute;
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                timeLimitUnit = EnumTimeLimitUnit.Day;
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                timeLimitUnit = EnumTimeLimitUnit.Hour;
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                timeLimitUnit = EnumTimeLimitUnit.Minute;
                break;
            case TimerUnit.DAY:
                // 天
                timeLimitUnit = EnumTimeLimitUnit.Day;
                break;
            case TimerUnit.HOUR:
                // 小时
                timeLimitUnit = EnumTimeLimitUnit.Hour;
                break;
            case TimerUnit.MINUTE:
                // 分钟
                timeLimitUnit = EnumTimeLimitUnit.Minute;
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                timeLimitUnit = EnumTimeLimitUnit.Day;
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                timeLimitUnit = EnumTimeLimitUnit.Hour;
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                timeLimitUnit = EnumTimeLimitUnit.Minute;
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                timeLimitUnit = EnumTimeLimitUnit.Day;
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                timeLimitUnit = EnumTimeLimitUnit.Day;
                break;
            default:
                break;
        }

        Element timeLimitUnitNode = DocumentFactory.getInstance().createElement("timeLimitUnit");
        timeLimitUnitNode.setText(timeLimitUnit.getValue());
        timerElement.add(timeLimitUnitNode);
    }

    /**
     * @param timerElement
     * @param timerCategoryDto
     */
    private void addTimerConfigUuid(Element timerElement, TsTimerCategoryDto timerCategoryDto) {
        // 计时器名称
        String name = timerElement.selectSingleNode("name").getText();
        // 计时器ID
        String id = "flowTiming_" + UUID.randomUUID().toString();
        // 计时器编号
        String code = Calendar.getInstance().getTime().getTime() + StringUtils.EMPTY;
        // 计时配置分类UUID
        String categoryUuid = timerCategoryDto.getUuid();
        // 计时方式
        String timingMode = getTimingMode(timerElement);
        changeLimitUnitIfRequired(timerElement);
        // 时限类型
        String timeLimitType = getTimeLimitType(timerElement);
        // 时限
        String timeLimit = getTimeLimit(timerElement);
        // 时限单位
        String timeLimitUnit = timerElement.selectSingleNode("timeLimitUnit").getText();
        // 计时包含启动时间点，1是0否，默认0
        boolean includeStartTimePoint = StringUtils.equals("1",
                timerElement.selectSingleNode("includeStartTimePoint").getText());
        // 自动推迟到下一工作时间起始点前，1是0否，默认0
        boolean autoDelay = StringUtils.equals("1", timerElement.selectSingleNode("autoDelay").getText());
        String listener = TaskTimerListener.LISTENER_NAME;
        String remark = "流程计时器升级";

        TsTimerConfigDto timerConfigDto = new TsTimerConfigDto();
        timerConfigDto.setName(name);
        timerConfigDto.setId(id);
        timerConfigDto.setCode(code);
        timerConfigDto.setCategoryUuid(categoryUuid);
        timerConfigDto.setTimingMode(timingMode);
        timerConfigDto.setTimeLimitType(timeLimitType);
        timerConfigDto.setTimeLimit(timeLimit);
        timerConfigDto.setTimeLimitUnit(timeLimitUnit);
        timerConfigDto.setIncludeStartTimePoint(includeStartTimePoint);
        timerConfigDto.setAutoDelay(autoDelay);
        timerConfigDto.setListener(listener);
        timerConfigDto.setRemark(remark);
        String timerConfigUuid = timerConfigFacadeService.saveDto(timerConfigDto);

        Element timerConfigUuidNode = DocumentFactory.getInstance().createElement("timerConfigUuid");
        timerConfigUuidNode.setText(timerConfigUuid);
        timerElement.add(timerConfigUuidNode);
    }

    /**
     * @param timerElement
     * @return
     */
    private String getTimeLimitType(Element timerElement) {
        String limitTimeType = timerElement.selectSingleNode("limitTimeType").getText();
        if (StringUtils.equals("1", limitTimeType)) {
            return EnumTimeLimitType.NUMBER.getValue();
        } else if (StringUtils.equals("2", limitTimeType)) {
            return EnumTimeLimitType.CUSTOM_NUMBER.getValue();
        } else if (StringUtils.equals("3", limitTimeType)) {
            return EnumTimeLimitType.CUSTOM_DATE.getValue();
        } else if (StringUtils.equals("4", limitTimeType)) {
            return EnumTimeLimitType.DATE.getValue();
        }
        return EnumTimeLimitType.CUSTOM_NUMBER.getValue();
    }

    /**
     * @param string
     * @return
     */
    private String getTimeLimit(Element timerElement) {
        String limitTimeType = timerElement.selectSingleNode("limitTimeType").getText();
        String limitTime1 = StringUtils.EMPTY;
        if (timerElement.selectSingleNode("limitTime1") != null) {
            limitTime1 = timerElement.selectSingleNode("limitTime1").getText();
        }
        if (StringUtils.equals("1", limitTimeType)) {
            return limitTime1;
        } else if (StringUtils.equals("2", limitTimeType)) {
            return StringUtils.EMPTY;
        } else if (StringUtils.equals("3", limitTimeType)) {
            return StringUtils.EMPTY;
        }
        return limitTime1;
    }

    /**
     * @param timerElement
     * @return
     */
    private String getTimingMode(Element timerElement) {
        // 计时方式
        int limitUnit = Integer.valueOf(timerElement.selectSingleNode("limitUnit").getText());
        EnumTimingMode timingMode = EnumTimingMode.DAY;
        switch (limitUnit) {
            case TimerUnit.DATE_WORKING_DAY:
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                timingMode = EnumTimingMode.WORKING_DAY;
                break;
            case TimerUnit.DATE_WORKING_HOUR:
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                timingMode = EnumTimingMode.WORKING_HOUR;
                break;
            case TimerUnit.DATE_WORKING_MINUTE:
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                timingMode = EnumTimingMode.WORKING_MINUTE;
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                timingMode = EnumTimingMode.WORKING_DAY_24;
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                timingMode = EnumTimingMode.WORKING_HOUR_24;
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                timingMode = EnumTimingMode.WORKING_MINUTE_24;
                break;
            case TimerUnit.DAY:
                // 天
                timingMode = EnumTimingMode.DAY;
                break;
            case TimerUnit.HOUR:
                // 小时
                timingMode = EnumTimingMode.HOUR;
                break;
            case TimerUnit.MINUTE:
                // 分钟
                timingMode = EnumTimingMode.MINUTE;
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                timingMode = EnumTimingMode.WORKING_DAY_24;
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                timingMode = EnumTimingMode.WORKING_HOUR_24;
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                timingMode = EnumTimingMode.WORKING_MINUTE_24;
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                timingMode = EnumTimingMode.DAY;
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                timingMode = EnumTimingMode.DAY;
                break;
            default:
                break;
        }
        return timingMode.getValue();
    }

    /**
     * @param timerElement
     */
    private void changeLimitUnitIfRequired(Element timerElement) {
        // 计时方式
        int limitUnit = Integer.valueOf(timerElement.selectSingleNode("limitUnit").getText());
        switch (limitUnit) {
            case TimerUnit.DATE_WORKING_DAY:
                // case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.WORKING_DAY.getValue());
                break;
            case TimerUnit.DATE_WORKING_HOUR:
                // case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.WORKING_HOUR.getValue());
                break;
            case TimerUnit.DATE_WORKING_MINUTE:
                // case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.WORKING_MINUTE.getValue());
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                break;
            case TimerUnit.DAY:
                // 天
                break;
            case TimerUnit.HOUR:
                // 小时
                break;
            case TimerUnit.MINUTE:
                // 分钟
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.WORKING_DAY_24.getValue());
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.WORKING_HOUR_24.getValue());
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.WORKING_MINUTE_24.getValue());
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.DAY.getValue());
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                timerElement.selectSingleNode("limitUnit").setText(EnumTimingMode.DAY.getValue());
                break;
            default:
                break;
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#upgrade2v6_2_9_1(java.lang.String)
     */
    @Override
    public List<String> upgrade2v6_2_9_1(String flowDefUuid) {
        List<String> updatedFlowInfos = Lists.newArrayList();
        // 获取要升级的流程定义XML
        Map<String, String> toUpgradeFlowDefXmlMap = getUpgradeFlowDefinitionXml2v6_2_9_1(flowDefUuid);

        // 保存要升级的流程定义XML
        for (String toUpgradeFlowDefUuid : toUpgradeFlowDefXmlMap.keySet()) {
            FlowDefinition flowDefinition = saveUpgradeFlowDefinitionXml(toUpgradeFlowDefUuid,
                    toUpgradeFlowDefXmlMap.get(toUpgradeFlowDefUuid));
            updatedFlowInfos.add(flowDefinition.getName() + ":" + flowDefinition.getVersion());
        }
        return updatedFlowInfos;
    }

    /**
     * @param flowDefUuid
     * @return
     */
    @Transactional(readOnly = true)
    private Map<String, String> getUpgradeFlowDefinitionXml2v6_2_9_1(String flowDefUuid) {
        Map<String, String> toUpgradeFlowDefXmlMap = Maps.newHashMap();
        String hql = null;
        if (StringUtils.isBlank(flowDefUuid)) {
            hql = "select t1.uuid from FlowDefinition t1 where t1.flowSchemaUuid in( select t2.uuid from FlowSchema t2 where t2.content like '%<optNames>%' and t2.content not like '%enableOpinionPosition%')";
        } else {
            hql = "select t1.uuid from FlowDefinition t1 where t1.uuid='" + flowDefUuid + "'";
        }
        System.out.println(1);
        List<FlowDefinition> flowDefinitionUuids = flowDefinitionService.listByHQL(hql, null);
        for (Object flowDefinitionUuid : flowDefinitionUuids) {
            try {
                FlowDefinition flowDefinition = flowDefinitionService.getOne(ObjectUtils.toString(flowDefinitionUuid));
                upgrade2v6_2_9_1IfRequired(flowDefinition, toUpgradeFlowDefXmlMap);
                flowDefinitionService.clearSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toUpgradeFlowDefXmlMap;
    }

    /**
     * @param flowDefinition
     * @param toUpgradeFlowDefXmlMap
     * @throws Exception
     */
    private void upgrade2v6_2_9_1IfRequired(FlowDefinition flowDefinition, Map<String, String> toUpgradeFlowDefXmlMap) throws Exception {
        boolean hasChanged = false;
        String flowXml = flowSchemeService.getFlowXml(flowDefinition.getUuid());
        FlowDelegate flowDelegate = new FlowDelegate(FlowDefinitionParser.parseFlow(flowXml));
        Document document = FlowDefinitionParser.createDocument(flowXml);
        if (StringUtils.isBlank(flowDefinition.getFormUuid())) {
            return;
        }
        // 生成是否启用待办意见立场
        if (enableOpinionPosition2v6_2_9_1IfRequired(flowDelegate, document)) {
            hasChanged = true;
        }
        // 流程定义变更，加入待升级
        if (hasChanged) {
            toUpgradeFlowDefXmlMap.put(flowDefinition.getUuid(), document.asXML());
        }
    }

    /**
     * @param flowDelegate
     * @param document
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean enableOpinionPosition2v6_2_9_1IfRequired(FlowDelegate flowDelegate, Document document) {
        boolean hasChanged = false;
        List<Element> taskNodes = document.selectNodes("/flow/tasks/task");
        for (Element taskNode : taskNodes) {
            org.dom4j.Node enableOpinionPositionNode = taskNode.selectSingleNode("enableOpinionPosition");
            List<Element> unitNodes = taskNode.selectNodes("optNames/unit");
            if ((enableOpinionPositionNode == null || StringUtils.isBlank(enableOpinionPositionNode.getText()))
                    && CollectionUtils.isNotEmpty(unitNodes) && CollectionUtils.size(unitNodes) > 0) {
                taskNode.add(createEnableOpinionPositionElement());
                hasChanged = true;
            }
        }
        return hasChanged;
    }

    /**
     * @return
     */
    private Element createEnableOpinionPositionElement() {
        Element enableOpinionPosition = DocumentFactory.getInstance().createElement("enableOpinionPosition");
        enableOpinionPosition.setText("1");
        return enableOpinionPosition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowDefinitionUpgradeService#upgrade2v6_2_12(java.lang.String)
     */
    @Override
    public List<String> upgrade2v6_2_12(String flowDefUuid) {
        List<String> updatedFlowInfos = Lists.newArrayList();
        // 获取要升级的流程定义XML
        Map<String, String> toUpgradeFlowDefXmlMap = getUpgradeFlowDefinitionXml2v6_2_12(flowDefUuid);

        // 保存要升级的流程定义XML
        for (String toUpgradeFlowDefUuid : toUpgradeFlowDefXmlMap.keySet()) {
            FlowDefinition flowDefinition = saveUpgradeFlowDefinitionXml(toUpgradeFlowDefUuid,
                    toUpgradeFlowDefXmlMap.get(toUpgradeFlowDefUuid));
            updatedFlowInfos.add(flowDefinition.getName() + ":" + flowDefinition.getVersion());
        }
        return updatedFlowInfos;
    }

    /**
     * @param flowDefUuid
     * @return
     */
    private Map<String, String> getUpgradeFlowDefinitionXml2v6_2_12(String flowDefUuid) {
        Map<String, String> toUpgradeFlowDefXmlMap = Maps.newHashMap();
        String hql = null;
        if (StringUtils.isBlank(flowDefUuid)) {
            hql = "select t1.uuid from FlowDefinition t1 where t1.flowSchemaUuid in( select t2.uuid from FlowSchema t2 where t2.content like '%defaultTaskFormOpinionAssembler%' and t2.content not like '%fieldNotValidate%')";
        } else {
            hql = "select t1.uuid from FlowDefinition t1 where t1.uuid='" + flowDefUuid + "'";
        }
        List<FlowDefinition> flowDefinitionUuids = flowDefinitionService.listByHQL(hql, null);
        for (Object flowDefinitionUuid : flowDefinitionUuids) {
            try {
                FlowDefinition flowDefinition = flowDefinitionService.getOne(ObjectUtils.toString(flowDefinitionUuid));
                upgrade2v6_2_12IfRequired(flowDefinition, toUpgradeFlowDefXmlMap);
                flowDefinitionService.clearSession();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return toUpgradeFlowDefXmlMap;
    }

    /**
     * @param flowDefinition
     * @param toUpgradeFlowDefXmlMap
     * @throws Exception
     */
    private void upgrade2v6_2_12IfRequired(FlowDefinition flowDefinition, Map<String, String> toUpgradeFlowDefXmlMap) throws Exception {
        boolean hasChanged = false;
        String flowXml = flowSchemeService.getFlowXml(flowDefinition.getUuid());
        FlowDelegate flowDelegate = new FlowDelegate(FlowDefinitionParser.parseFlow(flowXml));
        Document document = FlowDefinitionParser.createDocument(flowXml);
        if (StringUtils.isBlank(flowDefinition.getFormUuid())) {
            return;
        }
        // 生成信息记录不验证表单字段结点
        if (addRecordFieldNotValidate2v6_12_1IfRequired(flowDelegate, document)) {
            hasChanged = true;
        }
        // 流程定义变更，加入待升级
        if (hasChanged) {
            toUpgradeFlowDefXmlMap.put(flowDefinition.getUuid(), document.asXML());
        }
    }

    /**
     * @param flowDelegate
     * @param document
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean addRecordFieldNotValidate2v6_12_1IfRequired(FlowDelegate flowDelegate, Document document) {
        boolean hasChanged = false;
        List<Element> flowRecordNodes = document.selectNodes("/flow/property/records/record");
        for (Element recordNode : flowRecordNodes) {
            List<Element> fieldNotValidates = recordNode.selectNodes("fieldNotValidate");
            if (CollectionUtils.isEmpty(fieldNotValidates)) {
                recordNode.add(createFieldNotValidateElement());
                hasChanged = true;
            }
        }

        List<Element> taskNodes = document.selectNodes("/flow/tasks/task");
        for (Element taskNode : taskNodes) {
            List<Element> recordNodes = taskNode.selectNodes("records/record");
            for (Element recordNode : recordNodes) {
                List<Element> fieldNotValidates = recordNode.selectNodes("fieldNotValidate");
                if (CollectionUtils.isEmpty(fieldNotValidates)) {
                    recordNode.add(createFieldNotValidateElement());
                    hasChanged = true;
                }
            }
        }
        return hasChanged;
    }

    /**
     * @return
     */
    private Element createFieldNotValidateElement() {
        Element fieldNotValidate = DocumentFactory.getInstance().createElement("fieldNotValidate");
        fieldNotValidate.setText("1");
        return fieldNotValidate;
    }


}
