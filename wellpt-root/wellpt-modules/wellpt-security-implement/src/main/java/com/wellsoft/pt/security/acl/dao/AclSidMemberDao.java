/*
 * @(#)2013-2-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.dao;

import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.security.acl.entity.AclSidMember;

import java.util.List;

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
public interface AclSidMemberDao extends JpaDao<AclSidMember, String> {

    /**
     * @param sid
     * @param member
     * @param moduleId
     * @return
     */
    Boolean isExists(String sid, String member, String moduleId);

    /**
     * @param sid
     * @param member
     * @param moduleId
     */
    void removeMember(String sid, String member, String moduleId);

    /**
     * @param sid
     * @param moduleId
     */
    void removeAllMember(String sid, String moduleId);

    /**
     * @param sid
     * @param moduleId
     * @return
     */
    List<AclSidMember> getMember(String sid, String moduleId);

    /**
     * @param member
     * @param moduleId
     * @return
     */
    List<String> getSids(String member, String moduleId);

}
