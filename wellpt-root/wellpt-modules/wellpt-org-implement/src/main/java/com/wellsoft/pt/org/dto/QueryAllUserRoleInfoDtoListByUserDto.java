package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.multi.org.bean.OrgUserVo;
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
 * 2021/3/14.1	    zenghw		2021/3/14		    Create
 * </pre>
 * @date 2021/3/14
 */
@ApiModel("获取用户的所有权限数据列表入参对象")
public class QueryAllUserRoleInfoDtoListByUserDto {

    @ApiModelProperty("组织用户对象")
    private OrgUserVo user;
    @ApiModelProperty("roleUuids")
    private String[] roleUuids;

    public OrgUserVo getUser() {
        return user;
    }

    public void setUser(OrgUserVo user) {
        this.user = user;
    }

    public String[] getRoleUuids() {
        return roleUuids;
    }

    public void setRoleUuids(String[] roleUuids) {
        this.roleUuids = roleUuids;
    }
}
