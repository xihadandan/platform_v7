package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 环节意见立场输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/8/23.1	    zenghw		2021/8/23		    Create
 * </pre>
 * @date 2021/8/23
 */
@ApiModel("环节意见立场输出对象")
public class UnitElementDto implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7719630605874776249L;
    @ApiModelProperty("意见值")
    private String value;
    @ApiModelProperty("意见名称")
    private String argValue;

    public String getValue() {
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getArgValue() {
        return this.argValue;
    }

    public void setArgValue(final String argValue) {
        this.argValue = argValue;
    }
}
