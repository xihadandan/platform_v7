package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.support.CallApiParams;

import java.io.Serializable;

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
public class ApiAdapterRequest implements Serializable {

    public static final String API_MODE_IN = "IN";
    public static final String API_MODE_OUT = "OUT";

    private static final long serialVersionUID = 6025965786518926466L;

    private ApiRequest apiRequest;

    private String realResponseBody;

    private CallApiParams params;

    private String endpoint;

    private int connectionLiveSeconds = 60;//连接存活时间设置

    private String apiMode;//

    private String realRequestBody;

    private String commandUuid;//指令id

    public ApiRequest getApiRequest() {
        return apiRequest;
    }

    public void setApiRequest(ApiRequest apiRequest) {
        this.apiRequest = apiRequest;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public int getConnectionLiveSeconds() {
        return connectionLiveSeconds;
    }

    public void setConnectionLiveSeconds(int connectionLiveSeconds) {
        this.connectionLiveSeconds = connectionLiveSeconds;
    }

    public CallApiParams getParams() {
        return params;
    }

    public void setParams(CallApiParams params) {
        this.params = params;
    }

    public String getRealRequestBody() {
        return realRequestBody;
    }

    public void setRealRequestBody(String realRequestBody) {
        this.realRequestBody = realRequestBody;
    }

    public String getCommandUuid() {
        return commandUuid;
    }

    public void setCommandUuid(String commandUuid) {
        this.commandUuid = commandUuid;
    }

    public String getRealResponseBody() {
        return realResponseBody;
    }

    public void setRealResponseBody(String realResponseBody) {
        this.realResponseBody = realResponseBody;
    }

    public String getApiMode() {
        return apiMode;
    }

    public void setApiMode(String apiMode) {
        this.apiMode = apiMode;
    }
}
