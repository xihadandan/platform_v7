package com.wellsoft.pt.security.acl.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.acl.entity.AclEntry;

import java.util.List;

public interface AclEntryDao extends JpaDao<AclEntry, String> {

    public List<AclEntry> findForUnread(String uuid);

    Long countByObjectIdentity(Long identityId);

    /**
     * @param identityId
     * @return
     */
    Integer getMaxAceByAclObjectIdentity(Long identityId);
}
