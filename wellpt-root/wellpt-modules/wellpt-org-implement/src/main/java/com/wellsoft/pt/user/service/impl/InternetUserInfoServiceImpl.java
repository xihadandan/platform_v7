/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.user.dao.InternetUserInfoDaoImpl;
import com.wellsoft.pt.user.entity.InternetUserInfoEntity;
import com.wellsoft.pt.user.service.InternetUserInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 数据库表INTERNET_USER_INFO的service服务接口实现类
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
public class InternetUserInfoServiceImpl
        extends AbstractJpaServiceImpl<InternetUserInfoEntity, InternetUserInfoDaoImpl, String>
        implements InternetUserInfoService {

    @Override
    public InternetUserInfoEntity getDetailByAccountUuid(String accountUuid) {
        InternetUserInfoEntity entity = new InternetUserInfoEntity();
        entity.setAccountUuid(accountUuid);
        List<InternetUserInfoEntity> entities = this.dao.listByEntity(entity);
        if (entities.size() == 0) {
            return new InternetUserInfoEntity();
        }
        return entities.get(0);
    }
}
