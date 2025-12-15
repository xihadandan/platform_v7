/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.pt.org.entity.GroupNestedGroup;

import java.util.List;

/**
 * 群组包含群组DAO
 *
 * </pre>
 */
public interface GroupNestedGroupService {
    /**
     * @param uuid
     * @return
     */
    public List<GroupNestedGroup> getByGroup(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByGroup(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteNestedGroup(String uuid);

    /**
     * @param uuid
     * @return
     */
    public List<GroupNestedGroup> getNestedGroups(String uuid);

    public void save(GroupNestedGroup entity);


}
