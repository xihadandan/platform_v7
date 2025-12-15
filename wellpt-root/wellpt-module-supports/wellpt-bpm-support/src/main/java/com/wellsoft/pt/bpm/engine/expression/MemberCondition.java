/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.access.FormFieldIdentityResolver;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.JobIdentity;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
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
 * 2013-4-26.1	zhulh		2013-4-26		Create
 * </pre>
 * @date 2013-4-26
 */
public class MemberCondition extends AbstractCondition {

    private static final String IS_MEMBER_PATTERN = "@ISMEMBER\\(\".*?(?=\"\\).*)";

    private static final String IS_MEMBER_PREFIX = "@ISMEMBER(\"";

    private static final String CURRENT_USER = "<CURUSER>";

    private String extraData;

    private MemberConditionConfig config;

    /**
     * @param expression
     */
    public MemberCondition(String expression) {
        super(expression);
    }

    /**
     * @param expression
     */
    public MemberCondition(String expression, String extraData) {
        super(expression);
        if (StringUtils.isNotBlank(extraData) && extraData.startsWith("{")) {
            this.config = JsonUtils.json2Object(extraData, MemberConditionConfig.class);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.expression.Condition#evaluate(com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public String evaluate(Token token, Node to) {
        String expression = this.getExpression().trim();
        StringBuilder sb = new StringBuilder();

//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        FormFieldIdentityResolver formFieldIdentityResolver = ApplicationContextHolder.getBean(FormFieldIdentityResolver.class);
        TaskData taskData = token.getTaskData();
        DyFormData mainData = taskData.getDyFormData(taskData.getDataUuid());
        // 使用正则表达式的最小匹配、零宽断言
        Pattern pattern = Pattern.compile(IS_MEMBER_PATTERN);
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String group = matcher.group();
            String member = group.substring(IS_MEMBER_PREFIX.length());
            String[] members = member.split("\",\"");
            if (members.length != 2) {
                continue;
            }

            // 获取选择的用户
            String rawUserId = members[0];
            String rawLimitUserId = members[1];
            List<String> userIds = new ArrayList<String>();
            if (CURRENT_USER.equals(rawUserId)) {
                userIds.add(SpringSecurityUtils.getCurrentUserId());
            } else {
                Object value = mainData.getFieldValue(rawUserId);
                if (value != null) {
                    userIds.addAll(IdentityResolverStrategy.resolveUserIds(Arrays.asList(value.toString().split(
                            Separator.SEMICOLON.getValue()))));
                }
            }

            // 用户范围组织是否表单字段
            boolean isFormValue = isFormValueOfLimitUser(rawLimitUserId);
            // 更新用户范围组织或表单字段值
            rawLimitUserId = updateLimitUserIdIfRequired(rawLimitUserId, mainData.getFormDataOfMainform());
            String[] rawUserIds = new String[0];
            List<String> rawLimitUserIds = new ArrayList<String>();
            List<FlowUserSid> limitUserSids = new ArrayList<FlowUserSid>();
            // 获取表单字段的用户范围
            if (isFormValue) {
                rawUserIds = StringUtils.split(rawLimitUserId, Separator.SEMICOLON.getValue());
                rawLimitUserIds.add(getFormFieldOfLimitUser(members[1]));
                limitUserSids.addAll(formFieldIdentityResolver.resolve(to, token, rawLimitUserIds, ParticipantType.TodoUser));
            } else {
                // 获取组织的用户范围
                if (config != null && CollectionUtils.isNotEmpty(config.getValue())) {
                    IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder.getBean(IdentityResolverStrategy.class);
                    limitUserSids.addAll(identityResolverStrategy.resolve(to, token, config.getValue(), ParticipantType.TodoUser, SidGranularity.USER));
                } else {
                    rawLimitUserId = rawLimitUserId.replaceAll(",", " , ");
                    rawUserIds = StringUtils.split(rawLimitUserId, Separator.COMMA.getValue());
                    for (int i = 0; i < rawUserIds.length; i++) {
                        rawUserIds[i] = StringUtils.trim(rawUserIds[i]);
                    }
                    if (rawUserIds.length == 5) {
                        for (int i = 1; i < rawUserIds.length; i++) {
                            rawLimitUserIds.add(rawUserIds[i]);
                        }
                        limitUserSids.addAll(IdentityResolverStrategy.resolveCustomBtnUsers(token, to, rawLimitUserIds,
                                ParticipantType.TodoUser));
                    }
                }
            }

            String preTaskId = token.getTask() != null ? token.getTask().getId() : token.getTaskData().getPreTaskId(to.getId());
            if (StringUtils.isBlank(preTaskId)) {
                preTaskId = StringUtils.isNotBlank(taskData.getTaskId()) ? taskData.getTaskId() : to.getId();
            }
            JobIdentity jobIdentity = token.getFlowDelegate().getJobIdentity(preTaskId);
//            String multiJobFlowType = jobIdentity.getMultiJobFlowType();// token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
//            boolean isMemberOfMailJob = FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType);
//            boolean isMemberOfSelectedJob = FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType);
            String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
            String jobId = null;//isMemberOfSelectedJob ? getFlowByUserSelectJob(preTaskId, taskData, token) : null;
            // 限制范围的用户ID
            List<String> limitUserIds = getLimitUserIds(limitUserSids);
            // 用户包含判断
            if (CollectionUtils.isNotEmpty(userIds)) {
                for (String userId : userIds) {
                    if (jobIdentity.isUserSelectJob()) {
                        jobId = getFlowByUserSelectJob(preTaskId, taskData, token);
                    } else if (jobIdentity.isUserMainJob()) {
                        jobId = workflowOrgService.listUserJobIdentity(userId, orgVersionIds).stream()
                                .filter(j -> j.isPrimary()).map(j -> j.getJobId())
                                .collect(Collectors.joining(Separator.SEMICOLON.getValue()));
                    }
//                    if (orgApiFacade.isMemberOf(userId, rawUserIds,
//                            isMemberOfMailJob ? orgApiFacade.getUserMainJobId(userId) : (isMemberOfSelectedJob ? jobId : null)
//                    )) {
//                        sb.append("true");
//                        break;
//                    }
                    FlowUserSid flowUserSid = getFlowUserSid(userId, limitUserSids);
                    if (flowUserSid != null || workflowOrgService.isMemberOf(userId, rawUserIds, orgVersionIds)) {
                        // 身份匹配
                        if (StringUtils.isNotBlank(jobId)) {
                            List<String> jobIds = Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue()));
                            boolean matchJobIdentity = false;
                            if (flowUserSid != null) {
                                if (CollectionUtils.isEmpty(flowUserSid.getOrgUserJobDtos())
                                        || flowUserSid.getOrgUserJobDtos().stream().filter(jobDto -> jobIds.contains(jobDto.getJobId())).findFirst().isPresent()) {
                                    matchJobIdentity = true;
                                }
//                                for (String jobIdentityId : jobIds) {
//                                    if (workflowOrgService.isMemberOf(jobIdentityId, rawUserIds, orgVersionIds)) {
//                                        matchJobIdentity = true;
//                                        break;
//                                    }
//                                }
                            }
                            if (matchJobIdentity) {
                                sb.append("true");
                                break;
                            } else {
                                sb.append("false");
                                break;
                            }
                        } else {
                            sb.append("true");
                            break;
                        }
                    }
                    if (!limitUserIds.contains(userId)) {
                        sb.append("false");
                        break;
                    }
                }
            } else {
                sb.append("false");
            }
        }

        // 设置返回表达式
        String returnExpression = sb.toString();
        if (StringUtils.isBlank(returnExpression)) {
            if (expression.startsWith("&")) {
                returnExpression = " & true ";
            } else if (expression.startsWith("|")) {
                returnExpression = " | true ";
            } else {
                returnExpression = " true ";
            }
        } else {
            if (expression.startsWith("&")) {
                returnExpression = " & " + returnExpression;
            } else if (expression.startsWith("|")) {
                returnExpression = " | " + returnExpression;
            }
        }
        return returnExpression;
    }

    /**
     * @param userId
     * @param limitUserSids
     * @return
     */
    private FlowUserSid getFlowUserSid(String userId, List<FlowUserSid> limitUserSids) {
        for (FlowUserSid flowUserSid : limitUserSids) {
            if (StringUtils.equals(userId, flowUserSid.getId())) {
                return flowUserSid;
            }
        }
        return null;
    }

    /**
     * @param limitUserSids
     * @return
     */
    private List<String> getLimitUserIds(List<FlowUserSid> limitUserSids) {
        List<String> limitUserIds = Lists.newArrayList();
        for (FlowUserSid limitUserSid : limitUserSids) {
            limitUserIds.add(limitUserSid.getId());
        }
        return limitUserIds;
    }

    /**
     * @param rawLimitUserId
     * @return
     */
    private boolean isFormValueOfLimitUser(String rawLimitUserId) {
        return StringUtils.endsWith(rawLimitUserId, ":2");
    }

    /**
     * @param rawLimitUserId
     * @return
     */
    private String getFormFieldOfLimitUser(String rawLimitUserId) {
        if (StringUtils.endsWith(rawLimitUserId, ":1") || StringUtils.endsWith(rawLimitUserId, ":2")) {
            String[] parts = StringUtils.split(rawLimitUserId, Separator.SPACE.getValue());
            int formValueIndex = parts.length - 1;
            String formValueType = StringUtils.substring(parts[formValueIndex], parts[formValueIndex].length() - 1);
            // 常量值或表单字段值
            String formValue = StringUtils.substring(parts[formValueIndex], 0, parts[formValueIndex].length() - 2);
            // 表单字段值
            if (StringUtils.equals(formValueType, VALUE_TYPE_FORM_FIELD)) {
                return formValue;
            }
        }
        return rawLimitUserId;
    }

    /**
     * @param rawLimitUserId
     * @param formDataOfMainform
     * @return
     */
    private String updateLimitUserIdIfRequired(String rawLimitUserId, Map<String, Object> root) {
        if (StringUtils.endsWith(rawLimitUserId, ":1") || StringUtils.endsWith(rawLimitUserId, ":2")) {
            String[] parts = StringUtils.split(rawLimitUserId, Separator.SPACE.getValue());
            int formValueIndex = parts.length - 1;
            String formValueType = StringUtils.substring(parts[formValueIndex], parts[formValueIndex].length() - 1);
            // 常量值或表单字段值
            String formValue = StringUtils.substring(parts[formValueIndex], 0, parts[formValueIndex].length() - 2);
            // 表单字段值
            if (StringUtils.equals(formValueType, VALUE_TYPE_FORM_FIELD)) {
                formValue = ObjectUtils.toString(root.get(formValue));
            }
            parts[parts.length - 1] = formValue;
            rawLimitUserId = StringUtils.join(parts, Separator.SPACE.getValue());
        }
        return rawLimitUserId;
    }

    private String getFlowByUserSelectJob(String taskId, TaskData taskData, Token token) {
        FlowUserJobIdentityService flowUserJobIdentityService = ApplicationContextHolder.getBean(FlowUserJobIdentityService.class);
        return flowUserJobIdentityService.getUserSelectJobId(taskData.getUserId(), token.getFlowDelegate().getTaskNode(taskId), token);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class MemberConditionConfig extends BaseObject {
        private List<UserUnitElement> value;

        /**
         * @return the value
         */
        public List<UserUnitElement> getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(List<UserUnitElement> value) {
            this.value = value;
        }
    }
}
