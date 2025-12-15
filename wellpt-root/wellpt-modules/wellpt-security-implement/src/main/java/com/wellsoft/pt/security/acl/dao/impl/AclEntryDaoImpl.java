package com.wellsoft.pt.security.acl.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.security.acl.dao.AclEntryDao;
import com.wellsoft.pt.security.acl.entity.AclEntry;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class AclEntryDaoImpl extends AbstractJpaDaoImpl<AclEntry, String> implements AclEntryDao {
    private final static String COUNT_ACLENTRY = "select count(*) from AclEntry acl_entry where acl_entry.aclObjectIdentity.uuid =:identityUuid";

    private final static String MAX_ACE = "select max(acl_entry.aceOrder) from AclEntry acl_entry where acl_entry.aclObjectIdentity.uuid =:identityUuid";

    private static final String QUERY_USER_FOR_UNREAD = "select a1 from AclEntry a1 where a1.mask = 512 and a1.objectIdIdentity = :uuid";

    public List<AclEntry> findForUnread(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return this.listByHQL(QUERY_USER_FOR_UNREAD, params);
    }

    public Long countByObjectIdentity(Long identityId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("identityUuid", identityId);
        return (Long) getNumberByHQL(COUNT_ACLENTRY, params);
    }

    /**
     * @param identityId
     * @return
     */
    public Integer getMaxAceByAclObjectIdentity(Long identityId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("identityUuid", identityId);
        return (Integer) getNumberByHQL(MAX_ACE, params);
    }
}
