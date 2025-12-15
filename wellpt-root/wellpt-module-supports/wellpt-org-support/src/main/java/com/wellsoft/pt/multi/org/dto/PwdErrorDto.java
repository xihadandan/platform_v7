package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 密码错误输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/29.1	    zenghw		2021/3/29		    Create
 * </pre>
 * @date 2021/3/29
 */
@ApiModel("密码错误输出对象")
public class PwdErrorDto {

    /**
     * 返回信息
     **/
    @ApiModelProperty("返回信息")
    private String message;
    /**
     * 当 isSuccess 为false时
     * 是否账号锁定
     **/
    @ApiModelProperty("当 isSuccess 为false时\n" + "是否账号锁定")
    private Boolean isLocked = false;

    public Boolean getLocked() {
        return isLocked;
    }

    public void setLocked(Boolean locked) {
        isLocked = locked;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
