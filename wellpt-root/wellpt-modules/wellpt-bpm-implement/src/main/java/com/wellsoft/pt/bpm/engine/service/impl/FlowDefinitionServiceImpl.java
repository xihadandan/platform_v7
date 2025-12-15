/*
 * @(#)2013-3-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.access.UnitIdentityResolver;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.FlowDefConstants;
import com.wellsoft.pt.bpm.engine.dao.FlowDefinitionDao;
import com.wellsoft.pt.bpm.engine.dao.FlowSchemaDao;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.dto.SaveLogFlowDefinitionDto;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.enums.ForkMode;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowAclSid;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.FlowConfiguration;
import com.wellsoft.pt.bpm.engine.parser.FlowDefinitionParser;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.ManagementType;
import com.wellsoft.pt.bpm.engine.timer.listener.TaskTimerListener;
import com.wellsoft.pt.bpm.engine.timer.support.TimerHelper;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.dto.SaveLogManageOperationDto;
import com.wellsoft.pt.log.enums.DataParseTypeEnum;
import com.wellsoft.pt.log.enums.LogManageDataTypeEnum;
import com.wellsoft.pt.log.enums.LogManageModuleEnum;
import com.wellsoft.pt.log.enums.LogManageOperationEnum;
import com.wellsoft.pt.log.facade.service.LogManageOperationFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.audit.dto.AuditDataItemLogDto;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsTimerCategoryDto;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.enums.EnumTimeLimitType;
import com.wellsoft.pt.timer.facade.service.TsTimerCategoryFacadeService;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.workflow.entity.WfOpinionCheckSetEntity;
import com.wellsoft.pt.workflow.facade.service.WfOpinionCheckSetFacadeService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 流程定义服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-17.1	zhulh		2013-3-17		Create
 * </pre>
 * @date 2013-3-17
 */
@Service
public class FlowDefinitionServiceImpl extends AbstractJpaServiceImpl<FlowDefinition, FlowDefinitionDao, String>
        implements FlowDefinitionService {

    // 数字格式化
    private static final DecimalFormat decimalFormat = new DecimalFormat("#.#");
    // 流程定义编号
    private static final String FLOW_DEF_CODE_PATTERN = "000000";
    // 必须签署意见
    private static String requiredSignOpinion = "B004026";
    // 可编辑文档
    private static String editDocument = "B004025";
    // 撤回B004005、关注B004008、抄送B004010、办理过程B004013
    private static String[] doneRights = new String[]{"B004005", "B004008", "B004010", "B004013"};
    // 催办B004014、关注B004008、抄送B004010、办理过程B004013
    private static String[] monitorRights = new String[]{"B004014", "B004008", "B004010", "B004013"};
    // 特送环节B004016、特送个人B004015、催办B004014、关注B004008、抄送B004010、办理过程B004013
    private static String[] adminRights = new String[]{"B004016", "B004015", "B004014", "B004008", "B004010",
            "B004013"};
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private TaskInstanceService taskInstanceService;
    @Autowired
    private TaskBranchService taskBranchService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private TsTimerCategoryFacadeService timerCategoryFacadeService;
    @Autowired
    private TsTimerConfigFacadeService timerConfigFacadeService;
    @Autowired
    private WfOpinionCheckSetFacadeService opinionCheckSetFacadeService;
    @Autowired
    private LogManageOperationFacadeService logManageOperationFacadeService;
    @Autowired
    private FlowSchemeService flowSchemeService;
    @Autowired
    private FlowSchemaService flowSchemaService;
    @Autowired
    private FlowSchemaDao flowSchemaDao;
    @Autowired
    private WfFlowDefinitionUserService flowDefinitionUserService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;

    /**
     * 设置流程发起者的权限
     *
     * @param flowDefinition
     * @param creators
     */
    private static void setFlowCreators(FlowDefinition flowDefinition, List<UserUnitElement> creators) {
        String uuid = flowDefinition.getUuid();
        // UnitIdentityResolver identityResolver =
        // ApplicationContextHolder.getBean(UnitIdentityResolver.class);
        AclService aclService = ApplicationContextHolder.getBean(AclService.class);
        List<String> rawCreators = new ArrayList<String>();
        for (UnitElement unitElement : creators) {
            String rawUser = unitElement.getValue();
            if (StringUtils.isBlank(rawUser)) {
                continue;
            }
            List<String> sids = Arrays.asList(StringUtils.split(rawUser, Separator.SEMICOLON.getValue()));
            rawCreators.addAll(sids);
        }

        // 基于流程用户、部门、群组、职务、职位的SID
        String groupFlowCreatorSid = WorkFlowAclSid.GROUP_FLOW_CREATOR.name() + uuid;
        aclService.removeSid(groupFlowCreatorSid);
        aclService.removePermission(FlowDefinition.class, uuid, BasePermission.CREATE);

        // 管理员用户ID
//        List<String> allAdminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<String> allAdminIds = workflowOrgService.listCurrentTenantAdminIds(workflowOrgService.getOrgVersionId(FlowDelegateUtils.getFlowDelegate(flowDefinition)));
        // List<String> userIds = identityResolver.resolve(null, null,
        // rawCreators, ParticipantType.TodoUser);
        if (!rawCreators.isEmpty()) {
            String currentUserId = SpringSecurityUtils.getCurrentUserId();
            if (!rawCreators.contains(currentUserId)) {
                rawCreators.add(currentUserId);
            }
            // 管理员用户ID
            for (String adminId : allAdminIds) {
                if (!rawCreators.contains(adminId)) {
                    rawCreators.add(adminId);
                }
            }
            for (String rawCreator : rawCreators) {
                String flowCreatorSid = rawCreator;
                if (!flowCreatorSid.startsWith(IdPrefix.USER.getValue())) {
                    flowCreatorSid = WorkFlowAclSid.ROLE_FLOW_CREATOR + "_" + flowCreatorSid;
                }
                aclService.addPermission(FlowDefinition.class, uuid, BasePermission.CREATE, flowCreatorSid);
            }
            // aclService.addPermission(FlowDefinition.class, uuid,
            // BasePermission.CREATE, flowCreatorSid);
            // for (String userId : userIds) {
            // aclService.addMember(flowCreatorSid, userId,
            // ModuleID.WORKFLOW.getValue());
            // }
        }

        // 所有人可创建流程
        if (rawCreators.isEmpty()) {
            aclService.addPermission(FlowDefinition.class, uuid, BasePermission.CREATE,
                    WorkFlowAclSid.ROLE_FLOW_ALL_CREATOR.name());
        } else {
            aclService.removePermission(FlowDefinition.class, uuid, BasePermission.CREATE,
                    WorkFlowAclSid.ROLE_FLOW_ALL_CREATOR.name());
        }
    }

    /**
     * 设置流程参与者的权限
     *
     * @param flowDefinition
     * @param users
     */
    private static void setFlowUsers(FlowDefinition flowDefinition, List<UserUnitElement> users) {
        String uuid = flowDefinition.getUuid();
        UnitIdentityResolver identityResolver = ApplicationContextHolder.getBean(UnitIdentityResolver.class);
        AclService aclService = ApplicationContextHolder.getBean(AclService.class);
        List<String> rawUsers = new ArrayList<String>();
        for (UnitElement unitElement : users) {
            String rawUser = unitElement.getValue();
            if (StringUtils.isBlank(rawUser)) {
                continue;
            }
            List<String> sids = Arrays.asList(StringUtils.split(rawUser, Separator.SEMICOLON.getValue()));
            rawUsers.addAll(sids);
        }

        // 基于流程群组的SID
        String flowUserSid = WorkFlowAclSid.GROUP_FLOW_USER.name() + uuid;
        List<String> userIds = IdentityResolverStrategy.resolveUserIds(rawUsers);
        rawUsers.forEach(rawUser -> {
            if (!userIds.contains(rawUser)) {
                userIds.add(rawUser);
            }
        });
        // identityResolver.resolve(null, null, rawUsers,
        // ParticipantType.TodoUser);
        if (!userIds.isEmpty()) {
            if (!aclService.hasPermission(FlowDefinition.class, uuid, AclPermission.TODO, flowUserSid)) {
                aclService.addPermission(FlowDefinition.class, uuid, AclPermission.TODO, flowUserSid);
            }
            for (String userId : userIds) {
                aclService.addMember(flowUserSid, userId, ModuleID.WORKFLOW.getValue());
            }
        } else {
            aclService.removeSid(flowUserSid);
        }
        // 所有人可参与流程
        if (rawUsers.isEmpty()) {
            if (!aclService.hasPermission(FlowDefinition.class, uuid, AclPermission.TODO,
                    WorkFlowAclSid.ROLE_FLOW_ALL_USER.name())) {
                aclService.addPermission(FlowDefinition.class, uuid, AclPermission.TODO,
                        WorkFlowAclSid.ROLE_FLOW_ALL_USER.name());
            }
        } else {
            aclService.removePermission(FlowDefinition.class, uuid, AclPermission.TODO,
                    WorkFlowAclSid.ROLE_FLOW_ALL_USER.name());
        }
    }

    /**
     * 设置流程阅读者的权限
     *
     * @param flowDefinition
     * @param viewers
     */
    @Transactional
    public static void setFlowViewers(FlowDefinition flowDefinition, List<UserUnitElement> viewers) {
        String uuid = flowDefinition.getUuid();
        AclService aclService = ApplicationContextHolder.getBean(AclService.class);
        /* modified by huanglinchuan 2014.11.24 begin */
        // UnitIdentityResolver identityResolver =
        // ApplicationContextHolder.getBean(UnitIdentityResolver.class);
        // List<String> rawViewers = new ArrayList<String>();
        if (viewers == null || viewers.isEmpty()) {
            aclService.removePermission(FlowDefinition.class, uuid, AclPermission.READ);
        }
        // 阅读人组织机构ID
        Set<String> readOrgIds = new HashSet<String>();
        for (UnitElement unitElement : viewers) {
            String sidValue = unitElement.getValue();
            if (StringUtils.isBlank(sidValue)) {
                continue;
            }
            List<String> sids = Arrays.asList(StringUtils.split(sidValue, Separator.SEMICOLON.getValue()));
            readOrgIds.addAll(sids);
            sids.forEach(sid -> {
                if (!sid.startsWith(IdPrefix.USER.getValue())) {
                    sid = "GROUP_" + sid;
                }
                if (!aclService.hasPermission(flowDefinition, AclPermission.READ, sid)) {
                    aclService.addPermission(FlowDefinition.class, uuid, AclPermission.READ, sid);
                }
            });
        }
        /*
         * String flowViewerSid = WorkFlowAclSid.GROUP_FLOW_VIEWER.name() + uuid;
         * List<String> userIds = identityResolver.resolve(null, null, rawViewers,
         * ParticipantType.TodoUser); if (!userIds.isEmpty()) {
         * aclService.addPermission(FlowDefinition.class, uuid, AclPermission.READ,
         * flowViewerSid); aclService.removeAllMember(flowViewerSid
         * ,ModuleID.WORKFLOW.getValue()); for (String userId : userIds) {
         * aclService.addMember(flowViewerSid, userId, ModuleID.WORKFLOW.getValue()); }
         * } else { aclService.removeSid(flowViewerSid); }
         */
        /* modified by huanglinchuan 2014.11.24 end */

        // 删除流程管理中定义的人员
        FlowManagementService flowManagementService = ApplicationContextHolder.getBean(FlowManagementService.class);
        flowManagementService.remove(uuid, ManagementType.READ);

        // 管理员用户ID
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        List<String> allAdminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<String> allAdminIds = workflowOrgService.listCurrentTenantAdminIds(workflowOrgService.getOrgVersionId(FlowDelegateUtils.getFlowDelegate(flowDefinition)));
        readOrgIds.addAll(allAdminIds);
        flowManagementService.create(ManagementType.READ, readOrgIds, uuid);
    }

    @Override
    public List<FlowDefinition> getListByIdsOrCategoryUuids(List<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return Lists.newArrayList();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuids", uuids);
        return this.dao.listByNameSQLQuery("getListByIdsOrCategoryUuids", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#checkFlowXmlForUpdate(java.lang.String)
     */
    @Override
    public boolean checkFlowXmlForUpdate(String xml) {
        Document document = FlowDefinitionParser.createDocument(xml);
        Element rootElement = document.getRootElement();
        // 主键
        String uuid = rootElement.attributeValue(FlowDefConstants.FLOW_UUID);
        if (StringUtils.isBlank(uuid)) {
            return true;
        }
        FlowDefinition flowDefinition = dao.getOne(uuid);
        FlowSchema flowSchemaOld = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());// flowDefinition.getFlowSchema();
        String oldXml = flowSchemaOld.getContentAsString();
        boolean isInUse = flowInstanceService.isFlowActivityDefInUse(flowDefinition.getUuid());
        // 流程已经被使用,并且节点个数和流向个数不一致的情况
        if (isInUse && !checkFlowXml(FlowDefinitionParser.parseFlow(document.asXML()), FlowDefinitionParser.parseFlow(oldXml), uuid)) {
            throw new WorkFlowException("存在未办结的流程无法对流程的流向，和节点进行增删，请保存为新版本！");
        }
        return true;
    }

    /**
     * 解析并保存流程定义
     *
     * @param xml
     * @param pbNew
     * @return
     */
    @Override
    @Transactional
    public FlowConfiguration parseAndSave(String xml, Boolean pbNew, Boolean isCopy) {
//        Document document = FlowDefinitionParser.createDocument(xml);
//        Element rootElement = document.getRootElement();
        FlowConfiguration configuration = FlowConfiguration.from(xml);
        FlowDefinition flowDefinition = null;
        FlowDefinition oldFlowDefinition = null;
        FlowSchema oldFlowSchema = null;
        // 主键
        String uuid = configuration.getUuid();// rootElement.attributeValue(FlowDefConstants.FLOW_UUID);
        // 保存为新版本
        if (pbNew) {
            FlowDefinition flowDefinition2 = getById(configuration.getId());// getById(rootElement.attributeValue(FlowDefConstants.FLOW_ID));
            oldFlowDefinition = new FlowDefinition();
            BeanUtils.copyProperties(flowDefinition2, oldFlowDefinition);
            oldFlowSchema = flowSchemaService.getOne(oldFlowDefinition.getFlowSchemaUuid());
            flowDefinition = create(configuration, xml);
            // task:6441 开发-主导-流程定义修改日志
            saveLogManageOperation(flowDefinition, oldFlowDefinition, LogManageOperationEnum.saveFlowNew);
            saveFlowI18ns(configuration.getFlowElement(), flowDefinition);
        } else if (StringUtils.isEmpty(uuid)) {// 创建新的流程定义
            if (!isExists(configuration, xml)) {
                flowDefinition = create(configuration, xml);
                if (!isCopy) {
                    // task:6441 开发-主导-流程定义修改日志
                    saveLogManageOperation(flowDefinition, null, LogManageOperationEnum.add);
                }
                saveFlowI18ns(configuration.getFlowElement(), flowDefinition);
            }
        } else {// 更新现有流程定义
            FlowDefinition flowDefinition2 = this.dao.getOne(uuid);
            oldFlowSchema = flowSchemaService.getOne(flowDefinition2.getFlowSchemaUuid());
            if (flowDefinition2 != null) {
                appDefElementI18nService.deleteAllI18n(null, flowDefinition2.getId(), new BigDecimal(flowDefinition2.getVersion()), IexportType.FlowDefinition);
                oldFlowDefinition = new FlowDefinition();
                BeanUtils.copyProperties(flowDefinition2, oldFlowDefinition);
                SaveLogFlowDefinitionDto oldFlowDef = toSaveLogFlowDefinitionDto(oldFlowDefinition);
                flowDefinition = update(configuration, xml);
                // task:6441 开发-主导-流程定义修改日志
                saveLogManageOperationByOldFlow(flowDefinition, oldFlowDef, LogManageOperationEnum.edit);
                saveFlowI18ns(configuration.getFlowElement(), flowDefinition);
            } else {
                flowDefinition = create(configuration, xml);
                // task:6441 开发-主导-流程定义修改日志
                saveLogManageOperation(flowDefinition, null, LogManageOperationEnum.add);
                saveFlowI18ns(configuration.getFlowElement(), flowDefinition);
            }
        }
        if (flowDefinition != null) {
            AuditDataLogDto dto = new AuditDataLogDto().name(flowDefinition.getName()).diffEntity(flowDefinition, oldFlowDefinition);
            dto.getDataItems().add(new AuditDataItemLogDto("流程定义json", "definitionJson", null, xml, oldFlowSchema != null ? oldFlowSchema.getDefinitionJsonAsString() : null));
            ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(dto);
        }
        return configuration;
    }

    private void saveFlowI18ns(FlowElement flowElement, FlowDefinition flowDefinition) {
        List<AppDefElementI18nEntity> i18nEntities = Lists.newArrayList();
        if (MapUtils.isNotEmpty(flowElement.getI18n())) {
            Set<Map.Entry<String, Map<String, String>>> entrySet = flowElement.getI18n().entrySet();
            for (Map.Entry<String, Map<String, String>> map : entrySet) {
                String locale = map.getKey();
                Map<String, String> props = map.getValue();
                Set<Map.Entry<String, String>> propEntrySet = props.entrySet();
                for (Map.Entry<String, String> prop : propEntrySet) {
                    AppDefElementI18nEntity entity = new AppDefElementI18nEntity();
                    entity.setCode(prop.getKey());
                    entity.setVersion(new BigDecimal(flowDefinition.getVersion()));
                    entity.setDefId(flowDefinition.getId());
                    entity.setApplyTo(IexportType.FlowDefinition);
                    entity.setContent(prop.getValue());
                    entity.setLocale(locale);
                    i18nEntities.add(entity);
                }
            }
        }
        appDefElementI18nService.saveAll(i18nEntities);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#parseAndSaveForUpdate(String)
     */
    @Override
    @Transactional
    public FlowDefinition parseAndSaveForUpdate(String xml) {
        Document document = FlowDefinitionParser.createDocument(xml);
        Element rootElement = document.getRootElement();

        FlowDefinition flowDefinition = null;
        // 主键
        String uuid = rootElement.attributeValue(FlowDefConstants.FLOW_UUID);

        try {
            File outDir = new File(Config.APP_DATA_DIR + "/flowdefinition", uuid + ".xml");
            OutputStream out = new FileOutputStream(outDir);
            IOUtils.write(xml, out);
            IOUtils.closeQuietly(out);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        // 更新标题表达式
        Attribute attribute = rootElement.attribute("titleExpression");
        if (attribute != null) {
            String titleExpression = attribute.getValue();
            if (StringUtils.isBlank(titleExpression)) {
                rootElement.addAttribute("titleExpression",
                        "${流程名称}_${年}-${月}-${日} ${时}:${分}:${秒}_${发起人姓名}_${发起人所在部门名称}");
            }
        } else {
            rootElement.addAttribute("titleExpression", "${流程名称}_${年}-${月}-${日} ${时}:${分}:${秒}_${发起人姓名}_${发起人所在部门名称}");
        }
        // 更新任务权限
        // 1、待办权限
        List<Element> todoNodes = rootElement.selectNodes("/flow/tasks/task/rights");
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(this.dao.getOne(uuid));
        for (Element todoNode : todoNodes) {
            List<Element> unitNodes = todoNode.selectNodes("unit");
            String taskId = todoNode.getParent().attributeValue("id");
            if (flowDelegate.isFirstTaskNode(taskId)) {
                // 所有流程的第一个环节立达信需要默认可以编辑文档，所以第一个环节都增加“可编辑文档”权限
                if (!hasRight(editDocument, unitNodes)) {
                    List<Element> els = createElements(new String[]{editDocument});
                    todoNode.add(els.get(0));
                }
            } else {
                // 所有流程环节除第一个环节外的待办权限都增加“必须签署意见”
                if (!hasRight(requiredSignOpinion, unitNodes)) {
                    List<Element> els = createElements(new String[]{requiredSignOpinion});
                    todoNode.add(els.get(0));
                }
            }
        }
        // 2、已办权限
        List<Element> doneNodes = rootElement.selectNodes("/flow/tasks/task/doneRights");
        for (Element doneNode : doneNodes) {
            // 删除原有权限
            List<Element> unitNodes = doneNode.selectNodes("unit");
            for (Element unitNode : unitNodes) {
                doneNode.remove(unitNode);
            }
            // 新增新权限
            List<Element> els = createElements(doneRights);
            for (Element element : els) {
                doneNode.add(element);
            }
        }

        // 3、监控权限
        List<Element> monitorNodes = rootElement.selectNodes("/flow/tasks/task/monitorRights");
        for (Element monitorNode : monitorNodes) {
            // 删除原有权限
            List<Element> unitNodes = monitorNode.selectNodes("unit");
            for (Element unitNode : unitNodes) {
                monitorNode.remove(unitNode);
            }
            // 新增新权限
            List<Element> els = createElements(monitorRights);
            for (Element element : els) {
                monitorNode.add(element);
            }
        }

        // 4、管理权限
        List<Element> adminNodes = rootElement.selectNodes("/flow/tasks/task/adminRights");
        for (Element adminNode : adminNodes) {
            // 删除原有权限
            List<Element> unitNodes = adminNode.selectNodes("unit");
            for (Element unitNode : unitNodes) {
                adminNode.remove(unitNode);
            }
            // 新增新权限
            List<Element> els = createElements(adminRights);
            for (Element element : els) {
                adminNode.add(element);
            }
        }
        // 更新现有流程定义
        flowDefinition = update(FlowConfiguration.from(document.asXML()), xml);
        return flowDefinition;
    }

    /**
     * @param rigth
     * @param unitNodes
     * @return
     */
    private boolean hasRight(String rigth, List<Element> unitNodes) {
        for (Element unitNode : unitNodes) {
            String text = unitNode.getText();
            if (rigth.equals(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param adminRights2
     * @return
     */
    private List<Element> createElements(String[] rights) {
        List<Element> els = new ArrayList<Element>();
        for (String right : rights) {
            Element unit = DocumentFactory.getInstance().createElement("unit");
            unit.addAttribute("type", "32");
            unit.setText(right);
            els.add(unit);
        }
        return els;
    }

    /**
     * 检查流程ID是否已经存在
     *
     * @param configuration
     * @param xml
     * @return
     */
    private boolean isExists(FlowConfiguration configuration, String xml) {
//        Element rootElement = document.getRootElement();
        String id = configuration.getId();// rootElement.attributeValue(FlowDefConstants.FLOW_ID);// 主键
        Long count = dao.countById(id);
        if (count > 0) {
            throw new RuntimeException("ID为" + id + "的流程已经存在！");
        }
        return false;
    }

    /**
     * @param flowConfiguration
     */
    private void checkBranchTask(FlowConfiguration flowConfiguration) {
        FlowElement flowElementNew = flowConfiguration.getFlowElement();// FlowDefinitionParser.parseFlow(xmlNew);
        // 指向子流程环节的流向只能是静态分支
        List<DirectionElement> directionElements = flowElementNew.getDirections();
        for (DirectionElement directionElement : directionElements) {
            TaskElement toTaskElement = flowElementNew.getTask(directionElement.getToID());
            if (toTaskElement != null && "2".equals(toTaskElement.getType())
                    && StringUtils.isNotBlank(directionElement.getBranchMode())
                    && !"1".equals(directionElement.getBranchMode())) {
                throw new RuntimeException("只有静态分支才能指向子流程！");
            }
        }
    }

    /**
     * @param newXML
     * @param oldXml
     * @return
     */
    private boolean checkFlowXml(FlowElement newFlowElement, FlowElement oldFlowElement, String flowDefUuid) {
//        FlowElement newFlowElement = FlowDefinitionParser.parseFlow(newXML);
//        FlowElement oldFlowElement = FlowDefinitionParser.parseFlow(oldXml);

        // 修改流程属性-高级设置-等价流程，自由流程设置，信息提示：存在未办结的流程，无法设置等价、自由流程，请保存新版本
        EqualFlowElement newEqualFlow = newFlowElement.getProperty().getEqualFlow();
        EqualFlowElement oldEqualFlow = oldFlowElement.getProperty().getEqualFlow();
        if (!StringUtils.equals(newEqualFlow.getId(), oldEqualFlow.getId())) {
            throw new WorkFlowException("存在未办结的流程，无法设置等价流程，请保存新版本！");
        }
        if (newFlowElement.getProperty().isFree() != oldFlowElement.getProperty().isFree()) {
            throw new WorkFlowException("存在未办结的流程，无法设置自由流程，请保存新版本！");
        }

        // 修改流程属性-基本属性-关联表单，信息提示：存在未办结的流程，无法修改使用表单，请保存新版本
        if (!StringUtils.equals(newFlowElement.getProperty().getFormID(), oldFlowElement.getProperty().getFormID())) {
            throw new WorkFlowException("存在未办结的流程，无法修改使用表单，请保存新版本！");
        }

        // 删除的环节定义存在尚在流转的当前环节实例，信息提示：正在流转的环节无法直接删除，请保存新版本
        Set<String> deletedTaskIds = getDeletedTaskIds(newFlowElement, oldFlowElement);
        if (CollectionUtils.isNotEmpty(deletedTaskIds)) {
            long ufinishedTaskCount = taskInstanceService.countUnfinishedTasksByFlowDefUuidAndTaskIds(flowDefUuid,
                    deletedTaskIds);
            if (ufinishedTaskCount > 0) {
                List<String> checkJsonTaskIds = Lists.newArrayList(deletedTaskIds);
                // 检查JSON定义是否存在新增环节
                if (newFlowElement.isXmlDefinition()) {
                    FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
                    checkJsonTaskIds.removeAll(flowDelegate.getFlow().getTasks().stream().map(taskElement -> taskElement.getId()).collect(Collectors.toList()));
                } else {
                    try {
                        String xml = flowSchemeService.getFlowXml(flowDefUuid);
                        if (StringUtils.isNotBlank(xml)) {
                            FlowElement xmlFlowElement = FlowDefinitionParser.parseFlow(xml);
                            checkJsonTaskIds.removeAll(xmlFlowElement.getTasks().stream().map(taskElement -> taskElement.getId()).collect(Collectors.toList()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (CollectionUtils.isNotEmpty(checkJsonTaskIds)) {
                    throw new RuntimeException("正在流转的环节无法直接删除，请保存新版本！");
                }
            }
        }

        // 修改环节的环节属性-分支、聚合模式：校验是否修改分支模式；校验是否存在已进入分支模式的实例，有则不能保存，信息提示：存在未办结的流程已进入分支模式，无法修改分支/聚合模式，请保存新版本
        Map<String, TaskElement> oldTaskElementMap = oldFlowElement.getTaskAsMap();
        List<String> branchTaskIds = Lists.newArrayList();
        for (TaskElement newTaskElement : newFlowElement.getTasks()) {
            TaskElement oldTaskElement = oldTaskElementMap.get(newTaskElement.getId());
            if (oldTaskElement == null) {
                continue;
            }
            ParallelGatewayElement newParallelGatewayElement = newTaskElement.getParallelGateway();
            ParallelGatewayElement oldParallelGatewayElement = oldTaskElement.getParallelGateway();
            if (newParallelGatewayElement != null && oldParallelGatewayElement != null) {
                if (newParallelGatewayElement.getForkMode() != oldParallelGatewayElement.getForkMode()) {
                    branchTaskIds.add(oldTaskElement.getId());
                }
            }
        }
        if (CollectionUtils.isNotEmpty(branchTaskIds)) {
            long unfinishedBranchCount = taskBranchService.countUnfinishedBranchByFlowDefUuidAndTaskIds(flowDefUuid,
                    branchTaskIds);
            if (unfinishedBranchCount > 0) {
                throw new RuntimeException("存在未办结的流程已进入分支模式，无法修改分支/聚合模式，请保存新版本！");
            }
        }
        return true;
    }

    /**
     * @param newFlowElement
     * @param oldFlowElement
     * @return
     */
    private Set<String> getDeletedTaskIds(FlowElement newFlowElement, FlowElement oldFlowElement) {
        Map<String, TaskElement> oldTaskMap = oldFlowElement.getTaskAsMap();
        List<TaskElement> tasksElements = newFlowElement.getTasks();
        tasksElements.forEach(t -> {
            oldTaskMap.remove(t.getId());
        });
        return oldTaskMap.keySet();
    }

    /**
     * 检查节点和流向是否被删除，或者新增
     *
     * @param xmlOld
     * @param xmlNew
     * @return
     */
    private boolean isNeedSaveNew(String xmlOld, String xmlNew) {
        FlowElement flowElementOld = FlowDefinitionParser.parseFlow(xmlOld);
        FlowElement flowElementNew = FlowDefinitionParser.parseFlow(xmlNew);
        Map<String, TaskElement> oldTaskElementMap = Maps.newHashMap();
        Map<String, String> oldTaskflowMap = Maps.newHashMap();
        Map<String, String> oldDirectionMap = Maps.newHashMap();
        Map<String, String> oldDirectionFromMap = Maps.newHashMap();
        Map<String, String> oldDirectionToMap = Maps.newHashMap();
        Boolean isNeedNew = true;
        for (TaskElement taskElement : flowElementOld.getTasks()) {
            oldTaskflowMap.put(taskElement.getId(), taskElement.getName());
            oldTaskElementMap.put(taskElement.getId(), taskElement);
        }
        for (DirectionElement directionElement : flowElementOld.getDirections()) {
            oldDirectionMap.put(directionElement.getId(), directionElement.getName());
            oldDirectionFromMap.put(directionElement.getId(), directionElement.getFromID());
            oldDirectionToMap.put(directionElement.getId(), directionElement.getToID());
        }

        // 流向个数一致，
        if (flowElementOld.getTasks().size() == flowElementNew.getTasks().size()
                && flowElementOld.getDirections().size() == flowElementNew.getDirections().size()) {
            isNeedNew = false;
        }

        if (isNeedNew) {
            return isNeedNew;
        }
        // 环节的ID是否一致不一致,个数一致判断名称是否一致
        for (TaskElement taskElement : flowElementNew.getTasks()) {
            if (oldTaskflowMap.get(taskElement.getId()) == null) {
                isNeedNew = true;
                break;
            } else {
                // 分支模式变更
                TaskElement oldTaskElement = oldTaskElementMap.get(taskElement.getId());
                ParallelGatewayElement parallelGatewayElement = taskElement.getParallelGateway();
                ParallelGatewayElement oldParallelGatewayElement = oldTaskElement.getParallelGateway();
                if (parallelGatewayElement != null && oldParallelGatewayElement != null) {
                    if (parallelGatewayElement.getForkMode() != oldParallelGatewayElement.getForkMode()) {
                        isNeedNew = true;
                        break;
                    }
                }
            }
        }
        if (isNeedNew) {
            return isNeedNew;
        }
        // 流向的ID是否一致
        for (DirectionElement directionElement : flowElementNew.getDirections()) {
            String odfId = oldDirectionFromMap.get(directionElement.getId());
            String odtId = oldDirectionToMap.get(directionElement.getId());
            if (oldDirectionMap.get(directionElement.getId()) == null
                    || (odfId != null && !odfId.equals(directionElement.getFromID()))
                    || (odtId != null && !odtId.equals(directionElement.getToID()))) {
                isNeedNew = true;
                break;
            }
        }
        return isNeedNew;
    }

    /**
     * 根据flwFlowSchema保存flwFlowSchemaLog日记小版本信息
     *
     * @param flowSchema
     */
    private void saveLog(FlowSchema flowSchema, FlowConfiguration configuration, boolean isCreate) {
        saveLog(flowSchema.getUuid(), flowSchema.getName(), flowSchema.getContentAsString(), flowSchema.getDefinitionJsonAsString(), configuration, isCreate);
    }

    /**
     * @param flowSchemaUuid
     * @param flowSchemaName
     * @param oldXml
     * @param oldJson
     * @param configuration
     * @param isCreate
     */
    private void saveLog(String flowSchemaUuid, String flowSchemaName, String oldXml, String oldJson, FlowConfiguration configuration, boolean isCreate) {
        flowSchemaService.saveLog(flowSchemaUuid, flowSchemaName, oldXml, oldJson, configuration, isCreate, true);
    }

    /**
     * 更新现有流程定义
     *
     * @param configuration
     * @param xml
     * @return
     */
    private FlowDefinition update(FlowConfiguration configuration, String xml) {
        // Element rootElement = document.getRootElement();
        String uuid = configuration.getUuid();// rootElement.attributeValue(FlowDefConstants.FLOW_UUID);// 主键

        // 更新流程定义
        FlowDefinition flowDefinition = dao.getOne(uuid);
        checkOldFlowDefinition(flowDefinition, configuration);

        FlowElement flowElement = configuration.getFlowElement();// FlowDefinitionParser.parseFlow(xml);

        String name = flowElement.getName();// 名称
        String id = flowElement.getId();// 唯一标识(别名)
        String code = flowElement.getCode();// 编号
        String systemUnitId = flowElement.getSystemUnitId(); // 系统归属单位ID
        // 如果编号为空生成自动编号
        if (StringUtils.isBlank(code)) {
            code = idGeneratorService.generate(FlowDefinition.class, FLOW_DEF_CODE_PATTERN, false);
            // rootElement.addAttribute(FlowDefConstants.FLOW_CODE, code);
            configuration.setCode(code);
        }
        String version = flowElement.getVersion();// 版本号
        String categorySN = flowElement.getProperty().getCategorySN();// 分类
        String formId = flowElement.getProperty().getFormID();// 动态表单
        String active = flowElement.getProperty().getIsActive();// 是否启用
        String free = flowElement.getProperty().getIsFree();// 是否自由流程
        Boolean autoUpdateTitle = Boolean.FALSE;// 是否自动更新标题
        if (StringUtils.isNotBlank(flowElement.getProperty().getAutoUpdateTitle())
                && "1".equals(flowElement.getProperty().getAutoUpdateTitle())) {
            autoUpdateTitle = Boolean.TRUE;
        }

        // String isIndependent =
        // flowElement.getProperty().getIsIndependent();// 是否应用独立
        String equalFlowId = flowElement.getProperty().getEqualFlow().getId();// 等价流程
        String remark = flowElement.getProperty().getRemark();// 流程备注
        String customJsModule = flowElement.getProperty().getCustomJsModule();// 二开的JS模块
        flowDefinition.setAutoUpdateTitle(autoUpdateTitle);
        flowDefinition.setName(name);
        flowDefinition.setId(id);
        flowDefinition.setCode(code);
        flowDefinition.setVersion(Double.valueOf(version));
        flowDefinition.setSystemUnitId(systemUnitId);
        flowDefinition.setCategory(categorySN);
        flowDefinition.setFormUuid(formId);
        flowDefinition.setModuleId(flowElement.getModuleId());
        flowDefinition.setMultiJobFlowType(flowElement.getProperty().getMultiJobFlowType());
        flowDefinition.setJobField(flowElement.getProperty().getJobField());
        flowDefinition.setIsMobileShow("1".equals(flowElement.getProperty().getIsMobileShow()));
        flowDefinition.setPcShowFlag("1".equals(flowElement.getProperty().getPcShowFlag()));
        // FormDefinition formDefinition =
        // dytableApiFacade.getFormByUUID(formId);
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formId);
        if (formDefinition != null) {
            flowDefinition.setFormName(formDefinition.getName());
        }
        flowDefinition.setEnabled("1".equals(active));
        flowDefinition.setFreeed("1".equals(free));
        // flowDefinition.setIndependent("1".equals(isIndependent));
        flowDefinition.setEqualFlowId(equalFlowId);
        flowDefinition.setRemark(remark);
        if (StringUtils.isBlank(flowDefinition.getSystem())) {
            flowDefinition.setSystem(RequestSystemContextPathResolver.system());
        }
        if (StringUtils.isBlank(flowDefinition.getTenant())) {
            flowDefinition.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }

        // 二开JS模块
        if (StringUtils.isNotBlank(customJsModule)) {
            Map<String, Object> developJsonMap = Maps.newHashMap();
            developJsonMap.put(FlowConstant.CUSTOM_JS_MODULE, customJsModule);
            flowDefinition.setDevelopJson(JsonUtils.object2Json(developJsonMap));
        } else {
            flowDefinition.setDevelopJson(null);
        }

        flowDefinition.setApplyId(flowElement.getApplyId());
        /* add by huanglinchuan 2014.10.20 begin */
        flowDefinition.setTitleExpression(flowElement.getTitleExpression());
        /* add by huanglinchuan 2014.10.20 end */

        // 生成计时服务配置
        if (configuration.getDocument() != null) {
            createTimerConfig(flowDefinition, configuration.getDocument().getRootElement(), false);
        } else {
            createTimerConfig(flowDefinition, configuration.getFlowElement(), false);
        }

        // 更新流程规划
        String flowSchemaUuid = flowSchemaService.save(flowDefinition.getFlowSchemaUuid(), name, configuration);
        // flowSchema.setFlowDefinition(flowDefinition);
        flowDefinition.setFlowSchemaUuid(flowSchemaUuid);

        dao.save(flowDefinition);
        flowSchemaDao.getSession().flush();
        flowSchemaDao.getSession().clear();

        saveAcl(flowDefinition, flowElement);

        // 签署意见校验设置保存
        saveOpinionCheckSet(flowDefinition, flowElement);

        // 保存流程定义用户信息
        saveDefUser(flowDefinition, flowElement);

        return flowDefinition;
    }

    /**
     * @param flowDefinition
     * @param configuration
     */
    private void checkOldFlowDefinition(FlowDefinition flowDefinition, FlowConfiguration configuration) {
        String uuid = flowDefinition.getUuid();
        FlowElement oldFlowElement = flowSchemeService.getFlowElement(flowDefinition);
        String oldXml = oldFlowElement.getDefinitionXml();
        String oldJson = oldFlowElement.getDefinitionJson();
        if (StringUtils.isNotBlank(flowDefinition.getEqualFlowId())) {
            FlowSchema flowSchemaOld = flowSchemaService.getOne(flowDefinition.getFlowSchemaUuid());// flowDefinition.getFlowSchema();
            oldFlowElement = FlowDefinitionParser.parseFlow(flowSchemaOld);
        }
        boolean isInUse = flowInstanceService.isFlowActivityDefInUse(flowDefinition.getUuid());
        // 流程已经被使用,并且节点个数和流向个数不一致的情况
        if (isInUse) {
            if (StringUtils.isNotBlank(oldXml) && !checkFlowXml(configuration.getFlowElement(), oldFlowElement, uuid)) {
                throw new WorkFlowException("存在未办结的流程无法对流程的流向，和节点进行增删，请保存为新版本！");
            } else if (StringUtils.isNotBlank(oldJson) && !checkFlowXml(configuration.getFlowElement(), oldFlowElement, uuid)) {
                throw new WorkFlowException("存在未办结的流程无法对流程的流向，和节点进行增删，请保存为新版本！");
            }
        }

        // 检查子流程流向
        checkBranchTask(configuration);
        if ((StringUtils.isNotBlank(oldXml) && !StringUtils.equals(oldXml, configuration.asXML())) ||
                (StringUtils.isNotBlank(oldJson) && !StringUtils.equals(oldJson, configuration.asJSON()))) {
            saveLog(flowDefinition.getFlowSchemaUuid(), flowDefinition.getName(), oldXml, oldJson, configuration, false);
        }
    }

    /**
     * 签署意见校验设置保存
     *
     * @param flowDefinition
     * @param flowElement
     * @return void
     **/
    private void saveOpinionCheckSet(FlowDefinition flowDefinition, FlowElement flowElement) {
        List<WfOpinionCheckSetEntity> dtos = new ArrayList<>();
        List<OpinionCheckSetElement> opinionCheckSetElements = flowElement.getProperty().getOpinionCheckSets();
        if (CollectionUtils.isNotEmpty(opinionCheckSetElements)) {
            for (OpinionCheckSetElement opinionCheckSetElement : opinionCheckSetElements) {
                WfOpinionCheckSetEntity entity = new WfOpinionCheckSetEntity();
                entity.setFlowDefId(flowDefinition.getId());
                entity.setOpinionRuleUuid(opinionCheckSetElement.getOpinionRuleUuid());
                entity.setTaskIds(opinionCheckSetElement.getTaskIds());
                entity.setScene(opinionCheckSetElement.getId().replace("scene", ""));
                dtos.add(entity);
            }
        }
        opinionCheckSetFacadeService.saveOpinionCheckSetList(flowDefinition.getId(), dtos);
    }

    /**
     * 创建新的流程定义
     *
     * @param configuration
     * @param xml
     * @return
     */
    private FlowDefinition create(FlowConfiguration configuration, String xml) {
        // Element rootElement = document.getRootElement();

        // 创建流程定义
        FlowDefinition flowDefinition = new FlowDefinition();
        FlowElement flowElement = configuration.getFlowElement();// FlowDefinitionParser.parseFlow(xml);
        String name = flowElement.getName();// 名称
        String id = flowElement.getId();// 唯一标识(别名)
        String code = flowElement.getCode();// 编号
        String systemUnitId = flowElement.getSystemUnitId();// 系统归属单位ID
        String moduleId = flowElement.getModuleId();// 归属模块ID
        // 如果编号为空生成自动编号
        if (StringUtils.isBlank(code)) {
            code = idGeneratorService.generate(FlowDefinition.class, FLOW_DEF_CODE_PATTERN, false);
            //  rootElement.addAttribute(FlowDefConstants.FLOW_CODE, code);
            configuration.setCode(code);
        }
        // String version = flowElement.getVersion();// 版本号
        Double latestVersion = dao.getLatestVersionById(id);
        if (latestVersion != null) {
            latestVersion += 0.1;
        } else {
            latestVersion = 1d;
        }
        String categorySN = flowElement.getProperty().getCategorySN();// 分类
        String formId = flowElement.getProperty().getFormID();// 动态表单
        String active = flowElement.getProperty().getIsActive();// 是否启用
        String free = flowElement.getProperty().getIsFree();// 是否自由流程
        String mobileShow = flowElement.getProperty().getIsMobileShow();// 是否移动端展示
        String pcShowFlag = flowElement.getProperty().getPcShowFlag();// 是否PC端展示
        String multiJobFlowType = flowElement.getProperty().getMultiJobFlowType();// 多职流转设置
        String jobField = flowElement.getProperty().getJobField();// 职位选择字段
        // String isIndependent =
        // flowElement.getProperty().getIsIndependent();// 是否应用独立
        String equalFlowId = flowElement.getProperty().getEqualFlow().getId();// 等价流程
        String remark = flowElement.getProperty().getRemark();// 流程备注
        String customJsModule = flowElement.getProperty().getCustomJsModule();// 二开的JS模块
        Boolean autoUpdateTitle = Boolean.FALSE;// 是否自动更新标题
        if (StringUtils.isNotBlank(flowElement.getProperty().getAutoUpdateTitle())
                && "1".equals(flowElement.getProperty().getAutoUpdateTitle())) {
            autoUpdateTitle = Boolean.TRUE;
        }
        flowDefinition.setAutoUpdateTitle(autoUpdateTitle);
        flowDefinition.setName(name);
        flowDefinition.setId(id);
        flowDefinition.setCode(code);
        flowDefinition.setVersion(latestVersion);
        flowDefinition.setCategory(categorySN);
        flowDefinition.setSystemUnitId(systemUnitId);
        flowDefinition.setModuleId(moduleId);
        flowDefinition.setFormUuid(formId);
        flowDefinition.setApplyId(flowElement.getApplyId());
        /* add by huanglinchuan 2014.10.20 begin */
        flowDefinition.setTitleExpression(flowElement.getTitleExpression());
        /* add by huanglinchuan 2014.10.20 end */
        // FormDefinition formDefinition =
        // dytableApiFacade.getFormByUUID(formId);
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formId);
        if (formDefinition != null) {
            flowDefinition.setFormName(formDefinition.getName());
        }
        flowDefinition.setEnabled("1".equals(active));
        flowDefinition.setFreeed("1".equals(free));
        flowDefinition.setIsMobileShow("1".equals(mobileShow));
        flowDefinition.setPcShowFlag("1".equals(pcShowFlag));
        // flowDefinition.setIndependent("1".equals(isIndependent));
        flowDefinition.setEqualFlowId(equalFlowId);
        flowDefinition.setRemark(remark);
        flowDefinition.setMultiJobFlowType(multiJobFlowType);
        flowDefinition.setJobField(jobField);
        flowDefinition.setSystem(RequestSystemContextPathResolver.system());
        flowDefinition.setTenant(SpringSecurityUtils.getCurrentTenantId());

        // 二开JS模块
        if (StringUtils.isNotBlank(customJsModule)) {
            Map<String, Object> developJsonMap = Maps.newHashMap();
            developJsonMap.put(FlowConstant.CUSTOM_JS_MODULE, customJsModule);
            flowDefinition.setDevelopJson(JsonUtils.object2Json(developJsonMap));
        } else {
            String developJson = null;// 二开JS
            FlowDefinition oldFlowDefinition = dao.getById(id);
            if (oldFlowDefinition != null) {
                developJson = oldFlowDefinition.getDevelopJson();
            }
            flowDefinition.setDevelopJson(developJson);
        }
        dao.save(flowDefinition);

        // 设置UUID
        configuration.setUuid(flowDefinition.getUuid());
        configuration.setLastVersion(decimalFormat.format(flowDefinition.getVersion()));
//        rootElement.addAttribute(FlowDefConstants.FLOW_UUID, flowDefinition.getUuid());
//        rootElement.addAttribute(FlowDefConstants.FLOW_LASTVER, decimalFormat.format(flowDefinition.getVersion()));

        // 生成计时服务配置
        if (configuration.getDocument() != null) {
            createTimerConfig(flowDefinition, configuration.getDocument().getRootElement(), true);
        } else {
            createTimerConfig(flowDefinition, configuration.getFlowElement(), true);
        }

        // 创建流程规划
        String flowSchemaUuid = flowSchemaService.save(null, name, configuration);

        flowDefinition.setFlowSchemaUuid(flowSchemaUuid);
        dao.save(flowDefinition);

        dao.getSession().flush();

        // 检查子流程流向
        checkBranchTask(configuration);

        // 保存日志
//        FlowSchema flowSchema = flowSchemaDao.getOne(flowSchemaUuid);
//        saveLog(flowSchema, configuration, true);
        saveAcl(flowDefinition, flowElement);

        // 保存流程定义用户信息
        saveDefUser(flowDefinition, flowElement);

        return flowDefinition;
    }

    /**
     * @param flowDefinition
     * @param rootElement
     * @param upgrade
     */
    @SuppressWarnings("unchecked")
    private void createTimerConfig(FlowDefinition flowDefinition, Element rootElement, boolean upgrade) {
        TsTimerCategoryDto timerCategoryDto = timerCategoryFacadeService.getById("flowTiming");
        List<Element> timerElements = (List<Element>) rootElement.selectNodes("/flow/timers/timer");
        for (Element timerElement : timerElements) {
            Element timerConfigUuidElement = (Element) timerElement.selectSingleNode("timerConfigUuid");
            // 保存为新版本
            if (upgrade) {
                timerElement.remove(timerConfigUuidElement);
                timerConfigUuidElement = null;
            }
            if (timerConfigUuidElement == null || StringUtils.isBlank(timerConfigUuidElement.getText()) || upgrade) {
                // 增加计时器配置UUID
                addOrUpdateTimerConfigUuid(flowDefinition, timerElement, timerCategoryDto, null);
                // 删除空的冗余计时配置
                List<Element> elements = timerElement.selectNodes("timerConfigUuid");
                for (Element element : elements) {
                    if (StringUtils.equals(StringUtils.EMPTY, element.getText())) {
                        timerElement.remove(element);
                    }
                }
            } else {
                String timerConfigUuid = timerConfigUuidElement.getText();
                timerElement.remove(timerConfigUuidElement);
                addOrUpdateTimerConfigUuid(flowDefinition, timerElement, timerCategoryDto, timerConfigUuid);
                // 删除冗余的计时配置
                List<Element> elements = timerElement.selectNodes("timerConfigUuid");
                boolean visitedTimerConfigUuid = false;
                for (Element element : elements) {
                    if (visitedTimerConfigUuid || !StringUtils.equals(timerConfigUuid, element.getText())) {
                        timerElement.remove(element);
                    } else {
                        visitedTimerConfigUuid = true;
                    }
                }
            }
        }
    }

    private void createTimerConfig(FlowDefinition flowDefinition, FlowElement flowElement, boolean upgrade) {
        List<TimerElement> timerElements = flowElement.getTimers();
        if (CollectionUtils.isEmpty(timerElements)) {
            return;
        }
        TsTimerCategoryDto timerCategoryDto = timerCategoryFacadeService.getById("flowTiming");
        for (TimerElement timerElement : timerElements) {
            String timerConfigUuid = timerElement.getTimerConfigUuid();
            // 增加计时器配置UUID
            addOrUpdateTimerConfigUuid(flowDefinition, timerElement, timerCategoryDto, timerConfigUuid);
        }
    }

    /**
     * @param timerElement
     * @param timerCategoryDto
     * @param timerConfigUuid
     */
    private void addOrUpdateTimerConfigUuid(FlowDefinition flowDefinition, Element timerElement,
                                            TsTimerCategoryDto timerCategoryDto, String timerConfigUuid) {
        org.dom4j.Node introductionTypeNode = timerElement.selectSingleNode("introductionType");
        // 引用计时器，直接返回
        if (introductionTypeNode != null && StringUtils.equals("introduction", introductionTypeNode.getText())) {
            return;
        }

        // 计时器名称
        String name = flowDefinition.getName() + "_" + timerElement.selectSingleNode("name").getText() + "_"
                + flowDefinition.getVersion();
        // 计时器ID
        String id = flowDefinition.getId() + "_" + timerElement.selectSingleNode("timerId").getText() + "_"
                + flowDefinition.getVersion() + "_" + System.currentTimeMillis();
        // 计时器编号
        String code = Calendar.getInstance().getTime().getTime() + StringUtils.EMPTY;
        // 计时配置分类UUID
        String categoryUuid = timerCategoryDto.getUuid();
        // 计时方式
        String timingMode = TimerHelper.getTimingMode(timerElement.selectSingleNode("limitUnit").getText());
        // 时限类型
        String timeLimitType = getTimeLimitType(timerElement.selectSingleNode("limitTimeType").getText());
        // 时限
        String limitTimeType = timerElement.selectSingleNode("limitTimeType").getText();
        String limitTime1 = StringUtils.EMPTY;
        if (timerElement.selectSingleNode("limitTime1") != null) {
            limitTime1 = timerElement.selectSingleNode("limitTime1").getText();
        }
        String timeLimit = getTimeLimit(limitTimeType, limitTime1);
        // 时限单位
        org.dom4j.Node node = timerElement.selectSingleNode("timeLimitUnit");
        // 计时器没有升级或重新配置
        if (node == null) {
            return;
        }
        String timeLimitUnit = node.getText();
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
        String newTimerConfigUuid = null;

        // 更新配置
        if (StringUtils.isNotBlank(timerConfigUuid)) {
            TsTimerConfigDto dto = timerConfigFacadeService.getDto(timerConfigUuid);
            BeanUtils.copyProperties(timerConfigDto, dto, IdEntity.BASE_FIELDS);
            newTimerConfigUuid = timerConfigFacadeService.saveDto(dto);
        } else {
            newTimerConfigUuid = timerConfigFacadeService.saveDto(timerConfigDto);
        }

        Element timerConfigUuidNode = DocumentFactory.getInstance().createElement("timerConfigUuid");
        timerConfigUuidNode.setText(newTimerConfigUuid);
        timerElement.add(timerConfigUuidNode);
    }

    /**
     * @param timerElement
     * @param timerCategoryDto
     * @param timerConfigUuid
     */
    private void addOrUpdateTimerConfigUuid(FlowDefinition flowDefinition, TimerElement timerElement,
                                            TsTimerCategoryDto timerCategoryDto, String timerConfigUuid) {
        // 引用计时器，直接返回
        if (StringUtils.equals("introduction", timerElement.getIntroductionType())) {
            return;
        }

        // 计时器名称
        String name = flowDefinition.getName() + "_" + timerElement.getName() + "_" + flowDefinition.getVersion();
        // 计时器ID
        String id = flowDefinition.getId() + "_" + timerElement.getTimerId() + "_" + flowDefinition.getVersion() + "_" + System.currentTimeMillis();
        // 计时器编号
        String code = Calendar.getInstance().getTime().getTime() + StringUtils.EMPTY;
        // 计时配置分类UUID
        String categoryUuid = timerCategoryDto.getUuid();
        if (StringUtils.isBlank(categoryUuid)) {
            categoryUuid = createFlowTimingCategory();
        }
        // 计时方式
        String timingMode = timerElement.getLimitUnit();
        if (StringUtils.isNotBlank(timerElement.getLimitUnit())) {
            timingMode = TimerHelper.getTimingMode(timerElement.getLimitUnit());
        }
        String timingModeType = timerElement.getTimingModeType();
        String timingModeUnit = timerElement.getTimingModeUnit();
        if (StringUtils.isNotBlank(timingModeType) && StringUtils.isNotBlank(timingModeUnit)) {
            timingMode = TimerHelper.getTimingMode(timingModeType, timingModeUnit);
        }
        // 时限类型
        String timeLimitType = getTimeLimitType(timerElement.getLimitTimeType());
        // 时限
        String timeLimit = getTimeLimit(timerElement.getLimitTimeType(), timerElement.getLimitTime1());
        // 时限单位
        // org.dom4j.Node node = timerElement.selectSingleNode("timeLimitUnit");
        String timeLimitUnit = timerElement.getTimeLimitUnit();
        // 计时器没有升级或重新配置
        if (StringUtils.isBlank(timeLimitUnit)) {
            return;
        }
        // 计时包含启动时间点，1是0否，默认0
        boolean includeStartTimePoint = StringUtils.equals("1", timerElement.getIncludeStartTimePoint());
        // 自动推迟到下一工作时间起始点前，1是0否，默认0
        boolean autoDelay = StringUtils.equals("1", timerElement.getAutoDelay());
        String listener = TaskTimerListener.LISTENER_NAME;
        String remark = "流程计时器升级";

        TsTimerConfigDto timerConfigDto = new TsTimerConfigDto();
        timerConfigDto.setName(name);
        timerConfigDto.setId(id);
        timerConfigDto.setCode(code);
        timerConfigDto.setCategoryUuid(categoryUuid);
        timerConfigDto.setTimingMode(timingMode);
        timerConfigDto.setTimingModeType(timingModeType);
        timerConfigDto.setTimingModeUnit(timingModeUnit);
        timerConfigDto.setTimeLimitType(timeLimitType);
        timerConfigDto.setTimeLimit(timeLimit);
        timerConfigDto.setTimeLimitUnit(timeLimitUnit);
        timerConfigDto.setIncludeStartTimePoint(includeStartTimePoint);
        timerConfigDto.setAutoDelay(autoDelay);
        timerConfigDto.setListener(listener);
        timerConfigDto.setRemark(remark);
        timerConfigDto.setSystem(RequestSystemContextPathResolver.system());
        timerConfigDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        String newTimerConfigUuid = null;

        // 更新配置
        if (StringUtils.isNotBlank(timerConfigUuid)) {
            TsTimerConfigDto dto = timerConfigFacadeService.getDto(timerConfigUuid);
            BeanUtils.copyProperties(timerConfigDto, dto, IdEntity.BASE_FIELDS);
            newTimerConfigUuid = timerConfigFacadeService.saveDto(dto);
        } else {
            newTimerConfigUuid = timerConfigFacadeService.saveDto(timerConfigDto);
        }
        timerElement.setTimerConfigUuid(newTimerConfigUuid);
    }

    private String createFlowTimingCategory() {
        TsTimerCategoryFacadeService timerCategoryFacadeService = ApplicationContextHolder.getBean(TsTimerCategoryFacadeService.class);
        TsTimerCategoryDto timerCategoryDto = new TsTimerCategoryDto();
        timerCategoryDto.setId("flowTiming");
        timerCategoryDto.setCode("flowTiming");
        timerCategoryDto.setName("流程计时器");
        timerCategoryDto.setRemark("流程计时器分类");
        timerCategoryDto.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        timerCategoryDto.setSystem(RequestSystemContextPathResolver.system());
        timerCategoryDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        return timerCategoryFacadeService.saveDto(timerCategoryDto);
    }

    /**
     * @param limitTimeType
     * @return
     */
    private String getTimeLimitType(String limitTimeType) {
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
     * @param limitTimeType
     * @param limitTime1
     * @return
     */
    private String getTimeLimit(String limitTimeType, String limitTime1) {
        if (StringUtils.equals("1", limitTimeType)) {
            return limitTime1;
        } else if (StringUtils.equals("2", limitTimeType)) {
            return StringUtils.EMPTY;
        } else if (StringUtils.equals("3", limitTimeType)) {
            return StringUtils.EMPTY;
        }
        return limitTime1;
    }

    @Override
    public void saveAcl(FlowDefinition flowDefinition, FlowElement flowElement) {
        // 流程创建者，解析组织机构的选择值
        List<UserUnitElement> creators = flowElement.getProperty().getCreators();
        setFlowCreators(flowDefinition, creators);
        // 流程参与者，解析组织机构的选择值
        List<UserUnitElement> users = flowElement.getProperty().getUsers();
        setFlowUsers(flowDefinition, users);
        // 督办人
        List<UserUnitElement> monitors = flowElement.getProperty().getMonitors();
        setFlowSupervise(flowDefinition, monitors);
        // 监控人
        List<UserUnitElement> admins = flowElement.getProperty().getAdmins();
        setFlowMonitor(flowDefinition, admins);
        /* add by huanglinchuan 2014.10.28 begin */
        // 流程查看授权
        List<UserUnitElement> viewers = flowElement.getProperty().getViewers();
        setFlowViewers(flowDefinition, viewers);
        /* add by huanglinchuan 2014.10.28 end */

        // checkNodeParallelGateway(flowDefinition);
    }

    private void checkNodeParallelGateway(FlowDefinition flowDefinition) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        for (Node node : flowDelegate.getAllTaskNodes()) {
            if (node.getForkMode() == ForkMode.MULTI.getValue()
                    && !flowDelegate.isConditionTask(flowDelegate.getTaskNode(node.getId()))) {
                throw new RuntimeException("多路分支节点(" + node.getName() + ")的下一个节点必须是判断点");
            }
        }
    }


    /**
     * @param flowDefinition
     * @param flowElement
     */
    public void saveDefUser(FlowDefinition flowDefinition, FlowElement flowElement) {
        flowDefinitionUserService.sync(flowDefinition, flowElement);
    }

    /**
     * @param flowDefinition
     * @param monitors
     */
    private void setFlowSupervise(FlowDefinition flowDefinition, List<UserUnitElement> monitors) {
        // 过滤组组织机构ID
        List<UnitElement> supervises = new ArrayList<UnitElement>(0);
        if (monitors != null) {
            for (UnitElement unitElement : monitors) {
                Integer type = unitElement.getType();
                if (Integer.valueOf(1).equals(type)) {
                    supervises.add(unitElement);
                }
            }
        }

        // 督办人组织机构ID
        Set<String> superviseOrgIds = new HashSet<String>();
        for (UnitElement unitElement : supervises) {
            String sidValue = unitElement.getValue();
            if (StringUtils.isBlank(sidValue)) {
                continue;
            }
            List<String> sids = Arrays.asList(StringUtils.split(sidValue, Separator.SEMICOLON.getValue()));
            superviseOrgIds.addAll(sids);
        }

        String uuid = flowDefinition.getUuid();
        // 删除流程管理中定义的人员
        FlowManagementService flowManagementService = ApplicationContextHolder.getBean(FlowManagementService.class);
        flowManagementService.remove(uuid, ManagementType.SUPERVISE);

        // 管理员用户ID
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        List<String> allAdminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<String> allAdminIds = workflowOrgService.listCurrentTenantAdminIds(workflowOrgService.getOrgVersionId(FlowDelegateUtils.getFlowDelegate(flowDefinition)));
        superviseOrgIds.addAll(allAdminIds);
        flowManagementService.create(ManagementType.SUPERVISE, superviseOrgIds, uuid);
    }

    /* add by huanglinchuan 2014.10.28 begin */

    /**
     * @param flowDefinition
     * @param admins
     */
    private void setFlowMonitor(FlowDefinition flowDefinition, List<UserUnitElement> admins) {
        // 过滤组组织机构ID
        List<UnitElement> monitors = new ArrayList<UnitElement>(0);
        if (admins != null) {
            for (UnitElement unitElement : admins) {
                Integer type = unitElement.getType();
                if (Integer.valueOf(1).equals(type)) {
                    monitors.add(unitElement);
                }
            }
        }

        // 监控人组织机构ID
        Set<String> monitorOrgIds = new HashSet<String>();
        for (UnitElement unitElement : monitors) {
            String sidValue = unitElement.getValue();
            if (StringUtils.isBlank(sidValue)) {
                continue;
            }
            List<String> sids = Arrays.asList(StringUtils.split(sidValue, Separator.SEMICOLON.getValue()));
            monitorOrgIds.addAll(sids);
        }

        String uuid = flowDefinition.getUuid();
        // 删除流程管理中定义的人员
        FlowManagementService flowManagementService = ApplicationContextHolder.getBean(FlowManagementService.class);
        flowManagementService.remove(uuid, ManagementType.MONITOR);

        // 管理员用户ID
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        List<String> allAdminIds = orgApiFacade.queryAllAdminIdsByUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<String> allAdminIds = workflowOrgService.listCurrentTenantAdminIds(workflowOrgService.getOrgVersionId(FlowDelegateUtils.getFlowDelegate(flowDefinition)));
        monitorOrgIds.addAll(allAdminIds);
        flowManagementService.create(ManagementType.MONITOR, monitorOrgIds, uuid);
    }

    /* add by huanglinchuan 2014.10.28 end */

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#updateCategory(java.lang.String, java.lang.String)
     */
    @Override
    public void updateCategory(String oldCategoryCode, String newCategoryCode) {
        String hql = "update FlowDefinition t set t.category = :newCategoryCode where t.category = :oldCategoryCode";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("oldCategoryCode", oldCategoryCode);
        values.put("newCategoryCode", newCategoryCode);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#isMaxVersion(java.lang.String)
     */
    @Override
    public boolean isMaxVersion(String uuid) {
        FlowDefinition flowDefinition = dao.getOne(uuid);
        Double version = this.dao.getLatestVersionById(flowDefinition.getId());
        return version.equals(flowDefinition.getVersion());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#countByEntity(com.wellsoft.pt.bpm.engine.entity.FlowDefinition)
     */
    @Override
    public Long countByEntity(FlowDefinition example) {
        return this.dao.countByEntity(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getLatest(com.wellsoft.context.jdbc.support.Page)
     */
    @Override
    public void getLatest(Page<FlowDefinition> dataPage) {
        dao.getLatest(dataPage);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#countByCategory(java.lang.String)
     */
    @Override
    public Long countByCategory(String category) {
        return dao.countByCategory(category);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#countById(java.lang.String)
     */
    @Override
    public Long countById(String id) {
        return dao.countById(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getByCategory(java.lang.String)
     */
    @Override
    public List<FlowDefinition> getByCategory(String categorySN) {
        return dao.getByCategory(categorySN);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getByFormUuid(java.lang.String)
     */
    @Override
    public List<FlowDefinition> getByFormUuid(String formUuid) {
        return dao.getByFormUuid(formUuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getById(java.lang.String)
     */
    @Override
    public FlowDefinition getById(String id) {
        return dao.getById(id);
    }

    @Override
    public List<FlowDefinition> getByIds(Set<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return null;
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("ids", ids);

        return this.dao.listByNameSQLQuery("getFlowDefinitionListByIds", values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getLatestVersionById(java.lang.String)
     */
    @Override
    public Double getLatestVersionById(String id) {
        return dao.getLatestVersionById(id);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#flowDefinitionTreeQuery(java.lang.String, java.util.Map, java.lang.Class, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> flowDefinitionTreeQuery(String queryName, Map<String, Object> values,
                                                   PagingInfo pagingInfo) {
        return dao.listQueryItemByNameHQLQuery(queryName, values, pagingInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getAllUuids()
     */
    @Override
    public List<String> getAllUuids() {
        return dao.listCharSequenceByHQL("select uuid from FlowDefinition t", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getLatestUpdatedUuids(int)
     */
    @Override
    public List<String> getLatestUpdatedUuids(int count) {
        String hql = "select uuid from FlowDefinition where modifyTime is not null order by modifyTime desc";
        return dao.listCharSequenceByHqlAndPage(hql, null, new PagingInfo(1, count));
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#findByExample(com.wellsoft.pt.bpm.engine.entity.FlowDefinition)
     */
    @Override
    public List<FlowDefinition> findByExample(FlowDefinition example) {
        return dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getAllUuidsById(java.lang.String)
     */
    @Override
    public List<String> getAllUuidsById(String flowDefId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowDefId", flowDefId);
        return dao.listCharSequenceByHQL("select uuid from FlowDefinition t where t.id = :flowDefId", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getFlowNameByTaskInstUuid(java.lang.String)
     */
    @Override
    public String getFlowNameByTaskInstUuid(String taskInstUuid) {
        String hql = "select t1.name from FlowDefinition t1 where exists(select t2.uuid from TaskInstance t2 where t2.uuid = :taskInstUuid and t1.uuid = t2.flowDefinition.uuid)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        List<String> flowNames = dao.listCharSequenceByHQL(hql, values);
        if (CollectionUtils.isEmpty(flowNames)) {
            return StringUtils.EMPTY;
        }
        return flowNames.get(0);
    }

    @Override
    public Select2QueryData loadSelectDataWorkflowDefinition(Select2QueryInfo select2QueryInfo) {
        Map<String, Object> params = Maps.newHashMap();
        String moduleId = select2QueryInfo.getOtherParams("moduleId");
        String idProperty = select2QueryInfo.getOtherParams("idProperty", "id");
        String excludeModuleIds = select2QueryInfo.getOtherParams("excludeModuleIds");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(moduleId)) {
            params.put("moduleId", moduleId);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(excludeModuleIds)) {
            params.put("excludeModuleIds", Arrays.asList(excludeModuleIds.split(Separator.SEMICOLON.getValue())));
        }
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId");
        if (org.apache.commons.lang3.StringUtils.isNotBlank(systemUnitId)) {
            params.put("systemUnitId", systemUnitId);
        }
        List<FlowDefinition> formDefinitions = this.dao.listByNameHQLQuery("queryAllWorkflowDefinition", params);
        if (StringUtils.equals("id", idProperty)) {
            Map<String, String> idMap = Maps.newHashMap();
            formDefinitions = formDefinitions.stream().sorted(Comparator.comparing(FlowDefinition::getVersion))
                    .filter(definition -> {
                        if (idMap.containsKey(definition.getId())) {
                            return false;
                        }
                        idMap.put(definition.getId(), definition.getId());
                        return true;
                    }).collect(Collectors.toList());
        }
        return new Select2QueryData(formDefinitions, idProperty, "name");
    }

    @Override
    public long countBySystemUnitId(String systemUnitId) {
        long count = this.dao.countByHQL("select count(uuid) from FlowDefinition ", null);
        return count;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.FlowDefinitionService#getFlowDelegate(java.lang.String)
     */
    @Override
    public FlowDelegate getFlowDelegate(String flowDefUuid) {
        FlowDefinition flowDefinition = this.getOne(flowDefUuid);
        return FlowDelegateUtils.getFlowDelegate(flowDefinition);
    }

    /**
     * 保存流程分类的管理操作日志
     *
     * @param newFlowDefinition      新的的流程定义数据
     * @param oldFlowDefinition      旧的流程定义数据 新增时为null
     * @param logManageOperationEnum 操作类型
     * @return void
     **/
    @Override
    public void saveLogManageOperation(FlowDefinition newFlowDefinition, FlowDefinition oldFlowDefinition,
                                       LogManageOperationEnum logManageOperationEnum) {
        SaveLogFlowDefinitionDto oldFlowDef = toSaveLogFlowDefinitionDto(oldFlowDefinition);
        saveLogManageOperationByOldFlow(newFlowDefinition, oldFlowDef, logManageOperationEnum);

    }

    /**
     * 保存流程分类的管理操作日志
     *
     * @param newFlowDefinition      新的的流程定义数据
     * @param oldFlowDefinition      旧的流程定义数据 新增时为null
     * @param logManageOperationEnum 操作类型
     * @return void
     **/
    @Override
    public void saveLogManageOperation(FlowDefinition newFlowDefinition, FlowDefinition oldFlowDefinition,
                                       FlowSchema oldflowSchema, LogManageOperationEnum logManageOperationEnum) {
        SaveLogFlowDefinitionDto oldFlowDef = toSaveLogFlowDefinitionDto(oldFlowDefinition, oldflowSchema);
        saveLogManageOperationByOldFlow(newFlowDefinition, oldFlowDef, logManageOperationEnum);

    }

    @Override
    public FlowDefinition getByFlowSchemaUuid(String flowSchemaUuid) {
        List<FlowDefinition> flowDefinitions = this.dao.listByFieldEqValue("flowSchemaUuid", flowSchemaUuid);
        if (CollectionUtils.isNotEmpty(flowDefinitions)) {
            return flowDefinitions.get(0);
        }
        return null;
    }

    @Override
    public List<String> listFormUuidByCategoryUuidsAndFlowDefIds(List<String> categoryUuids, List<String> flowDefIds) {
        String hql = "select t.formUuid as formUuid from FlowDefinition t where 1 = 1 ";
        Map<String, Object> params = Maps.newHashMap();
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            params.put("system", system);
            hql += " and (exists (select pm.moduleId from AppProdModuleEntity pm, AppSystemInfoEntity si where pm.prodVersionUuid = si.prodVersionUuid and t.id = pm.moduleId and si.system = :system) or t.system = :system)";
        }
        if (CollectionUtils.isNotEmpty(categoryUuids)) {
            hql += " and t.category in (:categoryUuids) ";
            params.put("categoryUuids", categoryUuids);
        }
        if (CollectionUtils.isNotEmpty(flowDefIds)) {
            hql += " and t.id in (:flowDefIds) ";
            params.put("flowDefIds", flowDefIds);
        }
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * 流程定义对象转换成要保存日志-流程定义对象
     *
     * @param oldFlowDefinition
     * @return com.wellsoft.pt.bpm.engine.dto.SaveLogFlowDefinitionDto
     **/
    private SaveLogFlowDefinitionDto toSaveLogFlowDefinitionDto(FlowDefinition oldFlowDefinition) {
        return toSaveLogFlowDefinitionDto(oldFlowDefinition, null);
    }

    /**
     * 流程定义对象转换成要保存日志-流程定义对象
     *
     * @param oldFlowDefinition
     * @return com.wellsoft.pt.bpm.engine.dto.SaveLogFlowDefinitionDto
     **/
    private SaveLogFlowDefinitionDto toSaveLogFlowDefinitionDto(FlowDefinition oldFlowDefinition,
                                                                FlowSchema oldflowSchema) {
        String oldXml = null;
        SaveLogFlowDefinitionDto oldFlowDef = null;
        try {
            if (oldFlowDefinition != null) {
                if (oldflowSchema != null) {
                    oldXml = flowSchemeService.getFlowXml(oldFlowDefinition, oldflowSchema);
                } else {
                    oldXml = flowSchemeService.getFlowXml(oldFlowDefinition.getUuid());
                }

                oldFlowDef = new SaveLogFlowDefinitionDto();
                oldFlowDef.setId(oldFlowDefinition.getId());
                oldFlowDef.setName(oldFlowDefinition.getName());
                oldFlowDef.setOldXml(oldXml);
                oldFlowDef.setVersion(oldFlowDefinition.getVersion());
            }
        } catch (Exception e) {
            logger.error("获取流程定义的XML内容报错：", e);
        }
        return oldFlowDef;
    }

    /**
     * 保存流程分类的管理操作日志
     *
     * @param newFlowDefinition 新的的流程定义数据
     * @param oldFlowDefinition 旧的流程定义数据 新增时为null
     * @return void
     **/
    private void saveLogManageOperationByOldFlow(FlowDefinition newFlowDefinition,
                                                 SaveLogFlowDefinitionDto oldFlowDefinition, LogManageOperationEnum logManageOperationEnum) {
        SaveLogManageOperationDto dto = new SaveLogManageOperationDto();
        dto.setDataType(LogManageDataTypeEnum.FlowDef.getName());
        dto.setDataTypeId(LogManageDataTypeEnum.FlowDef.getValue());
        dto.setModuleId(LogManageModuleEnum.FlowDef.getValue());
        dto.setModuleName(LogManageModuleEnum.FlowDef.getName());
        String newXml = null;
        String oldXml = null;
        Boolean ingoreSave = false;
        try {
            if (newFlowDefinition != null) {
                newXml = flowSchemeService.getFlowXml(newFlowDefinition.getUuid());
            }
            if (oldFlowDefinition != null) {
                oldXml = oldFlowDefinition.getOldXml();
            }
        } catch (Exception e) {
            logger.error("获取流程定义的XML内容报错：", e);
        }

        switch (logManageOperationEnum) {
            case add:
                dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                dto.setAfterMessageValue(newXml);
                dto.setBeforeDataName("");
                dto.setBeforeMessageValue("");
                dto.setDataId(newFlowDefinition.getId());
                dto.setDataNameInfo("");
                dto.setOperation(LogManageOperationEnum.add.getName());
                dto.setOperationId(LogManageOperationEnum.add.getValue());
                break;
            case edit:
                dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                dto.setAfterMessageValue(newXml);
                dto.setBeforeDataName(oldFlowDefinition.getName() + "(" + oldFlowDefinition.getVersion() + ")");
                dto.setBeforeMessageValue(oldXml);
                dto.setDataId(newFlowDefinition.getId());
                dto.setDataNameInfo(
                        "流程定义名称：" + "从【" + oldFlowDefinition.getName() + "】改为【" + newFlowDefinition.getName() + "】");
                dto.setOperation(LogManageOperationEnum.edit.getName());
                dto.setOperationId(LogManageOperationEnum.edit.getValue());
                break;
            case delete:
                dto.setAfterDataName("");
                dto.setAfterMessageValue("");
                dto.setBeforeDataName(oldFlowDefinition.getName() + "(" + oldFlowDefinition.getVersion() + ")");
                dto.setBeforeMessageValue(oldXml);
                dto.setDataId(oldFlowDefinition.getId());
                dto.setDataNameInfo("");
                dto.setOperation(LogManageOperationEnum.delete.getName());
                dto.setOperationId(LogManageOperationEnum.delete.getValue());
                break;
            case defImport:
                if (oldFlowDefinition == null) {
                    // 定义导入-同新增
                    dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                    dto.setAfterMessageValue(newXml);
                    dto.setBeforeDataName("");
                    dto.setBeforeMessageValue("");
                    dto.setDataId(newFlowDefinition.getId());
                    dto.setDataNameInfo("");
                } else {
                    // 定义导入-同编辑
                    dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                    dto.setAfterMessageValue(newXml);
                    dto.setBeforeDataName(oldFlowDefinition.getName() + "(" + oldFlowDefinition.getVersion() + ")");
                    dto.setBeforeMessageValue(oldXml);
                    dto.setDataId(newFlowDefinition.getId());
                    dto.setDataNameInfo(
                            "流程定义名称：" + "从【" + oldFlowDefinition.getName() + "】改为【" + newFlowDefinition.getName() + "】");
                }
                dto.setOperation(LogManageOperationEnum.defImport.getName());
                dto.setOperationId(LogManageOperationEnum.defImport.getValue());
                break;
            case defExport:
                dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                dto.setAfterMessageValue(newXml);
                dto.setBeforeDataName("");
                dto.setBeforeMessageValue("");
                dto.setDataId(newFlowDefinition.getId());
                dto.setDataNameInfo("");
                dto.setOperation(LogManageOperationEnum.defExport.getName());
                dto.setOperationId(LogManageOperationEnum.defExport.getValue());
                break;
            case flowListExport:
                break;
            case copyFlow:
                dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                dto.setAfterMessageValue(newXml);
                dto.setBeforeDataName("");
                dto.setBeforeMessageValue("");
                dto.setDataId(newFlowDefinition.getId());
                dto.setDataNameInfo("从流程【" + oldFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")"
                        + "】复制出流程【" + newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")" + "】");
                dto.setOperation(LogManageOperationEnum.copyFlow.getName());
                dto.setOperationId(LogManageOperationEnum.copyFlow.getValue());
                break;
            case saveFlowNew:
                dto.setAfterDataName(newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")");
                dto.setAfterMessageValue(newXml);
                dto.setBeforeDataName("");
                dto.setBeforeMessageValue("");
                dto.setDataId(newFlowDefinition.getId());
                dto.setDataNameInfo("从流程【" + oldFlowDefinition.getName() + "(" + oldFlowDefinition.getVersion() + ")"
                        + "】保存新版本出流程【" + newFlowDefinition.getName() + "(" + newFlowDefinition.getVersion() + ")" + "】");
                dto.setOperation(LogManageOperationEnum.saveFlowNew.getName());
                dto.setOperationId(LogManageOperationEnum.saveFlowNew.getValue());
                break;
            default:
                ingoreSave = Boolean.TRUE;
                break;
        }
        if (!ingoreSave) {
            logManageOperationFacadeService.saveLogManageOperation(dto, DataParseTypeEnum.Xml);
        }
    }

}
