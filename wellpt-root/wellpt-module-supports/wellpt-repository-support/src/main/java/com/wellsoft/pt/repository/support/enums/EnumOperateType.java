package com.wellsoft.pt.repository.support.enums;


public enum EnumOperateType {
    NEW("1", "新建"), UPDATE("2", "更新所属文件夹"), DEL("3", "删除文件"), POP("4", "从内嵌数组中删除数据"), PUSH("5", "将数据推送入内嵌数组中 "), RECOVERY(
            "6", "恢复"), COPY("6", "复制");
    private String value;
    private String remark;

    private EnumOperateType(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static EnumOperateType type2EnumObj(String type) {
        if (type == null) {
            return null;
        }
        EnumOperateType enumObj = null;
        for (EnumOperateType status : EnumOperateType.values()) {
            if (status.getValue().equalsIgnoreCase(type)) {
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
