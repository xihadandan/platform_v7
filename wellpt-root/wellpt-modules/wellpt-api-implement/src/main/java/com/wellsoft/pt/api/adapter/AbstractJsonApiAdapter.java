package com.wellsoft.pt.api.adapter;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wellsoft.context.enums.ApiCodeEnum;
import com.wellsoft.pt.api.request.ApiAdapterRequest;
import com.wellsoft.pt.api.request.ApiRequest;
import com.wellsoft.pt.api.response.ApiResponse;
import org.apache.commons.io.Charsets;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/10/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/22    chenq		2018/10/22		Create
 * </pre>
 */
public abstract class AbstractJsonApiAdapter<T extends ApiResponse> implements WellptApiAdapter {

    protected static Gson GSON;

    Class<T> responseClass;


    public AbstractJsonApiAdapter() {
        GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                responseClass = (Class<T>) params[0];
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    /**
     * 调用服务
     *
     * @param request
     * @return
     */
    @Override
    public T invoke(ApiAdapterRequest request) {
        // 创建http POST请求
        HttpPost httpPost = new HttpPost(request.getEndpoint());
        String json = this.parse(request.getApiRequest());

        if (json != null) {
            // 构造一个请求实体
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // 将请求实体设置到httpPost对象中
            httpPost.setEntity(stringEntity);
        }
        RequestConfig requestConf = RequestConfig.custom().setConnectTimeout(
                request.getConnectionLiveSeconds() * 1000).setSocketTimeout(
                request.getConnectionLiveSeconds() * 1000).build();

        CloseableHttpResponse response = null;
        CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().setRetryHandler(
                new DefaultHttpRequestRetryHandler()).setDefaultRequestConfig(requestConf).build();
        String responseBody;
        request.setRealRequestBody(json);
        try {
            // 执行请求
            response = closeableHttpClient.execute(httpPost);
            responseBody = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            request.setRealResponseBody(responseBody);
            return GSON.fromJson(responseBody, responseClass);
        } catch (Exception e) {
            logger.error("执行http请求异常：", e);
            return exceptionResponse(e);
        } finally {
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(closeableHttpClient);
        }

    }


    protected T exceptionResponse(Exception e) {
        if (e instanceof ConnectTimeoutException || e instanceof SocketTimeoutException) {
            //服务超时
            return (T) ApiResponse.build().code(ApiCodeEnum.REQUEST_OVER_TIME);
        }

        return (T) ApiResponse.build().code(ApiCodeEnum.API_SYSTEM_ERROR);
    }


    /**
     * 转换报文
     *
     * @param request
     * @return
     */
    protected String parse(ApiRequest request) {
        return GSON.toJson(request);
    }
}
