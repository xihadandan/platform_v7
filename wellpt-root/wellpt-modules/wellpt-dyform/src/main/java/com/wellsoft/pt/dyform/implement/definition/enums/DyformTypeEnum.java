package com.wellsoft.pt.dyform.implement.definition.enums;

public enum DyformTypeEnum {
    P("P", "存储单据"), V("V", "展现单据"), M("M", "手机单据"), MST("MST", "子表单"), C("C", "产品定义单据");
    private String value = "";
    private String remark;

    private DyformTypeEnum(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static boolean isPform(String formType) {
        if (DyformTypeEnum.P.getValue().equalsIgnoreCase(formType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMform(String formType) {
        if (DyformTypeEnum.M.getValue().equalsIgnoreCase(formType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isMSTform(String formType) {
        if (DyformTypeEnum.MST.getValue().equalsIgnoreCase(formType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isVform(String formType) {
        if (DyformTypeEnum.V.getValue().equalsIgnoreCase(formType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCform(String formType) {
        if (DyformTypeEnum.C.getValue().equalsIgnoreCase(formType)) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isExtendsPform(String formType) {
        return isMform(formType) || isVform(formType) || isCform(formType);
    }

    public static DyformTypeEnum value2EnumObj(String value) {
        DyformTypeEnum enumObj = null;
        for (DyformTypeEnum status : DyformTypeEnum.values()) {
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
