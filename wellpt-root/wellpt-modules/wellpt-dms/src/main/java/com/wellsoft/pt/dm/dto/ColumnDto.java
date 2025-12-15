package com.wellsoft.pt.dm.dto;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年04月11日   chenq	 Create
 * </pre>
 */
public class ColumnDto implements Serializable {

    private static final long serialVersionUID = -7187283943512242044L;
    private String uuid;
    private String title;
    private String column;
    private String alias;
    private String dataType;
    private Integer length;
    private Integer scale;
    private Boolean notNull;
    private String unique;
    private String remark;

    public ColumnDto() {
    }

    public ColumnDto(String uuid, String title, String column, String dataType, Integer length, Boolean notNull) {
        this.uuid = uuid;
        this.title = title;
        this.column = column;
        this.dataType = dataType;
        this.length = length;
        this.notNull = notNull;
    }

    public ColumnDto(String uuid, String title, String column, String dataType, Integer length, Boolean notNull, Integer scale) {
        this.uuid = uuid;
        this.title = title;
        this.column = column;
        this.dataType = dataType;
        this.length = length;
        this.notNull = notNull;
        this.scale = scale;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(final String column) {
        this.column = column;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public Integer getLength() {
        return this.length;
    }

    public void setLength(final Integer length) {
        this.length = length;
    }

    public Integer getScale() {
        return this.scale;
    }

    public void setScale(final Integer scale) {
        this.scale = scale;
    }

    public Boolean getNotNull() {
        return this.notNull;
    }

    public void setNotNull(final Boolean notNull) {
        this.notNull = notNull;
    }

    public String getUnique() {
        return this.unique;
    }

    public void setUnique(final String unique) {
        this.unique = unique;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
