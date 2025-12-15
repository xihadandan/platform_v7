package com.wellsoft.pt.log.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 模块名称
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/6/29.1	    zenghw		2021/6/29		    Create
 * </pre>
 * @date 2021/6/29
 */
public enum LogManageModuleEnum {

    FlowDef("流程管理-流程定义", "FlowDef");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private LogManageModuleEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static LogManageModuleEnum getByValue(String value) {
        LogManageModuleEnum[] values = values();
        for (LogManageModuleEnum enum1 : values) {
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
