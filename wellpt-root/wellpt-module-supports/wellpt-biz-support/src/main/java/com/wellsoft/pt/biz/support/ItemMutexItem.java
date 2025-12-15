/*
 * @(#)10/26/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

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
 * 10/26/22.1	zhulh		10/26/22		Create
 * </pre>
 * @date 10/26/22
 */
public class ItemMutexItem extends BaseObject {
    private static final long serialVersionUID = -3451897796197638385L;

    private String mutexItemCode;

    /**
     * @return the mutexItemCode
     */
    public String getMutexItemCode() {
        return mutexItemCode;
    }

    /**
     * @param mutexItemCode 要设置的mutexItemCode
     */
    public void setMutexItemCode(String mutexItemCode) {
        this.mutexItemCode = mutexItemCode;
    }
}
