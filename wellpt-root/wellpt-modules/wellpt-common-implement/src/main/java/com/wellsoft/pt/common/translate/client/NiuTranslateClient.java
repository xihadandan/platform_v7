package com.wellsoft.pt.common.translate.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import net.sf.json.JSONObject;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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
public class NiuTranslateClient implements TranslateClient {

    @Value("${translate.api.niu.url:http://api.niutrans.com/NiuTransServer}")
    private String url;

    @Value("${translate.api.niu.appKey}")
    private String appKey;

    private int perRequestCharLimit = 5000;

    @Override
    public String translate(String word, String from, String to) {
        StringBuilder uri = new StringBuilder(this.url + "/translation");
        GetMethod getMethod = null;
        if (word.length() > perRequestCharLimit) {
            throw new RuntimeException("单次请求字符超过请求字符上限: " + perRequestCharLimit);
        }
        try {
            uri.append("?apikey=").append(this.appKey);
            uri.append("&from=").append(from);
            uri.append("&to=").append(to);
            uri.append("&src_text=").append(URLEncoder.encode(word, StandardCharsets.UTF_8.toString()));
            getMethod = new GetMethod(uri.toString());
            //设置请求的编码方式
            getMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + "utf-8");
            new HttpClient().executeMethod(getMethod);
            String responseBody = getMethod.getResponseBodyAsString();
            Gson gson = GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays().create();
            Map<String, Object> result = gson.fromJson(responseBody, Map.class);
            if (result.containsKey("tgt_text")) {
                return result.get("tgt_text").toString();
            }
        } catch (Exception e) {
            logger.error("百度翻译API请求返回结果异常", e);
        }
        return null;
    }


    @Override
    public Map<String, String> translate(Set<String> word, String from, String to) {
        Map<String, String> resultMap = Maps.newHashMap();
        String[] wordArray = word.toArray(new String[]{});
        StringBuilder query = new StringBuilder();
        List<List<String>> requestWords = Lists.newArrayList();
        for (int i = 0, len = wordArray.length; i < len; i++) {
            // 单次请求不能超过限制
            if (query.length() + wordArray[i].length() < this.perRequestCharLimit) {
                query.append(wordArray[i]).append("\n");
                if (i == wordArray.length - 1) {
                    requestWords.add(Arrays.asList(query.toString().split("\n")));
                    query.setLength(0);
                }
            } else {
                requestWords.add(Arrays.asList(query.toString().split("\n")));
                query.setLength(0);
                query.append(wordArray[i]).append("\n");
                if (i == wordArray.length - 1) {
                    requestWords.add(Arrays.asList(query.toString().split("\n")));
                }
            }
        }
        for (List<String> w : requestWords) {
            Map<String, String> map = this.translateList(w, from, to);
            if (map != null) {
                resultMap.putAll(map);
            }
        }
        return resultMap;
    }

    private Map<String, String> translateList(List<String> word, String from, String to) {
        StringBuilder uri = new StringBuilder(this.url + "/translationArray");
        Map<String, String> map = Maps.newHashMap();
        try {
            PostMethod postMethod = new PostMethod(uri.toString());
            JSONObject json = new JSONObject();
            json.put("apikey", this.appKey);
            json.put("from", from);
            json.put("to", to);
            json.put("src_text", word);
            StringRequestEntity requestEntity = new StringRequestEntity(json.toString(), "application/json", "utf-8");
            postMethod.setRequestEntity(requestEntity);
            new HttpClient().executeMethod(postMethod);
            String responseBody = postMethod.getResponseBodyAsString();
            Gson gson = GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays().create();
            Map<String, Object> result = gson.fromJson(responseBody, Map.class);
            if (result != null && result.get("result_code").toString().equals("200")) {
                List<Map<String, String>> list = (List<Map<String, String>>) result.get("tgt_list");
                for (int i = 0, len = word.size(); i < len; i++) {
                    map.put(word.get(i), list.get(i).get("tgt_text").toString());
                }

            }
        } catch (Exception e) {
            logger.error("批量请求小牛api翻译异常", e);
        }
        return map;
    }


    public NiuTranslateClient() {
    }

    public NiuTranslateClient(String url, String appKey) {
        this.url = url;
        this.appKey = appKey;
    }

    public static void main(String[] args) {
        NiuTranslateClient client = new NiuTranslateClient("http://api.niutrans.com/NiuTransServer", "8e7a1462bc979d817cab2f09ded9c2dd");
        Map<String, String> map = client.translate(Sets.newHashSet("你好", "大家", "明天"), "zh", "en");
        System.out.println(map);
//        String x = client.translate("你好\n世界", "zh", "en");
//        System.out.println(x);
    }

    public static class BaiduTransResponse implements Serializable {
        private String from;
        private String to;
        private List<TranslateResult> transResult;
    }

    public static class TranslateResult implements Serializable {
        private String src;
        private String dst;

        public String getSrc() {
            return src;
        }

        public void setSrc(String src) {
            this.src = src;
        }

        public String getDst() {
            return dst;
        }

        public void setDst(String dst) {
            this.dst = dst;
        }
    }
}
