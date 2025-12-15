package com.wellsoft.pt;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/11/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/11/14    chenq		2019/11/14		Create
 * </pre>
 */
@Configuration
@ImportResource("classpath:/config/applicationContext-camel.xml")
public class DataIntegrationConfiguration {
/*
    @Autowired
    private Bus bus;


    @Bean
    public CamelContextFactoryBean camelContext() {
        CamelContextFactoryBean camelContextFactoryBean = new CamelContextFactoryBean();
        ContextScanDefinition contextScanDefinition = new ContextScanDefinition();
        contextScanDefinition.setIncludes(Lists.<String>newArrayList("com.wellsoft"));
        camelContextFactoryBean.setContextScan(contextScanDefinition);
        camelContextFactoryBean.setId("camelContext");
        return camelContextFactoryBean;
    }

    @Bean
    public CamelProducerTemplateFactoryBean defaultProducerTemplate() {
        CamelProducerTemplateFactoryBean producerTemplateFactoryBean = new CamelProducerTemplateFactoryBean();
        producerTemplateFactoryBean.setMaximumCacheSize(500);
        return producerTemplateFactoryBean;
    }

    @Bean
    public CamelConsumerTemplateFactoryBean defaultConsumerTemplate() {
        CamelConsumerTemplateFactoryBean producerTemplateFactoryBean = new CamelConsumerTemplateFactoryBean();
        producerTemplateFactoryBean.setMaximumCacheSize(500);
        return producerTemplateFactoryBean;
    }


    @Bean
    public DIWebserviceImpl diWebservice() {
        return new DIWebserviceImpl();
    }

    @Bean
    public Server diwebServer(DIWebserviceImpl diWebservice) {
        JAXRSServerFactoryBean endpoint = new JAXRSServerFactoryBean();
        endpoint.setBus(bus);
        endpoint.setAddress("/diwebservice");
        endpoint.setServiceBeans(
                Lists.<Object>newArrayList(diWebservice));
        return endpoint.create();
    }*/


}
