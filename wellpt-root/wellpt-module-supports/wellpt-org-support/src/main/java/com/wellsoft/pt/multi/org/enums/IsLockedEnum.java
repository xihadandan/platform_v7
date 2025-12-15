package com.wellsoft.pt.multi.org.enums;

/**
 * Description: 密码锁定和冻结共用这个字段
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
public enum IsLockedEnum {

    Locked("锁住", 1), UnLocked("正常", 0);

    // 成员变量
    private String name;
    private Integer value;

    // 构造方法
    private IsLockedEnum(String name, Integer value) {
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
