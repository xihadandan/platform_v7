package com.wellsoft.pt.dyform.implement.definition.control.enums;

import com.wellsoft.pt.dyform.implement.definition.control.bean.FieldPropertyBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 表单字段公共的布尔类型属性
 *
 * @author hongjz
 */
public enum EnumCommonFieldProperties {
    READONLY("readOnly", "只读"), EDITABLE("editable", "可编辑"), HIDDEN("hidden", "隐藏"), REQURIED("reqired", "必填");
    private String value;
    private String remark;

    private EnumCommonFieldProperties(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static List<FieldPropertyBean> toFieldPropertyBeanList() {
        List<FieldPropertyBean> beans = new ArrayList<FieldPropertyBean>();
        for (EnumCommonFieldProperties config : EnumCommonFieldProperties.values()) {
            FieldPropertyBean bean = new FieldPropertyBean(config.getValue(), config.getRemark());
            beans.add(bean);
        }
        return beans;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }

}
