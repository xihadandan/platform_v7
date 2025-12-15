/*
 * @(#)2015-07-20 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.params.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

/**
 * Description: 如何描述该类
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
@Entity
@Table(name = "SYS_PARAM_ITEM")
@DynamicUpdate
@DynamicInsert
public class SysParamItem extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1437372328472L;

    // key
    @NotBlank
    private String key;
    // value
    //@MaxLength(max = 1000)
    private String value;
    // name
    @NotBlank
    private String name;
    // sourcetype
    @Digits(fraction = 0, integer = 10)
    private Integer sourcetype;
    // code
    @NotBlank
    private String code;
    // type
    @NotBlank
    private String type;

    /**
     * @return the key
     */
    @Column(name = "\"KEY\"")
    public String getKey() {
        return this.key;
    }

    /**
     * @param key
     */
    public String setKey(String key) {
        return this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * @param value
     */
    public String setValue(String value) {
        return this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public String setName(String name) {
        return this.name = name;
    }

    /**
     * @return the sourcetype
     */
    public Integer getSourcetype() {
        return this.sourcetype;
    }

    /**
     * @param sourcetype
     */
    public Integer setSourcetype(Integer sourcetype) {
        return this.sourcetype = sourcetype;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public String setCode(String code) {
        return this.code = code;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public String setType(String type) {
        return this.type = type;
    }

}
