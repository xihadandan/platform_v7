package com.wellsoft.pt.api.request;

import java.io.Serializable;

/**
 * Description: 异步包裹请求
 *
 * @author chenq
 * @date 2018/10/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/30    chenq		2018/10/30		Create
 * </pre>
 */
public class AsyncRequestWrapper<T extends ApiRequest> implements Serializable {

    private T requestBody;

    private Requester requester;


    public AsyncRequestWrapper() {
    }

    public AsyncRequestWrapper(T requestBody,
                               Requester requester) {
        this.requestBody = requestBody;
        this.requester = requester;
    }

    public T getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(T requestBody) {
        this.requestBody = requestBody;
    }

    public Requester getRequester() {
        return requester;
    }

    public void setRequester(Requester requester) {
        this.requester = requester;
    }

    public static class Requester implements Serializable {

        private static final long serialVersionUID = 4756636777053941480L;

        private String systemCode;//外部系统编码

        private String systemName;//外部系统名称

        private String responseUnitId;//响应者的组织id


        public Requester() {
        }

        public Requester(String systemCode, String systemName, String responseUnitId) {
            this.systemCode = systemCode;
            this.systemName = systemName;
            this.responseUnitId = responseUnitId;
        }

        public String getSystemCode() {
            return systemCode;
        }

        public void setSystemCode(String systemCode) {
            this.systemCode = systemCode;
        }

        public String getSystemName() {
            return systemName;
        }

        public void setSystemName(String systemName) {
            this.systemName = systemName;
        }

        public String getResponseUnitId() {
            return responseUnitId;
        }

        public void setResponseUnitId(String responseUnitId) {
            this.responseUnitId = responseUnitId;
        }
    }
}
