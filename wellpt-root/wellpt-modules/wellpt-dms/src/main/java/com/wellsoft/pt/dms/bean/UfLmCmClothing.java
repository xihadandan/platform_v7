/*
 * @(#)Feb 21, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.bean;

import com.wellsoft.pt.dms.core.proxy.DyFormDataProxyBaseObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 21, 2017.1	zhulh		Feb 21, 2017		Create
 * </pre>
 * @date Feb 21, 2017
 */
public class UfLmCmClothing extends DyFormDataProxyBaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2731579945249581490L;

    private String name;

    private Integer sortNum;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the sortNum
     */
    public Integer getSortNum() {
        return sortNum;
    }

    /**
     * @param sortNum 要设置的sortNum
     */
    public void setSortNum(Integer sortNum) {
        this.sortNum = sortNum;
    }

}
