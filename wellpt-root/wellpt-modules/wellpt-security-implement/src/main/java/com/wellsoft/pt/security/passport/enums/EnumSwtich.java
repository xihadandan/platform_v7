package com.wellsoft.pt.security.passport.enums;

/**
 * Description: 开关
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/3.1	    zenghw		2021/3/3		    Create
 * </pre>
 * @date 2021/3/3
 */
public enum EnumSwtich {
    OPEN("开", "1"), CLOSE("关", "0");

    private String name;

    private String value;

    EnumSwtich(String name, String value) {
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
