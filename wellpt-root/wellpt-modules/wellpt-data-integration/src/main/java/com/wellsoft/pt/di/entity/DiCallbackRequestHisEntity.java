/*
 * @(#)2019-07-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Blob;


/**
 * Description: 数据库表DI_CALLBACK_REQUEST_HIS的实体类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-25.1	chenq		2019-07-25		Create
 * </pre>
 * @date 2019-07-25
 */
@Entity
@Table(name = "DI_CALLBACK_REQUEST_HIS")
@DynamicUpdate
@DynamicInsert
public class DiCallbackRequestHisEntity extends IdEntity {

    private static final long serialVersionUID = 1564060100254L;


    private Integer callbackStatus;

    private String requestBody;

    private Blob responseBody;


    private Object responseBodyObj;

    private Object requestBodyObj;

    private Integer timeConsuming;

    private String callbackClass;

    private String requestId;

    /**
     * @return the callbackStatus
     */
    public Integer getCallbackStatus() {
        return this.callbackStatus;
    }

    /**
     * @param callbackStatus
     */
    public void setCallbackStatus(Integer callbackStatus) {
        this.callbackStatus = callbackStatus;
    }

    /**
     * @return the requestBody
     */
    public String getRequestBody() {
        return this.requestBody;
    }

    /**
     * @param requestBody
     */
    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    /**
     * @return the responseBody
     */
    public Blob getResponseBody() {
        return this.responseBody;
    }

    /**
     * @param responseBody
     */
    public void setResponseBody(Blob responseBody) {
        this.responseBody = responseBody;
    }

    /**
     * @return the timeConsuming
     */
    public Integer getTimeConsuming() {
        return this.timeConsuming;
    }

    /**
     * @param timeConsuming
     */
    public void setTimeConsuming(Integer timeConsuming) {
        this.timeConsuming = timeConsuming;
    }

    /**
     * @return the callbackClass
     */
    public String getCallbackClass() {
        return this.callbackClass;
    }

    /**
     * @param callbackClass
     */
    public void setCallbackClass(String callbackClass) {
        this.callbackClass = callbackClass;
    }

    @Transient
    public Object getResponseBodyObj() {
        return responseBodyObj;
    }

    public void setResponseBodyObj(Object responseBodyObj) {
        this.responseBodyObj = responseBodyObj;
    }

    @Transient
    public Object getRequestBodyObj() {
        return requestBodyObj;
    }

    public void setRequestBodyObj(Object requestBodyObj) {
        this.requestBodyObj = requestBodyObj;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

}
