package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年05月13日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "API_INVOKE_LOG")
@DynamicUpdate
@DynamicInsert
public class ApiInvokeLogEntity extends SysEntity {

    private Long apiLinkUuid;
    private Long apiOperationUuid;
    private String protocol;
    private String invokeUrl;
    private String path;
    private String reqMethod;
    private String reqHeaders;
    private String reqQueryParams;
    private String reqBody;
    private Date reqTime;
    private Date resTime;
    private Integer latency;
    private Integer resStatus;
    private String resBody;
    private String resHeaders;
    private String errorMessage;

    public Long getApiLinkUuid() {
        return apiLinkUuid;
    }

    public void setApiLinkUuid(Long apiLinkUuid) {
        this.apiLinkUuid = apiLinkUuid;
    }

    public Long getApiOperationUuid() {
        return apiOperationUuid;
    }

    public void setApiOperationUuid(Long apiOperationUuid) {
        this.apiOperationUuid = apiOperationUuid;
    }

    public String getInvokeUrl() {
        return invokeUrl;
    }

    public void setInvokeUrl(String invokeUrl) {
        this.invokeUrl = invokeUrl;
    }

    public String getReqMethod() {
        return reqMethod;
    }

    public void setReqMethod(String reqMethod) {
        this.reqMethod = reqMethod;
    }

    public String getReqHeaders() {
        return reqHeaders;
    }

    public void setReqHeaders(String reqHeaders) {
        this.reqHeaders = reqHeaders;
    }

    public String getReqQueryParams() {
        return reqQueryParams;
    }

    public void setReqQueryParams(String reqQueryParams) {
        this.reqQueryParams = reqQueryParams;
    }

    public String getReqBody() {
        return reqBody;
    }

    public void setReqBody(String reqBody) {
        this.reqBody = reqBody;
    }

    public Date getReqTime() {
        return reqTime;
    }

    public void setReqTime(Date reqTime) {
        this.reqTime = reqTime;
    }

    public Date getResTime() {
        return resTime;
    }

    public void setResTime(Date resTime) {
        this.resTime = resTime;
    }

    public Integer getLatency() {
        return latency;
    }

    public void setLatency(Integer latency) {
        this.latency = latency;
    }

    public Integer getResStatus() {
        return resStatus;
    }

    public void setResStatus(Integer resStatus) {
        this.resStatus = resStatus;
    }

    public String getResBody() {
        return resBody;
    }

    public void setResBody(String resBody) {
        this.resBody = resBody;
    }

    public String getResHeaders() {
        return resHeaders;
    }

    public void setResHeaders(String resHeaders) {
        this.resHeaders = resHeaders;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
}
