/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.InternetUserInfoDaoImpl;
import com.wellsoft.pt.user.entity.InternetUserInfoEntity;

/**
 * Description: 数据库表INTERNET_USER_INFO的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-04-08.1	zenghw		2021-04-08		Create
 * </pre>
 * @date 2021-04-08
 */
public interface InternetUserInfoService extends JpaService<InternetUserInfoEntity, InternetUserInfoDaoImpl, String> {

    /**
     * 获取互联网用户信息详情
     *
     * @param accountUuid
     * @return com.wellsoft.pt.user.entity.InternetUserInfoEntity
     **/
    public InternetUserInfoEntity getDetailByAccountUuid(String accountUuid);
}
