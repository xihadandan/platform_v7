package com.wellsoft.oauth2.web.controller;

import com.wellsoft.oauth2.entity.OAuthClientDetailEntity;
import com.wellsoft.oauth2.service.OAuthClientDetailService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.savedrequest.DefaultSavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/10    chenq		2019/9/10		Create
 * </pre>
 */
@Controller
public class LoginController {

    @Autowired
    OAuthClientDetailService oAuthClientDetailService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request) {
        DefaultSavedRequest defaultSavedRequest = (DefaultSavedRequest) request.getSession().getAttribute("SPRING_SECURITY_SAVED_REQUEST");
        if (defaultSavedRequest != null) {
            String[] clientId = defaultSavedRequest.getParameterValues("client_id");
            if (clientId != null) {
                OAuthClientDetailEntity clientDetailEntity = oAuthClientDetailService.getByClientId(clientId[0]);
                if (clientDetailEntity != null && StringUtils.isNotBlank(clientDetailEntity.getLoginPage())) {
                    return clientDetailEntity.getLoginPage();
                }
            }
        }
        return "login";
    }


    @RequestMapping("/")
    public String main() {
        return "main";
    }

}
