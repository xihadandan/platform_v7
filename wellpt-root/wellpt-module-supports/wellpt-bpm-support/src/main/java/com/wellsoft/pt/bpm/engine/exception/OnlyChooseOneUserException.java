/*
 * @(#)2013-4-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.exception;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.service.SystemParamsFacadeService;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

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
 * 2013-4-3.1	zhulh		2013-4-3		Create
 * </pre>
 * @date 2013-4-3
 */
public class OnlyChooseOneUserException extends WorkFlowException {

    private static final long serialVersionUID = 2688175019361816586L;

    private String title;
    private String taskName;
    private String taskId;
    private List<String> userIds;
    private Map<String, String> userJobIdentityMap;
    private List<Map<String, String>> users;
    private Map<String, Integer> eleOrderMap;

    private String submitButtonId;

    private String currentOrgVersionId;
    private Set<String> orgVersionIds;
    private Map<String, List<String>> orgIdBizOrgIdMap;
    private String bizOrgId;
    private String sidGranularity;

    private String defaultViewStyle = "list";
    private String autoViewStyleNum = "20";
    private String viewFormMode = "default";

    public OnlyChooseOneUserException(Node node, Token token, String taskName, String taskId, Collection<FlowUserSid> userIds, String submitButtonId,
                                      List<Map<String, String>> users, List<UserUnitElement> unitElements) {
        // 解析办理人员编号
//		final Map<String, String> codeByOrgIds = IdentityResolverStrategy.resolveCodeByOrgIds(userIds);
//		Collections.sort(userIds, new Comparator<String>() {
//			@Override
//			public int compare(String o1, String o2) {
//				String code1 = codeByOrgIds.get(o1);
//				String code2 = codeByOrgIds.get(o2);
//				if (code1 != null && code2 != null) {
//					return code1.compareTo(code2);
//				}
//				return 0;
//			}
//		});
        // 解析办理人详情
//        IdentityResolverStrategy.resolveOrgDetail(users);
//        eleOrderMap = getOrgEleOrderByEleIdPaths(users);
//        Collections.sort(users, new Comparator<Map<String, String>>() {
//            @Override
//            public int compare(Map<String, String> o1, Map<String, String> o2) {
//                String code1 = o1.get("code");
//                String code2 = o2.get("code");
//                if (code1 != null && code2 != null) {
//                    return code1.compareTo(code2);
//                }
//                return 0;
//            }
//        });
        this.title = "(" + token.getFlowDelegate().getFlow().getName() + ":" + taskName + ")";
        this.taskName = taskName;
        this.taskId = taskId;
        this.userIds = userIds.stream().map(FlowUserSid::getId).collect(Collectors.toList());
        this.userJobIdentityMap = userIds.stream().collect(Collectors.toMap(FlowUserSid::getId, FlowUserSid::getIdentityId));
        this.users = users;
        this.submitButtonId = submitButtonId;
        Map<String, Object> orgVersionInfo = Maps.newHashMap();
        OrgVersionUtils.addOrgVersionInfo(orgVersionInfo, token);
        this.currentOrgVersionId = (String) orgVersionInfo.get("currentOrgVersionId");// OrgVersionUtils.getFlowOrgVersionId(token);
        this.orgVersionIds = (Set<String>) orgVersionInfo.get("orgVersionIds");// OrgVersionUtils.getAvailableFlowOrgVersionIds(token);
        this.orgIdBizOrgIdMap = (Map<String, List<String>>) orgVersionInfo.get("orgIdBizOrgIdMap");//OrgVersionUtils.getAvailableFlowOrgIdBizOrgIdMap(token);
        this.bizOrgId = extractBizOrgId(unitElements, userIds);
        this.sidGranularity = token.getFlowDelegate().getGranularity(node.getId(), token.getTaskData(), unitElements);
        limitOrgVersion(unitElements);

        SystemParamsFacadeService systemParamsFacadeService = ApplicationContextHolder.getBean(SystemParamsFacadeService.class);
        this.defaultViewStyle = systemParamsFacadeService.getValue("unit.dialog.task.users.defaultViewStyle", this.defaultViewStyle);
        this.autoViewStyleNum = systemParamsFacadeService.getValue("unit.dialog.task.users.autoViewStyle.num", this.autoViewStyleNum);
    }

    /**
     * 限制当前流程实例的组织版本
     *
     * @param unitElements
     */
    private void limitOrgVersion(List<UserUnitElement> unitElements) {
        if (CollectionUtils.isEmpty(unitElements)) {
            return;
        }

        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        List<String> orgIds = unitElements.stream().filter(unitElement -> StringUtils.isNotBlank(unitElement.getOrgId())).map(UserUnitElement::getOrgId).collect(Collectors.toList());
        List<String> bizOrgIds = unitElements.stream().filter(unitElement -> StringUtils.isNotBlank(unitElement.getBizOrgId())).map(UserUnitElement::getOrgId).collect(Collectors.toList());
        List<String> orgIdsOfBizOrgIds = Lists.newArrayListWithExpectedSize(0);
        if (CollectionUtils.isNotEmpty(bizOrgIds)) {
            orgIdsOfBizOrgIds.addAll(workflowOrgService.listOrgIdByBizOrgIds(bizOrgIds));
        }
        List<String> allOrgIds = Lists.newArrayList(orgIds);
        allOrgIds.addAll(orgIdsOfBizOrgIds);
        if (CollectionUtils.isNotEmpty(allOrgIds)) {
            List<String> orgVersionIds = workflowOrgService.listOrgVersionIdsByOrgIds(allOrgIds);
            if (CollectionUtils.isNotEmpty(orgVersionIds)) {
                if (!orgVersionIds.contains(this.currentOrgVersionId)) {
                    this.currentOrgVersionId = orgVersionIds.get(0);
                }
                this.orgVersionIds = Sets.newHashSet(orgVersionIds);
                this.orgIdBizOrgIdMap = Maps.newHashMap(this.orgIdBizOrgIdMap);
                orgIds.forEach(orgId -> {
                    if (!bizOrgIds.contains(orgId)) {
                        this.orgIdBizOrgIdMap.remove(orgId);
                    }
                });
            }
        }
    }

    private String extractBizOrgId(List<UserUnitElement> unitElements, Collection<FlowUserSid> flowUserSids) {
        if (CollectionUtils.isEmpty(unitElements)) {
            return null;
        }

        boolean hasOrgJobIdentity = hasOrgJobIdentity(flowUserSids);

        String bizOrgId = null;
        for (UserUnitElement unitElement : unitElements) {
            if (Integer.valueOf(32).equals(unitElement.getType())) {
                // 多个业务组织
                if (StringUtils.isNotBlank(bizOrgId)) {
                    bizOrgId = null;
                    break;
                } else {
                    bizOrgId = unitElement.getBizOrgId();
                }
            } else if (hasOrgJobIdentity) {
                bizOrgId = null;
                break;
            }
        }
        return bizOrgId;
    }

    /**
     * @param flowUserSids
     * @return
     */
    private boolean hasOrgJobIdentity(Collection<FlowUserSid> flowUserSids) {
        boolean hasOrgJobIdentity = false;
        List<FlowUserSid> userSids = flowUserSids.stream().filter(flowUserSid -> CollectionUtils.isNotEmpty(flowUserSid.getOrgUserJobDtos())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(userSids)) {
            for (FlowUserSid userSid : userSids) {
                List<OrgUserJobDto> orgUserJobDtos = userSid.getOrgUserJobDtos().stream().filter(orgUserJobDto -> orgUserJobDto.getOrgVersionUuid() != null).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(orgUserJobDtos)) {
                    hasOrgJobIdentity = true;
                    break;
                }
            }
        } else {
            hasOrgJobIdentity = true;
        }
        return hasOrgJobIdentity;
    }

    /**
     * @param orgInfos
     * @return
     */
//    private Map<String, Integer> getOrgEleOrderByEleIdPaths(List<Map<String, String>> orgInfos) {
//        Set<String> paths = Sets.newHashSet();
//        for (Map<String, String> orgInfo : orgInfos) {
//            String mainJobIdPath = orgInfo.get("mainJobIdPath");
//            if (StringUtils.isBlank(mainJobIdPath)) {
//                continue;
//            }
//            String[] idPaths = mainJobIdPath.split(Separator.SLASH.getValue());
//            if (idPaths.length <= 1) {
//                continue;
//            }
//            List<String> tempPaths = Lists.newArrayList();
//            tempPaths.add(idPaths[0]);
//            for (int index = 1; index < idPaths.length; index++) {
//                tempPaths.add(idPaths[index]);
//                paths.add(StringUtils.join(tempPaths, Separator.SLASH.getValue()));
//            }
//        }
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        return orgApiFacade.getOrgEleOrderByEleIdPaths(Arrays.asList(paths.toArray(new String[0])));
//    }

    /**
     * @return the taskName
     */
    @JsonIgnore
    public String getTaskName() {
        return taskName;
    }

    /**
     * @return the taskId
     */
    @JsonIgnore
    public String getTaskId() {
        return taskId;
    }

    /**
     * @return the userIds
     */
    @JsonIgnore
    public List<String> getUserIds() {
        return userIds;
    }

    /**
     * @return the users
     */
    @JsonIgnore
    public List<Map<String, String>> getUsers() {
        return users;
    }

    /**
     * @return the submitButtonId
     */
    public String getSubmitButtonId() {
        return submitButtonId;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("title", title);
        variables.put("taskName", taskName);
        variables.put("taskId", taskId);
        variables.put("userIds", userIds);
        variables.put("userJobIdentityMap", userJobIdentityMap);
        variables.put("users", users);
        variables.put("submitButtonId", submitButtonId);
        variables.put("eleOrderMap", eleOrderMap);
        variables.put("currentOrgVersionId", currentOrgVersionId);
        variables.put("orgVersionIds", orgVersionIds);
        variables.put("orgIdBizOrgIdMap", orgIdBizOrgIdMap);
        variables.put("bizOrgId", bizOrgId);
        variables.put("sidGranularity", sidGranularity);
        variables.put("defaultViewStyle", defaultViewStyle);
        variables.put("autoViewStyleNum", autoViewStyleNum);
        variables.put("viewFormMode", viewFormMode);
        return variables;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.OnlyChooseOneUser;
    }

    /**
     * @return the viewFormMode
     */
    public String getViewFormMode() {
        return viewFormMode;
    }

    /**
     * @param viewFormMode 要设置的viewFormMode
     */
    public void setViewFormMode(String viewFormMode) {
        this.viewFormMode = viewFormMode;
    }
}
