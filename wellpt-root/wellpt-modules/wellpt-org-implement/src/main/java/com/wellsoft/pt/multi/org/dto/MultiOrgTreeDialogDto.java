package com.wellsoft.pt.multi.org.dto;

import com.wellsoft.pt.multi.org.bean.OrgTreeDialogParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 组织弹出框参数对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/15.1	    zenghw		2021/3/15		    Create
 * </pre>
 * @date 2021/3/15
 */
@ApiModel(value = "组织弹出框参数对象")
public class MultiOrgTreeDialogDto {
    @ApiModelProperty("//类型")
    private String type;
    @ApiModelProperty("params")
    private OrgTreeDialogParams params;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public OrgTreeDialogParams getParams() {
        return params;
    }

    public void setParams(OrgTreeDialogParams params) {
        this.params = params;
    }
}
