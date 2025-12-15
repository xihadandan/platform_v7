/*
 * @(#)8/9/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/9/23.1	zhulh		8/9/23		Create
 * </pre>
 * @date 8/9/23
 */
@ApiModel("字典数据项扩展属性")
@Entity
@Table(name = "cd_data_dictionary_item_attr")
@DynamicUpdate
@DynamicInsert
public class CdDataDictionaryItemAttributeEntity extends com.wellsoft.context.jdbc.entity.Entity {

    @ApiModelProperty("扩展属性键")
    private String attrKey;

    @ApiModelProperty("扩展属性值")
    private String attrVal;

    @ApiModelProperty("字典数据项UUID")
    private Long itemUuid;

    /**
     * @return the attrKey
     */
    public String getAttrKey() {
        return attrKey;
    }

    /**
     * @param attrKey 要设置的attrKey
     */
    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    /**
     * @return the attrVal
     */
    public String getAttrVal() {
        return attrVal;
    }

    /**
     * @param attrVal 要设置的attrVal
     */
    public void setAttrVal(String attrVal) {
        this.attrVal = attrVal;
    }

    /**
     * @return the itemUuid
     */
    public Long getItemUuid() {
        return itemUuid;
    }

    /**
     * @param itemUuid 要设置的itemUuid
     */
    public void setItemUuid(Long itemUuid) {
        this.itemUuid = itemUuid;
    }
}
