/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.user.dao.UserExtraInfoDaoImpl;
import com.wellsoft.pt.user.entity.UserExtraInfoEntity;
import com.wellsoft.pt.user.service.UserExtraInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表USER_EXTRA_INFO的service服务接口实现类
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
@Service
public class UserExtraInfoServiceImpl extends AbstractJpaServiceImpl<UserExtraInfoEntity, UserExtraInfoDaoImpl, String>
        implements UserExtraInfoService {

    @Override
    @Transactional
    public void deleteByAccountUuid(String accountUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("accountUuid", accountUuid);
        this.dao.deleteByNamedSQL("deleteByAccountUuid", values);
    }

    @Override
    public List<UserExtraInfoEntity> getListByAccountUuid(String accountUuid) {
        UserExtraInfoEntity entity = new UserExtraInfoEntity();
        entity.setAccountUuid(accountUuid);
        List<UserExtraInfoEntity> entities = this.dao.listByEntity(entity);
        return entities;
    }
}
