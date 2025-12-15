/*
 * @(#)2020年1月20日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.ext.enumcls.store;

import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceField;
import com.wellsoft.pt.basicdata.datastore.support.DataStoreInterfaceFieldElement;
import com.wellsoft.pt.jpa.criteria.InterfaceParam;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月20日.1	zhulh		2020年1月20日		Create
 * </pre>
 * @date 2020年1月20日
 */
public class EnumClassDataStoreInterfaceParam implements InterfaceParam {

    @DataStoreInterfaceField(name = "枚举类", service = "enumClassSelect2QueryService.queryAll4SelectOptions", domType = DataStoreInterfaceFieldElement.SELECT)
    private String className;

    /**
     * @return the className
     */
    public String getClassName() {
        return className;
    }

    /**
     * @param className 要设置的className
     */
    public void setClassName(String className) {
        this.className = className;
    }

}
