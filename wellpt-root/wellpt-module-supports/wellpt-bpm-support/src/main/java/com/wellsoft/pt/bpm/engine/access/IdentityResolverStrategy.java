/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.impl.DefaultIdentityFilter;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.UnitElement;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.exception.*;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.IdentityService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.UnitUser;
import com.wellsoft.pt.bpm.engine.support.WorkFlowTodoType;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserWorkInfo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.rule.engine.RuleEngine;
import com.wellsoft.pt.rule.engine.RuleEngineFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.*;

/**
 * Description: 默认的工作流参与者解析
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
@Component
public class IdentityResolverStrategy {
    private static Logger logger = LoggerFactory.getLogger(IdentityResolverStrategy.class);

    @Autowired
    private UnitIdentityResolver unitIdentityResolver;

    @Autowired
    private BizUnitIdentityResolver bizUnitIdentityResolver;

    @Autowired
    private FormFieldIdentityResolver formFieldIdentityResolver;

    @Autowired
    private TaskHistoryIdentityResolver taskHistoryIdentityResolver;

    @Autowired
    private OptionIdentityResolver participantIdentityResolver;

    @Autowired
    private UserCustomIdentityResolver userCustomIdentityResolver;

    @Autowired
    private DefaultIdentityFilter identityFilter;

    @Autowired
    private SidGranularityResolver sidGranularityResolver;

    /**
     * 返回指定用户的职位和部门的对应关系
     * key:jobId
     * value:deptId
     *
     * @param multiOrgUserWorkInfos
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    public static Map<String, String> getDeptIdMap(List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos) {
        Map<String, String> deptIdMap = Maps.newHashMap();
        for (MultiOrgUserWorkInfo multiOrgUserWorkInfo : multiOrgUserWorkInfos) {
            if (StringUtils.isNotBlank(multiOrgUserWorkInfo.getJobIds())) {
                String jobIds = multiOrgUserWorkInfo.getJobIds();
                if (StringUtils.isBlank(jobIds)) {
                    continue;
                }
                String[] jobIdStrs = jobIds.split(";");
                String eleIdPaths = multiOrgUserWorkInfo.getEleIdPaths();
                if (StringUtils.isBlank(eleIdPaths)) {
                    continue;
                }
                String[] eleIdPathStrs = eleIdPaths.split(";");

                // job_ids 和dept_ids 不一定是一一对应的，job_ids 和ele_id_paths才是一一对应
                for (int i = 0; i < jobIdStrs.length; i++) {
                    String eleIdPathStr = eleIdPathStrs[i];
                    String[] eleIds = eleIdPathStr.split("/");
                    if (jobIdStrs[i].equals(eleIds[eleIds.length - 1]) && eleIds.length >= 2) {
                        // 职位的父级节点 是否存在部门节点
                        String jobParentDeptId = "";
                        for (int y = eleIds.length - 1; y >= 0; y--) {
                            if (eleIds[y].startsWith("D")) {
                                jobParentDeptId = eleIds[y];
                                break;
                            }
                        }
                        if (StringUtils.isNotBlank(jobParentDeptId)) {
                            deptIdMap.put(jobIdStrs[i], jobParentDeptId);
                        }
                    }
                }
            }
        }
        return deptIdMap;
    }

    /**
     * 返回指定用户的职位和职位全路径的对应关系
     * key:jobId
     * value:eleIdPaths
     *
     * @param multiOrgUserWorkInfos
     * @return java.util.Map<java.lang.String, java.lang.String>
     **/
    public static Map<String, String> getEleIdPathsMap(List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos) {
        Map<String, String> eleIdPathMap = Maps.newHashMap();
        for (MultiOrgUserWorkInfo multiOrgUserWorkInfo : multiOrgUserWorkInfos) {
            if (StringUtils.isNotBlank(multiOrgUserWorkInfo.getJobIds())) {
                String jobs = multiOrgUserWorkInfo.getJobIds();
                if (StringUtils.isBlank(jobs)) {
                    continue;
                }
                String[] jobStrs = jobs.split(";");
                String eleIdPaths = multiOrgUserWorkInfo.getEleIdPaths();
                String[] eleIdPathStrs = eleIdPaths.split(";");
                for (int i = 0; i < eleIdPathStrs.length; i++) {
                    eleIdPathMap.put(jobStrs[i], eleIdPathStrs[i]);
                }
            }
        }
        return eleIdPathMap;
    }

    /**
     * 解析前台界面用户选择的组织框选择的抄送人员
     *
     * @param token
     * @param taskId
     */
    public static List<FlowUserSid> resolveTaskUsers(Token token, Node node, final String taskId) {
        List<FlowUserSid> userIds = new ArrayList<FlowUserSid>();
        Set<String> rawUsers = token.getTaskData().getTaskUsers(taskId);
        if (!rawUsers.isEmpty()) {
            SidGranularityResolver sidGranularityResolver = ApplicationContextHolder
                    .getBean(SidGranularityResolver.class);
            userIds.addAll(sidGranularityResolver.resolve(node, token, rawUsers));
        } else {
            Map<String, Object> variables = new HashMap<String, Object>();
            String name = token.getFlowDelegate().getFlow().getName();
            variables.put("title", "(" + name + ":" + node.getName() + ")");
            variables.put("taskName", node.getName());
            variables.put("taskId", taskId);
            variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
            throw new TaskNotAssignedUserException(variables, token);
        }
        return userIds;
    }

    /**
     * 解析前台界面用户选择的组织框选择的抄送人员
     *
     * @param token
     * @param taskId
     */
    public static List<FlowUserSid> resolveTaskCopyUsers(Token token, Node node, final String taskId) {
        List<FlowUserSid> userIds = new ArrayList<FlowUserSid>();
        Set<String> rawCopyUsers = token.getTaskData().getTaskCopyUsers(taskId);
        if (rawCopyUsers.isEmpty() && token.getTask() != null
                && CollectionUtils.isNotEmpty(token.getTaskData().getTaskCopyUsers(token.getTask().getId()))) {
            // 如果前一环节由指定抄送人，那这个环节就不需要抛异常弹窗抄送人选择弹窗
            return Collections.emptyList();
        }
        if (!rawCopyUsers.isEmpty()) {
            SidGranularityResolver sidGranularityResolver = ApplicationContextHolder
                    .getBean(SidGranularityResolver.class);
            userIds.addAll(sidGranularityResolver.resolve(node, token, rawCopyUsers, SidGranularity.ACTIVITY));
        } else if (!token.getTaskData().isDaemon()) {
            // 非后台提交运行的工作前端需选择抄送人
            Map<String, Object> variables = new HashMap<String, Object>();
            String name = token.getFlowDelegate().getFlow().getName();
            variables.put("title", "(" + name + ":" + node.getName() + ")");
            variables.put("taskId", taskId);
            variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
            throw new TaskNotAssignedCopyUserException(variables, token);
        }
        return userIds;
    }

    /**
     * @param node
     * @param token
     * @param rawOrgIds
     * @return
     */
    public static List<FlowUserSid> resolveFlowUserSids(Node node, Token token, Collection<String> rawOrgIds) {
        SidGranularityResolver sidGranularityResolver = ApplicationContextHolder.getBean(SidGranularityResolver.class);
        return sidGranularityResolver.resolve(node, token, rawOrgIds);
    }

    /**
     * @param sids
     * @return
     */
    public static Collection<String> resolveAsUserIds(Collection<FlowUserSid> sids) {
        SidGranularityResolver sidGranularityResolver = ApplicationContextHolder.getBean(SidGranularityResolver.class);
        return sidGranularityResolver.resolveAsUserIds(sids);
    }

    /**
     * @param rawUsers
     * @return
     */
    public static List<String> resolveUserIds(List<String> rawUsers) {
        List<String> userIds = new ArrayList<String>(0);
        if (rawUsers == null || rawUsers.isEmpty()) {
            return userIds;
        }
        // 用户、部门、群组ID、职务、职位
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        Map<String, String> users = workflowOrgService.getUsersByIds(rawUsers);
//        // key=用户id，value=用户名字
//        Map<String, String> users = orgApiFacade.getUsersByOrgIds(rawUsers);
//        Set<String> userIdSet = new LinkedHashSet<String>();
//        for (String userId : users.keySet()) {
//            if (StringUtils.isNotBlank(userId)) {
//                userIdSet.add(userId);
//            }
//        }
        userIds.addAll(users.keySet());
        return userIds;
    }

    /**
     * @param rawUser
     * @return
     */
    public static List<String> resolveUserId(String rawUser) {
        List<String> userIds = new ArrayList<String>(0);
        if (StringUtils.isBlank(rawUser)) {
            return userIds;
        }

        // 用户、部门、群组ID、职务、职位
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        Map<String, String> users = workflowOrgService.getUsersByIds(Lists.newArrayList(rawUser));
//        // key=用户id，value=用户名字
//        Map<String, String> users = orgApiFacade.getUsersByOrgIds(rawUser);
//        Set<String> userIdSet = new LinkedHashSet<String>();
//        for (String userId : users.keySet()) {
//            if (StringUtils.isNotBlank(userId)) {
//                userIdSet.add(userId);
//            }
//        }
        userIds.addAll(users.keySet());
        return userIds;
    }

    /**
     * @param sids
     * @return
     */
    public static String resolveAsNames(List<String> sids) {
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        Map<String, String> userMap = workflowOrgService.getNamesByIds(sids);
        StringBuilder sb = new StringBuilder();
        Iterator<String> it = sids.iterator();
        while (it.hasNext()) {
            String sid = it.next();
            if (userMap.containsKey(sid)) {
                sb.append(userMap.get(sid));
            } else {
                sb.append(sid);
            }

            if (it.hasNext()) {
                sb.append(Separator.SEMICOLON.getValue());
            }
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     */
    public static String resolveAsName(String sid) {
        if (StringUtils.isBlank(sid)) {
            return sid;
        }
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        return workflowOrgService.getNameById(sid);
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

    public static List<FlowUserSid> resolveCustomBtnUsers(Token token, Node to, List<String> rawUserIds,
                                                          ParticipantType participantType) {
        UnitIdentityResolver unitIdentityResolver = ApplicationContextHolder.getBean(UnitIdentityResolver.class);
        FormFieldIdentityResolver formFieldIdentityResolver = ApplicationContextHolder
                .getBean(FormFieldIdentityResolver.class);
        TaskHistoryIdentityResolver taskHistoryIdentityResolver = ApplicationContextHolder
                .getBean(TaskHistoryIdentityResolver.class);
        OptionIdentityResolver participantIdentityResolver = ApplicationContextHolder
                .getBean(OptionIdentityResolver.class);
        DefaultIdentityFilter identityFilter = ApplicationContextHolder.getBean(DefaultIdentityFilter.class);
        List<FlowUserSid> userIds = new ArrayList<FlowUserSid>();
        StopWatch timer = new StopWatch(
                (to != null ? "[" + to.getName() + "]" : "") + "IdentityResolverStrategy.resolveCustomBtnUsers");
        // 1、解析组织选择框选择的用户
        timer.start("解析组织选择框用户");
        String[] participants = rawUserIds.get(0).trim().split(Separator.SEMICOLON.getValue());
        userIds.addAll(
                unitIdentityResolver.resolve(to, token, filterEmptyUser(Arrays.asList(participants)), participantType));
        timer.stop();
        // 2、解析表单域中的用户
        timer.start("解析表单域用户");
        participants = rawUserIds.get(1).trim().split(Separator.SEMICOLON.getValue());
        userIds.addAll(formFieldIdentityResolver.resolve(to, token, filterEmptyUser(Arrays.asList(participants)),
                participantType));
        timer.stop();
        // 3、解析已运行过的流程的参与者
        timer.start("解析已运行过的流程的参与者");
        participants = rawUserIds.get(2).trim().split(Separator.SEMICOLON.getValue());
        userIds.addAll(taskHistoryIdentityResolver.resolve(to, token, filterEmptyUser(Arrays.asList(participants)),
                participantType));
        timer.stop();
        // 4、 解析参与者
        timer.start("解析参与者");
        participants = rawUserIds.get(3).trim().split(Separator.SEMICOLON.getValue());
        userIds.addAll(participantIdentityResolver.resolve(to, token, filterEmptyUser(Arrays.asList(participants)),
                participantType));
        timer.stop();

        // 人员过滤，对组织机构、单位业务角色、文档域、办理环节和人员选项统一解析出来的人员进行过滤
        userIds = Arrays
                .asList(identityFilter.doFilter(userIds, to, token, filterEmptyUser(Arrays.asList(participants))).toArray(new FlowUserSid[0]));

        logger.info(timer.prettyPrint());
        return userIds;
    }

    /**
     * 判断自定义提交按钮的用户是否为空
     *
     * @param rawUserIds
     * @return
     */
    public static boolean isCustomBtnUsersEmpty(List<String> rawUserIds) {
        for (String rawUserId : rawUserIds) {
            if (StringUtils.isNotBlank(rawUserId)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 过滤掉空数据
     *
     * @param list
     * @return
     */
    private static List<String> filterEmptyUser(List<String> list) {
        List<String> users = new ArrayList<String>();
        for (String rawUser : list) {
            if (StringUtils.isNotBlank(rawUser)) {
                users.add(rawUser.trim());
            }
        }
        return users;
    }

    /**
     * @param sids
     * @return
     */
    public static List<String> resolveAsOrgIds(Collection<FlowUserSid> sids) {
        List<String> orgIds = new ArrayList<String>();
        for (FlowUserSid sid : sids) {
            orgIds.add(sid.getId());
        }
        return orgIds;
    }

    /**
     * @param sids
     * @return
     */
    public static List<String> resolveAsOrgNames(Collection<FlowUserSid> sids) {
        List<String> orgIds = new ArrayList<String>();
        for (FlowUserSid sid : sids) {
            orgIds.add(sid.getName());
        }
        return orgIds;
    }

//    public static Map<String, String> resolveCodeByOrgIds(final List<String> userIds) {
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        Map<String, String> result = Maps.newHashMap();
//        // 1. 分组用户数据，批量查询
//        ImmutableListMultimap<String, String> map = Multimaps.index(userIds, new Function<String, String>() {
//            @Nullable
//            @Override
//            public String apply(@Nullable String sid) {
//                if (sid.startsWith(IdPrefix.USER.getValue())) {
//                    return IdPrefix.USER.getValue();
//                } else if (sid.startsWith("W") // FIXME: 职务有 W 开头的？？？
//                        || sid.startsWith(IdPrefix.DUTY.getValue())) {
//                    return IdPrefix.DUTY.getValue();
//                } else if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
//                    return IdPrefix.DEPARTMENT.getValue();
//                } else if (sid.startsWith(IdPrefix.GROUP.getValue())) {
//                    return IdPrefix.GROUP.getValue();
//                } else if (sid.startsWith(IdPrefix.JOB.getValue())) {
//                    return IdPrefix.JOB.getValue();
//                } else if (IdPrefix.startsWithExternal(sid)) {
//                    return "External";
//                }
//                return "element";
//            }
//        });
//        BusinessFacadeService businessFacadeService = ApplicationContextHolder.getBean(BusinessFacadeService.class);
//        Map<String, Collection<String>> groupMap = map.asMap();
//        Set<String> keys = groupMap.keySet();
//        for (String sid : keys) {
//            List<String> ids = Lists.newArrayList(groupMap.get(sid));
//            if (sid.equals(IdPrefix.USER.getValue())) {
//                List<MultiOrgUserAccount> accounts = orgApiFacade.getAccountsByUserIds(ids);
//                if (accounts != null) {
//                    for (MultiOrgUserAccount account : accounts) {
//                        result.put(account.getId(), account.getCode());
//                    }
//                }
//            } else if (sid.equals(IdPrefix.DUTY.getValue())) {
//                List<MultiOrgDuty> dutys = orgApiFacade.getDutysByIds(ids);
//                if (dutys != null) {
//                    for (MultiOrgDuty d : dutys) {
//                        result.put(d.getId(), d.getCode());
//                    }
//                }
//            } else if (sid.equals(IdPrefix.DEPARTMENT.getValue()) || sid.equals(IdPrefix.JOB.getValue())
//                    || sid.equals("element")) {
//                List<MultiOrgElement> elements = orgApiFacade.getOrgElementsByIds(ids);
//                if (elements != null) {
//                    for (MultiOrgElement element : elements) {
//                        result.put(element.getId(), element.getCode());
//                    }
//                }
//            } else if (sid.equals(IdPrefix.GROUP.getValue())) {
//                List<MultiOrgGroup> multiOrgGroups = orgApiFacade.getGroupsByIds(ids);
//                if (multiOrgGroups != null) {
//                    for (MultiOrgGroup element : multiOrgGroups) {
//                        result.put(element.getId(), element.getCode());
//                    }
//                }
//            } else if (sid.equals("External")) {
//                List<BusinessCategoryOrgEntity> businessCategoryOrgEntities = businessFacadeService
//                        .getBusinessByIds(ids);
//                if (businessCategoryOrgEntities != null) {
//                    for (BusinessCategoryOrgEntity entity : businessCategoryOrgEntities) {
//                        result.put(entity.getId(), entity.getCode());
//                    }
//                }
//            }
//        }
//        return result;
//    }

//    public static void resolveOrgDetail(List<Map<String, String>> users) {
//        if (CollectionUtils.isEmpty(users)) {
//            return;
//        }
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//        BusinessFacadeService businessFacadeService = ApplicationContextHolder.getBean(BusinessFacadeService.class);
//        Map<String, List<String>> elementMap = Maps.newHashMap();
//        Map<String, Map<String, String>> idUserMap = Maps.newHashMap();
//        List<Map<String, String>> userList = Lists.newArrayList();
//        List<String> userids = Lists.newArrayList();
//        for (Map<String, String> u : users) {
//            String sid = u.get("id");
//            idUserMap.put(sid, u);
//            if (!sid.startsWith(IdPrefix.USER.getValue())) {
//                // 非用户元素
//                String type = "element";
//                if (sid.startsWith(IdPrefix.DEPARTMENT.getValue())) {
//                    type = IdPrefix.DEPARTMENT.getValue();
//                } else if (sid.startsWith(IdPrefix.GROUP.getValue())) {
//                    type = IdPrefix.GROUP.getValue();
//                } else if (sid.startsWith(IdPrefix.JOB.getValue())) {
//                    type = IdPrefix.JOB.getValue();
//                } else if (IdPrefix.startsWithExternal(sid)) {
//                    type = "External";
//                } else if (sid.startsWith("W")) {
//                    type = "W";
//                }
//                if (!elementMap.containsKey(type)) {
//                    elementMap.put(type, Lists.<String>newArrayList());
//                }
//                elementMap.get(type).add(sid);
//            } else {
//                userids.add(sid);
//                userList.add(u);
//            }
//        }
//        Set<String> types = elementMap.keySet();
//        // 非用户节点查询
//        if (types != null) {
//            for (String sid : types) {
//                List<String> ids = elementMap.get(sid);
//                if (sid.equals(IdPrefix.DEPARTMENT.getValue()) || sid.equals(IdPrefix.JOB.getValue())
//                        || sid.equals("element")) {
//                    List<MultiOrgElement> elements = orgApiFacade.getOrgElementsByIds(ids);
//                    if (elements != null) {
//                        for (MultiOrgElement element : elements) {
//                            if (idUserMap.containsKey(element.getId())) {
//                                idUserMap.get(element.getId()).put("code", element.getCode());
//                            }
//                        }
//                    }
//                } else if (sid.equals(IdPrefix.GROUP.getValue())) {
//                    List<MultiOrgGroup> multiOrgGroups = orgApiFacade.getGroupsByIds(ids);
//                    if (multiOrgGroups != null) {
//                        for (MultiOrgGroup element : multiOrgGroups) {
//                            if (idUserMap.containsKey(element.getId())) {
//                                idUserMap.get(element.getId()).put("code", element.getCode());
//                            }
//                        }
//                    }
//                } else if (sid.equals("W")) {
//                    List<MultiOrgDuty> dutys = orgApiFacade.getDutysByIds(ids);
//                    if (dutys != null) {
//                        for (MultiOrgDuty d : dutys) {
//                            if (idUserMap.containsKey(d.getId())) {
//                                idUserMap.get(d.getId()).put("code", d.getCode());
//                            }
//                        }
//                    }
//                } else if (sid.equals("External")) {
//                    List<BusinessCategoryOrgEntity> businessCategoryOrgEntities = businessFacadeService
//                            .getBusinessByIds(ids);
//                    if (businessCategoryOrgEntities != null) {
//                        for (BusinessCategoryOrgEntity entity : businessCategoryOrgEntities) {
//                            if (idUserMap.containsKey(entity.getId())) {
//                                idUserMap.get(entity.getId()).put("code", entity.getCode());
//                            }
//                        }
//                    }
//                }
//
//            }
//        }
//        Map<String, OrgUserVo> userVoMap = orgApiFacade.getUserAccoutVoWithAllJobByIds(userids);
//        // 用户节点查询，需要职位、部门相关信息
//        for (Map<String, String> user : userList) {
//            if (userVoMap.containsKey(user.get("id"))) {
//                OrgUserVo vo = userVoMap.get(user.get("id"));
//                user.put("code", vo.getCode());
//                user.put("namePy", vo.getUserNamePy());
//                user.put("mainJobName", vo.getMainJobName());
//                user.put("mainJobNamePath", vo.getMainJobNamePath());
//                user.put("mainJobIdPath", vo.getMainJobIdPath());
//                user.put("mainDepartmentName", vo.getMainDepartmentName());
//
//                user.put("otherJobNames", vo.getOtherJobNames());
//                user.put("otherJobNamePaths", vo.getOtherJobNamePaths());
//                user.put("otherJobIdPaths", vo.getOtherJobIdPaths());
//
//            }
//        }
//    }

    /**
     * 给用户添加职位路径
     *
     * @param userIdSet
     * @param todoUserEntities
     * @return void
     **/
//    public static void addJobPath(Set<FlowUserSid> userIdSet, List<WfTaskInstanceTodoUserEntity> todoUserEntities) {
//
//        for (FlowUserSid flowUserSid : userIdSet) {
//            for (WfTaskInstanceTodoUserEntity todoUserEntity : todoUserEntities) {
//                if (flowUserSid.getId().equals(todoUserEntity.getTodoUserId())) {
//                    flowUserSid.setJobIdPath(todoUserEntity.getTodoUserJobPath());
//                    flowUserSid.setSaveJobPath(Boolean.TRUE);
//                }
//            }
//        }
//    }

    /**
     * 判断是否来源同一组织版本或同一单位 ，增加标记 isShowVer isShowUnit
     *
     * @param users
     * @return void
     **/
    public static void addIsShowVerAndIsShowUnit(List<Map<String, String>> users) {
        Set<String> jobIdVs = new HashSet<>();
        Set<String> jobIdUs = new HashSet<>();
        for (Map<String, String> user : users) {
            String showJobIdPath = user.get("showJobIdPath");
            if (showJobIdPath == null) {
                continue;
            }
            String[] showJobIdPaths = showJobIdPath.split("/");
            for (String jobIdPath : showJobIdPaths) {
                if (jobIdPath.indexOf(SidGranularity.VERSION) > -1) {
                    jobIdVs.add(jobIdPath);
                    break;
                }
            }

            for (String jobIdPath : showJobIdPaths) {
                if (jobIdPath.indexOf(SidGranularity.UNIT) > -1) {
                    jobIdUs.add(jobIdPath);
                    break;
                }
            }
        }

        for (Map<String, String> user : users) {
            if (jobIdVs.size() > 1) {
                user.put("isShowVer", "1");
            } else {
                user.put("isShowVer", "0");
            }

            if (jobIdUs.size() > 1) {
                user.put("isShowUnit", "1");
            } else {
                user.put("isShowUnit", "0");
            }
        }
    }

    /**
     * 新增 showJobIdPath 和 showJobNamePath
     *
     * @param users
     * @param unitElements
     * @return void
     **/
//    public static void addShowJobIdPathAndShowJobNamePath(Node node, Token token, List<Map<String, String>> users,
//                                                          List<UnitElement> unitElements) {
//        MultiOrgGroupFacade multiOrgGroupFacade = ApplicationContextHolder.getBean(MultiOrgGroupFacade.class);
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//
//        MultiOrgJobDutyFacade multiOrgJobDutyFacade = ApplicationContextHolder.getBean(MultiOrgJobDutyFacade.class);
//
//        // 组织框
//        List<String> rawUnitUsers = new ArrayList<String>();
//        // 文档域
//        List<String> rawFormFieldUsers = new ArrayList<String>();
//        // 办理环节
//        List<String> rawTaskHistoryUsers = new ArrayList<String>();
//        // 人员选项和人员过滤
//        List<String> rawOptionUsers = new ArrayList<String>();
//        for (UnitElement unitElement : unitElements) {
//            Integer type = unitElement.getType();
//            String value = unitElement.getValue();
//            if (Integer.valueOf(1).equals(type)) { // 通过组织机构设置办理人
//                rawUnitUsers.add(value);
//            } else if (Integer.valueOf(2).equals(type)) { // 通过文档域设置办理人
//                rawFormFieldUsers.add(value);
//            } else if (Integer.valueOf(4).equals(type)) { // 通过办理环节设置办理人
//                rawTaskHistoryUsers.add(value);
//            } else if (Integer.valueOf(8).equals(type)) { // 通过人员选项和人员过滤设置办理人
//                rawOptionUsers.add(value);
//            }
//        }
//
//        // 文档域获取组织
//        List<String> orgIds = FormFieldIdentityResolver.resolveOrgIds(node, token, rawFormFieldUsers);
//        if (orgIds.size() > 0) {
//            rawUnitUsers.addAll(orgIds);
//        }
//        // 获取用户的职位对应的部门
//        List<String> userIds = Lists.newArrayList();
//        for (Map<String, String> user : users) {
//            userIds.add(user.get("id"));
//        }
//        List<MultiOrgUserWorkInfo> multiOrgUserWorkInfos = orgApiFacade.getUserWorkInfosByUserIds(userIds);
//        Map<String, String> deptIdMap = getDeptIdMap(multiOrgUserWorkInfos);
//        // 申请人
//        String creator = token.getFlowInstance().getStartUserId();
//        // 当前办理人
//        String userId = token.getTaskData().getUserId();
//        // 前办理人ID
//        String priorUserId = getPriorUserId(token, userId);
//        for (Map<String, String> user : users) {
//            // 指定环节获取人员的路径 FlowUserSid改造
//            if (rawTaskHistoryUsers.size() > 0) {
//                Boolean isSaveJobPath = Boolean.valueOf(user.get("isSaveJobPath"));
//                if (isSaveJobPath) {
//                    // 显示指定环节办理人员的办理路径
//                    String jobIdPath = user.get("jobIdPath");
//                    addShowObject(user, jobIdPath, Boolean.FALSE, null, null);
//                }
//            }
//
//            // 组织机构，表单字段，办理环节
//            for (String rawUnitUser : rawUnitUsers) {
//                if (rawUnitUser.indexOf(SidGranularity.GROUP) > -1) {
//                    // 群组
//                    OrgGroupVo orgGroupVo = multiOrgGroupFacade.getGroupVoById(rawUnitUser);
//                    List<MultiOrgGroupMember> orgGroupMembers = orgGroupVo.getMemberList();
//                    if (orgGroupMembers != null && orgGroupMembers.size() > 0) {
//                        for (MultiOrgGroupMember orgGroupMember : orgGroupMembers) {
//                            addShowObject(user, orgGroupMember.getMemberObjId(), Boolean.TRUE, orgGroupVo.getId(),
//                                    orgGroupVo.getName());
//                        }
//                    }
//                } else if (rawUnitUser.indexOf(IdPrefix.DUTY.getValue()) > -1) {
//                    // 职务群组
//                    MultiOrgDuty multiOrgDuty = orgApiFacade.getDutyById(rawUnitUser);
//                    if (multiOrgDuty == null) {
//                        continue;
//                    }
//                    // 获取职务群组下 的 职位
//                    List<MultiOrgJobDuty> multiOrgJobDutyList = multiOrgJobDutyFacade.getJobDutyByDutyId(rawUnitUser);
//                    if (CollectionUtils.isNotEmpty(multiOrgJobDutyList)) {
//                        for (MultiOrgJobDuty duty : multiOrgJobDutyList) {
//                            addShowObject(user, duty.getJobId(), Boolean.TRUE, multiOrgDuty.getId(),
//                                    multiOrgDuty.getName());
//                        }
//                    }
//                } else {
//                    addShowObject(user, rawUnitUser, Boolean.FALSE, null, null);
//                }
//            }
//
//            // 通过人员选项和人员过滤设置办理人
//            if (rawOptionUsers.size() > 0) {
//
//                String multiJobFlowType = token.getFlowInstance().getFlowDefinition().getMultiJobFlowType();
//                if (StringUtils.isBlank(multiJobFlowType)) {
//                    multiJobFlowType = FlowDefConstants.FLOW_BY_USER_ALL_JOBS;
//                }
//                Set<String> showDeptIds = Sets.newHashSet();
//                for (String rawOptionUser : rawOptionUsers) {
//                    Participant participant = Enum.valueOf(Participant.class, rawOptionUser);
//                    switch (participant) {
//                        // 人员选择
//                        // 1、前办理人
//                        case PriorUser:
//                            break;
//                        // 2、前办理人的直接汇报人
//                        case DirectLeaderOfPriorUser:
//                            break;
//                        // 2、前办理人的上级领导
//                        case LeaderOfPriorUser:
//                            break;
//                        // 2、前办理人的部门领导
//                        case DeptLeaderOfPriorUser:
//                            break;
//                        // 2、前办理人的分管领导
//                        case BranchedLeaderOfPriorUser:
//                            break;
//                        // 3、前办理人的所有上级领导
//                        case AllLeaderOfPriorUser:
//                            break;
//                        // 4、申请人
//                        case Creator:
//                            break;
//                        // 2、申请人的直属上级领导
//                        case DirectLeaderOfCreator:
//                            break;
//                        // 5、申请人的上级领导
//                        case LeaderOfCreator:
//                            break;
//                        // 5、申请人的部门领导
//                        case DeptLeaderOfCreator:
//                            break;
//                        // 5、申请人的分管领导
//                        case BranchedLeaderOfCreator:
//                            break;
//                        // 6、申请人的所有上级领导
//                        case AllLeaderOfCreator:
//                            break;
//                        // 7、前一个环节办理人
//                        case PriorTaskUser:
//                            break;
//                        // 8、前办理人的部门
//                        case DeptOfPriorUser:
//                            if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryAllJobDepartmentIdListByUserId(priorUserId));
//                            } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryMainJobDepartmentIdListByUserId(priorUserId));
//                            } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryJobDepartmentUserListByUserId(priorUserId,
//                                        getSelectJobValue(token)));
//                            }
//
//                            break;
//                        // 9、前办理人的上级部门
//                        case ParentDeptOfPriorUser:
//                            if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryAllJobParentDepartmentIdsByUserId(priorUserId));
//                            } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryMainJobParentDepartmentIdsByUserId(priorUserId));
//                            } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryJobParentDepartmentIdsByUserId(priorUserId,
//                                        getSelectJobValue(token)));
//                            }
//                            break;
//                        // 10、前办理人的根部门
//                        case RootDeptOfPriorUser:
//                            if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryAllJobRootDepartmentIdsByUserId(priorUserId));
//                            } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryMainJobRootDepartmentIdsByUserId(priorUserId));
//                            } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryJobRootDepartmentIdsByUserId(priorUserId,
//                                        getSelectJobValue(token)));
//                            }
//                            break;
//                        // 10、前办理人的业务单位
//                        case BizUnitOfPriorUser:
//                            break;
//                        // 11、申请人的部门
//                        case DeptOfCreator:
//                            if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryAllJobDepartmentIdListByUserId(creator));
//                            } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryMainJobDepartmentIdListByUserId(creator));
//                            } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(
//                                        orgApiFacade.queryJobDepartmentUserListByUserId(creator, getSelectJobValue(token)));
//                            }
//
//                            break;
//                        // 12、申请人的上级部门
//                        case ParentDeptOfCreator:
//                            if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryAllJobParentDepartmentIdsByUserId(creator));
//                            } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryMainJobParentDepartmentIdsByUserId(creator));
//                            } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryJobParentDepartmentIdsByUserId(creator,
//                                        getSelectJobValue(token)));
//                            }
//
//                            break;
//                        // 13、申请人的根部门
//                        case RootDeptOfCreator:
//                            if (FlowDefConstants.FLOW_BY_USER_ALL_JOBS.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryAllJobRootDepartmentIdsByUserId(creator));
//                            } else if (FlowDefConstants.FLOW_BY_USER_MAIN_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(orgApiFacade.queryMainJobRootDepartmentIdsByUserId(creator));
//                            } else if (FlowDefConstants.FLOW_BY_USER_SELECT_JOB.equalsIgnoreCase(multiJobFlowType)) {
//                                showDeptIds.addAll(
//                                        orgApiFacade.queryJobRootDepartmentIdsByUserId(creator, getSelectJobValue(token)));
//                            }
//                            break;
//                        // 10、申请人的业务单位
//                        case BizUnitOfCreator:
//                            break;
//                        // 14、全组织 zyguo 等价于根部门，所以取消掉
//                        case Corp:
//                            break;
//                        default:
//                            break;
//                    }
//                }
//
//                // 人员选项处理
//                Boolean applyUserFlag = Boolean.valueOf(user.get("applyUserFlag"));
//                if (applyUserFlag) {
//                    // 人员选项的路径
//                    String jobIdPath = user.get("applyUserJobIds");
//                    String[] jobIdPaths = jobIdPath.split(";");
//                    Set<String> jobIdPathSet = Sets.newHashSet();
//                    for (String idPath : jobIdPaths) {
//                        jobIdPathSet.add(idPath);
//                    }
//                    for (String jobIdPath1 : jobIdPathSet) {
//
//                        if (rawUnitUsers.size() == 0) {
//                            applyFilterNotGroup(jobIdPath1, deptIdMap, user, showDeptIds);
//                            continue;
//                        }
//                        // 人员选项或人员过滤 是否在群组中
//                        for (String rawUnitUser : rawUnitUsers) {
//                            if (rawUnitUser.indexOf(SidGranularity.GROUP) > -1) {
//                                // 群组
//                                OrgGroupVo orgGroupVo = multiOrgGroupFacade.getGroupVoById(rawUnitUser);
//                                List<MultiOrgGroupMember> orgGroupMembers = orgGroupVo.getMemberList();
//                                if (orgGroupMembers != null && orgGroupMembers.size() > 0) {
//                                    Set<String> nodeIdSet = new HashSet<>();
//                                    for (MultiOrgGroupMember orgGroupMember : orgGroupMembers) {
//                                        nodeIdSet.add(orgGroupMember.getMemberObjId());
//                                    }
//                                    List<MultiOrgTreeNode> multiOrgTreeNodes = orgApiFacade
//                                            .getOrgTreeNodeListByNodeIds(nodeIdSet);
//                                    for (MultiOrgTreeNode multiOrgTreeNode : multiOrgTreeNodes) {
//                                        if (multiOrgTreeNode.getEleIdPath().indexOf(jobIdPath1) > -1) {
//                                            addShowObject(user, multiOrgTreeNode.getEleId(), Boolean.TRUE,
//                                                    orgGroupVo.getId(), orgGroupVo.getName());
//                                        }
//                                    }
//                                }
//                            } else {
//                                // 非群组
//                                applyFilterNotGroup(jobIdPath1, deptIdMap, user, showDeptIds);
//                            }
//                        }
//                    }
//                }
//
//                // 人员过滤 组织，表单，环节，人员选项show处理后，最后再取交集
//                Boolean filterUserFlag = Boolean.valueOf(user.get("filterUserFlag"));
//                if (filterUserFlag) {
//                    // 人员过滤的路径
//                    String jobIdPath = user.get("filterUserJobIds");
//                    filterShowObject(user, jobIdPath, rawUnitUsers);
//
//                }
//            }
//
//        }
//
//        for (Map<String, String> user : users) {
//            // 都没有命中，设置默认主职
//            Boolean applyUserFlag = Boolean.valueOf(user.get("applyUserFlag"));
//            if (user.get("showJobIdPath") == null) {
//                if (applyUserFlag) {
//                    user.put("showJobIdPath", "");
//                    user.put("showJobNamePath", "");
//                    user.put("showJobName", "");
//                } else {
//                    String mainJobIdPath = user.get("mainJobIdPath");
//                    String mainJobNamePath = user.get("mainJobNamePath");
//                    String mainJobName = user.get("mainJobName");
//                    user.put("showJobIdPath", mainJobIdPath);
//                    user.put("showJobNamePath", mainJobNamePath);
//                    user.put("showJobName", mainJobName);
//                }
//            }
//            // 去掉不必要返回给前端的user属性值
//            user.remove("isSaveJobPath");
//            user.remove("jobIdPath");
//            user.remove("applyUserJobIds");
//            user.remove("applyUserFlag");
//            user.remove("filterUserJobIds");
//            user.remove("filterUserFlag");
//            user.remove("isSelectMainJob");
//            user.remove("notRemoveUser");
//        }
//        for (int i = 0; i < users.size(); i++) {
//            Map<String, String> user = users.get(i);
//            if (StringUtils.isNotBlank(user.get("removeUser"))) {
//                users.remove(i--);
//            }
//        }
//
//    }

    /**
     * 人员选项 人员过滤 非群组处理
     *
     * @param jobIdPath1
     * @param deptIdMap
     * @param user
     * @return void
     **/
//    private static void applyFilterNotGroup(String jobIdPath1, Map<String, String> deptIdMap, Map<String, String> user,
//                                            Set<String> showDeptIds) {
//        if (showDeptIds.size() == 0) {
//            // 非群组
//            if (jobIdPath1.indexOf(SidGranularity.JOB) > -1) {
//                // 取职位的部门
//                if (StringUtils.isNotBlank(deptIdMap.get(jobIdPath1))) {
//                    addShowObject(user, deptIdMap.get(jobIdPath1), Boolean.FALSE, null, null);
//                } else {
//                    addShowObject(user, jobIdPath1, Boolean.FALSE, null, null);
//                }
//            } else {
//                addShowObject(user, jobIdPath1, Boolean.FALSE, null, null);
//            }
//        } else {
//            for (String showDeptId : showDeptIds) {
//                addShowObject(user, showDeptId, Boolean.FALSE, null, null);
//            }
//        }
//
//    }

    /**
     * 过滤显示的节点
     *
     * @param user
     * @param jobIdPath
     * @param rawUnitUsers
     * @return void
     **/
    private static void filterShowObject(Map<String, String> user, String jobIdPath, List<String> rawUnitUsers) {
        MultiOrgGroupFacade multiOrgGroupFacade = ApplicationContextHolder.getBean(MultiOrgGroupFacade.class);
        filterShowObjectByJobId(user, jobIdPath);
        // 过滤显示的节点-选中的节点才能显示
        filterShowObjectByRawUnitUsers(user, rawUnitUsers);

    }

    /**
     * 过滤显示的节点-选中的节点才能显示
     *
     * @param user
     * @param rawUnitUsers
     * @return void
     **/
    private static void filterShowObjectByRawUnitUsers(Map<String, String> user, List<String> rawUnitUsers) {

        if ("notRemoveUser".equals(user.get("notRemoveUser"))) {
            // 多个用;号隔开
            String showJobIdPaths = user.get("showJobIdPath");
            if (StringUtils.isBlank(showJobIdPaths)) {
                return;
            }
            Boolean showFlag = Boolean.FALSE;
            String[] showJobIdPathStrs = showJobIdPaths.split(";");
            StringBuilder rawUnitUserIds = new StringBuilder();
            for (int i = 0; i < showJobIdPathStrs.length; i++) {
                String showJobIdPathStr = showJobIdPathStrs[i];

                for (String rawUnitUser : rawUnitUsers) {
                    if (showJobIdPathStr.indexOf(rawUnitUser) > -1) {
                        showFlag = Boolean.TRUE;
                        rawUnitUserIds.append(rawUnitUser);
                    }
                }
            }
            if (!showFlag) {
                user.put("removeUser", "removeUser");
                user.remove("notRemoveUser");
            } else {
                filterShowObjectByJobId(user, rawUnitUserIds.toString());
            }
        }
    }

    /**
     * 过滤显示的节点-通过指定节点
     *
     * @param user
     * @param jobIdPath
     * @return void
     **/
    private static void filterShowObjectByJobId(Map<String, String> user, String jobIdPath) {
        // 多个用;号隔开
        String showJobIdPaths = user.get("showJobIdPath");
        String showJobNamePaths = user.get("showJobNamePath");
        String showJobNames = user.get("showJobName");

        if (StringUtils.isBlank(showJobIdPaths) || StringUtils.isBlank(showJobNamePaths)
                || StringUtils.isBlank(showJobNames)) {
            user.put("removeUser", "removeUser");
            user.remove("notRemoveUser");
            return;
        }

        String[] showJobIdPathStrs = showJobIdPaths.split(";");
        String[] showJobNamePathStrs = showJobNamePaths.split(";");
        String[] showJobNameStrs = showJobNames.split(";");
        StringBuilder showJobIdPathSb = new StringBuilder();
        StringBuilder showJobNamePathSb = new StringBuilder();
        StringBuilder showJobNameSb = new StringBuilder();

        for (int i = 0; i < showJobIdPathStrs.length; i++) {
            String showJobIdPathStr = showJobIdPathStrs[i];
            String[] jobIdPaths = jobIdPath.split(";");
            for (String idPath : jobIdPaths) {
                if (showJobIdPathStr.indexOf(idPath) > -1) {
                    // 取交集
                    showJobIdPathSb.append(showJobIdPathStr);
                    showJobNamePathSb.append(showJobNamePathStrs[i]);
                    showJobNameSb.append(showJobNameStrs[i]);
                    if (i < showJobIdPathStrs.length - 1) {
                        showJobIdPathSb.append(";");
                        showJobNamePathSb.append(";");
                        showJobNameSb.append(";");
                    }
                }
            }
        }
        if (StringUtils.isNotBlank(showJobIdPathSb.toString())) {
            // 有命中
            String showJobIdPathLastStr = showJobIdPathSb.toString().substring(showJobIdPathSb.toString().length() - 1);
            if (showJobIdPathLastStr.indexOf(";") > -1) {
                user.put("showJobIdPath",
                        showJobIdPathSb.toString().substring(0, showJobIdPathSb.toString().length() - 1));
                user.put("showJobNamePath",
                        showJobNamePathSb.toString().substring(0, showJobNamePathSb.toString().length() - 1));
                user.put("showJobName", showJobNameSb.toString().substring(0, showJobNameSb.toString().length() - 1));
            } else {
                user.put("showJobIdPath", showJobIdPathSb.toString());
                user.put("showJobNamePath", showJobNamePathSb.toString());
                user.put("showJobName", showJobNameSb.toString());
            }
            user.put("notRemoveUser", "notRemoveUser");
            user.remove("removeUser");
        } else {
            if (StringUtils.isBlank(user.get("notRemoveUser"))) {
                user.put("removeUser", "removeUser");
                user.remove("notRemoveUser");
            }

        }
    }

    /**
     * 添加要显示的职位id路径,职位名称路径，职位名称
     *
     * @param user        用户信息
     * @param rawUnitUser 选中的节点
     * @return void
     **/
//    private static void addShowObject(Map<String, String> user, String rawUnitUser, Boolean isGroup, String groupId,
//                                      String groupName) {
//        // 判断主职
//        String mainJobIdPath = user.get("mainJobIdPath");
//        String otherJobIdPaths = user.get("otherJobIdPaths");
//        // 多个用;号隔开
//        String showJobIdPaths = user.get("showJobIdPath");
//        String showJobNamePaths = user.get("showJobNamePath");
//        String showJobNames = user.get("showJobName");
//
//        // 判断去重
//        if (StringUtils.isNotBlank(showJobIdPaths) && showJobIdPaths.indexOf(rawUnitUser) > -1) {
//            return;
//        }
//
//        // 直接命中用户
//        if (rawUnitUser.startsWith("U") && user.get("id").equals(rawUnitUser)) {
//            if (StringUtils.isNotBlank(showJobIdPaths)) {
//                if (isGroup) {
//                    user.put("showJobIdPath", showJobIdPaths + ";" + groupId + "/");
//                    user.put("showJobNamePath", showJobNamePaths + ";" + groupName + "/");
//                    user.put("showJobName", showJobNames + ";" + " ");
//
//                } else {
//                    user.put("showJobIdPath", showJobIdPaths + ";" + " ");
//                    user.put("showJobNamePath", showJobNamePaths + ";" + " ");
//                    user.put("showJobName", showJobNames + ";" + " ");
//                }
//            } else {
//                // 直接命中用户
//                if (isGroup) {
//                    user.put("showJobIdPath", groupId + "/");
//                    user.put("showJobNamePath", groupName + "/");
//                    user.put("showJobName", " ");
//
//                } else {
//                    user.put("showJobIdPath", " ");
//                    user.put("showJobNamePath", " ");
//                    user.put("showJobName", " ");
//                }
//            }
//            return;
//        }
//
//        if (StringUtils.isNotBlank(mainJobIdPath) && StringUtils.isNotBlank(rawUnitUser)
//                && mainJobIdPath.indexOf(rawUnitUser) > -1) {
//            userMainJobHand(user, rawUnitUser, isGroup, groupId, groupName);
//        }
//
//        if (StringUtils.isNotBlank(otherJobIdPaths) && StringUtils.isNotBlank(rawUnitUser)
//                && otherJobIdPaths.indexOf(rawUnitUser) > -1) {
//            // 其他职位存在多个时，只显示命中的职位
//            userOtherJobHand(user, rawUnitUser, isGroup, groupId, groupName);
//        }
//    }

    /**
     * 添加要显示的职位id路径,职位名称路径，职位名称-主职职位处理
     *
     * @param user
     * @param rawUnitUser
     * @param isGroup
     * @param groupId
     * @param groupName
     * @return void
     **/
//    private static void userMainJobHand(Map<String, String> user, String rawUnitUser, Boolean isGroup, String groupId,
//                                        String groupName) {
//        String mainJobIdPath = user.get("mainJobIdPath");
//        String mainJobNamePath = user.get("mainJobNamePath");
//        String mainJobName = user.get("mainJobName");
//        String showJobIdPaths = user.get("showJobIdPath");
//        String showJobNamePaths = user.get("showJobNamePath");
//        String showJobNames = user.get("showJobName");
//        if (isGroup) {
//            if (StringUtils.isNotBlank(showJobIdPaths) || " ".equals(showJobIdPaths)) {
//                user.put("showJobIdPath", showJobIdPaths + ";" + groupId + "/" + mainJobIdPath);
//            } else {
//                user.put("showJobIdPath", groupId + "/" + mainJobIdPath);
//            }
//
//            if (StringUtils.isNotBlank(showJobNamePaths) || " ".equals(showJobNamePaths)) {
//                user.put("showJobNamePath", showJobNamePaths + ";" + groupName + "/" + mainJobNamePath);
//            } else {
//                user.put("showJobNamePath", groupName + "/" + mainJobNamePath);
//            }
//
//            groupNamePathHandle(user, rawUnitUser, groupId);
//
//        } else {
//            if (StringUtils.isNotBlank(showJobIdPaths) || " ".equals(showJobIdPaths)) {
//                user.put("showJobIdPath", showJobIdPaths + ";" + mainJobIdPath);
//            } else {
//                user.put("showJobIdPath", mainJobIdPath);
//            }
//
//            if (StringUtils.isNotBlank(showJobNamePaths) || " ".equals(showJobNamePaths)) {
//                user.put("showJobNamePath", showJobNamePaths + ";" + mainJobNamePath);
//            } else {
//                user.put("showJobNamePath", mainJobNamePath);
//            }
//        }
//        if (StringUtils.isNotBlank(showJobNames) || " ".equals(showJobNames)) {
//            user.put("showJobName", showJobNames + ";" + mainJobName);
//        } else {
//            user.put("showJobName", mainJobName);
//        }
//    }

    /**
     * 添加要显示的职位id路径,职位名称路径，职位名称-其他职位处理
     *
     * @param user
     * @param rawUnitUser
     * @param isGroup
     * @param groupId
     * @param groupName
     * @return void
     **/
//    private static void userOtherJobHand(Map<String, String> user, String rawUnitUser, Boolean isGroup, String groupId,
//                                         String groupName) {
//        int selectIndex = 0;
//        String otherJobIdPaths = user.get("otherJobIdPaths");
//        String otherJobNamePaths = user.get("otherJobNamePaths");
//        String otherJobNames = user.get("otherJobNames");
//        String[] otherJobIdPathStrs = otherJobIdPaths.split(";");
//        String[] otherJobNamePathStrs = otherJobNamePaths.split(";");
//        String[] otherJobNameStrs = otherJobNames.split(";");
//        for (int i = 0; i < otherJobIdPathStrs.length; i++) {
//            if (otherJobIdPathStrs[i].indexOf(rawUnitUser) > -1) {
//                selectIndex = i;
//                // 判断其他职位
//                String showJobIdPaths = showJobIdPaths = user.get("showJobIdPath");
//                String showJobNamePaths = showJobNamePaths = user.get("showJobNamePath");
//                String showJobNames = showJobNames = user.get("showJobName");
//
//                if (isGroup) {
//                    if (StringUtils.isNotBlank(showJobIdPaths) || " ".equals(showJobIdPaths)) {
//                        user.put("showJobIdPath",
//                                showJobIdPaths + ";" + groupId + "/" + otherJobIdPathStrs[selectIndex]);
//                    } else {
//                        user.put("showJobIdPath", groupId + "/" + otherJobIdPathStrs[selectIndex]);
//                    }
//
//                    if (StringUtils.isNotBlank(showJobNamePaths) || " ".equals(showJobNamePaths)) {
//                        user.put("showJobNamePath",
//                                showJobNamePaths + ";" + groupName + "/" + otherJobNamePathStrs[selectIndex]);
//                    } else {
//                        user.put("showJobNamePath", groupName + "/" + otherJobNamePathStrs[selectIndex]);
//                    }
//
//                    groupNamePathHandle(user, rawUnitUser, groupId);
//                } else {
//                    if (StringUtils.isNotBlank(showJobIdPaths) || " ".equals(showJobIdPaths)) {
//                        user.put("showJobIdPath", showJobIdPaths + ";" + otherJobIdPathStrs[selectIndex]);
//                    } else {
//                        user.put("showJobIdPath", otherJobIdPathStrs[selectIndex]);
//                    }
//
//                    if (StringUtils.isNotBlank(showJobNamePaths) || " ".equals(showJobNamePaths)) {
//                        user.put("showJobNamePath", showJobNamePaths + ";" + otherJobNamePathStrs[selectIndex]);
//                    } else {
//                        user.put("showJobNamePath", otherJobNamePathStrs[selectIndex]);
//                    }
//                }
//
//                if (StringUtils.isNotBlank(showJobNames) || " ".equals(showJobNames)) {
//                    user.put("showJobName", showJobNames + ";" + otherJobNameStrs[selectIndex]);
//                } else {
//                    user.put("showJobName", otherJobNameStrs[selectIndex]);
//                }
//            }
//        }
//    }

    /**
     * 群组名称路径处理，用于在前端显示到群组成员的路径节点
     *
     * @param user
     * @param rawUnitUser
     * @param groupId
     */
//    private static void groupNamePathHandle(Map<String, String> user, String rawUnitUser, String groupId) {
//        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(rawUnitUser)) {
//            OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
//            if (groupId.startsWith(IdPrefix.GROUP.getValue())) {
//                MultiOrgGroupMember multiOrgGroupMember = orgApiFacade.getGroupVoById(groupId).getMemberList().stream()
//                        .filter(member -> member.getMemberObjId().equals(rawUnitUser)).findFirst().get();
//                if (multiOrgGroupMember != null) {
//                    if (StringUtils.isNotBlank(user.get("groupType"))) {
//                        user.put("groupType",
//                                user.get("groupType") + Separator.SEMICOLON.getValue() + IdPrefix.GROUP.getValue());
//                    } else {
//                        user.put("groupType", IdPrefix.GROUP.getValue());
//                    }
//                    if (StringUtils.isNotBlank(user.get("memberObjId"))) {
//                        user.put("memberObjId", user.get("memberObjId") + Separator.SEMICOLON.getValue()
//                                + multiOrgGroupMember.getMemberObjId());
//                    } else {
//                        user.put("memberObjId", multiOrgGroupMember.getMemberObjId());
//                    }
//                    if (StringUtils.isNotBlank(user.get("memberObjName"))) {
//                        user.put("memberObjName", user.get("memberObjName") + Separator.SEMICOLON.getValue()
//                                + multiOrgGroupMember.getMemberObjName());
//                    } else {
//                        user.put("memberObjName", multiOrgGroupMember.getMemberObjName());
//                    }
//                }
//            } else if (groupId.startsWith(IdPrefix.DUTY.getValue())) {
//                MultiOrgJobDutyFacade multiOrgJobDutyFacade = ApplicationContextHolder
//                        .getBean(MultiOrgJobDutyFacade.class);
//                MultiOrgJobDuty multiOrgJobDuty = multiOrgJobDutyFacade.getJobDutyByDutyId(groupId).stream()
//                        .filter(member -> member.getJobId().equals(rawUnitUser)).findFirst().get();
//                if (multiOrgJobDuty != null) {
//                    MultiOrgTreeNode multiOrgTreeNode = orgApiFacade
//                            .getOrgTreeNodeListByNodeIds(Collections.singleton(multiOrgJobDuty.getJobId())).get(0);
//                    if (StringUtils.isNotBlank(user.get("groupType"))) {
//                        user.put("groupType",
//                                user.get("groupType") + Separator.SEMICOLON.getValue() + IdPrefix.DUTY.getValue());
//                    } else {
//                        user.put("groupType", IdPrefix.DUTY.getValue());
//                    }
//                    if (StringUtils.isNotBlank(user.get("memberObjId"))) {
//                        user.put("memberObjId", user.get("memberObjId") + Separator.SEMICOLON.getValue()
//                                + multiOrgTreeNode.getEleIdPath());
//                    } else {
//                        user.put("memberObjId", multiOrgTreeNode.getEleIdPath());
//                    }
//                    if (StringUtils.isNotBlank(user.get("memberObjName"))) {
//                        user.put("memberObjName", user.get("memberObjName") + Separator.SEMICOLON.getValue() + "");
//                    } else {
//                        user.put("memberObjName", "");
//                    }
//                }
//            }
//        }
//    }

    /**
     * 获取前办理人用户ID，如果是委托待办则返回委托人，否则返回当前用户
     *
     * @param token
     * @param currentUserId
     * @return
     */
    private static String getPriorUserId(Token token, String currentUserId) {
        TaskInstance taskInstance = token.getTask();
        IdentityService identityService = ApplicationContextHolder.getBean(IdentityService.class);
        if (taskInstance == null) {
            return currentUserId;
        }
        String key = taskInstance.getUuid() + currentUserId;
        String taskIdentityUuid = token.getTaskData().getTaskIdentityUuid(key);
        if (StringUtils.isBlank(taskIdentityUuid)) {
            return currentUserId;
        }
        TaskIdentity taskIdentity = identityService.get(taskIdentityUuid);
        if (taskIdentity == null) {
            return currentUserId;
        }
        // 判断当前用户是否进行委托提交
        Integer todoType = taskIdentity.getTodoType();
        if (!WorkFlowTodoType.Delegation.equals(todoType)) {
            return currentUserId;
        }
        String ownerId = taskIdentity.getOwnerId();
        if (StringUtils.isNotBlank(ownerId)) {
            return ownerId;
        }
        return currentUserId;
    }

    /**
     * @param token
     * @return
     */
    private static String getSelectJobValue(Token token) {
        String jobField = token.getFlowInstance().getFlowDefinition().getJobField();
        DyFormData dyFormData = token.getTaskData().getDyFormData(token.getTaskData().getDataUuid());
        if (StringUtils.isNotBlank(jobField)) {
            String jobValue = ObjectUtils.toString(dyFormData.getFieldValue(jobField), StringUtils.EMPTY);
            if (StringUtils.isNotBlank(jobValue)) {
                return jobValue;
            }
        }
        return token.getFlowInstance().getStartJobId();
    }

    /**
     * (non-Javadoc)
     */
    public List<FlowUserSid> resolve(final Node node, final Token token) {
        // 一、承办人
        // 获取流程定义中设置的参与者
        Set<FlowUserSid> userIds = Sets.newLinkedHashSet();
        final FlowDelegate flowDelegate = token.getFlowDelegate();
        TaskData taskData = token.getTaskData();
        boolean startNewFlow = taskData.getStartNewFlow(token.getFlowInstance().getUuid());
        // 当前用户ID
        final String userId = taskData.getUserId();
        // 当前环节ID
        final String taskId = node.getId();
        // 是否现在确定参与者
        boolean isSetUser = flowDelegate.getIsSetTaskUser(taskId);
        // 1.1、子流程
        if (node instanceof SubTaskNode) {
            userIds.add(new FlowUserSid(userId, taskData.getUserName()));
        } else if (Boolean.TRUE.equals(startNewFlow)) {// 1.2、启动流程
            // 由前一办理人指定
            Set<String> rawUsers = taskData.getTaskUsers(taskId);
            if (!rawUsers.isEmpty()) {
                userIds.addAll(sidGranularityResolver.resolve(node, token, rawUsers));
            } else if (StringUtils.isNotBlank(userId)) {// 如果是启动流程第一个任务开始，则从办理人为当前用户
                // 如果是自定义提交按钮，则开始提交到该按钮
                if (taskData.getUseCustomDynamicButton(taskId)) {
                    if (flowDelegate.isFirstTaskNode(taskId)) {
                        userIds.add(new FlowUserSid(userId, taskData.getUserName()));
                    } else {
                        userIds.addAll(
                                resolve(node, token, flowDelegate.getTaskUsers(taskId), ParticipantType.TodoUser));
                    }
                } else if (taskId.equals(taskData.getToTaskId(FlowDelegate.START_FLOW_ID))) {
                    // 子流程提交到指定环节
                    userIds.addAll(resolve(node, token, flowDelegate.getTaskUsers(taskId), ParticipantType.TodoUser));
                    if (CollectionUtils.isEmpty(userIds)) {
                        // 由子流程启动的新流程
                        String startNewFlowId = (String) taskData.get("startNewFlowId");
                        if (StringUtils.isNotBlank(startNewFlowId)) {
                            Map<String, Object> variables = new HashMap<String, Object>();
                            String name = flowDelegate.getFlow().getName();
                            variables.put("title", "(" + name + ":" + node.getName() + ")");
                            variables.put("taskId", startNewFlowId);
                            variables.put("submitButtonId", taskData.getSubmitButtonId());
                            throw new TaskNotAssignedUserException(variables, token);
                        }
                    }
                } else {
                    FlowUserJobIdentityService flowUserJobIdentityService = ApplicationContextHolder.getBean(FlowUserJobIdentityService.class);
                    userIds.add(flowUserJobIdentityService.getStartUserSid(userId, taskData, node, flowDelegate, token));
                }
            } else {
                Map<String, Object> variables = new HashMap<String, Object>();
                String name = flowDelegate.getFlow().getName();
                variables.put("title", "(" + name + ":" + node.getName() + ")");
                variables.put("taskName", node.getName());
                variables.put("taskId", taskId);
                variables.put("submitButtonId", taskData.getSubmitButtonId());
                throw new TaskNotAssignedUserException(variables, token);
            }
        } else if (!taskData.getTaskUsers(taskId).isEmpty()) {// 1.4、由前一办理人指定
            Set<String> rawUsers = taskData.getTaskUsers(taskId);
            List<FlowUserSid> flowUserSids = sidGranularityResolver.resolve(node, token, rawUsers);
            List<String> jobPaths = taskData.getTaskUserJobPaths(taskId);
            if (CollectionUtils.isNotEmpty(flowUserSids) && CollectionUtils.isNotEmpty(jobPaths)) {
                FlowUserJobIdentityService flowUserJobIdentityService = ApplicationContextHolder.getBean(FlowUserJobIdentityService.class);
                flowUserJobIdentityService.addUnitUserJobIdentity(flowUserSids, jobPaths, false, node.getId(), token, ParticipantType.TodoUser);
            }
            userIds.addAll(flowUserSids);
        } else if (isSetUser && !taskData.isSpecifyTaskUser(taskId)) {// 1.3、指定办理人 现在指定，且办理人不是从客户端提交的事
            userIds.addAll(resolve(node, token, flowDelegate.getTaskUsers(taskId), ParticipantType.TodoUser));
        } else {
            // 由子流程启动的新流程
            String startNewFlowId = (String) taskData.get("startNewFlowId");
            String interactionTaskId = taskId;
            if (StringUtils.isNotBlank(startNewFlowId)) {
                interactionTaskId = startNewFlowId;
            }
            Map<String, Object> variables = new HashMap<String, Object>();
            String name = flowDelegate.getFlow().getName();
            variables.put("title", "(" + name + ":" + node.getName() + ")");
            variables.put("taskId", interactionTaskId);
            variables.put("submitButtonId", taskData.getSubmitButtonId());
            throw new TaskNotAssignedUserException(variables, token);
        }

        // 1.5、办理人办空自动进入下一环节
        // 办理人为空自动进入下一个环节，如果没有指定下一节点则由当前节点算出，否则进入指定的下一节点
        boolean isSetUserEmptyToTask = flowDelegate.getIsSetUserEmptyToTask(taskId);
        boolean isSetUserEmptyToUser = flowDelegate.getIsSetUserEmptyToUser(taskId);
        if (isSetUser && userIds.isEmpty()) {
            taskData.setIsEmptyToTask(taskId, isSetUserEmptyToTask);
            if (isSetUserEmptyToTask) {
                String emptyToTask = flowDelegate.getEmptyToTask(taskId);
                if (StringUtils.isBlank(emptyToTask)) {
                    throw new WorkFlowException("找不到办理人时，自动进入下一个环节为空！");
                }
                Node emptyToTaskNode = flowDelegate.getTaskNode(emptyToTask);
                if (emptyToTaskNode == null || emptyToTaskNode.getId() == null) { // 环节不存在，则弹出组织选择框让用户选择
                    Map<String, Object> variables = new HashMap<String, Object>();
                    variables.put("title", "(" + flowDelegate.getFlow().getName() + ":" + node.getName() + ")");
                    variables.put("taskId", taskId);
                    variables.put("submitButtonId", taskData.getSubmitButtonId());
                    throw new TaskNotAssignedUserException(variables, token);
                }
                logger.debug("指定的办理人办空" + emptyToTask);
                taskData.setEmptyToTask(taskId, emptyToTask);

                // 办理人为空转办时消息通知已办人员
                boolean emptyNoteDone = flowDelegate.getEmptyNoteDone(taskId);
                taskData.setEmptyNoteDone(taskId, emptyNoteDone);
                logger.debug("办理人为空转办时消息通知已办人员" + emptyNoteDone);
            } else if (isSetUserEmptyToUser) {
                userIds.addAll(
                        resolve(node, token, flowDelegate.getTaskEmptyToUsers(taskId), ParticipantType.TodoUser));
                if (userIds.isEmpty()) {
                    // 办理人为空，且没有设置"办理人为空自动进入下一个环节"，则进行用户选择
                    logger.debug("环节[" + taskId + "]办理人为空，且没有设置[办理人为空自动进入下一个环节]，则进行用户选择");
                    Map<String, Object> variables = new HashMap<String, Object>();
                    String name = flowDelegate.getFlow().getName();
                    variables.put("title", "(" + name + ":" + node.getName() + ")");
                    variables.put("taskId", taskId);
                    variables.put("submitButtonId", taskData.getSubmitButtonId());
                    throw new TaskNotAssignedUserException(variables, token);
                }
                // 办理人为空转办时消息通知已办人员
                boolean emptyNoteDone = flowDelegate.getEmptyNoteDone(taskId);
                taskData.setEmptyNoteDone(taskId, emptyNoteDone);
                logger.debug("办理人为空转办时消息通知已办人员" + emptyNoteDone);
            } else {
                // 办理人为空，且没有设置"办理人为空自动进入下一个环节"，则进行用户选择
                logger.debug("环节[" + taskId + "]办理人为空，且没有设置[办理人为空自动进入下一个环节]，则进行用户选择");
                Map<String, Object> variables = new HashMap<String, Object>();
                String name = flowDelegate.getFlow().getName();
                variables.put("title", "(" + name + ":" + node.getName() + ")");
                variables.put("taskId", taskId);
                variables.put("submitButtonId", taskData.getSubmitButtonId());
                throw new TaskNotAssignedUserException(variables, token);
            }
        }

        // 二、多人办理
        if (!userIds.isEmpty() && userIds.size() > 1) {
            // 2.1 、选择具体办理人
            boolean isSelectAgain = flowDelegate.isSelectAgain(taskId);
            // 2.2、只能选择一个人办理
            boolean isOnlyOne = flowDelegate.isOnlyOne(taskId);
            // 从组织选择框选择人员
            UnitUser unitUser = null;
            if (token.getTask() != null) {
                String taskInstUuid = token.getTask().getUuid();
                String key = taskInstUuid + "_" + taskId;
                unitUser = (UnitUser) taskData.get(key);
            }
            List<UserUnitElement> unitElements = flowDelegate.getTaskUsers(taskId);
            if (isSelectAgain && isOnlyOne) {
                List<Map<String, String>> users = getOrgIdNameMapList(userIds);
                if (unitUser == null) {
                    throw new OnlyChooseOneUserException(node, token, node.getName(), taskId, Lists.newArrayList(userIds),
                            token.getTaskData().getSubmitButtonId(), users, unitElements);
                } else {
                    unitUser.setMultiple(false);
                    throw new ChooseUnitUserException(node, token, unitUser, node.getName(), taskId,
                            userIds, token.getTaskData().getSubmitButtonId(), users,
                            JsonDataErrorCode.OnlyChooseOneUser, unitElements);
                }
            } else if (isSelectAgain && !taskData.isSpecifyTaskUser(taskId)) {
                if (taskData.isApiInvoke()) {
                    List<Map<String, String>> users = getOrgIdNameMapList(userIds);
                    if (unitUser == null) {
                        throw new ChooseSpecificUserException(node, token, node.getName(), taskId,
                                userIds, token.getTaskData().getSubmitButtonId(), users, unitElements);
                    } else {
                        throw new ChooseUnitUserException(node, token, unitUser, node.getName(), taskId,
                                userIds, taskData.getSubmitButtonId(), users,
                                JsonDataErrorCode.ChooseSpecificUser, unitElements);
                    }
                } else {
                    if (unitUser == null) {
                        List<Map<String, String>> users = getOrgIdNameMapList(userIds);
                        throw new ChooseSpecificUserException(node, token, node.getName(), taskId,
                                userIds, token.getTaskData().getSubmitButtonId(), users, unitElements);
                    } else {
                        throw new ChooseUnitUserException(node, token, unitUser, node.getName(), taskId,
                                userIds, taskData.getSubmitButtonId(), null,
                                JsonDataErrorCode.ChooseSpecificUser, unitElements);
                    }
                }
            }

            // 2.3、只需要其中一个人办理
            boolean isAnyone = flowDelegate.isAnyone(taskId);
            if (isAnyone) {
                taskData.setIsAnyone(taskId, isAnyone);
            }
        }
        // 2.4 、按人员顺序依次办理，加签可让人员变多人
        boolean isByOrder = flowDelegate.isByOrder(taskId);
        if (isByOrder) {
            taskData.setIsByOrder(taskId, isByOrder);
        }

        // 三、抄告人
        if (node instanceof TaskNode) {
            // 解析抄送人，抄送的权限粒度都为人员
            List<FlowUserSid> copyUserIds = new ArrayList<FlowUserSid>();
            String isSetCopyUser = flowDelegate.getIsSetCopyUser(taskId);
            // 1、现在确定抄送人
            if ("1".equals(isSetCopyUser)) {
                // 是否二次确认抄送人
                boolean isConfirmCopyUser = flowDelegate.isConfirmCopyUser(taskId);
                Set<String> confirmCopyUsers = null;
                if (isConfirmCopyUser) {
                    confirmCopyUsers = token.getTaskData().getTaskCopyUsers(taskId);
                }
                // 二次确认选择的抄送人
                if (CollectionUtils.isNotEmpty(confirmCopyUsers)) {
                    copyUserIds.addAll(resolveTaskCopyUsers(token, node, taskId));
                } else {
                    String copyUserCondition = flowDelegate.getCopyUserCondition(taskId);
                    if (StringUtils.isBlank(copyUserCondition)) {
                        copyUserIds.addAll(sidGranularityResolver.resolveWithSid(node, token,
                                resolve(node, token, flowDelegate.getTaskCopyUsers(taskId), ParticipantType.CopyUser)));
                    } else {
                        copyUserIds.addAll(resolveWidthCopyUserCondition(node, token, taskId,
                                new Function<Token, List<FlowUserSid>>() {

                                    @Override
                                    public List<FlowUserSid> apply(Token token) {
                                        return sidGranularityResolver.resolveWithSid(node, token, resolve(node, token,
                                                flowDelegate.getTaskCopyUsers(taskId), ParticipantType.CopyUser));
                                    }

                                }));
                    }
                    // 抄送人进行二次确认
                    if (isConfirmCopyUser && CollectionUtils.isNotEmpty(copyUserIds)) {
                        List<Map<String, String>> users = getOrgIdNameMapList(
                                Sets.<FlowUserSid>newHashSet(copyUserIds));
                        throw new ChooseSpecificCopyUserException(node, token, node.getName(), taskId,
                                copyUserIds, token.getTaskData().getSubmitButtonId(), users,
                                flowDelegate.getTaskCopyUsers(taskId));
                    }
                }
            } else if ("2".equals(isSetCopyUser)) {// 2、由前一环节办理人确定
                copyUserIds.addAll(resolveTaskCopyUsers(token, node, taskId));
            } else if ("3".equals(isSetCopyUser)) {// 3、由先决条件确定
                copyUserIds.addAll(
                        resolveWidthCopyUserCondition(node, token, taskId, new Function<Token, List<FlowUserSid>>() {

                            @Override
                            public List<FlowUserSid> apply(Token token) {
                                return resolveTaskCopyUsers(token, node, taskId);
                            }

                        }));
            }
            // 直接提交至退回环节时抄送跳过的环节
            copyUserIds.addAll(taskData.getSkipTaskCopyUsers());
            // 清空办理人前台页面选择确定的
            taskData.getTaskCopyUserSids(taskId).clear();
            taskData.addTaskCopyUserSids(taskId, copyUserIds);
        }
        return Lists.newArrayList(userIds);
    }

    /**
     * 解析抄送人为由先决条件确定的人员
     *
     * @param node
     * @param token
     * @param taskId
     * @return
     */
    private List<FlowUserSid> resolveWidthCopyUserCondition(Node node, Token token, String taskId,
                                                            Function<Token, List<FlowUserSid>> function) {
        List<FlowUserSid> copyUserIds = new ArrayList<FlowUserSid>();
        FlowDelegate flowDelegate = token.getFlowDelegate();
        TaskInstance taskInstance = token.getTask();
        TaskData taskData = token.getTaskData();
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        String copyUserCondition = flowDelegate.getCopyUserCondition(taskId);
        RuleEngine ruleEngine = RuleEngineFactory.getRuleEngine();
        ruleEngine.setVariable("dyFormData", dyFormData);
        if (dyFormData != null) {
            ruleEngine.setVariable("dyform", dyFormData.getFormDataOfMainform());
        } else {
            ruleEngine.setVariable("dyform", Maps.newHashMapWithExpectedSize(0));
        }
        if (taskInstance != null) {
            ruleEngine.setVariable("actionType", taskInstance.getActionType());
            ruleEngine.setVariable("fromTaskId", taskInstance.getId());
            ruleEngine.setVariable("toTaskId", node.getId());
            ruleEngine.setVariable("taskId", node.getId());
        }
        String exp = copyUserCondition;
        Object conditionResult = StringUtils.isBlank(copyUserCondition);
        if (StringUtils.isNotBlank(copyUserCondition)) {
            String scriptText = "if (" + exp + "){ set conditionResult = true end } end";
            try {
                ruleEngine.execute(scriptText);
            } catch (Exception e) {
                logger.error("解析流程抄送人的前置条件表达式异常, 表达式 = {} ：", exp, e);
                throw new RuntimeException("流程抄送异常");
            }
            conditionResult = ruleEngine.getVariable("conditionResult");
        }
        if (Boolean.TRUE.equals(conditionResult)) {
            copyUserIds.addAll(function.apply(token));
        }
        return copyUserIds;
    }

    /**
     * @param orgSids
     * @return
     */
    private List<Map<String, String>> getOrgIdNameMapList(Set<FlowUserSid> orgSids) {
        List<Map<String, String>> sids = new ArrayList<Map<String, String>>();
        if (CollectionUtils.isEmpty(orgSids)) {
            return sids;
        }
        for (FlowUserSid orgSid : orgSids) {
            Map<String, String> userMap = new HashMap<String, String>();
            userMap.put("id", orgSid.getId());
            userMap.put("name", orgSid.getName());
            // isSaveJobPath jobIdPath
//            userMap.put("isSaveJobPath", String.valueOf(orgSid.getSaveJobPath()));
//            userMap.put("jobIdPath", orgSid.getJobIdPath());
//
//            userMap.put("applyUserFlag", String.valueOf(orgSid.getApplyUserFlag()));
//            userMap.put("applyUserJobIds", getPriorUserJobIdStr(orgSid.getApplyUserJobIds()));
//            userMap.put("filterUserFlag", String.valueOf(orgSid.getFilterUserFlag()));
//            userMap.put("filterUserJobIds", getPriorUserJobIdStr(orgSid.getFilterUserJobIds()));
            sids.add(userMap);
        }
        return sids;
    }

    private String getPriorUserJobIdStr(List<String> priorUserJobIds) {
        StringBuilder jobIds = new StringBuilder();

        if (priorUserJobIds == null) {
            return jobIds.toString();
        }
        for (String priorUserJobId : priorUserJobIds) {
            jobIds.append(priorUserJobId);
            jobIds.append(";");
        }
        return jobIds.toString();
    }

    /**
     * @param node
     * @param token
     * @param unitElements
     * @return
     */
    public List<FlowUserSid> resolve(Node node, Token token, List<UserUnitElement> unitElements,
                                     ParticipantType participantType) {
        String sidGranularity = null;
        if (node != null && token != null) {
            sidGranularity = token.getFlowDelegate().getGranularity(node.getId(), token.getTaskData(), unitElements);
        }
        return resolve(node, token, unitElements, participantType, sidGranularity);
    }

    /**
     * @param node
     * @param token
     * @param unitElements
     * @param sidGranularity
     * @return
     */
    public List<FlowUserSid> resolve(Node node, Token token, List<UserUnitElement> unitElements,
                                     ParticipantType participantType, String sidGranularity) {
        if (CollectionUtils.isEmpty(unitElements)) {
            return Collections.emptyList();
        }

        List<FlowUserSid> userSids = null;
        boolean hasGroup = unitElements.stream().filter(unitElement -> StringUtils.isNotBlank(unitElement.getGroupId())).count() > 0;
        // 新版支持业务组织的人员解析
        if (hasGroup) {
            userSids = resolveUserUnits(node, token, unitElements, participantType, sidGranularity);
        } else {
            List<FlowUserSid> userIds = new ArrayList<FlowUserSid>();
            // 组织框
            List<String> rawUnitUsers = new ArrayList<String>();
            // 文档域
            List<String> rawFormFieldUsers = new ArrayList<String>();
            // 办理环节
            List<String> rawTaskHistoryUsers = new ArrayList<String>();
            // 人员选项和人员过滤
            List<String> rawOptionUsers = new ArrayList<String>();
            // 自定义
            List<String> rawBizRoleUsers = new ArrayList<String>();
            UnitUser unitUser = new UnitUser();
            // 只配置组织选择框
            boolean onlyUnitUser = true;
            for (UnitElement unitElement : unitElements) {
                Integer type = unitElement.getType();
                String value = unitElement.getValue();
                if (Integer.valueOf(1).equals(type)) { // 通过组织机构设置办理人
                    rawUnitUsers.add(value);
                    unitUser.getInitIds().add(value);
                    unitUser.getInitNames().add(unitElement.getArgValue());
                } else if (Integer.valueOf(2).equals(type)) { // 通过文档域设置办理人
                    rawFormFieldUsers.add(value);
                    onlyUnitUser = false;
                } else if (Integer.valueOf(4).equals(type)) { // 通过办理环节设置办理人
                    rawTaskHistoryUsers.add(value);
                    onlyUnitUser = false;
                } else if (Integer.valueOf(8).equals(type)) { // 通过人员选项和人员过滤设置办理人
                    rawOptionUsers.add(value);
                    // onlyUnitUser = false;
                } else if (Integer.valueOf(16).equals(type)) { // 通过自定义设置办理人
                    rawBizRoleUsers.add(value);
                    onlyUnitUser = false;
                }
            }

            // 1、解析组织选择框选择的用户
            List<FlowUserSid> unitUserIds = unitIdentityResolver.resolve(node, token, rawUnitUsers, participantType,
                    sidGranularity);
            // 2、解析表单域中的用户
            List<FlowUserSid> formFieldUserIds = formFieldIdentityResolver.resolve(node, token, rawFormFieldUsers,
                    participantType, sidGranularity);
            // 3、解析已运行过的流程的参与者
            List<FlowUserSid> taskHistoryUserIds = taskHistoryIdentityResolver.resolve(node, token, rawTaskHistoryUsers,
                    participantType, sidGranularity);
            // 4、 解析参与者
            List<FlowUserSid> optionUserIds = participantIdentityResolver.resolve(node, token, rawOptionUsers,
                    participantType, sidGranularity);

            unitUser.setInitIds(
                    Arrays.asList(token.getTaskData().getTaskOptionUnitUserIds(node.getId()).toArray(new String[0])));
            userIds.addAll(unitUserIds);
            userIds.addAll(formFieldUserIds);
            userIds.addAll(taskHistoryUserIds);
            userIds.addAll(optionUserIds);

            // 人员过滤，对组织机构、单位业务角色、文档域、办理环节和人员选项统一解析出来的人员进行过滤
            userIds = Arrays
                    .asList(identityFilter.doFilter(userIds, node, token, rawOptionUsers).toArray(new FlowUserSid[0]));

            // zyguo 客户端统一以弹出组织框显示，不需要用到unitUsers了，屏蔽掉
            // 人员限制
            // if (DefaultIdentityFilter.isDoFilter(rawOptionUsers)) {
            // unitUser.setLimitIds(resolveAsOrgIds(userIds));
            // }
            //
            // if (onlyUnitUser && token.getTask() != null &&
            // !unitUser.getInitIds().isEmpty()) {
            // String taskInstUuid = token.getTask().getUuid();
            // String key = taskInstUuid + "_" + node.getId();
            // token.getTaskData().put(key, unitUser);
            // }

            // 5、 用户自定义人员, 权限最高
            userSids = userCustomIdentityResolver.resolve(node, token, rawBizRoleUsers,
                    participantType, sidGranularity);
            userSids.addAll(userIds);
        }

        return userSids;
    }

    /**
     * 新版支持业务组织的人员解析
     *
     * @param node
     * @param token
     * @param unitElements
     * @param participantType
     * @param sidGranularity
     * @return
     */
    public List<FlowUserSid> resolveUserUnits(Node node, Token token, List<UserUnitElement> unitElements, ParticipantType participantType, String sidGranularity) {
        List<FlowUserSid> userSids = Lists.newArrayList();
        Map<String, List<UserUnitElement>> userUnitElementMap = Maps.newLinkedHashMap();
        List<UserUnitElement> filterElements = Lists.newArrayList();
        unitElements.forEach(unitElement -> {
            // 新版类型为8时只有人员过滤
            if (Integer.valueOf(8).equals(unitElement.getType())) {
                filterElements.add(unitElement);
            } else {
                String groupId = unitElement.getGroupId();
                List<UserUnitElement> userUnitElements = userUnitElementMap.get(groupId);
                if (userUnitElements == null) {
                    userUnitElements = Lists.newArrayList();
                    userUnitElementMap.put(groupId, userUnitElements);
                }
                userUnitElements.add(unitElement);
            }
        });

        // 人员解析
        for (Map.Entry<String, List<UserUnitElement>> entry : userUnitElementMap.entrySet()) {
            List<UserUnitElement> userUnitElements = entry.getValue();
            for (UserUnitElement unitElement : userUnitElements) {
                int type = unitElement.getType();
                switch (type) {
                    // 行政组织
                    case 1:
                        List<FlowUserSid> unitUserIds = unitIdentityResolver.resolve(node, token, unitElement, participantType, sidGranularity);
                        userSids.addAll(unitUserIds);
                        break;
                    // 表单字段
                    case 2:
                        List<FlowUserSid> formFieldUserIds = formFieldIdentityResolver.resolve(node, token, unitElement, participantType, sidGranularity);
                        userSids.addAll(formFieldUserIds);
                        break;
                    // 办理环节
                    case 4:
                        List<FlowUserSid> taskHistoryUserIds = taskHistoryIdentityResolver.resolve(node, token, unitElement, participantType, sidGranularity);
                        userSids.addAll(taskHistoryUserIds);
                        break;
                    // 自定义
                    case 16:
                        List<FlowUserSid> userCustomUserIds = userCustomIdentityResolver.resolve(node, token, unitElement, participantType, sidGranularity);
                        userSids.addAll(userCustomUserIds);
                        break;
                    // 业务组织
                    case 32:
                        List<FlowUserSid> bizUnitUserIds = bizUnitIdentityResolver.resolve(node, token, unitElement, participantType, sidGranularity);
                        userSids.addAll(bizUnitUserIds);
                        break;
                }
            }
        }

        // 人员过滤
        if (CollectionUtils.isNotEmpty(userSids) && CollectionUtils.isNotEmpty(filterElements)) {
            for (UserUnitElement filterElement : filterElements) {
                userSids = Lists.newArrayList(identityFilter.doFilter(userSids, node, token, filterElement));
            }
        }
        return userSids;
    }

}
