/*
 * @(#)2015年9月16日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.selective.support;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年9月16日.1	zhulh		2015年9月16日		Create
 * </pre>
 * @date 2015年9月16日
 */
public class DataItem implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7262444952254379914L;
    // 权限主体条目ID
    private String aceId;

    private String label;

    private Object value;

    /**
     * @return the aceId
     */
    public String getAceId() {
        return aceId;
    }

    /**
     * @param aceId 要设置的aceId
     */
    public void setAceId(String aceId) {
        this.aceId = aceId;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label 要设置的label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the value
     */
    public Object getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(Object value) {
        this.value = value;
    }

}
