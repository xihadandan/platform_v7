package com.wellsoft.context.util.xml.converter;

import com.thoughtworks.xstream.converters.basic.DateConverter;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.Date;

/**
 * Description: xml 的时间转换器，基于时间格式的转换
 *
 * @author chenq
 * @date 2018/11/9
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/9    chenq		2018/11/9		Create
 * </pre>
 */
public class DateFormateConverter extends DateConverter {

    private String formate = "yyyy-MM-dd HH:mm:ss";//默认的时间格式

    public DateFormateConverter() {
        super();
    }

    public DateFormateConverter(String formate) {
        super();
        this.formate = formate;
    }

    @Override
    public String toString(Object obj) {
        final Date date = (Date) obj;
        try {
            return DateFormatUtils.format(date, formate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date.getTime() + "";
    }
}
