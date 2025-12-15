/*
 * @(#)2012-11-15 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Encoding;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.exception.StarFlowParserException;
import com.wellsoft.pt.bpm.engine.util.NodeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Description: 流程定义解释类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-15.1	zhulh		2012-11-15		Create
 * </pre>
 * @date 2012-11-15
 */
public class FlowDefinitionParser implements FlowDefConstants {
    /**
     * 创建代码流程定义的XML的文档对象
     *
     * @param xml
     * @return
     */
    public static Document createDocument(String xml) {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            // XML外部实体注入XXE
            reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
            reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            reader.setEncoding(Encoding.UTF8.getValue());
            document = reader.read(IOUtils.toInputStream(xml, Encoding.UTF8.getValue()));
            document.setXMLEncoding(Encoding.UTF8.getValue());
        } catch (Exception e) {
            throw new StarFlowParserException("流程定义信息不正确", e);
        }
        return document;
    }

    /**
     * 解析流程
     *
     * @param flowSchema
     * @return
     */
    public static FlowElement parseFlow(FlowSchema flowSchema) {
        FlowElement flowElement = null;
        String definitionJson = flowSchema.getDefinitionJsonAsString();
        if (StringUtils.isNotBlank(definitionJson)) {
            flowElement = JsonUtils.json2Object(definitionJson, FlowElement.class);
            flowElement.setDefinitionJson(definitionJson);
        } else {
            flowElement = parseFlow(flowSchema.getContentAsString());
        }
        return flowElement;
    }

    /**
     * @param configuration
     * @return
     */
    public static FlowElement parseFlow(FlowConfiguration configuration) {
        FlowElement flowElement = null;
        String definitionXml = configuration.asXML();
        String definitionJson = configuration.asJSON();
        if (StringUtils.isNotBlank(definitionJson)) {
            flowElement = JsonUtils.json2Object(definitionJson, FlowElement.class);
            flowElement.setDefinitionJson(definitionJson);
        } else {
            flowElement = parseFlow(definitionXml);
        }
        return flowElement;
    }

    /**
     * 解析流程
     *
     * @param xml
     * @return
     */
    public static FlowElement parseFlow(String xml) {
        Document document = createDocument(xml);
        Element root = document.getRootElement();
        FlowElement flow = new FlowElement();
        flow.setXmlDefinition(true);
        flow.setDefinitionXml(xml);
        // 基本属性
        // 名称
        String name = root.attributeValue(FLOW_NAME);
        flow.setName(name);
        // 唯一标识(别名)
        String id = root.attributeValue(FLOW_ID);
        flow.setId(id);
        // 编号
        String sn = root.attributeValue(FLOW_CODE);
        flow.setCode(sn);
        // 版本号
        String version = root.attributeValue(FLOW_VERSION);
        flow.setVersion(version);
        // 系统归属单位ID
        String systemUnitId = root.attributeValue(FLOW_SYSTEM_UNIT_ID);
        flow.setSystemUnitId(systemUnitId);

        flow.setModuleId(root.attributeValue(FLOW_MODULE_ID));// 归属模块ID

        // 主键
        String uuid = root.attributeValue(FLOW_UUID);
        flow.setUuid(uuid);
        // 应用类型
        String applyId = root.attributeValue(FLOW_APPLY_ID);
        flow.setApplyId(applyId);
        /* add by huanglinchuan2014.10.20 begin */
        // 流程标题表达式
        String titleExpression = root.attributeValue(FLOW_TITLE_EXPRESSION);
        flow.setTitleExpression(titleExpression);
        /* add by huanglinchuan2014.10.20 end */

        // 解析流程属性
        parseProperty(document, flow);

        // 解析定时器
        parseTimer(document, flow);

        // 解析任务
        parseTask(document, flow);

        // 解析流向
        parseDirection(document, flow);

        // 解析文本
        parseLabel(document, flow);

        // 解析已删除
        parseDelete(document, flow);

        return flow;
    }

    /**
     * 解析流程属性
     *
     * @param document
     * @param flow
     */
    private static void parseProperty(Document document, FlowElement flow) {
        PropertyElement property = new PropertyElement();
        Element propertyNode = selectSingleElement(document, FLOW_PROPERTY_XPATH);

        // 元素
        // 基本属性
        if (propertyNode != null) {
            // 流程分类
            String categorySN = NodeUtil.getNodeStringValue(propertyNode, FLOW_CATEGORY_SN);
            property.setCategorySN(categorySN);
            // 流程表单
            String formID = NodeUtil.getNodeStringValue(propertyNode, FLOW_FORMID);
            property.setFormID(formID);

            // 等价流程
            Element equalFlowNode = selectSingleElement(document, FLOW_EQUALFLOW_XPATH);
            String equalFlowName = NodeUtil.getNodeStringValue(equalFlowNode, FLOW_EQUAL_FLOW_NAME);
            String equalFlowID = NodeUtil.getNodeStringValue(equalFlowNode, FLOW_EQUAL_FLOWID);
            EqualFlowElement equalFlow = new EqualFlowElement();
            equalFlow.setName(equalFlowName);
            equalFlow.setId(equalFlowID);
            property.setEqualFlow(equalFlow);

            // 默认的组织版本和是否自动更新最新版本
            String useDefaultOrg = NodeUtil.getNodeStringValue(propertyNode, FLOW_USE_DEFAULT_ORG);
            String orgId = NodeUtil.getNodeStringValue(propertyNode, FLOW_ORG_ID);
            String enableMultiOrg = NodeUtil.getNodeStringValue(propertyNode, FLOW_ENABLE_MULTI_ORG);
            String multiOrgId = NodeUtil.getNodeStringValue(propertyNode, FLOW_MULTI_ORG_ID);
            String autoUpgradeOrgVersion = NodeUtil.getNodeStringValue(propertyNode, FLOW_AUTO_UPGRADE_ORG_VERSION);
            property.setUseDefaultOrg(useDefaultOrg);
            property.setOrgId(orgId);
            property.setEnableMultiOrg(enableMultiOrg);
            property.setMultiOrgId(multiOrgId);
            property.setAutoUpgradeOrgVersion(autoUpgradeOrgVersion);

            String multiJobFlowType = NodeUtil.getNodeStringValue(propertyNode, MULTI_JOB_FLOW_TYPE);
            property.setMultiJobFlowType(multiJobFlowType);

            String pcShowFlag = NodeUtil.getNodeStringValue(propertyNode, PC_SHOW_FLAG);
            property.setPcShowFlag(pcShowFlag);
            String isMobileShow = NodeUtil.getNodeStringValue(propertyNode, IS_MOBILE_SHOW);
            property.setIsMobileShow(isMobileShow);

            String jobField = NodeUtil.getNodeStringValue(propertyNode, JOB_FIELD);
            property.setJobField(jobField);

            property.setIndexType(NodeUtil.getNodeStringValue(propertyNode, INDEX_TYPE));
            property.setIndexTitleExpression(NodeUtil.getNodeStringValue(propertyNode, INDEX_TITLE_EXPS));
            property.setIndexContentExpression(NodeUtil.getNodeStringValue(propertyNode, INDEX_CONTENT_EXPS));

            // 岗位替代
            List<Element> bakUserNodes = selectElements(document, FLOW_BAK_USERS_XPATH);
            List<UserUnitElement> bakUsers = new ArrayList<>();
            for (Element node : bakUserNodes) {
                bakUsers.add(parseUserUnit(document, node));
            }
            property.setBakUsers(bakUsers);

            // 流程状态
            List<Element> flowStateNodes = selectElements(document, FLOW_STATE_XPATH);
            List<FlowStateElement> flowStates = new ArrayList<FlowStateElement>();
            for (Element node : flowStateNodes) {
                flowStates.add(parseFlowState(document, node));
            }
            property.setFlowStates(flowStates);

            // 消息模板
            List<Element> messageTemplateNodes = selectElements(document, FLOW_MESSAGE_TEMPLATES_XPATH);
            List<MessageTemplateElement> messageTemplates = new ArrayList<MessageTemplateElement>();
            for (Element node : messageTemplateNodes) {
                messageTemplates.add(parseMessageTemplate(document, node));
            }
            property.setMessageTemplates(messageTemplates);

            // 发起人
            List<Element> creatorNodes = selectElements(document, FLOW_CREATORS_XPATH);
            List<UserUnitElement> creators = new ArrayList<>();
            for (Element node : creatorNodes) {
                creators.add(parseUserUnit(document, node));
            }
            property.setCreators(creators);

            // 参与人
            List<Element> userNodes = selectElements(document, FLOW_USERS_XPATH);
            List<UserUnitElement> users = new ArrayList<>();
            for (Element node : userNodes) {
                users.add(parseUserUnit(document, node));
            }
            property.setUsers(users);

            // 督办人
            List<Element> monitorNodes = selectElements(document, FLOW_MONITORS_XPATH);
            List<UserUnitElement> monitors = new ArrayList<>();
            for (Element node : monitorNodes) {
                monitors.add(parseUserUnit(document, node));
            }
            property.setMonitors(monitors);

            /* lmw 2015-4-23 11:10 begin */
            // 意见
            List<Element> recordNodes = selectElements(document, FLOW_RECORDS_XPATH);
            List<RecordElement> records = new LinkedList<RecordElement>();
            for (Element node : recordNodes) {
                RecordElement r = parseRecord(document, node);
                if (r != null) {
                    records.add(r);
                }
            }
            property.setRecords(records);
            /* lmw 2015-4-23 11:10 end */

            // 监控者
            List<Element> adminNodes = selectElements(document, FLOW_ADMINS_XPATH);
            List<UserUnitElement> admins = new ArrayList<>();
            for (Element node : adminNodes) {
                admins.add(parseUserUnit(document, node));
            }
            property.setAdmins(admins);

            /* add by huanglinchuan 2014.10.28 begin */
            // 阅读者
            List<Element> viewerNodes = selectElements(document, FLOW_VIEWERS_XPATH);
            List<UserUnitElement> viewers = new ArrayList<>();
            for (Element node : viewerNodes) {
                viewers.add(parseUserUnit(document, node));
            }
            property.setViewers(viewers);
            /* add by huanglinchuan 2014.10.28 end */

            // 文件接收者
            List<Element> fileRecipientNodes = selectElements(document, FLOW_FILE_RECIPIENTS_XPATH);
            List<UnitElement> fileRecipients = new ArrayList<UnitElement>();
            for (Element node : fileRecipientNodes) {
                fileRecipients.add(parseUnit(document, node));
            }
            property.setFileRecipients(fileRecipients);

            // 消息接收者
            List<Element> msgRecipientNodes = selectElements(document, FLOW_MSG_RECIPIENTS_XPATH);
            List<UnitElement> msgRecipients = new ArrayList<UnitElement>();
            for (Element node : msgRecipientNodes) {
                msgRecipients.add(parseUnit(document, node));
            }
            property.setMsgRecipients(msgRecipients);
            // 自动更新标题
            String autoUpdateTitle = NodeUtil.getNodeStringValue(propertyNode, AUTO_UPDATE_TITLE);
            property.setAutoUpdateTitle(autoUpdateTitle);
            // 是否为自由流程
            String isFree = NodeUtil.getNodeStringValue(propertyNode, FLOW_IS_FREE);
            property.setIsFree(isFree);
            // 是否启用流程
            String isActive = NodeUtil.getNodeStringValue(propertyNode, FLOW_IS_ACTIVE);
            property.setIsActive(isActive);
            // 流程备注
            String remark = NodeUtil.getNodeStringValue(propertyNode, FLOW_REMARK);
            property.setRemark(remark);
            // 办结时保留督办、监控者的运行时权限
            String keepRuntimePermission = NodeUtil.getNodeStringValue(propertyNode, FLOW_KEEP_RUNTIME_PERMISSION);
            property.setKeepRuntimePermission(keepRuntimePermission);
            // 权限粒度
            String granularity = NodeUtil.getNodeStringValue(propertyNode, FLOW_IS_GRANULARITY);
            property.setGranularity(granularity);
            // 权限粒度重复包含时，只需要其中一个办理
            String isOnlyOneGranularity = NodeUtil.getNodeStringValue(propertyNode, FLOW_IS_ONLY_ONE_GRANULARITY);
            property.setIsOnlyOneGranularity(isOnlyOneGranularity);
            // 是否启用流程访问权限提供者
            String enableAccessPermissionProvider = NodeUtil.getNodeStringValue(propertyNode,
                    FLOW_ENABLE_ACCESS_PERMISSION_PROVIDER);
            property.setEnableAccessPermissionProvider(enableAccessPermissionProvider);
            // 流程访问权限提供者
            String accessPermissionProvider = NodeUtil.getNodeStringValue(propertyNode,
                    FLOW_ACCESS_PERMISSION_PROVIDER);
            property.setAccessPermissionProvider(accessPermissionProvider);
            // 只使用流程访问权限提供者
            String onlyUseAccessPermissionProvider = NodeUtil.getNodeStringValue(propertyNode,
                    FLOW_ONLY_USE_ACCESS_PERMISSION_PROVIDER);
            property.setOnlyUseAccessPermissionProvider(onlyUseAccessPermissionProvider);

            // 计时系统
            String dueTime = NodeUtil.getNodeStringValue(propertyNode, FLOW_DUE_TIME);
            property.setDueTime(dueTime);
            String timeUnit = NodeUtil.getNodeStringValue(propertyNode, FLOW_TIME_UNIT);
            property.setTimeUnit(timeUnit);
            // 计时开始流向
            List<Element> beginDirectionNodes = propertyNode.selectNodes(FLOW_BEGINDIRECTIONS_XPATH);
            List<UnitElement> beginDirections = new ArrayList<UnitElement>();
            for (Element node : beginDirectionNodes) {
                beginDirections.add(parseUnit(document, node));
            }
            property.setBeginDirections(beginDirections);
            // 计时结束流向
            List<Element> endDirectionNodes = propertyNode.selectNodes(FLOW_ENDDIRECTIONS_XPATH);
            List<UnitElement> endDirections = new ArrayList<UnitElement>();
            for (Element node : endDirectionNodes) {
                endDirections.add(parseUnit(document, node));
            }
            property.setEndDirections(endDirections);

            // 文件分发
            String isSendFile = NodeUtil.getNodeStringValue(propertyNode, FLOW_IS_SEND_FILE);
            property.setIsSendFile(isSendFile);
            // 消息分发
            String isSendMsg = NodeUtil.getNodeStringValue(propertyNode, FLOW_IS_SENDMSG);
            property.setIsSendMsg(isSendMsg);
            // 文件模板
            String fileTemplate = NodeUtil.getNodeStringValue(propertyNode, FLOW_FILE_TEMPLATE);
            property.setFileTemplate(fileTemplate);
            // 打印模板
            String printTemplate = NodeUtil.getNodeStringValue(propertyNode, FLOW_PRINT_TEMPLATE);
            property.setPrintTemplate(printTemplate);
            // 打印模板ID
            String printTemplateId = NodeUtil.getNodeStringValue(propertyNode, FLOW_PRINT_TEMPLATE_ID);
            property.setPrintTemplateId(printTemplateId);
            // 打印模板UUID
            String printTemplateUuid = NodeUtil.getNodeStringValue(propertyNode, FLOW_PRINT_TEMPLATE_UUID);
            property.setPrintTemplateUuid(printTemplateUuid);
            // 流程事件监听器
            String listener = NodeUtil.getNodeStringValue(propertyNode, FLOW_LISTENER);
            property.setListener(listener);
            // 全局环节事件监听器
            String globalTaskListener = NodeUtil.getNodeStringValue(propertyNode, GLOBAL_TASK_FLOW_LISTENER);
            globalTaskListener = globalTaskListener == null ? "" : globalTaskListener;
            property.setGlobalTaskListener(globalTaskListener);
            // 计时监听器
            String timerListener = NodeUtil.getNodeStringValue(propertyNode, TIMER_LISTENER);
            timerListener = timerListener == null ? "" : timerListener;
            property.setTimerListener(timerListener);
            // 加载的JS模块
            String customJsModule = NodeUtil.getNodeStringValue(propertyNode, FLOW_CUSTOM_JS_MODULE);
            property.setCustomJsModule(customJsModule);
            // 事件脚本
            List<Element> eventScriptsNodes = propertyNode.selectNodes(FLOW_EVENT_SCRIPT_XPATH);
            List<ScriptElement> eventScripts = new ArrayList<ScriptElement>();
            for (Element node : eventScriptsNodes) {
                eventScripts.add(parseEventScript(document, node));
            }
            property.setEventScripts(eventScripts);

            // 签署意见校验设置
            List<OpinionCheckSetElement> opinionCheckSets = new ArrayList<>();
            List<Element> opinionCheckSetsNodes = propertyNode.selectNodes(Flow_OPINION_CHECK_SETS);
            for (Element node : opinionCheckSetsNodes) {
                List<Element> sonNodes = node.selectNodes(Flow_OPINION_CHECK_SET);
                for (Element sonNode : sonNodes) {
                    opinionCheckSets.add(parseOpinionCheckSet(sonNode));
                }
            }
            property.setOpinionCheckSets(opinionCheckSets);
        }
        // 设置流程属性
        flow.setProperty(property);
    }

    /**
     * 解析签署意见校验设置
     *
     * @param sonNode
     * @return com.wellsoft.pt.bpm.engine.element.OpinionCheckSetElement
     **/
    private static OpinionCheckSetElement parseOpinionCheckSet(Element sonNode) {
        String id = sonNode.attributeValue("id");
        String opinionRuleUuid = sonNode.attributeValue("opinionRuleUuid");
        String taskIds = sonNode.attributeValue("taskIds");
        OpinionCheckSetElement opinionCheckSetElement = new OpinionCheckSetElement();
        opinionCheckSetElement.setId(id);
        opinionCheckSetElement.setOpinionRuleUuid(opinionRuleUuid);
        opinionCheckSetElement.setTaskIds(taskIds);
        return opinionCheckSetElement;
    }

    /**
     * @param document
     * @param node
     * @return
     */
    private static FlowStateElement parseFlowState(Document document, Element node) {
        FlowStateElement flowState = new FlowStateElement();
        flowState.setCode(NodeUtil.getNodeStringValue(node, FLOW_STATE_CODE));
        flowState.setName(NodeUtil.getNodeStringValue(node, FLOW_STATE_NAME));
        return flowState;
    }

    /**
     * @param document
     * @param node
     * @return
     */
    private static MessageTemplateElement parseMessageTemplate(Document document, Element node) {
        MessageTemplateElement template = new MessageTemplateElement();
        template.setType(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_TYPE));
        template.setTypeName(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_TYPE_NAME));
        template.setId(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_ID));
        template.setName(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_NAME));
        template.setCondition(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_CONDITION));
        /* modified by huanglinchuan 2014.10.21 begin */
        template.setIsSendMsg(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_IS_SEND_MSG));
        template.setExtraMsgRecipients(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_RECIPIENTS));
        template.setExtraMsgRecipientUserIds(
                NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_RECIPIENT_USER_IDS));
        template.setExtraMsgCustomRecipients(
                NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_CUSTOM_RECIPIENTS));
        template.setExtraMsgCustomRecipientUserIds(
                NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_EXTRA_MSG_CUSTOM_RECIPIENT_USER_IDS));
        /* modified by huanglinchuan 2014.10.21 end */

        // 抄送人员
        List<Element> copyMsgRecipientElements = node.selectNodes(FLOW_MESSAGE_TEMPLATES_COPYMSGRECIPIENTS_XPATH);
        template.setCopyMsgRecipients(Lists.<UserUnitElement>newArrayList());
        for (Element n : copyMsgRecipientElements) {
            template.getCopyMsgRecipients().add(parseUserUnit(document, n));
        }

        template.setConditionEnable(NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_CONDITION_ENABLE));
        template.setCondExpressionSignal(
                NodeUtil.getNodeStringValue(node, FLOW_MESSAGE_TEMPLATE_CON_EXPRESSION_SIGNAL));
        // 分发人员设置
        List<Element> distributers = node.selectNodes(FLOW_MESSAGE_TEMPLATES_DISTRIBUTERS_XPATH);
        template.setDistributerElements(Lists.<MessageDistributerElement>newArrayList());
        for (Element element : distributers) {
            MessageDistributerElement distributerElement = new MessageDistributerElement();
            distributerElement
                    .setDistributerType(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_DISTRIBUION_TYPE));
            distributerElement.setDistributerTypeName(
                    NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_DISTRIBUION_TYPE_NAME));
            distributerElement.setId(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_ID));
            distributerElement.setName(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_NAME));
            distributerElement.setDesignee(Lists.<UserUnitElement>newArrayList());
            List<Element> unitElements = element.selectNodes(FLOW_MESSAGE_TEMPLATES_DESIGNEE_UNIT_XPATH);
            for (Element n : unitElements) {
                distributerElement.getDesignee().add(parseUserUnit(document, n));
            }
            template.getDistributerElements().add(distributerElement);
        }

        // 分发节点
        List<Element> distributions = node.selectNodes(FLOW_MESSAGE_TEMPLATES_DISTRIBUTIONS_XPATH);
        template.setDistributionElements(Lists.<MessageDistributionElement>newArrayList());
        for (Element element : distributions) {
            MessageDistributionElement distributionElement = new MessageDistributionElement();
            distributionElement.setType(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_DISTRIBUION_TYPE));
            distributionElement.setValue(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_DISTRIBUION_VALUE));
            template.getDistributionElements().add(distributionElement);
        }

        // 分发条件
        List<Element> conditions = node.selectNodes(FLOW_MESSAGE_TEMPLATES_CONDITIONS_XPATH);
        template.setConditionElements(Lists.<ConditionElement>newArrayList());
        for (Element element : conditions) {
            ConditionElement conditionElement = new ConditionElement();
            conditionElement.setCode(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_CONDITION_CODE));
            conditionElement.setValue(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_CONDITION_VALUE));
            conditionElement.setSymbols(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_CONDITION_SYMBOLS));
            conditionElement.setType(NodeUtil.getNodeStringValue(element, FLOW_MESSAGE_TEMPLATE_CONDITION_TYPE));
            template.getConditionElements().add(conditionElement);
        }

        return template;
    }

    /**
     * 解析定时器
     *
     * @param document
     * @param flow
     */
    private static void parseTimer(Document document, FlowElement flow) {
        List<TimerElement> timers = new ArrayList<TimerElement>();
        List<Element> timerNodes = selectElements(document, FLOW_TIMERS_XPATH);
        for (Element timerNode : timerNodes) {
            TimerElement timer = new TimerElement();
            // name
            String name = NodeUtil.getNodeStringValue(timerNode, TIMER_NAME);
            timer.setName(name);

            // timerId
            String timerId = NodeUtil.getNodeStringValue(timerNode, TIMER_ID);
            timer.setTimerId(timerId);

            // timerConfigUuid
            String timerConfigUuid = NodeUtil.getNodeStringValue(timerNode, TIMER_CONFIG_UUID);
            timer.setTimerConfigUuid(timerConfigUuid);

            // introductionType
            String introductionType = NodeUtil.getNodeStringValue(timerNode, TIMER_INTRODUCTION_TYPE);
            timer.setIntroductionType(introductionType);

            // includeStartTimePoint
            String includeStartTimePoint = NodeUtil.getNodeStringValue(timerNode, TIMER_INCLUDE_START_TIME_POINT);
            timer.setIncludeStartTimePoint(includeStartTimePoint);

            // autoDelay
            String autoDelay = NodeUtil.getNodeStringValue(timerNode, TIMER_AUTO_DELAY);
            timer.setAutoDelay(autoDelay);

            // timeLimitUnit
            String timeLimitUnit = NodeUtil.getNodeStringValue(timerNode, TIMER_TIME_LIMIT_UNIT);
            timer.setTimeLimitUnit(timeLimitUnit);

            // workTimePlanUuid
            String workTimePlanUuid = NodeUtil.getNodeStringValue(timerNode, TIMER_WORK_TIME_PLAN_UUID);
            timer.setWorkTimePlanUuid(workTimePlanUuid);

            // workTimePlanId
            String workTimePlanId = NodeUtil.getNodeStringValue(timerNode, TIMER_WORK_TIME_PLAN_ID);
            timer.setWorkTimePlanId(workTimePlanId);

            // workTimePlanName
            String workTimePlanName = NodeUtil.getNodeStringValue(timerNode, TIMER_WORK_TIME_PLAN_NAME);
            timer.setWorkTimePlanName(workTimePlanName);

            // limitTimeType
            String limitTimeType = NodeUtil.getNodeStringValue(timerNode, TIMER_LIMIT_TYPE);
            timer.setLimitTimeType(limitTimeType);

            // limitTime1
            String limitTime1 = NodeUtil.getNodeStringValue(timerNode, TIMER_LIMIT_1);
            timer.setLimitTime1(limitTime1);

            // limitTime
            String limitTime = NodeUtil.getNodeStringValue(timerNode, TIMER_LIMIT);
            timer.setLimitTime(limitTime);

            // autoUpdateLimitTime
            String autoUpdateLimitTime = NodeUtil.getNodeStringValue(timerNode, TIMER_AUTO_UPDATE_LIMIT_TIME);
            timer.setAutoUpdateLimitTime(autoUpdateLimitTime);

            // ignoreEmptyLimitTime
            String ignoreEmptyLimitTime = NodeUtil.getNodeStringValue(timerNode, TIMER_IGNORE_EMPTY_LIMIT_TIME);
            timer.setIgnoreEmptyLimitTime(ignoreEmptyLimitTime);

            // limitUnit
            String limitUnit = NodeUtil.getNodeStringValue(timerNode, TIMER_UNIT);
            timer.setLimitUnit(limitUnit);

            // dutys
            List<Element> dutyNodes = timerNode.selectNodes(TIMER_DUTYS_XPATH);
            List<UnitElement> dutys = new ArrayList<UnitElement>();
            for (Element node : dutyNodes) {
                dutys.add(parseUnit(document, node));
            }
            timer.setDutys(dutys);

            // tasks
            List<Element> taskNodes = timerNode.selectNodes(TIMER_TASKS_XPATH);
            List<UnitElement> tasks = new ArrayList<UnitElement>();
            for (Element node : taskNodes) {
                tasks.add(parseUnit(document, node));
            }
            timer.setTasks(tasks);

            // subTasks
            List<Element> subTaskNodes = timerNode.selectNodes(TIMER_SUBTASKS_XPATH);
            List<SubTaskTimerElement> subTasks = Lists.newArrayListWithCapacity(0);
            for (Element node : subTaskNodes) {
                subTasks.add(parseSubTaskTimer(document, node));
            }
            timer.setSubTasks(subTasks);

            // affectMainFlow
            String affectMainFlow = NodeUtil.getNodeStringValue(timerNode, TIMER_AFFECT_MAIN_FLOW);
            timer.setAffectMainFlow(affectMainFlow);

            // timeEndType
            String timeEndType = NodeUtil.getNodeStringValue(timerNode, TIMER_TIME_END_TYPE);
            timer.setTimeEndType(timeEndType);

            // overDirections
            String overDirections = NodeUtil.getNodeStringValue(timerNode, TIMER_OVER_DIRECTIONS);
            timer.setOverDirections(overDirections);

            // enableAlarm
            String enableAlarm = NodeUtil.getNodeStringValue(timerNode, TIMER_ENABLE_ALARM);
            timer.setEnableAlarm(enableAlarm);

            // enableDueDoing
            String enableDueDoing = NodeUtil.getNodeStringValue(timerNode, TIMER_ENABLE_DUE_DOING);
            timer.setEnableDueDoing(enableDueDoing);

            // alarmTime
            String alarmTime = NodeUtil.getNodeStringValue(timerNode, TIMER_ALARM_TIME);
            timer.setAlarmTime(alarmTime);

            // alarmUnit
            String alarmUnit = NodeUtil.getNodeStringValue(timerNode, TIMER_ALARM_UNIT);
            timer.setAlarmUnit(alarmUnit);

            // alarmFrequency
            String alarmFrequency = NodeUtil.getNodeStringValue(timerNode, TIMER_ALARM_FREQUENCY);
            timer.setAlarmFrequency(alarmFrequency);

            // alarmObjects
            List<Element> alarmObjectNodes = timerNode.selectNodes(TIMER_ALARM_OBJECTS_XPATH);
            List<UserUnitElement> alarmObjects = new ArrayList<>();
            for (Element node : alarmObjectNodes) {
                alarmObjects.add(parseUserUnit(document, node));
            }
            timer.setAlarmObjects(alarmObjects);

            // alarmUsers
            List<Element> alarmUserNodes = timerNode.selectNodes(TIMER_ALARM_USERS_XPATH);
            List<UserUnitElement> alarmUsers = new ArrayList<>();
            for (Element node : alarmUserNodes) {
                alarmUsers.add(parseUserUnit(document, node));
            }
            timer.setAlarmUsers(alarmUsers);

            // alarmFlow
            Element alarmFlowNode = (Element) timerNode.selectSingleNode(TIMER_ALARM_FLOW_XPATH);
            if (alarmFlowNode != null) {
                AlarmFlowElement alarmFlow = new AlarmFlowElement();
                alarmFlow.setName(NodeUtil.getNodeStringValue(alarmFlowNode, TIMER_ALARM_FLOWNAME));
                alarmFlow.setId(NodeUtil.getNodeStringValue(alarmFlowNode, TIMER_ALARM_FLOWID));
                timer.setAlarmFlow(alarmFlow);
            }

            // alarmFlowDoings
            List<Element> alarmFlowDoingNodes = timerNode.selectNodes(TIMER_ALARM_FLOWDOINGS_XPATH);
            List<UserUnitElement> alarmFlowDoings = new ArrayList<>();
            for (Element node : alarmFlowDoingNodes) {
                alarmFlowDoings.add(parseUserUnit(document, node));
            }
            timer.setAlarmFlowDoings(alarmFlowDoings);

            // alarmFlowDoingUsers
            List<Element> alarmFlowDoingUserNodes = timerNode.selectNodes(TIMER_ALARM_FLOWDOING_USERS_XPATH);
            List<UserUnitElement> alarmFlowDoingUsers = new ArrayList<>();
            for (Element node : alarmFlowDoingUserNodes) {
                alarmFlowDoingUsers.add(parseUserUnit(document, node));
            }
            timer.setAlarmFlowDoingUsers(alarmFlowDoingUsers);

            // alarm
            List<Element> alarmNodes = timerNode.selectNodes(TIMER_ALARMS_XPATH);
            if (CollectionUtils.isNotEmpty(alarmNodes)) {
                timer.setAlarmElements(Lists.<AlarmElement>newArrayList());
                for (Element node : alarmNodes) {
                    timer.getAlarmElements().add(parseAlarmElement(document, node));
                }
            }

            // dueTime
            String dueTime = NodeUtil.getNodeStringValue(timerNode, TIMER_DUE_TIME);
            timer.setDueTime(dueTime);

            // dueUnit
            String dueUnit = NodeUtil.getNodeStringValue(timerNode, TIMER_DUE_UNIT);
            timer.setDueUnit(dueUnit);

            // dueFrequency
            String dueFrequency = NodeUtil.getNodeStringValue(timerNode, TIMER_DUE_FREQUENCY);
            timer.setDueFrequency(dueFrequency);

            // dueObjects
            List<Element> dueObjectNodes = timerNode.selectNodes(TIMER_DUE_OBJECTS_XPATH);
            List<UserUnitElement> dueObjects = new ArrayList<>();
            for (Element node : dueObjectNodes) {
                dueObjects.add(parseUserUnit(document, node));
            }
            timer.setDueObjects(dueObjects);

            // dueUsers
            List<Element> dueUserNodes = timerNode.selectNodes(TIMER_DUE_USERS_XPATH);
            List<UserUnitElement> dueUsers = new ArrayList<>();
            for (Element node : dueUserNodes) {
                dueUsers.add(parseUserUnit(document, node));
            }
            timer.setDueUsers(dueUsers);

            // dueAction
            String dueAction = NodeUtil.getNodeStringValue(timerNode, TIMER_DUE_ACTION);
            timer.setDueAction(dueAction);

            // dueToUsers
            List<Element> dueToUserNodes = timerNode.selectNodes(TIMER_DUE_TOUSERS_XPATH);
            List<UserUnitElement> dueToUsers = new ArrayList<>();
            for (Element node : dueToUserNodes) {
                dueToUsers.add(parseUserUnit(document, node));
            }
            timer.setDueToUsers(dueToUsers);

            // dueToTask
            String dueToTask = NodeUtil.getNodeStringValue(timerNode, TIMER_DUE_TOTASK);
            timer.setDueToTask(dueToTask);

            // dueFlow
            Element dueFlowNode = (Element) timerNode.selectSingleNode(TIMER_DUE_FLOW_XPATH);
            if (dueFlowNode != null) {
                DueFlowElement dueFlow = new DueFlowElement();
                dueFlow.setName(NodeUtil.getNodeStringValue(dueFlowNode, TIMER_DUE_FLOWNAME));
                dueFlow.setId(NodeUtil.getNodeStringValue(dueFlowNode, TIMER_DUE_FLOWID));
                timer.setDueFlow(dueFlow);
            }

            // dueFlowDoings
            List<Element> dueFlowDoingNodes = timerNode.selectNodes(TIMER_DUE_FLOWDOINGS_XPATH);
            List<UserUnitElement> dueFlowDoings = new ArrayList<>();
            for (Element node : dueFlowDoingNodes) {
                dueFlowDoings.add(parseUserUnit(document, node));
            }
            timer.setDueFlowDoings(dueFlowDoings);

            // dueFlowDoingUsers
            List<Element> dueFlowDoingUserNodes = timerNode.selectNodes(TIMER_DUE_FLOWDOINGUSERS_XPATH);
            List<UserUnitElement> dueFlowDoingUsers = new ArrayList<>();
            for (Element node : dueFlowDoingUserNodes) {
                dueFlowDoingUsers.add(parseUserUnit(document, node));
            }
            timer.setDueFlowDoingUsers(dueFlowDoingUsers);

            timers.add(timer);
        }

        flow.setTimers(timers);
    }

    /**
     * @param document
     * @param subTaskTimerNode
     * @return
     */
    private static SubTaskTimerElement parseSubTaskTimer(Document document, Element subTaskTimerNode) {
        SubTaskTimerElement subTaskTimerElement = new SubTaskTimerElement();
        // taskId
        String taskId = NodeUtil.getNodeStringValue(subTaskTimerNode, TIMER_SUBTASK_TASK_ID);
        subTaskTimerElement.setTaskId(taskId);
        // timingMode
        String timingMode = NodeUtil.getNodeStringValue(subTaskTimerNode, TIMER_SUBTASK_TIMING_MODE);
        subTaskTimerElement.setTimingMode(timingMode);
        // timers
        List<Element> timerNodes = subTaskTimerNode.selectNodes(TIMER_SUBTASK_TIMERS_XPATH);
        List<NewFlowTimerElement> timers = Lists.newArrayListWithCapacity(0);
        for (Element node : timerNodes) {
            timers.add(parseNewFlowTimer(document, node));
        }
        subTaskTimerElement.setTimers(timers);
        return subTaskTimerElement;
    }

    /**
     * @param document
     * @param newFlowTimerNode
     * @return
     */
    private static NewFlowTimerElement parseNewFlowTimer(Document document, Element newFlowTimerNode) {
        NewFlowTimerElement newFlowTimerElement = new NewFlowTimerElement();
        // newFlowId
        String newFlowId = NodeUtil.getNodeStringValue(newFlowTimerNode, TIMER_SUBTASK_TIMERS_NEW_FLOW_ID);
        newFlowTimerElement.setNewFlowId(newFlowId);
        // newFlowTimerName
        String newFlowTimerName = NodeUtil.getNodeStringValue(newFlowTimerNode,
                TIMER_SUBTASK_TIMERS_NEW_FLOW_TIMER_NAME);
        newFlowTimerElement.setNewFlowTimerName(newFlowTimerName);
        // newFlowTimerId
        String newFlowTimerId = NodeUtil.getNodeStringValue(newFlowTimerNode, TIMER_SUBTASK_TIMERS_NEW_FLOW_TIMER_ID);
        newFlowTimerElement.setNewFlowTimerId(newFlowTimerId);
        return newFlowTimerElement;
    }

    private static AlarmElement parseAlarmElement(Document document, Element timerNode) {
        AlarmElement timer = new AlarmElement();
        // alarmTime
        String alarmTime = NodeUtil.getNodeStringValue(timerNode, TIMER_ALARM_TIME);
        timer.setAlarmTime(alarmTime);

        // alarmUnit
        String alarmUnit = NodeUtil.getNodeStringValue(timerNode, TIMER_ALARM_UNIT);
        timer.setAlarmUnit(alarmUnit);

        // alarmFrequency
        String alarmFrequency = NodeUtil.getNodeStringValue(timerNode, TIMER_ALARM_FREQUENCY);
        timer.setAlarmFrequency(alarmFrequency);

        // alarmObjects
        List<Element> alarmObjectNodes = timerNode.selectNodes(TIMER_ALARM_OBJECTS_XPATH);
        List<UserUnitElement> alarmObjects = new ArrayList<>();
        for (Element node : alarmObjectNodes) {
            alarmObjects.add(parseUserUnit(document, node));
        }
        timer.setAlarmObjects(alarmObjects);

        // alarmUsers
        List<Element> alarmUserNodes = timerNode.selectNodes(TIMER_ALARM_USERS_XPATH);
        List<UserUnitElement> alarmUsers = new ArrayList<>();
        for (Element n : alarmUserNodes) {
            alarmUsers.add(parseUserUnit(document, n));
        }
        timer.setAlarmUsers(alarmUsers);

        // alarmFlow
        Element alarmFlowNode = (Element) timerNode.selectSingleNode(TIMER_ALARM_FLOW_XPATH);
        if (alarmFlowNode != null) {
            AlarmFlowElement alarmFlow = new AlarmFlowElement();
            alarmFlow.setName(NodeUtil.getNodeStringValue(alarmFlowNode, TIMER_ALARM_FLOWNAME));
            alarmFlow.setId(NodeUtil.getNodeStringValue(alarmFlowNode, TIMER_ALARM_FLOWID));
            timer.setAlarmFlow(alarmFlow);
        }

        // alarmFlowDoings
        List<Element> alarmFlowDoingNodes = timerNode.selectNodes(TIMER_ALARM_FLOWDOINGS_XPATH);
        List<UserUnitElement> alarmFlowDoings = new ArrayList<>();
        for (Element node : alarmFlowDoingNodes) {
            alarmFlowDoings.add(parseUserUnit(document, node));
        }
        timer.setAlarmFlowDoings(alarmFlowDoings);

        // alarmFlowDoingUsers
        List<Element> alarmFlowDoingUserNodes = timerNode.selectNodes(TIMER_ALARM_FLOWDOING_USERS_XPATH);
        List<UserUnitElement> alarmFlowDoingUsers = new ArrayList<>();
        for (Element node : alarmFlowDoingUserNodes) {
            alarmFlowDoingUsers.add(parseUserUnit(document, node));
        }
        timer.setAlarmFlowDoingUsers(alarmFlowDoingUsers);

        return timer;
    }

    /**
     * 解析任务
     *
     * @param document
     * @param flow
     */
    private static void parseTask(Document document, FlowElement flow) {
        List<TaskElement> tasks = new ArrayList<TaskElement>();
        List<Element> taskNodes = selectElements(document, TASK_XPATH);
        for (Element taskNode : taskNodes) {
            // 解析子流程任务
            if ("2".equals(NodeUtil.getNodeAttrValue(taskNode, TASK_TYPE))) {
                SubTaskElement subTask = parseSubTask(document, taskNode);
                tasks.add(subTask);
                continue;
            }
            // 流程任务
            TaskElement task = new TaskElement();
            // 任务属性
            String name = NodeUtil.getNodeAttrValue(taskNode, TASK_NAME);
            task.setName(name);
            String id = NodeUtil.getNodeAttrValue(taskNode, TASK_ID);
            task.setId(id);
            String type = NodeUtil.getNodeAttrValue(taskNode, TASK_TYPE);
            task.setType(type);
            String code = NodeUtil.getNodeAttrValue(taskNode, TASK_CODE);
            task.setCode(code);
            // 是否可编辑表单
            String canEditForm = NodeUtil.getNodeStringValue(taskNode, TASK_CAN_EDIT_FORM);
            task.setCanEditForm(canEditForm);
            // X坐标
            String x = NodeUtil.getNodeStringValue(taskNode, TASK_X);
            task.setX(x);
            // Y坐标
            String y = NodeUtil.getNodeStringValue(taskNode, TASK_Y);
            task.setY(y);
            // conditionName
            String conditionName = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_NAME);
            task.setConditionName(conditionName);
            // conditionBody
            String conditionBody = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_BODY);
            task.setConditionBody(conditionBody);
            // conditionX
            String conditionX = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_X);
            task.setConditionX(conditionX);
            // conditionY
            String conditionY = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_Y);
            task.setConditionY(conditionY);
            // conditionLine
            String conditionLine = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_LINE);
            task.setConditionLine(conditionLine);

            /* lmw 2015-4-22 begin */
            // formID
            String formID = NodeUtil.getNodeStringValue(taskNode, TASK_FORMID);
            task.setFormID(formID);
            /* lmw 2015-4-22 end */

            // isSetUser
            String isSetUser = NodeUtil.getNodeStringValue(taskNode, TASK_IS_SET_USER);
            task.setIsSetUser(isSetUser);
            // isSetCopyUser
            String isSetCopyUser = NodeUtil.getNodeStringValue(taskNode, TASK_IS_SET_COPY_USER);
            task.setIsSetCopyUser(isSetCopyUser);

            String isSetTransferUser = NodeUtil.getNodeStringValue(taskNode, TASK_IS_SET_TRANSFER_USER);
            task.setIsSetTransferUser(isSetTransferUser);
            // isConfirmCopyUser
            String isConfirmCopyUser = NodeUtil.getNodeStringValue(taskNode, TASK_IS_CONFIRM_COPY_USER);
            task.setIsConfirmCopyUser(isConfirmCopyUser);
            // copyUserCondition
            String copyUserCondition = NodeUtil.getNodeStringValue(taskNode, TASK_COPY_USER_CONDITION);
            task.setCopyUserCondition(copyUserCondition);
            // isSetUserEmpty
            String isSetUserEmpty = NodeUtil.getNodeStringValue(taskNode, TASK_IS_SET_USER_EMPTY);
            task.setIsSetUserEmpty(isSetUserEmpty);
            // emptyToTask
            String emptyToTask = NodeUtil.getNodeStringValue(taskNode, TASK_EMPTY_TOTASK);
            task.setEmptyToTask(emptyToTask);
            // emptyNoteDone
            String emptyNoteDone = NodeUtil.getNodeStringValue(taskNode, TASK_EMPTY_NOTE_DONE);
            task.setEmptyNoteDone(emptyNoteDone);
            // isSelectAgain
            String isSelectAgain = NodeUtil.getNodeStringValue(taskNode, TASK_IS_SELECT_AGAIN);
            task.setIsSelectAgain(isSelectAgain);
            // isOnlyOne
            String isOnlyOne = NodeUtil.getNodeStringValue(taskNode, TASK_IS_ONLYONE);
            task.setIsOnlyOne(isOnlyOne);
            // isAnyone
            String isAnyone = NodeUtil.getNodeStringValue(taskNode, TASK_IS_ANYONE);
            task.setIsAnyone(isAnyone);
            // isByOrder
            String isByOrder = NodeUtil.getNodeStringValue(taskNode, TASK_IS_BYORDER);
            task.setIsByOrder(isByOrder);
            // sameUserSubmit
            String sameUserSubmit = NodeUtil.getNodeStringValue(taskNode, TASK_SAME_USER_SUBMIT);
            task.setSameUserSubmit(sameUserSubmit);
            // isSetMonitor
            String isSetMonitor = NodeUtil.getNodeStringValue(taskNode, TASK_IS_SET_MONITOR);
            task.setIsSetMonitor(isSetMonitor);
            // isAllowApp
            String isAllowApp = NodeUtil.getNodeStringValue(taskNode, TASK_IS_ALLOW_APP);
            task.setIsAllowApp(isAllowApp);
            // isInheritMonitor
            String isInheritMonitor = NodeUtil.getNodeStringValue(taskNode, TASK_IS_INHERIT_MONITOR);
            task.setIsInheritMonitor(isInheritMonitor);
            // granularity
            String granularity = NodeUtil.getNodeStringValue(taskNode, TASK_GRANULARITY);
            task.setGranularity(granularity);
            // untreadType
            String untreadType = NodeUtil.getNodeStringValue(taskNode, TASK_UNTREAD_TYPE);
            task.setUntreadType(untreadType);
            // snName
            String snName = NodeUtil.getNodeStringValue(taskNode, TASK_SN_NAME);
            task.setSnName(snName);
            // serialNo
            String serialNo = NodeUtil.getNodeStringValue(taskNode, TASK_SERIAL_NO);
            task.setSerialNo(serialNo);
            // printTemplate
            String printTemplate = NodeUtil.getNodeStringValue(taskNode, TASK_PRINT_TEMPLATE);
            task.setPrintTemplate(printTemplate);
            // printTemplateId
            String printTemplateId = NodeUtil.getNodeStringValue(taskNode, TASK_PRINT_TEMPLATE_ID);
            task.setPrintTemplateId(printTemplateId);
            // printTemplateUuid
            String printTemplateUuid = NodeUtil.getNodeStringValue(taskNode, TASK_PRINT_TEMPLATE_UUID);
            task.setPrintTemplateUuid(printTemplateUuid);
            // listener
            String listener = NodeUtil.getNodeStringValue(taskNode, TASK_LISTENER);
            task.setListener(listener);
            // customJsModule
            String customJsModule = NodeUtil.getNodeStringValue(taskNode, TASK_CUSTOM_JS_MODULE);
            task.setCustomJsModule(customJsModule);

            // untreadTasks
            List<Element> untreadTaskNodes = selectElements(document, TASK_UNTREAD_TASKS_XPATH);
            List<UnitElement> untreadTasks = new ArrayList<UnitElement>();
            for (Element node : untreadTaskNodes) {
                untreadTasks.add(parseUnit(document, node));
            }
            task.setUntreadTasks(untreadTasks);

            // readFields
            List<Element> readFieldNodes = taskNode.selectNodes(TASK_READ_FIELDS_XPATH);
            List<UnitElement> readFields = new ArrayList<UnitElement>();
            for (Element node : readFieldNodes) {
                readFields.add(parseUnit(document, node));
            }
            task.setReadFields(readFields);

            // editFields
            List<Element> editFieldNodes = taskNode.selectNodes(TASK_EDIT_FIELDS_XPATH);
            List<UnitElement> editFields = new ArrayList<UnitElement>();
            for (Element node : editFieldNodes) {
                editFields.add(parseUnit(document, node));
            }
            task.setEditFields(editFields);

            // fileRightsFields
            List<Element> fileRightsNodes = taskNode.selectNodes(TASK_FILE_RIGHTS_FIELDS_XPATH);
            List<UnitElement> fileRightsFields = new ArrayList<UnitElement>();
            for (Element node : fileRightsNodes) {
                fileRightsFields.add(parseUnit(document, node));
            }
            task.setFileRights(fileRightsFields);

            // allFormFields
            List<Element> allFormFieldNodes = taskNode.selectNodes(ALL_FORM_FIELDS_XPATH);
            List<UnitElement> allFormFields = new ArrayList<UnitElement>();
            for (Element node : allFormFieldNodes) {
                allFormFields.add(parseUnit(document, node));
            }
            task.setAllFormFields(allFormFields);

            // allFormFieldWidgetId
            List<Element> allFormFieldWidgetIdNodes = taskNode.selectNodes(TASK_ALL_FORM_FIELD_WIDGET_IDS_XPATH);
            List<UnitElement> allFormFieldWidgetId = new ArrayList<UnitElement>();
            for (Element node : allFormFieldWidgetIdNodes) {
                allFormFieldWidgetId.add(parseUnit(document, node));
            }
            task.setAllFormFieldWidgetIds(allFormFieldWidgetId);

            // formBtnRightSetting
            List<Element> formBtnRightSettingNodes = taskNode.selectNodes(TASK_FORM_BTN_RIGHT_SETTINGS_XPATH);
            List<UnitElement> formBtnRightSettings = new ArrayList<UnitElement>();
            for (Element node : formBtnRightSettingNodes) {
                formBtnRightSettings.add(parseUnit(document, node));
            }
            task.setFormBtnRightSettings(formBtnRightSettings);

            // hideFields
            List<Element> hideFieldNodes = taskNode.selectNodes(TASK_HIDE_FIELDS_XPATH);
            List<UnitElement> hideFields = new ArrayList<UnitElement>();
            for (Element node : hideFieldNodes) {
                hideFields.add(parseUnit(document, node));
            }
            task.setHideFields(hideFields);

            // notNullFields
            List<Element> notNullFieldNodes = taskNode.selectNodes(TASK_NOTNULL_FIELDS_XPATH);
            List<UnitElement> notNullFields = new ArrayList<UnitElement>();
            for (Element node : notNullFieldNodes) {
                notNullFields.add(parseUnit(document, node));
            }
            task.setNotNullFields(notNullFields);

            // add by wujx 20160728
            // fieldProperty
            Element fieldPropertyNode = (Element) taskNode.selectSingleNode(TASK_FIELD_PROPERTY_XPATH);
            task.setFieldProperty(parseFieldProperty(document, fieldPropertyNode));

            // hideBlocks
            List<Element> hideBlockNodes = taskNode.selectNodes(TASK_HIDE_BLOCKS_XPATH);
            List<UnitElement> hideBlocks = new ArrayList<UnitElement>();
            for (Element node : hideBlockNodes) {
                hideBlocks.add(parseUnit(document, node));
            }
            task.setHideBlocks(hideBlocks);
            // hideTabs
            List<Element> hideTabsNodes = taskNode.selectNodes(TASK_HIDE_TABS_XPATH);
            List<UnitElement> hideTabs = new ArrayList<UnitElement>();
            for (Element node : hideTabsNodes) {
                hideTabs.add(parseUnit(document, node));
            }
            task.setHideTabs(hideTabs);

            // start rights
            List<Element> startRightNodes = taskNode.selectNodes(TASK_START_RIGHTS_XPATH);
            List<RightUnitElement> startRights = new ArrayList<>();
            for (Element node : startRightNodes) {
                startRights.add(parseRightUnit(document, node));
            }
            task.setStartRights(startRights);
            task.setStartRightConfig(new RightConfigElement());

            // rights
            List<Element> rightNodes = taskNode.selectNodes(TASK_RIGHTS_XPATH);
            List<RightUnitElement> rights = new ArrayList<>();
            for (Element node : rightNodes) {
                rights.add(parseRightUnit(document, node));
            }
            task.setRights(rights);
            task.setTodoRightConfig(new RightConfigElement());

            // done rights
            List<Element> doneRightNodes = taskNode.selectNodes(TASK_DONE_RIGHTS_XPATH);
            List<RightUnitElement> doneRights = new ArrayList<>();
            for (Element node : doneRightNodes) {
                doneRights.add(parseRightUnit(document, node));
            }
            task.setDoneRights(doneRights);
            task.setDoneRightConfig(new RightConfigElement());

            // monitor rights
            List<Element> monitorRightNodes = taskNode.selectNodes(TASK_MONITOR_RIGHTS_XPATH);
            List<RightUnitElement> monitorRights = new ArrayList<>();
            for (Element node : monitorRightNodes) {
                monitorRights.add(parseRightUnit(document, node));
            }
            task.setMonitorRights(monitorRights);
            task.setMonitorRightConfig(new RightConfigElement());

            // admin rights
            List<Element> adminRightNodes = taskNode.selectNodes(TASK_ADMIN_RIGHTS_XPATH);
            List<RightUnitElement> adminRights = new ArrayList<>();
            for (Element node : adminRightNodes) {
                adminRights.add(parseRightUnit(document, node));
            }
            task.setAdminRights(adminRights);
            task.setAdminRightConfig(new RightConfigElement());

            // copyTo rights
            List<Element> copyToRightNodes = taskNode.selectNodes(TASK_COPY_TO_RIGHTS_XPATH);
            List<RightUnitElement> copyToRights = new ArrayList<>();
            for (Element node : copyToRightNodes) {
                copyToRights.add(parseRightUnit(document, node));
            }
            task.setCopyToRights(copyToRights);
            task.setCopyToRightConfig(new RightConfigElement());

            // viewer rights
            List<Element> viewerRightNodes = taskNode.selectNodes(TASK_VIEWER_RIGHTS_XPATH);
            List<RightUnitElement> viewerRights = new ArrayList<>();
            for (Element node : viewerRightNodes) {
                viewerRights.add(parseRightUnit(document, node));
            }
            task.setViewerRights(viewerRights);
            task.setViewerRightConfig(new RightConfigElement());

            // buttons
            List<Element> buttonNodes = taskNode.selectNodes(TASK_BUTTONS_XPATH);
            List<ButtonElement> buttons = new ArrayList<ButtonElement>();
            for (Element node : buttonNodes) {
                buttons.add(parseTaskButtonXml(node));
            }
            task.setButtons(buttons);

            // enableOpinionPosition
            String enableOpinionPosition = NodeUtil.getNodeStringValue(taskNode, TASK_ENABLE_OPINION_POSITION);
            task.setEnableOpinionPosition(enableOpinionPosition);
            // optNames
            List<Element> optNameNodes = taskNode.selectNodes(TASK_OPTNAMES_XPATH);
            List<UnitElement> optNames = new ArrayList<UnitElement>();
            for (Element node : optNameNodes) {
                optNames.add(parseUnit(document, node));
            }
            task.setOptNames(optNames);
            // requiredOpinionPosition
            String requiredOpinionPosition = NodeUtil.getNodeStringValue(taskNode, TASK_REQUIRED_OPINION_POSITION);
            task.setRequiredOpinionPosition(requiredOpinionPosition);
            // showUserOpinionPosition
            String showUserOpinionPosition = NodeUtil.getNodeStringValue(taskNode, TASK_SHOW_USER_OPINION_POSITION);
            task.setShowUserOpinionPosition(showUserOpinionPosition);
            // showOpinionPositionStatistics
            String showOpinionPositionStatistics = NodeUtil.getNodeStringValue(taskNode,
                    TASK_SHOW_OPINION_POSITION_STATISTICS);
            task.setShowOpinionPositionStatistics(showOpinionPositionStatistics);

            // records
            // 添加过程记录
            List<Element> records = taskNode.selectNodes(TASK_RECORDS_XPATH);
            for (Element node : records) {
                RecordElement record = parseRecordXml(node);
                task.getRecords().add(record);
            }

            // user
            List<Element> userNodes = taskNode.selectNodes(TASK_USERS_XPATH);
            List<UserUnitElement> users = new ArrayList<>();
            for (Element node : userNodes) {
                users.add(parseUserUnit(document, node));
            }
            task.setUsers(users);

            // copyUsers
            List<Element> copyUserNodes = taskNode.selectNodes(TASK_COPYUSERS_XPATH);
            List<UserUnitElement> copyUsers = new ArrayList<>();
            for (Element node : copyUserNodes) {
                copyUsers.add(parseUserUnit(document, node));
            }
            task.setCopyUsers(copyUsers);

            // transferUsers
            List<Element> transferUserNodes = taskNode.selectNodes(TASK_TRANSFERUSERS_XPATH);
            List<UserUnitElement> transferUsers = new ArrayList<>();
            for (Element node : transferUserNodes) {
                transferUsers.add(parseUserUnit(document, node));
            }
            task.setTransferUsers(transferUsers);

            // emptyToUsers
            List<Element> emptyToUserNodes = taskNode.selectNodes(TASK_EMPTY_TO_USERS_XPATH);
            List<UserUnitElement> emptyToUsers = new ArrayList<>();
            for (Element node : emptyToUserNodes) {
                emptyToUsers.add(parseUserUnit(document, node));
            }
            task.setEmptyToUsers(emptyToUsers);

            // monitors
            List<Element> monitorNodes = taskNode.selectNodes(TASK_MONITORS_XPATH);
            List<UserUnitElement> monitors = new ArrayList<>();
            for (Element node : monitorNodes) {
                monitors.add(parseUserUnit(document, node));
            }
            task.setMonitors(monitors);
            // 运转模式
            Element forkNode = (Element) taskNode.selectSingleNode(TASK_FORK_MODE_XPATH);
            Element joinNode = (Element) taskNode.selectSingleNode(TASK_JOIN_MODE_XPATH);
            if (forkNode != null && joinNode != null) {
                Integer forkValue = NodeUtil.getNodeIntValue(forkNode, TASK_FORK_MODE_VALUE);
                String chooseForkingDirection = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_CHOOSE_FORKING_DIRECTION);
                String businessType = NodeUtil.getNodeStringValue(forkNode, TASK_FORK_MODE_BUSINESS_TYPE);
                String businessTypeName = NodeUtil.getNodeStringValue(forkNode, TASK_FORK_MODE_BUSINESS_TYPE_NAME);
                String businessRole = NodeUtil.getNodeStringValue(forkNode, TASK_FORK_MODE_BUSINESS_ROLE);
                String businessRoleName = NodeUtil.getNodeStringValue(forkNode, TASK_FORK_MODE_BUSINESS_ROLE_NAME);
                List<Element> branchTaskMonitorNodes = taskNode.selectNodes(TASK_FORK_MODE_BRANCH_TASK_MONITORS_XPATH);
                List<UserUnitElement> branchTaskMonitors = Lists.newArrayList();
                for (Element node : branchTaskMonitorNodes) {
                    branchTaskMonitors.add(parseUserUnit(document, node));
                }
                String undertakeSituationPlaceHolder = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_UNDERTAKE_SITUATION_PLACE_HOLDER);
                String undertakeSituationTitleExpression = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_UNDERTAKE_SITUATION_TITLE_EXPRESSION);
                List<Element> undertakeSituationButtonNodes = taskNode
                        .selectNodes(TASK_FORK_MODE_UNDERTAKE_SITUATION_BUTTONS_XPATH);
                List<ButtonElement> undertakeSituationButtons = Lists.newArrayList();
                for (Element node : undertakeSituationButtonNodes) {
                    undertakeSituationButtons.add(parseTaskButtonXml(node));
                }
                List<Element> undertakeSituationColumnNodes = taskNode
                        .selectNodes(TASK_FORK_MODE_UNDERTAKE_SITUATION_COLUMNS_XPATH);
                List<ColumnElement> undertakeSituationColumns = Lists.newArrayList();
                for (Element node : undertakeSituationColumnNodes) {
                    undertakeSituationColumns.add(parseUndertakeSituationColumn(node));
                }
                String sortRule = NodeUtil.getNodeStringValue(taskNode, TASK_FORK_MODE_SORT_RULE_XPATH);
                List<Element> undertakeSituationOrderNodes = taskNode
                        .selectNodes(TASK_FORK_MODE_UNDERTAKE_SITUATION_ORDER_XPATH);
                List<OrderElement> undertakeSituationOrders = Lists.newArrayList();
                for (Element node : undertakeSituationOrderNodes) {
                    undertakeSituationOrders.add(parseUndertakeSituationOrder(node));
                }

                String infoDistributionPlaceHolder = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_INFO_DISTRIBUTION_PLACE_HOLDER);
                String infoDistributionTitleExpression = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_INFO_DISTRIBUTION_TITLE_EXPRESSION);
                String operationRecordPlaceHolder = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_OPERATION_RECORD_PLACE_HOLDER);
                String operationRecordTitleExpression = NodeUtil.getNodeStringValue(forkNode,
                        TASK_FORK_MODE_OPERATION_RECORD_TITLE_EXPRESSION);
                Integer joinValue = NodeUtil.getNodeIntValue(joinNode, TASK_JOIN_MODE_VALUE);
                ParallelGatewayElement parallelGateway = new ParallelGatewayElement();
                parallelGateway.setForkMode(forkValue == null ? 1 : forkValue);
                parallelGateway.setChooseForkingDirection(chooseForkingDirection);
                parallelGateway.setBusinessType(businessType);
                parallelGateway.setBusinessTypeName(businessTypeName);
                parallelGateway.setBusinessRole(businessRole);
                parallelGateway.setBusinessRoleName(businessRoleName);
                parallelGateway.setBranchTaskMonitors(branchTaskMonitors);
                parallelGateway.setUndertakeSituationPlaceHolder(undertakeSituationPlaceHolder);
                parallelGateway.setUndertakeSituationTitleExpression(undertakeSituationTitleExpression);
                parallelGateway.setUndertakeSituationButtons(undertakeSituationButtons);
                parallelGateway.setUndertakeSituationColumns(undertakeSituationColumns);
                parallelGateway.setSortRule(sortRule);
                parallelGateway.setUndertakeSituationOrders(undertakeSituationOrders);
                parallelGateway.setInfoDistributionPlaceHolder(infoDistributionPlaceHolder);
                parallelGateway.setInfoDistributionTitleExpression(infoDistributionTitleExpression);
                parallelGateway.setOperationRecordPlaceHolder(operationRecordPlaceHolder);
                parallelGateway.setOperationRecordTitleExpression(operationRecordTitleExpression);
                parallelGateway.setJoinMode(joinValue == null ? 1 : joinValue);
                task.setParallelGateway(parallelGateway);
            }
            // 退回设置
            // allowReturnAfterRollback
            String allowReturnAfterRollback = NodeUtil.getNodeStringValue(taskNode, TASK_ALLOW_RETURN_AFTER_ROLLBACK);
            task.setAllowReturnAfterRollback(allowReturnAfterRollback);
            // onlyReturnAfterRollback
            String onlyReturnAfterRollback = NodeUtil.getNodeStringValue(taskNode, TASK_ONLY_RETURN_AFTER_ROLLBACK);
            task.setOnlyReturnAfterRollback(onlyReturnAfterRollback);

            String notRollback = NodeUtil.getNodeStringValue(taskNode, TASK_NOT_ROLLBACK);
            task.setNotRollback(notRollback);

            String notCancel = NodeUtil.getNodeStringValue(taskNode, TASK_NOT_CANCEL);
            task.setNotCancel(notCancel);

            // 事件脚本
            List<Element> eventScriptsNodes = taskNode.selectNodes(TASK_EVENT_SCRIPT_XPATH);
            List<ScriptElement> eventScripts = new ArrayList<ScriptElement>();
            for (Element node : eventScriptsNodes) {
                eventScripts.add(parseEventScript(document, node));
            }
            task.setEventScripts(eventScripts);
            tasks.add(task);
        }
        // 设置流程任务
        flow.setTasks(tasks);
    }

    /**
     * @param node
     * @return
     */
    private static ButtonElement parseTaskButtonXml(Element node) {
        ButtonElement button = new ButtonElement();
        String btnSource = NodeUtil.getNodeStringValue(node, BUTTON_BTN_SOURCE);
        button.setBtnSource(btnSource);
        String btnRole = NodeUtil.getNodeStringValue(node, BUTTON_BTN_ROLE);
        button.setBtnRole(btnRole);
        String btnId = NodeUtil.getNodeStringValue(node, BUTTON_BTN_ID);
        button.setBtnId(btnId);
        String piUuid = NodeUtil.getNodeStringValue(node, BUTTON_PI_UUID);
        button.setPiUuid(piUuid);
        String piName = NodeUtil.getNodeStringValue(node, BUTTON_PI_NAME);
        button.setPiName(piName);
        String hashType = NodeUtil.getNodeStringValue(node, BUTTON_HASH_TYPE);
        button.setHashType(hashType);
        String hash = NodeUtil.getNodeStringValue(node, BUTTON_HASH);
        button.setHash(hash);
        String eventParams = NodeUtil.getNodeStringValue(node, BUTTON_EVENT_PARAMS);
        button.setEventParams(eventParams);
        String targetPosition = NodeUtil.getNodeStringValue(node, BUTTON_TARGET_POSITION);
        button.setTargetPosition(targetPosition);
        String id = NodeUtil.getNodeStringValue(node, BUTTON_ID);
        button.setId(id);
        String name = NodeUtil.getNodeStringValue(node, BUTTON_NAME);
        button.setName(name);
        String btnValue = NodeUtil.getNodeStringValue(node, BUTTON_BTNVALUE);
        button.setBtnValue(btnValue);
        String newName = NodeUtil.getNodeStringValue(node, BUTTON_NEWNAME);
        button.setNewName(newName);
        String newCode = NodeUtil.getNodeStringValue(node, BUTTON_NEWCODE);
        button.setNewCode(newCode);
        String useWay = NodeUtil.getNodeStringValue(node, BUTTON_USE_WAY);
        button.setUseWay(useWay);
        String btnArgument = NodeUtil.getNodeStringValue(node, BUTTON_BTNARGUMENT);
        button.setBtnArgument(btnArgument);
        String btnOwners = NodeUtil.getNodeStringValue(node, BUTTON_OWNERS);
        button.setBtnOwners(btnOwners);
        String btnOwnerIds = NodeUtil.getNodeStringValue(node, BUTTON_OWNER_IDS);
        button.setBtnOwnerIds(btnOwnerIds);
        String btnUsers = NodeUtil.getNodeStringValue(node, BUTTON_USERS);
        button.setBtnUsers(btnUsers);
        String btnUserIds = NodeUtil.getNodeStringValue(node, BUTTON_USER_IDS);
        button.setBtnUserIds(btnUserIds);
        String btnCopyUsers = NodeUtil.getNodeStringValue(node, BUTTON_COPY_USERS);
        button.setBtnCopyUsers(btnCopyUsers);
        String btnCopyUserIds = NodeUtil.getNodeStringValue(node, BUTTON_COPY_USER_IDS);
        button.setBtnCopyUserIds(btnCopyUserIds);
        String btnClassName = NodeUtil.getNodeStringValue(node, BUTTON_CLASS_NAME);
        button.setBtnClassName(btnClassName);
        String btnIcon = NodeUtil.getNodeStringValue(node, BUTTON_BTN_ICON);
        button.setBtnIcon(btnIcon);
        String btnStyle = NodeUtil.getNodeStringValue(node, BUTTON_BTN_STYLE);
        button.setBtnStyle(btnStyle);
        String btnRemark = NodeUtil.getNodeStringValue(node, BUTTON_BTN_REMARK);
        button.setBtnRemark(btnRemark);
        String sortOrder = NodeUtil.getNodeStringValue(node, BUTTON_SORT_ORDER);
        button.setSortOrder(sortOrder);
        return button;
    }

    /**
     * @param node
     * @return
     */
    private static ColumnElement parseUndertakeSituationColumn(Element node) {
        ColumnElement column = new ColumnElement();
        String type = NodeUtil.getNodeStringValue(node, COLUMN_TYPE);
        column.setType(type);
        String typeName = NodeUtil.getNodeStringValue(node, COLUMN_TYPE_NAME);
        column.setTypeName(typeName);
        String index = NodeUtil.getNodeStringValue(node, COLUMN_INDEX);
        column.setIndex(index);
        String name = NodeUtil.getNodeStringValue(node, COLUMN_NAME);
        column.setName(name);
        String sources = NodeUtil.getNodeStringValue(node, COLUMN_SOURCES);
        column.setSources(sources);
        Integer searchFlag = NodeUtil.getNodeIntValue(node, COLUMN_SEARCH_FLAG);
        column.setSearchFlag(searchFlag);
        String extraColumn = NodeUtil.getNodeStringValue(node, COLUMN_EXTRA_COLUMN);
        column.setExtraColumn(extraColumn);
        return column;
    }

    /**
     * @param node
     * @return
     */
    private static OrderElement parseUndertakeSituationOrder(Element node) {
        OrderElement element = new OrderElement();
        String name = NodeUtil.getNodeStringValue(node, ORDER_NAME);
        element.setName(name);
        String direction = NodeUtil.getNodeStringValue(node, ORDER_DIRECTION);
        element.setDirection(("desc".equals(direction) || "DESC".equals(direction)) ? "DESC" : "ASC");
        return element;
    }

    /**
     * 解析子流程任务
     *
     * @param taskNode
     * @return
     */
    private static SubTaskElement parseSubTask(Document document, Element taskNode) {
        // 流程任务
        SubTaskElement task = new SubTaskElement();
        // 任务属性
        String name = NodeUtil.getNodeAttrValue(taskNode, TASK_NAME);
        task.setName(name);
        String id = NodeUtil.getNodeAttrValue(taskNode, TASK_ID);
        task.setId(id);
        String type = NodeUtil.getNodeAttrValue(taskNode, TASK_TYPE);
        task.setType(type);
        String code = NodeUtil.getNodeAttrValue(taskNode, TASK_CODE);
        task.setCode(code);
        String expandList = NodeUtil.getNodeAttrValue(taskNode, TASK_EXPAND_LIST);
        task.setExpandList(expandList);
        // X坐标
        String x = NodeUtil.getNodeStringValue(taskNode, TASK_X);
        task.setX(x);
        // Y坐标
        String y = NodeUtil.getNodeStringValue(taskNode, TASK_Y);
        task.setY(y);

        // conditionName
        String conditionName = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_NAME);
        task.setConditionName(conditionName);
        // conditionBody
        String conditionBody = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_BODY);
        task.setConditionBody(conditionBody);
        // conditionX
        String conditionX = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_X);
        task.setConditionX(conditionX);
        // conditionY
        String conditionY = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_Y);
        task.setConditionY(conditionY);
        // conditionLine
        String conditionLine = NodeUtil.getNodeStringValue(taskNode, TASK_CONDITION_LINE);
        task.setConditionLine(conditionLine);

        // businessType
        String businessType = NodeUtil.getNodeStringValue(taskNode, TASK_BUSINESS_TYPE);
        task.setBusinessType(businessType);
        // businessTypeName
        String businessTypeName = NodeUtil.getNodeStringValue(taskNode, TASK_BUSINESS_TYPE_NAME);
        task.setBusinessTypeName(businessTypeName);
        // businessRole
        String businessRole = NodeUtil.getNodeStringValue(taskNode, TASK_BUSINESS_ROLE);
        task.setBusinessRole(businessRole);
        // businessRoleName
        String businessRoleName = NodeUtil.getNodeStringValue(taskNode, TASK_BUSINESS_ROLE_NAME);
        task.setBusinessRoleName(businessRoleName);

        // newFlows
        List<Element> newFlowNodes = taskNode.selectNodes(TASK_NEW_FLOW);
        List<NewFlowElement> newFlows = new ArrayList<NewFlowElement>();
        for (Element node : newFlowNodes) {
            newFlows.add(parseNewFlow(document, node));
        }
        task.setNewFlows(newFlows);

        // relations
        List<Element> relationNodes = taskNode.selectNodes(TASK_RELATIONS_XPATH);
        List<RelationElement> relations = new ArrayList<RelationElement>();
        for (Element node : relationNodes) {
            relations.add(parseRelation(document, node));
        }
        task.setRelations(relations);

        // undertakeSituationPlaceHolder
        String undertakeSituationPlaceHolder = NodeUtil.getNodeStringValue(taskNode,
                TASK_UNDERTAKE_SITUATION_PLACE_HOLDER);
        task.setUndertakeSituationPlaceHolder(undertakeSituationPlaceHolder);
        // undertakeSituationTitleExpression
        String undertakeSituationTitleExpression = NodeUtil.getNodeStringValue(taskNode,
                TASK_UNDERTAKE_SITUATION_TITLE_EXPRESSION);
        task.setUndertakeSituationTitleExpression(undertakeSituationTitleExpression);
        // undertakeSituationButtons
        List<Element> undertakeSituationButtonNodes = taskNode.selectNodes(TASK_UNDERTAKE_SITUATION_BUTTONS_XPATH);
        List<ButtonElement> undertakeSituationButtons = Lists.newArrayList();
        for (Element node : undertakeSituationButtonNodes) {
            undertakeSituationButtons.add(parseTaskButtonXml(node));
        }
        task.setUndertakeSituationButtons(undertakeSituationButtons);
        // undertakeSituationColumns
        List<Element> undertakeSituationColumnNodes = taskNode.selectNodes(TASK_UNDERTAKE_SITUATION_COLUMNS_XPATH);
        List<ColumnElement> undertakeSituationColumns = Lists.newArrayList();
        for (Element node : undertakeSituationColumnNodes) {
            undertakeSituationColumns.add(parseUndertakeSituationColumn(node));
        }
        task.setUndertakeSituationColumns(undertakeSituationColumns);

        String sortRule = NodeUtil.getNodeStringValue(taskNode, TASK_SORT_RULE_XPATH);
        task.setSortRule(sortRule);
        // undertakeSituationOrders
        List<Element> undertakeSituationOrderNodes = taskNode.selectNodes(TASK_UNDERTAKE_SITUATION_ORDERS_XPATH);
        List<OrderElement> undertakeSituationOrders = Lists.newArrayList();
        for (Element node : undertakeSituationOrderNodes) {
            undertakeSituationOrders.add(parseUndertakeSituationOrder(node));
        }
        task.setUndertakeSituationOrders(undertakeSituationOrders);

        // infoDistributionPlaceHolder
        String infoDistributionPlaceHolder = NodeUtil.getNodeStringValue(taskNode, TASK_INFO_DISTRIBUTION_PLACE_HOLDER);
        task.setInfoDistributionPlaceHolder(infoDistributionPlaceHolder);
        // infoDistributionTitleExpression
        String infoDistributionTitleExpression = NodeUtil.getNodeStringValue(taskNode,
                TASK_INFO_DISTRIBUTION_TITLE_EXPRESSION);
        task.setInfoDistributionTitleExpression(infoDistributionTitleExpression);
        // operationRecordPlaceHolder
        String operationRecordPlaceHolder = NodeUtil.getNodeStringValue(taskNode, TASK_OPERATION_RECORD_PLACE_HOLDER);
        task.setOperationRecordPlaceHolder(operationRecordPlaceHolder);
        // operationRecordTitleExpression
        String operationRecordTitleExpression = NodeUtil.getNodeStringValue(taskNode,
                TASK_OPERATION_RECORD_TITLE_EXPRESSION);
        task.setOperationRecordTitleExpression(operationRecordTitleExpression);
        // subTaskMonitors
        List<Element> subTaskMonitorNodes = taskNode.selectNodes(TASK_SUB_TASK_MONITORS_XPATH);
        List<UserUnitElement> subTaskMonitors = new ArrayList<>();
        for (Element node : subTaskMonitorNodes) {
            subTaskMonitors.add(parseUserUnit(document, node));
        }
        task.setSubTaskMonitors(subTaskMonitors);
        // inStagesCondition
        String inStagesCondition = NodeUtil.getNodeStringValue(taskNode, TASK_IN_STAGES_CONDITION);
        task.setInStagesCondition(inStagesCondition);
        // childLookParent
        String childLookParent = NodeUtil.getNodeAttrValue(taskNode, TASK_CHILD_LOOK_PARENT);
        task.setChildLookParent(childLookParent);
        // parentSetChild
        String parentSetChild = NodeUtil.getNodeAttrValue(taskNode, TASK_PARENT_SET_CHILD);
        task.setParentSetChild(parentSetChild);
        // stageDivisionFormId
        String stageDivisionFormId = NodeUtil.getNodeStringValue(taskNode, TASK_STAGE_DIVISION_FORM_ID);
        task.setStageDivisionFormId(stageDivisionFormId);
        // stageHandlingState
        String stageHandlingState = NodeUtil.getNodeStringValue(taskNode, TASK_STAGE_HANDLING_STATE);
        task.setStageHandlingState(stageHandlingState);
        // stageState
        String stageState = NodeUtil.getNodeStringValue(taskNode, TASK_STAGE_STATE);
        task.setStageState(stageState);
        // stages
        List<Element> stageNodes = taskNode.selectNodes(TASK_STAGES_XPATH);
        List<StageElement> stages = new ArrayList<StageElement>();
        for (Element node : stageNodes) {
            stages.add(parseStage(document, node));
        }
        task.setStages(stages);
        // traceTask
        String traceTask = NodeUtil.getNodeStringValue(taskNode, TASK_TRACE_TASK);
        task.setTraceTask(traceTask);
        return task;
    }

    /**
     * 解析流向
     *
     * @param document
     * @param flow
     */
    private static void parseDirection(Document document, FlowElement flow) {
        // 流程流向
        List<DirectionElement> directions = new ArrayList<DirectionElement>();
        // 流向元素
        List<Element> directionNodes = selectElements(document, DIRECTION_XPATH);
        for (Element directionNode : directionNodes) {
            // 流向
            DirectionElement direction = new DirectionElement();
            // name
            String name = NodeUtil.getNodeAttrValue(directionNode, DIRECTION_NAME);
            direction.setName(name);
            // useAsButton
            String useAsButton = NodeUtil.getNodeStringValue(directionNode, DIRECTION_USE_AS_BUTTON);
            direction.setUseAsButton(useAsButton);
            // buttonName
            String buttonName = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BUTTON_NAME);
            direction.setButtonName(buttonName);
            // buttonClassName
            String buttonClassName = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BUTTON_CLASS_NAME);
            direction.setButtonClassName(buttonClassName);
            // btnStyle
            String btnStyle = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BTN_STYLE);
            direction.setBtnStyle(btnStyle);
            // buttonOrder
            String buttonOrder = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BUTTON_ORDER);
            direction.setButtonOrder(buttonOrder);
            // id
            String id = NodeUtil.getNodeAttrValue(directionNode, DIRECTION_ID);
            direction.setId(id);
            // type
            String type = NodeUtil.getNodeAttrValue(directionNode, DIRECTION_TYPE);
            direction.setType(type);
            // fromID
            String fromID = NodeUtil.getNodeAttrValue(directionNode, DIRECTION_FROMID);
            direction.setFromID(fromID);
            // toID
            String toID = NodeUtil.getNodeAttrValue(directionNode, DIRECTION_TOID);
            direction.setToID(toID);
            // sortOrder
            String sortOrder = NodeUtil.getNodeStringValue(directionNode, DIRECTION_SORT_ORDER);
            direction.setSortOrder(sortOrder);
            // remark
            String remark = NodeUtil.getNodeStringValue(directionNode, DIRECTION_REMARK);
            direction.setRemark(remark);
            // showRemark
            String showRemark = NodeUtil.getNodeStringValue(directionNode, DIRECTION_SHOW_REMARK);
            direction.setShowRemark(showRemark);
            // terminalName
            String terminalName = NodeUtil.getNodeStringValue(directionNode, DIRECTION_TERMINAL_NAME);
            direction.setTerminalName(terminalName);
            // terminalType
            String terminalType = NodeUtil.getNodeStringValue(directionNode, DIRECTION_TERMINAL_TYPE);
            direction.setTerminalType(terminalType);
            // terminalX
            String terminalX = NodeUtil.getNodeStringValue(directionNode, DIRECTION_TERMINAL_X);
            direction.setTerminalX(terminalX);
            // terminalY
            String terminalY = NodeUtil.getNodeStringValue(directionNode, DIRECTION_TERMINAL_Y);
            direction.setTerminalY(terminalY);
            // terminalBody
            String terminalBody = NodeUtil.getNodeStringValue(directionNode, DIRECTION_TERMINAL_BODY);
            direction.setTerminalBody(terminalBody);
            // lineLabel
            String lineLabel = NodeUtil.getNodeStringValue(directionNode, DIRECTION_LINE_LABEL);
            direction.setLineLabel(lineLabel);
            // line
            String line = NodeUtil.getNodeStringValue(directionNode, DIRECTION_LINE);
            direction.setLine(line);
            // isShowName
            String isShowName = NodeUtil.getNodeStringValue(directionNode, DIRECTION_IS_SHOW_NAME);
            direction.setIsShowName(isShowName);

            // conditions
            List<Element> conditionNodes = directionNode.selectNodes(DIRECTION_CONDITIONS_XPATH);// selectElements(document,
            // DIRECTION_CONDITIONS_XPATH);
            List<ConditionUnitElement> conditions = new ArrayList<ConditionUnitElement>();
            for (Element node : conditionNodes) {
                conditions.add(parseConditionUnit(document, node));
            }
            direction.setConditions(conditions);

            // fileRecipients
            List<Element> fileRecipientNodes = directionNode.selectNodes(DIRECTION_FILE_RECIPIENTS_XPATH);
            List<UnitElement> fileRecipients = new ArrayList<UnitElement>();
            for (Element node : fileRecipientNodes) {
                fileRecipients.add(parseUnit(document, node));
            }
            direction.setFileRecipients(fileRecipients);

            // msgRecipients
            List<Element> msgRecipientNodes = directionNode.selectNodes(DIRECTION_MSG_RECIPIENTS_XPATH);
            List<UserUnitElement> msgRecipients = new ArrayList<>();
            for (Element node : msgRecipientNodes) {
                msgRecipients.add(parseUserUnit(document, node));
            }
            direction.setMsgRecipients(msgRecipients);

            // isDefault
            String isDefault = NodeUtil.getNodeStringValue(directionNode, DIRECTION_IS_DEFAULT);
            direction.setIsDefault(isDefault);
            String isEveryCheck = NodeUtil.getNodeStringValue(directionNode, DIRECTION_IS_EVERYCHECK);
            direction.setIsEveryCheck(isEveryCheck);
            String isSendFile = NodeUtil.getNodeStringValue(directionNode, DIRECTION_IS_SEND_FILE);
            direction.setIsSendFile(isSendFile);
            String isSendMsg = NodeUtil.getNodeStringValue(directionNode, DIRECTION_IS_SEND_MSG);
            direction.setIsSendMsg(isSendMsg);

            // listener
            String listener = NodeUtil.getNodeStringValue(directionNode, DIRECTION_LISTENER);
            direction.setListener(listener);

            // branchMode
            String branchMode = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_MODE);
            direction.setBranchMode(branchMode);

            // branchInstanceType
            String branchInstanceType = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_INSTANCE_TYPE);
            direction.setBranchInstanceType(branchInstanceType);

            // branchCreateWay
            String branchCreateWay = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_CREATE_WAY);
            direction.setBranchCreateWay(branchCreateWay);

            // branchCreateInstanceFormId
            String branchCreateInstanceFormId = NodeUtil.getNodeStringValue(directionNode,
                    DIRECTION_BRANCH_CREATE_INSTANCE_FORM_ID);
            direction.setBranchCreateInstanceFormId(branchCreateInstanceFormId);

            // isMainFormBranchCreateWay
            String isMainFormBranchCreateWay = NodeUtil.getNodeStringValue(directionNode,
                    DIRECTION_IS_MAIN_FORM_BRANCH_CREATE_WAY);
            direction.setIsMainFormBranchCreateWay(isMainFormBranchCreateWay);

            // branchTaskUsers
            String branchTaskUsers = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_TASK_USERS);
            direction.setBranchTaskUsers(branchTaskUsers);

            // branchCreateInstanceWay
            String branchCreateInstanceWay = NodeUtil.getNodeStringValue(directionNode,
                    DIRECTION_BRANCH_CREATE_INSTANCE_WAY);
            direction.setBranchCreateInstanceWay(branchCreateInstanceWay);

            // branchCreateInstanceBatch
            String branchCreateInstanceBatch = NodeUtil.getNodeStringValue(directionNode,
                    DIRECTION_BRANCH_CREATE_INSTANCE_BATCH);
            direction.setBranchCreateInstanceBatch(branchCreateInstanceBatch);

            // branchTimer
            String branchTimer = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_TIMER);
            direction.setBranchTimer(branchTimer);

            // branchInterface
            String branchInterface = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_INTERFACE);
            direction.setBranchInterface(branchInterface);

            // branchInterfaceName
            String branchInterfaceName = NodeUtil.getNodeStringValue(directionNode, DIRECTION_BRANCH_INTERFACE);
            direction.setBranchInterfaceName(branchInterfaceName);

            // shareBranch
            String shareBranch = NodeUtil.getNodeStringValue(directionNode, DIRECTION_SHARE_BRANCH);
            direction.setShareBranch(shareBranch);

            // isIndependentBranch
            String isIndependentBranch = NodeUtil.getNodeStringValue(directionNode, DIRECTION_IS_INDEPENDENT_BRANCH);
            direction.setIsIndependentBranch(isIndependentBranch);

            // eventScript
            Element eventScriptNode = (Element) directionNode.selectSingleNode(DIRECTION_EVENT_SCRIPT);
            if (eventScriptNode != null) {
                direction.setEventScript(parseEventScript(document, eventScriptNode));
            }

            // archives
            List<Element> archivesNodes = directionNode.selectNodes(DIRECTION_ARCHIVE_XPATH);
            List<ArchiveElement> archiveElements = new ArrayList<ArchiveElement>(0);
            for (Element archivesNode : archivesNodes) {
                archiveElements.add(parseArchives(document, archivesNode));
            }
            direction.setArchives(archiveElements);

            // 添加流向
            directions.add(direction);
        }

        // 流向排序
        Collections.sort(directions);
        // 设置流程流向
        flow.setDirections(directions);
    }

    /**
     * 解析文本
     *
     * @param document
     * @param flow
     */
    private static void parseLabel(Document document, FlowElement flow) {
    }

    /**
     * 解析已删除
     *
     * @param document
     * @param flow
     */
    private static void parseDelete(Document document, FlowElement flow) {
    }

    /* lmw 2015-4-23 11:18 begin */
    private static RecordElement parseRecord(Document document, Element node) {
        if (node == null) {
            return null;
        }

        RecordElement re = new RecordElement();
        String name = NodeUtil.getNodeStringValue(node, RECORD_NAME);
        String field = NodeUtil.getNodeStringValue(node, RECORD_FIELD);
        String way = NodeUtil.getNodeStringValue(node, RECORD_WAY);
        String assembler = NodeUtil.getNodeStringValue(node, RECORD_ASSEMBLER);
        String ignoreEmpty = NodeUtil.getNodeStringValue(node, RECORD_IGNORE_EMPTY);
        String fieldNotValidate = NodeUtil.getNodeStringValue(node, RECORD_FIELD_NOT_VALIDATE);
        String enableWysiwyg = NodeUtil.getNodeStringValue(node, RECORD_ENABLE_WYSIWYG);
        String value = NodeUtil.getNodeStringValue(node, RECORD_VALUE);
        String taskIds = NodeUtil.getNodeStringValue(node, RECORD_TASK_IDS);
        String contentOrigin = NodeUtil.getNodeStringValue(node, CONTENT_ORIGIN);
        String enablePreCondition = NodeUtil.getNodeStringValue(node, RECORD_ENABLE_PRE_CONDITION);
        // conditions
        List<Element> conditionNodes = node.selectNodes(RECORD_CONDITIONS_XPATH);
        List<ConditionUnitElement> conditions = new ArrayList<ConditionUnitElement>();
        for (Element conditionNode : conditionNodes) {
            conditions.add(parseConditionUnit(document, conditionNode));
        }
        re.setField(field);
        re.setName(name);
        re.setValue(value);
        re.setWay(way);
        re.setAssembler(assembler);
        re.setIgnoreEmpty(ignoreEmpty);
        re.setFieldNotValidate(fieldNotValidate);
        re.setEnableWysiwyg(enableWysiwyg);
        re.setTaskIds(taskIds);
        re.setEnablePreCondition(enablePreCondition);
        re.setConditions(conditions);
        re.setContentOrigin(contentOrigin);
        return re;
    }

    /* lmw 2015-4-23 11:18 end */

    private static UnitElement parseUnit(Document document, Element node) {
        UnitElement unit = new UnitElement();
        Integer type = NodeUtil.getNodeAttrIntValue(node, UNIT_TPYE);
        unit.setType(type);
        if (UNIT_TYPE_USER.equals(type)) {
            unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
            unit.setArgValue(NodeUtil.getNodeStringValue(node, UNIT_ARGVALUE));
        } else if (UNIT_TYPE_MULTIVALUE.equals(type)) {
            unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
            unit.setArgValue(NodeUtil.getNodeStringValue(node, UNIT_ARGVALUE));
        } else if (UNIT_TYPE_SINGLEVALUE.equals(type)) {
            unit.setValue(node.getTextTrim());
        } else {
            unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
        }
        return unit;
    }

    private static UserUnitElement parseUserUnit(Document document, Element node) {
        UserUnitElement unit = new UserUnitElement();
        Integer type = NodeUtil.getNodeAttrIntValue(node, UNIT_TPYE);
        unit.setType(type);
        if (UNIT_TYPE_USER.equals(type)) {
            unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
            unit.setArgValue(NodeUtil.getNodeStringValue(node, UNIT_ARGVALUE));
        } else if (UNIT_TYPE_MULTIVALUE.equals(type)) {
            unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
            unit.setArgValue(NodeUtil.getNodeStringValue(node, UNIT_ARGVALUE));
        } else if (UNIT_TYPE_SINGLEVALUE.equals(type)) {
            unit.setValue(node.getTextTrim());
        } else {
            unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
        }
        return unit;
    }

    private static RightUnitElement parseRightUnit(Document document, Element node) {
        UnitElement unit = parseUnit(document, node);
        RightUnitElement rightUnit = new RightUnitElement();
        rightUnit.setName(NodeUtil.getNodeStringValue(node, BUTTON_NAME));
        rightUnit.setTitle(NodeUtil.getNodeStringValue(node, BUTTON_TITLE));
        rightUnit.setType(unit.getType());
        rightUnit.setValue(unit.getValue());
        rightUnit.setArgValue(unit.getArgValue());
        return rightUnit;
    }

    private static ConditionUnitElement parseConditionUnit(Document document, Element node) {
        ConditionUnitElement unit = new ConditionUnitElement();
        unit.setType(NodeUtil.getNodeAttrIntValue(node, UNIT_TPYE));
        unit.setValue(NodeUtil.getNodeStringValue(node, UNIT_VALUE));
        unit.setArgValue(NodeUtil.getNodeStringValue(node, UNIT_ARGVALUE));
        return unit;
    }

    /**
     * @param document
     * @param node
     * @return
     */
    private static ScriptElement parseEventScript(Document document, Element node) {
        String pointcut = node.attributeValue(EVENT_SCRIPT_POINTCUT);
        String scriptType = node.attributeValue(EVENT_SCRIPT_TYPE);
        String contentType = node.attributeValue(EVENT_SCRIPT_CONTENT_TYPE);
        String scriptContent = node.getText();
        ScriptElement eventScript = new ScriptElement();
        eventScript.setPointcut(pointcut);
        eventScript.setType(scriptType);
        eventScript.setContentType(contentType);
        eventScript.setContent(scriptContent);
        return eventScript;
    }

    /**
     * @param document
     * @param node
     * @return
     */
    private static ArchiveElement parseArchives(Document document, Element node) {
        ArchiveElement archive = new ArchiveElement();
        archive.setArchiveId(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_ARCHIVE_ID));
        archive.setArchiveWay(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_ARCHIVE_WAY));
        archive.setArchiveStrategy(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_ARCHIVE_STRATEGY));
        archive.setBotRuleName(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_BOT_RULE_NAME));
        archive.setBotRuleId(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_BOT_RULE_ID));
        archive.setDestFolderName(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_DEST_FOLDER_NAME));
        archive.setDestFolderUuid(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_DEST_FOLDER_UUID));
        archive.setSubFolderRule(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_SUB_FOLDER_RULE));
        archive.setFillDateTime(
                Boolean.parseBoolean(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_FILL_DATETIME)));
        archive.setArchiveScriptType(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_ARCHIVE_SCRIPT_TYPE));
        archive.setArchiveScript(NodeUtil.getNodeStringValue(node, DIRECTION_ARCHIVE_ARCHIVE_SCRIPT));
        return archive;
    }

    // add by wujx 20160728
    private static FieldPropertyElement parseFieldProperty(Document document, Element node) {
        FieldPropertyElement fieldProperty = new FieldPropertyElement();
        String value = NodeUtil.getNodeStringValue(node, TASK_FIELD_PROPERTY_VALUE);
        String name = NodeUtil.getNodeStringValue(node, TASK_FIELD_PROPERTY_NAME);
        fieldProperty.setValue(value);
        fieldProperty.setName(name);
        return fieldProperty;
    }

    private static RecordElement parseRecordXml(Element node) {
        RecordElement record = new RecordElement();
        // record.setUuid(NodeUtil.getNodeAttrValue(node, FLOW_UUID));
        record.setName(NodeUtil.getNodeStringValue(node, RECORD_NAME));
        record.setField(NodeUtil.getNodeStringValue(node, RECORD_FIELD));
        record.setWay(NodeUtil.getNodeStringValue(node, RECORD_WAY));
        record.setAssembler(NodeUtil.getNodeStringValue(node, RECORD_ASSEMBLER));
        record.setIgnoreEmpty(NodeUtil.getNodeStringValue(node, RECORD_IGNORE_EMPTY));
        record.setFieldNotValidate(NodeUtil.getNodeStringValue(node, RECORD_FIELD_NOT_VALIDATE));
        record.setEnableWysiwyg(NodeUtil.getNodeStringValue(node, RECORD_ENABLE_WYSIWYG));
        record.setValue(NodeUtil.getNodeStringValue(node, RECORD_VALUE));
        record.setEnablePreCondition(NodeUtil.getNodeStringValue(node, RECORD_ENABLE_PRE_CONDITION));
        // conditions
        List<Element> conditionNodes = node.selectNodes(RECORD_CONDITIONS_XPATH);
        List<ConditionUnitElement> conditions = new ArrayList<ConditionUnitElement>();
        for (Element conditionNode : conditionNodes) {
            conditions.add(parseConditionUnit(null, conditionNode));
        }
        record.setConditions(conditions);
        record.setContentOrigin(NodeUtil.getNodeStringValue(node, CONTENT_ORIGIN));
        return record;
    }

    /**
     * 解析子流程
     *
     * @param document
     * @param node
     * @return
     */
    private static NewFlowElement parseNewFlow(Document document, Element node) {
        NewFlowElement newFlow = new NewFlowElement();
        newFlow.setId(NodeUtil.getNodeStringValue(node, "id"));
        newFlow.setName(NodeUtil.getNodeStringValue(node, "name"));
        newFlow.setValue(NodeUtil.getNodeStringValue(node, "value"));
        newFlow.setLabel(NodeUtil.getNodeStringValue(node, "label"));
        newFlow.setConditions(NodeUtil.getNodeStringValue(node, "conditions"));
        newFlow.setCreateWay(NodeUtil.getNodeStringValue(node, "createWay"));
        newFlow.setCreateInstanceFormId(NodeUtil.getNodeStringValue(node, "createInstanceFormId"));
        newFlow.setIsMainFormCreateWay(NodeUtil.getNodeStringValue(node, "isMainFormCreateWay"));
        newFlow.setTaskUsers(NodeUtil.getNodeStringValue(node, "taskUsers"));
        newFlow.setCreateInstanceWay(NodeUtil.getNodeStringValue(node, "createInstanceWay"));
        newFlow.setCreateInstanceBatch(NodeUtil.getNodeStringValue(node, "createInstanceBatch"));
        newFlow.setInterfaceName(NodeUtil.getNodeStringValue(node, "interfaceName"));
        newFlow.setInterfaceValue(NodeUtil.getNodeStringValue(node, "interfaceValue"));
        newFlow.setIsMajor(NodeUtil.getNodeStringValue(node, "isMajor"));
        newFlow.setIsMerge(NodeUtil.getNodeStringValue(node, "isMerge"));
        newFlow.setIsWait(NodeUtil.getNodeStringValue(node, "isWait"));
        newFlow.setIsShare(NodeUtil.getNodeStringValue(node, "isShare"));
        newFlow.setNotifyDoing(NodeUtil.getNodeStringValue(node, "notifyDoing"));
        newFlow.setToTaskName(NodeUtil.getNodeStringValue(node, "toTaskName"));
        newFlow.setToTaskId(NodeUtil.getNodeStringValue(node, "toTaskId"));
        newFlow.setTitle(NodeUtil.getNodeStringValue(node, "title"));
        newFlow.setTitleExpression(NodeUtil.getNodeStringValue(node, "titleExpression"));
        newFlow.setCopyBotRuleId(NodeUtil.getNodeStringValue(node, "copyBotRuleId"));
        newFlow.setSyncBotRuleId(NodeUtil.getNodeStringValue(node, "syncBotRuleId"));
        newFlow.setReturnWithOver(NodeUtil.getNodeStringValue(node, "returnWithOver"));
        newFlow.setReturnWithDirection(NodeUtil.getNodeStringValue(node, "returnWithDirection"));
        newFlow.setReturnDirectionId(NodeUtil.getNodeStringValue(node, "returnDirectionId"));
        newFlow.setReturnBotRuleId(NodeUtil.getNodeStringValue(node, "returnBotRuleId"));
        newFlow.setCopyFields(NodeUtil.getNodeStringValue(node, "copyFields"));
        newFlow.setReturnOverrideFields(NodeUtil.getNodeStringValue(node, "returnOverrideFields"));
        newFlow.setReturnAdditionFields(NodeUtil.getNodeStringValue(node, "returnAdditionFields"));
        /* add by huanglinchuan 2014.10.24 begin */
        newFlow.setSubformId(NodeUtil.getNodeStringValue(node, "subformId"));
        /* add by huanglinchuan 2014.10.24 end */
        // 办理信息展示
        newFlow.setUndertakeSituationPlaceHolder(NodeUtil.getNodeStringValue(node, "undertakeSituationPlaceHolder"));
        newFlow.setInfoDistributionPlaceHolder(NodeUtil.getNodeStringValue(node, "infoDistributionPlaceHolder"));
        newFlow.setOperationRecordPlaceHolder(NodeUtil.getNodeStringValue(node, "operationRecordPlaceHolder"));
        return newFlow;
    }

    /**
     * 解析子流程并联关系
     *
     * @param document
     * @param node
     * @return
     */
    private static RelationElement parseRelation(Document document, Element node) {
        RelationElement relation = new RelationElement();
        relation.setNewFlowName(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_NEW_FLOW_NAME));
        relation.setNewFlowId(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_NEW_FLOW_ID));
        relation.setTaskName(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_TASK_NAME));
        relation.setTaskId(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_TASK_ID));
        relation.setFrontNewFlowName(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_FRONT_NEW_FLOW_NAME));
        relation.setFrontNewFlowId(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_FRONT_NEW_FLOW_ID));
        relation.setFrontTaskName(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_FRONT_TASK_NAME));
        relation.setFrontTaskId(NodeUtil.getNodeStringValue(node, TASK_RELATIONS_FRONT_TASK_ID));
        return relation;
    }

    /**
     * @param document
     * @param node
     * @return
     */
    private static StageElement parseStage(Document document, Element node) {
        StageElement stage = new StageElement();
        stage.setNewFlowId(NodeUtil.getNodeStringValue(node, TASK_STAGES_NEW_FLOW_ID));
        stage.setNewFlowName(NodeUtil.getNodeStringValue(node, TASK_StageS_NEW_FLOW_NAME));
        stage.setTaskUsers(NodeUtil.getNodeStringValue(node, TASK_STAGES_TASK_USERS));
        stage.setTaskUsersName(NodeUtil.getNodeStringValue(node, TASK_STAGES_TASK_USERS_NAME));
        stage.setCreateInstanceWay(NodeUtil.getNodeStringValue(node, TASK_STAGES_CREATE_INSTANCE_WAY));
        stage.setLimitTime(NodeUtil.getNodeStringValue(node, TASK_STAGES_LIMIT_TIME));
        stage.setLimitTimeName(NodeUtil.getNodeStringValue(node, TASK_STAGES_LIMIT_TIME_NAME));
        return stage;
    }

    /**
     * 选择单个元素
     *
     * @param document
     * @param xPath
     * @return
     */
    public static Element selectSingleElement(Document document, String xPath) {
        XPath selector = DocumentHelper.createXPath(xPath);
        return (Element) selector.selectSingleNode(document);
    }

    /**
     * 选择多个元素
     *
     * @param document
     * @param xPath
     * @return
     */
    @SuppressWarnings({"unchecked", "cast"})
    public static List<Element> selectElements(Document document, String xPath) {
        XPath selector = DocumentHelper.createXPath(xPath);
        return (List<Element>) selector.selectNodes(document);
    }

    public static String getTaskSubNode(String node) {
        if (StringUtils.isBlank(node)) {
            return FlowDefConstants.TASK_XPATH;
        } else if (node.startsWith("/")) {
            return FlowDefConstants.TASK_XPATH + node;
        }
        return FlowDefConstants.TASK_XPATH + "/" + node;
    }

    public static String getPropertySubNode(String node) {
        if (StringUtils.isBlank(node)) {
            return FlowDefConstants.FLOW_PROPERTY_XPATH;
        } else if (node.startsWith("/")) {
            return FlowDefConstants.FLOW_PROPERTY_XPATH + node;
        }
        return FlowDefConstants.FLOW_PROPERTY_XPATH + "/" + node;
    }

    public static String getDirectionSubNode(String node) {
        if (StringUtils.isBlank(node)) {
            return FlowDefConstants.DIRECTION_XPATH;
        } else if (node.startsWith("/")) {
            return FlowDefConstants.DIRECTION_XPATH + node;
        }
        return FlowDefConstants.DIRECTION_XPATH + "/" + node;
    }

    public static List<Element> emptyElements(Document document, String xPath) {
        return FlowDefinitionParser.setElementsText(document, xPath, "");
    }

    public static List<Element> setElementsText(Document document, String xPath, String text) {
        XPath selector = DocumentHelper.createXPath(xPath);
        List<Element> elements = (List<Element>) selector.selectNodes(document);
        if (elements != null && elements.size() > 0) {
            for (Element element : elements) {
                if (StringUtils.isBlank(text) && element.getParent().remove(element)) {
                } else {
                    element.setText(text);
                    System.out.println(element.getText());
                }
            }
        }
        return elements;
    }

    public static void setElementsAttr(List<Element> elements, String attributeName, String attributeValue) {
        if (elements == null || elements.isEmpty()) {
            return;
        }
        for (Element element : elements) {
            FlowDefinitionParser.setElementAttr(element, attributeName, attributeValue);
        }
    }

    public static void setElementAttr(Element element, String attributeName, String attributeValue) {
        if (element != null && StringUtils.isNotBlank(attributeName)) {
            element.addAttribute(attributeName, attributeValue);
        }
    }
}
