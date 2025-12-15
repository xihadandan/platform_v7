/*
 * @(#)2020年10月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.autoconfigure;

import com.wellsoft.context.config.Config;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年10月27日.1	zhulh		2020年10月27日		Create
 * </pre>
 * @date 2020年10月27日
 */
public class OnInternetDyformCondition implements Condition {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.annotation.Condition#matches(org.springframework.context.annotation.ConditionContext, org.springframework.core.type.AnnotatedTypeMetadata)
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String enabledInternetDyform = Config.getValue("dyform.internet.enabled");
        return Config.TRUE.equalsIgnoreCase(StringUtils.trim(enabledInternetDyform));
    }

}
