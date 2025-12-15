package com.wellsoft.pt.security.core.userdetails;

import java.io.Serializable;

/**
 * Description: 密码校验输出对象
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/4/1.1	    zenghw		2021/4/1		    Create
 * </pre>
 * @date 2021/4/1
 */
public class PwdRoleCheckObj implements Serializable {
    /**
     * 是否校验成功
     **/
    private Boolean isSuccess = true;

    /**
     * //是否锁定
     **/
    private Boolean isLocked;
    /**
     * //是否跳转强制修改密码页面
     **/
    private Boolean isOpenUpdatePwdPage = false;

    /**
     * //返回信息
     **/
    private String message;

    public Boolean getOpenUpdatePwdPage() {
        return isOpenUpdatePwdPage;
    }

    public void setOpenUpdatePwdPage(Boolean openUpdatePwdPage) {
        isOpenUpdatePwdPage = openUpdatePwdPage;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

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
