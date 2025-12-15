package com.wellsoft.pt.multi.org.support.dataprovider;

import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.multi.org.bean.OrgTreeDialLogAsynParms;
import com.wellsoft.pt.multi.org.bean.UserNode;
import com.wellsoft.pt.multi.org.bean.UserNodePy;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.util.*;

/**
 * 角色组织弹出框接口
 *
 * @author yt
 * @title: RoleOrgTreeDialogDataProvider
 * @date 2020/7/16 10:41
 */
@Component
public class RoleOrgTreeDialogDataProvider implements OrgTreeDialogProvider, OrgTreeAllUserProvider {

    public static final String TYPE = "Role";

    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private MultiOrgUserRoleService multiOrgUserRoleService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private MultiOrgElementRoleService multiOrgElementRoleService;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgGroupRoleService multiOrgGroupRoleService;
    @Autowired
    private MultiOrgGroupMemberService multiOrgGroupMemberService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public List<OrgNode> children(OrgTreeDialLogAsynParms parms) {
        return this.orgNodeList(parms.getUserId(), parms.getUnitId(), null, parms.getCheckedIds());
    }

    @Override
    public List<OrgNode> full(OrgTreeDialLogAsynParms parms) {
        return this.orgNodeList(parms.getUserId(), parms.getUnitId(), null, parms.getCheckedIds());
    }

    @Override
    public List<OrgNode> search(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getKeyword(), "keyword 不能为空");
        return this.orgNodeList(parms.getUserId(), parms.getUnitId(), parms.getKeyword(), parms.getCheckedIds());
    }

    @Override
    public UserNodePy allUserSearch(OrgTreeDialLogAsynParms parms) {
        Assert.hasText(parms.getTreeNodeId(), "treeNodeId 不能为空");
        String roleUuid = parms.getTreeNodeId().replace(IdPrefix.ROLE.getValue(), "");
        List<MultiOrgUserRole> userRoleList = multiOrgUserRoleService.queryUserListByRole(roleUuid);
        Set<String> userIdSet = new HashSet<>();
        for (MultiOrgUserRole userRole : userRoleList) {
            userIdSet.add(userRole.getUserId());
        }

        List<MultiOrgElementRole> elementRoleList = multiOrgElementRoleService.queryElementByRole(roleUuid);
        Set<String> jobIdSet = new HashSet<>();
        Set<String> eleIdSet = new HashSet<>();
        for (MultiOrgElementRole orgElementRole : elementRoleList) {
            if (orgElementRole.getEleId().startsWith(IdPrefix.JOB.getValue())) {
                jobIdSet.add(orgElementRole.getEleId());
            } else {
                eleIdSet.add(orgElementRole.getEleId());
            }
        }
        List<MultiOrgGroupRole> groupRoleList = multiOrgGroupRoleService.queryGroupListByRole(roleUuid);
        for (MultiOrgGroupRole multiOrgGroupRole : groupRoleList) {
            List<MultiOrgGroupMember> groupMemberList = multiOrgGroupMemberService.queryMemberListOfGroup(multiOrgGroupRole.getGroupId(), false);
            for (MultiOrgGroupMember multiOrgGroupMember : groupMemberList) {
                if (multiOrgGroupMember.getMemberObjType().equals(IdPrefix.JOB.getValue())) {
                    jobIdSet.add(multiOrgGroupMember.getMemberObjId());
                } else if (multiOrgGroupMember.getMemberObjType().equals(IdPrefix.USER.getValue())) {
                    userIdSet.add(multiOrgGroupMember.getMemberObjId());
                } else {
                    eleIdSet.add(multiOrgGroupMember.getMemberObjId());
                }
            }
        }
        if (eleIdSet.size() > 0) {
            HashMap<String, Object> query = new HashMap<String, Object>();
            StringBuilder hql = new StringBuilder("select a from MultiOrgTreeNode a,MultiOrgVersion b where a.orgVersionId = b.id and b.status = 1 and ");
            HqlUtils.appendSql("a.eleId", query, hql, Sets.<Serializable>newHashSet(eleIdSet));
            List<MultiOrgTreeNode> multiOrgTreeNodeList = multiOrgTreeNodeService.listByHQL(hql.toString(), query);
            for (MultiOrgTreeNode treeNode : multiOrgTreeNodeList) {
                HashMap<String, Object> querParam = new HashMap<String, Object>();
                querParam.put("eleIdPath", treeNode.getEleIdPath() + MultiOrgService.PATH_SPLIT_SYSMBOL + "%");
                List<MultiOrgTreeNode> multiOrgTreeNodes = multiOrgTreeNodeService.listByHQL("from MultiOrgTreeNode where eleIdPath like :eleIdPath ", querParam);
                for (MultiOrgTreeNode multiOrgTreeNode : multiOrgTreeNodes) {
                    if (multiOrgTreeNode.getEleId().startsWith(IdPrefix.JOB.getValue())) {
                        jobIdSet.add(multiOrgTreeNode.getEleId());
                    }
                }
            }
        }
        if (jobIdSet.size() > 0) {
            HashMap<String, Object> queryUser = new HashMap<String, Object>();
            StringBuilder hqlUser = new StringBuilder("select a from MultiOrgUserTreeNode a,MultiOrgVersion b where a.orgVersionId = b.id and b.status = 1 and ");
            HqlUtils.appendSql("a.eleId", queryUser, hqlUser, Sets.<Serializable>newHashSet(jobIdSet));
            List<MultiOrgUserTreeNode> userTreeNodeList = multiOrgUserTreeNodeService.listByHQL(hqlUser.toString(), queryUser);
            for (MultiOrgUserTreeNode userTreeNode : userTreeNodeList) {
                userIdSet.add(userTreeNode.getUserId());
            }
        }
        UserNodePy userNodePy = new UserNodePy();
        if (userIdSet.size() > 0) {
            HashMap<String, Object> queryU = new HashMap<String, Object>();
            StringBuilder hqlSb = new StringBuilder("select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from "
                    + "MultiOrgUserAccount a,MultiOrgUserInfo b where  a.id = b.userId and ");
            HqlUtils.appendSql("a.id", queryU, hqlSb, Sets.<Serializable>newHashSet(userIdSet));
            if (StringUtils.isNotEmpty(parms.getSort()) && parms.getSort().equals("code")) {
                hqlSb.append(" order by a.code");
            } else {
                hqlSb.append(" order by a.userNamePy");
            }
            List<UserNode> userNodes = multiOrgUserTreeNodeService.listItemHqlQuery(hqlSb.toString(), UserNode.class, queryU);
            userNodePy = ConvertOrgNode.convertUserNodePy(userNodes);
        }
        return userNodePy;
    }

    private List<OrgNode> orgNodeList(String userId, String unitId, String keyword, List<String> checkedIds) {
        // 如果没有指定用户，则用当前登录用户ID
        if (StringUtils.isBlank(userId)) {
            userId = SpringSecurityUtils.getCurrentUserId();
        }
        // 需指定 单位Id
        if (StringUtils.isBlank(unitId)) {
            unitId = SpringSecurityUtils.getCurrentUserUnitId();
        }
        List<Role> roleList = roleFacadeService.roleNoDefList(unitId, keyword, "code");
        List<OrgNode> orgNodeList = new ArrayList<>();
        if (checkedIds == null) {
            checkedIds = new ArrayList<>();
        }
        Set<String> checkedIdSet = new HashSet<>(checkedIds);
        for (Role role : roleList) {
            OrgNode orgNode = new OrgNode();
            orgNode.setId(role.getId());
            if (checkedIdSet.contains(orgNode.getId())) {
                orgNode.setChecked(true);
            }
            orgNode.setName(role.getName());
            orgNode.setType(IdPrefix.ROLE.getValue());
            orgNode.setIconSkin(IdPrefix.ROLE.getValue());
            orgNodeList.add(orgNode);
        }
        return orgNodeList;
    }
}
