package com.wellsoft.pt.common.translate.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.common.translate.client.TranslateClient;
import com.wellsoft.pt.common.translate.service.TranslateService;
import com.wellsoft.pt.document.MongoDocumentService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
@Service
public class TranslateServiceImpl implements TranslateService {


    @Autowired
    private TranslateClient translateClient;

    @Autowired
    MongoDocumentService mongoDocumentService;

    private final String translateCollection = "TRANSLATE_COLLECTION";


    @Override
    public String translate(String word, String from, String to) {
        word.trim();
        Map<String, Object> result = mongoDocumentService.getOneAsMapByFieldEq(translateCollection, "source_text", word);
        if (result != null && result.containsKey(to)) {
            return result.get(to).toString();
        }
        String text = translateClient.translate(word, from, to);
        Map<String, Object> data = Maps.newHashMap();
        data.put(to, text);
        data.put("source_text", word);
        Map<String, Object> query = Maps.newHashMap();
        query.put("source_text", word);
        mongoDocumentService.saveOrUpdate(translateCollection, data, query);
        return text;
    }

    @Override
    public Map<String, String> translate(Set<String> wordSet, String from, String to) {
        Map<String, String> resultMap = Maps.newHashMap();
        List<String> w = Lists.newArrayList(wordSet);
        w.replaceAll(s -> s != null ? s.trim() : null);
        w.removeIf(s -> s == null || s.isEmpty());
        Set<String> word = Sets.newHashSet(w);
        List<Map<String, Object>> dataList = mongoDocumentService.listByFieldInValue(translateCollection, "source_text", word);
        if (CollectionUtils.isNotEmpty(dataList)) {
            for (Map<String, Object> data : dataList) {
                String text = data.get("source_text").toString();
                if (word.contains(text) && data.get(to) != null) {
                    word.remove(text);
                    resultMap.put(text, data.get(to).toString());
                }
            }
        }
        if (!word.isEmpty()) {
            Map<String, String> translateResult = translateClient.translate(word, from, to);
            Set<Map.Entry<String, String>> entries = translateResult.entrySet();
            for (Map.Entry<String, String> ent : entries) {
                Map<String, Object> data = Maps.newHashMap();
                data.put(to, ent.getValue());
                data.put("source_text", ent.getKey());
                Map<String, Object> query = Maps.newHashMap();
                query.put("source_text", ent.getKey());
                mongoDocumentService.saveOrUpdate(translateCollection, data, query);
                resultMap.put(ent.getKey(), ent.getValue());
            }
        }
        return resultMap;
    }
}
