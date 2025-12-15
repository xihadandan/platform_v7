package com.wellsoft.pt.security.oauth2.token;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/10/15
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/10/15    chenq		2019/10/15		Create
 * </pre>
 */
public class LocalOAuth2UserInfo implements Serializable {

    private String accountNumber;

    private String password;

    private String userName;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
