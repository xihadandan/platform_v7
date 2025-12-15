package com.wellsoft.pt.ei.processor.utils;

import java.io.Serializable;
import java.util.List;

/**
 * @Auther: yt
 * @Date: 2021/9/28 14:26
 * @Description:
 */
public class FieldDesc implements Serializable {
    private static final long serialVersionUID = -8761498719510327059L;


    private String fieldName;
    private String type;
    private String desc;
    private String displayValue;
    private String dictValue;
    private List<FieldDesc> fields;

    public FieldDesc() {
    }

    public FieldDesc(String fieldName, String type, String desc, String displayValue, String dictValue) {
        this.fieldName = fieldName;
        this.type = type;
        this.desc = desc;
        this.displayValue = displayValue;
        this.dictValue = dictValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDictValue() {
        return dictValue;
    }

    public void setDictValue(String dictValue) {
        this.dictValue = dictValue;
    }

    public List<FieldDesc> getFields() {
        return fields;
    }

    public void setFields(List<FieldDesc> fields) {
        this.fields = fields;
    }
}
