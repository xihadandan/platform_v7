package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 用户导入对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/4/19.1	    zenghw		2021/4/19		    Create
 * </pre>
 * @date 2021/4/19
 */
public class UserImportDto {

    /**
     * 是否修改密码成功
     **/
    @ApiModelProperty("是否修改密码成功")
    private Boolean isSuccess = false;
    /**
     * 返回信息
     **/
    @ApiModelProperty("返回信息")
    private String message;

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
