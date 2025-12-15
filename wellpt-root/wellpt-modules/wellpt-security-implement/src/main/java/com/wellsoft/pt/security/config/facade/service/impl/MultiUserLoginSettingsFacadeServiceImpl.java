/*
 * @(#)2021-11-18 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.config.facade.service.MultiUserLoginSettingsFacadeService;
import com.wellsoft.pt.security.config.service.MultiUserLoginSettingsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表MULTI_USER_LOGIN_SETTINGS的门面服务实现类
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
@Service
public class MultiUserLoginSettingsFacadeServiceImpl extends AbstractApiFacade implements MultiUserLoginSettingsFacadeService {

    @Autowired
    private MultiUserLoginSettingsService multiUserLoginSettingsService;


}
