package com.wellsoft.oauth2.event;

import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

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
public class OAuth2ClientAuthenticateSuccessEvent extends AuthenticationSuccessEvent {

    public OAuth2ClientAuthenticateSuccessEvent(
            Authentication authentication) {
        super(authentication);
    }
}
