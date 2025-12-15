/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition.impl;

import com.wellsoft.pt.biz.condition.BizCondition;
import com.wellsoft.pt.biz.condition.BizConditionParam;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
public class BizLogicCondition implements BizCondition {

    private String connector;

    /**
     * @param connector
     */
    public BizLogicCondition(String connector) {
        this.connector = connector;
    }

    @Override
    public String evaluate(BizConditionParam conditionParam) {
        return this.connector;
    }
}
