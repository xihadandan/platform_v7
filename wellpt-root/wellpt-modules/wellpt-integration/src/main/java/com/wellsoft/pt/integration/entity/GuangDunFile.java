/*
 * @(#)2014-4-15 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Blob;

/**
 * Description: 如何描述该类
 *
 * @author xujianjia
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年5月16日.1	xujianjia		2017年5月16日		Create
 * </pre>
 * @date 2017年5月16日
 */
@Entity
@Table(name = "is_guangdun_attachrment")
@DynamicUpdate
@DynamicInsert
public class GuangDunFile extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3655590905220951791L;

    /**  **/
    /**
     * 附件标识ID尽量唯一
     */
    private String id;
    /**
     * 附件的名称  如附件的文件名等
     * 可为空
     */
    private String name;
    /**
     * 附件的简单描述
     * 可为空
     **/
    private String description;
    /**
     * 附件的实际内容
     **/
    private Blob data;
    /**
     * 校验值
     **/
    private String checkValue;
    /**
     * 数据Id
     */
    private String dataId;

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description 要设置的description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the data
     */
    public Blob getData() {
        return data;
    }

    /**
     * @param data 要设置的data
     */
    public void setData(Blob data) {
        this.data = data;
    }

    /**
     * @return the checkValue
     */
    public String getCheckValue() {
        return checkValue;
    }

    /**
     * @param checkValue 要设置的checkValue
     */
    public void setCheckValue(String checkValue) {
        this.checkValue = checkValue;
    }

    /**
     * @return the dataId
     */
    public String getDataId() {
        return dataId;
    }

    /**
     * @param dataId 要设置的dataId
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

}
