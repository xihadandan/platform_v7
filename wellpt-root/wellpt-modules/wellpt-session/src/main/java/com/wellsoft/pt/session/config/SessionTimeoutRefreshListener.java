package com.wellsoft.pt.session.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Description: 根据配置数据更新重新掉注解上的session超时时间
 *
 * @author chenq
 * @date 2019/7/5
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/5    chenq		2019/7/5		Create
 * </pre>
 */
@Component
public class SessionTimeoutRefreshListener implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${session.timeout:14400}")//session超时时间，默认14400秒=4小时
    private Integer sessionTimeout;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
            contextRefreshedEvent.getApplicationContext().getBean(
                    RedisOperationsSessionRepository.class).setDefaultMaxInactiveInterval(
                    sessionTimeout);
            logger.info("设置redis http session超时时间：{} min ",
                    TimeUnit.SECONDS.toMinutes(sessionTimeout));
        }

    }


}
