/*
 * @(#)2019年8月22日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.metadata;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月22日.1	zhulh		2019年8月22日		Create
 * </pre>
 * @date 2019年8月22日
 */
public class ColumnMetadata extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3684882776505373073L;

    private String name;
    private String dataType;
    // 是否可为空
    private boolean isNullable;
    // 长度
    private int precision;
    // 小数位
    private int scale;

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
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType 要设置的dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the isNullable
     */
    public boolean isNullable() {
        return isNullable;
    }

    /**
     * @param isNullable 要设置的isNullable
     */
    public void setNullable(boolean isNullable) {
        this.isNullable = isNullable;
    }

    /**
     * @return the precision
     */
    public int getPrecision() {
        return precision;
    }

    /**
     * @param precision 要设置的precision
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }

    /**
     * @return the scale
     */
    public int getScale() {
        return scale;
    }

    /**
     * @param scale 要设置的scale
     */
    public void setScale(int scale) {
        this.scale = scale;
    }

}
