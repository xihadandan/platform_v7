package com.wellsoft.context.util.barcode;

import org.springframework.util.Assert;

/**
 * Description: UUID/CODE转换工具类
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
public class CodeUtils {

    /**
     * UUID剔除“-”转化为CODE
     *
     * @param uuid
     * @return code
     */
    public static String enCode(String uuid) {
        Assert.notNull(uuid, "parameter[uuid]  is null");
        return uuid.toUpperCase().replaceAll("-", "");
    }

    /**
     * CODE补充“-”转化为UUID
     *
     * @param code
     * @return uuid
     */
    public static String deCode(String code) {
        Assert.notNull(code, "parameter[code]  is null");
        StringBuilder sb = new StringBuilder(code);
        sb.insert(8, "-"); // 9 - 1
        sb.insert(13, "-"); // 14 - 1
        sb.insert(18, "-"); // 19 - 1
        sb.insert(23, "-"); // 24 - 1
        return sb.toString().toLowerCase();
    }

    /**
     * 在字符串指定位置加入字符串
     *
     * @param string       原始字符串
     * @param index        insert位置
     * @param insertString 将加入字符串
     * @return 拼接后的字符串
     */
    public static String insertString(String string, int index, String insertString) {
        if (insertString == null || string == null) {
            return string;
        }
        if (index < 0 || index >= string.length()) {
            throw new IllegalArgumentException("parameter[index]  is Out Of Array");
        }
        StringBuilder sb = new StringBuilder(string.substring(0, index));
        sb.append(insertString);
        sb.append(string.substring(index, string.length()));
        return sb.toString();
    }

    public static void main(String[] args) {
        String uuid = "d8326bc3-1616-449d-a494-6eb3c1aa9b90";
        String code = enCode(uuid);
        System.out.println(code);
        String deuuid = deCode(code);
        System.out.println(deuuid);
        System.out.println(deuuid.equals(uuid));
    }
}
