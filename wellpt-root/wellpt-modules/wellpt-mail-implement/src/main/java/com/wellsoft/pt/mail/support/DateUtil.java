package com.wellsoft.pt.mail.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

/**
 * Description: 日期工具类
 *
 * @author wuzq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-1	wuzq		2013-2-7		Create
 * </pre>
 * @date 2013-3-1
 */
public class DateUtil {
    private static Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 获得下一个月
     */
    public static Date addDate(Date date, int n) {
        try {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int day = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, day + n);
            return calendar.getTime();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return new Date();
        }
    }

    /**
     * 获取当前日期和时间
     *
     * @return
     */
    public static Date getCurrentDate() {
        return new Date();
    }
}
