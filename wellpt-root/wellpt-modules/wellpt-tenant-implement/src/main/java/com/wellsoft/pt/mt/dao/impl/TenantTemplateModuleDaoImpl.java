/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.dao.impl;

import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.mt.dao.TenantTemplateModuleDao;
import com.wellsoft.pt.mt.entity.TenantTemplateModule;
import org.springframework.stereotype.Repository;

/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-21.1	lilin		2012-10-21		Create
 * </pre>
 * @date 2012-10-21
 */
@Repository
public class TenantTemplateModuleDaoImpl extends AbstractJpaDaoImpl<TenantTemplateModule, String> implements
        TenantTemplateModuleDao {
}
