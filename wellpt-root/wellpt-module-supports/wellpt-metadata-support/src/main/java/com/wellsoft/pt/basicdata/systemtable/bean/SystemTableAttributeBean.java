/*
 * @(#)2013-1-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.systemtable.bean;

import com.wellsoft.pt.basicdata.systemtable.entity.SystemTableAttribute;

/**
 * Description: 系统表数据属性VO类
 *
 * @author zhouyq
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-22.1	zhouyq		2013-3-22		Create
 * </pre>
 * @date 2013-3-22
 */
public class SystemTableAttributeBean extends SystemTableAttribute {
    private static final long serialVersionUID = 1L;

    // jqGrid默认传过来的行标识
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

}
