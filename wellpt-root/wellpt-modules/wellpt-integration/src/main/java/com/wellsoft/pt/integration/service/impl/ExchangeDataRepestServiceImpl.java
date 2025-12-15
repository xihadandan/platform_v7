/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service.impl;

import com.wellsoft.pt.integration.dao.ExchangeDataRepestDao;
import com.wellsoft.pt.integration.entity.ExchangeDataRepest;
import com.wellsoft.pt.integration.service.ExchangeDataRepestService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月17日.1	chenqiong		2018年4月17日		Create
 * </pre>
 * @date 2018年4月17日
 */
@Service
public class ExchangeDataRepestServiceImpl extends
        AbstractJpaServiceImpl<ExchangeDataRepest, ExchangeDataRepestDao, String> implements ExchangeDataRepestService {

    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIngYZYM() {
        return dao.getExchangeDataRepestIngYZYM();
    }

    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIngGSDJ() {
        return dao.getExchangeDataRepestIngGSDJ();
    }

    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIngSSDJ() {
        return dao.getExchangeDataRepestIngSSDJ();
    }

    @Override
    public List<ExchangeDataRepest> getExchangeDataRepestIng() {
        return dao.getExchangeDataRepestIng();
    }

}
