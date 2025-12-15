/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.log.dao.BusinessOperationLogDao;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import com.wellsoft.pt.log.service.BusinessOperationLogService;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表LOG_BUSINESS_OPERATION的service服务接口实现类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-08.1	zhongzh		2021-01-08		Create
 * </pre>
 * @date 2021-01-08
 */
@Service
public class BusinessOperationLogServiceImpl extends
        AbstractJpaServiceImpl<BusinessOperationLog, BusinessOperationLogDao, String> implements
        BusinessOperationLogService {

    @Override
    public void saveLog(BusinessOperationLog entity) {
        dao.getSession().save(entity);
    }
}
