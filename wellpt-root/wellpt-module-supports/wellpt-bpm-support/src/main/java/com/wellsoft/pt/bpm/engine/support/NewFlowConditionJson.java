/*
 * @(#)2019年2月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年2月28日.1	zhulh		2019年2月28日		Create
 * </pre>
 * @date 2019年2月28日
 */
public class NewFlowConditionJson extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1457191742757224524L;

    private List<NewFlowConditionConfig> conditionConfigs = Lists.newArrayListWithCapacity(0);

    /**
     * @return the conditionConfigs
     */
    public List<NewFlowConditionConfig> getConditionConfigs() {
        return conditionConfigs;
    }

    /**
     * @param conditionConfigs 要设置的conditionConfigs
     */
    public void setConditionConfigs(List<NewFlowConditionConfig> conditionConfigs) {
        this.conditionConfigs = conditionConfigs;
    }

    /**
     * @return
     */
    public String getCondition() {
        StringBuilder sb = new StringBuilder();
        for (NewFlowConditionConfig conditionConfig : conditionConfigs) {
            sb.append(conditionConfig.getCondition());
        }
        return sb.toString();
    }

}
