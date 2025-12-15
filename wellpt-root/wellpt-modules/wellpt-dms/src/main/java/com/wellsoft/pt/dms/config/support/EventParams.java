/*
 * @(#)2018年6月10日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月10日.1	zhulh		2018年6月10日		Create
 * </pre>
 * @date 2018年6月10日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventParams extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6076119893603353982L;

    private Map<String, Object> params = new HashMap<String, Object>(0);

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
