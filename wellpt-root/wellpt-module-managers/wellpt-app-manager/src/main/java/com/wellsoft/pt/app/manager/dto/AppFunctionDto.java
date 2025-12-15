/*
 * @(#)2019年6月11日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.dto;

import com.wellsoft.pt.app.bean.AppFunctionBean;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月11日.1	zhulh		2019年6月11日		Create
 * </pre>
 * @date 2019年6月11日
 */
public class AppFunctionDto extends AppFunctionBean {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1722807044527970434L;

    private Boolean isProtected;

    /**
     * @return the isProtected
     */
    public Boolean getIsProtected() {
        return isProtected;
    }

    /**
     * @param isProtected 要设置的isProtected
     */
    public void setIsProtected(Boolean isProtected) {
        this.isProtected = isProtected;
    }

}
