package com.wellsoft.context.aop;

import com.wellsoft.context.annotation.IpLog;
import com.wellsoft.context.util.NetUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

/**
 * Description: 访问指定方法的ip记录日志
 *
 * @author chenq
 * @date 2019/1/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/30    chenq		2019/1/30		Create
 * </pre>
 */
@Aspect
@Component
public class IpLogAspect {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before(value = "@annotation(com.wellsoft.context.annotation.IpLog)")
    public void before(JoinPoint joinPoint) {
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            logger.info("IP=[ {} ] -> {} | {} ", new String[]{NetUtils.getRequestIp(),
                    AnnotationUtils.getAnnotation(methodSignature.getMethod(), IpLog.class).info(),
                    joinPoint.getSignature().toShortString()});
        } catch (Exception e) {

        }

    }


}
