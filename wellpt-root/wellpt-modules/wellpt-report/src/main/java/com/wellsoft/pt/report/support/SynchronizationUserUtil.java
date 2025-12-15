package com.wellsoft.pt.report.support;

import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

//import com.fr.privilege.providers.dao.AbstractPasswordValidator;

@Deprecated
public class SynchronizationUserUtil /*extends AbstractPasswordValidator*/ {
    private static final long serialVersionUID = 1L;
    private PasswordEncoder passwordEncoder = new Md5PasswordEncoder();

    public boolean validatePassword(String localPassword, String clinetPassword) {
        String user_loginName = null;
        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            user_loginName = request.getParameter("username");//获取单点登录请求参数
            System.out.println("user_loginName:" + user_loginName);
        }
        String newPassword = passwordEncoder.encodePassword(clinetPassword, user_loginName);
        return localPassword.equals(newPassword);
    }
}
