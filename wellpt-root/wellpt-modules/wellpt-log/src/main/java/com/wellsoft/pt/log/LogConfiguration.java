package com.wellsoft.pt.log;

/**
 * es6 升级 es7
 *
 * @author baozh
 * import java.net.InetAddress;
 * <p>
 * import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
 * import org.elasticsearch.client.Client;
 * import org.elasticsearch.client.transport.TransportClient;
 * import org.elasticsearch.common.settings.Settings;
 * import org.elasticsearch.common.transport.InetSocketTransportAddress;
 * <p>
 * import org.springframework.beans.factory.annotation.Qualifier;
 * <p>
 * import org.springframework.context.annotation.Bean;
 * <p>
 * import org.springframework.context.annotation.PropertySource;
 * import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
 **/

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/1/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/30    chenq		2019/1/30		Create
 * </pre>
 */
@Configuration
public class LogConfiguration {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    //
//    @Value("${elasticsearch.server.host:localhost}")
//    private String elasticsearchHost;
//
//    @Value("${elasticsearch.server.port:9300}")
//    private Integer port;
//    @Value("${elasticsearch.server.cluster-name:elasticsearch}")
//    private String clusterName;
//
//    @Value("${elasticsearch.server.max_result_window:10000}")
//    private String maxResultWindow;
//
    @Value("${elasticsearch.server.save_days:30}")
    private Integer saveDays;
//
//    @Bean(name = "elasticsearchClient")
//    public Client client() {
//        try {
//            Client client = TransportClient
//                    .builder()
//                    .build()
//                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticsearchHost), port));
//            try {
//                setMaxResultWindow(client);
//            } catch (Exception e) {
//                // ignore
//            }
//            return client;
//        } catch (Exception e) {
//            logger.error("创建elasticsearch客户端异常：", e);
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    @Bean
//    public ElasticsearchTemplate elasticsearchTemplate(@Qualifier(value = "elasticsearchClient") Client client) {
//        return new ElasticsearchTemplate(client);
//    }
//
//    /**
//     * 由原来的http协议发送请求改为
//     *  java代码api来实现功能
//     * 如何描述该方法
//     * 可以使用curl命令进行设置
//     *  curl -XPUT 192.168.0.163:9200/_all/_settings -d '{"max_result_window" : 1000000}'
//     *  返回 {"acknowledged":true} 表示设置成功
//     */
//    public void setMaxResultWindow(Client client) {
//        // _all表示参数索引
//        UpdateSettingsResponse indexResponse = client.admin().indices().prepareUpdateSettings("_all")
//                .setSettings(Settings.builder().put("index.max_result_window", maxResultWindow).build()).get();
//        boolean result = indexResponse.isAcknowledged();
//        if (result) {
//            logger.info("elastic设置max_result_window成功");
//        } else {
//            logger.error("elastic设置max_result_window失败");
//        }
//    }
//

    /**
     * @return the saveDays
     */
    public Integer getSaveDays() {
        return saveDays;
    }

    /**
     * @param saveDays 要设置的saveDays
     */
    public void setSaveDays(Integer saveDays) {
        this.saveDays = saveDays;
    }

}
