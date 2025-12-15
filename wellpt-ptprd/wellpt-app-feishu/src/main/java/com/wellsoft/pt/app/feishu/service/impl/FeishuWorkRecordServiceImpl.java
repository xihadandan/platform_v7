/*
 * @(#)3/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.lark.oapi.service.im.v1.model.ChatMemberUser;
import com.lark.oapi.service.im.v1.model.P2ChatMemberUserAddedV1Data;
import com.lark.oapi.service.im.v1.model.P2ChatMemberUserDeletedV1Data;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.feishu.dao.FeishuWorkRecordDao;
import com.wellsoft.pt.app.feishu.entity.FeishuWorkRecordEntity;
import com.wellsoft.pt.app.feishu.service.FeishuConfigService;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.app.feishu.service.FeishuWorkRecordService;
import com.wellsoft.pt.app.feishu.support.FeishuEventHoler;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.groupchat.StartGroupChat;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/21/25.1	    zhulh		3/21/25		    Create
 * </pre>
 * @date 3/21/25
 */
@Service
public class FeishuWorkRecordServiceImpl extends AbstractJpaServiceImpl<FeishuWorkRecordEntity, FeishuWorkRecordDao, Long> implements FeishuWorkRecordService {

    @Autowired
    private FeishuConfigService feishuConfigService;

    @Autowired
    private FeishuUserService feishuUserService;

    @Autowired
    private FlowService flowService;

    @Override
    public List<FeishuWorkRecordEntity> listByTaskInstUuidAndTypeAndState(String taskInstUuid, FeishuWorkRecordEntity.Type type, FeishuWorkRecordEntity.State state) {
        FeishuWorkRecordEntity entity = new FeishuWorkRecordEntity();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setType(type);
        entity.setState(state);
        return this.listByEntity(entity);
    }

    @Override
    public List<FeishuWorkRecordEntity> listGroupChatByFlowInstUuidAndTypeAndState(String flowInstUuid, FeishuWorkRecordEntity.Type type,
                                                                                   FeishuWorkRecordEntity.State state) {
        FeishuWorkRecordEntity entity = new FeishuWorkRecordEntity();
        entity.setFlowInstUuid(flowInstUuid);
        entity.setType(type);
        entity.setState(state);
        entity.setGroupChat(true);
        return this.listByEntity(entity);
    }

    @Override
    public FeishuWorkRecordEntity getGroupChatByChatId(String chatId) {
        FeishuWorkRecordEntity entity = new FeishuWorkRecordEntity();
        entity.setChatId(chatId);
        entity.setGroupChat(true);
        List<FeishuWorkRecordEntity> list = this.listByEntity(entity);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    @Transactional
    public void logicDeleteGroupChatByChatId(String chatId) {
        FeishuWorkRecordEntity entity = new FeishuWorkRecordEntity();
        entity.setChatId(chatId);
        entity.setGroupChat(true);
        List<FeishuWorkRecordEntity> list = this.listByEntity(entity);
        list.forEach(chatEntity -> chatEntity.setState(FeishuWorkRecordEntity.State.Deleted));
        this.dao.saveAll(list);
    }

    @Override
    @Transactional
    public void addGroupChatMembers(P2ChatMemberUserAddedV1Data p2ChatMemberUserAddedV1Data) {
        FeishuWorkRecordEntity workRecordEntity = getGroupChatByChatId(p2ChatMemberUserAddedV1Data.getChatId());
        if (workRecordEntity == null) {
            FeishuEventHoler.error("未找到群聊记录");
            return;
        }
        if (!FeishuWorkRecordEntity.Type.USER.equals(workRecordEntity.getType())) {
            FeishuEventHoler.error("不是用户发起的群聊记录");
            return;
        }

        ChatMemberUser[] chatMemberUsers = p2ChatMemberUserAddedV1Data.getUsers();
        Set<String> openIdSet = Arrays.stream(chatMemberUsers).map(chatMemberUser -> chatMemberUser.getUserId().getOpenId()).collect(Collectors.toSet());
        Map<String, String> oaUserIdMap = feishuUserService.listOaUserIdMapByOpenUserId(openIdSet, workRecordEntity.getAppId());
        List<String> oaUserIds = Lists.newArrayList(StringUtils.split(workRecordEntity.getOaUserId(), Separator.SEMICOLON.getValue()));
        List<String> openIds = Lists.newArrayList(StringUtils.split(workRecordEntity.getOpenId(), Separator.SEMICOLON.getValue()));
        oaUserIds.addAll(oaUserIdMap.keySet());
        openIds.addAll(oaUserIdMap.values());
        workRecordEntity.setOaUserId(StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
        workRecordEntity.setOpenId(StringUtils.join(openIds, Separator.SEMICOLON.getValue()));
        this.dao.save(workRecordEntity);
    }

    @Override
    @Transactional
    public void deleteGroupChatMembers(P2ChatMemberUserDeletedV1Data p2ChatMemberUserDeletedV1Data) {
        FeishuWorkRecordEntity workRecordEntity = getGroupChatByChatId(p2ChatMemberUserDeletedV1Data.getChatId());
        if (workRecordEntity == null) {
            FeishuEventHoler.error("未找到群聊记录");
            return;
        }
        if (!FeishuWorkRecordEntity.Type.USER.equals(workRecordEntity.getType())) {
            FeishuEventHoler.error("不是用户发起的群聊记录");
            return;
        }

        ChatMemberUser[] chatMemberUsers = p2ChatMemberUserDeletedV1Data.getUsers();
        Set<String> openIdSet = Arrays.stream(chatMemberUsers).map(chatMemberUser -> chatMemberUser.getUserId().getOpenId()).collect(Collectors.toSet());
        Map<String, String> oaUserIdMap = feishuUserService.listOaUserIdMapByOpenUserId(openIdSet, workRecordEntity.getAppId());
        List<String> oaUserIds = Lists.newArrayList(StringUtils.split(workRecordEntity.getOaUserId(), Separator.SEMICOLON.getValue()));
        List<String> openIds = Lists.newArrayList(StringUtils.split(workRecordEntity.getOpenId(), Separator.SEMICOLON.getValue()));
        oaUserIds.removeAll(oaUserIdMap.keySet());
        openIds.removeAll(oaUserIdMap.values());
        workRecordEntity.setOaUserId(StringUtils.join(oaUserIds, Separator.SEMICOLON.getValue()));
        workRecordEntity.setOpenId(StringUtils.join(openIds, Separator.SEMICOLON.getValue()));
        this.dao.save(workRecordEntity);
    }

    @Override
    public ProviderInfo getProviderInfo() {
        FeishuConfigVo feishuConfigVo = feishuConfigService.getBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setName("飞书");
        providerInfo.setId("feishu");
        providerInfo.setDescription("提供飞书群聊服务");
        providerInfo.setEnabled(BooleanUtils.isTrue(feishuConfigVo.getEnabled()));
        return providerInfo;
    }

    @Override
    @Transactional
    public String startGroupChat(StartGroupChat startGroupChat) {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        FeishuConfigVo feishuConfigVo = feishuConfigService.getBySystemAndTenant(system, tenant);

        Set<String> memberIds = Sets.newHashSet(StringUtils.split(startGroupChat.getMemberIds(), Separator.SEMICOLON.getValue()));
        memberIds.add(startGroupChat.getOwnerId());

        Map<String, String> openIdMap = feishuUserService.listOpenIdMapByOaUserId(memberIds, feishuConfigVo.getAppId());

        String url = FeishuApiUtils.getPcUrl(startGroupChat.getTaskInstUuid(), startGroupChat.getFlowInstUuid(),
                system, feishuConfigVo.getRedirectUri());

        String openOwnerId = null;
        for (Map.Entry<String, String> entry : openIdMap.entrySet()) {
            if (StringUtils.equals(startGroupChat.getOwnerId(), entry.getValue())) {
                openOwnerId = entry.getKey();
                break;
            }
        }

        Map<String, String> content = Maps.newHashMap();
        content.put("groupName", startGroupChat.getGroupName());

        FeishuWorkRecordEntity recordEntity = new FeishuWorkRecordEntity();
        recordEntity.setConfigUuid(feishuConfigVo.getUuid());
        recordEntity.setAppId(feishuConfigVo.getAppId());
        recordEntity.setTitle(startGroupChat.getTitle());
        recordEntity.setFlowInstUuid(startGroupChat.getFlowInstUuid());
        recordEntity.setTaskInstUuid(startGroupChat.getTaskInstUuid());
        recordEntity.setUrl(url);
        recordEntity.setContent(JsonUtils.object2Json(content));
        recordEntity.setOaUserId(StringUtils.join(openIdMap.values(), Separator.SEMICOLON.getValue()));
        recordEntity.setOpenId(StringUtils.join(openIdMap.keySet(), Separator.SEMICOLON.getValue()));
        recordEntity.setOpenOwnerId(openOwnerId);
        recordEntity.setGroupChat(true);
        recordEntity.setType(FeishuWorkRecordEntity.Type.USER);
        recordEntity.setState(FeishuWorkRecordEntity.State.ToSend);
        recordEntity.setSystem(system);
        recordEntity.setTenant(tenant);

        FeishuApiUtils.createChat(recordEntity, feishuConfigVo);

        if (StringUtils.isNotBlank(recordEntity.getErrMsg())) {
            throw new BusinessException(recordEntity.getErrMsg());
        }
        this.save(recordEntity);

        flowService.addRuntimeFlowAccessPermissionProvider(startGroupChat.getFlowInstUuid(), "feishuFlowAccessPermissionProvider");

        FeishuApiUtils.sendCreateChatMessage("发起群聊通知", recordEntity, feishuConfigVo);
        return recordEntity.getChatId();
    }

}
