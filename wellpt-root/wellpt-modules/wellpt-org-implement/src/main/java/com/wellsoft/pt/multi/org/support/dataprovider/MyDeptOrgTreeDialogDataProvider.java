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
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
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
public class MyDeptOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements
        OrgTreeDialogProvider, OrgTreeAllUserProvider {
    public static final String TYPE = "MyDept";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4922122952464733669L;

    @Override
    public String getType() {
        return TYPE;
    }

    // 获取我的当前版本部门节点
    private List<OrgTreeNodeDto> computeMyDeptNodeList(String userId, String orgVersionId) {
        // 获取我的当前职位列表
        List<OrgUserJobDto> jobList = this.computeMyJobNodeList(userId, orgVersionId);

        if (!CollectionUtils.isEmpty(jobList)) {
            List<OrgTreeNodeDto> nodeList = new ArrayList<OrgTreeNodeDto>();
            for (OrgUserJobDto jobVo : jobList) {
                OrgTreeNodeDto nodeDto = jobVo.getOrgTreeNodeDto();
                String jobOrgVersionId = nodeDto.getOrgVersionId();
                String deptId = nodeDto.getDeptId();
                // 因为有些职位是直接挂在单位下，是没有上级部门节点的，所有如果没有的话，则采用他的上级节点
                if (StringUtils.isBlank(deptId)) {
                    deptId = nodeDto.getParentId();
                }
                OrgTreeNodeDto deptNode = this.multiOrgService.getNodeByEleIdAndOrgVersion(deptId, jobOrgVersionId);
                nodeList.add(deptNode);
            }
            // 因为一个用户可能担任同一个部门的不同职位，所以需要去重
            return removeRepeatObjFormList(nodeList);
        }
        return null;
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

        List<OrgTreeNodeDto> deptList = this.computeMyDeptNodeList(userId, orgVersionId);
        treeNodeList = createOrgTreeFromNodeList(deptList, isNeedUser, false);
        return treeNodeList;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        //节点Id null 根节点 组织版本查询
        if (StringUtils.isBlank(parms.getTreeNodeId())) {
            return this.getTopTreeNode(parms.getUserId(), parms.getUnitId());
        }
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");

        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        List<OrgNode> treeNodes = new ArrayList<>();
        //查询组织版本下的 我的部门
        if (parms.getOrgVersionId().equals(parms.getTreeNodeId())) {
            List<MultiOrgTreeNode> multiOrgTreeNodes = this.getUserJobTreeNode(parms.getUserId(), parms.getOrgVersionId());
            Set<String> deptIdSet = new HashSet<>();
            for (MultiOrgTreeNode multiOrgTreeNode : multiOrgTreeNodes) {
                String deptId = multiOrgTreeNode.getDeptId();
                // 因为有些职位是直接挂在单位下，是没有上级部门节点的，所有如果没有的话，则采用他的上级节点
                if (StringUtils.isBlank(deptId)) {
                    deptId = multiOrgTreeNode.getParentId();
                    //如果上级 是组织版本 就直接返回 职位节点Id
                    if (deptId.startsWith(IdPrefix.ORG_VERSION.getValue())) {
                        deptId = multiOrgTreeNode.getEleId();
                    }
                }
                deptIdSet.add(deptId);
            }
            if (deptIdSet.size() == 0) {
                return treeNodes;
            }
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("ids", deptIdSet);
            List<MultiOrgElement> elements = this.multiOrgElementService.listByHQL("from MultiOrgElement where id in (:ids) order by code ", query);
            this.addTreeNodes(parms.getOrgVersionId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());
            return treeNodes;
        }

        /**
         * 根据code排序 查询子节点数据
         * treeNodeId 没有严格校验是否 我的部门 及下级节点
         * 如需校验 需要根据上面查询 我的部门代码来获取部门 信息来校验
         */
        List<MultiOrgElement> elements = multiOrgTreeNodeService.children(parms.getOrgVersionId(), parms.getTreeNodeId());
        this.addTreeNodes(parms.getOrgVersionId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());
        //职位节点 并且 需要用户数据
        if (parms.getTreeNodeId().startsWith(IdPrefix.JOB.getValue()) && parms.getIsNeedUser() == 1) {
            treeNodes = this.getJobUserTreeNode(parms.getOrgVersionId(), parms.getTreeNodeId(), parms.getCheckedIds());
        }
        return treeNodes;
    }


    /**
     * 部门 eleIdPath
     *
     * @return
     */
    private Set<String> deptIdPathSet(String userId, String orgVersionId) {
        Set<String> deptIdPathSet = new HashSet<>();
        List<MultiOrgTreeNode> multiOrgTreeNodes = this.getUserJobTreeNode(userId, orgVersionId);
        for (MultiOrgTreeNode multiOrgTreeNode : multiOrgTreeNodes) {
            String deptIdPath = multiOrgTreeNode.getDeptIdPath();
            // 因为有些职位是直接挂在单位下，是没有上级部门节点的，所有如果没有的话，则采用他的上级节点
            if (StringUtils.isBlank(deptIdPath)) {
                deptIdPath = multiOrgTreeNode.getParentIdPath();
            }
            //上级部门为顶级 取当前职位eleIdPath
            if (deptIdPath.indexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) < 0) {
                deptIdPath = multiOrgTreeNode.getEleIdPath();
            }
            deptIdPathSet.add(deptIdPath);
        }
        return deptIdPathSet;
    }


    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }

        Set<String> deptIdPathSet = this.deptIdPathSet(parms.getUserId(), parms.getOrgVersionId());
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", parms.getOrgVersionId());
        //查询 所有 部门节点数据
        StringBuilder hqlSb = new StringBuilder("select a.eleId as id,b.name as name,b.type as type,b.code as code,a.eleIdPath as eleIdPath from MultiOrgTreeNode a,MultiOrgElement b ");
        hqlSb.append(" where a.eleId = b.id and a.orgVersionId=:orgVersionId ");
        this.addHql(hqlSb, deptIdPathSet, query, "a", "eleIdPath");
        List<OrgNodeDto> nodeDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), OrgNodeDto.class, query);
        //最大长度eleIdPath
        Set<String> eleIdPathMax = new HashSet<>();
        //转为map
        Map<String, OrgNodeDto> nodeDtoMap = new HashMap<String, OrgNodeDto>((int) ((float) nodeDtoList.size() / 0.75F + 1.0F));
        //保存JosIds
        Set<String> jobIdsSet = new HashSet<>();
        for (OrgNodeDto obj : nodeDtoList) {
            if (obj != null) {
                nodeDtoMap.put(obj.getId(), obj);
                if (obj.getId().startsWith(IdPrefix.JOB.getValue())) {
                    jobIdsSet.add(obj.getId());
                }
                //替换成最大长度eleIdPath
                ConvertOrgNode.containsIndexOf(eleIdPathMax, obj.getEleIdPath());
            }
        }
        //构造顶级节点
        OrgNode orgNode = new OrgNode();
        orgNode.setId(parms.getOrgVersionId());
        orgNode.setTreeMap(new TreeMap<String, List<OrgNode>>());
        //保存所有已处理节点
        Map<String, OrgNode> orgNodeMap = new HashMap<>();
        //遍历 最大长度eleIdPath 就能遍历所有节点
        Iterator<String> eleIdPathMaxIterator = eleIdPathMax.iterator();
        while (eleIdPathMaxIterator.hasNext()) {
            String eleIdPath = eleIdPathMaxIterator.next();
            //eleIdPaths V0000000424/B0000000191/O0000000425/D0000000127/J0000000089
            String[] eleIdPaths = eleIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            int index = getDeptIndex(deptIdPathSet, eleIdPath);
            ConvertOrgNode.addChildren(orgNode, nodeDtoMap, orgNodeMap, eleIdPaths, index - 1, parms.getCheckedIds());
        }

        //需要用户数据
        if (parms.getIsNeedUser() == 1) {
            query.put("orgVersionId", parms.getOrgVersionId());
            query.put("isForbidden", 0);
            StringBuilder uhqlSb = new StringBuilder("select a.id as id,a.userName as name,b.sex as sex,a.code as code,c.eleId as eleId from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
            uhqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and ");
            HqlUtils.appendSql("c.eleId", query, uhqlSb, Sets.<Serializable>newHashSet(jobIdsSet));
            List<OrgNodeUserDto> userNodes = multiOrgUserAccountService.listItemHqlQuery(uhqlSb.toString(), OrgNodeUserDto.class, query);
            for (OrgNodeUserDto userNode : userNodes) {
                OrgNode userJob = orgNodeMap.get(userNode.getEleId());
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
                ConvertOrgNode.checked(userOrgNode, parms.getCheckedIds(), null);
                userOrgNodeList.add(userOrgNode);

            }
        }

        List<OrgNode> orgNodes = new ArrayList<>();
        for (List<OrgNode> nodes : orgNode.getTreeMap().values()) {
            orgNodes.addAll(nodes);
        }
        return orgNodes;
    }

    private int getDeptIndex(Set<String> deptIdPathSet, String eleIdPath) {
        Iterator<String> iterator = deptIdPathSet.iterator();
        while (iterator.hasNext()) {
            String deptIdPath = iterator.next();
            if (eleIdPath.contains(deptIdPath)) {
                return deptIdPath.split(MultiOrgService.PATH_SPLIT_SYSMBOL).length;
            }
        }
        return 1;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("userId", parms.getUserId());
        query.put("orgVersionId", parms.getOrgVersionId());
        query.put("key_eq", parms.getKeyword());
        query.put("key_right", parms.getKeyword() + "%");
        query.put("key_left", "%" + parms.getKeyword());
        query.put("key_like", "%" + parms.getKeyword() + "%");
        //查询 用户 职位 treeNode
        List<MultiOrgTreeNode> multiOrgTreeNodes = this.getUserJobTreeNode(parms.getUserId(), parms.getOrgVersionId());
        Set<String> deptIdPathSet = new HashSet<>();
        for (MultiOrgTreeNode multiOrgTreeNode : multiOrgTreeNodes) {
            String deptIdPath = multiOrgTreeNode.getDeptIdPath();
            // 因为有些职位是直接挂在单位下，是没有上级部门节点的，所有如果没有的话，则采用他的上级节点
            if (StringUtils.isBlank(deptIdPath)) {
                deptIdPath = multiOrgTreeNode.getParentIdPath();
            }
            //上级部门为顶级 取当前职位eleIdPath
            if (deptIdPath.indexOf(MultiOrgService.PATH_SPLIT_SYSMBOL) < 0) {
                deptIdPath = multiOrgTreeNode.getEleIdPath();
            }
            deptIdPathSet.add(deptIdPath);
        }

        //查询 所有 部门节点数据
        StringBuilder hqlSb = new StringBuilder("select a.eleId as id,b.name as name,b.type as type,b.code as code,a.eleIdPath as eleIdPath from MultiOrgTreeNode a,MultiOrgElement b ");
        hqlSb.append(" where a.eleId = b.id and a.orgVersionId=:orgVersionId and (b.name like :key_like or b.shortName like :key_like) ");
        this.addHql(hqlSb, deptIdPathSet, query, "a", "eleIdPath");
        hqlSb.append(" order by (" +
                "CASE WHEN (b.name = :key_eq OR b.shortName = :key_eq ) THEN 1 " +
                "WHEN (b.name LIKE :key_right OR b.shortName LIKE :key_right) THEN 2 " +
                "WHEN (b.name LIKE :key_left OR b.shortName LIKE :key_left ) THEN 3 " +
                "WHEN (b.name LIKE :key_like OR b.shortName LIKE :key_like ) THEN 4 " +
                "ELSE 5 END),b.code ");
        List<OrgNodeDto> nodeDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), OrgNodeDto.class, query);
        List<OrgNode> treeNodes = new ArrayList<>();
        //需要用户数据
        if (parms.getIsNeedUser() == 1) {
            StringBuilder hqlu = new StringBuilder("select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgTreeNode d");
            hqlu.append(" where a.id = b.userId and a.id = c.userId and c.eleId=d.eleId and c.orgVersionId=d.orgVersionId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId ");
            this.addHql(hqlu, deptIdPathSet, query, "d", "eleIdPath");
            /**
             *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
             *  全模糊查询
             *  按匹配度 =**》**%》%**》%**% 排序
             */
            hqlu.append("and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like) " +
                    "order by (" +
                    "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq OR a.userNameJp = :key_eq ) THEN 1 " +
                    "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right  OR a.userNameJp LIKE :key_right ) THEN 2 " +
                    "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left OR a.userNameJp LIKE :key_left ) THEN 3 " +
                    "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like OR a.userNameJp LIKE :key_like ) THEN 4 " +
                    "ELSE 5 END),a.userNamePy ");

            List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlu.toString(), UserNode.class, query);
            for (UserNode userNode : userNodes) {
                //转换treeNode
                OrgNode treeNode = ConvertOrgNode.convert(userNode);
                treeNodes.add(treeNode);
            }
        }
        for (OrgNodeDto orgNodeDto : nodeDtoList) {
            OrgNode treeNode = ConvertOrgNode.convert(orgNodeDto);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    private void addHql(StringBuilder hqlSb, Set<String> deptIdPathSet, HashMap<String, Object> query, String as, String fieldName) {
        hqlSb.append(" and (");
        Iterator<String> iterator = deptIdPathSet.iterator();
        int i = 1;
        while (iterator.hasNext()) {
            hqlSb.append(" ").append(as).append(".").append(fieldName).append(" like :").append(fieldName).append(i).append(" or");
            query.put(fieldName + i, iterator.next() + "%");
            i++;
        }
        hqlSb.deleteCharAt(hqlSb.length() - 1).deleteCharAt(hqlSb.length() - 1);
        hqlSb.append(") ");
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(parms.getUserId())) {
            parms.setUserId(SpringSecurityUtils.getCurrentUserId());
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", parms.getOrgVersionId());
        query.put("userId", parms.getUserId());
        StringBuilder hqlSb = new StringBuilder("select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from " +
                "MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgTreeNode d " +
                "where  d.orgVersionId=:orgVersionId and d.eleId = c.eleId and a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId ");
        if (StringUtils.isNotBlank(parms.getTreeNodeId()) && !parms.getOrgVersionId().equals(parms.getTreeNodeId())) {
            MultiOrgTreeNode treeNode = multiOrgTreeNodeService.queryByVerIdEleId(parms.getOrgVersionId(), parms.getTreeNodeId());
            query.put("eleIdPath", treeNode.getEleIdPath() + "%");
            hqlSb.append("and d.eleIdPath like :eleIdPath ");
        } else {
            Set<String> deptIdPathSet = this.deptIdPathSet(parms.getUserId(), parms.getOrgVersionId());
            this.addHql(hqlSb, deptIdPathSet, query, "d", "eleIdPath");
        }
        if (StringUtils.isNotBlank(parms.getKeyword())) {
            query.put("key_like", "%" + parms.getKeyword() + "%");
            hqlSb.append(" and (a.loginName like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like )");
        }
        if (StringUtils.isNotEmpty(parms.getSort()) && parms.getSort().equals("code")) {
            hqlSb.append(" order by a.code");
        } else {
            hqlSb.append(" order by a.userNamePy");
        }
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        UserNodePy userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
        return userNodePy;
    }
}
