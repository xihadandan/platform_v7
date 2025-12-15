package com.wellsoft.oauth2.token.wechat;

import com.wellsoft.oauth2.entity.UserAccountEntity;
import com.wellsoft.oauth2.security.SecurityUserDetails;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.token.AuthoritiesExtractor;
import com.wellsoft.oauth2.token.FixedAuthoritiesExtractor;
import com.wellsoft.oauth2.token.FixedPrincipal;
import com.wellsoft.oauth2.token.PrincipalSourceEnum;
import com.wellsoft.oauth2.utils.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Description: wechat用户token解析服务
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
public class WechatUserTokenServices implements ResourceServerTokenServices {
    protected final Logger logger = LoggerFactory.getLogger(WechatUserTokenServices.class);

    private WechatRestOperations restTemplate;

    private OAuth2ClientContext oAuth2ClientContext;

    private String userTokenEndpointUrl;

    private String clientId;

    private String clientSecret;

    private String tokenName = "token";

    private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;


    private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();


    public WechatUserTokenServices() {

    }


    public void setRestTemplate(WechatRestOperations restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setoAuth2ClientContext(
            OAuth2ClientContext oAuth2ClientContext) {
        this.oAuth2ClientContext = oAuth2ClientContext;
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
//        access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN
        WechatOAuth2AccessToken oAuth2AccessToken = (WechatOAuth2AccessToken) oAuth2ClientContext.getAccessToken();


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
        return new FixedPrincipal(map.get("openid").toString(),
                PrincipalSourceEnum.WECHAT_OAUTH2_PRINCIPAL, map.get("nickname").toString());
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        FixedPrincipal principal = (FixedPrincipal) getPrincipal(map);
        UserAccountService userAccountService = SpringContextHolder.getBean(
                UserAccountService.class);
        UserDetailsService userDetailsService = SpringContextHolder.getBean(
                UserDetailsService.class);
        UserAccountEntity userAccountEntity = userAccountService.addExtAccountWhenNotExist(
                principal.getUsername(), principal.getFrom(), principal.getNickname());
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


    private Map<String, Object> getForMap(String path, String accessToken) {

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Getting user info from: " + path);
        }
        try {
            OAuth2RestOperations restTemplate = this.restTemplate;
            WechatOAuth2AccessToken existingToken = (WechatOAuth2AccessToken) restTemplate.getOAuth2ClientContext()
                    .getAccessToken();
            if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
                        accessToken);
                token.setTokenType(this.tokenType);
                restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }

            path += "?access_token=" + existingToken.getValue();
            path += "&openid=" + existingToken.getOpenid();
            path += "&lang=zh_CN";
            ResponseEntity responseEntity = restTemplate.getForEntity(path, Map.class);
            return (Map<String, Object>) responseEntity.getBody();
        } catch (Exception ex) {
            this.logger.warn("Could not fetch user details: " + ex.getClass() + ", "
                    + ex.getMessage());
            return Collections.<String, Object>singletonMap("error",
                    "Could not fetch user details");
        }

    }
}
