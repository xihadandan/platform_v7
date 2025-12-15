/*
 * @(#)2019年5月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.dto;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月29日.1	zhulh		2019年5月29日		Create
 * </pre>
 * @date 2019年5月29日
 */
public class AppBreadcrumbsParamDto extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8931954128838603192L;

    // 产品UUID
    private String appProductUuid;

    // 产品集成信息UUID
    private String appPiUuid;

    /**
     * @return the appProductUuid
     */
    public String getAppProductUuid() {
        return appProductUuid;
    }

    /**
     * @param appProductUuid 要设置的appProductUuid
     */
    public void setAppProductUuid(String appProductUuid) {
        this.appProductUuid = appProductUuid;
    }

    /**
     * @return the appPiUuid
     */
    public String getAppPiUuid() {
        return appPiUuid;
    }

    /**
     * @param appPiUuid 要设置的appPiUuid
     */
    public void setAppPiUuid(String appPiUuid) {
        this.appPiUuid = appPiUuid;
    }

}
