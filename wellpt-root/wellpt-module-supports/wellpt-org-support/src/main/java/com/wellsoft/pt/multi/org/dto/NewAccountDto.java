package com.wellsoft.pt.multi.org.dto;

/**
 * Description: 新账号输出对象
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
public class NewAccountDto {
    // 登录名
    private String loginName;
    // 登录密码
    private String password;
    // 姓名
    private String userName;

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
