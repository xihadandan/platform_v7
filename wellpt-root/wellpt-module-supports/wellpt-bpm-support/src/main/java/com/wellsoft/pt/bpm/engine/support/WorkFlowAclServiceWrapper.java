/*
 * @(#)2013-10-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.security.acl.entity.AclSid;
import org.springframework.security.acls.model.Permission;

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
public interface WorkFlowAclServiceWrapper {

    List<AclSid> getSid(Class<TaskInstance> entityClass, String entityUuid, Permission permission);

}
