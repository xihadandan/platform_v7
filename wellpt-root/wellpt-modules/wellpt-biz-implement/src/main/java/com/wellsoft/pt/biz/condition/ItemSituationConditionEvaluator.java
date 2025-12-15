/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition;

import com.wellsoft.pt.biz.support.ProcessItemConfig;

import java.util.Map;

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
public interface ItemSituationConditionEvaluator {
    /**
     * @param situationConfig
     * @param values
     * @return
     */
    boolean evaluate(ProcessItemConfig.SituationConfig situationConfig, Map<String, Object> values);
}
