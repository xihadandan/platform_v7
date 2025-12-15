/*
 * @(#)Aug 4, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.workflow.dto.FlowDelegationSettingsDto;
import com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.dto.WfCommonDelegationSettingDto;
import com.wellsoft.pt.bpm.engine.entity.FlowDelegationSettings;
import com.wellsoft.pt.bpm.engine.entity.WfCommonDelegationSettingEntity;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowVariables;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.bpm.engine.service.FlowDelegationSettingsService;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.service.WfCommonDelegationSettingService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.org.entity.DutyAgent;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.service.FlowSchemeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Aug 4, 2017.1	zhulh		Aug 4, 2017		Create
 * </pre>
 * @date Aug 4, 2017
 */
@Service
public class WorkflowDelegationSettiongsServiceImpl implements WorkflowDelegationSettiongsService {

    // 委托失效
    private static final String DUTY_AGENT_DEACTIVE = "DUTY_AGENT_DEACTIVE";
    // 委托生效
    private static final String DUTY_AGENT_ACTIVE = "DUTY_AGENT_ACTIVE";
    // 征求受托人意见
    private static final String DUTY_AGENT_CONSULT = "DUTY_AGENT_CONSULT";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private FlowSchemeService flowSchemeService;

    @Autowired
    private FlowDelegationSettingsService flowDelegationSettingsService;

    @Autowired
    private WfCommonDelegationSettingService commonDelegationSettingService;

    @Autowired
    private TaskDelegationService taskDelegationService;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    //    @Autowired
//    private OrgApiFacade orgApiFacade;
    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#getBean(java.lang.String)
     */
    @Override
    public FlowDelegationSettingsDto getBean(String uuid) {
        FlowDelegationSettings flowDelegationSettings = flowDelegationSettingsService.getOne(uuid);
        FlowDelegationSettingsDto flowDelegationSettingsDto = new FlowDelegationSettingsDto();
        if (flowDelegationSettings != null) {
            BeanUtils.copyProperties(flowDelegationSettings, flowDelegationSettingsDto);
            // 1、设置委托人及受托人的名称
            Set<String> userIdSet = new HashSet<String>();
            if (StringUtils.isNotBlank(flowDelegationSettings.getConsignor())) {
                userIdSet.addAll(Arrays.asList(StringUtils.split(flowDelegationSettings.getConsignor(),
                        Separator.SEMICOLON.getValue())));
            }
            if (StringUtils.isNotBlank(flowDelegationSettings.getTrustee())) {
                userIdSet.addAll(Arrays.asList(StringUtils.split(flowDelegationSettings.getTrustee(),
                        Separator.SEMICOLON.getValue())));
            }
//        List<MultiOrgUserAccount> users = orgApiFacade.queryUserAccountListByIds(userIdSet);
//        Map<String, MultiOrgUserAccount> userMap = ConvertUtils.convertElementToMap(users, "id");
            Map<String, String> userIdNameMap = workflowOrgService.getNamesByIds(Lists.<String>newArrayList(userIdSet));
            flowDelegationSettingsDto.setConsignorName(resolveUsername(userIdNameMap, flowDelegationSettings.getConsignor()));
            flowDelegationSettingsDto.setTrusteeName(resolveUsername(userIdNameMap, flowDelegationSettings.getTrustee()));
            // 2、格式化开始时间与结束时间
//        if (flowDelegationSettings.getFromTime() != null) {
//            flowDelegationSettingsDto.setFormatedFromTime(DateUtils.formatDateTimeMin(flowDelegationSettings
//                    .getFromTime()));
//        }
//        if (flowDelegationSettings.getToTime() != null) {
//            flowDelegationSettingsDto
//                    .setFormatedToTime(DateUtils.formatDateTimeMin(flowDelegationSettings.getToTime()));
//        }
        }
        return flowDelegationSettingsDto;
    }

    /**
     * 根据用户ID返回相应的用户名
     *
     * @param userMap
     * @param userId
     * @return
     */
    private String resolveUsername(Map<String, String> userMap, String userId) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(userId)) {
            String[] userIds = userId.split(Separator.SEMICOLON.getValue());
            Iterator<String> it = Arrays.asList(userIds).iterator();
            while (it.hasNext()) {
                String key = it.next();
                if (userMap.containsKey(key)) {
//                    sb.append(userMap.get(key).getUserName());
                    sb.append(userMap.get(key));
                } else {
                    sb.append(key);
                }
                if (it.hasNext()) {
                    sb.append(Separator.SEMICOLON.getValue());
                }
            }
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#saveBean(com.wellsoft.pt.app.workflow.dto.FlowDelegationSettingsDto)
     */
    @Override
    @Transactional
    public ResultMessage saveBean(FlowDelegationSettingsDto flowDelegationSettingsDto) {
        FlowDelegationSettings delegationSettings = new FlowDelegationSettings();
        if (StringUtils.isBlank(flowDelegationSettingsDto.getUuid())) {
            flowDelegationSettingsDto.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
            flowDelegationSettingsDto.setUuid(null);
            flowDelegationSettingsDto.setSystem(RequestSystemContextPathResolver.system());
            flowDelegationSettingsDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        } else {
            delegationSettings = flowDelegationSettingsService.getOne(flowDelegationSettingsDto.getUuid());
            // 状态为【征求受托人意见】操作【激活】、【终止】，自动收回征求受托人意见的通知
            if (!IgnoreLoginUtils.isIgnoreLogin()) {
                if (FlowDelegationSettings.STATUS_CONSULT.equals(delegationSettings.getStatus())) {
                    if (FlowDelegationSettings.STATUS_ACTIVE.equals(flowDelegationSettingsDto.getStatus())
                            || FlowDelegationSettings.STATUS_DEACTIVE.equals(flowDelegationSettingsDto.getStatus())) {
                        String consultMessageId = flowDelegationSettingsDto.getConsultMessageId();
                        messageClientApiFacade.cancelMessage(consultMessageId);
                        flowDelegationSettingsDto.setConsultMessageId(null);
                    }
                }
            }
        }

        BeanUtils.copyProperties(flowDelegationSettingsDto, delegationSettings);
//        try {
//            if (StringUtils.isNotBlank(flowDelegationSettingsDto.getFormatedFromTime())) {
//                delegationSettings.setFromTime(DateUtils.parseDateTimeMin(flowDelegationSettingsDto
//                        .getFormatedFromTime()));
//            } else {
//                delegationSettings.setFromTime(null);
//            }
//            if (StringUtils.isNotBlank(flowDelegationSettingsDto.getFormatedToTime())) {
//                delegationSettings.setToTime(DateUtils.parseDateTimeMin(flowDelegationSettingsDto.getFormatedToTime()));
//            } else {
//                delegationSettings.setToTime(null);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//
//            throw new RuntimeException(e);
//        }

        // 时间的有效性判断
        if (!FlowDelegationSettings.STATUS_DEACTIVE.equals(flowDelegationSettingsDto.getStatus())) {
            if (delegationSettings.getToTime() != null && delegationSettings.getFromTime() != null) {
                // 结束时间不能小于开始时间
                if (delegationSettings.getToTime().before(delegationSettings.getFromTime())) {
                    throw new RuntimeException("结束时间不能小于开始时间!");
                }
            }
            if (delegationSettings.getToTime() != null) {
                // 结束时间不能小于当前时间
                if (delegationSettings.getToTime().before(Calendar.getInstance().getTime())) {
                    throw new RuntimeException("结束时间不能小于当前时间!");
                }
            }
        }

        flowDelegationSettingsService.save(delegationSettings);

        // 校验同样的委托内容，是否在同一时间内委托给一个人（即受托人、委托内容、开始时间和结束时间是否存在所有一样的记录），若有，弹窗提示“相同的委托内容，在同一时间只能委托给一个人！”；若无，该项记录生成，显示在列表中
        // （修改备注：校验相同委托内容增加 过滤掉 status状态为终止 的匹配）
        checkTheSameDelegationSettings(delegationSettings);

        // 激活状态时，当前工作委托
        if (FlowDelegationSettings.STATUS_ACTIVE.equals(flowDelegationSettingsDto.getStatus())) {
            FlowEngine.getInstance().getDelegationExecutor().delegationCurrentWork(delegationSettings);
        } else if (FlowDelegationSettings.STATUS_DEACTIVE.equals(flowDelegationSettingsDto.getStatus())) {
            // 手动终止时回收受拖人未处理的待办工作
            FlowEngine.getInstance().getDelegationExecutor().deactiveToTakeBack(delegationSettings);
        }

        // 征求受托人意见，发送消息
        Integer status = delegationSettings.getStatus();
        if (DutyAgent.STATUS_CONSULT.equals(status)) {
            consultTrustee(delegationSettings);
        }

        ResultMessage resultMessage = new ResultMessage("保存成功！");
        // 委托内容存在交集判断
        if (hasIntersectionContent(delegationSettings)) {
            resultMessage.clear();
            resultMessage.addMessage("您设置的委托与原有委托存在冲突，冲突部分按最新的设置处理！");
        }

        if (flowDelegationSettingsDto.getCommonUuid() != null) {
            commonDelegationSettingService.usedByUuid(flowDelegationSettingsDto.getCommonUuid());
        }

        return resultMessage;
    }

    /**
     * 如何描述该方法
     *
     * @param delegationSettings
     */
    private void checkTheSameDelegationSettings(FlowDelegationSettings delegationSettings) {
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return;
        }
        List<FlowDelegationSettings> flowDelegationSettingsList = flowDelegationSettingsService
                .getForCheckTheSameDelegationSettings(delegationSettings);
        Map<String, String> contentMap = new HashMap<String, String>();
        for (FlowDelegationSettings flowDelegationSettings : flowDelegationSettingsList) {
            if (!new Integer(0).equals(flowDelegationSettings.getStatus())) { // 过滤掉终止状态的委托设置
                String content = flowDelegationSettings.getContent();
                if (StringUtils.isBlank(content)) {
                    content = StringUtils.EMPTY;
                }
                if (contentMap.containsKey(content)) {
                    throw new BusinessException("相同的委托内容，在同一时间只能委托给一个人！");
                }
                contentMap.put(content, content);
            }
        }
    }

    /**
     * @param delegationSettings
     * @return
     */
    private boolean hasIntersectionContent(FlowDelegationSettings delegationSettings) {
        // 委托生效
        if (IgnoreLoginUtils.isIgnoreLogin()) {
            return false;
        }
        if (FlowDelegationSettings.STATUS_DEACTIVE.equals(delegationSettings.getStatus())) {
            return false;
        }
        List<String> userIds = new ArrayList<String>();
        userIds.add(delegationSettings.getConsignor());
        List<FlowDelegationSettings> delegationSettingsList = flowDelegationSettingsService.getByUserIdsAndSystem(userIds, delegationSettings.getSystem());
        String content = delegationSettings.getContent();
        // 包含所有流程，返回true
        if (StringUtils.isBlank(content) && delegationSettingsList.size() > 1) {
            return true;
        } else if (StringUtils.isBlank(content)) {
            return false;
        }
        Set<String> otherContentIds = new HashSet<String>();
        for (FlowDelegationSettings settings : delegationSettingsList) {
            if (StringUtils.equals(delegationSettings.getUuid(), settings.getUuid())) {
                continue;
            }
            String settingContent = settings.getContent();
            // 包含所有流程，返回true
            if (StringUtils.isBlank(settingContent)) {
                return true;
            }
            otherContentIds.addAll(Arrays.asList(StringUtils.split(settingContent, Separator.SEMICOLON.getValue())));
        }
        // 流程分类、ID判断
        Set<String> contentIds = new HashSet<String>();
        contentIds.addAll(Arrays.asList(StringUtils.split(content, Separator.SEMICOLON.getValue())));
        for (String contentId : contentIds) {
            if (otherContentIds.contains(contentId)) {
                return true;
            }
        }
        // 流程分类解析成流程ID后判断
        Set<String> otherFlowDefIds = getFlowDefIds(otherContentIds);
        Set<String> flowDefIds = getFlowDefIds(contentIds);
        flowDefIds.retainAll(otherFlowDefIds);
        return CollectionUtils.isNotEmpty(flowDefIds);
    }

    /**
     * @param contentIds
     * @return
     */
    private Set<String> getFlowDefIds(Set<String> contentIds) {
        Set<String> flowDefIds = new HashSet<String>();
        for (String contentId : contentIds) {
            if (StringUtils.isBlank(contentId)) {
                continue;
            }
            if (flowDefIds.contains(contentId)) {
                continue;
            }
            flowDefIds.add(contentId);
            String categoryPrefix = WorkFlowVariables.FLOW_CATEGORY_PREFIX.getName();
            if (contentId.startsWith(categoryPrefix)) {
                String categoryCode = contentId.substring(categoryPrefix.length());
                List<Permission> permissions = new ArrayList<Permission>();
                permissions.add(BasePermission.CREATE);
                FlowDefinitionQuery flowDefinitionQuery = FlowEngine.getInstance().createQuery(
                        FlowDefinitionQuery.class);
                flowDefinitionQuery.category(categoryCode);
                flowDefinitionQuery.permission(SpringSecurityUtils.getCurrentUserId(), permissions);
                List<FlowDefinitionQueryItem> flowDefinitionQueryItems = flowDefinitionQuery.list();
                for (FlowDefinitionQueryItem flowDefinitionQueryItem : flowDefinitionQueryItems) {
                    flowDefIds.add(flowDefinitionQueryItem.getId());
                }
            }
        }
        return flowDefIds;
    }

    /**
     * 征求受托人意见
     *
     * @param delegationSettings
     */
    private void consultTrustee(FlowDelegationSettings delegationSettings) {
        if (StringUtils.isBlank(delegationSettings.getTrustee())) {
            return;
        }

        List<String> trustees = Arrays.asList(StringUtils.split(delegationSettings.getTrustee(),
                Separator.SEMICOLON.getValue()));
        // 撤销已发送的消息
        String consultMessageId = delegationSettings.getConsultMessageId();
        if (StringUtils.isNotBlank(consultMessageId)) {
            messageClientApiFacade.cancelMessage(consultMessageId);
        }
        // 重新发送消息
        String messageId = UUID.randomUUID().toString();
        messageClientApiFacade.send(DUTY_AGENT_CONSULT, delegationSettings, trustees, messageId);
        delegationSettings.setConsultMessageId(messageId);
        flowDelegationSettingsService.save(delegationSettings);
    }

    /**
     * @param uuid
     * @return
     */
    @Override
    public List<TreeNode> getContentAsTreeAsync(String uuid) {
        return flowSchemeService.getAllFlowAsCategoryTree();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#active(java.util.Collection)
     */
    @Override
    @Transactional
    public void active(Collection<String> uuids) {
        for (String uuid : uuids) {
            FlowDelegationSettingsDto flowDelegationSettingsDto = getBean(uuid);
            flowDelegationSettingsDto.setStatus(FlowDelegationSettings.STATUS_ACTIVE);
            saveBean(flowDelegationSettingsDto);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#deactive(java.util.Collection)
     */
    @Override
    @Transactional
    public void deactive(Collection<String> uuids) {
        for (String uuid : uuids) {
            flowDelegationSettingsService.deactive(uuid);
//			FlowDelegationSettingsDto flowDelegationSettingsDto = getBean(uuid);
//			flowDelegationSettingsDto.setStatus(FlowDelegationSettings.STATUS_DEACTIVE);
//			saveBean(flowDelegationSettingsDto);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#deleteAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void deleteAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            Long count = taskDelegationService.countByDelegationSettingsUuid(uuid);
            if (count > 0) {
                throw new BusinessException("委托设置下存在委托数据，不能删除！");
            }
            FlowDelegationSettings delegationSettings = flowDelegationSettingsService.getOne(uuid);
            FlowEngine.getInstance().getDelegationExecutor().deactiveToTakeBack(delegationSettings);
        }
        flowDelegationSettingsService.deleteByUuids(Arrays.asList(uuids.toArray(new String[0])));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#delegationActive(java.lang.String)
     */
    @Override
    @Transactional
    public void delegationActive(String uuid) {
        FlowDelegationSettingsDto flowDelegationSettingsDto = getBean(uuid);
        flowDelegationSettingsDto.setStatus(DutyAgent.STATUS_ACTIVE);
        if (BooleanUtils.isTrue(flowDelegationSettingsDto.getIncludeCurrentWork())) {
            String system = RequestSystemContextPathResolver.system();
            scheduledExecutorService.execute(() -> {
                try {
                    RequestSystemContextPathResolver.setSystem(system);
                    IgnoreLoginUtils.login(SpringSecurityUtils.getCurrentTenantId(), flowDelegationSettingsDto.getConsignor());
                    saveBean(flowDelegationSettingsDto);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    IgnoreLoginUtils.logout();
                    RequestSystemContextPathResolver.clear();
                }
            });
        } else {
            try {
                IgnoreLoginUtils.login(SpringSecurityUtils.getCurrentTenantId(), flowDelegationSettingsDto.getConsignor());
                saveBean(flowDelegationSettingsDto);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                IgnoreLoginUtils.logout();
            }
        }

        // 受托人受托生效，发送消息
        List<String> consignor = Arrays.asList(StringUtils.split(flowDelegationSettingsDto.getConsignor(),
                Separator.SEMICOLON.getValue()));
        messageClientApiFacade.send(DUTY_AGENT_ACTIVE, flowDelegationSettingsDto, consignor);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.workflow.facade.service.WorkflowDelegationSettiongsService#delegationRefuse(java.lang.String)
     */
    @Override
    @Transactional
    public void delegationRefuse(String uuid) {
        FlowDelegationSettings delegationSettings = flowDelegationSettingsService.getOne(uuid);
        delegationSettings.setStatus(FlowDelegationSettings.STATUS_REFUSE);
        flowDelegationSettingsService.save(delegationSettings);

        // 受托人委托终止，发送消息
        List<String> consignor = Arrays.asList(StringUtils.split(delegationSettings.getConsignor(),
                Separator.SEMICOLON.getValue()));
        messageClientApiFacade.send(DUTY_AGENT_DEACTIVE, delegationSettings, consignor);
    }

    /**
     * @param commonDelegationSettingDto
     * @return
     */
    @Override
    public Long saveCommonBean(WfCommonDelegationSettingDto commonDelegationSettingDto) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        WfCommonDelegationSettingEntity entity = new WfCommonDelegationSettingEntity();
        BeanUtils.copyProperties(commonDelegationSettingDto, entity);
        entity.setUserId(userDetails.getUserId());
        entity.setTenant(userDetails.getTenantId());
        entity.setSystem(RequestSystemContextPathResolver.system());
        commonDelegationSettingService.save(entity);
        return entity.getUuid();
    }

    /**
     * @param userId
     * @param pagingInfo
     * @return
     */
    @Override
    public List<WfCommonDelegationSettingDto> listCommonByUserId(String userId, PagingInfo pagingInfo) {
        WfCommonDelegationSettingEntity entity = new WfCommonDelegationSettingEntity();
        entity.setUserId(userId);
        entity.setSystem(RequestSystemContextPathResolver.system());
        entity.setTenant(SpringSecurityUtils.getCurrentTenantId());
        List<WfCommonDelegationSettingEntity> entities = commonDelegationSettingService.listAllByPage(entity, pagingInfo, "usedCount desc, createTime desc");
        return BeanUtils.copyCollection(entities, WfCommonDelegationSettingDto.class);
    }

    @Override
    public void deleteCommon(Long uuid) {
        commonDelegationSettingService.delete(uuid);
    }

}
