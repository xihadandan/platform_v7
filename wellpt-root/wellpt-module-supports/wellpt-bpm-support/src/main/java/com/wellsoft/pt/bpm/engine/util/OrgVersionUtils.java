/*
 * @(#)2018年6月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.FlowElement;
import com.wellsoft.pt.bpm.engine.element.MultiOrgElement;
import com.wellsoft.pt.bpm.engine.element.PropertyElement;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.org.entity.OrgVersionEntity;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;
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
 * 2018年6月14日.1	zhulh		2018年6月14日		Create
 * </pre>
 * @date 2018年6月14日
 */
public class OrgVersionUtils {
    // 组织版本类型，现在确定
    private static String ORG_VERSION_TYPE_1 = "1";

    /**
     * 获取流程默认的组织版本
     *
     * @param token
     * @return
     */
    public static String getFlowOrgVersionId(Token token) {
        return getFlowOrgVersionId(token, null);
    }

    /**
     * 获取流程默认的组织版本
     *
     * @param token
     * @return
     */
    public static String getFlowOrgVersionId(Token token, String userId) {
        TaskData taskData = token.getTaskData();
        FlowElement flowElement = token.getFlowDelegate().getFlow();
        // 获取当前运行数据的组织版本
        String orgVersionId = (String) taskData.get("orgVersionId_" + flowElement.getUuid());
        if (StringUtils.isNotBlank(orgVersionId)) {
            return orgVersionId;
        }

        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        PropertyElement flowProperty = flowElement.getProperty();
        if (token.getFlowInstance() != null) {
            orgVersionId = token.getFlowInstance().getOrgVersionId();
        }
        // 默认组织版本
        if (flowProperty.getIsUseDefaultOrg()) {
            String defaultOrgVersionId = workflowOrgService.getOrgVersionId(token.getFlowDelegate());
            // 自动升级到最新版本
            if (flowProperty.getIsAutoUpgradeOrgVersion()) {
                if (StringUtils.equals(orgVersionId, defaultOrgVersionId)) {
                    orgVersionId = workflowOrgService.getLatestOrgVersionId(orgVersionId);
                } else {
                    orgVersionId = defaultOrgVersionId;
                }
            } else if (StringUtils.isBlank(orgVersionId)) {
                orgVersionId = workflowOrgService.getOrgVersionId(token.getFlowDelegate());
            }
        } else {
            // 指定组织版本
            String specifyOrgVersionId = workflowOrgService.getOrgVersionId(token.getFlowDelegate());
            // 自动升级到最新版本
            if (flowProperty.getIsAutoUpgradeOrgVersion()) {
                if (StringUtils.isNotBlank(specifyOrgVersionId)) {
                    orgVersionId = workflowOrgService.getLatestOrgVersionId(specifyOrgVersionId);
                }
            } else if (StringUtils.isBlank(orgVersionId)) {
                orgVersionId = specifyOrgVersionId;// workflowOrgService.getOrgVersionId(token.getFlowDelegate());
            }
        }

        // 设置当前运行数据的组织版本
        taskData.put("orgVersionId_" + flowElement.getUuid(), orgVersionId);
        return orgVersionId;
    }

    /**
     * 解析环节承办人自定义的组织版本
     *
     * @param token
     * @param orgVersionIdString
     * @return
     */
    public static String resolve(Token token, String orgVersionIdString) {
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        if (StringUtils.isBlank(orgVersionIdString)) {
            return StringUtils.EMPTY;
        }
        String[] orgVersions = StringUtils.split(orgVersionIdString, Separator.COLON.getValue());
        if (orgVersions.length != 2) {
            return StringUtils.EMPTY;
        }
        String orgVersionType = orgVersions[0];
        String orgVersionId = orgVersions[1];
        // 现在确定
        if (StringUtils.equals(orgVersionType, ORG_VERSION_TYPE_1)) {
            if (StringUtils.startsWith(orgVersionId, IdPrefix.ORG.getValue())) {
                String latestOrgVersionId = workflowOrgService.getOrgVersionIdByOrgId(orgVersionId);
                if (StringUtils.isNotBlank(latestOrgVersionId)) {
                    orgVersionId = latestOrgVersionId;
                }
            }
            return orgVersionId;
        }
        // 文档域
        TaskData taskData = token.getTaskData();
        String formUuid = taskData.getFormUuid();
        String dataUuid = taskData.getDataUuid();
        DyFormData dyFormData = taskData.getDyFormData(dataUuid);
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        // 获取表单域的值
        if (!dyFormData.isFieldExist(orgVersionId)) {
            return StringUtils.EMPTY;
        }
        Object fieldValueObject = dyFormData.getFieldValue(orgVersionId);
        if (fieldValueObject == null || StringUtils.isBlank(fieldValueObject.toString())) {
            return StringUtils.EMPTY;
        }
        // 根据表单域的组织元素ID获取其所在的组织版本
        String fieldValue = fieldValueObject.toString();
        List<String> orgVersionIds = workflowOrgService.listOrgVersionIdsByOrgIds(Lists.newArrayList(fieldValue));
//        Set<String> orgVersionIds = orgApiFacade.getCurrentOrgVersionByOrgId(Arrays.asList(StringUtils.split(
//                fieldValue, Separator.SEMICOLON.getValue())));
        if (orgVersionIds.isEmpty()) {
            return StringUtils.EMPTY;
        }
        return orgVersionIds.iterator().next();
    }

    public static Set<String> getAvailableFlowOrgVersionIds(Token token) {
        TaskData taskData = token.getTaskData();
        Set<String> availableOrgVersionIds = (Set<String>) taskData.get("availableOrgVersionIds_" + token.getFlowDelegate().getFlow().getUuid());
        if (availableOrgVersionIds != null) {
            return availableOrgVersionIds;
        }
        // 当前运行的组织
        availableOrgVersionIds = Sets.newLinkedHashSet();
        String currentOrgVersionId = getFlowOrgVersionId(token, null);
        if (StringUtils.isNotBlank(currentOrgVersionId)) {
            availableOrgVersionIds.add(currentOrgVersionId);
        }

        // 多组织审批
        PropertyElement flowProperty = token.getFlowDelegate().getFlow().getProperty();
        if (flowProperty.getIsEnableMultiOrg()) {
            WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
            List<MultiOrgElement> multiOrgElements = flowProperty.getMultiOrgs();
            if (CollectionUtils.isNotEmpty(multiOrgElements)) {
                List<String> orgIds = multiOrgElements.stream().map(MultiOrgElement::getOrgId).collect(Collectors.toList());
                String system = RequestSystemContextPathResolver.system();
                if (StringUtils.isNotBlank(system)) {
                    availableOrgVersionIds.addAll(workflowOrgService.listOrgVersionIdsByOrgIdsAndSystem(orgIds, system));
                } else {
                    availableOrgVersionIds.addAll(workflowOrgService.listOrgVersionIdsByOrgIds(orgIds));
                }
            } else {
                String multiOrgId = flowProperty.getMultiOrgId();
                if (StringUtils.isNotBlank(multiOrgId)) {
                    List<String> orgIds = Arrays.asList(StringUtils.split(multiOrgId, Separator.SEMICOLON.getValue()));
                    availableOrgVersionIds.addAll(workflowOrgService.listOrgVersionIdsByOrgIds(orgIds));
                }
            }
        }

        // 设置当前运行数据可用的组织版本
        taskData.put("availableOrgVersionIds_" + token.getFlowDelegate().getFlow().getUuid(), availableOrgVersionIds);
        return availableOrgVersionIds;
    }

    public static String[] getAvailableFlowOrgVersionIdsAsArray(Token token) {
        return getAvailableFlowOrgVersionIds(token).toArray(new String[0]);
    }

    public static String[] getFlowOrgVersionIdsAsArray(String orgId, Token token) {
        if (StringUtils.isNotBlank(orgId)) {
            TaskData taskData = token.getTaskData();
            String orgVersionId = (String) taskData.get("orgVersionId_" + orgId);
            if (StringUtils.isBlank(orgVersionId)) {
                WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
                orgVersionId = workflowOrgService.getOrgVersionIdByOrgId(orgId);
                if (StringUtils.isNotBlank(orgVersionId)) {
                    taskData.put("orgVersionId_" + orgId, orgVersionId);
                }
            }

            if (StringUtils.isNotBlank(orgVersionId)) {
                return new String[]{orgVersionId};
            }
        }
        return getAvailableFlowOrgVersionIds(token).toArray(new String[0]);
    }

    public static void addOrgVersionInfo(Map<String, Object> variables, Token token) {
        String currentOrgVersionId = getFlowOrgVersionId(token);
        Set<String> orgVersionIds = getAvailableFlowOrgVersionIds(token);
        if (CollectionUtils.isNotEmpty(orgVersionIds)) {
            Set<String> enabledOrgVersionIds = Sets.newHashSet();
            WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
            List<OrgVersionEntity> entities = workflowOrgService.listOrgVersionByOrgVersionIds(orgVersionIds);
            Map<String, Long> orgUuidMap = entities.stream().collect(Collectors.toMap(OrgVersionEntity::getId, OrgVersionEntity::getOrgUuid));
            List<Long> orgUuids = entities.stream().map(OrgVersionEntity::getOrgUuid).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(orgUuids)) {
                List<OrganizationDto> organizationDtos = workflowOrgService.listOrganizationByOrgUuids(orgUuids);
                Date currentTime = Calendar.getInstance().getTime();
                List<Long> enabledOrgUuids = organizationDtos.stream().filter(entity -> {
                    if (BooleanUtils.isFalse(entity.getEnable())) {
                        return false;
                    }
                    Date expireTime = entity.getExpireTime();
                    if (expireTime != null && org.apache.commons.lang3.BooleanUtils.isFalse(entity.getExpired())
                            && org.apache.commons.lang3.BooleanUtils.isFalse(entity.getNeverExpire()) && expireTime.before(currentTime)) {
                        return false;
                    }
                    return true;
                }).map(OrganizationDto::getUuid).collect(Collectors.toList());
                orgUuidMap.forEach((orgVersionId, orgUuid) -> {
                    if (enabledOrgUuids.contains(orgUuid)) {
                        enabledOrgVersionIds.add(orgVersionId);
                    }
                });
            }
            if (CollectionUtils.isNotEmpty(enabledOrgVersionIds) && !enabledOrgVersionIds.contains(currentOrgVersionId)) {
                currentOrgVersionId = enabledOrgVersionIds.iterator().next();
                orgVersionIds = enabledOrgVersionIds;
            }
        }
        variables.put("currentOrgVersionId", currentOrgVersionId);
        variables.put("orgVersionIds", orgVersionIds);
        variables.put("orgIdBizOrgIdMap", getAvailableFlowOrgIdBizOrgIdMap(token));
    }

    public static Map<String, List<String>> getAvailableFlowOrgIdBizOrgIdMap(Token token) {
        Map<String, List<String>> orgIdBizOrgIdMap = Maps.newLinkedHashMap();
        // 多业务组织
        PropertyElement flowProperty = token.getFlowDelegate().getFlow().getProperty();
        String availableBizOrg = flowProperty.getAvailableBizOrg();
        String orgId = flowProperty.getOrgId();
        boolean isAutoUpgradeOrgVersion = flowProperty.getIsAutoUpgradeOrgVersion();
        if (!isAutoUpgradeOrgVersion) {
            WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
            String orgversionId = token.getFlowInstance().getOrgVersionId();
            if (StringUtils.isNotBlank(orgversionId)) {
                String runtimeOrgId = workflowOrgService.getOrgIdByOrgVersionId(orgversionId);
                if (StringUtils.isNotBlank(runtimeOrgId)) {
                    orgId = runtimeOrgId;
                }
            }
        }
        // 全部业务组织
        if (StringUtils.equals("all", availableBizOrg)) {
            orgIdBizOrgIdMap.put(orgId, Collections.emptyList());
        } else if (StringUtils.equals("assign", availableBizOrg)) {
            // 指定业务组织
            String bizOrgId = flowProperty.getBizOrgId();
            if (StringUtils.isNotBlank(bizOrgId)) {
                List<String> bizOrgIds = Arrays.asList(StringUtils.split(bizOrgId, Separator.SEMICOLON.getValue()));
                orgIdBizOrgIdMap.put(orgId, bizOrgIds);
            } else {
                orgIdBizOrgIdMap.put(orgId, Collections.emptyList());
            }
        } else {
            // 禁用
            orgIdBizOrgIdMap.put(orgId, Lists.newArrayList("disabled"));
        }

        // 多组织审批
        if (flowProperty.getIsEnableMultiOrg()) {
            List<MultiOrgElement> multiOrgElements = flowProperty.getMultiOrgs();
            if (CollectionUtils.isNotEmpty(multiOrgElements)) {
                multiOrgElements.forEach(multiOrgElement -> {
                    String bizOrgId = multiOrgElement.getBizOrgId();
                    // 全部业务组织
                    if (StringUtils.equals("all", multiOrgElement.getAvailableBizOrg())) {
                        orgIdBizOrgIdMap.put(multiOrgElement.getOrgId(), Collections.emptyList());
                    } else if (StringUtils.equals("assign", multiOrgElement.getAvailableBizOrg())) {
                        // 指定业务组织
                        if (StringUtils.isNotBlank(bizOrgId)) {
                            List<String> bizOrgIds = Arrays.asList(StringUtils.split(bizOrgId, Separator.SEMICOLON.getValue()));
                            orgIdBizOrgIdMap.put(multiOrgElement.getOrgId(), bizOrgIds);
                        } else {
                            orgIdBizOrgIdMap.put(multiOrgElement.getOrgId(), Collections.emptyList());
                        }
                    } else {
                        // 禁用
                        orgIdBizOrgIdMap.put(multiOrgElement.getOrgId(), Lists.newArrayList("disabled"));
                    }
                });
            }
        }

        return orgIdBizOrgIdMap;
    }

    /**
     * 获取可用的业务组织ID
     *
     * @param bizOrgId
     * @param token
     * @return
     */
    public static String getAvailableBizOrgId(String bizOrgId, Token token) {
        return bizOrgId;
//        if (StringUtils.isBlank(bizOrgId)) {
//            return bizOrgId;
//        }
//
//        Map<String, List<String>> bizOrgIdMap = getAvailableFlowOrgIdBizOrgIdMap(token);
//        List<String> avaiableBizOrgIds = bizOrgIdMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
//        if (avaiableBizOrgIds.contains(bizOrgId)) {
//            return bizOrgId;
//        }
//
//        TaskData taskData = token.getTaskData();
//        String availableBizOrgId = (String) taskData.get("availableBizOrgId_" + bizOrgId);
//        if (StringUtils.isNotBlank(availableBizOrgId)) {
//            return availableBizOrgId;
//        }
//
//        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
//        String orgId = workflowOrgService.getOrgIdByBizOrgId(bizOrgId);
//        if (bizOrgIdMap.containsKey(orgId) && CollectionUtils.isEmpty(bizOrgIdMap.get(orgId))) {
//            taskData.put("availableBizOrgId_" + bizOrgId, bizOrgId);
//            return bizOrgId;
//        }
//        return bizOrgId + "_disabled";
    }

}
