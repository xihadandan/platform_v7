/*
 * @(#)2014-4-25 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-25.1	zhulh		2014-4-25		Create
 * </pre>
 * @date 2014-4-25
 */
public class FlowDelegateUtils {
    /**
     * @param flowDefinition
     * @return
     */
    public static FlowDelegate getFlowDelegate(FlowDefinition flowDefinition) {
        return new FlowDelegate(flowDefinition);
    }

    /**
     * @param flowDefUuid
     * @return
     */
    public static FlowDelegate getFlowDelegate(String flowDefUuid) {
        return new FlowDelegate(flowDefUuid);
    }
}
