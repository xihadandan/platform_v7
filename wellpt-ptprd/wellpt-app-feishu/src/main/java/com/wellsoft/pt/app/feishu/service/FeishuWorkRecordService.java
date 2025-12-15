/*
 * @(#)3/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.service;

import com.lark.oapi.service.im.v1.model.P2ChatMemberUserAddedV1Data;
import com.lark.oapi.service.im.v1.model.P2ChatMemberUserDeletedV1Data;
import com.wellsoft.pt.app.feishu.dao.FeishuWorkRecordDao;
import com.wellsoft.pt.app.feishu.entity.FeishuWorkRecordEntity;
import com.wellsoft.pt.bpm.engine.support.groupchat.FlowGroupChatProvider;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

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
public interface FeishuWorkRecordService extends JpaService<FeishuWorkRecordEntity, FeishuWorkRecordDao, Long>, FlowGroupChatProvider {

    /**
     * @param taskInstUuid
     * @param state
     * @return
     */
    List<FeishuWorkRecordEntity> listByTaskInstUuidAndTypeAndState(String taskInstUuid, FeishuWorkRecordEntity.Type type, FeishuWorkRecordEntity.State state);

    /**
     * @param flowInstUuid
     * @param state
     * @return
     */
    List<FeishuWorkRecordEntity> listGroupChatByFlowInstUuidAndTypeAndState(String flowInstUuid, FeishuWorkRecordEntity.Type type, FeishuWorkRecordEntity.State state);

    /**
     * @param chatId
     * @return
     */
    FeishuWorkRecordEntity getGroupChatByChatId(String chatId);

    void logicDeleteGroupChatByChatId(String chatId);

    void addGroupChatMembers(P2ChatMemberUserAddedV1Data p2ChatMemberUserAddedV1Data);

    void deleteGroupChatMembers(P2ChatMemberUserDeletedV1Data p2ChatMemberUserDeletedV1Data);
}
