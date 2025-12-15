/*
 * @(#)2019-02-21 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表BUSINESS_APPLICATION的实体类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-21.1	leo		2019-02-21		Create
 * </pre>
 * @date 2019-02-21
 */
@Entity
@Table(name = "BUSINESS_APPLICATION")
@DynamicUpdate
@DynamicInsert
public class BusinessApplicationEntity extends IdEntity {

    private static final long serialVersionUID = 1550738838582L;


    private String businessCategoryUuid;

    /**
     * @return the businessCategoryUuid
     */
    public String getBusinessCategoryUuid() {
        return this.businessCategoryUuid;
    }

    /**
     * @param businessCategoryUuid
     */
    public void setBusinessCategoryUuid(String businessCategoryUuid) {
        this.businessCategoryUuid = businessCategoryUuid;
    }

}
