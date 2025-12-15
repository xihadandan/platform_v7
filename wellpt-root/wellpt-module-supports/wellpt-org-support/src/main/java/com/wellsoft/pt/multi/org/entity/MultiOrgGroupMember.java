/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.multi.org.bean.OrgTreeNode;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 组织节点基本类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_GROUP_MEMBER")
@DynamicUpdate
@DynamicInsert
public class MultiOrgGroupMember extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1446219739207353847L;
    @ApiModelProperty("群组ID")
    private String groupId;
    @ApiModelProperty("成员ID")
    private String memberObjId;
    @ApiModelProperty("成员名称")
    private String memberObjName;
    @ApiModelProperty("成员类型")
    private String memberObjType;

    public static String memberList2ids(List<MultiOrgGroupMember> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgGroupMember m : list) {
                ids.add(m.getMemberObjId());
            }
            return StringUtils.join(ids, ";");
        }
    }

    public static String memberList2names(List<MultiOrgGroupMember> list) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            ArrayList<String> ids = new ArrayList<String>();
            for (MultiOrgGroupMember m : list) {
                ids.add(m.getMemberObjName());
            }
            return StringUtils.join(ids, ";");
        }
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId 要设置的groupId
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the memberObjId
     */
    public String getMemberObjId() {
        return memberObjId;
    }

    /**
     * @param memberObjId 要设置的memberObjId
     */
    public void setMemberObjId(String memberObjId) {
        this.memberObjId = memberObjId;
    }

    /**
     * @return the memberObjName
     */
    public String getMemberObjName() {
        return memberObjName;
    }

    /**
     * @param memberObjName 要设置的memberObjName
     */
    public void setMemberObjName(String memberObjName) {
        this.memberObjName = memberObjName;
    }

    /**
     * @return the memberObjType
     */
    public String getMemberObjType() {
        return memberObjType;
    }

    /**
     * @param memberObjType 要设置的memberObjType
     */
    public void setMemberObjType(String memberObjType) {
        this.memberObjType = memberObjType;
    }

    public OrgTreeNode convert2TreeNode() {
        OrgTreeNode treeNode = new OrgTreeNode();
        treeNode.setId(this.memberObjId);
        treeNode.setName(this.memberObjName);
        treeNode.setType(this.memberObjType);
        treeNode.setIconSkin(this.memberObjType);
        return treeNode;
    }
}
