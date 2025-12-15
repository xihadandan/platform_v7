package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Set;

/**
 * Description: 根据memberObjId查询角色Id输入对象
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
@ApiModel("根据memberObjId查询角色Id输入对象")
public class GetRoleIdByGroupMemberDto {

    @ApiModelProperty("memberObjId集合")
    private Set<String> memberObjIdSet;

    public Set<String> getMemberObjIdSet() {
        return memberObjIdSet;
    }

    public void setMemberObjIdSet(Set<String> memberObjIdSet) {
        this.memberObjIdSet = memberObjIdSet;
    }
}
