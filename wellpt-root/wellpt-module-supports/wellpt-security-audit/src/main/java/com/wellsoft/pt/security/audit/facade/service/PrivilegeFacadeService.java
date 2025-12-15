/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.security.audit.bean.PrivilegeBean;
import com.wellsoft.pt.security.audit.dto.UpdatePrivilegeDto;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.Collection;
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
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
public interface PrivilegeFacadeService extends Facade {

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    PrivilegeBean getPrivilegeBean(String uuid);

    List<TreeNode> getPrivilegetTree();

    /**
     * 如何描述该方法
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

    List<TreeNode> getOtherResourceTreeOnlyCheck(String uuid, String privilegeUuid);

    /**
     * 根据权限UUID获取相应的权限树
     *
     * @param uuid
     * @return
     */
    TreeNode getResourceTree(String uuid);

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
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    Privilege get(String uuid);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<Privilege> getAll();

    /**
     * 保存工作台使用者
     */
    void saveAppPageEleIds(boolean isRef, String appPiUuid, String uuid, List<String> eleIds);

    Role initSaveAppPageRoleIds(String systemUnitId, boolean isRef, String appPiUuid, String uuid, Set<String> eleIds);

    /**
     * 查询工作台使用者节点信息
     *
     * @param uuid
     * @return
     */
    List<OrgNode> getEleIds(boolean isRef, String appPiUuid, String uuid);

    /**
     * 保存 工作台默认设置 角色权限
     */
//    void saveAppPageDef(Boolean isDefault,String appPageUuid,String appPiUuid);
    public List<PrivilegeResource> getByPrivilegeUuid(String privilegeUuid);

    List<Privilege> getPrivilegeByAppId(String appId);

    List<String> updatePrivilege(List<UpdatePrivilegeDto> dtoList);

    Privilege getSystemDefPrivilegeByCode(String code);
}
