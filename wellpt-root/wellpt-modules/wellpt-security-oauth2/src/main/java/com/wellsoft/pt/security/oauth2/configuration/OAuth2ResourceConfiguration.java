package com.wellsoft.pt.security.oauth2.configuration;

import com.wellsoft.pt.security.access.JwtAuthenticationTokenFilter;
import com.wellsoft.pt.security.oauth2.token.DefaultBearerTokenExtractor;
import com.wellsoft.pt.security.oauth2.token.DefaultRemoteTokenServices;
import com.wellsoft.pt.security.oauth2.token.LocalOAuth2AccessTokenConverter;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月22日   chenq	 Create
 * </pre>
 */
@Configuration
@EnableResourceServer
public class OAuth2ResourceConfiguration extends ResourceServerConfigurerAdapter {


    @Value("${spring.security.oauth2.clientId}")
    private String oauth2ClientId;
    @Value("${spring.security.oauth2.clientSecret}")
    private String oauth2ClientSecret;
    @Value("${spring.security.oauth2.checkTokenUri}")
    private String oauth2CheckTokenUri;
    @Value("#{'${spring.security.oauth2.scope:read,write}'.split(',')}")
    private List<String> oauth2Scope;
    @Value(("${spring.security.oauth2.resource.url:/rest-api/**,/api/**}"))
    private String oauthResourceApiPaths;
    @Autowired
    private LocalOAuth2AccessTokenConverter localOAuth2AccessTokenConverter;
    @Value("${spring.security.anonymousUrl:}")
    private String annoymousUrls;
    @Value("${spring.security.oauth2.ignoreUrl:}")
    private String oauth2IgnoreUrls;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        DefaultRemoteTokenServices remoteTokenServices = new DefaultRemoteTokenServices();
        remoteTokenServices.setClientId(oauth2ClientId);
        remoteTokenServices.setClientSecret(oauth2ClientSecret);
        remoteTokenServices.setCheckTokenEndpointUrl(oauth2CheckTokenUri);
        remoteTokenServices.setAccessTokenConverter(localOAuth2AccessTokenConverter);
        if (StringUtils.isNotBlank(annoymousUrls)) {
            remoteTokenServices.setAnoymouseUrls(annoymousUrls.split(",|;"));
        }
        //忽略地址
        oauth2IgnoreUrls += "/web/login/config/appLoginPageConfigSetting,/wopi/files/**,/web/login/config/loginPageSettings";
        resources.tokenExtractor(new DefaultBearerTokenExtractor(oauth2IgnoreUrls.split(",|;")));
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        oauthResourceApiPaths += ",/pt/**,/security/**,/webapp/**,/dms/**,/repository/**,/basicdata/**,/common/**,/multi/org/**,/web/app/**,/json/data/services,/mobile/**";
        String[] urls = oauthResourceApiPaths.split(",|;");
        http.cors();
        http.addFilterBefore(new CharacterEncodingFilter(Charsets.UTF_8.name(), true), SecurityContextPersistenceFilter.class);
        http.addFilterAfter(new JwtAuthenticationTokenFilter(), AbstractPreAuthenticatedProcessingFilter.class);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .and().authorizeRequests().antMatchers("/web/login/config/appLoginPageConfigSetting", "/wopi/files/**", "/web/login/config/loginPageSettings").permitAll()
                .and().authorizeRequests().antMatchers(urls).hasAnyRole("TRUSTED_CLIENT", "INTERNET_USER", "USER");
        http.headers().frameOptions().disable();

    }

}
