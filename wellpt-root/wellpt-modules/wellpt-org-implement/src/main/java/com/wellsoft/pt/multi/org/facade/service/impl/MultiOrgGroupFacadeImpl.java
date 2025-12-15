/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.OrgGroupVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupMember;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupRole;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupUserRangeEntity;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupMemberService;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupRoleService;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupService;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupUserRangeService;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

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
@Service
public class MultiOrgGroupFacadeImpl extends AbstractApiFacade implements MultiOrgGroupFacade {
    private static final String GROUP_ID_PATTERN = IdPrefix.GROUP.getValue() + "0000000000";
    @Autowired
    private IdGeneratorService idGeneratorService;
    @Autowired
    private MultiOrgGroupRoleService multiOrgGroupRoleService;
    @Autowired
    private MultiOrgGroupService multiOrgGroupService;
    @Autowired
    private MultiOrgGroupMemberService multiOrgGroupMemberService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private RoleFacadeService roleService;
    @Autowired
    private MultiOrgGroupUserRangeService groupUserRangeService;

    private void checkOrgGroupVo(OrgGroupVo vo, boolean isModify) {
        Assert.notNull(vo, "参数不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getName()), "组名不能为空");
        Assert.isTrue(vo.getType() != null, "类型不能为空");
        Assert.isTrue(StringUtils.isNotBlank(vo.getCode()), "编码不能为空");
        if (isModify) {
            Assert.isTrue(StringUtils.isNotBlank(vo.getUuid()), "uuid不能为空");
        }
    }

    // 添加新群组
    @Override
    @Transactional
    public OrgGroupVo addGroup(OrgGroupVo vo) {
        // 检查参数
        checkOrgGroupVo(vo, false);

        MultiOrgGroup newGroup = new MultiOrgGroup();
        newGroup.setAttrFromOrgGroupVo(vo);
        String newId = idGeneratorService.generate(MultiOrgGroup.class, GROUP_ID_PATTERN);
        newGroup.setId(newId);
        newGroup.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        this.multiOrgGroupService.save(newGroup);

        // 保存群组成员信息
        this.addGroupMembers(newGroup.getId(), vo);

        // 保存群组使用者信息
        this.addGroupUserRange(newGroup.getId(), vo);

        // 保存群组角色信息
        this.addGroupRoles(newGroup.getId(), vo);

        // 角色信息变更 ，需要发布一下，不管是删除，还是新增，或是修改，对应的角色都需要发布下
        Set<String> roleUuids = Sets.newHashSet();
        // 新角色需要放进来
        if (StringUtils.isNotBlank(vo.getRoleUuids())) {
            roleUuids.addAll(Lists.newArrayList(vo.getRoleUuids().split(";")));
        }
        for (String roleUuid : roleUuids) {
            roleService.publishRoleUpdatedEvent(roleUuid);
        }

        vo.setUuid(newGroup.getUuid());

        return vo;
    }

    /**
     * 保存群组使用者信息
     *
     * @param groupId 群组ID
     * @param vo      使用者信息对象
     * @return void
     **/
    private void addGroupUserRange(String groupId, OrgGroupVo vo) {
        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(vo.getUserRangeReals())
                && StringUtils.isNotBlank(vo.getUserRangeDisplays())) {
            String[] userRangeDisplays = vo.getUserRangeDisplays().split(";");
            String[] userRangeReals = vo.getUserRangeReals().split(";");
            if (userRangeReals.length != userRangeDisplays.length) {
                new BusinessException("使用者信息参数数据长度不一致，请检查！");
            }
            ArrayList<MultiOrgGroupUserRangeEntity> list = Lists.newArrayList();
            MultiOrgGroupUserRangeEntity userRangeEntity = null;
            for (int i = 0; i < userRangeReals.length; i++) {
                userRangeEntity = new MultiOrgGroupUserRangeEntity();
                userRangeEntity.setGroupId(groupId);
                userRangeEntity.setUserRangeReal(userRangeReals[i]);
                userRangeEntity.setUserRangeDisplay(userRangeDisplays[i]);
                list.add(userRangeEntity);
            }
            groupUserRangeService.saveAll(list);
        }
    }

    private void addGroupMembers(String groupId, OrgGroupVo vo) {
        if (StringUtils.isNotBlank(vo.getMemberIdPaths())) {
            // 因为群组可以嵌套群组，所以需要检查是否出现嵌套死循环
            if (StringUtils.isNotBlank(vo.getMemberIdPaths())) {
                String[] memberIdPaths = vo.getMemberIdPaths().split(";");
                String[] memberNames = vo.getMemberNames().split(";");
                ArrayList<MultiOrgGroupMember> list = new ArrayList<MultiOrgGroupMember>();
                int length = memberIdPaths.length;
                for (int i = 0; i < length; i++) {
                    String idPath = memberIdPaths[i];
                    String[] idAndVer = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    String memberId = idAndVer[idAndVer.length - 1];
                    MultiOrgGroupMember m = new MultiOrgGroupMember();
                    m.setGroupId(groupId);
                    m.setMemberObjId(memberId);
                    m.setMemberObjName(memberNames[i]);
                    m.setMemberObjType(memberId.substring(0, 1));

                    if (memberId.equals(groupId)) {
                        Assert.isTrue(false, "群成员不能包含自己");
                    }
                    // 成员也是群组
                    if (memberId.startsWith(IdPrefix.GROUP.getValue())) {
                        if (this.multiOrgGroupMemberService.isMember(memberId, groupId, true)) {
                            Assert.isTrue(false, memberNames[i] + "已经包含了该群组，不能互相嵌套");
                        }
                    }
                    list.add(m);
                }
                this.multiOrgGroupMemberService.saveAll(list);
            }

        }
    }

    private void addGroupRoles(String groupId, OrgGroupVo vo) {
        if (StringUtils.isNotBlank(vo.getRoleUuids())) {
            this.multiOrgGroupRoleService.addRoleListOfGroup(groupId, vo.getRoleUuids());
        }
    }

    // 修改群组
    @Override
    @Transactional
    public OrgGroupVo modifyGroup(OrgGroupVo vo) {
        checkOrgGroupVo(vo, true);
        MultiOrgGroup group = this.multiOrgGroupService.getOne(vo.getUuid());
        Assert.isTrue(group != null, "对应的群组不存在");

        group.setAttrFromOrgGroupVo(vo);
        this.multiOrgGroupService.save(group);

        List<MultiOrgGroupRole> oldRoles = this.multiOrgGroupRoleService.queryRoleListOfGroup(group.getId());

        // 修改群组成员信息, 先删后加
        this.multiOrgGroupMemberService.deleteMemberListOfGroup(group.getId());
        this.addGroupMembers(group.getId(), vo);

        // 修改群组使用者信息, 先删后加
        this.groupUserRangeService.deleteByGroupId(group.getId());
        this.addGroupUserRange(group.getId(), vo);
        // 修改角色信息, 先删后加
        this.multiOrgGroupRoleService.deleteRoleListOfGroup(group.getId());
        this.addGroupRoles(group.getId(), vo);
        // 角色信息变更 ，需要发布一下，不管是删除，还是新增，或是修改，对应的角色都需要发布下
        Set<String> roleUuids = Sets.newHashSet();
        // 旧角色需要放进来
        if (oldRoles != null) {
            for (MultiOrgGroupRole gr : oldRoles) {
                roleUuids.add(gr.getRoleUuid());
            }
        }
        // 新角色需要放进来
        if (StringUtils.isNotBlank(vo.getRoleUuids())) {
            roleUuids.addAll(Lists.newArrayList(vo.getRoleUuids().split(";")));
        }
        for (String roleUuid : roleUuids) {
            roleService.publishRoleUpdatedEvent(roleUuid);
        }
        return vo;
    }

    /**
     * 通过UUID，获取一个群组信息
     */
    @Override
    public OrgGroupVo getGroupVo(String uuid) {
        // Assert.isTrue(StringUtils.isBlank(uuid), "参数不能为空");
        MultiOrgGroup group = this.multiOrgGroupService.getOne(uuid);
        Assert.isTrue(group != null, "对应的群组不存在");

        OrgGroupVo vo = new OrgGroupVo();
        // 设置群组信息
        BeanUtils.copyProperties(group, vo);

        // 获取成员信息
        List<MultiOrgGroupMember> members = this.multiOrgGroupMemberService.queryMemberListOfGroup(group.getId(),
                false);
        if (!CollectionUtils.isEmpty(members)) {
            String memberIdPaths = MultiOrgGroupMember.memberList2ids(members);
            String memberNames = MultiOrgGroupMember.memberList2names(members);
            vo.setMemberIdPaths(memberIdPaths);
            vo.setMemberNames(memberNames);
            vo.setMemberList(members);
        }

        // 设置群组使用者信息
        List<MultiOrgGroupUserRangeEntity> userRangeEntityList = this.groupUserRangeService
                .getUserRangeListBygroupId(group.getId());
        if (!CollectionUtils.isEmpty(userRangeEntityList)) {
            String userRangeReals = MultiOrgGroupUserRangeEntity.userRangeListToIds(userRangeEntityList);
            String userRangeDisplays = MultiOrgGroupUserRangeEntity.userRangeListToNames(userRangeEntityList);
            vo.setUserRangeReals(userRangeReals);
            vo.setUserRangeDisplays(userRangeDisplays);
        }
        // 获取角色信息
        List<MultiOrgGroupRole> roleList = this.queryRoleListOfGroup(group.getId());
        String roleUuids = ListUtils.list2StringsByField(roleList, "roleUuid");
        vo.setRoleUuids(roleUuids);

        return vo;
    }

    @Override
    public List<MultiOrgGroupRole> queryRoleListOfGroup(String groupId) {
        return this.multiOrgGroupRoleService.queryRoleListOfGroup(groupId);
    }

    // 通过groupId获取群组信息
    @Override
    public OrgGroupVo getGroupVoById(String groupId) {
        Assert.isTrue(StringUtils.isNotBlank(groupId), "groupId不能为空");
        MultiOrgGroup a = this.multiOrgGroupService.getById(groupId);
        if (a == null) {
            return null;
        }
        return this.getGroupVo(a.getUuid());
    }

    /**
     * 批量获取群组,后面需要改进成去数据库一次性拉取
     */
    // TODO
    @Override
    public List<MultiOrgGroup> getByIds(List<String> ids) {
        ArrayList<MultiOrgGroup> list = new ArrayList<MultiOrgGroup>();
        if (!CollectionUtils.isEmpty(ids)) {
            for (String id : ids) {
                list.add(this.multiOrgGroupService.getById(id));
            }
        }
        return list;
    }

    /**
     * 处理组织版本升级的对应事件
     * TODO
     */
    @Override
    public boolean dealOrgUpgradeEvent(String oldOrgVersionId, String newOrgVersionId) {
        return true;
    }

    @Override
    public List<MultiOrgGroup> queryGroupListByRole(String roleUuid) {
        ArrayList<MultiOrgGroup> list = Lists.newArrayList();
        List<MultiOrgGroupRole> objs = this.multiOrgGroupRoleService.queryGroupListByRole(roleUuid);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgGroupRole groupRole : objs) {
                MultiOrgGroup group = this.multiOrgGroupService.getById(groupRole.getGroupId());
                if (group != null) {
                    list.add(group);
                }
            }
        }
        return list;
    }

    /**
     * 角色删除，引用该角色的群组，必须跟着删除掉
     */
    @Override
    public boolean dealRoleRemoveEvent(String roleUuid) {
        this.multiOrgGroupRoleService.deleteGroupListOfRole(roleUuid);
        return true;
    }

    // 获取群组的权限结果树
    @Override
    @Transactional(readOnly = true)
    public TreeNode getGroupPrivilegeResultTree(String uuid) {
        OrgGroupVo group = this.getGroupVo(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(group.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        if (StringUtils.isNotBlank(group.getRoleUuids())) {
            String[] roles = group.getRoleUuids().split(";");
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (String roleUuid : roles) {
                Role role = this.roleService.get(roleUuid);
                TreeNode child = new TreeNode();
                child.setId(role.getUuid());
                child.setName(role.getName());
                children.add(child);
                this.roleService.buildRoleNestedRoleTree(child, role);
            }
            treeNode.setChildren(children);
        }
        return treeNode;
    }

    @Override
    public List<TreeNode> queryGroupListAsTreeByType(int type) {
        List<MultiOrgGroup> objs = this.multiOrgGroupService.queryGroupByType(type);
        List<TreeNode> list = new ArrayList<TreeNode>();
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgGroup multiOrgGroup : objs) {
                TreeNode treeNode = multiOrgGroup.convert2TreeNode();
                List<TreeNode> children = this.queryMemberListAsTree(multiOrgGroup.getId());
                if (children != null) {
                    treeNode.setChildren(children);
                }
                list.add(treeNode);
            }
        }
        return list;
    }

    // 递归获取一个群组的成员列表，以树形态展示
    private List<TreeNode> queryMemberListAsTree(String groupId) {
        List<TreeNode> list = new ArrayList<TreeNode>();
        // 获取所有的公共群组
        List<MultiOrgGroupMember> members = this.multiOrgGroupMemberService.queryMemberListOfGroup(groupId, false);
        if (!CollectionUtils.isEmpty(members)) {
            for (MultiOrgGroupMember member : members) {
                TreeNode treeNode = member.convert2TreeNode();
                // if
                // (OrgApiFacadeImpl.isMultiOrgEleNode(member.getMemberObjId()))
                // {
                // // 有可能群组成员被删了，所以需要加上一个非空判断
                // OrgTreeNodeDto dto =
                // this.orgApiFacade.getNodeOfCurrentVerisonByEleId(member.getMemberObjId());
                // if (dto != null) {
                // treeNode.setPath(dto.getEleIdPath());
                // }
                // }
                // 成员也是群组，则需要递归获取子群组的数据
                if (member.getMemberObjType().equals(IdPrefix.GROUP.getValue())) {
                    List<TreeNode> children = this.queryMemberListAsTree(member.getMemberObjId());
                    if (!CollectionUtils.isEmpty(children)) {
                        treeNode.setChildren(children);
                    }
                }
                list.add(treeNode);
            }
        }
        return list;

    }

    @Override
    public List<MultiOrgGroupMember> queryGroupListByMemberId(String id) {
        return this.multiOrgGroupMemberService.queryGroupListByMemberId(id);
    }

    /**
     * 删除群组
     * 1，删除群组
     * 2，删除群组成员
     * 3，删除群组的角色
     * 4，删除群组的嵌套关系
     */
    @Override
    @Transactional
    public boolean deleteGroup(String groupId) {
        MultiOrgGroup group = this.multiOrgGroupService.getById(groupId);
        Assert.isTrue(group != null, "对应的群组不存在");

        // 删除群组对应的成员
        this.multiOrgGroupMemberService.deleteMemberListOfGroup(group.getId());

        // 删除群组对应的角色
        this.multiOrgGroupRoleService.deleteRoleListOfGroup(group.getId());

        // 删除群组
        this.multiOrgGroupService.delete(group.getUuid());

        // 将该群组从嵌套的群组中删除
        List<MultiOrgGroupMember> parentGroups = this.queryGroupListByMemberId(group.getId());
        if (!CollectionUtils.isEmpty(parentGroups)) {
            this.multiOrgGroupMemberService.deleteByEntities(parentGroups);
        }

        // 角色信息变更 ，需要发布一下，不管是删除，还是新增，或是修改，对应的角色都需要发布下
        List<MultiOrgGroupRole> oldRoles = this.multiOrgGroupRoleService.queryRoleListOfGroup(group.getId());
        if (oldRoles != null) {
            for (MultiOrgGroupRole gr : oldRoles) {
                roleService.publishRoleUpdatedEvent(gr.getRoleUuid());
            }
        }
        return true;
    }

    @Override
    @Transactional
    public void deleteGroups(List<String> groupIds) {
        if (!CollectionUtils.isEmpty(groupIds)) {
            for (String id : groupIds) {
                this.deleteGroup(id);
            }
        }
    }

    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public MultiOrgGroup getGroupById(String groupId) {
        return this.multiOrgGroupService.getById(groupId);
    }

    @Override
    public MultiOrgGroup getGroupByUuid(String groupUuid) {
        return this.multiOrgGroupService.getOne(groupUuid);
    }

    @Override
    public void addRoleListOfGroup(String id, String roleUuid) {
        this.multiOrgGroupRoleService.addRoleListOfGroup(id, roleUuid);
    }

    @Override
    public Set<String> getRoleIdByGroupMember(Set<String> memberObjIdSet) {
        if (memberObjIdSet.size() == 0) {
            return new HashSet<>();
        }
        HashMap<String, Object> params = new HashMap<String, Object>();
        StringBuilder hql = new StringBuilder("select distinct groupId from MultiOrgGroupMember where ");
        HqlUtils.appendSql("memberObjId", params, hql, Sets.<Serializable>newHashSet(memberObjIdSet));
        List<String> groupIdList = multiOrgGroupMemberService.getDao().listCharSequenceByHQL(hql.toString(), params);
        if (groupIdList.size() == 0) {
            return new HashSet<>();
        }
        Set<String> groupIdSet = new HashSet<>(groupIdList);
        params = new HashMap<String, Object>();
        hql = new StringBuilder("select distinct roleUuid from MultiOrgGroupRole where ");
        HqlUtils.appendSql("groupId", params, hql, Sets.<Serializable>newHashSet(groupIdSet));
        List<String> roleUuidList = multiOrgGroupRoleService.getDao().listCharSequenceByHQL(hql.toString(), params);
        if (roleUuidList.size() == 0) {
            return new HashSet<>();
        }
        Set<String> roleUuidSet = new HashSet<>(roleUuidList);
        return roleUuidSet;
    }

    @Override
    public List<MultiOrgGroup> getGroupsByIds(List<String> ids) {
        return multiOrgGroupService.getGroupsByIds(ids);
    }

    @Override
    public List<QueryItem> queryRoleListOfGroupIds(Set<String> groupIds) {
        return this.multiOrgGroupRoleService.queryRoleListOfGroupIds(groupIds);
    }
}
