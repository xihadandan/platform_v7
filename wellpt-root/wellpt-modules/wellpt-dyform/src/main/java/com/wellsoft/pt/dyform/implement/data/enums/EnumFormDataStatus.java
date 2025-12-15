package com.wellsoft.pt.dyform.implement.data.enums;

public enum EnumFormDataStatus {
    DYFORM_DATA_STATUS_DEL("1", "已删除"), DYFORM_DATA_STATUS_DEFAULT("0", "默认值"), DYFORM_DATA_STATUS_NOT_DELETABLE("2", "不可删除的表单数据");
    private String value = "";
    private String remark;

    private EnumFormDataStatus(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static EnumFormDataStatus value2EnumObj(String value) {
        EnumFormDataStatus enumObj = null;
        for (EnumFormDataStatus status : EnumFormDataStatus.values()) {
            if (status.getValue().equals(value)) {
                enumObj = status;
            }
        }

        return enumObj;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }
}
