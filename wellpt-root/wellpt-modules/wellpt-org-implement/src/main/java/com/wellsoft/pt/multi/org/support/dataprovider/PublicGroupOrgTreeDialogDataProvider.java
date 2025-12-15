/*
 * @(#)2017年12月18日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.support.dataprovider;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.entity.MultiOrgGroupUserRangeEntity;
import com.wellsoft.pt.multi.org.entity.MultiOrgVersion;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgVersionFacade;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.service.MultiOrgGroupUserRangeService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.org.facade.service.impl.OrgApiFacadeImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import joptsimple.internal.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
public class PublicGroupOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl
        implements OrgTreeDialogProvider, OrgTreeAllUserProvider {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 2878259433645500125L;
    private static Logger logger = LoggerFactory.getLogger(PublicGroupOrgTreeDialogDataProvider.class);
    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;

    @Autowired
    private MultiOrgService multiOrgService;

    @Autowired
    private MultiOrgVersionFacade multiOrgVersionFacade;

    @Autowired
    private MultiOrgGroupUserRangeService multiOrgGroupUserRangeService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private MyUnitOrgTreeDialogDataProvider myUnitOrgTreeDialogDataProvider;

    @Override
    public String getType() {
        return "PublicGroup";
    }

    @Override
    public List<TreeNode> provideData(OrgTreeDialogParams p) {
        List<TreeNode> groupList = new ArrayList<TreeNode>();
        // 如果没有指定用户，则用当前登录用户ID
        String userId = p.getUserId();
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }
        // 是否需要包含用户数据，默认不需要
        int isNeedUser = p.getIsNeedUser(); // 是否返回用户数据
        boolean isInMyUnit = p.getIsInMyUnit(); // 是否包含子单位，默认不包含

        groupList = this.multiOrgGroupFacade.queryGroupListAsTreeByType(MultiOrgGroup.TYPE_PUBLIC_GROUP);
        if (CollectionUtils.isNotEmpty(groupList)) {
            // 因为实施在使用过程中，大量使用群组，所以不能一个一个查，效率太低，所以是一次性把当前用户的完整组织树弄出来
            // 然后转成map，最后直接从map中获取，以提高消息
            Map<String, TreeNode> allMap = getOrgTreeNodeMap();
            Stopwatch w = Stopwatch.createStarted();
            // 所有的成员，需要按树方式，递归展示对应的子节点
            for (TreeNode group : groupList) {
                List<TreeNode> memberList = group.getChildren();
                if (CollectionUtils.isNotEmpty(memberList))
                    for (TreeNode member : memberList) {
                        // 成员是个组织节点
                        if (OrgApiFacadeImpl.isMultiOrgEleNode(member.getId())) {
                            TreeNode node = allMap.get(member.getId());
                            if (node != null) {
                                // 替换成完整节点
                                member.setPath(node.getPath());
                                member.setChildren(node.getChildren());
                            }
                        } else if (member.getId().startsWith(IdPrefix.GROUP.getValue())) {
                            // 群组不允许嵌套了， 所有如果有嵌套，也不处理了
                        }
                    }
            }
            w.stop();
            logger.info("总耗时=" + w.toString());

        }
        return groupList;
    }

    private Map<String, TreeNode> getOrgTreeNodeMap() {
        Map<String, TreeNode> allMap = Maps.newHashMap();
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<MultiOrgVersion> vers = this.multiOrgVersionFacade.queryCurrentActiveVersionListOfSystemUnit(unitId);
        if (vers != null) {
            for (MultiOrgVersion v : vers) {
                OrgTreeNode treeNode = this.getTreeNodeByVerisonAndEleIdPath(v.getId(), v.getId(), 1, true);
                treeNode2map(treeNode, allMap);
            }
        }
        return allMap;
    }

    // 将treeNode，全部放入map中，方便搜索
    private void treeNode2map(TreeNode treeNode, Map<String, TreeNode> map) {
        if (treeNode != null) {
            map.put(treeNode.getId(), treeNode);
        }
        if (CollectionUtils.isNotEmpty(treeNode.getChildren())) {
            for (TreeNode child : treeNode.getChildren()) {
                this.treeNode2map(child, map);
            }
        }
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        List<UserNode> userNodes = new ArrayList<>();
        // 公共群组中，成员是可以随便选的，组织版本不是只有一个，这边如果限制了组织版本，那么查询到的群组成员的结构是不准确的，会比实际情况少
        parms.setOrgVersionId(Strings.EMPTY);
        this.addJobUserNodesForGroup(parms.getUnitId(), parms.getTreeNodeId(), userNodes);
        return ConvertOrgNode.convertUserNodePy(userNodes);
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> resultList = this.getGroupNodeList(parms.getOtherParams().get("groupId"), parms.getNameDisplayMethod(), parms.getUserId(),
                parms.getUnitId(), parms.getTreeNodeId(), MultiOrgGroup.TYPE_PUBLIC_GROUP, parms.getCheckedIds(),
                parms.getIsNeedUser());
        resultList = filterPublicGroupByUserRange(parms, resultList);

        return resultList;
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> resultList = this.getFullGroupNodeList(parms.getOtherParams().get("groupId"), parms.getNameDisplayMethod(), parms.getUserId(),
                parms.getUnitId(), MultiOrgGroup.TYPE_PUBLIC_GROUP, parms.getCheckedIds(), parms.getIsNeedUser());
        resultList = filterPublicGroupByUserRange(parms, resultList);
        return resultList;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        List<OrgNode> resultList = this.full(parms);
        List<OrgNode> searchList = new ArrayList<>();
        this.addSearchList(searchList, resultList, parms.getKeyword());
        // 去重
        Map<String, OrgNode> orgNodeMap = Maps.newHashMap();
        List<OrgNode> newSearchList = new ArrayList<>();
        for (OrgNode orgNode : searchList) {
            if (orgNodeMap.get(orgNode.getId()) == null) {
                newSearchList.add(orgNode);
                orgNodeMap.put(orgNode.getId(), orgNode);
            }
        }
        resultList = filterPublicGroupByUserRange(parms, newSearchList);
        return resultList;
    }

    public void addSearchList(List<OrgNode> searchList, List<OrgNode> resultList, String keyword) {
        for (OrgNode orgNode : resultList) {
            if (orgNode.getName().indexOf(keyword) > -1) {
                searchList.add(orgNode);
            }
            if (orgNode.getChildren() != null) {
                addSearchList(searchList, orgNode.getChildren(), keyword);
            }
        }
    }

    /**
     * 当前用户的使用者信息过滤公共群组
     *
     * @param
     * @return
     **/
    private List<OrgNode> filterPublicGroupByUserRange(OrgTreeDialLogAsynParms parms, List<OrgNode> resultList) {

        if (parms.getOtherParams() != null && parms.getOtherParams().size() > 0) {
            // 从入口群组的使用者进来时，不做过滤
            String fromUserRange = "1";
            if (fromUserRange.equals(parms.getOtherParams().get("fromUserRange"))) {
                return resultList;
            }
        }
        List<String> groupIds = Lists.newArrayList();
        if (!org.springframework.util.CollectionUtils.isEmpty(resultList)) {
            for (OrgNode orgNode : resultList) {
                groupIds.add(orgNode.getId());
            }
        } else {
            return resultList;
        }
        List<OrgNode> orgNodeList = Lists.newArrayList();
        List<MultiOrgGroupUserRangeEntity> groupUserRangeEntities = multiOrgGroupUserRangeService
                .getUserRangeListBygroupIds(groupIds);
        // key:groupId
        Map<String, List<MultiOrgGroupUserRangeEntity>> userRangeMaps = Maps.newHashMap();
        if (org.springframework.util.CollectionUtils.isEmpty(groupUserRangeEntities)) {
            // 0、如果群组的使用者信息数据全都为空，则当前用户可见此群组；
            return resultList;
        } else {
            for (MultiOrgGroupUserRangeEntity groupUserRangeEntity : groupUserRangeEntities) {
                List<MultiOrgGroupUserRangeEntity> userRangeEntityList = userRangeMaps
                        .get(groupUserRangeEntity.getGroupId());
                if (userRangeEntityList == null) {
                    userRangeEntityList = Lists.newArrayList();
                }
                userRangeEntityList.add(groupUserRangeEntity);
                userRangeMaps.put(groupUserRangeEntity.getGroupId(), userRangeEntityList);
            }

            // 判断群组的使用者信息数据是否为空
            for (OrgNode orgNode : resultList) {
                List<MultiOrgGroupUserRangeEntity> userRangeEntityList = userRangeMaps.get(orgNode.getId());
                if (userRangeEntityList == null || userRangeEntityList.size() == 0) {
                    // 1、如果群组的使用者信息数据为空，则当前用户可见此群组；
                    orgNodeList.add(orgNode);
                } else {
                    // 使用者信息不为空
                    // 判断群组的使用者信息是否包含当前用户
                    String userRangesStr = MultiOrgGroupUserRangeEntity.userRangeListToIds(userRangeEntityList);
                    boolean isExist = orgApiFacade.isMemberOf(SpringSecurityUtils.getCurrentUserId(), userRangesStr);
                    if (isExist) {
                        // 2、如果群组的使用者信息不为空，且群组的使用者信息包含当前用户，则当前用户可见此群组；
                        orgNodeList.add(orgNode);
                    }
                }
            }
        }
        return orgNodeList;
    }
}
