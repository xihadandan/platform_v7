/*
 * @(#)2014-2-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dispatcher;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Transition;

/**
 * Description: 分支流分发接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年3月1日.1	zhulh		2019年3月1日		Create
 * </pre>
 * @date 2019年3月1日
 */
public interface DispatcherBranchTaskResolver {

    /**
     * @param transition
     * @param direction
     * @param executionContext
     */
    void resolve(Transition transition, Direction direction, ExecutionContext executionContext);

}
