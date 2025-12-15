package com.wellsoft.pt.ei.dto;

import java.io.Serializable;

public class ExportFieldItemData implements Serializable {

    private String fieldName;
    private String type;
    private Object value;

    public ExportFieldItemData() {
    }

    public ExportFieldItemData(String fieldName, String type, Object value) {
        this.fieldName = fieldName;
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
