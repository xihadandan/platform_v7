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
import com.wellsoft.pt.multi.org.entity.MultiOrgGroup;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgGroupFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.org.facade.service.impl.OrgApiFacadeImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

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
public class MyGroupOrgTreeDialogDataProvider extends AbstractOrgTreeDialogServiceImpl implements
        OrgTreeDialogProvider, OrgTreeAllUserProvider {
    public static final String TYPE = "MyGroup";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8628237586839490984L;
    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;
    @Autowired
    private MultiOrgService multiOrgService;

    @Override
    public String getType() {
        return TYPE;
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

        groupList = this.multiOrgGroupFacade.queryGroupListAsTreeByType(MultiOrgGroup.TYPE_MY_GROUP);
        if (CollectionUtils.isNotEmpty(groupList)) {
            // 所有的成员，需要按树方式，递归展示对应的子节点
            for (TreeNode group : groupList) {
                List<TreeNode> memberList = group.getChildren();
                if (CollectionUtils.isNotEmpty(memberList))
                    for (TreeNode member : memberList) {
                        // 成员是个组织节点
                        if (OrgApiFacadeImpl.isMultiOrgEleNode(member.getId())) {
                            OrgTreeNodeDto node = multiOrgService.getNodeOfCurrentVerisonByEleId(member.getId());
                            if (node != null) {
                                OrgTreeNode subNode = this.getTreeNodeByVerisonAndEleIdPath(node.getEleIdPath(),
                                        node.getOrgVersionId(), isNeedUser, isInMyUnit);
                                if (subNode.getChildren() != null) {
                                    member.getChildren().addAll(subNode.getChildren());
                                }
                            }
                        } else if (member.getId().startsWith(IdPrefix.GROUP.getValue())) {
                            // 群组不允许嵌套了， 所有如果有嵌套，也不处理了
                        }
                    }
            }
        }
        return groupList;
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        List<UserNode> userNodes = new ArrayList<>();
        this.addJobUserNodesForGroup(parms.getUnitId(), parms.getTreeNodeId(), userNodes);
        return ConvertOrgNode.convertUserNodePy(userNodes);
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> resultList = this.getGroupNodeList(parms.getOtherParams().get("groupId"), parms.getNameDisplayMethod(), parms.getUserId(), parms.getUnitId(), parms.getTreeNodeId(), MultiOrgGroup.TYPE_MY_GROUP, parms.getCheckedIds(), parms.getIsNeedUser());
        return resultList;
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        List<OrgNode> resultList = this.getFullGroupNodeList(parms.getOtherParams().get("groupId"), parms.getNameDisplayMethod(), parms.getUserId(), parms.getUnitId(), MultiOrgGroup.TYPE_MY_GROUP, parms.getCheckedIds(), parms.getIsNeedUser());
        return resultList;
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        List<OrgNode> resultList = this.full(parms);
        List<OrgNode> searchList = new ArrayList<>();
        this.addSearchList(searchList, resultList, parms.getKeyword());
        return searchList;
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
}
