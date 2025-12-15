/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.acceptor;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.facade.service.PrintTemplateFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.entity.SerialNumber;
import com.wellsoft.pt.basicdata.serialnumber.facade.service.SerialNumberFacadeService;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.parser.FlowDefinitionParser;
import com.wellsoft.pt.bpm.engine.service.FlowSchemaService;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.facade.service.ResourceFacadeService;
import com.wellsoft.pt.workflow.dao.FlowFormatDao;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
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
public class FlowDefinitionIexportData extends IexportData {

    public static final String FLOW_DEFINITION = "flowDefinition";
    public static final String FLOW_SCHEMA = "flowSchema";

    public FlowDefinition flowDefinition;

    public FlowDefinitionIexportData(FlowDefinition flowDefinition) {
        this.flowDefinition = flowDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return flowDefinition.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "流程主体：" + flowDefinition.getName();
    }

    @Override
    public Integer getRecVer() {
        return flowDefinition.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.FlowDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        Map<String, byte[]> dataMap = new HashMap<String, byte[]>();
        try {
            FlowSchemaService flowSchemaService = ApplicationContextHolder.getBean(FlowSchemaService.class);
            FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
            IexportData flowSchemaIexportData = IexportDataProviderFactory.getDataProvider(IexportType.FlowSchema)
                    .getData(flowDefinition.getFlowSchemaUuid());
            dataMap.put(FLOW_SCHEMA, IOUtils.toByteArray(IexportDataResultSetUtils.iexportDataResultSet2InputStream(
                    flowSchemaIexportData, flowSchema)));
            dataMap.put(FLOW_DEFINITION, IOUtils.toByteArray(IexportDataResultSetUtils
                    .iexportDataResultSet2InputStream(this, flowDefinition)));
            return IexportDataResultSetUtils.object2InputStream(dataMap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        // 流程表单定义表
        if (StringUtils.isNotBlank(flowDefinition.getFormUuid())) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.FormDefinition).getData(
                    flowDefinition.getFormUuid()));
        }
        // 流程分类
        FlowCategoryService flowCategoryService = ApplicationContextHolder.getBean(FlowCategoryService.class);

        if (StringUtils.isNotBlank(flowDefinition.getCategory())) {
            FlowCategory flowCategorys = flowCategoryService.getOne(flowDefinition.getCategory());
            if (flowCategorys != null) {
                dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.FlowCategory).getData(
                        flowCategorys.getUuid()));
            }
        }
        FlowSchemaService flowSchemaService = ApplicationContextHolder.getBean(FlowSchemaService.class);
        FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());// flowDefinition.getFlowSchema();
        // 等价流程
        String eqflowId = flowDefinition.getEqualFlowId();
        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
        if (StringUtils.isNotBlank(eqflowId)) {
            FlowDefinition flowDefinitionChild = flowService.getFlowDefinitionById(eqflowId);
            if (flowDefinitionChild != null) {
                dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.FlowDefinition).getData(
                        flowDefinitionChild.getUuid()));
            }
        }
        // 资源
        ResourceFacadeService resourceService = ApplicationContextHolder.getBean(ResourceFacadeService.class);
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        PrintTemplateFacadeService printTemplateService = ApplicationContextHolder
                .getBean(PrintTemplateFacadeService.class);
        FlowFormatDao formatDao = ApplicationContextHolder.getBean(FlowFormatDao.class);
        MessageTemplateApiFacade messageTemplateService = ApplicationContextHolder
                .getBean(MessageTemplateApiFacade.class);
        SerialNumberFacadeService serialNumberService = ApplicationContextHolder
                .getBean(SerialNumberFacadeService.class);
        if (flowSchema != null) {// rights、doneRights、monitorRights、adminRights及buttons
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
                MessageTemplate messageTemplate = messageTemplateService.getById(messageTemplateElement.getId());
                if (messageTemplate != null) {
                    dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.MessageTemplate).getData(
                            messageTemplate.getUuid()));
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
                        Resource resource = resourceService.getResourceByCode(resourceCode);
                        if (resource != null) {
                            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.ResourceParent)
                                    .getData(resource.getUuid()));
                        }
                    }
                }

                for (UnitElement unitElement : unitElements) {
                    String resourceCode = unitElement.getValue();
                    if (StringUtils.isNotBlank(resourceCode)) {
                        Resource resource = resourceService.getResourceByCode(resourceCode);
                        if (resource != null) {
                            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.ResourceParent)
                                    .getData(resource.getUuid()));
                        }
                    }
                }

            }

            for (UnitElement unitElement : taskUsers) {
                String code = unitElement.getValue();
                if (code.startsWith("G")) {
// TODO
//                    MultiOrgGroup group = orgApiFacade.getGroupById(code);
//                    if (group != null) {
//                        dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.Group).getData(
//                                group.getUuid()));
//                    }
                }
            }
            // 打印模板
            for (String printTempltId : printTempltIds) {
                PrintTemplate printTemplate = printTemplateService.getPrintTemplateById(printTempltId);
                if (printTemplate != null) {
                    dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.PrintTemplate).getData(
                            printTemplate.getUuid()));
                }
            }
            // 打印模板
            for (String printTempltUuid : printTempltUuids) {
                PrintTemplate printTemplate = printTemplateService.getByUuid(printTempltUuid);
                if (printTemplate != null) {
                    dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.PrintTemplate).getData(
                            printTemplate.getUuid()));
                }
            }

            // 信息格式
            for (RecordElement recordElement : recordElements) {
                String id = recordElement.getValue();
                FlowFormat flowFormat = formatDao.getByCode(id);
                if (flowFormat != null) {
                    IexportData dependency = IexportDataProviderFactory.getIexportData(IexportType.FlowFormat,
                            flowFormat.getUuid());
                    dependencies.add(dependency);
                }

            }
            // 流水号
            for (String serialNo : serialNos) {
                SerialNumber serialNumber = serialNumberService.getById(serialNo);
                if (serialNumber != null) {
                    IexportData dependency = IexportDataProviderFactory.getDataProvider(IexportType.SerialNumber)
                            .getData(serialNumber.getUuid());
                    dependencies.add(dependency);
                }

            }
        }
        // 流程二开配置
        return dependencies;
    }
}
