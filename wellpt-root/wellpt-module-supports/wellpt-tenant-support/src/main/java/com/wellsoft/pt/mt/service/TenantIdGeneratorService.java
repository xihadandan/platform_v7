/*
 * @(#)2013-12-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.service.BaseService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-12-5.1	zhulh		2013-12-5		Create
 * </pre>
 * @date 2013-12-5
 */
public interface TenantIdGeneratorService extends BaseService {
    public <ENTITY extends IdEntity> String generate(Class<ENTITY> entityClass, String pattern);
}
