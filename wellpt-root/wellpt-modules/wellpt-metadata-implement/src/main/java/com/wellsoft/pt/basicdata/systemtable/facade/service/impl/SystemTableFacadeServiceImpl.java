/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.systemtable.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.systemtable.facade.service.SystemTableFacadeService;
import com.wellsoft.pt.basicdata.systemtable.service.SystemTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class SystemTableFacadeServiceImpl extends AbstractApiFacade implements SystemTableFacadeService {

    @Autowired
    SystemTableService systemTableService;

    @Override
    public Map<String, String> getColumnMap(Class clazz, String propertyName) {
        return systemTableService.getColumnMap(clazz, propertyName);
    }

    @Override
    public Map<String, Object> getColumnPropOfSet(Class clazz, String propertyName) {
        return systemTableService.getColumnPropOfSet(clazz, propertyName);
    }

}
