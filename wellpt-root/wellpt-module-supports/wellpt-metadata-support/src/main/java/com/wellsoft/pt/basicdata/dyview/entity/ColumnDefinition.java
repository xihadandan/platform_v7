/*
 * @(#)2013-3-13 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Description: 视图自定义的列的属性
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2013-3-13.1	Administrator		2013-3-13		Create
 * </pre>
 * @date 2013-3-13
 */
@Entity
@Table(name = "dyview_column_definition")
@DynamicUpdate
@DynamicInsert
public class ColumnDefinition extends IdEntity {

    private static final long serialVersionUID = -8350054441179629318L;

    private String titleName;// 显示标题

    private String fieldName; // 对应的表字段

    private String value;// 列值

    private String valueType;// 列值类型 1表示基础列值 2表示高级列值

    private String width;// 列宽

    private Boolean parseHtml;// 是否解释html

    private String defaultSort;// 默认排序

    private Boolean hidden;// 列是否隐藏

    private Boolean sortAble;// 是否允许点击排序

    private Boolean fieldPermission;// 列是否具有权限

    private Integer sortOrder;// 视图列排序

    private String columnType;//列类型

    private String columnDataType; //列数据类型

    private String entityName; //列所属于的类

    private String columnAliase;//列别名

    private String showLine;//显示行

    private String otherName;//别名

    @UnCloneable
    private ViewDefinition viewDefinition; // 所属的视图

    public Object getColumnDataType(String data) {
        if (columnDataType == null || "".equals(columnDataType)) {
            return data;
        }
        Object value = null;
        if (columnDataType.equals("STRING")) {
            value = data;
        } else if (columnDataType.equals("CLOB")) {
            value = data;
        } else if (columnDataType.equals("INTEGER")) {
            value = Integer.valueOf(data);
        } else if (columnDataType.equals("LONG")) {
            value = Long.valueOf(data);
        } else if (columnDataType.equals("DOUBLE")) {
            value = Double.valueOf(data);
        } else if (columnDataType.equals("DATE")) {
            SimpleDateFormat sim = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            try {
                value = sim.parse(data);
            } catch (ParseException e) {
                return value = "0000-00-00 00:00:00";
            }
        } else if (columnDataType.equals("BOOLEAN")) {
            value = Boolean.valueOf(data);
        }
        return value;
    }

    /**
     * @return the fieldPermission
     */
    public Boolean getFieldPermission() {
        return fieldPermission;
    }

    /**
     * @param fieldPermission 要设置的fieldPermission
     */
    public void setFieldPermission(Boolean fieldPermission) {
        this.fieldPermission = fieldPermission;
    }

    /**
     * @return the columnDataType
     */
    public String getColumnDataType() {
        return columnDataType;
    }

    /**
     * @param columnDataType 要设置的columnDataType
     */
    public void setColumnDataType(String columnDataType) {
        this.columnDataType = columnDataType;
    }

    /**
     * @return the entityName
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * @param entityName 要设置的entityName
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * @return the columnAliase
     */
    public String getColumnAliase() {
        return columnAliase;
    }

    /**
     * @param columnAliase 要设置的columnAliase
     */
    public void setColumnAliase(String columnAliase) {
        this.columnAliase = columnAliase;
    }

    /**
     * @return the columnType
     */
    public String getColumnType() {
        return columnType;
    }

    /**
     * @param columnType 要设置的columnType
     */
    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the valueType
     */
    public String getValueType() {
        return valueType;
    }

    /**
     * @param valueType 要设置的valueType
     */
    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the titleName
     */
    public String getTitleName() {
        return titleName;
    }

    /**
     * @param titleName 要设置的titleName
     */
    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the width
     */
    public String getWidth() {
        return width;
    }

    /**
     * @param width 要设置的width
     */
    public void setWidth(String width) {
        this.width = width;
    }

    /**
     * @return the viewDefinition
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "view_def_uuid", nullable = false)
    public ViewDefinition getViewDefinition() {
        return viewDefinition;
    }

    /**
     * @param viewDefinition 要设置的viewDefinition
     */
    public void setViewDefinition(ViewDefinition viewDefinition) {
        this.viewDefinition = viewDefinition;
    }

    /**
     * @return the parseHtml
     */
    public Boolean getParseHtml() {
        return parseHtml;
    }

    /**
     * @param parseHtml 要设置的parseHtml
     */
    public void setParseHtml(Boolean parseHtml) {
        this.parseHtml = parseHtml;
    }

    /**
     * @return the defaultSort
     */
    public String getDefaultSort() {
        return defaultSort;
    }

    /**
     * @param defaultSort 要设置的defaultSort
     */
    public void setDefaultSort(String defaultSort) {
        this.defaultSort = defaultSort;
    }

    /**
     * @return the hidden
     */
    public Boolean getHidden() {
        return hidden;
    }

    /**
     * @param hidden 要设置的hidden
     */
    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * @return the sortAble
     */
    public Boolean getSortAble() {
        return sortAble;
    }

    /**
     * @param sortAble 要设置的sortAble
     */
    public void setSortAble(Boolean sortAble) {
        this.sortAble = sortAble;
    }

    /**
     * @return the showLine
     */
    public String getShowLine() {
        return showLine;
    }

    /**
     * @param showLine 要设置的showLine
     */
    public void setShowLine(String showLine) {
        this.showLine = showLine;
    }

    /**
     * @return the otherName
     */
    public String getOtherName() {
        return otherName;
    }

    /**
     * @param otherName 要设置的otherName
     */
    public void setOtherName(String otherName) {
        this.otherName = otherName;
    }

}
