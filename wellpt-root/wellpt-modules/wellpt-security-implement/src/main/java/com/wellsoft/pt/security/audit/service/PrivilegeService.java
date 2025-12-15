/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.bean.PrivilegeBean;
import com.wellsoft.pt.security.audit.bean.PrivilegeDto;
import com.wellsoft.pt.security.audit.bean.ResourceDto;
import com.wellsoft.pt.security.audit.dao.PrivilegeDao;
import com.wellsoft.pt.security.audit.dto.UpdatePrivilegeDto;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Description: 权限管理服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
public interface PrivilegeService extends JpaService<Privilege, PrivilegeDao, String> {

    /**
     * 根据UUID获取权限
     *
     * @param uuid
     * @return
     */
    PrivilegeBean getBean(String uuid);

    /**
     * 保存权限
     *
     * @param bean
     */
    String saveBean(PrivilegeBean bean);

    /**
     * 发布权限更新事件
     */
    void publishPrivilegeUpdatedEvent(String uuid);

    /**
     * 根据UUID删除权限
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据UUID，批量删除权限
     *
     * @param uuid
     */
    void removeAll(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @param queryInfo
     * @return
     */
    JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 根据权限UUID获取相应的权限树
     *
     * @param uuid
     * @return
     */
    TreeNode getResourceTree(String uuid);

    /**
     * 根据权限UUID获取相应的其他权限树
     *
     * @param uuid
     * @return
     */
    List<TreeNode> getOtherResourceTree(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param appSystemUuid
     * @return
     */
    List<TreeNode> getOtherResourceTree(String uuid, String appSystemUuid);

    /**
     * 获取所有其他类型资源的ID列表
     *
     * @return
     */
    List<String> getAllOtherResourceIds();

    /**
     * 根据USERUUID获取对应的权限列表
     * @param userUuid
     * @return
     */
    /*
     * Set<Privilege> getPrivilegesByUserUuid(String userUuid);
     *//**
     * 根据departmentUuid获取对应的权限列表
     * @param userUuid
     * @return
     */
    /*
     * Set<Privilege> getPrivilegesByDepartmentUuid(String departmentUuid);
     *//**
     * 根据jobUuid获取对应的权限列表
     * @param userUuid
     * @return
     */
    /*
     * Set<Privilege> getPrivilegesByJobUuid(String jobUuid);
     */

    /**
     * 根据jobUuid获取对应的权限列表
     *
     * @param userUuid
     * @return
     */
    /*
     * Set<Privilege> getPrivilegesByGroupUuid(String groupUuid);
     */

    List<Privilege> getAll();

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    Privilege get(String uuid);

    void save(Privilege privilege);

    /**
     * 如何描述该方法
     *
     * @param nullity
     * @return
     */
    List<TreeNode> getPrivilegetTree();

    /**
     * 如何描述该方法
     *
     * @param unitId
     * @param appIds
     * @return
     */
    List<Privilege> queryPrivilegeListOfUnitIdAndPTRoleList(String unitId, String... appIds);

    List<PrivilegeDto> queryAppRolePrivileges(String appId);

    void savePrivilegeOtherResource(String uuid, Set<PrivilegeResource> otherResources);

    void savePrivilegeRoles(String uuid, List<String> roleUuids);

    List<ResourceDto> getPrivilegeResourceByPrivilegeUuid(String uuid);

    String savePrivilegeResource(PrivilegeBean bean);

    List<TreeNode> getPrivilegeOtherResourceTreeNode(String uuid);

    PrivilegeDto getPrivilegeWithRoleDetails(String uuid);


    List<String> updatePrivilege(List<UpdatePrivilegeDto> dtoList);

    List<PrivilegeDto> getPrivilegeInTenantSystem(String system, String tenant);

    PrivilegeBean getPrivilegeBeanByCode(String code);

    Privilege getSystemDefPrivilegeByCode(String code);
}
