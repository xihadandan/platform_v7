package com.wellsoft.oauth2.event.publish;

import com.wellsoft.oauth2.event.OAuth2ClientAuthenticateSuccessEvent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/24
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/24    chenq		2019/9/24		Create
 * </pre>
 */
@Component
public class OAuth2ClientAuthenticateSuccessEvenPublisher implements
        ApplicationEventPublisher, ApplicationContextAware {
    ApplicationContext applicationContext;

    @Override
    public void publishEvent(ApplicationEvent applicationEvent) {
        this.publisAuthenticationEvent(applicationEvent);
    }

    private void publisAuthenticationEvent(ApplicationEvent applicationEvent) {
        applicationContext.publishEvent(
                new OAuth2ClientAuthenticateSuccessEvent(
                        (Authentication) applicationEvent.getSource()));
    }

    @Override
    public void publishEvent(Object o) {
        applicationContext.publishEvent(o);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
