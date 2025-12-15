/*
 * @(#)2018-12-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.config.dao.AppLoginPageConfigDao;
import com.wellsoft.pt.security.config.entity.AppLoginPageConfigEntity;

import java.util.List;

/**
 * Description: 数据库表APP_LOGIN_PAGE_CONFIG的service服务接口
 *
 * @author linst
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-12-07.1	leo		2018-12-07		Create
 * </pre>
 * @date 2018-12-07
 */
public interface AppLoginPageConfigService extends
        JpaService<AppLoginPageConfigEntity, AppLoginPageConfigDao, String> {


    public void saveAppLoginPageConfigEntity(AppLoginPageConfigEntity po);

    /**
     * 获取指定单位ID的登录页配置
     *
     * @param systemUnitId
     * @return
     */
    AppLoginPageConfigEntity getBySystemUnitId(String systemUnitId);

    /**
     * 初始化单位的登录页
     *
     * @param systemUnitId
     * @return
     */
    AppLoginPageConfigEntity saveInitPageConfig(String systemUnitId);

    AppLoginPageConfigEntity getLoginConfigBySystemUnitId(String systemUnitId,
                                                          boolean image2base64);

    List<AppLoginPageConfigEntity> listByLoginPageUrl(String loginUrl);

    public void turnOffLoginPageSwitch();
}
