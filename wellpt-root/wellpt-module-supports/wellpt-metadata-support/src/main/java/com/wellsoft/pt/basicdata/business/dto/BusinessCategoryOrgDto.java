/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.dto;

import com.wellsoft.pt.basicdata.business.entity.BusinessCategoryOrgEntity;

/**
 * Description: 数据库表BUSINESS_CATEGORY_ORG的对应的DTO类
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
public class BusinessCategoryOrgDto extends BusinessCategoryOrgEntity {

    private static final long serialVersionUID = 1550125081057L;

    private String parentName;

    // 管理单位value值
    private String manageDeptValue;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getManageDeptValue() {
        return manageDeptValue;
    }

    public void setManageDeptValue(String manageDeptValue) {
        this.manageDeptValue = manageDeptValue;
    }


}
