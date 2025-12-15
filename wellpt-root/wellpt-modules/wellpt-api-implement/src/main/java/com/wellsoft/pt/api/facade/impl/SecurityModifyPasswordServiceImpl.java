/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: SecurityModifyPasswordServiceImpl.java
 * @Package com.wellsoft.pt.api.facade.impl
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-11 上午10:47:46
 * @version V1.0
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.SecurityModifyPasswordRequest;
import com.wellsoft.pt.api.response.SecurityModifyPasswordResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Administrator
 * @ClassName: SecurityModifyPasswordServiceImpl
 * @Description: TODO
 * @date 2014-12-11 上午10:47:46
 */
@Service(ApiServiceName.SECURITY_MODIFYPASSWORD)
@Transactional
public class SecurityModifyPasswordServiceImpl extends BaseServiceImpl implements WellptService<SecurityModifyPasswordRequest> {
    @Autowired
    private UserService userService;

    /* (No Javadoc)
     * <p>Title: doService</p>
     * <p>Description: </p>
     * @param request
     * @return
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(SecurityModifyPasswordRequest req) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        // 生成返回结果
        SecurityModifyPasswordResponse response = new SecurityModifyPasswordResponse();
        try {
            String res = userService.changePassword(userDetails.getUserUuid(), req.getOldPassword(),
                    req.getNewPassword());
            if (!"success".equals(res)) {
                response.setCode("-1");
                response.setSuccess(false);
                response.setMsg(res);
            }
        } catch (Exception e) {
            logger.error("error", e);
        }
        return response;
    }

}
