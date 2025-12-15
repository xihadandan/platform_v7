/*
 * @(#)2015年8月28日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.enums.Separator;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

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
 * 2015年8月28日.1	zhulh		2015年8月28日		Create
 * </pre>
 * @date 2015年8月28日
 */
public class TaskUserExpressionConfigJson {

    // 表达式配置
    private List<TaskUserExpressionConfig> expressionConfigs;

    /**
     * @return the expressionConfigs
     */
    public List<TaskUserExpressionConfig> getExpressionConfigs() {
        return expressionConfigs;
    }

    /**
     * @param expressionConfigs 要设置的expressionConfigs
     */
    public void setExpressionConfigs(List<TaskUserExpressionConfig> expressionConfigs) {
        this.expressionConfigs = expressionConfigs;
    }

    /**
     * @return
     */
    @JsonIgnore
    public String getExpression(boolean useValuePath) {
        StringBuilder sb = new StringBuilder();
        for (TaskUserExpressionConfig expressionConfig : expressionConfigs) {
            sb.append(expressionConfig.getExpression(useValuePath));
        }
        return sb.toString();
    }

    @JsonIgnore
    public String getExpressionDisplayName() {
        List<String> displayNames = Lists.newArrayList();
        for (TaskUserExpressionConfig expressionConfig : expressionConfigs) {
            displayNames.add(expressionConfig.getExpressionDisplayName());
        }
        return StringUtils.join(displayNames, Separator.SPACE.getValue());
    }

}
