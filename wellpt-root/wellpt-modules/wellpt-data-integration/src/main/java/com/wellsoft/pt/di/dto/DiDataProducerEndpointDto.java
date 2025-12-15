/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.dto;

import java.io.Serializable;


/**
 * Description: 数据库表DI_DATA_PRODUCER_ENDPOINT的对应的DTO类
 *
 * @author chenq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-07-23.1	chenq		2019-07-23		Create
 * </pre>
 * @date 2019-07-23
 */
public class DiDataProducerEndpointDto implements Serializable {

    private static final long serialVersionUID = 1563853756015L;

    private String uuid;

    private String definition;

    private Integer seq;

    private String callbackClass;

    private String diConfigUuid;

    private String edpType;

    private Boolean isAsyncCallback;

    /**
     * @return the definition
     */
    public String getDefinition() {
        return this.definition;
    }

    /**
     * @param definition
     */
    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * @return the seq
     */
    public Integer getSeq() {
        return this.seq;
    }

    /**
     * @param seq
     */
    public void setSeq(Integer seq) {
        this.seq = seq;
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

    /**
     * @return the diConfigUuid
     */
    public String getDiConfigUuid() {
        return this.diConfigUuid;
    }

    /**
     * @param diConfigUuid
     */
    public void setDiConfigUuid(String diConfigUuid) {
        this.diConfigUuid = diConfigUuid;
    }

    public String getEdpType() {
        return edpType;
    }

    public void setEdpType(String edpType) {
        this.edpType = edpType;
    }

    /**
     * @return the isAsyncCallback
     */
    public Boolean getIsAsyncCallback() {
        return this.isAsyncCallback;
    }

    /**
     * @param isAsyncCallback
     */
    public void setIsAsyncCallback(Boolean isAsyncCallback) {
        this.isAsyncCallback = isAsyncCallback;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
