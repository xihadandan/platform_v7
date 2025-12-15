/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.dto;

import com.wellsoft.pt.timer.entity.TsHolidayEntity;
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
 * 2021年5月24日.1	zhulh		2021年5月24日		Create
 * </pre>
 * @date 2021年5月24日
 */
@ApiModel("节假日管理")
public class TsHolidayDto extends TsHolidayEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7145706170492812366L;

    // 节假日日期名称，如阳历5月20日
    @ApiModelProperty("节假日名称，如阳历5月20日")
    private String holidayDateName;

    /**
     * @return the holidayDateName
     */
    public String getHolidayDateName() {
        return holidayDateName;
    }

    /**
     * @param holidayDateName 要设置的holidayDateName
     */
    public void setHolidayDateName(String holidayDateName) {
        this.holidayDateName = holidayDateName;
    }

}
