/*
 * @(#)2014-10-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.unit.dao.BusinessTypeDao;
import com.wellsoft.pt.unit.dao.BusinessUnitTreeRoleDao;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.BusinessUnitTreeRole;
import com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-13.1	zhulh		2014-10-13		Create
 * </pre>
 * 0
 * @date 2014-10-13
 */
@Service
@Transactional
public class BusinessUnitTreeRoleServiceImpl extends BaseServiceImpl implements BusinessUnitTreeRoleService {

    @Autowired
    private BusinessUnitTreeRoleDao businessUnitTreeRoleDao;

    @Autowired
    private BusinessTypeDao businessTypeDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService#getByBusinessUnitTreeUuid(java.lang.String)
     */
    @Override
    public List<BusinessUnitTreeRole> getByBusinessUnitTreeUuid(String businessUnitTreeUuid) {
        BusinessUnitTreeRole example = new BusinessUnitTreeRole();
        example.setBusinessUnitTreeUuid(businessUnitTreeUuid);
        return this.dao.findByExample(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService#get(java.lang.String)
     */
    @Override
    public BusinessUnitTreeRole get(String uuid) {
        return this.dao.get(BusinessUnitTreeRole.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(BusinessUnitTreeRole.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService#remove(com.wellsoft.pt.unit.entity.BusinessUnitTreeRole)
     */
    @Override
    public void remove(BusinessUnitTreeRole businessUnitTreeRole) {
        this.dao.delete(businessUnitTreeRole);
    }

    @Override
    public void save(BusinessUnitTreeRole role) {
        // TODO Auto-generated method stub
        this.businessUnitTreeRoleDao.save(role);
    }

    @Override
    public List<BusinessUnitTree> getBusinessUnitTree(String tenantId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("tenantId", tenantId);
        values.put("orderBy", " o.unit.tenantId desc");
        return businessTypeDao.namedQuery("businessUnitTreeQuery", values, BusinessUnitTree.class, null);
    }

}
