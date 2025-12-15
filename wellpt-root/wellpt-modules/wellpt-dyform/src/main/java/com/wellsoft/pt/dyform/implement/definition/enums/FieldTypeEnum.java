package com.wellsoft.pt.dyform.implement.definition.enums;

/**
 * Description: 字段类型
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月28日.1	hongjz		2018年3月28日		Create
 * </pre>
 * @date 2018年3月28日
 */
public enum FieldTypeEnum {
    NUMBER("NUMBER", "数值类型")/* 不同的数据库，根据实际需要进行转换 */, TIMESTAMP("TIMESTAMP", "日期类型(TIMESTAMP)"), FILE("FILE", "文件"), TEXT(
            "TEXT", "文本类型"), CLOB("CLOB", "大字段");
    private String value = "";
    private String remark;

    private FieldTypeEnum(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }

    public FieldTypeEnum value2EnumObj(String value) {
        FieldTypeEnum enumObj = null;
        for (FieldTypeEnum status : FieldTypeEnum.values()) {
            if (status.getValue().equals(value)) {
                enumObj = status;
            }
        }
        return enumObj;
    }
}
