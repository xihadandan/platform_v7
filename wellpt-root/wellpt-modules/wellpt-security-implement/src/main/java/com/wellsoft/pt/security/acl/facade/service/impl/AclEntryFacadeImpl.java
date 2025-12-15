/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.facade.service.impl;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.security.acl.entity.AclEntry;
import com.wellsoft.pt.security.acl.facade.service.AclEntryFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service
public class AclEntryFacadeImpl extends AbstractApiFacade implements AclEntryFacade {

    @Autowired
    private com.wellsoft.pt.security.acl.service.AclEntryService aclEntryService;

    @Override
    public List<AclEntry> findForUnread(String uuid) {
        return aclEntryService.findForUnread(uuid);
    }

}
