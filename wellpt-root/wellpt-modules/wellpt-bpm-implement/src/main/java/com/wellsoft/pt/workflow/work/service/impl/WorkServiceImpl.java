/*
 * @(#)2012-11-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.context.util.reflection.ServiceInvokeUtils;
import com.wellsoft.context.util.web.JsonDataServicesContextHolder;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppCodeI18nService;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplate;
import com.wellsoft.pt.basicdata.printtemplate.facade.service.PrintTemplateFacadeService;
import com.wellsoft.pt.basicdata.serialnumber.support.SerialNumberBuildParams;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.access.FlowPermissionEvaluator;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.core.*;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.enums.*;
import com.wellsoft.pt.bpm.engine.exception.PrintTemplateNotFoundException;
import com.wellsoft.pt.bpm.engine.exception.PrintTemplateStreamIsNullException;
import com.wellsoft.pt.bpm.engine.exception.TaskDataChangedException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.executor.RollBackTask;
import com.wellsoft.pt.bpm.engine.executor.RollBackTaskInfo;
import com.wellsoft.pt.bpm.engine.executor.RollbackTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.form.Button;
import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityItem;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStack;
import com.wellsoft.pt.bpm.engine.parser.activity.TaskActivityStackFactary;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.service.impl.FlowServiceImpl;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.support.groupchat.FlowGroupChatProvider;
import com.wellsoft.pt.bpm.engine.support.groupchat.StartGroupChat;
import com.wellsoft.pt.bpm.engine.util.*;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.common.i18n.service.DataI18nService;
import com.wellsoft.pt.common.marker.entity.ReadMarker;
import com.wellsoft.pt.common.marker.service.ReadMarkerService;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.dyform.facade.dto.*;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.data.exceptions.FormDataValidateException;
import com.wellsoft.pt.dyform.implement.data.utils.FormDataHandler;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.ControlTypeUtils;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.DyShowType;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumFieldPropertyName;
import com.wellsoft.pt.dyform.implement.definition.constant.DyFormConfig.EnumHideSubFormOperateBtn;
import com.wellsoft.pt.dyform.implement.definition.entity.FormDefinition;
import com.wellsoft.pt.dyform.implement.definition.util.dyform.FormDefinitionHandler;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.log.LogEvent;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.support.ContextLogs;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.acl.entity.AclTaskEntry;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.entity.FlowOpinion;
import com.wellsoft.pt.workflow.enums.SortFieldEnum;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import com.wellsoft.pt.workflow.listener.WfFlowBusinessDirectionListener;
import com.wellsoft.pt.workflow.listener.WfFlowBusinessFlowListener;
import com.wellsoft.pt.workflow.listener.WfFlowBusinessTaskListener;
import com.wellsoft.pt.workflow.service.FlowOpinionService;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import com.wellsoft.pt.workflow.work.bean.*;
import com.wellsoft.pt.workflow.work.service.WorkService;
import com.wellsoft.pt.workflow.work.vo.SubTaskDataVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-26.1	zhulh		2012-11-26		Create
 * </pre>
 * @date 2012-11-26
 */
@Service
@Transactional(readOnly = true)
public class WorkServiceImpl implements WorkService {

    /**
     * 流程-流程实例-workbean缓存是否启用
     **/
    private static final String WORKFLOW_WORKBEAN_ENABLE = "workflow_workbean_enable";

    private static final Pattern CHINESE_PATTERN =
            Pattern.compile("[\\u4e00-\\u9fa5\\u3000-\\u303F\\uFF00-\\uFFEF]+");
    /**
     * 查询最高版本的流程定义
     */
    // private static final String QUERY_FLOW_DEFINITION_MAX_VERSION =
    // "from FlowDefinition flow_def where flow_def.enabled = true "
    // +
    // "and exists(select id, max(version) from FlowDefinition wf_flow_definition "
    // +
    // "group by id having wf_flow_definition.id = flow_def.id and max(version) =
    // flow_def.version)";

    private static final String QUERY_FLOW_WHERE_HQL = "o.enabled = true "
            + "and exists(select id, max(version) from FlowDefinition wf_flow_definition "
            + "group by id, enabled having wf_flow_definition.id = o.id and max(version) = o.version and enabled = true)";
    private static final String QUERY_RECENT_USE_FLOW_WHERE_HQL = " and exists (select 1 from RecentUseEntity recentuse "
            + "where  recentuse.objectIdIdentity = o.id and recentuse=.moduleId='WORKFLOW' and recentuse.userId=:userId )";
    @Autowired
    protected TaskService taskService;
    @Autowired
    protected FlowService flowService;
    @Autowired
    protected AclTaskService aclTaskService;
    @Autowired
    protected ReadMarkerService readMarkerService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private TaskBranchService taskBranchService;
    @Autowired
    private FlowDefinitionService flowDefinitionService;
    @Autowired
    private FlowInstanceService flowInstanceService;
    @Autowired
    private TaskInstanceService taskInstanceService;
    @Autowired
    private FlowSchemeService flowSchemeService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;
    @Autowired
    private TaskSubFlowService taskSubFlowService;
    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired
    private TaskActivityService taskActivityService;

    @Autowired
    private TaskOperationService taskOperationService;

    @Autowired
    private TaskDelegationService taskDelegationService;

    @Autowired
    private TaskTimerLogService taskTimerLogService;

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private IdentityService identityService;

    @Autowired
    private TaskFormOpinionService taskFormOpinionService;

    @Autowired
    private FlowOpinionService flowOpinionService;

    @Autowired
    private NativeDao nativeDao;

    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private FlowPermissionEvaluator flowPermissionEvaluator;

    @Autowired
    private FlowSamplerService flowSamplerService;

    @Autowired
    private FlowIndexDocumentService flowIndexDocumentService;

    @Autowired
    private PrintTemplateFacadeService printTemplateFacadeService;

    @Autowired
    private TaskOperationTempService taskOperationTempService;

    @Autowired
    private DyFormFacade dyFormApiFacade;

    @Autowired
    private WfFlowSettingService flowSettingService;
    @Autowired
    private AppDefElementI18nService appDefElementI18nService;
    @Autowired
    private OrgFacadeService orgFacadeService;
    @Autowired
    private DataI18nService dataI18nService;
    @Autowired
    private TranslateService translateService;
    @Autowired
    private AppCodeI18nService appCodeI18nService;
    @Autowired
    private WfTaskTodoTempService taskTodoTempService;

    @Autowired(required = false)
    private List<FlowGroupChatProvider> flowGroupChatProviders;

    /**
     * 填充任务数据
     *
     * @param workBean
     * @param user
     * @param taskInstUuid
     * @param taskData
     */
    private static void fillTaskData(WorkBean workBean, UserDetails user, String taskInstUuid,
                                     List<String> opinionLogUuids, TaskData taskData) {
        String userId = user.getUserId();
        // 设置签署意见
        String key = taskInstUuid + userId;
        taskData.setOpinionValue(key, workBean.getOpinionValue());
        taskData.setOpinionLabel(key, workBean.getOpinionLabel());
        taskData.setOpinionText(key, workBean.getOpinionText());
        taskData.setOpinionFiles(key, workBean.getOpinionFiles());
        if (workBean.getActionCode() != null) {
            taskData.setActionCode(taskInstUuid, workBean.getActionCode());
        }

        // 设置办理人标识
        taskData.setTaskIdentityUuid(key, workBean.getTaskIdentityUuid());

        // ACL角色
        taskData.setAclRole(workBean.getAclRole());
        // 转办、会签、加签查看表单方式
        taskData.setViewFormMode(workBean.getViewFormMode());

        // 操作动作及操作类型
        taskData.setAction(key, workBean.getAction());
        taskData.setActionType(key, workBean.getActionType());

        // 意见日志信息
        if (CollectionUtils.isNotEmpty(opinionLogUuids)) {
            if (StringUtils.isNotBlank(taskInstUuid)) {
                taskData.setTaskFormOpinionLogUuids(taskInstUuid + userId, opinionLogUuids);
            } else {
                taskData.setTaskFormOpinionLogUuids(workBean.getTaskId() + userId, opinionLogUuids);
            }
        }
    }

    /**
     * @param object
     * @return
     */
    private static final String objectToString(Object object) {
        return object == null ? null : object.toString();
    }

    /**
     * @param dyFormData
     * @param rawFields
     * @return
     */
    private static List<String> getExistsFields(DyFormData dyFormData, List<String> rawFields, String formUuid) {
        if (rawFields == null || rawFields.isEmpty()) {
            return rawFields;
        }
        List<String> returnFields = new ArrayList<String>();
        for (String fieldName : rawFields) {
            if (dyFormData.isFieldExist(fieldName, formUuid)) {
                returnFields.add(fieldName);
            }
        }
        return returnFields;
    }

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param editFieldMap
     * @param onlyReadFieldMap
     * @param formDefinition
     * @param subformDefinionMap
     */
    private static void fillEditAndOnlyReadFieldMap(String formUuid, Map<String, List<String>> editFieldMap,
                                                    Map<String, List<String>> onlyReadFieldMap, DyFormFormDefinition formDefinition,
                                                    Map<String, DyformSubformFormDefinition> subformDefinionMap,
                                                    Map<String, DyFormFormDefinition> templateDefinionMap) {
        // 主表字段为空的情况
        List<DyformFieldDefinition> fieldDefintions = formDefinition.doGetFieldDefintions();
        if (!editFieldMap.containsKey(formUuid)) {
            onlyReadFieldMap.put(formUuid, new ArrayList<String>(0));
            for (DyformFieldDefinition dyformFieldDefinition : fieldDefintions) {
                // bug#48460 附件没有编辑设置，不用设置只读域
                if (ControlTypeUtils.isInputModeEqAttach(dyformFieldDefinition.getInputMode())) {
                    continue;
                }
                onlyReadFieldMap.get(formUuid).add(dyformFieldDefinition.getFieldName());
            }
        } else {
            // 主表字段不为空的情况
            for (DyformFieldDefinition dyformFieldDefinition : fieldDefintions) {
                // bug#48460 附件没有编辑设置，不用设置只读域
                if (ControlTypeUtils.isInputModeEqAttach(dyformFieldDefinition.getInputMode())) {
                    continue;
                }
                if (!editFieldMap.get(formUuid).contains(dyformFieldDefinition.getFieldName())) {
                    if (!onlyReadFieldMap.containsKey(formUuid)) {
                        onlyReadFieldMap.put(formUuid, new ArrayList<String>(0));
                    }
                    onlyReadFieldMap.get(formUuid).add(dyformFieldDefinition.getFieldName());
                }
            }
        }

        // 从表字段
        for (String subformKey : subformDefinionMap.keySet()) {
            List<DyformSubformFieldDefinition> subformDyformFieldDefinitions = subformDefinionMap.get(subformKey)
                    .getSubformFieldDefinitions();
            if (!onlyReadFieldMap.containsKey(subformKey)) {
                onlyReadFieldMap.put(subformKey, new ArrayList<String>(0));
            }
            if (!editFieldMap.containsKey(subformKey)) {
                editFieldMap.put(subformKey, new ArrayList<String>(0));
            }
            if (!editFieldMap.get(subformKey).contains(TaskForm.getAddBtnField(subformKey))) {
                onlyReadFieldMap.get(subformKey).add(TaskForm.getAddBtnField(subformKey));
            }
            if (!editFieldMap.get(subformKey).contains(TaskForm.getDelBtnField(subformKey))) {
                onlyReadFieldMap.get(subformKey).add(TaskForm.getDelBtnField(subformKey));
            }
            if (!editFieldMap.get(subformKey).contains(TaskForm.getImpBtnField(subformKey))) {
                onlyReadFieldMap.get(subformKey).add(TaskForm.getImpBtnField(subformKey));
            }
            if (!editFieldMap.get(subformKey).contains(TaskForm.getExpBtnField(subformKey))) {
                onlyReadFieldMap.get(subformKey).add(TaskForm.getExpBtnField(subformKey));
            }
            if (!editFieldMap.get(subformKey).contains(TaskForm.getAllBtnField(subformKey))) {
                onlyReadFieldMap.get(subformKey).add(TaskForm.getAllBtnField(subformKey));
            }
            // 从表字段为空的情况
            if (!editFieldMap.containsKey(subformKey)) {
                for (DyformSubformFieldDefinition subformDyformFieldDefinition : subformDyformFieldDefinitions) {
                    onlyReadFieldMap.get(subformKey).add(subformDyformFieldDefinition.getName());
                }
            } else {
                // 如果只设置了按钮则忽略掉其他域
                // if (isAllBbnField(subformKey, editFieldMap.get(subformKey)))
                // {
                // continue;
                // }
                // 从表字段不为空的情况
                for (DyformSubformFieldDefinition subformDyformFieldDefinition : subformDyformFieldDefinitions) {
                    if (!editFieldMap.get(subformKey).contains(subformDyformFieldDefinition.getName())) {
                        onlyReadFieldMap.get(subformKey).add(subformDyformFieldDefinition.getName());
                    }
                }
            }
        }

        // 子表单
        for (String templateUuid : templateDefinionMap.keySet()) {
            DyFormFormDefinition dyFormFormDefinition = templateDefinionMap.get(templateUuid);
            List<DyformFieldDefinition> templateFieldDefinitions = dyFormFormDefinition.doGetFieldDefintions();
            if (!onlyReadFieldMap.containsKey(templateUuid)) {
                onlyReadFieldMap.put(templateUuid, new ArrayList<String>(0));
            }
            // 子表单为空的情况
            if (!editFieldMap.containsKey(templateUuid)) {
                editFieldMap.put(templateUuid, new ArrayList<String>(0));
                for (DyformFieldDefinition templateFieldDefinition : templateFieldDefinitions) {
                    onlyReadFieldMap.get(templateUuid).add(templateFieldDefinition.getName());
                }
            } else {
                if (!editFieldMap.containsKey(templateUuid)) {
                    editFieldMap.put(templateUuid, new ArrayList<String>(0));
                }
                // 子表单不为空的情况
                for (DyformFieldDefinition templateFieldDefinition : templateFieldDefinitions) {
                    if (!editFieldMap.get(templateUuid).contains(templateFieldDefinition.getName())) {
                        onlyReadFieldMap.get(templateUuid).add(templateFieldDefinition.getName());
                    }
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param editFields
     * @return
     */
    private static boolean hasAllBbnField(String formUuid, List<String> editFields) {
        if (editFields.isEmpty()) {
            return false;
        }
        for (String editField : editFields) {
            if (TaskForm.isAllBtnField(formUuid, editField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param editFields
     * @return
     */
    private static boolean isAddBbnField(String formUuid, List<String> editFields) {
        if (editFields.isEmpty()) {
            return false;
        }
        for (String editField : editFields) {
            if (TaskForm.isAddBtnField(formUuid, editField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param editFields
     * @return
     */
    private static boolean isDelBbnField(String formUuid, List<String> editFields) {
        if (editFields.isEmpty()) {
            return false;
        }
        for (String editField : editFields) {
            if (TaskForm.isDelBtnField(formUuid, editField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param editFields
     * @return
     */
    private static boolean isImpBbnField(String formUuid, List<String> editFields) {
        if (editFields.isEmpty()) {
            return false;
        }
        for (String editField : editFields) {
            if (TaskForm.isImpBtnField(formUuid, editField)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 如何描述该方法
     *
     * @param editFields
     * @return
     */
    private static boolean isExpBbnField(String formUuid, List<String> editFields) {
        if (editFields.isEmpty()) {
            return false;
        }
        for (String editField : editFields) {
            if (TaskForm.isExpBtnField(formUuid, editField)) {
                return true;
            }
        }
        return false;
    }

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
     * @param formDefinition
     * @return
     */
    private static Map<String, DyFormFormDefinition> getTemplateDefinionMap(DyFormFormDefinition formDefinition) {
        Map<String, DyFormFormDefinition> map = new HashMap<String, DyFormFormDefinition>(0);
        List<String> templateUuids = formDefinition.doGetTemplateUuids();
        if (templateUuids != null) {
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            for (String templateUuid : templateUuids) {
                map.put(templateUuid, dyFormFacade.getFormDefinition(templateUuid));
            }
        }
        return map;
    }

//    /**
//     * (non-Javadoc)
//     *
//     * @see com.wellsoft.pt.workflow.work.service.WorkService#getUsers(java.util.List)
//     */
//    @Override
//    public List<MultiOrgUserAccount> getUsers(List<String> userIds) {
//        return orgApiFacade.queryUserAccountListByIds(userIds);
//    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getAllUsers()
     */
//    @Override
//    public List<User> queryForUsers(String queryValue, List<String> limitUserIds) {
//        Map<String, Object> values = new HashMap<String, Object>();
//        if (StringUtils.isNotBlank(queryValue)) {
//            values.put("loginName", queryValue);
//            values.put("userName", queryValue);
//            values.put("departmentName", queryValue);
//            values.put("majorJobName", queryValue);
//            /* add by huanglinchuan 2015.3.22 begin */
//            values.put("pinyin", queryValue);
//            /* add by huanglinchuan 2015.3.22 end */
//        }
//        if (limitUserIds != null && !limitUserIds.isEmpty()) {
//            values.put("limitUserIds", limitUserIds);
//        }
//        PagingInfo pagingInfo = new PagingInfo();
//        pagingInfo.setAutoCount(false);
//        pagingInfo.setCurrentPage(1);
//        pagingInfo.setPageSize(200);
//        List<User> users = this.nativeDao.namedQuery("getAllUsersQuery", values, User.class, pagingInfo);
//        return users;
//    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#queryUsers(java.util.List, java.lang.String)
     */
//    @Override
//    public List<QueryItem> queryUsers(List<String> userIds, String queryValue) {
//        /*
//         * Map<String, Object> values = new HashMap<String, Object>(); if
//         * (StringUtils.isNotBlank(queryValue)) { values.put("loginName", queryValue);
//         * values.put("userName", queryValue); } values.put("userIds", userIds); return
//         * this.dao.namedQuery("userChooseQuery", values, QueryItem.class);
//         */
//        return null;
//    }

    /*
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#query(com.wellsoft.
     * pt.core.support.QueryInfo)
     */
    @Override
    public List<FlowDefinition> query(QueryInfo queryInfo) {
        return doFlowDefinitionQuery(queryInfo, false, false);
    }

    @Override
    public List<FlowDefinition> queryRecentUse(QueryInfo queryInfo, boolean isMobile) {
        return doFlowDefinitionQuery(queryInfo, isMobile, true);
    }

    /**
     * @param queryInfo
     * @return
     */
    private List<FlowDefinition> doFlowDefinitionQuery(QueryInfo queryInfo, boolean isMobile, boolean recentUse) {
        Integer first = queryInfo.getPagingInfo().getFirst();
        Integer pageSize = queryInfo.getPagingInfo().getPageSize();
        boolean autoCount = queryInfo.getPagingInfo().isAutoCount();
        String orderBy = queryInfo.getOrderBy();
        List<Permission> permissions = new ArrayList<Permission>();
        permissions.add(BasePermission.CREATE);
        String userId = SpringSecurityUtils.getCurrentUserId();
//        Set<String> orgIds = orgApiFacade.getUserOrgIds(userId);
        Set<String> orgIds = workflowOrgService.getUserRelatedIds(userId);
        List<String> sids = new ArrayList<String>();
        for (String orgId : orgIds) {
            String sid = orgId;
            if (sid.startsWith(IdPrefix.USER.getValue())) {
                continue;
            }
            sid = WorkFlowAclSid.ROLE_FLOW_CREATOR + "_" + sid;
            sids.add(sid);
        }
        if (!sids.contains(userId)) {
            sids.add(userId);
        }
        sids.add(WorkFlowAclSid.ROLE_FLOW_ALL_CREATOR.name());
        String system = RequestSystemContextPathResolver.system();
        FlowDefinitionQuery flowDefinitionQuery = FlowEngine.getInstance().createQuery(FlowDefinitionQuery.class);
        flowDefinitionQuery.permission(userId, permissions);
        flowDefinitionQuery.distinctVersion();
        if (StringUtils.isNotBlank(system)) {
            flowDefinitionQuery.system(system);
        }
        if (!queryInfo.getPropertyFilters().isEmpty()) {
            String queryValue = ObjectUtils.toString(queryInfo.getPropertyFilters().get(0).getMatchValue());
            if (StringUtils.isNotBlank(queryValue)) {
                flowDefinitionQuery.nameLike(queryValue);
                flowDefinitionQuery.idLike(queryValue);
                flowDefinitionQuery.categoryLike(queryValue);
            }
        }
//        if (!SpringSecurityUtils.isInternetLoginUser()) {
//            List<String> systemUnitIds = new ArrayList<String>();
//            systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
//            if (SpringSecurityUtils.getCurrentUserUnitId() != null) {
//                systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
//            }
//            flowDefinitionQuery.systemUnitIds(systemUnitIds);
//        }
        flowDefinitionQuery.setFirstResult(first);
        flowDefinitionQuery.setMaxResults(pageSize);
        if (StringUtils.isNotBlank(orderBy)) {
            flowDefinitionQuery.order(orderBy);
        } else {
            flowDefinitionQuery.orderByCodeAsc();
        }
        // 是否过滤移动端的流程
        if (isMobile) {
            flowDefinitionQuery.mobileShow(isMobile);
        }
        // 是否过滤最近使用的流程
        if (recentUse) {
            flowDefinitionQuery.recentUse();
        }
        List<FlowDefinitionQueryItem> queryItems = flowDefinitionQuery.list();
        if (autoCount) {
            queryInfo.getPagingInfo().setTotalCount(flowDefinitionQuery.count());
        }
        return BeanUtils.copyCollection(queryItems, FlowDefinition.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#get(java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getTodo(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.TODO);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(task.getId());

        // 是否任务的第一个结点
        workBean.setIsFirstTaskNode(taskConfiguration.isFirstTaskNode());
        // 设置套打模板ID
        workBean.setPrintTemplateId(taskConfiguration.getPrintTemplateId());
        workBean.setPrintTemplateUuid(taskConfiguration.getPrintTemplateUuid());

        // 设置操作按钮
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.TODO, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);

        // 设置意见立场
        workBean.setOpinions(taskConfiguration.getOpinions());

        return workBean;
    }

    /**
     * @param rights
     * @param task
     * @param taskConfiguration
     * @return
     */
    private List<String> filterTodoRight(List<String> rights, TaskInstance task, TaskConfiguration taskConfiguration) {
        List<String> removedRights = Lists.newArrayList();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskUuid = task.getUuid();
        // 判断该环节是否可退回(退回到指定的环节)
        if (taskConfiguration.isFirstTaskNode()) {
            if (taskService.isAllowedRollbackToTask(userId, taskUuid)) {
                removedRights.add(WorkFlowPrivilege.Rollback.getCode());
                // rights.remove(WorkFlowPrivilege.Rollback.getCode());
                if (!taskService.isAllowedDirectRollbackToTask(userId, taskUuid)) {
                    removedRights.add(WorkFlowPrivilege.DirectRollback.getCode());
                    // rights.remove(WorkFlowPrivilege.DirectRollback.getCode());
                }
            } else {
                removedRights.add(WorkFlowPrivilege.Rollback.getCode());
                removedRights.add(WorkFlowPrivilege.DirectRollback.getCode());
                // rights.remove(WorkFlowPrivilege.Rollback.getCode());
                // rights.remove(WorkFlowPrivilege.DirectRollback.getCode());
            }
        } else if (rights.contains(WorkFlowPrivilege.Rollback.getCode())) {
            if (!taskService.isAllowedRollbackToTask(userId, taskUuid)) {
                removedRights.add(WorkFlowPrivilege.Rollback.getCode());
                // rights.remove(WorkFlowPrivilege.Rollback.getCode());
            }
        }
        // 待办第一个环节才可删除
        if (!taskConfiguration.isFirstTaskNode() && rights.contains(WorkFlowPrivilege.Delete.getCode())) {
            removedRights.add(WorkFlowPrivilege.Delete.getCode());
        }
        if (Boolean.FALSE.equals(task.getIsParallel())
                && !taskService.isAllowedDirectRollbackToTask(userId, taskUuid)) {
            removedRights.add(WorkFlowPrivilege.DirectRollback.getCode());
            // rights.remove(WorkFlowPrivilege.DirectRollback.getCode());
        }
        // 判断该环节是否可退回主流程
        if (!taskService.isAllowedRollbackToMainFlow(userId, taskUuid)) {
            removedRights.add(WorkFlowPrivilege.RollbackToMainFlow.getCode());
            // rights.remove(WorkFlowPrivilege.RollbackToMainFlow.getCode());
        }
        return removedRights;
    }

    /**
     * @param taskInstance
     * @param taskConfiguration
     * @param aclRole
     * @return
     */
    private WorkFlowRights getWorkFlowRights(TaskInstance taskInstance, String taskIdentityUuid,
                                             TaskConfiguration taskConfiguration, String aclRole, Set<Permission> permissions) {
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        WorkFlowRights workFlowRights = new WorkFlowRights(aclRole, workFlowSettings.isAclRoleIsolation());
        Set<String> removedRights = Sets.newHashSet();
        // 逻辑删除可恢复
        if (taskInstance != null && Integer.valueOf(SuspensionState.LOGIC_DELETED.getState()).equals(taskInstance.getSuspensionState())) {
            workFlowRights.setTodoButtons(taskConfiguration.filterTodoButtons(WorkFlowPrivilege.Delete, WorkFlowPrivilege.ViewProcess));
        } else if (workFlowSettings.isAclRoleIsolation()) {
            // 多角色时流程操作按角色隔离
            switch (aclRole) {
                case WorkFlowAclRole.DRAFT:
                    workFlowRights.setStartButtons(taskConfiguration.getStartButtons());
                    updateRightConfig(workFlowRights, taskConfiguration.getStartRightConfig());
                    break;
                case WorkFlowAclRole.TODO:
                    workFlowRights.setTodoButtons(taskConfiguration.getTodoButtons());
                    updateRightConfig(workFlowRights, taskConfiguration.getTodoRightConfig());
                    List<String> todoRights = taskConfiguration.getTodoRights();
                    removedRights.addAll(filterTodoRight(todoRights, taskInstance, taskConfiguration));
                    break;
                case WorkFlowAclRole.DONE:
                case WorkFlowAclRole.OVER:
                    if (permissions.contains(AclPermission.DONE)) {
                        workFlowRights.setDoneButtons(taskConfiguration.getDoneButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getDoneRightConfig());
                        List<String> doneRights = taskConfiguration.getDoneRights();
                        if (StringUtils.equals(WorkFlowAclRole.DONE, aclRole)) {
                            removedRights.addAll(filterDoneRight(doneRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskIdentityUuid, taskConfiguration));
                        } else {
                            removedRights.addAll(filterOverRight(doneRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskConfiguration));
                        }
                    } else if (permissions.contains(AclPermission.UNREAD) || permissions.contains(AclPermission.FLAG_READ)) {
                        workFlowRights.setCopyToButtons(taskConfiguration.getCopyToButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getCopyToRightConfig());
                    } else if (permissions.contains(AclPermission.READ) || CollectionUtils.isNotEmpty(permissions)) {
                        workFlowRights.setViewerButtons(taskConfiguration.getViewerButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getViewerRightConfig());
                    }
                    break;
                case WorkFlowAclRole.SUPERVISE:
                    workFlowRights.setMonitorButtons(taskConfiguration.getMonitorButtons());
                    updateRightConfig(workFlowRights, taskConfiguration.getMonitorRightConfig());
                    List<String> superviseRights = taskConfiguration.getMonitorRights();
                    removedRights.addAll(filterSuperviseRight(superviseRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskConfiguration));
                    break;
                case WorkFlowAclRole.MONITOR:
                    workFlowRights.setAdminButtons(taskConfiguration.getAdminButtons());
                    updateRightConfig(workFlowRights, taskConfiguration.getAdminRightConfig());
                    List<String> monitorRights = taskConfiguration.getAdminRights();
                    removedRights.addAll(filterMonitorRight(monitorRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskConfiguration));
                    break;
                case WorkFlowAclRole.UNREAD:
                case WorkFlowAclRole.FLAG_READ:
                    workFlowRights.setCopyToButtons(taskConfiguration.getCopyToButtons());
                    updateRightConfig(workFlowRights, taskConfiguration.getCopyToRightConfig());
                    break;
                case WorkFlowAclRole.ATTENTION:
                case WorkFlowAclRole.VIEWER:
                    workFlowRights.setViewerButtons(taskConfiguration.getViewerButtons());
                    updateRightConfig(workFlowRights, taskConfiguration.getViewerRightConfig());
                    break;
            }
        } else if (taskInstance == null) {
            // 发起权限
            workFlowRights.setStartButtons(taskConfiguration.getStartButtons());
            updateRightConfig(workFlowRights, taskConfiguration.getStartRightConfig());
        } else if (CollectionUtils.isNotEmpty(permissions)) {
            workFlowRights.setViewerButtons(taskConfiguration.getViewerButtons());
            permissions.forEach(permission -> {
                switch (permission.getMask()) {
                    // READ
                    case 1:
                        workFlowRights.setViewerButtons(taskConfiguration.getViewerButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getViewerRightConfig());
                        break;
                    // DRAFT
                    case 1 << 5:
                        workFlowRights.setStartButtons(taskConfiguration.getStartButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getStartRightConfig());
                        break;
                    // TODO
                    case 1 << 6:
                        workFlowRights.setTodoButtons(taskConfiguration.getTodoButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getTodoRightConfig());
                        List<String> todoRights = taskConfiguration.getTodoRights();
                        removedRights.addAll(filterTodoRight(todoRights, taskInstance, taskConfiguration));
                        break;
                    // DONE
                    case 1 << 7:
                        workFlowRights.setDoneButtons(taskConfiguration.getDoneButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getDoneRightConfig());
                        List<String> doneRights = taskConfiguration.getDoneRights();
                        if (taskInstance.getEndTime() == null) {
                            removedRights.addAll(filterDoneRight(doneRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskIdentityUuid, taskConfiguration));
                        } else {
                            removedRights.addAll(filterOverRight(doneRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskConfiguration));
                        }
                        break;
                    // ATTENTION
                    case 1 << 8:
                        workFlowRights.setViewerButtons(taskConfiguration.getViewerButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getViewerRightConfig());
                        break;
                    // UNREAD、FLAG_READ
                    case 1 << 9:
                    case 1 << 10:
                        workFlowRights.setCopyToButtons(taskConfiguration.getCopyToButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getCopyToRightConfig());
                        break;
                    // SUPERVISE
                    case 1 << 11:
                        workFlowRights.setMonitorButtons(taskConfiguration.getMonitorButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getMonitorRightConfig());
                        List<String> superviseRights = taskConfiguration.getMonitorRights();
                        removedRights.addAll(filterSuperviseRight(superviseRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskConfiguration));
                        break;
                    // MONITOR
                    case 1 << 12:
                        workFlowRights.setAdminButtons(taskConfiguration.getAdminButtons());
                        updateRightConfig(workFlowRights, taskConfiguration.getAdminRightConfig());
                        List<String> monitorRights = taskConfiguration.getAdminRights();
                        removedRights.addAll(filterMonitorRight(monitorRights, workFlowSettings.isAclRoleIsolation(), taskInstance, taskConfiguration));
                        break;
                }
            });
        }
        workFlowRights.removeAll(removedRights);
        return workFlowRights;
    }

    /**
     * @param workFlowRights
     * @param rightConfig
     */
    private void updateRightConfig(WorkFlowRights workFlowRights, RightConfigElement rightConfig) {
        if (rightConfig == null) {
            return;
        }

        // 提交必填意见
        List<Button> buttons = Lists.newArrayList();
        if (rightConfig.isRequiredSubmitOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.Submit.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredSignOpinion.getCode()));
        }

        // 提交自动套打
        if (rightConfig.isPrintAfterSubmit()) {
            buttons.add(Button.from("B004020"));
        }

        // 退回必填意见
        if (rightConfig.isRequiredRollbackOpinion() && (workFlowRights.hasButtonByCode(WorkFlowPrivilege.Rollback.getCode())
                || workFlowRights.hasButtonByCode(WorkFlowPrivilege.DirectRollback.getCode()))) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredRollbackOpinion.getCode()));
        }

        // 转办必填意见
        if (rightConfig.isRequiredTransferOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.Transfer.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredTransferOpinion.getCode()));
        }

        // 会签必填意见
        if (rightConfig.isRequiredCounterSignOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.CounterSign.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredCounterSignOpinion.getCode()));
        }

        // 加签必填意见
        if (rightConfig.isRequiredAddSignOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.AddSign.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredAddSignOpinion.getCode()));
        }
        // 撤回必填意见
        if (rightConfig.isRequiredCancelOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.Cancel.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredCancelOpinion.getCode()));
        }

        // 催办必填意见
        if (rightConfig.isRequiredRemindOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.Remind.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredRemindOpinion.getCode()));
        }

        // 移交必填意见
        if (rightConfig.isRequiredHandOverOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.HandOver.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredHandOverOpinion.getCode()));
        }

        // 跳转必填意见
        if (rightConfig.isRequiredGotoTaskOpinion() && workFlowRights.hasButtonByCode(WorkFlowPrivilege.GotoTask.getCode())) {
            buttons.add(Button.from(WorkFlowPrivilege.RequiredGotoTaskOpinion.getCode()));
        }

        workFlowRights.addExtraButtons(buttons);
    }

    /* add by huanglinchuan 2014.10.31 end */

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getTodoTaskByFlowInstUuid(java.lang.String)
     */
    @Override
    public TaskInstance getTodoTaskByFlowInstUuid(String flowInstUuid) {
        TaskInstance taskInstance = new TaskInstance();
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<TaskInstance> taskInstances = taskService.getTodoTasks(userId, flowInstUuid);
        if (!taskInstances.isEmpty()) {
            BeanUtils.copyProperties(taskInstances.get(0), taskInstance);
        }
        return taskInstance;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getDone(java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getDone(String taskInstUuid, String flowInstUuid) {
        return getDone(taskInstUuid, null, flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getDone(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getDone(String taskInstUuid, String taskIdentityUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.DONE);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));

        // 设置操作按钮
        WorkFlowRights workFlowRights = getWorkFlowRights(task, taskIdentityUuid, taskConfiguration, WorkFlowAclRole.DONE, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);

        // 设置套打模板ID
        workBean.setPrintTemplateId(taskConfiguration.getPrintTemplateId());
        workBean.setPrintTemplateUuid(taskConfiguration.getPrintTemplateUuid());
        return workBean;
    }

    /**
     * @param rights
     * @param taskInstance
     * @param taskIdentityUuid
     * @param taskConfiguration
     * @return
     */
    private List<String> filterDoneRight(List<String> rights, boolean aclRoleIsolation, TaskInstance taskInstance, String taskIdentityUuid, TaskConfiguration taskConfiguration) {
        List<String> removedRights = Lists.newArrayList();
        String taskInstUuid = taskInstance.getUuid();
        // 判断该环节是否可撤回
        if (rights.contains(WorkFlowPrivilege.Cancel.getCode()) || Integer.valueOf(2).equals(taskInstance.getType())) {
            String userId = SpringSecurityUtils.getCurrentUserId();
            if (!taskService.isAllowedCancel(userId, taskInstUuid)) {
                removedRights.add(WorkFlowPrivilege.Cancel.getCode());
                // rights.remove(WorkFlowPrivilege.Cancel.getCode());
            }
//            else if (Integer.valueOf(2).equals(taskInstance.getType())) {
//                // 撤回子流程
//                removedRights.add(WorkFlowPrivilege.Cancel.getCode());
//                // rights.add(WorkFlowPrivilege.Cancel.getCode());
//            }

            // 如果工作已经办结则不可退回
            if (taskInstance.getEndTime() != null && aclRoleIsolation) {
                removedRights.add(WorkFlowPrivilege.Submit.getCode());
                removedRights.add(WorkFlowPrivilege.Rollback.getCode());
                removedRights.add(WorkFlowPrivilege.DirectRollback.getCode());
                removedRights.add(WorkFlowPrivilege.CounterSign.getCode());
                removedRights.add(WorkFlowPrivilege.Transfer.getCode());
                // removedRights.add(WorkFlowPrivilege.Cancel.getCode());
                removedRights.add(WorkFlowPrivilege.HandOver.getCode());
                removedRights.add(WorkFlowPrivilege.GotoTask.getCode());
                removedRights.add(WorkFlowPrivilege.Remind.getCode());
                removedRights.add(WorkFlowPrivilege.Delete.getCode());
//                rights.remove(WorkFlowPrivilege.Submit.getCode());
//                rights.remove(WorkFlowPrivilege.Rollback.getCode());
//                rights.remove(WorkFlowPrivilege.DirectRollback.getCode());
//                rights.remove(WorkFlowPrivilege.CounterSign.getCode());
//                rights.remove(WorkFlowPrivilege.Transfer.getCode());
//                rights.remove(WorkFlowPrivilege.Cancel.getCode());
//                rights.remove(WorkFlowPrivilege.HandOver.getCode());
//                rights.remove(WorkFlowPrivilege.GotoTask.getCode());
//                rights.remove(WorkFlowPrivilege.Remind.getCode());
//                rights.remove(WorkFlowPrivilege.Delete.getCode());
            }
        }
        // 委托人打开委托数据，添加催办权限
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            String userId = SpringSecurityUtils.getCurrentUserId();
            TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
            if (taskIdentity != null && StringUtils.equals(userId, taskIdentity.getOwnerId())
                    && WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType())
                    && Integer.valueOf(SuspensionState.NORMAL.getState()).equals(taskIdentity.getSuspensionState())) {
                removedRights.add(WorkFlowPrivilege.Remind.getCode());
                // rights.add(WorkFlowPrivilege.Remind.getCode());
            }
        }
        return removedRights;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getOver(java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getOver(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        String taskInstUuid = task.getUuid();
        String taskId = task.getId();
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        // 办结环节定义不存在，取最新流转过的环节定义信息
        if (!flowDelegate.existsTaskNode(taskId)) {
            TaskActivityItem taskActivityItem = getExistsTaskActivityItem(taskInstUuid, flowDelegate);
            if (taskActivityItem != null) {
                taskInstUuid = taskActivityItem.getTaskInstUuid();
                taskId = taskActivityItem.getTaskId();
            }
        }
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.OVER);
        // TaskData taskData = taskService.getConfigInfo(taskInstUuid);
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
        // 设置操作按钮
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.OVER, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);
        // 设置套打模板ID
        workBean.setPrintTemplateId(taskConfiguration.getPrintTemplateId());
        workBean.setPrintTemplateUuid(taskConfiguration.getPrintTemplateUuid());
        return workBean;
    }

    /**
     * 当前节点是脚本节点时，取前一用户相关的环节ID
     *
     * @param taskInstance
     * @param flowDelegate
     * @return
     */
    private String getUserRelatedTaskId(TaskInstance taskInstance, FlowDelegate flowDelegate) {
        String retTaskId = taskInstance.getId();
        if (TaskNodeType.ScriptTask.getValueAsInt().equals(taskInstance.getType())) {
            List<TaskActivityQueryItem> activityQueryItems = taskActivityService.getAllActivityByTaskInstUuid(taskInstance.getUuid());
            Map<String, TaskActivityQueryItem> activityQueryItemMap = ConvertUtils.convertElementToMap(activityQueryItems, "taskInstUuid");
            TaskInstance userTask = taskInstance;
            TaskActivityQueryItem activityQueryItem = activityQueryItemMap.get(userTask.getUuid());
            while (true) {
                String preTaskInstUuid = activityQueryItem.getPreTaskInstUuid();
                activityQueryItem = activityQueryItemMap.get(preTaskInstUuid);
                if (activityQueryItem == null) {
                    break;
                }
                if (!flowDelegate.isScriptTaskNode(activityQueryItem.getPreTaskId())) {
                    retTaskId = activityQueryItem.getPreTaskId();
                    break;
                }
            }
        }
        return retTaskId;
    }

    /**
     * @param rights
     * @param aclRoleIsolation
     * @param taskInstance
     * @param taskConfiguration
     * @return
     */
    private List<String> filterOverRight(List<String> rights, boolean aclRoleIsolation, TaskInstance taskInstance, TaskConfiguration taskConfiguration) {
        List<String> removedRights = Lists.newArrayList();
        String taskInstUuid = taskInstance.getUuid();
        // 工作已经办结则不可撤回
        if (!taskService.isAllowedCancel(SpringSecurityUtils.getCurrentUserId(), taskInstUuid)) {
            removedRights.add(WorkFlowPrivilege.Cancel.getCode());
            // rights.remove(WorkFlowPrivilege.Cancel.getCode());
        }
        return removedRights;
    }

    /**
     * @param taskInstUuid
     * @param flowDelegate
     * @return
     */
    private TaskActivityItem getExistsTaskActivityItem(String taskInstUuid, FlowDelegate flowDelegate) {
        TaskActivityItem item = null;
        List<TaskActivityQueryItem> taskActivityQueryItems = taskActivityService
                .getAllActivityByTaskInstUuid(taskInstUuid);
        TaskActivityStack taskActivityStack = TaskActivityStackFactary.build(taskInstUuid, taskActivityQueryItems);
        while (!taskActivityStack.isEmpty()) {
            item = taskActivityStack.pop();
            if (flowDelegate.existsTaskNode(item.getTaskId())) {
                break;
            }
        }
        return item;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getUnread(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public WorkBean getUnread(String taskUuid, String flowInstUuid, boolean openToRead) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.UNREAD);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
        String userId = SpringSecurityUtils.getCurrentUserId();
        // 删除未阅，标记为已读
        if (openToRead) {
            aclTaskService.removePermission(task.getUuid(), AclPermission.UNREAD, userId);
            if (!aclTaskService.hasPermission(task.getUuid(), AclPermission.FLAG_READ, userId)) {
                aclTaskService.addFlagReadPermission(userId, PermissionGranularityUtils.getCurrentUserSids(), task.getUuid());
                // aclTaskService.addPermission(task.getUuid(), AclPermission.FLAG_READ, userId);
            }
        }
        List<CustomDynamicButton> customDynamicButtons = taskConfiguration.getCustomDynamicButtons();
        // 已阅未阅流程可查看办理过程
        // List<String> rights = Lists.newArrayList(WorkFlowPrivilege.ViewProcess.getCode());
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.UNREAD, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, customDynamicButtons, flowDelegate);
        return workBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getRead(java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getRead(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.FLAG_READ);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
        List<CustomDynamicButton> customDynamicButtons = taskConfiguration.getCustomDynamicButtons();
        // 已阅未阅流程可查看办理过程
        // List<String> rights = Lists.newArrayList(WorkFlowPrivilege.ViewProcess.getCode());
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.FLAG_READ, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, customDynamicButtons, flowDelegate);
        return workBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getAttention(java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getAttention(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.ATTENTION);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));

        // 设置关注/取消关注权限
//        List<String> rights = new ArrayList<String>();
//        rights.add(WorkFlowPrivilege.Attention.getCode());
//        rights.add(WorkFlowPrivilege.Unfollow.getCode());
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.ATTENTION, getAclPermission(workBean));
        List<CustomDynamicButton> customDynamicButtons = taskConfiguration.getCustomDynamicButtons();
        setButtonRights(workBean, workFlowRights, customDynamicButtons, flowDelegate);
        return workBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getSupervise(java.lang.String)
     */
    @Override
    public WorkBean getSupervise(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.SUPERVISE);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
        // 设置操作按钮
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.SUPERVISE, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);
        // 设置套打模板ID
        workBean.setPrintTemplateId(taskConfiguration.getPrintTemplateId());
        workBean.setPrintTemplateUuid(taskConfiguration.getPrintTemplateUuid());
        return workBean;
    }

    /**
     * @param rights
     * @param aclRoleIsolation
     * @param taskInstance
     * @param taskConfiguration
     * @return
     */
    private List<String> filterSuperviseRight(List<String> rights, boolean aclRoleIsolation, TaskInstance taskInstance, TaskConfiguration taskConfiguration) {
        List<String> removedRights = Lists.newArrayList();
        if (taskInstance.getEndTime() != null && aclRoleIsolation) {
            removedRights.add(WorkFlowPrivilege.Submit.getCode());
            removedRights.add(WorkFlowPrivilege.Rollback.getCode());
            removedRights.add(WorkFlowPrivilege.DirectRollback.getCode());
            removedRights.add(WorkFlowPrivilege.CounterSign.getCode());
            removedRights.add(WorkFlowPrivilege.Transfer.getCode());
            removedRights.add(WorkFlowPrivilege.Cancel.getCode());
            removedRights.add(WorkFlowPrivilege.HandOver.getCode());
            removedRights.add(WorkFlowPrivilege.GotoTask.getCode());
            removedRights.add(WorkFlowPrivilege.Remind.getCode());
            removedRights.add(WorkFlowPrivilege.Delete.getCode());
//            rights.remove(WorkFlowPrivilege.Submit.getCode());
//            rights.remove(WorkFlowPrivilege.Rollback.getCode());
//            rights.remove(WorkFlowPrivilege.DirectRollback.getCode());
//            rights.remove(WorkFlowPrivilege.CounterSign.getCode());
//            rights.remove(WorkFlowPrivilege.Transfer.getCode());
//            rights.remove(WorkFlowPrivilege.Cancel.getCode());
//            rights.remove(WorkFlowPrivilege.HandOver.getCode());
//            rights.remove(WorkFlowPrivilege.GotoTask.getCode());
//            rights.remove(WorkFlowPrivilege.Remind.getCode());
//            rights.remove(WorkFlowPrivilege.Delete.getCode());
        }
        return removedRights;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getMonitor(java.lang.String)
     */
    @Override
    public WorkBean getMonitor(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.MONITOR);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
        // 设置操作按钮
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.MONITOR, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);
        // 设置套打模板ID
        workBean.setPrintTemplateId(taskConfiguration.getPrintTemplateId());
        workBean.setPrintTemplateUuid(taskConfiguration.getPrintTemplateUuid());
        return workBean;
    }

    @Override
    public WorkBean getViewer(String taskInstUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.VIEWER);
        // TaskData taskData = taskService.getConfigInfo(task.getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
        List<CustomDynamicButton> customDynamicButtons = taskConfiguration.getCustomDynamicButtons();
        // 已阅未阅流程可查看办理过程
        // List<String> rights = Lists.newArrayList(WorkFlowPrivilege.ViewProcess.getCode());
        WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.VIEWER, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, customDynamicButtons, flowDelegate);
        return workBean;
    }

    /**
     * @param rights
     * @param aclRoleIsolation
     * @param taskInstance
     * @param taskConfiguration
     * @return
     */
    private List<String> filterMonitorRight(List<String> rights, boolean aclRoleIsolation, TaskInstance taskInstance, TaskConfiguration taskConfiguration) {
        List<String> removedRights = Lists.newArrayList();
        if (taskInstance.getEndTime() != null && aclRoleIsolation) {
            removedRights.add(WorkFlowPrivilege.Submit.getCode());
            removedRights.add(WorkFlowPrivilege.Rollback.getCode());
            removedRights.add(WorkFlowPrivilege.DirectRollback.getCode());
            removedRights.add(WorkFlowPrivilege.CounterSign.getCode());
            removedRights.add(WorkFlowPrivilege.Transfer.getCode());
            removedRights.add(WorkFlowPrivilege.Cancel.getCode());
            removedRights.add(WorkFlowPrivilege.HandOver.getCode());
            // 办结可跳转
            // rights.remove(WorkFlowPrivilege.GotoTask.getCode());
            removedRights.add(WorkFlowPrivilege.Remind.getCode());
            removedRights.add(WorkFlowPrivilege.Delete.getCode());
//            rights.remove(WorkFlowPrivilege.Submit.getCode());
//            rights.remove(WorkFlowPrivilege.Rollback.getCode());
//            rights.remove(WorkFlowPrivilege.DirectRollback.getCode());
//            rights.remove(WorkFlowPrivilege.CounterSign.getCode());
//            rights.remove(WorkFlowPrivilege.Transfer.getCode());
//            rights.remove(WorkFlowPrivilege.Cancel.getCode());
//            rights.remove(WorkFlowPrivilege.HandOver.getCode());
//            // 办结可跳转
//            // rights.remove(WorkFlowPrivilege.GotoTask.getCode());
//            rights.remove(WorkFlowPrivilege.Remind.getCode());
//            rights.remove(WorkFlowPrivilege.Delete.getCode());
        }
//        // 查看数据快照
//        if (!taskConfiguration.getFlowDelegate().isAudit()) {
//            removedRights.add(WorkFlowPrivilege.ViewFlowDataSnapshot.getCode());
//            // rights.remove(WorkFlowPrivilege.ViewFlowDataSnapshot.getCode());
//        }
        return removedRights;
    }

    /* add by huanglinchuan 2014.10.31 begin */
    @Override
    public WorkBean getByPermissions(String taskInstUuid, String flowInstUuid, List<Permission> permissions) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        if (!StringUtils.trimToEmpty(taskInstUuid).equals("")) {
            TaskInstance task = taskService.get(taskInstUuid);
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
            WorkBean workBean = getWorkBean(task, flowDelegate);
            // TaskData taskData = taskService.getConfigInfo(task.getUuid());
            TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(getUserRelatedTaskId(task, flowDelegate));
            // 设置操作按钮
            boolean has_todo = false;
            boolean has_done = false;
            boolean has_supervise = false;
            boolean has_monitor = false;
            boolean has_flag_read = false;
            boolean has_unread = false;
            Set<String> allRights = new HashSet<String>();
            for (Permission permission : permissions) {
                if (permission.getMask() == AclPermission.TODO.getMask()) {
                    allRights.addAll(taskConfiguration.getTodoRights());
                    has_todo = true;
                } else if (permission.getMask() == AclPermission.DONE.getMask()) {
                    allRights.addAll(taskConfiguration.getDoneRights());
                    has_done = true;
                } else if (permission.getMask() == AclPermission.SUPERVISE.getMask()) {
                    allRights.addAll(taskConfiguration.getMonitorRights());
                    has_supervise = true;
                } else if (permission.getMask() == AclPermission.MONITOR.getMask()) {
                    allRights.addAll(taskConfiguration.getAdminRights());
                    has_monitor = true;
                } else if (permission.getMask() == AclPermission.FLAG_READ.getMask()) {
                    has_flag_read = true;
                } else if (permission.getMask() == AclPermission.UNREAD.getMask()) {
                    has_unread = true;
                }
            }
            if (task.getEndTime() != null) {
                allRights.remove(WorkFlowPrivilege.Submit.getCode());
                allRights.remove(WorkFlowPrivilege.Rollback.getCode());
                allRights.remove(WorkFlowPrivilege.DirectRollback.getCode());
                allRights.remove(WorkFlowPrivilege.CounterSign.getCode());
                allRights.remove(WorkFlowPrivilege.Transfer.getCode());
                allRights.remove(WorkFlowPrivilege.Cancel.getCode());
                allRights.remove(WorkFlowPrivilege.HandOver.getCode());
                allRights.remove(WorkFlowPrivilege.GotoTask.getCode());
                allRights.remove(WorkFlowPrivilege.Remind.getCode());
                allRights.remove(WorkFlowPrivilege.Delete.getCode());
            } else {
                if (allRights.contains(WorkFlowPrivilege.Cancel.getCode())
                        && !taskService.isAllowedCancel(userId, taskInstUuid)) {
                    allRights.remove(WorkFlowPrivilege.Cancel.getCode());
                }
                if (allRights.contains(WorkFlowPrivilege.Rollback.getCode())
                        && !taskService.isAllowedRollbackToTask(userId, taskInstUuid)) {
                    allRights.remove(WorkFlowPrivilege.Rollback.getCode());
                }
                if (allRights.contains(WorkFlowPrivilege.DirectRollback.getCode())
                        && !taskService.isAllowedDirectRollbackToTask(userId, taskInstUuid)) {
                    allRights.remove(WorkFlowPrivilege.DirectRollback.getCode());
                }
            }
            if (has_flag_read) {
                workBean.setAclRole(WorkFlowAclRole.FLAG_READ);
            }
            if (has_unread) {
                workBean.setAclRole(WorkFlowAclRole.UNREAD);
            }
            if (has_done) {
                workBean.setAclRole(WorkFlowAclRole.DONE);
            }
            if (has_supervise) {
                workBean.setAclRole(WorkFlowAclRole.SUPERVISE);
            }
            if (has_monitor) {
                workBean.setAclRole(WorkFlowAclRole.MONITOR);
            }
            if (has_todo) {
                workBean.setAclRole(WorkFlowAclRole.TODO);
            }
            WorkFlowRights workFlowRights = getWorkFlowRights(task, null, taskConfiguration, WorkFlowAclRole.TODO, getAclPermission(workBean));
            setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);
            return workBean;
        }

        if (!StringUtils.trimToEmpty(flowInstUuid).equals("")) {

        }

        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getDraft(java.lang.String, java.lang.String)
     */
    @Override
    public WorkBean getDraft(String flowInstUuid) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDelegate flowDelegate = new FlowDelegate(flowInstance.getFlowDefinition());
        WorkBean workBean = getDraftWorkBean(flowInstance, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.DRAFT);
        // TaskData taskData = flowService.getFirstTaskData(flowInstance.getFlowDefinition().getUuid());
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(flowDelegate.getStartNode().getToID());
        // 开始环节ID
        workBean.setFromTaskId(flowDelegate.getStartNode().getToID());
        // 第一个环节ID
        workBean.setTaskId(taskConfiguration.getTaskId());
        workBean.setTaskName(taskConfiguration.getTaskName());
        /* add by huanglinchuan 2014.12.25 begin */
        // 设置流水号定义ID
        workBean.setSerialNoDefId(taskConfiguration.getSerialNoDefId());
        /* add by huanglinchuan 2014.12.25 end */
        // 设置操作按钮
//        List<String> rights = taskData.getStartRights();
//        // 发起权限兼容性处理，没有配置使用待办权限，不需要必须签署意见
//        if (CollectionUtils.isEmpty(rights)) {
//            rights = taskData.getTodoRights();
//            rights.remove(WorkFlowPrivilege.RequiredSignOpinion.getCode());
//        }
        WorkFlowRights workFlowRights = getWorkFlowRights(null, null, taskConfiguration, WorkFlowAclRole.DRAFT, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);
        return workBean;
    }

    /**
     * @param taskInstance
     * @return
     */
    private WorkBean getWorkBean(TaskInstance taskInstance, FlowDelegate flowDelegate) {
        String taskId = taskInstance.getId();
        Node taskNode = flowDelegate.getTaskNode(taskId);

        WorkBean workBean = new WorkBean();
        BeanUtils.copyProperties(taskInstance, workBean);
        workBean.setName(taskInstance.getFlowDefinition().getName());
        workBean.setTaskInstUuid(taskInstance.getUuid());
        workBean.setTaskInstRecVer(taskInstance.getRecVer());
        workBean.setCanEditForm(taskNode.getCanEditForm());
        workBean.setTaskId(taskId);
        workBean.setTaskName(taskInstance.getName());
        workBean.setIsFirstTaskNode(flowDelegate.isFirstTaskNode(taskId));
        workBean.setFromTaskId(taskId);
        workBean.setTaskStartTime(taskInstance.getStartTime());
        workBean.setVersion(flowDelegate.getFlow().getVersion());
        List<AppDefElementI18nEntity> i18ns = appDefElementI18nService.getI18ns(flowDelegate.getFlow().getId(), ""
                , new BigDecimal(flowDelegate.getFlow().getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
        if (CollectionUtils.isNotEmpty(i18ns)) {
            Map<String, String> i18nMaps = Maps.newHashMap();
            for (AppDefElementI18nEntity i : i18ns) {
                i18nMaps.put(i.getCode(), i.getContent());
            }
            workBean.setI18n(i18nMaps);
        }
        // 流程已经结束
        // if (taskInstance.getEndTime() != null) {
        workBean.setFlowDefUuid(taskInstance.getFlowDefinition().getUuid());
        // }
        workBean.setFlowInstUuid(taskInstance.getFlowInstance().getUuid());
        workBean.setFlowDefId(taskInstance.getFlowInstance().getId());
        workBean.setTitle(taskInstance.getFlowInstance().getTitle());
//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
//            FlowDataI18nEntity i18nEntity = dataI18nService.getByDataIdAndCodeAndLocale(FlowDataI18nEntity.class, taskInstance.getFlowInstance().getUuid(), "title", LocaleContextHolder.getLocale().toString());
//            if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
//                workBean.setTitle(i18nEntity.getContent());
//            } else {
//                String _title = translateService.translate(taskInstance.getFlowInstance().getTitle(), Locale.SIMPLIFIED_CHINESE.getLanguage(), LocaleContextHolder.getLocale().toString().split(Separator.UNDERLINE.getValue())[0]);
//                workBean.setTitle(_title);
//            }
//        }
        String pformUuid = taskInstance.getFormUuid();
        workBean.setFormUuid(pformUuid);
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(pformUuid);
        if (dyFormFormDefinition != null) {
            // workBean.setDefaultVFormUuid(dyFormFormDefinition.doGetDefaultVFormUuid());
            workBean.setDefaultMFormUuid(dyFormFormDefinition.doGetDefaultMFormUuid());
        }
        String taskFormUuid = taskNode.getFormID();
        if (StringUtils.isNotBlank(taskFormUuid) && !StringUtils.equals(pformUuid, taskFormUuid)) {
            String pformTblName = dyFormFacade.getTblNameByFormUuid(pformUuid);
            String vformTblName = dyFormFacade.getTblNameByFormUuid(taskFormUuid);
            if (StringUtils.equals(pformTblName, vformTblName)) {
                workBean.setDefaultVFormUuid(taskFormUuid);
            }
        }
        workBean.setDataUuid(taskInstance.getDataUuid());
        workBean.setDevelopJson(taskInstance.getFlowDefinition().getDevelopJson());
        workBean.setCustomJsModule(extractCustomJsModule(workBean.getDevelopJson()));
        workBean.setSuspensionState(taskInstance.getSuspensionState());
        if (flowDelegate.existsTaskNode(taskId)) {
            workBean.setCustomJsFragmentModule(flowDelegate.getFlow().getTask(taskId).getCustomJsModule());
        }

        // 设置用户自定参数
        setCustomParameters(workBean, taskInstance.getFlowInstance().getUuid());
        return workBean;
    }

    /**
     * @param developJson
     * @return
     */
    private String extractCustomJsModule(String developJson) {
        if (StringUtils.isBlank(developJson)) {
            return "WorkView";
        }
        Map<String, String> developJsonMap = JsonUtils.json2Object(developJson, Map.class);
        String jsModule = developJsonMap.get("customJsModule");
        if (StringUtils.isBlank(jsModule)) {
            return "WorkView";
        }
        return jsModule;
    }

    /**
     * @param flowInstance
     * @return
     */
    private WorkBean getDraftWorkBean(FlowInstance flowInstance, FlowDelegate flowDelegate) {
        TaskData taskData = flowDelegate.getFistTaskData();
        String firstTaskId = taskData.getFirstTaskId();
        String pformUuid = flowInstance.getFlowDefinition().getFormUuid();

        WorkBean workBean = new WorkBean();
        BeanUtils.copyProperties(flowInstance, workBean);
        workBean.setFlowDefUuid(flowInstance.getFlowDefinition().getUuid());
        workBean.setVersion(flowDelegate.getFlow().getVersion());
        workBean.setFlowInstUuid(flowInstance.getUuid());
        workBean.setFlowDefId(flowInstance.getId());
        workBean.setTitle(flowInstance.getTitle());
//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
//            FlowDataI18nEntity i18nEntity = dataI18nService.getByDataIdAndCodeAndLocale(FlowDataI18nEntity.class, flowInstance.getUuid(), "title", LocaleContextHolder.getLocale().toString());
//            if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
//                workBean.setTitle(i18nEntity.getContent());
//            } else {
//                String _title = translateService.translate(flowInstance.getTitle(), Locale.SIMPLIFIED_CHINESE.getLanguage(), LocaleContextHolder.getLocale().toString().split(Separator.UNDERLINE.getValue())[0]);
//                workBean.setTitle(_title);
//            }
//        }
        workBean.setFormUuid(pformUuid);
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(pformUuid);
        if (dyFormFormDefinition != null) {
            // workBean.setDefaultVFormUuid(dyFormFormDefinition.doGetDefaultVFormUuid());
            workBean.setDefaultMFormUuid(dyFormFormDefinition.doGetDefaultMFormUuid());
        }
        if (!StringUtils.equals(pformUuid, taskData.getFormUuid())) {
            workBean.setDefaultVFormUuid(taskData.getFormUuid());
        }
        workBean.setDataUuid(flowInstance.getDataUuid());
        workBean.setDevelopJson(flowInstance.getFlowDefinition().getDevelopJson());
        workBean.setCustomJsModule(extractCustomJsModule(workBean.getDevelopJson()));
        if (flowDelegate.existsTaskNode(firstTaskId)) {
            workBean.setCustomJsFragmentModule(flowDelegate.getFlow().getTask(firstTaskId).getCustomJsModule());
        }

        // 设置用户自定参数
        setCustomParameters(workBean, flowInstance.getUuid());

        List<AppDefElementI18nEntity> i18ns = appDefElementI18nService.getI18ns(flowDelegate.getFlow().getId(), ""
                , new BigDecimal(flowDelegate.getFlow().getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
        if (CollectionUtils.isNotEmpty(i18ns)) {
            Map<String, String> i18nMaps = Maps.newHashMap();
            for (AppDefElementI18nEntity i : i18ns) {
                i18nMaps.put(i.getCode(), i.getContent());
            }
            workBean.setI18n(i18nMaps);
        }
        return workBean;
    }

    /**
     * 设置用户自定参数
     *
     * @param workBean
     * @param uuid
     */
    private void setCustomParameters(WorkBean workBean, String flowInstanceUuid) {
        List<FlowInstanceParameter> parameters = flowInstanceParameterService.getByFlowInstanceUuid(flowInstanceUuid);
        for (FlowInstanceParameter parameter : parameters) {
            workBean.addExtraParam(parameter.getName(), parameter.getValue());
        }
    }

    /**
     * 删除工作草稿
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#deleteDraft(java.util.Collection)
     */
    @Override
    @Transactional
    public void deleteDraft(Collection<String> flowInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String flowInstUuid : flowInstUuids) {
            flowService.deleteDraft(userId, flowInstUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#delete(java.util.Collection)
     */
    @Override
    @Transactional
    public void delete(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            taskService.delete(userId, taskInstUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#topping(java.util.Collection)
     */
    @Override
    @Transactional
    public void topping(Collection<String> taskInstUuids) {
        taskService.topping(taskInstUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#untopping(java.util.Collection)
     */
    @Override
    @Transactional
    public void untopping(Collection<String> taskInstUuids) {
        taskService.untopping(taskInstUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#deleteByAdmin(java.util.Collection)
     */
    @Override
    @Transactional
    public void deleteByAdmin(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            taskService.deleteByAdmin(userId, taskInstUuid);
        }
    }

    @Override
    @Transactional
    public void logicalDeleteByAdmin(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            taskService.logicalDeleteByAdmin(userId, taskInstUuid);
        }
    }

    @Override
    @Transactional
    public void recover(Collection<String> taskInstUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            taskService.recover(userId, taskInstUuid);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.workflow.work.service.WorkService#newWork(java.lang.String )
     */
    @Override
    public WorkBean newWork(String flowDefUuid) {
        FlowDefinition definition = flowService.getFlowDefinition(flowDefUuid);
        return creatNewWork(definition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#newWorkById(java.lang.String)
     */
    @Override
    public WorkBean newWorkById(String flowDefId) {
        return creatNewWork(this.flowService.getFlowDefinitionById(flowDefId));
    }

    /**
     * @param definition
     * @return
     */
    private WorkBean creatNewWork(FlowDefinition definition) {
        FlowDelegate flowDelegate = new FlowDelegate(definition);
        // TaskData taskData = flowDelegate.getFistTaskData();
        TaskConfiguration taskConfiguration = flowDelegate.getTaskConfiguration(flowDelegate.getStartNode().getToID());
        String firstTaskId = taskConfiguration.getTaskId();
        String pformUuid = definition.getFormUuid();

        WorkBean workBean = new WorkBean();
        List<AppDefElementI18nEntity> i18ns = appDefElementI18nService.getI18ns(flowDelegate.getFlow().getId(), "",
                new BigDecimal(flowDelegate.getFlow().getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
        if (CollectionUtils.isNotEmpty(i18ns)) {
            Map<String, String> i18nMaps = Maps.newHashMap();
            for (AppDefElementI18nEntity i : i18ns) {
                i18nMaps.put(i.getCode(), i.getContent());
            }
            workBean.setI18n(i18nMaps);
        }

        workBean.setAclRole(WorkFlowAclRole.DRAFT);
        workBean.setFlowDefUuid(definition.getUuid());
        workBean.setVersion(flowDelegate.getFlow().getVersion());
        workBean.setFlowDefId(definition.getId());
        workBean.setTitle(definition.getName());
        workBean.setName(definition.getName());
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            AppDefElementI18nEntity i18nEntity = appDefElementI18nService.getI18n(definition.getId(), null, "workflowName", new BigDecimal(definition.getVersion()), IexportType.FlowDefinition, LocaleContextHolder.getLocale().toString());
            if (i18nEntity != null && StringUtils.isNotBlank(i18nEntity.getContent())) {
                workBean.setTitle(i18nEntity.getContent());
            }
        }
        // 获取流程的动态表单
        workBean.setFormUuid(pformUuid);
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(pformUuid);
        if (dyFormFormDefinition != null) {
            // workBean.setDefaultVFormUuid(dyFormFormDefinition.doGetDefaultVFormUuid());
            workBean.setDefaultMFormUuid(dyFormFormDefinition.doGetDefaultMFormUuid());
        }
        if (!StringUtils.equals(pformUuid, taskConfiguration.getFormUuid())) {
            workBean.setDefaultVFormUuid(taskConfiguration.getFormUuid());
        }
        workBean.setDataUuid(StringUtils.EMPTY);
        workBean.setDevelopJson(definition.getDevelopJson());
        workBean.setCustomJsModule(extractCustomJsModule(workBean.getDevelopJson()));
        // 开始环节ID
        workBean.setFromTaskId(firstTaskId);
        // 第一个环节ID
        workBean.setTaskId(firstTaskId);
        workBean.setTaskName(taskConfiguration.getTaskName());
        // 环节二开片段
        workBean.setCustomJsFragmentModule(flowDelegate.getFlow().getTask(firstTaskId).getCustomJsModule());
        // 设置流水号定义ID
        workBean.setSerialNoDefId(taskConfiguration.getSerialNoDefId());
        // 信息记录
        workBean.setRecords(flowDelegate.getTaskForm(firstTaskId).getRecords());
        // 设置操作按钮
//        List<String> rights = taskData.getStartRights();
//        // 发起权限兼容性处理，没有配置使用待办权限，不需要必须签署意见
//        if (CollectionUtils.isEmpty(rights)) {
//            rights = taskData.getTodoRights();
//            rights.remove(WorkFlowPrivilege.RequiredSignOpinion.getCode());
//        }

        WorkFlowRights workFlowRights = getWorkFlowRights(null, null, taskConfiguration, WorkFlowAclRole.DRAFT, getAclPermission(workBean));
        setButtonRights(workBean, workFlowRights, taskConfiguration.getCustomDynamicButtons(), flowDelegate);
        return workBean;
    }

    /**
     * 设置操作按钮
     *
     * @param workBean
     * @param workFlowRights
     * @param customDynamicButtons
     * @param flowDelegate
     */
    private void setButtonRights(WorkBean workBean, WorkFlowRights workFlowRights,
                                 List<CustomDynamicButton> customDynamicButtons, FlowDelegate flowDelegate) {
        Set<Button> grantedButtons = new LinkedHashSet<>();
        Set<Button> buttonRights = workFlowRights.getButtons();
        Map<String, Integer> buttonOrderMap = Maps.newHashMap();
        Map<String, Button> buttonRightMap = ConvertUtils.convertElementToMap(buttonRights, "code");
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        // 若操作权限在资源配置中有设置，则可操作，否则不可操作
        // 1、办理权限
        int orderIndex = 0;
        for (Button right : buttonRights) {
            // 7.0版本的按钮排序号
            buttonOrderMap.put(right.getCode(), ++orderIndex * 10);
            // 查看主流程按钮
            if (StringUtils.equals(right.getCode(), WorkFlowPrivilege.ViewTheMainFlow.getCode())) {
                TaskInstance taskInstance = this.taskInstanceService.get(workBean.getTaskInstUuid());
                if (taskInstance.getParent() == null) {
                    continue;
                }
                String taskId = taskInstance.getParent().getId();
                String viewMainFlowJsonStr = taskInstance.getParent().getFlowInstance().getViewMainFlowJson();
                if (StringUtils.isNotBlank(viewMainFlowJsonStr)) {
                    net.sf.json.JSONObject viewMainJson = net.sf.json.JSONObject.fromObject(viewMainFlowJsonStr);
                    if (viewMainFlowJsonStr.isEmpty() || !viewMainJson.containsKey(taskId)
                            || !viewMainJson.getString(taskId).equals("1")) {
                        continue;
                    }
                }
            }
            grantedButtons.add(right);
        }
        Set<Permission> permissions = getAclPermission(workBean);
        Set<String> grantedRights = Sets.newLinkedHashSet(grantedButtons.stream().map(button -> {
            String btnCode = button.getCode();
            if (flowDelegate.isXmlDefinition()) {
                return btnCode;
            } else if (WorkFlowPrivilege.Attention.getCode().equals(btnCode) && permissions.contains(AclPermission.ATTENTION)) {
                // 关注与取消关注多状态
                return WorkFlowPrivilege.Unfollow.getCode();
            } else if (WorkFlowPrivilege.Suspend.getCode().equals(btnCode)
                    && SuspensionState.LOGIC_SUSPEND.getState() == workBean.getSuspensionState()) {
                // 挂起与恢复
                return WorkFlowPrivilege.Resume.getCode();
            } else if (WorkFlowPrivilege.Delete.getCode().equals(btnCode)
                    && SuspensionState.LOGIC_DELETED.getState() == workBean.getSuspensionState()) {
                // 逻辑删除与恢复
                return WorkFlowPrivilege.Recover.getCode();
            }
            return btnCode;
        }).collect(Collectors.toList()));

        // 2、关注与取消关注互斥
        if (StringUtils.isNotBlank(workBean.getTaskInstUuid())
                && grantedRights.contains(WorkFlowPrivilege.Attention.getCode())
                && grantedRights.contains(WorkFlowPrivilege.Unfollow.getCode())) {
            if (permissions.contains(AclPermission.ATTENTION)) {
                grantedRights.remove(WorkFlowPrivilege.Attention.getCode());
            } else {
                grantedRights.remove(WorkFlowPrivilege.Unfollow.getCode());
            }
        }
        // 3、挂起与恢复
        if (SuspensionState.LOGIC_SUSPEND.getState() == workBean.getSuspensionState()) {
            grantedRights.remove(WorkFlowPrivilege.Suspend.getCode());
        } else {
            grantedRights.remove(WorkFlowPrivilege.Resume.getCode());
        }

        // 协作节点完成操作
        if (grantedRights.contains(WorkFlowPrivilege.Complete.getCode())) {
            Map<String, Object> extraParams = workBean.getExtraParams();
            if (flowDelegate.isCollaborationTaskNode(workBean.getTaskId()) && MapUtils.isNotEmpty(extraParams)) {
                String decisionMakers = Objects.toString(extraParams.get("decisionMakers_" + workBean.getTaskId() + "_" + workBean.getTaskInstUuid()), StringUtils.EMPTY);
                if (!StringUtils.contains(decisionMakers, SpringSecurityUtils.getCurrentUserId())) {
                    grantedRights.remove(WorkFlowPrivilege.Complete.getCode());
                }
            } else {
                grantedRights.remove(WorkFlowPrivilege.Complete.getCode());
            }
        }

        // 4、操作定义
        List<CustomDynamicButton> cdButtons = customDynamicButtons;
        List<CustomDynamicButton> unmatchRoleButtons = Lists.newArrayList();
        for (CustomDynamicButton customDynamicButton : cdButtons) {
            List<String> configRoles = null;
            if (StringUtils.isNotBlank(customDynamicButton.getBtnRole())) {
                configRoles = Arrays
                        .asList(StringUtils.split(customDynamicButton.getBtnRole(), Separator.SEMICOLON.getValue()));
            }
            if (CollectionUtils.isEmpty(configRoles)) {// 没有"应用场景"的情况下，非草稿/非待办
                if (!(WorkFlowAclRole.DRAFT.equals(workBean.getAclRole())
                        || WorkFlowAclRole.TODO.equals(workBean.getAclRole()))) {
                    unmatchRoleButtons.add(customDynamicButton);
                }
            } else {
                if (!configRoles.contains(workBean.getAclRole())) {
                    unmatchRoleButtons.add(customDynamicButton);
                }
            }
        }
        cdButtons.removeAll(unmatchRoleButtons);
        Map<String, CustomDynamicButton> notRequiredNewCodePrivilegeMap = Maps.newHashMap();
        Map<String, String> newCodeNameMap = Maps.newHashMap();
        // 事件处理的按钮
        Map<String, List<CustomDynamicButton>> piButtonMap = Maps.newHashMap();
        for (CustomDynamicButton customDynamicButton : cdButtons) {
            String btnSource = customDynamicButton.getBtnSource();
            if (StringUtils.equals(CustomDynamicButton.SOURCE_EVENT_HANDLER, btnSource)) {
                grantedRights.add(customDynamicButton.getId());
                if (!piButtonMap.containsKey(customDynamicButton.getId())) {
                    piButtonMap.put(customDynamicButton.getId(),
                            Lists.<CustomDynamicButton>newArrayListWithExpectedSize(1));
                }
                piButtonMap.get(customDynamicButton.getId()).add(customDynamicButton);
            } else {
                String newCode = customDynamicButton.getNewCode();
                grantedRights.add(newCode);
                notRequiredNewCodePrivilegeMap.put(newCode, customDynamicButton);
            }
        }

        Set<CustomDynamicButton> buttons = new LinkedHashSet<CustomDynamicButton>();
        for (String right : grantedRights) {
            // 事件处理按钮
            if (piButtonMap.containsKey(right)) {
//                if (securityApiFacade.isGranted(right, IexportType.AppProductIntegration)) {
                for (CustomDynamicButton dynamicButton : piButtonMap.get(right)) {
                    CustomDynamicButton button = new CustomDynamicButton();
                    button.setBtnSource(dynamicButton.getBtnSource());
                    button.setId(StringUtils.isBlank(dynamicButton.getId()) ? dynamicButton.getPiUuid()
                            : dynamicButton.getId());
                    button.setName(dynamicButton.getName());
                    button.setCode(dynamicButton.getPiUuid());
                    button.setHashType(dynamicButton.getHashType());
                    button.setHash(dynamicButton.getHash());
                    button.setEventHandler(dynamicButton.getEventHandler());
                    button.setEventParams(dynamicButton.getEventParams());
                    button.setTargetPosition(dynamicButton.getTargetPosition());
                    String className = dynamicButton.getClassName();
                    button.setClassName(StringUtils.isBlank(className) ? "btn w-btn-primary" : className);
                    button.setBtnIcon(dynamicButton.getBtnIcon());
                    button.setSortOrder(dynamicButton.getSortOrder());
                    button.setI18n(dynamicButton.getI18n());
                    button.setUuid(dynamicButton.getUuid());
                    button.setConfiguration(dynamicButton.getConfiguration());
                    buttons.add(button);
//                    }
                }
            } else {
                // 流程内置按钮
                // Resource resource = securityApiFacade.getButtonByCode(right);
                Button buttonRight = buttonRightMap.get(right);
                if (buttonRight == null) {
                    buttonRight = workFlowSettings.getButtonByCode(right);
                }

                if (buttonRight == null) {
                    if (notRequiredNewCodePrivilegeMap.containsKey(right)) {
                        buttons.add(notRequiredNewCodePrivilegeMap.get(right));
                    } else {
                        logger.warn("The button resource by the code[" + right + "] is not found!");
                    }
                    continue;
                } else if (StringUtils.isBlank(buttonRight.getTitle())) {
                    // 62旧的流程定义兼容性处理
                    buttonRight.setTitle(workFlowSettings.getButtonTitleByCode(buttonRight.getCode()));
                }
                CustomDynamicButton button = new CustomDynamicButton();
                button.setId(buttonRight.getCode());
                button.setName(newCodeNameMap.containsKey(right) ? newCodeNameMap.get(right) : buttonRight.getTitle());
                button.setCode(buttonRight.getCode());
                button.setI18n(buttonRight.getI18n());
                button.setUuid(buttonRight.getUuid());
                if (flowDelegate.isXmlDefinition()) {
//                button.setScript(resource.getTarget());
//                String className = resource.getClassName();
//                button.setClassName(StringUtils.isBlank(className) ? "btn w-btn-primary" : className);
                    button.setSortOrder(WorkFlowOperationComparator.getOrder(buttonRight.getCode()));
                } else {
                    Integer buttonOrder = buttonOrderMap.get(buttonRight.getCode());
                    button.setSortOrder(buttonOrder != null ? buttonOrder : WorkFlowOperationComparator.getOrder(buttonRight.getCode()));
                }
                buttons.add(button);
            }
        }

        List<CustomDynamicButton> dynamicButtons = new ArrayList<>(buttons);
        // 附加、替换处理
        if (CollectionUtils.isNotEmpty(cdButtons)) {
            Map<CustomDynamicButton, List<CustomDynamicButton>> replaceMap = Maps.newHashMap();
            Map<CustomDynamicButton, List<CustomDynamicButton>> appendMap = Maps.newHashMap();
            buttons.removeAll(cdButtons);
            buttons.stream().forEach(button -> {
                for (CustomDynamicButton dynamicButton : cdButtons) {
                    String code = dynamicButton.getCode();
                    if (StringUtils.equals(code, button.getCode())) {
                        dynamicButtons.remove(dynamicButton);
                        // 1(替换)、2(新增)
                        String useWay = dynamicButton.getUseWay();
                        if (CustomDynamicButton.REPLACE.equals(useWay)) {
                            List<CustomDynamicButton> btns = replaceMap.get(button);
                            if (CollectionUtils.isEmpty(btns)) {
                                btns = Lists.newArrayList();
                                replaceMap.put(button, btns);
                            }
                            btns.add(dynamicButton);
                        } else {
                            List<CustomDynamicButton> btns = appendMap.get(button);
                            if (CollectionUtils.isEmpty(btns)) {
                                btns = Lists.newArrayList();
                                appendMap.put(button, btns);
                            }
                            btns.add(dynamicButton);
                        }
                    }
                }
            });

            if (MapUtils.isNotEmpty(replaceMap) || MapUtils.isNotEmpty(appendMap)) {
                for (Entry<CustomDynamicButton, List<CustomDynamicButton>> entry : appendMap.entrySet()) {
                    int index = dynamicButtons.indexOf(entry.getKey());
                    dynamicButtons.addAll(index + 1, entry.getValue());
                }
                for (Entry<CustomDynamicButton, List<CustomDynamicButton>> entry : replaceMap.entrySet()) {
                    int index = dynamicButtons.indexOf(entry.getKey());
                    dynamicButtons.addAll(index, entry.getValue());
                    dynamicButtons.remove(entry.getKey());
                }
            }
        }

        Collections.sort(dynamicButtons, new CustomDynamicButton.CustomDynamicButtonComparator());

        workBean.setButtons(dynamicButtons);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#save(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public Map<String, String> save(WorkBean workBean) {
        // 保存前服务处理
        String beforeSaveService = workBean.getBeforeSaveService();
        if (StringUtils.isNotBlank(beforeSaveService)) {
            ServiceInvokeUtils.invoke(beforeSaveService, new Class[]{WorkBean.class}, workBean);

            taskInstanceService.flushSession();
            taskInstanceService.clearSession();
        }

        String taskInstUuid = workBean.getTaskInstUuid();
        String flowInstUuid = workBean.getFlowInstUuid();
        String flowDefUuid = workBean.getFlowDefUuid();
        /* modified by huanglinchuan 2014.12.25 begin */
        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        FlowInstance flowInstance = null;
        boolean needGenerateTitle = false;
        /* modified by huanglinchuan 2014.12.25 end */
        String userId = SpringSecurityUtils.getCurrentUserId();
        String userName = SpringSecurityUtils.getCurrentUserName();
        String taskKey = taskInstUuid + userId;
        TaskData taskData = new TaskData();
        taskData.setAction(taskKey, WorkFlowOperation.getName(WorkFlowOperation.SAVE));
        taskData.setActionType(taskKey, WorkFlowOperation.SAVE);
        taskData.setUserId(userId);
        taskData.setUserName(userName);
        // 加入自定义运行时参数
        Map<String, Object> exTraParams = workBean.getExtraParams();
        for (String key : exTraParams.keySet()) {
            if (key.startsWith(CustomRuntimeData.PREFIX)) {
                taskData.setCustomData(key, exTraParams.get(key));
            }
        }
        DyFormData dyFormData = workBean.getDyFormData();
        // 检验表单数据
        ValidateMsg validateMsg = dyFormData.validateFormDataWithDatabaseConstraints();
        if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
            throw new FormDataValidateException(validateMsg);
        }
        if (StringUtils.isBlank(flowInstUuid)) {
            // 保存动态表单
            String dataUuid = dyFormFacade.saveFormData(dyFormData);// dytableApiFacade.saveFormData(workBean.getRootFormDataBean());
            taskData.setFormUuid(workBean.getFormUuid());
            taskData.setDataUuid(dataUuid);
            // 流程标题
            String title = workBean.getTitle();
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.TITLE.getValue())) {
                Object tmpTitle = dyFormData.getFieldValueByMappingName(WorkFlowFieldMapping.TITLE.getValue());
                title = tmpTitle == null ? title : tmpTitle.toString();
            }
            taskData.setTitle(workBean.getFlowDefId(), title);

            String businessTypeId = workBean.getBusinessTypeId();
            setFlowDepartmentAndUnit(workBean, userId, taskData, businessTypeId);

            // 设置预留字段
            setReservedFields(taskData, workBean.getFormUuid(), dataUuid, workBean);
            // 自动更新标题逻辑处理
            title = TitleExpressionUtils.generateFlowInstanceTitle(flowDefinition, flowInstance, userId, dyFormData);
            taskData.setTitle(flowDefinition.getId(), title);

            // 工作保存为草稿
            /* modified by huanglinchuan 2014.12.25 begin */
            flowInstance = flowService.saveAsDraft(flowDefUuid, taskData);
            needGenerateTitle = true;

            // 保存更新的表单数据
            saveUpdatedDyFormDatasIfRequired(taskData);

            /* modified by huanglinchuan 2014.12.25 end */
            workBean.setDataUuid(dataUuid);
            workBean.setFlowInstUuid(flowInstance.getUuid());
        } else {
            // 保存动态表单
            dyFormFacade.saveFormData(dyFormData);
            // dytableApiFacade.saveFormData(workBean.getRootFormDataBean());
            /* modified by huanglinchuan 2014.12.25 begin */
            if (StringUtils.isBlank(taskInstUuid)) {
                flowInstance = flowService.getFlowInstance(flowInstUuid);
                needGenerateTitle = true;
                // 设置预留字段
                setReservedFields(taskData, workBean.getFormUuid(), workBean.getDataUuid(), workBean);
                ReservedFieldUtils.setReservedFields(flowInstance, taskData);
            }
            /* modified by huanglinchuan 2014.12.25 end */
        }

        if (StringUtils.isNotBlank(taskInstUuid)) {
            TaskInstance task = taskInstanceService.get(taskInstUuid);
            setReservedFields(taskData, workBean.getFormUuid(), workBean.getDataUuid(), workBean);

            /* modified by huanglinchuan 2014.12.25 begin */
            flowInstance = task.getFlowInstance();
            // 设置预留字段
            ReservedFieldUtils.setReservedFields(flowInstance, taskData);

            // 同步子流程信息
            flowService.syncSubFlowInstances(flowInstance, taskData);

            if (flowDelegate.isFirstTaskNode(task.getId())) {
                needGenerateTitle = true;
            }
            /* modified by huanglinchuan 2014.12.25 end */
            this.flowInstanceService.save(flowInstance);

            // 保存任务
            this.taskInstanceService.save(task);
        }

        /* modified by huanglinchuan 2014.12.25 begin */
        if (needGenerateTitle || BooleanUtils.isTrue(flowDefinition.getAutoUpdateTitle())) {
            setFlowInstanceTitle(flowDefinition, flowInstance, taskData, dyFormData);
        }
        /* modified by huanglinchuan 2014.12.25 end */

        // 同步表单映射的流程标题
        updateDyformMappingTitle(workBean, flowInstance, null, dyFormData);

        // 保存后服务处理
        String afterSaveService = workBean.getAfterSaveService();
        if (StringUtils.isNotBlank(afterSaveService)) {
            taskInstanceService.flushSession();
            taskInstanceService.clearSession();

            ServiceInvokeUtils.invoke(afterSaveService, new Class[]{WorkBean.class}, workBean);
        }
        // 用户操作日志
        BusinessOperationLog log = new BusinessOperationLog();
        log.setModuleId(flowDefinition.getModuleId());// ModuleID.WORKFLOW.getValue()
        // log.setModuleName(flowDefinition.getModuleId());// "工作流程"
        log.setDataDefType(ModuleID.WORKFLOW.getValue());
        log.setDataDefId(flowDefinition.getId());
        log.setDataDefName(flowDefinition.getName());
        log.setOperation(WorkFlowOperation.getName(WorkFlowOperation.SAVE));
        log.setUserId(taskData.getUserId());
        log.setUserName(taskData.getUserName());
        log.setDataId(flowInstance.getUuid());
        log.setDataName(flowInstance.getTitle());
        // userOperationLogService.save(log);
        Map<String, Object> details = Maps.newHashMap();
        // details.put("dyform", dyFormData.cloneDyFormDatasToJson());
        // details.put("flowInstUuid", flowInstUuid);
        // details.put("taskInstUuid", taskInstUuid);
        ContextLogs.sendLogEvent(new LogEvent(log, details));

        // 创建流程数据快照
        flowSamplerService.createSnapshot(taskData, null, workBean.getTaskInstUuid(), flowInstance);
        taskData.setFlowInstUuid(flowInstance.getUuid());
        taskData.setDyFormData(flowInstance.getDataUuid(), dyFormData);
        flowIndexDocumentService.indexWorkflowDocument(taskData);

        TaskOperationTemp temp = new TaskOperationTemp();
        temp.setOpinionLabel(workBean.getOpinionLabel());
        temp.setOpinionValue(workBean.getOpinionValue());
        temp.setOpinionText(workBean.getOpinionText());
        temp.setAssignee(userId);
        temp.setAssigneeName(userName);
        temp.setTaskId(workBean.getTaskId());
        temp.setTaskName(workBean.getTaskName());
        temp.setTaskInstUuid(workBean.getTaskInstUuid());
        temp.setFlowInstUuid(workBean.getFlowInstUuid());
        temp.setTaskIdentityUuid(workBean.getTaskIdentityUuid());
        taskOperationTempService.saveTemp(temp);

        Map<String, String> item = new HashMap<String, String>();
        // setReservedFields(dyFormData, workBean);
        // item.put("reservedText1", workBean.getReservedText1());
        // item.put("reservedText2", workBean.getReservedText2());
        // item.put("reservedText3", workBean.getReservedText3());
        // item.put("reservedText4", workBean.getReservedText4());
        // item.put("reservedText5", workBean.getReservedText5());
        // item.put("reservedText6", workBean.getReservedText6());
        // item.put("reservedText7", workBean.getReservedText7());
        // item.put("reservedText8", workBean.getReservedText8());
        // item.put("reservedText9", workBean.getReservedText9());
        // item.put("reservedText10", workBean.getReservedText10());
        // item.put("reservedNumber1", workBean.getReservedNumber1());
        // item.put("reservedNumber2", workBean.getReservedNumber2());
        // item.put("reservedNumber3", workBean.getReservedNumber3());
        // item.put("reservedDate1", workBean.getReservedDate1());
        // item.put("reservedDate2", workBean.getReservedDate2());
        item.put("flowInstUuid", workBean.getFlowInstUuid());
        item.put("dataUuid", workBean.getDataUuid());
        item.put("formDataUpdated", FormDataHandler.isUpdated() + "");
        return item;
    }

    @Override
    @Transactional
    public Map<String, String> saveTemp(WorkBean workBean) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        DyFormData dyFormData = workBean.getDyFormData();
        // 检验表单数据
        ValidateMsg validateMsg = dyFormData.validateFormDataWithDatabaseConstraints();
        if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
            throw new FormDataValidateException(validateMsg);
        }

        String title = workBean.getTitle();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String flowInstUuid = workBean.getFlowInstUuid();
        String taskInstUuid = workBean.getTaskInstUuid();
        List<String> opinionFileIds = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(workBean.getOpinionFiles())) {
            opinionFileIds.addAll(workBean.getOpinionFiles().stream().map(LogicFileInfo::getFileID).collect(Collectors.toList()));
        }
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(workBean.getFlowDefUuid());
        if (flowDelegate.getFlow().getProperty().getIsAutoUpdateTitle()) {
            FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
            TaskData taskData = new TaskData();
            title = TitleExpressionUtils.generateFlowInstanceTitle(flowInstance.getFlowDefinition(), flowInstance, taskData, dyFormData);
        }

        Map<String, Object> formData = Maps.newHashMap();
        WfTaskTodoTempEntity taskTodoTempEntity = taskTodoTempService.getByTaskInstUuidAndUserId(taskInstUuid, userId);
        if (taskTodoTempEntity == null) {
            taskTodoTempEntity = new WfTaskTodoTempEntity();
            DyFormData currentFormData = dyFormFacade.getDyFormData(dyFormData.getFormUuid(), dyFormData.getDataUuid());
            formData.put("init", currentFormData.getFormDatas());
            formData.put("temp", dyFormData.getFormDatas());
        } else {
            formData = JsonUtils.json2Object(taskTodoTempEntity.getFormData(), Map.class);
            formData.put("temp", dyFormData.getFormDatas());
            // 合并了最新的表单数据
            if (workBean.getTempTaskInstRecVer() != null && workBean.getTaskInstRecVer() != null
                    && workBean.getTempTaskInstRecVer() >= workBean.getTaskInstRecVer()) {
                DyFormData currentFormData = dyFormFacade.getDyFormData(dyFormData.getFormUuid(), dyFormData.getDataUuid());
                formData.put("init", currentFormData.getFormDatas());
            }
        }
        taskTodoTempEntity.setTitle(title);
        taskTodoTempEntity.setFlowInstUuid(flowInstUuid);
        taskTodoTempEntity.setTaskInstUuid(taskInstUuid);
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        if (taskInstance.getRecVer().equals(taskTodoTempEntity.getTaskInstRecVer())) {
            // 忽略数据变更
        } else if (workBean.getTempTaskInstRecVer() != null) {
            taskTodoTempEntity.setTaskInstRecVer(workBean.getTempTaskInstRecVer());
        } else if (workBean.getTaskInstRecVer() != null) {
            taskTodoTempEntity.setTaskInstRecVer(workBean.getTaskInstRecVer());
        } else {
            taskTodoTempEntity.setTaskInstRecVer(taskInstance.getRecVer());
        }
        taskTodoTempEntity.setUserId(userId);
        taskTodoTempEntity.setFormUuid(dyFormData.getFormUuid());
        taskTodoTempEntity.setDataUuid(dyFormData.getDataUuid());
        taskTodoTempEntity.setFormData(JsonUtils.object2Json(formData));
        taskTodoTempEntity.setOpinionLabel(workBean.getOpinionLabel());
        taskTodoTempEntity.setOpinionValue(workBean.getOpinionValue());
        taskTodoTempEntity.setOpinionText(workBean.getOpinionText());
        taskTodoTempEntity.setOpinionFileIds(StringUtils.join(opinionFileIds, Separator.SEMICOLON.getValue()));
        taskTodoTempService.save(taskTodoTempEntity);
        if (CollectionUtils.isNotEmpty(opinionFileIds)) {
            mongoFileService.pushFilesToFolder(taskTodoTempEntity.getUuid().toString(), opinionFileIds, "task_todo_temp");
        }

        Map<String, String> item = Maps.newHashMap();
        item.put("flowInstUuid", workBean.getFlowInstUuid());
        item.put("dataUuid", workBean.getDataUuid());
        item.put("formDataUpdated", FormDataHandler.isUpdated() + "");
        return item;
    }

    @Override
    public void fillTaskTodoTempIfRequired(WorkBean workBean) {
        WfTaskTodoTempEntity taskTodoTempEntity = taskTodoTempService.getByTaskInstUuidAndUserId(workBean.getTaskInstUuid(), SpringSecurityUtils.getCurrentUserId());
        if (taskTodoTempEntity != null && workBean.getDyFormData() != null && StringUtils.equals(workBean.getDyFormData().getFormUuid(), taskTodoTempEntity.getFormUuid())
                && StringUtils.equals(workBean.getDataUuid(), taskTodoTempEntity.getDataUuid())) {
            Map<String, Object> formData = JsonUtils.json2Object(taskTodoTempEntity.getFormData(), Map.class);
            workBean.setTitle(taskTodoTempEntity.getTitle());
            // 环节只有一个办理人办理，不需要合并表单数据
            String currentUserId = SpringSecurityUtils.getCurrentUserId();
            TaskInstance taskInstance = taskService.getTask(taskTodoTempEntity.getTaskInstUuid());
            List<Integer> actionCodes = Lists.newArrayList(WorkFlowOperation.getActionCodeOfSubmit());
            actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
            actionCodes.addAll(Lists.newArrayList(ActionCode.TRANSFER.getCode(), ActionCode.COUNTER_SIGN.getCode(), ActionCode.ADD_SIGN.getCode()));
            if (StringUtils.equals(taskInstance.getOwner(), currentUserId)
                    && StringUtils.equals(taskInstance.getOwner(), currentUserId)
                    && StringUtils.equals(currentUserId, taskInstance.getTodoUserId())
                    && CollectionUtils.isEmpty(taskOperationService.listByTaskInstUuidAndActionCodes(taskInstance.getUuid(), actionCodes))) {
                workBean.setTempTaskInstRecVer(taskInstance.getRecVer());
            } else {
                if (formData.containsKey("init")) {
                    workBean.getDyFormData().setFormDatas((Map<String, List<Map<String, Object>>>) formData.get("init"), false);
                }
                workBean.setTempTaskInstRecVer(taskTodoTempEntity.getTaskInstRecVer());
            }
            if (formData.containsKey("temp")) {
                workBean.setTempFormDatas((Map<String, List<Map<String, Object>>>) formData.get("temp"));
            }
            workBean.setOpinionLabel(taskTodoTempEntity.getOpinionLabel());
            workBean.setOpinionValue(taskTodoTempEntity.getOpinionValue());
            workBean.setOpinionText(taskTodoTempEntity.getOpinionText());
            if (StringUtils.isNotBlank(taskTodoTempEntity.getOpinionFileIds())) {
                workBean.setOpinionFiles(mongoFileService.getNonioFilesFromFolder(taskTodoTempEntity.getUuid().toString(), "task_todo_temp"));
            }
        }
    }

    /**
     * @param taskInstUuid
     * @param userId
     */
    private void deleteTemp(String taskInstUuid, String userId) {
        if (StringUtils.isBlank(taskInstUuid)) {
            return;
        }

        WfTaskTodoTempEntity taskTodoTempEntity = taskTodoTempService.getByTaskInstUuidAndUserId(taskInstUuid, userId);
        if (taskTodoTempEntity != null) {
            taskTodoTempService.delete(taskTodoTempEntity);
            if (StringUtils.isNotBlank(taskTodoTempEntity.getOpinionFileIds())) {
                mongoFileService.popAllFilesFromFolder(taskTodoTempEntity.getUuid().toString());
            }
        }
    }

    /**
     * @param taskInstUuid
     */
    private void deleteTempByTaskInstUuid(String taskInstUuid) {
        if (StringUtils.isBlank(taskInstUuid)) {
            return;
        }

        List<WfTaskTodoTempEntity> taskTodoTempEntities = taskTodoTempService.listByTaskInstUuid(taskInstUuid);
        if (CollectionUtils.isNotEmpty(taskTodoTempEntities)) {
            taskTodoTempService.deleteByEntities(taskTodoTempEntities);
            taskTodoTempEntities.forEach(entity -> {
                if (StringUtils.isNotBlank(entity.getOpinionFileIds())) {
                    mongoFileService.popAllFilesFromFolder(entity.getUuid().toString());
                }
            });
        }
    }

    /**
     * @param workBean
     * @param flowInstance
     * @param taskData
     * @param dyFormData
     */
    private void updateDyformMappingTitle(WorkBean workBean, FlowInstance flowInstance, TaskData taskData,
                                          DyFormData dyFormData) {
        if (dyFormData == null) {
            return;
        }
        // 同步流程字段映射
        boolean hasUpdateDyformData = false;
        // 标题
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.TITLE.getValue())) {
            dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.TITLE.getValue(), flowInstance.getTitle());
            hasUpdateDyformData = true;
        }
        // 流程实例UUID
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.FLOW_INST_UUID.getValue())) {
            dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.FLOW_INST_UUID.getValue(),
                    flowInstance.getUuid());
            hasUpdateDyformData = true;
        }
        if (hasUpdateDyformData) {
            if (taskData != null) {
                taskData.setFormUuid(StringUtils.isNotBlank(flowInstance.getFormUuid()) ? flowInstance.getFormUuid() : dyFormData.getFormUuid());
                taskData.setDataUuid(dyFormData.getDataUuid());
                taskData.setDyFormData(dyFormData.getDataUuid(), dyFormData);
                taskData.addUpdatedDyFormData(dyFormData.getDataUuid(), dyFormData);
            } else {
                dyFormData.doForceCover();
                String dataUuid = dyFormFacade.saveFormData(dyFormData);
                workBean.setDataUuid(dataUuid);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#submit(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public ResultMessage submit(WorkBean workBean) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        // 提交前服务处理
        String beforeSubmitService = workBean.getBeforeSubmitService();
        if (StringUtils.isNotBlank(beforeSubmitService)) {
            ServiceInvokeUtils.invoke(beforeSubmitService, new Class[]{WorkBean.class}, workBean);

            taskInstanceService.flushSession();
            taskInstanceService.clearSession();
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        workBean.setUserId(userId);
        workBean.setUserName(user.getUserName());
        String taskInstUuid = workBean.getTaskInstUuid();
        String flowInstUuid = workBean.getFlowInstUuid();
        String flowDefUuid = workBean.getFlowDefUuid();

        /* modified by huanglinchuan 2014.12.25 begin */
        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        FlowInstance flowInstance = null;
        // 当移动端审批已打开，后面又关闭移动端审批，在关闭前若有用户打开审批界面，进行审批，提交时校验并提示：系统管理员关闭移动端审批权限，请在电脑端进行审批
        checkMobileAppSubmit(workBean, flowDelegate);

        // 意见日志信息
        // 保存动态表单，设置信息格式
        List<String> opinionLogUuids = null;
        if (StringUtils.isNotBlank(flowInstUuid)) {
            opinionLogUuids = taskFormOpinionService.recordTaskFormOpinion(workBean, flowDelegate);
        }
        /* modified by huanglinchuan 2014.10.27 end */

        String dataUuid = null;
        Map<String, Map<String, List<String>>> updatedFormDatas = null;
        if (workBean.getDyFormData() != null) {
            if (flowDelegate.getFlow().getProperty().isEnabledAutoSubmit()) {
                updatedFormDatas = JsonUtils.json2Object(JsonUtils.object2Json(workBean.getDyFormData().getUpdatedFormDatas()), Map.class);
            }
            ValidateMsg validateMsg = workBean.getDyFormData().validateFormDataWithDatabaseConstraints();
            // 生成信息记录后进行检验
            if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
                if (CollectionUtils.isNotEmpty(opinionLogUuids)) {
                    validateMsg.setMsg("流程生成的信息记录设置到表单后，表单数据检验失败");
                }
                throw new FormDataValidateException(validateMsg);
            }
            dataUuid = dyFormFacade.saveFormData(workBean.getDyFormData());
            taskInstanceService.flushSession();
            taskInstanceService.clearSession();
        } else {
            dataUuid = workBean.getDataUuid();
        }

        // 创建提交的任务数据
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);
        taskData.setUpdatedFormDatas(dataUuid, updatedFormDatas);

        // 任务UUID不为空则提交当前工作
        if (StringUtils.isNotBlank(taskInstUuid)) {
            // 填充任务数据
            fillTaskData(workBean, user, taskInstUuid, opinionLogUuids, taskData);

            if (workBean.isRequiredSubmitPermission()) {
                preCheckTodo(user, taskInstUuid);
            }
            /* modified by huanglinchuan 2014.12.25 begin */
            TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
            flowInstance = taskInstance.getFlowInstance();
            Boolean isSubFlow = flowInstance.getParent() == null ? Boolean.FALSE : Boolean.TRUE;
            if ((flowDelegate.isFirstTaskNode(taskInstance.getId()) && !isSubFlow)
                    || Boolean.TRUE.equals(workBean.isAutoUpdateTitle())) {
                setFlowInstanceTitle(flowDefinition, flowInstance, taskData, workBean.getDyFormData());
                // 同步表单映射的流程标题
                updateDyformMappingTitle(workBean, flowInstance, taskData, workBean.getDyFormData());
            }
            /* modified by huanglinchuan 2014.12.25 end */
            taskService.submit(taskInstUuid, taskData);
            // 环节提交消息通知
            List<MessageTemplate> templates = flowDelegate.getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY.getType());
            MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY, templates,
                    taskInstance, taskInstance.getFlowInstance(), Collections.EMPTY_LIST, ParticipantType.CopyUser);
        } else if (StringUtils.isNotBlank(flowInstUuid)) {// 从工作草稿启动流程
            flowInstance = flowService.startFlowInstance(flowInstUuid, taskData);
            setFlowInstanceTitle(flowDefinition, flowInstance, taskData, workBean.getDyFormData());
            // 同步表单映射的流程标题
            updateDyformMappingTitle(workBean, flowInstance, taskData, workBean.getDyFormData());
            // 提交第一个任务
            List<TaskInstance> taskInstances = taskService.getTodoTasks(userId, flowInstUuid);
            for (TaskInstance taskInstance : taskInstances) {
                // 填充任务数据
                fillTaskData(workBean, user, taskInstance.getUuid(), opinionLogUuids, taskData);

                if (workBean.isRequiredSubmitPermission()) {
                    preCheckTodo(user, taskInstance.getUuid());
                }
                taskService.submit(taskInstance, taskData);
                // 环节提交消息通知
                List<MessageTemplate> templates = flowDelegate.getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY.getType());
                MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY, templates,
                        taskInstance, taskInstance.getFlowInstance(), Collections.EMPTY_LIST, ParticipantType.CopyUser);
            }
        } else if (StringUtils.isNotBlank(flowDefUuid)) {// 从流程定义启动流程
            // 工作保存为草稿
            /* modified by huanglinchuan 2014.12.25 begin */
            flowInstance = flowService.saveAsDraft(flowDefUuid, taskData);
            // 意见日志信息
            // 保存动态表单，设置信息格式
            workBean.setFlowInstUuid(flowInstance.getUuid());
            workBean.setDataUuid(dataUuid);
            opinionLogUuids = taskFormOpinionService.recordTaskFormOpinion(workBean, flowDelegate);
            // 生成信息记录后进行检验
            if (CollectionUtils.isNotEmpty(opinionLogUuids)) {
                ValidateMsg validateMsg = workBean.getDyFormData().validateFormDataWithDatabaseConstraints();
                if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
                    validateMsg.setMsg("流程生成的信息记录设置到表单后，表单数据检验失败");
                    throw new FormDataValidateException(validateMsg);
                }
                dyFormFacade.saveFormData(workBean.getDyFormData());
            }
            setFlowInstanceTitle(flowDefinition, flowInstance, taskData, workBean.getDyFormData());
            // 同步表单映射的流程标题
            updateDyformMappingTitle(workBean, flowInstance, taskData, workBean.getDyFormData());

            flowInstUuid = flowInstance.getUuid();
            workBean.setFlowInstUuid(flowInstUuid);
            flowService.startFlowInstance(flowInstUuid, taskData);
            /* modified by huanglinchuan 2014.12.25 end */
            // 提交第一个任务
            List<TaskInstance> taskInstances = taskService.getTodoTasks(userId, flowInstUuid);
            for (TaskInstance taskInstance : taskInstances) {
                // 填充任务数据
                fillTaskData(workBean, user, taskInstance.getUuid(), opinionLogUuids, taskData);

                if (workBean.isRequiredSubmitPermission()) {
                    preCheckTodo(user, taskInstance.getUuid());
                }
                taskService.submit(taskInstance, taskData);
                FlowInstance flowInstanceNew = flowInstanceService.get(flowInstance.getUuid());
                setFlowInstanceTitle(flowDefinition, flowInstanceNew, taskData, workBean.getDyFormData());
                // 环节提交消息通知
                List<MessageTemplate> templates = flowDelegate.getMessageTemplateMap().get(WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY.getType());
                MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_TASK_SUBMIT_NOTIFY, templates,
                        taskInstance, taskInstance.getFlowInstance(), Collections.EMPTY_LIST, ParticipantType.CopyUser);

            }
        }

        // 提交后套打
        boolean isPrintAfterSubmit = workBean.isPrintAfterSubmit();
        SubmitResult submitResult = taskData.getSubmitResult();
        if (StringUtils.isBlank(submitResult.getFlowInstUuid())) {
            submitResult.setFlowInstUuid(flowInstUuid);
        }
        if (isPrintAfterSubmit) {
            if (StringUtils.isBlank(submitResult.getFromTaskId())) {
                submitResult.setFlowInstUuid(taskData.getFlowInstUuid());
                submitResult.setFromTaskInstUuid(taskData.getTaskInstUuid());
                submitResult.setFromTaskId(workBean.getFromTaskId());
            }
            printAfterSubmit(submitResult, flowDelegate);
        }

        // 索引流程文档
        flowIndexDocumentService.indexWorkflowDocument(taskData);

        // 提交后服务处理
        String afterSubmitService = workBean.getAfterSubmitService();
        if (StringUtils.isNotBlank(afterSubmitService)) {
            ServiceInvokeUtils.invoke(afterSubmitService, new Class[]{WorkBean.class}, workBean);
        }

        // 添加最近签署意见
        if (StringUtils.isNotBlank(workBean.getOpinionText())) {
            flowOpinionService.addRecentOpinion(userId, workBean.getFlowDefId(), workBean.getTaskId(),
                    StringUtils.trim(workBean.getOpinionText()));
            // 删除多于10条的最近办理意见
            flowOpinionService.removeMoreThanTenRecentOpinion(userId, workBean.getFlowDefId(), workBean.getTaskId());
        }
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), userId);
        deleteTemp(workBean.getTaskInstUuid(), userId);

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        // 返回提交结果
        ResultMessage msg = new ResultMessage();
        submitResult.setFormDataUpdated(FormDataHandler.isUpdated());
        msg.setData(submitResult);
        return msg;
    }

    /**
     * @param workBean
     */
    private void preCheckWorkDataState(WorkBean workBean) {
        if (SuspensionState.SUSPEND.getState() == workBean.getSuspensionState()
                || SuspensionState.LOGIC_SUSPEND.getState() == workBean.getSuspensionState()) {
            String action = workBean.getAction();
            if (StringUtils.isBlank(action)) {
                action = "操作";
            }
            throw new WorkFlowException("当前环节处于挂起状态，不能进行" + action + "!");
        }

        // 校验环节实例数据是否变更
        Integer tempTaskInstRecVer = workBean.getTempTaskInstRecVer();
        if (tempTaskInstRecVer == null) {
            // 后台任务执行的不进行检验
            if (workBean.isDaemon() || IgnoreLoginUtils.isIgnoreLogin()) {
                return;
            }
            tempTaskInstRecVer = workBean.getTaskInstRecVer();
        }
        if (tempTaskInstRecVer != null && StringUtils.isNotBlank(workBean.getTaskInstUuid())) {
            TaskInstance taskInstance = taskService.getTask(workBean.getTaskInstUuid());
            if (taskInstance != null && !taskInstance.getRecVer().equals(tempTaskInstRecVer)) {
                String action = taskInstance.getAction();
                String actionType = taskInstance.getActionType();
                String currentUserId = SpringSecurityUtils.getCurrentUserId();
                List<Integer> actionCodes = Lists.newArrayList(WorkFlowOperation.getActionCodeOfSubmit());
                actionCodes.addAll(WorkFlowOperation.getActionCodeOfRollback());
                actionCodes.addAll(Lists.newArrayList(ActionCode.TRANSFER.getCode(), ActionCode.COUNTER_SIGN.getCode(), ActionCode.ADD_SIGN.getCode()));
                List<TaskOperation> taskOperations = taskOperationService.listByTaskInstUuidAndActionCodes(taskInstance.getUuid(), actionCodes);
                if ((CollectionUtils.isEmpty(taskOperations)
                        && StringUtils.equals(taskInstance.getOwner(), currentUserId)
                        && StringUtils.equals(currentUserId, taskInstance.getTodoUserId()))
                        || isIgnoreTaskTodoTempChanged(taskInstance, currentUserId)) {
                    // 单人办理直接提交，不用合并
                } else {
                    TaskOperation taskOperation = CollectionUtils.isEmpty(taskOperations) ? null : taskOperations.get(0);
                    if (taskOperation != null) {
                        action = taskOperation.getAction();
                        actionType = taskOperation.getActionType();
                    }
                    throw new TaskDataChangedException(taskInstance.getUuid(), taskInstance.getRecVer(), action, actionType);
                }
            }
        }
    }

    /**
     * @param taskInstance
     * @param userId
     * @return
     */
    private boolean isIgnoreTaskTodoTempChanged(TaskInstance taskInstance, String userId) {
        WfTaskTodoTempEntity taskTodoTempEntity = taskTodoTempService.getByTaskInstUuidAndUserId(taskInstance.getUuid(), userId);
        return taskTodoTempEntity == null || taskInstance.getRecVer().equals(taskTodoTempEntity.getTaskInstRecVer());
    }

    /**
     * @param workBean
     * @param flowDelegate
     */
    private void checkMobileAppSubmit(WorkBean workBean, FlowDelegate flowDelegate) {
        if (isMobileApp() && !flowDelegate.isAllowApp(workBean.getTaskId())) {
            throw new WorkFlowException("系统管理员关闭移动端审批权限，请在电脑端进行审批！");
        }
    }

    /**
     * @param submitResult
     * @param flowDelegate
     */
    private void printAfterSubmit(SubmitResult submitResult, FlowDelegate flowDelegate) {
        List<com.wellsoft.pt.bpm.engine.support.PrintTemplate> printTemplates = flowDelegate
                .getTaskPrintTemplates(submitResult.getFromTaskId());
        if (CollectionUtils.isEmpty(printTemplates)) {
            throw new RuntimeException("流程配置了提交并套打，但没有配置套打模板！");
        }
        LogicFileInfo logicFileInfo = print(submitResult.getFromTaskInstUuid(), printTemplates.get(0).getId());
        submitResult.setPrintResultFileId(logicFileInfo.getFileID());
    }

    /**
     * @param taskInstUuid
     */
    private void preCheckTodo(UserDetails user, String taskInstUuid) {
        if (!flowPermissionEvaluator.hasPermission(user, taskInstUuid, AclPermission.TODO)) {
            throw new WorkFlowException("工作不存在或已经提交!");
        }
    }

    /**
     * 如何描述该方法
     *
     * @param workBean
     * @param user
     * @param userId
     * @param taskUuid
     * @param dataUuid
     * @return
     */
    protected TaskData createTaskData(WorkBean workBean, UserDetails user, String taskInstUuid, String dataUuid) {
        String userId = user.getUserId();
        // 设置任务数据
        TaskData taskData = new TaskData();
        // 当前用户Id
        taskData.setUserId(userId);
        // 当前用户名
        taskData.setUserName(user.getUserName());
        // 当前用户信息
        taskData.setUserDetails(user);
        // 表单定义UUID
        taskData.setFormUuid(workBean.getFormUuid());
        // 表单数据UUID
        taskData.setDataUuid(dataUuid);
        // 流程实例UUID
        taskData.setFlowInstUuid(workBean.getFlowInstUuid());
        // 环节实例UUID
        taskData.setTaskInstUuid(taskInstUuid);
        // 表单数据
        DyFormData dyFormData = workBean.getDyFormData();
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(workBean.getFormUuid(), dataUuid);
            workBean.setDyFormData(dyFormData);
        } else {
            dyFormData.setLoadDictionary(false);
            dyFormData.setLoadDefaultFormData(true);
            dyFormData.setLoadSubformDefinition(true);
            dyFormData.initFormDefintion();
        }
        taskData.setDyFormData(dataUuid, dyFormData);
        taskData.setRecords(workBean.getTaskId(), workBean.getRecords());
        // 流程标题
        String title = workBean.getTitle();
        if (workBean.getDyFormData().hasFieldMappingName(WorkFlowFieldMapping.TITLE.getValue())) {
            Object tmpTitle = workBean.getDyFormData()
                    .getFieldValueByMappingName(WorkFlowFieldMapping.TITLE.getValue());
            title = tmpTitle == null ? title : tmpTitle.toString();
        }
        taskData.setTitle(workBean.getFlowDefId(), title);
        // 流水号定义ID
        taskData.setSerialNoDefId(workBean.getSerialNoDefId());
        // 提交按钮ID
        taskData.setSubmitButtonId(workBean.getSubmitButtonId());
        // 承办人
        taskData.setTaskUsers(workBean.getTaskUsers());
        // 受理人 承办人 职位路径保存
        taskData.setTaskUserJobPaths(workBean.getTaskUserJobPaths());
        // 抄送人
        taskData.addTaskCopyUsers(workBean.getTaskCopyUsers());
        if (MapUtils.isNotEmpty(workBean.getTaskCopyUsers())) {
            for (Map.Entry<String, List<String>> entry : workBean.getTaskCopyUsers().entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue())) {
                    taskData.setSpecifyTaskCopyUser(entry.getKey(), true);
                }
            }
        }
        // 督办人
        taskData.addTaskMonitors(workBean.getTaskMonitors());
        // 决策人
        taskData.addTaskDecisionMakers(workBean.getTaskDecisionMakers());
        // 下一流向ID
        taskData.setToDirectionId(workBean.getFromTaskId(), workBean.getToDirectionId());
        // 下一流向ID MAP
        taskData.setToDirectionIds(workBean.getToDirectionIds());
        // 下一环节ID
        taskData.setToTaskId(workBean.getFromTaskId(), workBean.getToTaskId());
        // 下一环节ID MAP
        taskData.setToTaskIds(workBean.getToTaskIds());
        // 是否跳转到下一环节
        taskData.setGotoTask(workBean.getFromTaskId(), workBean.isGotoTask());
        // 下一子流程ID
        taskData.setToSubFlowId(workBean.getToSubFlowId());
        // 子流程等待合并<subFlowInstUuid, isWait>
        taskData.setWaitForMerge(workBean.getWaitForMerge());
        // 归档夹
        taskData.setArchiveFolderUuid(workBean.getTaskId(), workBean.getArchiveFolderUuid());
        // 设置流程的业务类型
        String businessTypeId = workBean.getBusinessTypeId();
        taskData.setBusinessType(businessTypeId);
        taskData.setJobSelected(userId, workBean.getJobSelected());// 设置前端弹出选择的职位
        taskData.setUserJobIdentityId(userId, taskInstUuid, workBean.getJobSelected());
        taskData.setMainJob(user.getMainJob() != null ? user.getMainJob().getEleId() : null);// 设置当前用户的主职

        // 自定义提交按钮
        if (workBean.getCustomDynamicButton() != null) {
            CustomDynamicButton customDynamicButton = workBean.getCustomDynamicButton();
            taskData.setUseCustomDynamicButton(workBean.getTaskId(), true);
            taskData.setCustomDynamicButton(workBean.getTaskId(), customDynamicButton);
        } else {
            taskData.setUseCustomDynamicButton(workBean.getTaskId(), false);
        }
        // 设置预留字段
        setReservedFields(taskData, workBean.getFormUuid(), dataUuid, workBean);

        // 设置流程所属部门及单位
        if (StringUtils.isBlank(taskInstUuid)) {
            setFlowDepartmentAndUnit(workBean, userId, taskData, businessTypeId);
            setStartJobId(workBean, taskData);
        }

        // 加入自定义运行时参数
        Map<String, Object> exTraParams = workBean.getExtraParams();
        for (String key : exTraParams.keySet()) {
            if (key.startsWith(CustomRuntimeData.PREFIX)) {
                taskData.setCustomData(key, exTraParams.get(key));
            }
        }

        // 添加流程业务运行时数据
        addFlowBizCustomRuntimeData(workBean.getFlowBizDefId(), taskData);

        // 填充任务数据
        fillTaskData(workBean, user, taskInstUuid, null, taskData);

        taskData.setDaemon(workBean.isDaemon());

        return taskData;
    }

    /**
     * 添加流程业务运行时数据
     *
     * @param flowBizDefId
     * @param taskData
     */
    private void addFlowBizCustomRuntimeData(String flowBizDefId, TaskData taskData) {
        if (StringUtils.isBlank(flowBizDefId)) {
            return;
        }
        // 流程业务ID运行时参数
        taskData.setCustomData(CustomRuntimeData.KEY_FLOW_BIZ_DEF_ID, flowBizDefId);
        // 流程业务运行时监听器
        List<String> rtFlowListeners = Lists.newArrayList(WfFlowBusinessFlowListener.BEAN_NAME);
        List<String> rtTaskListeners = Lists.newArrayList(WfFlowBusinessTaskListener.BEAN_NAME);
        List<String> rtDirectionListeners = Lists.newArrayList(WfFlowBusinessDirectionListener.BEAN_NAME);
        String rtFlowListener = Objects.toString(taskData.getCustomData(CustomRuntimeData.KEY_FLOW_LISTENER), StringUtils.EMPTY);
        String rtTaskListener = Objects.toString(taskData.getCustomData(CustomRuntimeData.KEY_TASK_LISTENER), StringUtils.EMPTY);
        String rtDirectionListener = Objects.toString(taskData.getCustomData(CustomRuntimeData.KEY_DIRECTION_LISTENER), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(rtFlowListener)) {
            rtFlowListeners.addAll(Arrays.asList(StringUtils.split(rtFlowListener, Separator.SEMICOLON.getValue())));
        }
        if (StringUtils.isNotBlank(rtTaskListener)) {
            rtTaskListeners.addAll(Arrays.asList(StringUtils.split(rtTaskListener, Separator.SEMICOLON.getValue())));
        }
        if (StringUtils.isNotBlank(rtDirectionListener)) {
            rtDirectionListeners.addAll(Arrays.asList(StringUtils.split(rtDirectionListener, Separator.SEMICOLON.getValue())));
        }
        taskData.setCustomData(CustomRuntimeData.KEY_FLOW_LISTENER, StringUtils.join(rtFlowListeners, Separator.SEMICOLON.getValue()));
        taskData.setCustomData(CustomRuntimeData.KEY_TASK_LISTENER, StringUtils.join(rtTaskListeners, Separator.SEMICOLON.getValue()));
        taskData.setCustomData(CustomRuntimeData.KEY_DIRECTION_LISTENER, StringUtils.join(rtDirectionListeners, Separator.SEMICOLON.getValue()));
    }

    private void setStartJobId(WorkBean workBean, TaskData taskData) {
    }

    /**
     * 如何描述该方法
     *
     * @param workBean
     * @param userId
     * @param taskData
     * @param businessTypeId
     */
    private void setFlowDepartmentAndUnit(WorkBean workBean, String userId, TaskData taskData, String businessTypeId) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        UserSystemOrgDetails userSystemOrgDetails = userDetails.getUserSystemOrgDetails();
        String jobPath = null;
        String depId = null;
        String unitId = null;
        if (StringUtils.equals(userDetails.getUserId(), userId)) {
            UserSystemOrgDetails.OrgDetail orgDetail = userSystemOrgDetails.currentSystemOrgDetail();
            if (orgDetail != null) {
                OrgTreeNodeDto mainJob = orgDetail.getMainJob();
                jobPath = mainJob != null ? mainJob.getEleIdPath() : StringUtils.EMPTY;
                depId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
                unitId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.SYSTEM_UNIT.getValue());
            }
        } else {
            List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(userId);
            OrgUserJobDto mainJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst()
                    .orElse(CollectionUtils.isNotEmpty(userJobDtos) ? userJobDtos.get(0) : null);
            jobPath = mainJob != null ? mainJob.getJobIdPath() : StringUtils.EMPTY;
            depId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
            unitId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.SYSTEM_UNIT.getValue());
        }

        // 流程所有者
        if (StringUtils.isNotBlank(workBean.getOwnerId())) {
            taskData.setFlowOwnerId(workBean.getOwnerId());
        }

        // 流程所起部门
        if (StringUtils.isNotBlank(workBean.getStartDepartmentId())) {
            taskData.setFlowStartDepartmentId(workBean.getStartDepartmentId());
        } else {
            taskData.setFlowStartDepartmentId(depId);
        }
        // 流程所起单位
        if (StringUtils.isNotBlank(workBean.getStartUnitId())) {
            taskData.setFlowStartUnitId(workBean.getStartUnitId());
        } else {
            taskData.setFlowStartUnitId(unitId);
        }

        // 流程所属部门
        if (StringUtils.isNotBlank(workBean.getOwnerDepartmentId())) {
            taskData.setFlowOwnerDepartmentId(workBean.getOwnerDepartmentId());
        } else {
            taskData.setFlowOwnerDepartmentId(depId);
        }
        // 流程所属单位
        if (StringUtils.isNotBlank(workBean.getOwnerUnitId())) {
            taskData.setFlowOwnerUnitId(workBean.getOwnerUnitId());
        } else {
            taskData.setFlowOwnerUnitId(unitId);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskData
     * @param formUuid
     * @param dataUuid
     */
    private void setReservedFields(TaskData taskData, String formUuid, String dataUuid, WorkBean workBean) {
        // FormAndDataBean formAndDataBean =
        // dytableApiFacade.getFormData(formUuid, dataUuid);
        DyFormData dyFormData = workBean.getDyFormData();
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid, false);
        }
        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_1.getValue())) {
            Object reservedText1 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_1.getValue());
            taskData.setReservedText1(objectToString(reservedText1));
        } else {
            taskData.setReservedText1(workBean.getReservedText1());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_2.getValue())) {
            Object reservedText2 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_2.getValue());
            taskData.setReservedText2(objectToString(reservedText2));
        } else {
            taskData.setReservedText2(workBean.getReservedText2());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_3.getValue())) {
            Object reservedText3 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_3.getValue());
            taskData.setReservedText3(objectToString(reservedText3));
        } else {
            taskData.setReservedText3(workBean.getReservedText3());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_4.getValue())) {
            Object reservedText4 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_4.getValue());
            taskData.setReservedText4(objectToString(reservedText4));
        } else {
            taskData.setReservedText4(workBean.getReservedText4());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_5.getValue())) {
            Object reservedText5 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_5.getValue());
            taskData.setReservedText5(objectToString(reservedText5));
        } else {
            taskData.setReservedText5(workBean.getReservedText5());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_6.getValue())) {
            Object reservedText6 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_6.getValue());
            taskData.setReservedText6(objectToString(reservedText6));
        } else {
            taskData.setReservedText6(workBean.getReservedText6());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_7.getValue())) {
            Object reservedText7 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_7.getValue());
            taskData.setReservedText7(objectToString(reservedText7));
        } else {
            taskData.setReservedText7(workBean.getReservedText7());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_8.getValue())) {
            Object reservedText8 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_8.getValue());
            taskData.setReservedText8(objectToString(reservedText8));
        } else {
            taskData.setReservedText8(workBean.getReservedText8());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_9.getValue())) {
            Object reservedText9 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_9.getValue());
            taskData.setReservedText9(objectToString(reservedText9));
        } else {
            taskData.setReservedText9(workBean.getReservedText9());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_10.getValue())) {
            Object reservedText10 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_10.getValue());
            taskData.setReservedText10(objectToString(reservedText10));
        } else {
            taskData.setReservedText10(workBean.getReservedText10());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_11.getValue())) {
            Object reservedText11 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_11.getValue());
            taskData.setReservedText11(objectToString(reservedText11));
        } else {
            taskData.setReservedText11(workBean.getReservedText11());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_TEXT_12.getValue())) {
            Object reservedText12 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_TEXT_12.getValue());
            taskData.setReservedText12(objectToString(reservedText12));
        } else {
            taskData.setReservedText12(workBean.getReservedText12());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_1.getValue())) {
            Object reservedNumber1 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_1.getValue());
            taskData.setReservedNumber1(reservedNumber1 == null ? null : Integer.valueOf(reservedNumber1.toString()));
        } else {
            taskData.setReservedNumber1(workBean.getReservedNumber1());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_2.getValue())) {
            Object reservedNumber2 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_2.getValue());
            taskData.setReservedNumber2(reservedNumber2 == null ? null : Double.valueOf(reservedNumber2.toString()));
        } else {
            taskData.setReservedNumber2(workBean.getReservedNumber2());
        }

        if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_3.getValue())) {
            Object reservedNumber3 = dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_NUMBER_3.getValue());
            taskData.setReservedNumber3(reservedNumber3 == null ? null : Double.valueOf(reservedNumber3.toString()));
        } else {
            taskData.setReservedNumber3(workBean.getReservedNumber3());
        }

        try {
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_DATE_1.getValue())) {
                Object reservedDate1 = dyFormData
                        .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_DATE_1.getValue());
                if (reservedDate1 instanceof Date) {
                    taskData.setReservedDate1((Date) reservedDate1);
                } else {
                    taskData.setReservedDate1(reservedDate1 == null ? null : DateUtils.parse(reservedDate1.toString()));
                }
            } else {
                taskData.setReservedDate1(workBean.getReservedDate1());
            }

            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.RESERVED_DATE_2.getValue())) {
                Object reservedDate2 = dyFormData
                        .getFieldValueByMappingName(WorkFlowFieldMapping.RESERVED_DATE_2.getValue());
                if (reservedDate2 instanceof Date) {
                    taskData.setReservedDate2((Date) reservedDate2);
                } else {
                    taskData.setReservedDate2(reservedDate2 == null ? null : DateUtils.parse(reservedDate2.toString()));
                }
            } else {
                taskData.setReservedDate2(workBean.getReservedDate2());
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));

            throw new WorkFlowException("提交失败，日期类型数据解析错误!");
        }
    }

    /**
     * @param workBean
     * @return
     */
    @Override
    @Transactional
    public ResultMessage complete(WorkBean workBean) {
        workBean.setRequiredSubmitPermission(false);
        return this.submit(workBean);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.workflow.work.service.WorkService#getWorkData(com.wellsoft
     * .pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional(readOnly = true)
    public WorkBean getWorkData(WorkBean workBean) {
        String taskInstUuid = workBean.getTaskInstUuid();
        String flowInstUuid = workBean.getFlowInstUuid();
        TaskInstance taskInstance = null;
        FlowInstance flowInstance = null;
        FlowDefinition flowDefinition = null;
        if (StringUtils.isNotBlank(taskInstUuid)) {
            taskInstance = taskService.getTask(taskInstUuid);
            flowInstance = taskInstance.getFlowInstance();
            flowDefinition = taskInstance.getFlowDefinition();
            workBean.setDevelopJson(flowDefinition.getDevelopJson());
        } else if (StringUtils.isNotBlank(flowInstUuid)) {
            flowInstance = flowService.getFlowInstance(flowInstUuid);
            taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
            if (taskInstance != null) {
                workBean.setTaskInstUuid(taskInstance.getUuid());
                workBean.setTaskInstRecVer(taskInstance.getRecVer());
            }
            flowDefinition = flowInstance.getFlowDefinition();
            workBean.setDevelopJson(flowDefinition.getDevelopJson());
        } else {
            flowDefinition = flowService.getFlowDefinition(workBean.getFlowDefUuid());
            workBean.setDevelopJson(flowDefinition.getDevelopJson());
        }
        workBean.setFlowDefUuid(flowDefinition.getUuid());
        workBean.setVersion(flowDefinition.getVersion().toString());
        workBean.setCustomJsModule(extractCustomJsModule(flowDefinition.getDevelopJson()));

        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        TaskElement taskElement = null;
        if (null != taskInstance) {
            taskElement = flowDelegate.getFlow().getTask(taskInstance.getId());
            if (taskElement != null) {
                workBean.setCustomJsFragmentModule(taskElement.getCustomJsModule());
            }
            workBean.setIsAllowApp(flowDelegate.getIsAllowApp(taskInstance.getId()));
        } else {
            taskElement = flowDelegate.getFlow().getTask(flowDelegate.getFistTaskData().getFirstTaskId());
            if (taskElement != null) {
                workBean.setCustomJsFragmentModule(taskElement.getCustomJsModule());
            }
            workBean.setIsAllowApp(flowDelegate.getIsAllowApp(workBean.getTaskId()));
        }
        // 是否有设置表单可编辑选项
        if (taskElement != null && StringUtils.isNotBlank(taskElement.getCanEditForm())
                && (SuspensionState.NORMAL.getState() == workBean.getSuspensionState()
                || SuspensionState.LOGIC_SUSPEND.getState() == workBean.getSuspensionState())) {
            workBean.setCanEditForm("1".equals(taskElement.getCanEditForm()));
        } else {
            workBean.setCanEditForm(false);
        }
        WorkFlowSettings flowSettings = flowSettingService.getWorkFlowSettings();
        // 加载待办提交类型
        if ((WorkFlowAclRole.TODO.equals(workBean.getAclRole()) || !flowSettings.isAclRoleIsolation())
                && StringUtils.isNotBlank(taskInstUuid)) {
            addTaskIdentityInfo(taskInstUuid, workBean);
        }
        // 多个提交办理方式
        if (taskInstance != null) {
            String multiSubmitType = "isMultiSubmit";
            if (WorkFlowTodoType.Supplement.equals(workBean.getTodoType())) {
                // 补审补办需多人办理
            } else if (flowDelegate.isAnyone(taskInstance.getId())) {
                multiSubmitType = "isAnyone";
            } else if (flowDelegate.isByOrder(taskInstance.getId())) {
                multiSubmitType = "isByOrder";
            }
            workBean.setMultiSubmitType(multiSubmitType);
        }

        // 提取表单数据
        if (workBean.isLoadDyFormData()) {
            extractDyformData(workBean, taskInstance, flowInstance, flowDelegate);
        } else {
            // 获取任务表单配置信息
            TaskForm taskForm = getTaskForm(workBean);
            workBean.setTaskForm(taskForm);
            // 获取信息格式的内容
            workBean.setRecords(taskForm.getRecords());
        }

        // 设置办理过程，通过接口getTaskProcessByTaskInstUuidAndFlowDefId获取
        // if (!workBean.isDaemon()) {
        // workBean.setWorkProcess(getWorkProcess(taskInstUuid,
        // workBean.getFlowInstUuid(), workBean.getFlowDefUuid(),
        // workBean.getFormUuid()));
        // }

        // 设置共享数据
        if (StringUtils.isNotBlank(taskInstUuid) && (flowInstance != null)) {
            // 分支流数据
            workBean.setBranchTaskData(taskBranchService.getBranchTaskData(taskInstance, flowInstance));
            // 子流程数据
            workBean.setSubTaskData(getSubTaskData(taskInstance, flowInstance));
        }

        // 获取是否自动更新标题字段值
        if (flowDefinition.getAutoUpdateTitle() == null) {
            workBean.setAutoUpdateTitle(Boolean.FALSE);
        } else {
            workBean.setAutoUpdateTitle(flowDefinition.getAutoUpdateTitle());
        }
        return workBean;
    }

    /**
     * @param taskInstUuid
     * @param workBean
     */
    private void addTaskIdentityInfo(String taskInstUuid, WorkBean workBean) {
        String taskIdentityUuid = workBean.getTaskIdentityUuid();
        TaskIdentity taskIdentity = null;
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            taskIdentity = identityService.get(taskIdentityUuid);
            workBean.setTodoType(taskIdentity.getTodoType());
        } else {
            UserDetails user = SpringSecurityUtils.getCurrentUser();
            List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuidAndUserDetails(taskInstUuid,
                    user);
            // 优先取用户自身粒度的待办标识
            taskIdentity = taskIdentities.stream().filter(identity -> StringUtils.equals(identity.getUserId(), user.getUserId())).findFirst().orElse(null);
            if (taskIdentity != null) {
                workBean.setTodoType(taskIdentity.getTodoType());
                workBean.setTaskIdentityUuid(taskIdentity.getUuid());
            } else if (CollectionUtils.isNotEmpty(taskIdentities) && taskService.hasTodoPermission(user, taskInstUuid)) {
                taskIdentity = taskIdentities.get(0);
                workBean.setTodoType(taskIdentity.getTodoType());
                workBean.setTaskIdentityUuid(taskIdentity.getUuid());
            }
        }

        if (taskIdentity != null && taskIdentity.getTodoType() != null) {
            Integer todoType = taskIdentity.getTodoType();
            if (WorkFlowTodoType.Transfer.equals(todoType) || WorkFlowTodoType.CounterSign.equals(todoType)
                    || WorkFlowTodoType.AddSign.equals(todoType) || WorkFlowTodoType.Supplement.equals(todoType)) {
                // 表单权限
                ViewFormMode viewFormMode = taskIdentity.getViewFormMode();
                if (viewFormMode != null) {
                    // 只读权限
                    if (ViewFormMode.READONLY.equals(viewFormMode)) {
                        workBean.setCanEditForm(false);
                    } else if (ViewFormMode.DEFAULT.equals(viewFormMode)) {
                        // 同操作人权限
                        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
                        if (StringUtils.isNotBlank(sourceTaskIdentityUuid)) {
                            TaskIdentity sourceTaskIdentity = identityService.get(sourceTaskIdentityUuid);
                            if (sourceTaskIdentity != null && ViewFormMode.READONLY.equals(sourceTaskIdentity.getViewFormMode())) {
                                workBean.setCanEditForm(false);
                            }
                        }
                    }
                }

                // 操作权限
                TodoTypeOperate todoTypeOperate = taskIdentity.getTodoTypeOperate();
                boolean noneOperate = false;
                if (todoTypeOperate != null) {
                    // 不可操作
                    if (TodoTypeOperate.NONE.equals(todoTypeOperate)) {
                        noneOperate = true;
                    } else if (TodoTypeOperate.DEFAULT.equals(todoTypeOperate)) {
                        // 同操作人权限
                        String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
                        if (StringUtils.isNotBlank(sourceTaskIdentityUuid)) {
                            TaskIdentity sourceTaskIdentity = identityService.get(sourceTaskIdentityUuid);
                            if (sourceTaskIdentity != null && TodoTypeOperate.NONE.equals(sourceTaskIdentity.getViewFormMode())) {
                                noneOperate = true;
                            }
                        }
                    }
                }
                // 无权限操作
                if (noneOperate) {
                    workBean.setButtons(workBean.getButtons().stream().filter(button -> {
                        if (WorkFlowTodoType.Transfer.equals(todoType)) {
                            if (WorkFlowPrivilege.Transfer.getCode().equals(button.getCode())) {
                                return false;
                            }
                        } else if (WorkFlowTodoType.CounterSign.equals(todoType)) {
                            if (WorkFlowPrivilege.CounterSign.getCode().equals(button.getCode())) {
                                return false;
                            }
                        } else {
                            if (WorkFlowPrivilege.AddSign.getCode().equals(button.getCode())) {
                                return false;
                            }
                        }
                        return true;
                    }).collect(Collectors.toList()));
                } else if (TodoTypeOperate.SUBMIT.equals(todoTypeOperate)) {
                    // 只有提交权限
                    workBean.setButtons(workBean.getButtons().stream().filter(button -> {
                        String btnCode = button.getCode();
                        if (WorkFlowPrivilege.Submit.getCode().equals(btnCode) || WorkFlowPrivilege.SignOpinion.getCode().equals(btnCode)
                                || WorkFlowPrivilege.ViewProcess.getCode().equals(btnCode) || WorkFlowPrivilege.Print.getCode().equals(btnCode)
                                || WorkFlowPrivilege.PrintForm.getCode().equals(btnCode) || WorkFlowPrivilege.Attention.getCode().equals(btnCode)
                                || WorkFlowPrivilege.Unfollow.getCode().equals(btnCode) || WorkFlowPrivilege.ViewTheMainFlow.getCode().equals(btnCode)
                                || WorkFlowPrivilege.StartGroupChat.getCode().equals(btnCode)) {
                            return true;
                        }
                        return false;
                    }).collect(Collectors.toList()));
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param workBean
     * @param taskInstance
     * @param flowInstance
     */
    private void extractDyformData(WorkBean workBean, TaskInstance taskInstance, FlowInstance flowInstance,
                                   FlowDelegate flowDelegate) {
        String formUuid = workBean.getFormUuid();
        String dataUuid = workBean.getDataUuid();
        String vformUuid = workBean.getDefaultVFormUuid();
        String mformUuid = workBean.getDefaultMFormUuid();
        // 获取任务表单配置信息
        TaskForm taskForm = getTaskForm(workBean);
        if (StringUtils.isBlank(vformUuid)) {
            vformUuid = taskForm.getVformUuid();
        }
        Map<String, String> wrapperKeyMap = Maps.newHashMap();
        // 获取任务动态表单定义与数据信息
        DyFormData dyFormData = null;
        if (isMobileApp() && StringUtils.isNotBlank(mformUuid)) {
            dyFormData = dyFormFacade.getDyFormData(mformUuid, dataUuid);
            wrapperKeyMap.put(formUuid, mformUuid);
        } else if (StringUtils.isNotBlank(vformUuid)) {
            dyFormData = dyFormFacade.getDyFormData(vformUuid, dataUuid);
            wrapperKeyMap.put(formUuid, vformUuid);
        } else {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        String configFormUuid = taskForm.getFormUuid();
        // 运行的表单与配置的表单不一致时，直接返回运行的表单数据
        if (!StringUtils.equals(formUuid, configFormUuid)) {
            String formId = dyFormFacade.getTblNameByFormUuid(formUuid);
            String configFormId = dyFormFacade.getTblNameByFormUuid(configFormUuid);
            if (!StringUtils.equals(formId, configFormId)) {
                workBean.setDyFormData(dyFormData);
                return;
            }
        }

        // 设置字段控制信息，7.0前端通过taskForm处理
//        if (workBean.getCanEditForm() != null) {
//            setTaskDyformDataControlInfo(dyFormData, taskForm, workBean, wrapperKeyMap);
//        }
        workBean.setTaskForm(taskForm);

        // 获取信息格式的内容
        workBean.setRecords(taskForm.getRecords());

        if (flowInstance != null) {
            // 设置预留字段
            workBean.setReservedText1(flowInstance.getReservedText1());
            workBean.setReservedText2(flowInstance.getReservedText2());
            workBean.setReservedText3(flowInstance.getReservedText3());
            workBean.setReservedText4(flowInstance.getReservedText4());
            workBean.setReservedText5(flowInstance.getReservedText5());
            workBean.setReservedText6(flowInstance.getReservedText6());
            workBean.setReservedText7(flowInstance.getReservedText7());
            workBean.setReservedText8(flowInstance.getReservedText8());
            workBean.setReservedText9(flowInstance.getReservedText9());
            workBean.setReservedText10(flowInstance.getReservedText10());
            workBean.setReservedText11(flowInstance.getReservedText11());
            workBean.setReservedText12(flowInstance.getReservedText12());
            workBean.setReservedNumber1(flowInstance.getReservedNumber1());
            workBean.setReservedNumber2(flowInstance.getReservedNumber2());
            workBean.setReservedNumber3(flowInstance.getReservedNumber3());
            workBean.setReservedDate1(flowInstance.getReservedDate1());
            workBean.setReservedDate2(flowInstance.getReservedDate2());
        }

        // 流程草稿
        if (flowInstance == null || taskInstance == null) {
            // 当前流程状态名称
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_NAME.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_NAME.getValue(),
                        flowDelegate.getFlowStateName(WorkFlowState.Draft));
            }
            // 当前流程状态代码
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_CODE.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_FLOW_STATE_CODE.getValue(),
                        WorkFlowState.Draft);
            }
        }

        if (flowInstance != null) {
            // 标题
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.TITLE.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.TITLE.getValue(), flowInstance.getTitle());
            }
            // 流程实例UUID
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.FLOW_INST_UUID.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.FLOW_INST_UUID.getValue(),
                        flowInstance.getUuid());
            }
        }

        if (taskInstance != null) {
            // 设置动态表单映射值
            // 环节实例UUID
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.TASK_INST_UUID.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.TASK_INST_UUID.getValue(),
                        taskInstance.getUuid());
            }
            // 当前环节名称
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_TASK.getValue())) {
                if (flowInstance.getEndTime() == null) {
                    dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_TASK.getValue(),
                            taskInstance.getName());
                } else {
                    dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_TASK.getValue(),
                            flowDelegate.getFlowStateName(WorkFlowState.Over));
                }
            }
            // 当前环节ID
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_TASK_ID.getValue())) {
                if (flowInstance.getEndTime() == null) {
                    dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_TASK_ID.getValue(),
                            taskInstance.getId());
                } else {
                    dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_TASK_ID.getValue(),
                            FlowConstant.END_FLOW_ID);
                }
            }
            // 当前环节办理人名称
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_NAME.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_NAME.getValue(),
                        taskInstance.getTodoUserName());
            }
            // 当前环节办理人ID
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_ID.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CURRENT_TASK_TODO_USER_ID.getValue(),
                        taskInstance.getTodoUserId());
            }
            // 下一环节
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.NEXT_TASK.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.NEXT_TASK.getValue(),
                        taskInstance.getName());
            }
            // 办理意见
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.OPINION.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.OPINION.getValue(),
                        this.getOpinions(workBean));
            }
            // 流水号
            if (StringUtils.isNotBlank(taskInstance.getSerialNo())
                    && dyFormData.hasFieldMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue(),
                        taskInstance.getSerialNo());
            }
            // 到期时间
            if (flowInstance != null && flowInstance.getDueTime() != null
                    && dyFormData.hasFieldMappingName(WorkFlowFieldMapping.DUE_TIME.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.DUE_TIME.getValue(),
                        flowInstance.getDueTime());
            }
        } else {
            // 临时流水号
            /* add by huanglinchuan 2014.12.26 begin */
            String serial_no = (String) dyFormData
                    .getFieldValueByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue());
            if (StringUtils.isNotBlank(workBean.getSerialNoDefId()) && StringUtils.isBlank(serial_no)) {
                /* add by huanglinchuan 2014.12.26 end */
                try {
                    String serialNoDefId = workBean.getSerialNoDefId();
                    if (basicDataApiFacade.getSerialNumberById(serialNoDefId) != null) {
                        List<IdEntity> entities = new ArrayList<IdEntity>();
                        TaskInstance tmpTaskInstance = new TaskInstance();
                        entities.add(tmpTaskInstance);
                        FlowInstance tmpFlowInstance = new FlowInstance();
                        entities.add(tmpFlowInstance);
                        // add by huanglinchuan 2014.10.17 begin
                        entities.add(flowService.getFlowDefinition(workBean.getFlowDefUuid()));
                        // add by huanglinchuan 2014.10.17 end

                        dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue(),
                                basicDataApiFacade.getSerialNumber(workBean.getSerialNoDefId(), entities, false,
                                        WorkFlowVariables.SERIAL_NO.getName()));
                    } else {
                        List<String> snFieldNames = dyFormData.getFieldNamesByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue());
                        SerialNumberBuildParams params = new SerialNumberBuildParams();
                        params.setSerialNumberId(serialNoDefId);
                        params.setIsBackEnd(true);
                        params.setOccupied(false);
                        if (CollectionUtils.isNotEmpty(snFieldNames)) {
                            params.setFormField(snFieldNames.get(0));
                        }
                        Map<String, Object> renderParams = new HashMap<String, Object>();
                        renderParams.put("流程名称", flowDelegate.getFlow().getName());
                        renderParams.put("流程ID", flowDelegate.getFlow().getId());
                        renderParams.put("流程编号", flowDelegate.getFlow().getCode());
                        Map<String, Object> flowDefinitionMap = new HashMap<String, Object>();
                        flowDefinitionMap.put("name", flowDelegate.getFlow().getName());
                        flowDefinitionMap.put("id", flowDelegate.getFlow().getId());
                        flowDefinitionMap.put("code", flowDelegate.getFlow().getCode());
                        renderParams.put("flowDefinition", flowDefinitionMap);
                        params.setRenderParams(renderParams);
                        params.setFormUuid(dyFormData.getFormUuid());
                        params.setDataUuid(dyFormData.getDataUuid());
                        renderParams.put("dyFormData", dyFormData);
                        String sn = basicDataApiFacade.generateSerialNumber(params);
                        dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue(), sn);
                    }
                } catch (Exception e) {
                    /* modified by huanglinchuan 2014.12.26 begin */
                    logger.error("临时流水号生成失败", e);
                    /* modified by huanglinchuan 2014.12.26 end */
                }
            }
            // 设置动态表单映射值
            // 创建人
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CREATOR.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CREATOR.getValue(),
                        SpringSecurityUtils.getCurrentUserName());
            }
            // 创建时间
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CREATE_TIME.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CREATE_TIME.getValue(),
                        Calendar.getInstance().getTime());
            }
            // 创建人部门
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CREATOR_DEPARTMENT.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CREATOR_DEPARTMENT.getValue(),
                        SpringSecurityUtils.getCurrentUserDepartmentName());
            }
            // 创建人岗位
            if (dyFormData.hasFieldMappingName(WorkFlowFieldMapping.CREATOR_POST.getValue())) {
                dyFormData.setFieldValueByMappingName(WorkFlowFieldMapping.CREATOR_POST.getValue(),
                        SpringSecurityUtils.getCurrentUserJobName());
            }
        }
        workBean.setDyFormData(dyFormData);
    }

    /**
     * @param dyFormData
     * @param taskForm
     */
    protected void setTaskDyformDataControlInfo(DyFormData dyFormData, TaskForm taskForm, WorkBean workBean,
                                                Map<String, String> wrapperKeyMap) {
        String formUuid = dyFormData.getFormUuid();
        // 工作流中，如果设置了编辑域，则其他域为只读域，如果没有设置编辑域，有设置只读域，则除只读域外其他域为表单默认配置
        // Map<formUuid, List<fileName>
        List<String> allFormFieldList = taskForm.getAllFormFields();
        Set<String> subformUuids = taskForm.getAllSubformUuids();
        Map<String, List<String>> editFieldMap = taskForm.getEditFieldMap();
        Map<String, List<String>> onlyReadFieldMap = taskForm.getOnlyReadFieldMap();
        DyFormFormDefinition formDefinition = dyFormFacade.getFormDefinition(formUuid);
        Map<String, DyformSubformFormDefinition> subformDefinionMap = getSubformDefinionMap(formDefinition);
        Map<String, DyFormFormDefinition> templateDefinionMap = getTemplateDefinionMap(formDefinition);

        // 设置隐藏域
        Map<String, List<String>> hideFieldMap = taskForm.getHideFieldMap();
        // 隐藏的字段
        Map<String, List<String>> hiddenFieldMap = Maps.newHashMap();
        // 隐藏的真实值占位符
        List<String> hiddenPlaceholderCtrs = Lists.newArrayListWithExpectedSize(0);
        // 隐藏的文件库维护组件
        List<String> hiddenFileLibrarys = Lists.newArrayListWithExpectedSize(0);
        // 隐藏的视图列表组件
        List<String> hiddenTableViews = Lists.newArrayListWithExpectedSize(0);
        // 隐藏的从表
        List<String> hiddenSubformIds = Lists.newArrayListWithExpectedSize(0);
        for (String key : hideFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            List<String> hideFields = hideFieldMap.get(key);
            if (templateDefinionMap.containsKey(key)) {
                // 子表单
                dyFormData.setTemplateHiddenFields(key, hideFields);
            } else {
                // 真实值占位符
                if (TaskForm.PLACEHOLDER_CTR_PREFIX.startsWith(key)) {
                    for (String hideField : hideFields) {
                        dyFormData.hidePlaceholderCtr(hideField);
                        hiddenPlaceholderCtrs.add(hideField);
                    }
                    continue;
                } else if (TaskForm.FILE_LIBRARY_PREFIX.startsWith(key)) {
                    // 文件库维护组件
                    for (String hideField : hideFields) {
                        dyFormData.hideFileLibrary(hideField);
                        hiddenFileLibrarys.add(hideField);
                    }
                    continue;
                } else if (TaskForm.TABLE_VIEW_PREFIX.startsWith(key)) {
                    // 视图列表组件
                    for (String hideField : hideFields) {
                        dyFormData.hideTableView(hideField);
                        hiddenTableViews.add(hideField);
                    }
                    continue;
                }
                // 隐藏操作按钮
                List<String> buttonFields = Lists.newArrayListWithCapacity(0);
                for (String hideField : hideFields) {
                    if (TaskForm.isAllBtnField(key, hideField)) {
                        buttonFields.add(hideField);
                        dyFormData.hideSubformOperateBtn(wrapperKey);
                    } else if (TaskForm.isAddBtnField(key, hideField)) {
                        buttonFields.add(hideField);
                        dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showDel);
                    } else if (TaskForm.isDelBtnField(key, hideField)) {
                        buttonFields.add(hideField);
                        dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showAdd);
                    } else if (TaskForm.isImpBtnField(key, hideField)) {
                        buttonFields.add(hideField);
                        dyFormData.hideImp(wrapperKey);
                    } else if (TaskForm.isExpBtnField(key, hideField)) {
                        buttonFields.add(hideField);
                        dyFormData.hideExp(wrapperKey);
                    }
                }
                hideFields.removeAll(buttonFields);
                // 隐藏主表字段
                List<String> fields = getExistsFields(dyFormData, hideFields, wrapperKey);
                List<String> showFields = getSubList(taskForm.isMainForm(key) ? taskForm.cloneAllMainformFields()
                        : taskForm.cloneAllSubformFields(wrapperKey), fields);
                dyFormData.setHiddenFields(wrapperKey, fields, getExistsFields(dyFormData, showFields, wrapperKey));
                // 隐藏从表
                List<String> subformIds = getExistsSubforms(dyFormData, hideFields, wrapperKey, subformDefinionMap);
                hiddenSubformIds.addAll(subformIds);
                dyFormData.setHiddenSubforms(wrapperKey, subformIds);

                // 隐藏的字段
                hiddenFieldMap.put(key, fields);
            }
        }
        // 显示的真实值占位符
        List<String> showPlaceholderCtrs = getShownPlaceholderCtrs(hiddenPlaceholderCtrs, formDefinition);
        for (String showPlaceholderCtr : showPlaceholderCtrs) {
            dyFormData.showPlaceholderCtr(showPlaceholderCtr);
        }
        // 显示的文件库维护组件
        List<String> showFileLibrarys = getShownFileLibrarys(hiddenFileLibrarys, formDefinition);
        for (String showFileLibrary : showFileLibrarys) {
            dyFormData.showFileLibrary(showFileLibrary);
        }
        // 显示的视图列表组件
        List<String> showTableViews = getShownTableViews(hiddenTableViews, formDefinition);
        for (String showTableView : showTableViews) {
            dyFormData.showTableView(showTableView);
        }
        // 显示的从表
        List<String> showSubformIds = getShownSubforms(hiddenSubformIds, subformDefinionMap, taskForm);
        dyFormData.setEditableSubforms(formUuid, showSubformIds);
        // dyFormData.setHiddenFields(formUuid, taskForm.getHideFields());

        fillEditAndOnlyReadFieldMapForNewField(formUuid, editFieldMap, onlyReadFieldMap, formDefinition,
                subformDefinionMap, templateDefinionMap, allFormFieldList);
        // 填充过滤编辑域与只读域
        if (!editFieldMap.isEmpty()) {
            onlyReadFieldMap = new HashMap<String, List<String>>();
            fillEditAndOnlyReadFieldMap(formUuid, editFieldMap, onlyReadFieldMap, formDefinition, subformDefinionMap,
                    templateDefinionMap);
        } else if (!onlyReadFieldMap.isEmpty()) {
            // editFieldMap = new HashMap<String, List<String>>();
            // fillEditAndOnlyReadFieldMap(formUuid, onlyReadFieldMap,
            // editFieldMap, formDefinition, subformDefinionMap);
        } else {
            // 新版流程定义没有只读域配置
            onlyReadFieldMap = new HashMap<String, List<String>>();
            fillEditAndOnlyReadFieldMap(formUuid, editFieldMap, onlyReadFieldMap, formDefinition, subformDefinionMap,
                    templateDefinionMap);
        }
        // fix bug 52007
        // if (BooleanUtils.isTrue(workBean.getCanEditForm())) {// 新版流程定义的“可编辑表单”开启的情况下
        // if (MapUtils.isEmpty(editFieldMap)) {
        // workBean.setCanEditForm(false);
        // }
        // }
        if (workBean.getCanEditForm() != null && !workBean.getCanEditForm()) {// 新版流程定义的关闭“可编辑表单”的情况下
            for (String key : editFieldMap.keySet()) {
                if (!taskForm.isMainForm(key)) {
                    dyFormData.setEditableSubformOperateBtns(key, Collections.EMPTY_SET);
                }
            }
            clearEditField(editFieldMap, taskForm, subformDefinionMap);
        }

        // 设置编辑域
        for (String key : editFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            // 设置主表
            if (taskForm.isMainForm(key)) {
                List<String> editFields = editFieldMap.get(key);
                dyFormData.setEditableFields(wrapperKey, getExistsFields(dyFormData, editFields, wrapperKey));
            } else if (subformUuids.contains(key)) {// 新增的表单流程配置不存在，以表单配置为准
                // 设置从表
                List<String> editFields = editFieldMap.get(key);
                // 显示操作按钮
                List<String> buttonFields = new ArrayList<String>(0);
                if (workBean.getCanEditForm() != null) {// 新版流程定义的从表控制按钮
                    Set<String> operationBtnCodes = Sets.newHashSet();
                    for (String ef : editFields) {
                        if (ef.endsWith(key)) {
                            operationBtnCodes.add(ef.replaceFirst("_" + key, ""));
                        }
                    }

                    dyFormData.setEditableSubformOperateBtns(key, operationBtnCodes);
                } else {
                    // 从表是否只设置操作按钮
                    boolean isAllBtnField = hasAllBbnField(key, editFields);
                    boolean isAddBtnField = isAddBbnField(key, editFields);
                    boolean isDelBtnField = isDelBbnField(key, editFields);
                    // 设置编辑域
                    if (isAllBtnField) {
                        String btnField = TaskForm.getAllBtnField(formUuid);
                        buttonFields.add(btnField);
                        dyFormData.showSubformOperateBtn(wrapperKey);
                    } else {
                        dyFormData.hideSubformOperateBtn(wrapperKey);
                    }
                    if (isAddBtnField && isDelBtnField == false) {
                        String btnField = TaskForm.getAddBtnField(formUuid);
                        buttonFields.add(btnField);
                        dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showAdd);
                    } else if (isDelBtnField && isAddBtnField == false) {
                        String btnField = TaskForm.getDelBtnField(formUuid);
                        buttonFields.add(btnField);
                        dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showDel);
                    }
                    if (isImpBbnField(key, editFields)) {
                        String btnField = TaskForm.getImpBtnField(formUuid);
                        buttonFields.add(btnField);
                        dyFormData.showImp(wrapperKey);
                    } else {
                        dyFormData.hideImp(wrapperKey);
                    }
                    if (isExpBbnField(key, editFields)) {
                        String btnField = TaskForm.getExpBtnField(formUuid);
                        buttonFields.add(btnField);
                        dyFormData.showExp(wrapperKey);
                    } else {
                        dyFormData.hideExp(wrapperKey);
                    }
                }

                // 新版流程定义的可编辑从表按钮控制
                editFields.removeAll(buttonFields);
                dyFormData.setEditableFields(wrapperKey, getExistsFields(dyFormData, editFields, wrapperKey));
            } else if (templateDefinionMap.containsKey(key)) {
                // 子表单
                List<String> editFields = editFieldMap.get(key);
                dyFormData.setTemplateEditableFields(key, editFields);
            }
        }

        // 设置只读域
        for (String key : onlyReadFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            // 设置主表
            if (taskForm.isMainForm(key)) {
                List<String> onlyReadFields = onlyReadFieldMap.get(key);
                // 除去隐藏的字段，隐藏的字段不可再设置为只读
                if (hiddenFieldMap.containsKey(key)) {
                    onlyReadFields.removeAll(hiddenFieldMap.get(key));
                }
                dyFormData.setLabelFields(wrapperKey, getExistsFields(dyFormData, onlyReadFields, wrapperKey));
            } else if (subformUuids.contains(key)) {// 新增的表单流程配置不存在，以表单配置为准
                // 设置从表
                List<String> onlyReadFields = onlyReadFieldMap.get(key);
                // 从表是否只设置操作按钮
                boolean isAllBtnField = hasAllBbnField(key, onlyReadFields);
                boolean isAddBtnField = isAddBbnField(key, onlyReadFields);
                boolean isDelBtnField = isDelBbnField(key, onlyReadFields);
                // 设置只读域
                // 显示操作按钮
                List<String> buttonFields = new ArrayList<String>(0);
                if (isAllBtnField) {
                    String btnField = TaskForm.getAllBtnField(key);
                    buttonFields.add(btnField);
                    dyFormData.hideSubformOperateBtn(wrapperKey);
                } else {
                    dyFormData.showSubformOperateBtn(wrapperKey);
                }
                if (isAddBtnField && isDelBtnField == false) {
                    String btnField = TaskForm.getAddBtnField(formUuid);
                    buttonFields.add(btnField);
                    dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showDel);
                } else if (isDelBtnField && isAddBtnField == false) {
                    String btnField = TaskForm.getDelBtnField(formUuid);
                    buttonFields.add(btnField);
                    dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showAdd);
                }
                if (isImpBbnField(key, onlyReadFields)) {
                    String btnField = TaskForm.getImpBtnField(formUuid);
                    buttonFields.add(btnField);
                    dyFormData.hideImp(wrapperKey);
                } else {
                    dyFormData.showImp(wrapperKey);
                }
                if (isExpBbnField(key, onlyReadFields)) {
                    String btnField = TaskForm.getExpBtnField(formUuid);
                    buttonFields.add(btnField);
                    dyFormData.hideExp(wrapperKey);
                } else {
                    dyFormData.showExp(wrapperKey);
                }
                onlyReadFields.removeAll(buttonFields);
                // 除去隐藏的字段，隐藏的字段不可再设置为只读
                if (hiddenFieldMap.containsKey(key)) {
                    onlyReadFields.removeAll(hiddenFieldMap.get(key));
                }
                dyFormData.setLabelFields(wrapperKey, getExistsFields(dyFormData, onlyReadFields, wrapperKey));
            } else if (templateDefinionMap.containsKey(key)) {
                // 子表单
                List<String> onlyReadFields = onlyReadFieldMap.get(key);
                dyFormData.setTemplateLabelFields(key, onlyReadFields);
            }
        }

        // 设置必填域
        Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
        for (String key : notNullFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            List<String> requiredFields = notNullFieldMap.get(key);
            // 设置主表
            if (taskForm.isMainForm(key)) {
                dyFormData.setRequiredFields(wrapperKey, getExistsFields(dyFormData, requiredFields, wrapperKey),
                        getExistsFields(dyFormData, getSubList(taskForm.cloneAllMainformFields(), requiredFields),
                                wrapperKey));
            } else if (subformUuids.contains(key)) {// 新增的表单流程配置不存在，以表单配置为准
                // 显示操作按钮
                List<String> buttonFields = new ArrayList<String>(0);
                for (String requiredField : requiredFields) {
                    if (TaskForm.isAllBtnField(key, requiredField)) {
                        buttonFields.add(requiredField);
                        dyFormData.showSubformOperateBtn(wrapperKey);
                    } else if (TaskForm.isAddBtnField(key, requiredField)) {
                        buttonFields.add(requiredField);
                        dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showAdd);
                    } else if (TaskForm.isDelBtnField(key, requiredField)) {
                        buttonFields.add(requiredField);
                        dyFormData.showSubformOperateBtn(wrapperKey, EnumHideSubFormOperateBtn.showDel);
                    } else if (TaskForm.isImpBtnField(key, requiredField)) {
                        buttonFields.add(requiredField);
                        dyFormData.showImp(wrapperKey);
                    } else if (TaskForm.isExpBtnField(key, requiredField)) {
                        buttonFields.add(requiredField);
                        dyFormData.showExp(wrapperKey);
                    }
                }
                requiredFields.removeAll(buttonFields);
                dyFormData.setRequiredFields(wrapperKey, getExistsFields(dyFormData, requiredFields, wrapperKey),
                        getExistsFields(dyFormData,
                                getSubList(taskForm.cloneAllSubformFields(wrapperKey), requiredFields), wrapperKey));
            } else if (templateDefinionMap.containsKey(key)) {
                // 子表单
                dyFormData.setTemplateRequiredFields(key, requiredFields);
            }
        }
        // dyFormData.setRequiredFields(formUuid, taskForm.getNotNullFields());

        // add by wujx 20160728 begin
        // 设置可上传域
        Map<String, List<String>> allowUploadFieldMap = taskForm.getAllowUploadFieldMap();
        for (String key : allowUploadFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            List<String> allowUploadFields = allowUploadFieldMap.get(key);
            dyFormData.setAllowUploadFields(wrapperKey, getExistsFields(dyFormData, allowUploadFields, wrapperKey),
                    true);
        }

        // 设置可下载域
        Map<String, List<String>> allowDownloadFieldMap = taskForm.getAllowDownloadFieldMap();
        for (String key : allowDownloadFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            List<String> allowDownloadFields = allowDownloadFieldMap.get(key);
            dyFormData.setAllowDownloadFields(wrapperKey, getExistsFields(dyFormData, allowDownloadFields, wrapperKey),
                    true);
        }

        // 设置可删除域
        Map<String, List<String>> allowDeleteFieldMap = taskForm.getAllowDeleteFieldMap();
        for (String key : allowDeleteFieldMap.keySet()) {
            String wrapperKey = getWrapperKey(key, dyFormData, wrapperKeyMap);
            List<String> allowDeleteFields = allowDeleteFieldMap.get(key);
            dyFormData.setAllowDeleteFields(wrapperKey, getExistsFields(dyFormData, allowDeleteFields, wrapperKey),
                    true);
        }
        // add by wujx 20160728 end

        // 设置隐藏区块
        List<String> hideBlocks = taskForm.getHideBlocks();
        if (hideBlocks != null && !hideBlocks.isEmpty()) {
            for (String blockCode : hideBlocks) {
                dyFormData.hideBlock(blockCode);
            }
        }
        // 设置隐藏页签
        List<String> hideTabs = taskForm.getHideTabs();
        if (hideTabs != null && !hideTabs.isEmpty()) {
            for (String tabName : hideTabs) {
                dyFormData.hideTab(tabName);
            }
        }

        // 设置附件按钮权限 (注释隐藏，6.1.1.5 流程附件按钮权限先不处理)
        Map<String, List<String>> fileRightsMap = new HashMap<>();// 主表 {fieldName:secDevBtnIdList}
        Map<String, Map<String, List<String>>> subFormFileRightsMap = new HashMap<>();// 从表{subFormUuid:fieldName:secDevBtnIdList}
        List<String> fileRights = taskForm.getFileRights();
        if (CollectionUtils.isNotEmpty(fileRights)) {
            for (String fileRight : fileRights) {
                String[] fileRightSplit = fileRight.split(":");
                if (fileRightSplit.length == 2) {
                    // 主表附件
                    List<String> list = fileRightsMap.get(fileRightSplit[0]);
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(fileRightSplit[1]);
                    fileRightsMap.put(fileRightSplit[0], list);
                } else if (fileRightSplit.length > 2) {
                    // 从表附件
                    Map<String, List<String>> subFileRightsMap = subFormFileRightsMap.get(fileRightSplit[0]);
                    if (subFileRightsMap == null) {
                        subFileRightsMap = new HashMap<>();
                    }
                    List<String> list = subFileRightsMap.get(fileRightSplit[1]);
                    if (CollectionUtils.isEmpty(list)) {
                        list = new ArrayList<>();
                    }
                    list.add(fileRightSplit[2]);
                    subFileRightsMap.put(fileRightSplit[1], list);
                    subFormFileRightsMap.put(fileRightSplit[0], subFileRightsMap);
                }
            }
        }
        for (String fieldName : fileRightsMap.keySet()) {
            dyFormData.setFileFieldSecDevBtnId(formUuid, fieldName,
                    StringUtils.join(fileRightsMap.get(fieldName), ";"));
        }
        // 无权限的文件
        for (String fieldName : taskForm.getAllMainformFields()) {
            if (!fileRightsMap.containsKey(fieldName) && dyFormData.isFileField(fieldName)) {
                dyFormData.setFileFieldSecDevBtnId(formUuid, fieldName, StringUtils.EMPTY);
            }
        }
        for (String subFormUuid : subFormFileRightsMap.keySet()) {
            Map<String, List<String>> listMap = subFormFileRightsMap.get(subFormUuid);
            for (String fieldName : listMap.keySet()) {
                dyFormData.setFileFieldSecDevBtnId(subFormUuid, fieldName,
                        StringUtils.join(listMap.get(fieldName), ";"));
            }
            Map<String, DyformSubformFieldDefinition> subfieldMap = getSubformFieldMap(subFormUuid, subformDefinionMap);
            List<String> subformFields = taskForm.getAllSubformFields(subFormUuid);
            // 无权限的文件
            for (String fieldName : subfieldMap.keySet()) {
                if (subformFields.contains(fieldName) && !listMap.containsKey(fieldName)
                        && isFileField(subfieldMap.get(fieldName))) {
                    dyFormData.setFileFieldSecDevBtnId(subFormUuid, fieldName, StringUtils.EMPTY);
                }
            }
        }
    }

    /**
     * @param editFieldMap
     * @param taskForm
     * @param subformDefinionMap
     */
    private void clearEditField(Map<String, List<String>> editFieldMap, TaskForm taskForm,
                                Map<String, DyformSubformFormDefinition> subformDefinionMap) {
        editFieldMap.remove(taskForm.getFormUuid());
        Set<String> subformUuids = taskForm.getAllSubformUuids();
        for (String subformUuid : subformUuids) {
            List<String> subformFields = editFieldMap.get(subformUuid);
            DyformSubformFormDefinition subformFormDefinition = subformDefinionMap.get(subformUuid);
            if (CollectionUtils.isNotEmpty(subformFields) && subformFormDefinition != null) {
                List<DyformSubformFieldDefinition> subformFieldDefinitions = subformFormDefinition
                        .getSubformFieldDefinitions();
                for (DyformSubformFieldDefinition dyformSubformFieldDefinition : subformFieldDefinitions) {
                    subformFields.remove(dyformSubformFieldDefinition.getName());
                }
            }
        }
    }

    /**
     * @param subFormUuid
     * @param subformDefinionMap
     * @return
     */
    private Map<String, DyformSubformFieldDefinition> getSubformFieldMap(String subFormUuid,
                                                                         Map<String, DyformSubformFormDefinition> subformDefinionMap) {
        DyformSubformFormDefinition dyformSubformFormDefinition = subformDefinionMap.get(subFormUuid);
        if (dyformSubformFormDefinition == null) {
            return Collections.emptyMap();
        }
        List<DyformSubformFieldDefinition> subformFieldDefinitions = dyformSubformFormDefinition
                .getSubformFieldDefinitions();
        return ConvertUtils.convertElementToMap(subformFieldDefinitions, "name");
    }

    /**
     * @param subformFieldDefinition
     * @return
     */
    private boolean isFileField(DyformSubformFieldDefinition subformFieldDefinition) {
        if (subformFieldDefinition == null) {
            return false;
        }
        return ControlTypeUtils.isInputModeEqAttach(subformFieldDefinition.getInputMode());
    }

    /**
     * 处理新增字段的editFieldMap onlyReadFieldMap
     */
    private void fillEditAndOnlyReadFieldMapForNewField(String formUuid, Map<String, List<String>> editFieldMap,
                                                        Map<String, List<String>> onlyReadFieldMap, DyFormFormDefinition dyFormDefinition,
                                                        Map<String, DyformSubformFormDefinition> subformDefinionMap,
                                                        Map<String, DyFormFormDefinition> templateDefinionMap, List<String> allFormFieldList) {

        FormDefinition formDefinition = (FormDefinition) dyFormDefinition;
        FormDefinitionHandler formDefinitionHandler = formDefinition.doGetFormDefinitionHandler();

        if (CollectionUtils.isNotEmpty(allFormFieldList)) {
            // 主表
            List<String> fieldNamesOfMainform = formDefinitionHandler.getFieldNamesOfMainform();
            for (String fieldNameOfMainForm : fieldNamesOfMainform) {
                if (!allFormFieldList.contains(fieldNameOfMainForm)) {
                    if (!editFieldMap.containsKey(formUuid)) {
                        editFieldMap.put(formUuid, new ArrayList<String>());
                    }
                    JSONObject jsonObject = formDefinitionHandler.getFieldDefinitionJson(fieldNameOfMainForm);
                    try {
                        // bug#49271
                        if (jsonObject != null && jsonObject.has(EnumFieldPropertyName.showType.name())
                                && DyShowType.edit
                                .equals(jsonObject.getString(EnumFieldPropertyName.showType.name()))) {
                            editFieldMap.get(formUuid).add(fieldNameOfMainForm);
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }

            // 从表
            for (String subFormUuid : subformDefinionMap.keySet()) {
                JSONObject subformJSONObject = formDefinitionHandler.getSubformJSONObject(subFormUuid);
                try {
                    boolean isNewSubform = false;
                    JSONObject fieldsJsonObject = subformJSONObject.getJSONObject("fields");

                    for (Object fieldNameObject : fieldsJsonObject.keySet()) {
                        JSONObject fieldJsonObject = fieldsJsonObject.getJSONObject(fieldNameObject.toString());
                        String name = fieldJsonObject.getString("name");
                        String hidden = fieldJsonObject.has("hidden") ? fieldJsonObject.getString("hidden") : "1";
                        if (StringUtils.equals("1", hidden) // 非隐藏字段
                                && !allFormFieldList.contains(subFormUuid + ":" + name) // 流程保存的所有字段信息不包括该字段 ->
                            // 新增字段/新显示字段
                        ) {
                            if (!editFieldMap.containsKey(subFormUuid)) {
                                isNewSubform = true;
                                editFieldMap.put(subFormUuid, new ArrayList<String>());
                            }
                            editFieldMap.get(subFormUuid).add(name);
                        }
                    }
                    // 新增从表，添加所有操作按钮
                    if (isNewSubform && subformJSONObject.has("tableButtonInfo")) {
                        JSONArray tableButtonInfos = subformJSONObject.getJSONArray("tableButtonInfo");
                        for (int index = 0; index < tableButtonInfos.length(); index++) {
                            JSONObject tableButtonInfo = tableButtonInfos.getJSONObject(index);
                            String btnCode = tableButtonInfo.getString("code");
                            editFieldMap.get(subFormUuid).add(btnCode + "_" + subFormUuid);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // 子表单
            for (String templateUuid : templateDefinionMap.keySet()) {
                DyFormFormDefinition dyFormFormDefinition = templateDefinionMap.get(templateUuid);
                List<DyformFieldDefinition> fieldDefinitions = dyFormFormDefinition.doGetFieldDefintions();
                for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
                    if (!allFormFieldList.contains(templateUuid + ":" + fieldDefinition.getName())) {
                        if (!editFieldMap.containsKey(templateUuid)) {
                            editFieldMap.put(templateUuid, new ArrayList<String>());
                        }
                        editFieldMap.get(templateUuid).add(fieldDefinition.getName());
                    }
                }
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    private boolean isMobileApp() {
        return JsonDataServicesContextHolder.isMobileRequest();
    }

    /**
     * @param key
     * @param dyFormData
     * @param wrapperKeyMap
     * @return
     */
    private String getWrapperKey(String key, DyFormData dyFormData, Map<String, String> wrapperKeyMap) {
        String wrapperKey = getWrapperKey(key, dyFormData);
        return wrapperKeyMap.containsKey(wrapperKey) ? getWrapperKey(wrapperKeyMap.get(wrapperKey), dyFormData)
                : wrapperKey;
    }

    /**
     * 根据配置的表单定义UUID获取表单数据中实际存在的表单定义UUID
     *
     * @param key
     * @param dyFormData
     * @return
     */
    private String getWrapperKey(String key, DyFormData dyFormData) {
        if (TaskForm.PLACEHOLDER_CTR_PREFIX.startsWith(key) || TaskForm.FILE_LIBRARY_PREFIX.startsWith(key)
                || TaskForm.TABLE_VIEW_PREFIX.startsWith(key)) {
            return key;
        }
        if (dyFormData.isFormUuidInThisForm(key)) {
            return key;
        }
        // 旧版本的表单定义UUID通过表单定义ID获取表单数据中存在的对应版本的表单定义UUID
        String formId = dyFormFacade.getFormIdByFormUuid(key);
        if (StringUtils.isBlank(formId)) {
            return key;
        }
        String newKey = dyFormData.getFormUuidByFormId(formId);
        if (StringUtils.isBlank(newKey)) {
            return key;
        }
        if (dyFormData.isFormUuidInThisForm(newKey)) {
            return newKey;
        }
        return key;
    }

    /**
     * @param dyFormData
     * @param hideFields
     * @param wrapperKey
     * @param subformDefinionMap
     * @return
     */
    private List<String> getExistsSubforms(DyFormData dyFormData, List<String> hideFields, String wrapperKey,
                                           Map<String, DyformSubformFormDefinition> subformDefinionMap) {
        List<String> subformIds = Lists.newArrayListWithExpectedSize(0);
        if (CollectionUtils.isEmpty(hideFields) || MapUtils.isEmpty(subformDefinionMap)) {
            return subformIds;
        }
        for (String key : subformDefinionMap.keySet()) {
            String subformId = subformDefinionMap.get(key).getName();
            if (hideFields.contains(subformId)) {
                subformIds.add(subformId);
            }
        }
        return subformIds;
    }

    private List<String> getSubList(List<String> allFields, List<String> subList) {
        allFields.removeAll(subList);
        return allFields;
    }

    /**
     * @param hiddenPlaceholderCtrs
     * @param formDefinition
     * @return
     */
    private List<String> getShownPlaceholderCtrs(List<String> hiddenPlaceholderCtrs,
                                                 DyFormFormDefinition formDefinition) {
        List<String> placeholderCtrIds = formDefinition.doGetPlaceholderCtrIds();
        placeholderCtrIds.removeAll(hiddenPlaceholderCtrs);
        return placeholderCtrIds;
    }

    /**
     * @param hiddenFileLibrarys
     * @param formDefinition
     * @return
     */
    private List<String> getShownFileLibrarys(List<String> hiddenFileLibrarys, DyFormFormDefinition formDefinition) {
        List<String> fileLibraryIds = formDefinition.doGetFileLibraryIds();
        fileLibraryIds.removeAll(hiddenFileLibrarys);
        return fileLibraryIds;
    }

    /**
     * @param hiddenTableViews
     * @param formDefinition
     * @return
     */
    private List<String> getShownTableViews(List<String> hiddenTableViews, DyFormFormDefinition formDefinition) {
        List<String> tableViewIds = formDefinition.doGetTableViewIds();
        tableViewIds.removeAll(hiddenTableViews);
        return tableViewIds;
    }

    /**
     * @param hiddenSubformIds
     * @param subformDefinionMap
     * @return
     */
    private List<String> getShownSubforms(List<String> hiddenSubformIds,
                                          Map<String, DyformSubformFormDefinition> subformDefinionMap, TaskForm taskForm) {
        List<String> subformIds = Lists.newArrayListWithExpectedSize(0);
        if (MapUtils.isEmpty(subformDefinionMap)) {
            return subformIds;
        }
        Set<String> subformUuids = taskForm.getAllSubformUuids();
        for (String key : subformDefinionMap.keySet()) {
            // 表单新增的从表不做处理
            if (subformUuids.contains(key)) {
                String subformId = subformDefinionMap.get(key).getName();
                if (!hiddenSubformIds.contains(subformId)) {
                    subformIds.add(subformId);
                }
            }
        }
        return subformIds;
    }

    /**
     * 如何描述该方法
     *
     * @param workBean
     * @return
     */
    public TaskForm getTaskForm(WorkBean workBean) {
        TaskForm taskForm = null;
        String aclRole = workBean.getAclRole();
        String formUuid = workBean.getFormUuid();
        String flowInstUuid = workBean.getFlowInstUuid();
        String taskInstUuid = workBean.getTaskInstUuid();
        Set<Permission> permissions = getAclPermission(workBean);
        // 表单的域显示控制原则按优先级如下：
        // 1、督办、监控角色人员表单内容均可查看。
        // 2、经办人（待办、已办）根据所属最后一次办理环节的权限查看表单信息（无论流程是否归档）
        // 3、流程归档后，流程阅读人需要在流程属性中增加专门显示/隐藏控制的设置后，根据设置要求进行查看表单。
        if (WorkFlowAclRole.TODO.equals(aclRole) || WorkFlowAclRole.DRAFT.equals(aclRole)
                || permissions.contains(AclPermission.TODO) || permissions.contains(AclPermission.DRAFT)) {
            // 待办、草搞处理
            if (StringUtils.isNotBlank(taskInstUuid)) {
                taskForm = taskService.getTaskForm(taskInstUuid);
            } else {
                taskForm = taskService.getStartTaskForm(workBean.getFlowDefUuid());
            }
        } else if (WorkFlowAclRole.MONITOR.equals(aclRole) || WorkFlowAclRole.SUPERVISE.equals(aclRole)
                || permissions.contains(AclPermission.MONITOR) || permissions.contains(AclPermission.SUPERVISE)) {
            // 督办、监控角色处理
            taskForm = new TaskForm(formUuid);
        } else {
            // 已办处理
            String userId = SpringSecurityUtils.getCurrentUserId();
            String copyToTaskInstUuid = null;
            // 获取用户最新的操作信息
            TaskOperation taskOperation = null;
            WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
            if (permissions.contains(AclPermission.DONE)) {
                // 已办流程表单权限，done限于已办人所办理环节的表单内容
                if (StringUtils.equals("done", workFlowSettings.getDoneViewFormMode())) {
                    taskOperation = taskOperationService.getLastestDoneByUserId(userId, flowInstUuid);
                } else {
                    // current查看流程当前环节的表单内容
                    taskForm = taskService.getTaskForm(taskInstUuid);
                }
            } else if (WorkFlowAclRole.UNREAD.equals(aclRole) || WorkFlowAclRole.FLAG_READ.equals(aclRole)
                    || permissions.contains(AclPermission.UNREAD) || permissions.contains(AclPermission.FLAG_READ)) {
                // 被抄送人表单权限，copy固定为抄送时的表单内容
                if (StringUtils.equals("copy", workFlowSettings.getCoptyToViewFormMode())) {
                    taskOperation = taskOperationService.getLastestCopyToByUserId(userId, flowInstUuid);
                    // 提交时抄送
                    if (taskOperation == null) {
                        copyToTaskInstUuid = taskOperationService.getLastestTaskInstUuidBySubmitAndCopyToUser(userId, flowInstUuid);
                    } else {
                        if (StringUtils.isNotBlank(taskOperation.getExtraInfo())) {
                            Map<String, Object> copyInfo = JsonUtils.json2Object(taskOperation.getExtraInfo(), Map.class);
                            copyToTaskInstUuid = ObjectUtils.toString(copyInfo.get("taskInstUuid"), StringUtils.EMPTY);
                        }
                        if (StringUtils.isBlank(copyToTaskInstUuid)) {
                            copyToTaskInstUuid = taskOperation.getTaskInstUuid();
                        }
                    }
                    // 查看流程
                    if (StringUtils.isBlank(copyToTaskInstUuid)) {
                        copyToTaskInstUuid = taskInstUuid;
                    }
                } else {
                    // current流程当前环节的表单内容
                    copyToTaskInstUuid = taskInstUuid;
                }
            } else if (permissions.contains(AclPermission.ATTENTION) || permissions.contains(AclPermission.READ)) {
                copyToTaskInstUuid = taskInstUuid;
            } else if (WorkFlowAclRole.OVER.equals(aclRole) == false) {
                taskOperation = taskOperationService.getLastestByUserId(userId, flowInstUuid);
            }

            if (taskForm == null) {
                if (StringUtils.isNotBlank(copyToTaskInstUuid)) {
                    taskForm = taskService.getTaskForm(copyToTaskInstUuid);
                } else if (taskOperation == null) {
                    taskForm = new TaskForm(formUuid);
                } else {
                    taskForm = taskService.getTaskForm(taskOperation.getTaskInstUuid());
                }
            }
        }
        return taskForm;
    }

    /**
     * @param workBean
     * @return
     */
    private Set<Permission> getAclPermission(WorkBean workBean) {
        Set<Permission> permissions = workBean.getPermissions();
        if (CollectionUtils.isNotEmpty(permissions)) {
            return permissions;
        }

        String flowDefUuid = workBean.getFlowDefUuid();
        String taskInstUuid = workBean.getTaskInstUuid();
        permissions = taskService.getCurrentUserPermissions(taskInstUuid, flowDefUuid);
        workBean.setPermissions(permissions);
        return permissions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getTaskProcessByTaskInstUuidAndFlowDefId(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, Map<String, Object>> getTaskProcessByTaskInstUuidAndFlowDefId(String taskInstUuid,
                                                                                     String flowDefId) {
        if (StringUtils.isBlank(taskInstUuid)) {
            FlowDefinition flowDefinition = flowDefinitionService.getById(flowDefId);
            return getWorkProcess(taskInstUuid, null, flowDefinition.getUuid(), flowDefinition.getFormUuid());
        } else {
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            return getWorkProcess(taskInstUuid, taskInstance.getFlowInstance().getUuid(),
                    taskInstance.getFlowDefinition().getUuid(), taskInstance.getFormUuid());
        }
    }

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     */
    private Map<String, Map<String, Object>> getWorkProcess(String taskInstUuid, String flowInstUuid,
                                                            String flowDefUuid, String formUuid) {
        Map<String, Map<String, Object>> process = new HashMap<String, Map<String, Object>>();

        // 1、新流程建时
        if (StringUtils.isBlank(taskInstUuid)) {
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            // 当前环节
            TaskData firstData = flowService.getFirstTaskProcessInfo(flowDefUuid);
            Map<String, Object> first = new HashMap<String, Object>();
            first.put("taskName", firstData.getTaskName());
            first.put("taskId", firstData.getTaskId());
            first.put("assignee", StringUtils.isNotBlank(userDetails.getLocalUserName()) ? userDetails.getLocalUserName() : userDetails.getUserName());
            // 待办、已办人员名称
            first.put("todoUserNames", Lists.newArrayList(first.get("assignee")));
            first.put("doneUserNames", Collections.emptyList());
            process.put("current", first);

            // 下一环节
            TaskData nextData = flowService.getNextTaskProcessInfo(flowDefUuid, firstData.getTaskId());
            Map<String, Object> next = new HashMap<String, Object>();
            next.put("taskName", nextData.getTaskName());
            next.put("taskId", nextData.getTaskId());

            String taskRawUserNames = nextData.getTaskRawUserNames();
            taskRawUserNames = decorateRawNames(taskRawUserNames, formUuid);
            next.put("assignee", taskRawUserNames);
            process.put("next", next);
            return process;
        } else {
            // 补审补办环节信息
            List<String> supplementTaskUuids = Lists.newArrayList();
            FlowInstanceParameter flowInstanceParameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, SameUserSubmitService.KEY_SUPPLEMENT_INFO);
            if (flowInstanceParameter != null) {
                SameUserSupplementInfo supplementInfo = JsonUtils.json2Object(flowInstanceParameter.getValue(), SameUserSupplementInfo.class);
                supplementTaskUuids = supplementInfo.getSupplementTaskUuids();
            }

            List<TaskActivityQueryItem> taskActivities = taskActivityService.getAllActivityByFlowInstUuid(flowInstUuid);
            List<TaskOperation> allTaskOperations = taskOperationService.getByFlowInstUuid(flowInstUuid);
            // 2、流程办理中
            String currentTaskInstUuid = taskInstUuid;
            TaskInstance currentTasnInstance = taskService.getTask(currentTaskInstUuid);
            Set<String> previousTaskUuids = getPreviousTaskUuids(taskActivities, currentTasnInstance);
            if (currentTasnInstance.getEndTime() == null) {
                // 上一环节
                List<String> previousTaskNames = Lists.newArrayList();
                List<String> previousTaskIds = Lists.newArrayList();
                Set<String> previousAssignees = Sets.newLinkedHashSet();
                boolean isSupplement = false;
                for (String previousTaskUuid : previousTaskUuids) {
                    TaskInstance previousTaskInstance = taskService.getTask(previousTaskUuid);
                    // 子流程
                    if (Integer.valueOf(2).equals(previousTaskInstance.getType())) {
                        previousTaskNames.add(previousTaskInstance.getName());
                        previousTaskIds.add(previousTaskInstance.getId());
                        List<TaskSubFlow> taskSubFlows = taskSubFlowService
                                .getAllByParentTaskInstUuid(previousTaskUuid);
                        List<String> userIds = Lists.newArrayList();
                        for (TaskSubFlow taskSubFlow : taskSubFlows) {
                            Integer subFlowCompletionState = taskSubFlow.getCompletionState();
                            String subFlowModifier = taskSubFlow.getModifier();
                            if (TaskSubFlow.STATUS_COMPLETED.equals(subFlowCompletionState)) {
                                previousAssignees.add(taskSubFlow.getTodoName());
                            } else if (TaskSubFlow.STATUS_CANCEL.equals(subFlowCompletionState)) {
                                if (!userIds.contains(subFlowModifier)) {
                                    userIds.add(subFlowModifier);
                                }
                            } else if (TaskSubFlow.STATUS_ROLLBACK.equals(subFlowCompletionState)) {
                                if (!userIds.contains(subFlowModifier)) {
                                    userIds.add(subFlowModifier);
                                }
                            }
                        }
                        previousAssignees.addAll(Arrays.asList(StringUtils.split(
                                IdentityResolverStrategy.resolveAsNames(userIds), Separator.SEMICOLON.getValue())));
                    } else {
                        List<TaskOperation> taskOperations = filterTaskOperations(allTaskOperations, previousTaskUuid);
                        Map<String, TaskActivityQueryItem> taskActivityMap = null;
                        String preTaskUuid = previousTaskUuid;
                        if (CollectionUtils.isEmpty(taskOperations)) {
                            taskActivityMap = ConvertUtils.convertElementToMap(taskActivities, "taskInstUuid");
                        }
                        while (CollectionUtils.isEmpty(taskOperations)) {
                            TaskActivityQueryItem taskActivityQueryItem = taskActivityMap.get(preTaskUuid);
                            if (taskActivityQueryItem == null) {
                                break;
                            }
                            preTaskUuid = taskActivityQueryItem.getPreTaskInstUuid();
                            if (StringUtils.isBlank(preTaskUuid)) {
                                break;
                            }
                            taskOperations = filterTaskOperations(allTaskOperations, preTaskUuid);
                        }
                        if (CollectionUtils.isNotEmpty(taskOperations)) {
                            String taskName = taskOperations.get(0).getTaskName();
                            // 取历史环节操作人
                            Set<String> assigneeNames = getAssigneeNames(taskOperations);
                            previousTaskNames.add(taskName);
                            previousTaskIds.add(taskOperations.get(0).getTaskId());
                            previousAssignees.add(StringUtils.join(assigneeNames, Separator.SEMICOLON.getValue()));
                            isSupplement = supplementTaskUuids.contains(previousTaskUuid);
                            break;
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(previousTaskNames)) {
                    Map<String, Object> previous = new HashMap<String, Object>();
                    previous.put("taskName", StringUtils.join(previousTaskNames, Separator.COMMA.getValue()));
                    previous.put("taskId", StringUtils.join(previousTaskIds, Separator.COMMA.getValue()));
                    previous.put("supplemented", isSupplement);
                    previous.put("assignee", StringUtils.join(previousAssignees, Separator.COMMA.getValue()));
                    process.put("previous", previous);
                }

                // 当前环节
                List<TaskOperation> taskOperations = null;
                Set<String> todoUserNames = Sets.newLinkedHashSet();
                // 子流程
                if (Integer.valueOf(2).equals(currentTasnInstance.getType())) {
                    Set<String> doneUserNames = Sets.newLinkedHashSet();
                    List<TaskSubFlow> taskSubFlows = taskSubFlowService.getAllByParentTaskInstUuid(currentTaskInstUuid);
                    for (TaskSubFlow taskSubFlow : taskSubFlows) {
                        // 未完成
                        if (TaskSubFlow.STATUS_NORMAL.equals(taskSubFlow.getCompletionState())) {
                            todoUserNames.add(taskSubFlow.getTodoName());
                        } else if (TaskSubFlow.STATUS_COMPLETED.equals(taskSubFlow.getCompletionState())) {
                            // 正常结束
                            doneUserNames.add(taskSubFlow.getTodoName());
                        }
                    }
                    // todoUserNames.addAll(taskSubFlowService.listTodoUserNameByParentTaskInstUuid(currentTaskInstUuid));
                    // taskOperations =
                    // taskOperationService.listByParentTaskInstUuid(currentTaskInstUuid);
                    Map<String, Object> current = new HashMap<String, Object>();
                    current.put("taskName", currentTasnInstance.getName());
                    current.put("taskId", currentTasnInstance.getId());
                    current.put("supplemented", supplementTaskUuids.contains(currentTasnInstance.getUuid()));
                    current.put("assignee", StringUtils.join(todoUserNames, Separator.SEMICOLON.getValue()));
                    // 待办、已办人员名称
                    current.put("todoUserNames", todoUserNames);
                    current.put("doneUserNames", doneUserNames);
                    process.put("current", current);
                } else {
                    taskOperations = filterTaskOperations(allTaskOperations, currentTaskInstUuid);
                    todoUserNames.addAll(getTaskAssigneeNames(currentTasnInstance, taskService.getTodoUserIds(currentTaskInstUuid)));
                    // 待办人包含委托人
                    List<TaskDelegation> taskDelegations = taskDelegationService
                            .listRunningTaskDelegationByTaskInstUuid(currentTaskInstUuid);
                    if (CollectionUtils.isNotEmpty(taskDelegations)) {
                        for (TaskDelegation taskDelegation : taskDelegations) {
                            String consignorName = taskDelegation.getConsignorName();
                            String trusteeName = taskDelegation.getTrusteeName();
                            todoUserNames.remove(consignorName);
                            todoUserNames.remove(trusteeName);
                            todoUserNames.add(consignorName + "（委托给 " + trusteeName + "）");
                        }
                    }
                    Map<String, Object> current = new HashMap<String, Object>();
                    current.put("taskName", currentTasnInstance.getName());
                    current.put("taskId", currentTasnInstance.getId());
                    current.put("supplemented", supplementTaskUuids.contains(currentTaskInstUuid));
                    current.put("assignee", StringUtils.join(todoUserNames, Separator.SEMICOLON.getValue()));
                    // 待办、已办人员名称
                    current.put("todoUserNames", todoUserNames);
                    current.put("doneUserNames", getAssigneeNames(taskOperations));
                    process.put("current", current);
                }

                // 下一环节
                try {
                    TaskData taskData = taskService.getNextConfigInfo(currentTaskInstUuid);// taskService.getConfigInfo(currentTaskUuid);
                    Map<String, Object> next = new HashMap<String, Object>();
                    next.put("taskName", taskData.getTaskName());
                    next.put("taskId", taskData.getTaskId());
                    String taskRawUserNames = taskData.getTaskRawUserNames();
                    taskRawUserNames = decorateRawNames(taskRawUserNames, formUuid);
                    next.put("assignee", taskRawUserNames);
                    process.put("next", next);
                } catch (WorkFlowException ex) {
                    logger.warn("获取流程下一环节失败：" + ex.getMessage(), ex);
                }
            } else {
                // 3、流程已结束
                List<TaskOperation> taskOperations = taskOperationService.getByTaskInstUuid(taskInstUuid);
                String taskName = null;
                String assigneeName = null;
                if (CollectionUtils.isNotEmpty(taskOperations)) {
                    taskName = taskOperations.get(0).getTaskName();
                    // 取历史环节操作人
                    Set<String> assigneeNames = getAssigneeNames(taskOperations);
                    assigneeName = StringUtils.join(assigneeNames, Separator.SEMICOLON.getValue());
                    Map<String, Object> previous = new HashMap<String, Object>();
                    previous.put("taskName", taskName);
                    previous.put("taskId", taskOperations.get(0).getTaskId());
                    previous.put("supplemented", supplementTaskUuids.contains(taskInstUuid));
                    previous.put("assignee", "null".equals(assigneeName) ? StringUtils.EMPTY : assigneeName);
                    process.put("previous", previous);
                } else {
                    TaskInstance taskInstance = taskService.getTask(taskInstUuid);
                    Map<String, Object> previous = new HashMap<String, Object>();
                    previous.put("taskName", taskInstance.getName());
                    previous.put("taskId", taskInstance.getId());
                    previous.put("supplemented", supplementTaskUuids.contains(taskInstUuid));
                    previous.put("assignee",
                            "null".equals(assigneeName) || assigneeName == null ? StringUtils.EMPTY : assigneeName);
                    process.put("previous", previous);
                }
                Map<String, Object> over = new HashMap<String, Object>();
                over.put("taskName", "结束");
                over.put("assignee", StringUtils.EMPTY);
                process.put("current", over);
            }
        }

        return process;
    }

    /**
     * @param taskActivities
     * @param taskInstance
     * @return
     */
    private Set<String> getPreviousTaskUuids(List<TaskActivityQueryItem> taskActivities, TaskInstance taskInstance) {
        String taskInstUuid = taskInstance.getUuid();
        Set<String> previousTaskUuids = Sets.newLinkedHashSet();
        Map<String, TaskActivityQueryItem> taskActivityMap = ConvertUtils.convertElementToMap(taskActivities,
                "taskInstUuid");
        String parallelTaskInstUuid = null;
        for (TaskActivityQueryItem item : taskActivities) {
            if (StringUtils.equals(item.getTaskInstUuid(), taskInstUuid)) {
                String preTaskInstUuid = item.getPreTaskInstUuid();
                if (StringUtils.isNotBlank(preTaskInstUuid)) {
                    previousTaskUuids.add(preTaskInstUuid);
                    TaskActivityQueryItem preItem = taskActivityMap.get(preTaskInstUuid);
                    // 并行分支合并情况
                    if (StringUtils.isNotBlank(preItem.getParallelTaskInstUuid())
                            && StringUtils.isBlank(taskInstance.getParallelTaskInstUuid())) {
                        parallelTaskInstUuid = preItem.getParallelTaskInstUuid();
                        break;
                    }
                }
            }
        }
        // 并行分支合并情况
        if (StringUtils.isNotBlank(parallelTaskInstUuid)) {
            List<TaskBranch> taskBranchs = taskBranchService.listByParallelTaskInstUuid(parallelTaskInstUuid);
            for (TaskBranch taskBranch : taskBranchs) {
                if (StringUtils.equals(taskInstUuid, taskBranch.getJoinTaskInstUuid())) {
                    previousTaskUuids.add(taskBranch.getCurrentTaskInstUuid());
                }
            }
        }
        return previousTaskUuids;
    }

    /**
     * @param allTaskOperations
     * @param taskInstUuid
     * @return
     */
    private List<TaskOperation> filterTaskOperations(List<TaskOperation> allTaskOperations, String taskInstUuid) {
        List<TaskOperation> taskOperations = Lists.newArrayList();
        for (TaskOperation taskOperation : allTaskOperations) {
            if (StringUtils.equals(taskOperation.getTaskInstUuid(), taskInstUuid)) {
                taskOperations.add(taskOperation);
            }
        }
        return taskOperations;
    }

    /**
     * @param taskOperations
     * @param assigneeName
     */
    private Set<String> getAssigneeNames(List<TaskOperation> taskOperations) {
        Set<String> assigneeNames = Sets.newLinkedHashSet();
        Map<String, TaskOperation> delegationOperationMap = Maps.newLinkedHashMap();
        for (TaskOperation taskOperation : taskOperations) {
            Integer actionCode = taskOperation.getActionCode();
            actionCode = actionCode == null ? 1 : actionCode;
            switch (actionCode) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                    // case 16:特送环节不算已办
                case 23:
                case 27:
                    // 提交 SUBMIT(1)
                    // 会签提交 COUNTER_SIGN_SUBMIT(2)
                    // 转办提交 TRANSFER_SUBMIT(3)
                    // 退回 ROLLBACK(4)
                    // 直接退回 DIRECT_ROLLBACK(5)
                    // 撤回 CANCEL(6)
                    // 转办 TRANSFER(7)
                    // 会签 COUNTER_SIGN(8)
                    // 特送环节 GOTO_TASK(16)
                    // 委托 DELEGATION(23)
                    // 委托提交 DELEGATION_SUBMIT(27)

                    // 委托
                    if (ActionCode.DELEGATION.getCode().equals(actionCode)) {
                        delegationOperationMap.put(taskOperation.getTaskIdentityUuid(), taskOperation);
                    } else if (ActionCode.DELEGATION_SUBMIT.getCode().equals(actionCode)) {
                        // 受托人办理
                        String identityJson = taskOperation.getExtraInfo();
                        if (StringUtils.isNotBlank(identityJson)) {
                            TaskIdentity historyTaskIdentity = JsonUtils.json2Object(identityJson, TaskIdentity.class);
                            String sourceTaskIdentityUuid = historyTaskIdentity.getSourceTaskIdentityUuid();
                            if (StringUtils.isNotBlank(sourceTaskIdentityUuid)
                                    && delegationOperationMap.containsKey(sourceTaskIdentityUuid)) {
                                String entrustToLabel = AppCodeI18nMessageSource.getMessage("WorkflowWork.taskAction.entrustTo", "pt-workflow", LocaleContextHolder.getLocale().toString(), "委托给");
                                assigneeNames.add(delegationOperationMap.get(sourceTaskIdentityUuid).getAssigneeName()
                                        + "（" + entrustToLabel + " " +
                                        (StringUtils.isNotBlank(taskOperation.getAssignee()) && LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) ? taskOperation.getAssigneeName() :
                                                IdentityResolverStrategy.resolveAsName(taskOperation.getAssignee()))
                                        + "）");
                                delegationOperationMap.remove(sourceTaskIdentityUuid);
                            }
                        }
                    } else {
                        assigneeNames.add(StringUtils.isNotBlank(taskOperation.getAssignee()) && LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) ?
                                taskOperation.getAssigneeName() : IdentityResolverStrategy.resolveAsName(taskOperation.getAssignee()));
                    }
                    break;
                default:
                    break;
            }
        }
        // 委托不算已办
        // // 委托人办理
        // for (TaskOperation taskOperation : delegationOperationMap.values()) {
        // assigneeNames.add(taskOperation.getAssigneeName());
        // }
        return assigneeNames;
    }

    /**
     * @param formUuid
     * @param nextData
     * @return
     */
    private String decorateRawNames(String taskRawUserNames, String formUuid) {
        if (StringUtils.isNotBlank(taskRawUserNames)) {
            String currentUserName = SpringSecurityUtils.getCurrentUserName();
            StringBuilder sb = new StringBuilder();
            List<DyformFieldDefinition> dyformFieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
            Map<String, DyformFieldDefinition> DyformFieldDefinitionMap = ConvertUtils
                    .convertElementToMap(dyformFieldDefinitions, "name");
            List<String> rawUsers = Arrays.asList(StringUtils.split(taskRawUserNames, Separator.COMMA.getValue()));
            Iterator<String> it = rawUsers.iterator();
            while (it.hasNext()) {
                String userName = it.next();
                if (ParticipantUtils.containsKey(userName)) {
                    sb.append(ParticipantUtils.getName(userName));
                } else if (DyformFieldDefinitionMap.containsKey(userName)) {
                    sb.append(DyformFieldDefinitionMap.get(userName).getDisplayName());
                } else {
                    if (StringUtils.contains(userName, currentUserName)) {
                        List<String> userNames = Arrays.stream(StringUtils.split(userName, Separator.SEMICOLON.getValue())).map(name -> {
                            if (StringUtils.equals(name, currentUserName)) {
                                String me = StringUtils.defaultIfBlank(AppCodeI18nMessageSource.getMessage("User.Me", "pt-org", LocaleContextHolder.getLocale().toString()), "我");
                                return name + "(" + me + ")";
                            }
                            return name;
                        }).collect(Collectors.toList());
                        sb.append(StringUtils.join(userNames, Separator.SEMICOLON.getValue()));
                    } else {
                        sb.append(userName);
                    }
                }

                if (it.hasNext()) {
                    sb.append(Separator.COMMA.getValue());
                }
            }
            taskRawUserNames = sb.toString();
            return taskRawUserNames;
        } else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    private SubTaskData getSubTaskData(TaskInstance taskInstance, FlowInstance flowInstance) {
        String flowInstUuid = flowInstance.getUuid();
        boolean isParentFlowInstance = taskSubFlowService.countByParentFlowInstUuid(flowInstUuid) > 0;
        boolean isChildFlowInstance = taskSubFlowService.countByFlowInstUuid(flowInstUuid) > 0;
        if (!isParentFlowInstance && !isChildFlowInstance) {
            return null;
        }
        SubTaskData subTaskData = null;
        String subTaskId = null;
        FlowDefinition flowDefinition = null;

        // 作为子流程
        if (!isParentFlowInstance && isChildFlowInstance) {
            TaskInstance parentTaskInstance = taskInstance.getParent();
            if (parentTaskInstance != null) {
                subTaskId = parentTaskInstance.getId();
                flowDefinition = parentTaskInstance.getFlowDefinition();
                return getSubTaskDataAsChild(taskInstance, flowInstance, parentTaskInstance,
                        parentTaskInstance.getFlowInstance(), subTaskId, flowDefinition);
            }
        } else {
            // 作为主流程
            Integer taskType = taskInstance.getType();
            if (Integer.valueOf(2).equals(taskType)) {
                subTaskId = taskInstance.getId();
                flowDefinition = taskInstance.getFlowDefinition();
            } else {
                TaskSubFlow taskSubFlow = taskSubFlowService.getLatestOneByParentFlowInstUuid(flowInstUuid);
                subTaskId = taskSubFlow.getParentTaskId();
                flowDefinition = flowInstance.getFlowDefinition();
            }
            subTaskData = getSubTaskDataAsParent(taskInstance, flowInstance, subTaskId, flowDefinition);
            // 同时作为子流程
            if (isChildFlowInstance) {
                TaskInstance parentTaskInstance = taskInstance.getParent();
                subTaskData.setParentTaskInstUuid(parentTaskInstance.getUuid());
                subTaskData.setParentFlowInstUuid(parentTaskInstance.getFlowInstance().getUuid());
                FlowDelegate parentFlowDelegate = FlowDelegateUtils
                        .getFlowDelegate(parentTaskInstance.getFlowDefinition());
                Node parentNode = parentFlowDelegate.getTaskNode(parentTaskInstance.getId());
                // 子流程的办理信息显示位置配置
                List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
                if (CollectionUtils.isNotEmpty(taskSubFlows)) {
                    SubTaskNode parentSubTaskNode = (SubTaskNode) parentNode;
                    TaskSubFlow taskSubFlow = taskSubFlows.get(0);
                    NewFlow newFlow = getNewFlow(parentFlowDelegate, taskSubFlow);
                    // 办理进度显示位置，1同主流程显示位置,其他指定显示位置
                    if (newFlow != null && newFlow.isCustomUndertakeSituationPlaceHolder()) {
                        subTaskData.setAsChildUndertakeSituationPlaceHolder(newFlow.getUndertakeSituationPlaceHolder());
                    } else {
                        subTaskData.setAsChildUndertakeSituationPlaceHolder(
                                parentSubTaskNode.getUndertakeSituationPlaceHolder());
                    }
                    // 信息分发显示位置，1同主流程显示位置,其他指定显示位置
                    if (newFlow != null && newFlow.isCustomInfoDistributionPlaceHolder()) {
                        subTaskData.setAsChildInfoDistributionPlaceHolder(newFlow.getInfoDistributionPlaceHolder());
                    } else {
                        subTaskData.setAsChildInfoDistributionPlaceHolder(
                                parentSubTaskNode.getInfoDistributionPlaceHolder());
                    }
                    // 操作记录显示位置，1同主流程显示位置,其他指定显示位置
                    if (newFlow != null && newFlow.isCustomOperationRecordPlaceHolder()) {
                        subTaskData.setAsChildOperationRecordPlaceHolder(newFlow.getOperationRecordPlaceHolder());
                    } else {
                        subTaskData.setAsChildOperationRecordPlaceHolder(
                                parentSubTaskNode.getOperationRecordPlaceHolder());
                    }
                }
                subTaskData.setChildFlowInstance(true);
            }
        }

        return subTaskData;
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param subTaskId
     * @param flowDefinition
     * @return
     */
    private SubTaskData getSubTaskDataAsChild(TaskInstance taskInstance, FlowInstance flowInstance,
                                              TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, String subTaskId,
                                              FlowDefinition flowDefinition) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        Node node = flowDelegate.getTaskNode(subTaskId);
        String flowInstUuid = flowInstance.getUuid();
        SubTaskData subTaskData = null;
        if (node instanceof SubTaskNode) {
            subTaskData = new SubTaskData();
            subTaskData.setFlowInstUuid(flowInstUuid);
            subTaskData.setTaskInstUuid(taskInstance.getUuid());
            subTaskData.setParentTaskInstUuid(parentTaskInstance.getUuid());
            subTaskData.setParentFlowInstUuid(parentFlowInstance.getUuid());
            SubTaskNode subTaskNode = (SubTaskNode) node;
            subTaskData.setBusinessType(subTaskNode.getBusinessType());
            subTaskData.setBusinessRole(subTaskNode.getBusinessRole());
            subTaskData.setUndertakeSituationPlaceHolder(subTaskNode.getUndertakeSituationPlaceHolder());
            subTaskData.setInfoDistributionPlaceHolder(subTaskNode.getInfoDistributionPlaceHolder());
            subTaskData.setOperationRecordPlaceHolder(subTaskNode.getOperationRecordPlaceHolder());
            // 子流程的办理信息显示位置配置
            List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
            if (CollectionUtils.isNotEmpty(taskSubFlows)) {
                TaskSubFlow taskSubFlow = taskSubFlows.get(0);
                NewFlow newFlow = getNewFlow(flowDelegate, taskSubFlow);
                // 办理进度显示位置，1同主流程显示位置,其他指定显示位置
                if (newFlow != null && newFlow.isCustomUndertakeSituationPlaceHolder()) {
                    subTaskData.setUndertakeSituationPlaceHolder(newFlow.getUndertakeSituationPlaceHolder());
                }
                // 信息分发显示位置，1同主流程显示位置,其他指定显示位置
                if (newFlow != null && newFlow.isCustomInfoDistributionPlaceHolder()) {
                    subTaskData.setInfoDistributionPlaceHolder(newFlow.getInfoDistributionPlaceHolder());
                }
                // 操作记录显示位置，1同主流程显示位置,其他指定显示位置
                if (newFlow != null && newFlow.isCustomOperationRecordPlaceHolder()) {
                    subTaskData.setOperationRecordPlaceHolder(newFlow.getOperationRecordPlaceHolder());
                }
            }
            subTaskData.setChildFlowInstance(true);
            subTaskData.setExpandList(flowDelegate.getFlow().getTask(subTaskId).getExpandList());
        }
        return subTaskData;
    }

    /**
     * @param flowInstance
     * @param subTaskId
     * @param flowDefinition
     * @return
     */
    private SubTaskData getSubTaskDataAsParent(TaskInstance taskInstance, FlowInstance flowInstance, String subTaskId,
                                               FlowDefinition flowDefinition) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        Node node = flowDelegate.getTaskNode(subTaskId);
        String flowInstUuid = flowInstance.getUuid();
        SubTaskData subTaskData = null;
        if (node instanceof SubTaskNode) {
            subTaskData = new SubTaskData();
            subTaskData.setFlowInstUuid(flowInstUuid);
            subTaskData.setTaskInstUuid(taskInstance.getUuid());
            subTaskData.setParentTaskInstUuid(null);
            subTaskData.setParentFlowInstUuid(null);
            SubTaskNode subTaskNode = (SubTaskNode) node;
            subTaskData.setBusinessType(subTaskNode.getBusinessType());
            subTaskData.setBusinessRole(subTaskNode.getBusinessRole());
            subTaskData.setUndertakeSituationPlaceHolder(subTaskNode.getUndertakeSituationPlaceHolder());
            subTaskData.setInfoDistributionPlaceHolder(subTaskNode.getInfoDistributionPlaceHolder());
            subTaskData.setOperationRecordPlaceHolder(subTaskNode.getOperationRecordPlaceHolder());
            subTaskData.setParentFlowInstance(true);
            subTaskData.setExpandList(flowDelegate.getFlow().getTask(subTaskId).getExpandList());
        }
        return subTaskData;
    }

    private NewFlow getNewFlow(FlowDelegate flowDelegate, TaskSubFlow taskSubFlow) {
        String parentTaskId = taskSubFlow.getParentTaskId();
        String flowDefId = taskSubFlow.getFlowId();
        Boolean isMajor = taskSubFlow.getIsMajor();
        Node taskNode = flowDelegate.getTaskNode(parentTaskId);
        if (taskNode instanceof SubTaskNode) {
            List<NewFlow> newFlows = ((SubTaskNode) taskNode).getNewFlows();
            for (NewFlow newFlow : newFlows) {
                if (StringUtils.equals(newFlow.getFlowId(), flowDefId)
                        && Boolean.valueOf(newFlow.isMajor()).equals(isMajor)) {
                    return newFlow;
                }
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#loadBranchTaskData(com.wellsoft.pt.workflow.work.bean.BranchTaskData)
     */
    @Override
    public BranchTaskData loadBranchTaskData(BranchTaskData branchTaskData) {
        return taskBranchService.loadBranchTaskData(branchTaskData);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#loadSubTaskData(com.wellsoft.pt.workflow.work.bean.SubTaskData)
     */
    @Override
    public SubTaskData loadSubTaskData(SubTaskData subTaskData) {
        boolean isParentFlowInstance = subTaskData.isParentFlowInstance();
        boolean isChildFlowInstance = subTaskData.isChildFlowInstance();
        String taskInstUuid = subTaskData.getTaskInstUuid();
        String flowInstUuid = subTaskData.getFlowInstUuid();
        String parentFlowInstUuid = subTaskData.getParentFlowInstUuid();
        List<String> subFlowInstUuids = subTaskData.getSubFlowInstUuids();
        if (!isParentFlowInstance && !isChildFlowInstance) {
            return subTaskData;
        }
        // if (StringUtils.isBlank(parentFlowInstUuid)) {
        // return subTaskData;
        // }
        // <显示位置，承办信息列表>
        Map<String, List<FlowShareData>> shareDatas = Maps.newHashMapWithExpectedSize(0);
        // <显示位置，信息分发列表>
        Map<String, List<TaskInfoDistributionData>> distributeInfos = Maps.newHashMapWithExpectedSize(0);
        // <显示位置，操作记录列表>
        Map<String, List<TaskOperationData>> taskOperations = Maps.newHashMapWithExpectedSize(0);
        String undertakeSituationPlaceHolder = subTaskData.getUndertakeSituationPlaceHolder();
        String infoDistributionPlaceHolder = subTaskData.getInfoDistributionPlaceHolder();
        String operationRecordPlaceHolder = subTaskData.getOperationRecordPlaceHolder();
        // 作为子流程
        if (!isParentFlowInstance && isChildFlowInstance) {
            // 承办信息
            if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
                shareDatas.put(undertakeSituationPlaceHolder, taskSubFlowService.getUndertakeSituationDatas(
                        taskInstUuid, flowInstUuid, parentFlowInstUuid, subFlowInstUuids, null, false, true));
            }
            // 信息分发
            if (StringUtils.isNotBlank(infoDistributionPlaceHolder)) {
                distributeInfos.put(infoDistributionPlaceHolder,
                        taskSubFlowService.getDistributeInfos(parentFlowInstUuid));
            }
            // 操作记录
            if (StringUtils.isNotBlank(operationRecordPlaceHolder)) {
                taskOperations.put(operationRecordPlaceHolder,
                        taskSubFlowService.getSubflowRelateOperation(parentFlowInstUuid));
            }
        } else {
            // 作为主流程
            // 承办信息
            if (StringUtils.isNotBlank(undertakeSituationPlaceHolder)) {
                shareDatas.put(undertakeSituationPlaceHolder, taskSubFlowService.getUndertakeSituationDatas(
                        taskInstUuid, flowInstUuid, flowInstUuid, subFlowInstUuids, null, false, true));
            }
            // 信息分发
            if (StringUtils.isNotBlank(infoDistributionPlaceHolder)) {
                distributeInfos.put(infoDistributionPlaceHolder, taskSubFlowService.getDistributeInfos(flowInstUuid));
            }
            // 操作记录
            if (StringUtils.isNotBlank(operationRecordPlaceHolder)) {
                taskOperations.put(operationRecordPlaceHolder,
                        taskSubFlowService.getSubflowRelateOperation(flowInstUuid));
            }
            // 同时作为子流程
            if (isChildFlowInstance) {
                // 承办信息
                if (StringUtils.isNotBlank(subTaskData.getAsChildUndertakeSituationPlaceHolder())) {
                    List<FlowShareData> childShareDatas = taskSubFlowService.getUndertakeSituationDatas(taskInstUuid,
                            flowInstUuid, parentFlowInstUuid, subFlowInstUuids, null, false, true);
                    if (shareDatas.containsKey(subTaskData.getAsChildUndertakeSituationPlaceHolder())) {
                        shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder()).addAll(childShareDatas);
                        // 按分发时间降序
                        Collections.sort(shareDatas.get(subTaskData.getAsChildUndertakeSituationPlaceHolder()));
                    } else {
                        shareDatas.put(subTaskData.getAsChildUndertakeSituationPlaceHolder(), childShareDatas);
                    }
                }
                // 信息分发
                if (StringUtils.isNotBlank(subTaskData.getAsChildInfoDistributionPlaceHolder())) {
                    List<TaskInfoDistributionData> childDistributeInfos = taskSubFlowService
                            .getDistributeInfos(parentFlowInstUuid);
                    if (distributeInfos.containsKey(subTaskData.getAsChildInfoDistributionPlaceHolder())) {
                        List<TaskInfoDistributionData> allDistributeInfos = distributeInfos
                                .get(subTaskData.getAsChildInfoDistributionPlaceHolder());
                        allDistributeInfos.addAll(childDistributeInfos);
                        // 删除重复的信息分发
                        Set<String> flowInstUuids = Sets.newHashSet();
                        List<TaskInfoDistributionData> removeDistributeInfos = Lists.newArrayList();
                        for (TaskInfoDistributionData taskInfoDistributionData : allDistributeInfos) {
                            for (TaskInfoDistributionBean bean : taskInfoDistributionData.getDistributeInfos()) {
                                if (flowInstUuids.contains(bean.getFlowInstUuid())) {
                                    removeDistributeInfos.add(taskInfoDistributionData);
                                } else {
                                    flowInstUuids.add(bean.getFlowInstUuid());
                                    break;
                                }
                            }
                        }
                        allDistributeInfos.removeAll(removeDistributeInfos);
                    } else {
                        distributeInfos.put(subTaskData.getAsChildInfoDistributionPlaceHolder(), childDistributeInfos);
                    }
                }
                // 操作记录
                if (StringUtils.isNotBlank(subTaskData.getAsChildOperationRecordPlaceHolder())) {
                    List<TaskOperationData> childTaskOperations = taskSubFlowService
                            .getSubflowRelateOperation(parentFlowInstUuid);
                    if (taskOperations.containsKey(subTaskData.getAsChildOperationRecordPlaceHolder())) {
                        List<TaskOperationData> allTaskOperations = taskOperations
                                .get(subTaskData.getAsChildOperationRecordPlaceHolder());
                        allTaskOperations.addAll(childTaskOperations);
                        // 删除重复的操作记录
                        Set<String> flowInstUuids = Sets.newHashSet();
                        List<TaskOperationData> removeTaskOperations = Lists.newArrayList();
                        for (TaskOperationData taskOperationData : allTaskOperations) {
                            for (TaskOperationBean bean : taskOperationData.getOperations()) {
                                if (flowInstUuids.contains(bean.getFlowInstUuid())) {
                                    removeTaskOperations.add(taskOperationData);
                                } else {
                                    flowInstUuids.add(bean.getFlowInstUuid());
                                    break;
                                }
                            }
                        }
                        allTaskOperations.removeAll(removeTaskOperations);
                    } else {
                        taskOperations.put(subTaskData.getAsChildOperationRecordPlaceHolder(), childTaskOperations);
                    }
                }
            }
        }

//        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
//        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
//        FlowElement flowEntity = flowSchemeService.getFlowElement(flowDefinition);// FlowDefinitionParser.parseFlow(content);
//        subTaskData.setExpandList(flowEntity.getTask(taskInstanceService.get(taskInstUuid).getId()).getExpandList());

        subTaskData.setShareDatas(shareDatas);
        subTaskData.setDistributeInfos(distributeInfos);
        subTaskData.setTaskOperations(taskOperations);
        return subTaskData;
    }

    @Override
    public String loadSubFlowTitleExpression(String flowInstUuid, String taskInstUuid, String subFlowId) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
        // String content = flowDefinition.getFlowSchema().getContentAsString();
        FlowElement flowEntity = flowSchemeService.getFlowElement(flowDefinition);// FlowDefinitionParser.parseFlow(content);
        SubTaskElement subTaskElement = (SubTaskElement) flowEntity
                .getTask(taskInstanceService.get(taskInstUuid).getId());
        List<NewFlowElement> newFlowElements = subTaskElement.getNewFlows();
        for (NewFlowElement tmp : newFlowElements) {
            if (tmp.getValue().equals(subFlowId)) {
                return tmp.getTitleExpression();
            }
        }
        // 找不到，可能是旧数据，没有设置标题格式，直接按照默认格式
        return "${流程名称}_${子流程实例办理人}";
    }

    @Override
    public NewFlowElement loadSubFlowElement(String flowInstUuid, String taskInstUuid, String taskId,
                                             String flowDefId) {
        Assert.hasText(flowInstUuid, "loadSubFlowElement方法入参flowInstUuid不能为空");
        Assert.hasText(taskInstUuid, "loadSubFlowElement方法入参taskInstUuid不能为空");
        Assert.hasText(taskId, "loadSubFlowElement方法入参taskId不能为空");
        Assert.hasText(flowDefId, "loadSubFlowElement方法入参flowDefId不能为空");
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
//        String content = flowDefinition.getFlowSchema().getContentAsString();
        FlowElement flowEntity = flowSchemeService.getFlowElement(flowDefinition);// FlowDefinitionParser.parseFlow(content);
        SubTaskElement subTaskElement = (SubTaskElement) flowEntity.getTask(taskId);
        List<NewFlowElement> newFlowElements = subTaskElement.getNewFlows();
        for (NewFlowElement tmp : newFlowElements) {
            if (tmp.getValue().equals(flowDefId)) {
                if (StringUtils.isBlank(tmp.getTitleExpression())) {
                    tmp.setTitleExpression("${流程名称}_${子流程实例办理人}");
                }
                return tmp;
            }
        }
        return new NewFlowElement();
    }

    /**
     * 加载子流程默认展开的配置
     *
     * @param subTaskData flowInstUuid、taskInstUuid
     * @return
     */
    @Override
    public String loadExpandSetting(SubTaskData subTaskData) {
        FlowInstance flowInstance = flowInstanceService.get(subTaskData.getFlowInstUuid());
        if (null != flowInstance) {
            FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
//            String content = flowDefinition.getFlowSchema().getContentAsString();
            FlowElement flowEntity = flowSchemeService.getFlowElement(flowDefinition);// FlowDefinitionParser.parseFlow(content);

            String expandList = flowEntity.getTask(taskInstanceService.get(subTaskData.getTaskInstUuid()).getId())
                    .getExpandList();
            if (StringUtils.isNotBlank(expandList)) {
                return expandList;
            }
        }
        return "1";
    }

    /**
     * 加载子流程默认展开的配置
     *
     * @param flowInstUuid、taskInstUuid
     * @return
     */
    @Override
    public String loadExpandSetting(String flowInstUuid, String taskInstUuid) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        if (null != flowInstance) {
            FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
//            String content = flowDefinition.getFlowSchema().getContentAsString();
            FlowElement flowEntity = flowSchemeService.getFlowElement(flowDefinition);// FlowDefinitionParser.parseFlow(content);

            String expandList = flowEntity.getTask(taskInstanceService.get(taskInstUuid).getId()).getExpandList();
            if (StringUtils.isNotBlank(expandList)) {
                return expandList;
            }
        }
        return "1";
    }

    // 承办信息
    @Override
    public List<FlowShareData> loadShareDatasByPage(SubTaskDataVo vo) {
        String parentFlowInstUuid = vo.getParentFlowInstUuid();
        if (StringUtils.isBlank(parentFlowInstUuid)) {
            return null;
        }
        return taskSubFlowService.getUndertakeSituationDatasByPage(vo);
    }

    @Override
    public Page<TaskInfoDistributionData> loadDistributeInfosByPage(SubTaskDataVo vo) {
        return taskSubFlowService.getDistributeInfosByPage(vo);
    }

    @Override
    public Page<TaskOperationData> loadRelateOperationByPage(SubTaskDataVo vo) {
        return taskSubFlowService.getSubflowRelateOperationByPage(vo);
    }

    @Override
    public List<Map<String, String>> getSortFields() {
        List<Map<String, String>> result = new ArrayList<>();
        Map<String, String> sortMap = null;
        for (SortFieldEnum sortField : SortFieldEnum.values()) {
            sortMap = new HashMap<>();
            sortMap.put("id", sortField.getId());
            sortMap.put("text", sortField.getText());
            result.add(sortMap);
        }
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getProcess(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    public String getProcess(String flowInstUuid, boolean showRollbackRecord, boolean showNoOpinionRecord) {
        List<WorkProcessBean> workProcessBeans = this.getWorkProcess(flowInstUuid);
        if (showRollbackRecord == false) {
            // 不显示撤回前及撤回记录
            workProcessBeans = filterCancel(workProcessBeans);
        }
        if (showNoOpinionRecord == false) {
            // 不显示未签署意见记录
            workProcessBeans = filterNoOpinion(workProcessBeans);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='view_process_table' width='900px'>");
        sb.append("<thead class='view_process_head'>");
        sb.append("<tr>");
        sb.append(
                "<th class='th1'>环节名称</th><th class='th2'>办理人</th><th class='th3'>办理意见</th><th class='th4'>主送给</th><th class='th5'>抄送给</th><th class='th6'>阅读情况</th><th class='th7'>状态</th><th class='th8'>动作</th><th class='th9'>收到时间</th><th class='th10'>发出时间</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody class='view_process_body'>");
        for (WorkProcessBean workProcessBean : workProcessBeans) {
            sb.append("<tr>");
            // 办理环节
            sb.append("<td class='td1'>" + nullToEmpty(workProcessBean.getTaskName()) + "</td>");
            // 办理人
            sb.append("<td class='td2'>" + nullToEmpty(workProcessBean.getAssignee()) + "</td>");
            // 办理意见
            sb.append("<td class='td3'>" + nullToEmpty(workProcessBean.getOpinion()) + "</td>");
            // 送办对象
            sb.append("<td class='td4'>" + nullToEmpty(workProcessBean.getToUser()) + "</td>");
            // 送阅对象
            sb.append("<td class='td5'>" + nullToEmpty(workProcessBean.getCopyUser()) + "</td>");
            // 阅读情况
            sb.append("<td class='td6'>" + nullToEmpty(workProcessBean.getReadStatus()) + "</td>");
            // 状态
            if ("完成".equals(workProcessBean.getStatus())) {
                sb.append("<td class='td7'>" + nullToEmpty(workProcessBean.getStatus()) + "</td>");
            } else {
                sb.append("<td class='td7'><font color='red'>" + nullToEmpty(workProcessBean.getStatus())
                        + "</font></td>");
            }
            // 动作
            String mobileAppOpt = "";
            if (Boolean.TRUE.equals(workProcessBean.getIsMobileApp())) {
                mobileAppOpt = "(移动端)";
            }
            sb.append("<td class='td8'>" + nullToEmpty(workProcessBean.getActionName()) + mobileAppOpt + "</td>");
            // 提交时间
            if (workProcessBean.getSubmitTime() == null) {
                sb.append("<td class='td9'></td>");
            } else {
                sb.append("<td class='td9'>" + DateUtils.formatDateTimeMin(workProcessBean.getSubmitTime()) + "</td>");
            }
            // 完成时间
            if (workProcessBean.getEndTime() == null) {
                sb.append("<td class='td10'></td>");
            } else {
                sb.append("<td class='td10'>" + DateUtils.formatDateTimeMin(workProcessBean.getEndTime()) + "</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</tbody>");
        sb.append("</table>");
        return sb.toString();
    }

    /**
     * @param string
     * @return
     */
    private String nullToEmpty(Object object) {
        return object == null ? "" : object.toString();
    }

    /**
     * 不显示未签署意见记录
     *
     * @param workProcessBeans
     * @return
     */
    private List<WorkProcessBean> filterNoOpinion(List<WorkProcessBean> workProcessBeans) {
        List<WorkProcessBean> workProcesses = new ArrayList<WorkProcessBean>();
        for (WorkProcessBean workProcessBean : workProcessBeans) {
            if (StringUtils.isBlank(workProcessBean.getOpinion())) {
                workProcesses.add(workProcessBean);
            }
        }
        workProcessBeans.removeAll(workProcesses);
        return workProcessBeans;
    }

    /**
     * 不显示撤回前及撤回记录
     *
     * @param workProcessBeans
     * @return
     */
    private List<WorkProcessBean> filterCancel(List<WorkProcessBean> workProcessBeans) {
        List<WorkProcessBean> workProcesses = new ArrayList<WorkProcessBean>();
        boolean ignore = false;
        for (int index = workProcessBeans.size() - 1; index >= 0; index--) {
            WorkProcessBean workProcessBean = workProcessBeans.get(index);
            if (WorkFlowOperation.CANCEL.equals(workProcessBean.getActionType())) {
                workProcesses.add(workProcessBean);
                ignore = true;
                continue;
            }
            if (ignore == true) {
                workProcesses.add(workProcessBean);
                if (WorkFlowOperation.SUBMIT.equals(workProcessBean.getActionType())) {
                    ignore = false;
                }
            }
        }
        workProcessBeans.removeAll(workProcesses);
        return workProcessBeans;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getWorkProcess(java.lang.String)
     */
    @Override
    public List<WorkProcessBean> getWorkProcess(String flowInstUuid) {
        List<TaskActivity> taskActivities = taskService.getTaskActivities(flowInstUuid);
        Map<String, List<TaskOperation>> operationMap = taskService.getOperationAsMap(flowInstUuid);
        List<LogicFileInfo> opinionFiles = mongoFileService.getNonioFilesFromFolder(flowInstUuid, null);
        List<WorkProcessBean> workProcessBeans = extractWorkProcess(flowInstUuid, taskActivities, operationMap, opinionFiles);
        // for (WorkProcessBean workProcessBean : workProcessBeans) {
        // System.out.print(workProcessBean.getAssignee() + "\t");
        // System.out.print(workProcessBean.getOpinion() + "\t");
        // System.out.print(workProcessBean.getToUser() + "\t");
        // System.out.print(workProcessBean.getCopyUser() + "\t");
        // System.out.print(workProcessBean.getReadStatus() + "\t");
        // System.out.print(workProcessBean.getTaskName() + "\t");
        // System.out.print(workProcessBean.getStatus() + "\t");
        // System.out.print(workProcessBean.getActionName() + "\t");
        // System.out.print(workProcessBean.getSubmitTime() + "\t");
        // System.out.print(workProcessBean.getEndTime() + "\t");
        // System.out.println();
        // }
        return workProcessBeans;
    }

    /**
     * @param taskActivities
     * @param operationMap
     * @param opinionFiles
     * @return
     */
    private List<WorkProcessBean> extractWorkProcess(String flowInstUuid, List<TaskActivity> taskActivities,
                                                     Map<String, List<TaskOperation>> operationMap,
                                                     List<LogicFileInfo> opinionFiles) {
        Map<String, String> taskInstUuidMap = Maps.newHashMap();
        List<WorkProcessBean> workProcessBeans = new ArrayList<WorkProcessBean>();
        Map<String, TaskActivity> taskActivityMap = ConvertUtils.convertElementToMap(taskActivities, "preTaskInstUuid");
        Map<String, List<LogicFileInfo>> opinionFileMap = ListUtils.list2group(opinionFiles, "purpose");
        List<TaskInstance> unfinishedTaskInstances = new ArrayList<TaskInstance>();
        // 重置撤回的办理意见为（办理意见已撤回）
        Map<String, TaskOperation> cancelOperationMap = resetCancelOperationOpinion(taskActivities, operationMap);
        List<String> taskInstUuids = taskActivities.stream().map(taskActivity -> taskActivity.getTaskInstUuid()).collect(Collectors.toList());
        Map<String, String> operatorIdNameMap = getOperatorIdNameMap(operationMap);
        List<TaskInstance> taskInstances = taskInstanceService.listByUuids(taskInstUuids);
        Map<String, TaskInstance> taskInstanceMap = ConvertUtils.convertElementToMap(taskInstances, "uuid");
        // 补审补办环节信息
        List<String> supplementTaskUuids = null;
        FlowInstanceParameter flowInstanceParameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, SameUserSubmitService.KEY_SUPPLEMENT_INFO);
        if (flowInstanceParameter != null) {
            SameUserSupplementInfo supplementInfo = JsonUtils.json2Object(flowInstanceParameter.getValue(), SameUserSupplementInfo.class);
            supplementTaskUuids = supplementInfo.getSupplementTaskUuids();
        }
        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
        Map<String, List<OrgUserJobDto>> userJobsMap = Maps.newHashMap();
        for (TaskActivity taskActivity : taskActivities) {
            // 流转信息
            String taskInstUuid = taskActivity.getTaskInstUuid();
            TaskInstance taskInstance = taskInstanceMap.get(taskInstUuid);
            // 操作信息
            List<TaskOperation> taskOperations = operationMap.get(taskInstUuid);
            if (taskOperations != null) {
                for (TaskOperation taskOperation : taskOperations) {
                    WorkProcessBean workProcess = new WorkProcessBean();
                    // 办理环节
                    workProcess.setTaskName(taskInstance.getName());
                    workProcess.setTaskId(taskInstance.getId());
                    workProcess.setTaskInstUuid(taskInstance.getUuid());
                    // 办理人
                    workProcess.setAssignee(resolveUserIdAsName(taskOperation.getAssignee(), operatorIdNameMap));
                    workProcess.setAssigneeId(taskOperation.getAssignee());
                    workProcess.setIdentityNamePath(resolveUserIdentityNamePath(taskInstance, taskOperation.getAssignee(), taskOperation.getOperatorIdentityId(), taskOperation.getOperatorIdentityNamePath(), userJobsMap, workFlowSettings));
//                    workProcess.setDeptName(resolveUserIdAsDeptName(taskOperation.getAssignee()));
//                    workProcess.setMainJobName(resolveUserIdAsJobName(taskOperation.getAssignee()));
                    // 办理意见
                    workProcess.setOpinion(taskOperation.getOpinionText());
                    // 意见立场值
                    workProcess.setOpinionValue(taskOperation.getOpinionValue());
                    // 意见立场名称
                    workProcess.setOpinionLabel(taskOperation.getOpinionLabel());
                    // 意见立场附件
                    workProcess.setOpinionFiles(getOpinionFiles(opinionFileMap, taskOperation));
                    // 送办对象
                    workProcess.setToUser(resolveUserIdAsName(taskOperation.getUserId(), operatorIdNameMap));
                    // 送阅对象
                    String copyUserId = taskOperation.getCopyUserId();
                    String preTaskInstUuid = taskOperation.getTaskInstUuid();
                    workProcess.setCopyUser(resolveUserIdAsName(copyUserId, operatorIdNameMap));
                    if (StringUtils.isNotBlank(copyUserId) && taskActivityMap.get(preTaskInstUuid) != null) {
                        String[] copyUserIds = copyUserId.split(Separator.SEMICOLON.getValue());
                        StringBuilder readStatus = new StringBuilder();
                        for (String userId : copyUserIds) {
                            ReadMarker readMarker = readMarkerService
                                    .get(taskActivityMap.get(preTaskInstUuid).getTaskInstUuid(), userId);
                            if (readMarker != null) {
                                // 加入回车换行符
                                if (readStatus.length() != 0) {
                                    readStatus.append("<br>");
                                }
                                readStatus.append(resolveUserIdAsName(userId, operatorIdNameMap) + "(" + readMarker.getReadTime() + ")");
                            }
                        }
                        // 阅读情况
                        workProcess.setReadStatus(readStatus.toString());
                    }
                    // 挂起状态(0正常、1挂起、2结束)，办结时跳转
                    if (WorkFlowOperation.GOTO_TASK.equals(taskOperation.getActionType())) {
                        workProcess.setSuspensionState(getTaskSuspensionState(taskOperation));
                        workProcess.setGotoTaskId(getGotoTaskId(taskOperation));
                    }
                    // 状态
                    if (WorkFlowOperation.GOTO_TASK.equals(taskOperation.getActionType())
                            && isGotoEndFlow(taskOperation)) {
                        workProcess.setStatus("办结");
                    } else {
                        workProcess.setStatus("完成");
                    }
                    // 动作类型
                    workProcess.setActionType(taskOperation.getActionType());
                    // 动作名称
                    String action = taskOperation.getAction();
                    workProcess.setActionName(
                            StringUtils.isBlank(action) ? WorkFlowOperation.getName(taskOperation.getActionType())
                                    : action);
                    // 动作代码
                    workProcess.setActionCode(taskOperation.getActionCode());
                    // 提交时间(任务到达时间)
                    if (WorkFlowOperation.TRANSFER.equals(taskOperation.getActionType())
                            || WorkFlowOperation.COUNTER_SIGN.equals(taskOperation.getActionType())) {
                        // 已加入办理过程设置转办、会签的时间为对应的操作时间
                        if (taskInstUuidMap.containsKey(taskInstUuid)) {
                            workProcess.setSubmitTime(taskOperation.getCreateTime());
                        } else {
                            workProcess.setSubmitTime(taskInstance.getCreateTime());
                        }
                    } else if (WorkFlowOperation.TRANSFER_SUBMIT.equals(taskOperation.getActionType())
                            || WorkFlowOperation.CANCEL.equals(taskOperation.getActionType())) {
                        if (StringUtils.isNotBlank(taskOperation.getTaskIdentityUuid())) {
                            workProcess.setSubmitTime(
                                    identityService.get(taskOperation.getTaskIdentityUuid()).getCreateTime());
                        } else {
                            workProcess.setSubmitTime(taskInstance.getCreateTime());
                        }
                    } else {
                        workProcess.setSubmitTime(taskInstance.getCreateTime());
                    }
                    // 标记已加入办理过程
                    taskInstUuidMap.put(taskInstUuid, taskInstUuid);
                    // 完成时间
                    workProcess.setEndTime(taskOperation.getCreateTime());
                    // 是否移动端应用的操作
                    workProcess.setIsMobileApp(taskOperation.getIsMobileApp());
                    // 是否撤回
                    workProcess.setCanceled(cancelOperationMap.containsKey(taskOperation.getUuid()));
                    // 是否补审补办
                    workProcess.setSupplemented(CollectionUtils.isNotEmpty(supplementTaskUuids) && supplementTaskUuids.contains(taskInstance.getUuid()));

                    workProcessBeans.add(workProcess);
                }
            }
            // 未完成处理
            if (taskInstance.getEndTime() == null) {
                unfinishedTaskInstances.add(taskInstance);
            }
        }

        // 未完成的任务
        for (TaskInstance unfinishedTaskInstance : unfinishedTaskInstances) {
            List<AclTaskEntry> aclSids = aclTaskService.getSid(unfinishedTaskInstance.getUuid(), AclPermission.TODO);
            Set<String> sids = Sets.newLinkedHashSet();
            for (AclTaskEntry aclSid : aclSids) {
                sids.add(aclSid.getSid());
            }
            List<String> assigneeNames = getTaskAssigneeNames(unfinishedTaskInstance, Lists.newArrayList(sids));
            WorkProcessBean workProcess = new WorkProcessBean();
            // 待办人包含委托人
            List<TaskDelegation> taskDelegations = taskDelegationService
                    .listRunningTaskDelegationByTaskInstUuid(unfinishedTaskInstance.getUuid());
            if (CollectionUtils.isNotEmpty(taskDelegations)) {
                List<TaskIdentity> todoIdentities = identityService.getTodoByTaskInstUuid(unfinishedTaskInstance.getUuid());
                for (TaskDelegation taskDelegation : taskDelegations) {
                    if (!sids.contains(taskDelegation.getTrustee())) {
                        continue;
                    }
                    String consignorName = taskDelegation.getConsignorName();
                    String trusteeName = taskDelegation.getTrusteeName();
                    if (StringUtils.isBlank(consignorName)) {
                        consignorName = resolveUserIdAsName(taskDelegation.getConsignor());
                    }
                    if (StringUtils.isBlank(trusteeName) || !LocaleContextHolder.getLocale().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                        trusteeName = resolveUserIdAsName(taskDelegation.getTrustee());
                    }
                    assigneeNames.remove(consignorName);
                    if (!LocaleContextHolder.getLocale().equals(Locale.SIMPLIFIED_CHINESE.toString()) && StringUtils.isNotBlank(taskDelegation.getConsignor())) {
                        consignorName = resolveUserIdAsName(taskDelegation.getConsignor());
                    }
                    // 委托人有自己的待办时，不删除委托人名称
                    boolean trusteeHasTodo = todoIdentities.stream().filter(identity -> StringUtils.equals(identity.getUserId(), taskDelegation.getTrustee())
                            && !StringUtils.equals(identity.getUuid(), taskDelegation.getTaskIdentityUuid())).findFirst().isPresent();
                    if (!trusteeHasTodo) {
                        assigneeNames.remove(trusteeName);
                    }
                    String entrustToLabel = AppCodeI18nMessageSource.getMessage("WorkflowWork.taskAction.entrustTo", "pt-workflow", LocaleContextHolder.getLocale().toString(), "委托给");
                    assigneeNames.add(consignorName + "（" + entrustToLabel + " " + trusteeName + "）");
                }
            }
            // 办理人
            workProcess.setAssignee(StringUtils.join(assigneeNames, Separator.SEMICOLON.getValue()));
            workProcess.setAssigneeId(StringUtils.join(sids, Separator.SEMICOLON.getValue()));
            workProcess.setDecisionMakerName(getDecisionMakerName(unfinishedTaskInstance, flowInstUuid));
            // workProcess.setDeptName(resolveUserIdAsDeptName(StringUtils.join(sids, Separator.SEMICOLON.getValue())));
            // workProcess.setMainJobName(resolveUserIdAsJobName(StringUtils.join(sids, Separator.SEMICOLON.getValue())));
            // 办理环节
            workProcess.setTaskName(unfinishedTaskInstance.getName());
            workProcess.setTaskId(unfinishedTaskInstance.getId());
            workProcess.setTaskInstUuid(unfinishedTaskInstance.getUuid());
            // 状态
            workProcess.setStatus("未完成");
            // 提交时间
            workProcess.setSubmitTime(unfinishedTaskInstance.getCreateTime());
            // 是否补审补办
            workProcess.setSupplemented(CollectionUtils.isNotEmpty(supplementTaskUuids) && supplementTaskUuids.contains(unfinishedTaskInstance.getUuid()));

            workProcessBeans.add(workProcess);
        }
        if (unfinishedTaskInstances.isEmpty() && workProcessBeans.size() > 1) {
            workProcessBeans.get(workProcessBeans.size() - 1).setStatus("办结");
        }
        return workProcessBeans;
    }

    /**
     * @param operationMap
     * @return
     */
    private Map<String, String> getOperatorIdNameMap(Map<String, List<TaskOperation>> operationMap) {
        Set<String> operatorIds = operationMap.values().stream().flatMap(operations -> {
            List<String> userIds = Lists.newArrayList();
            operations.forEach(operation -> {
                if (StringUtils.isNotBlank(operation.getAssignee())) {
                    userIds.addAll(Arrays.asList(operation.getAssignee().split(Separator.SEMICOLON.getValue())));
                }
                if (StringUtils.isNotBlank(operation.getUserId())) {
                    userIds.addAll(Arrays.asList(operation.getUserId().split(Separator.SEMICOLON.getValue())));
                }
                if (StringUtils.isNotBlank(operation.getCopyUserId())) {
                    userIds.addAll(Arrays.asList(operation.getCopyUserId().split(Separator.SEMICOLON.getValue())));
                }
            });
            return userIds.stream();
        }).collect(Collectors.toSet());
        return workflowOrgService.getNamesByIds(Lists.newArrayList(operatorIds));
    }

    /**
     * @param taskInstance
     * @param flowInstUuid
     * @return
     */
    private String getDecisionMakerName(TaskInstance taskInstance, String flowInstUuid) {
        if (TaskNodeType.CollaborationTask.getValueAsInt().equals(taskInstance.getType())) {
            // 环节决策人
            String decisionMakerName = "decisionMakers_" + taskInstance.getId() + "_" + taskInstance.getUuid();
            FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstUuid, decisionMakerName);
            if (parameter != null && StringUtils.isNotBlank(parameter.getValue())) {
                Map<String, String> userMap = null;
                if (StringUtils.startsWith(parameter.getValue(), "{")) {
                    Map<String, String> superviseIdMap = JsonUtils.json2Object(parameter.getValue(), Map.class);
                    userMap = workflowOrgService.getNamesByIds(Lists.newArrayList(superviseIdMap.keySet()));
                } else {
                    userMap = workflowOrgService.getNamesByIds(Arrays.asList(StringUtils.split(parameter.getValue(), Separator.SEMICOLON.getValue())));
                }
                return StringUtils.join(userMap.values(), Separator.SEMICOLON.getValue());
            }
        }
        return null;
    }

    /**
     * @param unfinishedTaskInstance
     * @return
     */
    private List<String> getTaskAssigneeNames(TaskInstance unfinishedTaskInstance, List<String> sids) {
        List<String> assigneeNames = Lists.newArrayList();
        Map<String, String> userMap = workflowOrgService.getNamesByIds(sids);
        for (Entry<String, String> entry : userMap.entrySet()) {
            if (StringUtils.startsWith(entry.getKey(), IdPrefix.USER.getValue())) {
                assigneeNames.add(entry.getValue());
            } else {
                String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(new Token(unfinishedTaskInstance, new TaskData()));
                Map<String, String> orgUserMap = workflowOrgService.getUsersByIds(Lists.newArrayList(entry.getKey()), orgVersionIds);
                List<String> doneMarkerUserIds = aclTaskService.listSidDoneMarkerUserId(entry.getKey(), unfinishedTaskInstance.getUuid());
                if (MapUtils.isNotEmpty(orgUserMap) && CollectionUtils.isNotEmpty(doneMarkerUserIds)) {
                    doneMarkerUserIds.forEach(doneUserId -> orgUserMap.remove(doneUserId));
                }
                String assigneeName = workflowOrgService.getNameById(entry.getKey());
                if (MapUtils.isNotEmpty(orgUserMap)) {
                    assigneeName += "(" + StringUtils.join(orgUserMap.values(), Separator.COMMA.getValue()) + ")";
                }
                assigneeNames.add(assigneeName);
            }
        }
        return assigneeNames;
    }

    /**
     * @param opinionFileMap
     * @param taskOperation
     * @return
     */
    private static List<LogicFileInfo> getOpinionFiles(Map<String, List<LogicFileInfo>> opinionFileMap, TaskOperation taskOperation) {
        List<LogicFileInfo> logicFileInfos = opinionFileMap.get(taskOperation.getUuid());
        if (CollectionUtils.isEmpty(logicFileInfos) || CollectionUtils.size(logicFileInfos) <= 1
                || StringUtils.isBlank(taskOperation.getOpinionFileIds())) {
            return logicFileInfos;
        }
        List<String> fileIds = Arrays.asList(StringUtils.split(taskOperation.getOpinionFileIds(), Separator.SEMICOLON.getValue()));
        Collections.sort(logicFileInfos, Comparator.comparingInt(file -> fileIds.indexOf(file.getFileID())));
        return logicFileInfos;
    }

    /**
     * @param taskOperation
     * @return
     */
    @SuppressWarnings({"unchecked"})
    private Integer getTaskSuspensionState(TaskOperation taskOperation) {
        String extraInfo = taskOperation.getExtraInfo();
        int normalState = SuspensionState.NORMAL.getState();
        if (StringUtils.isBlank(extraInfo)) {
            return normalState;
        }
        try {
            Collection<TaskIdentity> identities = JsonUtils.toCollection(extraInfo, TaskIdentity.class);
            if (CollectionUtils.isEmpty(identities)) {
                return normalState;
            }
            Integer overState = SuspensionState.DELETED.getState();
            for (TaskIdentity taskIdentity : identities) {
                if (!overState.equals(taskIdentity.getSuspensionState())) {
                    return normalState;
                }
            }
            return overState;
        } catch (Exception e) {
        }
        return SuspensionState.NORMAL.getState();
    }

    /**
     * @param taskOperation
     * @return
     */
    private String getGotoTaskId(TaskOperation taskOperation) {
        String extraInfo = taskOperation.getExtraInfo();
        String gotoTaskId = StringUtils.EMPTY;
        if (StringUtils.isBlank(extraInfo)) {
            return gotoTaskId;
        }
        try {
            Collection<TaskIdentity> identities = JsonUtils.toCollection(extraInfo, TaskIdentity.class);
            if (CollectionUtils.isEmpty(identities)) {
                return gotoTaskId;
            }
            for (TaskIdentity taskIdentity : identities) {
                if (StringUtils.isNotBlank(taskIdentity.getGotoTaskId())) {
                    gotoTaskId = taskIdentity.getGotoTaskId();
                    break;
                }
            }
            return gotoTaskId;
        } catch (Exception e) {
        }
        return gotoTaskId;
    }

    /**
     * @param taskOperation
     * @return
     */
    @SuppressWarnings({"unchecked"})
    private boolean isGotoEndFlow(TaskOperation taskOperation) {
        String extraInfo = taskOperation.getExtraInfo();
        if (StringUtils.isBlank(extraInfo)) {
            return false;
        }
        try {
            Collection<TaskIdentity> identities = JsonUtils.toCollection(extraInfo, TaskIdentity.class);
            if (CollectionUtils.isEmpty(identities)) {
                return false;
            }
            for (TaskIdentity taskIdentity : identities) {
                if (StringUtils.equals(FlowDelegate.END_FLOW_ID, taskIdentity.getGotoTaskId())) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 重置撤回的办理意见为（办理意见已撤回）
     *
     * @param taskActivities
     * @param operationMap
     */
    private Map<String, TaskOperation> resetCancelOperationOpinion(List<TaskActivity> taskActivities,
                                                                   Map<String, List<TaskOperation>> operationMap) {
        Map<String, TaskOperation> cancelOperationMap = Maps.newHashMap();
        // 1、流转操作
        Map<String, TaskActivity> taskActivityMap = ConvertUtils.convertElementToMap(taskActivities, "taskInstUuid");
        for (TaskActivity taskActivity : taskActivities) {
            if (!TransferCode.Cancel.getCode().equals(taskActivity.getTransferCode())) {
                continue;
            }
            // 确保前一环节的流转代码为提交
            TaskActivity preTaskActivity = taskActivityMap.get(taskActivity.getPreTaskInstUuid());
            if (preTaskActivity == null) {
                continue;
            }
            // 确保前一环节的流转代码不是环节跳过
            TaskActivity skipTaskActity = taskActivityMap.get(preTaskActivity.getPreTaskInstUuid());
            while (skipTaskActity != null && TransferCode.SkipTask.getCode().equals(skipTaskActity.getTransferCode())) {
                skipTaskActity = taskActivityMap.get(skipTaskActity.getPreTaskInstUuid());
            }

            List<TaskOperation> operations = null;
            if (skipTaskActity != null) {
                operations = operationMap.get(skipTaskActity.getTaskInstUuid());
            } else {
                operations = operationMap.get(preTaskActivity.getPreTaskInstUuid());
            }

            if (CollectionUtils.isEmpty(operations)) {
                continue;
            }
            TaskOperation taskOperation = filterTaskOperationByCreator(operations, taskActivity.getCreator());
            if (taskOperation != null) {
                if (StringUtils.isNotBlank(taskOperation.getOpinionText())) {
                    taskOperation.setOpinionText("（办理意见已撤回）");
                }
                cancelOperationMap.put(taskOperation.getUuid(), taskOperation);
            }
        }
        // 2、同一环节操作
        for (Entry<String, List<TaskOperation>> entry : operationMap.entrySet()) {
            List<TaskOperation> taskOperations = Lists.newArrayList(entry.getValue());
            Collections.sort(taskOperations, IdEntityComparators.CREATE_TIME_DESC);
            Map<String, List<TaskOperation>> userTaskOperationMap = ListUtils.list2group(taskOperations,
                    IdEntity.CREATOR);
            for (Entry<String, List<TaskOperation>> userTaskOperationEntry : userTaskOperationMap.entrySet()) {
                Iterator<TaskOperation> it = userTaskOperationEntry.getValue().iterator();
                boolean hasCancel = false;
                while (it.hasNext()) {
                    TaskOperation taskOperation = it.next();
                    if (hasCancel && !(ActionCode.AUTO_SUBMIT.getCode().equals(taskOperation.getActionCode())
                            || ActionCode.SKIP_SUBMIT.getCode().equals(taskOperation.getActionCode()))) {
                        if (StringUtils.isNotBlank(taskOperation.getOpinionText())) {
                            taskOperation.setOpinionText("（办理意见已撤回）");
                        }
                        cancelOperationMap.put(taskOperation.getUuid(), taskOperation);
                        break;
                    }
                    // 撤回操作
                    if (ActionCode.CANCEL.getCode().equals(taskOperation.getActionCode())) {
                        hasCancel = true;
                    }
                }
            }
        }
        return cancelOperationMap;
    }

    /**
     * @param operations
     * @param creator
     */
    private TaskOperation filterTaskOperationByCreator(List<TaskOperation> operations, String creator) {
        List<TaskOperation> tmpOperations = Lists.newArrayList(operations);
        Collections.sort(tmpOperations, IdEntityComparators.CREATE_TIME_DESC);
        for (TaskOperation taskOperation : tmpOperations) {
            if ((ActionCode.SKIP_SUBMIT.getCode().equals(taskOperation.getActionCode())
                    || ActionCode.AUTO_SUBMIT.getCode().equals(taskOperation.getActionCode())
                    && StringUtils.equals(taskOperation.getCreator(), creator))) {
                continue;
            }
            if (StringUtils.equals(taskOperation.getCreator(), creator)) {
                return taskOperation;
            }
        }
        return null;
    }

    public String resolveUserIdAsName(String userId, Map<String, String> userNameMap) {
        if (StringUtils.isBlank(userId)) {
            return StringUtils.EMPTY;
        }
        String[] userIds = userId.split(Separator.SEMICOLON.getValue());
        List<String> names = Lists.newArrayList();
        for (String id : userIds) {
            names.add(userNameMap.get(id));
        }
        return StringUtils.join(names, Separator.SEMICOLON.getValue());
    }

    public String resolveUserIdAsName(String userId) {
        if (StringUtils.isBlank(userId)) {
            return StringUtils.EMPTY;
        }
        String[] userIds = userId.split(Separator.SEMICOLON.getValue());
        return IdentityResolverStrategy.resolveAsNames(Lists.newArrayList(userIds));
        // StringBuilder username = new StringBuilder();
        // for (int index = 0; index < userIds.length; index++) {
        // username.append(IdentityResolverStrategy.resolveAsName(userIds[index]));
        // if (index != userIds.length - 1) {
        // username.append(Separator.SEMICOLON.getValue());
        // }
        // // 回车换行
        // if (index > 0 && index % 2 == 0) {
        // username.append(Separator.LINE.getValue());
        // }
        // }
        // return username.toString();
    }

    public String resolveUserIdAsDeptName(String userId) {
        String departmentName = StringUtils.EMPTY;
        if (StringUtils.isBlank(userId) || userId.contains(Separator.SEMICOLON.getValue())
                || !StringUtils.startsWith(userId, IdPrefix.USER.getValue())) {
            return departmentName;
        }
//        OrgUserVo user = orgApiFacade.getUserVoById(userId);
//        if (user == null || StringUtils.isBlank(user.getMainJobIdPath())) {
//            return "";
//        }
//        String jobPath = user.getMainJobIdPath();
//        String deptId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
//        MultiOrgElement dept = orgApiFacade.getOrgElementById(deptId);
//        if (user == null || dept == null) {
//            return "";
//        }
//        String departmentName = dept.getName();
//        if (departmentName.contains("/")) {
//            return departmentName.substring(departmentName.lastIndexOf("/") + 1);
//        }
        List<OrgUserJobDto> userJobDtos = workflowOrgService.listUserJobs(userId);
        if (CollectionUtils.isEmpty(userJobDtos)) {
            return departmentName;
        }
        OrgUserJobDto userJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst().orElse(userJobDtos.get(0));
        List<String> idPaths = Arrays.asList(StringUtils.split(userJob.getJobIdPath(), Separator.SLASH.getValue()));
        List<String> namePaths = Arrays.asList(StringUtils.split(userJob.getJobNamePath(), Separator.SLASH.getValue()));
        if (CollectionUtils.size(idPaths) != CollectionUtils.size(namePaths)) {
            return departmentName;
        }
        for (int index = idPaths.size() - 1; index >= 0; index--) {
            String deptId = idPaths.get(index);
            if (StringUtils.startsWith(deptId, IdPrefix.DEPARTMENT.getValue())) {
                return namePaths.get(index);
            }
        }
        return departmentName;
    }

    public String resolveUserIdAsJobName(String userId) {
        String majorJobName = StringUtils.EMPTY;
        if (StringUtils.isBlank(userId) || userId.contains(Separator.SEMICOLON.getValue())
                || !StringUtils.startsWith(userId, IdPrefix.USER.getValue())) {
            return majorJobName;
        }
//        OrgUserVo user = orgApiFacade.getUserVoById(userId);
//        if (user == null || user.getMainJobName() == null) {
//            return "";
//        }
//        String majorJobName = user.getMainJobName();
//        if (majorJobName.contains("/")) {
//            return majorJobName.substring(majorJobName.lastIndexOf("/") + 1);
//        }
        List<OrgUserJobDto> jobDtos = workflowOrgService.listUserJobs(userId);
        if (CollectionUtils.isEmpty(jobDtos)) {
            return majorJobName;
        }
        OrgUserJobDto majorJob = jobDtos.stream().filter(job -> job.isPrimary()).findFirst().orElse(null);
        if (majorJob != null) {
            majorJobName = majorJob.getJobName();
        }
        return majorJobName;
    }

    /**
     * @param userId
     * @param operatorIdentityId
     * @param userJobsMap
     * @param workFlowSettings
     * @return
     */
    private String resolveUserIdentityNamePath(TaskInstance taskInstance, String userId, String operatorIdentityId, String operatorIdentityNamePath,
                                               Map<String, List<OrgUserJobDto>> userJobsMap, WorkFlowSettings workFlowSettings) {
        String majorJobName = StringUtils.EMPTY;
        if (StringUtils.isBlank(userId) || userId.contains(Separator.SEMICOLON.getValue())
                || !StringUtils.startsWith(userId, IdPrefix.USER.getValue())) {
            return majorJobName;
        }

        if (StringUtils.isNotBlank(operatorIdentityNamePath) && !workFlowSettings.isShowOperatorPrimaryIdentity()) {
            String[] names = operatorIdentityNamePath.split(Separator.SEMICOLON.getValue());
            String[] ids = operatorIdentityId.split(Separator.SEMICOLON.getValue());
            Set<String> identityIds = Sets.newHashSet(operatorIdentityId.split(Separator.SEMICOLON.getValue()));
            Set<String> idSet = Sets.newHashSet();
            for (String i : identityIds) {
                idSet.addAll(Arrays.asList(i.split(Separator.SLASH.getValue())));
            }
            Map<String, String> eleNames = orgFacadeService.getNamePathByOrgEleIds(Arrays.asList(ids));
            for (int i = 0, len = ids.length; i < len; i++) {
                if (eleNames.containsKey(ids[i])) {
                    names[i] = eleNames.get(ids[i]);
                }
            }
            return StringUtils.join(names, Separator.SEMICOLON.getValue());

//            return operatorIdentityNamePath;
        }

        List<String> identityIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(operatorIdentityId)) {
            identityIds.addAll(Arrays.asList(StringUtils.split(operatorIdentityId, Separator.SEMICOLON.getValue())));
        }

        List<OrgUserJobDto> jobDtos = userJobsMap.get(userId);
        if (jobDtos == null) {
            jobDtos = workFlowSettings.isShowOperatorPrimaryIdentity() ? workflowOrgService.listUserJobIdentity(userId, false)
                    : workflowOrgService.listUserJobIdentity(userId, new Token(taskInstance.getFlowInstance(), new TaskData()));
            userJobsMap.put(userId, jobDtos);
        }

        List<OrgUserJobDto> jobIdentityDtos = Lists.newArrayList();
        if (workFlowSettings.isShowOperatorPrimaryIdentity()) {
            OrgUserJobDto orgUserJobDto = jobDtos.stream().filter(jobDto -> jobDto.isPrimary()).findFirst().orElse(null);
            if (orgUserJobDto == null) {
                if (CollectionUtils.isNotEmpty(jobDtos)) {
                    jobIdentityDtos.add(jobDtos.get(0));
                }
            } else {
                jobIdentityDtos.add(orgUserJobDto);
            }
        } else if (StringUtils.isBlank(operatorIdentityId)) {
            jobIdentityDtos.addAll(jobDtos);
        } else {
            jobIdentityDtos.addAll(jobDtos.stream().filter(jobDto -> identityIds.contains(jobDto.getJobId()))
                    .collect(Collectors.toList()));
            if (CollectionUtils.isEmpty(jobIdentityDtos) && CollectionUtils.isNotEmpty(jobDtos)) {
                jobIdentityDtos.add(jobDtos.get(0));
            }
        }

        return jobIdentityDtos.stream().

                map(dto -> dto.getJobNamePath()).

                collect(Collectors.joining(Separator.SEMICOLON.getValue()));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getWorkProcesses(java.util.Collection)
     */
    @Override
    public Map<String, List<WorkProcessBean>> getWorkProcesses(Collection<String> flowInstUuids) {
        Map<String, List<WorkProcessBean>> processMap = new HashMap<String, List<WorkProcessBean>>();
        for (String flowInstUuid : flowInstUuids) {
            processMap.put(flowInstUuid, getWorkProcess(flowInstUuid));
        }
        return processMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getBranchTaskProcesses(java.util.Collection)
     */
    @Override
    public Map<String, List<WorkProcessBean>> getBranchTaskProcesses(Collection<String> taskInstUuids) {
        if (CollectionUtils.isEmpty(taskInstUuids)) {
            return Collections.emptyMap();
        }

        List<TaskBranch> taskBranches = taskBranchService.getByCurrentTaskInstUuids(taskInstUuids);
        ImmutableListMultimap<String, TaskBranch> taskBranchGroups = Multimaps.index(taskBranches.iterator(),
                new Function<TaskBranch, String>() {

                    @Override
                    public String apply(TaskBranch input) {
                        return input.getFlowInstUuid();
                    }
                });
        ImmutableMap<String, Collection<TaskBranch>> immutableMap = taskBranchGroups.asMap();
        Map<String, List<WorkProcessBean>> processMap = Maps.newHashMap();
        for (Entry<String, Collection<TaskBranch>> entry : immutableMap.entrySet()) {
            String flowInstUuid = entry.getKey();
            Collection<TaskBranch> branchTasks = entry.getValue();
            processMap.putAll(getBranchTaskProcess(branchTasks, flowInstUuid));
        }
        return processMap;
    }

    /**
     * @param branchTasks
     * @param flowInstUuid
     * @return
     */
    private Map<String, List<WorkProcessBean>> getBranchTaskProcess(Collection<TaskBranch> branchTasks,
                                                                    String flowInstUuid) {
        Map<String, List<WorkProcessBean>> branchTaskProcessMap = Maps.newHashMap();
        List<TaskActivity> taskActivities = taskService.getTaskActivities(flowInstUuid);
        Map<String, List<TaskOperation>> operationMap = taskService.getOperationAsMap(flowInstUuid);
        List<LogicFileInfo> opinionFiles = mongoFileService.getNonioFilesFromFolder(flowInstUuid, null);
        for (TaskBranch branchTask : branchTasks) {
            List<TaskActivity> branchTaskActivity = taskBranchService.filterBranchTaskActivity(branchTask,
                    taskActivities);
            branchTaskProcessMap.put(branchTask.getCurrentTaskInstUuid(),
                    extractWorkProcess(flowInstUuid, branchTaskActivity, operationMap, opinionFiles));
        }
        return branchTaskProcessMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getTimeline(java.lang.String)
     */
    @Override
    public WorkTimelineBean getTimeline(String flowInstUuid) {
        WorkTimelineBean timeline = new WorkTimelineBean();
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        timeline.setFlowName(flowInstance.getName());
        timeline.setFlowId(flowInstance.getId());
        timeline.setTitle(flowInstance.getTitle());
        Set<TaskInstance> taskInstances = flowInstance.getTaskInstances();

        // 所有计时器
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            // 设置计时器信息
            Map<String, String> timer = new HashMap<String, String>();
            timer.put("uuid", taskTimer.getUuid());
            timer.put("name", taskTimer.getName());
            timeline.getTimers().add(timer);

            String uuid = taskTimer.getUuid();
            // 开始时间
            TimelineItem timelineItem = new TimelineItem();
            timelineItem.setId(uuid + "_start");
            timelineItem.setType(TimelineItem.TYPE_TIMER);
            timelineItem.setTime(taskTimer.getCreateTime());
            timelineItem.setContent("计时器[" + taskTimer.getName() + "]开始计时");
            timeline.getItems().add(timelineItem);

            // 预警时间
            if (taskTimer.getTaskAlarmTime() != null) {
                timelineItem = new TimelineItem();
                timelineItem.setId(uuid + "_alarm");
                timelineItem.setType(TimelineItem.TYPE_TIMER);
                timelineItem.setTime(taskTimer.getTaskAlarmTime());
                timelineItem.setContent("计时器[" + taskTimer.getName() + "]预警时间");
                timeline.getItems().add(timelineItem);
            }

            // 到期时间
            timelineItem = new TimelineItem();
            timelineItem.setId(uuid + "_due");
            timelineItem.setType(TimelineItem.TYPE_TIMER);
            timelineItem.setTime(taskTimer.getTaskDueTime());
            timelineItem.setContent("计时器[" + taskTimer.getName() + "]到期时间");
            timeline.getItems().add(timelineItem);
        }

        // 1、流程启动
        addTimelineItem(timeline, flowInstance.getStartTime(), "流程[" + flowInstance.getName() + "]启动");

        for (TaskInstance taskInstance : taskInstances) {
            // 2、环节开始
            addTimelineItem(timeline, taskInstance.getStartTime(), "环节" + taskInstance.getName() + "开始");

            // 3、计时日志
            TaskTimerLog example = new TaskTimerLog();
            example.setFlowInstUuid(flowInstance.getUuid());
            example.setTaskInstUuid(taskInstance.getUuid());
            List<TaskTimerLog> taskTimerLogs = taskTimerLogService.findByExample(example, "logTime asc");
            for (TaskTimerLog taskTimerLog : taskTimerLogs) {
                String taskTimerUuid = taskTimerLog.getTaskTimerUuid();
                if (TaskTimerLog.TYPE_START.equals(taskTimerLog.getType())) {
                    // 计时器开始计时
                    String content = "计时器[" + taskTimerService.getOne(taskTimerUuid).getName() + "]开始计时";
                    addTimelineItem(timeline, taskTimerLog.getLogTime(), content);
                } else if (TaskTimerLog.TYPE_ALARM.equals(taskTimerLog.getType())) {
                    // 计时器预警提醒
                    String content = "计时器[" + taskTimerService.getOne(taskTimerUuid).getName() + "]预警提醒开始";
                    addTimelineItem(timeline, taskTimerLog.getLogTime(), content);
                } else if (TaskTimerLog.TYPE_DUE_DOING.equals(taskTimerLog.getType())) {
                    // 计时器逾期处理
                    String content = "计时器[" + taskTimerService.getOne(taskTimerUuid).getName() + "]逾期处理开始";
                    addTimelineItem(timeline, taskTimerLog.getLogTime(), content);
                }
            }

            // 4、环节结束
            if (taskInstance.getEndTime() != null) {
                addTimelineItem(timeline, taskInstance.getEndTime(), "环节" + taskInstance.getName() + "结束");

                for (TaskTimerLog taskTimerLog : taskTimerLogs) {
                    // 计时器暂停计时
                    if (TaskTimerLog.TYPE_PAUSE.equals(taskTimerLog.getType())) {
                        String taskTimerUuid = taskTimerLog.getTaskTimerUuid();

                        String content = "计时器[" + taskTimerService.getOne(taskTimerUuid).getName() + "]暂停计时";
                        addTimelineItem(timeline, taskTimerLog.getLogTime(), content);
                    }
                }
            } else {
                // 5、环节待执行
                addTimelineItem(timeline, Calendar.getInstance().getTime(), "环节" + taskInstance.getName() + "待执行");
            }
        }

        // 6、流程结束
        if (flowInstance.getEndTime() != null) {
            addTimelineItem(timeline, flowInstance.getEndTime(), "流程[" + flowInstance.getName() + "]结束");
        }

        // 排序
        Collections.sort(timeline.getItems());

        // 输出时间轴
        printTimeline(timeline);

        return timeline;
    }

    /**
     * 如何描述该方法
     *
     * @param timeline
     */
    private void printTimeline(WorkTimelineBean timeline) {
        System.out.println("时间轴 " + timeline.getTitle());
        for (TimelineItem item : timeline.getItems()) {
            System.out.println(DateUtils.formatDateTime(item.getTime()) + " " + item.getContent());
        }
    }

    /**
     * 如何描述该方法
     *
     * @param timeline
     * @param flowInstance
     */
    private void addTimelineItem(WorkTimelineBean timeline, Date time, String content) {
        TimelineItem item = new TimelineItem();
        item.setTime(time);
        item.setContent(content);
        timeline.getItems().add(item);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#rollback(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public ResultMessage rollback(WorkBean workBean) {
        return rollbackWithWorkData(workBean);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#rollbackWithWorkData(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public ResultMessage rollbackWithWorkData(WorkBean workBean) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        // 退回前服务处理
        String beforeRollbackService = workBean.getBeforeRollbackService();
        if (StringUtils.isNotBlank(beforeRollbackService)) {
            ServiceInvokeUtils.invoke(beforeRollbackService, new Class[]{WorkBean.class}, workBean);
            taskInstanceService.flushSession();
            taskInstanceService.clearSession();
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();

        String taskInstUuid = workBean.getTaskInstUuid();
        String dataUuid = workBean.getDataUuid();
        // 可选择的退回环节ID
        String candidateRollbackToTaskId = StringUtils.EMPTY;
        // 自定义退回的按钮
        CustomDynamicButton customDynamicButton = workBean.getCustomDynamicButton();
        if (customDynamicButton != null && StringUtils.isNotBlank(customDynamicButton.getId())) {
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            CustomDynamicButton dynamicButton = flowDelegate.convertTaskCustomDynamicButton(taskInstance.getId(),
                    customDynamicButton);
            candidateRollbackToTaskId = dynamicButton.getTaskId();
        }

        // 设置任务数据
        // 创建提交的任务数据
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);

        // 可选择的退回环节ID
        taskData.setCustomData(taskInstUuid + "_candidateRollbackToTaskId", candidateRollbackToTaskId);
        // 要退回到的环节ID
        taskData.setRollbackToTaskId(taskInstUuid, workBean.getRollbackToTaskId());
        // 要退回到的环节实例UUID
        taskData.setRollbackToTaskInstUuid(taskInstUuid, workBean.getRollbackToTaskInstUuid());
        // 是否退回到前环节
        taskData.setRollbackToPreTask(taskInstUuid, workBean.isRollbackToPreTask());

        if (workBean.isRequiredSubmitPermission()) {
            preCheckTodo(user, taskInstUuid);
        }
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        taskService.rollback(taskInstUuid, taskData);
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), user.getUserId());
        // 返回提交结果
        ResultMessage msg = new ResultMessage();
        SubmitResult submitResult = taskData.getSubmitResult();
        msg.setData(submitResult);
        if (CollectionUtils.isNotEmpty(submitResult.getTaskInstUUids())) {
            deleteTempByTaskInstUuid(workBean.getTaskInstUuid());
        } else {
            deleteTemp(workBean.getTaskInstUuid(), user.getUserId());
        }
        return msg;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#rollbackToMainFlowWithWorkData(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public ResultMessage rollbackToMainFlowWithWorkData(WorkBean workBean) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        // 退回前服务处理
        String beforeRollbackService = workBean.getBeforeRollbackService();
        if (StringUtils.isNotBlank(beforeRollbackService)) {
            ServiceInvokeUtils.invoke(beforeRollbackService, new Class[]{WorkBean.class}, workBean);
            taskInstanceService.flushSession();
            taskInstanceService.clearSession();
        }

        UserDetails user = SpringSecurityUtils.getCurrentUser();

        String taskInstUuid = workBean.getTaskInstUuid();
        String dataUuid = workBean.getDataUuid();

        // 设置任务数据
        // 创建提交的任务数据
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);

        if (workBean.isRequiredSubmitPermission()) {
            preCheckTodo(user, taskInstUuid);
        }
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        taskService.rollbackToMainFlow(workBean.getTaskInstUuid(), taskData);
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), user.getUserId());
        deleteTempByTaskInstUuid(workBean.getTaskInstUuid());
        // 返回提交结果
        ResultMessage msg = new ResultMessage();
        SubmitResult submitResult = taskData.getSubmitResult();
        msg.setData(submitResult);
        return msg;
    }

    // /**
    // * (non-Javadoc)
    // * @see
    // com.wellsoft.pt.workflow.work.service.WorkService#directRollback(java.util.Collection)
    // */
    // @Override
    // public void directRollback(Collection<String> taskUuids) {
    // for (String taskUuid : taskUuids) {
    // taskService.rollback(taskUuid);
    // }
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#cancel(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void cancel(Collection<String> taskInstUuids) {
        for (String taskInstUuid : taskInstUuids) {
            taskService.cancel(taskInstUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#cancelWithOpinion(java.util.Collection, java.lang.String)
     */
    @Override
    @Transactional
    public void cancelWithOpinion(Collection<String> taskInstUuids, String opinionText) {
        for (String taskInstUuid : taskInstUuids) {
            taskService.cancel(taskInstUuid, opinionText);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#cancelWithWorkData(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void cancelWithWorkData(WorkBean workBean) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        UserDetails user = SpringSecurityUtils.getCurrentUser();

        String taskInstUuid = workBean.getTaskInstUuid();
        String dataUuid = workBean.getDataUuid();

        // 设置任务数据
        // 创建提交的任务数据
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        taskService.cancel(taskInstUuid, taskData);
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), user.getUserId());
        SubmitResult submitResult = taskData.getSubmitResult();
        if (CollectionUtils.isNotEmpty(submitResult.getTaskInstUUids())) {
            deleteTempByTaskInstUuid(workBean.getTaskInstUuid());
        } else {
            String taskIdentityUuid = workBean.getTaskIdentityUuid();
            if (StringUtils.isNotBlank(taskIdentityUuid)) {
                List<TaskIdentity> taskIdentities = identityService.listBySourceTaskIdentityUuid(taskIdentityUuid);
                taskIdentities.forEach(taskIdentity -> deleteTemp(taskIdentity.getTaskInstUuid(), taskIdentity.getUserId()));
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#cancelOver(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void cancelOver(Collection<String> flowInstUuids) {
        for (String flowInstUuid : flowInstUuids) {
            taskService.cancelOver(flowInstUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#transfer(java.util.Collection, java.util.List)
     */
    @Override
    @Transactional
    public void transfer(Collection<String> taskInstUuids, List<String> userIds, String opinionName,
                         String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        for (String taskInstUuid : taskInstUuids) {
            WorkBean workBean = getTodo(taskInstUuid, null);
            workBean = getWorkData(workBean);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.TRANSFER));
            workBean.setActionType(WorkFlowOperation.TRANSFER);
            TaskData taskData = createTaskData(workBean, user, taskInstUuid, workBean.getDataUuid());
            // 自动更新标题逻辑处理
            autoUpdateTitle(taskData, workBean);
            taskService.transfer(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);
            // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), user.getUserId());
            deleteTemp(workBean.getTaskInstUuid(), user.getUserId());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#transferWithWorkData(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void transferWithWorkData(WorkBean workBean, List<String> userIds) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        String taskInstUuid = workBean.getTaskInstUuid();
        String dataUuid = workBean.getDataUuid();
        String opinionName = workBean.getOpinionLabel();
        String opinionValue = workBean.getOpinionValue();
        String opinionText = workBean.getOpinionText();
        List<LogicFileInfo> opinionFiles = workBean.getOpinionFiles();
        String taskId = workBean.getTaskId();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        if (workBean.getTaskTransferUsers() != null && workBean.getTaskTransferUsers().containsKey(taskId)) {
            List<String> transferUserIds = workBean.getTaskTransferUsers().get(taskId);
            if (transferUserIds != null) {
                userIds.addAll(transferUserIds);
            }
        }
        taskService.transfer(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), user.getUserId());
        deleteTemp(workBean.getTaskInstUuid(), user.getUserId());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#counterSign(java.util.Collection, java.util.List)
     */
    @Override
    @Transactional
    public void counterSign(Collection<String> taskInstUuids, List<String> userIds, String opinionName,
                            String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        for (String taskInstUuid : taskInstUuids) {
            WorkBean workBean = getTodo(taskInstUuid, null);
            workBean = getWorkData(workBean);
            workBean.setAction(WorkFlowOperation.getName(WorkFlowOperation.COUNTER_SIGN));
            workBean.setActionType(WorkFlowOperation.COUNTER_SIGN);
            TaskData taskData = createTaskData(workBean, user, taskInstUuid, workBean.getDataUuid());
            // 自动更新标题逻辑处理
            autoUpdateTitle(taskData, workBean);
            taskService.counterSign(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);
            // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), userId);
            deleteTemp(workBean.getTaskInstUuid(), user.getUserId());
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#counterSignWithWorkData(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void counterSignWithWorkData(WorkBean workBean, List<String> userIds) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = workBean.getTaskInstUuid();
        String dataUuid = workBean.getDataUuid();
        String opinionName = workBean.getOpinionLabel();
        String opinionValue = workBean.getOpinionValue();
        String opinionText = workBean.getOpinionText();
        List<LogicFileInfo> opinionFiles = workBean.getOpinionFiles();
        String taskId = workBean.getTaskId();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        if (workBean.getTaskCounterSignUsers() != null && workBean.getTaskCounterSignUsers().containsKey(taskId)) {
            List<String> counterSignUserIds = workBean.getTaskCounterSignUsers().get(taskId);
            if (counterSignUserIds != null) {
                userIds.addAll(counterSignUserIds);
            }
        }
        taskService.counterSign(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), userId);
        deleteTemp(workBean.getTaskInstUuid(), user.getUserId());
    }

    /**
     * @param workBean
     * @param userIds
     */
    @Override
    @Transactional
    public void addSignWithWorkData(WorkBean workBean, List<String> userIds) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = workBean.getTaskInstUuid();
        String dataUuid = workBean.getDataUuid();
        String opinionName = workBean.getOpinionLabel();
        String opinionValue = workBean.getOpinionValue();
        String opinionText = workBean.getOpinionText();
        List<LogicFileInfo> opinionFiles = workBean.getOpinionFiles();
        String taskId = workBean.getTaskId();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        if (workBean.getTaskAddSignUsers() != null && workBean.getTaskAddSignUsers().containsKey(taskId)) {
            List<String> addSignUserIds = workBean.getTaskAddSignUsers().get(taskId);
            if (addSignUserIds != null) {
                userIds.addAll(addSignUserIds);
            }
        }
        taskService.addSign(userId, taskInstUuid, userIds, opinionName, opinionValue, opinionText, opinionFiles, taskData);

        deleteTemp(workBean.getTaskInstUuid(), user.getUserId());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#attention(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void attention(Collection<String> taskUuids) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskUuid : taskUuids) {
            taskService.attention(userId, taskUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getPrintType(java.lang.String)
     */
    @Override
    public String getPrintType(String printTemplateId) {
        String printType = "doc";
        PrintTemplate printTemplate = basicDataApiFacade.getPrintTemplateById(printTemplateId);
        if (printTemplate != null && PrintTemplate.TEMPLATE_TYPE_HTML.equals(printTemplate.getTemplateType())) {
            printType = "html";
        }
        return printType;
    }

    @Override
    @Transactional
    public LogicFileInfo print(String taskInstUuid, String printTemplateId) {
        return print(taskInstUuid, printTemplateId, null, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#print(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    // @Override
    @Transactional
    public LogicFileInfo print(String taskInstUuid, String printTemplateId, String printTemplateUuid, String lang) {
        LogicFileInfo logicFileInfo = null;
        PrintResult printResult = null;
        try {
            printResult = taskService.print(taskInstUuid, printTemplateId, printTemplateUuid, lang);
            MongoFileEntity mongoFileEntity = mongoFileService.saveFile(taskInstUuid, printResult.getFileName(),
                    printResult.getStream());
            mongoFileService.pushFileToFolder(taskInstUuid, mongoFileEntity.getFileID(), null);
            logicFileInfo = mongoFileEntity.getLogicFileInfo();
        } catch (PrintTemplateNotFoundException e1) {
            logger.error(e1.getMessage(), e1);
            String errorMsg = "没有设置套打模板!";
            throw new BusinessException(errorMsg);
        } catch (PrintTemplateStreamIsNullException e2) {
            logger.error(e2.getMessage(), e2);
            String errorMsg = "请上传模板再套打!";
            throw new BusinessException(errorMsg);
        } catch (Exception e3) {
            String errorMsg = e3.getMessage();
            if (StringUtils.isBlank(errorMsg)) {
                errorMsg = "服务器忙，请稍候再试!";
            }
            logger.error(errorMsg, e3);
            throw new BusinessException(errorMsg);
        } finally {
            if (printResult != null && printResult.getStream() != null) {
                IOUtils.closeQuietly(printResult.getStream());
            }
        }
        return logicFileInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getDyformFileFieldDefinition(java.lang.String)
     */
    @Override
    public List<DyformFieldDefinition> getDyformFileFieldDefinitions(String taskInstUuid) {
        TaskInstance taskInstance = taskService.get(taskInstUuid);
        String formUuid = taskInstance.getFormUuid();
        List<DyformFieldDefinition> fieldDefinitions = dyFormFacade.getFieldDefinitions(formUuid);
        List<DyformFieldDefinition> fileFieldDefinitions = new ArrayList<DyformFieldDefinition>();
        for (DyformFieldDefinition fieldDefinition : fieldDefinitions) {
            if (DyFormConfig.InputModeUtils.isInputModeEqAttach(fieldDefinition.getInputMode())) {
                fileFieldDefinitions.add(fieldDefinition);
            }
        }
        return fileFieldDefinitions;
    }

    /**
     * 工作抄送
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#copyTo(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void copyTo(Collection<String> taskInstUuids, List<String> rawCopyUsers, String aclRole) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        for (String taskInstUuid : taskInstUuids) {
            taskService.copyTo(userId, taskInstUuid, rawCopyUsers, aclRole);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getPrintTemplates(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<com.wellsoft.pt.bpm.engine.support.PrintTemplate> getPrintTemplates(String taskInstUuid,
                                                                                    String flowInstUuid) {
        FlowDefinition flowDefinition = null;
        String taskId = null;
        if (StringUtils.isNotBlank(taskInstUuid)) {
            flowDefinition = this.taskInstanceService.get(taskInstUuid).getFlowDefinition();
            taskId = this.taskInstanceService.get(taskInstUuid).getId();
        } else if (StringUtils.isNotBlank(flowInstUuid)) {
            flowDefinition = this.flowInstanceService.get(flowInstUuid).getFlowDefinition();
        }
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefinition);
        if (StringUtils.isBlank(taskId)) {
            taskId = flowDelegate.getFistTaskData().getFirstTaskId();
        }
        return flowDelegate.getTaskPrintTemplates(taskId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getPrintTemplates(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TreeNode> getPrintTemplatesTree(String taskInstUuid, String flowInstUuid) {

        List<String> printTemplateUuidList = new ArrayList<>();
        List<com.wellsoft.pt.bpm.engine.support.PrintTemplate> printTemplateList = this.getPrintTemplates(taskInstUuid,
                flowInstUuid);
        for (com.wellsoft.pt.bpm.engine.support.PrintTemplate printTemplate : printTemplateList) {
            printTemplateUuidList.add(printTemplate.getUuid());
        }

        return printTemplateFacadeService.getPrintTemplateTree(printTemplateUuidList);
    }

    /**
     * 工作催办
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#remind(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void remind(Collection<String> taskInstUuids, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles) {
        for (String taskInstUuid : taskInstUuids) {
            taskService.remind(taskInstUuid, opinionName, opinionValue, opinionText, opinionFiles);
        }
    }

    /**
     * 工作标记已阅
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#markRead(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void markRead(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.markRead(taskUuid);
        }
    }

    /**
     * 工作标记未阅
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#markUnread(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void markUnread(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.markUnread(taskUuid);
        }
    }

    /**
     * 工作取消关注
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#unfollow(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    @Transactional
    public void unfollow(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.unfollow(taskUuid);
        }
    }

    @Override
    @Transactional
    public void setViewMainFlow(String flowInstUuid, String taskId, String childLookParent) {
        FlowInstance flowInstance = flowService.getFlowInstance(flowInstUuid);
        net.sf.json.JSONObject viewMainFlowJson = net.sf.json.JSONObject.fromObject(flowInstance.getViewMainFlowJson());
        viewMainFlowJson.put(taskId, childLookParent);
        flowInstance.setViewMainFlowJson(viewMainFlowJson.toString());
        flowService.saveFlowInstance(flowInstance);
    }

    /**
     * 工作移交
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#handOver(java.util.Collection, java.util.List)
     */
    @Override
    @Transactional
    public void handOver(WorkBean workBean, List<String> rawHandOverUser) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = workBean.getTaskInstUuid();
        String opinionName = workBean.getOpinionLabel();
        String opinionValue = workBean.getOpinionValue();
        String opinionText = workBean.getOpinionText();
        List<LogicFileInfo> opinionFiles = workBean.getOpinionFiles();
        // 创建提交的任务数据
        String dataUuid = workBean.getDataUuid();
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        taskService.handOver(userId, taskInstUuid, rawHandOverUser, opinionName, opinionValue, opinionText, opinionFiles, true);
        deleteTempByTaskInstUuid(taskInstUuid);
    }

    /**
     * 获取可跳转的环节，不包含开始环节、当前环节、子环节
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getGotoTasks(java.lang.String)
     */
    @Override
    public Map<String, Object> getGotoTasks(String taskInstUuid) {
        return taskService.getToTasks(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getCurrentUserOpinion2Sign(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public WorkOpinionBean getCurrentUserOpinion2Sign(String flowDefUuid, String taskId) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        WorkOpinionBean opinionBean = new WorkOpinionBean();
        // 意见立场
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
        TaskElement taskElement = flowDelegate.getFlow().getTask(taskId);
        List<UnitElement> optNames = taskElement.getOptNames();
        if (CollectionUtils.isNotEmpty(optNames)) {
            List<FlowOpinion> opinions = new ArrayList<FlowOpinion>();
            for (UnitElement optName : optNames) {
                FlowOpinion opinion = new FlowOpinion();
                opinion.setContent(optName.getArgValue());
                opinion.setCode(optName.getValue());
                opinions.add(opinion);
            }
            opinionBean.setOpinions(opinions);
        }
        // 最近意见
        List<FlowOpinion> recents = flowOpinionService.getUserRecentOpinions(userId, flowDelegate.getFlow().getId(), taskId);
        List<FlowOpinion> flowOpinions = BeanUtils.convertCollection(recents, FlowOpinion.class);
        opinionBean.setRecents(flowOpinions);
        // 用户意见
        opinionBean.setUserOpinionCategories(flowOpinionService.getOpinionCategoryBeans());
        // 公共意见
        opinionBean.setPublicOpinionCategory(flowOpinionService.getPublicOpinionCategory());
        // 启用待办意见立场
        opinionBean.setEnableOpinionPosition(taskElement.getIsEnableOpinionPosition());
        // 意见立场必填
        opinionBean.setRequiredOpinionPosition(taskElement.getIsRequiredOpinionPosition());
        return opinionBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getOpinionPositionConfigsByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskOpinionPositionConfig> getOpinionPositionConfigsByFlowInstUuid(String flowInstUuid) {
        List<TaskOpinionPositionConfig> opinionPositionConfigs = Lists.newArrayList();
        FlowDelegate flowDelegate = FlowDelegateUtils
                .getFlowDelegate(flowInstanceService.getOne(flowInstUuid).getFlowDefinition());
        List<TaskElement> taskElements = flowDelegate.getFlow().getTasks();
        for (TaskElement taskElement : taskElements) {
            TaskOpinionPositionConfig config = new TaskOpinionPositionConfig();
            config.setTaskName(taskElement.getName());
            config.setTaskId(taskElement.getId());
            List<UnitElement> optNames = taskElement.getOptNames();
            if (CollectionUtils.isNotEmpty(optNames)) {
                List<OpinionPosition> opinionPositions = new ArrayList<OpinionPosition>();
                for (UnitElement optName : optNames) {
                    OpinionPosition opinionPosition = new OpinionPosition();
                    opinionPosition.setLabel(optName.getArgValue());
                    opinionPosition.setValue(optName.getValue());
                    opinionPositions.add(opinionPosition);
                }
                config.setOpinionPositions(opinionPositions);
            }
            config.setEnableOpinionPosition(taskElement.getIsEnableOpinionPosition());
            config.setRequiredOpinionPosition(taskElement.getIsRequiredOpinionPosition());
            config.setShowUserOpinionPosition(taskElement.getIsShowUserOpinionPosition());
            config.setShowOpinionPositionStatistics(taskElement.getIsShowOpinionPositionStatistics());
            opinionPositionConfigs.add(config);
        }
        return opinionPositionConfigs;
    }

    /**
     * 签署意见
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#signOpinion(java.util.Collection, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void signOpinion(Collection<String> taskUuids, String text, String value) {
        for (String taskUuid : taskUuids) {
            taskService.signOpinion(taskUuid, text, value);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getOpinions(com.wellsoft.pt.workflow.work.bean.WorkBean)
     */
    @Override
    public String getOpinions(WorkBean workBean) {
        List<TaskActivity> taskActivities = taskService.getTaskActivities(workBean.getFlowInstUuid());
        Map<String, List<TaskOperation>> operationMap = taskService.getOperationAsMap(workBean.getFlowInstUuid());
        StringBuilder sb = new StringBuilder();
        for (TaskActivity taskActivity : taskActivities) {
            // 流转信息
            String taskInstUuid = taskActivity.getTaskInstUuid();
            TaskInstance taskInstance = taskInstanceService.get(taskInstUuid);
            sb.append("<" + taskInstance.getName() + ">");
            sb.append(" ");
            sb.append(DateUtils.formatDateTimeMin(taskActivity.getCreateTime()));
            sb.append(" ");
            //  sb.append(orgApiFacade.getAccountByUserId(taskActivity.getCreator()).getUserName());
            sb.append(workflowOrgService.getNameById(taskActivity.getCreator()));
            sb.append(Separator.LINE.getValue() + "<br>");

            // 操作信息
            List<TaskOperation> taskOperations = operationMap.get(taskInstUuid);
            if (taskOperations == null) {
                continue;
            }
            for (TaskOperation taskOperation : taskOperations) {
                // sb.append(orgApiFacade.getAccountByUserId(taskOperation.getAssignee()).getUserName());
                sb.append(workflowOrgService.getNameById(taskOperation.getAssignee()));
                sb.append(" ");
                sb.append(DateUtils.formatDateTimeMin(taskOperation.getCreateTime()));
                sb.append(" ");
                sb.append(WorkFlowOperation.getName(taskOperation.getActionType()));
                if (StringUtils.isNotBlank(taskOperation.getOpinionText())) {
                    sb.append(Separator.LINE.getValue() + "<br>");
                    sb.append(" ");
                    sb.append(taskOperation.getOpinionText());
                }
                sb.append(Separator.LINE.getValue() + "<br>");
            }
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getPreTaskOperation(java.lang.String)
     */
    @Override
    public List<TaskOperation> getPreTaskOperations(String taskInstUuid) {
        TaskActivity taskActivity = taskActivityService.getByTaskInstUuid(taskInstUuid);
        String preTaskInstUuid = taskActivity.getPreTaskInstUuid();
        List<TaskOperation> taskOperations = taskOperationService.getByTaskInstUuid(preTaskInstUuid);
        return taskOperations;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#suspend(java.util.Collection)
     */
    @Override
    @Transactional
    public void suspend(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.suspend(taskUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#resume(java.util.Collection)
     */
    @Override
    @Transactional
    public void resume(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.resume(taskUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#suspendTimer(java.util.Collection)
     */
    @Override
    @Transactional
    public void suspendTimer(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.suspendTimer(taskUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#resumeTimer(java.util.Collection)
     */
    @Override
    @Transactional
    public void resumeTimer(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.resumeTimer(taskUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#reject(java.util.Collection)
     */
    @Override
    @Transactional
    public void reject(Collection<String> taskUuids) {
        for (String taskUuid : taskUuids) {
            taskService.reject(taskUuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#gotoTask(java.util.Collection, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public ResultMessage gotoTask(WorkBean workBean) {
        // 检查工作数据状态
        preCheckWorkDataState(workBean);

        String userId = SpringSecurityUtils.getCurrentUserId();
        String taskInstUuid = workBean.getTaskInstUuid();

        // 可选择的退回环节ID
        String gotoTaskId = workBean.getGotoTaskId();
        // 自定义退回的按钮
        CustomDynamicButton customDynamicButton = workBean.getCustomDynamicButton();
        if (customDynamicButton != null && StringUtils.isNotBlank(customDynamicButton.getId())) {
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
            CustomDynamicButton dynamicButton = flowDelegate.convertTaskCustomDynamicButton(taskInstance.getId(),
                    customDynamicButton);
            gotoTaskId = dynamicButton.getTaskId();
        }

        // 判断跳转环节是否存在
        if (StringUtils.isNotBlank(gotoTaskId)) {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(workBean.getFlowDefUuid());
            if (!flowDelegate.existsTaskNode(gotoTaskId)) {
                throw new WorkFlowException("系统管理员更新流程定义，跳转环节已不存在，请重新操作！");
            }
        }

        TaskData taskData = createTaskDataFromWorkData(workBean);
//        taskData.setUserId(userId);
//        taskData.setUserName(SpringSecurityUtils.getCurrentUserName());
        taskData.setCustomData("gotoTaskId", gotoTaskId);

//        // 办理意见
//        String key = taskInstUuid + userId;
//        taskData.setOpinionLabel(key, workBean.getOpinionLabel());
//        taskData.setOpinionValue(key, workBean.getOpinionValue());
//        taskData.setOpinionText(key, workBean.getOpinionText());
//        // 操作动作
//        taskData.setAction(key, workBean.getAction());
//        taskData.setActionType(key, workBean.getActionType());
//        // 任务用户
//        if (StringUtils.isNotBlank(gotoTaskId)) {
//            taskData.setTaskUsers(workBean.getTaskUsers());
//        }
//        // 抄送人
//        taskData.addTaskCopyUsers(workBean.getTaskCopyUsers());
//        // 监督人
//        taskData.addTaskMonitors(workBean.getTaskMonitors());
        // 自动更新标题逻辑处理
        autoUpdateTitle(taskData, workBean);
        taskService.gotoTask(taskInstUuid, gotoTaskId, taskData);
        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);
        // taskOperationTempService.delTemp(workBean.getFlowInstUuid(), workBean.getTaskInstUuid(), userId);
        deleteTempByTaskInstUuid(workBean.getTaskInstUuid());

        // 返回跳转结果
        ResultMessage msg = new ResultMessage();
        SubmitResult submitResult = taskData.getSubmitResult();
        msg.setData(submitResult);
        return msg;
    }

    /* add by huanglinchuan 2014.12.25 begin */


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#setFlowInstanceTitle(com.wellsoft.pt.bpm.engine.entity.FlowDefinition, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData, com.wellsoft.pt.dyform.facade.dto.DyFormData)
     */
    @Transactional
    public void setFlowInstanceTitle(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskData taskData,
                                     DyFormData dyFormData) {
        // 标题仅按中文环境计算保存
        Locale locale = LocaleContextHolder.getLocale();
        LocaleContextHolder.setLocale(Locale.SIMPLIFIED_CHINESE);
        String title = TitleExpressionUtils.generateFlowInstanceTitle(flowDefinition, flowInstance, taskData,
                dyFormData);
        flowInstance.setTitle(title);
        LocaleContextHolder.setLocale(locale);
        flowInstanceService.update(flowInstance);
        taskData.setTitle(flowDefinition.getId(), flowInstance.getTitle());
//        WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
//        if (workFlowSettings.isAutoTranslateTitle()) {
//            asyncExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        IgnoreLoginUtils.login(Config.DEFAULT_TENANT, flowInstance.getStartUserId());
//                        RequestSystemContextPathResolver.setSystem(flowInstance.getSystem());
//                        WorkService workService = ApplicationContextHolder.getBean("workService", WorkService.class);
//                        workService.translateFlowInstanceTitle(flowInstance.getUuid());
//                    } catch (Exception e) {
//                        logger.error("计算流程标题国际化异常: ", e);
//                    } finally {
//                        RequestSystemContextPathResolver.clear();
//                    }
//                }
//            });
//        }
    }

    /* add by huanglinchuan 2014.12.25 end */

    /**
     * (non-Javadoc)
     */
    private void setFlowInstanceTitle(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskData taskData,
                                      String startUserId, DyFormData dyFormData) {
        String title = TitleExpressionUtils.generateFlowInstanceTitle(flowDefinition, flowInstance, startUserId,
                dyFormData);
        flowInstance.setTitle(title);
        taskData.setTitle(flowDefinition.getId(), flowInstance.getTitle());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getTaskOperation(java.lang.String)
     */
    @Override
    public TaskOperation getTaskOperation(String taskOperationUuid) {
        return taskOperationService.get(taskOperationUuid);
    }

    @Override
    public void genTime(Collection<String> taskInstUUids, HttpServletResponse response) {
        ServletOutputStream outStream = null;
        try {
            HSSFWorkbook wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("流程时效表");
            // 标题
            HSSFRow headrow = sheet.createRow(0);
            headrow.setHeight((short) 250);
            List<String> titles = new ArrayList<String>();
            titles.add("标题");
            // 样式
            HSSFCellStyle style = wb.createCellStyle();
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setBorderBottom(BorderStyle.THIN); // 下边框
            style.setBorderLeft(BorderStyle.THIN);// 左边框
            style.setBorderTop(BorderStyle.THIN);// 上边框
            style.setBorderRight(BorderStyle.THIN);// 右边框
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            // 填充流程时效内容
            int rowNum = 1;
            HSSFRow row = null;
            HSSFCell cell = null;
            int cellNum = 0;
            // 内容
            for (String taskUid : taskInstUUids) {
                cellNum = 0;

                row = sheet.createRow(rowNum);

                TaskInstance task = taskInstanceService.get(taskUid);
                String dataUuid = task.getDataUuid();
                // 第一行环节标题
                cell = row.createCell(cellNum);
                cell.setCellValue(new HSSFRichTextString(task.getFlowInstance().getTitle()));
                cell.setCellStyle(style);

                cellNum++;
                List<TaskInstance> taskInstances = taskInstanceService.getByDateUuid(task.getFormUuid(), dataUuid);

                Map<String, TaskInstance> taskInstancesMap = new HashMap<String, TaskInstance>();
                List<String> taskInstanceNameList = new ArrayList<String>();

                // 同一环节取最后一个实例
                for (TaskInstance taskInstance : taskInstances) {
                    if (!taskInstanceNameList.contains(taskInstance.getName())) {
                        taskInstanceNameList.add(taskInstance.getName());
                    }
                    taskInstancesMap.put(taskInstance.getName(), taskInstance);
                }

                for (String taskName : taskInstanceNameList) {
                    if (titles.size() <= cellNum) {
                        titles.add(cellNum, taskName);
                    }
                    TaskInstance taskInstance = taskInstancesMap.get(taskName);
                    cell = row.createCell(cellNum);
                    cell.setCellValue(new HSSFRichTextString(formatTime(taskInstance.getDuration())));
                    cell.setCellStyle(style);
                    cellNum++;
                }
                rowNum++;
            }
            // 创建标题和设置列宽
            cellNum = 0;
            for (String title : titles) {
                // 设置列宽
                if (0 == cellNum) {
                    sheet.setColumnWidth(cellNum, 20 * 550);
                } else {
                    sheet.setColumnWidth(cellNum, 20 * 256);
                }

                // 创建标题
                cell = headrow.createCell(cellNum);
                cell.setCellValue(new HSSFRichTextString(title));
                cell.setCellStyle(style);
                cellNum++;
            }

            response.reset();
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + new String(("流程时效表").getBytes("gb2312"), "ISO-8859-1") + ".xls");
            outStream = response.getOutputStream();
            wb.write(outStream);
            IOUtils.closeQuietly(wb);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            try {
                if (null != outStream) {
                    outStream.close();
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /* lmw 2015-4-24 17:43 end */

    @Override
    public String isSameFlow(Collection<String> taskInstUUids) {
        if (null == taskInstUUids || taskInstUUids.isEmpty()) {
            return "y";
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuids", taskInstUUids);
        List<QueryItem> result = nativeDao.namedQuery("isSameFlowForTask", values, QueryItem.class);
        if (null == result || result.size() < 2) {
            return "y";
        }
        return "n";
    }

    @Override
    public Map<String, Object> viewReadLog(String taskInstUuid) {
        // 已阅人员
        List<String> readUserIds = Lists.newArrayList();
        List<Map<String, String>> readUsers = Lists.newArrayList();
        List<ReadMarker> readMarkers = readMarkerService.getReadMarkers(taskInstUuid);
        // 按阅读时间降序
        Collections.sort(readMarkers, IdEntityComparators.CREATE_TIME_DESC);
        for (ReadMarker readMarker : readMarkers) {
            Map<String, String> readUserMap = Maps.newHashMap();
            // readUserMap.put("userName", orgApiFacade.getUserNameById(readMarker.getUserId()));
            readUserMap.put("userName", workflowOrgService.getNameById(readMarker.getUserId()));
            readUserMap.put("readTime", DateUtils.formatDateTimeMin(readMarker.getCreateTime()));
            readUsers.add(readUserMap);
            readUserIds.add(readMarker.getUserId());
        }

        // 未阅人员
        List<String> unReadUserIds = Lists.newArrayList();
        List<AclTaskEntry> aclEntrys = aclTaskService.getSid(taskInstUuid, AclPermission.UNREAD);
        for (AclTaskEntry po : aclEntrys) {
            unReadUserIds.add(po.getSid());
        }
        // 待办人员ID
        unReadUserIds.addAll(taskService.getTodoUserIds(taskInstUuid));
        unReadUserIds.removeAll(readUserIds);
//        Map<String, String> usersMap = new HashMap<>();
        Map<String, String> usersMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(unReadUserIds)) {
//            usersMap = orgApiFacade.getUsersByOrgIds(unReadUserIds);
            usersMap = workflowOrgService.getUsersByIds(unReadUserIds);
        }

        Map<String, Object> map = Maps.newHashMap();
        map.put("readUser", readUsers);
        map.put("unReadUser", usersMap.values());
        return map;
    }

    /**
     * 将时间time格式化
     *
     * @param time
     * @return
     */
    private String formatTime(Long time) {
        if (time < 1000) {
            return time + "毫秒";
        } else if (time < 1000 * 60) {
            return time / 1000f + "秒";
        } else if (time < 1000 * 60 * 60) {
            return time / 1000f / 60 + "分";
        } else if (time < 1000 * 60 * 60 * 24) {
            return time / 1000f / 60 / 60 + "时";
        } else {
            return time / 1000f / 60 / 60 / 24 + "天";
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#listFlowDataSnapshotWithoutDyformDataByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<FlowDataSnapshot> listFlowDataSnapshotWithoutDyformDataByFlowInstUuid(String flowInstUuid) {
        return flowSamplerService.listWithoutDyformDataByFlowInstUuid(flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getFlowDataSnapshotByIds(java.lang.String[])
     */
    @Override
    public List<String> getFlowDataSnapshotByIds(String... ids) {
        List<String> snapshots = Lists.newArrayList();
        for (String id : ids) {
            snapshots.add(flowSamplerService.getAsStringById(id));
        }
        return snapshots;
    }

    @Override
    public FlowDataSnapshotAuditLog getFlowDataSnapshotAuditLogByObjecId(String objectId) {
        return flowSamplerService.getFlowDataSnapshotAuditLogByObjecId(objectId);
    }

    @Override
    public FlowDataDyformFieldModifyInfo getDyformFieldModifyInfo(String flowInstUuid, List<String> fieldNames) {
        return flowSamplerService.getDyformFieldModifyInfo(flowInstUuid, fieldNames);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#getFlowHandingStateInfo(java.lang.String)
     */
    @Override
    public FlowHandingStateInfoDto getFlowHandingStateInfo(String flowInstUuid) {
        FlowHandingStateInfoDto flowHandingStateInfoDto = new FlowHandingStateInfoDto();
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowInstance.getFlowDefinition());
        // 初始化流程办理状态
        initFlowHandingStateInfo(flowHandingStateInfoDto, flowDelegate);

        List<TaskActivityQueryItem> taskActivityQueryItems = taskActivityService
                .getAllActivityByFlowInstUuid(flowInstUuid);
        // 填充流程办理状态
        fillFlowHandingStateInfo(flowInstance, flowHandingStateInfoDto, flowDelegate, taskActivityQueryItems);
        return flowHandingStateInfoDto;
    }

    /**
     * @param flowHandingStateInfoDto
     * @param flowDelegate
     */
    private void initFlowHandingStateInfo(FlowHandingStateInfoDto flowHandingStateInfoDto, FlowDelegate flowDelegate) {
        // 环节状态设置为未办
        List<Node> taskNodes = flowDelegate.getAllTaskNodes();
        for (Node node : taskNodes) {
            Map<String, Object> values = Maps.newHashMap();
            values.put(FlowHandingStateInfoDto.STATE_KEY, FlowHandingStateInfoDto.STATE_UNDO);
            // 子流程环节
            if (StringUtils.equals(TaskNodeType.SubTask.getValue(), node.getType())) {
                flowHandingStateInfoDto.getSubflows().put(node.getId(), values);
            } else {
                // 环节信息
                flowHandingStateInfoDto.getTasks().put(node.getId(), values);
            }
        }

        // 结束结点状态为未完成
        List<Direction> directions = flowDelegate.getAllDirections();
        Set<String> endFromIdSet = Sets.newHashSet();
        for (Direction direction : directions) {
            if (StringUtils.equals(FlowConstant.END_FLOW_ID, direction.getToID())) {
                if (endFromIdSet.contains(direction.getFromID())) {
                    continue;
                }
                endFromIdSet.add(direction.getFromID());
                Map<String, Object> values = Maps.newHashMap();
                values.put(FlowHandingStateInfoDto.COMPLETED_KEY, false);
                values.put(FlowHandingStateInfoDto.FROM_ID_KEY, direction.getFromID());
                flowHandingStateInfoDto.getEnds().add(values);
            }
        }
    }

    /**
     * @param flowHandingStateInfoDto
     * @param flowDelegate
     * @param taskActivityQueryItems
     */
    private void fillFlowHandingStateInfo(FlowInstance flowInstance, FlowHandingStateInfoDto flowHandingStateInfoDto,
                                          FlowDelegate flowDelegate, List<TaskActivityQueryItem> taskActivityQueryItems) {
        List<TaskInstance> taskInstances = taskService.getAllByFlowInstUuid(flowInstance.getUuid());
        Map<String, TaskInstance> taskInstanceMap = ConvertUtils.convertElementToMap(taskInstances, IdEntity.UUID);
        // 草稿状态，没有环节流转信息
        if (CollectionUtils.isEmpty(taskActivityQueryItems)) {
            flowHandingStateInfoDto.getStart().put(FlowHandingStateInfoDto.STARTED_KEY, false);
        } else {
            Map<String, List<TaskActivityQueryItem>> preTaskActivityMap = ListUtils.list2group(taskActivityQueryItems, "preTaskInstUuid");
            // 流程结束结点信息
            List<Map<String, Object>> ends = flowHandingStateInfoDto.getEnds();
            flowHandingStateInfoDto.getStart().put(FlowHandingStateInfoDto.STARTED_KEY, true);
            TaskActivityStack stack = TaskActivityStackFactary.build(null, true, taskActivityQueryItems);
            Iterator<TaskActivityItem> it = stack.descendingIterator();
            while (it.hasNext()) {
                TaskActivityItem item = it.next();
                String taskId = item.getTaskId();
                // 流程定义已经不存在的结点忽略掉
                if (!flowDelegate.existsTaskNode(taskId)) {
                    continue;
                }
                Node taskNode = flowDelegate.getTaskNode(taskId);
                // 子流程环节节点
                if (StringUtils.equals(TaskNodeType.SubTask.getValue(), taskNode.getType())) {
                    Map<String, Object> subflowInfo = flowHandingStateInfoDto.getSubflows().get(taskId);
                    addTaskHandingStateInfo(flowInstance, taskInstanceMap, item, subflowInfo);
                } else {
                    // 环节节点
                    Map<String, Object> taskInfo = flowHandingStateInfoDto.getTasks().get(taskId);
                    addTaskHandingStateInfo(flowInstance, taskInstanceMap, item, taskInfo);
                }

                // 流程办结状态
                for (Map<String, Object> end : ends) {
                    if (StringUtils.equals(taskId, ObjectUtils.toString(end.get(FlowHandingStateInfoDto.FROM_ID_KEY)))
                            && item.getEndTime() != null && CollectionUtils.isEmpty(preTaskActivityMap.get(item.getTaskInstUuid()))) {
                        end.put(FlowHandingStateInfoDto.COMPLETED_KEY, true);
                        String paramName = "toDirectionId_" + item.getTaskInstUuid();
                        FlowInstanceParameter parameter = flowInstanceParameterService.getByFlowInstUuidAndName(flowInstance.getUuid(), paramName);
                        if (parameter != null) {
                            end.put("toDirectionId", parameter.getValue());
                        }
                    }
                }
            }
        }
    }

    /**
     * @param flowInstance
     * @param taskInstanceMap
     * @param item
     * @param taskInfo
     */
    @SuppressWarnings("unchecked")
    private void addTaskHandingStateInfo(FlowInstance flowInstance, Map<String, TaskInstance> taskInstanceMap,
                                         TaskActivityItem item, Map<String, Object> taskInfo) {
        String preTaskId = item.getPreTaskId();
        String preGatewayIds = item.getPreGatewayIds();
        TaskInstance taskInstance = taskInstanceMap.get(item.getTaskInstUuid());
        // 前一办理环节
        Set<String> preTaskIds = (Set<String>) taskInfo.get(FlowHandingStateInfoDto.PRE_TASK_IDS_KEY);
        if (preTaskIds == null) {
            preTaskIds = Sets.<String>newLinkedHashSet();
            taskInfo.put(FlowHandingStateInfoDto.PRE_TASK_IDS_KEY, preTaskIds);
        }
        if (StringUtils.isNotBlank(preTaskId)) {
            preTaskIds.add(preTaskId);
        }

        // 前一网关(判断节点)
        if (StringUtils.isNotBlank(preGatewayIds)) {
            Set<String> preGatewayIdSet = (Set<String>) taskInfo.get(FlowHandingStateInfoDto.PRE_GATEWAY_IDS_KEY);
            if (preGatewayIdSet == null) {
                preGatewayIdSet = Sets.<String>newLinkedHashSet();
            }
            preGatewayIdSet.addAll(Arrays.asList(StringUtils.split(preGatewayIds, Separator.SEMICOLON.getValue())));
            taskInfo.put(FlowHandingStateInfoDto.PRE_GATEWAY_IDS_KEY, preGatewayIdSet);
        }

        // 待办
        if (item.getEndTime() == null) {
            taskInfo.put(FlowHandingStateInfoDto.STATE_KEY, FlowHandingStateInfoDto.STATE_TODO);
        } else {
            // 已办
            if (!FlowHandingStateInfoDto.STATE_TODO.equals(taskInfo.get(FlowHandingStateInfoDto.STATE_KEY))) {
                taskInfo.put(FlowHandingStateInfoDto.STATE_KEY, FlowHandingStateInfoDto.STATE_DONE);
            }
        }
        // 计时信息
        if (taskInstance != null) {
            if (taskInstance.getEndTime() == null) {
                taskInfo.put(FlowHandingStateInfoDto.IS_TIMMING_KEY, flowInstance.getIsTiming());
            } else {
                taskInfo.put(FlowHandingStateInfoDto.IS_TIMMING_KEY, false);
            }
            taskInfo.put(FlowHandingStateInfoDto.IS_ALARM_KEY, Integer.valueOf(1).equals(taskInstance.getAlarmState()));
            taskInfo.put(FlowHandingStateInfoDto.IS_OVER_DUE_KEY,
                    Integer.valueOf(1).equals(taskInstance.getOverDueState()));
        }
    }

    @Override
    public WorkBean getShare(String taskUuid, String flowInstUuid) {
        TaskInstance task = taskService.get(taskUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
        WorkBean workBean = getWorkBean(task, flowDelegate);
        workBean.setAclRole(WorkFlowAclRole.VIEWER);
        return workBean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#lockWork(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void lockWork(String taskInstUuid) {
        taskService.addCurrentUserLock(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#unlockWork(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void unlockWork(String taskInstUuid) {
        taskService.removeCurrentUserLock(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#listLockInfo(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskLockInfo> listLockInfo(String taskInstUuid) {
        return taskService.listTaskLock(taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#listAllLockInfo(java.util.Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public Map<String, List<TaskLockInfo>> listAllLockInfo(Collection<String> taskInstUuids) {
        Map<String, List<TaskLockInfo>> map = Maps.newLinkedHashMap();
        for (String taskInstUuid : taskInstUuids) {
            map.put(taskInstUuid, listLockInfo(taskInstUuid));
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#releaseAllLock(java.util.Collection)
     */
    @Override
    @Transactional(readOnly = true)
    public void releaseAllLock(Collection<String> taskInstUuids) {
        for (String taskInstUuid : taskInstUuids) {
            taskService.releaseLock(taskInstUuid);
        }
    }

    /**
     * 自动更新标题逻辑处理
     *
     * @param taskData
     * @param workBean
     * @return void
     **/
    private void autoUpdateTitle(TaskData taskData, WorkBean workBean) {
        FlowDefinition flowDefinition = flowDefinitionService.getOne(workBean.getFlowDefUuid());
        FlowInstance flowInstance = flowService.getFlowInstance(workBean.getFlowInstUuid());
        if (workBean.isAutoUpdateTitle()) {
            if (workBean.getDyFormData() == null) {
                DyFormData dyFormData = dyFormFacade.getDyFormData(workBean.getFormUuid(), workBean.getDataUuid());
                workBean.setDyFormData(dyFormData);
            }
            setFlowInstanceTitle(flowDefinition, flowInstance, taskData, flowInstance.getCreator(),
                    workBean.getDyFormData());
            // 同步表单映射的流程标题
            DyFormData dyFormData = dyFormApiFacade.getDyFormData(workBean.getFormUuid(), workBean.getDataUuid());
            updateDyformMappingTitle(workBean, flowInstance, taskData, dyFormData);
        }
    }

    @Override
    public void submitFlowVisa(WorkBean workBean) {

        //// 提交前服务处理
        // String beforeSubmitService = workBean.getBeforeSubmitService();
        // if (StringUtils.isNotBlank(beforeSubmitService)) {
        // ServiceInvokeUtils.invoke(beforeSubmitService, new Class[] { WorkBean.class
        //// }, workBean);
        //
        // taskInstanceService.flushSession();
        // taskInstanceService.clearSession();
        // }

        UserDetails user = SpringSecurityUtils.getCurrentUser();
        String userId = user.getUserId();
        workBean.setUserId(userId);
        workBean.setUserName(user.getUserName());
        String taskInstUuid = workBean.getTaskInstUuid();
        String flowInstUuid = workBean.getFlowInstUuid();
        String flowDefUuid = workBean.getFlowDefUuid();

        /* modified by huanglinchuan 2014.12.25 begin */
        FlowDefinition flowDefinition = flowDefinitionService.getOne(flowDefUuid);
        FlowDelegate flowDelegate = new FlowDelegate(flowDefinition);
        FlowInstance flowInstance = null;
        // 意见日志信息
        // 保存动态表单，设置信息格式
        List<String> opinionLogUuids = null;
        if (StringUtils.isNotBlank(flowInstUuid)) {
            opinionLogUuids = taskFormOpinionService.recordTaskFormOpinion(workBean, flowDelegate);
        }
        /* modified by huanglinchuan 2014.10.27 end */

        String dataUuid = null;
        /* add by huanglinchuan 2014.12.1 begin */
        String serialNo = null;
        /* add by huanglinchuan 2014.12.1 end */
        if (workBean.getDyFormData() != null) {
            // 真正生成流水号
            /* modified by huanglinchuan 2014.12.25 begin */
            // 检查发现，在拟稿环节如果先保存后，再提交，这个时候workBean.getSerialNoDefId()会为空，所以才会导致出问题
            // 修改getDraft方法即可
            // if (StringUtils.isNotBlank(workBean.getSerialNoDefId()) &&
            // StringUtils.isBlank(taskInstUuid)) {
            // try {
            // List<IdEntity> entities = new ArrayList<IdEntity>();
            // TaskInstance tmpTaskInstance = new TaskInstance();
            // entities.add(tmpTaskInstance);
            // FlowInstance tmpFlowInstance = new FlowInstance();
            // entities.add(tmpFlowInstance);
            // // add by huanglinchuan 2014.10.17 begin
            // entities.add(flowService.getFlowDefinition(workBean.getFlowDefUuid()));
            // // add by huanglinchuan 2014.10.17 end
            //
            // /* modified by huanglinchuan 2014.12.1 begin */
            // serialNo = basicDataApiFacade.getSerialNumber(workBean.getSerialNoDefId(),
            // entities, true,
            // WorkFlowVariables.SERIAL_NO.getName());
            // workBean.getDyFormData().setFieldValueByMappingName(WorkFlowFieldMapping.SERIAL_NO.getValue(),
            // serialNo);
            // /* modified by huanglinchuan 2014.12.1 end */
            // } catch (Exception e) {
            // logger.error(ExceptionUtils.getStackTrace(e));
            // }
            // }
            /* modified by huanglinchuan 2014.12.25 end */
            // ValidateMsg validateMsg =
            // workBean.getDyFormData().validateFormDataWithDatabaseConstraints();
            //// 生成信息记录后进行检验
            // if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
            // if (CollectionUtils.isNotEmpty(opinionLogUuids)) {
            // validateMsg.setMsg("流程生成的信息记录设置到表单后，表单数据检验失败");
            // }
            // throw new FormDataValidateException(validateMsg);
            // }
            dataUuid = dyFormFacade.saveFormData(workBean.getDyFormData());
            taskInstanceService.flushSession();
            taskInstanceService.clearSession();
        } else {
            dataUuid = workBean.getDataUuid();
        }

        // 创建提交的任务数据
        TaskData taskData = createTaskData(workBean, user, taskInstUuid, dataUuid);

        /* add by huanglinchuan 2014.12.1 begin */
        if (serialNo != null) {
            taskData.setTaskSerialNo(serialNo);
        }
        /* add by huanglinchuan 2014.12.1 end */

        // 任务UUID不为空则提交当前工作
        if (StringUtils.isNotBlank(taskInstUuid)) {
            // 填充任务数据
            fillTaskData(workBean, user, taskInstUuid, opinionLogUuids, taskData);

            if (workBean.isRequiredSubmitPermission()) {
                preCheckTodo(user, taskInstUuid);
            }
            /* modified by huanglinchuan 2014.12.25 begin */
            TaskInstance taskInstance = this.taskInstanceService.get(taskInstUuid);
            flowInstance = taskInstance.getFlowInstance();
            Boolean isSubFlow = flowInstance.getParent() == null ? Boolean.FALSE : Boolean.TRUE;
            if ((flowDelegate.isFirstTaskNode(taskInstance.getId()) && !isSubFlow)
                    || Boolean.TRUE.equals(workBean.isAutoUpdateTitle())) {
                setFlowInstanceTitle(flowDefinition, flowInstance, taskData, workBean.getDyFormData());
                // 同步表单映射的流程标题
                updateDyformMappingTitle(workBean, flowInstance, taskData, workBean.getDyFormData());
            }
            /* modified by huanglinchuan 2014.12.25 end */
            taskService.submit(taskInstUuid, taskData);
        } else if (StringUtils.isNotBlank(flowInstUuid)) {// 从工作草稿启动流程
            flowInstance = flowService.startFlowInstance(flowInstUuid, taskData);
            setFlowInstanceTitle(flowDefinition, flowInstance, taskData, workBean.getDyFormData());
            // 同步表单映射的流程标题
            updateDyformMappingTitle(workBean, flowInstance, taskData, workBean.getDyFormData());
            // 提交第一个任务
            List<TaskInstance> taskInstances = taskService.getTodoTasks(userId, flowInstUuid);
            for (TaskInstance taskInstance : taskInstances) {
                // 填充任务数据
                fillTaskData(workBean, user, taskInstance.getUuid(), opinionLogUuids, taskData);

                if (workBean.isRequiredSubmitPermission()) {
                    preCheckTodo(user, taskInstance.getUuid());
                }
                taskService.submit(taskInstance, taskData);
            }
        } else if (StringUtils.isNotBlank(flowDefUuid)) {// 从流程定义启动流程
            //// 工作保存为草稿
            /// * modified by huanglinchuan 2014.12.25 begin */
            // flowInstance = flowService.saveAsDraft(flowDefUuid, taskData);
            //// 意见日志信息
            //// 保存动态表单，设置信息格式
            // workBean.setFlowInstUuid(flowInstance.getUuid());
            // workBean.setDataUuid(dataUuid);
            // opinionLogUuids = taskFormOpinionService.recordTaskFormOpinion(workBean,
            //// flowDelegate);
            //// 生成信息记录后进行检验
            // if (CollectionUtils.isNotEmpty(opinionLogUuids)) {
            // ValidateMsg validateMsg =
            //// workBean.getDyFormData().validateFormDataWithDatabaseConstraints();
            // if (CollectionUtils.isNotEmpty(validateMsg.getErrors())) {
            // validateMsg.setMsg("流程生成的信息记录设置到表单后，表单数据检验失败");
            // throw new FormDataValidateException(validateMsg);
            // }
            // dyFormFacade.saveFormData(workBean.getDyFormData());
            // }
            // setFlowInstanceTitle(flowDefinition, flowInstance, taskData,
            //// workBean.getDyFormData());
            //// 同步表单映射的流程标题
            // updateDyformMappingTitle(workBean, flowInstance, taskData,
            //// workBean.getDyFormData());
            //
            // flowInstUuid = flowInstance.getUuid();
            // workBean.setFlowInstUuid(flowInstUuid);
            // flowService.startFlowInstance(flowInstUuid, taskData);
            /// * modified by huanglinchuan 2014.12.25 end */
            //// 提交第一个任务
            // List<TaskInstance> taskInstances = taskService.getTodoTasks(userId,
            //// flowInstUuid);
            // for (TaskInstance taskInstance : taskInstances) {
            // // 填充任务数据
            // fillTaskData(workBean, user, taskInstance.getUuid(), opinionLogUuids,
            //// taskData);
            //
            // if (workBean.isRequiredSubmitPermission()) {
            // preCheckTodo(user, taskInstance.getUuid());
            // }
            // taskService.submit(taskInstance, taskData);
            // }
        }
        //
        //// 提交后套打
        // boolean isPrintAfterSubmit = workBean.isPrintAfterSubmit();
        // if (isPrintAfterSubmit) {
        // printAfterSubmit(taskData.getSubmitResult(), flowDelegate);
        // }
        //
        //// 索引流程文档
        // flowIndexDocumentService.indexWorkflowDocument(taskData);
        //
        //// 提交后服务处理
        // String afterSubmitService = workBean.getAfterSubmitService();
        // if (StringUtils.isNotBlank(afterSubmitService)) {
        // ServiceInvokeUtils.invoke(afterSubmitService, new Class[] { WorkBean.class },
        // workBean);
        // }

        // 添加最近签署意见
        if (StringUtils.isNotBlank(workBean.getOpinionText())) {
            flowOpinionService.addRecentOpinion(userId, workBean.getFlowDefId(), workBean.getTaskId(),
                    StringUtils.trim(workBean.getOpinionText()));
        }

        // 保存更新的表单数据
        saveUpdatedDyFormDatasIfRequired(taskData);

        // 返回提交结果
        ResultMessage msg = new ResultMessage();
        SubmitResult submitResult = taskData.getSubmitResult();
        msg.setData(submitResult);
    }

    /**
     * @param taskData
     */
    private void saveUpdatedDyFormDatasIfRequired(TaskData taskData) {
        List<DyFormData> dyFormDatas = taskData.getAllUpdatedDyFormDatas();
        for (DyFormData dyFormData : dyFormDatas) {
            dyFormData.doForceCover();
            dyFormFacade.saveFormData(dyFormData);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WorkService#checkRecordPreCondition(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.facade.dto.DyFormData, com.wellsoft.pt.bpm.engine.form.Record, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean checkRecordPreCondition(String taskInstUuid, String flowInstUuid, String flowDefUuid,
                                           DyFormData dyFormData, Record record, String opinionLabel, String opinionValue, String opinionText) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("opinionText", opinionText);
        values.put("opinionLabel", opinionLabel);
        values.put("opinionValue", opinionValue);
        return taskFormOpinionService.checkRecordPreCondition(taskInstUuid, flowInstUuid, flowDefUuid, record,
                dyFormData, values);
    }

    @Override
    public TaskOperationTemp getTaskOperationTemp(String flowInstUuid, String taskInstUuid) {
        TaskOperationTemp temp = taskOperationTempService.queryTemp(flowInstUuid, taskInstUuid,
                SpringSecurityUtils.getCurrentUserId());
        return temp;
    }

    /**
     * 从流程数据创建TaskData
     *
     * @param workBean
     * @return
     */
    @Override
    public TaskData createTaskDataFromWorkData(WorkBean workBean) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        return createTaskData(workBean, userDetails, workBean.getTaskInstUuid(), workBean.getDataUuid());
    }

    /**
     * 必填字段是否为空
     *
     * @param workBean
     * @return
     */
    @Override
    public boolean requiredFieldIsBlank(WorkBean workBean) {
        if (BooleanUtils.isNotTrue(workBean.getCanEditForm())) {
            return false;
        }
        TaskForm taskForm = getTaskForm(workBean);
        if (taskForm == null) {
            return false;
        }
        String mainFormUuid = workBean.getFormUuid();
        String dataUuid = workBean.getDataUuid();
        DyFormData dyFormData = workBean.getDyFormData();
        if (dyFormData == null && StringUtils.isNotBlank(mainFormUuid) && StringUtils.isNotBlank(dataUuid)) {
            dyFormData = dyFormApiFacade.getDyFormData(mainFormUuid, dataUuid);
        }
        if (dyFormData == null) {
            return false;
        }

        Map<String, List<String>> notNullFieldMap = taskForm.getNotNullFieldMap();
        if (MapUtils.isEmpty(notNullFieldMap)) {
            return false;
        }

        List<String> emptyValueFields = Lists.newArrayList();
        for (Entry<String, List<String>> entry : notNullFieldMap.entrySet()) {
            String formUuid = entry.getKey();
            List<String> requireFields = entry.getValue();
            // 主表判断
            if (StringUtils.equals(mainFormUuid, formUuid)) {
                emptyValueFields.addAll(getRequiredFieldOfBlankValue(dyFormData, requireFields));
            } else {
                // 从表判断
                List<DyFormData> dyFormDataList = dyFormData.getDyformDatas(formUuid);
                if (CollectionUtils.isNotEmpty(dyFormDataList)) {
                    for (DyFormData subformData : dyFormDataList) {
                        emptyValueFields.addAll(getRequiredFieldOfBlankValue(subformData, requireFields));
                    }
                }
            }
        }
        return CollectionUtils.isNotEmpty(emptyValueFields);
    }

    /**
     * 获取表单数据中空值的必填字段列表
     *
     * @param dyFormData
     * @param requireFields
     * @return
     */
    private List<String> getRequiredFieldOfBlankValue(DyFormData dyFormData, List<String> requireFields) {
        List<String> emptyValueFields = Lists.newArrayList();
        for (String requireField : requireFields) {
            if (!dyFormData.isFieldExist(requireField)) {
                continue;
            }
            Object fieldValue = dyFormData.getFieldValue(requireField);
            if (fieldValue == null || StringUtils.isBlank(Objects.toString(fieldValue))) {
                emptyValueFields.add(requireField);
            }
        }
        return emptyValueFields;
    }


    /**
     * 获取操作提示信息
     *
     * @param actionId
     * @param actionCode
     * @param taskInstUuid
     * @param taskId
     * @param toTaskId
     * @param taskIdentityUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    @Override
    public Map<String, Object> getActionTipWithActionId(String actionId, String actionCode, String taskInstUuid, String taskId, String toTaskId,
                                                        String taskIdentityUuid, String flowInstUuid, String flowDefUuid) {
        Map<String, Object> actionTip = Maps.newHashMap();
        WorkFlowPrivilege action = WorkFlowPrivilege.getByCode(actionId);
        if (action != null && !StringUtils.equals(WorkFlowPrivilege.Click.getCode(), actionId)) {
            switch (action) {
                case Submit:
                    actionTip = getSubmitActionTip(taskInstUuid, taskId, toTaskId, taskIdentityUuid, flowDefUuid);
                    break;
                case Rollback:
                    actionTip = getRollbackActionTip(taskInstUuid, taskId, taskIdentityUuid, flowInstUuid, flowDefUuid);
                    break;
                case DirectRollback:
                    actionTip = getDirectRollbackActionTip(taskInstUuid, null, taskId, taskIdentityUuid, flowInstUuid, flowDefUuid);
                    break;
                default:
                    break;
            }
        } else {
            FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
            Direction direction = flowDelegate.getDirectionByIdDigest(actionId);
            // 流向提交
            if (direction != null) {
                actionTip = getDirectionSubmitActionTip(actionId, taskInstUuid, taskIdentityUuid, taskId, flowDelegate);
            } else {
                // 自定义操作按钮提交、退回
                CustomDynamicButton customDynamicButton = new CustomDynamicButton();
                customDynamicButton.setNewCode(actionId);
                customDynamicButton.setCode(actionCode);
                CustomDynamicButton button = flowDelegate.convertTaskCustomDynamicButton(taskId, customDynamicButton);
                if (button != customDynamicButton) {
                    actionTip = getCustomDynamicButtonActionTip(taskInstUuid, taskIdentityUuid, taskId, button, flowDelegate);
                } else {
                    // actionTip = Maps.newHashMapWithExpectedSize(0);
                }
            }
        }
        return actionTip;
    }

    @Override
    @Transactional
    public String startGroupChat(String provider, StartGroupChat startGroupChat) {
        if (StringUtils.isBlank(startGroupChat.getFlowInstUuid())) {
            throw new BusinessException("流程实例UUID不能为空");
        }
        if (StringUtils.isBlank(startGroupChat.getTaskInstUuid())) {
            throw new BusinessException("环节实例UUID不能为空");
        }
        if (CollectionUtils.isEmpty(flowGroupChatProviders)) {
            return null;
        }

        for (FlowGroupChatProvider providerImpl : flowGroupChatProviders) {
            if (StringUtils.equals(provider, providerImpl.getProviderInfo().getId())) {
                return providerImpl.startGroupChat(startGroupChat);
            }
        }
        return null;
    }

    @Override
    public List<FlowGroupChatProvider.ProviderInfo> listGroupChatProvider() {
        List<FlowGroupChatProvider.ProviderInfo> providerInfos = Lists.newArrayList();
        for (FlowGroupChatProvider providerImpl : flowGroupChatProviders) {
            providerInfos.add(providerImpl.getProviderInfo());
        }
        return providerInfos;
    }

//    @Override
//    @Transactional(propagation = Propagation.REQUIRES_NEW)
//    public void translateFlowInstanceTitle(String flowInstanceUuid) {
//        Locale locale = LocaleContextHolder.getLocale();
//        try {
//            FlowInstance flowInstance = flowInstanceService.getOne(flowInstanceUuid);
//            FlowDefinition flowDefinition = flowInstance.getFlowDefinition();
//            DyFormData dyFormData = dyFormFacade.getDyFormData(flowInstance.getFormUuid(), flowInstance.getDataUuid());
//            Set<String> locales = appCodeI18nService.getAllLocaleString();
//            if (CollectionUtils.isNotEmpty(locales)) {
//                // 计算其他环境的标题
//                for (String l : locales) {
//                    if (l.equals(Locale.SIMPLIFIED_CHINESE.toString())) {
//                        continue;
//                    }
//                    String[] p = l.split(Separator.UNDERLINE.getValue());
//                    Locale tempLocale = new Locale(p[0], p[1]);
//                    LocaleContextHolder.setLocale(tempLocale);
//                    String t = TitleExpressionUtils.generateFlowInstanceTitle(flowDefinition, flowInstance, new TaskData(),
//                            dyFormData);
//                    Matcher matcher = CHINESE_PATTERN.matcher(t);
//                    Set<String> texts = Sets.newHashSet();
//                    while (matcher.find()) {
//                        texts.add(matcher.group());
//                    }
//                    Map<String, String> wordMap = translateService.translate(texts, Locale.SIMPLIFIED_CHINESE.getLanguage(), p[0]);
//                    Set<String> keys = wordMap.keySet();
//                    for (String k : keys) {
//                        t = t.replaceAll(k, wordMap.get(k));
//                    }
//                    FlowDataI18nEntity i18nEntity = new FlowDataI18nEntity();
//                    i18nEntity.setContent(t);
//                    i18nEntity.setLocale(l);
//                    i18nEntity.setDataId(flowInstance.getUuid());
//                    i18nEntity.setDataUuid(Long.parseLong(flowInstance.getUuid()));
//                    i18nEntity.setDataCode("title");
//                    dataI18nService.deleteByDataIdAndCodeAndLocale(flowInstance.getUuid(), "title", l, FlowDataI18nEntity.class);
//                    dataI18nService.save(i18nEntity);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("计算流程国际化标题异常", e);
//        } finally {
//            LocaleContextHolder.setLocale(locale);
//        }
//    }

    /**
     * 获取提交操作提示信息
     *
     * @param taskInstUuid
     * @param taskId
     * @param taskIdentityUuid
     * @param toTaskId
     * @param flowDefUuid
     * @return
     */
    private Map<String, Object> getSubmitActionTip(String taskInstUuid, String taskId, String toTaskId, String taskIdentityUuid, String flowDefUuid) {
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowDefUuid);
        TaskIdentity taskIdentity = null;
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            taskIdentity = identityService.get(taskIdentityUuid);
        }
        // 当前环节提交
        if (StringUtils.isNotBlank(taskInstUuid) && StringUtils.isBlank(toTaskId) && taskIdentity != null) {
            Integer todoType = taskIdentity.getTodoType();
            switch (todoType) {
                // 会签
                case 2:
                    return getTodoCounterSignSubmitActionTip(taskInstUuid, taskId, taskIdentity, flowDelegate);
                // 转办、委托
                case 3:
                case 5:
                    return getTodoTransferSubmitActionTip(taskInstUuid, taskId, taskIdentity, flowDelegate);
                default:
                    return getTodoSubmitActionTip(taskInstUuid, taskId, taskIdentity, flowDelegate);
            }
        } else {
            // 提交到下一环节
            return getSubmitToTaskActionTip(taskInstUuid, taskIdentityUuid, taskId, toTaskId, flowDelegate);
        }
    }

    /**
     * @param taskInstUuid
     * @param taskId
     * @param taskIdentity
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getTodoCounterSignSubmitActionTip(String taskInstUuid, String taskId, TaskIdentity taskIdentity, FlowDelegate flowDelegate) {
        TaskNode taskNode = (TaskNode) flowDelegate.getTaskNode(taskId);
        List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuid(taskInstUuid);
        // 按人员顺序依次办理
        if (taskNode.isByOrder()) {
            // 移除按人员顺序依次办理的人
            taskIdentities = taskIdentities.stream()
                    .filter(identity -> identity.getSortOrder() == null)
                    .collect(Collectors.toList());
            String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
            boolean isLastTaskIdentity = identityService
                    .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid);
            // 添加会签返回的待办标识
            if (isLastTaskIdentity) {
                taskIdentities.add(identityService.get(sourceTaskIdentityUuid));
            }
        }
        // 移除自身待办标识
        taskIdentities = taskIdentities.stream().filter(identity -> !StringUtils.equals(identity.getUuid(), taskIdentity.getUuid()))
                .collect(Collectors.toList());
        return getSubmitToTaskIdentityActionTip(taskIdentities);
    }

    /**
     * @param taskInstUuid
     * @param taskId
     * @param taskIdentity
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getTodoTransferSubmitActionTip(String taskInstUuid, String taskId, TaskIdentity taskIdentity, FlowDelegate flowDelegate) {
        // 转办、委托提交返回的按顺序办理处理
        TaskNode taskNode = (TaskNode) flowDelegate.getTaskNode(taskId);
        List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuid(taskInstUuid);
        if (taskNode.isByOrder()) {
            // 按人员顺序依次办理的人
            List<TaskIdentity> orderTaskIdentities = taskIdentities.stream()
                    .filter(identity -> identity.getSortOrder() != null)
                    .collect(Collectors.toList());
            String sourceTaskIdentityUuid = taskIdentity.getSourceTaskIdentityUuid();
            boolean isLastTaskIdentity = identityService
                    .isLastTodoTaskIdentityBySourceTaskIdentityUuid(sourceTaskIdentityUuid);
            // 转办、委托最后一个人办理
            if (isLastTaskIdentity) {
                // 只需要其中一个人办理
                if (taskNode.isAnyone()) {
                    // 提交到下一环节
                    return getSubmitToTaskActionTip(taskInstUuid, taskIdentity.getUuid(), taskId, null, flowDelegate);
                } else if (CollectionUtils.isNotEmpty(orderTaskIdentities)) {
                    // 下一个办理人办理
                    return getSubmitToTaskIdentityActionTip(Lists.newArrayList(orderTaskIdentities.get(0)));
                } else {
                    // 提交到下一环节
                    return getSubmitToTaskActionTip(taskInstUuid, taskIdentity.getUuid(), taskId, null, flowDelegate);
                }
            } else {
                // 转办、委托多个办理
                // 移除按顺序办理的人
                taskIdentities.removeAll(orderTaskIdentities);
                // 移除自身待办标识
                taskIdentities = taskIdentities.stream().filter(identity -> !StringUtils.equals(identity.getUuid(), taskIdentity.getUuid()))
                        .collect(Collectors.toList());
                return getSubmitToTaskIdentityActionTip(taskIdentities);
            }
        }
        return getTodoSubmitActionTip(taskInstUuid, taskId, taskIdentity, flowDelegate);
    }

    /**
     * @param taskInstUuid
     * @param taskId
     * @param taskIdentity
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getTodoSubmitActionTip(String taskInstUuid, String taskId, TaskIdentity taskIdentity, FlowDelegate flowDelegate) {
        String currentUserId = SpringSecurityUtils.getCurrentUserId();
        if (flowDelegate.isCollaborationTaskNode(taskId)) {
            // 将其他已办的人变为待办
            List<AclTaskEntry> todoSids = aclTaskService.getSid(taskInstUuid, AclPermission.TODO);
            List<AclTaskEntry> doneSids = aclTaskService.getSid(taskInstUuid, AclPermission.DONE);
            doneSids.removeAll(todoSids);
            Set<String> ownerTaskIdentityUuids = Sets.newHashSet();
            List<TaskIdentity> allIdentities = identityService.getByTaskInstUuid(taskInstUuid);
            List<TaskIdentity> todoIdentities = Lists.newArrayList();
            List<TaskIdentity> doneIdentities = Lists.newArrayList();
            allIdentities.forEach(identity -> {
                if (StringUtils.isBlank(identity.getSourceTaskIdentityUuid())) {
                    ownerTaskIdentityUuids.add(identity.getUuid());
                }
                if (Integer.valueOf(SuspensionState.NORMAL.getState()).equals(identity.getSuspensionState())
                        && !StringUtils.equals(identity.getUserId(), currentUserId)) {
                    todoIdentities.add(identity);
                } else if (Integer.valueOf(SuspensionState.DELETED.getState()).equals(identity.getSuspensionState())) {
                    doneIdentities.add(identity);
                }
            });
            Set<String> doneUserIdSet = Sets.newHashSet();
            if (taskIdentity != null && StringUtils.isNotBlank(taskIdentity.getSourceTaskIdentityUuid())
                    && (WorkFlowTodoType.Transfer.equals(taskIdentity.getTodoType()) ||
                    WorkFlowTodoType.Delegation.equals(taskIdentity.getTodoType()))) {
                doneUserIdSet.add(taskIdentity.getCreator());
            }
            List<TaskIdentity> appendIdentities = doneIdentities.stream().filter(doneTaskIdentity -> {
                String doneUserId = doneTaskIdentity.getUserId();
                if (StringUtils.equals(doneUserId, currentUserId) || doneUserIdSet.contains(doneUserId)) {
                    return false;
                } else if (WorkFlowTodoType.AddSign.equals(doneTaskIdentity.getTodoType())) {
                    if (!ownerTaskIdentityUuids.contains(doneTaskIdentity.getSourceTaskIdentityUuid())) {
                        return false;
                    }
                } else if (StringUtils.isNotBlank(doneTaskIdentity.getSourceTaskIdentityUuid())) {
                    return false;
                }
                doneUserIdSet.add(doneUserId);
                AclTaskEntry sid = doneSids.stream().filter(doneSid -> StringUtils.equals(doneSid.getSid(), doneUserId)).findFirst().orElse(null);
                return sid != null;
            }).collect(Collectors.toList());
            todoIdentities.addAll(appendIdentities);
            return getSubmitToTaskIdentityActionTip(todoIdentities);
        } else if (flowDelegate.isAnyone(taskId)) {
            // 1、只需要其中一个人办理
            // 提交到下一环节
            return getSubmitToTaskActionTip(taskInstUuid, taskIdentity != null ? taskIdentity.getUuid() : null, taskId, null, flowDelegate);
        } else if (flowDelegate.isByOrder(taskId)) {
            // 2、按人员顺序依次办理
            List<TaskIdentity> identities = identityService.getByOrdersByTaskInstUuid(taskInstUuid);
            TaskIdentity nextTaskIdentity = getNextTaskIdentity(identities, currentUserId);
            if (nextTaskIdentity != null) {
                // 提交到当前环节
                return getSubmitToTaskIdentityActionTip(Lists.newArrayList(nextTaskIdentity));
            } else {
                // 提交到下一环节
                return getSubmitToTaskActionTip(taskInstUuid, taskIdentity != null ? taskIdentity.getUuid() : null, taskId, null, flowDelegate);
            }
        } else {
            // 3、正常办理
            List<TaskIdentity> taskIdentities = identityService.getTodoByTaskInstUuid(taskInstUuid);
            TaskIdentity orgIdentity = taskIdentities.stream().filter(identity -> !StringUtils.startsWith(identity.getUserId(), IdPrefix.USER.getValue())).findFirst().orElse(null);
            if (CollectionUtils.size(taskIdentities) <= 1) {
                // 提交到下一环节
                if (orgIdentity == null || isCompleteSubmitOrgIdentity(currentUserId, orgIdentity, flowDelegate)) {
                    return getSubmitToTaskActionTip(taskInstUuid, taskIdentity != null ? taskIdentity.getUuid() : null, taskId, null, flowDelegate);
                } else {
                    return getSubmitToTaskIdentityActionTip(taskIdentities);
                }
            } else {
                // 提交到当前环节
                taskIdentities = taskIdentities.stream()
                        .filter(identity -> {
                            // 委托人提交
                            if (taskIdentity != null && WorkFlowTodoType.Delegation.equals(identity.getTodoType())
                                    && StringUtils.equals(identity.getSourceTaskIdentityUuid(), taskIdentity.getUuid())) {
                                return false;
                            }
                            return !StringUtils.equals(identity.getUserId(), currentUserId);
                        })
                        .collect(Collectors.toList());
                if (CollectionUtils.isEmpty(taskIdentities)) {
                    return getSubmitToTaskActionTip(taskInstUuid, taskIdentity != null ? taskIdentity.getUuid() : null, taskId, null, flowDelegate);
                } else {
                    // 提交到下一环节
                    List<TaskIdentity> otherUserIdentities = taskIdentities.stream().filter(identity -> {
                        return IdPrefix.startsUser(identity.getUserId()) && !StringUtils.equals(identity.getUserId(), currentUserId);
                    }).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(otherUserIdentities) &&
                            (orgIdentity == null || isCompleteSubmitOrgIdentity(currentUserId, orgIdentity, flowDelegate))) {
                        return getSubmitToTaskActionTip(taskInstUuid, taskIdentity != null ? taskIdentity.getUuid() : null, taskId, null, flowDelegate);
                    } else {
                        return getSubmitToTaskIdentityActionTip(taskIdentities);
                    }
                }
            }
        }
    }

    /**
     * @param userId
     * @param taskIdentity
     * @param flowDelegate
     * @return
     */
    private boolean isCompleteSubmitOrgIdentity(String userId, TaskIdentity taskIdentity, FlowDelegate flowDelegate) {
        Token token = new Token(flowDelegate, null, new TaskData());
        // 权限颗粒度大于用户
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        Map<String, String> userMap = workflowOrgService.getUsersByIds(Lists.newArrayList(taskIdentity.getUserId()), orgVersionIds);
        List<String> orgUserIds = Lists.newArrayList(userMap.keySet());
        List<String> sidDoneUserIds = aclTaskService.listSidDoneMarkerUserId(taskIdentity.getUserId(), taskIdentity.getTaskInstUuid());
        orgUserIds.removeAll(sidDoneUserIds);
        orgUserIds.remove(userId);
        return CollectionUtils.isEmpty(orgUserIds);
    }

    /**
     * 获取提交到当前环节的操作提示信息
     *
     * @param taskIdentities
     * @return
     */
    private Map<String, Object> getSubmitToTaskIdentityActionTip(List<TaskIdentity> taskIdentities) {
        Map<String, Object> actionTip = Maps.newHashMap();
        actionTip.put("actionName", WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
        actionTip.put("actionType", WorkFlowOperation.SUBMIT);
        actionTip.put("toTaskName", "当前环节");
        Set<String> userIdSet = taskIdentities.stream().map(identity -> identity.getUserId()).collect(Collectors.toSet());
        actionTip.put("todoUserNames", StringUtils.split(IdentityResolverStrategy.resolveAsNames(Lists.newArrayList(userIdSet)), Separator.SEMICOLON.getValue()));
        return actionTip;
    }

    /**
     * @param identities
     * @param userId
     * @return
     */
    private TaskIdentity getNextTaskIdentity(List<TaskIdentity> identities, String userId) {
        // 正常按顺序办理
        if (CollectionUtils.size(identities) > 1) {
            return identities.get(1);
        } else {
            return null;
        }
    }

    /**
     * 获取退回操作提示信息
     *
     * @param taskInstUuid
     * @param taskId
     * @param taskIdentityUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    private Map<String, Object> getRollbackActionTip(String taskInstUuid, String taskId, String taskIdentityUuid, String flowInstUuid, String flowDefUuid) {
        Map<String, Object> actionTip = Maps.newHashMap();
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        RollbackTaskActionExecutor rollbackTaskActionExecutor = ApplicationContextHolder.getBean(RollbackTaskActionExecutor.class);
        List<RollBackTask> rollBackTasks = rollbackTaskActionExecutor.buildToRollbackTasks(taskInstance);
        if (CollectionUtils.size(rollBackTasks) > 1) {
            actionTip.put("remark", "选择退回环节");
        } else if (CollectionUtils.size(rollBackTasks) == 1) {
            actionTip = getDirectRollbackActionTip(taskInstUuid, rollBackTasks.get(0).getTaskInstUuid(), rollBackTasks.get(0).getId(), taskIdentityUuid, flowInstUuid, flowDefUuid);
        }
        return actionTip;
    }

    /**
     * 获取直接退回操作提示信息
     *
     * @param taskInstUuid
     * @param taskId
     * @param taskIdentityUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    private Map<String, Object> getDirectRollbackActionTip(String currentTaskInstUuid, String rollbackToTaskInstUuid, String taskId, String taskIdentityUuid, String flowInstUuid, String flowDefUuid) {
        Map<String, Object> actionTip = Maps.newHashMap();
        RollbackTaskActionExecutor rollbackTaskActionExecutor = ApplicationContextHolder.getBean(RollbackTaskActionExecutor.class);
        RollBackTaskInfo rollBackTaskInfo = rollbackTaskActionExecutor.getDirectRollbackTaskInfo(currentTaskInstUuid, rollbackToTaskInstUuid, taskIdentityUuid);
        if (rollBackTaskInfo != null) {
            actionTip.put("actionName", WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK));
            actionTip.put("actionType", WorkFlowOperation.ROLLBACK);
            actionTip.put("toTaskName", StringUtils.equals(currentTaskInstUuid, rollBackTaskInfo.getTaskInstUuid()) ? "当前环节" : rollBackTaskInfo.getName());
            actionTip.put("todoUserNames", StringUtils.split(resolveUserIdAsName(StringUtils.join(rollBackTaskInfo.getUserIds(), Separator.SEMICOLON.getValue())), Separator.SEMICOLON.getValue()));
        }
        return actionTip;
    }

    /**
     * 获取流向提交操作提示信息
     *
     * @param actionId
     * @param taskInstUuid
     * @param taskIdentityUuid
     * @param taskId
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getDirectionSubmitActionTip(String actionId, String taskInstUuid, String taskIdentityUuid,
                                                            String taskId, FlowDelegate flowDelegate) {
        String actionName = WorkFlowOperation.getName(WorkFlowOperation.SUBMIT);
        String toTaskId = null;
        String toTaskName = null;
        String remark = null;
        Node to = flowDelegate.getToTaskNodeWidthDirectionIdDigest(taskId, actionId);
        if (to != null) {
            toTaskId = to.getId();
            toTaskName = to.getName();
            Direction direction = flowDelegate.getDirectionByIdDigest(actionId);
            if (direction != null) {
                // 不同送结束的流向toID相同，但流向名称可能不相同
                if (FlowConstant.END_FLOW_ID.equals(direction.getToID())) {
                    toTaskName = direction.getName();
                }
                if (direction.getShowRemark()) {
                    remark = direction.getRemark();
                }
            }
        }

        Map<String, Object> actionTip = getSubmitToTaskActionTip(taskInstUuid, taskIdentityUuid, taskId, toTaskId, flowDelegate);
        if (StringUtils.equals("送结束", toTaskName)) {
            actionTip.put("remark", "提交结束流程");
        } else {
            actionTip.put("actionName", actionName);
            actionTip.put("actionType", WorkFlowOperation.SUBMIT);
            actionTip.put("toTaskName", toTaskName);
            actionTip.put("remark", remark);
        }
        return actionTip;
    }

    /**
     * 自定义操作按钮提交、退回
     *
     * @param taskInstUuid
     * @param taskIdentityUuid
     * @param taskId
     * @param button
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getCustomDynamicButtonActionTip(String taskInstUuid, String taskIdentityUuid, String taskId, CustomDynamicButton button, FlowDelegate flowDelegate) {
        Map<String, Object> actionTip = Maps.newHashMap();
        String toTaskId = button.getTaskId();
        WorkFlowPrivilege action = WorkFlowPrivilege.getByCode(button.getCode());
        switch (action) {
            case Submit:
                actionTip = getSubmitToTaskActionTip(taskInstUuid, taskIdentityUuid, taskId, toTaskId, flowDelegate);
                Node to = flowDelegate.getTaskNode(toTaskId);
                List<String> rawUserIds = button.getUsers();
                if (to != null && CollectionUtils.isNotEmpty(rawUserIds)) {
                    Token token = null;
                    TaskData taskData = new TaskData();
                    taskData.setTaskId(taskId);
                    taskData.setUserId(SpringSecurityUtils.getCurrentUserId());
                    if (StringUtils.isNotBlank(taskInstUuid)) {
                        token = new Token(taskService.getTask(taskInstUuid), taskData);
                    } else {
                        token = new Token(flowDelegate, flowDelegate.getTaskNode(taskId), taskData);
                    }
                    List<FlowUserSid> userIds = IdentityResolverStrategy.resolveCustomBtnUsers(token, to,
                            rawUserIds, ParticipantType.TodoUser);
                    List<String> userNames = userIds.stream().map(sid -> sid.getName()).collect(Collectors.toList());
                    actionTip.put("todoUserNames", userNames);
                }
                break;
            case Rollback:
                RollbackTaskActionExecutor rollbackTaskActionExecutor = ApplicationContextHolder.getBean(RollbackTaskActionExecutor.class);
                TaskInstance taskInstance = taskService.getTask(taskInstUuid);
                List<RollBackTask> rollBackTasks = rollbackTaskActionExecutor.buildToRollbackTasks(taskInstance);
                RollBackTask rollBackTask = rollBackTasks.stream().filter(item -> StringUtils.equals(item.getId(), toTaskId)).findFirst().orElse(null);
                if (rollBackTask != null) {
                    actionTip.put("actionName", WorkFlowOperation.getName(WorkFlowOperation.ROLLBACK));
                    actionTip.put("actionType", WorkFlowOperation.ROLLBACK);
                    actionTip.put("toTaskName", StringUtils.equals(taskInstUuid, rollBackTask.getTaskInstUuid()) ? "当前环节" : rollBackTask.getName());
                    actionTip.put("todoUserNames", StringUtils.split(resolveUserIdAsName(StringUtils.join(taskService.getTaskOwners(rollBackTask.getTaskInstUuid()), Separator.SEMICOLON.getValue())), Separator.SEMICOLON.getValue()));
                }
                break;
        }
        return actionTip;
    }

    /**
     * @param taskInstUuid
     * @param taskId
     * @param toTaskId
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getSubmitToTaskActionTip(String taskInstUuid, String taskIdentityUuid, String taskId, String toTaskId, FlowDelegate flowDelegate) {
        String toTaskName = null;
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskData nextData = new TaskData();
        nextData.setUserId(userDetails.getUserId());
        nextData.setUserName(userDetails.getUserName());
        nextData.setToTaskId(taskId, toTaskId);
        if (StringUtils.isNotBlank(taskIdentityUuid)) {
            nextData.setTaskIdentityUuid(taskInstUuid + userDetails.getUserId(), taskIdentityUuid);
        }
        // 新建流程
        if (StringUtils.isBlank(taskInstUuid)) {
            nextData.setFormUuid(flowDelegate.getFlow().getProperty().getFormID());
            nextData.setTaskId(taskId);
            Token token = new Token(flowDelegate, flowDelegate.getTaskNode(taskId), nextData);
            Transition transition = token.getNode().getLeavingTransition();
            TransitionResolver transitionResolver = TransitionResolverFactory.getResolver(transition);
            FlowServiceImpl.getTaskConfigData(nextData, token, flowDelegate, transition, transitionResolver);
        } else {
            taskService.getNextConfigInfo(taskInstUuid, nextData);
        }

        String daiding = StringUtils.defaultIfBlank(AppCodeI18nMessageSource.getMessage("WorkflowWork.taskProcess.nextUnknownTaskName", "pt-workflow"), "待定...");
        String userNameDaiding = StringUtils.defaultIfBlank(AppCodeI18nMessageSource.getMessage("WorkflowWork.taskProcess.nextUnknownUserName", "pt-workflow"), "下一环节确定");
        List<String> taskNames = Arrays.asList(StringUtils.split(nextData.getTaskName(), Separator.COMMA.getValue()));
        List<String> taskRawUserNamesList = Arrays.asList(StringUtils.split(nextData.getTaskRawUserNames(), Separator.COMMA.getValue()));
        List<Map<String, Object>> actionTips = Lists.newArrayList();
        int index = 0;
        for (String taskName : taskNames) {
            String taskRawUserNames = taskRawUserNamesList.get(index);
            // 提交环节
            if (StringUtils.isBlank(toTaskId)) {
                if (StringUtils.equals(taskName, daiding)) {
                    toTaskName = StringUtils.EMPTY;
                } else {
                    toTaskName = taskName;
                }
            } else {
                toTaskName = nextData.getTaskName();
                if (StringUtils.isBlank(toTaskName)) {
                    toTaskName = flowDelegate.getTaskNode(toTaskId).getName();
                }
            }

            // 环节办理人
            taskRawUserNames = decorateRawNames(taskRawUserNames, flowDelegate.getFlow().getProperty().getFormID());
            List<String> todoUserNames = Arrays.asList(StringUtils.split(taskRawUserNames, Separator.SEMICOLON.getValue()));
            // 办理人为待定，清空列表
            if (todoUserNames.contains(userNameDaiding)) {
                todoUserNames = Lists.newArrayListWithCapacity(0);
            }

            Map<String, Object> actionTip = Maps.newHashMap();
            if (StringUtils.equals("结束", toTaskName)) {
                actionTip.put("remark", "提交结束流程");
            } else {
                actionTip.put("actionName", WorkFlowOperation.getName(WorkFlowOperation.SUBMIT));
                actionTip.put("actionType", WorkFlowOperation.SUBMIT);
                actionTip.put("toTaskName", toTaskName);
                actionTip.put("todoUserNames", todoUserNames);
                actionTip.put("taskId", nextData.getTaskId());
            }
            actionTips.add(actionTip);
            index++;
        }

        Map<String, Object> actionTip = null;
        if (CollectionUtils.isEmpty(actionTips)) {
            return Collections.emptyMap();
        } else if (CollectionUtils.size(actionTips) == 1) {
            return actionTips.get(0);
        } else {
            actionTip = Maps.newHashMap();
            actionTip.put("list", actionTips);
        }
        return actionTip;
    }

    /* lmw 2015-4-24 17:43 begin */
    private static class TitleExpression {
        private static String prefix = "${";
        private static String suffix = "}";
        private StringBuilder express;
        private int currentPositon = 0;

        public TitleExpression(String expression) {
            express = new StringBuilder(expression != null ? expression : "");
        }

        public boolean hasNext() {
            if (currentPositon >= express.length()) {
                return false;
            } else {
                int prefixPos = getPrefixPos();
                int suffixPos = getSuffixPos();
                return !(prefixPos >= suffixPos && prefixPos < currentPositon && suffixPos < currentPositon);
            }
        }

        public String next() {
            if (currentPositon >= express.length()) {
                return null;
            }
            int prefixPos = getPrefixPos();
            int suffixPos = getSuffixPos();
            if (prefixPos < currentPositon || suffixPos < currentPositon) {
                return null;
            }
            String next = express.substring(prefixPos + 2, suffixPos);
            currentPositon = suffixPos + 1;
            return next;
        }

        public void clear() {
            currentPositon = 0;
        }

        private int getPrefixPos() {
            return express.indexOf(prefix, currentPositon);
        }

        private int getSuffixPos() {
            return express.indexOf(suffix, currentPositon);
        }
    }
}
