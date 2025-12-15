/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service;

import com.wellsoft.pt.basicdata.business.dao.BusinessRoleDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表BUSINESS_ROLE的service服务接口
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
public interface BusinessRoleService extends JpaService<BusinessRoleEntity, BusinessRoleDao, String> {

    public List<BusinessRoleDto> findByCategoryId(String categoryId);

    public List<BusinessRoleDto> findByCategoryUuid(String categoryUuid);

}
