/*
 * @(#)2021年5月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.dto;

import com.wellsoft.pt.timer.entity.TsWorkTimePlanEntity;
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
 * 2021年5月25日.1	zhulh		2021年5月25日		Create
 * </pre>
 * @date 2021年5月25日
 */
@ApiModel("工作时间方案")
public class TsWorkTimePlanDto extends TsWorkTimePlanEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -778375480477196066L;

    // 新版本提示
    @ApiModelProperty("新版本提示")
    private String newVersionTip;

    // 是否立即生效
    @ApiModelProperty("是否立即生效，是true否false")
    private boolean activeRightNow;

    /**
     * @return the newVersionTip
     */
    public String getNewVersionTip() {
        return newVersionTip;
    }

    /**
     * @param newVersionTip 要设置的newVersionTip
     */
    public void setNewVersionTip(String newVersionTip) {
        this.newVersionTip = newVersionTip;
    }

    /**
     * @return the activeRightNow
     */
    public boolean isActiveRightNow() {
        return activeRightNow;
    }

    /**
     * @param activeRightNow 要设置的activeRightNow
     */
    public void setActiveRightNow(boolean activeRightNow) {
        this.activeRightNow = activeRightNow;
    }

}
