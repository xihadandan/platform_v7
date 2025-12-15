/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgGroupRoleDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupRole;

import java.util.List;
import java.util.Set;

/**
 * Description:
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgGroupRoleService extends JpaService<MultiOrgGroupRole, MultiOrgGroupRoleDao, String> {

    /**
     * 批量添加用户的角色信息
     *
     * @param groupId
     * @param roleUuids
     * @return
     */
    List<MultiOrgGroupRole> addRoleListOfGroup(String groupId, String roleUuids);

    /**
     * 删除指定用户的角色信息
     *
     * @param groupId
     * @return
     */
    boolean deleteRoleListOfGroup(String groupId);

    /**
     * 获取指定用户的角色信息
     *
     * @param groupId
     * @return
     */
    List<MultiOrgGroupRole> queryRoleListOfGroup(String groupId);

    /**
     * 删除指定角色对应的用户
     *
     * @param roleUuid
     */
    void deleteGroupListOfRole(String roleUuid);

    /**
     * 通过角色获取指定的组
     *
     * @param roleUuid
     * @return
     */
    List<MultiOrgGroupRole> queryGroupListByRole(String roleUuid);

    List<QueryItem> queryRoleListOfGroupIds(Set<String> groupIds);
}
