/*
 * @(#)2016年9月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personalinfo.facade.service.impl;

import com.wellsoft.pt.app.personalinfo.facade.service.PersonalInfoService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.dto.EncryptChangePasswordByIdDto;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 平台应用发起工作门面服务实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月10日.1	zhulh		2016年9月10日		Create
 * </pre>
 * @date 2016年9月10日
 */
@Service
@Transactional
public class PersonalInfoServiceImpl extends BaseServiceImpl implements PersonalInfoService {
    @Autowired
    private MultiOrgUserService userService;

    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;

    @Override
    @Transactional(readOnly = true)
    public OrgUserVo getUserInfo(String userId) {
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }
        OrgUserVo user = userService.getUserById(userId);
        return user;
    }

    /**
     * 获取当前用户信息
     *
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public OrgUserVo getCurrentUserInfo() {
        return getUserInfo(SpringSecurityUtils.getCurrentUserId());
    }

    @Override
    public String modifyCurrentUserPassword(String oldPwd, String newPwd) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        try {
            multiOrgUserAccountFacadeService.changePasswordById(userDetails.getUserId(), oldPwd, newPwd);
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return "success";
    }

    @Override
    public EncryptChangePasswordByIdDto modifyCurrentUserPasswordEncrypt(String oldPwd, String newPwd) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        EncryptChangePasswordByIdDto dto = new EncryptChangePasswordByIdDto();
        try {
            dto = multiOrgUserAccountFacadeService.encryptChangePasswordById(userDetails.getUserId(), oldPwd, newPwd);
        } catch (Exception ex) {
            logger.error("修改密码报错：", ex);
            dto.setMessage(ex.getMessage());
            dto.setSuccess(Boolean.FALSE);
        }
        return dto;
    }
}
