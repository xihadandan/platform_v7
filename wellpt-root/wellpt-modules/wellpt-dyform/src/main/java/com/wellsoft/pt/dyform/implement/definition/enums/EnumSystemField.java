package com.wellsoft.pt.dyform.implement.definition.enums;

public enum EnumSystemField {
    uuid("id", "uuid", "uuid", "string", 64), // uuid字段
    creator("property", "creator", "creator", "string", 64), // creator字段
    create_time("property", "create_time", "create_time", "java.sql.Timestamp", null), // createTime字段
    modifier("property", "modifier", "modifier", "string", 64), // modifier字段
    modify_time("property", "modify_time", "modify_time", "java.sql.Timestamp", null), // modifyTime字段
    rec_ver("property", "rec_ver", "rec_ver", "integer", null), // rec_ver字段

    form_uuid("property", "form_uuid", "form_uuid", "string", 64), // 表dytable_form_definition的外键
    // parentUuid("property", "parentUuid", "parent_uuid", "string", 255),
    // //在表单数据中也存在父节点和子节点的关系,父节点和子节点的关系即通过该字段关联起来
    status("property", "status", "status", "string", 1), // 表单数据状态 : 0/1值
    signature_("property", "signature_", "signature_", "string", 50), // 签名,长度设置为50主要是为了便于保存旧数据
    // valid("property", "valid", "valid", "string", 10);//合法标识
    version("property", "version", "version", "string", 9), // 版本号
    system_unit_id("property", "system_unit_id", "system_unit_id", "string", 32), // 旧系统单位ID
    tenant("property", "tenant", "tenant", "string", 64), // 归属租户ID
    system("property", "system", "system", "string", 64), // 归属系统ID
    ;

    private String elementType;
    private String name;
    private String dataType;
    private String column;
    private Integer length;

    private EnumSystemField(String elementType, String name, String column, String dataType, Integer length) {
        this.elementType = elementType;
        this.name = name;
        this.dataType = dataType;
        this.column = column;
        this.length = length;
    }

    public static EnumSystemField value2EnumObj(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        EnumSystemField enumObj = null;
        for (EnumSystemField status : EnumSystemField.values()) {
            if (status.getColumn().equalsIgnoreCase(fieldName)) {
                enumObj = status;
            }
        }

        return enumObj;
    }

    public String getElementType() {
        return elementType;
    }

    public void setElementType(String elementType) {
        this.elementType = elementType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Integer getLength() {
        return length;
    }

}
