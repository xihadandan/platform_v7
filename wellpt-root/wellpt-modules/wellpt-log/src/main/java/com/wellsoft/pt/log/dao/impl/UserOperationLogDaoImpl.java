/*
 * @(#)2013-10-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.log.dao.UserOperationLogDao;
import com.wellsoft.pt.log.entity.UserOperationLog;
import org.springframework.stereotype.Repository;

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
@Repository
public class UserOperationLogDaoImpl extends AbstractJpaDaoImpl<UserOperationLog, String> implements
        UserOperationLogDao {

}
