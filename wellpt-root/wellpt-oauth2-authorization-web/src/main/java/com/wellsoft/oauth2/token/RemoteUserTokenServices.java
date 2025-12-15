package com.wellsoft.oauth2.token;

import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.security.SecurityUserDetails;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/16    chenq		2019/9/16		Create
 * </pre>
 */
public class RemoteUserTokenServices implements ResourceServerTokenServices {
    protected final Logger logger = LoggerFactory.getLogger(RemoteUserTokenServices.class);

    private OAuth2RestOperations restTemplate;

    private String userTokenEndpointUrl;

    private String clientId;

    private String clientSecret;

    private String tokenName = "token";

    private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

    private PrincipalExtractor principalExtractor = new FixedPrincipalExtractor();

    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();


    public RemoteUserTokenServices() {

    }


    public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {
        this.principalExtractor = principalExtractor;
    }

    public void setUserTokenEndpointUrl(String userTokenEndpointUrl) {
        this.userTokenEndpointUrl = userTokenEndpointUrl;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }


    public void setTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public OAuth2Authentication loadAuthentication(
            String accessToken) throws AuthenticationException, InvalidTokenException {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
        formData.add(tokenName, accessToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", getAuthorizationHeader(clientId, clientSecret));
        Map<String, Object> map = getForMap(userTokenEndpointUrl, accessToken);

        if (map.containsKey("error")) {
            if (logger.isDebugEnabled()) {
                logger.debug("check_token returned error: " + map.get("error"));
            }
            throw new InvalidTokenException(accessToken);
        }

        // gh-838
        if (map.containsKey("active") && !"true".equals(String.valueOf(map.get("active")))) {
            logger.debug("check_token returned active attribute: " + map.get("active"));
            throw new InvalidTokenException(accessToken);
        }

        return extractAuthentication(map);
    }

    protected Object getPrincipal(Map<String, Object> map) {
        return this.principalExtractor.extractPrincipal(map);
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        FixedPrincipal principal = (FixedPrincipal) getPrincipal(map);
        UserAccountService userAccountService = SpringContextHolder.getBean(
                UserAccountService.class);
        UserDetailsService userDetailsService = SpringContextHolder.getBean(
                UserDetailsService.class);
        UserAccountEntity userAccountEntity = userAccountService.addExtAccountWhenNotExist(
                principal.getUsername(), principal.getFrom(), map.get("name").toString());
        List<GrantedAuthority> authorities = this.authoritiesExtractor
                .extractAuthorities(map);

        SecurityUserDetails securityUserDetails = (SecurityUserDetails) userDetailsService.loadUserByUsername(
                userAccountEntity.getAccountNumber());
        securityUserDetails.setUuid(userAccountEntity.getUuid());
        authorities.addAll(securityUserDetails.getAuthorities());//扩充本地应用的权限

        OAuth2Request request = new OAuth2Request(null, this.clientId, null, true, null,
                null, null, null, null);

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                securityUserDetails, "N/A", authorities);
        token.setDetails(map);

        return new OAuth2Authentication(request, token);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String accessToken) {
        throw new UnsupportedOperationException("Not supported: read access token");
    }

    private String getAuthorizationHeader(String clientId, String clientSecret) {

        if (clientId == null || clientSecret == null) {
            logger.warn(
                    "Null Client ID or Client Secret detected. Endpoint that requires authentication will reject request with 401 error.");
        }

        String creds = String.format("%s:%s", clientId, clientSecret);
        try {
            return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Could not convert String");
        }
    }

    private Map<String, Object> getForMap(String path, String accessToken) {

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Getting user info from: " + path);
        }
        try {
            OAuth2RestOperations restTemplate = this.restTemplate;
            if (restTemplate == null) {
                BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
                resource.setClientId(this.clientId);
                restTemplate = new OAuth2RestTemplate(resource);
            }
            OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
                    .getAccessToken();
            if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
                        accessToken);
                token.setTokenType(this.tokenType);
                restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }
            return restTemplate.getForEntity(path, Map.class).getBody();
        } catch (Exception ex) {
            this.logger.warn("Could not fetch user details: " + ex.getClass() + ", "
                    + ex.getMessage());
            return Collections.<String, Object>singletonMap("error",
                    "Could not fetch user details");
        }
        /*if (headers.getContentType() == null) {
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        }
        @SuppressWarnings("rawtypes")
        Map map = restTemplate.exchange(path, HttpMethod.GET,
                new HttpEntity<MultiValueMap<String, String>>(formData, headers),
                Map.class).getBody();
        @SuppressWarnings("unchecked")
        Map<String, Object> result = map;
        return result;*/
    }
}
