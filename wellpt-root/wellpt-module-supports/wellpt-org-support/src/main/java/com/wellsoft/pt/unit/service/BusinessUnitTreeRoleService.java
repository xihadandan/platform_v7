/*
 * @(#)2014-10-13 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.unit.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.BusinessUnitTreeRole;

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
 * 2014-10-13.1	zhulh		2014-10-13		Create
 * </pre>
 * @date 2014-10-13
 */
public interface BusinessUnitTreeRoleService extends BaseService {

    /**
     * 根据业务树UUID获取业务角色列表
     *
     * @param businessUnitTreeUuid
     * @return
     */
    List<BusinessUnitTreeRole> getByBusinessUnitTreeUuid(String businessUnitTreeUuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    BusinessUnitTreeRole get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void remove(BusinessUnitTreeRole businessUnitTreeRole);

    void save(BusinessUnitTreeRole role);

    List<BusinessUnitTree> getBusinessUnitTree(String tenantId);
}
