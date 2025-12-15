package com.wellsoft.pt.workflow.enums;

/**
 * Description: 流程意见校验设置_场景
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
public enum SceneEnum {

    SUBMIT("提交", "S001"), RETURN("退回", "S002"), TURN_TO("转办", "S003"), COUNTERSIGN("会签", "SC04");

    // 成员变量
    private String name;
    private String value;

    // 构造方法
    private SceneEnum(String name, String value) {
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
