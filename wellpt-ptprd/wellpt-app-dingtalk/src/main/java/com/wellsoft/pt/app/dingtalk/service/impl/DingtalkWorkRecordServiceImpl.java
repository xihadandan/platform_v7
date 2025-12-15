/*
 * @(#)4/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.dao.DingtalkWorkRecordDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkUserService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkWorkRecordService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.support.groupchat.StartGroupChat;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/24/25.1	    zhulh		4/24/25		    Create
 * </pre>
 * @date 4/24/25
 */
@Service
public class DingtalkWorkRecordServiceImpl extends AbstractJpaServiceImpl<DingtalkWorkRecordEntity, DingtalkWorkRecordDao, Long> implements
        DingtalkWorkRecordService {

    @Autowired
    private DingtalkConfigFacadeService dingtalkConfigFacadeService;

    @Autowired
    private DingtalkUserService dingtalkUserService;

    @Autowired
    private FlowService flowService;

    @Override
    public List<DingtalkWorkRecordEntity> listByTaskInstUuidAndTypeAndState(String taskInstUuid, DingtalkWorkRecordEntity.Type type, DingtalkWorkRecordEntity.State state) {
        DingtalkWorkRecordEntity entity = new DingtalkWorkRecordEntity();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setType(type);
        entity.setState(state);
        return this.listByEntity(entity);
    }

    @Override
    public List<DingtalkWorkRecordEntity> listGroupChatByFlowInstUuidAndTypeAndState(String flowInstUuid, DingtalkWorkRecordEntity.Type type, DingtalkWorkRecordEntity.State state) {
        DingtalkWorkRecordEntity entity = new DingtalkWorkRecordEntity();
        entity.setFlowInstUuid(flowInstUuid);
        entity.setType(type);
        entity.setState(state);
        entity.setGroupChat(true);
        return this.listByEntity(entity);
    }

    @Override
    public ProviderInfo getProviderInfo() {
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(RequestSystemContextPathResolver.system(), SpringSecurityUtils.getCurrentTenantId());
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setName("钉钉");
        providerInfo.setId("dingtalk");
        providerInfo.setDescription("提供钉钉群聊服务");
        providerInfo.setEnabled(BooleanUtils.isTrue(dingtalkConfigVo.getEnabled()));
        return providerInfo;
    }

    @Override
    @Transactional
    public String startGroupChat(StartGroupChat startGroupChat) {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(system, tenant);

        List<String> memberIds = Lists.newArrayList(Arrays.stream(StringUtils.split(startGroupChat.getMemberIds(), Separator.SEMICOLON.getValue())).iterator());
        if (!memberIds.contains(startGroupChat.getOwnerId())) {
            memberIds.add(startGroupChat.getOwnerId());
        }

        Map<String, String> userIdMap = dingtalkUserService.listUserIdMapByOaUserIds(memberIds, dingtalkConfigVo.getAppId());

        String url = DingtalkApiV2Utils.getPcUrl(startGroupChat.getTaskInstUuid(), startGroupChat.getFlowInstUuid(),
                system, dingtalkConfigVo.getCorpDomainUri());

        String ownerId = null;
        for (Map.Entry<String, String> entry : userIdMap.entrySet()) {
            if (StringUtils.equals(startGroupChat.getOwnerId(), entry.getValue())) {
                ownerId = entry.getKey();
                break;
            }
        }

        Map<String, String> content = Maps.newHashMap();
        content.put("groupName", startGroupChat.getGroupName());

        DingtalkWorkRecordEntity recordEntity = new DingtalkWorkRecordEntity();
        recordEntity.setConfigUuid(dingtalkConfigVo.getUuid());
        recordEntity.setAppId(dingtalkConfigVo.getAppId());
        recordEntity.setTitle(startGroupChat.getTitle());
        recordEntity.setFlowInstUuid(startGroupChat.getFlowInstUuid());
        recordEntity.setTaskInstUuid(startGroupChat.getTaskInstUuid());
        recordEntity.setUrl(url);
        recordEntity.setContent(JsonUtils.object2Json(content));
        recordEntity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        recordEntity.setUserId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));
        recordEntity.setOwnerId(ownerId);
        recordEntity.setGroupChat(true);
        recordEntity.setType(DingtalkWorkRecordEntity.Type.USER);
        recordEntity.setState(DingtalkWorkRecordEntity.State.ToSend);
        recordEntity.setSystem(system);
        recordEntity.setTenant(tenant);

        DingtalkApiV2Utils.createChat(recordEntity, dingtalkConfigVo);

        if (StringUtils.isNotBlank(recordEntity.getErrMsg())) {
            throw new BusinessException(recordEntity.getErrMsg());
        }
        this.save(recordEntity);

        flowService.addRuntimeFlowAccessPermissionProvider(startGroupChat.getFlowInstUuid(), "dingtalkFlowAccessPermissionProvider");

        DingtalkApiV2Utils.sendCreateChatMessage("发起群聊通知", recordEntity, dingtalkConfigVo);
        return recordEntity.getChatId();
    }

}
