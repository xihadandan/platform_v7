/*
 * @(#)2013-10-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.acl.entity.AclSid;
import com.wellsoft.pt.security.acl.service.AclService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author rzhu
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-5.1	rzhu		2013-10-5		Create
 * </pre>
 * @date 2013-10-5
 */
@Service
@Transactional(readOnly = true)
public class WorkFlowAclServiceWrapperImpl extends BaseServiceImpl implements WorkFlowAclServiceWrapper {

    @Autowired
    private AclService aclService;

    @Override
    public List<AclSid> getSid(Class<TaskInstance> entityClass, String entityUuid, Permission permission) {
        return aclService.getSid(entityClass, entityUuid, permission);
    }

}
