/*
 * @(#)2019年12月4日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service.impl;

import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.facade.service.NestedRoleFacadeService;
import com.wellsoft.pt.security.audit.service.NestedRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月4日.1	zhulh		2019年12月4日		Create
 * </pre>
 * @date 2019年12月4日
 */
@Service
public class NestedRoleFacadeServiceImpl implements NestedRoleFacadeService {

    @Autowired
    private NestedRoleService nestedRoleService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.NestedRoleFacadeService#getByRoleUuid(java.lang.String)
     */
    @Override
    public List<NestedRole> getByRoleUuid(String roleUuid) {
        return nestedRoleService.getByRole(roleUuid);
    }

    /**
     * @param roleUuid
     * @return
     */
    @Override
    public List<String> getAllParentRoleUuidsByRoleUuid(String roleUuid) {
        return nestedRoleService.getAllParentRoleUuidsByRoleUuid(roleUuid);
    }

}
