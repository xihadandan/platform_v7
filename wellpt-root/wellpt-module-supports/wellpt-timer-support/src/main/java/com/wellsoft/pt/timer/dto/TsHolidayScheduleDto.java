/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.dto;

import com.wellsoft.pt.timer.entity.TsHolidayScheduleEntity;
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
@ApiModel("节假日安排")
public class TsHolidayScheduleDto extends TsHolidayScheduleEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6560566799807560902L;

    // 节假日名称
    @ApiModelProperty("节假日名称")
    private String holidayName;

    // 具体年份节假日日期，格式yyyy-MM-dd
    @ApiModelProperty("具体年份节假日实例日期，格式yyyy-MM-dd")
    private String holidayInstanceDate;

    /**
     * @return the holidayName
     */
    public String getHolidayName() {
        return holidayName;
    }

    /**
     * @param holidayName 要设置的holidayName
     */
    public void setHolidayName(String holidayName) {
        this.holidayName = holidayName;
    }

    /**
     * @return the holidayInstanceDate
     */
    public String getHolidayInstanceDate() {
        return holidayInstanceDate;
    }

    /**
     * @param holidayInstanceDate 要设置的holidayInstanceDate
     */
    public void setHolidayInstanceDate(String holidayInstanceDate) {
        this.holidayInstanceDate = holidayInstanceDate;
    }

}
