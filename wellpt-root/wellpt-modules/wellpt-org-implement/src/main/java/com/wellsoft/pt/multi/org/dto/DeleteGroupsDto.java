package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 批量删除群组输入对象
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
@ApiModel("批量删除群组输入对象")
public class DeleteGroupsDto {

    @ApiModelProperty("群组ID集合")
    private List<String> groupIds;

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }
}
