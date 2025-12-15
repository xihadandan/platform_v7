/*
 * @(#)2019-07-23 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表DI_DATA_PRODUCER_ENDPOINT的实体类
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
@Entity
@Table(name = "DI_DATA_PRODUCER_ENDPOINT")
@DynamicUpdate
@DynamicInsert
public class DiDataProducerEndpointEntity extends IdEntity {

    private static final long serialVersionUID = 1563853756015L;


    private String definition;


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

    public String getEdpType() {
        return edpType;
    }

    public void setEdpType(String edpType) {
        this.edpType = edpType;
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

}
