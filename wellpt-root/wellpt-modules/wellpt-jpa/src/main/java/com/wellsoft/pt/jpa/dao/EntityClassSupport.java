/*
 * @(#)2012-10-22 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao;


/**
 * Description: 如何描述该类
 *
 * @author lilin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-22.1	lilin		2012-10-22		Create
 * </pre>
 * @date 2012-10-22
 */
public class EntityClassSupport {
    //	public List getAllEntity() {
    //
    //	}
    //
    //	public List getAllCommonEntity() {
    //
    //	}

    public static boolean isCommonDb(Class clazz) {

        DBType type = (DBType) clazz.getAnnotation(DBType.class);
        if (type != null) {
            return type.common();
        }
        return false;

    }
}
