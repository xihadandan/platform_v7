/*
 * @(#)2/4/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dto;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;

import java.util.Map;

/**
 * Description: 角色组织信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/4/24.1	zhulh		2/4/24		Create
 * </pre>
 * @date 2/4/24
 */
public class OrgRelaRoleMembersDto extends BaseObject {
    private static final long serialVersionUID = -5132057596898650246L;

    private String roleUuid;

    private Map<String, String> users = Maps.newLinkedHashMapWithExpectedSize(0);

    private Map<String, String> orgElements = Maps.newLinkedHashMapWithExpectedSize(0);

    private Map<String, String> groups = Maps.newLinkedHashMapWithExpectedSize(0);

    /**
     * @return the users
     */
    public Map<String, String> getUsers() {
        return users;
    }

    /**
     * @param users 要设置的users
     */
    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    /**
     * @return the orgElements
     */
    public Map<String, String> getOrgElements() {
        return orgElements;
    }

    /**
     * @param orgElements 要设置的orgElements
     */
    public void setOrgElements(Map<String, String> orgElements) {
        this.orgElements = orgElements;
    }

    /**
     * @return the groups
     */
    public Map<String, String> getGroups() {
        return groups;
    }

    /**
     * @param groups 要设置的groups
     */
    public void setGroups(Map<String, String> groups) {
        this.groups = groups;
    }
}
