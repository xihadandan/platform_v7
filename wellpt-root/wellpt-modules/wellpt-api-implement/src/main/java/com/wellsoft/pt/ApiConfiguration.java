package com.wellsoft.pt;

import com.google.common.collect.Lists;
import com.wellsoft.pt.api.interceptor.IpAddressLimitInInterceptor;
import com.wellsoft.pt.api.service.impl.WellptSoapWebServiceImpl;
import com.wellsoft.pt.api.service.impl.WellptWebServiceImpl;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.xml.ws.Endpoint;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/13    chenq		2019/11/13		Create
 * </pre>
 */
@Configuration
@ImportResource("classpath:META-INF/cxf/cxf.xml")
public class ApiConfiguration {

    @Autowired
    private Bus bus;
    @Value("${spring.api.corePoolSize:10}")
    private int apiCorePoolSize;
    @Value("${spring.api.maxPoolSize:30}")
    private int apiMaxPoolSize;
    @Value("${spring.api.queueCapacity:10}")
    private int apiQueueCapacity;
    @Value("${spring.api.keepApliveSecondes:60}")
    private int apiKeepApliveSecondes;

    @Bean
    public IpAddressLimitInInterceptor ipAddressLimitInInterceptor() {
        return new IpAddressLimitInInterceptor();
    }

    @Bean
    public WellptWebServiceImpl wellptWebService() {
        return new WellptWebServiceImpl();
    }

    @Bean
    public Server rsServer(WellptWebServiceImpl wellptWebService,
                           IpAddressLimitInInterceptor ipAddressLimitInInterceptor) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/wellpt/rest/service");
        endpoint.setServiceBeans(
                Lists.<Object>newArrayList(wellptWebService));
        endpoint.setInInterceptors(
                Lists.<Interceptor<? extends Message>>newArrayList(ipAddressLimitInInterceptor));
        return endpoint.create();
    }

    @Bean
    public Endpoint endpoint(IpAddressLimitInInterceptor ipAddressLimitInInterceptor) {
        EndpointImpl endpoint = new EndpointImpl(bus, new WellptSoapWebServiceImpl());
        endpoint.setAddress("/wellpt/soap/service");
        endpoint.setInInterceptors(
                Lists.<Interceptor<? extends Message>>newArrayList(ipAddressLimitInInterceptor));
        return endpoint;
    }

    @Bean
    public ThreadPoolTaskExecutor apiExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(apiCorePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(apiMaxPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(apiQueueCapacity);
        threadPoolTaskExecutor.setKeepAliveSeconds(apiKeepApliveSecondes);
        threadPoolTaskExecutor.setThreadNamePrefix("apiExecutor-");
        threadPoolTaskExecutor.setRejectedExecutionHandler(
                new ThreadPoolExecutor.CallerRunsPolicy());
        return threadPoolTaskExecutor;
    }
}
