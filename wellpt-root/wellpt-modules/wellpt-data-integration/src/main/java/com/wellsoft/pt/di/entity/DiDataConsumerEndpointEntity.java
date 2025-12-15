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
 * Description: 数据库表DI_DATA_CONSUMER_ENDPOINT的实体类
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
@Table(name = "DI_DATA_CONSUMER_ENDPOINT")
@DynamicUpdate
@DynamicInsert
public class DiDataConsumerEndpointEntity extends IdEntity {

    private static final long serialVersionUID = 1563853755361L;

    // 定义数据(json)
    private String definition;
    // 来源类型
    private String edpType;
    // 数据交换配置UUID
    private String diConfigUuid;


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
     * @return the edpType
     */
    public String getEdpType() {
        return this.edpType;
    }

    /**
     * @param edpType
     */
    public void setEdpType(String edpType) {
        this.edpType = edpType;
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

}
