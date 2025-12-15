/*
 * @(#)2014-3-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.report.support;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description: 报表工作日计算自定义函数
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-7.1	zhouyq		2014-3-7		Create
 * </pre>
 * @date 2014-3-7
 */
@Deprecated
public class WorkDays /*extends AbstractFunction */ {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 获取某段日期内的工作日数
     * <p>
     * (non-Javadoc)
     *
     * @see com.fr.script.AbstractFunction#run(java.lang.Object[])
     */
//	@Override
    public Object run(Object[] args) {
        BasicDataApiFacade basicDataApiFacade = (BasicDataApiFacade) ApplicationContextHolder
                .getBean(BasicDataApiFacade.class);
        List<Date> dataList = new ArrayList<Date>();
        Object para;
        for (int i = 0; i < args.length; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            para = args[i];
            try {
                dataList.add(sdf.parse((String) para));
            } catch (ParseException e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        Date date1 = dataList.get(0);//开始日期
        Date date2 = dataList.get(1);//结束日期
        WorkPeriod workPeriod = basicDataApiFacade.getWorkPeriod(date1, date2);
        return workPeriod.getDays();
    }
}
