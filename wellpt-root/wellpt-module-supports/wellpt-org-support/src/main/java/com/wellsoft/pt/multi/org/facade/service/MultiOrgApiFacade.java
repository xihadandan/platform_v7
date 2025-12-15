/*
 * @(#)2013-1-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service;

import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgUserTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.bean.UserNode;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.support.utils.ConvertOrgNode;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 组织机构对外统一提供的接口
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-27.1	zyguo		2013-1-27		Create
 * </pre>
 * @date 2013-1-27
 */
@Component
public class MultiOrgApiFacade extends AbstractApiFacade {
    @Autowired
    private MultiOrgService multiOrgService;

//	@Autowired
//	private OrgApiFacade orgApiFacade;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private MultiOrgGroupFacade multiOrgGroupFacade;

    // 判断是否多组织的元素节点
    public static boolean isMultiOrgEleNode(String id) {
        if (id.startsWith(IdPrefix.ORG.getValue())) {
            return true;
        } else if (id.startsWith(IdPrefix.BUSINESS_UNIT.getValue())) {
            return true;
        } else if (id.startsWith(IdPrefix.DEPARTMENT.getValue()) && !id.startsWith(IdPrefix.DUTY.getValue())) {
            return true;
        } else if (id.startsWith(IdPrefix.JOB.getValue())) {
            return true;
        }
        return false;
    }

    /**
     * 处理角色删除事件
     */
    public void dealRoleRemoveEvent(String roleUuid) {
        // 1、删除角色与用户组多对多关系中作为被控方的关系表
        this.multiOrgService.dealRoleRemoveEvent(roleUuid);

        // 2、删除角色与用户多对多关系中作为被控方的关系表
        this.multiOrgUserService.dealRoleRemoveEvent(roleUuid);

        // 3、删除角色与群组多对多关系中作为被控方的关系表
        this.multiOrgGroupFacade.dealRoleRemoveEvent(roleUuid);
    }

    /**
     * 通过角色获取拥有该角色的用户账号列表
     */
    public List<MultiOrgUserAccount> queryUserListByRole(String roleUuid) {
        List<MultiOrgUserAccount> list = new ArrayList<MultiOrgUserAccount>();
        List<MultiOrgUserRole> objs = this.multiOrgUserService.queryUserListByRole(roleUuid);
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgUserRole userRole : objs) {
                MultiOrgUserAccount a = this.multiOrgUserService.getAccountByUserId(userRole.getUserId());
                if (a != null) {
                    list.add(a);
                }
            }
        }
        return list;
    }

    /**
     * 通过角色获取拥有该角色的组织节点元素列表
     */
    public List<OrgTreeNodeDto> queryOrgNodeListByRole(String roleUuid) {
        return this.multiOrgService.queryOrgNodeListByRole(roleUuid);
    }

    /**
     * 通过角色获取拥有该角色的群组列表
     */
    public List<MultiOrgGroup> queryGroupListByRole(String roleUuid) {
        return this.multiOrgGroupFacade.queryGroupListByRole(roleUuid);
    }

    /**
     * 通过用户ID,获取一个用户的完整信息
     */
    public OrgUserVo getUserById(String userId) {
        return this.multiOrgUserService.getUserById(userId);
    }

    public OrgUserVo getSimpleUserInfoById(String userId) {
        return this.multiOrgUserService.getSimpleUserInfoById(userId);
    }

    /**
     * 通过元素ID,获取元素的角色列表
     */
    public List<MultiOrgElementRole> queryRoleListOfElement(String eleId) {
        return this.multiOrgService.queryRoleListOfElement(eleId);
    }

    /**
     * 通过成员ID,获取该成员归属的群组列表
     */
    public List<MultiOrgGroupMember> queryGroupListByMemberId(String memberId) {
        return this.multiOrgGroupFacade.queryGroupListByMemberId(memberId);
    }

    /**
     * 通过群组ID,获取该群组的角色列表
     */
    public List<MultiOrgGroupRole> queryRoleListOfGroup(String groupId) {
        return this.multiOrgGroupFacade.queryRoleListOfGroup(groupId);
    }

    public List<OrgUserTreeNodeDto> queryUserByOrgTreeNode(String eleIdPath, String orgVersionId) {
        return this.multiOrgUserService.queryUserByOrgTreeNode(eleIdPath, orgVersionId);
    }

    public void addRoleMembers(String roleUuid, String memberIds) {
        if (StringUtils.isNotBlank(memberIds)) {
            String[] ids = memberIds.split(";");
            for (String id : ids) {
                if (id.startsWith(IdPrefix.USER.getValue())) {
                    this.multiOrgUserService.addRoleListOfUser(id, roleUuid);
                } else if (id.startsWith(IdPrefix.GROUP.getValue())) {
                    this.multiOrgGroupFacade.addRoleListOfGroup(id, roleUuid);
                } else if (isMultiOrgEleNode(id)) {
                    this.multiOrgService.addRoleListOfElement(id, roleUuid);
                }
            }
        }
    }

    public Select2QueryData querySelectDataFromMultiOrgSystemUnit(Select2QueryInfo select2QueryInfo) {

        String queryValue = select2QueryInfo.getSearchValue();
        List<MultiOrgSystemUnit> units = null;
        if (StringUtils.isBlank(queryValue)) {
            units = multiOrgService.findMultiOrgSystemUnitForAll();
        } else {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", "%" + queryValue + "%");
            units = multiOrgService.findMultiOrgSystemUnitByName(map);
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        for (MultiOrgSystemUnit unit : units) {
            beans.add(new Select2DataBean(unit.getId(), unit.getName()));
        }
        return new Select2QueryData(beans);

    }

    public Select2QueryData loadSelectDataFromMultiOrgSystemUnit(Select2QueryInfo select2QueryInfo) {
        String[] uuids = select2QueryInfo.getIds();
        if (uuids.length == 0) {
            return new Select2QueryData();
        }

        List<Select2DataBean> beans = new ArrayList<Select2DataBean>();
        MultiOrgSystemUnit unit = multiOrgService.getMultiOrgSystemUnit(uuids[0]);
        beans.add(new Select2DataBean(unit.getId(), unit.getName()));
        return new Select2QueryData(beans);
    }

    public List<OrgNode> queryOrgNodeListByRoleUuid(String roleUuid) {
        List<OrgNode> orgNodes = new ArrayList<>();
        List<MultiOrgUserRole> userRoleList = this.multiOrgUserService.queryUserListByRole(roleUuid);
        List<MultiOrgElementRole> elementRoleList = this.multiOrgService.queryRoleListOfElementByRoleUuid(roleUuid);
        for (MultiOrgUserRole multiOrgUserRole : userRoleList) {
            UserNode userNode = this.multiOrgUserService.queryUserNode(multiOrgUserRole.getUserId());
            if (userNode != null) {
                OrgNode orgNode = ConvertOrgNode.convert(userNode);
                orgNodes.add(orgNode);
            }
        }
        for (MultiOrgElementRole multiOrgElementRole : elementRoleList) {
            OrgNode orgNode = this.multiOrgService.queryOrgNode(multiOrgElementRole.getEleId());
            if (orgNode != null) {
                orgNodes.add(orgNode);
            }
        }
        return orgNodes;
    }
}
