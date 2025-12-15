/*
 * @(#)2019-03-01 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.dto;

import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;


/**
 * Description: 数据库表BUSINESS_ROLE_ORG_USER的对应的DTO类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-03-01.1	leo		2019-03-01		Create
 * </pre>
 * @date 2019-03-01
 */
public class BusinessRoleOrgUserDto extends BusinessRoleOrgUserEntity {

    private static final long serialVersionUID = 1551423356625L;

    private String roleName;

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }


}
