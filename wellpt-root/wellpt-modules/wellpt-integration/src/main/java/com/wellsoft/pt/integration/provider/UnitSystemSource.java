/*
 * @(#)2013-4-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.provider;

import com.wellsoft.pt.integration.entity.ExchangeSystem;

import java.util.Map;

/**
 * Description: 解析单位系统接口类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-2-9.1	ruanhg		2015-2-9		Create
 * </pre>
 * @date 2015-2-9
 */
public interface UnitSystemSource {

    /**
     * 获取解析单位ID
     *
     * @return
     */
    public String getUnitSystemSourceId();

    /**
     * 获取解析单位的名字
     *
     * @return
     */
    public String getUnitSystemSourceName();

    /**
     * 通过单位id及业务数据解析对接系统
     *
     * @param Unit
     * @param BusinessData
     * @return
     */
    public ExchangeSystem getUnitSystem(String Unit, Map<String, Object> BusinessData);

}
