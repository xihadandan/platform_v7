/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.select2.*;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.dto.DataItem;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.KeyValuePair;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.facade.api.AppFunctionFacade;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.business.facade.service.BusinessFacadeService;
import com.wellsoft.pt.basicdata.datadict.dto.CdDataDictionaryItemDto;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.support.PrintUtils;
import com.wellsoft.pt.basicdata.serialnumber.dto.SnSerialNumberDefinitionDto;
import com.wellsoft.pt.bpm.engine.access.FlowAccessPermissionProvider;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.listener.*;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.dispatcher.CustomDispatcherBranchTaskResolver;
import com.wellsoft.pt.bpm.engine.dispatcher.CustomDispatcherFlowResolver;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.entity.WfFlowDefinitionUserEntity;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowVariables;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.StartNode;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.DictionParser;
import com.wellsoft.pt.bpm.engine.parser.FlowConfiguration;
import com.wellsoft.pt.bpm.engine.parser.FlowDefinitionParser;
import com.wellsoft.pt.bpm.engine.parser.FlowDictionary;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.FlowDefinitionUserModifyParams;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.TaskUserExpressionConfigJson;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.NodeUtil;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.jedis.JedisCache;
import com.wellsoft.pt.dyform.facade.dto.*;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;
import com.wellsoft.pt.dyform.implement.definition.enums.DyformTypeEnum;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.facade.service.MessageTemplateApiFacade;
import com.wellsoft.pt.org.dto.OrgRoleDto;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.rule.engine.RuleEngine;
import com.wellsoft.pt.rule.engine.RuleEngineFactory;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import com.wellsoft.pt.workflow.service.WfOpinionCheckSetService;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
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
@Service
@Transactional(readOnly = true)
public class FlowSchemeServiceImpl extends BaseServiceImpl implements FlowSchemeService {

    private static final String QUERY_FLOW_DEF_WHERE_HQL = "select o.uuid as uuid, o.id as id, o.name as name, o.code as code, o.category as category from FlowDefinition o "
            + "where o.enabled = true " + "and exists(select id, max(version) from FlowDefinition wf_flow_definition "
            + "group by id, enabled having wf_flow_definition.id = o.id and max(version) = o.version and enabled = true) order by o.code asc";

    private static final String QUERY_FLOW_DEF_BY_CATEGORY = "select id as id, name as name from FlowDefinition o where o.enabled = true and o.category = :category and id != :excludeFlowId "
            + "and exists(select id, max(version) from FlowDefinition wf_flow_definition "
            + "group by id, enabled having wf_flow_definition.id = o.id and max(version) = o.version and enabled = true)";
    private static final String FLOW_DEFINITION_CACHE_ID = ModuleID.WORKFLOW.getName();
    private static final String NODE_FLOWS = "flows";
    private static final String NODE_CATEGORY = "category";
    private static final String NODE_CODE = "code";
    private static final String NODE_NAME = "name";
    private static final String NODE_FLOW = "flow";

    // @Autowired
    // private DytableApiFacade dytableApiFacade;
    private static final String NODE_FLOW_ID = "id";
    private static final String NODE_FLOW_VERSION = "version";

    @Autowired
    AppFunctionFacade appFunctionFacade;
    @Autowired
    private WorkService workService;
    @Autowired
    private FlowSchemaService flowSchemaService;
    @Autowired
    private DictionParser dictionParser;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private FlowCategoryService categoryService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;
    @Autowired
    private MessageTemplateApiFacade messageTemplateApiFacade;
    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    // @Autowired
    // private BusinessCategoryService businessCategoryService;
    //
    // @Autowired
    // private BusinessRoleService businessRoleService;
    @Autowired
    private BusinessFacadeService businessFacadeService;
    @Autowired
    private WfOpinionCheckSetService wfOpinionCheckSetService;
    @Autowired(required = false)
    private List<Listener> listeners = Lists.newArrayListWithCapacity(0);
    @Autowired(required = false)
    private List<CustomDispatcherFlowResolver> dispatcherFlowResolvers = Lists.newArrayListWithCapacity(0);
    @Autowired(required = false)
    private List<CustomDispatcherBranchTaskResolver> dispatcherBranchTaskResolvers = Lists.newArrayListWithCapacity(0);
    @Autowired(required = false)
    private List<FlowAccessPermissionProvider> flowAccessPermissionProviders;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private WfFlowDefinitionUserService flowDefinitionUserService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;


    /**
     * 如何描述该方法
     *
     * @param formDefinition
     * @return
     */
    private static Map<String, DyformSubformFormDefinition> getSubformDefinionMap(DyFormFormDefinition formDefinition) {
        Map<String, DyformSubformFormDefinition> map = new HashMap<String, DyformSubformFormDefinition>(0);
        List<DyformSubformFormDefinition> subformDefinitions = formDefinition.doGetSubformDefinitions();
        if (subformDefinitions != null) {
            for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
                map.put(subformDefinition.getFormUuid(), subformDefinition);
            }
        }
        return map;
    }

    /**
     * 如何描述该方法
     *
     * @param fieldName
     * @return
     */
    private static boolean isSubformField(String fieldName) {
        return fieldName.indexOf(":") != -1;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService
     */
    @Override
    public String getDictionXml(String id, String moduleId) {
        // String xml = dictionParser.paserDiction().asXML();
        // System.out.println(xml);
        // return
        // return
        // "<diction><categorys><category><name>分类一</name><code>01</code><parent></parent></category><category><name>分类二</name><code>02</code><parent></parent></category></categorys><rights><right><name>编辑文档</name><code>01</code><value>Edit</value><isDefault>1</isDefault></right><right><name>签署意见</name><code>02</code><value>Remark</value><isDefault>1</isDefault></right><right><name>允许提交</name><code>03</code><value>Submit</value><isDefault>1</isDefault></right><right><name>允许转办</name><code>04</code><value>Transfer</value><isDefault>0</isDefault></right><right><name>允许会签</name><code>05</code><value>AskAround</value><isDefault>0</isDefault></right><right><name>允许退回</name><code>06</code><value>Untread</value><isDefault>0</isDefault></right></rights><buttons><button><name>签署意见</name><code>01</code><value>Remark</value></button><button><name>提交</name><code>02</code><value>Submit</value></button><button><name>转办</name><code>03</code><value>Transfer</value></button><button><name>会签</name><code>04</code><value>AskAround</value></button><button><name>退回</name><code>05</code><value>Untread</value></button></buttons><formats><format><name>办理人</name><code>01</code><value>AllUser</value></format><format><name>默认格式</name><code>02</code><value>Default</value></format></formats><forms><form><name>表单一</name><id>One</id></form><form><name>表单二</name><id>Two</id></form></forms></diction>";
        return dictionParser.paserDiction(id, moduleId).asXML();
    }

    /**
     * 返回工作流数据字典
     *
     * @param uuid
     * @param moduleId
     * @return
     */
    @Override
    public FlowDictionary getFlowDictionary(String uuid, String moduleId) {
        return dictionParser.paserFlowDictionary(uuid, moduleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowXml(java.lang.String)
     */
    @Override
    public String getFlowXml(String uuid) throws Exception {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
        return getFlowXml(flowDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowXmlById(java.lang.String)
     */
    @Override
    public String getFlowXmlById(String id) throws Exception {
        FlowDefinition flowDefinition = flowDefinitionService.getById(id);
        return getFlowXml(flowDefinition);
    }

    @Override
    public String getFlowXml(FlowDefinition flowDefinition, FlowSchema oldflowSchema) {
        String xml = getFlowSchemaXml(oldflowSchema);
        Document document = FlowDefinitionParser.createDocument(xml);
        Element rootElement = document.getRootElement();
        org.dom4j.Node propertyNode = rootElement.selectSingleNode("property");
        if (propertyNode != null && propertyNode.selectSingleNode("categorySN") != null) {
            String categorySN = propertyNode.selectSingleNode("categorySN").getText();
            String flowCategory = flowDefinition.getCategory();
            if (StringUtils.isNotBlank(flowCategory) && !StringUtils.equals(categorySN, flowCategory)) {
                propertyNode.selectSingleNode("categorySN").setText(flowDefinition.getCategory());
            }
        }
        rootElement.addAttribute("name", flowDefinition.getName());
        rootElement.addAttribute("version", flowDefinition.getVersion() + "");
        rootElement.addAttribute("id", flowDefinition.getId());
        rootElement.addAttribute("code", flowDefinition.getCode());
        rootElement.addAttribute("uuid", flowDefinition.getUuid());
        // 同步流程JS模块配置
        String developJson = flowDefinition.getDevelopJson();
        if (StringUtils.isNotBlank(developJson)) {
            Map<String, String> values = JsonUtils.json2Object(developJson, Map.class);
            String jsModule = values.get(FlowConstant.CUSTOM_JS_MODULE);
            if (StringUtils.isNotBlank(jsModule)) {
                org.dom4j.Node jsModuleNode = propertyNode.selectSingleNode(FlowConstant.CUSTOM_JS_MODULE);
                if (jsModuleNode == null) {
                    Element propertyElement = (Element) propertyNode;
                    jsModuleNode = DocumentFactory.getInstance().createElement(FlowConstant.CUSTOM_JS_MODULE);
                    propertyElement.add(jsModuleNode);
                }
                if (StringUtils.isBlank(jsModuleNode.getText())
                        || !StringUtils.equals(jsModule, jsModuleNode.getText())) {
                    jsModuleNode.setText(jsModule);
                }
            }
        }
        return document.asXML();
    }

    /**
     * @param flowDefinition
     * @return
     * @throws IOException
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    private String getFlowXml(FlowDefinition flowDefinition) throws IOException, SQLException {
        String hql = "from FlowSchema o where o.uuid = :uuid and o.content is not null";
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", flowDefinition.getFlowSchemaUuid());
        List<FlowSchema> flowSchemas = flowSchemaService.listByHQL(hql, params);
        if (CollectionUtils.isEmpty(flowSchemas)) {
            return StringUtils.EMPTY;
        }
        FlowSchema flowSchema = flowSchemas.get(0);// flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
        String xml = getFlowSchemaXml(flowSchema);
        if (StringUtils.isBlank(xml)) {
            return StringUtils.EMPTY;
        }
        Document document = FlowDefinitionParser.createDocument(xml);
        Element rootElement = document.getRootElement();
        org.dom4j.Node propertyNode = rootElement.selectSingleNode("property");
        if (propertyNode != null && propertyNode.selectSingleNode("categorySN") != null) {
            String categorySN = propertyNode.selectSingleNode("categorySN").getText();
            String flowCategory = flowDefinition.getCategory();
            if (StringUtils.isNotBlank(flowCategory) && !StringUtils.equals(categorySN, flowCategory)) {
                propertyNode.selectSingleNode("categorySN").setText(flowDefinition.getCategory());
            }
        }
        // 逻辑删除流程定义会禁用流程
        if (propertyNode != null && propertyNode.selectSingleNode("isActive") != null) {
            if (flowDefinition.getEnabled()) {
                propertyNode.selectSingleNode("isActive").setText("1");
            } else {
                propertyNode.selectSingleNode("isActive").setText("0");
            }
        }
        rootElement.addAttribute("name", flowDefinition.getName());
        rootElement.addAttribute("version", flowDefinition.getVersion() + "");
        rootElement.addAttribute("id", flowDefinition.getId());
        rootElement.addAttribute("code", flowDefinition.getCode());
        rootElement.addAttribute("uuid", flowDefinition.getUuid());
        // 同步流程JS模块配置
        String developJson = flowDefinition.getDevelopJson();
        if (StringUtils.isNotBlank(developJson)) {
            Map<String, String> values = JsonUtils.json2Object(developJson, Map.class);
            String jsModule = values.get(FlowConstant.CUSTOM_JS_MODULE);
            if (StringUtils.isNotBlank(jsModule)) {
                org.dom4j.Node jsModuleNode = propertyNode.selectSingleNode(FlowConstant.CUSTOM_JS_MODULE);
                if (jsModuleNode == null) {
                    Element propertyElement = (Element) propertyNode;
                    jsModuleNode = DocumentFactory.getInstance().createElement(FlowConstant.CUSTOM_JS_MODULE);
                    propertyElement.add(jsModuleNode);
                }
                if (StringUtils.isBlank(jsModuleNode.getText())
                        || !StringUtils.equals(jsModule, jsModuleNode.getText())) {
                    jsModuleNode.setText(jsModule);
                }
            }
        }
        return document.asXML();
    }

    /**
     * 根据流程定义UUID，获取流程定义的XML内容
     *
     * @param uuid
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public String getFlowJson(String uuid) throws IOException, SQLException {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(uuid);
        // 确保缓存中有流程定义
        FlowElement flowElement = getFlowElement(flowDefinition);
        String definitionJson = flowElement.getDefinitionJson();
        if (StringUtils.isNotBlank(flowDefinition.getEqualFlowId())) {
            FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
            definitionJson = flowSchema.getDefinitionJsonAsString();
        }
        if (StringUtils.isBlank(definitionJson)) {
            String xml = getFlowXml(flowDefinition);
            flowElement = FlowDefinitionParser.parseFlow(xml);
            definitionJson = JsonUtils.object2Json(flowElement);
        }
        return definitionJson;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#checkFlowXml(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public boolean checkFlowXmlForUpdate(String xml) {
        return flowDefinitionService.checkFlowXmlForUpdate(xml);
    }

    /**
     * (non-Javadoc)
     *
     * @throws Exception
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#
     */
    @Override
    @Transactional
    public String saveFlowXml(String xml, Boolean pbNew, Boolean isCopy) throws Exception {
        FlowConfiguration flowConfiguration = flowDefinitionService.parseAndSave(xml, pbNew, isCopy);
        this.flowDefinitionService.flushSession();
        FlowDefinition flowDefinition = this.flowDefinitionService.getOne(flowConfiguration.getUuid());

        if (StringUtils.isNotBlank(flowDefinition.getModuleId())) {
            // 流程定义生成功能定义，并查看归属的模块是否已经添加到产品集成树下，如果没有则添加
            appFunctionFacade.synchronizeFunction2ModuleProductIntegrate(flowDefinition.getUuid(),
                    AppFunctionType.FlowDefinition, flowDefinition.getModuleId(), false);
        }

        // FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());// flowDefinition.getFlowSchema();
        // flowSchemaService.refreshEntity(flowSchema, LockOptions.UPGRADE);
        // String content = getFlowSchemaXml(flowSchema);
        // 删除缓存
        clearFlowElementCache(flowDefinition.getUuid());
        // 更新缓存
        updateFlowElementCache(flowConfiguration);
        return flowConfiguration.asXML();
    }

    /**
     * 根据流程定义的JSON内容，新建或更新流程定义
     *
     * @param json
     * @param pbNew
     * @param isCopy
     * @return
     */
    @Override
    @Transactional
    public String saveFlowJson(String json, Boolean pbNew, boolean isCopy) throws Exception {
        FlowConfiguration flowConfiguration = flowDefinitionService.parseAndSave(json, pbNew, isCopy);
        this.flowDefinitionService.flushSession();
        FlowDefinition flowDefinition = this.flowDefinitionService.getOne(flowConfiguration.getUuid());

        if (StringUtils.isNotBlank(flowDefinition.getModuleId())) {
            // 流程定义生成功能定义，并查看归属的模块是否已经添加到产品集成树下，如果没有则添加
            appFunctionFacade.synchronizeFunction2ModuleProductIntegrate(flowDefinition.getUuid(),
                    AppFunctionType.FlowDefinition, flowDefinition.getModuleId(), false);
        }

//        FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
//        String content = flowSchema.getDefinitionJsonAsString();
        // 删除缓存
        clearFlowElementCache(flowDefinition.getUuid());
        // 更新缓存
        updateFlowElementCache(flowConfiguration);
        return flowConfiguration.asJSON();
    }

    @Override
    @Transactional(readOnly = true)
    public FlowElement getFlowElement(FlowDefinition flowDefinition) {
        return getFlowElement(flowDefinition.getUuid(), flowDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowElementByFlowDefUuid(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public FlowElement getFlowElementByFlowDefUuid(String flowDefUuid) {
        return getFlowElement(flowDefUuid, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowElement(java.lang.String, com.wellsoft.pt.bpm.engine.entity.FlowDefinition)
     */
    @Transactional(readOnly = true)
    public FlowElement getFlowElement(String flowDefUuid, FlowDefinition defaultFlowDefinition) {
        FlowElement flow = null;
        Cache cache = cacheManager.getCache(FLOW_DEFINITION_CACHE_ID);
        String cacheKey = getFlowElementCacheKey(flowDefUuid);
        ValueWrapper valueWrapper = cache.get(cacheKey);
        if (valueWrapper != null) {
            flow = (FlowElement) valueWrapper.get();
        }
        if (flow != null) {
            try {
                // 验证jedis缓存的流程定义
                if (cache instanceof JedisCache) {
                    flow.getTasks().forEach(t -> t.getId());
                }
                return flow;
            } catch (Exception e) {
            }
        }

        FlowDefinition flowDefinition = defaultFlowDefinition;
        FlowSchema flowSchema = null;
        if (flowDefinition == null) {
            flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        }
        if (StringUtils.isNotBlank(flowDefinition.getEqualFlowId())) {
            FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);
            FlowDefinition equalflowDefinition = flowService.getFlowDefinitionById(flowDefinition.getEqualFlowId());
            if (equalflowDefinition != null) {
                flowSchema = flowSchemaService.getOne(equalflowDefinition.getFlowSchemaUuid());
                FlowElement equalFlow = FlowDefinitionParser.parseFlow(flowSchema);
                flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
                FlowElement rawFlow = FlowDefinitionParser.parseFlow(flowSchema);
                flow = fixedEqualFlow(equalFlow, rawFlow);
            }
        } else {
            flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
            if (flowSchema != null) {
                flow = FlowDefinitionParser.parseFlow(flowSchema);
            }
        }

        // 放入缓存
        if (flow != null) {
            cache.put(cacheKey, flow);
        }
        return flow;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#clearFlowElementCache(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void clearFlowElementCache(String flowDefUuid) {
        Cache cache = cacheManager.getCache(FLOW_DEFINITION_CACHE_ID);
        String cacheKey = getFlowElementCacheKey(flowDefUuid);
        cache.evict(cacheKey);
    }

    /**
     * @param flowDefUuid
     * @return
     */
    private String getFlowElementCacheKey(String flowDefUuid) {
        return "FlowElement_" + flowDefUuid;
    }

    /**
     * @param equalFlow
     * @param rawFlow
     */
    private FlowElement fixedEqualFlow(FlowElement equalFlow, FlowElement rawFlow) {
        equalFlow.setName(rawFlow.getName());
        equalFlow.setId(rawFlow.getId());
        equalFlow.setCode(rawFlow.getCode());
        equalFlow.setUuid(rawFlow.getUuid());
        equalFlow.setVersion(rawFlow.getVersion());

        equalFlow.setProperty(rawFlow.getProperty());
        equalFlow.setTimers(rawFlow.getTimers());

        return equalFlow;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#copy(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public String copy(String uuid, String newFlowDefName, String newFlowDefId) {
        String newFlowDefUuid = null;
        try {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(uuid);
            FlowElement flowElement = flowDelegate.getFlow();
            if (flowElement.isXmlDefinition()) {
                String flowDefXml = getFlowXml(uuid);
                Document document = FlowDefinitionParser.createDocument(flowDefXml);
                Element rootElement = document.getRootElement();
                rootElement.remove(rootElement.attribute(FlowDefConstants.FLOW_UUID));
                rootElement.attribute(FlowDefConstants.FLOW_NAME).setValue(newFlowDefName);
                rootElement.attribute(FlowDefConstants.FLOW_ID).setValue(newFlowDefId);
                String newFlowDefXml = saveFlowXml(document.asXML(), false, true);
                newFlowDefUuid = FlowDefinitionParser.createDocument(newFlowDefXml).getRootElement()
                        .attributeValue(FlowDefConstants.FLOW_UUID);
            } else {
                flowElement = JsonUtils.json2Object(JsonUtils.object2Json(flowElement), FlowElement.class);
                flowElement.setUuid(null);
                flowElement.setName(newFlowDefName);
                flowElement.setId(newFlowDefId);
                String newFlowDefJson = saveFlowJson(JsonUtils.object2Json(flowElement), false, true);
                newFlowDefUuid = JsonUtils.json2Object(newFlowDefJson, FlowElement.class).getUuid();
            }
            // 复制流程意见校验设置
            copyFlowOpinionCheckSet(uuid, newFlowDefId);
        } catch (Exception e) {
            throw new WorkFlowException(e.getMessage());
        }
        // task:6441 开发-主导-流程定义修改日志
        FlowDefinition oldFlowDefinition = flowDefinitionService.getOne(uuid);
        FlowDefinition newFlowDefinition = flowDefinitionService.getOne(newFlowDefUuid);
        flowDefinitionService.saveLogManageOperation(newFlowDefinition, oldFlowDefinition,
                LogManageOperationEnum.copyFlow);
        return newFlowDefUuid;
    }

    /**
     * @param flowSchema
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public String getFlowSchemaXml(FlowSchema flowSchema) {
        return flowSchema.getContentAsString();//IOUtils.toString(flowSchema.getContent().getCharacterStream());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowTreeXml()
     */
    @Override
    public String getFlowTreeXml() {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_FLOWS);
        // category
        for (FlowCategory category : categoryService.getTopLevel()) {
            // Element unitElement = root.addElement(NODE_CATEGORY);
            iteratorTree(root, category);
        }
        return document.asXML();
    }

    /**
     * @param element
     * @param category
     */
    private void iteratorTree(Element element, FlowCategory category) {
        if (category != null) {
            // 处理该category
            Element unitElement = element.addElement(NODE_CATEGORY);
            parseCategoryBean(unitElement, category);
            for (FlowCategory children : category.getChildren()) {
                // 进入递归处理
                iteratorTree(unitElement, children);
            }
        }
    }

    /**
     * @param unitElement
     * @param category
     */
    private void parseCategoryBean(Element unitElement, FlowCategory category) {
        unitElement.addAttribute(NODE_CODE, category.getCode());
        unitElement.addAttribute(NODE_NAME, category.getName());
        List<FlowDefinition> flowDefinitions = flowDefinitionService.getByCategory(category.getCode());
        for (FlowDefinition flowDefinition : flowDefinitions) {
            Element flowElement = unitElement.addElement(NODE_FLOW);
            flowElement.addAttribute(NODE_FLOW_ID, NodeUtil.getString(flowDefinition.getId()));
            flowElement.addAttribute(NODE_FLOW_VERSION, NodeUtil.getString(flowDefinition.getVersion() + ""));
            flowElement.addText(NodeUtil.getString(flowDefinition.getName()));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFlowTree(String treeNodeId, String excludeFlowId) {
        TreeNode treeNode = new TreeNode();
        // category
        if (TreeNode.ROOT_ID.equals(treeNodeId)) {
            traverseCategoryTree(treeNode, categoryService.getTopLevel(), null);
        } else {
            traverseTree(treeNode, treeNodeId, excludeFlowId);
        }
        return treeNode.getChildren();
    }

    @Override
    public List<TreeNode> getFlowMessageTemplateType() {
        TreeNode treeNode = new TreeNode();
        Set<String> keys = WorkFlowMessageTemplate.FLOW_MSG_TEMPLATE_CATEGORY.keySet();
        for (String k : keys) {
            TreeNode node = new TreeNode();
            node.setId(k);
            node.setName(k);
            node.setNocheck(true);
            node.setOpen(true);
            List<WorkFlowMessageTemplate> templates = WorkFlowMessageTemplate.FLOW_MSG_TEMPLATE_CATEGORY.get(k);
            for (WorkFlowMessageTemplate t : templates) {
                TreeNode tnode = new TreeNode();
                tnode.setId(t.getType());
                tnode.setName(t.getName());
                node.getChildren().add(tnode);
            }
            treeNode.getChildren().add(node);
        }
        return treeNode.getChildren();
    }

    @Override
    public Select2GroupData getSelectFlowMessageTemplateType() {
        Select2GroupData groupData = new Select2GroupData();
        Set<String> keys = WorkFlowMessageTemplate.FLOW_MSG_TEMPLATE_CATEGORY.keySet();
        for (String k : keys) {
            List<WorkFlowMessageTemplate> templates = WorkFlowMessageTemplate.FLOW_MSG_TEMPLATE_CATEGORY.get(k);
            for (WorkFlowMessageTemplate t : templates) {
                groupData.addResultData(k, new Select2DataBean(t.getType(), t.getName()));
            }
        }
        return groupData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getAllFlowTree(java.lang.String)
     */
    @Override
    public List<TreeNode> getAllFlowTree(String treeNodeId) {
        return getAllFlowAsCategoryTree();
    }

    /**
     * 根据流程定义ID，获取最高版本的流程定义的环节map<id, name>
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowTasks(java.lang.String)
     */
    @Override
    public List<KeyValuePair> getFlowTasks(String flowDefId) {
        List<KeyValuePair> keyValuePairs = new ArrayList<KeyValuePair>();
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        if (flowDefinition == null) {
            return keyValuePairs;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            // 忽略掉子结点、机器节点
            if (TaskNodeType.SubTask.getValue().equals(node.getType()) || TaskNodeType.ScriptTask.getValue().equals(node.getType())) {
                continue;
            }
            KeyValuePair keyValuePair = new KeyValuePair();
            keyValuePair.put("id", node.getId());
            keyValuePair.put("name", node.getName());
            keyValuePair.put("type", node.getType());
            keyValuePairs.add(keyValuePair);
        }
        return keyValuePairs;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#isAutoSubmitForkTask(java.lang.String)
     */
    @Override
    public boolean isAutoSubmitForkTask(String flowDefId) {
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        if (flowDefinition == null) {
            return false;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        TaskNode node = (TaskNode) flowDelegate.getTaskNode(flowDelegate.getStartNode().getToID());
        if (node == null) {
            return false;
        }
        if (CollectionUtils.size(node.getToIDs()) > 1) {
            return true;
        }
        return false;
    }

    /**
     * 根据流程定义ID，获取最高版本的流程定义的子流程环节map<id, name>
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowTasks(java.lang.String)
     */
    @Override
    public List<KeyValuePair> getFlowSubTasks(String flowDefId) {
        List<KeyValuePair> keyValuePairs = new ArrayList<KeyValuePair>();
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        if (flowDefinition == null) {
            return keyValuePairs;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            // 取子结点
            if (!"2".equals(node.getType())) {
                continue;
            }
            KeyValuePair keyValuePair = new KeyValuePair();
            keyValuePair.put("id", node.getId());
            keyValuePair.put("name", node.getName());
            keyValuePairs.add(keyValuePair);
        }
        return keyValuePairs;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowTaskTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFlowTaskTree(String treeNodeId, String flowDefUuid) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (StringUtils.isBlank(flowDefUuid)) {
            return treeNodes;
        }
        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        if (flowDefinition == null) {
            return treeNodes;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            if (String.valueOf(1).equals(node.getType())) {
                continue;
            }
            TreeNode treeNode = new TreeNode();
            treeNode.setId(node.getId());
            treeNode.setName(node.getName());
            treeNode.setData(node.getId());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowTaskByTaskIds(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getFlowTaskByTaskIds(String flowDefUuid, String taskIds) {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        List<String> labes = Lists.newArrayList();
        List<String> values = Lists.newArrayList();
        if (flowDefinition != null && StringUtils.isNotBlank(taskIds)) {
            FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
            List<String> taskIdList = Arrays.asList(StringUtils.split(taskIds, Separator.SEMICOLON.getValue()));
            for (String taskId : taskIdList) {
                Node node = flowDelegate.getTaskNode(taskId);
                if (node != null && StringUtils.isNotBlank(node.getName())) {
                    labes.add(node.getName());
                    values.add(node.getId());
                }
            }
        }
        QueryItem item = new QueryItem();
        item.put("label", StringUtils.join(labes, Separator.SEMICOLON.getValue()));
        item.put("value", StringUtils.join(values, Separator.SEMICOLON.getValue()));
        return item;
    }

    /**
     * 根据流程定义ID，获取最高版本的流程定义的流向map<id, name>
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowDirections(java.lang.String)
     */
    @Override
    public List<KeyValuePair> getFlowDirections(String flowDefId) {
        List<KeyValuePair> keyValuePairs = new ArrayList<KeyValuePair>();
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        if (flowDefinition == null) {
            return keyValuePairs;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        List<Direction> nodes = flowDelegate.getAllDirections();
        for (Direction node : nodes) {
            KeyValuePair keyValuePair = new KeyValuePair();
            keyValuePair.put("id", node.getId());
            keyValuePair.put("name", node.getName());
            keyValuePair.put("toID", node.getToID());
            keyValuePair.put("formID", node.getFromID());
            keyValuePairs.add(keyValuePair);
        }
        return keyValuePairs;
    }

    /**
     * 遍历流程分类树
     *
     * @param treeNode
     * @param categories
     * @param categoryNodes
     */
    private void traverseCategoryTree(TreeNode treeNode, Collection<FlowCategory> categories, Map<String, TreeNode> categoryNodes) {
        for (FlowCategory category : categories) {
            TreeNode categoryNode = new TreeNode();
            categoryNode.setId(category.getUuid());
            categoryNode.setName(category.getName());
            categoryNode.setNocheck(true);
            categoryNode.setIsParent(true);
            treeNode.getChildren().add(categoryNode);
            if (categoryNodes != null) {
                categoryNodes.put(category.getUuid(), categoryNode);
            }
            traverseCategoryTree(categoryNode, category.getChildren(), categoryNodes);
        }
    }

    /**
     * @param treeNode
     * @param category
     */
    private void traverseTree(TreeNode treeNode, String category, String excludeFlowId) {
        // 处理该category
        String hql = QUERY_FLOW_DEF_BY_CATEGORY;
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("category", category);
        params.put("excludeFlowId", excludeFlowId);
        List<QueryItem> queryItems = this.flowDefinitionService.listQueryItemByHQL(hql, params, null);
        for (QueryItem queryItem : queryItems) {
            TreeNode node = new TreeNode();
            String id = queryItem.get("id") == null ? "" : queryItem.get("id").toString();
            String name = queryItem.get("name") == null ? "" : queryItem.get("name").toString();
            node.setId(id);
            node.setName(name);
            node.setData(id);
            treeNode.getChildren().add(node);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#
     */
    @Override
    public List<DataItem> getVformsByPformUuid(String formUuid) {
        List<DataItem> taskForms = Lists.newArrayList();
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        List<DyFormFormDefinition> dyFormFormDefinitions = dyFormFacade.getFormDefinitionsByTblName(dyFormFormDefinition.getTableName());
        dyFormFormDefinitions = dyFormFormDefinitions.stream().filter(formDefinition ->
                        DyformTypeEnum.V.getValue().equals(formDefinition.getFormType())
                                && !StringUtils.equals(dyFormFormDefinition.getUuid(), formDefinition.getUuid()))
                .collect(Collectors.toList());
        for (DyFormFormDefinition formDefinition : dyFormFormDefinitions) {
            DataItem dataItem = new DataItem();
            dataItem.setLabel(formDefinition.getName());
            dataItem.setValue(formDefinition.getUuid());
            taskForms.add(dataItem);
        }
        return taskForms;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormBlocks(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFormBlocks(String treeNodeId, String formUuid) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (StringUtils.isBlank(formUuid)) {
            return treeNodes;
        }
        List<DyformBlock> dyformBlocks = dyFormFacade.getBlocksByformUuid(formUuid);
        for (DyformBlock dyformBlock : dyformBlocks) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(dyformBlock.getBlockCode());
            treeNode.setName(dyformBlock.getBlockTitle());
            treeNode.setData(dyformBlock.getBlockCode());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormBlocksByFlowDefId(java.lang.String)
     */
    @Override
    public List<TreeNode> getFormBlocksByFlowDefId(String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return Collections.emptyList();
        }
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        if (flowDefinition == null) {
            return Collections.emptyList();
        }
        return getFormBlocks(TreeNode.ROOT_ID, flowDefinition.getFormUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getBlockByBlockCodes(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getBlockByBlockCodes(String formUuid, String blockCodes) {
        QueryItem item = new QueryItem();
        List<DyformBlock> dyformBlocks = dyFormFacade.getBlocksByformUuid(formUuid);
        String[] fieldNameArray = blockCodes.split(Separator.SEMICOLON.getValue());
        for (DyformBlock formBlock : dyformBlocks) {
            for (String string : fieldNameArray) {
                if (StringUtils.equals(string, formBlock.getBlockCode())) {
                    String label = formBlock.getBlockTitle();
                    String value = formBlock.getBlockCode();
                    if (item.containsKey("label")) {
                        item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + label);
                        item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + value);
                    } else {
                        item.put("label", label);
                        item.put("value", value);
                    }
                }
            }
        }
        return item;
    }

    @Override
    public List<TreeNode> getFormFields(String treeNodeId, String formUuid, String includeInputModes) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (StringUtils.isBlank(formUuid) || "-1".equals(formUuid)) {
            return treeNodes;
        }
        DyFormFormDefinition dyFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        if (dyFormDefinition == null) {
            throw new RuntimeException("流程使用的表单不存在，请重新设置！");
        }
        String[] inputModes = null;
        if (StringUtils.isNotBlank(includeInputModes)) {
            inputModes = includeInputModes.split(Separator.SEMICOLON.getValue());
        }
        // 主表字段
        List<DyformFieldDefinition> fieldDefinitions = dyFormDefinition.doGetFieldDefintions();
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            if (inputModes != null && !ArrayUtils.contains(inputModes, fieldDefinition.getInputMode())) {
                continue;
            }
            TreeNode treeNode = new TreeNode();
            treeNode.setId(fieldDefinition.getName());
            treeNode.setName(fieldDefinition.getDisplayName());
            treeNode.setData(fieldDefinition.getName());
            treeNode.setType(fieldDefinition.getInputMode());
            treeNodes.add(treeNode);
        }

        // 从表信息
        List<DyformSubformFormDefinition> subformDefinitions = dyFormFacade.getSubformDefinitions(formUuid);
        for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
            String displayName = subformDefinition.getDisplayName();
            String subformUuid = subformDefinition.getFormUuid();
            TreeNode treeNode = new TreeNode();
            treeNode.setId(subformUuid);
            treeNode.setName(displayName);
            treeNode.setData(subformUuid);
            treeNode.setNocheck(true);
            treeNode.setIsParent(true);
            if (inputModes == null) {
                String allBtnId = subformUuid + ":" + TaskForm.BTN_ALL_PREFIX + subformUuid;
                TreeNode child = new TreeNode();
                child.setId(allBtnId);
                child.setName("添加删除行");
                child.setData(allBtnId);
                treeNode.getChildren().add(child);

                String addBtnId = subformUuid + ":" + TaskForm.BTN_ADD_PREFIX + subformUuid;
                TreeNode childAdd = new TreeNode();
                childAdd.setId(addBtnId);
                childAdd.setName("添加行");
                childAdd.setData(addBtnId);
                treeNode.getChildren().add(childAdd);

                String delBtnId = subformUuid + ":" + TaskForm.BTN_DEL_PREFIX + subformUuid;
                TreeNode childDel = new TreeNode();
                childDel.setId(delBtnId);
                childDel.setName("删除行");
                childDel.setData(delBtnId);
                treeNode.getChildren().add(childDel);

                String impBtnId = subformUuid + ":" + TaskForm.BTN_IMP_PREFIX + subformUuid;
                TreeNode childImp = new TreeNode();
                childImp.setId(impBtnId);
                childImp.setName("导入");
                childImp.setData(impBtnId);
                treeNode.getChildren().add(childImp);

                String expBtnId = subformUuid + ":" + TaskForm.BTN_EXP_PREFIX + subformUuid;
                TreeNode childExp = new TreeNode();
                childExp.setId(expBtnId);
                childExp.setName("导出");
                childExp.setData(expBtnId);
                treeNode.getChildren().add(childExp);
            }

            // 从表字段
            List<DyformSubformFieldDefinition> subformFieldDefinitions = subformDefinition.getSubformFieldDefinitions();
            for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitions) {
                if (inputModes != null && !ArrayUtils.contains(inputModes, subformFieldDefinition.getInputMode())) {
                    continue;
                }
                String fieldDisplayName = subformFieldDefinition.getDisplayName();
                String fieldName = subformFieldDefinition.getName();
                TreeNode childNode = new TreeNode();
                String id = subformUuid + ":" + fieldName;
                childNode.setId(id);
                childNode.setName(fieldDisplayName);
                childNode.setData(id);
                childNode.setType(subformFieldDefinition.getInputMode());
                treeNode.getChildren().add(childNode);
            }
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormFields(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFormFields(String treeNodeId, String formUuid) {
        return this.getFormFields(treeNodeId, formUuid, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormFieldsAsSelect2GroupData(java.lang.String)
     */
    @Override
    public Select2GroupData getFormFieldsAsSelect2GroupData(String formUuid) {
        DyFormFormDefinition dyFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        if (dyFormDefinition == null) {
            throw new RuntimeException("流程使用的表单不存在，请重新设置！");
        }

        ArrayList<Select2GroupBean> results = Lists.newArrayList();
        Select2GroupBean groupBean = new Select2GroupBean();
        groupBean.setText(dyFormDefinition.getName());
        // 主表字段
        List<DyformFieldDefinition> fieldDefinitions = dyFormDefinition.doGetFieldDefintions();
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            Select2DataBean select2DataBean = new Select2DataBean(fieldDefinition.getName(),
                    fieldDefinition.getDisplayName());
            groupBean.getChildren().add(select2DataBean);
        }
        results.add(groupBean);

        // 从表信息
        List<DyformSubformFormDefinition> subformDefinitions = dyFormFacade.getSubformDefinitions(formUuid);
        for (DyformSubformFormDefinition subformDefinition : subformDefinitions) {
            String displayName = subformDefinition.getDisplayName();
            String subformId = subformDefinition.getOuterId();
            Select2GroupBean subformGroupBean = new Select2GroupBean();
            subformGroupBean.setText(displayName);

            // 从表字段
            List<DyformSubformFieldDefinition> subformFieldDefinitions = subformDefinition.getSubformFieldDefinitions();
            for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitions) {
                Select2DataBean select2DataBean = new Select2DataBean(
                        "#" + subformId + "#" + subformFieldDefinition.getName(),
                        subformFieldDefinition.getDisplayName());
                subformGroupBean.getChildren().add(select2DataBean);
            }
            results.add(subformGroupBean);
        }

        Select2GroupData select2GroupData = new Select2GroupData();
        select2GroupData.setResults(results);
        return select2GroupData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormFieldsByFormId(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFormFieldsByFormId(String treeNodeId, String formId) {
        return getFormFields("-1", dyFormFacade.getFormUuidById(formId));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormFieldsByFlowDefId(java.lang.String)
     */
    @Override
    public List<TreeNode> getFormFieldsByFlowDefId(String flowDefId) {
        if (StringUtils.isBlank(flowDefId)) {
            return Lists.newArrayList();
        }
        FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
        return getFormFields("-1", flowDefinition.getFormUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getSubForms(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getSubForms(String treeNodeId, String formUuid) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (StringUtils.isBlank(formUuid)) {
            return treeNodes;
        }
        List<DyformSubformFormDefinition> subForms = dyFormFacade.getSubformDefinitions(formUuid);
        for (DyformSubformFormDefinition subformDefinition : subForms) {
            TreeNode treeNode = new TreeNode();
            /* modified by huanglinchuan 2014.10.23 begin */
            treeNode.setId(subformDefinition.getFormUuid());
            /* modified by huanglinchuan 2014.10.23 end */
            treeNode.setName(subformDefinition.getDisplayName());
            treeNode.setData(subformDefinition.getOuterId());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormFieldByFieldNames(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getFormFieldByFieldNames(String formUuid, String fieldNames) {
        QueryItem item = new QueryItem();
        // List<DyformFieldDefinition> fieldDefinitions =
        // dytableApiFacade.getFieldByForm(formUuid);
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formUuid);
        Map<String, DyformSubformFormDefinition> subformDefinionMap = getSubformDefinionMap(formDefinition);
        List<DyformFieldDefinition> fieldDefinitions = formDefinition.doGetFieldDefintions();
        String[] fieldNameArray = fieldNames.split(Separator.SEMICOLON.getValue());
        for (String fieldName : fieldNameArray) {
            if (!isSubformField(fieldName)) {
                for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
                    if (StringUtils.equals(fieldName, fieldDefinition.getName())) {
                        String label = fieldDefinition.getDisplayName();
                        String value = fieldDefinition.getName();
                        if (item.containsKey("label")) {
                            item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + label);
                            item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + value);
                        } else {
                            item.put("label", label);
                            item.put("value", value);
                        }
                    }
                }
            } else {
                String[] fieldInfos = StringUtils.split(fieldName, ":");
                String subformUuid = fieldInfos[0];
                String subformFieldName = fieldInfos[1];
                if (subformDefinionMap.containsKey(subformUuid)) {
                    DyformSubformFormDefinition subformDefinition = subformDefinionMap.get(subformUuid);
                    String subformName = subformDefinition.getDisplayName();
                    // 从表按钮
                    if (TaskForm.isSubformAllBtnField(fieldName) || TaskForm.isSubformAddBtnField(fieldName)
                            || TaskForm.isSubformDelBtnField(fieldName) || TaskForm.isSubformImpBtnField(fieldName)
                            || TaskForm.isSubformAddBtnField(fieldName) || TaskForm.isSubformExpBtnField(fieldName)) {
                        String label = subformName + "/" + "添加删除行";
                        if (TaskForm.isSubformAddBtnField(fieldName)) {
                            label = subformName + "/" + "添加行";
                        } else if (TaskForm.isSubformDelBtnField(fieldName)) {
                            label = subformName + "/" + "删除行";
                        } else if (TaskForm.isSubformImpBtnField(fieldName)) {
                            label = subformName + "/" + "导入";
                        } else if (TaskForm.isSubformExpBtnField(fieldName)) {
                            label = subformName + "/" + "导出";
                        }
                        String value = fieldName;
                        if (item.containsKey("label")) {
                            item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + label);
                            item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + value);
                        } else {
                            item.put("label", label);
                            item.put("value", value);
                        }
                    } else {
                        // 从表字段
                        List<DyformSubformFieldDefinition> subformFieldDefinitions = subformDefinionMap.get(subformUuid)
                                .getSubformFieldDefinitions();
                        for (DyformSubformFieldDefinition subformFieldDefinition : subformFieldDefinitions) {
                            if (StringUtils.equals(subformFieldName, subformFieldDefinition.getName())) {
                                String label = subformName + "/" + subformFieldDefinition.getDisplayName();
                                String value = subformUuid + ":" + subformFieldDefinition.getName();
                                if (item.containsKey("label")) {
                                    item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + label);
                                    item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + value);
                                } else {
                                    item.put("label", label);
                                    item.put("value", value);
                                }
                            }
                        }
                    }
                }
            }
        }
        return item;
    }

    @Override
    public Select2QueryData getFormFieldSelections(Select2QueryInfo queryInfo) {
        String formuuid = queryInfo.getOtherParams("formUuid", "");
        if (StringUtils.isBlank(formuuid)) {
            return new Select2QueryData();
        }
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formuuid);
        List<DyformFieldDefinition> fieldDefinitions = formDefinition.doGetFieldDefintions();
        String inputMode = queryInfo.getOtherParams("inputMode", "");
        String[] inputModes = null;
        if (StringUtils.isNotBlank(inputMode)) {
            inputModes = inputMode.split(",");
        }
        Select2QueryData select2QueryData = new Select2QueryData();
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            if (inputModes != null && ArrayUtils.contains(inputModes, fieldDefinition.getInputMode())) {
                select2QueryData.addResultData(
                        new Select2DataBean(fieldDefinition.getFieldName(), fieldDefinition.getDisplayName()));
            }
        }
        return select2QueryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getBizRoles(java.lang.String)
     */
    @Override
    public List<TreeNode> getBizRoles(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<CdDataDictionaryItemDto> dataDictionaries = basicDataApiFacade.getDataDictionariesByType("BUSINESS_MANAGEMENT");
        for (CdDataDictionaryItemDto dataDictionary : dataDictionaries) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(Objects.toString(dataDictionary.getUuid(), StringUtils.EMPTY));
            treeNode.setName(dataDictionary.getLabel());
            treeNode.setData(dataDictionary.getValue());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getBusinessTypes()
     */
    @Override
    public List<DataItem> getBusinessTypes() {
        List<DataItem> businessTypes = Lists.newArrayList();
        List<OrganizationDto> organizationDtos = workflowOrgService.listOrganizationBySystem(null);
        for (OrganizationDto organizationDto : organizationDtos) {
            DataItem item = new DataItem();
            item.setLabel(organizationDto.getName());
            item.setValue(organizationDto.getId());
            businessTypes.add(item);
        }
//        List<BusinessCategoryDto> categoryDtos = businessFacadeService.getBusinessCategoryListBySystemUuid();
//        for (BusinessCategoryDto categoryDto : categoryDtos) {
//            DataItem item = new DataItem();
//            item.setLabel(categoryDto.getName());
//            item.setValue(categoryDto.getUuid());
//            businessTypes.add(item);
//        }
        return businessTypes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getBusinessRoles(java.lang.String)
     */
    @Override
    public List<DataItem> getBusinessRoles(String businessType) {
        List<DataItem> businessRoles = Lists.newArrayList();
        List<OrgRoleDto> orgRoleDtos = workflowOrgService.listOrgRoleByOrgId(businessType);
        for (OrgRoleDto orgRoleDto : orgRoleDtos) {
            DataItem item = new DataItem();
            item.setLabel(orgRoleDto.getName());
            item.setValue(orgRoleDto.getId());
            businessRoles.add(item);
        }
//        List<BusinessRoleDto> businessRoleDtos = businessFacadeService.getBusinessRoleByCategoryUuid(businessType);
//        for (BusinessRoleDto businessRoleDto : businessRoleDtos) {
//            DataItem item = new DataItem();
//            item.setLabel(businessRoleDto.getName());
//            item.setValue(businessRoleDto.getUuid());
//            businessRoles.add(item);
//        }
        return businessRoles;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getSerialNumbers(java.lang.String)
     */
    @Override
    public List<TreeNode> getSerialNumbers(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        // 新版流水号定义
        List<SnSerialNumberDefinitionDto> dtos = basicDataApiFacade.getAllSerialNumberDefinitions();
        for (SnSerialNumberDefinitionDto serialNumber : dtos) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(serialNumber.getUuid());
            treeNode.setName(serialNumber.getName());
            treeNode.setData(serialNumber.getId());
            treeNodes.add(treeNode);
        }

//        List<SerialNumber> serialNumbers = basicDataApiFacade.getAllSerialNumbers();
//        for (SerialNumber serialNumber : serialNumbers) {
//            TreeNode treeNode = new TreeNode();
//            treeNode.setId(serialNumber.getUuid());
//            treeNode.setName(serialNumber.getName());
//            treeNode.setData(serialNumber.getId());
//            treeNodes.add(treeNode);
//        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getPrintTemplates(java.lang.String)
     */
    @Override
    public List<TreeNode> getPrintTemplates(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<PrintTemplate> printTemplates = basicDataApiFacade.getAllPrintTemplates();
        for (PrintTemplate printTemplate : printTemplates) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(printTemplate.getUuid());
            treeNode.setName(
                    printTemplate.getName() + " (" + PrintUtils.versionFormat.format(printTemplate.getVersion()) + ")");
            treeNode.setData(printTemplate.getId());
            // treeNode.setData(PrintUtils.PRINT_UUID_SCHEMA + printTemplate.getUuid());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getMessageTemplates()
     */
    @Override
    public List<QueryItem> getMessageTemplates() {
        List<MessageTemplate> templates = messageTemplateApiFacade.getAll("code", false);
        List<QueryItem> items = new ArrayList<QueryItem>();
        for (MessageTemplate template : templates) {
            QueryItem item = new QueryItem();
            item.put("name", template.getName());
            item.put("id", template.getId());
            items.add(item);
        }
        return items;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowListeners(java.lang.String)
     */
    @Override
    public List<TreeNode> getFlowListeners(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<Listener> flowListeners = filterListerner(FlowListener.class);
        for (Listener listener : flowListeners) {
            treeNodes.add(getListenerTreeNode(listener));
        }
        return treeNodes;
    }

    /**
     * @param cls
     * @return
     */
    private List<Listener> filterListerner(Class<?> cls) {
        List<Listener> filterListerners = new ArrayList<Listener>();
        for (Listener listener : listeners) {
            if (cls.isAssignableFrom(listener.getClass()) && StringUtils.isNotBlank(listener.getName())) {
                filterListerners.add(listener);
            }
        }
        Collections.sort(filterListerners, new Comparator<Listener>() {
            @Override
            public int compare(Listener o1, Listener o2) {
                if (o1.getOrder() > o2.getOrder()) {
                    return 1;
                } else if (o1.getOrder() == o2.getOrder()) {
                    return 0;
                }
                return -1;
            }
        });
        return filterListerners;
    }

    /**
     * @param listener
     * @return
     */
    private TreeNode getListenerTreeNode(Listener listener) {
        TreeNode treeNode = new TreeNode();
        String simpleName = ClassUtils.getUserClass(listener.getClass()).getSimpleName();
        simpleName = StringUtils.uncapitalize(simpleName);
        treeNode.setId(simpleName);
        treeNode.setName(listener.getName());
        treeNode.setData(simpleName);
        return treeNode;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getTaskListeners(java.lang.String)
     */
    @Override
    public List<TreeNode> getTaskListeners(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<Listener> taskListeners = filterListerner(TaskListener.class);
        for (Listener listener : taskListeners) {
            // if (!(listener instanceof InternalTaskListener)) {
            treeNodes.add(getListenerTreeNode(listener));
            // }
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getDirectionListeners(java.lang.String)
     */
    @Override
    public List<TreeNode> getDirectionListeners(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<Listener> directionListeners = filterListerner(DirectionListener.class);
        for (Listener listener : directionListeners) {
            treeNodes.add(getListenerTreeNode(listener));
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getTimerListeners(java.lang.String)
     */
    @Override
    public List<TreeNode> getTimerListeners(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<Listener> timerListeners = filterListerner(TimerListener.class);
        for (Listener listener : timerListeners) {
            if (listener instanceof InternalListener) {
                continue;
            }
            treeNodes.add(getListenerTreeNode(listener));
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowAccessPermissionProvider(java.lang.String)
     */
    @Override
    public List<TreeNode> getFlowAccessPermissionProvider(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (CollectionUtils.isEmpty(flowAccessPermissionProviders)) {
            return treeNodes;
        }
        for (FlowAccessPermissionProvider provider : flowAccessPermissionProviders) {
            TreeNode treeNode = new TreeNode();
            String simpleName = ClassUtils.getUserClass(provider.getClass()).getSimpleName();
            simpleName = StringUtils.uncapitalize(simpleName);
            treeNode.setId(simpleName);
            treeNode.setName(provider.getName());
            treeNode.setData(simpleName);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFlowKeyValuePair(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getFlowKeyValuePair(String businessType, String flowDefId) {
        QueryItem item = new QueryItem();
        if (StringUtils.isNotBlank(flowDefId)) {
            String[] flowDefIds = flowDefId.split(Separator.SEMICOLON.getValue());
            for (String id : flowDefIds) {
                FlowDefinition example = new FlowDefinition();
                example.setId(id);
                List<FlowDefinition> definitions = this.flowDefinitionService.findByExample(example);
                if (definitions.size() > 0) {
                    FlowDefinition definition = definitions.get(0);
                    String categoryId = definition.getCategory();
                    FlowCategory category = categoryService.getOne(categoryId);
                    String categoryName = categoryId;
                    if (category != null) {
                        categoryName = category.getName();
                        String label = categoryName + Separator.SLASH.getValue() + definition.getName();
                        if (item.get("value") == null) {
                            item.put("label", label);
                            item.put("value", id);
                        } else {
                            item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + label);
                            item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + id);
                        }
                    } else {
                        item.put("label", definition.getName());
                        item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + id);
                    }
                } else {
                    item.put("label", id);
                    item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + id);
                }
            }
        }
        return item;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getAllUuids()
     */
    @Override
    public List<String> getAllUuids() {
        return flowDefinitionService.getAllUuids();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#
     */
    @Override
    public List<TreeNode> getAllFlowAsCategoryTree() {
        return this.buildFlowDefinitionCategoryTree(false);
    }

    private List<TreeNode> buildFlowDefinitionCategoryTree(boolean isMobile) {
        List<FlowCategory> categories = categoryService.getAllBySystemUnitIds();
        // 扩大
        PagingInfo pagingInfo = new PagingInfo(0, 2048, false);
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setPagingInfo(pagingInfo);
        queryInfo.setOrderBy("code asc");
        // flowDefinitionDao.query(QUERY_FLOW_DEF_WHERE_HQL,null,FlowDefinition.class);
        List<FlowDefinition> flowDefinitions = workService.query(queryInfo);
        Map<String, List<FlowDefinition>> flowDefMap = new HashMap<String, List<FlowDefinition>>();
        for (FlowDefinition flowDefinition : flowDefinitions) {
            String categoryCode = flowDefinition.getCategory();
            if (StringUtils.isBlank(categoryCode) || !flowDefinition.getEnabled()) {
                // 分类不存在或流程状态为关闭
                continue;
            } else {
                if (isMobile) {
                    if (!flowDefinition.getIsMobileShow()) {
                        // 移动端要过滤掉隐藏的
                        continue;
                    }
                } else {
                    if (flowDefinition.getPcShowFlag() != null && !flowDefinition.getPcShowFlag()) {
                        // PC端要过滤掉隐藏的
                        continue;
                    }
                }
            }

            if (!flowDefMap.containsKey(categoryCode)) {
                flowDefMap.put(categoryCode, new ArrayList<FlowDefinition>());
            }
            flowDefMap.get(categoryCode).add(flowDefinition);
        }

        TreeNode treeNode = new TreeNode();
        Map<String, TreeNode> categoryNodes = Maps.newHashMap();
        Map<String, TreeNode> flowNodes = Maps.newHashMap();

        for (FlowCategory category : categories) {
            TreeNode categoryNode = new TreeNode();
            String id = WorkFlowVariables.FLOW_CATEGORY_PREFIX.getName() + category.getUuid();
            categoryNode.setId(category.getUuid());
            categoryNode.setName(category.getName());
            categoryNode.setData(id);
            categoryNode.setIsParent(false);
            categoryNode.setIconSkin(category.getIcon());
            if (StringUtils.isNotBlank(category.getIconColor())) {
                categoryNode.setIconStyle("color:" + category.getIconColor() + ";");
            }
            treeNode.getChildren().add(categoryNode);
            categoryNodes.put(category.getUuid(), categoryNode);
            traverseCategoryTreeAndAddFlowNode(categoryNode, category.getChildren(), flowDefMap, flowNodes, categoryNodes);
        }
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            if (CollectionUtils.isNotEmpty(categories)) {
                List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(categoryNodes.keySet(), IexportType.FlowCategory, "name", LocaleContextHolder.getLocale().toString());
                if (CollectionUtils.isNotEmpty(i18nEntities)) {
                    for (AppDefElementI18nEntity i : i18nEntities) {
                        if (StringUtils.isNotEmpty(i.getContent()) && categoryNodes.containsKey(i.getDefId())) {
                            categoryNodes.get(i.getDefId()).setName(i.getContent());
                        }
                    }
                }
            }
            if (MapUtils.isNotEmpty(flowNodes)) {
                List<AppDefElementI18nEntity> i18nEntities = appDefElementI18nService.getI18ns(flowNodes.keySet(), IexportType.FlowDefinition,
                        "workflowName", null, LocaleContextHolder.getLocale().toString());
                if (CollectionUtils.isNotEmpty(i18nEntities)) {
                    for (AppDefElementI18nEntity i : i18nEntities) {
                        if (StringUtils.isNotEmpty(i.getContent()) && flowNodes.containsKey(i.getDefId())) {
                            flowNodes.get(i.getDefId()).setName(i.getContent());
                        }
                    }
                }
            }
        }

        return treeNode.getChildren();
    }

    /**
     * 遍历流程分类树并添加流程子结点
     *
     * @param treeNode
     * @param categories
     * @param nodes
     * @param flowNodes
     */
    private void traverseCategoryTreeAndAddFlowNode(TreeNode treeNode, Collection<FlowCategory> categories,
                                                    Map<String, List<FlowDefinition>> flowDefMap, Map<String, TreeNode> flowNodes, Map<String, TreeNode> categoryNodes) {
        if (CollectionUtils.isNotEmpty(categories)) {
            for (FlowCategory category : categories) {
                TreeNode categoryNode = new TreeNode();
                categoryNode.setId(category.getUuid());
                categoryNode.setName(category.getName());
                categoryNode.setNocheck(true);
                categoryNode.setIconSkin(category.getIcon());
                if (StringUtils.isNotBlank(category.getIconColor())) {
                    categoryNode.setIconStyle("color:" + category.getIconColor() + ";");
                }
                categoryNode.setIsParent(true);
                treeNode.getChildren().add(categoryNode);
                categoryNodes.put(category.getUuid(), categoryNode);
                traverseCategoryTree(categoryNode, category.getChildren(), categoryNodes);
            }
        }

        // 添加流程子结点
        String categoryCode = treeNode.getId();
        if (flowDefMap.containsKey(categoryCode)) {
            List<FlowDefinition> flowDefinitions = flowDefMap.get(categoryCode);
            for (FlowDefinition flowDefinition : flowDefinitions) {
                TreeNode flowDefNode = new TreeNode();
                flowDefNode.setId(flowDefinition.getId());
                flowDefNode.setName(flowDefinition.getName());
                flowDefNode.setData(flowDefinition.getId());
                flowDefNode.setType(flowDefinition.getApplyId());
                flowNodes.put(flowDefinition.getId(), flowDefNode);
                treeNode.getChildren().add(flowDefNode);
            }
        }
    }

    /**
     * 获取应用了指定的表单字段的节点
     * lmw 2015-5-25 16:22
     */
    @Override
    public Map<String, Map<String, String>> getElementApplyFormField(String formUuid, String filed) {
        Map<String, Map<String, String>> r = new HashMap<String, Map<String, String>>();
        List<FlowDefinition> flows = flowDefinitionService.getByFormUuid(formUuid);

        String[] fileds = filed.split(";");

        for (FlowDefinition flowDefinition : flows) {
            Map<String, String> rf = new HashMap<String, String>();
            FlowSchema flowSchema = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());
            FlowElement flowElement = FlowDefinitionParser.parseFlow(flowSchema);

            for (String f : fileds) {
                int isSubForm = f.indexOf(":");
                PropertyElement property = flowElement.getProperty();
                List<RecordElement> records = property.getRecords();
                StringBuilder stBuilder = new StringBuilder();
                for (RecordElement re : records) {
                    if (isSubForm > 0 && isSubForm == f.length()) {
                        if (f.indexOf(re.getField()) == 0) {
                            stBuilder.append(re.getName() + ",");
                        }
                    } else {
                        if (f.equals(re.getField())) {
                            stBuilder.append(re.getName() + ",");
                        }
                    }
                }
                if (stBuilder.length() != 0) {
                    rf.put("公共意见域", stBuilder.toString());
                }
                stBuilder = null;

                List<TaskElement> tasks = flowElement.getTasks();
                for (TaskElement re : tasks) {
                    StringBuilder stBuilder1 = new StringBuilder();
                    List<UnitElement> readFields = re.getReadFields();
                    for (UnitElement u : readFields) {
                        if (isSubForm > 0 && isSubForm == f.length()) {
                            if (f.indexOf(u.getValue()) == 0) {
                                stBuilder1.append("只读域,");
                                break;
                            }
                        } else {
                            if (f.equals(u.getValue())) {
                                stBuilder1.append("只读域,");
                                break;
                            }
                        }
                    }

                    List<UnitElement> editFields = re.getEditFields();
                    for (UnitElement u : editFields) {
                        if (isSubForm > 0 && isSubForm == f.length()) {
                            if (f.indexOf(u.getValue()) == 0) {
                                stBuilder1.append("编辑域,");
                                break;
                            }
                        } else {
                            if (f.equals(u.getValue())) {
                                stBuilder1.append("编辑域,");
                                break;
                            }
                        }

                    }

                    List<UnitElement> hideFields = re.getHideFields();
                    for (UnitElement u : hideFields) {
                        if (isSubForm > 0 && isSubForm == f.length()) {
                            if (f.indexOf(u.getValue()) == 0) {
                                stBuilder1.append("隐藏域,");
                                break;
                            }
                        } else {
                            if (f.equals(u.getValue())) {
                                stBuilder1.append("隐藏域,");
                                break;
                            }
                        }
                    }

                    List<UnitElement> notNullFields = re.getNotNullFields();
                    for (UnitElement u : notNullFields) {
                        if (isSubForm > 0 && isSubForm == f.length()) {
                            if (f.indexOf(u.getValue()) == 0) {
                                stBuilder1.append("必填域,");
                                break;
                            }
                        } else {
                            if (f.equals(u.getValue())) {
                                stBuilder1.append("必填域,");
                                break;
                            }
                        }
                    }

                    records = re.getRecords();
                    for (RecordElement rec : records) {
                        if (isSubForm > 0 && isSubForm == f.length()) {
                            if (f.indexOf(rec.getField()) == 0) {
                                stBuilder1.append("意见域,");
                                break;
                            }
                        } else {
                            if (f.equals(rec.getField())) {
                                stBuilder1.append("意见域,");
                                break;
                            }
                        }
                    }
                    if (stBuilder1.length() != 0) {
                        rf.put(re.getId(), stBuilder1.toString());
                    }

                }
                if (!rf.isEmpty()) {
                    r.put(flowDefinition.getName(), rf);
                }
            }
        }
        return r;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getTaskUserTaskHistorys(java.lang.String, java.util.Map)
     */
    @Override
    public List<TreeNode> getTaskUserTaskHistorys(String treeNodeId, Map<String, String> tasksMap) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (tasksMap != null) {
            for (Entry<String, String> entry : tasksMap.entrySet()) {
                TreeNode treeNode = new TreeNode();
                treeNode.setId(entry.getKey());
                treeNode.setName(entry.getValue());
                treeNode.setData(entry.getKey());
                treeNodes.add(treeNode);
            }
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getTaskUserOptionUsers(java.lang.String)
     */
    @Override
    public List<TreeNode> getTaskUserOptionUsers(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        // 前办理人 PriorUser
        TreeNode priorUser = new TreeNode();
        priorUser.setId(Participant.PriorUser.name());
        priorUser.setName("前办理人");
        priorUser.setData(Participant.PriorUser.name());
        // 申请人Creator
        TreeNode creator = new TreeNode();
        creator.setId(Participant.Creator.name());
        creator.setName("申请人");
        creator.setData(Participant.Creator.name());
        // 前一环节办理人 PriorTaskUser
        TreeNode priorTaskUser = new TreeNode();
        priorTaskUser.setId(Participant.PriorTaskUser.name());
        priorTaskUser.setName("前一环节办理人");
        priorTaskUser.setData(Participant.PriorTaskUser.name());
        // 全组织Corp
//        TreeNode corp = new TreeNode();
//        corp.setId(Participant.Corp.name());
//        corp.setName("全组织");
//        corp.setData(Participant.Corp.name());

        treeNodes.add(priorUser);
        treeNodes.add(creator);
        treeNodes.add(priorTaskUser);
//        treeNodes.add(corp);
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getTaskUserCustomInterfaces(java.lang.String)
     */
    @Override
    public List<TreeNode> getTaskUserCustomInterfaces(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        List<Listener> taskUserIndicates = filterListerner(TaskUserIndicate.class);
        for (Listener listener : taskUserIndicates) {
            treeNodes.add(getListenerTreeNode(listener));
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#checkUserCustomExpression(java.lang.String)
     */
    @Override
    public boolean checkUserCustomExpression(String expressionConfig) {
        TaskUserExpressionConfigJson json = JsonUtils.json2Object(expressionConfig, TaskUserExpressionConfigJson.class);
        String expression = json.getExpression(true);
        if (StringUtils.isBlank(expression)) {
            return true;
        }
        String scriptText = "SetOperation A = " + expression + "; end";
        RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
        boolean result = true;
        try {
            ruleEngine.parse(scriptText);
        } catch (Exception e) {
            result = false;
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getUserCustomExpression(java.lang.String)
     */
    @Override
    public String getUserCustomExpression(String expressionConfig) {
        TaskUserExpressionConfigJson json = JsonUtils.json2Object(expressionConfig, TaskUserExpressionConfigJson.class);
        return json.getExpression(false);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#evaluateUserCustomExpression(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public String evaluateUserCustomExpression(String setExpression) {
        String scriptText = "SetOperation A = " + setExpression + "; end";
        RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
        List<String> userIds = new ArrayList<String>();
        try {
            Token token = new Token(new TaskData());
            StartNode startNode = new StartNode();
            ruleEngine.setVariable("node", startNode);
            ruleEngine.setVariable("token", token);
            ruleEngine.execute(scriptText);
            Object set = ruleEngine.getVariable("A");
            userIds.addAll((Collection<String>) set);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));

            throw new RuntimeException(e);
        }
        return workflowOrgService.getUsersByIds(userIds).toString();
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Select2QueryData loadSelectData(Select2QueryInfo select2QueryInfo) {
        String queryValue = select2QueryInfo.getSearchValue();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", queryValue);
        values.put("id", queryValue);
        values.put("systemId", RequestSystemContextPathResolver.system());
        List<FlowDefinition> list = this.dao.namedQuery("flowDefinitionSelect2Query", values, FlowDefinition.class,
                select2QueryInfo.getPagingInfo());
        Set<String> flowDefIdSet = Sets.newHashSet();
        // ID去重
        list = list.stream().filter(definition -> {
            if (flowDefIdSet.contains(definition.getId())) {
                return false;
            }
            flowDefIdSet.add(definition.getId());
            return true;
        }).collect(Collectors.toList());
        FlowDefinition defaultOpt = new FlowDefinition();
        defaultOpt.setId("");
        defaultOpt.setName("");
        list.add(0, defaultOpt);
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public Select2QueryData loadSelectDataByIds(Select2QueryInfo select2QueryInfo) {
        String[] flowDefIds = select2QueryInfo.getIds();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefIds", flowDefIds);
        List<FlowDefinition> list = this.dao.namedQuery("flowDefinitionSelect2IdsQuery", values, FlowDefinition.class);
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getSubtaskDispatcherCustomInterfaces(java.lang.String)
     */
    @Override
    public List<TreeNode> getSubtaskDispatcherCustomInterfaces(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (CustomDispatcherFlowResolver dispatcher : dispatcherFlowResolvers) {
            if (StringUtils.isNotBlank(dispatcher.getName())) {
                TreeNode node = new TreeNode();
                String simpleName = ClassUtils.getUserClass(dispatcher.getClass()).getSimpleName();
                simpleName = StringUtils.uncapitalize(simpleName);
                node.setId(simpleName);
                node.setData(simpleName);
                node.setName(dispatcher.getName());
                treeNodes.add(node);
            }
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getCustomDispatcherBranchTaskInterfaces(java.lang.String)
     */
    @Override
    public List<TreeNode> getCustomDispatcherBranchTaskInterfaces(String treeNodeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        for (CustomDispatcherBranchTaskResolver dispatcher : dispatcherBranchTaskResolvers) {
            if (StringUtils.isNotBlank(dispatcher.getName())) {
                TreeNode node = new TreeNode();
                String simpleName = ClassUtils.getUserClass(dispatcher.getClass()).getSimpleName();
                simpleName = StringUtils.uncapitalize(simpleName);
                node.setId(simpleName);
                node.setData(simpleName);
                node.setName(dispatcher.getName());
                treeNodes.add(node);
            }
        }
        return treeNodes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFieldPropertyInfos(java.lang.String)
     */
    @Override
    public Map<String, Object> getFieldPropertyInfos(String formUuid) {
        Map<String, Object> map = new HashMap<String, Object>();
        // 字段配置属性
        Map<String, List<FieldPropertyBean>> propertyMap = new LinkedHashMap<String, List<FieldPropertyBean>>();
        // 字段名称
        Map<String, String> fieldNameMap = new LinkedHashMap<String, String>();
        // DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, "");
        // List<DyformFieldDefinition> fieldDefinitions =
        // dyFormFacade.getFieldDefinitions(formUuid);
        // for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
        // String fieldName = fieldDefinition.getFieldName();
        // List<FieldPropertyBean> fps = dyFormData.getFieldPropertyItems(formUuid,
        // fieldName);
        // propertyMap.put(fieldName, fps);
        // fieldNameMap.put(fieldName, fieldDefinition.getDisplayName());
        // }
        // 从表配置属性
        // TODO

        map.put("fieldProperties", propertyMap);
        map.put("fieldNames", fieldNameMap);
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getCurrentUserUnitOrgVersions()
     */
    @Override
    public List<OrganizationDto> getCurrentUserUnitOrgVersions() {
        List<OrganizationDto> multiOrgVersions = new ArrayList<OrganizationDto>();
        String userId = SpringSecurityUtils.getCurrentUserId();
        try {
            multiOrgVersions = workflowOrgService.listOrganizationBySystem(RequestSystemContextPathResolver.system());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return multiOrgVersions;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getFormSubtabs(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getFormSubtabs(String treeNodeId, String formUuid) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (StringUtils.isBlank(formUuid)) {
            return treeNodes;
        }
        List<DyformTab> dyformSubTabs = dyFormFacade.getSubTabsByformUuid(formUuid);
        if (CollectionUtils.isEmpty(dyformSubTabs)) {
            return treeNodes;
        }
        for (DyformTab dyformBlock : dyformSubTabs) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(dyformBlock.getName());
            treeNode.setName(dyformBlock.getDisplayName());
            treeNode.setData(dyformBlock.getName());
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#getBySubtabsByName(java.lang.String, java.lang.String)
     */
    @Override
    public QueryItem getBySubtabsByName(String formUuid, String names) {
        QueryItem item = new QueryItem();
        List<DyformTab> dyformSubTabs = dyFormFacade.getSubTabsByformUuid(formUuid);
        String[] fieldNameArray = names.split(Separator.SEMICOLON.getValue());

        for (DyformTab formSubtab : dyformSubTabs) {
            for (String string : fieldNameArray) {
                if (StringUtils.equals(string, formSubtab.getName())) {
                    String label = formSubtab.getDisplayName();
                    String value = formSubtab.getName();
                    if (item.containsKey("label")) {
                        item.put("label", item.get("label") + Separator.SEMICOLON.getValue() + label);
                        item.put("value", item.get("value") + Separator.SEMICOLON.getValue() + value);
                    } else {
                        item.put("label", label);
                        item.put("value", value);
                    }
                }
            }
        }
        return item;
    }

    @Override
    public List<DyFormFormDefinition> listFormDefinition(List<String> flowCategoryUuids, List<String> flowDefIds) {
        List<String> formUuids = flowDefinitionService.listFormUuidByCategoryUuidsAndFlowDefIds(flowCategoryUuids, flowDefIds);
        if (CollectionUtils.isNotEmpty(formUuids)) {
            return dyFormFacade.listDyFormDefinitionByUuids(formUuids);
        }
        return Collections.emptyList();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#loadDirectionSelectData(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData loadDirectionSelectData(Select2QueryInfo queryInfo) {
        List<Direction> directions = Lists.newArrayList();
        String flowDefId = queryInfo.getOtherParams("flowDefId");
        if (StringUtils.isNotBlank(flowDefId)) {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinitionService.getById(flowDefId));
            directions = flowDelegate.getAllDirections();
            for (Direction direction : directions) {
                direction.setName(direction.getName() + " | " + direction.getId());
            }
        }
        return new Select2QueryData(directions, "id", "name", queryInfo.getPagingInfo());
    }

    @Override
    public Select2QueryData loadFlowTaskSelectData(Select2QueryInfo queryInfo) {

        Select2QueryData select2QueryData = new Select2QueryData();
        String flowDefUuid = queryInfo.getOtherParams("flowDefUuid");
        if (StringUtils.isBlank(flowDefUuid)) {
            return select2QueryData;
        }
        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        if (flowDefinition == null) {
            return select2QueryData;
        }
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        List<Node> nodes = flowDelegate.getAllTaskNodes();
        for (Node node : nodes) {
            if (Integer.valueOf(1).equals(node.getType())) {
                continue;
            }
            select2QueryData.addResultData(new Select2DataBean(node.getId(), node.getName()));
        }
        return select2QueryData;
    }

    @Override
    public List<FlowDefinition> getRecentUseFlowDefintions(boolean isMobile) {
        QueryInfo queryInfo = new QueryInfo();
        queryInfo.setPagingInfo(new PagingInfo(0, 20, false));
        return workService.queryRecentUse(queryInfo, isMobile);
    }

    @Override
    public List<TreeNode> getAllMobileFlowAsCategoryTree() {
        return this.buildFlowDefinitionCategoryTree(true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#listFlowTimerByFlowId(java.lang.String)
     */
    @Override
    public List<Map<String, Object>> listFlowTimerByFlowId(String flowId) {
        List<Map<String, Object>> timerInfos = Lists.newArrayList();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinitionService.getById(flowId));
        List<TimerElement> timerElements = flowDelegate.getFlow().getTimers();
        for (TimerElement timerElement : timerElements) {
            Map<String, Object> map = Maps.newHashMap();
            map.put("id", timerElement.getTimerId());
            map.put("name", timerElement.getName());
            timerInfos.add(map);
        }
        return timerInfos;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowSchemeService#isExistsUnfinishedFlowInstanceByFlowDefUuid(java.lang.String)
     */
    @Override
    public boolean isExistsUnfinishedFlowInstanceByFlowDefUuid(String flowDefUuid) {
        if (StringUtils.isBlank(flowDefUuid)) {
            return false;
        }
        return flowInstanceService.isFlowActivityDefInUse(flowDefUuid);
    }

    @Override
    @Transactional
    public List<String> modifyUserWithFlowDefinitionUserUuid(String flowDefUuid, List<String> definitionUserUuids,
                                                             FlowDefinitionUserModifyParams modifyParams, boolean saveAsNewVersion) {
        List<String> successDefinitionUserUuids = Lists.newArrayList();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
        FlowElement flowElement = flowDelegate.getFlow();
        if (flowElement.isXmlDefinition()) {
            throw new BusinessException("不支持旧版XML流程定义的办理人修改");
        }

        try {
            definitionUserUuids.forEach(definitionUserUuid -> {
                WfFlowDefinitionUserEntity definitionUserEntity = flowDefinitionUserService.getOne(Long.valueOf(definitionUserUuid));
                // 验证要替换的人员是否在业务组织中
                boolean result = flowDefinitionUserService.validateBizOrgUser(definitionUserEntity, flowElement, modifyParams);
                if (!result) {
                    return;
                }
                flowDefinitionUserService.modifyUserOfFlowElement(definitionUserEntity, flowElement, modifyParams);
                successDefinitionUserUuids.add(definitionUserUuid);
            });
            String newFlowDefJson = saveFlowJson(JsonUtils.object2Json(flowElement), saveAsNewVersion, false);
            String newFlowDefUuid = JsonUtils.json2Object(newFlowDefJson, FlowElement.class).getUuid();

            FlowDefinition oldFlowDefinition = flowDefinitionService.getOne(flowDefUuid);
            FlowDefinition newFlowDefinition = flowDefinitionService.getOne(newFlowDefUuid);
            flowDefinitionService.saveLogManageOperation(newFlowDefinition, oldFlowDefinition,
                    LogManageOperationEnum.identityReplace);
        } catch (Exception e) {
            throw new WorkFlowException(e.getMessage());
        }

        return successDefinitionUserUuids;
    }

    public void updateFlowElementCache(FlowConfiguration configuration) {
        String flowDefUuid = configuration.getUuid();
        Cache cache = cacheManager.getCache(FLOW_DEFINITION_CACHE_ID);
        String cacheKey = getFlowElementCacheKey(flowDefUuid);

        FlowElement flow = FlowDefinitionParser.parseFlow(configuration);

        // 放入缓存
        if (flow != null) {
            cache.put(cacheKey, flow);
        }
    }

    // --------------------------------------private

    /**
     * 复制流程意见校验设置
     *
     * @param oldFlowDefUuid
     * @param newFlowDefId
     * @return void
     **/
    private void copyFlowOpinionCheckSet(String oldFlowDefUuid, String newFlowDefId) {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(oldFlowDefUuid);
        List<WfOpinionCheckSetEntity> opinionCheckSetEntities = wfOpinionCheckSetService
                .getOpinionCheckSets(flowDefinition.getId());
        for (WfOpinionCheckSetEntity entity : opinionCheckSetEntities) {
            entity.setFlowDefId(newFlowDefId);
            entity.setUuid(null);

        }
        wfOpinionCheckSetService.saveAll(opinionCheckSetEntities);
    }

}
