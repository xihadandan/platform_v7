/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgUserRoleDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserRole;

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
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgUserRoleService extends JpaService<MultiOrgUserRole, MultiOrgUserRoleDao, String> {
    /**
     * 批量添加用户的角色信息
     *
     * @param userId
     * @param roleUuids
     * @return
     */
    List<MultiOrgUserRole> addRoleListOfUser(String userId, String roleUuids);

    /**
     * 删除指定用户的角色信息
     *
     * @param userId
     * @return
     */
    boolean deleteRoleListOfUser(String userId);

    /**
     * 获取指定用户的角色信息
     *
     * @param userId
     * @return
     */
    List<MultiOrgUserRole> queryRoleListOfUser(String userId);

    List<String> queryRoleUuidsOfUser(String userId);

    /**
     * 删除指定角色对应的用户
     *
     * @param roleUuid
     */
    void deleteUserListOfRole(String roleUuid);

    /**
     * 如何描述该方法
     *
     * @param roleUuid
     * @return
     */
    List<MultiOrgUserRole> queryUserListByRole(String roleUuid);
}
