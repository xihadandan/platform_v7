/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.bean;

import com.wellsoft.context.form.CustomDynamicButton;
import com.wellsoft.pt.basicdata.view.entity.ViewDefinitionNew;

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
public class ViewDefinitionNewBean extends ViewDefinitionNew {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8134669557493427155L;

    private Set<ColumnDefinitionNewBean> columnFields = new LinkedHashSet<ColumnDefinitionNewBean>();

    private Set<ColumnDefinitionNewBean> columnDeFields = new LinkedHashSet<ColumnDefinitionNewBean>();

    private Set<ColumnCssDefinitionNewBean> columnCssDefinitionFields = new LinkedHashSet<ColumnCssDefinitionNewBean>();

    private SelectDefinitionNewBean selectFields;

    private SelectDefinitionNewBean selectDeleteFields;

    private PageDefinitionNewBean pageFields;

    private Set<CustomButtonNewBean> customButtonFields = new LinkedHashSet<CustomButtonNewBean>();

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
        ViewDefinitionNewBean other = (ViewDefinitionNewBean) obj;
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
    public Set<ColumnDefinitionNewBean> getColumnDeFields() {
        return columnDeFields;
    }

    /**
     * @param columnDeFields 要设置的columnDeFields
     */
    public void setColumnDeFields(Set<ColumnDefinitionNewBean> columnDeFields) {
        this.columnDeFields = columnDeFields;
    }

    /**
     * @return the selectDeleteFields
     */
    public SelectDefinitionNewBean getSelectDeleteFields() {
        return selectDeleteFields;
    }

    /**
     * @param selectDeleteFields 要设置的selectDeleteFields
     */
    public void setSelectDeleteFields(SelectDefinitionNewBean selectDeleteFields) {
        this.selectDeleteFields = selectDeleteFields;
    }

    /**
     * @return the columnFields
     */
    public Set<ColumnDefinitionNewBean> getColumnFields() {
        return columnFields;
    }

    /**
     * @param columnFields 要设置的columnFields
     */
    public void setColumnFields(Set<ColumnDefinitionNewBean> columnFields) {
        this.columnFields = columnFields;
    }

    /**
     * @return the selectFields
     */
    public SelectDefinitionNewBean getSelectFields() {
        return selectFields;
    }

    /**
     * @param selectFields 要设置的selectFields
     */
    public void setSelectFields(SelectDefinitionNewBean selectFields) {
        this.selectFields = selectFields;
    }

    /**
     * @return the pageFields
     */
    public PageDefinitionNewBean getPageFields() {
        return pageFields;
    }

    /**
     * @param pageFields 要设置的pageFields
     */
    public void setPageFields(PageDefinitionNewBean pageFields) {
        this.pageFields = pageFields;
    }

    /**
     * @return the customButtonFields
     */
    public Set<CustomButtonNewBean> getCustomButtonFields() {
        return customButtonFields;
    }

    /**
     * @param customButtonFields 要设置的customButtonFields
     */
    public void setCustomButtonFields(Set<CustomButtonNewBean> customButtonFields) {
        this.customButtonFields = customButtonFields;
    }

    /**
     * @return the columnCssDefinitionFields
     */
    public Set<ColumnCssDefinitionNewBean> getColumnCssDefinitionFields() {
        return columnCssDefinitionFields;
    }

    /**
     * @param columnCssDefinitionFields 要设置的columnCssDefinitionFields
     */
    public void setColumnCssDefinitionFields(Set<ColumnCssDefinitionNewBean> columnCssDefinitionFields) {
        this.columnCssDefinitionFields = columnCssDefinitionFields;
    }

}
