package com.wellsoft.pt.xxljob.autoconf;

import com.wellsoft.pt.xxljob.XxlJobConfig;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @Auther: yt
 * @Date: 2020/12/31 15:43
 * @Description:
 */
public class XxlJobExecutorCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        if (XxlJobConfig.isStart()) {
            return true;
        }
        return false;
    }
}
