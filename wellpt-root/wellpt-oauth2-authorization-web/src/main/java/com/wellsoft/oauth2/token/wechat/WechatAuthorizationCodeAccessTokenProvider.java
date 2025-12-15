/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Copyright 2002-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wellsoft.oauth2.token.wechat;

import com.google.common.collect.Lists;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;


/**
 * wechat授权访问码处理
 */
public class WechatAuthorizationCodeAccessTokenProvider extends
        AuthorizationCodeAccessTokenProvider implements
        AccessTokenProvider {


    protected ResponseExtractor<ResponseEntity<Void>> getAuthorizationResponseExtractor() {
        return new ResponseExtractor<ResponseEntity<Void>>() {
            public ResponseEntity<Void> extractData(
                    ClientHttpResponse response) throws IOException {
                return new ResponseEntity<Void>(response.getHeaders(), response.getStatusCode());
            }
        };
    }


    @Override
    protected ResponseExtractor<OAuth2AccessToken> getResponseExtractor() {
        getRestTemplate(); // force initialization
        return new HttpMessageConverterExtractor(
                WechatOAuth2AccessToken.class,
                Lists.newArrayList(new WechatFormOAuth2AccessTokenMessageConverter()));


    }


    @Override
    protected String getAccessTokenUri(OAuth2ProtectedResourceDetails resource,
                                       MultiValueMap<String, String> form) {
        String accessTokenUri = resource.getAccessTokenUri();

        if (logger.isDebugEnabled()) {
            logger.debug("Retrieving token from " + accessTokenUri);
        }

        StringBuilder builder = new StringBuilder(accessTokenUri);
        form.put("appid", form.get("client_id"));
        form.put("secret", form.get("client_secret"));
        form.remove("client_id");
        form.remove("client_secret");
        if (getHttpMethod() == HttpMethod.GET) {
            String separator = "?";
            if (accessTokenUri.contains("?")) {
                separator = "&";
            }

            for (String key : form.keySet()) {
                builder.append(separator);
                builder.append(key + "={" + key + "}");
                separator = "&";
            }
        }

        return builder.toString();
    }

    @Override
    protected HttpMethod getHttpMethod() {
        return HttpMethod.GET;
    }


}
