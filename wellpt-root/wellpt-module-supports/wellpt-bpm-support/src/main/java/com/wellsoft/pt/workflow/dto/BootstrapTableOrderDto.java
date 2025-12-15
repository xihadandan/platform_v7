package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 表格组件列表排序对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/9/1.1	    zenghw		2021/9/1		    Create
 * </pre>
 * @date 2021/9/1
 */
@ApiModel("表格组件列表排序对象")
public class BootstrapTableOrderDto implements Serializable {

    private static final long serialVersionUID = 7719630605874776247L;
    @ApiModelProperty("列名")
    private String columnName;
    @ApiModelProperty("排序")
    private String order;

    public static long getSerialVersionUID() {
        return BootstrapTableOrderDto.serialVersionUID;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    public String getOrder() {
        return this.order;
    }

    public void setOrder(final String order) {
        this.order = order;
    }
}
