/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementRoleDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementRole;

import java.util.List;
import java.util.Set;

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
public interface MultiOrgElementRoleService extends JpaService<MultiOrgElementRole, MultiOrgElementRoleDao, String> {
    /**
     * 批量添加节点元素的角色信息
     *
     * @param eleId
     * @param roleUuids
     * @return
     */
    List<MultiOrgElementRole> addRoleListOfElement(String eleId, String roleUuids);

    /**
     * 删除指定节点元素的角色信息
     *
     * @param eleId
     * @return
     */
    boolean deleteRoleListOfElement(String eleId);

    /**
     * 获取指定元素的角色信息
     *
     * @param eleId
     * @return
     */
    List<MultiOrgElementRole> queryRoleListOfElement(String eleId);

    /**
     * 删除指定角色的节点元素列表
     *
     * @param roleUuid
     */
    void deleteElementListOfRole(String roleUuid);

    /**
     * 获取包含指定角色的所有元素节点
     *
     * @param roleUuid
     * @return
     */
    List<MultiOrgElementRole> queryElementByRole(String roleUuid);

    List<QueryItem> queryRoleListOfElementIds(Set<String> userOrgIds);
}
