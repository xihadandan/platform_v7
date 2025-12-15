package com.wellsoft.pt.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 通过orgId 批量获取对应的组织名称 输入对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/4/7.1	    zenghw		2021/4/7		    Create
 * </pre>
 * @date 2021/4/7
 */
@ApiModel("通过orgId 批量获取对应的组织名称 输入对象")
public class GetNameByOrgEleIdsDto {
    @ApiModelProperty("组织ID集合")
    private List<String> orgIds;

    public List<String> getOrgIds() {
        return orgIds;
    }

    public void setOrgIds(List<String> orgIds) {
        this.orgIds = orgIds;
    }
}
