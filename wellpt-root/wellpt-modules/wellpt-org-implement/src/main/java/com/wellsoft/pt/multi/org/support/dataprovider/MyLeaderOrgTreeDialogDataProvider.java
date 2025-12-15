/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.LeaderType;
import com.wellsoft.pt.multi.org.entity.MultiOrgElementLeader;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

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
@Component
public class MyLeaderOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements
        OrgTreeDialogProvider, OrgTreeAllUserProvider {
    public static final String TYPE = "MyLeader";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7418090608258760732L;
    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public String getType() {
        return TYPE;
    }

    private List<OrgTreeNode> queryMyLeaderNodeByJobList(List<OrgUserJobDto> jobList, int isNeedUser, int type) {
        List<OrgTreeNode> list = new ArrayList<OrgTreeNode>();
        List<OrgTreeNodeDto> leaderList = new ArrayList<OrgTreeNodeDto>();
        // 1,如果是单位领导，依次找到自己的职位的上级各个节点的负责人
        // 2,如果是分管领导，依次找打自己的职位，以及上级各个节点的分管领导
        if (!CollectionUtils.isEmpty(jobList)) {
            for (OrgUserJobDto job : jobList) {
                OrgTreeNodeDto jobDto = job.getOrgTreeNodeDto();
                String jobPath = jobDto.getEleIdPath();
                String[] ids = jobPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                for (int i = 0; i < ids.length; i++) {
                    List<OrgTreeNodeDto> leaders = null;
                    if (type == LeaderType.TYPE_BOSS) {
                        leaders = this.multiOrgService.queryBossLeaderNodeListByNode(ids[i], jobDto.getOrgVersionId());
                    } else if (type == LeaderType.TYPE_BRANCHED_LEADER) {
                        leaders = this.multiOrgService
                                .queryBranchLeaderNodeListByNode(ids[i], jobDto.getOrgVersionId());
                    }
                    if (!CollectionUtils.isEmpty(leaders)) {
                        leaderList.addAll(leaders);
                    }
                }
            }
            // 去掉重复
            leaderList = removeRepeatObjFormList(leaderList);
            for (OrgTreeNodeDto dto : leaderList) {
                OrgTreeNode jobNode = dto.convert2TreeNode();
                if (isNeedUser == 1) { // 需要用户
                    this.addUserToJobNode(jobNode);
                }
                list.add(jobNode);
            }
        }
        return list;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        // 是否需要包含用户数据，默认不需要
        int isNeedUser = p.getIsNeedUser(); // 是否返回用户数据
        String orgVersionId = p.getOrgVersionId();
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        // 如果没有指定用户，则用当前登录用户ID
        String userId = p.getUserId();
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }

        List<OrgUserJobDto> jobList = this.computeMyJobNodeList(userId, orgVersionId);
        // 计算我的本单位内的领导
        List<OrgTreeNode> myUnitLeaderList = this.queryMyLeaderNodeByJobList(jobList, isNeedUser, LeaderType.TYPE_BOSS);
        if (!CollectionUtils.isEmpty(myUnitLeaderList)) {
            // OrgTreeNodeDto orgTreeNodeDto =
            // jobList.get(0).getOrgTreeNodeDto();
            TreeNode myUnitNode = new TreeNode();
            myUnitNode.setName("单位领导");
            myUnitNode.setId(String.valueOf(LeaderType.TYPE_BOSS));
            // myUnitNode.setPath(orgTreeNodeDto.getEleIdPath());
            myUnitNode.setType(getType());
            myUnitNode.getChildren().addAll(myUnitLeaderList);
            treeNodeList.add(myUnitNode);
        }

        // 计算我的分管领导
        List<OrgTreeNode> myBranchLeaderList = this.queryMyLeaderNodeByJobList(jobList, isNeedUser,
                LeaderType.TYPE_BRANCHED_LEADER);
        if (!CollectionUtils.isEmpty(myBranchLeaderList)) {
            // OrgTreeNodeDto orgTreeNodeDto =
            // jobList.get(0).getOrgTreeNodeDto();
            TreeNode myBranchNode = new TreeNode();
            myBranchNode.setName("分管领导");
            myBranchNode.setId(String.valueOf(LeaderType.TYPE_BRANCHED_LEADER));
            // myBranchNode.setPath(orgTreeNodeDto.getEleIdPath());
            myBranchNode.setType(getType());
            myBranchNode.getChildren().addAll(myBranchLeaderList);
            treeNodeList.add(myBranchNode);
        }

        return treeNodeList;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        //节点Id null 根节点 组织版本查询
        if (StringUtils.isBlank(parms.getTreeNodeId())) {
            return this.getTopTreeNode(parms.getUserId(), parms.getUnitId());
        }
        return this.full(parms);
    }

    private OrgNode getBoss() {
        OrgNode boss = new OrgNode();
        boss.setName("部门领导");
        boss.setId(String.valueOf(LeaderType.TYPE_BOSS));
        boss.setType(getType());
        boss.setNocheck(true);
        boss.setOpen(true);
        boss.setIconSkin("L");
        return boss;
    }

    private OrgNode getBranched() {
        OrgNode branched = new OrgNode();
        branched.setName("分管领导");
        branched.setId(String.valueOf(LeaderType.TYPE_BRANCHED_LEADER));
        branched.setType(getType());
        branched.setNocheck(true);
        branched.setOpen(true);
        branched.setIconSkin("L");
        return branched;
    }

    private Set<String> deptIdSet(String orgVersionId, String userId) {
        List<MultiOrgTreeNode> multiOrgTreeNodes = this.getUserJobTreeNode(userId, orgVersionId);
        Set<String> deptIdSet = new HashSet<>();
        for (MultiOrgTreeNode multiOrgTreeNode : multiOrgTreeNodes) {
            String[] eleIds = multiOrgTreeNode.getEleIdPath().split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            for (String eleId : eleIds) {
                if (eleId.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                    deptIdSet.add(eleId);
                }
            }
        }
        return deptIdSet;
    }

    private List<OrgNode> getLeaderNode(String orgVersionId, Set<String> deptIdSet,
                                        List<String> checkedIds, Integer leaderType) {
        List<OrgNode> treeNodes = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("leaderType", leaderType);
        query.put("eleOrgVersionId", orgVersionId);
        query.put("eleIds", deptIdSet);
        //查询部门领导
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.listByHQL("from MultiOrgElementLeader where leaderType=:leaderType and eleOrgVersionId=:eleOrgVersionId and eleId in (:eleIds)", query);
        for (MultiOrgElementLeader leader : leaders) {
            String eleId = leader.getTargetObjId();
            if (eleId.startsWith(IdPrefix.JOB.getValue())) {
                List<OrgNode> orgNodeUsers = this.getJobUserTreeNode(leader.getTargetObjOrgVersionId(), eleId,
                        checkedIds);
                for (OrgNode orgNodeUser : orgNodeUsers) {
                    if (!treeNodes.contains(orgNodeUser)) {
                        treeNodes.add(orgNodeUser);
                    }
                }
            } else if (eleId.startsWith(IdPrefix.USER.getValue())) {
                StringBuilder hqlSb = new StringBuilder(
                        "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b");
                hqlSb.append(" where a.id = b.userId and a.isForbidden = 0 and a.id =:userId ");
                HashMap<String, Object> userQuery = new HashMap<String, Object>();
                userQuery.put("userId", eleId);
                List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(),
                        UserNode.class, userQuery);
                if (userNodes.size() > 0) {
                    OrgNode orgNode = ConvertOrgNode.convert(userNodes.get(0));
                    ConvertOrgNode.checked(orgNode, checkedIds, null);
                    if (!treeNodes.contains(orgNode)) {
                        treeNodes.add(orgNode);
                    }
                }
            }
        }
        return treeNodes;
    }


    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        List<OrgNode> treeNodes = new ArrayList<>();
        OrgNode boss = this.getBoss();
        OrgNode branched = this.getBranched();
        treeNodes.add(boss);
        treeNodes.add(branched);
        Set<String> deptIdSet = this.deptIdSet(parms.getOrgVersionId(), parms.getUserId());
        if (deptIdSet.size() == 0) {
            return treeNodes;
        }
        boss.setChildren(this.getLeaderNode(parms.getOrgVersionId(), deptIdSet, parms.getCheckedIds(), LeaderType.TYPE_BOSS));
        branched.setChildren(this.getLeaderNode(parms.getOrgVersionId(), deptIdSet, parms.getCheckedIds(), LeaderType.TYPE_BRANCHED_LEADER));
        return treeNodes;
    }


    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        List<OrgNode> treeNodes = new ArrayList<>();
        Set<String> deptIdSet = this.deptIdSet(parms.getOrgVersionId(), parms.getUserId());
        if (deptIdSet.size() == 0) {
            return treeNodes;
        }
        //key targetObjOrgVersionId set[0]:jobId set[1]:userId
        HashMap<String, Set<String>[]> jobMap = new HashMap<>();
        this.putLeaderJobMap(parms.getOrgVersionId(), deptIdSet, jobMap, LeaderType.TYPE_BOSS);
        this.putLeaderJobMap(parms.getOrgVersionId(), deptIdSet, jobMap, LeaderType.TYPE_BRANCHED_LEADER);
        treeNodes.addAll(this.searchJobUser(jobMap, parms.getKeyword()));
        return treeNodes;
    }


    private void putLeaderJobMap(String orgVersionId, Set<String> deptIdSet, HashMap<String, Set<String>[]> jobMap, Integer leaderType) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("leaderType", leaderType);
        query.put("eleOrgVersionId", orgVersionId);
        query.put("eleIds", deptIdSet);
        //查询部门领导
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.listByHQL("from MultiOrgElementLeader where leaderType = :leaderType and eleOrgVersionId=:eleOrgVersionId and eleId in (:eleIds)", query);
        for (MultiOrgElementLeader leader : leaders) {
            String eleId = leader.getTargetObjId();
            String verId = leader.getTargetObjOrgVersionId();
            if (!jobMap.containsKey(verId)) {
                jobMap.put(verId, new HashSet[]{new HashSet(), new HashSet()});
            }
            if (eleId.startsWith(IdPrefix.JOB.getValue())) {
                jobMap.get(verId)[0].add(eleId);
            } else if (eleId.startsWith(IdPrefix.USER.getValue())) {
                jobMap.get(verId)[1].add(eleId);
            }
        }
    }

    private List<OrgNode> searchJobUser(HashMap<String, Set<String>[]> jobMap, String keyword) {
        List<OrgNode> treeNodes = new ArrayList<>();
        Iterator<String> iterator = jobMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            //查询职位下用户数据
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("orgVersionId", key);
            query.put("key_eq", keyword);
            query.put("key_right", keyword + "%");
            query.put("key_left", "%" + keyword);
            query.put("key_like", "%" + keyword + "%");

            StringBuilder hqlSb = new StringBuilder(
                    "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
            hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId ");
            if (jobMap.get(key)[0].size() > 0 && jobMap.get(key)[1].size() > 0) {
                query.put("eleIds", jobMap.get(key)[0]);
                query.put("userIds", jobMap.get(key)[1]);
                hqlSb.append(" and (c.eleId in (:eleIds) or c.userId in (:userIds)) ");
            } else if (jobMap.get(key)[0].size() > 0) {
                query.put("eleIds", jobMap.get(key)[0]);
                hqlSb.append(" and c.eleId in (:eleIds) ");
            } else if (jobMap.get(key)[1].size() > 0) {
                query.put("userIds", jobMap.get(key)[1]);
                hqlSb.append(" and c.userId in (:userIds) ");
            }

            /**
             *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
             *  全模糊查询
             *  按匹配度 =**》**%》%**》%**% 排序
             */
            hqlSb.append("and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like) "
                    + "order by ("
                    + "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq OR a.userNameJp = :key_eq ) THEN 1 "
                    + "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right OR a.userNameJp LIKE :key_right ) THEN 2 "
                    + "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left OR a.userNameJp LIKE :key_left ) THEN 3 "
                    + "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like OR a.userNameJp LIKE :key_like) THEN 4 "
                    + "ELSE 5 END),a.userNamePy ");

            List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class,
                    query);
            for (UserNode userNode : userNodes) {
                //转换treeNode
                OrgNode treeNode = ConvertOrgNode.convert(userNode);
                if (!treeNodes.contains(treeNode)) {
                    treeNodes.add(treeNode);
                }
            }
        }
        return treeNodes;
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        UserNodePy userNodePy = new UserNodePy();
        Set<String> deptIdSet = this.deptIdSet(parms.getOrgVersionId(), parms.getUserId());
        if (deptIdSet.size() == 0) {
            return userNodePy;
        }

        //部门领导
        if (StringUtils.equals(parms.getTreeNodeId(), LeaderType.TYPE_BOSS + "")) {
            List<UserNode> userNodes = this.getUserNode(parms.getOrgVersionId(), deptIdSet, LeaderType.TYPE_BOSS,
                    parms.getKeyword());
            userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
            return userNodePy;
        }
        //分管领导
        if (StringUtils.equals(parms.getTreeNodeId(), LeaderType.TYPE_BRANCHED_LEADER + "")) {
            List<UserNode> userNodes = this.getUserNode(parms.getOrgVersionId(), deptIdSet,
                    LeaderType.TYPE_BRANCHED_LEADER, parms.getKeyword());
            userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
            return userNodePy;
        }
        return userNodePy;
    }

    private List<UserNode> getUserNode(String orgVersionId, Set<String> deptIdSet, Integer leaderType, String keyword) {
        List<UserNode> treeNodes = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("leaderType", leaderType);
        query.put("eleOrgVersionId", orgVersionId);
        query.put("eleIds", deptIdSet);
        //查询部门领导
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService
                .listByHQL(
                        "from MultiOrgElementLeader where leaderType = :leaderType and eleOrgVersionId=:eleOrgVersionId and eleId in (:eleIds)",
                        query);
        for (MultiOrgElementLeader leader : leaders) {
            String eleId = leader.getTargetObjId();
            if (eleId.startsWith(IdPrefix.JOB.getValue())) {
                List<UserNode> userNodes = this.getUserNodes(leader.getTargetObjOrgVersionId(), eleId, keyword);
                for (UserNode orgNodeUser : userNodes) {
                    if (!treeNodes.contains(orgNodeUser)) {
                        treeNodes.add(orgNodeUser);
                    }
                }
            } else if (eleId.startsWith(IdPrefix.USER.getValue())) {
                StringBuilder hqlSb = new StringBuilder(
                        "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b");
                hqlSb.append(" where a.id = b.userId and a.isForbidden = 0 and a.id =:userId ");
                HashMap<String, Object> userQuery = new HashMap<String, Object>();
                userQuery.put("userId", eleId);
                if (StringUtils.isNotEmpty(keyword)) {
                    query.put("key_like", "%" + keyword + "%");
                    hqlSb.append(" and (a.loginName like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like)");
                }
                List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(),
                        UserNode.class, userQuery);
                if (userNodes.size() > 0) {
                    UserNode userNode = userNodes.get(0);
                    userNode.setOrgVersionId(leader.getTargetObjOrgVersionId());
                    if (!treeNodes.contains(userNode)) {
                        treeNodes.add(userNode);
                    }
                }
            }
        }
        return treeNodes;
    }

    private List<UserNode> getUserNodes(String orgVerId, String eleId, String keyword) {
        List<UserNode> treeNodes = new ArrayList<>();
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and c.eleId = :eleId ");
        //查询职位下用户数据
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", orgVerId);
        query.put("eleId", eleId);
        query.put("isForbidden", 0);
        if (StringUtils.isNotEmpty(keyword)) {
            query.put("key_like", "%" + keyword + "%");
            hqlSb.append(" and (a.loginName like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like)");
        }
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            ConvertOrgNode.setUserNode(userNode);
            treeNodes.add(userNode);
        }
        return treeNodes;
    }
}
