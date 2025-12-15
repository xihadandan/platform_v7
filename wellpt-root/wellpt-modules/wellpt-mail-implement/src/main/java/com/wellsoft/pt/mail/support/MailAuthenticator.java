package com.wellsoft.pt.mail.support;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Description: 邮件密码验证类
 *
 * @author wuzq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1	wuzq		2013-2-7		Create
 * </pre>
 * @date 2013-3-1
 */
public class MailAuthenticator extends Authenticator {
    private String username;
    private String password;

    /**
     * @param username
     * @param password
     */
    public MailAuthenticator(String username, String password) {
        super();
        this.username = username;
        this.password = password;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.mail.Authenticator#getPasswordAuthentication()
     */
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        // TODO Auto-generated method stub
        return new PasswordAuthentication(username, password);
    }

}
