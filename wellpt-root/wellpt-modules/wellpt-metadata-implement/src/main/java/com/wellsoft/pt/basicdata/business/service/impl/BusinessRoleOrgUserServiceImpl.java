/*
 * @(#)2019-03-01 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.business.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.basicdata.business.dao.BusinessRoleDao;
import com.wellsoft.pt.basicdata.business.dao.BusinessRoleOrgUserDao;
import com.wellsoft.pt.basicdata.business.dto.BusinessRoleOrgUserDto;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleEntity;
import com.wellsoft.pt.basicdata.business.entity.BusinessRoleOrgUserEntity;
import com.wellsoft.pt.basicdata.business.service.BusinessRoleOrgUserService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 数据库表BUSINESS_ROLE_ORG_USER的service服务接口实现类
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
@Service
public class BusinessRoleOrgUserServiceImpl extends AbstractJpaServiceImpl<BusinessRoleOrgUserEntity, BusinessRoleOrgUserDao, String> implements BusinessRoleOrgUserService {

    @Autowired
    private BusinessRoleDao businessRoleDao;

    @Override
    @Transactional
    public void batchSave(String orgUuid, List<String> roleUuidList, List<String> usersList, List<String> usersValueList) {

        List<BusinessRoleOrgUserEntity> entitys = dao.listByFieldEqValue("businessCategoryOrgUuid", orgUuid);
        dao.deleteByEntities(entitys);

        for (int i = 0; i < roleUuidList.size(); i++) {
            BusinessRoleOrgUserEntity po = new BusinessRoleOrgUserEntity();
            po.setBusinessCategoryOrgUuid(orgUuid);
            po.setBusinessRoleUuid(roleUuidList.get(i));
            po.setUsers(usersList.get(i));
            po.setUsersValue(usersValueList.get(i));
            dao.save(po);
        }
    }

    @Override
    public List<BusinessRoleOrgUserDto> findByOrgUuid(String orgUuid) {

        List<BusinessRoleOrgUserDto> dtos = new ArrayList<BusinessRoleOrgUserDto>();

        List<BusinessRoleOrgUserEntity> entitys = dao.listByFieldEqValue("businessCategoryOrgUuid", orgUuid);

        for (BusinessRoleOrgUserEntity entity : entitys) {
            BusinessRoleOrgUserDto dto = new BusinessRoleOrgUserDto();
            BeanUtils.copyProperties(entity, dto);
            BusinessRoleEntity role = businessRoleDao.getOne(dto.getBusinessRoleUuid());
            dto.setRoleName(role.getName());
            dtos.add(dto);
        }

        return dtos;
    }

    public List<BusinessRoleOrgUserEntity> findByOrgUuidAndRoleUuid(String categoryUuid, String orgId, String roleUuid) {

        String sql = "select * from BUSINESS_ROLE_ORG_USER t where 1 = 1";

        Map<String, Object> paramMap = new HashMap<String, Object>();

        if (StringUtils.isNotBlank(categoryUuid)) {
            paramMap.put("categoryUuid", categoryUuid);
            sql = sql + " and exists (select 1 from BUSINESS_ROLE r where r.uuid = t.BUSINESS_ROLE_UUID and r.BUSINESS_CATEGORY_UUID =:categoryUuid)";
        }

        if (StringUtils.isNotBlank(orgId)) {
            paramMap.put("orgId", orgId);
            sql = sql + " and exists (select 1 from BUSINESS_CATEGORY_ORG o where o.uuid = t.business_category_org_uuid and o.id =:orgId)";
        }
        if (StringUtils.isNotBlank(roleUuid)) {
            paramMap.put("roleUuid", roleUuid);
            sql = sql + " and t.business_role_uuid = :roleUuid";
        }

        return dao.listBySQL(sql, paramMap);
    }

    @Override
    public boolean isMemberOf(String userId, Set<String> orgIds) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgIds", orgIds);
        param.put("userid", "%" + userId + "%");
        return this.dao.countBySQL("select 1 from BUSINESS_ROLE_ORG_USER u , " +
                "BUSINESS_CATEGORY_ORG o where o.uuid=t.business_category_org_uuid and o.id in (:orgIds) and u.users like :userid", param) > 0;
    }
}
