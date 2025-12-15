/*
 * @(#)2013-3-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.view.bean;

import com.wellsoft.pt.basicdata.view.entity.SelectDefinitionNew;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-22.1	wubin		2013-3-22		Create
 * </pre>
 * @date 2013-3-22
 */
public class SelectDefinitionNewBean extends SelectDefinitionNew {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5393452216604323238L;

    //jqgrid的id标示
    private String id;

    private Set<FieldSelectBean> fieldSelectBeans = new HashSet<FieldSelectBean>();

    private Set<ConditionTypeNewBean> conditionTypeFields = new HashSet<ConditionTypeNewBean>();

    /**
     * @return the fieldSelectBeans
     */
    public Set<FieldSelectBean> getFieldSelectBeans() {
        return fieldSelectBeans;
    }

    /**
     * @param fieldSelectBeans 要设置的fieldSelectBeans
     */
    public void setFieldSelectBeans(Set<FieldSelectBean> fieldSelectBeans) {
        this.fieldSelectBeans = fieldSelectBeans;
    }

    /**
     * @return the conditionTypeFields
     */
    public Set<ConditionTypeNewBean> getConditionTypeFields() {
        return conditionTypeFields;
    }

    /**
     * @param conditionTypeFields 要设置的conditionTypeFields
     */
    public void setConditionTypeFields(Set<ConditionTypeNewBean> conditionTypeFields) {
        this.conditionTypeFields = conditionTypeFields;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
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
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        SelectDefinitionNewBean other = (SelectDefinitionNewBean) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
