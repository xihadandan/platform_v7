/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dao.impl;

import com.wellsoft.pt.app.dao.AppSystemDao;
import com.wellsoft.pt.app.dao.AppThemeDefinitionJsonDao;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.entity.AppThemeDefinitionJsonEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
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
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
@Repository
public class AppThemeDefinitionJsonDaoImpl extends AbstractJpaDaoImpl<AppThemeDefinitionJsonEntity, String>
        implements AppThemeDefinitionJsonDao {
}
