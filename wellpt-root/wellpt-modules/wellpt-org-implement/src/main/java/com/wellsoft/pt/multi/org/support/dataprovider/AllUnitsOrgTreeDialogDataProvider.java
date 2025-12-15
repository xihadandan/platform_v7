package com.wellsoft.pt.multi.org.support.dataprovider;

import cn.hutool.core.collection.CollectionUtil;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.group.entity.MultiGroup;
import com.wellsoft.pt.multi.group.entity.MultiGroupTreeNode;
import com.wellsoft.pt.multi.group.service.MultiGroupService;
import com.wellsoft.pt.multi.group.service.MultiGroupTreeNodeService;
import com.wellsoft.pt.multi.group.vo.MultiGroupVo;
import com.wellsoft.pt.multi.group.vo.TreeNodeVo;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.bean.UserNode;
import com.wellsoft.pt.multi.org.bean.UserNodePy;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.entity.MultiOrgTreeNode;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.service.MultiOrgTreeNodeService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 * Description: 邮件 --> 组织选择项 --> 全部单位
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022/2/28.1	liuyz		2022/2/28		Create
 * </pre>
 * @date 2022/2/28
 */
@Component
public class AllUnitsOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements OrgTreeDialogProvider, OrgTreeAllUserProvider {

    public static final String TYPE = "AllUnits";
    private Logger logger = LoggerFactory.getLogger(AllUnitsOrgTreeDialogDataProvider.class);
    @Autowired
    private MultiGroupService multiGroupService;
    @Autowired
    private MultiGroupTreeNodeService multiGroupTreeNodeService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> treeNodes = new ArrayList<>();
        if (StringUtils.isBlank(parms.getTreeNodeId())) {
            // 如果用户所在的系统单位不是集团单位，就获取用户所在系统单位的默认、启用的组织版本
            // 如果用户所在的系统单位是集团单位，就获取用户所在系统单位和成员单位的默认、启用的组织版本
            List<MultiGroup> multiGroupList = multiGroupService.getEnableList(SpringSecurityUtils.getCurrentUserUnitId());
            for (MultiGroup multiGroup : multiGroupList) {
                MultiGroupTreeNode multiGroupTreeNode = multiGroupTreeNodeService.getById(multiGroup.getId());
                this.addTreeNodes(treeNodes, Arrays.asList(multiGroupTreeNode), parms.getRootId());
            }
            if (CollectionUtil.isEmpty(multiGroupList)) {
                MultiOrgVersion multiOrgVersion = multiOrgVersionService.enabledByDefault(SpringSecurityUtils.getCurrentUserUnitId());
                if (multiOrgVersion != null) {
                    List<MultiOrgElement> elements = multiOrgTreeNodeService.children(multiOrgVersion.getId(), multiOrgVersion.getId());
                    this.addTreeNodes(multiOrgVersion.getId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());
                }
            }
            return treeNodes;
        }
        if (parms.getTreeNodeId().startsWith(IdPrefix.MULTI_GROUP.getValue())
                || parms.getTreeNodeId().startsWith(IdPrefix.MULTI_GROUP_CATEGORY.getValue())) {
            if (parms.getTreeNodeId().startsWith(IdPrefix.MULTI_GROUP.getValue())) {
                MultiGroupTreeNode groupTreeNode = multiGroupTreeNodeService.getById(parms.getTreeNodeId());
                // 可能只创建了集团单位，但是没有往里面添加成员单位，这里做个判断
                if (null != groupTreeNode) {
                    String systemUnitId = groupTreeNode.getEleId();
                    MultiOrgVersion multiOrgVersion = multiOrgVersionService.enabledByDefault(systemUnitId);
                    if (multiOrgVersion != null) {
                        List<MultiOrgElement> elements = multiOrgTreeNodeService.children(multiOrgVersion.getId(), multiOrgVersion.getId());
                        this.addTreeNodes(multiOrgVersion.getId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());
                    }
                }
            }
            List<MultiGroupTreeNode> groupTreeNodeList = multiGroupTreeNodeService.queryParentId(parms.getTreeNodeId(), parms.getRootId());
            this.addTreeNodes(treeNodes, groupTreeNodeList, parms.getRootId());
            return treeNodes;
        }
        if (parms.getTreeNodeId().startsWith(IdPrefix.ORG_VERSION.getValue())) {
            List<MultiOrgElement> elements = multiOrgTreeNodeService.children(parms.getTreeNodeId(), parms.getTreeNodeId());
            this.addTreeNodes(parms.getTreeNodeId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());

            // 节点下，除了组织版本，还有可能挂着其它子单位或者分类
            MultiOrgVersion multiOrgVersion = multiOrgVersionService.getById(parms.getTreeNodeId());
            List<MultiGroupTreeNode> groupTreeNodeList = multiGroupTreeNodeService.queryParentId(multiOrgVersion.getSystemUnitId(), parms.getRootId());
            this.addTreeNodes(treeNodes, groupTreeNodeList, parms.getRootId());
            return treeNodes;
        }
        if (parms.getTreeNodeId().startsWith(IdPrefix.SYSTEM_UNIT.getValue())) {
            // 系统单位ID，则该节点对应的系统单位，没有默认已开启的组织版本，因此不查询系统单位的组织版本树，直接查询该节点在集团单位下是否还有子节点
            List<MultiGroupTreeNode> groupTreeNodeList = multiGroupTreeNodeService.queryParentId(parms.getTreeNodeId(), parms.getRootId());
            this.addTreeNodes(treeNodes, groupTreeNodeList, parms.getRootId());
            return treeNodes;
        }
        Assert.hasText(parms.getOrgVersionId(), "orgVersionId 不能为空");
        //用户节点 直接返回
        if (parms.getTreeNodeId().startsWith(IdPrefix.USER.getValue())) {
            return treeNodes;
        }
        List<MultiOrgElement> elements = multiOrgTreeNodeService.children(parms.getOrgVersionId(), parms.getTreeNodeId());
        this.addTreeNodes(parms.getOrgVersionId(), parms.getIsNeedUser(), treeNodes, elements, parms.getCheckedIds());
        //职位节点 并且 需要用户数据
        if (parms.getTreeNodeId().startsWith(IdPrefix.JOB.getValue()) && parms.getIsNeedUser() == 1) {
            treeNodes = this.getJobUserTreeNode(parms.getOrgVersionId(), parms.getTreeNodeId(), parms.getCheckedIds());
        }
        return treeNodes;
    }

    private void addTreeNodes(List<OrgNode> treeNodes, List<MultiGroupTreeNode> groupTreeNodeList, String groupId) {
        for (MultiGroupTreeNode multiGroupTreeNode : groupTreeNodeList) {
            OrgNode orgNode = new OrgNode();
            orgNode.setName(multiGroupTreeNode.getName());
            orgNode.setId(multiGroupTreeNode.getId());
            if (multiGroupTreeNode.getType() == 2) {
                MultiOrgVersion multiOrgVersion = multiOrgVersionService.enabledByDefault(multiGroupTreeNode.getId());
                if (multiOrgVersion == null) {
                    orgNode.setType(IdPrefix.SYSTEM_UNIT.getValue());
                    orgNode.setIconSkin(IdPrefix.SYSTEM_UNIT.getValue());
                    // 判断这个节点下还有没有挂机其它子单位
                    if (multiGroupTreeNodeService.countByParentId(multiGroupTreeNode.getId(), groupId) > 0) {
                        orgNode.setIsParent(true);
                    } else {
                        orgNode.setIsParent(false);
                    }
//                    continue;
                } else {
                    orgNode.setType(IdPrefix.ORG_VERSION.getValue());
                    orgNode.setIconSkin(IdPrefix.ORG_VERSION.getValue());
                    orgNode.setIsParent(true);
                    orgNode.setId(multiOrgVersion.getId());
                }
            } else {
                // 判断这个节点下还有没有挂机其它子单位
                if (multiGroupTreeNodeService.countByParentId(multiGroupTreeNode.getId(), groupId) > 0) {
                    orgNode.setIsParent(true);
                } else {
                    orgNode.setIsParent(false);
                }
                orgNode.setType(IdPrefix.MULTI_GROUP_CATEGORY.getValue());
            }
            treeNodes.add(orgNode);
        }
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> treeNodes = new ArrayList<>();
        List<MultiGroup> multiGroupList = multiGroupService.getEnableList(SpringSecurityUtils.getCurrentUserUnitId());
        for (MultiGroup multiGroup : multiGroupList) {
            String groupId = multiGroup.getId();
            OrgNode orgNode = new OrgNode();
            orgNode.setName(multiGroupTreeNodeService.getById(multiGroup.getId()).getName());
            orgNode.setId(multiGroup.getId());
            orgNode.setType(IdPrefix.MULTI_GROUP_CATEGORY.getValue());
            MultiGroupVo groupVo = multiGroupService.get(multiGroup.getUuid());
            if (groupVo.getTreeNode() != null) {
                MultiGroupTreeNode groupTreeNode = multiGroupTreeNodeService.getById(groupVo.getTreeNode().getId());
                String systemUnitId = groupTreeNode.getEleId();
                MultiOrgVersion multiOrgVersion = multiOrgVersionService.enabledByDefault(systemUnitId);
                orgNode.setChildren(new ArrayList<>());
                if (multiOrgVersion != null) {
                    orgNode.getChildren().addAll(this.orgVersionList(multiOrgVersion.getId(), parms.getCheckedIds(), parms.getIsNeedUser()));
                }
                if (groupVo.getTreeNode().getChildren().size() > 0) {
                    this.addChildren(orgNode, groupVo.getTreeNode().getChildren(), parms.getCheckedIds(), parms.getIsNeedUser(), groupId);
                }
            }
            treeNodes.add(orgNode);
        }
        return treeNodes;
    }

    private void addChildren(OrgNode orgNode, List<TreeNodeVo> children, List<String> checkedIds, Integer isNeedUser, String groupId) {
        for (TreeNodeVo child : children) {
            OrgNode childOrgNode = new OrgNode();
            childOrgNode.setName(child.getName());
            childOrgNode.setId(child.getId());
            childOrgNode.setType(child.getId().substring(0, 1));
            childOrgNode.setIconSkin(child.getId().substring(0, 1));
            if (child.getType() == 2) {
                MultiOrgVersion multiOrgVersion = multiOrgVersionService.enabledByDefault(child.getId());
                if (multiOrgVersion == null) {
                    orgNode.getChildren().add(childOrgNode);

                    // 系统单位ID，则该节点对应的系统单位，没有默认已开启的组织版本，因此不查询系统单位的组织版本树，直接查询该节点在集团单位下是否还有子节点
                    /*List<MultiGroupTreeNode> groupTreeNodeList = multiGroupTreeNodeService.queryParentId(child.getId(), groupId);
                    childOrgNode.setChildren(new ArrayList<>());
                    this.addTreeNodes(childOrgNode.getChildren(), groupTreeNodeList, groupId);*/

                    if (child.getChildren().size() > 0) {
                        childOrgNode.setChildren(new ArrayList<>());
                        this.addChildren(childOrgNode, child.getChildren(), checkedIds, isNeedUser, groupId);
                    }

                    continue;
                }
                childOrgNode.setId(multiOrgVersion.getId());
                childOrgNode.setChildren(new ArrayList<>());
                childOrgNode.getChildren().addAll(this.orgVersionList(childOrgNode.getId(), checkedIds, isNeedUser));
            } else {
                childOrgNode.setChildren(new ArrayList<>());
            }
            if (child.getChildren().size() > 0) {
                this.addChildren(childOrgNode, child.getChildren(), checkedIds, isNeedUser, groupId);
            }
            orgNode.getChildren().add(childOrgNode);
        }
    }

    private List<OrgNode> orgVersionList(String orgVersionId, List<String> checkedIds, Integer isNeedUser) {
        Map<String, OrgNode> orgNodeMap = new HashMap<>();
        Set<String> jobIdSet = new HashSet<>();
        //构造顶级节点
        OrgNode orgNode = new OrgNode();
        orgNode.setId(orgVersionId);
        orgNode.setTreeMap(new TreeMap<String, List<OrgNode>>());
        orgNodeMap = this.getStringOrgNodeMap(orgNode, checkedIds, jobIdSet, orgVersionId, orgVersionId);
        //需要用户数据
        if (isNeedUser == 1) {
            jobNodeAddUsers(orgVersionId, checkedIds, orgNodeMap, jobIdSet);
        }
        List<OrgNode> orgNodes = new ArrayList<>();
        for (List<OrgNode> nodes : orgNode.getTreeMap().values()) {
            orgNodes.addAll(nodes);
        }
        return orgNodes;
    }


    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> treeNodes = new ArrayList<>();
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("key_eq", parms.getKeyword());
        query.put("key_right", parms.getKeyword() + "%");
        query.put("key_left", "%" + parms.getKeyword());
        query.put("key_like", "%" + parms.getKeyword() + "%");
        Set<String> versionIdSet = this.allVersionId();
        if (versionIdSet.size() > 0) {
            //需要用户数据
            if (parms.getIsNeedUser() == 1) {
                StringBuilder hqlSb = new StringBuilder(
                        "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
                hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and ");
                HqlUtils.appendSql("c.orgVersionId", query, hqlSb, Sets.<Serializable>newHashSet(versionIdSet));
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

            StringBuilder hqlSb = new StringBuilder("from MultiOrgElement a where exists (select 1 from MultiOrgTreeNode b where b.eleId=a.id and ");
            HqlUtils.appendSql("b.orgVersionId", query, hqlSb, Sets.<Serializable>newHashSet(versionIdSet));
            hqlSb.append(" ) and (a.name like :key_like or a.shortName like :key_like) ");
            hqlSb.append("order by (");
            hqlSb.append("CASE WHEN (a.name = :key_eq OR a.shortName = :key_eq ) THEN 1 ");
            hqlSb.append("WHEN (a.name LIKE :key_right OR a.shortName LIKE :key_right) THEN 2 ");
            hqlSb.append("WHEN (a.name LIKE :key_left OR a.shortName LIKE :key_left ) THEN 3 ");
            hqlSb.append("WHEN (a.name LIKE :key_like OR a.shortName LIKE :key_like ) THEN 4 ELSE 5 END),a.code ");
            List<MultiOrgElement> elements = multiOrgElementService.listByHQL(hqlSb.toString(), query);
            for (MultiOrgElement element : elements) {
                OrgNode treeNode = ConvertOrgNode.convert(element, 0);
                treeNodes.add(treeNode);
            }
        }
        StringBuilder hqlSb = new StringBuilder("from MultiGroupTreeNode a where exists (select 1 from MultiGroup b where b.uuid=a.groupUuid and b.isEnable = 1) ");
        hqlSb.append("and (a.name like :key_like or a.shortName like :key_like) ");
        hqlSb.append("order by (");
        hqlSb.append("CASE WHEN (a.name = :key_eq OR a.shortName = :key_eq ) THEN 1 ");
        hqlSb.append("WHEN (a.name LIKE :key_right OR a.shortName LIKE :key_right) THEN 2 ");
        hqlSb.append("WHEN (a.name LIKE :key_left OR a.shortName LIKE :key_left ) THEN 3 ");
        hqlSb.append("WHEN (a.name LIKE :key_like OR a.shortName LIKE :key_like ) THEN 4 ELSE 5 END),a.seq ");
        List<MultiGroupTreeNode> groupTreeNodeList = multiGroupTreeNodeService.listByHQL(hqlSb.toString(), query);
        this.addTreeNodes(treeNodes, groupTreeNodeList, parms.getRootId());

        hqlSb = new StringBuilder("from MultiGroup a where a.isEnable = 1 ");
        hqlSb.append("and a.name like :key_like ");
        hqlSb.append("order by a.code ");
        List<MultiGroup> groupList = multiGroupService.listByHQL(hqlSb.toString(), query);
        for (MultiGroup multiGroup : groupList) {
            OrgNode orgNode = new OrgNode();
            orgNode.setName(multiGroup.getName());
            orgNode.setId(multiGroup.getId());
            orgNode.setType(IdPrefix.MULTI_GROUP.getValue());
            orgNode.setIconSkin(IdPrefix.ORG_VERSION.getValue());
            treeNodes.add(orgNode);
        }
        return treeNodes;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    private Set<String> allVersionId() {
        Set<String> versionIdSet = new HashSet<>();
        List<MultiGroup> multiGroupList = multiGroupService.getEnableList();
        for (MultiGroup multiGroup : multiGroupList) {
            Set<String> tmp = multiGroupTreeNodeService.getOrgVersionIdById(multiGroup.getId());
            if (CollectionUtil.isNotEmpty(tmp)) {
                versionIdSet.addAll(tmp);
            }
        }
        return versionIdSet;
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        UserNodePy userNodePy = new UserNodePy();
        Set<String> versionIdSet = this.allVersionId();
        if (versionIdSet.size() == 0) {
            return userNodePy;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        StringBuilder hqlSb = new StringBuilder(
                "select distinct a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin,a.code as code from MultiOrgUserAccount a,MultiOrgUserInfo b,MultiOrgUserTreeNode c");
        hqlSb.append(" where a.id = b.userId and a.id = c.userId and a.isForbidden = 0 and ");
        HqlUtils.appendSql("c.orgVersionId", query, hqlSb, Sets.<Serializable>newHashSet(versionIdSet));
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

    private Set<String> eleIdPath(String eleIdPath) {
        String[] paths = eleIdPath.split(";");
        Set<String> set = new LinkedHashSet<>();
        for (String path : paths) {
            //替换成最小长度eleIdPath
            ConvertOrgNode.containsMinIndexOf(set, path);
        }
        return set;
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

}
