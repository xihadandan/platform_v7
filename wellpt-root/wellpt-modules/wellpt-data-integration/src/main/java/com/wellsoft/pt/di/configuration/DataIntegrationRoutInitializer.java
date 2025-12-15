package com.wellsoft.pt.di.configuration;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.pt.di.callback.AbstractDiCallback;
import com.wellsoft.pt.di.component.AbstractEndpoint;
import com.wellsoft.pt.di.constant.DiConstant;
import com.wellsoft.pt.di.entity.DiConfigEntity;
import com.wellsoft.pt.di.entity.DiDataConsumerEndpointEntity;
import com.wellsoft.pt.di.entity.DiDataProcessorEntity;
import com.wellsoft.pt.di.entity.DiDataProducerEndpointEntity;
import com.wellsoft.pt.di.processor.AbstractDIProcessor;
import com.wellsoft.pt.di.processor.DataIntegrationBeginProcessor;
import com.wellsoft.pt.di.processor.ExceptionOccuredProcessor;
import com.wellsoft.pt.di.processor.FailOccuredProcessor;
import com.wellsoft.pt.di.service.DiConfigService;
import com.wellsoft.pt.di.service.DiDataConsumerEndpointService;
import com.wellsoft.pt.di.service.DiDataProcessorService;
import com.wellsoft.pt.di.service.DiDataProducerEndpointService;
import com.wellsoft.pt.di.support.CustomComponentResolver;
import com.wellsoft.pt.di.synchronization.DataIntegrationEndSynchronization;
import com.wellsoft.pt.di.synchronization.SubspendRounteSynchronization;
import com.wellsoft.pt.di.transform.AbstractDataTransform;
import com.wellsoft.pt.di.transform.DataTransform;
import com.wellsoft.pt.di.util.CamelContextUtils;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.DefaultErrorHandlerBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.MulticastDefinition;
import org.apache.camel.model.RouteDefinition;
import org.apache.camel.model.language.ConstantExpression;
import org.apache.camel.processor.DelegateAsyncProcessor;
import org.apache.camel.processor.DelegateSyncProcessor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Description: 加载并初始化系统的所有配置路由信息
 *
 * @author chenq
 * @date 2019/7/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/15    chenq		2019/7/15		Create
 * </pre>
 */
@Component
public class DataIntegrationRoutInitializer extends SpringRouteBuilder {

    static Map<String, Class<? extends AbstractEndpoint>> customEndpointClassMap = Maps.newHashMap();//自定义的端点
    static Map<String, Class<? extends AbstractDIProcessor>> customProcessorClassMap = Maps.newHashMap();//自定义处理器
    static Map<String, Class<? extends AbstractDataTransform>> customTransformerClassMap = Maps.newHashMap();//自定义转换器
    static Map<String, Class<? extends AbstractDiCallback>> customCallbackClassMap = Maps.newHashMap();//自定义回调函数
    private static ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private static MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(
            resourcePatternResolver);

    static {
        loadDataIntegrateClasses();
    }

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ConfigurableListableBeanFactory configurableListableBeanFactory;
    @Autowired
    DiConfigService diConfigService;
    @Autowired
    DiDataConsumerEndpointService consumerEndpointService;
    @Autowired
    DiDataProducerEndpointService producerEndpointService;
    @Autowired
    DiDataProcessorService diDataProcessorService;
    @Value("${di.enable:true}")
    private String enableDi;
    @Value("${di.callback.delay:1}")
    private Integer callbackDelay;

    private static void loadDataIntegrateClasses() throws BeansException {
        try {
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "com/wellsoft/**/*Endpoint.class";

            Resource[] resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                if (!Modifier.isAbstract(mappedClass.getModifiers()) &&
                        AbstractEndpoint.class.isAssignableFrom(
                                mappedClass)) {
                    AbstractEndpoint endpoint = (AbstractEndpoint) mappedClass.newInstance();
                    customEndpointClassMap.put(endpoint.endpointPrefix(),
                            (Class<? extends AbstractEndpoint>) mappedClass);
                }
            }


            searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "com/wellsoft/**/*Processor.class";

            resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                if (!Modifier.isAbstract(
                        mappedClass.getModifiers()) && AbstractDIProcessor.class.isAssignableFrom(
                        mappedClass)) {
                    AbstractDIProcessor processor = (AbstractDIProcessor) mappedClass.newInstance();
                    customProcessorClassMap.put(processor.name(),
                            (Class<? extends AbstractDIProcessor>) mappedClass);
                }
            }


            searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "com/wellsoft/**/*Transform.class";

            resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                if (!Modifier.isAbstract(
                        mappedClass.getModifiers()) && AbstractDataTransform.class.isAssignableFrom(
                        mappedClass)) {
                    AbstractDataTransform transform = (AbstractDataTransform) mappedClass.newInstance();
                    customTransformerClassMap.put(transform.name(),
                            (Class<? extends AbstractDataTransform>) mappedClass);
                }
            }

            searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX
                    + "com/wellsoft/pt/**/*Callback.class";

            resources = resourcePatternResolver.getResources(searchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> mappedClass = Class.forName(className);
                if (!Modifier.isAbstract(
                        mappedClass.getModifiers()) && AbstractDiCallback.class.isAssignableFrom(
                        mappedClass)) {
                    AbstractDiCallback callback = (AbstractDiCallback) mappedClass.newInstance();
                    customCallbackClassMap.put(callback.name(),
                            (Class<? extends AbstractDiCallback>) mappedClass);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static Map<String, Class<? extends AbstractDiCallback>> getCustomCallbackClassMap() {
        return customCallbackClassMap;
    }

    @Override
    public void configure() throws Exception {

        if ("false".equals(System.getProperty("di.enable"))) {
            logger.warn(">>> 应用容器的参数配置di.enable=false，不启用数据交换！<<<");
            return;
        }
        if (!"true".equals(enableDi)) {
            return;
        }
        //组件解析器，自动加载基于平台组件标准的组件定义类
        ((DefaultCamelContext) getContext()).setComponentResolver(new CustomComponentResolver());
        List<DiConfigEntity> diConfigEntityList = diConfigService.listAll();
        logger.info("开始构建数据交换路由规则");
        //开始构建路由
        for (final DiConfigEntity config : diConfigEntityList) {
            try {
                addRouteDeleteWhenExist(config);
            } catch (Exception e) {
                logger.error("构建数据交换路由规则异常，路由配置UUID[{}]", config.getUuid(), e);
            }
        }

        //系统回调服务路由创建
        /*RouteDefinition routeDefinition = new RouteDefinition();
        routeDefinition.from("callback://system");
        routeDefinition.process(new CallbackServiceProcessor());
        routeDefinition.delay(callbackDelay * 1000);
        routeDefinition.end();
        getContext().addRouteDefinition(routeDefinition);*/

        logger.info("结束构建数据交换路由规则");
    }

    public void addRouteDeleteWhenExist(DiConfigEntity config) {
        try {
            CamelContextUtils.removeRoute(config.getUuid());
            CamelContextUtils.removeRoute("retry_" + config.getUuid());
            addRoute(config);
        } catch (Exception e) {
            logger.error("数据交换配置[{}]添加数据交换路由异常", config.getName());
            throw new RuntimeException("数据交换配置[" + config.getName() + "]添加数据交换路由异常：", e);
        }
    }

    public void stopRoute(DiConfigEntity config) {
        CamelContextUtils.removeRoute(config.getUuid());
    }

    public void addRoute(DiConfigEntity config) throws Exception {
        DiDataConsumerEndpointEntity consumerEndpointEntity = consumerEndpointService.getByDiConfigUuid(
                config.getUuid());
        DiDataProducerEndpointEntity producerEndpointEntity = producerEndpointService.getByDiConfigUuid(
                config.getUuid());

        final RouteDefinition routeDefinition = new RouteDefinition();
        final RouteDefinition retryRouteDefinition = new RouteDefinition();//构建一条重试的路由
        routeDefinition.setId(config.getUuid());
        retryRouteDefinition.setId("retry_" + config.getUuid());
        retryRouteDefinition.autoStartup(true);
        routeDefinition.autoStartup(
                config.getIsEnable() && StringUtils.isBlank(
                        config.getJobUuid()));//启用情况下，非调度任务执行的，自动启动

        String fromUri = buildConsumerEndpointUri(config, consumerEndpointEntity);
        if (StringUtils.isBlank(fromUri)) {
            return;
        }
        routeDefinition.from(fromUri);
        retryRouteDefinition.from("direct:retry_" + config.getUuid());
        retryRouteDefinition.setProperty(DiConstant.DI_RETRY_PROPERTY_NAME,
                new ConstantExpression("true"));
        routeDefinition.setProperty(DiConstant.DI_UUID_PROPERTY_NAME,
                new ConstantExpression(config.getUuid()));
        routeDefinition.process(new DataIntegrationBeginProcessor());
        addProcessors(routeDefinition, config);
        addProcessors(retryRouteDefinition, config);

        if (StringUtils.isNotBlank(config.getJobUuid())) {//定时任务运行完，挂起路由
            routeDefinition.delay(0L);
            routeDefinition.process(new Processor() {
                @Override
                public void process(Exchange exchange) throws Exception {
                    exchange.getUnitOfWork().addSynchronization(
                            new SubspendRounteSynchronization());
                }
            });
        }
        routeDefinition.process(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getUnitOfWork().addSynchronization(
                        new DataIntegrationEndSynchronization());
            }
        });

        String to = buildProducerEndpointUri(producerEndpointEntity);
        MulticastDefinition multicast = routeDefinition.multicast();
        multicast.parallelProcessing();
        MulticastDefinition retryMulticast = retryRouteDefinition.multicast();
        if (StringUtils.isNotBlank(to)) {
            multicast.to(to);
            retryMulticast.to(to);
        }

        if (StringUtils.isNotBlank(producerEndpointEntity.getCallbackClass())) {
            if (!producerEndpointEntity.getIsAsyncCallback()) {
                multicast.end();
                retryMulticast.end();
                //同步回调函数，执行完整路由闭环
                AbstractDiCallback callback = (AbstractDiCallback) Class.forName(
                        producerEndpointEntity.getCallbackClass()).newInstance();
                routeDefinition.pipeline().process(new DelegateSyncProcessor(callback));
                retryRouteDefinition.pipeline().process(new DelegateSyncProcessor(callback));
            } else {
                String callbackto = "callback://" + config.getUuid() + "?callbackClass=" + producerEndpointEntity.getCallbackClass();
                //异步的回调，保存到等待响应池
                multicast.to(callbackto);
                multicast.end();

                retryMulticast.to(callbackto);
                retryMulticast.end();
            }
        } else {
            multicast.end();
            retryMulticast.end();
        }

        addErrorHandler(routeDefinition, config, consumerEndpointEntity);
        routeDefinition.end();
        retryRouteDefinition.end();

        getContext().addRouteDefinition(routeDefinition);
        getContext().addRouteDefinition(retryRouteDefinition);
    }

    private void addErrorHandler(RouteDefinition routeDefinition,
                                 DiConfigEntity config,
                                 DiDataConsumerEndpointEntity consumerEndpointEntity) {
        DefaultErrorHandlerBuilder handlerBuilder = new DefaultErrorHandlerBuilder();
        handlerBuilder.setAsyncDelayedRedelivery(
                !consumerEndpointEntity.getEdpType().equalsIgnoreCase("direct"));//非同步接口交换的，重试都是异步执行
        boolean redelivery = config.getRedeliveryMaximum() != null
                || config.getRedeliveryInterval() != null
                || StringUtils.isNotBlank(config.getRedeliveryRulePattern());

        if (config.getRedeliveryMaximum() != null) {
            handlerBuilder.maximumRedeliveries(config.getRedeliveryMaximum());
        }
        if (config.getRedeliveryInterval() != null) {
            handlerBuilder.maximumRedeliveryDelay(config.getRedeliveryInterval());
        }
        if (StringUtils.isNotBlank(config.getRedeliveryRulePattern())) {
            handlerBuilder.delayPattern(config.getRedeliveryRulePattern());
        }
        if (!redelivery) {//如果没有配置重试参数，默认重试一次
            handlerBuilder.maximumRedeliveries(1);
        }
        handlerBuilder.setOnExceptionOccurred(new ExceptionOccuredProcessor());
        handlerBuilder.setFailureProcessor(new FailOccuredProcessor());
        handlerBuilder.log(logger);
        routeDefinition.errorHandler(handlerBuilder);

    }

    private String buildProducerEndpointUri(
            DiDataProducerEndpointEntity producerEndpointEntity) throws Exception {

        Class endpointClass = customEndpointClassMap.get(producerEndpointEntity.getEdpType());
        if (endpointClass != null) {
            AbstractEndpoint endpoint = (AbstractEndpoint) endpointClass.newInstance();
            String definition = producerEndpointEntity.getDefinition();
            return endpoint.toProducerUri(producerEndpointEntity.getDiConfigUuid(), definition);
        } else {
            Map<String, Object> params = new Gson().fromJson(
                    producerEndpointEntity.getDefinition(), Map.class);

            if (producerEndpointEntity.getEdpType().equals(DiConstant.FILE_ROUTE_PREFIX)) {

                //文件流
                return DiConstant.FILE_ROUTE_PREFIX + "://" + params.get(
                        "filePath").toString() + "?" +
                        "fileName=" + params.get("fileName").toString();

            } else if (producerEndpointEntity.getEdpType().equals(DiConstant.FTP_ROUTE_PREFIX)) {

                return DiConstant.FTP_ROUTE_PREFIX + "://" + params.get(
                        "ftpServer").toString() + "?" +
                        "username=" + params.get("username") +
                        "&password=" + params.get("password");
            }

            return "";
        }

    }

    private void addProcessors(RouteDefinition routeDefinition,
                               DiConfigEntity config) throws Exception {
        List<DiDataProcessorEntity> processorEntityList = diDataProcessorService.listByDiConfigUuidOrderBySeqAsc(
                config.getUuid());
        for (DiDataProcessorEntity entity : processorEntityList) {
            Map<String, Object> parameters = null;
            if (StringUtils.isNotBlank(entity.getProcessorParameter())) {
                parameters = new Gson().fromJson(entity.getProcessorParameter(),
                        HashMap.class);
            }
            Class targetClass = Class.forName(entity.getProcessorClass());
            Object inst = Class.forName(entity.getProcessorClass()).newInstance();
            if (parameters != null) {
                Set<String> keys = parameters.keySet();
                for (String k : keys) {
                    if (parameters.get(k) == null) {
                        continue;
                    }
                    try {
                        Field field = targetClass.getDeclaredField(k);
                        field.setAccessible(true);
                        if (Boolean.class.isAssignableFrom(
                                field.getType()) || field.getType().getName().equals("boolean")
                                || field.get(inst) instanceof Boolean) {
                            field.set(inst, BooleanUtils.toBoolean(parameters.get(k).toString()));
                        } else if (field.getType().isArray()) {
                            field.set(inst, parameters.get(k).toString().split(","));
                        } else if (List.class.isAssignableFrom(field.getType())) {
                            field.set(inst, Arrays.asList(parameters.get(k).toString().split(",")));
                        } else {
                            field.set(inst, parameters.get(k));
                        }
                    } catch (Exception e) {
                        continue;
                    }
                }

                /*Field processorUuidField = targetClass.getField("processorUuid");
                if (processorUuidField != null) {//设置处理器UUID
                    processorUuidField.setAccessible(true);
                    processorUuidField.set(inst, entity.getUuid());
                }*/

            }
            routeDefinition.setProperty(DiConstant.DI_PROCESSOR_UUID_PROPERTY_NAME,
                    new ConstantExpression(entity.getUuid()));
            if (entity.getType().equals(DiConstant.PROCESSOR_TYPE_TRANSFORMER)) {//数据转换器
                routeDefinition.transform((DataTransform) inst);
            } else if (entity.getType().equals(DiConstant.PROCESSOR_TYPE_PROCESSOR)) {//数据处理器
                if (entity.getIsAsync()) {//异步处理器
                    DelegateAsyncProcessor asyncProcessor = new DelegateAsyncProcessor();
                    asyncProcessor.setProcessor((AbstractDIProcessor) inst);
                    routeDefinition.process(asyncProcessor);
                } else {
                    routeDefinition.process((AbstractDIProcessor) inst);
                }

            }
        }

    }

    private String buildConsumerEndpointUri(
            DiConfigEntity config,
            DiDataConsumerEndpointEntity consumerEndpointEntity) throws Exception {
        Class endpointClass = customEndpointClassMap.get(consumerEndpointEntity.getEdpType());

        if (endpointClass != null) {
            AbstractEndpoint endpoint = (AbstractEndpoint) endpointClass.newInstance();
            return endpoint.toConsumerUri(consumerEndpointEntity.getDiConfigUuid(),
                    consumerEndpointEntity.getDefinition(), config.getTimeInterval());
        } else {
            Map<String, Object> params = new Gson().fromJson(
                    consumerEndpointEntity.getDefinition(), Map.class);
            Object successPath = params.get("successPath");
            if (StringUtils.isBlank((String) successPath)) {
                successPath = ".success";
            }
            Object failPath = params.get("failPath");
            if (StringUtils.isBlank((String) failPath)) {
                failPath = ".fail";
            }
            if (consumerEndpointEntity.getEdpType().equals(DiConstant.FILE_ROUTE_PREFIX)) {

                //文件流
                return DiConstant.FILE_ROUTE_PREFIX + "://" + params.get(
                        "filePath").toString() + "?" +
                        "delay=" + (config.getTimeInterval() != null ? config.getTimeInterval() * 1000L : 1000) +
                        "&move=" + successPath.toString() + "/${date:now:yyyy-MM-dd}/${file:name}" +
                        "&moveFailed=" + failPath.toString() + "/${date:now:yyyy-MM-dd}/${file:name}" +
                        "&probeContentType=true"
                        + (StringUtils.isNotBlank(
                        (String) params.get("include")) ? "&include=" + params.get("include") : "")
                        /*+"&copyAndDeleteOnRenameFail=false"*/;

            } else if (consumerEndpointEntity.getEdpType().equals(DiConstant.FTP_ROUTE_PREFIX)) {

                return DiConstant.FTP_ROUTE_PREFIX + "://" + params.get(
                        "ftpServer").toString() + "?" +
                        "delay=" + (config.getTimeInterval() != null ? config.getTimeInterval() * 1000L : 1000) +
                        "&username=" + params.get("username") +
                        "&password=" + params.get("password") +
                        "&move=" + successPath.toString() + "/${date:now:yyyy-MM-dd}/${file:name}" +
                        "&moveFailed=" + failPath.toString() + "/${date:now:yyyy-MM-dd}/${file:name}" +
                        "&localWorkDirectory=" + System.getProperty("java.io.tmpdir") + "/camelftp";
            } else if (consumerEndpointEntity.getEdpType().endsWith(
                    DiConstant.DIRECT_ROUTE_PREFIX)) {//直接调用
                return "direct:" + consumerEndpointEntity.getDiConfigUuid();
            }

            return "";
        }

    }

    public Map<String, Class<? extends AbstractEndpoint>> getCustomEndpointClassMap() {
        return customEndpointClassMap;
    }

    public Map<String, Class<? extends AbstractDIProcessor>> getCustomProcessorClassMap() {
        return customProcessorClassMap;
    }

    public Map<String, Class<? extends AbstractDataTransform>> getCustomTransformerClassMap() {
        return customTransformerClassMap;
    }


}
