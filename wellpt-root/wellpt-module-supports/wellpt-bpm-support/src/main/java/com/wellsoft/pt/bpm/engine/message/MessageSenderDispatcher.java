/*
 * @(#)2014-9-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.message;

import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-3.1	zhulh		2014-9-3		Create
 * </pre>
 * @date 2014-9-3
 */
public interface MessageSenderDispatcher {

    void onSend(FlowInstance flowInstance, TaskInstance taskInstance, TaskData taskData,
                WorkFlowMessageTemplate templateId, String userId, ParticipantType participantType);

}
