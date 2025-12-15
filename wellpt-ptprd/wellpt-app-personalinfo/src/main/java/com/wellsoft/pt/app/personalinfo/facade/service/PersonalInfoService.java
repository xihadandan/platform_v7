/*
 * @(#)2016年9月10日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personalinfo.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.dto.EncryptChangePasswordByIdDto;

/**
 * Description: 平台应用个人信息门面服务接口
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月14日.1	wujx		2016年9月14日		Create
 * </pre>
 * @date 2016年9月14日
 */
public interface PersonalInfoService extends BaseService {

    /**
     * 获取当前用户信息
     *
     * @return
     */
    OrgUserVo getCurrentUserInfo();

    /**
     * 修改当前用户密码
     *
     * @param oldPwd
     * @param newPwd
     * @return
     */
    String modifyCurrentUserPassword(String oldPwd, String newPwd);

    /**
     * 修改当前用户密码
     * -加密传输
     *
     * @param oldPwd
     * @param newPwd
     * @return
     */
    EncryptChangePasswordByIdDto modifyCurrentUserPasswordEncrypt(String oldPwd, String newPwd);

    public abstract OrgUserVo getUserInfo(String userId);

}
