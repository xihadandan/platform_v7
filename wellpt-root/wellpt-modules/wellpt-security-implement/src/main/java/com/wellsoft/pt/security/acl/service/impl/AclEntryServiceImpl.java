/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.dao.AclEntryDao;
import com.wellsoft.pt.security.acl.entity.AclEntry;
import com.wellsoft.pt.security.acl.service.AclEntryService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class AclEntryServiceImpl extends AbstractJpaServiceImpl<AclEntry, AclEntryDao, String>
        implements AclEntryService {

    @Override
    public List<AclEntry> findForUnread(String uuid) {
        return dao.findForUnread(uuid);
    }

    @Override
    @Transactional
    public void deleteByObjectIdIdentity(String objectIdIdentity) {
        AclEntry entity = new AclEntry();
        entity.setObjectIdIdentity(objectIdIdentity);
        entity.setGranting(Boolean.TRUE);
        Integer doneMask = 128;
        entity.setMask(doneMask);
        entity.setCreator(SpringSecurityUtils.getCurrentUserId());
        List<AclEntry> entities = this.dao.listByEntity(entity);
        if (entities.size() > 0) {
            for (AclEntry aclEntry : entities) {
                this.dao.delete(aclEntry);
            }
        }
    }

}
