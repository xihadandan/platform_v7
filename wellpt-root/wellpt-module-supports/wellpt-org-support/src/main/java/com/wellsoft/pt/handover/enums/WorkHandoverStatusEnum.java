package com.wellsoft.pt.handover.enums;

/**
 * Description: 工作交接-工作交接状态
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
public enum WorkHandoverStatusEnum {
    NotExecution("未执行 ", 1), Execution("执行中", 2), Completed("已完成", 3);

    // 成员变量
    private String name;
    private Integer value;

    // 构造方法
    private WorkHandoverStatusEnum(String name, Integer value) {
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
    public Integer getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(Integer value) {
        this.value = value;
    }
}
