/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.UserExtraInfoDaoImpl;
import com.wellsoft.pt.user.entity.UserExtraInfoEntity;

import java.util.List;

/**
 * Description: 数据库表USER_EXTRA_INFO的service服务接口
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
public interface UserExtraInfoService extends JpaService<UserExtraInfoEntity, UserExtraInfoDaoImpl, String> {

    /**
     * 删除用户信息扩展表通过账号Uuid
     *
     * @param accountUuid
     * @return void
     **/
    public void deleteByAccountUuid(String accountUuid);

    /**
     * 获取用户扩展列表数据
     *
     * @param accountUuid
     * @return void
     **/
    public List<UserExtraInfoEntity> getListByAccountUuid(String accountUuid);
}
