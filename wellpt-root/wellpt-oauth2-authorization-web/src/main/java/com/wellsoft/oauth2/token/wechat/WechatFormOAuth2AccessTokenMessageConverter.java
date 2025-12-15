/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.wellsoft.oauth2.token.wechat;

import com.google.common.collect.Sets;
import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;

import java.io.IOException;
import java.util.Date;
import java.util.Map;

/**
 * wechat的accessToken消息转换器
 */
public class WechatFormOAuth2AccessTokenMessageConverter extends
        AbstractHttpMessageConverter<OAuth2AccessToken> {

    private final FormHttpMessageConverter delegateMessageConverter;

    public WechatFormOAuth2AccessTokenMessageConverter() {
        super(MediaType.APPLICATION_FORM_URLENCODED, MediaType.APPLICATION_JSON,
                MediaType.TEXT_PLAIN);
        this.delegateMessageConverter = new FormHttpMessageConverter();
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return WechatOAuth2AccessToken.class.equals(clazz);
    }

    @Override
    protected OAuth2AccessToken readInternal(Class<? extends OAuth2AccessToken> clazz,
                                             HttpInputMessage inputMessage)
            throws IOException, HttpMessageNotReadableException {
        final Map<String, Object> accessToken = new Gson().fromJson(
                IOUtils.toString(inputMessage.getBody()), Map.class);
        WechatOAuth2AccessToken token = new WechatOAuth2AccessToken(
                accessToken.get("access_token").toString());
        token.setScope(Sets.<String>newHashSet(accessToken.get("scope").toString()));
        token.setExpiration(DateUtils.addSeconds(new Date(),
                (int) Double.parseDouble(accessToken.get("expires_in").toString())));
        token.setRefreshToken(new OAuth2RefreshToken() {
            @Override
            public String getValue() {
                return accessToken.get("refresh_token").toString();
            }
        });
        token.setOpenid(accessToken.get("openid").toString());
        return token;

    }

    @Override
    protected void writeInternal(OAuth2AccessToken accessToken,
                                 HttpOutputMessage outputMessage) throws IOException,
            HttpMessageNotWritableException {
        throw new UnsupportedOperationException(
                "This converter is only used for converting from externally aqcuired form data");
    }
}
