/*
 * @(#)2013-3-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.form;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-28.1	zhulh		2013-3-28		Create
 * </pre>
 * @date 2013-3-28
 */
@ApiModel("自定义列数据")
public class CustomDynamicColumnValue extends BaseObject {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6868929500623049540L;

    // 类型——文本、数字、日期、附件等
    @ApiModelProperty("类型——文本、数字、日期、附件等")
    private String type;

    // 列索引
    @ApiModelProperty("列索引")
    private String index;

    // 列值
    @ApiModelProperty("列值")
    private Object value;

    /**
     * 如何描述该构造方法
     */
    public CustomDynamicColumnValue() {
        super();
    }

    /**
     * @param value
     */
    public CustomDynamicColumnValue(Object value) {
        super();
        this.value = value;
    }

    /**
     * @param type
     * @param value
     */
    public CustomDynamicColumnValue(String type, Object value) {
        super();
        this.type = type;
        this.value = value;
    }

    /**
     * @param type
     * @param index
     * @param value
     */
    public CustomDynamicColumnValue(String type, String index, Object value) {
        super();
        this.type = type;
        this.index = index;
        this.value = value;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the index
     */
    public String getIndex() {
        return index;
    }

    /**
     * @param index 要设置的index
     */
    public void setIndex(String index) {
        this.index = index;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
