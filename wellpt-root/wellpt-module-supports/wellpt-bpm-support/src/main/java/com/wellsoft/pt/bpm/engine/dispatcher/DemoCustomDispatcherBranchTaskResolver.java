/*
 * @(#)2019年3月1日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dispatcher;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Transition;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
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
@Component
public class DemoCustomDispatcherBranchTaskResolver implements CustomDispatcherBranchTaskResolver {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.dispatcher.CustomDispatcherBranchTaskResolver#getName()
     */
    @Override
    public String getName() {
        return "测试自定义分支流接口";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.dispatcher.DispatcherBranchTaskResolver#resolve(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Direction, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void resolve(Transition transition, Direction direction, ExecutionContext executionContext) {
        throw new RuntimeException("测试自定义分支流接口暂未实现！");
    }

}
