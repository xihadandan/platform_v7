/*
 * @(#)2021-06-28 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.log.facade.service.LogManageDetailsFacadeService;
import com.wellsoft.pt.log.service.LogManageDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表LOG_MANAGE_DETAILS的门面服务实现类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-06-28.1	zenghw		2021-06-28		Create
 * </pre>
 * @date 2021-06-28
 */
@Service
public class LogManageDetailsFacadeServiceImpl extends AbstractApiFacade implements LogManageDetailsFacadeService {

    @Autowired
    private LogManageDetailsService logManageDetailsService;

}
