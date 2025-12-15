/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.condition;

import com.wellsoft.pt.biz.condition.impl.BizFormFieldCondition;
import com.wellsoft.pt.biz.condition.impl.BizLogicCondition;
import com.wellsoft.pt.biz.condition.impl.BizProcessItemMilestoneCondition;
import com.wellsoft.pt.biz.condition.impl.BizProcessStateCondition;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.ProcessNodeConfig;

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
public class BizConditionFactory {
    /**
     * 获取过程状态条件
     *
     * @param config
     * @return
     */
    public static BizCondition getCondition(ProcessNodeConfig.StateConditionConfig config) {
        String type = config.getType();
        BizCondition bizCondition = null;
        switch (type) {
            case "1":
                bizCondition = new BizProcessStateCondition(config);
                break;
            case "2":
                bizCondition = new BizProcessItemMilestoneCondition(config);
                break;
            case "9":
                bizCondition = new BizLogicCondition(config.getConnector());
                break;
            default:
                break;
        }
        return bizCondition;
    }

    /**
     * 获取事项办理情形条件
     *
     * @param situationConditionConfig
     * @return
     */
    public static BizCondition getSituationCondition(ProcessItemConfig.SituationConditionConfig situationConditionConfig) {
        String type = situationConditionConfig.getType();
        BizCondition bizCondition = null;
        switch (type) {
            case "3":
                bizCondition = new BizFormFieldCondition(situationConditionConfig);
                break;
            case "9":
                bizCondition = new BizLogicCondition(situationConditionConfig.getConnector());
                break;
            default:
                break;
        }
        return bizCondition;
    }
}
