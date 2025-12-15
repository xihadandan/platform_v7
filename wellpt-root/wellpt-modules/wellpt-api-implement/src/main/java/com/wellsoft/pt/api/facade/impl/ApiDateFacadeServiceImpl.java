/*
 * @(#)2018年9月30日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.enums.ApiDateEnum;
import com.wellsoft.pt.api.facade.ApiDateFacadeService;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkHourUtils;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author linxr
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月30日.1	linxr		2018年9月30日		Create
 * </pre>
 * @date 2018年9月30日
 */
@Service
public class ApiDateFacadeServiceImpl extends BaseServiceImpl implements ApiDateFacadeService {

    /**
     * 传入字符串和日期格式，返回指定日期
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.ApiDateFacadeService#getSpecificDateByDateFormat(java.util.Date, java.lang.String)
     */
    @Override
    public String getSpecificDateByDateFormat(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date newDate = new Date();
        try {
            newDate = sdf.parse(date);
        } catch (ParseException e) {
            logger.error(e.getMessage(), e);
        }
        return newDate + "";
    }

    /**
     * 传递日期和工作日天数，返回指定日期前后多少个工作日的日期
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.ApiDateFacadeService#getSpecificDateByDatewkhrParam(java.util.Date, int, java.lang.String)
     */
    @Override
    public Date getSpecificDateByDatewkhrParam(Date date, Double workHour, String param) {
        Date newDate = new Date();
        if (ApiDateEnum.AFTER.getName().equals(param)) {
            newDate = WorkHourUtils.getWorkDate(date, workHour, WorkUnit.WorkingDay);
        } else if (ApiDateEnum.BEFORE.getName().equals(param)) {
            newDate = WorkHourUtils.getWorkDate(date, 0 - workHour, WorkUnit.WorkingDay);
        }
        return newDate;
    }

}
