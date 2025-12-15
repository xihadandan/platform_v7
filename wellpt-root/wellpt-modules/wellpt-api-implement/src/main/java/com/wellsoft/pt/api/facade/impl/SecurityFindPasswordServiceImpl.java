/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: SecurityFindPasswordServiceImpl.java
 * @Package com.wellsoft.pt.api.facade.impl
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午10:58:18
 * @version V1.0
 */
package com.wellsoft.pt.api.facade.impl;

import com.jasson.im.api.APIClient;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.SecurityFindPasswordRequest;
import com.wellsoft.pt.api.response.SecurityFindPasswordResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.bean.UserBean;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * @author Administrator
 * @ClassName: SecurityFindPasswordServiceImpl
 * @Description: TODO
 * @date 2014-12-11 上午10:58:18
 */
@Service(ApiServiceName.SECURITY_FINDPASSWORD)
@Transactional
public class SecurityFindPasswordServiceImpl extends BaseServiceImpl implements
        WellptService<SecurityFindPasswordRequest> {
    @Autowired
    private UserService userService;
	/* @Autowired
	private SendMailService sendMailService; */

    /* (No Javadoc)
     * <p>Title: doService</p>
     * <p>Description: </p>
     * @param request
     * @return
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(SecurityFindPasswordRequest req) {
        UserDetails userDetails =
                SpringSecurityUtils.getCurrentUser();
        // 生成返回结果
        SecurityFindPasswordResponse response =
                new SecurityFindPasswordResponse();
        try {
            UserBean userBean = userService.getBean(userDetails.getUserUuid());
            // 生成6位随机密码
            Random random = new Random();
            StringBuilder sb = new StringBuilder();
            for (int index = 0; index < 6; index++) {
                sb.append(random.nextInt(10));
            }
            userBean.setPassword(sb.toString());
            logger.debug("生成6位随机密码" + sb.toString());
            // 保存密码
            userService.saveBean(userBean);
            String msg = "您的密码已经更改成:" + sb.toString() + "请及时登录系统并修改密码！";
            // 发送短信
            APIClient apiClient = new APIClient();
            apiClient.sendSM(userBean.getMobilePhone(), msg,
                    System.currentTimeMillis());
            // 发送邮件
			/* sendMailService.sendBySystem(
					new String[]{userBean.getEmail()}, "您的密码已经更改成功", msg); */
            response.setMsg("密码已通过手机短信和电子邮件发送，请及时查收！");
        } catch (Exception e) {
            logger.error("error", e);
        }
        return response;
    }

}
