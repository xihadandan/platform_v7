/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageWebserviceParm;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-22.1	zhulh		2014-10-22		Create
 * </pre>
 * @date 2014-10-22
 */
@Component
public class WebServiceMessageProcessor extends AbstractMessageProcessor {
    private static final String DEFAULT_ENCODING = "UTF-8";

    private HttpClient httpClient = HttpClients.createDefault();

    /**
     * 将输入字节流用指定编辑转化为字符串
     *
     * @param stream
     * @param charset
     * @return
     * @throws IOException
     */
    private static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, charset));
            StringWriter writer = new StringWriter();

            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }

            return writer.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.processor.MessageProcessor#doProcessor(com.wellsoft.pt.message.support.Message)
     */
    @Override
    public void doProcessor(Message msg) {
        IdEntity webParm = msg.getReservedIdentity();
        if (webParm instanceof MessageWebserviceParm) {
            sendWebserviceMessage((MessageWebserviceParm) webParm);//发送webservice
        }

    }

    /**
     * 通过webservice发送消息
     *
     * @param parm
     * @return
     */
    private String sendWebserviceMessage(MessageWebserviceParm parm) {
        // 设置请求参数
        HttpPost post = new HttpPost(parm.getWebServiceUrl());
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair tenantIdPair = new BasicNameValuePair(parm.getTenantidKey(), parm.getTenantidValue());
        NameValuePair usernamePair = new BasicNameValuePair(parm.getUsernameKey(), parm.getUsernameValue());
        NameValuePair passwordPair = new BasicNameValuePair(parm.getPasswordKey(), parm.getPasswordValue());
        NameValuePair jsonPair = new BasicNameValuePair("json", parm.getJsondata());
        params.add(tenantIdPair);
        params.add(usernamePair);
        params.add(passwordPair);
        params.add(jsonPair);
        String responseBody = null;
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params, DEFAULT_ENCODING);
            post.setEntity(httpEntity);
            HttpResponse response = httpClient.execute(post);
            responseBody = getStreamAsString(response.getEntity().getContent(), DEFAULT_ENCODING);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        logger.info("返回对象：" + responseBody);
        return responseBody;
    }
}
