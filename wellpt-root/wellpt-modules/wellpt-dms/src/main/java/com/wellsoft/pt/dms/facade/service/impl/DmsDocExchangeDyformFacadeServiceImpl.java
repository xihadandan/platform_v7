/*
 * @(#)2021-07-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.dms.facade.service.DmsDocExchangeDyformFacadeService;
import com.wellsoft.pt.dms.service.DmsDocExchangeDyformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description: 数据库表DMS_DOC_EXCHANGE_DYFORM的门面服务实现类
 *
 * @author yt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-07-13.1	yt		2021-07-13		Create
 * </pre>
 * @date 2021-07-13
 */
@Service
public class DmsDocExchangeDyformFacadeServiceImpl extends AbstractApiFacade implements DmsDocExchangeDyformFacadeService {

    @Autowired
    private DmsDocExchangeDyformService dmsDocExchangeDyformService;


}
