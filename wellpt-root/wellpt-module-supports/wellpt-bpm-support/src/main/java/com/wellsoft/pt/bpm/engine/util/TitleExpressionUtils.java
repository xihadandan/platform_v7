/*
 * @(#)2019年2月26日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.AbstractTitleGenerate;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import com.wellsoft.pt.app.service.AppDefElementI18nService;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.NewFlowElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSettings;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.dto.FieldDefinition;
import com.wellsoft.pt.jpa.template.TemplateEngine;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.i18n.LocaleContextHolder;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Description: 标题表达式工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年2月26日.1	zhulh		2019年2月26日		Create
 * </pre>
 * @date 2019年2月26日
 */
public class TitleExpressionUtils extends AbstractTitleGenerate {

    private static final Logger LOG = LoggerFactory.getLogger(TitleExpressionUtils.class);

    private static final String DEFAULT_TITLE_EXPRESSION = "${流程名称}_${发起人姓名}-${发起人所在部门名称}_${发起年}-${发起月}-${发起日}";


    /**
     * 生成子流程实例标题
     *
     * @param subFlowElement       子流程的子流程弹框数据
     * @param parentFlowDefinition 父流程定义
     * @param parentflowInstance   父流程的流程袖珍
     * @param taskData             环节数据
     * @param dyFormData           表单数据
     * @param todoName             环节办理人名称
     * @return
     */
    public static String generateSubFlowInstanceTitle(NewFlowElement subFlowElement,
                                                      FlowDefinition parentFlowDefinition, FlowInstance parentflowInstance, TaskData taskData,
                                                      DyFormData dyFormData, String todoName) {
        String titleExpression = subFlowElement.getTitleExpression();
        if (StringUtils.trimToEmpty(titleExpression).equals("")) {
            titleExpression = "${流程名称}_${子流程实例办理人}";
        }

        // 流程内置变量
        Map<String, Object> allData = getFlowCommonVariables(parentflowInstance.getCreator(), parentFlowDefinition,
                parentflowInstance);
        // 创建方式：1代表：生成单一实例 2代表：按办理人生成实例
        String SINGLE_INSTANCE = "1";
        String TRANSACTOR_GENERATE_INSTANCE = "2";
        String createInstanceWay = subFlowElement.getCreateInstanceWay();

        if (titleExpression.indexOf("子流程实例办理人") > 0) {

            if (SINGLE_INSTANCE.equals(createInstanceWay)) {
                // 生成单一实例
                allData.put("子流程实例办理人", "(" + todoName + ")");
            } else {
                // 按办理人生成实例
                allData.put("子流程实例办理人", todoName);
            }
        }

        // 表单主表变量
        addMainformVariables(allData, dyFormData);
        /* lmw 2015-4-24 15:12 begin */
        // 表单从表变量
        titleExpression = addSubformVariables(titleExpression, allData, dyFormData);

        /* lmw 2015-4-24 15:12 end */
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, allData);
        } catch (Exception ex) {
            /* modified by huanglinchuan 2014.12.15 begin */
            LOG.error(titleExpression);
            LOG.error(JsonUtils.object2Json(allData));
            LOG.error("流程标题解析错误：", ex);
            /* modified by huanglinchuan 2014.12.15 end */
        }
        return title;
    }

    /**
     * 生成流程实例标题
     *
     * @param flowDefinition
     * @param flowInstance
     * @param taskData
     * @param dyFormData
     * @return
     */
    public static String generateFlowInstanceTitle(FlowDefinition flowDefinition, FlowInstance flowInstance,
                                                   TaskData taskData, DyFormData dyFormData) {
        String titleExpression = flowDefinition.getTitleExpression();
        if (StringUtils.trimToEmpty(titleExpression).equals(StringUtils.EMPTY)) {
            WfFlowSettingService flowSettingService = ApplicationContextHolder.getBean(WfFlowSettingService.class);
            WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
            titleExpression = workFlowSettings.getTitleExpression();
            if (StringUtils.isBlank(titleExpression)) {
                titleExpression = DEFAULT_TITLE_EXPRESSION;
            }
        }

        // 流程内置变量
        Map<String, Object> allData = getFlowCommonVariables(flowInstance.getCreator(), flowDefinition, flowInstance);
        // 表单主表变量
        addMainformVariables(allData, dyFormData);
        /* lmw 2015-4-24 15:12 begin */
        // 表单从表变量
        titleExpression = addSubformVariables(titleExpression, allData, dyFormData);

        /* lmw 2015-4-24 15:12 end */
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, allData);
        } catch (Exception ex) {
            /* modified by huanglinchuan 2014.12.15 begin */
            LOG.error(flowInstance.getFlowDefinition().getTitleExpression());
            LOG.error(JsonUtils.object2Json(allData));
            LOG.error("流程标题解析错误：", ex);
            /* modified by huanglinchuan 2014.12.15 end */
        }
        return title;
    }

    /**
     * 生成流程实例标题
     *
     * @param flowDefinition
     * @param flowInstance
     * @param userId         发起人用户ID（流程创建人用户ID）
     * @param dyFormData
     * @return
     */
    public static String generateFlowInstanceTitle(FlowDefinition flowDefinition, FlowInstance flowInstance,
                                                   String userId, DyFormData dyFormData) {
        String titleExpression = flowDefinition.getTitleExpression();
        if (StringUtils.trimToEmpty(titleExpression).equals(StringUtils.EMPTY)) {
            WfFlowSettingService flowSettingService = ApplicationContextHolder.getBean(WfFlowSettingService.class);
            WorkFlowSettings workFlowSettings = flowSettingService.getWorkFlowSettings();
            titleExpression = workFlowSettings.getTitleExpression();
            if (StringUtils.isBlank(titleExpression)) {
                titleExpression = DEFAULT_TITLE_EXPRESSION;
            }
        }

        // 流程内置变量
        Map<String, Object> allData = getFlowCommonVariables(userId, flowDefinition, flowInstance);
        // 表单主表变量
        addMainformVariables(allData, dyFormData);
        /* lmw 2015-4-24 15:12 begin */
        // 表单从表变量
        titleExpression = addSubformVariables(titleExpression, allData, dyFormData);

        /* lmw 2015-4-24 15:12 end */
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, allData);
        } catch (Exception ex) {
            /* modified by huanglinchuan 2014.12.15 begin */
            LOG.error(flowInstance.getFlowDefinition().getTitleExpression());
            LOG.error(JsonUtils.object2Json(allData));
            LOG.error("流程标题解析错误：", ex);
            /* modified by huanglinchuan 2014.12.15 end */
        }
        return title;
    }

    /**
     * @param expression
     * @param flowDefinition
     * @param flowInstance
     * @param taskData
     * @param dyFormData
     * @param formDataVariableModes
     * @return
     */
    public static String generateExpressionString(String expression, FlowDefinition flowDefinition, FlowInstance flowInstance,
                                                  TaskData taskData, DyFormData dyFormData, List<String> formDataVariableModes) {
        if (StringUtils.isBlank(expression)) {
            return StringUtils.EMPTY;
        }

        // 流程内置变量
        Map<String, Object> allData = getFlowCommonVariables(flowInstance.getCreator(), flowDefinition, flowInstance);
        allData.put("流程标题", flowInstance.getTitle());
        // 表单主表变量
        addMainformVariables(expression, allData, dyFormData, formDataVariableModes);
        if (expression.indexOf("${表单数据结构}") != -1) {
            allData.put("表单数据结构", dyFormData.geFormDataDisplayValueString());
        }
        /* lmw 2015-4-24 15:12 end */
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(expression, allData);
        } catch (Exception ex) {
            /* modified by huanglinchuan 2014.12.15 begin */
            LOG.error(expression);
            LOG.error(JsonUtils.object2Json(allData));
            LOG.error("流程字符表达式解析错误：", ex);
            /* modified by huanglinchuan 2014.12.15 end */
        }
        return title;
    }

    /**
     * 流程内置变量
     *
     * @param starterUserId
     * @param flowDefinition
     * @param flowInstance   流程实例 非子流程，子流程传Null即可
     * @return
     */
    public static Map<String, Object> getFlowCommonVariables(String starterUserId, FlowDefinition flowDefinition,
                                                             FlowInstance flowInstance) {
        Map<String, Object> commonVariables = AbstractTitleGenerate.getCommonVariables();
        commonVariables.put("流程名称", flowDefinition.getName());
        commonVariables.put("流程ID", flowDefinition.getId());
        commonVariables.put("流程编号", flowDefinition.getCode());
        commonVariables.put("flowDefName", flowDefinition.getName());
        commonVariables.put("flowDefId", flowDefinition.getId());
        commonVariables.put("flowCode", flowDefinition.getCode());
        AppDefElementI18nService appDefElementI18nService = ApplicationContextHolder.getBean(AppDefElementI18nService.class);
        if (!Locale.SIMPLIFIED_CHINESE.toString().equals(LocaleContextHolder.getLocale().toString())) {
            AppDefElementI18nEntity i18nEntity = appDefElementI18nService.getI18n(flowDefinition.getId(), null, "workflowName", new BigDecimal(flowDefinition.getVersion()), IexportType.FlowDefinition
                    , LocaleContextHolder.getLocale().toString());
            if (i18nEntity != null && StringUtils.isNotEmpty(i18nEntity.getContent())) {
                commonVariables.put("flowDefName", i18nEntity.getContent());
                commonVariables.put("流程名称", i18nEntity.getContent());
            }
        }
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (IdPrefix.startsUser(starterUserId)) {
            if (StringUtils.equals(userDetails.getUserId(), starterUserId) && Locale.SIMPLIFIED_CHINESE.toString().equals(LocaleContextHolder.getLocale().toString())) {
                UserSystemOrgDetails userSystemOrgDetails = userDetails.getUserSystemOrgDetails();
                UserSystemOrgDetails.OrgDetail orgDetail = userSystemOrgDetails.currentSystemOrgDetail();
                commonVariables.put("发起人姓名", userDetails.getUserName());
                if (orgDetail != null) {
                    OrgTreeNodeDto mainJob = orgDetail.getMainJob();
                    if (mainJob != null) {
                        commonVariables.put("发起人所在部门名称", mainJob.getDeptName());
                        commonVariables.put("发起人所在部门名称全路径", mainJob.getDeptNamePath());
                    } else {
                        commonVariables.put("发起人所在部门名称", StringUtils.EMPTY);
                        commonVariables.put("发起人所在部门名称全路径", StringUtils.EMPTY);
                    }
                } else {
                    commonVariables.put("发起人所在部门名称", StringUtils.EMPTY);
                    commonVariables.put("发起人所在部门名称全路径", StringUtils.EMPTY);
                }

            } else {
                WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
                commonVariables.put("发起人姓名", workflowOrgService.getNameById(starterUserId));
                List<OrgUserJobDto> userJobDtos = null;
                if (flowInstance != null) {
                    userJobDtos = workflowOrgService.listUserJobs(starterUserId, flowInstance.getOrgVersionId());
                } else {
                    userJobDtos = workflowOrgService.listUserJobs(starterUserId);
                }
                OrgUserJobDto mainJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst()
                        .orElse(CollectionUtils.isNotEmpty(userJobDtos) ? userJobDtos.get(0) : null);
                String jobPath = mainJob != null ? mainJob.getJobIdPath() : StringUtils.EMPTY;
//            String jobPath = userVo.getMainJobIdPath();
                String depId = MultiOrgTreeNode.getNearestEleIdByType(jobPath, IdPrefix.DEPARTMENT.getValue());
                if (StringUtils.isNotBlank(depId)) {
//                MultiOrgElement depEle = orgApiFacade.getOrgElementById(depId);
//                commonVariables.put("发起人所在部门名称", depEle == null ? "" : depEle.getName());
                    commonVariables.put("发起人所在部门名称", workflowOrgService.getNameById(depId));
                } else {
                    commonVariables.put("发起人所在部门名称", StringUtils.EMPTY);
                }
//            String departmentPath = orgApiFacade.getDepartmentNamePathByJobIdPath(userVo.getMainJobIdPath(), false);
//            commonVariables.put("发起人所在部门名称全路径", departmentPath);
                commonVariables.put("发起人所在部门名称全路径", getUserDepartmentPath(userJobDtos));
            }
        } else {
            UserInfoFacadeService userInfoFacadeService = ApplicationContextHolder.getBean(UserInfoFacadeService.class);
            if (userInfoFacadeService.isNotStaffUser(starterUserId)) {
                commonVariables.put("发起人姓名",
                        userInfoFacadeService.getFullInternetUserByLoginName(starterUserId, null).getUserName());
            }
        }
        commonVariables.put("startUserName", commonVariables.get("发起人姓名"));
        commonVariables.put("startUserDepartmentName", commonVariables.get("发起人所在部门名称"));
        commonVariables.put("startUserDepartmentPathName", commonVariables.get("发起人所在部门名称全路径"));

        // 增加发起年、发起月、发起日、发起时、发起分、发起秒、发起简年内置变量 2021-06-15需求
        if (flowInstance != null) {
            Date startTime = flowInstance.getStartTime();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startTime);
            int year = calendar.get(Calendar.YEAR);// 获取年份
            int month = calendar.get(Calendar.MONTH) + 1;// 获取月份
            int day = calendar.get(Calendar.DATE);// 获取日
            int hour = calendar.get(Calendar.HOUR_OF_DAY);// 小时
            int minute = calendar.get(Calendar.MINUTE);// 分
            int second = calendar.get(Calendar.SECOND);// 秒
            commonVariables.put("发起年", String.valueOf(year));
            commonVariables.put("发起月", DateUtil.getFormatDate(month));
            commonVariables.put("发起日", DateUtil.getFormatDate(day));
            commonVariables.put("发起时", DateUtil.getFormatDate(hour));
            commonVariables.put("发起分", DateUtil.getFormatDate(minute));
            commonVariables.put("发起秒", DateUtil.getFormatDate(second));
            commonVariables.put("发起简年", String.valueOf(year).substring(2));
        }
        return commonVariables;
    }

    /**
     * @param userJobDtos
     * @return
     */
    private static String getUserDepartmentPath(List<OrgUserJobDto> userJobDtos) {
        String deptName = StringUtils.EMPTY;
        if (CollectionUtils.isEmpty(userJobDtos)) {
            return deptName;
        }

        OrgUserJobDto userJob = userJobDtos.stream().filter(job -> job.isPrimary()).findFirst().orElse(userJobDtos.get(0));
        List<String> idPaths = Arrays.asList(StringUtils.split(userJob.getJobIdPath(), Separator.SLASH.getValue()));
        List<String> namePaths = Arrays.asList(StringUtils.split(userJob.getJobNamePath(), Separator.SLASH.getValue()));
        if (CollectionUtils.size(idPaths) != CollectionUtils.size(namePaths)) {
            return deptName;
        }

        for (int index = idPaths.size() - 1; index >= 0; index--) {
            String deptId = idPaths.get(index);
            if (StringUtils.startsWith(deptId, IdPrefix.DEPARTMENT.getValue())) {
                Object[] deptNamePaths = ArrayUtils.subarray(namePaths.toArray(new String[0]), 0, index + 1);
                return StringUtils.join(deptNamePaths, Separator.SLASH.getValue());
            }
        }
        return deptName;
    }

    /**
     * @param titleExpression
     * @param flowDelegate
     * @param parentTaskInstance
     * @param parentFlowInstance
     * @param parentFlowDefinition
     * @return
     */
    public static String getUndertakeSituationTitleExpression(String titleExpression, FlowDelegate flowDelegate,
                                                              TaskInstance parentTaskInstance, FlowInstance parentFlowInstance, FlowDefinition parentFlowDefinition) {
        // 流程内置变量
        Map<String, Object> allData = getFlowCommonVariables(parentFlowInstance.getStartUserId(), parentFlowDefinition,
                null);
        // 子流程分发变量
        addDistributeVariables(allData, parentTaskInstance.getStartTime());
        if (StringUtils.contains(titleExpression, "$")) {
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            DyFormData dyFormData = dyFormFacade.getDyFormData(parentFlowInstance.getFormUuid(),
                    parentFlowInstance.getDataUuid());
            // 表单主表变量
            addMainformVariables(allData, dyFormData);
            /* lmw 2015-4-24 15:12 begin */
            // 表单从表变量
            titleExpression = addSubformVariables(titleExpression, allData, dyFormData);
        }

        /* lmw 2015-4-24 15:12 end */
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, allData);
        } catch (Exception ex) {
            LOG.error("办理情况标题解析错误：", ex);
        }
        return title;
    }

    /**
     * @param titleExpression
     * @param flowDelegate
     * @param taskInstance
     * @param flowInstance
     * @param flowDefinition
     * @return
     */
    public static String getBranchTaskInfoTitleExpression(String titleExpression, FlowDelegate flowDelegate,
                                                          TaskInstance taskInstance, FlowInstance flowInstance, FlowDefinition flowDefinition) {
        // 流程内置变量
        Map<String, Object> allData = getFlowCommonVariables(flowInstance.getStartUserId(), flowDefinition,
                flowInstance);
        // 分支流分发变量
        addDistributeVariables(allData, taskInstance.getEndTime());
        if (StringUtils.contains(titleExpression, "$")) {
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            DyFormData dyFormData = dyFormFacade.getDyFormData(flowInstance.getFormUuid(), flowInstance.getDataUuid());
            // 表单主表变量
            addMainformVariables(allData, dyFormData);
            /* lmw 2015-4-24 15:12 begin */
            // 表单从表变量
            titleExpression = addSubformVariables(titleExpression, allData, dyFormData);
        }

        /* lmw 2015-4-24 15:12 end */
        TemplateEngine templateEngine = TemplateEngineFactory.getDefaultTemplateEngine();
        String title = StringUtils.EMPTY;
        try {
            title = templateEngine.process(titleExpression, allData);
        } catch (Exception ex) {
            LOG.error("办理情况标题解析错误：", ex);
        }
        return title;
    }

    /**
     * @param allData
     * @param distributeTime
     */
    private static void addDistributeVariables(Map<String, Object> allData, Date distributeTime) {
        // ${分发年},${分发简年},${分发月},${分发日},${分发时},${分发分},${分发秒}
        Map<String, Object> subTaskVariables = Maps.newHashMap();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(distributeTime);
        int year = calendar.get(Calendar.YEAR);// 获取年份
        int month = calendar.get(Calendar.MONTH) + 1;// 获取月份
        int day = calendar.get(Calendar.DATE);// 获取日
        int hour = calendar.get(Calendar.HOUR_OF_DAY);// 小时
        int minute = calendar.get(Calendar.MINUTE);// 分
        int second = calendar.get(Calendar.SECOND);// 秒
        subTaskVariables.put("分发年", String.valueOf(year));
        subTaskVariables.put("分发月", DateUtil.getFormatDate(month));
        subTaskVariables.put("分发日", DateUtil.getFormatDate(day));
        subTaskVariables.put("分发时", DateUtil.getFormatDate(hour));
        subTaskVariables.put("分发分", DateUtil.getFormatDate(minute));
        subTaskVariables.put("分发秒", DateUtil.getFormatDate(second));
        subTaskVariables.put("分发简年", String.valueOf(year).substring(2));

        // ${发起年},${发起简年},${发起月},${发起日},${发起时},${发起分},${发起秒}
        subTaskVariables.put("发起年", String.valueOf(year));
        subTaskVariables.put("发起月", DateUtil.getFormatDate(month));
        subTaskVariables.put("发起日", DateUtil.getFormatDate(day));
        subTaskVariables.put("发起时", DateUtil.getFormatDate(hour));
        subTaskVariables.put("发起分", DateUtil.getFormatDate(minute));
        subTaskVariables.put("发起秒", DateUtil.getFormatDate(second));
        subTaskVariables.put("发起简年", String.valueOf(year).substring(2));
        allData.putAll(subTaskVariables);
    }

    /**
     * @param expression
     * @param allData
     * @param dyFormData
     * @param formDataVariableModes
     */
    public static void addMainformVariables(String expression, Map<String, Object> allData, DyFormData dyFormData, List<String> formDataVariableModes) {
        addMainformVariables(allData, dyFormData);

        if (CollectionUtils.isNotEmpty(formDataVariableModes) && formDataVariableModes.contains("label")) {
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(dyFormData.getFormUuid());
            boolean showDisplayValue = formDataVariableModes.contains("label");
            boolean showRealValue = formDataVariableModes.contains("value");
            Iterator<DyformFieldDefinition> it = dyFormFormDefinition.doGetFieldDefintions().iterator();
            while (it.hasNext()) {
                FieldDefinition fieldDefinition = (FieldDefinition) it.next();

                String displayValueString = StringUtils.EMPTY;
                String realValueString = StringUtils.EMPTY;
                String fieldName = fieldDefinition.getFieldName();
                if (!StringUtils.contains(expression, fieldName)) {
                    continue;
                }

                // 显示值
                if (showDisplayValue) {
                    Object displayValue = dyFormData.getFieldDisplayValue(fieldName);
                    displayValueString = StringUtils.trim(Objects.toString(displayValue, StringUtils.EMPTY));
                }
                // 真实值
                if (showRealValue && !dyFormData.isFileField(fieldName)) {
                    realValueString = Objects.toString(dyFormData.getFieldValue(fieldName), StringUtils.EMPTY);
                    if (StringUtils.equals(displayValueString, realValueString) && StringUtils.isNotBlank(realValueString)) {
                        realValueString = StringUtils.EMPTY;
                    }
                }

                String fieldValue = StringUtils.EMPTY;
                // 拼接显示值、真实值
                if (StringUtils.isNotBlank(displayValueString) && StringUtils.isNotBlank(realValueString)) {
                    if (StringUtils.equals(displayValueString, realValueString)) {
                        fieldValue = displayValueString;
                    } else {
                        fieldValue = displayValueString + "(" + realValueString + ")";
                    }
                } else if (StringUtils.isNotBlank(displayValueString)) {
                    fieldValue = displayValueString;
                } else if (StringUtils.isNotBlank(realValueString)) {
                    fieldValue = realValueString;
                }
                allData.put(fieldName, fieldValue);
            }
        }
    }

    /**
     * @param allData
     * @param dyFormData
     */
    public static void addMainformVariables(Map<String, Object> allData, DyFormData dyFormData) {
        Map<String, Object> dyformMainData = dyFormData.getFormDataOfMainform();
        if (dyformMainData != null) {
            allData.putAll(dyformMainData);
            for (String f : dyformMainData.keySet()) {
                if (dyFormData.isFileField(f) && dyformMainData.get(f) != null) {
                    Object files = dyformMainData.get(f);
                    if (files instanceof List && CollectionUtils.isNotEmpty(((List) files))) {
                        allData.put(f + "的所有文件名", dyFormData.getFileNamesOfField(files));
                    } else if (files instanceof Map && MapUtils.isNotEmpty(((Map) files))) {
                        allData.put(f + "的所有文件名", dyFormData.getFileNamesOfField(files));
                    }
                }
            }
        }
    }

    /**
     * @param titleExpression
     * @param allData
     * @param dyFormData
     */
    public static String addSubformVariables(String titleExpression, Map<String, Object> allData,
                                             DyFormData dyFormData) {
        TitleExpression express = new TitleExpression(titleExpression);
        String ch = null;
        while (express.hasNext()) {
            ch = express.next();
            if (ch.indexOf("#") == 0) {
                String ts = ch.substring(1);
                String[] t = ts.split("#");
                if (t.length == 2) {
                    String tt = fetchLetter(ch);
                    titleExpression = titleExpression.replace(ch, tt);
                    List<Map<String, Object>> subFormDatas = dyFormData.getFormDatasById(t[0]);
                    String value = "";
                    if (subFormDatas != null && subFormDatas.size() != 0) {
                        Object o = subFormDatas.get(0).get(t[1]);
                        if (o != null) {
                            value = String.valueOf(o);
                        }
                    }
                    allData.put(tt, value);
                }
            }
        }
        express = null;
        return titleExpression;
    }

    private static String resolveAsNames(List<String> sids) throws ExecutionException, InterruptedException {

        if (CollectionUtils.isEmpty(sids)) {
            return "";
        }
        List<CompletableFuture<String>> completableFutures = Lists.newArrayList();
        for (String sid : sids) {
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                String name = "";
                name = resolveAsName(sid);
                return name;
            });
            completableFutures.add(future);
        }

        // 开多个线程去跑
        StringBuilder nameStr = new StringBuilder();
        CompletableFuture.allOf(completableFutures.toArray(new CompletableFuture[sids.size()]))
                .whenComplete((v, th) -> {
                    // 等待所有结果完成
                });
        for (CompletableFuture<String> completableFuture : completableFutures) {
            nameStr.append(completableFuture.get());
            nameStr.append("、");
        }
        String nameStrTemp = nameStr.toString();
        return nameStrTemp.substring(0, nameStrTemp.length() - 1);
    }

    /**
     * (non-Javadoc)
     */
    private static String resolveAsName(String sid) {
        if (StringUtils.isBlank(sid)) {
            return sid;
        }
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        return workflowOrgService.getNameById(sid);
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        MultiOrgJobDutyFacade multiOrgJobDutyFacade = ApplicationContextHolder.getBean(MultiOrgJobDutyFacade.class);
//        if (sid.startsWith(IdPrefix.USER.getValue())) {
//            MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(sid);
//            if (user != null) {
//                return user.getUserName();
//            }
//        } else if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
//            MultiOrgElement department = orgApiFacade.getOrgElementById(sid);
//            if (department != null) {
//                return department.getName();
//            }
//        } else if (sid.startsWith(IdPrefix.GROUP.getValue())) {
//            MultiOrgGroup group = orgApiFacade.getGroupById(sid);
//            if (group != null) {
//                return group.getName();
//            }
//        } else if (sid.startsWith(IdPrefix.JOB.getValue())) {
//            MultiOrgElement job = orgApiFacade.getOrgElementById(sid);
//            if (job != null) {
//                return job.getName();
//            }
//        } else if (sid.startsWith("W") || sid.startsWith(IdPrefix.DUTY.getValue())) {
//            MultiOrgDuty duty = orgApiFacade.getDutyById(sid);
//            if (duty != null) {
//                return duty.getName();
//            }
//        } else {
//            MultiOrgElement element = orgApiFacade.getOrgElementById(sid);
//            if (element != null) {
//                return element.getName();
//            } else if (IdPrefix.startsWithExternal(sid)) {
//                // 业务通讯录的业务分类结点
//                BusinessFacadeService businessFacadeService = ApplicationContextHolder
//                        .getBean(BusinessFacadeService.class);
//                BusinessCategoryOrgDto businessCategoryOrgDto = businessFacadeService.getBusinessById(sid);
//                if (businessCategoryOrgDto != null) {
//                    return businessCategoryOrgDto.getName();
//                }
//            }
//        }
//        return sid;
    }

}
