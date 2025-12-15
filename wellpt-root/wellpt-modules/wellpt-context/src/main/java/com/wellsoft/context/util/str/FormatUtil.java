package com.wellsoft.context.util.str;

import net.sf.json.JSONObject;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * 格式化工具类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月22日.1	Asus		2015年12月22日		Create
 * </pre>
 * @date 2015年12月22日
 */
public class FormatUtil {
    @SuppressWarnings("unused")
    private static Logger log = LoggerFactory.getLogger(FormatUtil.class);

    /**
     * 判断访问发起的端是否是手机
     *
     * @param request http请求数据
     * @return ture-发起端手机,false-PC
     */
    public static final boolean isMobile(HttpServletRequest request) {
        String userAgent = request.getHeader("user-agent").toLowerCase();
        return userAgent.contains("android") || userAgent.contains("iphone");
    }

    /**
     * 转化数据大小
     *
     * @param size 大小单位B
     * @return 带单位数据大小字符串
     */
    public static String formatSize(long size) {
        DecimalFormat df = new DecimalFormat("#0.0");
        String str;

        if (size / 1024 < 1) {
            str = size + " B";
        } else if (size / 1048576 < 1) {
            str = df.format(size / 1024.0) + " KB";
        } else if (size / 1073741824 < 1) {
            str = df.format(size / 1048576.0) + " MB";
        } else if (size / 1099511627776L < 1) {
            str = df.format(size / 1073741824.0) + " GB";
        } else if (size / 1125899906842624L < 1) {
            str = df.format(size / 1099511627776.0) + " TB";
        } else {
            str = "BIG";
        }

        return str;
    }

    /**
     * 格式化时间（HH:mm:ss）
     *
     * @param time 时间毫秒数
     * @return 时间字符串
     */
    public static String formatTime(long time) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String str = df.format(time);
        return str;
    }

    /**
     * 格式化时间（yyyy-MM-dd）
     *
     * @param time 时间毫秒数
     * @return 时间字符串
     */
    public static String formatDate(long time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String str = df.format(time);
        return str;
    }

    /**
     * 格式化时间（yyyy-MM-dd HH:mm:ss）
     *
     * @param time 时间毫秒数
     * @return 时间字符串
     */
    public static String formatFullDate(long time) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = df.format(time);
        return str;
    }

    /**
     * 如何描述该方法
     *
     * @param time 时间毫秒数
     * @return 未知
     */
    @Deprecated
    public static String formatSeconds(long time) {
        long hours, minutes, seconds, mseconds;
        mseconds = time % 1000;
        time = time / 1000;
        hours = time / 3600;
        time = time - (hours * 3600);
        minutes = time / 60;
        time = time - (minutes * 60);
        seconds = time;
        return (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":"
                + (seconds < 10 ? "0" + seconds : seconds) + "."
                + (mseconds < 10 ? "00" + mseconds : (mseconds < 100 ? "0" + mseconds : mseconds));
    }

    /**
     * 格式化时间（dd-MM-yyyy HH:mm:ss）
     *
     * @param cal 时间Calendar对象
     * @return 时间字符串
     */
    public static String formatDate(Calendar cal) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(cal.getTime());
    }

    /**
     * 将String数组转为String
     *
     * @param values String数组
     * @return String字符串，空转换为“NULL”
     */
    public static String formatArray(String[] values) {
        if (values != null) {
            if (values.length == 1) {
                return values[0];
            } else {
                return ArrayUtils.toString(values);
            }
        } else {
            return "NULL";
        }
    }

    /**
     * 将Object对象转换为String
     *
     * @param value Object对象
     * @return String字符串，空转换为“NULL”
     */
    public static String formatObject(Object value) {
        if (value != null) {
            if (value instanceof Object[]) {
                return ArrayUtils.toString(value);
            } else {
                return value.toString();
            }
        } else {
            return "NULL";
        }
    }

    /**
     * 避开HTML标签
     *
     * @param str html内容
     * @return 转换后的html内容
     */
    public static String escapeHtml(String str) {
        return str.replace("<", "&lt;").replace(">", "&gt;");
    }

    // mapStr 格式为 {"B0000000163":"314","O0000000394":"1"}
    public static HashMap<String, String> jsonStr2Map(String str) {
        if (StringUtils.isNotBlank(str) && !"{}".equals(str)) {
            JSONObject obj = JSONObject.fromObject(str);
            HashMap<String, String> map = new HashMap<String, String>();
            for (Object key : obj.keySet()) {
                map.put(key.toString(), obj.get(key).toString());
            }
            return map;
        }
        return new HashMap<String, String>();
    }
}
