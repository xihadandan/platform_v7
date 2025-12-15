/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.acl.dao.AclSidDao;
import com.wellsoft.pt.security.acl.entity.AclSid;

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
public interface AclSidService extends JpaService<AclSid, AclSidDao, String> {
    AclSid get(AclSid aclSid);

    boolean isExists(boolean principal, String sid);

    List<AclSid> listBySids(List<String> sids);
}
