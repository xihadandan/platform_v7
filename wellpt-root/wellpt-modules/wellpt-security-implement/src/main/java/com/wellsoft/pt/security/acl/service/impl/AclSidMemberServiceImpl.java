/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.acl.dao.AclSidMemberDao;
import com.wellsoft.pt.security.acl.entity.AclSidMember;
import com.wellsoft.pt.security.acl.service.AclSidMemberService;
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
public class AclSidMemberServiceImpl extends AbstractJpaServiceImpl<AclSidMember, AclSidMemberDao, String> implements
        AclSidMemberService {

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidMemberService#isExists(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Boolean isExists(String sid, String member, String moduleId) {
        return dao.isExists(sid, member, moduleId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidMemberService#removeMember(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void removeMember(String sid, String member, String moduleId) {
        dao.removeMember(sid, member, moduleId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidMemberService#removeAllMember(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void removeAllMember(String sid, String moduleId) {
        dao.removeAllMember(sid, moduleId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidMemberService#getMember(java.lang.String, java.lang.String)
     */
    @Override
    public List<AclSidMember> getMember(String sid, String moduleId) {
        return dao.getMember(sid, moduleId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.acl.service.AclSidMemberService#getSids(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getSids(String member, String moduleId) {
        return dao.getSids(member, moduleId);
    }

}
