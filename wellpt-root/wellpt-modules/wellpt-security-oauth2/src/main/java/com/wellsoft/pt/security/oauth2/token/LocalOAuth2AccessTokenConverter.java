package com.wellsoft.pt.security.oauth2.token;

import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;


public class LocalOAuth2AccessTokenConverter extends DefaultAccessTokenConverter {

    public LocalOAuth2AccessTokenConverter() {
        super();
        super.setUserTokenConverter(new LocalOAuth2UserAuthenticationConverter());
    }


}