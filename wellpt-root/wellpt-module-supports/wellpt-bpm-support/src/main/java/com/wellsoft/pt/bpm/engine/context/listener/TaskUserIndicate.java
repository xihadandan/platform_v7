/*
 * @(#)2015年8月31日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener;

import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.Set;

/**
 * Description: 环节可选的办理人接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月31日.1	zhulh		2015年8月31日		Create
 * </pre>
 * @date 2015年8月31日
 */
public interface TaskUserIndicate extends Listener {

    /**
     * 选择对应环节可选的办理人ID集合
     *
     * @param node
     * @param taskData
     * @return
     */
    Set<String> getCandidateUsers(Node node, TaskData taskData);

}
