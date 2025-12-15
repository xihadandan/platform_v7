package com.wellsoft.distributedlog.configuration;

import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * Description: elasticsearch配置。初始化es的rest客户端，提供服务类调用
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
@Configuration
public class ElasticsearchConfiguration extends AbstractElasticsearchConfiguration {

    @Value("${log.esNodes:'localhost:9200'}")
    private String nodes;

    @Override
    @Bean
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(nodes.split(",|;"))
                .build();
        RestHighLevelClient client = RestClients.create(clientConfiguration).rest();
        try {
            client.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {
            throw new RuntimeException("无法连接elasticsearch服务: " + e.getMessage());
        }

        return client;
    }


}
