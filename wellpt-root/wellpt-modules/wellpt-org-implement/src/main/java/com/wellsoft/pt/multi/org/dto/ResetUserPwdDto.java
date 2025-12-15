package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Description: 重置用户密码输入对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/16.1	    zenghw		2021/3/16		    Create
 * </pre>
 * @date 2021/3/16
 */
@ApiModel("重置用户密码输入对象")
public class ResetUserPwdDto {

    @ApiModelProperty("用户uuid列表")
    private List<String> userUuids;

    public List<String> getUserUuids() {
        return userUuids;
    }

    public void setUserUuids(List<String> userUuids) {
        this.userUuids = userUuids;
    }
}
