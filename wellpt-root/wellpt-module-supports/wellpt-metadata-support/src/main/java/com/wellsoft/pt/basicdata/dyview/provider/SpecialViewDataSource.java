/*
 * @(#)2014-6-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.provider;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-12.1	wubin		2014-6-12		Create
 * </pre>
 * @date 2014-6-12
 */
public interface SpecialViewDataSource {

    public Map<String, Object> getSpecialFieldValue(Map<String, Object> requestParams, String[] responseParams);
}
