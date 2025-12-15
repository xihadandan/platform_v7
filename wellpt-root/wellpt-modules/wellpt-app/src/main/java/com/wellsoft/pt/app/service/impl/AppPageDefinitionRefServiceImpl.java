/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.app.dao.AppPageDefinitionRefDao;
import com.wellsoft.pt.app.entity.AppPageDefinitionRefEntity;
import com.wellsoft.pt.app.service.AppPageDefinitionRefService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2019年6月13日.1	zhulh		2019年6月13日		Create
 * </pre>
 * @date 2019年6月13日
 */
@Service
public class AppPageDefinitionRefServiceImpl extends
        AbstractJpaServiceImpl<AppPageDefinitionRefEntity, AppPageDefinitionRefDao, String> implements
        AppPageDefinitionRefService {

    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionRefService#remove(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String refUuid, String appPiUuid) {
        String hql = "delete from AppPageDefinitionRefEntity t where t.refUuid = :refUuid and t.appPiUuid = :appPiUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("refUuid", refUuid);
        values.put("appPiUuid", appPiUuid);
        this.dao.updateByHQL(hql, values);

        String roleId = StringUtils.join(AppConstants.PAGE_DEF_PREFIX, appPiUuid, "_", refUuid);
        Role role = roleFacadeService.getRoleById(roleId);
        if (role != null) {
            for (Privilege privilege : role.getPrivileges()) {
                privilegeFacadeService.remove(privilege.getUuid());
            }
            roleFacadeService.remove(role.getUuid());
        }
        hql = "delete from PrivilegeResource t where t.resourceUuid = :resourceUuid";
        String resourceUuid = StringUtils.join(AppConstants.PAGEREF_PREFIX, appPiUuid, "_", refUuid);
        values.put("resourceUuid", resourceUuid);
        this.dao.updateByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppPageDefinitionRefService#isReferenced(java.lang.String)
     */
    @Override
    public boolean isReferenced(String pageUuid) {
        AppPageDefinitionRefEntity refEntity = new AppPageDefinitionRefEntity();
        refEntity.setRefUuid(pageUuid);
        return this.dao.countByEntity(refEntity) > 0;
    }

}
