/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import com.wellsoft.pt.api.internal.parser.WellptJsonParser;
import com.wellsoft.pt.api.internal.parser.WellptParser;
import com.wellsoft.pt.api.request.FileUploadRequest;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
public class DefaultWellptClient implements WellptClient {

    private static final String DEFAULT_ENCODING = "UTF-8";

    private static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_ENCODING);

    // 文件上传地址后缀路径
    private static final String PATH_FILE = "/file";
    private static Logger logger = LoggerFactory.getLogger(DefaultWellptClient.class);
    private HttpClient httpClient = HttpClients.createDefault();
    private boolean keepResponseBody;
    private String responseBody;
    private String tenantId;
    private String username;
    private String password;
    private String serverUrl;
    private Map<String, String> extraParams = new HashMap<String, String>();
    private Map<String, StringBody> extraParts = new HashMap<String, StringBody>();

    /**
     * @param address
     */
    public DefaultWellptClient(String serverUrl, String tenantId, String username, String password) {
        super();
        this.serverUrl = serverUrl;
        this.tenantId = tenantId;
        this.username = username;
        this.password = password;
    }

    /**
     * @param address
     */
    public DefaultWellptClient(String serverUrl, String tenantId, String username, String password,
                               boolean keepResponseBody) {
        this(serverUrl, tenantId, username, password);
        this.keepResponseBody = keepResponseBody;
    }

    /**
     * 将请求响应内容转化为字符串
     *
     * @param conn
     * @return
     * @throws IOException
     */
    protected static String getResponseAsString(HttpURLConnection conn) throws IOException {
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), DEFAULT_ENCODING);
        } else {
            return getStreamAsString(es, DEFAULT_ENCODING);
        }
    }

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
     * @see com.wellsoft.pt.api.WellptClient#execute(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public <T extends WellptResponse> T execute(WellptRequest<T> request) {
        WellptParser<T> parser = new WellptJsonParser<T>(request.getResponseClass());
        String responseString = null;
        if (FileUploadRequest.class.isAssignableFrom(request.getClass())) {
            responseString = executeFileUploadRequestByHttpClient(request);
        } else {
            responseString = executeRequestByHttpClient(request);
        }
        //		try {
        //			JSONObject json = new JSONObject(responseBody);
        //			if (!json.has(WellptResponse.KEY_CODE) || !json.has(WellptResponse.KEY_MSG)
        //					|| !json.has(WellptResponse.KEY_SUCCESS)) {
        //				json.put(WellptResponse.KEY_CODE, "-3");
        //				json.put(WellptResponse.KEY_MSG, "服务器返回无法识别的信息：" + responseBody);
        //				json.put(WellptResponse.KEY_SUCCESS, false);
        //				responseBody = json.toString();
        //			}
        //		} catch (JSONException e) {
        //			logger.error(ExceptionUtils.getStackTrace(e));
        //		}

        return parser.parse(responseString);
    }

    /**
     * 使用HttpClient执行请求
     *
     * @param <T>
     * @param request
     * @return
     */
    public <T extends WellptResponse> String executeRequestByHttpClient(WellptRequest<T> request) {
        String requestBody = WellptJsonParser.object2Json(request);
        logger.error("请求地址：" + serverUrl);
        logger.error("登录信息tenantId: " + tenantId + ", username: " + username + ", password: " + password);
        logger.error("请求参数：" + requestBody);
        // 设置请求参数
        HttpPost post = new HttpPost(this.serverUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair tenantIdPair = new BasicNameValuePair("tenantId", tenantId);
        NameValuePair usernamePair = new BasicNameValuePair("username", username);
        NameValuePair passwordPair = new BasicNameValuePair("password", password);
        NameValuePair jsonPair = new BasicNameValuePair("json", requestBody);
        params.add(tenantIdPair);
        params.add(usernamePair);
        params.add(passwordPair);
        params.add(jsonPair);
        for (String key : extraParams.keySet()) {
            NameValuePair extraParam = new BasicNameValuePair(key, extraParams.get(key));
            params.add(extraParam);
        }
        String responseString = null;
        try {
            HttpEntity httpEntity = new UrlEncodedFormEntity(params, DEFAULT_ENCODING);
            post.setEntity(httpEntity);
            prePost(post);
            // post.setHeader("Content-Type",
            // "application/x-www-form-urlencoded;charset=" + DEFAULT_CHARSET);
            HttpResponse response = httpClient.execute(post);
            responseString = getStreamAsString(response.getEntity().getContent(), DEFAULT_ENCODING);
            if (this.keepResponseBody) {
                this.responseBody = responseString;
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        logger.error("返回对象：" + responseString);
        return responseString;
    }

    /**
     * 使用HttpClient执行请求
     *
     * @param <T>
     * @param request
     * @return
     */
    public <T extends WellptResponse> String executeFileUploadRequestByHttpClient(WellptRequest<T> request) {
        FileUploadRequest fileUploadApiRequest = (FileUploadRequest) request;
        String requestBody = WellptJsonParser.object2Json(request);
        requestBody = requestBody.replaceAll("%", "\\%");
        // 设置请求参数
        String requestUrl = this.serverUrl;
        if (!requestUrl.endsWith(PATH_FILE)) {
            requestUrl = this.serverUrl + PATH_FILE;
        }
        HttpPost post = new HttpPost(requestUrl);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        NameValuePair tenantIdPair = new BasicNameValuePair("tenantId", tenantId);
        NameValuePair usernamePair = new BasicNameValuePair("username", username);
        NameValuePair passwordPair = new BasicNameValuePair("password", password);
        NameValuePair jsonPair = new BasicNameValuePair("json", requestBody);
        params.add(tenantIdPair);
        params.add(usernamePair);
        params.add(passwordPair);
        params.add(jsonPair);
        for (String key : extraParams.keySet()) {
            NameValuePair extraParam = new BasicNameValuePair(key, extraParams.get(key));
            params.add(extraParam);
        }
        String responseString = null;
        try {
            // MultipartEntityBuilder multipartEntityBuilder =
            // MultipartEntityBuilder.create();
            // multipartEntityBuilder.addPart("file", bin);
            // multipartEntityBuilder.addPart("username", userId);
            // multipartEntityBuilder.setCharset(DEFAULT_CHARSET);
            // 以浏览器兼容模式运行，防止文件名乱码。
            StringBody tenantId1 = new StringBody(tenantId, ContentType.create("text/plain", DEFAULT_CHARSET));
            StringBody username1 = new StringBody(username, ContentType.create("text/plain", DEFAULT_CHARSET));
            StringBody password1 = new StringBody(password, ContentType.create("text/plain", DEFAULT_CHARSET));
            StringBody json = new StringBody(requestBody, ContentType.create("text/plain", DEFAULT_CHARSET));
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .addPart("tenantId", tenantId1).addPart("username", username1).addPart("password", password1)
                    .addPart("json", json);
            for (Entry<String, StringBody> entry : extraParts.entrySet()) {
                multipartEntityBuilder.addPart(entry.getKey(), entry.getValue());
            }
            HttpEntity httpEntity = multipartEntityBuilder.addBinaryBody("file", fileUploadApiRequest.getInputStream())
                    .setCharset(DEFAULT_CHARSET).build();
            //			HttpEntity httpEntity = new InputStreamEntity(fileUploadApiRequest.getInputStream());
            post.setEntity(httpEntity);
            prePost(post);
            HttpResponse response = httpClient.execute(post);
            responseString = getStreamAsString(response.getEntity().getContent(), DEFAULT_ENCODING);
            if (this.keepResponseBody) {
                this.responseBody = responseString;
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (ClientProtocolException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (IOException e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return responseString;
    }

    /**
     * @param post
     */
    protected void prePost(HttpPost post) {
    }

    @Override
    public Map<String, String> addRequestParam(String key, String value) {
        extraParams.put(key, value);
        return extraParams;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptClient#addRequestPart(java.lang.String, java.lang.String)
     */
    @Override
    public Map<String, StringBody> addRequestPart(String key, String value) {
        extraParts.put(key, new StringBody(value, ContentType.create("text/plain", DEFAULT_CHARSET)));
        return extraParts;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptClient#getResponseBody()
     */
    @Override
    public String getResponseBody() {
        return this.responseBody;
    }
}
