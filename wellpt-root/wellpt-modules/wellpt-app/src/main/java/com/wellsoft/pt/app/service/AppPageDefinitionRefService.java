/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppPageDefinitionRefDao;
import com.wellsoft.pt.app.entity.AppPageDefinitionRefEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月13日.1	zhulh		2019年6月13日		Create
 * </pre>
 * @date 2019年6月13日
 */
public interface AppPageDefinitionRefService extends
        JpaService<AppPageDefinitionRefEntity, AppPageDefinitionRefDao, String> {

    /**
     * @param refUuid
     * @param appPiUuid
     */
    void remove(String refUuid, String appPiUuid);

    /**
     * 判断页面是否被引用
     *
     * @param pageUuid
     * @return
     */
    boolean isReferenced(String pageUuid);

}
