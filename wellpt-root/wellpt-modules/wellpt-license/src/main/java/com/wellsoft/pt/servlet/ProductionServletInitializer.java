package com.wellsoft.pt.servlet;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.Context;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.NetUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.annotation.ServletSecurity;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.servlet.CryptoClassLoader.cdbLocationPattern;
import static com.wellsoft.pt.servlet.CryptoClassLoader.mdbLocationPattern;

/**
 * Description: web应用servlet初始化，提供产品版使用，需要验证授权码
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
@Order(1)
public class ProductionServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ResourcePatternResolver resourcePatternResolver = new ServletContextResourcePatternResolver(servletContext);
        try {
            Resource[] mdbResources = resourcePatternResolver.getResources(mdbLocationPattern);
            Resource[] cdbResources = resourcePatternResolver.getResources(cdbLocationPattern);
            if (mdbResources.length > 0 && cdbResources.length > 0) {
                ClassLoader classLoader = new CryptoClassLoader(Thread.currentThread().getContextClassLoader(), mdbResources, cdbResources);
                Thread.currentThread().setContextClassLoader(classLoader);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        try {
            // 校验证书，无效会中止tomcat进程
            Thread.currentThread().getContextClassLoader().loadClass("com.wellsoft.pt.servlet.License").newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStartup(servletContext);
    }

    @Override
    protected void registerContextLoaderListener(ServletContext servletContext) {
        super.registerContextLoaderListener(servletContext);
        String contextPath = StringUtils.defaultIfBlank(servletContext.getContextPath(), "ROOT");
        contextPath = contextPath.substring(contextPath.lastIndexOf("/") + 1);
        System.setProperty("server.contextPath", contextPath);
        System.setProperty("server.port", NetUtils.getServerPort() + "");//设置服务应用端口到系统属性

    }

    @Override
    protected void registerDispatcherServlet(ServletContext servletContext) {
        super.registerDispatcherServlet(servletContext);
        try {
            customizeFilters(servletContext);

            addListener(servletContext,
                    org.springframework.web.context.request.RequestContextListener.class,
                    org.springframework.security.web.session.HttpSessionEventPublisher.class,
                    org.springframework.web.util.IntrospectorCleanupListener.class,
                    Class.forName("com.wellsoft.pt.jpa.hibernate.SessionFactoryBasedMultiTenancyListener"),
                    org.jasig.cas.client.session.SingleSignOutHttpSessionListener.class,
                    Class.forName("com.wellsoft.pt.cache.SessionCacheListener"), DefaultSessionListener.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        //开发环境下调试OAuth2避免session异常
        if (Context.isDebug() && Context.isOauth2Enable()) {
            servletContext.getSessionCookieConfig().setName("JOAUTH2CLIENTSESSIONID");
        }

        servletContext.getSessionCookieConfig().setMaxAge(
                Config.getSessionTimeout());//session 过期时间（秒)

    }

    private void customizeFilters(ServletContext servletContext) throws Exception {
        if (!Config.getAppEnv().equalsIgnoreCase("dev")) {
            //解析混淆js的请求路径过滤器
            addFilters(servletContext, "resourceFilter",
                    Class.forName("com.wellsoft.pt.common.web.ResourceHttpRequestHandlerFilter"),
                    "/resources/*", arguments2Map("location", "/resources/"));
        }

        addFilters(servletContext, "dmsDataServicesControllerFilter",
                Class.forName("com.wellsoft.pt.dms.core.web.interceptor.DmsDataServiceControlFilter"),
                "/dms/data/services/*", null);

        //注册项目使用的其他servlet
        addServlet(servletContext, "ocxFileupload",
                Class.forName("com.wellsoft.pt.repository.web.OcxFileUpload"), new String[]{"/ocxFileupload"},
                0, null);

        addServlet(servletContext, "WebServiceFacadeServlet",
                Class.forName("com.wellsoft.pt.integration.support.WebServiceFacadeServlet"),
                new String[]{"/wsfacade/*"},
                1, null);

        addServlet(servletContext, "CXFServlet", org.apache.cxf.transport.servlet.CXFServlet.class,
                new String[]{"/webservices/*"}, null, null);

    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        try {
            return new Class[]{Class.forName("com.wellsoft.pt.RootContextConfiguration"), Class.forName("com.wellsoft.pt.profiles.dev.RootContextDevConfiguration")};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        try {
            return new Class[]{Class.forName("com.wellsoft.pt.WebMvcConfiguration"), Class.forName("com.wellsoft.pt.profiles.dev.WebMvcDevConfiguration")};
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{
                "/"
                , "/dwr/*"  // 支持dwr
        };
    }


    protected void addListener(ServletContext servletContext,
                               Class... listenerClasses) {
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
                              Class filterClass, String urlMapping,
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
                              Class servletClass, String[] urlMapping,
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
