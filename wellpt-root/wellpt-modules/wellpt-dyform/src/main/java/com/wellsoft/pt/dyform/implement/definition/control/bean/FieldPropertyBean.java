package com.wellsoft.pt.dyform.implement.definition.control.bean;

/**
 * 表单控件布尔配置项
 *
 * @author hongjz
 */
public class FieldPropertyBean {
    private String realName;
    private String displayName;

    public FieldPropertyBean(String realName, String displayName) {
        this.realName = realName;
        this.displayName = displayName;
    }

    public String getRealName() {
        return realName;
    }

    public String getDisplayName() {
        return displayName;
    }

}
