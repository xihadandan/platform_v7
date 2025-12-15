/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.service;


import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.log.dao.BusinessOperationLogDao;
import com.wellsoft.pt.log.entity.BusinessOperationLog;

/**
 * Description: 数据库表LOG_BUSINESS_OPERATION的service服务接口
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
public interface BusinessOperationLogService extends JpaService<BusinessOperationLog, BusinessOperationLogDao, String> {

    public void saveLog(BusinessOperationLog entity);
}
