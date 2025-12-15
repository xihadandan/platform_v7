package com.wellsoft.webmail;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang.StringUtils;

import javax.mail.internet.MimeUtility;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/6/5
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/5    chenq		2018/6/5		Create
 * </pre>
 */
public class Test {

    public static void main(String[] arrs) throws Exception {
        String p = "Microsoft =?gb2312?b?1cq7p7CyyKvQxc+i0enWpA==?=";
        String x = "2015Äê¡°597¸£ÖÝÈË²ÅÍø¡±¸£ÖÝÈËÃñ»áÌÃÇï¼¾ÏÖ³¡ÕÐÆ¸»á";
        String y = "2015年“597福州人才网”福州人民会堂秋季现场招聘会";
        String z = "=?utf-8?B?5oKo55qE5pm66IGU6LSm5oi35pyJMeS4quaWsOeahOiuv+mX?=\n\r =?utf-8?B?ru+8jOafpeeci+iuv+mXrg==?=";
//        p.substring(p.indexOf("=?"),p.lastIndexOf("?=")+2)
        System.out.println(decodeWord(z));


    }

    private static String decodeWord(String text) {
        try {
            if (text.indexOf("=?") != -1 && text.indexOf("?=") != -1) {

                if (text.indexOf("\r") == -1) {//没有换行的情况下直接转
                    return MimeUtility.decodeText(text);
                }

                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                        text.getBytes());
                BufferedReader br = new BufferedReader(new InputStreamReader(byteArrayInputStream));
                String r = "";
                String l = null;
                int start1 = text.indexOf(63, 2);
                String charset = text.substring(2, start1);
                String encoding = text.substring(start1 + 1, text.indexOf(63, start1 + 1));
                String ce = String.format("=?%s?%s?", charset, encoding);
                while ((l = br.readLine()) != null) {
                    if (StringUtils.isNotBlank(l)) {
                        l.trim();
                        r += l.substring(l.indexOf(ce) + ce.length(), l.length() - 2);
                    }
                }
                return MimeUtility.decodeWord(String.format("%s%s?=", ce, r));
            } else {
                return toGb2312(text);
            }
        } catch (Exception e) {
            text = toGb2312(text);
        }
        return text;
    }

    private static String toGb2312(String str) {
        if (str == null) return null;
        String retStr = str;
        byte b[];
        try {
            b = str.getBytes(Charsets.ISO_8859_1);

            for (int i = 0; i < b.length; i++) {
                byte b1 = b[i];
                if (b1 == 63)
                    break;    //1
                else if (b1 > 0)
                    continue;//2
                else if (b1 < 0) {        //不可能为0，0为字符串结束符
                    retStr = new String(b,
                            "GBK");
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            //  e.printStackTrace();    //To  change  body  of  catch  statement  use  File    |  Settings    |  File  Templates.
        }
        return retStr;
    }

    public static boolean isMessyCode(String strName) {
        Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
        Matcher m = p.matcher(strName);
        String after = m.replaceAll("");
        String temp = after.replaceAll("\\p{P}", "");
        char[] ch = temp.trim().toCharArray();
        float chLength = 0;
        float count = 0;
        for (int i = 0; i < ch.length; i++) {
            char c = ch[i];
            if (!Character.isLetterOrDigit(c)) {
                if (!isChinese(c)) {
                    count = count + 1;
                }
                chLength++;
            }
        }
        float result = count / chLength;
        if (result > 0.4) {
            return true;
        } else {
            return false;
        }
    }

    private static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

}
