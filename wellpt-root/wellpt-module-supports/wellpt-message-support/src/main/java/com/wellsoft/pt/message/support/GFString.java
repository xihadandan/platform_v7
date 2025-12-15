package com.wellsoft.pt.message.support;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * 字符串处理
 *
 * @author 范华鹏
 * @version 1.0
 */
public class GFString {
    public final static int MAXFIELDS = 150;
    /**
     * GSM字符对照表
     * 第一第二列组成一个GSM字母，当第一列为0时，表示该GSM字母只需要第二列的值即可表示。
     * "^"，"{"，"}"，"\"，"[","~"，"]"，"|"，"欧元符号" 为双字节字符， 且第一个字节byte值为27。
     * 其他都为单字节字符，但有些字符和普通的编码值不同，这里就不一一列举了。
     */
    private static final byte[][] gsm = {{0, 0, 64}, {0, 1, -93}, {0, 2, 36}, {0, 3, -91}, {0, 4, -24},
            {0, 5, -23}, {0, 6, -7}, {0, 7, -20}, {0, 8, -14}, {0, 9, -57}, {0, 10, 10}, {0, 11, -40},
            {0, 12, -8}, {0, 13, 13}, {0, 14, -59}, {0, 15, -27}, {0, 16, 0}, {0, 17, 95}, {0, 18, 0},
            {0, 19, 0}, {0, 20, 0}, {0, 21, 0}, {0, 22, 0}, {0, 23, 0}, {0, 24, 0}, {0, 25, 0},
            {0, 26, 0}, {0, 27, 0}, {27, 10, 12}, {27, 20, 94}, {27, 40, 123}, {27, 41, 125},
            {27, 47, 92}, {27, 60, 91}, {27, 61, 126}, {27, 62, 93}, {27, 64, 124}, {27, 101, -92},
            {0, 28, -58}, {0, 29, -26}, {0, 30, -33}, {0, 31, -55}, {0, 32, 32}, {0, 33, 33},
            {0, 34, 34}, {0, 35, 35}, {0, 36, -92}, {0, 37, 37}, {0, 38, 38}, {0, 39, 39}, {0, 40, 40},
            {0, 41, 41}, {0, 42, 42}, {0, 43, 43}, {0, 44, 44}, {0, 45, 45}, {0, 46, 46}, {0, 47, 47},
            {0, 48, 48}, {0, 49, 49}, {0, 50, 50}, {0, 51, 51}, {0, 52, 52}, {0, 53, 53}, {0, 54, 54},
            {0, 55, 55}, {0, 56, 56}, {0, 57, 57}, {0, 58, 58}, {0, 59, 59}, {0, 60, 60}, {0, 61, 61},
            {0, 62, 62}, {0, 63, 63}, {0, 64, -95}, {0, 65, 65}, {0, 66, 66}, {0, 67, 67}, {0, 68, 68},
            {0, 69, 69}, {0, 70, 70}, {0, 71, 71}, {0, 72, 72}, {0, 73, 73}, {0, 74, 74}, {0, 75, 75},
            {0, 76, 76}, {0, 77, 77}, {0, 78, 78}, {0, 79, 79}, {0, 80, 80}, {0, 81, 81}, {0, 82, 82},
            {0, 83, 83}, {0, 84, 84}, {0, 85, 85}, {0, 86, 86}, {0, 87, 87}, {0, 88, 88}, {0, 89, 89},
            {0, 90, 90}, {0, 91, -60}, {0, 92, -42}, {0, 93, -47}, {0, 94, -36}, {0, 95, -89},
            {0, 96, -65}, {0, 97, 97}, {0, 98, 98}, {0, 99, 99}, {0, 100, 100}, {0, 101, 101},
            {0, 102, 102}, {0, 103, 103}, {0, 104, 104}, {0, 105, 105}, {0, 106, 106}, {0, 107, 107},
            {0, 108, 108}, {0, 109, 109}, {0, 110, 110}, {0, 111, 111}, {0, 112, 112}, {0, 113, 113},
            {0, 114, 114}, {0, 115, 115}, {0, 116, 116}, {0, 117, 117}, {0, 118, 118}, {0, 119, 119},
            {0, 120, 120}, {0, 121, 121}, {0, 122, 122}, {0, 123, -28}, {0, 124, 124}, {0, 125, 125},
            {0, 126, 126}};
    static Logger logger = Logger.getLogger(GFString.class);

    /**
     * 把相临的两个字符对换，字符串长度为奇数时最后加“F”
     *
     * @param src
     * @return 处理后的字符串
     */
    public static String interChange(String src) {
        String result = "";

        if (src != null) {
            if (src.length() % 2 != 0)
                src += "F";
            src += "0";
            result = "";
            for (int i = 0; i < src.length() - 2; i += 2) {
                result += src.substring(i + 1, i + 2);
                result += src.substring(i, i + 1);
            }
        }

        return result;
    }

    /**
     * byte类型转换为十六进制字符串，长度为2位，不足的左补0
     *
     * @param s
     * @return 转换byte后得到的十六进制字符串
     */
    public static String byte2hex(byte s) {
        String result = null;

        result = Integer.toHexString(s & 0xFF);

        if (result.length() == 1) {
            result = "0" + result;
        }
        return result;
    }

    /**
     * byte数组转换为十六进制字符串，长度为2位，不足的左补0
     *
     * @param bytes
     * @return 转换bytes后得到的十六进制字符串
     */
    public static String byte2hex(byte[] bytes) {
        StringBuilder retString = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            retString.append(Integer.toHexString(0x0100 + (bytes[i] & 0x00FF)).substring(1).toUpperCase());
        }
        return retString.toString();
    }

    /**
     * 十六进制之付出转换为byte数组
     *
     * @param hex
     * @return 转换字符串后得到的byte数组
     */
    public static byte[] hex2byte(String hex) {
        byte[] bts = new byte[hex.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }

    /**
     * 将字符串转换为手机中使用的字符串，进行拆分，7BIT编码后返回PDU编码
     *
     * @param src          源字符串
     * @param concacencted 是否支持长短信
     * @return 编码后得到的字符串向量
     */
    public static List<String> stringToHexString(String src, boolean concacencted) {
        List<String> al = new ArrayList<String>();
        int n = 0;

        if (src != null && src.length() == src.getBytes().length) {
            byte[] asii = src.getBytes();
            int l = asii.length;
            int gl = gsm.length;
            byte[] b = new byte[l * 2];

            StringBuilder sb = new StringBuilder(l * 2);
            boolean lock;
            for (int i = 0; i < l; i++) {
                lock = false;
                for (int j = 0; j < gl; j++) {
                    if (asii[i] == gsm[j][2]) {
                        if (gsm[j][0] != 0) {
                            b[n] = gsm[j][0];
                            sb.append((char) gsm[j][0]);
                            n++;
                        }
                        b[n] = gsm[j][1];
                        sb.append((char) gsm[j][1]);
                        n++;
                        lock = true;
                        break;
                    }
                }
                if (!lock) {
                    logger.debug("GSM不存在的单字节字符【" + (char) asii[i] + "】【" + (byte) asii[i] + "】");
                    sb.append((char) asii[i]);
                    n++;
                }
            }

            int max = 160;
            if (n > max && concacencted)
                max = 153;

            String str = sb.toString();
            String tmp = str;
            int len = max;
            while (str != null) {
                if (str.length() > max) {
                    if (str.charAt(max - 1) == (char) 27)
                        len = max - 1;
                    else
                        len = max;
                    tmp = str.substring(0, len);
                    str = str.substring(len);
                    al.add(encode7bit(tmp));
                } else {
                    al.add(encode7bit(str));
                    str = null;
                }
            }
        }

        return al;
    }

    /**
     * 对字符串进行7BIT编码,把ASCII码值最高位为0的字符串进行压缩转换成8位二进制表示的字符串
     *
     * @param text 需编码的字符串
     * @return 编码后的字符串
     */
    public static String encode7bit(String text) {
        byte oldBytes[] = text.getBytes();
        BitSet bitSet = new BitSet(text.length() * 8);
        int value1 = 0;
        for (int i = 0; i < text.length(); i++) {
            for (int j = 0; j < 7; j++) {
                value1 = i * 7 + j;
                if ((oldBytes[i] & 1 << j) != 0)
                    bitSet.set(value1);
            }

        }

        int value2;
        if ((++value1 / 56) * 56 != value1)
            value2 = value1 / 8 + 1;
        else
            value2 = value1 / 8;
        if (value2 == 0)
            value2 = 1;
        byte newBytes[] = new byte[value2];
        for (int i = 0; i < value2; i++) {
            for (int j = 0; j < 8; j++)
                if (value1 + 1 > i * 8 + j && bitSet.get(i * 8 + j))
                    newBytes[i] |= (byte) (1 << j);

        }

        return byte2hex(newBytes);
    }

    /**
     * 对7-BIT编码进行解码
     *
     * @param src 十六进制的字符串，且为偶数个
     * @return 源字符串
     */
    public static String decode7bit(String src) {
        String result = null;
        int[] b;
        String temp = null;
        byte srcAscii;
        byte left = 0;
        if (src != null && src.length() % 2 == 0) {
            result = "";
            b = new int[src.length() / 2];
            temp = src + "0";
            for (int i = 0, j = 0, k = 0; i < temp.length() - 2; i += 2, j++) {
                b[j] = Integer.parseInt(temp.substring(i, i + 2), 16);

                k = j % 7;
                srcAscii = (byte) (((b[j] << k) & 0x7F) | left);
                result += (char) srcAscii;
                left = (byte) (b[j] >>> (7 - k));
                if (k == 6) {
                    result += (char) left;
                    left = 0;
                }
                if (j == src.length() / 2)
                    result += (char) left;
            }
        }
        return result;
    }

    /**
     * 对字符串进行8BIT解码
     *
     * @param src
     * @return 解码得到的字符串
     */
    public static String decode8bit(String src) {
        String temp = src;
        String result = "";
        for (int i = 0; i < src.length() - 2; i += 2)
            result += (char) Integer.parseInt(temp.substring(i, i + 2), 16);
        return result;
    }

    /**
     * 把UNICODE编码的字符串转化成汉字编码的字符串
     *
     * @param hexString
     * @return 转换后得到的字符串
     */
    public static String unicode2gb(String hexString) {
        StringBuilder sb = new StringBuilder();

        if (hexString == null)
            return null;

        for (int i = 0; i + 4 <= hexString.length(); i = i + 4) {
            try {
                int j = Integer.parseInt(hexString.substring(i, i + 4), 16);
                sb.append((char) j);
            } catch (NumberFormatException e) {
                return hexString;
            }
        }

        return sb.toString();
    }

    /**
     * 把汉字转化成UNICODE编码的字符串
     *
     * @param gbString
     * @param concacencted 是否支持长短信
     * @return UNICODE编码后的字符串
     */
    public static List<String> gb2unicode(String gbString, boolean concacencted) {
        List<String> v = new ArrayList<String>();

        if (gbString == null)
            return null;

        int maxLen = 70;
        if (gbString.length() > 70 && concacencted)
            maxLen = 67;

        String tmp = gbString;
        while (tmp != null)
            if (tmp.length() > maxLen) {
                String str = tmp.substring(0, maxLen);
                tmp = tmp.substring(maxLen);
                v.add(getHexString(str));
            } else {
                v.add(getHexString(tmp));
                tmp = null;
            }

        return v;
    }

    /**
     * 将字符串转换为十六进制字符串
     *
     * @param str
     * @return 转换后的字符串
     */
    public static String getHexString(String str) {
        String result = "";
        String temp = "";
        int value;
        char[] c = new char[str.length()];
        StringBuilder sb = new StringBuilder(str);
        sb.getChars(0, sb.length(), c, 0);

        for (int i = 0; i < c.length; i++) {
            value = (int) c[i];
            temp = Integer.toHexString(value);
            result += GFString.fill(temp, 4);
        }
        return result.toUpperCase();
    }

    /**
     * 判断字符串长度，对短字符串进行左补0
     *
     * @param temp
     * @param totalbit
     * @return 补位后的字符串
     */
    public static String fill(String temp, int totalbit) {
        String t = temp;
        while (t.length() < totalbit) {
            t = "0" + t;
        }
        return t;
    }

    /**
     * 判断字符串中是否包含中文等双字节字符
     *
     * @param text
     * @return 如果字符串包含双字节字符则返回True
     */
    public static boolean isContainsChineseChar(String text) {
        if (text.length() == text.getBytes().length)
            return false;
        else
            return true;
    }

    /**
     * 从动态表格的值中返回数组
     *
     * @param str
     * @return 二维数组
     */
    public static String[][] getArrayFromTable(String str) {
        return getArrayFromTable(str, "||", "$$");
    }

    /**
     * 从动态表格的值中返回数组
     *
     * @param str      动态表格字符串
     * @param colsplit 行分隔符
     * @param rowsplit 列分隔符
     * @return 二维数组
     */
    public static String[][] getArrayFromTable(String str, String colsplit, String rowsplit) {
        String[][] ret = null;
        if (str != null && !str.equals("")) {
            String[] row = split(str, colsplit);
            int l = row.length;
            ret = new String[l][];
            for (int i = 0; i < l; i++) {
                String[] line = split(row[i], rowsplit);
                ret[i] = line;
            }
        }
        return ret;
    }

    /**
     * 将字符串分割为数组
     *
     * @param psSouce
     * @param psSpacer
     * @return 得到的数组
     */
    public static String[] split(String psSouce, String psSpacer) {
        String[] results = new String[MAXFIELDS];
        int i = 0;
        if (psSouce == null || psSpacer == null)
            return null;
        while (!psSouce.equals("")) {
            if (psSouce.indexOf(psSpacer) != -1) {
                results[i] = psSouce.substring(0, psSouce.indexOf(psSpacer));
                psSouce = psSouce.substring(psSouce.indexOf(psSpacer) + psSpacer.length());
                if (psSouce.equals(""))
                    results[i + 1] = psSouce;
            } else {
                results[i] = psSouce;
                psSouce = "";
            }
            i++;
        }

        int Arrlen = 1;
        for (int m = 0; m < MAXFIELDS; m++) {
            if (results[m] == null) {
                Arrlen = m;
                break;
            }
        }

        if (Arrlen < 1) {
            Arrlen = 1;
        }
        String[] res = new String[Arrlen];
        if (Arrlen < 2) {
            res[0] = results[0];
        } else {
            for (int k = 0; k < Arrlen; k++) {
                res[k] = new String(results[k]);
            }
        }
        results = null;
        return res;
    }
}
