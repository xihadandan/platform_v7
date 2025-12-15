package com.wellsoft.pt.multi.org.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 密码配置-字符要求
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/26.1	    zenghw		2021/3/26		    Create
 * </pre>
 * @date 2021/3/26
 */
public enum LetterAskEnum {

    LEAST1("至少1种", "LA01"), LEAST2("至少2种", "LA02"), INCLUDE3("3种", "LA03");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private LetterAskEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static LetterAskEnum getByValue(String letterAskValue) {
        LetterAskEnum[] values = values();
        for (LetterAskEnum letterAskEnum : values) {
            if (StringUtils.equals(letterAskEnum.getValue(), letterAskValue)) {
                return letterAskEnum;
            }
        }
        return null;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
