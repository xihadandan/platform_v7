/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.multi.org.bean.OrgGroupVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupRole;

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
public interface MultiOrgGroupFacade extends BaseService {

    // 通过UUID 获取群组基本信息
    public MultiOrgGroup getGroupByUuid(String groupUuid);

    // 通过ID 获取群组基本信息
    public MultiOrgGroup getGroupById(String groupId);

    // 通过ID, 删除群组
    public boolean deleteGroup(String groupId);

    // 获取群组的所有成员
    public List<MultiOrgGroupMember> queryGroupListByMemberId(String memberId);

    // 获取所有的群组，以树形方式展示
    public List<TreeNode> queryGroupListAsTreeByType(int type);

    // 获取群组对应的权限树
    public TreeNode getGroupPrivilegeResultTree(String uuid);

    // 处理角色删除事宜
    public boolean dealRoleRemoveEvent(String roleUuid);

    // 通过角色获取拥有该角色的所有群组
    public List<MultiOrgGroup> queryGroupListByRole(String roleUuid);

    // 获取一个群组所拥有的所有角色
    public List<MultiOrgGroupRole> queryRoleListOfGroup(String groupId);

    // 处理组织版本升级事宜
    public boolean dealOrgUpgradeEvent(String oldOrgVersionId, String newOrgVersionId);

    // 通过群组ID，批量获取群组基本信息
    public List<MultiOrgGroup> getByIds(List<String> groupIds);

    // 通过ID 获取群组完整信息
    public OrgGroupVo getGroupVoById(String groupId);

    // 通过UUID 获取群组完整信息
    public OrgGroupVo getGroupVo(String uuid);

    // 修改群组
    public OrgGroupVo modifyGroup(OrgGroupVo vo);

    // 添加群组
    public OrgGroupVo addGroup(OrgGroupVo vo);

    // 给群组添加一个角色
    public void addRoleListOfGroup(String id, String roleUuid);

    // 批量删除群组
    void deleteGroups(List<String> groupIds);

    //根据 memberObjId 查询 角色Id
    Set<String> getRoleIdByGroupMember(Set<String> memberObjIdSet);

    List<MultiOrgGroup> getGroupsByIds(List<String> ids);

    List<QueryItem> queryRoleListOfGroupIds(Set<String> groupIds);
}
