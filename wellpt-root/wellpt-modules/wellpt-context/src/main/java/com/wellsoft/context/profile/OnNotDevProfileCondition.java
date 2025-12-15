/*
 * @(#)2020年12月29日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.profile;

import com.wellsoft.context.config.Config;
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
 * 2020年12月29日.1	zhulh		2020年12月29日		Create
 * </pre>
 * @date 2020年12月29日
 */
public class OnNotDevProfileCondition implements Condition {

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.context.annotation.Condition#matches(org.springframework.context.annotation.ConditionContext, org.springframework.core.type.AnnotatedTypeMetadata)
     */
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return !Config.ENV_DEV.equals(Config.getAppEnv());
    }

}
