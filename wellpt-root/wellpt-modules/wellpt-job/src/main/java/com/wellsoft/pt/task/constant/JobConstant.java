package com.wellsoft.pt.task.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/11/22
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/22    chenq		2018/11/22		Create
 * </pre>
 */
public class JobConstant {

    public static final Map<String, Object> engWeekDay2ChnDay = Maps.newHashMap();

    static {
        engWeekDay2ChnDay.put("SUN", "星期日");
        engWeekDay2ChnDay.put("MON", "星期一");
        engWeekDay2ChnDay.put("TUE", "星期二");
        engWeekDay2ChnDay.put("WED", "星期三");
        engWeekDay2ChnDay.put("THU", "星期四");
        engWeekDay2ChnDay.put("FRI", "星期五");
        engWeekDay2ChnDay.put("SAT", "星期六");
    }
}
