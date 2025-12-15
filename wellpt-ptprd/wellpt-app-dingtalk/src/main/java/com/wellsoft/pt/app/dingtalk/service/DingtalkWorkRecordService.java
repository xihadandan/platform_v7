/*
 * @(#)4/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.service;

import com.wellsoft.pt.app.dingtalk.dao.DingtalkWorkRecordDao;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
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
 * 4/24/25.1	    zhulh		4/24/25		    Create
 * </pre>
 * @date 4/24/25
 */
public interface DingtalkWorkRecordService extends JpaService<DingtalkWorkRecordEntity, DingtalkWorkRecordDao, Long>, FlowGroupChatProvider {

    List<DingtalkWorkRecordEntity> listByTaskInstUuidAndTypeAndState(String taskInstUuid, DingtalkWorkRecordEntity.Type type, DingtalkWorkRecordEntity.State state);

    List<DingtalkWorkRecordEntity> listGroupChatByFlowInstUuidAndTypeAndState(String flowInstUuid, DingtalkWorkRecordEntity.Type type, DingtalkWorkRecordEntity.State state);
}
