/*
 * @(#)2013-4-12 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.pt.org.entity.UnitMember;

/**
 * Description: 组织单元成员VO类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-12.1	zhulh		2013-4-12		Create
 * </pre>
 * @date 2013-4-12
 */
public class UnitMemberBean extends UnitMember {

    private static final long serialVersionUID = -8134554458717442364L;

    // jqGrid的行标识
    private String id;

    // 组织单元UUID
    private String unitUuid;

    // 组织单元名称
    private String unitName;

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
     * @return the unitUuid
     */
    public String getUnitUuid() {
        return unitUuid;
    }

    /**
     * @param unitUuid 要设置的unitUuid
     */
    public void setUnitUuid(String unitUuid) {
        this.unitUuid = unitUuid;
    }

    /**
     * @return the unitName
     */
    public String getUnitName() {
        return unitName;
    }

    /**
     * @param unitName 要设置的unitName
     */
    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

}
