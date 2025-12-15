/*
 * @(#)2019-02-14 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service.impl;

import com.wellsoft.pt.basicdata.business.dao.BusinessRoleDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Description: 数据库表BUSINESS_ROLE的service服务接口实现类
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
@Service
public class BusinessRoleServiceImpl extends AbstractJpaServiceImpl<BusinessRoleEntity, BusinessRoleDao, String>
        implements BusinessRoleService {

    private final static String FIND_BUSINESSROLE_BY_CATEGORYID_SQL = "select * from BUSINESS_ROLE t where exists(select 1 from business_category c where t.business_category_uuid = c.uuid and c.id = :categoryId)";

    @Override
    public List<BusinessRoleDto> findByCategoryId(String categoryId) {

        List<BusinessRoleDto> dtos = new ArrayList<BusinessRoleDto>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("categoryId", categoryId);

        List<BusinessRoleEntity> entitys = this.dao.listBySQL(FIND_BUSINESSROLE_BY_CATEGORYID_SQL, params);

        for (BusinessRoleEntity entity : entitys) {
            BusinessRoleDto dto = new BusinessRoleDto();
            BeanUtils.copyProperties(entity, dto);
            dtos.add(dto);
        }

        return dtos;
    }


    @Override
    public List<BusinessRoleDto> findByCategoryUuid(String categoryUuid) {

        List<BusinessRoleDto> dtos = new ArrayList<BusinessRoleDto>();

        List<BusinessRoleEntity> dbRoles = dao.listByFieldEqValue("businessCategoryUuid", categoryUuid);

        for (BusinessRoleEntity role : dbRoles) {
            BusinessRoleDto dto = new BusinessRoleDto();
            BeanUtils.copyProperties(role, dto);
            dtos.add(dto);
        }

        return dtos;
    }

}
