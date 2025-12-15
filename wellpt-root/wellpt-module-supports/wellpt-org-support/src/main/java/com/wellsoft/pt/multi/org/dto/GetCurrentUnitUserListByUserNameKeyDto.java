package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * Description: 通过用户名称模糊匹配获取当前用户单位的用户列表对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/11/25.1	    zenghw		2021/11/25		    Create
 * </pre>
 * @date 2021/11/25
 */
@ApiModel("通过用户名称模糊匹配获取当前用户单位的用户列表对象")
public class GetCurrentUnitUserListByUserNameKeyDto implements Serializable {

    private static final long serialVersionUID = 7696118177094786612L;

    @ApiModelProperty("userId")
    private String userId;
    @ApiModelProperty("用户名称")
    private String userName;
    @ApiModelProperty("职位路径名称")
    private String jobPathName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getJobPathName() {
        return jobPathName;
    }

    public void setJobPathName(String jobPathName) {
        this.jobPathName = jobPathName;
    }

}
