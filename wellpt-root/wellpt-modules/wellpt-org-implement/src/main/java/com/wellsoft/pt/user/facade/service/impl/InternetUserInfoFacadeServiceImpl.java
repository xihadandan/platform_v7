/*
 * @(#)2021-04-08 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.user.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.user.facade.service.InternetUserInfoFacadeService;
import com.wellsoft.pt.user.service.InternetUserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表INTERNET_USER_INFO的门面服务实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-04-08.1	zenghw		2021-04-08		Create
 * </pre>
 * @date 2021-04-08
 */
@Service
public class InternetUserInfoFacadeServiceImpl extends AbstractApiFacade implements InternetUserInfoFacadeService {

    @Autowired
    private InternetUserInfoService internetUserInfoService;


}
