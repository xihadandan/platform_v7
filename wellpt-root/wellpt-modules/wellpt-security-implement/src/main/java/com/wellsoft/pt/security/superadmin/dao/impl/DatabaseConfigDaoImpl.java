/*
 * @(#)2013-4-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.superadmin.dao.DatabaseConfigDao;
import com.wellsoft.pt.security.superadmin.entity.DatabaseConfig;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-24.1	rzhu		2013-4-24		Create
 * </pre>
 * @date 2013-4-24
 */
@Repository
public class DatabaseConfigDaoImpl extends AbstractJpaDaoImpl<DatabaseConfig, String> implements DatabaseConfigDao {

}
