/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.service;

/**
 * Description: 可租户化接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
public interface Tenantable {

    /**
     * 返回租户账号
     *
     * @return
     */
    public String getTenant();

    /**
     * 返回租户ID
     *
     * @return
     */
    public String getTenantId();
}
