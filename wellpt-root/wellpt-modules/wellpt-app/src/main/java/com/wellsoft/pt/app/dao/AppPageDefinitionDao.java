/*
 * @(#)2018年4月2日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dao;

import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.jpa.dao.JpaDao;

/**
 * Description: 页面定义的dao接口类
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
public interface AppPageDefinitionDao extends JpaDao<AppPageDefinition, String> {

    void updateUnDefaultByAppPiUuid(String appPiUuid);

    void updateDefaultTrueByUuid(String appPiUuid, String uuid);
}
