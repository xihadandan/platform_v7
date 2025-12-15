/*
 * @(#)2013-5-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.facade;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.service.ExchangeDataConfigService;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-27.1	ruanhg		2015-4-27		Create
 * </pre>
 * @date 2015-4-27
 */
@Service
@Transactional
public class DataExchangeApiFacade extends AbstractApiFacade {
    @Autowired
    private UnitApiFacade unitApiFacade;
    @Autowired
    private ExchangeDataConfigService exchangeDataConfigService;

    public boolean hasExternalSystem(String unitId) {
        // unitApiFacade.getTenantIdByCommonUnitId("");
        List<ExchangeSystem> syss = exchangeDataConfigService.getExchangeSystems(unitId);
        if (syss.size() > 0) {
            return true;
        }
        return false;

    }
}
