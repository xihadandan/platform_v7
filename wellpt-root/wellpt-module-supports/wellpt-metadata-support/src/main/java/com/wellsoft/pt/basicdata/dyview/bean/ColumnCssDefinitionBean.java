/*
 * @(#)2013-4-16 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.dyview.bean;

import com.wellsoft.pt.basicdata.dyview.entity.ColumnCssDefinition;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-16.1	wubin		2013-4-16		Create
 * </pre>
 * @date 2013-4-16
 */
public class ColumnCssDefinitionBean extends ColumnCssDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -547438782953506281L;

    private String id;

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
        ColumnCssDefinitionBean other = (ColumnCssDefinitionBean) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

}
