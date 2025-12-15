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
 * Description: 字典数据项
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
@ApiModel("字典数据项")
@Entity
@Table(name = "cd_data_dictionary_item")
@DynamicUpdate
@DynamicInsert
public class CdDataDictionaryItemEntity extends com.wellsoft.context.jdbc.entity.Entity {

    @ApiModelProperty("名称")
    private String label;

    @ApiModelProperty("值")
    private String value;

    @ApiModelProperty("排序号")
    private Integer sortOrder;

    @ApiModelProperty("所在数据字典UUID")
    private Long dataDictUuid;

    @ApiModelProperty("上级字典项UUID")
    private Long parentUuid;

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label 要设置的label
     */
    public void setLabel(String label) {
        this.label = label;
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
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the dataDictUuid
     */
    public Long getDataDictUuid() {
        return dataDictUuid;
    }

    /**
     * @param dataDictUuid 要设置的dataDictUuid
     */
    public void setDataDictUuid(Long dataDictUuid) {
        this.dataDictUuid = dataDictUuid;
    }

    /**
     * @return the parentUuid
     */
    public Long getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(Long parentUuid) {
        this.parentUuid = parentUuid;
    }
}
