/*
 * @(#)2020年2月25日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * es6 升级  es7
 *
 * @author baozh
 * <p>
 * import org.elasticsearch.action.admin.indices.settings.put.UpdateSettingsResponse;
 * import org.elasticsearch.client.Client;
 * import org.elasticsearch.client.transport.TransportClient;
 * import org.elasticsearch.common.settings.Settings;
 * import org.elasticsearch.common.transport.InetSocketTransportAddress;
 * import org.junit.Test;
 */

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月25日.1	wangrf		2020年2月25日		Create
 * </pre>
 * @date 2020年2月25日
 */
public class HttpPutTest {

    // 配置elastic window to large
    // curl -XPUT 192.168.0.163:9200/_all/_settings -d '{"max_result_window" : 1000000}'
    // 返回 {"acknowledged":true} 表示设置成功
    //    @Test
    public void sendPut() throws UnsupportedEncodingException {
        String resStr = null;
        HttpClient htpClient = new HttpClient();
        PutMethod putMethod = new PutMethod("http://192.168.0.163:9200/_all/_settings");

        RequestEntity requestEntity = new StringRequestEntity("{\"max_result_window\":1000000}", "application/json",
                "utf-8");
        putMethod.setRequestEntity(requestEntity);
        //        putMethod.addRequestHeader("Content-Type", "application/json");
        //        putMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "");
        //        putMethod.setRequestBody("");
        try {
            int statusCode = htpClient.executeMethod(putMethod);
            //            log.info(statusCode);
            if (statusCode != HttpStatus.SC_OK) {
                System.out.println("request ok");
            }
            byte[] responseBody = putMethod.getResponseBody();
            resStr = new String(responseBody, "utf-8");
            if ("{\"acknowledged\":true}".equals(resStr)) {
                System.out.println("set params success");
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            putMethod.releaseConnection();
        }
        System.out.println(resStr);
    }

    /**
     * es6 升级 es7
     *
     * @author baozh @Test
     * public void settingMaxWindow() throws UnknownHostException {
     * //
     * String elasticsearchHost = "192.168.0.163";
     * Integer port = 9300;
     * Client client = TransportClient.builder().build()
     * .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticsearchHost), port));
     * UpdateSettingsResponse indexResponse = client.admin().indices().prepareUpdateSettings("_all")
     * .setSettings(Settings.builder().put("index.max_result_window", 1000).build()).get();
     * //        String result = indexResponse.toString();
     * System.out.println(indexResponse.isAcknowledged());
     * //        System.out.println(result);
     * }
     **/

    //    @Test
    public void getMinDate() {
        String resStr = null;
        HttpClient htpClient = new HttpClient();
        GetMethod putMethod = new GetMethod("http://192.168.0.163:9200/_cat/indices?v");
        try {
            int statusCode = htpClient.executeMethod(putMethod);
            if (statusCode == HttpStatus.SC_OK) {
                System.out.println("request fail");
            }
            byte[] responseBody = putMethod.getResponseBody();
            resStr = new String(responseBody, "utf-8");
            // 保存所有的索引信息
            Pattern pattern = Pattern.compile("wellpt-log-[0-9]{4}\\.[0-9]{2}\\.[0-9]{2}");
            Matcher matcher = pattern.matcher(resStr);
            Set<String> sets = new HashSet<String>();
            while (matcher.find()) {
                sets.add(resStr.substring(matcher.start(), matcher.end()));
            }
            String[] array = new String[sets.size()];
            sets.toArray(array);
            Arrays.sort(array);
            for (int i = 0; i < array.length; i++) {
                System.out.println(array[i]);
            }
            System.out.println(array);
            System.out.println(sets);
        } catch (Exception e) {
            // e.printStackTrace();
        } finally {
            putMethod.releaseConnection();
        }
    }

    //    @Test
    public void testDate() {
        //        try {
        System.out.println("2020.02.10".replaceAll("\\.", "-"));
        //            Date minDate = DateUtils.parse();
        //            System.out.println(minDate.getTime());
        //        } catch (ParseException e) {
        //            e.printStackTrace();
        //        }
    }
}
