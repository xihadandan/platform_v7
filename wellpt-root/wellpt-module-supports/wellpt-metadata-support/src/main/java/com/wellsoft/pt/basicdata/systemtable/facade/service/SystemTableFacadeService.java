/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.systemtable.facade.service;

import com.wellsoft.context.service.Facade;

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
public interface SystemTableFacadeService extends Facade {

    /**
     * @param c
     * @param string
     * @return
     */
    Map<String, String> getColumnMap(Class c, String string);

    /**
     * @param c
     * @param string
     * @return
     */
    Map<String, Object> getColumnPropOfSet(Class c, String string);

}
