/*
 * @(#)2017-02-22 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 标签数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-22.1	zhulh		2017-02-22		Create
 * </pre>
 * @date 2017-02-22
 */
@Entity
@Table(name = "DMS_TAG_DATA")
@DynamicUpdate
@DynamicInsert
public class DmsTagDataEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1487735472484L;

    // 标签UUID
    @NotBlank
    private String tagUuid;
    // 数据UUID
    @NotBlank
    private String dataUuid;

    /**
     * @return the tagUuid
     */
    public String getTagUuid() {
        return tagUuid;
    }

    /**
     * @param tagUuid 要设置的tagUuid
     */
    public void setTagUuid(String tagUuid) {
        this.tagUuid = tagUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

}
