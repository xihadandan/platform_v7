/*
 * @(#)2021-01-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.log.dao.BusinessOperationLogDao;
import com.wellsoft.pt.log.entity.BusinessOperationLog;
import org.springframework.stereotype.Repository;


/**
 * Description: 数据库表LOG_BUSINESS_OPERATION的DAO接口实现类
 *
 * @author zhongzh
 * @version 1.0
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-01-08.1    zhongzh        2021-01-08		Create
 * </pre>
 * @date 2021-01-08
 */
@Repository
public class BusinessOperationLogDaoImpl extends AbstractJpaDaoImpl<BusinessOperationLog, String> implements BusinessOperationLogDao {


}

