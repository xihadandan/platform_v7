/*
 * @(#)2021年5月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.query;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
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
public class TsHolidayPerYearScheduleCountQueryItem implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1794999112532510360L;

    // 年份
    @ApiModelProperty("年份")
    private String year;

    // 节假日数量
    @ApiModelProperty("节假日数量")
    private Integer count;

    /**
     * @return the year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year 要设置的year
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return the count
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count 要设置的count
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((year == null) ? 0 : year.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TsHolidayPerYearScheduleCountQueryItem other = (TsHolidayPerYearScheduleCountQueryItem) obj;
        if (year == null) {
            if (other.year != null)
                return false;
        } else if (!year.equals(other.year))
            return false;
        return true;
    }

}
