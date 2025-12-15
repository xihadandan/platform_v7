/*
 * @(#)2015-9-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util.regex;

import com.wellsoft.context.util.groovy.GroovyUseable;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description: 常用正则表达式工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-29.1	zhulh		2015-9-29		Create
 * </pre>
 * @date 2015-9-29
 */
@GroovyUseable
public class RegexUtils {

    /**
     * 验证Email
     */
    private static final String REGEX_EMAIL = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
    /**
     * 验证身份证号码
     */
    private static final String REGEX_ID_CARD_NUMBER = "[1-9]\\d{13,16}[a-zA-Z0-9]{1}";
    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     */
    private static final String REGEX_MOBILE_PHONE = "(\\+\\d+)?1[3458]\\d{9}$";
    /**
     * 验证固定电话号码
     */
    private static final String REGEX_TELEPHONE = "(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$";
    /**
     * 验证整数（正整数和负整数）
     */
    private static final String REGEX_DIGIT = "\\-?[1-9]\\d+";
    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     */
    private static final String REGEX_DECIMALS = "\\-?[1-9]\\d+(\\.\\d+)?";
    /**
     * 验证数字
     */
    private static final String REGEX_NUMBER = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
    /**
     * 验证空白字符
     */
    private static final String REGEX_BLANK_SPACE = "\\s+";
    /**
     * 验证中文
     */
    private static final String REGEX_CHINESE = "^[\u4E00-\u9FA5]+$";
    /**
     * 验证日期（年月日）
     */
    private static final String REGEX_BIRTHDAY = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
    /**
     * 验证URL地址
     */
    private static final String REGEX_URL = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
    /**
     * 获取网址 URL的一级域名
     */
    private static final String REGEX_DOMAIN = "(?<=http://|\\.)[^.]*?\\.(com|cn|net|org|biz|info|cc|tv)";
    /**
     * 匹配中国邮政编码
     */
    private static final String REGEX_POSTCODE = "[1-9]\\d{5}";
    /**
     * 匹配IP4地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     */
    private static final String REGEX_IP4_ADDRESS = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
    private static final Map<Integer, String> zoneNum = new HashMap<Integer, String>();
    private static final int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static final int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    static {
        zoneNum.put(11, "北京");
        zoneNum.put(12, "天津");
        zoneNum.put(13, "河北");
        zoneNum.put(14, "山西");
        zoneNum.put(15, "内蒙古");
        zoneNum.put(21, "辽宁");
        zoneNum.put(22, "吉林");
        zoneNum.put(23, "黑龙江");
        zoneNum.put(31, "上海");
        zoneNum.put(32, "江苏");
        zoneNum.put(33, "浙江");
        zoneNum.put(34, "安徽");
        zoneNum.put(35, "福建");
        zoneNum.put(36, "江西");
        zoneNum.put(37, "山东");
        zoneNum.put(41, "河南");
        zoneNum.put(42, "湖北");
        zoneNum.put(43, "湖南");
        zoneNum.put(44, "广东");
        zoneNum.put(45, "广西");
        zoneNum.put(46, "海南");
        zoneNum.put(50, "重庆");
        zoneNum.put(51, "四川");
        zoneNum.put(52, "贵州");
        zoneNum.put(53, "云南");
        zoneNum.put(54, "西藏");
        zoneNum.put(61, "陕西");
        zoneNum.put(62, "甘肃");
        zoneNum.put(63, "青海");
        zoneNum.put(64, "新疆");
        zoneNum.put(71, "台湾");
        zoneNum.put(81, "香港");
        zoneNum.put(82, "澳门");
        zoneNum.put(91, "外国");
    }

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@zuidaima.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isEmail(String email) {
        String regex = REGEX_EMAIL;
        return Pattern.matches(regex, email);
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码15位或18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isIdCardNumber(String idCard) {
        //		String regex = REGEX_ID_CARD_NUMBER;
        //		return Pattern.matches(regex, idCard);
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18))
            return false;
        final char[] cs = idCard.toUpperCase().toCharArray();
        // 校验位数
        int power = 0;
        for (int i = 0; i < cs.length; i++) {
            if (i == cs.length - 1 && cs[i] == 'X')
                break;//最后一位可以 是X或x
            if (cs[i] < '0' || cs[i] > '9')
                return false;
            if (i < cs.length - 1) {
                power += (cs[i] - '0') * POWER_LIST[i];
            }
        }

        // 校验区位码
        if (!zoneNum.containsKey(Integer.valueOf(idCard.substring(0, 2)))) {
            return false;
        }

        // 校验年份
        String year = idCard.length() == 15 ? getIdcardCalendar() + idCard.substring(6, 8) : idCard.substring(6, 10);

        final int iyear = Integer.parseInt(year);
        if (iyear < 1900 || iyear > Calendar.getInstance().get(Calendar.YEAR)) {
            return false;//1900年的PASS，超过今年的PASS
        }

        // 校验月份
        String month = idCard.length() == 15 ? idCard.substring(8, 10) : idCard.substring(10, 12);
        final int imonth = Integer.parseInt(month);
        if (imonth < 1 || imonth > 12) {
            return false;
        }

        // 校验天数
        String day = idCard.length() == 15 ? idCard.substring(10, 12) : idCard.substring(12, 14);
        final int iday = Integer.parseInt(day);
        if (iday < 1 || iday > 31)
            return false;

        // 校验"校验码"
        if (idCard.length() == 15)
            return true;
        return cs[cs.length - 1] == PARITYBIT[power % 11];
    }

    private static int getIdcardCalendar() {
        GregorianCalendar curDay = new GregorianCalendar();
        int curYear = curDay.get(Calendar.YEAR);
        int year2bit = Integer.parseInt(String.valueOf(curYear).substring(2));
        return year2bit;
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobile 移动、联通、电信运营商的号码段
     *               <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *               、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *               <p>联通的号段：130、131、132、155、156（世界风专用）、185（未启用）、186（3g）</p>
     *               <p>电信的号段：133、153、180（未启用）、189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isMobilePhone(String mobile) {
        String regex = REGEX_MOBILE_PHONE;
        return Pattern.matches(regex, mobile);
    }

    /**
     * 验证固定电话号码
     *
     * @param phone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *              <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *              数字之后是空格分隔的国家（地区）代码。</p>
     *              <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     *              对不使用地区或城市代码的国家（地区），则省略该组件。</p>
     *              <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isTelephone(String phone) {
        String regex = REGEX_TELEPHONE;
        return Pattern.matches(regex, phone);
    }

    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDigit(String digit) {
        String regex = REGEX_DIGIT;
        return Pattern.matches(regex, digit);
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isDecimals(String decimals) {
        String regex = REGEX_DECIMALS;
        return Pattern.matches(regex, decimals);
    }

    /**
     * 验证数字
     *
     * @param number
     * @return
     */
    public static boolean isNumber(String number) {
        String regex = REGEX_NUMBER;
        return Pattern.matches(regex, number);
    }

    /**
     * 验证空白字符
     *
     * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isBlankSpace(String blankSpace) {
        String regex = REGEX_BLANK_SPACE;
        return Pattern.matches(regex, blankSpace);
    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isChinese(String chinese) {
        String regex = REGEX_CHINESE;
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isBirthday(String birthday) {
        String regex = REGEX_BIRTHDAY;
        return Pattern.matches(regex, birthday);
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isURL(String url) {
        String regex = REGEX_URL;
        return Pattern.matches(regex, url);
    }

    /**
     * <pre>
     * 获取网址 URL的一级域名
     * http://www.zuidaima.com/share/1550463379442688.htm ->> zuidaima.com
     * </pre>
     *
     * @param url
     * @return
     */
    public static String isDomain(String url) {
        Pattern p = Pattern.compile(REGEX_DOMAIN, Pattern.CASE_INSENSITIVE);
        // 获取完整的域名
        // Pattern p=Pattern.compile("[^//]*?\\.(com|cn|net|org|biz|info|cc|tv)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(url);
        matcher.find();
        return matcher.group();
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isPostcode(String postcode) {
        String regex = REGEX_POSTCODE;
        return Pattern.matches(regex, postcode);
    }

    /**
     * 匹配IP4地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param ipAddress IPv4标准地址
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean isIp4Address(String ipAddress) {
        String regex = REGEX_IP4_ADDRESS;
        return Pattern.matches(regex, ipAddress);
    }

}
