package com.wellsoft.pt.common.translate.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
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
public class BaiduTranslateClient implements TranslateClient {

    @Value("${translate.api.baidu.url:https://fanyi-api.baidu.com/ait/api/aiTextTranslate}")
    private String url;

    @Value("${translate.api.baidu.appId:20231018001851608}")
    private String appId;

    @Value("${translate.api.baidu.secret:G2wjzeiAr4eaQaDlb5pK}")
    private String secret;

    @Value("${translate.api.baidu.apiKey:3yrM_d4eulltcpvhhq4qck13g}")
    private String apiKey;

    @Value("${translate.api.baidu.modelType:llm}") // llm 大模型翻译 nmt 机器翻译
    private String translateModelType;

    private int perRequestCharLimit = 2000;

    @Override
    public String translate(String word, String from, String to) {
        List<Map<String, String>> result = this.invokeApi(word, from, to);
        if (result != null) {
            return result.get(0).get("dst");
        }
        return null;
    }

    private List<Map<String, String>> invokeApi(String word, String from, String to) {
        StringBuilder uri = new StringBuilder(this.url);
        HttpMethod getMethod = null;

        if (word.length() > perRequestCharLimit) {
            throw new RuntimeException("单次请求字符超过请求字符上限: " + perRequestCharLimit);
        }
        try {
            String salt = new Date().getTime() + "";
            uri.append("?appid=").append(this.appId);
            uri.append("&from=").append(from);
            uri.append("&to=").append(to);
            uri.append("&q=").append(URLEncoder.encode(word, StandardCharsets.UTF_8.toString()));
            uri.append("&model_type=").append(this.translateModelType);
            uri.append("&salt=").append(salt);
            if (StringUtils.isNotBlank(apiKey)) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(new StringBuilder(appId).append(word).append(salt).append(secret).toString().getBytes(StandardCharsets.UTF_8));
                StringBuilder sign = new StringBuilder();
                for (byte b : digest) {
                    sign.append(String.format("%02x", b));
                }
                uri.append("&sign=").append(sign.toString());
            }
            getMethod = new GetMethod(uri.toString());
            if (StringUtils.isNotBlank(apiKey)) {
                getMethod.setRequestHeader("Authorization", "Bearer " + this.apiKey);
            }
        } catch (Exception e) {
            logger.error("百度翻译API设置参数异常", e);
        }
        try {
            int code = new HttpClient().executeMethod(getMethod);
            String responseBody = getMethod.getResponseBodyAsString();
            Gson gson = GsonBuilderUtils.gsonBuilderWithBase64EncodedByteArrays().create();
            Map<String, Object> result = gson.fromJson(responseBody, Map.class);
            if (result.containsKey("trans_result")) {
                return (List<Map<String, String>>) result.get("trans_result");
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
        List<String> requestWords = Lists.newArrayList();
        for (int i = 0, len = wordArray.length; i < len; i++) {
            // 单次请求不能超过限制
            if (query.length() + wordArray[i].length() < this.perRequestCharLimit) {
                query.append(wordArray[i]).append("\n");
                if (i == wordArray.length - 1) {
                    requestWords.add(query.toString());
                    query.setLength(0);
                }
            } else {
                requestWords.add(query.toString());
                query.setLength(0);
                query.append(wordArray[i]).append("\n");
                if (i == wordArray.length - 1) {
                    requestWords.add(query.toString());
                }
            }
        }
        for (String w : requestWords) {
            List<Map<String, String>> map = this.invokeApi(w, from, to);
            if (map != null) {
                for (Map<String, String> item : map) {
                    resultMap.put(item.get("src").toString(), item.get("dst").toString());
                }
            }
        }

        return resultMap;
    }


    public BaiduTranslateClient() {
    }

    public BaiduTranslateClient(String url, String appId, String secret) {
        this.url = url;
        this.appId = appId;
        this.secret = secret;
    }

    public static void main(String[] args) {
//        BaiduTranslateClient client = new BaiduTranslateClient("http://api.fanyi.baidu.com/api/trans/vip/translate", "20231018001851608", "G2wjzeiAr4eaQaDlb5pK");
//        Map<String, String> map = client.translate(Sets.newHashSet("你好", "大家", "明天"), "zh", "en");
//        System.out.println(map);
//        String x = client.translate("你好", "zh", "en");
//        System.out.println(x);

        String[] wordArray = new String[]{"我的大家", "中国新年", "沉积"};
        StringBuilder query = new StringBuilder();
        List<String> requestWords = Lists.newArrayList();
        for (int i = 0, len = wordArray.length; i < len; i++) {
            // 单次请求不能超过限制
            if (query.length() + wordArray[i].length() < 8) {
                query.append(wordArray[i]).append("\n");
                if (i == wordArray.length - 1) {
                    requestWords.add(query.toString());
                    query.setLength(0);
                }
            } else {
                requestWords.add(query.toString());
                query.setLength(0);
                query.append(wordArray[i]).append("\n");
                if (i == wordArray.length - 1) {
                    requestWords.add(query.toString());
                }
            }
        }
        System.out.println(requestWords);
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
