package com.wellsoft.oauth2.token.wechat;

import com.google.common.collect.Lists;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.security.oauth2.client.http.OAuth2ErrorHandler;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.client.HttpMessageConverterExtractor;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Description: wechat oauth2的相关restful接口操作
 *
 * @author chenq
 * @date 2019/10/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/15    chenq		2019/10/15		Create
 * </pre>
 */
public class WechatRestOperations extends OAuth2RestTemplate implements OAuth2RestOperations {
    private final OAuth2ProtectedResourceDetails resource;


    private OAuth2ClientContext context;

    private OAuth2RequestAuthenticator authenticator = new DefaultOAuth2RequestAuthenticator();

    public WechatRestOperations(OAuth2ProtectedResourceDetails resource) {
        this(resource, new DefaultOAuth2ClientContext());
    }

    public WechatRestOperations(OAuth2ProtectedResourceDetails resource,
                                OAuth2ClientContext context) {
        super(resource, context);
        if (resource == null) {
            throw new IllegalArgumentException("An OAuth2 resource must be supplied.");
        }

        this.resource = resource;
        this.context = context;
        setErrorHandler(new OAuth2ErrorHandler(resource));
    }


    private String getClientId() {
        return resource.getClientId();
    }


    public OAuth2AccessToken getAccessToken() throws UserRedirectRequiredException {
        try {
            return super.getAccessToken();
        } catch (UserRedirectRequiredException e) {
            //修改客户端参数
            e.getRequestParams().put("appid", getClientId());
            e.getRequestParams().remove("client_id");
            e.getRequestParams().put("scope", WechatOAuth2AccessToken.SCOPE_USRINFO);
            throw e;
        }

    }


    /**
     * @return the context for this template
     */
    public OAuth2ClientContext getOAuth2ClientContext() {
        return context;
    }


    public void setAccessTokenProvider(AccessTokenProvider accessTokenProvider) {
        super.setAccessTokenProvider(accessTokenProvider);
    }

    @Override
    protected <T> ResponseExtractor<ResponseEntity<T>> responseEntityExtractor(Type responseType) {
        return new ResponseEntityResponseExtractor<T>(responseType);
    }


    @Override
    public List<HttpMessageConverter<?>> getMessageConverters() {
        List<HttpMessageConverter<?>> httpMessageConverters = Lists.newArrayList();
        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        gsonHttpMessageConverter.setSupportedMediaTypes(
                Lists.<MediaType>newArrayList(MediaType.APPLICATION_JSON,
                        new MediaType("application", "*+json"), MediaType.TEXT_PLAIN));
        httpMessageConverters.add(gsonHttpMessageConverter);//使用Gson转换器
        return httpMessageConverters;
    }

    private class ResponseEntityResponseExtractor<T> implements
            ResponseExtractor<ResponseEntity<T>> {

        private final HttpMessageConverterExtractor<T> delegate;

        public ResponseEntityResponseExtractor(Type responseType) {
            if (responseType != null && Void.class != responseType) {
                this.delegate = new HttpMessageConverterExtractor<T>(responseType,
                        getMessageConverters());
            } else {
                this.delegate = null;
            }
        }

        @Override
        public ResponseEntity<T> extractData(ClientHttpResponse response) throws IOException {
            if (this.delegate != null) {
                T body = this.delegate.extractData(response);
                return ResponseEntity.status(response.getRawStatusCode()).headers(
                        response.getHeaders()).body(body);
            } else {
                return ResponseEntity.status(response.getRawStatusCode()).headers(
                        response.getHeaders()).build();
            }
        }
    }

}
