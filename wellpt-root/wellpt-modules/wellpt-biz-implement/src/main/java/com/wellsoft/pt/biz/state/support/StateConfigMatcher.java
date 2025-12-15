/*
 * @(#)12/7/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.support;

import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/7/23.1	zhulh		12/7/23		Create
 * </pre>
 * @date 12/7/23
 */
@FunctionalInterface
public interface StateConfigMatcher {

    /**
     * 业务状态配置是否匹配
     *
     * @param config
     * @return
     */
    boolean match(StateDefinition.StateConfig config);

}
