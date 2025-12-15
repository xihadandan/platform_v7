/*
 * @(#)2018-12-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.config.facade.service.AppLoginPageConfigFacadeService;
import com.wellsoft.pt.security.config.service.AppLoginPageConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表APP_LOGIN_PAGE_CONFIG的门面服务实现类
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
@Service
public class AppLoginPageConfigFacadeServiceImpl extends AbstractApiFacade implements AppLoginPageConfigFacadeService {

    @Autowired
    private AppLoginPageConfigService appLoginPageConfigService;


}
