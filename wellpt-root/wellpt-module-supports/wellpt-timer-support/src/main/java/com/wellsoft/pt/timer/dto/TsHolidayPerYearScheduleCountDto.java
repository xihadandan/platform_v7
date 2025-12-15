/*
 * @(#)2021年5月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.dto;

import com.wellsoft.pt.timer.query.TsHolidayPerYearScheduleCountQueryItem;
import io.swagger.annotations.ApiModel;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 节假日每年安排数量
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
@ApiModel("节假日每年安排数量")
public class TsHolidayPerYearScheduleCountDto extends TsHolidayPerYearScheduleCountQueryItem
        implements Comparable<TsHolidayPerYearScheduleCountDto> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -387117488389958005L;

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TsHolidayPerYearScheduleCountDto o) {
        return ObjectUtils.toString(this.getYear(), StringUtils.EMPTY)
                .compareTo(ObjectUtils.toString(o.getYear(), StringUtils.EMPTY));
    }

}
