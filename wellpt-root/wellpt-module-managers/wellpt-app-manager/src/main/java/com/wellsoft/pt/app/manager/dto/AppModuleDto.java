/*
 * @(#)2019年6月4日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.dto;

import com.wellsoft.pt.app.bean.AppModuleBean;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月4日.1	zhulh		2019年6月4日		Create
 * </pre>
 * @date 2019年6月4日
 */
public class AppModuleDto extends AppModuleBean {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6726481122801055849L;

    // 归属产品集成信息UUID
    private String belongToAppPiUuid;
    // 归属系统UUID
    private String belongToAppSystemUuid;
    // 产品UUID
    private String appProductUuid;
    // 集成信息UUID
    private String appPiUuid;

    /**
     * @return the belongToAppPiUuid
     */
    public String getBelongToAppPiUuid() {
        return belongToAppPiUuid;
    }

    /**
     * @param belongToAppPiUuid 要设置的belongToAppPiUuid
     */
    public void setBelongToAppPiUuid(String belongToAppPiUuid) {
        this.belongToAppPiUuid = belongToAppPiUuid;
    }

    /**
     * @return the belongToAppSystemUuid
     */
    public String getBelongToAppSystemUuid() {
        return belongToAppSystemUuid;
    }

    /**
     * @param belongToAppSystemUuid 要设置的belongToAppSystemUuid
     */
    public void setBelongToAppSystemUuid(String belongToAppSystemUuid) {
        this.belongToAppSystemUuid = belongToAppSystemUuid;
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
