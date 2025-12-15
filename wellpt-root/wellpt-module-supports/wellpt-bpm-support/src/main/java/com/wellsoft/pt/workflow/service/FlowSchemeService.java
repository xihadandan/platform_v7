/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service;

import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryApi;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.jdbc.support.KeyValuePair;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.parser.FlowDictionary;
import com.wellsoft.pt.bpm.engine.support.FlowDefinitionUserModifyParams;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.org.dto.OrganizationDto;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Description: 流程数据加载服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-23.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 2012-10-23
 */
public interface FlowSchemeService extends Select2QueryApi {

    /**
     * 返回工作流数据字典
     *
     * @param id       流程定义UUID
     * @param moduleId 模块ID
     * @return
     */
    String getDictionXml(String id, String moduleId);

    /**
     * 返回工作流数据字典
     *
     * @param uuid
     * @param moduleId
     * @return
     */
    FlowDictionary getFlowDictionary(String uuid, String moduleId);

    /**
     * 根据流程定义UUID，获取流程定义的XML内容
     *
     * @param uuid
     * @return
     * @throws Exception
     */
    String getFlowXml(String uuid) throws Exception;

    /**
     * 根据流程定义ID，获取流程定义的XML内容
     *
     * @param id
     * @return
     * @throws Exception
     */
    String getFlowXmlById(String id) throws Exception;

    /**
     * 根据流程定义对象，获取流程定义的XML内容
     *
     * @param flowDefinition
     * @return
     * @throws Exception
     */
    String getFlowXml(FlowDefinition flowDefinition, FlowSchema oldflowSchema);

    /**
     * 根据流程定义UUID，获取流程定义的XML内容
     *
     * @param uuid
     * @return
     */
    String getFlowJson(String uuid) throws IOException, SQLException;

    /**
     * 检查流程定义XML内容是否可更新
     *
     * @param xml
     * @return
     */
    boolean checkFlowXmlForUpdate(String xml);

    /**
     * 根据流程定义的XML内容，新建或更新流程定义
     *
     * @param xml
     * @param pbNew
     */
    String saveFlowXml(String xml, Boolean pbNew, Boolean isCopy) throws Exception;

    /**
     * 根据流程定义的JSON内容，新建或更新流程定义
     *
     * @param json
     * @param pbNew
     * @param isCopy
     * @return
     */
    String saveFlowJson(String json, Boolean pbNew, boolean isCopy) throws Exception;

    /**
     * @param flowDefinition
     * @return
     */
    FlowElement getFlowElement(FlowDefinition flowDefinition);

    /**
     * @param flowDefUuid
     * @return
     */
    FlowElement getFlowElementByFlowDefUuid(String flowDefUuid);

    /**
     * @param flowDefUuid
     * @param defaultFlowDefinition
     * @return
     */
    FlowElement getFlowElement(String flowDefUuid, FlowDefinition defaultFlowDefinition);

    /**
     * @param flowDefUuid
     */
    void clearFlowElementCache(String flowDefUuid);

    /**
     * 复制流程定义
     *
     * @param uuid
     * @param newFlowDefName
     * @param newFlowDefId
     * @return
     */
    String copy(String uuid, String newFlowDefName, String newFlowDefId);

    /**
     * 获取选择流程的XML内容
     *
     * @return
     */
    String getFlowTreeXml();

    /**
     * 获取选择流程的树结点
     *
     * @return
     */
    List<TreeNode> getFlowTree(String treeNodeId, String excludeFlowId);

    List<TreeNode> getFlowMessageTemplateType();

    Select2GroupData getSelectFlowMessageTemplateType();

    /**
     * 获取选择流程的树结点
     *
     * @return
     */
    List<TreeNode> getAllFlowTree(String treeNodeId);

    /**
     * 根据流程定义ID，获取最高版本的流程定义的环节map<id, name>
     *
     * @return
     */
    List<KeyValuePair> getFlowTasks(String flowDefId);

    /**
     * 根据流程定义ID，判断是否自动提交分支流
     *
     * @param flowDefId
     * @return
     */
    boolean isAutoSubmitForkTask(String flowDefId);

    /**
     * 根据流程定义ID，获取最高版本的流程定义的子流程分发环节map<id, name>
     *
     * @return
     */
    List<KeyValuePair> getFlowSubTasks(String flowDefId);

    /**
     * 根据流程定义UUID，获取流程环节信息
     *
     * @param treeNodeId
     * @param flowDefUuid
     * @return
     */
    List<TreeNode> getFlowTaskTree(String treeNodeId, String flowDefUuid);

    /**
     * 根据流程定义UUID，获取环节名称
     *
     * @param flowDefUuid
     * @param taskIds
     * @return
     */
    QueryItem getFlowTaskByTaskIds(String flowDefUuid, String taskIds);

    /**
     * 根据流程定义ID，获取最高版本的流程定义的流向map<id, name>
     *
     * @return
     */
    List<KeyValuePair> getFlowDirections(String flowDefId);

    /**
     * 根据存储单据UUID获取对应的显示单据
     *
     * @param formUuid
     * @return
     */
    List<DataItem> getVformsByPformUuid(String formUuid);

    /**
     * 根据动态表单UUID，获取表单区块信息
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getFormBlocks(String treeNodeId, String formUuid);

    /**
     * 根据流程定义ID，获取表单区块信息
     *
     * @param flowDefId
     * @return
     */
    List<TreeNode> getFormBlocksByFlowDefId(String flowDefId);

    /**
     * 根据区块编号获取名称
     *
     * @param formUuid
     * @param blockCodes
     * @return
     */
    QueryItem getBlockByBlockCodes(String formUuid, String blockCodes);

    List<TreeNode> getFormFields(String treeNodeId, String formUuid, String includeInputModes);

    /**
     * 根据动态表单UUID，获取表单字段信息
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getFormFields(String treeNodeId, String formUuid);

    /**
     * 根据动态表单UUID，获取表单字段信息
     *
     * @param formUuid
     * @return
     */
    Select2GroupData getFormFieldsAsSelect2GroupData(String formUuid);

    /**
     * 根据动态表单ID，获取表单字段信息
     *
     * @param treeNodeId
     * @param formId
     * @return
     */
    List<TreeNode> getFormFieldsByFormId(String treeNodeId, String formId);

    /**
     * 根据流程定义ID，获取表单字段信息
     *
     * @param flowDefId
     * @return
     */
    List<TreeNode> getFormFieldsByFlowDefId(String flowDefId);

    /**
     * 根据表单定义UUID获取所有字段的属性项列表
     *
     * @param formUuid
     * @return
     */
    Map<String, Object> getFieldPropertyInfos(String formUuid);

    /**
     * 根据动态表单UUID，获取从表信息
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getSubForms(String treeNodeId, String formUuid);

    /**
     * 根据动态表单字段名，获取字段显示名
     *
     * @param formUuid
     * @param fieldNames
     * @return
     */
    QueryItem getFormFieldByFieldNames(String formUuid, String fieldNames);

    Select2QueryData getFormFieldSelections(Select2QueryInfo queryInfo);

    /**
     * 获取单位业务角色
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getBizRoles(String treeNodeId);

    /**
     * 获取业务类别
     *
     * @return
     */
    List<DataItem> getBusinessTypes();

    /**
     * 根据业务类别获取业务角色
     *
     * @param businessType
     * @return
     */
    List<DataItem> getBusinessRoles(String businessType);

    /**
     * 获取所有可用的流水号定义列表
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getSerialNumbers(String treeNodeId);

    /**
     * 获取所有可用的打印模板列表
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getPrintTemplates(String treeNodeId);

    /**
     * 获取所有可用的消息模板列表
     *
     * @return
     */
    List<QueryItem> getMessageTemplates();

    /**
     * 获取所有可用的流程事件监听器
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getFlowListeners(String treeNodeId);

    /**
     * 获取所有可用的环节事件监听器
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getTaskListeners(String treeNodeId);

    /**
     * 获取所有可用的流向事件监听器
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getDirectionListeners(String treeNodeId);

    /**
     * 获取所有可用的计时器事件监听器
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getTimerListeners(String treeNodeId);

    /**
     * 获取所有流程访问权限提供者
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getFlowAccessPermissionProvider(String treeNodeId);

    /**
     * @param businessType
     * @param value
     * @return
     */
    QueryItem getFlowKeyValuePair(String businessType, String flowDefId);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<String> getAllUuids();

    /**
     * 获取流程所有树结点，以流程分类的形式返回
     *
     * @return
     */
    List<TreeNode> getAllFlowAsCategoryTree();

    /**
     * 获取应用了指定的表单字段的节点
     * lmw 2015-5-25 16:22
     */
    Map<String, Map<String, String>> getElementApplyFormField(String formUuid, String filed);

    /**
     * 获取环节办理人,人员可选项
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getTaskUserTaskHistorys(String treeNodeId, Map<String, String> tasksMap);

    /**
     * 获取环节办理人,人员可选项
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getTaskUserOptionUsers(String treeNodeId);

    /**
     * 获取环节办理人自定义接口
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getTaskUserCustomInterfaces(String treeNodeId);

    /**
     * 检测表达式
     *
     * @param expressionConfig
     * @return
     */
    boolean checkUserCustomExpression(String expressionConfig);

    /**
     * 获取集合表达式
     *
     * @param expressionConfig
     * @return
     */
    String getUserCustomExpression(String expressionConfig);

    /**
     * 执行表达式
     *
     * @param setExpression
     * @return
     */
    String evaluateUserCustomExpression(String setExpression);

    /**
     * 获取子流程分发的自定义接口
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getSubtaskDispatcherCustomInterfaces(String treeNodeId);

    /**
     * 获取分支流分发的自定义接口
     *
     * @param treeNodeId
     * @return
     */
    List<TreeNode> getCustomDispatcherBranchTaskInterfaces(String treeNodeId);

    /**
     * 获取当前用户所在单位的组织版本
     *
     * @return
     */
    List<OrganizationDto> getCurrentUserUnitOrgVersions();

    /**
     * 根据动态表单UUID，获取表单页签信息
     *
     * @param treeNodeId
     * @param formUuid
     * @return
     */
    List<TreeNode> getFormSubtabs(String treeNodeId, String formUuid);

    /**
     * 根据页签编号获取名称
     *
     * @param formUuid
     * @param names
     * @return
     */
    QueryItem getBySubtabsByName(String formUuid, String names);

    /**
     * 根据流程分类UUID列表及流程定义ID列表获取表单定义
     *
     * @param flowCategoryUuids
     * @param flowDefIds
     * @return
     */
    List<DyFormFormDefinition> listFormDefinition(List<String> flowCategoryUuids, List<String> flowDefIds);

    /**
     * select2查询接口，根据流程定义ID加载流向数据
     *
     * @param queryInfo
     * @return
     */
    Select2QueryData loadDirectionSelectData(Select2QueryInfo queryInfo);

    /**
     * @param queryInfo
     * @return
     */
    Select2QueryData loadFlowTaskSelectData(Select2QueryInfo queryInfo);

    List<FlowDefinition> getRecentUseFlowDefintions(boolean isMobile);

    List<TreeNode> getAllMobileFlowAsCategoryTree();

    /**
     * 根据流程定义ID，获取流程计时器信息
     *
     * @param flowId
     * @return List<Map < id / name>>
     */
    List<Map<String, Object>> listFlowTimerByFlowId(String flowId);

    /**
     * 根据流程定义UUID判断是否存在未办结的流程实例
     *
     * @param flowDefUuid
     * @return
     */
    boolean isExistsUnfinishedFlowInstanceByFlowDefUuid(String flowDefUuid);

    /**
     * 修改流程办理人
     *
     * @param flowDefUuid
     * @param definitionUserUuids
     * @param modifyParams
     * @param saveAsNewVersion
     * @return
     */
    List<String> modifyUserWithFlowDefinitionUserUuid(String flowDefUuid, List<String> definitionUserUuids,
                                                      FlowDefinitionUserModifyParams modifyParams, boolean saveAsNewVersion);

}
