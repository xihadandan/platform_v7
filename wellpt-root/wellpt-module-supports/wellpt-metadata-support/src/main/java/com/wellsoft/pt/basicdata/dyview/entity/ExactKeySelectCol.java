/*
 * @(#)2013-3-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Description: 精确关键字查询的实体类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-27.1	wubin		2013-3-27		Create
 * </pre>
 * @date 2013-3-27
 */
@Entity
@Table(name = "dyview_select_exactkey")
@DynamicUpdate
@DynamicInsert
public class ExactKeySelectCol extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4935595753433396150L;

    private String keyName; //精确查询的关键字显示名称

    private String keyValue; //精确查询的关键字对应的视图的字段

    @UnCloneable
    private SelectDefinition selectDefinition;

    /**
     * @return the keyName
     */
    public String getKeyName() {
        return keyName;
    }

    /**
     * @param keyName 要设置的keyName
     */
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    /**
     * @return the keyValue
     */
    public String getKeyValue() {
        return keyValue;
    }

    /**
     * @param keyValue 要设置的keyValue
     */
    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    /**
     * @return the selectDefinition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "select_def_uuid", nullable = false)
    public SelectDefinition getSelectDefinition() {
        return selectDefinition;
    }

    /**
     * @param selectDefinition 要设置的selectDefinition
     */
    public void setSelectDefinition(SelectDefinition selectDefinition) {
        this.selectDefinition = selectDefinition;
    }

}
