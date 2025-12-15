/*
 * @(#)2018年4月17日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.service;

import com.wellsoft.pt.integration.dao.ExchangeSystemDao;
import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface ExchangeSystemService extends JpaService<ExchangeSystem, ExchangeSystemDao, String> {

    List<ExchangeSystem> getBeanByIds(String toSystem);

    List<ExchangeSystem> getExSystemListByUnids(String unids);

    List<ExchangeSystem> getExchangeSystemsByUnit(String unitId);

    ExchangeSystem getExchangeSystemByUnitAndType1(String unitId, String typeId);

    ExchangeSystem getExchangeSystemByUnitAndType(String unitId, String typeId);

    List<ExchangeSystem> findByExample(ExchangeSystem example);

    /**
     * @param systemId
     * @return
     */
    ExchangeSystem getById(String systemId);

}
