/*
 * @(#)2013-11-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support;

import com.wellsoft.pt.basicdata.dyview.provider.AbstractViewDataSource;
import com.wellsoft.pt.integration.security.ExchangeConfig;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-30.1	zhulh		2013-11-30		Create
 * </pre>
 * @date 2013-11-30
 */
public abstract class AbstractDataExchangeViewDataSource extends AbstractViewDataSource {

    @Autowired
    private UnitApiFacade unitApiFacade;

    /**
     * 获取当前用户的所在的挂接的单位
     *
     * @return
     */
    protected CommonUnit getCurrentUserCommonUnit() {
        List<CommonUnit> units = unitApiFacade.getCommonUnitsByBusinessTypeIdAndUserId(
                ExchangeConfig.EXCHANGE_BUSINESS_TYPE, SpringSecurityUtils.getCurrentUserId());
        return units.get(0);

    }
}
