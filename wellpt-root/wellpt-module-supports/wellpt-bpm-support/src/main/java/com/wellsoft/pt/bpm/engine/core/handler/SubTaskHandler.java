/*
 * @(#)2012-11-27 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core.handler;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
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
 * 2012-11-27.1	zhulh		2012-11-27		Create
 * </pre>
 * @date 2012-11-27
 */
@Service
@Transactional
public interface SubTaskHandler extends Handler {

    void checkSubFlowAllowSubmit(Node node, ExecutionContext executionContext);

    void updateSubFlowRelationStatus(Node node, ExecutionContext executionContext);

    void initSubTaskRelations(ExecutionContext executionContext, SubTaskNode subTaskNode);

}
