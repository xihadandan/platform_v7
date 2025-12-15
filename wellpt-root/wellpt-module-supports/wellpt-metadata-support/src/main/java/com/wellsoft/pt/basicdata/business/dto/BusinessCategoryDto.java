/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.dto;

import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据库表BUSINESS_CATEGORY的对应的DTO类
 *
 * @author leo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019-02-14.1	leo		2019-02-14		Create
 * </pre>
 * @date 2019-02-14
 */
public class BusinessCategoryDto extends BusinessCategoryEntity {

    private static final long serialVersionUID = 1550125139810L;

    private List<BusinessRoleDto> addRoles = new ArrayList<BusinessRoleDto>();
    private List<String> delRoleIds = new ArrayList<String>();

    private String applicationUuid;

    public List<BusinessRoleDto> getAddRoles() {
        return addRoles;
    }

    public void setAddRoles(List<BusinessRoleDto> addRoles) {
        this.addRoles = addRoles;
    }

    public List<String> getDelRoleIds() {
        return delRoleIds;
    }

    public void setDelRoleIds(List<String> delRoleIds) {
        this.delRoleIds = delRoleIds;
    }

    public String getApplicationUuid() {
        return applicationUuid;
    }

    public void setApplicationUuid(String applicationUuid) {
        this.applicationUuid = applicationUuid;
    }


}
