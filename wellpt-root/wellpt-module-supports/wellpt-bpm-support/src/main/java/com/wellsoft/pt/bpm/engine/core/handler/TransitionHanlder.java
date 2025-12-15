/*
 * @(#)2013-10-31 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Transition;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-31.1	zhulh		2013-10-31		Create
 * </pre>
 * @date 2013-10-31
 */
public interface TransitionHanlder extends Handler {

    void execute(Transition transition, ExecutionContext executionContext);
}
