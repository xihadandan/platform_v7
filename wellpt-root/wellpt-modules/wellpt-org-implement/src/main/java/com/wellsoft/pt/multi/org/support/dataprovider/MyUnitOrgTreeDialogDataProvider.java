/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class MyUnitOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements OrgTreeDialogProvider,
        OrgTreeAllUserProvider {

    public static final String TYPE = "MyUnit";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 442201326011028822L;
    Logger logger = LoggerFactory.getLogger(MyUnitOrgTreeDialogDataProvider.class);

    @Override
    public String getType() {
        return TYPE;
    }

    // 计算我的单位对应的节点列表
    private List<OrgTreeNodeDto> computeMyUnitNodeList(String orgVersionId, String unitId) {
        List<OrgTreeNodeDto> unitList = new ArrayList<OrgTreeNodeDto>();
        // 如果有指定版本，则采用指定版本的职位列表，没有指定版本，则采用当前单位的最新版本
        if (StringUtils.isBlank(orgVersionId)) {
            // 如果有指定单位，则返回指定单位的，如果没有则返回用户的当前单位
            if (StringUtils.isBlank(unitId)) {
                unitId = SpringSecurityUtils.getCurrentUserUnitId();
            }
            unitList = this.queryMyUnitNodeOfCurrentVersion(unitId);
        } else {
            MultiOrgVersion ver = this.multiOrgVersionService.getById(orgVersionId);
            OrgTreeNodeDto unitNode = new OrgTreeNodeDto();
            unitNode.setEleId(ver.getId());
            unitNode.setEleIdPath(ver.getId());
            unitNode.setOrgVersionId(orgVersionId);
            unitList.add(unitNode);
        }
        return unitList;
    }

    // 获取我的当前版本的单位节点
    private List<OrgTreeNodeDto> queryMyUnitNodeOfCurrentVersion(String unitId) {
        List<MultiOrgVersion> verList = this.multiOrgVersionService.queryCurrentActiveVersionListOfSystemUnit(unitId);
        List<OrgTreeNodeDto> list = new ArrayList<OrgTreeNodeDto>();
        if (!CollectionUtils.isEmpty(verList)) {
            Collections.sort(verList, new Comparator<MultiOrgVersion>() {
                @Override
                public int compare(MultiOrgVersion o1, MultiOrgVersion o2) {
                    if (o1.getIsDefault() && false == o2.getIsDefault()) {
                        return -1;
                    }
                    return 1;
                }
            });
            for (MultiOrgVersion ver : verList) {
                OrgTreeNodeDto dto = new OrgTreeNodeDto();
                dto.setEleId(ver.getId());
                dto.setEleIdPath(ver.getId());
                dto.setOrgVersionId(ver.getId());
                list.add(dto);
            }
        }
        return list;
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        Stopwatch timer = Stopwatch.createStarted();
        logger.info("加载组织结构树，开始");
        // 是否需要包含用户数据，默认不需要
        int isNeedUser = p.getIsNeedUser(); // 是否返回用户数据
        boolean isInMyUnit = p.getIsInMyUnit(); // 是否包含子单位，默认不包含
        String orgVersionId = p.getOrgVersionId();
        String eleIdPath = p.getOtherParams().get("eleIdPath");

        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        // 如果没有指定用户，则用当前登录用户ID
        String userId = p.getUserId();
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }
        // 如果是平台管理员，则列出所有的系统单位单位
        if (MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId)) {
            List<MultiOrgVersion> allVersion = this.multiOrgVersionService.queryCurrentActiveVersionListOfAllUnit();
            for (MultiOrgVersion ver : allVersion) {
                if (StringUtils.isNotBlank(p.getUnitId()) && !p.getUnitId().equals(ver.getSystemUnitId())) {//有指定unitid
                    continue;
                }
                OrgTreeNode treeNode = this.getTreeNodeByVerisonAndEleIdPath(ver.getId(), ver.getId(), isNeedUser,
                        isInMyUnit);
                if (treeNode != null) {
                    treeNodeList.add(treeNode);
                }
            }
        } else {
            // 如果有指定 eleIdPath, 则以 eleIdPath 为主
            if (StringUtils.isBlank(eleIdPath)) {
                // 获取本单位的节点就可以
                List<OrgTreeNodeDto> unitList = this.computeMyUnitNodeList(orgVersionId, p.getUnitId());
                treeNodeList = this.createOrgTreeFromNodeList(unitList, isNeedUser, isInMyUnit);
            } else {
                String[] paths = eleIdPath.split(";");
                for (String path : paths) {
                    String verId = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0];
                    OrgTreeNode treeNode = this.getTreeNodeByVerisonAndEleIdPath(path, verId, isNeedUser, isInMyUnit);
                    treeNodeList.add(treeNode);
                }
            }
        }
        logger.info("加载组织结构树，耗时：{}", timer.stop());
        return treeNodeList;
    }

    private Set<String> eleIdPath(String eleIdPath) {
        String[] paths = eleIdPath.split(";");
        Set<String> set = new LinkedHashSet<>();
        for (String path : paths) {
            //替换成最小长度eleIdPath
            ConvertOrgNode.containsMinIndexOf(set, path);
        }
        return set;
    }

    private Set<String> eleIdPahtVerIdSet(String eleIdPath) {
        Set<String> eleIdPathSet = this.eleIdPath(eleIdPath);
        Set<String> verIdSet = new LinkedHashSet<>();
        for (String path : eleIdPathSet) {
            String verId = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0];
            verIdSet.add(verId);
        }
        return verIdSet;
    }

    private String getUserId(String userId) {
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }
        return userId;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        String userId = this.getUserId(parms.getUserId());
        String eleIdPath = parms.getOtherParams().get("eleIdPath");
        List<OrgNode> treeNodes = new ArrayList<>();
        //节点Id null 根节点 组织版本查询
        if (StringUtils.isBlank(parms.getTreeNodeId())) {
            if (MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId) || StringUtils.isBlank(eleIdPath)) {
                return this.getTopTreeNode(parms.getUserId(), parms.getUnitId());
            }
            Set<String> verIdSet = this.eleIdPahtVerIdSet(eleIdPath);
            for (String verId : verIdSet) {
                MultiOrgVersion orgVersion = this.multiOrgVersionService.getById(verId);
                //转换treeNode
                OrgNode treeNode = ConvertOrgNode.convert(orgVersion);
                treeNodes.add(treeNode);
            }
            return treeNodes;
        }
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        //用户节点 直接返回
        if (parms.getTreeNodeId().startsWith(IdPrefix.USER.getValue())) {
            return treeNodes;
        }
        List<MultiOrgElement> elements = null;
        if (!MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId) &&
                StringUtils.isNotBlank(eleIdPath) &&
                parms.getOrgVersionId().equals(parms.getTreeNodeId())) {
            elements = this.getMultiOrgElement(eleIdPath, parms.getOrgVersionId());
        } else {
            elements = multiOrgTreeNodeService.children(parms.getOrgVersionId(), parms.getTreeNodeId());
        }
        this.addTreeNodes(parms.getOrgVersionId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());
        //职位节点 并且 需要用户数据
        if (parms.getTreeNodeId().startsWith(IdPrefix.JOB.getValue()) && parms.getIsNeedUser() == 1) {
            treeNodes = this.getJobUserTreeNode(parms.getOrgVersionId(), parms.getTreeNodeId(), parms.getCheckedIds());
        }
        return treeNodes;
    }

    private List<MultiOrgElement> getMultiOrgElement(String eleIdPath, String orgVersionId) {
        List<MultiOrgElement> elements = new ArrayList<>();
        Set<String> eleIdPathSet = this.eleIdPath(eleIdPath);
        Iterator<String> iterator = eleIdPathSet.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            String verId = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[0];
            if (!verId.equals(orgVersionId)) {
                iterator.remove();
            }
        }
        for (String path : eleIdPathSet) {
            String[] pathStrs = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String eleId = pathStrs[pathStrs.length - 1];
            if (eleId.equals(orgVersionId)) {
                elements.addAll(multiOrgTreeNodeService.children(orgVersionId, orgVersionId));
                return elements;
            }
            MultiOrgElement element = multiOrgElementService.getById(eleId);
            elements.add(element);
        }
        return elements;
    }

    private Map<String, OrgNode> orgNodeMap(OrgNode orgNode, OrgTreeDialLogAsynParms parms, Set<String> jobIdSet) {
        return this.getStringOrgNodeMap(orgNode, parms.getCheckedIds(), jobIdSet, parms.getOrgVersionId(), parms.getOrgVersionId());
    }


    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        String userId = this.getUserId(parms.getUserId());
        String eleIdPath = parms.getOtherParams().get("eleIdPath");
        Map<String, OrgNode> orgNodeMap = new HashMap<>();
        Map<String, OrgNodeDto> nodeDtoMap = new HashMap<>();
        Set<String> jobIdSet = new HashSet<>();
        //构造顶级节点
        OrgNode orgNode = new OrgNode();
        orgNode.setId(parms.getOrgVersionId());
        orgNode.setTreeMap(new TreeMap<String, List<OrgNode>>());
        if (!MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId) && StringUtils.isNotBlank(eleIdPath)) {
            Set<String> eleIdPathSet = this.eleIdPath(eleIdPath);
            for (String path : eleIdPathSet) {
                if (!path.startsWith(parms.getOrgVersionId())) {
                    continue;
                }
                HashMap<String, Object> query = new HashMap<String, Object>();
                query.put("orgVersionId", parms.getOrgVersionId());
                //查询所有子节点
                String[] pathStrs = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                String eleId = pathStrs[pathStrs.length - 1];
                if (eleId.equals(parms.getOrgVersionId())) {
                    query.put("eleIdPath", parms.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
                } else {
                    query.put("eleIdPath", path + "%");
                }
                StringBuilder hqlSb = new StringBuilder(
                        "select a.eleId as id,b.name as name,b.shortName as shortName,b.type as type,b.code as code,a.eleIdPath as eleIdPath from MultiOrgTreeNode a,MultiOrgElement b ");
                hqlSb.append(" where a.eleId = b.id and a.orgVersionId=:orgVersionId and a.eleIdPath like :eleIdPath ");
                List<OrgNodeDto> nodeDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(), OrgNodeDto.class, query);
                for (OrgNodeDto obj : nodeDtoList) {
                    nodeDtoMap.put(obj.getEleIdPath(), obj);
                    orgNodeMap.put(obj.getId(), ConvertOrgNode.convert(obj));
                    if (obj.getType().equals(IdPrefix.JOB.getValue())) {
                        jobIdSet.add(obj.getId());
                    }
                }
            }
            for (String key : nodeDtoMap.keySet()) {
                String[] eleIdPaths = key.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                int index = eleIdPaths.length - 2;
                if (index < 0) {
                    continue;
                }
                String eleId = eleIdPaths[index];
                OrgNode node = orgNodeMap.get(eleId);
                if (node == null) {
                    continue;
                }
                OrgNodeDto orgNodeDto = nodeDtoMap.get(key);
                List<OrgNode> orgNodes = this.getOrgNodeList(node, orgNodeDto);
                orgNodes.add(orgNodeMap.get(orgNodeDto.getId()));
            }

            for (String path : eleIdPathSet) {
                if (!path.startsWith(parms.getOrgVersionId())) {
                    continue;
                }
                if (path.equals(parms.getOrgVersionId())) {
                    orgNodeMap = this.orgNodeMap(orgNode, parms, jobIdSet);
                    break;
                } else {
                    OrgNodeDto orgNodeDto = nodeDtoMap.get(path);
                    List<OrgNode> orgNodes = this.getOrgNodeList(orgNode, orgNodeDto);
                    orgNodes.add(orgNodeMap.get(orgNodeDto.getId()));
                }

            }
        } else {
            orgNodeMap = this.orgNodeMap(orgNode, parms, jobIdSet);
        }
        //需要用户数据
        if (parms.getIsNeedUser() == 1) {
            jobNodeAddUsers(parms.getOrgVersionId(), parms.getCheckedIds(), orgNodeMap, jobIdSet);
        }
        List<OrgNode> orgNodes = new ArrayList<>();
        for (List<OrgNode> nodes : orgNode.getTreeMap().values()) {
            orgNodes.addAll(nodes);
        }
        return orgNodes;
    }

    private List<OrgNode> getOrgNodeList(OrgNode node, OrgNodeDto orgNodeDto) {
        TreeMap<String, List<OrgNode>> treeMap = node.getTreeMap();
        if (treeMap == null) {
            treeMap = new TreeMap<>();
            node.setTreeMap(treeMap);
        }
        List<OrgNode> orgNodes = treeMap.get(orgNodeDto.getCode());
        if (orgNodes == null) {
            orgNodes = new ArrayList<>();
            //以code 排序
            treeMap.put(orgNodeDto.getCode(), orgNodes);
        }
        return orgNodes;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        String userId = this.getUserId(parms.getUserId());
        String eleIdPath = parms.getOtherParams().get("eleIdPath");
        if (!MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId) && StringUtils.isNotBlank(eleIdPath)) {
            return this.searchEleIdPath(parms, eleIdPath);
        }
        List<OrgNode> treeNodes = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", parms.getOrgVersionId());
        query.put("key_eq", parms.getKeyword());
        query.put("key_right", parms.getKeyword() + "%");
        query.put("key_left", "%" + parms.getKeyword());
        query.put("key_like", "%" + parms.getKeyword() + "%");
        //需要用户数据
        if (parms.getIsNeedUser() == 1) {

            StringBuilder hqlSb = new StringBuilder(
                    "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b ");
            hqlSb.append(" where a.id = b.userId and a.isForbidden = 0 and exists(select 1 from MultiOrgUserTreeNode c where a.id = c.userId and c.orgVersionId=:orgVersionId )");
            /**
             *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
             *  全模糊查询
             *  按匹配度 =**》**%》%**》%**% 排序
             */
            hqlSb.append("and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like) "
                    + "order by ("
                    + "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq OR a.userNameJp = :key_eq ) THEN 1 "
                    + "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right OR a.userNameJp LIKE :key_right ) THEN 2 "
                    + "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left OR a.userNameJp LIKE :key_left) THEN 3 "
                    + "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like OR a.userNameJp LIKE :key_like) THEN 4 "
                    + "ELSE 5 END),a.userNamePy ");

            List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class,
                    query);
            for (UserNode userNode : userNodes) {
                //转换treeNode
                OrgNode treeNode = ConvertOrgNode.convert(userNode);
                treeNodes.add(treeNode);
            }
        }

        query.put("eleIdPath", parms.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");

        /**
         *  子查询MultiOrgTreeNode 指定 orgVersionId ; eleIdPath like 获取下一级
         *  全模糊查询
         *  按匹配度 =**》**%》%**》%**% 排序
         */
        String hql = "from MultiOrgElement a where exists (select 1 from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleId=a.id and b.eleIdPath like :eleIdPath) "
                + "and (a.name like :key_like or a.shortName like :key_like) "
                + "order by ("
                + "CASE WHEN (a.name = :key_eq OR a.shortName = :key_eq ) THEN 1 "
                + "WHEN (a.name LIKE :key_right OR a.shortName LIKE :key_right) THEN 2 "
                + "WHEN (a.name LIKE :key_left OR a.shortName LIKE :key_left ) THEN 3 "
                + "WHEN (a.name LIKE :key_like OR a.shortName LIKE :key_like ) THEN 4 " + "ELSE 5 END),a.code ";
        List<MultiOrgElement> elements = multiOrgElementService.listByHQL(hql, query);
        for (MultiOrgElement element : elements) {
            OrgNode treeNode = ConvertOrgNode.convert(element, 0);
            treeNodes.add(treeNode);
        }
        return treeNodes;
    }

    private List<OrgNode> searchEleIdPath(OrgTreeDialLogAsynParms parms, String eleIdPath) {
        Set<String> jobIdSet = this.getJobIdSet(parms.getOrgVersionId(), eleIdPath);
        List<OrgNode> treeNodes = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", parms.getOrgVersionId());
        query.put("key_eq", parms.getKeyword());
        query.put("key_right", parms.getKeyword() + "%");
        query.put("key_left", "%" + parms.getKeyword());
        query.put("key_like", "%" + parms.getKeyword() + "%");
        //需要用户数据
        if (parms.getIsNeedUser() == 1 && jobIdSet.size() > 0) {

            StringBuilder hqlSb = new StringBuilder(
                    "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b ");
            hqlSb.append(" where a.id = b.userId and a.isForbidden = 0 and exists(select 1 from MultiOrgUserTreeNode c where a.id = c.userId and c.orgVersionId=:orgVersionId and ");
            HqlUtils.appendSql("c.eleId", query, hqlSb, Sets.<Serializable>newHashSet(jobIdSet));
            hqlSb.append(" )");
            /**
             *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
             *  全模糊查询
             *  按匹配度 =**》**%》%**》%**% 排序
             */
            hqlSb.append("and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like) "
                    + "order by ("
                    + "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq OR a.userNameJp = :key_eq ) THEN 1 "
                    + "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right OR a.userNameJp LIKE :key_right ) THEN 2 "
                    + "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left OR a.userNameJp LIKE :key_left) THEN 3 "
                    + "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like OR a.userNameJp LIKE :key_like) THEN 4 "
                    + "ELSE 5 END),a.userNamePy ");

            List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class,
                    query);
            for (UserNode userNode : userNodes) {
                //转换treeNode
                OrgNode treeNode = ConvertOrgNode.convert(userNode);
                treeNodes.add(treeNode);
            }
        }
        Set<String> eleIdPathSet = this.eleIdPath(eleIdPath);
        for (String path : eleIdPathSet) {
            if (!path.startsWith(parms.getOrgVersionId())) {
                continue;
            }
            String[] pathStrs = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String eleId = pathStrs[pathStrs.length - 1];
            if (eleId.equals(parms.getOrgVersionId())) {
                query.put("eleIdPath", parms.getOrgVersionId() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
            } else {
                query.put("eleIdPath", path + "%");
            }

            /**
             *  子查询MultiOrgTreeNode 指定 orgVersionId ; eleIdPath like 获取下一级
             *  全模糊查询
             *  按匹配度 =**》**%》%**》%**% 排序
             */
            String hql = "from MultiOrgElement a where exists (select 1 from MultiOrgTreeNode b where b.orgVersionId=:orgVersionId and b.eleId=a.id and b.eleIdPath like :eleIdPath) "
                    + "and (a.name like :key_like or a.shortName like :key_like) "
                    + "order by ("
                    + "CASE WHEN (a.name = :key_eq OR a.shortName = :key_eq ) THEN 1 "
                    + "WHEN (a.name LIKE :key_right OR a.shortName LIKE :key_right) THEN 2 "
                    + "WHEN (a.name LIKE :key_left OR a.shortName LIKE :key_left ) THEN 3 "
                    + "WHEN (a.name LIKE :key_like OR a.shortName LIKE :key_like ) THEN 4 " + "ELSE 5 END),a.code ";
            List<MultiOrgElement> elements = multiOrgElementService.listByHQL(hql, query);
            for (MultiOrgElement element : elements) {
                OrgNode treeNode = ConvertOrgNode.convert(element, 0);
                treeNodes.add(treeNode);
            }
        }
        return treeNodes;
    }

    private Set<String> getJobIdSet(String orgVersionId, String eleIdPath) {
        Set<String> jobIdSet = new HashSet<>();
        Set<String> eleIdPathSet = this.eleIdPath(eleIdPath);
        for (String path : eleIdPathSet) {
            if (!path.startsWith(orgVersionId)) {
                continue;
            }
            HashMap<String, Object> queryJob = new HashMap<String, Object>();
            queryJob.put("orgVersionId", orgVersionId);
            queryJob.put("eleId", IdPrefix.JOB.getValue() + "%");
            //查询所有子节点
            String[] pathStrs = path.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String eleId = pathStrs[pathStrs.length - 1];
            if (eleId.equals(orgVersionId)) {
                queryJob.put("eleIdPath", orgVersionId + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
            } else {
                queryJob.put("eleIdPath", path + "%");
            }
            StringBuilder hqlSb = new StringBuilder("from MultiOrgTreeNode a ");
            hqlSb.append(" where a.orgVersionId=:orgVersionId and a.eleId like :eleId and a.eleIdPath like :eleIdPath ");
            List<MultiOrgTreeNode> multiOrgTreeNodeList = multiOrgTreeNodeService.listByHQL(hqlSb.toString(), queryJob);
            for (MultiOrgTreeNode treeNode : multiOrgTreeNodeList) {
                jobIdSet.add(treeNode.getEleId());
            }
        }
        return jobIdSet;
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        UserNodePy userNodePy = new UserNodePy();
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("orgVersionId", parms.getOrgVersionId());
        StringBuilder hqlSb = new StringBuilder(
                "select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        if (StringUtils.isNotBlank(parms.getTreeNodeId()) && !parms.getOrgVersionId().equals(parms.getTreeNodeId())) {
            MultiOrgTreeNode treeNode = multiOrgTreeNodeService.queryByVerIdEleId(parms.getOrgVersionId(), parms.getTreeNodeId());
            query.put("eleIdPath", treeNode.getEleIdPath() + "%");
            hqlSb.append(",MultiOrgTreeNode d where  d.orgVersionId=:orgVersionId and d.eleId = c.eleId and a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and d.eleIdPath like :eleIdPath ");
        } else {
            String eleIdPath = parms.getOtherParams().get("eleIdPath");
            if (StringUtils.isNotBlank(eleIdPath)) {
                Set<String> jobIdSet = this.getJobIdSet(parms.getOrgVersionId(), eleIdPath);
                if (jobIdSet.size() == 0) {
                    return userNodePy;
                }
                hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId and ");
                HqlUtils.appendSql("c.eleId", query, hqlSb, Sets.<Serializable>newHashSet(jobIdSet));
            } else {
                hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and c.orgVersionId=:orgVersionId ");
            }
        }
        if (StringUtils.isNotBlank(parms.getKeyword())) {
            query.put("key_eq", parms.getKeyword());
            query.put("key_right", parms.getKeyword() + "%");
            query.put("key_left", "%" + parms.getKeyword());
            query.put("key_like", "%" + parms.getKeyword() + "%");
            hqlSb.append(" and (a.loginName like :key_like or a.userName like :key_like or a.userNamePy like :key_like or a.userNameJp like :key_like) ");
        }
        if (StringUtils.isNotEmpty(parms.getSort()) && parms.getSort().equals("code")) {
            hqlSb.append(" order by a.code");
        } else {
            hqlSb.append(" order by a.userNamePy");
        }
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
        return userNodePy;
    }

}
