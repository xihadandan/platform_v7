/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.bean;

import com.wellsoft.context.form.CustomDynamicButton;
import com.wellsoft.pt.basicdata.dyview.entity.ViewDefinition;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-15.1	Administrator		2013-3-15		Create
 * </pre>
 * @date 2013-3-15
 */
public class ViewDefinitionBean extends ViewDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8134669557493427155L;

    private Set<ColumnDefinitionBean> columnFields = new LinkedHashSet<ColumnDefinitionBean>();

    private Set<ColumnDefinitionBean> columnDeFields = new LinkedHashSet<ColumnDefinitionBean>();

    private Set<ColumnCssDefinitionBean> columnCssDefinitionFields = new LinkedHashSet<ColumnCssDefinitionBean>();

    private SelectDefinitionBean selectFields;

    private SelectDefinitionBean selectDeleteFields;

    private PageDefinitionBean pageFields;

    private Set<CustomButtonBean> customButtonFields = new LinkedHashSet<CustomButtonBean>();

    private List<CustomDynamicButton> buttons = new ArrayList<CustomDynamicButton>();

    /**
     * @return the buttons
     */
    public List<CustomDynamicButton> getButtons() {
        return buttons;
    }

    /**
     * @param buttons 要设置的buttons
     */
    public void setButtons(List<CustomDynamicButton> buttons) {
        this.buttons = buttons;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((columnFields == null) ? 0 : columnFields.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ViewDefinitionBean other = (ViewDefinitionBean) obj;
        if (columnFields == null) {
            if (other.columnFields != null)
                return false;
        } else if (!columnFields.equals(other.columnFields))
            return false;
        return true;
    }

    /**
     * @return the columnDeFields
     */
    public Set<ColumnDefinitionBean> getColumnDeFields() {
        return columnDeFields;
    }

    /**
     * @param columnDeFields 要设置的columnDeFields
     */
    public void setColumnDeFields(Set<ColumnDefinitionBean> columnDeFields) {
        this.columnDeFields = columnDeFields;
    }

    /**
     * @return the selectDeleteFields
     */
    public SelectDefinitionBean getSelectDeleteFields() {
        return selectDeleteFields;
    }

    /**
     * @param selectDeleteFields 要设置的selectDeleteFields
     */
    public void setSelectDeleteFields(SelectDefinitionBean selectDeleteFields) {
        this.selectDeleteFields = selectDeleteFields;
    }

    /**
     * @return the columnFields
     */
    public Set<ColumnDefinitionBean> getColumnFields() {
        return columnFields;
    }

    /**
     * @param columnFields 要设置的columnFields
     */
    public void setColumnFields(Set<ColumnDefinitionBean> columnFields) {
        this.columnFields = columnFields;
    }

    /**
     * @return the selectFields
     */
    public SelectDefinitionBean getSelectFields() {
        return selectFields;
    }

    /**
     * @param selectFields 要设置的selectFields
     */
    public void setSelectFields(SelectDefinitionBean selectFields) {
        this.selectFields = selectFields;
    }

    /**
     * @return the pageFields
     */
    public PageDefinitionBean getPageFields() {
        return pageFields;
    }

    /**
     * @param pageFields 要设置的pageFields
     */
    public void setPageFields(PageDefinitionBean pageFields) {
        this.pageFields = pageFields;
    }

    /**
     * @return the customButtonFields
     */
    public Set<CustomButtonBean> getCustomButtonFields() {
        return customButtonFields;
    }

    /**
     * @param customButtonFields 要设置的customButtonFields
     */
    public void setCustomButtonFields(Set<CustomButtonBean> customButtonFields) {
        this.customButtonFields = customButtonFields;
    }

    /**
     * @return the columnCssDefinitionFields
     */
    public Set<ColumnCssDefinitionBean> getColumnCssDefinitionFields() {
        return columnCssDefinitionFields;
    }

    /**
     * @param columnCssDefinitionFields 要设置的columnCssDefinitionFields
     */
    public void setColumnCssDefinitionFields(Set<ColumnCssDefinitionBean> columnCssDefinitionFields) {
        this.columnCssDefinitionFields = columnCssDefinitionFields;
    }

}
