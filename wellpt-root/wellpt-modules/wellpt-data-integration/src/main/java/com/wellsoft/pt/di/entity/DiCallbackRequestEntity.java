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
import java.sql.Blob;


/**
 * Description: 数据库表DI_CALLBACK_REQUEST的实体类
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
@Table(name = "DI_CALLBACK_REQUEST")
@DynamicUpdate
@DynamicInsert
public class DiCallbackRequestEntity extends IdEntity {

    private static final long serialVersionUID = 1564060099843L;


    private Integer callbackStatus;

    private String requestBody;

    private Blob responseBody;


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


    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
