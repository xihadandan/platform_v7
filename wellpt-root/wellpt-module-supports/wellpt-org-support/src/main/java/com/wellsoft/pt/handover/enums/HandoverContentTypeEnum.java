package com.wellsoft.pt.handover.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 工作交接-交接内容类型
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/27.1	    zenghw		2021/3/27		    Create
 * </pre>
 * @date 2021/3/27
 */
public enum HandoverContentTypeEnum {
    TODO("待办流程", "todo"), CONSULT("查阅流程", "consult"), MONITOR("监控流程", "monitor"), DONE("督办流程",
            "done"), SUPERVISE("已办流程", "supervise");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private HandoverContentTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HandoverContentTypeEnum getByValue(String value) {
        HandoverContentTypeEnum[] values = values();
        for (HandoverContentTypeEnum enum1 : values) {
            if (StringUtils.equals(enum1.getValue(), value)) {
                return enum1;
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
