/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dao;

import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.jpa.dao.JpaDao;

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
public interface AppThemeDefinitionDao extends JpaDao<AppThemeDefinitionEntity, String> {
    /**
     * 根据主题ID获取数量
     *
     * @param id
     * @return
     */
    Long countById(String id);
}
