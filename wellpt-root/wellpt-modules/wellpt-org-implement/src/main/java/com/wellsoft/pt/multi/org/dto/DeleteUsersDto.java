package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 删除用户对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/14.1	    zenghw		2021/3/14		    Create
 * </pre>
 * @date 2021/3/14
 */
@ApiModel("用户对象")
public class DeleteUsersDto {

    @ApiModelProperty("用户id")
    private String[] userIds;

    public String[] getUserIds() {
        return userIds;
    }

    public void setUserIds(String[] userIds) {
        this.userIds = userIds;
    }
}
