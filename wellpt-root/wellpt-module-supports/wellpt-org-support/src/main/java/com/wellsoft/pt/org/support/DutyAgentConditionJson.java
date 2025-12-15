/*
 * @(#)2015-4-1 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.support;

import java.util.List;

/**
 * Description: 条件表达式
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-1.1	zhulh		2015-4-1		Create
 * </pre>
 * @date 2015-4-1
 */
public class DutyAgentConditionJson {

    List<DutyAgentConditionConfig> conditionConfigs;

    /**
     * @return the conditionConfigs
     */
    public List<DutyAgentConditionConfig> getConditionConfigs() {
        return conditionConfigs;
    }

    /**
     * @param conditionConfigs 要设置的conditionConfigs
     */
    public void setConditionConfigs(List<DutyAgentConditionConfig> conditionConfigs) {
        this.conditionConfigs = conditionConfigs;
    }

    /**
     * @return
     */
    public String getCondition() {
        StringBuilder sb = new StringBuilder();
        for (DutyAgentConditionConfig conditionConfig : conditionConfigs) {
            sb.append(conditionConfig.getCondition());
        }
        return sb.toString();
    }

}
