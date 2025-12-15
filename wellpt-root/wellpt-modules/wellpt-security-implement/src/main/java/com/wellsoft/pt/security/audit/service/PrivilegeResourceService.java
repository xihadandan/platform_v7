/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.dao.PrivilegeResourceDao;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Resource;

import java.util.List;

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
public interface PrivilegeResourceService extends
        JpaService<PrivilegeResource, PrivilegeResourceDao, String> {

    /**
     * 如何描述该方法
     *
     * @param example
     * @return
     */
    List<PrivilegeResource> findByExample(PrivilegeResource example);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void deleteByPrivilegeUuid(String privilegeUuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    List<PrivilegeResource> getByPrivilegeUuid(String privilegeUuid);

    List<String> listResourceUuidsByPrivilegeUuid(String uuid);

 }
