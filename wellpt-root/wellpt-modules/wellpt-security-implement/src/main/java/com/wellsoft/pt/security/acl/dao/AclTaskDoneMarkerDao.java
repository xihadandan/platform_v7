/*
 * @(#)1/8/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.acl.entity.AclTaskDoneMarker;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/8/25.1	    zhulh		1/8/25		    Create
 * </pre>
 * @date 1/8/25
 */
public interface AclTaskDoneMarkerDao extends JpaDao<AclTaskDoneMarker, Long> {

    long countByAclTaskUuidAndUserId(String aclTaskUuid, String userId);

    List<String> listUserIdByAclTaskUuids(List<String> aclTaskUuids);
}
