/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.node.Node;

/**
 * Description: 结点处理接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-27.1	zhulh		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
public interface Handler {
    public void enter(Node node, ExecutionContext executionContext);

    public void execute(Node node, ExecutionContext executionContext);

    public void afterExecuted(Node node, ExecutionContext executionContext);

    public boolean complete(Node node, ExecutionContext executionContext);

    public void leave(Node node, ExecutionContext executionContext);
}
