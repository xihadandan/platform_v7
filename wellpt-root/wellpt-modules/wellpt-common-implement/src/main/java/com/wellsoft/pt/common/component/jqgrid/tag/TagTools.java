package com.wellsoft.pt.common.component.jqgrid.tag;

import java.lang.reflect.Field;

public class TagTools {

    static String template = "<attribute>\r" + "\n\t<name>?</name>\r" + "\n\t<required>false</required>\r"
            + "\n\t<rtexprvalue>true</rtexprvalue>\r" + "</attribute>";

    /**
     * 键值对 转换成 html属性字符串
     *
     * @param args
     * @return
     */
    public static String KeyValuePair2Attribute(String... args) {
        return KeyValuePair2Properties(' ', args);
    }

    /**
     * 键值对 转换成 json属性字符串
     *
     * @param args
     * @return
     */
    public static String KeyValuePair2Json(String... args) {
        return KeyValuePair2Properties(',', args);
    }

    /**
     * 键值对 转换成 html属性字符串
     *
     * @param delimiter 分割符
     *                  当 delimiter为空格时,转换成html属性 即 KeyValuePair2Attribute(' ', "name:张三");  -->  name="张三"
     *                  当 delimiter为逗号时,转换成json属性 即 KeyValuePair2Attribute(',', "name:张三");  --> ,name:"张三"
     *                  KeyValuePair2Attribute(',', "isboy:false:true");  	-->  isboy:false
     *                  KeyValuePair2Attribute(',', "isboy:false:false");  -->  isboy:"false"
     *                  KeyValuePair2Attribute(',', "isboy:false");  		-->  isboy:"false"
     * @param args
     * @return
     */
    public static String KeyValuePair2Properties(char delimiter, String... args) {
        if (args == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0, j = 0; i < args.length; i++) {
            int k = args[i].indexOf(":");
            int l = args[i].lastIndexOf(":");
            String attrName = args[i].substring(0, k);
            String attrValue = args[i].substring(k + 1);
            boolean flag = false;//使用引号将属性值括起来
            if (k == l) {
                attrValue = args[i].substring(k + 1);

            } else {
                attrValue = args[i].substring(k + 1, l);
                flag = "true".equals(args[i].substring(l + 1)) ? true : false;
            }
            if (!Validate.isNull(attrValue) && !"null".equals(attrValue)) {
                if (delimiter == ' ') {
                    sb.append(" " + attrName + "=\"" + attrValue + "\"");
                } else if (delimiter == ',') {
                    if (j != 0) {
                        sb.append(",");
                    }
                    if (flag) {
                        sb.append(attrName + ":" + attrValue);
                    } else {
                        sb.append(attrName + ":\"" + attrValue + "\"");

                    }
                    j++;
                }
            }
        }
        return sb.toString();
    }

    public static String KeyValuePair2AttributeEscape(String... args) {
        return KeyValuePair2PropertiesEscape(' ', args);
    }

    public static String KeyValuePair2JsonEscape(String... args) {
        return KeyValuePair2PropertiesEscape(',', args);
    }

    public static String KeyValuePair2PropertiesEscape(char delimiter, String... args) {
        if (args == null)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0, j = 0; i < args.length; i++) {
            int k = args[i].indexOf(":");
            int l = args[i].lastIndexOf(":");
            String attrName = args[i].substring(0, k);
            String attrValue = args[i].substring(k + 1);
            boolean flag = false;//使用引号将属性值括起来
            if (k == l) {
                attrValue = args[i].substring(k + 1);

            } else {
                attrValue = args[i].substring(k + 1, l);
                flag = "true".equals(args[i].substring(l + 1)) ? true : false;
            }
            if (!Validate.isNull(attrValue) && !"null".equals(attrValue)) {
                if (delimiter == ' ') {
                    sb.append(" " + attrName + "=\"" + TagEntities.char2Entities(attrValue) + "\"");
                } else if (delimiter == ',') {
                    if (j != 0) {
                        sb.append(",");
                    }
                    if (flag) {
                        sb.append(attrName + ":" + TagEntities.char2Entities(attrValue));
                    } else {
                        sb.append(attrName + ":\"" + TagEntities.char2Entities(attrValue) + "\"");

                    }
                    j++;
                }
            }
        }
        return sb.toString();
    }

    public static void generateKeyValuePair(Class c) {
        Field[] fields = c.getDeclaredFields();
        StringBuilder sb = new StringBuilder("String jsonProperties = TagTools.KeyValuePair2Json(");
        for (int i = 0; i < fields.length; i++) {
            sb.append("\"" + fields[i].getName() + ":\"+this." + fields[i].getName() + "+\":false\"");
            if (i != fields.length - 1)
                sb.append(",");
            System.out.println(template.replace("?", fields[i].getName()));
        }
        sb.append(");");
        System.out.println(sb);

    }
}
