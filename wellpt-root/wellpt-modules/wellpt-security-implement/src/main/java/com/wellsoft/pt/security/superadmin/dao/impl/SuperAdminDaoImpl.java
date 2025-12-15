/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.superadmin.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.superadmin.dao.SuperAdminDao;
import com.wellsoft.pt.security.superadmin.entity.SuperAdmin;
import org.springframework.stereotype.Repository;

@Repository
public class SuperAdminDaoImpl extends AbstractJpaDaoImpl<SuperAdmin, String> implements SuperAdminDao {

}
