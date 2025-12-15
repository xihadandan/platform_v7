/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.jpa.util.HqlUtils;
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
@Component
public class MyUnderlingOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements
        OrgTreeDialogProvider, OrgTreeAllUserProvider {
    public static final String TYPE = "MyUnderling";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7926121271250514653L;
    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public String getType() {
        return TYPE;
    }

    // 获取我负责的节点
    private List<OrgTreeNodeDto> queryMyBossUnderlingListByJobList(List<OrgUserJobDto> jobList) {
        if (!CollectionUtils.isEmpty(jobList)) {
            List<OrgTreeNodeDto> nodeList = new ArrayList<OrgTreeNodeDto>();
            for (OrgUserJobDto jobVo : jobList) {
                OrgTreeNodeDto jobNode = jobVo.getOrgTreeNodeDto();
                // 查看该职位负责的部门
                List<OrgTreeNodeDto> myBossObjList = this.multiOrgService.queryBossUnderlingNodeListByJobId(
                        jobNode.getEleId(), jobNode.getOrgVersionId());
                if (myBossObjList != null) {
                    nodeList.addAll(myBossObjList);
                }
            }
            // 下属节点去重
            return removeRepeatObjFormList(nodeList);
        }
        return null;
    }

    // 获取我分管的节点
    private List<OrgTreeNodeDto> queryMyBranchUnderlingListByJobList(List<OrgUserJobDto> jobList) {
        // 获取我的当前职位列表
        if (!CollectionUtils.isEmpty(jobList)) {
            List<OrgTreeNodeDto> nodeList = new ArrayList<OrgTreeNodeDto>();
            for (OrgUserJobDto jobVo : jobList) {
                // 查看该职位分管的部门
                OrgTreeNodeDto jobNode = jobVo.getOrgTreeNodeDto();
                List<OrgTreeNodeDto> myBranchObjList = this.multiOrgService.queryBranchUnderlingNodeListByJobId(
                        jobNode.getEleId(), jobNode.getOrgVersionId());
                if (myBranchObjList != null) {
                    nodeList.addAll(myBranchObjList);
                }
            }
            // 下属节点去重
            return removeRepeatObjFormList(nodeList);
        }
        return null;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        // 是否需要包含用户数据，默认不需要
        int isNeedUser = p.getIsNeedUser(); // 是否返回用户数据
        boolean isInMyUnit = p.getIsInMyUnit(); // 是否包含子单位，默认不包含
        String orgVersionId = p.getOrgVersionId();

        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        // 如果没有指定用户，则用当前登录用户ID
        String userId = p.getUserId();
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }

        // 获取我的当前职位列表
        List<OrgUserJobDto> jobList = this.computeMyJobNodeList(userId, orgVersionId);
        // 获取我负责的部门节点
        List<OrgTreeNodeDto> myBossList = this.queryMyBossUnderlingListByJobList(jobList);
        if (!CollectionUtils.isEmpty(myBossList)) {
            // OrgTreeNodeDto orgTreeNodeDto = jobList.get(0).getOrgTreeNodeDto();
            TreeNode myBoss = new TreeNode();
            myBoss.setName("单位下属");
            myBoss.setId(String.valueOf(LeaderType.TYPE_BOSS));
            //myBoss.setPath(orgTreeNodeDto.getEleIdPath());
            myBoss.setType(getType());
            ArrayList<TreeNode> myBoosTreeNodeList = createOrgTreeFromNodeList(myBossList, isNeedUser, isInMyUnit);
            myBoss.getChildren().addAll(myBoosTreeNodeList);
            treeNodeList.add(myBoss);
        }

        // 获取我分管的部门节点
        List<OrgTreeNodeDto> myBranchList = this.queryMyBranchUnderlingListByJobList(jobList);
        if (!CollectionUtils.isEmpty(myBranchList)) {
            // OrgTreeNodeDto orgTreeNodeDto = jobList.get(0).getOrgTreeNodeDto();
            TreeNode myBranch = new TreeNode();
            myBranch.setName("分管下属");
            myBranch.setId(String.valueOf(LeaderType.TYPE_BRANCHED_LEADER));
            //myBranch.setPath(orgTreeNodeDto.getEleIdPath());
            myBranch.setType(getType());
            ArrayList<TreeNode> myBranchTreeNodeList = createOrgTreeFromNodeList(myBranchList, isNeedUser, isInMyUnit);
            myBranch.getChildren().addAll(myBranchTreeNodeList);
            treeNodeList.add(myBranch);
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


    private List<OrgNode> getChildren(String eleOrgVersionId, String eleId, List<String> checkedIds) {
        MultiOrgTreeNode treeNode = multiOrgTreeNodeService.queryByVerIdEleId(eleOrgVersionId, eleId);
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", eleOrgVersionId);
        query.put("eleIdPath", treeNode.getEleIdPath() + "%");
        StringBuilder hqlSb = new StringBuilder("select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        hqlSb.append(",MultiOrgTreeNode d where  d.orgVersionId=:orgVersionId and d.eleId = c.eleId and a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and d.eleIdPath like :eleIdPath ");
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        List<OrgNode> orgNodes = new ArrayList<>();
        for (UserNode userNode : userNodes) {
            OrgNode orgNode = ConvertOrgNode.convert(userNode);
            ConvertOrgNode.checked(orgNode, checkedIds, null);
            orgNodes.add(orgNode);
        }
        return orgNodes;
    }

    private OrgNode getDirect() {
        OrgNode direct = new OrgNode();
        direct.setName("直接下属");
        direct.setId(String.valueOf(LeaderType.TYPE_BOSS));
        direct.setType(getType());
        direct.setNocheck(true);
        direct.setOpen(true);
        direct.setIconSkin("L");
        return direct;
    }

    private OrgNode getInCharge() {
        OrgNode inCharge = new OrgNode();
        inCharge.setName("分管下属");
        inCharge.setId(String.valueOf(LeaderType.TYPE_BRANCHED_LEADER));
        inCharge.setType(getType());
        inCharge.setNocheck(true);
        inCharge.setOpen(true);
        inCharge.setIconSkin("L");
        return inCharge;
    }

    private List<String> userJobIds(String orgVersionId, String userId) {

        List<MultiOrgTreeNode> userJobs = this.getUserJobTreeNode(userId, orgVersionId);
        List<String> userJobIds = new ArrayList<>();
        for (MultiOrgTreeNode userJob : userJobs) {
            userJobIds.add(userJob.getEleId());
        }
        //加上用户Id 领导节点Id 可以是用户Id
        userJobIds.add(userId);
        return userJobIds;
    }


    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        List<OrgNode> treeNodes = new ArrayList<>();
        OrgNode direct = this.getDirect();
        OrgNode inCharge = this.getInCharge();
        treeNodes.add(direct);
        treeNodes.add(inCharge);
        List<String> userJobIds = this.userJobIds(parms.getOrgVersionId(), parms.getUserId());
        direct.setChildren(this.getTreeNodes(parms.getOrgVersionId(), userJobIds, parms.getCheckedIds(), LeaderType.TYPE_BOSS));
        inCharge.setChildren(this.getTreeNodes(parms.getOrgVersionId(), userJobIds, parms.getCheckedIds(), LeaderType.TYPE_BRANCHED_LEADER));
        return treeNodes;
    }

    private List<OrgNode> getTreeNodes(String orgVersionId, List<String> userJobIds, List<String> checkedIds, Integer leaderType) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("targetObjOrgVersionId", orgVersionId);
        query.put("targetObjIds", userJobIds);
        query.put("leaderType", leaderType);
        String hql = "from MultiOrgElementLeader where leaderType=:leaderType and targetObjOrgVersionId=:targetObjOrgVersionId and targetObjId in (:targetObjIds) ";
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.listByHQL(hql, query);
        List<OrgNode> treeNodes = new ArrayList<>();
        for (MultiOrgElementLeader leader : leaders) {
            List<OrgNode> orgNodeList = this.getChildren(leader.getEleOrgVersionId(), leader.getEleId(), checkedIds);
            for (OrgNode orgNode : orgNodeList) {
                if (!treeNodes.contains(orgNode)) {
                    treeNodes.add(orgNode);
                }
            }
        }
        return treeNodes;
    }

    private List<MultiOrgElementLeader> getMultiOrgElementLeaders(String orgVersionId, List<String> userJobIds, Integer leaderType) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("targetObjOrgVersionId", orgVersionId);
        query.put("targetObjIds", userJobIds);
        List<Integer> typelist = new ArrayList<>();
        if (leaderType == null) {
            typelist.add(LeaderType.TYPE_BOSS);
            typelist.add(LeaderType.TYPE_BRANCHED_LEADER);
        } else {
            typelist.add(leaderType);
        }
        query.put("leaderTypes", typelist);
        String hql = "from MultiOrgElementLeader where leaderType in (:leaderTypes) and targetObjOrgVersionId=:targetObjOrgVersionId and targetObjId in (:targetObjIds) ";
        List<MultiOrgElementLeader> leaders = this.multiOrgElementLeaderService.listByHQL(hql, query);
        return leaders;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        List<String> userJobIds = this.userJobIds(parms.getOrgVersionId(), parms.getUserId());
        List<MultiOrgElementLeader> leaderList = this.getMultiOrgElementLeaders(parms.getOrgVersionId(), userJobIds, null);
        List<UserNode> userNodes = this.searchUser(this.jobIdSet(leaderList), parms.getKeyword());
        List<OrgNode> orgNodes = new ArrayList<>();
        for (UserNode userNode : userNodes) {
            OrgNode orgNode = ConvertOrgNode.convert(userNode);
            orgNodes.add(orgNode);
        }
        return orgNodes;
    }

    private Map<String, Set<String>> jobIdSet(List<MultiOrgElementLeader> leaders) {
        Map<String, Set<String>> jobEleIdSet = new HashMap<>();
        for (MultiOrgElementLeader leader : leaders) {
            String eleId = leader.getEleId();
            String verId = leader.getEleOrgVersionId();
            this.putMapJobEleId(jobEleIdSet, eleId, verId);
        }
        return jobEleIdSet;
    }

    private void putMapJobEleId(Map<String, Set<String>> jobEleIdSet, String eleId, String verId) {
        Set<String> jobIdSet = jobEleIdSet.get(verId);
        if (jobIdSet == null) {
            jobIdSet = new HashSet<>();
            jobEleIdSet.put(verId, jobIdSet);
        }
        if (!eleId.startsWith(IdPrefix.JOB.getValue())) {
            MultiOrgTreeNode treeNode = multiOrgTreeNodeService.queryByVerIdEleId(verId, eleId);
            HashMap<String, Object> params = new HashMap<String, Object>();
            params.put("orgVersionId", verId);
            params.put("eleIdPath", treeNode.getEleIdPath() + "%");
            params.put("eleId", IdPrefix.JOB.getValue() + "%");
            List<MultiOrgTreeNode> multiOrgTreeNodeList = multiOrgTreeNodeService.listByHQL("from MultiOrgTreeNode where orgVersionId=:orgVersionId and eleId like :eleId and eleIdPath like :eleIdPath ", params);
            for (MultiOrgTreeNode node : multiOrgTreeNodeList) {
                jobIdSet.add(node.getEleId());
            }
        } else {
            jobIdSet.add(eleId);
        }
    }


    private List<UserNode> searchUser(Map<String, Set<String>> jobEleIdSet, String keyword) {
        if (jobEleIdSet.size() == 0) {
            return new ArrayList<>();
        }
        List<UserNode> userNodeList = new ArrayList<>();
        Iterator<String> iterator = jobEleIdSet.keySet().iterator();
        while (iterator.hasNext()) {
            String verId = iterator.next();
            Set<String> jobSet = jobEleIdSet.get(verId);
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("orgVersionId", verId);
            StringBuilder hqlSb = new StringBuilder("select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
            hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId = :orgVersionId and ");
            HqlUtils.appendSql("c.eleId", query, hqlSb, Sets.<Serializable>newHashSet(jobSet));
            if (StringUtils.isNotEmpty(keyword)) {
                query.put("key_eq", keyword);
                query.put("key_right", keyword + "%");
                query.put("key_left", "%" + keyword);
                query.put("key_like", "%" + keyword + "%");
                /**
                 *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
                 *  全模糊查询
                 *  按匹配度 =**》**%》%**》%**% 排序
                 */
                hqlSb.append(" and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like) " +
                        "order by (" +
                        "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq ) THEN 1 " +
                        "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right) THEN 2 " +
                        "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left) THEN 3 " +
                        "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like ) THEN 4 " +
                        "ELSE 5 END),a.userNamePy ");
            }
            List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
            for (UserNode userNode : userNodes) {
                if (!userNodeList.contains(userNode)) {
                    userNode.setOrgVersionId(verId);
                    userNodeList.add(userNode);
                }
            }
        }
        return userNodeList;
    }


    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        UserNodePy userNodePy = new UserNodePy();
        List<String> userJobIds = this.userJobIds(parms.getOrgVersionId(), parms.getUserId());

        if (StringUtils.isEmpty(parms.getTreeNodeId()) || parms.getTreeNodeId().equals(parms.getOrgVersionId())) {
            List<MultiOrgElementLeader> leaderList = this.getMultiOrgElementLeaders(parms.getOrgVersionId(), userJobIds, null);
            Map<String, Set<String>> jobIdSet = this.jobIdSet(leaderList);
            List<UserNode> userNodes = this.searchUser(jobIdSet, parms.getKeyword());
            userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
            return userNodePy;
        }
        //直接下属
        if (parms.getTreeNodeId().equals(LeaderType.TYPE_BOSS + "")) {
            List<MultiOrgElementLeader> bossList = this.getMultiOrgElementLeaders(parms.getOrgVersionId(), userJobIds, LeaderType.TYPE_BOSS);
            Map<String, Set<String>> jobIdSet = this.jobIdSet(bossList);
            List<UserNode> userNodes = this.searchUser(jobIdSet, parms.getKeyword());
            userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
            return userNodePy;
        }
        //分管下属
        if (parms.getTreeNodeId().equals(LeaderType.TYPE_BRANCHED_LEADER + "")) {
            List<MultiOrgElementLeader> branchedDeptList = this.getMultiOrgElementLeaders(parms.getOrgVersionId(), userJobIds, LeaderType.TYPE_BRANCHED_LEADER);
            Map<String, Set<String>> jobIdSet = this.jobIdSet(branchedDeptList);
            List<UserNode> userNodes = this.searchUser(jobIdSet, parms.getKeyword());
            userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
            return userNodePy;
        }

        return userNodePy;
    }

}
