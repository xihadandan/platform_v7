/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 解析表单域中的数据，来自组织选择框中的值
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-15.1	zhulh		2013-3-15		Create
 * </pre>
 * @date 2013-3-15
 */
@Component
public class FormFieldIdentityResolver extends AbstractIdentityResolver {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

//    @Autowired
//    private BusinessFacadeService businessFacadeService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * 解析字段中的组织值
     *
     * @param node
     * @param token
     * @param raws
     * @return java.util.List<java.lang.String>
     **/
    public static List<String> resolveOrgIds(Node node, Token token, List<String> raws) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        List<String> orgIds = new ArrayList<String>(0);
        if (CollectionUtils.isEmpty(raws)) {
            return orgIds;
        }

        TaskData taskData = token.getTaskData();
        String formUuid = taskData.getFormUuid();
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        // boolean startNewFlow = taskData.getStartNewFlow(token.getFlowInstance().getUuid());
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        if (dyFormData != null) {
            for (String formFieldName : raws) {
                if (isSubformField(formFieldName)) {
                    orgIds.addAll(resolveSubformFieldOrgIds(dyFormData, formFieldName, node));
                } else {
                    orgIds.addAll(resolveFormFieldOrgIds(dyFormData, formFieldName, node));
                }
            }
        }
        return orgIds;
    }

    /**
     * 解析字段中的组织值
     *
     * @return
     */
    public static List<String> resolveFormFieldOrgIds(DyFormData dyFormData, String formFieldName, Node node) {
        // BusinessFacadeService businessFacadeService = ApplicationContextHolder.getBean(BusinessFacadeService.class);
        //获取动态表单的字段值
        Object formValue = dyFormData.getFieldValue(formFieldName);
        String value = getOrgIdValue(formValue);
        // 子流程环节业务通讯录处理
        SubTaskNode subTaskNode = null;
        String businessType = null;
        String businessRole = null;
        if (node instanceof SubTaskNode) {
            subTaskNode = (SubTaskNode) node;
            businessType = subTaskNode.getBusinessType();
            businessRole = subTaskNode.getBusinessRole();
        }
        List<String> orgIds = Lists.newArrayList();
        List<String> bizEleIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(value)) {
            // 解析动态表单的字段值，包括部门、职位、用户ID
            String[] values = value.split(Separator.SEMICOLON.getValue());
            for (String orgId : values) {
                if (StringUtils.startsWith(orgId, IdPrefix.BIZ_PREFIX.getValue())
                        && StringUtils.startsWith(businessType, IdPrefix.BIZ_ORG.getValue())) {
                    bizEleIds.add(orgId);
                } else if (IdPrefix.startsWithExternal(orgId) && StringUtils.isNotBlank(businessType)
                        && StringUtils.isNotBlank(businessRole)) {
                    // orgIds.addAll(businessFacadeService.getUserByOrgUuidAndRoleUuid(businessType, orgId, businessRole));
                } else {
                    orgIds.add(orgId);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(bizEleIds)) {
            WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
            Map<String, String> userMap = workflowOrgService.getBizOrgUsersByIds(bizEleIds, businessType);
            orgIds.addAll(userMap.keySet());
        }
        return orgIds;
    }

    /**
     * 如何描述该方法
     *
     * @param formValue
     * @return
     */
    private static String getOrgIdValue(Object formValue) {
        String value = formValue == null ? StringUtils.EMPTY : formValue.toString();
        return value;
    }

    /**
     * @param dyFormData
     * @param fieldName
     * @return
     */
    private static Set<String> resolveSubformFieldOrgIds(DyFormData dyFormData, String fieldName, Node node) {
        Set<String> orgIds = Sets.newHashSet();
        String[] subformFields = StringUtils.split(fieldName, Separator.COLON.getValue());
        if (subformFields.length != 2) {
            return orgIds;
        }
        String subformUuid = getRealSubformUuid(subformFields[0], dyFormData);
        String subformFieldName = subformFields[1];
        List<DyFormData> dyFormDatas = dyFormData.getDyformDatas(subformUuid);
        if (CollectionUtils.isNotEmpty(dyFormDatas)) {
            for (DyFormData subformData : dyFormDatas) {
                orgIds.addAll(resolveFormFieldOrgIds(subformData, subformFieldName, node));
            }
        }
        return orgIds;
    }

    /**
     * @param fieldName
     * @return
     */
    private static boolean isSubformField(String fieldName) {
        return StringUtils.contains(fieldName, Separator.COLON.getValue());
    }

    /**
     * 根据配置的表单定义UUID获取表单数据中实际存在的表单定义UUID
     *
     * @param formUuid
     * @param dyFormData
     * @return
     */
    private static String getRealSubformUuid(String formUuid, DyFormData dyFormData) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        if (dyFormData.isFormUuidInThisForm(formUuid)) {
            return formUuid;
        }
        // 旧版本的表单定义UUID通过表单定义ID获取表单数据中存在的对应版本的表单定义UUID
        String formId = dyFormFacade.getFormIdByFormUuid(formUuid);
        if (StringUtils.isBlank(formId)) {
            return formUuid;
        }
        String realFormUuid = dyFormData.getFormUuidByFormId(formId);
        if (StringUtils.isBlank(realFormUuid)) {
            return formUuid;
        }
        if (dyFormData.isFormUuidInThisForm(realFormUuid)) {
            return realFormUuid;
        }
        return formUuid;
    }

    /**
     * (non-Javadoc)
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType,
                                     String sidGranularity) {
        List<FlowUserSid> sids = new ArrayList<FlowUserSid>(0);
        if (CollectionUtils.isEmpty(raws)) {
            return sids;
        }

        TaskData taskData = token.getTaskData();
        String formUuid = taskData.getFormUuid();
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        // boolean startNewFlow = taskData.getStartNewFlow(token.getFlowInstance().getUuid());
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        if (dyFormData != null) {
            for (String formFieldName : raws) {
                if (isSubformField(formFieldName)) {
                    sids.addAll(resolveSubformFieldUsers(dyFormData, formFieldName, node, token, participantType, sidGranularity));
                } else {
                    sids.addAll(resolveFormFieldUsers(dyFormData, formFieldName, node, token, participantType, sidGranularity));
                }
            }
        }
        return sids;
    }

    /**
     * @return
     */
    private List<FlowUserSid> resolveFormFieldUsers(DyFormData dyFormData, String formFieldName, Node node,
                                                    Token token, ParticipantType participantType, String sidGranularity) {
        List<FlowUserSid> sids = Lists.newArrayList();
        //获取动态表单的字段值
        Object formValue = dyFormData.getFieldValue(formFieldName);
        String value = getOrgIdValue(formValue);
        // 子流程环节业务通讯录处理
        SubTaskNode subTaskNode = null;
        String businessType = null;
        String businessRole = null;
        if (node instanceof SubTaskNode) {
            subTaskNode = (SubTaskNode) node;
            businessType = subTaskNode.getBusinessType();
            businessRole = subTaskNode.getBusinessRole();
        }
        List<String> orgIds = Lists.newArrayList();
        List<String> bizEleIds = Lists.newArrayList();
        List<String> idPaths = Lists.newArrayList();
        if (StringUtils.isNotBlank(value)) {
            // 解析动态表单的字段值，包括部门、职位、用户ID
            String[] values = value.split(Separator.SEMICOLON.getValue());
            for (String orgId : values) {
                if (StringUtils.startsWith(orgId, IdPrefix.BIZ_PREFIX.getValue())
                        && StringUtils.startsWith(businessType, IdPrefix.BIZ_ORG.getValue())) {
                    bizEleIds.add(orgId);
                    if (StringUtils.contains(orgId, Separator.SLASH.getValue())) {
                        idPaths.add(orgId);
                    }
                } else if (IdPrefix.startsWithExternal(orgId) && StringUtils.isNotBlank(businessType)
                        && StringUtils.isNotBlank(businessRole)) {
                    // orgIds.addAll(businessFacadeService.getUserByOrgUuidAndRoleUuid(businessType, orgId, businessRole));
                } else {
                    if (StringUtils.contains(orgId, Separator.SLASH.getValue())) {
                        String id = StringUtils.substringAfterLast(orgId, Separator.SLASH.getValue());
                        if (IdPrefix.hasPrefix(id)) {
                            orgIds.add(id);
                        } else {
                            String[] ids = StringUtils.split(orgId, Separator.SLASH.getValue());
                            orgIds.add(StringUtils.join(ArrayUtils.subarray(ids, ids.length - 2, ids.length), Separator.SLASH.getValue()));
                        }
                        idPaths.add(orgId);
                    } else {
                        orgIds.add(orgId);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(bizEleIds)) {
            Map<String, String> userMap = workflowOrgService.getBizOrgUsersByIds(bizEleIds, businessType);
            for (Map.Entry<String, String> entry : userMap.entrySet()) {
                sids.add(new FlowUserSid(entry.getKey(), entry.getValue()));
            }
        }
        sids.addAll(sidGranularityResolver.resolve(node, token, orgIds, sidGranularity));
        if (ParticipantType.TodoUser.equals(participantType) && CollectionUtils.isNotEmpty(sids) && CollectionUtils.isNotEmpty(idPaths)) {
            flowUserJobIdentityService.addUnitUserJobIdentity(sids, idPaths, false, node.getId(), token, participantType);
        }
        return sids;
    }

    /**
     * @param dyFormData
     * @param fieldName
     * @return
     */
    private Set<FlowUserSid> resolveSubformFieldUsers(DyFormData dyFormData, String fieldName, Node node, Token token,
                                                      ParticipantType participantType, String sidGranularity) {
        Set<FlowUserSid> userSids = Sets.newHashSet();
        String[] subformFields = StringUtils.split(fieldName, Separator.COLON.getValue());
        if (subformFields.length != 2) {
            return userSids;
        }
        String subformUuid = getRealSubformUuid(subformFields[0], dyFormData);
        String subformFieldName = subformFields[1];
        List<DyFormData> dyFormDatas = dyFormData.getDyformDatas(subformUuid);
        if (CollectionUtils.isNotEmpty(dyFormDatas)) {
            for (DyFormData subformData : dyFormDatas) {
                userSids.addAll(resolveFormFieldUsers(subformData, subformFieldName, node, token, participantType, sidGranularity));
            }
        }
        return userSids;
    }

}
