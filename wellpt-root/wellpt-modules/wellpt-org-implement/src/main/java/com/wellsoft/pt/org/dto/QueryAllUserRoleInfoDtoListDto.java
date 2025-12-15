package com.wellsoft.pt.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 获取用户的所有权限数据列表入参对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/12.1	    zenghw		2021/3/12		    Create
 * </pre>
 * @date 2021/3/12
 */
@ApiModel("获取用户的所有权限数据列表入参对象")
public class QueryAllUserRoleInfoDtoListDto {

    @ApiModelProperty("userId")
    private String userId;
    @ApiModelProperty("roleUuids")
    private String[] roleUuids;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String[] getRoleUuids() {
        return roleUuids;
    }

    public void setRoleUuids(String[] roleUuids) {
        this.roleUuids = roleUuids;
    }
}
