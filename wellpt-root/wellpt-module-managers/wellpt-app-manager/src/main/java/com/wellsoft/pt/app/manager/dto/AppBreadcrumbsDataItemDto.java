/*
 * @(#)2019年5月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.dto;

import com.wellsoft.context.dto.DataItem;

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
public class AppBreadcrumbsDataItemDto extends DataItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7202306436388598663L;

    // 导航ID
    private String navId;

    // 应用类型
    private int appType;

    // 产品UUID
    private String appProductUuid;

    // 系统UUID
    private String appSystemUuid;

    // 产品集成信息UUID
    private String appPiUuid;

    // 上级产品集成信息UUID
    private String parentAppPiUuid;

    /**
     * @return the navId
     */
    public String getNavId() {
        return navId;
    }

    /**
     * @param navId 要设置的navId
     */
    public void setNavId(String navId) {
        this.navId = navId;
    }

    /**
     * @return the appType
     */
    public int getAppType() {
        return appType;
    }

    /**
     * @param appType 要设置的appType
     */
    public void setAppType(int appType) {
        this.appType = appType;
    }

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
     * @return the appSystemUuid
     */
    public String getAppSystemUuid() {
        return appSystemUuid;
    }

    /**
     * @param appSystemUuid 要设置的appSystemUuid
     */
    public void setAppSystemUuid(String appSystemUuid) {
        this.appSystemUuid = appSystemUuid;
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

    /**
     * @return the parentAppPiUuid
     */
    public String getParentAppPiUuid() {
        return parentAppPiUuid;
    }

    /**
     * @param parentAppPiUuid 要设置的parentAppPiUuid
     */
    public void setParentAppPiUuid(String parentAppPiUuid) {
        this.parentAppPiUuid = parentAppPiUuid;
    }

}
