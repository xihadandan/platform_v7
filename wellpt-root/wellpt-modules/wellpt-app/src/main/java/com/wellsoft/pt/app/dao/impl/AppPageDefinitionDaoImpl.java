/*
 * @(#)2018年4月2日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dao.impl;

import com.wellsoft.pt.app.dao.AppPageDefinitionDao;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

/**
 * Description: 页面定义实体dao服务实现类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月2日.1	chenqiong		2018年4月2日		Create
 * </pre>
 * @date 2018年4月2日
 */
@Repository
public class AppPageDefinitionDaoImpl extends
        AbstractJpaDaoImpl<AppPageDefinition, String> implements
        AppPageDefinitionDao {

    @Override
    public void updateUnDefaultByAppPiUuid(String appPiUuid) {
        this.updateByHQL(
                "update AppPageDefinition set isDefault=false where isDefault=true and appPiUuid='" + appPiUuid + "'",
                null);
    }

    @Override
    public void updateDefaultTrueByUuid(String appPiUuid, String uuid) {
        this.updateByHQL(
                "update AppPageDefinition set isDefault=false where isDefault=true and appPiUuid='" + appPiUuid + "' and uuid <> '" + uuid + "'",
                null);
    }
}
