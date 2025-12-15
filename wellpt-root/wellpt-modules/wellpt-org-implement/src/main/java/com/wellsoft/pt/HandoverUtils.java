package com.wellsoft.pt;

import cn.hutool.core.date.DateUtil;
import com.wellsoft.context.exception.BusinessException;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Description: 工作交接工具类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2022/4/13.1	    zenghw		2022/4/13		    Create
 * </pre>
 * @date 2022/4/13
 */
public class HandoverUtils {

    /**
     * 获取工作交接执行时间
     * 最小时间分钟级别
     *
     * @param workTime 格式：01:00
     * @return java.util.Date
     **/
    public static Date getWorkDateTime(String workTime) {
        Assert.hasText(workTime, "workTime不能为空");
        Date getWorkDateTime = null;
        String[] times = workTime.split(":");
        if (times.length < 2) {
            throw new BusinessException("workTime的格式不对，正确格式：01:00");
        }
        Integer nowHour = DateUtil.hour(new Date(), Boolean.TRUE);
        Integer nowMinute = DateUtil.minute(new Date());
        String dateStr = null;
        if (Integer.valueOf(times[0]).equals(nowHour)) {
            // 小时数一样 比较分钟数
            if (Integer.valueOf(times[1]).compareTo(nowMinute) >= 0) {
                Date tomorrowDateTime = DateUtil.offsetDay(new Date(), 1);
                dateStr = DateUtil.formatDate(tomorrowDateTime);
            } else {
                dateStr = DateUtil.formatDate(new Date());
            }
            String dateTimeStr = dateStr + " " + workTime + ":00";
            getWorkDateTime = DateUtil.parseDateTime(dateTimeStr);
        } else if (Integer.valueOf(times[0]).compareTo(nowHour) < 0) {
            Date tomorrowDateTime = DateUtil.offsetDay(new Date(), 1);
            dateStr = DateUtil.formatDate(tomorrowDateTime);
            String dateTimeStr = dateStr + " " + workTime + ":00";
            getWorkDateTime = DateUtil.parseDateTime(dateTimeStr);
        } else {
            dateStr = DateUtil.formatDate(new Date());
            String dateTimeStr = dateStr + " " + workTime + ":00";
            getWorkDateTime = DateUtil.parseDateTime(dateTimeStr);
        }
        return getWorkDateTime;
    }
}
