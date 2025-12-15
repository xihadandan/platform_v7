/*
 * @(#)2019年6月6日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.dto;

import com.wellsoft.pt.app.bean.AppApplicationBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月6日.1	zhulh		2019年6月6日		Create
 * </pre>
 * @date 2019年6月6日
 */
@ApiModel("应用实体")
public class AppApplicationDto extends AppApplicationBean {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6885199946805511005L;

    @ApiModelProperty("归属集成信息UUID")
    private String belongToAppPiUuid;
    @ApiModelProperty("上级集成信息UUID")
    private String parentAppPiUuid;
    @ApiModelProperty("集成信息UUID")
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
