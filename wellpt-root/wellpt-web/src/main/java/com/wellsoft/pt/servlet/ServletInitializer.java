package com.wellsoft.pt.servlet;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.Context;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.NetUtils;
import com.wellsoft.context.util.ObfuscatedString;
import com.wellsoft.pt.RootContextConfiguration;
import com.wellsoft.pt.WebMvcConfiguration;
import com.wellsoft.pt.profiles.dev.RootContextDevConfiguration;
import com.wellsoft.pt.profiles.dev.WebMvcDevConfiguration;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.*;

/**
 * Description: web应用servlet初始化
 *
 * @author chenq
 * @date 2019/11/11
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/11    chenq		2019/11/11		Create
 * </pre>
 */
public class ServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    protected void registerContextLoaderListener(ServletContext servletContext) {
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        String contextPath = StringUtils.defaultIfBlank(servletContext.getContextPath(), "ROOT");
        contextPath = contextPath.substring(contextPath.lastIndexOf("/") + 1);
        System.setProperty("server.contextPath", contextPath);
        System.setProperty("server.port", NetUtils.getServerPort() + "");//设置服务应用端口到系统属性
        super.registerContextLoaderListener(servletContext);
    }

    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
        super.registerDispatcherServlet(servletContext);

        customizeFilters(servletContext);

        addListener(servletContext,
                org.springframework.web.context.request.RequestContextListener.class,
                org.springframework.security.web.session.HttpSessionEventPublisher.class,
                org.springframework.web.util.IntrospectorCleanupListener.class,
                com.wellsoft.pt.jpa.hibernate.SessionFactoryBasedMultiTenancyListener.class,
                org.jasig.cas.client.session.SingleSignOutHttpSessionListener.class,
                com.wellsoft.pt.cache.SessionCacheListener.class, DefaultSessionListener.class);

        //开发环境下调试OAuth2避免session异常
        if (Context.isDebug() && Context.isOauth2Enable()) {
            servletContext.getSessionCookieConfig().setName(System.getProperty("sessionCookieName", "JOAUTH2CLIENTSESSIONID"));
        }
        String maxAge = Config.getValue("session.cookie.maxAge");
        if (StringUtils.isBlank(maxAge) || "session".equalsIgnoreCase(maxAge) || !NumberUtils.isDigits(maxAge)) {
            return;
        }
        servletContext.getSessionCookieConfig().setMaxAge(Integer.parseInt(maxAge));//cookie 过期时间（秒)

    }

    private void customizeFilters(ServletContext servletContext) {
        if (!Config.getAppEnv().equalsIgnoreCase("dev")) {
            //解析混淆js的请求路径过滤器
            addFilters(servletContext, "resourceFilter",
                    com.wellsoft.pt.common.web.ResourceHttpRequestHandlerFilter.class,
                    "/resources/*", arguments2Map("location", "/resources/"));
        }

        addFilters(servletContext, "dmsDataServicesControllerFilter",
                com.wellsoft.pt.dms.core.web.interceptor.DmsDataServiceControlFilter.class,
                "/dms/data/services/*", null);

        addFilters(servletContext, "DruidStatViewFilter",
                com.alibaba.druid.support.http.StatViewFilter.class, "/druid/*", null);

        //注册项目使用的其他servlet
        addServlet(servletContext, "ocxFileupload",
                com.wellsoft.pt.repository.web.OcxFileUpload.class, new String[]{"/ocxFileupload"},
                0, null);

        addServlet(servletContext, "WebServiceFacadeServlet",
                com.wellsoft.pt.integration.support.WebServiceFacadeServlet.class,
                new String[]{"/wsfacade/*"},
                1, null);

        addServlet(servletContext, "CXFServlet", org.apache.cxf.transport.servlet.CXFServlet.class,
                new String[]{"/webservices/*"}, null, null);

    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{RootContextConfiguration.class, RootContextDevConfiguration.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{WebMvcConfiguration.class, WebMvcDevConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{
                "/"
                , "/dwr/*"  // 支持dwr
        };
    }


    protected void addListener(ServletContext servletContext,
                               Class<? extends EventListener>... listenerClasses) {
        for (Class clazz : listenerClasses) {
            servletContext.addListener(clazz);
        }
    }

    protected void addFilters(ServletContext servletContext, String filterName,
                              Filter inFilter, String urlMapping) {
        try {
            FilterRegistration.Dynamic filter = servletContext.addFilter(filterName,
                    inFilter);
            filter.addMappingForUrlPatterns(getDispatcherTypes(), isAsyncSupported(),
                    urlMapping);
        } catch (Exception e) {
            log.error("创建过滤器异常：{}", Throwables.getStackTraceAsString(e));
        }

    }


    protected void addFilters(ServletContext servletContext, String filterName,
                              Class<? extends Filter> filterClass, String urlMapping,
                              Map<String, String> initParams) {
        try {
            FilterRegistration.Dynamic filter = servletContext.addFilter(filterName,
                    filterClass);
            if (!MapUtils.isEmpty(initParams)) {
                filter.setInitParameters(initParams);
            }
            filter.addMappingForUrlPatterns(getDispatcherTypes(), isAsyncSupported(),
                    urlMapping);
        } catch (Exception e) {
            log.error("创建过滤器异常：{}", Throwables.getStackTraceAsString(e));
        }

    }

    protected void addServlet(ServletContext servletContext, String servletName,
                              Class<? extends HttpServlet> servletClass, String[] urlMapping,
                              Integer loadOnStartup, Map<String, String> initParams) {

        ServletRegistration.Dynamic servlet = servletContext.addServlet(
                StringUtils.isBlank(servletName) ? servletClass.getSimpleName() : servletName,
                servletClass);
        if (loadOnStartup != null) {
            servlet.setLoadOnStartup(loadOnStartup);
        }
        if (urlMapping != null) {
            servlet.addMapping(urlMapping);
        }
        if (!MapUtils.isEmpty(initParams)) {
            servlet.setInitParameters(initParams);
        }
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{
                new CharacterEncodingFilter(Charsets.UTF_8.name(), true)};
    }

    private EnumSet<DispatcherType> getDispatcherTypes() {
        return (isAsyncSupported() ?
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE,
                        DispatcherType.ASYNC) :
                EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE));
    }

    private Map<String, String> arguments2Map(String... arr) {
        Map<String, String> params = Maps.newHashMap();
        if (arr.length % 2 != 0) {
            throw new IllegalArgumentException("参数不成对！");
        }
        for (int i = 0; i < arr.length; i++) {
            if (i % 2 == 1) {
                params.put(arr[i - 1], arr[i]);
            }
        }
        return params;
    }

    protected void securityConstraint(ServletRegistration.Dynamic dispatchServletDynamic) {
        HttpConstraintElement httpConstraintElement = new HttpConstraintElement(
                ServletSecurity.TransportGuarantee.CONFIDENTIAL);
        List<HttpMethodConstraintElement> methodConstraintElements = Lists.newArrayList();
        HttpMethod[] httpMethods = HttpMethod.values();
        for (HttpMethod hm : httpMethods) {
            methodConstraintElements.add(new HttpMethodConstraintElement(
                    hm.name()));
        }
        ServletSecurityElement servletSecurityElement = new ServletSecurityElement(
                httpConstraintElement, methodConstraintElements);

        dispatchServletDynamic.setServletSecurity(servletSecurityElement);

    }


    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        securityConstraint(registration);
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        super.onStartup(servletContext);

        try {
            // 校验证书，无效会中止tomcat进程
            String cls = new ObfuscatedString(new long[]{0xF64D5E69E011FF52L, 0xEC4D7BF298CB7FFAL, 0x46364724E6BC41B6L, 0xF503891C653BE870L, 0x50FB552D61AD3172L, 0x2AA084D294071606L}).toString();
            Thread.currentThread().getContextClassLoader().loadClass(cls).newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class DefaultSessionListener implements HttpSessionListener {

        @Override
        public void sessionCreated(HttpSessionEvent httpSessionEvent) {
            if (httpSessionEvent.getSession() != null) {
                httpSessionEvent.getSession().setMaxInactiveInterval(Config.getSessionTimeout());// 设置无活动的session有效时间
            }
        }

        @Override
        public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

        }
    }
}
