/*
 * @(#)2013-2-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclSidMemberDao;
import com.wellsoft.pt.security.acl.entity.AclSidMember;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-21.1	zhulh		2013-2-21		Create
 * </pre>
 * @date 2013-2-21
 */
@Repository
public class AclSidMemberDaoImpl extends AbstractJpaDaoImpl<AclSidMember, String> implements AclSidMemberDao {
    private static final String COUNT_SID_MEMBER = "select count(*) from AclSidMember acl_sid_member where acl_sid_member.aclSid.sid =:sid and acl_sid_member.member =:member and acl_sid_member.moduleId =:moduleId";

    private static final String REMOVE_BY_SID_MEMBER = "delete from AclSidMember asm where exists (from AclSidMember acl_sid_member where asm.uuid = acl_sid_member.uuid and acl_sid_member.aclSid.sid =:sid and acl_sid_member.member =:member and acl_sid_member.moduleId =:moduleId)";

    private static final String REMOVE_BY_SID = "delete from AclSidMember asm where exists (from AclSidMember acl_sid_member where asm.uuid = acl_sid_member.uuid and acl_sid_member.aclSid.sid =:sid and acl_sid_member.moduleId =:moduleId)";

    private static final String QUERY_BY_MODULE_SID = "from AclSidMember acl_sid_member where acl_sid_member.aclSid.sid =:sid and acl_sid_member.moduleId =:moduleId";

    private static final String QUERY_SIDS = "select distinct acl_sid_member.aclSid.sid from AclSidMember acl_sid_member where acl_sid_member.member =:member and acl_sid_member.moduleId =:moduleId";

    /**
     * @param sid
     * @param member
     * @param moduleId
     * @return
     */
    public Boolean isExists(String sid, String member, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("member", member);
        params.put("moduleId", moduleId);
        return (Long) this.getNumberByHQL(COUNT_SID_MEMBER, params) > 0;
    }

    /**
     * @param sid
     * @param member
     * @param moduleId
     */
    public void removeMember(String sid, String member, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("member", member);
        params.put("moduleId", moduleId);
        this.deleteByHQL(REMOVE_BY_SID_MEMBER, params);
        this.getSession().flush();
    }

    /**
     * @param sid
     * @param moduleId
     */
    public void removeAllMember(String sid, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("moduleId", moduleId);
        this.deleteByHQL(REMOVE_BY_SID, params);
        this.getSession().flush();
    }

    /**
     * @param sid
     * @param moduleId
     * @return
     */
    public List<AclSidMember> getMember(String sid, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("sid", sid);
        params.put("moduleId", moduleId);
        return this.listByHQL(QUERY_BY_MODULE_SID, params);
    }

    /**
     * @param member
     * @param moduleId
     * @return
     */
    public List<String> getSids(String member, String moduleId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("member", member);
        params.put("moduleId", moduleId);
        return this.listCharSequenceByHQL(QUERY_SIDS, params);
    }
}
