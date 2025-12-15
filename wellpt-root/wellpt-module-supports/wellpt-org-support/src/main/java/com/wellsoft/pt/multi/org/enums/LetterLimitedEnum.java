package com.wellsoft.pt.multi.org.enums;

/**
 * Description: 密码配置-字母限制
 * 是否必须要有大写、小写
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
public enum LetterLimitedEnum {
    Yes("是", "LL01"), NO("否", "LL02");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private LetterLimitedEnum(String name, String value) {
        this.name = name;
        this.value = value;
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
