/*
 * @(#)2014-11-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.transfer;

import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.TaskTransferExecutor;
import com.wellsoft.pt.bpm.engine.executor.param.CancelTransferParam;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-2.1	zhulh		2014-11-2		Create
 * </pre>
 * @date 2014-11-2
 */
@Service
@Transactional
public class CancelTaskTransferExecutorImpl extends TaskTransferExecutor implements CancelTaskTransferExecutor {

    @Autowired
    private TaskService taskService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskTransferExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        CancelTransferParam transferParam = (CancelTransferParam) param;
        TaskData taskData = param.getTaskData();

        TaskInstance sourceTaskInstance = taskService.getTask(transferParam.getSourceTaskInstUuid());
        // 正常提交
        Token token = new Token(sourceTaskInstance, taskData);
        // token.signal();
    }

}
