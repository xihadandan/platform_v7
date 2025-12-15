/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.bean;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月1日.1	xiem		2016年11月1日		Create
 * </pre>
 * @date 2016年11月1日
 */
@ApiModel("渲染器")
public class DataStoreRendererBean {
    @ApiModelProperty("列索引")
    private String columnIndex;
    @ApiModelProperty("渲染器参数")
    private RendererParam param;

    /**
     * @return the columnIndex
     */
    public String getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex 要设置的columnIndex
     */
    public void setColumnIndex(String columnIndex) {
        this.columnIndex = columnIndex;
    }

    /**
     * @return the param
     */
    public RendererParam getParam() {
        return param;
    }

    /**
     * @param param 要设置的param
     */
    public void setParam(RendererParam param) {
        this.param = param;
    }

}
