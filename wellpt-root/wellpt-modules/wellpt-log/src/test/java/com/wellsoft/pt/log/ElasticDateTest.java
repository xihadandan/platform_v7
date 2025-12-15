/*
 * @(#)2020年3月6日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.log;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年3月6日.1	wangrf		2020年3月6日		Create
 * </pre>
 * @date 2020年3月6日
 */
public class ElasticDateTest {

    // 测试查询的时间范围
    @Test
    public void test() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        //        Date start = sdf.parse("2020-02-03");
        //        Date end = sdf.parse("2020-02-05");
        //        // now day
        //        int saveDay = 30;
        //        Date nowDay = DateUtils.truncate(new Date(), Calendar.DATE);
        //        Date minDate = new Date(nowDay.getTime() - saveDay * 24 * 3600 * 1000l);
        //        System.out.println(sdf.format(minDate));
        //        // 时间不符合处理
        //        if (end.getTime() <= minDate.getTime()) {
        //            // 查询的时间是最近的三十天
        //        }
        //        //
        //        if (start.getTime() < minDate.getTime()) {
        //            start = minDate;
        //        }
        //        //
        Date nowDay = DateUtils.truncate(new Date(), Calendar.DATE);
        Date minDate = DateUtils.addDays(nowDay, -(30 - 1));
        System.out.println(sdf.format(minDate));
    }

}
