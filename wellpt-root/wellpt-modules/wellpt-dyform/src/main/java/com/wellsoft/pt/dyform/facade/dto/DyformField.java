package com.wellsoft.pt.dyform.facade.dto;

import java.io.Serializable;

/**
 * Description: 表单字段
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年05月18日   chenq	 Create
 * </pre>
 */
public class DyformField implements Serializable {


    // 字段名称
    private String name;
    // 控件类型
    private String inputMode;
    // 字段编码
    private String code;

    /**
     * 构造方法
     */
    public DyformField() {
    }

    /**
     * 构造方法
     *
     * @param name
     * @param inputMode
     * @param code
     */
    public DyformField(String name, String inputMode, String code) {
        this.name = name;
        this.inputMode = inputMode;
        this.code = code;
    }

    /**
     * 获取字段名称
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置字段名称
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取控件类型
     *
     * @return
     */
    public String getInputMode() {
        return inputMode;
    }

    /**
     * 设置控件类型
     *
     * @param inputMode
     */
    public void setInputMode(String inputMode) {
        this.inputMode = inputMode;
    }

    /**
     * 获取字段编码
     *
     * @return
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置字段编码
     *
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }
}
