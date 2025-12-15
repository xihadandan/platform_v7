package com.wellsoft.pt;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.Context;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.filter.RedirectAuthorizedHomePageFilter;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.security.access.*;
import com.wellsoft.pt.security.access.intercept.FilterSecurityInterceptorImpl;
import com.wellsoft.pt.security.access.intercept.MultiTenantFilterInvocationSecurityMetadataSource;
import com.wellsoft.pt.security.audit.support.ResourceDataSource;
import com.wellsoft.pt.security.cas.WellCasAuthenticationEntryPoint;
import com.wellsoft.pt.security.cas.filter.CasLoginAuthenticationFilter;
import com.wellsoft.pt.security.cas.userdetails.GrantedAuthorityFromAssertionAttributesUserDetailsService;
import com.wellsoft.pt.security.core.authentication.AuthenticationSuccessHandler;
import com.wellsoft.pt.security.core.authentication.CustomDaoAuthenticationProvider;
import com.wellsoft.pt.security.core.authentication.CustomSaveRequestAwareAuthenticationSuccessHandler;
import com.wellsoft.pt.security.core.request.DefaultHttpSessionRequestCache;
import com.wellsoft.pt.security.core.support.AllowXFrameOptionsHeaderWriter;
import com.wellsoft.pt.security.core.support.RuleXFrameOptionsHeaderWriter;
import com.wellsoft.pt.security.core.support.RuleXFrameOptionsHeaderWriter.XFrameOptionsMode;
import com.wellsoft.pt.security.core.userdetails.CompositeUserDetailsCache;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.support.AuthenticationUrlConfiguration;
import com.wellsoft.pt.security.support.LoginAuthenticationSuccessHandler;
import com.wellsoft.pt.security.support.SecurityConfiguration;
import com.wellsoft.pt.security.util.TenantContextHolder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.header.HeaderWriter;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.session.ConcurrentSessionFilter;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Description: spring security配置
 *
 * @author chenq
 * @date 2019/11/7
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/7    chenq		2019/11/7		Create
 * </pre>
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private String tenantId = Config.DEFAULT_TENANT;

    @Resource(name = "loginUrlAuthenticationEntryPoint")
    private AuthenticationEntryPoint authenticationEntryPoint;
    @Resource
    private SecurityConfiguration securityConfiguration;
    @Autowired
    private CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy;
    @Autowired
    private RedirectAuthorizedHomePageFilter redirectAuthorizedHomePageFilter;
    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    private MultiTenantFilterInvocationSecurityMetadataSource securityMetadataSource;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private AccessDecisionManager accessDecisionManager;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Resource(name = "defaultAuthenticationProvider")
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private CasLoginAuthenticationFilter casLoginAuthenticationFilter;
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired(required = false)
    private List<ResourceDataSource> resourceDataSources;
    // 匿名权限的url配置
    @Value("${spring.security.anonymousUrl:}")
    private String annoymousUrls;
    @Value("${spring.security.internetUserLoginSuccessUrl:/passport/iuser/login/success}")
    private String internetUserAuthenticationSuccessUrl;
    @Value("${spring.security.web.ignoreUrls:}")
    private String webIgnoreUrls;
    @Value("${spring.security.user.expired.redirect:/pt/common/user_expired.jsp}")
    private String userExpiredRedirectUrl;
    @Value("${spring.security.session.expired.redirect:/index.jsp}")
    private String sessionExpiredRedirectUrl;
    @Value("${spring.security.cas.url:}")
    private String securityCasUrl;
    @Value("${spring.security.cas.application.url:}")
    private String securityCasAppUrl;
    @Autowired
    private CacheManager cacheManager;

    /**
     * 用户信息服务
     *
     * @return
     */
    @Override
    protected UserDetailsService userDetailsService() {
        return new com.wellsoft.pt.security.core.userdetails.impl.UserDetailServiceImpl();
    }

    @Override
    @Bean(name = BeanIds.USER_DETAILS_SERVICE)
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return userDetailsService();
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        DefaultHttpFirewall defaultHttpFirewall = new DefaultHttpFirewall();
        defaultHttpFirewall.setAllowUrlEncodedSlash(true);
        web.httpFirewall(defaultHttpFirewall);
        Set<String> urls = Sets.newHashSet("/resources/**", "/security/license/upload", "/static/**");
        if (StringUtils.isNotBlank(webIgnoreUrls)) {
            String[] ignores = webIgnoreUrls.split(",|;");
            urls.addAll(Arrays.asList(ignores));
        }
        web.ignoring().antMatchers(urls.toArray(new String[]{}));
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().authenticationEntryPoint(authenticationEntryPoint);

        // 请求授权
        http.authorizeRequests()
                .filterSecurityInterceptorOncePerRequest(true)
                .withObjectPostProcessor(new ObjectPostProcessor<Object>() {
                    @Override
                    public <O> O postProcess(O object) {
                        if (object instanceof FilterSecurityInterceptor) {
                            // 实例化到spring上下文，匿名过滤器服务使用
                            DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) WebSecurityConfiguration.this
                                    .getApplicationContext().getAutowireCapableBeanFactory();
                            defaultListableBeanFactory.registerSingleton("filterSecurityInterceptor", object);
                        }
                        return object;
                    }
                })
                // 匿名请求
                .antMatchers(defaultAnnoymousUrls().toArray(new String[]{}))
                .hasRole("ANONYMOUS")

                // 超级管理员请求
                .antMatchers("/superadmin/login/success**/**", "/superadmin/tenant/review**/**",
                        "/superadmin/tenant/active**/**", "/superadmin/tenant/deactive**/**",
                        "/superadmin/tenant/reject**/**", "/superadmin/database/config**/**",
                        "/superadmin/unit/commonUnitTree/config**/**", "/superadmin/unit/businessType/config**/**")
                .hasRole("ADMIN")
//                .antMatchers(nodeServerDispatchUrls().toArray(new String[0])).hasRole("TRUSTED_NODE_SERVER")
                // 用户请求
                .antMatchers("/security_homepage").hasRole("USER").antMatchers("/html/**", "/security/user/details/**")
                .hasAnyRole("USER", "INTERNET_USER").anyRequest().authenticated();

        // 其他保护设置
        http.headers().xssProtection().and().httpStrictTransportSecurity().and().frameOptions().disable().and().csrf()
                .disable();
        //
        // http.headers().addHeaderWriter(xFrameOptionsHeaderWriter());
        // 允许跨域请求
        http.cors();
        http.addFilterBefore(new CharacterEncodingFilter(Charsets.UTF_8.name(), true),
                SecurityContextPersistenceFilter.class);
        http.addFilterBefore(new ResolveRequestSystemContextPathFilter(), SecurityContextPersistenceFilter.class);// 添加解析请求的系统ID参数过滤器
        // AnonymousAuthenticationFilter
        if (Config.getSecurityJwtEnable() || Config.isBackendServer()) {// token验证
            http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
            http.addFilterBefore(new ResolveAnonymousUriTokenFilter(), JwtAuthenticationTokenFilter.class);
            http.addFilterBefore(new TrustedClientPostRequestFilter(), JwtAuthenticationTokenFilter.class);
            http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        } else {
            // Session并发管理
            http.sessionManagement().sessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }

        // 登出设置
        http.logout().logoutSuccessUrl("/index.jsp").logoutUrl("/j_spring_security_logout");

        // 授权设置
        http.exceptionHandling().accessDeniedHandler(new AccessDeniedHandler() {
            @Override
            public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                new Http403ForbiddenEntryPoint().commence(request, response, null);
            }
        });
        http.setSharedObject(RequestCache.class, new DefaultHttpSessionRequestCache());
        // 过滤器设置
        http.addFilterAt(concurrentSessionFilter(sessionRegistry), ConcurrentSessionFilter.class);
        http.addFilterAfter(redirectAuthorizedHomePageFilter, BasicAuthenticationFilter.class);
        http.addFilterBefore(superAdminLoginFilter(), UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(customFilterSecurityInterceptor(), FilterSecurityInterceptor.class);
        if (Config.getCasEnable()) {// cas 验证过滤器
            http.addFilterAt(casLoginAuthenticationFilter, CasAuthenticationFilter.class);
            http.addFilterBefore(logoutFilter(null), LogoutFilter.class);
            http.addFilterBefore(new SingleSignOutFilter(), CasAuthenticationFilter.class);
        } else {
            http.addFilterAfter(switchTenantUserFilter(securityConfiguration), FilterSecurityInterceptor.class);
            http.addFilterBefore(logoutFilter(securityConfiguration), LogoutFilter.class);
            http.addFilterBefore(internetUserLoginFilter(sessionAuthenticationStrategy),
                    UsernamePasswordAuthenticationFilter.class);
            http.addFilterBefore(loginFilter(sessionAuthenticationStrategy), UsernamePasswordAuthenticationFilter.class);

        }

        // 是否开启oauth2统一认证登录
        if (Context.isOauth2Enable()) {
            // 代理实例类过滤器：详见com.wellsoft.pt.security.oauth2.configuration.OAuth2ClientConfiguration
            http.addFilterAfter(
                    new DelegatingFilterProxy("oauth2ClientContextFilter", (WebApplicationContext) this
                            .getApplicationContext()), SecurityContextPersistenceFilter.class);
            // oauth2的密码授权模式处理
            http.addFilterBefore(new DelegatingFilterProxy("oAuth2ClientPasswordAuthenticationProcessingFilter",
                    (WebApplicationContext) this.getApplicationContext()), BasicAuthenticationFilter.class);
            // oauth2的授权码模式处理
            http.addFilterBefore(
                    new DelegatingFilterProxy("oAuth2AuthorizationCodeClientAuthenticationProcessingFilter",
                            (WebApplicationContext) this.getApplicationContext()), BasicAuthenticationFilter.class);
        }
    }

    class Http403ForbiddenEntryPoint implements AuthenticationEntryPoint {
        ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            objectMapper.writeValue(response.getWriter(), ImmutableMap.of("status", HttpServletResponse.SC_FORBIDDEN, "message", "Forbidden"));
        }
    }


    private List<String> nodeServerDispatchUrls() {
        return Lists.newArrayList("/api/theme/pack/getAllPublished",
                "/api/app/prod/allPublishedAnonUrls"
        );
    }

    public HeaderWriter xFrameOptionsHeaderWriter() {
        XFrameOptionsMode frameOptionsMode;
        String allowed = SystemParams.getValue("security.x.frame.allowed");
        String options = SystemParams.getValue("security.x.frame.options", "SAMEORIGIN");
        if (XFrameOptionsMode.DENY.getMode().equals(options)) {
            frameOptionsMode = XFrameOptionsMode.DENY;
        } else {
            frameOptionsMode = XFrameOptionsMode.SAMEORIGIN;
        }
        if (StringUtils.isNotBlank(allowed)) {
            return new AllowXFrameOptionsHeaderWriter(frameOptionsMode, Arrays.asList(allowed.split(";")));
        } else {
            return new RuleXFrameOptionsHeaderWriter(frameOptionsMode);
        }
    }

    @Bean
    public MultiTenantFilterInvocationSecurityMetadataSource securityMetadataSource() {
        return new MultiTenantFilterInvocationSecurityMetadataSource();
    }

    @Bean
    public AccessDecisionManager accessDecisionManager() {
        return new AccessDecisionManagerImpl();
    }

    @Bean
    public Filter customFilterSecurityInterceptor() {
        FilterSecurityInterceptorImpl interceptor = new FilterSecurityInterceptorImpl();
        interceptor.setAccessDecisionManager(accessDecisionManager);
        interceptor.setSecurityMetadataSource(securityMetadataSource);
        interceptor.setAuthenticationManager(authenticationManager);
        return interceptor;
    }


    @Bean
    public JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationTokenFilter();
    }

    @Bean
    public RedirectAuthorizedHomePageFilter redirectAuthorizedHomePageFilter() {
        return new RedirectAuthorizedHomePageFilter();
    }

    /**
     * 租户用户登录验证过滤器
     *
     * @param loginSuccessHandler
     * @param loginFailureHandler
     * @param authenticationManager
     * @param sessionAuthenticationStrategy
     * @return
     */
    private LoginAuthenticationProcessingFilter loginFilter(
            CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy) throws Exception {
        LoginAuthenticationProcessingFilter loginAuthenticationProcessingFilter = new LoginAuthenticationProcessingFilter();
        loginAuthenticationProcessingFilter
                .setAuthenticationSuccessHandler(new CustomSaveRequestAwareAuthenticationSuccessHandler(
                        securityConfiguration, authenticationSuccessHandler));
        loginAuthenticationProcessingFilter
                .setAuthenticationFailureHandler(new DomainSimpleUrlAuthenticationFailureHandler(securityConfiguration));
        loginAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        loginAuthenticationProcessingFilter.setFilterProcessesUrl("/passport/user/login/security_check");
        loginAuthenticationProcessingFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        return loginAuthenticationProcessingFilter;
    }

    /**
     * 超级管理员用户登录验证过滤器
     *
     * @return
     */
    private SuperAdminAuthenticationProcessingFilter superAdminLoginFilter() throws Exception {
        SuperAdminAuthenticationProcessingFilter superAdminAuthenticationProcessingFilter = new SuperAdminAuthenticationProcessingFilter();
        superAdminAuthenticationProcessingFilter
                .setAuthenticationSuccessHandler(new CustomSaveRequestAwareAuthenticationSuccessHandler(
                        securityConfiguration, authenticationSuccessHandler));
        superAdminAuthenticationProcessingFilter
                .setAuthenticationFailureHandler(new DomainSimpleUrlAuthenticationFailureHandler(securityConfiguration));
        superAdminAuthenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        superAdminAuthenticationProcessingFilter.setFilterProcessesUrl("/superadmin/login/security_check");
        return superAdminAuthenticationProcessingFilter;
    }

    private InternetUserAuthenticationProcessingFilter internetUserLoginFilter(
            CompositeSessionAuthenticationStrategy sessionAuthenticationStrategy) throws Exception {
        InternetUserAuthenticationProcessingFilter filter = new InternetUserAuthenticationProcessingFilter();
        filter.setAuthenticationSuccessHandler(new CustomSaveRequestAwareAuthenticationSuccessHandler(
                securityConfiguration, authenticationSuccessHandler));
        filter.setAuthenticationFailureHandler(new DomainSimpleUrlAuthenticationFailureHandler(securityConfiguration));
        filter.setAuthenticationManager(authenticationManager);
        filter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        filter.setFilterProcessesUrl("/passport/iuser/login/security_check");// 登录请求提交拦截的url
        return filter;
    }

    private Filter logoutFilter(SecurityConfiguration securityConfiguration) {
        if (Config.getCasEnable()) {
            SimpleUrlLogoutSuccessHandler urlLogoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
            urlLogoutSuccessHandler.setDefaultTargetUrl(securityCasUrl + "/logout?service=" + securityCasAppUrl
                    + "/j_spring_cas_security_check");
            CustomLogoutFilter customLogoutFilter = new CustomLogoutFilter(urlLogoutSuccessHandler,
                    new SecurityContextLogoutHandler());
            customLogoutFilter.setSuperAdminLogoutSuccessUrl(securityCasUrl + "/logout?service=" + securityCasAppUrl
                    + "/superadmin/login");
            customLogoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/j_spring_cas_security_logout"));
            return customLogoutFilter;
        }

        MutableLogoutFilter logoutFilter = new MutableLogoutFilter("/",
                new LogoutHandler[]{new SecurityContextLogoutHandler()});
        logoutFilter.setSecurityConfiguration(securityConfiguration);
        return logoutFilter;
    }

    @Bean
    public SwitchTenantUserFilter switchTenantUserFilter(SecurityConfiguration securityConfiguration) {
        SwitchTenantUserFilter switchTenantUserFilter = new SwitchTenantUserFilter();
        switchTenantUserFilter.setSuccessHandler(new CustomSaveRequestAwareAuthenticationSuccessHandler(
                securityConfiguration, authenticationSuccessHandler));
        switchTenantUserFilter
                .setFailureHandler(new DomainSimpleUrlAuthenticationFailureHandler(securityConfiguration));
        switchTenantUserFilter.setUserDetailsService(userDetailsService());
        return switchTenantUserFilter;
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new LoginAuthenticationSuccessHandler();
    }


    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter(SessionRegistry sessionRegistry) {
        ConcurrentSessionFilter concurrentSessionFilter = new ConcurrentSessionFilter(sessionRegistry,
                new SessionInformationExpiredStrategy() {
                    RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

                    @Override
                    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException,
                            ServletException {
                        Object principal = event.getSessionInformation().getPrincipal();
                        if (principal != null && principal instanceof UserDetails) {
                            UserDetails source = (UserDetails) principal;
                            TenantContextHolder.setLoginType(source.getLoginType());
                            org.springframework.security.core.userdetails.UserDetails userDetails = userDetailsService()
                                    .loadUserByUsername(source.getLoginName());
                            if (!userDetails.isAccountNonExpired()) {// 用户失效
                                redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(),
                                        userExpiredRedirectUrl);
                                return;
                            }
                        }
                        redirectStrategy.sendRedirect(event.getRequest(), event.getResponse(),
                                sessionExpiredRedirectUrl);
                    }
                });
        return concurrentSessionFilter;
    }

    private ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(securityCasAppUrl + "/j_spring_cas_security_check");
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public AuthenticationEntryPoint loginUrlAuthenticationEntryPoint(SecurityConfiguration securityConfiguration) {
        if (Config.getCasEnable()) {
            // 启用cas登录验证
            WellCasAuthenticationEntryPoint casAuthenticationEntryPoint = new WellCasAuthenticationEntryPoint();
            casAuthenticationEntryPoint.setLoginUrl(securityCasUrl);
            casAuthenticationEntryPoint.setSuperAdminLoginUrl(securityCasUrl + "/login?tenantId="
                    + Config.getValue("multi.tenancy.common.datasource"));
            casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
            return casAuthenticationEntryPoint;
        }

        if (Config.isBackendServer() || Config.getSecurityJwtEnable()) {
            return new Http403ForbiddenEntryPoint();
        }
        // 默认的表单登录验证
        CustomLoginUrlAuthenticationEntryPoint loginUrlAuthenticationEntryPoint = new CustomLoginUrlAuthenticationEntryPoint(
                "/tenant/" + tenantId + "?error=1");
        loginUrlAuthenticationEntryPoint.setSecurityConfiguration(securityConfiguration);
        return loginUrlAuthenticationEntryPoint;
    }

    @Bean
    public CasLoginAuthenticationFilter casLoginAuthenticationFilter() {
        CasLoginAuthenticationFilter casLoginAuthenticationFilter = new CasLoginAuthenticationFilter();
        casLoginAuthenticationFilter
                .setAuthenticationSuccessHandler(new CustomSaveRequestAwareAuthenticationSuccessHandler(
                        new LoginAuthenticationSuccessHandler(), "/passport/user/login/success"));
        casLoginAuthenticationFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler(
                "/tenant/" + tenantId + "?error=1"));
        casLoginAuthenticationFilter.setAuthenticationManager(authenticationManager);
        casLoginAuthenticationFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        return casLoginAuthenticationFilter;
    }

    @Bean
    public SecurityConfiguration securityConfiguration(
            @Qualifier("defaultAuthenticationUrlConfiguration") AuthenticationUrlConfiguration defaultAuthenticationUrlConfiguration,
            @Qualifier("superAdminAuthenticationUrlConfiguration") AuthenticationUrlConfiguration superAdminAuthenticationUrlConfiguration,
            @Qualifier("restfulAuthenticationUrlConfiguration") AuthenticationUrlConfiguration restfulAuthenticationUrlConfiguration,
            @Qualifier("internetUserAuthenticationUrlConfiguration") AuthenticationUrlConfiguration inernetUserAuthenticationUrlConfiguration) {
        SecurityConfiguration securityConfiguration = new SecurityConfiguration();
        securityConfiguration.setDefaultAuthenticationUrlConfiguration(defaultAuthenticationUrlConfiguration);
        securityConfiguration.getAuthenticationUrlMappings().put(LoginType.USER, defaultAuthenticationUrlConfiguration);
        securityConfiguration.getAuthenticationUrlMappings().put(LoginType.SUPER_ADMIN,
                superAdminAuthenticationUrlConfiguration);
        securityConfiguration.getAuthenticationUrlMappings().put(LoginType.RESTful,
                restfulAuthenticationUrlConfiguration);
        securityConfiguration.getAuthenticationUrlMappings().put(LoginType.INTERNET_USER,
                inernetUserAuthenticationUrlConfiguration);
        return securityConfiguration;
    }

    /**
     * 默认登录登出验证相关URL
     *
     * @return
     */
    @Bean
    public AuthenticationUrlConfiguration defaultAuthenticationUrlConfiguration() {
        AuthenticationUrlConfiguration authenticationUrlConfiguration = new AuthenticationUrlConfiguration();
        authenticationUrlConfiguration.setAuthenticationSuccessUrl("/passport/user/login/success");
        authenticationUrlConfiguration.setAuthenticationFailureUrl("/tenant/" + tenantId + "?error=1");
        authenticationUrlConfiguration.setLogoutFilterProcessesUrl("/j_spring_security_logout");
        authenticationUrlConfiguration.setLogoutSuccessUrl("/tenant/" + tenantId);
        authenticationUrlConfiguration.setMobileLogoutSuccessUrl("/mobile/mui/login.jsp");
        authenticationUrlConfiguration.setMobileAuthenticationFailureUrl("/mobile/mui/login.jsp");
        return authenticationUrlConfiguration;
    }

    @Bean
    public AuthenticationUrlConfiguration internetUserAuthenticationUrlConfiguration() {
        AuthenticationUrlConfiguration authenticationUrlConfiguration = new AuthenticationUrlConfiguration();
        authenticationUrlConfiguration.setAuthenticationSuccessUrl(Config.getValue(
                "spring.security.internetUserLoginSuccessUrl", "/passport/iuser/login/success"));
        authenticationUrlConfiguration.setAuthenticationFailureUrl("/iuser/login?error=1");
        authenticationUrlConfiguration.setLogoutFilterProcessesUrl("/j_spring_security_logout");
        authenticationUrlConfiguration.setLogoutSuccessUrl("/iuser/login");
        return authenticationUrlConfiguration;
    }

    /**
     * 超级管理员登录登出验证相关URL
     *
     * @return
     */
    @Bean
    public AuthenticationUrlConfiguration superAdminAuthenticationUrlConfiguration() {
        AuthenticationUrlConfiguration authenticationUrlConfiguration = new AuthenticationUrlConfiguration();
        authenticationUrlConfiguration.setAuthenticationSuccessUrl("/superadmin/login/success");
        authenticationUrlConfiguration.setAuthenticationFailureUrl("/superadmin/login?error=1");
        authenticationUrlConfiguration.setLogoutFilterProcessesUrl("/j_spring_security_logout");
        authenticationUrlConfiguration.setLogoutSuccessUrl("/superadmin/login");
        return authenticationUrlConfiguration;

    }

    /**
     * restful登录登出验证相关URL
     *
     * @return
     */
    @Bean
    public AuthenticationUrlConfiguration restfulAuthenticationUrlConfiguration() {
        AuthenticationUrlConfiguration authenticationUrlConfiguration = new AuthenticationUrlConfiguration();
        authenticationUrlConfiguration.setLogoutFilterProcessesUrl("/j_spring_security_logout");
        authenticationUrlConfiguration.setLogoutSuccessUrl("/web/app/pt-guest.html");
        authenticationUrlConfiguration.setMobileLogoutSuccessUrl("/mobile/mui/login.jsp");
        return authenticationUrlConfiguration;

    }

    @Bean
    public AuthenticationProvider defaultAuthenticationProvider() {
        if (Config.getCasEnable()) {
            CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
            GrantedAuthorityFromAssertionAttributesUserDetailsService casUserDetailsService = new GrantedAuthorityFromAssertionAttributesUserDetailsService();
            casUserDetailsService.setUserDetailsService(userDetailsService());
            casAuthenticationProvider.setAuthenticationUserDetailsService(casUserDetailsService);
            casAuthenticationProvider.setServiceProperties(serviceProperties());
            casAuthenticationProvider.setTicketValidator(new Cas20ServiceTicketValidator(securityCasUrl));
            casAuthenticationProvider.setKey("cas");
        }

        ReflectionSaltSource reflectionSaltSource = new ReflectionSaltSource();
        reflectionSaltSource.setUserPropertyToUse("loginNameLowerCase");
        CustomDaoAuthenticationProvider daoAuthenticationProvider = new CustomDaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(new Md5PasswordEncoder());
        daoAuthenticationProvider.setSaltSource(reflectionSaltSource);
        return daoAuthenticationProvider;
    }

    @Override
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private Set<String> defaultAnnoymousUrls() {
        Set<String> urls = Sets.newLinkedHashSet();
        // 开发环境生成token
        urls.add("/api/security/createToken");
        urls.add("/checkToken");
        urls.add("/web/login/config/getLoginPageConfig");
        urls.add("/web/login/config/appLoginPageConfigSetting");
        urls.add("/web/login/config/loginPageSettings");
        urls.add("/web/login/config/getAllLoginPageSettings");
        urls.add("/web/login/config/unitLoginPageSetting");
        urls.add("/wellSocket/**");
        urls.add("/wopi/files/**");
        urls.add("/repository/file/mongo/upgrade");
        urls.add("/repository/file/mongo/download4ocx");
        urls.add("/repository/file/mongo/downAllFiles4ocx");
        urls.add("/repository/file/mongo/savefiles");

        urls.add("/login/1");
        urls.add("/login/3");
        urls.add("/loginByIdNumberToken");
        urls.add("/loginByJwt");
        urls.add("/security/aid/createImageVerifyCode");
        urls.add("/common/verification/graphic/verify/code/show");
        urls.add("/security/aid/sendMobileSms");
        urls.add("/loginByMobileSms");
        urls.add("/basicdata/system/param/get");
        urls.add("/common/validation/metadata");
        urls.add("/api/app/theme/listByApplyToWithUpdatedTheme");
        urls.add("/api/app/prod/version/getDefaultLoginDef");

        // swagger2
        urls.add("/v2/api-docs");
        urls.add("/doc.html");
        urls.add("/swagger-resources/**");
        urls.add("/webjars/bycdao-ui/**");

        // ureport
        urls.add("/ureport/res/**");
        // dingtalk
        urls.add("/mobile/**");
        // 手册下载
        urls.add("/resfacade/share/**");
        urls.add("/resfacade/mobile/android");
        urls.add("/fileListConfig/list");// 获取列表附件配置

        urls.add("/api/theme/pack/getAllPublished");// 获取发布的主题包
        urls.add("/api/app/prod/allPublishedAnonUrls");// 获取产品所有已发布的匿名地址

        urls.add("/api/system/getEnableTenantSystemLoginPagePolicy");// 获取系统登录配置
        urls.add("/api/system/getAllEnabledLoginPage");// 获取系统登录配置
        urls.add("/api/app/codeI18n/getAllLocales");


        if (StringUtils.isNotBlank(annoymousUrls)) {
            urls.addAll(Sets.newHashSet(annoymousUrls.split(",|;")));
        }

        if (CollectionUtils.isNotEmpty(resourceDataSources)) {
            for (ResourceDataSource resourceDataSource : resourceDataSources) {
                List<String> anonymousResources = resourceDataSource.getAnonymousResources();
                for (String anonymousResource : anonymousResources) {
                    if (StringUtils.startsWith(anonymousResource, Separator.SLASH.getValue())) {
                        urls.add(anonymousResource);
                    }
                }
            }
        }

        return urls;
    }

    @Bean
    public CompositeUserDetailsCache compositeUserDetailsCache() {
        CompositeUserDetailsCache compositeUserDetailsCache = new CompositeUserDetailsCache();
        compositeUserDetailsCache.setCacheManager(cacheManager);
        return compositeUserDetailsCache;
    }

    /**
     * 注册springSecurityFilterChain
     */
    public static class SecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {

    }

}
