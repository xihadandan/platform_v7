package com.wellsoft.pt.fulltext;


/**
 * es6 升级 es7
 *
 * @author baozh
 * <p>
 * import org.elasticsearch.client.Client;
 * import org.elasticsearch.client.transport.TransportClient;
 * import org.elasticsearch.common.transport.InetSocketTransportAddress;
 * <p>
 * import org.springframework.beans.factory.annotation.Qualifier;
 * <p>
 * <p>
 * import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
 * <p>
 * import java.net.InetAddress;
 */

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月07日   chenq	 Create
 * </pre>
 */
@Configuration
public class FulltextConfiguration extends AppContextConfigurerAdapter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${elasticsearch.server.host}")
    private String[] elasticsearchHost;

    @Value("${elasticsearch.server.port:9300}")
    private Integer port;
    @Value("${elasticsearch.server.cluster-name:elasticsearch}")
    private String clusterName;

    @Value("${elasticsearch.server.max_result_window:10000}")
    private String maxResultWindow;

    @Value("${elasticsearch.server.save_days:30}")
    private Integer saveDays;


    @Bean(destroyMethod = "close")
    public RestHighLevelClient restHighLevelClient() {
        try {
            if (ArrayUtils.isEmpty(elasticsearchHost)) {
                return new RestHighLevelClient(RestClient.builder(new HttpHost("127.0.0.1", 9200, "http")));
            }
            HttpHost[] httpHosts = new HttpHost[elasticsearchHost.length];
            //将地址转换为http主机数组，未配置端口则采用默认9200端口，配置了端口则用配置的端口
            for (int i = 0; i < httpHosts.length; i++) {
                if (!StringUtils.isEmpty(elasticsearchHost[i])) {
                    if (elasticsearchHost[i].contains(":")) {
                        String[] uris = elasticsearchHost[i].split(":");
                        httpHosts[i] = new HttpHost(uris[0], Integer.parseInt(uris[1]), "http");
                    } else {
                        httpHosts[i] = new HttpHost(elasticsearchHost[i], 9200, "http");
                    }
                }
            }
            RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(httpHosts)
                    .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                            .setConnectTimeout(3000)
                            .setSocketTimeout(5000)
                            .setConnectionRequestTimeout(500)));
            return client;
        } catch (Exception e) {
            logger.error("创建elasticsearch客户端异常：", e);
            throw new RuntimeException(e);
        }

    }

//    @Bean
//    public ElasticsearchRestTemplate elasticsearchRestTemplate(RestHighLevelClient client) {
//        return new ElasticsearchRestTemplate(client);
//    }


    /**
     * es6 升级  es7
     *
     * @author baozh
     * @date 2021/9/13 10:48
     * @Bean(name = "elasticsearchClient")
     * public Client client() {
     * try {
     * Client client = TransportClient
     * .builder()
     * .build()
     * .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticsearchHost), port));
     * <p>
     * return client;
     * } catch (Exception e) {
     * logger.error("创建elasticsearch客户端异常：", e);
     * throw new RuntimeException(e);
     * }
     * <p>
     * }
     * @Bean public ElasticsearchTemplate elasticsearchTemplate(@Qualifier(value = "elasticsearchClient") Client client) {
     * return new ElasticsearchTemplate(client);
     * }
     **/

    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {

    }
}
