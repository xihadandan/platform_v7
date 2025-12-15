package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 表格组件列表列对象
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
@ApiModel("表格组件列表列对象")
public class TableColumnDto implements Serializable {

    private static final long serialVersionUID = 7719630605874776249L;
    @ApiModelProperty("字段名")
    private String columnName;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("数据类型")
    private String dataType;

    @ApiModelProperty("是否显示")
    private String isShow;


    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(final String columnName) {
        this.columnName = columnName;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public String getIsShow() {
        return this.isShow;
    }

    public void setIsShow(final String isShow) {
        this.isShow = isShow;
    }
}
