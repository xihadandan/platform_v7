/*
 * @(#)1/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dto;

import com.wellsoft.pt.org.entity.OrgUserEntity;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/13/25.1	    zhulh		1/13/25		    Create
 * </pre>
 * @date 1/13/25
 */
public class OrgUserDto extends OrgUserEntity {

    private static final long serialVersionUID = 1154114490848056955L;
    // 组织元素ID路径
    private String orgElementIdPath;
    // 组织元素路径名称
    private String orgElementCnPath;
    //  组织元素
    private String orgElementName;

    /**
     * @return the orgElementIdPath
     */
    public String getOrgElementIdPath() {
        return orgElementIdPath;
    }

    /**
     * @param orgElementIdPath 要设置的orgElementIdPath
     */
    public void setOrgElementIdPath(String orgElementIdPath) {
        this.orgElementIdPath = orgElementIdPath;
    }

    /**
     * @return the orgElementCnPath
     */
    public String getOrgElementCnPath() {
        return orgElementCnPath;
    }

    /**
     * @param orgElementCnPath 要设置的orgElementCnPath
     */
    public void setOrgElementCnPath(String orgElementCnPath) {
        this.orgElementCnPath = orgElementCnPath;
    }

    /**
     * @return the orgElementName
     */
    public String getOrgElementName() {
        return orgElementName;
    }

    /**
     * @param orgElementName 要设置的orgElementName
     */
    public void setOrgElementName(String orgElementName) {
        this.orgElementName = orgElementName;
    }
}
