/*
 * @(#)2013-12-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.response;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-4-28.1	Administrator		2014-4-28		Create
 * </pre>
 * @date 2014-4-28
 */
public class DXResponse extends Response {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -675223415341510543L;

    private Map<String, String> params;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

}
