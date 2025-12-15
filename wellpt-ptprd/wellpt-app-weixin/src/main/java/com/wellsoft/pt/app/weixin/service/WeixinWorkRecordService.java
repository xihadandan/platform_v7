/*
 * @(#)5/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.service;

import com.wellsoft.pt.app.weixin.dao.WeixinWorkRecordDao;
import com.wellsoft.pt.app.weixin.entity.WeixinWorkRecordEntity;
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
 * 5/26/25.1	    zhulh		5/26/25		    Create
 * </pre>
 * @date 5/26/25
 */
public interface WeixinWorkRecordService extends JpaService<WeixinWorkRecordEntity, WeixinWorkRecordDao, Long>, FlowGroupChatProvider {
    List<WeixinWorkRecordEntity> listByTaskInstUuidAndTypeAndState(String taskInstUuid, WeixinWorkRecordEntity.Type type, WeixinWorkRecordEntity.State state);

    List<WeixinWorkRecordEntity> listGroupChatByFlowInstUuidAndTypeAndState(String flowInstUuid, WeixinWorkRecordEntity.Type type, WeixinWorkRecordEntity.State state);
}
