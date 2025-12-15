package com.wellsoft.oauth2.config;

import com.wellsoft.oauth2.event.publish.OAuth2ClientAuthenticateSuccessEvenPublisher;
import com.wellsoft.oauth2.security.SecurityUserDetailsRepositoryManager;
import com.wellsoft.oauth2.security.UserDetailsServiceHolder;
import com.wellsoft.oauth2.service.UserAccountService;
import com.wellsoft.oauth2.token.FixedPrincipalExtractor;
import com.wellsoft.oauth2.token.PrincipalSourceEnum;
import com.wellsoft.oauth2.token.RemoteUserTokenServices;
import com.wellsoft.oauth2.token.wechat.WechatAuthorizationCodeAccessTokenProvider;
import com.wellsoft.oauth2.token.wechat.WechatRestOperations;
import com.wellsoft.oauth2.token.wechat.WechatUserTokenServices;
import com.wellsoft.oauth2.web.filter.EnableCorsFilter;
import com.wellsoft.oauth2.web.filter.SsoLogoutFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.AuthenticationScheme;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CompositeFilter;

import javax.servlet.Filter;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-oauth2-client.properties")
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;
    @Autowired
    OAuth2ClientContextFilter oauth2ClientContextFilter;
    @Autowired
    UserAccountService userService;
    @Autowired
    UserDetailsServiceHolder userDetailsServiceHolder;
    @Autowired
    @Qualifier("authenticationManagerBean")
    AuthenticationManager authenticationManager;
    @Autowired
    OAuth2ClientAuthenticateSuccessEvenPublisher evenPublisher;
    @Autowired
    private DataSource dataSource;
    @Value("${github.clientId}")
    private String githubClientId;

    @Value("${github.clientSecret}")
    private String githubClientSecret;

    @Value("${github.accessTokenUri}")
    private String githubAccessTokenUri;

    @Value("${github.userAuthorizationUri}")
    private String githubUserAuthorizationUri;

    @Value("${github.userTokenEndpoUrl}")
    private String githubUserTokenEndpointUrl;


    @Value("${wechat.clientId}")
    private String wechatClientId;

    @Value("${wechat.clientSecret}")
    private String wechatClientSecret;

    @Value("${wechat.accessTokenUri}")
    private String wechatAccessTokenUri;

    @Value("${wechat.userAuthorizationUri}")
    private String wechatUserAuthorizationUri;

    @Value("${wechat.userTokenEndpoUrl}")
    private String wechatUserTokenEndpointUrl;


    @Bean
    @Lazy
    public AuthenticationProvider authenticationProvider(PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }


    @Override
    @Bean
    @Lazy
    public UserDetailsService userDetailsService() {
        SecurityUserDetailsRepositoryManager userDetailsRepositoryManager = new SecurityUserDetailsRepositoryManager(
                userDetailsServiceHolder);
        userDetailsRepositoryManager.setAuthenticationManager(authenticationManager);
        return userDetailsRepositoryManager;
    }


    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/webjars/**", "/css/**", "/fonts/**", "/images/**",
                "/assets/**",
                "/oauth/uncache_approvals",
                "/oauth/cache_approvals");
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http
                .authorizeRequests()
                .antMatchers("/login**", "/oauth/token").permitAll()
                .antMatchers("/client**", "/user/addAccount", "/user/update", "/user/delete").hasRole("ADMIN")
                .antMatchers("/", "/user**").hasAnyRole("USER", "ADMIN")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/error/403.jsp?authorization_error=true")
                .and()
                .csrf()
                .requireCsrfProtectionMatcher(
                        new AntPathRequestMatcher("/oauth/authorize"))
                .disable()
                .csrf().ignoringAntMatchers("/logout"/*, "/user/registerAccount", "/user/check","/user/password","/user/expired"*/).and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                .and()
                /* .headers().addHeaderWriter(new XFrameOptionsHeaderWriter(
                 XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)).and()*/
                .headers().frameOptions().disable().and()
                .formLogin()
                .loginProcessingUrl("/login")
                .failureUrl("/login?authentication_error=true")
                .loginPage("/login")
                .and()
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                .addFilterBefore(new SsoLogoutFilter(), LogoutFilter.class)
                .addFilterBefore(new EnableCorsFilter("/cors/oauth2/logout", "/oauth/**", "/login"/*,
                                "/user/registerAccount", "/user/check","/user/password"*/),
                        WebAsyncManagerIntegrationFilter.class)

                .addFilterBefore(thirdOAuthAuthrizationFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(oauth2ClientContextFilter, SecurityContextPersistenceFilter.class)

        ;
        // @formatter:on
    }


    public OAuth2ProtectedResourceDetails githubDetails() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId(githubClientId);
        details.setClientSecret(githubClientSecret);
        details.setAccessTokenUri(githubAccessTokenUri);
        details.setUserAuthorizationUri(githubUserAuthorizationUri);
        details.setAuthenticationScheme(AuthenticationScheme.query);
        details.setClientAuthenticationScheme(AuthenticationScheme.form);
        //            details.setScope(Arrays.asList("read", "write"));
        return details;
    }


    public OAuth2ProtectedResourceDetails wechatDetails() {
        AuthorizationCodeResourceDetails details = new AuthorizationCodeResourceDetails();
        details.setClientId(wechatClientId);
        details.setClientSecret(wechatClientSecret);
        details.setAccessTokenUri(wechatAccessTokenUri);
        details.setUserAuthorizationUri(wechatUserAuthorizationUri);
        details.setAuthenticationScheme(AuthenticationScheme.query);
        details.setClientAuthenticationScheme(AuthenticationScheme.form);
        //            details.setScope(Arrays.asList("read", "write"));
        return details;
    }

    private Filter thirdOAuthAuthrizationFilter() {
        CompositeFilter filter = new CompositeFilter();
        List<Filter> filters = new ArrayList<>();
        filters.add(gitHubSsoFilter(githubDetails(), "/login/github"));
        filters.add(wechatSsoFilter(wechatDetails(), "/login/wechat"));
        filter.setFilters(filters);
        return filter;
    }

    private Filter wechatSsoFilter(OAuth2ProtectedResourceDetails details, String path) {
        OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter = new OAuth2ClientAuthenticationProcessingFilter(
                path);
        WechatRestOperations wechatRestOperations = new WechatRestOperations(details,
                oauth2ClientContext);
        wechatRestOperations.setAccessTokenProvider(
                new WechatAuthorizationCodeAccessTokenProvider());
        authenticationProcessingFilter.setRestTemplate(wechatRestOperations);
        WechatUserTokenServices userTokenServices = new WechatUserTokenServices();
        userTokenServices.setClientId(details.getClientId());
        userTokenServices.setClientSecret(details.getClientSecret());
        userTokenServices.setUserTokenEndpointUrl(wechatUserTokenEndpointUrl);
        userTokenServices.setoAuth2ClientContext(oauth2ClientContext);
        userTokenServices.setRestTemplate(wechatRestOperations);

        authenticationProcessingFilter.setTokenServices(userTokenServices);
        authenticationProcessingFilter.setApplicationEventPublisher(evenPublisher);
        return authenticationProcessingFilter;
    }


    private Filter gitHubSsoFilter(OAuth2ProtectedResourceDetails details, String path) {
        OAuth2ClientAuthenticationProcessingFilter authenticationProcessingFilter = new OAuth2ClientAuthenticationProcessingFilter(
                path);
        OAuth2RestTemplate restTemplate = new OAuth2RestTemplate(details,
                oauth2ClientContext);
        authenticationProcessingFilter.setRestTemplate(restTemplate);
        RemoteUserTokenServices remoteTokenServices = new RemoteUserTokenServices();
        remoteTokenServices.setClientId(githubClientId);
        remoteTokenServices.setClientSecret(githubClientSecret);
        remoteTokenServices.setUserTokenEndpointUrl(githubUserTokenEndpointUrl);
        remoteTokenServices.setPrincipalExtractor(
                new FixedPrincipalExtractor(PrincipalSourceEnum.GITHUB_OAUTH2_PRINCIPAL));
        authenticationProcessingFilter.setTokenServices(remoteTokenServices);
        authenticationProcessingFilter.setApplicationEventPublisher(evenPublisher);
        return authenticationProcessingFilter;
    }

    @Bean(name = "applicationEventMulticaster")
    public ApplicationEventMulticaster simpleApplicationEventMulticaster() {
        SimpleApplicationEventMulticaster eventMulticaster
                = new SimpleApplicationEventMulticaster();

        eventMulticaster.setTaskExecutor(new SimpleAsyncTaskExecutor());
        return eventMulticaster;
    }
}


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
    @Autowired
    private SpringSecurityConfiguration securityConfig;

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}


