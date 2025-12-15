/*
 * @(#)5/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.dao.WeixinWorkRecordDao;
import com.wellsoft.pt.app.weixin.entity.WeixinWorkRecordEntity;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.service.WeixinUserService;
import com.wellsoft.pt.app.weixin.service.WeixinWorkRecordService;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
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
 * 5/26/25.1	    zhulh		5/26/25		    Create
 * </pre>
 * @date 5/26/25
 */
@Service
public class WeixinWorkRecordServiceImpl extends AbstractJpaServiceImpl<WeixinWorkRecordEntity, WeixinWorkRecordDao, Long> implements WeixinWorkRecordService {

    @Autowired
    private WeixinConfigFacadeService weixinConfigFacadeService;

    @Autowired
    private WeixinUserService weixinUserService;

    @Autowired
    private FlowService flowService;

    @Override
    public List<WeixinWorkRecordEntity> listByTaskInstUuidAndTypeAndState(String taskInstUuid, WeixinWorkRecordEntity.Type type, WeixinWorkRecordEntity.State state) {
        WeixinWorkRecordEntity entity = new WeixinWorkRecordEntity();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setType(type);
        entity.setState(state);
        return this.listByEntity(entity);
    }

    @Override
    public List<WeixinWorkRecordEntity> listGroupChatByFlowInstUuidAndTypeAndState(String flowInstUuid, WeixinWorkRecordEntity.Type type, WeixinWorkRecordEntity.State state) {
        WeixinWorkRecordEntity entity = new WeixinWorkRecordEntity();
        entity.setFlowInstUuid(flowInstUuid);
        entity.setType(type);
        entity.setState(state);
        entity.setGroupChat(true);
        return this.listByEntity(entity);
    }

    @Override
    public ProviderInfo getProviderInfo() {
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(RequestSystemContextPathResolver.system());
        ProviderInfo providerInfo = new ProviderInfo();
        providerInfo.setName("企业微信");
        providerInfo.setId("weixin");
        providerInfo.setDescription("提供企业微信群聊服务");
        providerInfo.setEnabled(BooleanUtils.isTrue(weixinConfigVo.getEnabled()));
        return providerInfo;
    }

    @Override
    @Transactional
    public String startGroupChat(StartGroupChat startGroupChat) {
        String system = RequestSystemContextPathResolver.system();
        String tenant = SpringSecurityUtils.getCurrentTenantId();
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(system);

        List<String> memberIds = Lists.newArrayList(Arrays.stream(StringUtils.split(startGroupChat.getMemberIds(), Separator.SEMICOLON.getValue())).iterator());
        if (!memberIds.contains(startGroupChat.getOwnerId())) {
            memberIds.add(startGroupChat.getOwnerId());
        }

        Map<String, String> userIdMap = weixinUserService.listUserIdMapByOaUserIds(memberIds, weixinConfigVo.getCorpId());

        String url = WeixinApiUtils.getPcUrl(startGroupChat.getTaskInstUuid(), startGroupChat.getFlowInstUuid(),
                system, weixinConfigVo.getCorpDomainUri());

        String ownerId = null;
        for (Map.Entry<String, String> entry : userIdMap.entrySet()) {
            if (StringUtils.equals(startGroupChat.getOwnerId(), entry.getValue())) {
                ownerId = entry.getKey();
                break;
            }
        }

        Map<String, String> content = Maps.newHashMap();
        content.put("groupName", startGroupChat.getGroupName());

        WeixinWorkRecordEntity recordEntity = new WeixinWorkRecordEntity();
        recordEntity.setConfigUuid(weixinConfigVo.getUuid());
        recordEntity.setCorpId(weixinConfigVo.getCorpId());
        recordEntity.setAppId(weixinConfigVo.getAppId());
        recordEntity.setTitle(startGroupChat.getTitle());
        recordEntity.setFlowInstUuid(startGroupChat.getFlowInstUuid());
        recordEntity.setTaskInstUuid(startGroupChat.getTaskInstUuid());
        recordEntity.setUrl(url);
        recordEntity.setContent(JsonUtils.object2Json(content));
        recordEntity.setOaUserId(StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
        recordEntity.setUserId(StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));
        recordEntity.setOwnerId(ownerId);
        recordEntity.setGroupChat(true);
        recordEntity.setType(WeixinWorkRecordEntity.Type.USER);
        recordEntity.setState(WeixinWorkRecordEntity.State.ToSend);
        recordEntity.setSystem(system);
        recordEntity.setTenant(tenant);

        WeixinApiUtils.createChat(recordEntity, weixinConfigVo);

        if (StringUtils.isNotBlank(recordEntity.getErrMsg())) {
            throw new BusinessException(recordEntity.getErrMsg());
        }
        this.save(recordEntity);

        flowService.addRuntimeFlowAccessPermissionProvider(startGroupChat.getFlowInstUuid(), "weixinFlowAccessPermissionProvider");

        WeixinApiUtils.sendCreateChatMessage("发起群聊通知", recordEntity, weixinConfigVo);
        return recordEntity.getChatId();
    }

}
