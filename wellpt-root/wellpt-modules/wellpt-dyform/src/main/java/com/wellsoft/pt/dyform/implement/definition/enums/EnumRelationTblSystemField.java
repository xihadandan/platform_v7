package com.wellsoft.pt.dyform.implement.definition.enums;

public enum EnumRelationTblSystemField {
    uuid("id", "uuid", "uuid", "string", 64), // uuid字段
    creator("property", "creator", "creator", "string", 64), // creator字段
    create_time("property", "modify_time", "create_time", "java.sql.Timestamp", null), // createTime字段
    modifier("property", "modifier", "modifier", "string", 64), // modifier字段
    modify_time("property", "modify_time", "modify_time", "java.sql.Timestamp", null), // modifyTime字段
    rec_ver("property", "rec_ver", "rec_ver", "integer", null), // rec_ver字段
    data_uuid("property", "data_uuid", "data_uuid", "string", 64), // dataUuid字段
    mainform_data_uuid("property", "mainform_data_uuid", "mainform_data_uuid", "string", 64), // mainform_data_uuid字段
    mainform_form_uuid("property", "mainform_form_uuid", "mainform_form_uuid", "string", 64), // mainform_form_uuid字段
    sort_order("property", "sort_order", "sort_order", "integer", null), // sort_order字段
    parent_uuid("property", "parent_uuid", "parent_uuid", "string", 64); //在表单数据中也存在父节点和子节点的关系,父节点和子节点的关系即通过该字段关联起来

    private String elementType;
    private String name;
    private String dataType;
    private String column;
    private Integer length;

    private EnumRelationTblSystemField(String elementType, String name, String column, String dataType, Integer length) {
        this.elementType = elementType;
        this.name = name;
        this.dataType = dataType;
        this.column = column;
        this.length = length;
    }

    public static EnumRelationTblSystemField value2EnumObj(String fieldName) {
        if (fieldName == null) {
            return null;
        }
        EnumRelationTblSystemField enumObj = null;
        for (EnumRelationTblSystemField status : EnumRelationTblSystemField.values()) {
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
