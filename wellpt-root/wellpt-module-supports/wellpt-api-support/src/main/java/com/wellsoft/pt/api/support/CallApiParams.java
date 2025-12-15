package com.wellsoft.pt.api.support;

import com.wellsoft.pt.api.request.ApiRequest;

import java.io.Serializable;

/**
 * Description: 平台api调用参数封装
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
public class CallApiParams<T extends ApiRequest> implements Serializable {

    private static final long serialVersionUID = 8307814775738356640L;

    private String bizCorrelationKey;//业务关联值

    private String businessType;//业务类型描述

    private String idempotentKey;//幂等值

    private String systemCode; //外部系统编码

    private String serviceCode;//外部系统服务编码

    private T request;//请求报文体


    public CallApiParams() {
    }

    public CallApiParams(String bizCorrelationKey, String businessType, String idempotentKey,
                         String systemCode, String serviceCode,
                         T request) {
        this.bizCorrelationKey = bizCorrelationKey;
        this.businessType = businessType;
        this.idempotentKey = idempotentKey;
        this.systemCode = systemCode;
        this.serviceCode = serviceCode;
        this.request = request;
    }

    public String getIdempotentKey() {
        return idempotentKey;
    }

    public void setIdempotentKey(String idempotentKey) {
        this.idempotentKey = idempotentKey;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public T getRequest() {
        return request;
    }

    public void setRequest(T request) {
        this.request = request;
    }

    public String getBizCorrelationKey() {
        return bizCorrelationKey;
    }

    public void setBizCorrelationKey(String bizCorrelationKey) {
        this.bizCorrelationKey = bizCorrelationKey;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }
}
