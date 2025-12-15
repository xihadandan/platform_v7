package com.wellsoft.pt.workflow.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 流程意见规则_检验项_检验项约束条件
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/5/11.1	    zenghw		2021/5/11		    Create
 * </pre>
 * @date 2021/5/11
 */
public enum ItemConditionEnum {

    EQUAL("等于", "IC01"), UNEQUAL("不等于", "IC02"), GREATER_THAN("大于", "IC03"), GE("大于等于", "IC04"), LESS_THAN("小于",
            "IC05"), LE("小于等于", "IC06"), INCLUDE("包含", "IC07"), NOT_IN("不包含", "IC08");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private ItemConditionEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @param value
     * @return
     */
    public static ItemConditionEnum getByValue(String value) {
        ItemConditionEnum[] values = values();
        for (ItemConditionEnum enumState : values) {
            if (StringUtils.equals(enumState.getValue(), value)) {
                return enumState;
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
