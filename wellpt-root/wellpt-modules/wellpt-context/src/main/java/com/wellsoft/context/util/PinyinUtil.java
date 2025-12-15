package com.wellsoft.context.util;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.groovy.GroovyUseable;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * 汉字拼音工具类
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
@GroovyUseable
public class PinyinUtil {
    private static final Logger LOG = LoggerFactory.getLogger(PinyinUtil.class);


    /**
     * 将汉字转换为全拼
     *
     * @param src 汉字
     * @return String 拼音
     */
    public static String getPinYin(String src) {
        return getPinYinMulti(src, false);
    }

    /**
     * 返回多音的拼音
     *
     * @param src
     * @return
     */
    public static String getPinYinMulti(String src, boolean backMulti) {
        String t4 = "";
        if (StringUtils.isBlank(src)) {
            return t4;
        }
        char[] t1 = null;
        t1 = src.toCharArray();
        String[] t2 = new String[t1.length];
        // 设置汉字拼音输出的格式
        HanyuPinyinOutputFormat t3 = new HanyuPinyinOutputFormat();
        t3.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        t3.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        t3.setVCharType(HanyuPinyinVCharType.WITH_V);
        int t0 = t1.length;
        try {
            for (int i = 0; i < t0; i++) {
                // 判断能否为汉字字符
                if (Character.toString(t1[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    t2 = PinyinHelper.toHanyuPinyinStringArray(t1[i], t3);// 将汉字的几种全拼都存到t2数组中
                    String pinyin = t2[0];// 取出该汉字全拼的第一种读音并连接到字符串t4后
                    Set<String> pinyinSet = Sets.newHashSet(t2);// 不同音调去重
                    if (pinyinSet.size() > 1 && backMulti) {
                        pinyin += "(" + StringUtils.join(ArrayUtils.subarray(pinyinSet.toArray(new String[]{}), 1, t2.length), ",") + ")";
                    }
                    t4 += pinyin;
                } else {
                    // 如果不是汉字字符，间接取出字符并连接到字符串t4后
                    t4 += Character.toString(t1[i]);
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            LOG.error(e.getMessage(), e);
        }
        return t4;
    }

    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return String
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        if (StringUtils.isBlank(str)) {
            return convert;
        }
        for (int j = 0; j < str.length(); j++) {
            char word = str.charAt(j);
            // 提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert;
    }


    public static String getLoginName(String userName) {
        if (StringUtils.isBlank(userName)) {
            return userName;
        }
        if (userName.length() <= 2) {
            return getPinYin(userName);
        }
        return getPinYin(userName.substring(0, 1)) + getPinYinHeadChar(userName.substring(1));
    }

    public static void main(String[] args) throws Exception {
        System.out.println(getPinYinMulti("单", true));
    }
}
