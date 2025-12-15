package com.wellsoft.pt.dyform.implement.definition.enums;

public enum EnumDyformApplyToDictionary {
    DY_FORM_ID_MAPPING("表ID映射(主表应用于)", "011002", "DY_FORM_ID_MAPPING"), DY_FORM_FIELD_MAPPING("字段映射(字段应用于)", "011001",
            "DY_FORM_FIELD_MAPPING"), DY_FORM_SUBFORM_MAPPING("从表映射(从表应用于)", "011003", "DY_FORM_SUBFORM_MAPPING"), DY_FORM(
            "动态表单", "011", "DY_FORM");

    private String name;
    private String type;
    private String code;
    private EnumDyformApplyToDictionary(String name, String code, String type) {
        this.name = name;
        this.type = type;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getCode() {
        return code;
    }
}
