/*
 * @(#)2012-11-14 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Description: 数据字典属性实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-14.1	zhulh		2012-11-14		Create
 * </pre>
 * @date 2012-11-14
 */
@Entity
@Table(name = "cd_data_dict_attr")
@DynamicUpdate
@DynamicInsert
public class DataDictionaryAttribute extends IdEntity {

    private static final long serialVersionUID = 3865909809415107871L;

    /**
     * 域名
     */
    private String name;

    /**
     * 域值
     */
    private String value;

    /**
     * 排序
     */
    private Integer attrOrder;

    /**
     * 所属字典
     */
    @UnCloneable
    private DataDictionary dataDictionary;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the attrOrder
     */
    public Integer getAttrOrder() {
        return attrOrder;
    }

    /**
     * @param attrOrder 要设置的attrOrder
     */
    public void setAttrOrder(Integer attrOrder) {
        this.attrOrder = attrOrder;
    }

    /**
     * @return the dataDictionary
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "data_dict_uuid", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public DataDictionary getDataDictionary() {
        return dataDictionary;
    }

    /**
     * @param dataDictionary 要设置的dataDictionary
     */
    public void setDataDictionary(DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
    }

}
