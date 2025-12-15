/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dao.impl;

import com.wellsoft.pt.app.dao.AppThemeDefinitionDao;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

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
public class AppThemeDefinitionDaoImpl extends AbstractJpaDaoImpl<AppThemeDefinitionEntity, String> implements AppThemeDefinitionDao {
    /**
     * 根据主题ID获取数量
     *
     * @param id
     * @return
     */
    @Override
    public Long countById(String id) {
        Assert.hasLength(id, "主题ID不能为空！");

        AppThemeDefinitionEntity entity = new AppThemeDefinitionEntity();
        entity.setId(id);
        entity.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        return this.countByEntity(entity);
    }
}
