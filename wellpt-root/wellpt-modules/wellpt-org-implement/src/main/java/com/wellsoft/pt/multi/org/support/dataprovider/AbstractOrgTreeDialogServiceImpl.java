/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.collection.List2Map;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.impl.MultiOrgTreeDialogServiceImpl;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月18日.1	zyguo		2017年12月18日		Create
 * </pre>
 * @date 2017年12月18日
 */
public abstract class AbstractOrgTreeDialogServiceImpl implements OrgTreeDialogDataProvider {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Autowired
    protected MultiOrgService multiOrgService;
    @Autowired
    protected MultiOrgUserService multiOrgUserService;
    @Autowired
    protected MultiOrgTreeDialogService multiOrgTreeDialogService;
    @Autowired
    protected MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;
    @Autowired
    protected MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    protected MultiOrgUserAccountService multiOrgUserAccountService;
    @Autowired
    protected MultiOrgVersionService multiOrgVersionService;
    @Autowired
    protected MultiOrgElementService multiOrgElementService;
    @Autowired
    protected MultiOrgElementLeaderService multiOrgElementLeaderService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams params) {
        return null;
    }

    // 通过节点列表生成对应的组织树结构
    public ArrayList<TreeNode> createOrgTreeFromNodeList(List<OrgTreeNodeDto> nodeList, int isNeedUser,
                                                         boolean isInMyUnit) {
        ArrayList<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        if (!CollectionUtils.isEmpty(nodeList)) {
            for (OrgTreeNodeDto nodeDto : nodeList) {
                OrgTreeNode orgTree = this.getTreeNodeByVerisonAndEleIdPath(nodeDto.getEleIdPath(),
                        nodeDto.getOrgVersionId(), isNeedUser, isInMyUnit);
                treeNodeList.add(orgTree);
            }
        }
        return treeNodeList;
    }

    public OrgTreeNode getTreeNodeByVerisonAndEleIdPath(String eleIdPath, String orgVersionId, int isNeedUser,
                                                        boolean isInMyUnit) {
        return multiOrgTreeDialogService.getTreeNodeByVerisonAndEleIdPath(eleIdPath, orgVersionId, isNeedUser,
                isInMyUnit);
    }

    // 计算用户的职位节点
    protected List<OrgUserJobDto> computeMyJobNodeList(String userId, String orgVersionId) {
        List<OrgUserJobDto> jobList = new ArrayList<OrgUserJobDto>();
        if (StringUtils.isBlank(orgVersionId)) {
            jobList = this.multiOrgUserService.queryUserJobByUserId(userId);
        } else {
            jobList = this.multiOrgUserService.queryUserJobByOrgVersionId(userId, orgVersionId);
        }
        return jobList;
    }

    protected List<OrgTreeNodeDto> removeRepeatObjFormList(List<OrgTreeNodeDto> list) {
        Map<String, OrgTreeNodeDto> map = new List2Map<OrgTreeNodeDto>() {
            @Override
            protected String getMapKey(OrgTreeNodeDto obj) {
                return obj.getUuid();
            }
        }.convert(list);
        return new ArrayList<OrgTreeNodeDto>(map.values());
    }

    // 将用户挂到对应的职位位置下
    protected void addUserToJobNode(OrgTreeNode jobNode) {
        List<OrgUserTreeNodeDto> users = this.multiOrgUserService.queryUserByOrgTreeNode(jobNode.getId(),
                jobNode.getOrgVersionId());
        if (!CollectionUtils.isEmpty(users)) {
            for (OrgUserTreeNodeDto userDto : users) {
                OrgTreeNodeDto orgTreeNodeDto = MultiOrgTreeDialogServiceImpl.OrgUserTreeNodeDto2OrgTreeNodeDto(userDto,
                        jobNode);
                jobNode.getChildren().add(orgTreeNodeDto.convert2TreeNode());
            }
        }
    }

    /**
     * 顶级组织版本
     *
     * @param userId
     * @param unitId
     * @return
     */
    protected List<OrgNode> getTopTreeNode(String userId, String unitId) {
        List<OrgNode> treeNodes = new ArrayList<>();
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }
        // 非平台管理员用户 需指定 单位Id
        if (StringUtils.isBlank(unitId) && !MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId)) {
            unitId = SpringSecurityUtils.getCurrentUserUnitId();
        }
        List<MultiOrgVersion> allVersion = this.multiOrgVersionService
                .queryCurrentActiveVersionListOfSystemUnit(unitId);
        for (MultiOrgVersion multiOrgVersion : allVersion) {
            // 转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(multiOrgVersion);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    protected List<MultiOrgTreeNode> getUserJobTreeNode(String userId, String orgVersionId) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("userId", userId);
        query.put("orgVersionId", orgVersionId);
        // 查询 用户 职位 treeNode
        return this.multiOrgTreeNodeService.listByHQL(
                "from MultiOrgTreeNode a where a.orgVersionId=:orgVersionId and a.eleId in "
                        + "(select distinct b.eleId from MultiOrgUserTreeNode b where b.userId=:userId and b.orgVersionId=:orgVersionId) ",
                query);
    }

    protected Set<String> halfCheckIds(String orgVersionId, List<String> checkedIds) {
        if (checkedIds == null || checkedIds.size() == 0) {
            return null;
        }
        Set<String> set = new HashSet<>();
        for (String checkedId : checkedIds) {
            if (checkedId.startsWith(IdPrefix.USER.getValue())) {
                set.addAll(this.getUserJobIdSet(orgVersionId, checkedId));
            } else {
                set.add(checkedId);
            }
        }
        if (set.size() == 0) {
            return null;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        StringBuilder hqlSb = new StringBuilder("select a.eleIdPath as eleIdPath from MultiOrgTreeNode a ");
        hqlSb.append(" where a.orgVersionId=:orgVersionId and ");
        HqlUtils.appendSql("a.eleId", query, hqlSb, Sets.<Serializable>newHashSet(set));
        List<OrgNodeDto> nodeDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), OrgNodeDto.class,
                query);
        Set<String> halfCheckSet = new HashSet<>();
        for (OrgNodeDto orgNodeDto : nodeDtoList) {
            String eleIdPath = orgNodeDto.getEleIdPath();
            String[] eleIdPaths = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            // V0000000424/B0000000191/O0000000425/D0000000127/J0000000089
            if (eleIdPaths.length > 1) {
                // B0000000191/O0000000425/D0000000127/J0000000089
                for (int i = 1; i < eleIdPaths.length; i++) {
                    halfCheckSet.add(eleIdPaths[i]);
                }
            }
        }
        return halfCheckSet;
    }

    private Set<String> getUserJobIdSet(String orgVersionId, String userId) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("userId", userId);
        query.put("orgVersionId", orgVersionId);
        List<MultiOrgUserTreeNode> userTreeNodes = multiOrgUserTreeNodeService
                .listByHQL("from MultiOrgUserTreeNode where userId=:userId and orgVersionId=:orgVersionId ", query);
        Set<String> set = new HashSet<>();
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodes) {
            set.add(userTreeNode.getEleId());
        }
        return set;
    }

    /**
     * 添加子节点
     *
     * @param orgVersionId
     * @param isNeedUser
     * @param treeNodes
     * @param elements
     */
    protected void addTreeNodes(String orgVersionId, int isNeedUser, List<OrgNode> treeNodes,
                                List<MultiOrgElement> elements, List<String> checkedIds) {
        Set<String> halfCheckSet = this.halfCheckIds(orgVersionId, checkedIds);
        for (MultiOrgElement element : elements) {
            // 查询子节点数量
            int count = this.multiOrgTreeNodeService.countChildren(orgVersionId, element.getId());
            // 是否职位节点 并且 需要用户数据
            if (element.getType().equals(IdPrefix.JOB.getValue()) && isNeedUser == 1) {
                // 统计没有禁用的用户数量 累加到count
                count = count + multiOrgUserTreeNodeService.countUser(orgVersionId, element.getId());
            }
            // 转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(element, count);
            treeNode.setIdPath(orgVersionId + MultiOrgService.PATH_SPLIT_SYSMBOL + element.getId());
            // 勾选状态处理
            ConvertOrgNode.checked(treeNode, checkedIds, halfCheckSet);
            treeNodes.add(treeNode);
        }
    }

    /**
     * 添加子节点-禁用用户
     *
     * @param orgVersionId
     * @param isNeedUser
     * @param treeNodes
     * @param elements
     */
    protected void addDisTreeNodes(String orgVersionId, int isNeedUser, List<OrgNode> treeNodes,
                                   List<MultiOrgElement> elements, List<String> checkedIds) {
        Set<String> halfCheckSet = this.halfCheckIds(orgVersionId, checkedIds);
        for (MultiOrgElement element : elements) {
            // 查询子节点数量
            int count = this.multiOrgTreeNodeService.countChildren(orgVersionId, element.getId());
            // 是否职位节点 并且 需要用户数据
            if (element.getType().equals(IdPrefix.JOB.getValue()) && isNeedUser == 1) {
                // 统计没有禁用的用户数量 累加到count
                count = count + multiOrgUserTreeNodeService.countDisUser(orgVersionId, element.getId());
            }
            // 转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(element, count);
            treeNode.setIdPath(orgVersionId + MultiOrgService.PATH_SPLIT_SYSMBOL + element.getId());
            // 勾选状态处理
            ConvertOrgNode.checked(treeNode, checkedIds, halfCheckSet);
            treeNodes.add(treeNode);
        }
    }

    /**
     * 职位job 下禁用用户信息
     *
     * @param orgVersionId
     * @param treeNodeId
     */
    protected List<OrgNode> getDisJobUserTreeNode(String orgVersionId, String treeNodeId, List<String> checkedIds) {
        List<OrgNode> treeNodes = new ArrayList<>();
        // 查询职位下用户数据
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("eleId", treeNodeId);
        query.put("isForbidden", 0);
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        hqlSb.append(
                " where a.id = b.userId and a.id = c.userId  and c.orgVersionId=:orgVersionId and c.eleId = :eleId and (a.isForbidden = 1 or a.isLocked = 1)  order by a.code");
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            // 转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(userNode);
            ConvertOrgNode.checked(treeNode, checkedIds, null);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 职位job 下用户信息
     *
     * @param orgVersionId
     * @param treeNodeId
     */
    protected List<OrgNode> getJobUserTreeNode(String orgVersionId, String treeNodeId, List<String> checkedIds) {
        List<OrgNode> treeNodes = new ArrayList<>();
        // 查询职位下用户数据
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("eleId", treeNodeId);
        query.put("isForbidden", 0);
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgUserWorkInfo mouwi");
        hqlSb.append(
                " where a.id = b.userId and a.id = c.userId and a.id = mouwi.userId and a.isForbidden = 0 and mouwi.eleIdPaths is not null and c.orgVersionId=:orgVersionId and c.eleId = :eleId  order by a.code");
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            // 转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(userNode);
            ConvertOrgNode.checked(treeNode, checkedIds, null);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    /**
     * 添加职位job 下用户信息
     *
     * @param orgVersionId
     * @param treeNodeId
     * @param userNotes
     */
    protected void addJobUserNodes(String orgVersionId, String treeNodeId, List<UserNode> userNotes) {
        // 查询职位下用户数据
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("eleId", treeNodeId);
        query.put("isForbidden", 0);
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        hqlSb.append(
                " where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and c.eleId = :eleId  order by a.code");
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            userNotes.add(ConvertOrgNode.setUserNode(userNode));
        }
    }

    /**
     * 组织节点添加职位job 下用户信息
     *
     * @param systemUnitId
     * @param treeNodeId
     * @param userNotes
     */
    protected void addJobUserNodesForGroup(String systemUnitId, String treeNodeId, List<UserNode> userNotes) {
        Set<String> eleIdSet = new HashSet<>();
        Set<String> userIdSet = new HashSet<>();
        if (treeNodeId.startsWith(IdPrefix.GROUP.getValue())) {
            // 查询职位下用户数据
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("systemUnitId",
                    StringUtils.isEmpty(systemUnitId) ? SpringSecurityUtils.getCurrentUserUnitId() : systemUnitId);
            query.put("eleId", treeNodeId);
            query.put("isForbidden", 0);
            StringBuilder hqlSb = new StringBuilder(
                    "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgGroupMember d,MultiOrgGroup e ");
            hqlSb.append(
                    " where a.id = b.userId and a.isForbidden = 0 and d.memberObjId=a.id and d.groupId=e.id and e.systemUnitId=:systemUnitId and d.groupId=:eleId order by a.code");
            List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class,
                    query);
            for (UserNode userNode : userNodes) {
                userNotes.add(ConvertOrgNode.setUserNode(userNode));
                userIdSet.add(userNode.getId());
            }
            hqlSb = new StringBuilder(
                    "select a.memberObjId as id,a.memberObjName as name,a.memberObjType as type from MultiOrgGroupMember a where a.groupId = :groupId and a.memberObjType!='U' ");
            List<OrgNodeQueryItemDto> itemDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                    OrgNodeQueryItemDto.class, query);
            for (OrgNodeQueryItemDto dto : itemDtoList) {
                eleIdSet.add(dto.getId());
            }
        }
        eleIdSet.add(treeNodeId);
        for (String eleId : eleIdSet) {
            MultiOrgTreeNode treeNode = this.getTreeNode(systemUnitId, eleId);
            if (treeNode != null) {
                String orgVersionId = treeNode.getOrgVersionId();
                HashMap<String, Object> query = new HashMap<String, Object>();
                query.put("orgVersionId", orgVersionId);
                StringBuilder hqlSb = new StringBuilder(
                        "select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
                hqlSb.append(
                        ",MultiOrgTreeNode d where  d.orgVersionId=:orgVersionId and d.eleId = c.eleId and a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and d.eleIdPath like :eleIdPath ");
                query.put("eleIdPath", treeNode.getEleIdPath() + "%");
                if (userIdSet.size() > 0) {
                    hqlSb.append(" and a.id not in (:userIdSet) ");
                    query.put("userIdSet", userIdSet);
                }
                hqlSb.append(" order by a.userNamePy,a.code");
                userNotes.addAll(multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query));
            }
        }
    }

    /**
     * 组织节点添加职位job 下用户信息
     *
     * @param systemUnitId
     * @param treeNodeId
     * @param userNotes
     */
    protected void addJobUserNodesForDuty(String systemUnitId, String treeNodeId, List<UserNode> userNotes) {
        // 查询职位下用户数据
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("systemUnitId",
                StringUtils.isEmpty(systemUnitId) ? SpringSecurityUtils.getCurrentUserUnitId() : systemUnitId);
        query.put("eleId", treeNodeId);
        query.put("isForbidden", 0);
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgJobDuty d,MultiOrgDuty e ");
        hqlSb.append(
                " where a.id = b.userId and a.id = c.userId and d.jobId = c.eleId and e.id = d.dutyId and a.isForbidden = 0 and c.eleId = :eleId and e.systemUnitId=:systemUnitId order by a.code");
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            userNotes.add(ConvertOrgNode.setUserNode(userNode));
        }
    }

    /**
     * 递归获取组织子节点
     *
     * @param parentsList
     */
    protected void getGroupMemberChildrenAsTree(List<OrgNode> parentsList, int isNeedUser, String orgVersionId) {
        if (org.apache.commons.collections.CollectionUtils.isEmpty(parentsList)) {
            return;
        }
        for (OrgNode parentsNode : parentsList) {
            if (!parentsNode.getIsParent()) {
                continue;
            }
            HashMap<String, Object> query = new HashMap<>();
            query.put("groupId", parentsNode.getId());
            StringBuilder hqlSb = new StringBuilder(
                    "select a.memberObjId as id,a.memberObjName as name,a.memberObjType as type,(select count(b.uuid) from MultiOrgGroupMember b where b.groupId = a.memberObjId) as childrenCount from MultiOrgGroupMember a where a.groupId = :groupId");
            List<OrgNodeQueryItemDto> memberDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                    OrgNodeQueryItemDto.class, query);
            List<OrgNode> memberOrgNodeList = new ArrayList<>();
            for (OrgNodeQueryItemDto orgNodeQueryItemDto : memberDtoList) {
                OrgNode memberOrgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
                memberOrgNodeList.add(memberOrgNode);
                if (1 == isNeedUser) {
                    addUserChildren(memberOrgNode, orgVersionId);
                }
            }
            parentsNode.setChildren(memberOrgNodeList);
            getGroupMemberChildrenAsTree(memberOrgNodeList, isNeedUser, orgVersionId);
        }
    }

    /**
     * 职位节点 并且 需要用户数据
     *
     * @param orgNode
     * @param orgVersionId
     */
    protected void addUserChildren(OrgNode orgNode, String orgVersionId) {
        if (!orgNode.getType().startsWith(IdPrefix.JOB.getValue())) {
            return;
        }
        List<UserNode> userNodes = new ArrayList<>();
        this.addJobUserNodes(orgVersionId, orgNode.getId(), userNodes);
        List<OrgNode> childrenList = CollectionUtils.isEmpty(orgNode.getChildren()) ? new ArrayList<OrgNode>()
                : orgNode.getChildren();
        for (UserNode userNode : userNodes) {
            childrenList.add(ConvertOrgNode.convert(userNode));
        }
        orgNode.setChildren(childrenList);
    }

    protected Map<String, OrgNode> getStringOrgNodeMap(OrgNode orgNode, List<String> checkedIds, Set<String> jobIdSet,
                                                       String orgVersionId, String eleIdPath) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        // 查询所有子节点
        query.put("eleIdPath", eleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
        StringBuilder hqlSb = new StringBuilder(
                "select a.eleId as id,b.name as name,b.shortName as shortName,b.type as type,b.code as code,a.eleIdPath as eleIdPath from MultiOrgTreeNode a,MultiOrgElement b ");
        hqlSb.append(" where a.eleId = b.id and a.orgVersionId=:orgVersionId and a.eleIdPath like :eleIdPath ");
        List<OrgNodeDto> nodeDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), OrgNodeDto.class,
                query);
        // 最大长度eleIdPath
        Set<String> eleIdPathMax = new HashSet<>();
        // 转为map
        Map<String, OrgNodeDto> nodeDtoMap = new HashMap<String, OrgNodeDto>(
                (int) ((float) nodeDtoList.size() / 0.75F + 1.0F));
        for (OrgNodeDto obj : nodeDtoList) {
            nodeDtoMap.put(obj.getId(), obj);
            if (obj.getType().equals(IdPrefix.JOB.getValue())) {
                jobIdSet.add(obj.getId());
            }
            // 替换成最大长度eleIdPath
            ConvertOrgNode.containsIndexOf(eleIdPathMax, obj.getEleIdPath());
        }

        // 保存所有已处理节点
        Map<String, OrgNode> orgNodeMap = new HashMap<>();
        // 遍历 最大长度eleIdPath 就能遍历所有节点
        Iterator<String> iterator = eleIdPathMax.iterator();
        while (iterator.hasNext()) {
            String idPath = iterator.next();
            // eleIdPaths V0000000424/B0000000191/O0000000425/D0000000127/J0000000089
            idPath = idPath.replace(eleIdPath + MultiOrgService.PATH_SPLIT_SYSMBOL, "");
            String[] eleIdPaths = idPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            // 从 B0000000191 开始处理
            ConvertOrgNode.addChildren(orgNode, nodeDtoMap, orgNodeMap, eleIdPaths, 0, checkedIds);
        }
        return orgNodeMap;
    }

    protected void jobNodeAddUsers(String orgVersionId, List<String> checkedIds, Map<String, OrgNode> orgNodeMap,
                                   Set<String> jobIdSet) {
        if (jobIdSet.size() == 0) {
            return;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("isForbidden", 0);
        StringBuilder uhqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,b.sex as sex,a.code as code,c.eleId as eleId from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgUserWorkInfo mouwi");
        uhqlSb.append(
                " where a.id = b.userId and a.id = c.userId and a.id = mouwi.userId and a.isForbidden = 0 and mouwi.eleIdPaths is not null and c.orgVersionId=:orgVersionId and ");
        HqlUtils.appendSql("c.eleId", query, uhqlSb, Sets.<Serializable>newHashSet(jobIdSet));
        List<OrgNodeUserDto> userNodes = multiOrgUserAccountService.listItemHqlQuery(uhqlSb.toString(),
                OrgNodeUserDto.class, query);
        for (OrgNodeUserDto userNode : userNodes) {
            OrgNode userJob = orgNodeMap.get(userNode.getEleId());
            if (userJob == null) {
                continue;
            }
            TreeMap<String, List<OrgNode>> userJobTreeMap = userJob.getTreeMap();
            if (userJobTreeMap == null) {
                userJobTreeMap = new TreeMap<>();
                userJob.setTreeMap(userJobTreeMap);
            }
            List<OrgNode> userOrgNodeList = userJobTreeMap.get(userNode.getCode());
            if (userOrgNodeList == null) {
                userOrgNodeList = new ArrayList<>();
                userJobTreeMap.put(userNode.getCode(), userOrgNodeList);
            }
            OrgNode userOrgNode = ConvertOrgNode.convert(userNode);
            ConvertOrgNode.checked(userOrgNode, checkedIds, null);
            userOrgNodeList.add(userOrgNode);

        }
    }

    protected void jobNodeAddDisUsers(String orgVersionId, List<String> checkedIds, Map<String, OrgNode> orgNodeMap,
                                      Set<String> jobIdSet) {
        if (jobIdSet.size() == 0) {
            return;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVersionId);
        query.put("isForbidden", 1);
        StringBuilder uhqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,b.sex as sex,a.code as code,c.eleId as eleId from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        uhqlSb.append(
                " where a.id = b.userId and a.id = c.userId and a.isForbidden = 1 and c.orgVersionId=:orgVersionId and ");
        HqlUtils.appendSql("c.eleId", query, uhqlSb, Sets.<Serializable>newHashSet(jobIdSet));
        List<OrgNodeUserDto> userNodes = multiOrgUserAccountService.listItemHqlQuery(uhqlSb.toString(),
                OrgNodeUserDto.class, query);
        for (OrgNodeUserDto userNode : userNodes) {
            OrgNode userJob = orgNodeMap.get(userNode.getEleId());
            if (userJob == null) {
                continue;
            }
            TreeMap<String, List<OrgNode>> userJobTreeMap = userJob.getTreeMap();
            if (userJobTreeMap == null) {
                userJobTreeMap = new TreeMap<>();
                userJob.setTreeMap(userJobTreeMap);
            }
            List<OrgNode> userOrgNodeList = userJobTreeMap.get(userNode.getCode());
            if (userOrgNodeList == null) {
                userOrgNodeList = new ArrayList<>();
                userJobTreeMap.put(userNode.getCode(), userOrgNodeList);
            }
            OrgNode userOrgNode = ConvertOrgNode.convert(userNode);
            ConvertOrgNode.checked(userOrgNode, checkedIds, null);
            userOrgNodeList.add(userOrgNode);

        }
    }

    private void addGroupChildren(String unitId, OrgNode orgNode, List<String> checkedIds, int isNeedUser) {
        MultiOrgTreeNode treeNode = this.getTreeNode(unitId, orgNode.getId());
        if (treeNode == null) {
            return;
        }
        Set<String> jobIdSet = new HashSet<>();
        Map<String, OrgNode> orgNodeMap = this.getStringOrgNodeMap(orgNode, checkedIds, jobIdSet,
                treeNode.getOrgVersionId(), treeNode.getEleIdPath());
        // 需要用户数据
        if (isNeedUser == 1) {
            // 没有子结点，且群组下的成员就是职位时（没有包含部门结点），直接取当前节点的用户
            if (orgNodeMap.size() == 0 && orgNode.getType().equals(IdPrefix.JOB.getValue())
                    && StringUtils.isNotBlank(treeNode.getEleIdPath())) {
                orgNodeMap.put(orgNode.getId(), orgNode);
                jobIdSet.add(orgNode.getId());
            }
            jobNodeAddUsers(treeNode.getOrgVersionId(), checkedIds, orgNodeMap, jobIdSet);
        }
    }

    protected List<OrgNode> getFullGroupNodeList(String groupId, int nameDisplayMethod, String userId, String unitId, int groupType,
                                                 List<String> checkedIds, int isNeedUser) {
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<>();
        StringBuilder hqlSb = new StringBuilder();
        Set<String> groupIdSet = this.groupIdSet(groupId);
        // 查根节点
        hqlSb.append("select a.id as id,a.name as name,a.type as type from MultiOrgGroup a where a.type = :groupType ");
        if (groupType == MultiOrgGroup.TYPE_MY_GROUP) {
            // 个人群组只能自己查看自己的
            query.put("creator", StringUtils.isEmpty(userId) ? SpringSecurityUtils.getCurrentUserId() : userId);
            hqlSb.append(" and a.creator = :creator ");
        } else {
            // 公共群组看整个单位的
            query.put("systemUnitId",
                    StringUtils.isEmpty(unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId);
            hqlSb.append(" and a.systemUnitId = :systemUnitId ");
        }
        if (groupIdSet.size() > 0) {
            query.put("groupIds", groupIdSet);
            hqlSb.append(" and a.id in (:groupIds)");
        }
        query.put("groupType", groupType);// 0:公共群组;1:我的群组
        List<OrgNodeQueryItemDto> orgNodeQueryItemDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                OrgNodeQueryItemDto.class, query);
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : orgNodeQueryItemDtoList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            ConvertOrgNode.checked(orgNode, checkedIds, null);
            if (orgNode.getType().equals(IdPrefix.GROUP.getValue())) {
                // 查子节点member
                query = new HashMap<>();
                hqlSb = new StringBuilder();
                query.put("groupId", orgNode.getId());
                hqlSb.append(
                        "select a.memberObjId as id,a.memberObjName as name,a.memberObjType as type from MultiOrgGroupMember a where a.groupId = :groupId");
                List<OrgNodeQueryItemDto> childrenList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                        OrgNodeQueryItemDto.class, query);
                List<OrgNode> orgNodeChildrenList = new ArrayList<>();
                for (OrgNodeQueryItemDto nodeQueryItemDto : childrenList) {
                    OrgNode children = ConvertOrgNode.convert(nodeQueryItemDto);
                    ConvertOrgNode.checked(children, checkedIds, null);
                    orgNodeChildrenList.add(children);
                    if (!children.getType().equals(IdPrefix.USER.getValue())
                            && !children.getType().equals(IdPrefix.GROUP.getValue())) {
                        this.addGroupChildren(unitId, children, checkedIds, isNeedUser);
                    }
                }
                if (orgNodeChildrenList.size() > 0) {
                    this.addNamePath(nameDisplayMethod, orgNodeChildrenList);
                }
                orgNode.setChildren(orgNodeChildrenList);
            } else if (!orgNode.getType().equals(IdPrefix.USER.getValue())) {
                this.addGroupChildren(unitId, orgNode, checkedIds, isNeedUser);
            }
            resultList.add(orgNode);
        }

        return resultList;

    }

    private Set<String> groupIdSet(String groupId) {
        Set<String> set = new LinkedHashSet<>();
        if (StringUtils.isBlank(groupId)) {
            return set;
        }
        String[] eleIds = groupId.split(";");
        for (String eleId : eleIds) {
            if (eleId.startsWith(IdPrefix.GROUP.getValue())) {
                set.add(eleId);
            }
        }
        return set;
    }

    /**
     * 获取公共群组/我的群组节点
     *
     * @param userId
     * @param unitId
     * @param treeNodeId
     * @param groupType
     * @return
     */
    protected List<OrgNode> getGroupNodeList(String groupId, int nameDisplayMethod, String userId, String unitId, String treeNodeId,
                                             int groupType, List<String> checkedIds, int isNeedUser) {
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<>();
        StringBuilder hqlSb = new StringBuilder();
        Set<String> groupIdSet = this.groupIdSet(groupId);
        if (StringUtils.isEmpty(treeNodeId)) {
            // 查根节点
            hqlSb.append(
                    "select a.id as id,a.name as name,a.type as type,(select count(b.uuid) from MultiOrgGroupMember b where b.groupId = a.id) as childrenCount from MultiOrgGroup a where a.type = :groupType ");
            if (groupType == MultiOrgGroup.TYPE_MY_GROUP) {
                // 个人群组只能自己查看自己的
                query.put("creator", StringUtils.isEmpty(userId) ? SpringSecurityUtils.getCurrentUserId() : userId);
                hqlSb.append(" and a.creator = :creator ");
            } else {
                // 公共群组看整个单位的
                query.put("systemUnitId",
                        StringUtils.isEmpty(unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId);
                hqlSb.append(" and a.systemUnitId = :systemUnitId ");
            }
            query.put("groupType", groupType);// 0:公共群组;1:我的群组
            if (groupIdSet.size() > 0) {
                query.put("groupIds", groupIdSet);
                hqlSb.append(" and a.id in (:groupIds)");
            }
        } else if (treeNodeId.startsWith(IdPrefix.GROUP.getValue())) {
            // 查子节点member
            query.put("groupId", treeNodeId);
            hqlSb.append(
                    "select a.memberObjId as id,a.memberObjName as name,a.memberObjType as type,(select count(b.uuid) from MultiOrgGroupMember b where b.groupId = a.memberObjId) as childrenCount from MultiOrgGroupMember a where a.groupId = :groupId");
        }
        if (hqlSb.length() > 0) {
            List<OrgNodeQueryItemDto> orgNodeQueryItemDtoList = multiOrgTreeNodeService
                    .listItemHqlQuery(hqlSb.toString(), OrgNodeQueryItemDto.class, query);
            Set<String> halfCheckSet = this.halfCheckIdsForGroup(unitId, checkedIds);
            for (OrgNodeQueryItemDto orgNodeQueryItemDto : orgNodeQueryItemDtoList) {
                OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
                if (orgNode.getType().equals("0") || orgNode.getType().equals("1")) {
                    // 节点是群组时 //0:公共群组;1:我的群组
                    // 获取群组-成员
                    hqlSb = new StringBuilder();
                    // 查子节点member
                    query = Maps.newHashMap();
                    query.put("groupId", orgNode.getId());
                    hqlSb.append(
                            "select a.memberObjId as id,a.memberObjName as name,a.memberObjType as type,(select count(b.uuid) from MultiOrgGroupMember b where b.groupId = a.memberObjId) as childrenCount from MultiOrgGroupMember a where a.groupId = :groupId");
                    List<OrgNodeQueryItemDto> orgNodeGroupMemberQueryItemDtoList = multiOrgTreeNodeService
                            .listItemHqlQuery(hqlSb.toString(), OrgNodeQueryItemDto.class, query);
                    for (OrgNodeQueryItemDto nodeGroupMemberQueryItemDto : orgNodeGroupMemberQueryItemDtoList) {
                        OrgNode orgNodeTemp = ConvertOrgNode.convert(nodeGroupMemberQueryItemDto);
                        getGroupNodeListByHandler(orgNodeTemp, unitId, checkedIds, isNeedUser, halfCheckSet);
                    }
                } else {
                    getGroupNodeListByHandler(orgNode, unitId, checkedIds, isNeedUser, halfCheckSet);
                }
                resultList.add(orgNode);
            }
            if (resultList.size() > 0) {
                this.addNamePath(nameDisplayMethod, resultList);
            }
            return resultList;
        }
        MultiOrgTreeNode treeNode = this.getTreeNode(unitId, treeNodeId);
        if (treeNode == null) {
            return resultList;
        }
        String orgVersionId = treeNode.getOrgVersionId();
        // 职位节点 并且 需要用户数据
        if (treeNodeId.startsWith(IdPrefix.JOB.getValue()) && isNeedUser == 1) {
            return this.getJobUserTreeNode(orgVersionId, treeNodeId, checkedIds);
        }
        List<MultiOrgElement> elements = multiOrgTreeNodeService.children(orgVersionId, treeNodeId);
        this.addTreeNodes(orgVersionId, isNeedUser, resultList, elements, checkedIds);

        return resultList;
    }

    protected void addNamePath(int nameDisplayMethod, List<OrgNode> resultList) {

        if (CollectionUtils.isEmpty(resultList)) {
            // 群组下没有成员，跳过，不做处理，避免组织弹出框因后台异常不显示数据。（前端有对成员进行非空校验，数据库数据还是没有成员，可能是脏数据或者问题数据）
            return;
        }

        List<String> nodeIdList = new ArrayList<>();
        List<String> nodeNameList = new ArrayList<>();
        for (OrgNode orgNode : resultList) {
            nodeIdList.add(orgNode.getId());
            nodeNameList.add(orgNode.getName());
        }
        Map<String, OrgNode> map = multiOrgTreeDialogService.smartName(nameDisplayMethod, nodeIdList, nodeNameList);
        for (OrgNode orgNode : resultList) {
            OrgNode smartNode = map.get(orgNode.getId());
            orgNode.setSmartNamePath(smartNode.getSmartNamePath());
            orgNode.setNamePath(smartNode.getNamePath());
            orgNode.setShortName(smartNode.getShortName());
        }
    }

    protected MultiOrgTreeNode getTreeNode(String unitId, String treeNodeId) {
        // 非平台管理员用户 需指定 单位Id
        if (StringUtils.isBlank(unitId)) {
            unitId = SpringSecurityUtils.getCurrentUserUnitId();
        }
        HashMap<String, Object> query = new HashMap<>();
        query.put("systemUnitId", unitId);
        query.put("eleId", treeNodeId);
        List<MultiOrgTreeNode> multiOrgTreeNodeList = this.multiOrgTreeNodeService.listByHQL(
                "select b from MultiOrgVersion a,MultiOrgTreeNode b where a.id = b.orgVersionId and a.status=1 and a.systemUnitId=:systemUnitId and b.eleId=:eleId ",
                query);
        if (multiOrgTreeNodeList.size() > 0) {
            MultiOrgTreeNode treeNode = multiOrgTreeNodeList.get(0);
            return treeNode;
        }
        return null;
    }

    /**
     * @param keyword
     * @param groupType:0:公共群组/1:我的群组
     * @return
     */
    protected List<OrgNode> searchUserInGroup(String unitId, String userId, String keyword, int groupType) {
        Assert.hasText(keyword, "keyword 不能为空");
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("key_eq", keyword);
        query.put("key_right", keyword + "%");
        query.put("key_left", "%" + keyword);
        query.put("key_like", "%" + keyword + "%");
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgGroup d,MultiOrgGroupMember e ");
        hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 ");
        hqlSb.append(" and c.eleId = e.memberObjId and e.groupId = d.id ");
        query.put("groupType", groupType);
        hqlSb.append(" and d.type = :groupType ");
        if (groupType == 0) {// 公共群组
            // 公共群组看整个单位的
            query.put("systemUnitId",
                    StringUtils.isEmpty(unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId);
            hqlSb.append(" and d.systemUnitId = :systemUnitId ");
        } else {
            // 个人群组只能自己查看自己的
            query.put("creator", StringUtils.isEmpty(userId) ? SpringSecurityUtils.getCurrentUserId() : userId);
            hqlSb.append(" and d.creator = :creator ");
        }
        /**
         *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
         *  全模糊查询
         *  按匹配度 =**》**%》%**》%**% 排序
         */
        hqlSb.append(
                "and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like) "
                        + "order by ("
                        + "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq ) THEN 1 "
                        + "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right) THEN 2 "
                        + "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left) THEN 3 "
                        + "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like ) THEN 4 "
                        + "ELSE 5 END),a.userNamePy ");

        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            // 转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(userNode);
            resultList.add(treeNode);
        }
        return resultList;
    }

    /**
     * 获取公共群组/我的群组所有节点
     *
     * @param systemUnitId
     * @param userId
     * @param unitId
     * @param groupType
     * @return
     */
    protected List<OrgNode> getGroupNodeParentsAndMemberList(String systemUnitId, String userId, String unitId,
                                                             String keyword, int groupType) {
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<>();
        query.put("keyword", "%" + keyword + "%");
        query.put("groupType", groupType);// 0:公共群组;1:我的群组
        StringBuilder hqlSbParents = new StringBuilder();
        hqlSbParents.append(
                "select a.id as id,a.name as name,'G' as type from MultiOrgGroup a where a.type = :groupType and a.name like :keyword ");
        if (groupType == MultiOrgGroup.TYPE_MY_GROUP) {
            // 个人群组只能自己查看自己的
            query.put("creator", StringUtils.isEmpty(userId) ? SpringSecurityUtils.getCurrentUserId() : userId);
            hqlSbParents.append(" and a.creator = :creator ");
        } else {
            // 公共群组看整个单位的
            query.put("systemUnitId",
                    StringUtils.isEmpty(unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId);
            hqlSbParents.append(" and a.systemUnitId = :systemUnitId ");
        }
        List<OrgNodeQueryItemDto> parentsList = multiOrgTreeNodeService.listItemHqlQuery(hqlSbParents.toString(),
                OrgNodeQueryItemDto.class, query);
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : parentsList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            resultList.add(orgNode);
        }
        StringBuilder hqlSbChildren = new StringBuilder();
        hqlSbChildren.append(
                "select b.memberObjId as id,b.memberObjName as name,b.memberObjType as type from MultiOrgGroup a,MultiOrgGroupMember b where a.id = b.groupId and a.type = :groupType and b.memberObjName like :keyword ");
        List<OrgNodeQueryItemDto> childrenList = multiOrgTreeNodeService.listItemHqlQuery(hqlSbChildren.toString(),
                OrgNodeQueryItemDto.class, query);
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : childrenList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            resultList.add(orgNode);
        }
        return resultList;
    }

    protected Set<String> halfCheckIdsForGroup(String systemUnitId, List<String> checkedIds) {
        if (checkedIds == null || checkedIds.size() == 0) {
            return null;
        }
        systemUnitId = StringUtils.isEmpty(systemUnitId) ? SpringSecurityUtils.getCurrentUserUnitId() : systemUnitId;
        Set<String> halfCheckSet = new HashSet<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("systemUnitId", systemUnitId);
        StringBuilder hqlSb = new StringBuilder(
                "select a.memberObjId as id,a.groupId as parentsId from MultiOrgGroupMember a where ");
        HqlUtils.appendSql("a.memberObjId", query, hqlSb, Sets.<Serializable>newHashSet(checkedIds));
        List<OrgNodeQueryItemDto> multiOrgJobDutyList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                OrgNodeQueryItemDto.class, query);
        if (!CollectionUtils.isEmpty(multiOrgJobDutyList)) {
            for (OrgNodeQueryItemDto multiOrgJobDuty : multiOrgJobDutyList) {
                halfCheckSet.add(multiOrgJobDuty.getId());
                halfCheckSet.add(multiOrgJobDuty.getParentsId());
            }
        }
        return halfCheckSet;
    }

    /**
     * 职务群组的职位节点获取用户数据
     *
     * @param orgNode
     */
    protected void addUserChildrenForGroup(String systemUnitId, OrgNode orgNode) {
        List<UserNode> userNodes = new ArrayList<>();
        this.addJobUserNodesForGroup(
                StringUtils.isEmpty(systemUnitId) ? SpringSecurityUtils.getCurrentUserUnitId() : systemUnitId,
                orgNode.getId(), userNodes);
        if (!org.apache.commons.collections.CollectionUtils.isEmpty(userNodes)) {
            List<OrgNode> childrenList = org.apache.commons.collections.CollectionUtils.isEmpty(orgNode.getChildren())
                    ? new ArrayList<OrgNode>()
                    : orgNode.getChildren();
            for (UserNode userNode : userNodes) {
                OrgNode user = ConvertOrgNode.convert(userNode);
                if (!childrenList.contains(user)) {
                    childrenList.add(user);
                }
            }
            orgNode.setChildren(childrenList);
        }
    }

    private void getGroupNodeListByHandler(OrgNode orgNode, String unitId, List<String> checkedIds, int isNeedUser,
                                           Set<String> halfCheckSet) {
        if (!orgNode.getType().equals(IdPrefix.GROUP.getValue())
                && !orgNode.getType().equals(IdPrefix.USER.getValue())) {
            MultiOrgTreeNode treeNode = this.getTreeNode(unitId, orgNode.getId());
            if (treeNode == null) {
                return;
            }
            String orgVersionId = treeNode.getOrgVersionId();
            Set<String> halfSet = this.halfCheckIds(orgVersionId, checkedIds);
            ConvertOrgNode.checked(orgNode, checkedIds, halfSet);
            // 查询子节点数量
            int count = this.multiOrgTreeNodeService.countChildren(orgVersionId, orgNode.getId());
            // 是否职位节点 并且 需要用户数据
            if (orgNode.getType().equals(IdPrefix.JOB.getValue()) && isNeedUser == 1) {
                // 统计没有禁用的用户数量 累加到count
                count = count + multiOrgUserTreeNodeService.countUser(orgVersionId, orgNode.getId());
            }
            orgNode.setIsParent(count > 0 ? true : null);
        } else {
            ConvertOrgNode.checked(orgNode, checkedIds, halfCheckSet);
        }
    }

}
