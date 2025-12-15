/*
 * @(#)2021年9月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.context.base.BaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年9月25日.1	zhulh		2021年9月25日		Create
 * </pre>
 * @date 2021年9月25日
 */
public class OpinionPosition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1289567084989444166L;

    // 名称
    private String label;
    // 值
    private String value;

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
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

}
