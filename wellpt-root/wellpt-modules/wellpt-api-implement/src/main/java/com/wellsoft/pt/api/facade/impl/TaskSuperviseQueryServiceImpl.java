/*
 * @(#)2019年8月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.security.acl.support.AclPermission;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
 * 2019年8月14日.1	zhulh		2019年8月14日		Create
 * </pre>
 * @date 2019年8月14日
 */
@Service(ApiServiceName.TASK_SUPERVISE_QUERY)
public class TaskSuperviseQueryServiceImpl extends TaskQueryServiceImpl {

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        return Arrays.asList(AclPermission.SUPERVISE);
    }

}
