/*
 * @(#)2018年9月30日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade;

import com.wellsoft.context.service.BaseService;

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
public interface ApiDateFacadeService extends BaseService {
    /**
     * 如何描述该方法
     *
     * @param date
     * @param format
     * @return
     */
    public String getSpecificDateByDateFormat(String date, String format);

    public Date getSpecificDateByDatewkhrParam(Date date, Double workHour, String param);
}
