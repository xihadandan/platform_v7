package com.wellsoft.pt.common.translate;

import com.wellsoft.pt.common.translate.client.BaiduTranslateClient;
import com.wellsoft.pt.common.translate.client.TranslateClient;
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
 * 2025年03月24日   chenq	 Create
 * </pre>
 */
@Configuration
public class TranslateConfiguration {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${translate.api.class:com.wellsoft.pt.common.translate.client.BaiduTranslateClient}")
    private String translateClientClassPath;

    @Bean
    public TranslateClient translateClient() {
        try {
            Class clazz = Class.forName(translateClientClassPath);
            return (TranslateClient) clazz.getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("翻译api实例创建异常", e);
        }
        return new BaiduTranslateClient();
    }

}
