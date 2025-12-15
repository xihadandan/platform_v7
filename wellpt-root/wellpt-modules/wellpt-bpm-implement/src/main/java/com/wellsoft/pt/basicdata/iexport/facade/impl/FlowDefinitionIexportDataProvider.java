/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.facade.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.FlowDefinitionIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.parser.FlowDefinitionParser;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class FlowDefinitionIexportDataProvider extends AbstractIexportDataProvider<FlowDefinition, String> {
    static {
        TableMetaData.register(IexportType.FlowDefinition, "流程定义", FlowDefinition.class);
    }

    @Autowired
    private IexportDataRecordSetService iexportDataMetaDataService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private FlowSchemaService flowSchemaService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    @Transactional()
    public String getType() {
        return IexportType.FlowDefinition;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        FlowDefinition flowDefinition = this.dao.get(FlowDefinition.class, uuid);
        if (flowDefinition == null) {
            return new ErrorDataIexportData(IexportType.FlowDefinition, "找不到对应的流程定义依赖关系,可能已经被删除", "流程定义", uuid);
        }
        return new FlowDefinitionIexportData(flowDefinition);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#storeData(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        Object object = IexportDataResultSetUtils.inputStream2Object(iexportData.getInputStream());
        Map<String, byte[]> dataMap = (Map<String, byte[]>) object;
        InputStream flowSchemaStream = new ByteArrayInputStream((dataMap.get(FlowDefinitionIexportData.FLOW_SCHEMA)));
        InputStream flowDefinitionStream = new ByteArrayInputStream(
                dataMap.get(FlowDefinitionIexportData.FLOW_DEFINITION));

        IexportDataRecordSet flowSchemaStreamDataRecordSet = IexportDataResultSetUtils
                .inputStream2IexportDataResultSet(flowSchemaStream);
        iexportDataMetaDataService.save(flowSchemaStreamDataRecordSet);
        IexportDataRecordSet flowDefinitionDataRecordSet = IexportDataResultSetUtils
                .inputStream2IexportDataResultSet(flowDefinitionStream);
        iexportDataMetaDataService.save(flowDefinitionDataRecordSet);

        this.dao.flush();
        FlowDefinition flowDefinition = this.dao.get(FlowDefinition.class, iexportData.getUuid());
        flowDefinition.getVersion();
        FlowDefinition example = new FlowDefinition();
        example.setId(flowDefinition.getId());
        example.setVersion(flowDefinition.getVersion());
        if (this.dao.countByExample(example) > 1) {
            String msg = "流程ID为[" + flowDefinition.getId() + "], 版本号为[" + flowDefinition.getVersion()
                    + "]的流程已存在，但UUID不一致!";
            throw new RuntimeException(msg);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 流程定义ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.FlowDefinition), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(FlowDefinition.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(FlowDefinition flowDefinition) {
        return "流程: " + flowDefinition.getName();
    }

    @Override
    public TreeNode treeNode(String s) {
        TreeNode node = super.treeNode(s);
        // 流程关联的表单
        FlowDefinition flowDefinition = getEntity(s);
        if (flowDefinition != null) {
            if (StringUtils.isNotBlank(flowDefinition.getFormUuid())) {
                this.exportTreeNodeByDataProvider(flowDefinition.getFormUuid(),IexportType.FormDefinition,node);
//                TreeNode child = this.exportTreeNodeByDataProvider(flowDefinition.getFormUuid(), IexportType.FormDefinition);
//                if (child != null) {
//                    node.getChildren().add(child);
//                }
            }
        }

        // 流程分类
        try {
            if (flowDefinition != null && StringUtils.isNotBlank(flowDefinition.getCategory())) {
                TreeNode child = this.exportTreeNodeByDataProvider(flowDefinition.getCategory(), IexportType.FlowCategory);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
        } catch (Exception e) {
            logger.error("流程定义导出关联流程分类异常: ", e);
        }

        // 流程定义存储
        FlowElement flowElement = null;
        FlowSchema flowSchema = StringUtils.isNotBlank(flowDefinition.getFlowSchemaUuid()) ? flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid()) : null;// flowDefinition.getFlowSchema();
        if (flowSchema != null && StringUtils.isNotBlank(flowSchema.getUuid())) {
            TreeNode child = this.exportTreeNodeByDataProvider(flowSchema.getUuid(), IexportType.FlowSchema);
            if (child != null) {
                node.getChildren().add(child);
            }
            flowElement = FlowDefinitionParser.parseFlow(flowSchema);
        }
        if (flowElement != null) {
            PropertyElement propertyElement = flowElement.getProperty();
            // 打印
            Set<String> printTempltIds = new HashSet<String>();
            Set<String> printTempltUuids = new HashSet<String>();
            String printTemplatedId = propertyElement.getPrintTemplateId();
            if (StringUtils.isNotBlank(printTemplatedId)) {
                printTempltIds.add(printTemplatedId);
            }
            String printTemplatedUuid = propertyElement.getPrintTemplateUuid();
            if (StringUtils.isNotBlank(printTemplatedUuid)) {
                printTempltUuids.add(printTemplatedUuid);
            }
            // 流水号
            Set<String> serialNos = new HashSet<String>();
            // 消息模板
            List<MessageTemplateElement> messageTempleIdList = propertyElement.getMessageTemplates();
            if (CollectionUtils.isNotEmpty(messageTempleIdList)) {
                for (MessageTemplateElement messageTemplateElement : messageTempleIdList) {
                    TreeNode child = this.exportTreeNodeByDataProvider(messageTemplateElement.getId(), IexportType.MessageTemplate);
                    if (child != null) {
                        node.getChildren().add(child);
                    }
                }
            }
            // 消息格式
            List<RecordElement> recordElements = new ArrayList<RecordElement>();
            recordElements.addAll(propertyElement.getRecords());

            List<UnitElement> taskUsers = new ArrayList<UnitElement>();
            taskUsers.addAll(propertyElement.getCreators());
            // 取出人员
            taskUsers.addAll(propertyElement.getUsers());
            taskUsers.addAll(propertyElement.getMonitors());
            taskUsers.addAll(propertyElement.getAdmins());
            taskUsers.addAll(propertyElement.getViewers());

            List<TaskElement> taskElements = flowElement.getTasks();
            for (TaskElement taskElement : taskElements) {
                // 取出人员
                taskUsers.addAll(taskElement.getUsers());
                taskUsers.addAll(taskElement.getCopyUsers());
                taskUsers.addAll(taskElement.getMonitors());
                taskUsers.addAll(taskElement.getMonitors());
                // 取出打印模板
                String tempId = taskElement.getPrintTemplateId();
                if (StringUtils.isNotBlank(tempId)) {
                    printTempltIds.add(tempId);
                }
                String tempUuid = taskElement.getPrintTemplateUuid();
                if (StringUtils.isNotBlank(tempUuid)) {
                    printTempltUuids.add(tempUuid);
                }

                serialNos.add(taskElement.getSerialNo());
                // 取出消息格式
                recordElements.addAll(taskElement.getRecords());
                // 取出资源
                List<RightUnitElement> unitElements = taskElement.getRights();
                unitElements.addAll(taskElement.getDoneRights());
                unitElements.addAll(taskElement.getMonitorRights());
                unitElements.addAll(taskElement.getAdminRights());
                List<ButtonElement> buttonElements = taskElement.getButtons();
                for (ButtonElement buttonElement : buttonElements) {
                    String resourceCode = buttonElement.getNewCode();
                    if (StringUtils.isNotBlank(resourceCode)) {
                        TreeNode child = this.exportTreeNodeByDataProvider(resourceCode, IexportType.ResourceParent);
                        if (child != null) {
                            node.getChildren().add(child);
                        }
                    }
                }
                for (UnitElement unitElement : unitElements) {
                    String resourceCode = unitElement.getValue();
                    if (StringUtils.isNotBlank(resourceCode)) {
                        TreeNode child = this.exportTreeNodeByDataProvider(resourceCode, IexportType.ResourceParent);
                        if (child != null) {
                            node.getChildren().add(child);
                        }
                    }
                }
            }

            // 打印模板
            for (String printTempltId : printTempltIds) {
                TreeNode child = this.exportTreeNodeByDataProvider(printTempltId, IexportType.PrintTemplate);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
            // 打印模板
            for (String printTempltUuid : printTempltUuids) {
                TreeNode child = this.exportTreeNodeByDataProvider(printTempltUuid, IexportType.PrintTemplate);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }

            // 信息格式
            for (RecordElement recordElement : recordElements) {
                String id = recordElement.getValue();
                TreeNode child = this.exportTreeNodeByDataProvider(id, IexportType.FlowFormat);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
            // 新版流水号
            for (String serialNo : serialNos) {
                TreeNode child = this.exportTreeNodeByDataProvider(serialNo, IexportType.SnSerialNumberDefinition);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }

            // 计时器配置
            List<TimerElement> timers = new ArrayList<>();
            timers.addAll(flowElement.getTimers());
            for (TimerElement timerElement : timers) {
                String timerConfigUuid = timerElement.getTimerConfigUuid();
                TreeNode child = this.exportTreeNodeByDataProvider(timerConfigUuid, IexportType.TsTimerConfig);
                if (child != null) {
                    node.getChildren().add(child);
                }
            }
        }

        return node;
    }

    @Override
    protected IExportEntityStream exportStream(String s) {
        IExportEntityStream stream = super.exportStream(s);
        FlowDefinition definition = getEntity(s);
        return stream;
    }

    @Override
    public <P extends JpaEntity<String>, C extends JpaEntity<String>> BusinessProcessor<FlowDefinition> saveOrUpdate(
            Map<String, ProtoDataBeanTree<FlowDefinition, P, C>> map, Collection<Serializable> uuids) {
        List<FlowDefinition> oldList = this.getList(uuids);
        List<FlowDefinition> addList = new ArrayList<>();
        List<FlowDefinition> updateList = new ArrayList<>();
        Map<String, FlowDefinition> oldMap = new HashMap<>(16);
        List<FlowDefinition> list = new ArrayList<>();
        for (FlowDefinition old : oldList) {
            FlowDefinition oldFlowDefinition = new FlowDefinition();
            BeanUtils.copyProperties(old, oldFlowDefinition);
            ProtoDataBeanTree<FlowDefinition, P, C> t = map.get(old.getUuid());
            // 版本号不一致 修改
            if (!old.getRecVer().equals(t.getProtoDataBean().getData().getRecVer())) {
                String sql = super.entityToUpdateSql(t.getProtoDataBean().getData());
                super.executeUpdateSql(sql, t);
            }
            updateList.add(t.getProtoDataBean().getData());
            oldFlowDefinition.setFlowSchemaUuid(old.getFlowSchemaUuid());
            oldMap.put(old.getUuid(), oldFlowDefinition);
            map.remove(old.getUuid());
            // 删除流程定义对象缓存
            FlowSchemeService flowSchemeService = ApplicationContextHolder.getBean(FlowSchemeService.class);
            flowSchemeService.clearFlowElementCache(old.getUuid());
        }
        // 剩余的添加
        for (ProtoDataBeanTree<FlowDefinition, P, C> t : map.values()) {
            String sql = this.entityToInsertSql(t.getProtoDataBean().getData());
            addList.add(t.getProtoDataBean().getData());
            this.executeUpdateSql(sql, t);
        }

        BusinessProcessor<FlowDefinition> businessProcessor = new BusinessProcessor<FlowDefinition>(addList, updateList,
                oldMap) {
            @Override
            public void handle(Map<String, ProtoDataBean> beanMap) {
                for (FlowDefinition flowDefinition : this.getAddList()) {
                    ProtoDataBean<FlowSchema> bean = beanMap.get(IexportType.FlowSchema + Separator.UNDERLINE.getValue()
                            + flowDefinition.getFlowSchemaUuid());
                    FlowElement flowElement = FlowDefinitionParser.parseFlow(bean.getData());
                    flowDefinitionService.saveAcl(flowDefinition, flowElement);
                }
                // task:6441 开发-主导-流程定义修改日志
                // 类似新增
                for (FlowDefinition flowDefinition : this.getAddList()) {
                    flowDefinitionService.saveLogManageOperation(flowDefinition, null,
                            LogManageOperationEnum.defImport);
                }
                // 类似编辑
                for (FlowDefinition flowDefinition : this.getUpdateList()) {
                    FlowSchema oldflowSchema = flowSchemaService.getOne(this.getOldMap().get(flowDefinition.getUuid()).getFlowSchemaUuid());
                    flowDefinitionService.flushSession();
                    flowDefinitionService.clearSession();
                    flowDefinitionService.saveLogManageOperation(flowDefinition,
                            this.getOldMap().get(flowDefinition.getUuid()), oldflowSchema,
                            LogManageOperationEnum.defImport);
                }
            }
        };
        return businessProcessor;
    }

    @Override
    public void putChildProtoDataHqlParams(FlowDefinition flowDefinition, Map<String, FlowDefinition> parentMap,
                                           Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        // 流程表单定义表
        if (StringUtils.isNotBlank(flowDefinition.getFormUuid())) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + flowDefinition.getFormUuid(),
                    flowDefinition);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FormDefinition))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FormDefinition), this.getProtoDataHql("FormDefinition"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FormDefinition)), flowDefinition.getFormUuid());
        }

        if (StringUtils.isNotBlank(flowDefinition.getCategory())) {
            parentMap.put(start + "category" + Separator.UNDERLINE.getValue() + flowDefinition.getCategory(),
                    flowDefinition);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FlowCategory))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FlowCategory), this.getProtoDataHql("FlowCategory"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FlowCategory)), flowDefinition.getCategory());
        }
        FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
        // 等价流程
        String eqflowId = flowDefinition.getEqualFlowId();
        if (StringUtils.isNotBlank(eqflowId)) {
            parentMap.put(start + "eqflowId" + Separator.UNDERLINE.getValue() + eqflowId, flowDefinition);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FlowDefinition))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FlowDefinition), this.getProtoDataHql("FlowDefinition"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FlowDefinition)), eqflowId);
        }
        // 资源
        if (flowSchema != null) {// rights、doneRights、monitorRights、adminRights及buttons
            parentMap.put(start + "flowSchema" + Separator.UNDERLINE.getValue() + flowDefinition.getUuid(),
                    flowDefinition);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FlowSchema))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FlowSchema), this.getProtoDataHql("FlowSchema"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FlowSchema)), flowSchema.getUuid());

            FlowElement flowEntity = FlowDefinitionParser.parseFlow(flowSchema);
            PropertyElement propertyElement = flowEntity.getProperty();
            // 打印
            Set<String> printTempltIds = new HashSet<String>();
            Set<String> printTempltUuids = new HashSet<String>();
            String printTemplatedId = propertyElement.getPrintTemplateId();
            if (StringUtils.isNotBlank(printTemplatedId)) {
                printTempltIds.add(printTemplatedId);
            }
            String printTemplatedUuid = propertyElement.getPrintTemplateUuid();
            if (StringUtils.isNotBlank(printTemplatedUuid)) {
                printTempltUuids.add(printTemplatedUuid);
            }
            // 流水号
            Set<String> serialNos = new HashSet<String>();
            // 消息模板
            List<MessageTemplateElement> messageTempleIdList = propertyElement.getMessageTemplates();
            for (MessageTemplateElement messageTemplateElement : messageTempleIdList) {
                parentMap.put(
                        start + "messageTemplate" + Separator.UNDERLINE.getValue() + messageTemplateElement.getId(),
                        flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.MessageTemplate))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.MessageTemplate),
                            this.getProtoDataHqlId("MessageTemplate"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.MessageTemplate)),
                        messageTemplateElement.getId(), "ids");
            }
            // 消息格式
            List<RecordElement> recordElements = new ArrayList<RecordElement>();
            recordElements.addAll(propertyElement.getRecords());

            List<UnitElement> taskUsers = new ArrayList<UnitElement>();
            taskUsers.addAll(propertyElement.getCreators());
            // 取出人员
            taskUsers.addAll(propertyElement.getUsers());
            taskUsers.addAll(propertyElement.getMonitors());
            taskUsers.addAll(propertyElement.getAdmins());
            taskUsers.addAll(propertyElement.getViewers());

            List<TaskElement> taskElements = flowEntity.getTasks();
            for (TaskElement taskElement : taskElements) {
                // 取出人员
                taskUsers.addAll(taskElement.getUsers());
                taskUsers.addAll(taskElement.getCopyUsers());
                taskUsers.addAll(taskElement.getMonitors());
                taskUsers.addAll(taskElement.getMonitors());
                // 取出打印模板
                String tempId = taskElement.getPrintTemplateId();
                if (StringUtils.isNotBlank(tempId)) {
                    printTempltIds.add(tempId);
                }
                String tempUuid = taskElement.getPrintTemplateUuid();
                if (StringUtils.isNotBlank(tempUuid)) {
                    printTempltUuids.add(tempUuid);
                }

                serialNos.add(taskElement.getSerialNo());
                // 取出消息格式
                recordElements.addAll(taskElement.getRecords());
                // 取出资源
                List<RightUnitElement> unitElements = taskElement.getRights();
                unitElements.addAll(taskElement.getDoneRights());
                unitElements.addAll(taskElement.getMonitorRights());
                unitElements.addAll(taskElement.getAdminRights());
                List<ButtonElement> buttonElements = taskElement.getButtons();
                for (ButtonElement buttonElement : buttonElements) {
                    String resourceCode = buttonElement.getNewCode();
                    if (StringUtils.isNotBlank(resourceCode)) {
                        parentMap.put(start + "resourceParent" + Separator.UNDERLINE.getValue() + resourceCode,
                                flowDefinition);
                        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.ResourceParent))) {
                            hqlMap.put(this.getChildHqlKey(IexportType.ResourceParent),
                                    this.getProtoDataHqlCode("Resource"));
                        }
                        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.ResourceParent)), resourceCode,
                                "codes");
                    }
                }
                for (UnitElement unitElement : unitElements) {
                    String resourceCode = unitElement.getValue();
                    if (StringUtils.isNotBlank(resourceCode)) {
                        parentMap.put(start + "resourceParent" + Separator.UNDERLINE.getValue() + resourceCode,
                                flowDefinition);
                        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.ResourceParent))) {
                            hqlMap.put(this.getChildHqlKey(IexportType.ResourceParent),
                                    this.getProtoDataHqlCode("Resource"));
                        }
                        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.ResourceParent)), resourceCode,
                                "codes");
                    }
                }
            }

            for (UnitElement unitElement : taskUsers) {
                String code = unitElement.getValue();
                if (code.startsWith("G")) {
                    parentMap.put(start + "group" + Separator.UNDERLINE.getValue() + code, flowDefinition);
                    if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.Group))) {
                        hqlMap.put(this.getChildHqlKey(IexportType.Group), this.getProtoDataHqlId("MultiOrgGroup"));
                    }
                    this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.Group)), code, "ids");
                }
            }
            // 打印模板
            for (String printTempltId : printTempltIds) {
                parentMap.put(start + "printTemplateId" + Separator.UNDERLINE.getValue() + printTempltId,
                        flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.PrintTemplate))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.PrintTemplate),
                            new ProtoDataHql(getType(), "PrintTemplate"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.PrintTemplate)), printTempltId, "ids");
            }
            // 打印模板
            for (String printTempltUuid : printTempltUuids) {
                parentMap.put(start + "printTemplate" + Separator.UNDERLINE.getValue() + printTempltUuid,
                        flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.PrintTemplate))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.PrintTemplate),
                            new ProtoDataHql(getType(), "PrintTemplate"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.PrintTemplate)), printTempltUuid);
            }

            // 信息格式
            for (RecordElement recordElement : recordElements) {
                String id = recordElement.getValue();
                parentMap.put(start + "flowFormat" + Separator.UNDERLINE.getValue() + id, flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FlowFormat))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.FlowFormat), this.getProtoDataHqlCode("FlowFormat"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FlowFormat)), id, "codes");
            }
            // 流水号
            for (String serialNo : serialNos) {
                parentMap.put(start + "serialNumber" + Separator.UNDERLINE.getValue() + serialNo, flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.SerialNumber))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.SerialNumber), this.getProtoDataHqlId("SerialNumber"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.SerialNumber)), serialNo, "ids");
            }
            // 新版流水号
            for (String serialNo : serialNos) {
                parentMap.put(start + "snSerialNumberDefinition" + Separator.UNDERLINE.getValue() + serialNo, flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.SnSerialNumberDefinition))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.SnSerialNumberDefinition), this.getProtoDataHqlId("SnSerialNumberDefinitionEntity"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.SnSerialNumberDefinition)), serialNo, "ids");
            }
            // 计时器配置
            List<TimerElement> timers = new ArrayList<>();
            timers.addAll(flowEntity.getTimers());
            for (TimerElement timerElement : timers) {
                String timerConfigUuid = timerElement.getTimerConfigUuid();
                parentMap.put(start + IexportType.TsTimerConfig + Separator.UNDERLINE.getValue() + timerConfigUuid, flowDefinition);
                if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.TsTimerConfig))) {
                    hqlMap.put(this.getChildHqlKey(IexportType.TsTimerConfig), this.getProtoDataHql("TsTimerConfigEntity"));
                }
                this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.TsTimerConfig)), timerConfigUuid);
            }
        }
        if (StringUtils.isNotBlank(flowDefinition.getModuleId())) {
            this.putAppFunctionParentMap(flowDefinition, parentMap, hqlMap);
        }
    }

    @Override
    protected List<IExportTable> childTableStream() {
        // 导出表单依赖的资源定义关系数据
        return Lists.newArrayList(
                /* 国际化语言定义 */
                new IExportTable("select * from APP_DEF_ELEMENT_I18N where def_id=:id and apply_to='" + IexportType.FlowDefinition + "'"),
                new IExportTable("select s.* from wf_flow_schema s, wf_flow_definition d  where d.uuid=:uuid and d.flow_schema_uuid = s.uuid")

        );
    }

    protected ProtoDataHql getProtoDataHqlId(String entityName) {
        ProtoDataHql protoDataHql = new ProtoDataHql(getType(), entityName);
        protoDataHql.setGenerateHql(new GenerateHql() {
            @Override
            public void build(ProtoDataHql protoDataHql) {
                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                HqlUtils.appendSql("id", protoDataHql.getParams(), protoDataHql.getSbHql(),
                        (Set<Serializable>) protoDataHql.getParams().get("ids"));
            }
        });
        return protoDataHql;
    }

    protected ProtoDataHql getProtoDataHqlCode(String entityName) {
        ProtoDataHql protoDataHql = new ProtoDataHql(getType(), entityName);
        protoDataHql.setGenerateHql(new GenerateHql() {
            @Override
            public void build(ProtoDataHql protoDataHql) {
                protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                HqlUtils.appendSql("code", protoDataHql.getParams(), protoDataHql.getSbHql(),
                        (Set<Serializable>) protoDataHql.getParams().get("codes"));
            }
        });
        return protoDataHql;
    }

    @Override
    public Map<String, List<FlowDefinition>> getParentMapList(ProtoDataHql protoDataHql) {
        List<FlowDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(),
                FlowDefinition.class);
        Map<String, List<FlowDefinition>> map = new HashMap<>();
        // 页面或组件定义依赖的流程定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (FlowDefinition flowDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue()
                        + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, flowDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (FlowDefinition flowDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + flowDefinition.getUuid();
                this.putParentMap(map, flowDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowBusinessDefinition)) {
            String hql = "from FlowDefinition t where t.id in(:uuids)";
            list = this.dao.find(hql, protoDataHql.getParams(), FlowDefinition.class);
            for (FlowDefinition flowDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + flowDefinition.getId();
                this.putParentMap(map, flowDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (FlowDefinition flowDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "eqflowId"
                        + Separator.UNDERLINE.getValue() + flowDefinition.getId();
                this.putParentMap(map, flowDefinition, key);
            }
        } else {
            return super.getParentMapList(protoDataHql);
        }
        return map;
    }

    @Override
    public FlowDefinition saveEntityStream(IExportEntityStream stream) {
        FlowDefinition flowDefinition = super.saveEntityStream(stream);
        if (flowDefinition != null) {
            FlowSchemeService flowSchemeService = ApplicationContextHolder.getBean(FlowSchemeService.class);
            flowSchemeService.clearFlowElementCache(flowDefinition.getUuid());
        }
        return flowDefinition;
    }

}
