package com.wellsoft.pt.security.oauth2.configuration;

import com.wellsoft.pt.security.access.DomainSimpleUrlAuthenticationFailureHandler;
import com.wellsoft.pt.security.oauth2.token.LocalOAuth2AccessTokenConverter;
import com.wellsoft.pt.security.support.SecurityConfiguration;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.annotation.Resource;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/8
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/8    chenq		2019/10/8		Create
 * </pre>
 */
@Configuration
@EnableOAuth2Client
@PropertySource(value = {"classpath:system-application-security.properties", "classpath:system.properties"})
public class OAuth2ClientConfiguration {


    @Value("${spring.security.oauth2.clientId}")
    private String oauth2ClientId;
    @Value("${spring.security.oauth2.clientSecret}")
    private String oauth2ClientSecret;
    @Value("${spring.security.oauth2.accessTokenUri}")
    private String oauth2AccessTokenUri;
    @Value("${spring.security.oauth2.userAuthorizationUri}")
    private String oauth2UserAuthorizationUri;
    @Value("${spring.security.oauth2.checkTokenUri}")
    private String oauth2CheckTokenUri;
    @Value("#{'${spring.security.oauth2.scope:read,write}'.split(',')}")
    private List<String> oauth2Scope;

    @Resource
    @Qualifier("accessTokenRequest")
    private AccessTokenRequest accessTokenRequest;

    @Bean
    public OAuth2ProtectedResourceDetails trustedClientCredentialsResourceDetails() {
        ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
        details.setId(oauth2ClientId);
        details.setClientId(oauth2ClientId);
        details.setClientSecret(oauth2ClientSecret);
        details.setAccessTokenUri(oauth2AccessTokenUri);
        details.setScope(oauth2Scope);
        return details;
    }

    /**
     * 实例化客户端API调用：只需要当前客户端ID/密钥进行连接
     *
     * @param resourceDetails
     * @return
     */
    @Bean
    public OAuth2RestTemplate trustClientRestTemplate(@Qualifier("trustedClientCredentialsResourceDetails")
                                                              OAuth2ProtectedResourceDetails resourceDetails) {
        return new OAuth2RestTemplate(resourceDetails, new DefaultOAuth2ClientContext(/*accessTokenRequest*/));
    }


    @Bean
    public AuthorizationCodeResourceDetails authorizationCodeResourceDetails() {
        AuthorizationCodeResourceDetails resourceDetails = new AuthorizationCodeResourceDetails();
        resourceDetails.setClientId(oauth2ClientId);
        resourceDetails.setClientSecret(oauth2ClientSecret);
        resourceDetails.setAccessTokenUri(oauth2AccessTokenUri);
        resourceDetails.setUserAuthorizationUri(oauth2UserAuthorizationUri);
        resourceDetails.setScope(oauth2Scope);
        return resourceDetails;
    }

    @Bean
    public ResourceOwnerPasswordResourceDetails passwordResourceDetails() {
        ResourceOwnerPasswordResourceDetails resourceDetails = new ResourceOwnerPasswordResourceDetails();
        resourceDetails.setClientId(oauth2ClientId);
        resourceDetails.setClientSecret(oauth2ClientSecret);
        resourceDetails.setAccessTokenUri(oauth2AccessTokenUri);
        resourceDetails.setScope(oauth2Scope);
        return resourceDetails;
    }


    @Bean
    public OAuth2RestTemplate oAuth2AuthorizationCodeRestTemplate(
            @Qualifier("authorizationCodeResourceDetails") AuthorizationCodeResourceDetails resourceDetails,
            OAuth2ClientContext oAuth2ClientContext) {
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(resourceDetails,
                oAuth2ClientContext);
        return oAuth2RestTemplate;
    }

    @Bean
    public OAuth2RestTemplate oAuth2PasswordRestTemplate(
            ResourceOwnerPasswordResourceDetails passwordResourceDetails,
            OAuth2ClientContext oAuth2ClientContext) {
        OAuth2RestTemplate oAuth2RestTemplate = new OAuth2RestTemplate(passwordResourceDetails,
                oAuth2ClientContext);
        return oAuth2RestTemplate;
    }

    /**
     * 授权码授权模式的客户端处理
     *
     * @param oAuth2RestTemplate
     * @param remoteTokenServices
     * @return
     */
    @Bean
    public OAuth2ClientAuthenticationProcessingFilter oAuth2AuthorizationCodeClientAuthenticationProcessingFilter(
            @Qualifier("oAuth2AuthorizationCodeRestTemplate") OAuth2RestTemplate oAuth2RestTemplate, @Qualifier("remoteTokenServices") RemoteTokenServices remoteTokenServices) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
                "/login/oauth2");
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login/oauth2", HttpMethod.GET.name()));
        filter.setRestTemplate(oAuth2RestTemplate);
        filter.setTokenServices(remoteTokenServices);
        return filter;
    }

    /**
     * 密码授权模式的客户端处理
     *
     * @param oAuth2PasswordRestTemplate
     * @param remoteTokenServices
     * @return
     */
    @Bean
    public OAuth2ClientAuthenticationProcessingFilter oAuth2ClientPasswordAuthenticationProcessingFilter(
            @Qualifier("oAuth2PasswordRestTemplate") OAuth2RestTemplate oAuth2PasswordRestTemplate, @Qualifier("remoteTokenServices") RemoteTokenServices remoteTokenServices
            , SecurityConfiguration securityConfiguration, CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy) {
        OAuth2ClientAuthenticationProcessingFilter filter = new OAuth2ClientAuthenticationProcessingFilter(
                "/login/oauth2");
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher("/login/oauth2", HttpMethod.POST.name()));
        filter.setRestTemplate(oAuth2PasswordRestTemplate);
        filter.setTokenServices(remoteTokenServices);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        filter.setAuthenticationFailureHandler(new DomainSimpleUrlAuthenticationFailureHandler(securityConfiguration));
        return filter;
    }


    @Bean
    public LocalOAuth2AccessTokenConverter localOAuth2AccessTokenConverter() {
        return new LocalOAuth2AccessTokenConverter();
    }

    @Bean
    public RemoteTokenServices remoteTokenServices(
            LocalOAuth2AccessTokenConverter localOAuth2AccessTokenConverter) {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setClientId(oauth2ClientId);
        remoteTokenServices.setClientSecret(oauth2ClientSecret);
        remoteTokenServices.setAccessTokenConverter(localOAuth2AccessTokenConverter);
        remoteTokenServices.setCheckTokenEndpointUrl(oauth2CheckTokenUri);
        return remoteTokenServices;
    }
}
