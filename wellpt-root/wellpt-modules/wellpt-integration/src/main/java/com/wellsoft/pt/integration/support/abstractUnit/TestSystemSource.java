/*
 * @(#)2013-6-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.support.abstractUnit;

import com.wellsoft.pt.integration.entity.ExchangeSystem;
import com.wellsoft.pt.integration.provider.AbstractUnitSystemSource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 批次信息
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-28.1	Administrator		2013-11-28		Create
 * </pre>
 * @date 2013-11-28
 */
@Component
public class TestSystemSource extends AbstractUnitSystemSource {

    @Override
    public String getUnitSystemSourceId() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getUnitSystemSourceName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExchangeSystem getUnitSystem(String Unit, Map<String, Object> BusinessData) {
        // TODO Auto-generated method stub
        return null;
    }

}
