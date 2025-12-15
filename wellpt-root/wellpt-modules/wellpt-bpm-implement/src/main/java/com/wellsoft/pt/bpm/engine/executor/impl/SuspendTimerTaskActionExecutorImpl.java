/*
 * @(#)2014-10-9 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.impl;

import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.executor.SuspendTimerTaskActionExecutor;
import com.wellsoft.pt.bpm.engine.executor.TaskActionExecutor;
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
 * 2014-10-9.1	zhulh		2014-10-9		Create
 * </pre>
 * @date 2014-10-9
 */
@Service
@Transactional
public class SuspendTimerTaskActionExecutorImpl extends TaskActionExecutor implements SuspendTimerTaskActionExecutor {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.executor.TaskExecutor#execute(com.wellsoft.pt.bpm.engine.executor.Param)
     */
    @Override
    public void execute(Param param) {
        // TODO Auto-generated method stub

    }

}
