/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.dao.PrivilegeResourceDao;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
@Service
public class PrivilegeResourceServiceImpl extends
        AbstractJpaServiceImpl<PrivilegeResource, PrivilegeResourceDao, String> implements
        PrivilegeResourceService {

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Override
    public List<PrivilegeResource> findByExample(PrivilegeResource example) {
        return dao.listByEntity(example);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     */
    @Override
    @Transactional
    public void deleteByPrivilegeUuid(String uuid) {
        dao.deleteByPrivilegeUuid(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeResourceService#getByPrivilegeUuid(java.lang.String)
     */
    @Override
    public List<PrivilegeResource> getByPrivilegeUuid(String privilegeUuid) {
        return dao.getByPrivilegeUuid(privilegeUuid);
    }

    @Override
    public List<String> listResourceUuidsByPrivilegeUuid(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("puuid", uuid);
        return dao.listCharSequenceByHQL("from PrivilegeResource where privilegeUuid = :puuid", params);
    }


}
