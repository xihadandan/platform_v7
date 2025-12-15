package com.wellsoft.oauth2.event.listener;

import com.wellsoft.oauth2.event.OAuth2ClientAuthenticateSuccessEvent;
import com.wellsoft.oauth2.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
public class OAuth2ClientAuthenticateSuccessEvenListener implements
        ApplicationListener<OAuth2ClientAuthenticateSuccessEvent> {
    @Autowired
    UserAccountService userAccountService;

    @Override
    public void onApplicationEvent(OAuth2ClientAuthenticateSuccessEvent applicationEvent) {
        Authentication auth2Authentication = applicationEvent.getAuthentication();
        if (applicationEvent.getSource() instanceof OAuth2Authentication) {

        }

    }
}
