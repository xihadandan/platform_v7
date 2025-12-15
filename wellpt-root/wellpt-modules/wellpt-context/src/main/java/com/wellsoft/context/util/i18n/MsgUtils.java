/**
 * @Project:welloa
 * @Description: TODO(用一句话描述该文件做什么)
 * @author: Administrator
 * @version: V1.0
 * @date: 2012-10-17
 * @Copyright (c) 威尔公司-版权所有
 */

package com.wellsoft.context.util.i18n;

import com.wellsoft.context.util.ApplicationContextHolder;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * Description: 获取国际化信息工具类
 *
 * @author Asus
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月25日.1	Asus		2015年12月25日		Create
 * </pre>
 * @date 2015年12月25日
 */
public class MsgUtils {

    /**
     * @param code
     * @param args
     * @param defaultMessage
     * @param 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getMessage
     * @Description: 获取国际化信息
     */
    public static String getMessage(String code, Object[] args, String defaultMessage) {
        return ApplicationContextHolder.getBean("messageSource", MessageSource.class).getMessage(code, args,
                defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * @param code
     * @param args
     * @param 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getMessage
     * @Description: 获取国际化信息
     */
    public static String getMessage(String code, Object... args) {
        return ApplicationContextHolder.getBean("messageSource", MessageSource.class).getMessage(code, args,
                LocaleContextHolder.getLocale());
    }

}
