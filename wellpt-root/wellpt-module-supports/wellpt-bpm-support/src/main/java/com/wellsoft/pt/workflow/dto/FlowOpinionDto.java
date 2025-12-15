package com.wellsoft.pt.workflow.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 流程意见
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
@ApiModel("流程意见")
public class FlowOpinionDto implements Serializable {
    private static final long serialVersionUID = 7719630605874776244L;
    // 意见内容
    @ApiModelProperty("意见内容")
    private String content;

    public String getContent() {
        return this.content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

}
