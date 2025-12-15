/*
 * @(#)2015-4-3 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.entity.DutyAgentAuthorization;
import com.wellsoft.pt.org.service.DutyAgentAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-3.1	zhulh		2015-4-3		Create
 * </pre>
 * @date 2015-4-3
 */
@Service
@Transactional
public class DutyAgentAuthorizationServiceImpl extends BaseServiceImpl implements DutyAgentAuthorizationService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentAuthorizationService#get(java.lang.String)
     */
    @Override
    public DutyAgentAuthorization get(String uuid) {
        return this.dao.get(DutyAgentAuthorization.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentAuthorizationService#save(com.wellsoft.pt.org.entity.DutyAgentAuthorization)
     */
    @Override
    public void save(DutyAgentAuthorization dutyAgentAuthorization) {
        this.dao.save(dutyAgentAuthorization);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentAuthorizationService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        this.dao.deleteByPk(DutyAgentAuthorization.class, uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DutyAgentAuthorizationService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.dao.deleteByPk(DutyAgentAuthorization.class, uuid);
        }
    }
}
