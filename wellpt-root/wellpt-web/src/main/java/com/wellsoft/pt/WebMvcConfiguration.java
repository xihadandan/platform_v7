package com.wellsoft.pt;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.profile.OnNotDevProfileCondition;
import com.wellsoft.context.web.controller.CheckResourceJstlView;
import com.wellsoft.context.web.converter.MappingCodehausJacksonHttpMessageConverter;
import com.wellsoft.context.web.converter.UTF8StringHttpMessageConverter;
import com.wellsoft.context.web.process.ProcessAspect;
import com.wellsoft.pt.app.web.WebAppFacadeController;
import com.wellsoft.pt.app.web.WebResourcesFacadeController;
import com.wellsoft.pt.app.web.interceptor.WebAppFacadeInterceptor;
import com.wellsoft.pt.dms.web.DmsDataServicesController;
import com.wellsoft.pt.jpa.aop.HibernateDaoAspect;
import com.wellsoft.pt.jpa.generator.CustomAnnotationBeanNameGenerator;
import com.wellsoft.pt.security.web.interceptor.SecurityInterceptor;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.module.SimpleModule;
import org.codehaus.jackson.map.module.SimpleSerializers;
import org.codehaus.jackson.map.ser.std.ToStringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.DelegatingMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.feed.AtomFeedHttpMessageConverter;
import org.springframework.http.converter.feed.RssChannelHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;

/**
 * Description: spring mvc配置
 *
 * @author chenq
 * @date 2019/11/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/9    chenq		2019/11/9		Create
 * </pre>
 */
@EnableWebMvc
@Configuration
@Conditional(OnNotDevProfileCondition.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackages = "com.wellsoft,springfox", useDefaultFilters = false, nameGenerator = CustomAnnotationBeanNameGenerator.class, includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Controller.class)})
//@PropertySource(value = {"classpath:/i18n/basenames.properties", "classpath:system-application-mvc.properties"})
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {

    @Value("#{'${basenames}'.split(',')}")
    private String[] basenames;

    @Value("#{new Boolean('${spring.dwr.debug:false}')}")
    private Boolean enableDwrDebug;

    @Value("#{new Boolean('${spring.dwr.allowScriptTagRemoting:true}')}")
    private Boolean allowScriptTagRemoting;

    @Value("#{new Boolean('${spring.dwr.activeReverseAjaxEnabled:true}')}")
    private Boolean activeReverseAjaxEnabled;

    @Value("#{new Boolean('${spring.dwr.crossDomainSessionSecurity:false}')}")
    private Boolean crossDomainSessionSecurity;

    @Value("${spring.cors.url:}")
    private String corsUrl;

    @Value("${spring.cors.header:}")
    private String corsHeader;


    /**
     * 静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
        registry.addResourceHandler("/static/**").addResourceLocations("/static/", "classpath:/META-INF/resources/static/");
        registry.addResourceHandler("/i18n/**").addResourceLocations("classpath:/i18n/");
        registry.addResourceHandler("/mobile/**").addResourceLocations("/mobile/");
        registry.addResourceHandler("/pt/workflow/js/**").addResourceLocations("/pt/workflow/js/");
        registry.addResourceHandler("/pt/workflow/css/**").addResourceLocations("/pt/workflow/css/");
        registry.addResourceHandler("/pt/workflow/images/**").addResourceLocations("/pt/workflow/images/");
        registry.addResourceHandler("/pt/workflow/help/**").addResourceLocations("/pt/workflow/help/");
        registry.addResourceHandler("/ureport/res/**").addResourceLocations("classpath:/asserts/");
        registry.addResourceHandler("/html/**").addResourceLocations(
                "classpath:/META-INF/resources/WEB-INF/views/html/");
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    /**
     * 视图资源解析
     *
     * @param registry
     */
    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/pt/", ".jsp").viewClass(CheckResourceJstlView.class);
        registry.jsp("/app/", ".jsp").viewClass(CheckResourceJstlView.class);
        registry.jsp("/WEB-INF/views/", ".jsp").viewClass(CheckResourceJstlView.class);
        registry.jsp("/", ".jsp").viewClass(CheckResourceJstlView.class);
        registry.jsp("/WEB-INF/views/", ".html").viewClass(CheckResourceJstlView.class);
        registry.freeMarker().prefix("/app/");
        registry.freeMarker().prefix("/WEB-INF/views/");
    }

    /**
     * 配置消息内容转换器
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.addAll(messageConverters());
    }

    /**
     * freemarker配置
     *
     * @return
     */
    @Bean
    public FreeMarkerConfigurer freemarkerConfig() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setPreferFileSystemAccess(false);
        freeMarkerConfigurer.setTemplateLoaderPaths("/", "classpath:/META-INF/resources/");
        Properties properties = new Properties();
        properties.setProperty("template_update_delay", "0");
        properties.setProperty("defaultEncoding", Charsets.UTF_8.name());
        properties.setProperty("number_format", "0.##########");
        properties.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        properties.setProperty("classic_compatible", "true");
        properties.setProperty("template_exception_handler", "ignore");
        properties.setProperty("auto_import", "/template/common.ftl as al");
        freeMarkerConfigurer.setFreemarkerSettings(properties);
        return freeMarkerConfigurer;
    }

    /**
     * 增加请求拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor()).addPathPatterns(
				/*"/json/data/services",
				"/common/jqgrid/query",*/"/passport/admin/main", "/superadmin/login/success");
    }

    /**
     * 文件上传解析
     *
     * @return
     */
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding(Charsets.UTF_8.name());
        multipartResolver.setMaxUploadSize(Config.getFileMaxUploadSize());
        return multipartResolver;
    }

    @Bean
    public WebResourcesFacadeController webResourcesFacadeController() {
        return new WebResourcesFacadeController();
    }

    @Bean
    public SimpleUrlHandlerMapping webResSimpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setAlwaysUseFullPath(true);
        Properties prop = new Properties();
        prop.put("/web/res/**/**", "webResourcesFacadeController");
        simpleUrlHandlerMapping.setMappings(prop);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public WebAppFacadeController webAppFacadeController() {
        return new WebAppFacadeController();
    }

    @Bean
    public SimpleUrlHandlerMapping webAppSimpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setAlwaysUseFullPath(true);
        simpleUrlHandlerMapping.setOrder(1);
        Properties prop = new Properties();
        prop.put("/web/app/**/**", "webAppFacadeController");
        simpleUrlHandlerMapping.setInterceptors(new WebAppFacadeInterceptor());
        simpleUrlHandlerMapping.setMappings(prop);
        return simpleUrlHandlerMapping;
    }

    @Bean
    public DmsDataServicesController dmsDataServicesController() {
        return new DmsDataServicesController();
    }

    @Bean
    public SimpleUrlHandlerMapping dmsDataSimpleUrlHandlerMapping() {
        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
        simpleUrlHandlerMapping.setAlwaysUseFullPath(true);
        simpleUrlHandlerMapping.setOrder(2);
        Properties prop = new Properties();
        prop.put("/dms/data/services", "dmsDataServicesController");
        simpleUrlHandlerMapping.setMappings(prop);
        return simpleUrlHandlerMapping;
    }

//    @Override
//    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
//        SimpleMappingExceptionResolver errorSimpleMappingExceptionResolver = new SimpleMappingExceptionResolver();
//        errorSimpleMappingExceptionResolver.setDefaultErrorView("/common/" + HttpStatus.INTERNAL_SERVER_ERROR.value());
//        errorSimpleMappingExceptionResolver.setDefaultStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        exceptionResolvers.add(errorSimpleMappingExceptionResolver);
//    }

//    @Bean
//    public DwrController dwrController() {
//        DwrController dwrController = new DwrController();
//        dwrController.setDebug(enableDwrDebug);
//        Map<String, String> confParams = Maps.newHashMap();
//        confParams.put("activeReverseAjaxEnabled", activeReverseAjaxEnabled.toString());
//        confParams.put("allowScriptTagRemoting", allowScriptTagRemoting.toString());
//        confParams.put("crossDomainSessionSecurity", crossDomainSessionSecurity.toString());
//        dwrController.setConfigParams(confParams);
//        dwrController.setConfigurators(Collections.EMPTY_LIST);
//        return dwrController;
//    }

//    @Bean
//    public SimpleUrlHandlerMapping dwrSimpleUrlHandlerMapping() {
//        SimpleUrlHandlerMapping simpleUrlHandlerMapping = new SimpleUrlHandlerMapping();
//        simpleUrlHandlerMapping.setAlwaysUseFullPath(true);
//        Properties prop = new Properties();
//        prop.put("/dwr/**/*", "dwrController");
//        simpleUrlHandlerMapping.setMappings(prop);
//        return simpleUrlHandlerMapping;
//    }

    @Bean
    public HibernateDaoAspect hibernateDaoAspect() {
        return new HibernateDaoAspect();
    }

    @Bean
    public ProcessAspect processAspect() {
        return new ProcessAspect();
    }

    @Bean
    public ResourceBundleMessageSource normalMessageSource() {
        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasenames(basenames);
        resourceBundleMessageSource.setUseCodeAsDefaultMessage(true);
        return resourceBundleMessageSource;
    }

    @Bean
    public DelegatingMessageSource messageSource(ResourceBundleMessageSource source) {
        DelegatingMessageSource delegatingMessageSource = new DelegatingMessageSource();
        delegatingMessageSource.setParentMessageSource(source);
        return delegatingMessageSource;
    }

    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver();
    }


    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(messageConverters());
        return restTemplate;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }


    private List<HttpMessageConverter<?>> messageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(new UTF8StringHttpMessageConverter());
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(new Jaxb2RootElementHttpMessageConverter());
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

        SimpleSerializers simpleSerializers = new SimpleSerializers();
        simpleSerializers.addSerializer(Long.class, new ToStringSerializer());
        SimpleModule customModule = new SimpleModule("wellptCustomModule", Version.unknownVersion());
        customModule.addSerializer(Long.class, new ToStringSerializer());
        customModule.addSerializer(BigDecimal.class, new ToStringSerializer());
        objectMapper.registerModule(customModule);
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.enable(DeserializationConfig.Feature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        MappingCodehausJacksonHttpMessageConverter jackson2HttpMessageConverter = new MappingCodehausJacksonHttpMessageConverter(
                objectMapper);
        jackson2HttpMessageConverter.setSupportedMediaTypes(Lists.newArrayList(MediaType.APPLICATION_JSON_UTF8,
                MediaType.TEXT_HTML, MediaType.APPLICATION_JSON));
        converters.add(jackson2HttpMessageConverter);
        converters.add(new AtomFeedHttpMessageConverter());
        converters.add(new RssChannelHttpMessageConverter());
        converters.add(new MappingJackson2HttpMessageConverter());
        return converters;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        Set<String> defaultUrls = Sets.newHashSet("/dms/file/**", "/common/iexport/service/**", "/repository/file/mongo/**", "/api/repository/file/mongo/**", "/office/wps/**", "/newFileFromWPS/**", "/wopi/files/**");
        Set<String> defaultHeader = Sets.newHashSet("Authorization", "Authorization-JWT",
                "content-range", "content-disposition", "x-csrf-token", "x-requested-with");
        if (StringUtils.isNotBlank(corsHeader)) {
            defaultHeader.addAll(Arrays.asList(corsHeader.split(",|;")));
        }
        if (StringUtils.isNotBlank(corsUrl)) {
            defaultUrls.addAll(Arrays.asList(corsUrl.split(",|;")));
        }
        String[] header = defaultHeader.toArray(new String[]{});
        for (String u : defaultUrls) {
            registry.addMapping(u).allowCredentials(true).allowedOrigins("*").allowedHeaders(header).allowedMethods(HttpMethod.GET.name(), HttpMethod.POST.name());
        }
    }


    @Bean("sharedGuavaPool")
    public ListeningExecutorService sharedGuavaPool() {
        int cpuCores = Runtime.getRuntime().availableProcessors();
        // I/O 密集型：设为 2 * CPU 核心数
        int poolSize = Math.max(6, cpuCores * 2);
        return MoreExecutors.listeningDecorator(
                Executors.newFixedThreadPool(poolSize, r -> new Thread(r, "shared-guava-worker"))
        );
    }
}
