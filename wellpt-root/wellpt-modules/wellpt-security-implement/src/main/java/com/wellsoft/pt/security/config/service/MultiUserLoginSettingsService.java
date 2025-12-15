/*
 * @(#)2021-11-18 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.service;


import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.config.dao.MultiUserLoginSettingsDao;
import com.wellsoft.pt.security.config.dto.MultiUserLoginSettingsDto;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;

import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表MULTI_USER_LOGIN_SETTINGS的service服务接口
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-11-18.1	baozh		2021-11-18		Create
 * </pre>
 * @date 2021-11-18
 */
public interface MultiUserLoginSettingsService extends JpaService<MultiUserLoginSettingsEntity, MultiUserLoginSettingsDao, String> {

    List<MultiUserLoginSettingsDto> getBySystemUnitId(String systemUnitId);

    MultiUserLoginSettingsEntity getLoginSettingsEntity(String systemUnitId);

    MultiUserLoginSettingsEntity getLoginSettingsEntity();

    /**
     * 保存配置
     *
     * @param settingsEntity
     * @return
     * @author baozh
     * @date 2021/11/19 14:32
     */
    boolean saveLoginSettingsEntity(MultiUserLoginSettingsEntity settingsEntity);


    /**
     * 方法描述
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/11/23 18:59
     */
    String getUserNamePlaceholder(String systemUnitId);

    /**
     * 查询重复登录数据
     *
     * @return
     * @author baozh
     * @date 2022/1/12 9:34
     * @params *@params
     */
    List<QueryItem> queryUserLoginDoubleInfo(Map<String, Object> queryParams, PagingInfo pagingInfo);
}
