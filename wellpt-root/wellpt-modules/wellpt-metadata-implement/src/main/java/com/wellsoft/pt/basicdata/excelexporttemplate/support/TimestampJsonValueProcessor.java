package com.wellsoft.pt.basicdata.excelexporttemplate.support;

import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Description: 如何描述该类
 *
 * @author FashionSUN
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-15.1	FashionSUN		2014-10-15		Create
 * </pre>
 * @date 2014-10-15
 */
public class TimestampJsonValueProcessor implements JsonValueProcessor {
    /**
     * 字母 日期或时间元素 表示 示例 <br>
     * G Era 标志符 Text AD <br>
     * y 年 Year 1996; 96 <br>
     * M 年中的月份 Month July; Jul; 07 <br>
     * w 年中的周数 Number 27 <br>
     * W 月份中的周数 Number 2 <br>
     * D 年中的天数 Number 189 <br>
     * d 月份中的天数 Number 10 <br>
     * F 月份中的星期 Number 2 <br>
     * E 星期中的天数 Text Tuesday; Tue<br>
     * a Am/pm 标记 Text PM <br>
     * H 一天中的小时数（0-23） Number 0 <br>
     * k 一天中的小时数（1-24） Number 24<br>
     * K am/pm 中的小时数（0-11） Number 0 <br>
     * h am/pm 中的小时数（1-12） Number 12 <br>
     * m 小时中的分钟数 Number 30 <br>
     * s 分钟中的秒数 Number 55 <br>
     * S 毫秒数 Number 978 <br>
     * z 时区 General time zone Pacific Standard Time; PST; GMT-08:00 <br>
     * Z 时区 RFC 822 time zone -0800 <br>
     */
    public static final String Default_DATE_PATTERN = "yyyy-MM-dd";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private DateFormat dateFormat;

    public TimestampJsonValueProcessor(String datePattern) {
        try {
            dateFormat = new SimpleDateFormat(datePattern);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            dateFormat = new SimpleDateFormat(Default_DATE_PATTERN);
        }
    }

    @Override
    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        return process(value);
    }

    @Override
    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        return process(value);
    }

    private Object process(Object value) {
        if (value == null) {
            return "";
        } else if (value instanceof Timestamp) {
            return dateFormat.format((Timestamp) value);
        } else {
            return value;
        }
    }
}
