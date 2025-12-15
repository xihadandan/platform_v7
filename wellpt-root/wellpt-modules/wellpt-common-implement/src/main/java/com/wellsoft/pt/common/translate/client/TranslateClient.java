package com.wellsoft.pt.common.translate.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

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
public interface TranslateClient {

    Logger logger = LoggerFactory.getLogger(TranslateClient.class);

    String translate(String word, String from, String to);

    Map<String, String> translate(Set<String> word, String from, String to);
}
