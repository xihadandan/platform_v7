/*
 * @(#)2013-10-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.service.impl;

import com.google.gson.Gson;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.log.dao.UserOperationLogDao;
import com.wellsoft.pt.log.entity.UserOperationLog;
import com.wellsoft.pt.log.service.UserOperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-14.1	zhulh		2013-10-14		Create
 * </pre>
 * @date 2013-10-14
 */
@Service
public class UserOperationLogServiceImpl extends
        AbstractJpaServiceImpl<UserOperationLog, UserOperationLogDao, String>
        implements UserOperationLogService {

    /**
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void save(UserOperationLog userOperationLog) {
        logger.info("用户操作日志--[ " + new Gson().toJson(userOperationLog) + " ]");
        dao.save(userOperationLog);
    }

}
