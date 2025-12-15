/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import joptsimple.internal.Strings;
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
public class DutyGroupOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements
        OrgTreeDialogProvider, OrgTreeAllUserProvider {
    public static final String TYPE = "DutyGroup";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 58608016747280708L;
    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionService;

    @Override
    public String getType() {
        return TYPE;
    }

    // 计算职务群组列表，
    private Map<String, List<OrgJobDutyDto>> computeDutyGroupNodeList(String orgVersionId, String unitId, int isNeedUser) {
        List<MultiOrgVersion> versionList = new ArrayList<MultiOrgVersion>();
        // 如果有指定版本，则采用指定版本的职位列表，没有指定版本，则采用当前单位的最新版本
        if (StringUtils.isBlank(orgVersionId)) {
            // 如果有指定单位，则返回指定单位的，如果没有则返回用户的当前单位
            if (StringUtils.isBlank(unitId)) {
                unitId = SpringSecurityUtils.getCurrentUserUnitId();
            }
            versionList = this.multiOrgVersionService.queryCurrentActiveVersionListOfSystemUnit(unitId);
        } else {
            MultiOrgVersion ver = this.multiOrgVersionService.getOrgVersionById(orgVersionId);
            versionList.add(ver);
        }
        // 获取所有带职务的职位信息
        List<OrgJobDutyDto> allDutyList = new ArrayList<OrgJobDutyDto>();
        for (MultiOrgVersion multiOrgVersion : versionList) {
            List<OrgJobDutyDto> jobDutyList = this.multiOrgService.queryJobListWithDutyByVersionId(multiOrgVersion
                    .getId());
            if (!CollectionUtils.isEmpty(jobDutyList)) {
                allDutyList.addAll(jobDutyList);
            }
        }
        // 将所有的职位按职务进行分组
        Map<String, List<OrgJobDutyDto>> dutyMap = new List2GroupMap<OrgJobDutyDto>() {
            @Override
            protected String getGroupUuid(OrgJobDutyDto obj) {
                return obj.getDutyId();
            }
        }.convert(allDutyList);

        return dutyMap;
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

        Map<String, List<OrgJobDutyDto>> dutyMap = this.computeDutyGroupNodeList(orgVersionId, p.getUnitId(),
                isNeedUser);
        for (String dutyId : dutyMap.keySet()) {
            MultiOrgDuty duty = this.multiOrgService.getDutyById(dutyId);
            List<OrgJobDutyDto> jobList = dutyMap.get(dutyId);
            OrgTreeNode dutyNode = duty.convert2TreeNode();
            for (OrgJobDutyDto jobDto : jobList) {
                Map<String, MultiOrgElement> map = this.multiOrgService.queryElementMapByEleIdPath(jobDto
                        .getEleIdPath());
                jobDto.computeEleNamePath(map);
                OrgTreeNode jobNode = jobDto.convert2TreeNode();

                if (isNeedUser == 1) {
                    this.addUserToJobNode(jobNode);
                }
                dutyNode.getChildren().add(jobNode);
                // 职务是职位的一个属性，所以它的版本号跟职位的版本号是同一个
                dutyNode.setOrgVersionId(jobNode.getOrgVersionId());
            }
            treeNodeList.add(dutyNode);
        }
        return treeNodeList;
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        // 职务群组中，一个职务可以供所有的组织版本的成员使用，因此职务群组的成员，组织版本不止一个，将组织版本作为查询条件，查询到的结果不准确
        parms.setOrgVersionId(Strings.EMPTY);
        List<UserNode> userNodes = new ArrayList<>();
        this.addJobUserNodesForDuty(parms.getUnitId(), parms.getTreeNodeId(), userNodes);
        return ConvertOrgNode.convertUserNodePy(userNodes);
    }

    private Set<String> dutyIdSet(String dutyId) {
        Set<String> set = new LinkedHashSet<>();
        if (StringUtils.isBlank(dutyId)) {
            return set;
        }
        String[] eleIds = dutyId.split(";");
        for (String eleId : eleIds) {
            if (eleId.startsWith(IdPrefix.DUTY.getValue())) {
                set.add(eleId);
            }
        }
        return set;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<>();
        StringBuilder hqlSb = new StringBuilder();
        Set<String> dutyIdSet = this.dutyIdSet(parms.getOtherParams().get("dutyId"));
        if (StringUtils.isEmpty(parms.getTreeNodeId())) {
            //查根节点
            hqlSb.append("select a.id as id,a.name as name,'G' as type,(select count(b.uuid) from MultiOrgJobDuty b where b.dutyId = a.id) as childrenCount from MultiOrgDuty a where 1=1 ");
            if (!StringUtils.isEmpty(parms.getUserId())) {
                query.put("creator", parms.getUserId());
                hqlSb.append(" and a.creator = :creator ");
            }
            query.put(
                    "systemUnitId",
                    StringUtils.isEmpty(parms.getUnitId()) ? SpringSecurityUtils.getCurrentUserUnitId() : parms
                            .getUnitId());
            hqlSb.append(" and a.systemUnitId = :systemUnitId ");
            if (dutyIdSet.size() > 0) {
                query.put("dutyIds", dutyIdSet);
                hqlSb.append(" and a.id in (:dutyIds)");
            }
        } else {
            //查子节点
            query.put("dutyId", parms.getTreeNodeId());
            hqlSb.append("select a.jobId as id,b.name as name,'J' as type,'0' as childrenCount from MultiOrgJobDuty a,MultiOrgElement b where a.jobId = b.id and a.dutyId = :dutyId ");
        }
        List<OrgNodeQueryItemDto> orgNodeQueryItemDtoList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                OrgNodeQueryItemDto.class, query);
        Set<String> halfCheckSet = this.halfCheckIdsForDuty(parms.getCheckedIds());
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : orgNodeQueryItemDtoList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            ConvertOrgNode.checked(orgNode, parms.getCheckedIds(), halfCheckSet);
            if (1 == parms.getIsNeedUser()) {
                addUserChildrenForDuty(orgNode);
            }
            resultList.add(orgNode);
        }
        if (resultList.size() > 0) {
            this.addNamePath(parms.getNameDisplayMethod(), resultList);
        }
        return resultList;
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<>();
        StringBuilder hqlSbParents = new StringBuilder();
        query.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        //查根节点
        hqlSbParents.append("select a.id as id,a.name as name,'G' as type from MultiOrgDuty a where 1=1 ");
        if (!StringUtils.isEmpty(parms.getUserId())) {
            query.put("creator", parms.getUserId());
            hqlSbParents.append(" and a.creator = :creator ");
        }
        query.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        hqlSbParents.append(" and a.systemUnitId = :systemUnitId ");
        Set<String> dutyIdSet = this.dutyIdSet(parms.getOtherParams().get("dutyId"));
        if (dutyIdSet.size() > 0) {
            query.put("dutyIds", dutyIdSet);
            hqlSbParents.append(" and a.id in (:dutyIds)");
        }
        List<OrgNodeQueryItemDto> parentsList = multiOrgTreeNodeService.listItemHqlQuery(hqlSbParents.toString(),
                OrgNodeQueryItemDto.class, query);
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : parentsList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            ConvertOrgNode.checked(orgNode, parms.getCheckedIds(), null);
            resultList.add(orgNode);
        }
        //查子节点
        StringBuilder hqlSbChildren = new StringBuilder();
        hqlSbChildren
                .append("select a.jobId as id,b.name as name,'J' as type,'0' as childrenCount,a.dutyId as parentsId from MultiOrgElement b,MultiOrgJobDuty a ");
        hqlSbChildren.append(" where a.dutyId is not null and a.jobId = b.id ");
        hqlSbChildren.append(" and a.dutyId in (select id from MultiOrgDuty c where c.systemUnitId = :systemUnitId) ");
        if (dutyIdSet.size() > 0) {
            query.put("dutyIds", dutyIdSet);
            hqlSbChildren.append(" and a.dutyId in (:dutyIds)");
        }
        List<OrgNodeQueryItemDto> childrenList = multiOrgTreeNodeService.listItemHqlQuery(hqlSbChildren.toString(),
                OrgNodeQueryItemDto.class, query);
        Map<String, List<OrgNode>> childrenMap = new HashMap<>();
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : childrenList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            ConvertOrgNode.checked(orgNode, parms.getCheckedIds(), null);
            if (1 == parms.getIsNeedUser()) {
                addUserChildrenForDuty(orgNode);
            }
            //把子节点转成Map<dutyId,List>形式
            if (childrenMap.containsKey(orgNodeQueryItemDto.getParentsId())) {
                childrenMap.get(orgNodeQueryItemDto.getParentsId()).add(orgNode);
            } else {
                List<OrgNode> list = new ArrayList<>();
                list.add(orgNode);
                childrenMap.put(orgNodeQueryItemDto.getParentsId(), list);
            }
        }
        //组装跟节点和子节点
        for (OrgNode orgNode : resultList) {
            List<OrgNode> orgNodeList = childrenMap.get(orgNode.getId());
            this.addNamePath(parms.getNameDisplayMethod(), orgNodeList);
            orgNode.setChildren(orgNodeList);
        }

        return resultList;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
//		Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        List<OrgNode> resultList = new ArrayList<>();
        //需要用户数据
        if (parms.getIsNeedUser() == 1) {
            resultList.addAll(this.searchUserInDuty(null, parms.getUserId(), parms.getKeyword()));
        }
        resultList.addAll(this.searchDutyAndJob(null, parms.getUserId(), parms.getKeyword()));
        return resultList;
    }

    /**
     * @param keyword
     * @return
     */
    private List<OrgNode> searchUserInDuty(String unitId, String userId, String keyword) {
        //查职务下的用户
        LinkedHashSet<OrgNode> resultList = new LinkedHashSet<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("key_eq", keyword);
        query.put("key_right", keyword + "%");
        query.put("key_left", "%" + keyword);
        query.put("key_like", "%" + keyword + "%");
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgDuty d,MultiOrgJobDuty e ");
        hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 ");
        hqlSb.append(" and c.eleId = e.jobId and e.dutyId = d.id ");
        /**
         *  子查询MultiOrgTreeNode 指定 orgVersionId 获取 未禁用用户isForbidden=0
         *  全模糊查询
         *  按匹配度 =**》**%》%**》%**% 排序
         */
        hqlSb.append("and (a.loginName like :key_like or a.loginNameLowerCase like :key_like or a.userName like :key_like or a.userNamePy like :key_like) "
                + "order by ("
                + "CASE WHEN (a.loginName = :key_eq OR a.loginNameLowerCase = :key_eq OR a.userName = :key_eq OR a.userNamePy = :key_eq ) THEN 1 "
                + "WHEN (a.loginName LIKE :key_right OR a.loginNameLowerCase LIKE :key_right OR a.userName LIKE :key_right OR a.userNamePy LIKE :key_right) THEN 2 "
                + "WHEN (a.loginName LIKE :key_left OR a.loginNameLowerCase LIKE :key_left OR a.userName LIKE :key_left OR a.userNamePy LIKE :key_left) THEN 3 "
                + "WHEN (a.loginName LIKE :key_like OR a.loginNameLowerCase LIKE :key_like OR a.userName LIKE :key_like OR a.userNamePy LIKE :key_like ) THEN 4 "
                + "ELSE 5 END),a.userNamePy ");

        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        for (UserNode userNode : userNodes) {
            //转换treeNode
            OrgNode treeNode = ConvertOrgNode.convert(userNode);
            resultList.add(treeNode);
        }
        return new ArrayList<>(resultList);
    }

    private List<OrgNode> searchDutyAndJob(String unitId, String userId, String keyword) {
        List<OrgNode> resultList = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("key_like", "%" + keyword + "%");
        //查职务和职位
        StringBuilder hqlSbParents = new StringBuilder();
        query.put("systemUnitId", StringUtils.isEmpty(unitId) ? SpringSecurityUtils.getCurrentUserUnitId() : unitId);
        //查根节点
        hqlSbParents.append("select a.id as id,a.name as name,'G' as type from MultiOrgDuty a where 1=1 ");
        if (!StringUtils.isEmpty(userId)) {
            query.put("creator", userId);
            hqlSbParents.append(" and a.creator = :creator ");
        }
        hqlSbParents.append(" and a.systemUnitId = :systemUnitId ");
        hqlSbParents.append(" and a.name like :key_like ");
        List<OrgNodeQueryItemDto> parentsList = multiOrgTreeNodeService.listItemHqlQuery(hqlSbParents.toString(),
                OrgNodeQueryItemDto.class, query);
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : parentsList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            resultList.add(orgNode);
        }
        //查子节点
        StringBuilder hqlSbChildren = new StringBuilder();
        hqlSbChildren
                .append("select a.jobId as id,b.name as name,'J' as type,'0' as childrenCount,a.dutyId as parentsId from MultiOrgElement b,MultiOrgJobDuty a ");
        hqlSbChildren.append(" where a.dutyId is not null and a.jobId = b.id ");
        hqlSbChildren.append(" and a.dutyId in (select id from MultiOrgDuty c where c.systemUnitId = :systemUnitId) ");
        hqlSbChildren.append(" and b.name like :key_like ");
        List<OrgNodeQueryItemDto> childrenList = multiOrgTreeNodeService.listItemHqlQuery(hqlSbChildren.toString(),
                OrgNodeQueryItemDto.class, query);
        for (OrgNodeQueryItemDto orgNodeQueryItemDto : childrenList) {
            OrgNode orgNode = ConvertOrgNode.convert(orgNodeQueryItemDto);
            resultList.add(orgNode);
        }
        return resultList;
    }

    private Set<String> halfCheckIdsForDuty(List<String> checkedIds) {
        if (checkedIds == null || checkedIds.size() == 0) {
            return null;
        }
        Set<String> halfCheckSet = new HashSet<>();
        for (String checkedId : checkedIds) {
            HashMap<String, Object> query = new HashMap<String, Object>();
            query.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
            StringBuilder hqlSb = new StringBuilder();
            List<OrgNodeQueryItemDto> multiOrgJobDutyList = new ArrayList<>();
            if (checkedId.startsWith(IdPrefix.USER.getValue())) {
                query.put("userId", checkedId);
                hqlSb.append("select b.jobId as id,b.dutyId as parentsId from MultiOrgUserTreeNode a,MultiOrgJobDuty b,MultiOrgDuty c ");
                hqlSb.append(" where a.eleId = b.jobId and a.userId=:userId and c.systemUnitId = :systemUnitId ");
                multiOrgJobDutyList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                        OrgNodeQueryItemDto.class, query);
            } else if (checkedId.startsWith(IdPrefix.JOB.getValue())) {
                query.put("jobId", checkedId);
                hqlSb.append("select a.jobId as id,a.dutyId as parentsId from MultiOrgJobDuty a where a.jobId = :jobId ");
                multiOrgJobDutyList = multiOrgTreeNodeService.listItemHqlQuery(hqlSb.toString(),
                        OrgNodeQueryItemDto.class, query);
            } else {
                halfCheckSet.add(checkedId);
            }
            if (!CollectionUtils.isEmpty(multiOrgJobDutyList)) {
                for (OrgNodeQueryItemDto multiOrgJobDuty : multiOrgJobDutyList) {
                    halfCheckSet.add(multiOrgJobDuty.getId());
                    halfCheckSet.add(multiOrgJobDuty.getParentsId());
                }
            }
        }
        return halfCheckSet;
    }

    /**
     * 职务群组的职位节点获取用户数据
     *
     * @param orgNode
     */
    private void addUserChildrenForDuty(OrgNode orgNode) {
        if (!orgNode.getType().startsWith(IdPrefix.JOB.getValue())) {
            return;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("eleId", orgNode.getId());
        query.put("isForbidden", 0);
        StringBuilder hqlSb = new StringBuilder(
                "select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c,MultiOrgJobDuty d ");
        hqlSb.append(" where a.id = b.userId and a.id = c.userId and d.jobId = c.eleId and a.isForbidden = 0 and c.eleId = :eleId  order by a.code");
        List<UserNode> userNodes = multiOrgUserAccountService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        if (!CollectionUtils.isEmpty(userNodes)) {
            List<OrgNode> childrenList = CollectionUtils.isEmpty(orgNode.getChildren()) ? new ArrayList<OrgNode>()
                    : orgNode.getChildren();
            for (UserNode userNode : userNodes) {
                childrenList.add(ConvertOrgNode.convert(userNode));
            }
            orgNode.setChildren(childrenList);
        }
    }
}
