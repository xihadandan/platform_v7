package com.wellsoft.pt.security.oauth2.token;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import java.io.IOException;
import java.util.Map;

/**
 * Description:
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
public class LocalOAuth2RestTemplate extends OAuth2RestTemplate {
    public LocalOAuth2RestTemplate(
            OAuth2ProtectedResourceDetails resource) {
        super(resource);
    }

    public LocalOAuth2RestTemplate(
            OAuth2ProtectedResourceDetails resource,
            OAuth2ClientContext context) {
        super(resource, context);
    }


    public ResponseEntity registerAccount(String path, LocalOAuth2UserInfo userInfo) {

        return (ResponseEntity) this.execute(path, HttpMethod.POST,
                new BearAccessTokenReqeustCallback(getOAuth2ClientContext()),
                new ResponseExtractor<Map>() {
                    @Override
                    public Map extractData(
                            ClientHttpResponse clientHttpResponse) throws IOException {
                        return new Gson().fromJson(IOUtils.toString(clientHttpResponse.getBody()),
                                Map.class);
                    }
                });
    }


    private class BearAccessTokenReqeustCallback implements RequestCallback {

        OAuth2ClientContext context;

        public BearAccessTokenReqeustCallback(OAuth2ClientContext context) {
            this.context = context;
        }


        @Override
        public void doWithRequest(ClientHttpRequest clientHttpRequest) throws IOException {
            HttpHeaders httpHeaders = clientHttpRequest.getHeaders();
            httpHeaders.add("authorization", this.context.getAccessToken().getValue());
        }
    }


}
