package com.wellsoft.pt.dm.hibernate;

import java.io.Serializable;

public class Property implements Serializable {
    private static final long serialVersionUID = 367869352467763530L;
    private String name;
    private String type;

    private Integer length; // 长度（用于字符型）
    private Integer precision;// 精度（用于数字型）
    private int scale = 0; // 保留小数位（用于数字型）
    private Boolean unique = false; // 是否唯一
    private String uniqueKey = null;// 通过Key创建组合字段唯一性
    private Boolean notNull = false;//不为空
    private String comment;
    private String defaultValue;//默认值

    private String rename;// 重命名旧值

    public Property() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getPrecision() {
        return precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    public Boolean getNotNull() {
        return notNull;
    }

    public void setNotNull(Boolean notNull) {
        this.notNull = notNull;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getRename() {
        return rename;
    }

    public void setRename(String rename) {
        this.rename = rename;
    }

    public String getUniqueKey() {
        return uniqueKey;
    }

    public void setUniqueKey(String uniqueKey) {
        this.uniqueKey = uniqueKey;
    }
}