/*
 * @(#)2014-9-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.util;

import com.wellsoft.pt.bpm.engine.enums.Participant;
import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-9-28.1	zhulh		2014-9-28		Create
 * </pre>
 * @date 2014-9-28
 */
public class ParticipantUtils {
    // 人员<code,name>
    private static Map<String, String> ptMap = new LinkedHashMap<String, String>();

    static {
        // 前办理人
        ptMap.put(Participant.PriorUser.name(), "前办理人");
        ptMap.put(Participant.DirectLeaderOfPriorUser.name(), "前办理人的直接汇报人");
        // 前办理人的部门领导
        ptMap.put(Participant.DeptLeaderOfPriorUser.name(), "前办理人的部门领导");
        // 前办理人的上级领导
        ptMap.put(Participant.LeaderOfPriorUser.name(), "前办理人的上级领导");
        // 前办理人的分管领导
        ptMap.put(Participant.BranchedLeaderOfPriorUser.name(), "前办理人的分管领导");
        // 前办理人的所有上级领导
        ptMap.put(Participant.AllLeaderOfPriorUser.name(), "前办理人的所有上级领导");
        // 前办理人的部门人员
        ptMap.put(Participant.DeptOfPriorUser.name(), "前办理人的部门人员");
        // 前办理人的上级部门人员
        ptMap.put(Participant.ParentDeptOfPriorUser.name(), "前办理人的上级部门人员");
        // 前办理人的根部门人员
        ptMap.put(Participant.RootDeptOfPriorUser.name(), "前办理人的根部门人员");
        // 前办理人的根节点人员
        ptMap.put(Participant.RootNodeOfPriorUser.name(), "前办理人的根节点人员");
        // 前办理人的单位人员
        ptMap.put(Participant.BizUnitOfPriorUser.name(), "前办理人的单位人员");
        // 前办理人直接下属
        ptMap.put(Participant.SubordinateOfPriorUser.name(), "前办理人直接下属");
        // 前办理人的所有下属
        ptMap.put(Participant.AllSubordinateOfPriorUser.name(), "前办理人的所有下属");
        // 前一个环节办理人
        ptMap.put(Participant.PriorTaskUser.name(), "前一个环节办理人");

        // 申请人
        ptMap.put(Participant.Creator.name(), "申请人");
        // 申请人的直接汇报人
        ptMap.put(Participant.DirectLeaderOfCreator.name(), "申请人的直接汇报人");
        // 申请人的部门领导
        ptMap.put(Participant.DeptLeaderOfCreator.name(), "申请人的部门领导");
        // 申请人的上级领导
        ptMap.put(Participant.LeaderOfCreator.name(), "申请人的上级领导");
        // 申请人的分管领导
        ptMap.put(Participant.BranchedLeaderOfCreator.name(), "申请人的分管领导");
        // 申请人的所有上级领导
        ptMap.put(Participant.AllLeaderOfCreator.name(), "申请人的所有上级领导");
        // 申请人的部门人员
        ptMap.put(Participant.DeptOfCreator.name(), "申请人的部门人员");
        // 申请人的上级部门人员
        ptMap.put(Participant.ParentDeptOfCreator.name(), "申请人的上级部门人员");
        // 申请人的根部门人员
        ptMap.put(Participant.RootDeptOfCreator.name(), "申请人的根部门人员");
        // 申请人的根节点人员
        ptMap.put(Participant.RootNodeOfCreator.name(), "申请人的根节点人员");
        // 申请人的单位人员
        ptMap.put(Participant.BizUnitOfCreator.name(), "申请人的单位人员");
        // 全组织
        // ptMap.put(Participant.Corp.name(), "全组织");

        // 前办理人的同业务项人员
        ptMap.put(Participant.BizItemOfPriorUser.name(), "前办理人的同业务项人员");
        // 前办理人同业务项的指定角色人员
        ptMap.put(Participant.RoleUserOfBizItemOfPriorUser.name(), "前办理人同业务项的指定角色人员");
        // 前办理人的同部门同角色人员
        ptMap.put(Participant.DeptAndBizRoleOfPriorUser.name(), "前办理人的同部门同角色人员");
        // 前办理人同部门的指定角色人员
        ptMap.put(Participant.RoleUserOfDeptOfPriorUser.name(), "前办理人同部门的指定角色人员");
        // 前办理人上级部门的指定角色人员
        ptMap.put(Participant.RoleUserOfParentDeptOfPriorUser.name(), "前办理人上级部门的指定角色人员");
        // 前办理人根部门的指定角色人员
        ptMap.put(Participant.RoleUserOfRootDeptOfPriorUser.name(), "前办理人根部门的指定角色人员");
        // 前办理人根节点的指定角色人员
        ptMap.put(Participant.RoleUserOfRootNodeOfPriorUser.name(), "前办理人根节点的指定角色人员");
        // 前办理人的同角色人员
        ptMap.put(Participant.BizRoleOfPriorUser.name(), "前办理人的同角色人员");

        // 申请人的同业务项人员
        ptMap.put(Participant.BizItemOfCreator.name(), "申请人的同业务项人员");
        // 申请人同业务项的指定角色人员
        ptMap.put(Participant.RoleUserOfBizItemOfCreator.name(), "申请人同业务项的指定角色人员");
        // 申请人的同部门同角色人员
        ptMap.put(Participant.DeptAndBizRoleOfCreator.name(), "申请人的同部门同角色人员");
        // 申请人同部门的指定角色人员
        ptMap.put(Participant.RoleUserOfDeptOfCreator.name(), "申请人同部门的指定角色人员");
        // 申请人上级部门的指定角色人员
        ptMap.put(Participant.RoleUserOfParentDeptOfCreator.name(), "申请人上级部门的指定角色人员");
        // 申请人根部门的指定角色人员
        ptMap.put(Participant.RoleUserOfRootDeptOfCreator.name(), "申请人根部门的指定角色人员");
        // 申请人根节点的指定角色人员
        ptMap.put(Participant.RoleUserOfRootNodeOfCreator.name(), "申请人根节点的指定角色人员");
        // 申请人的同角色人员
        ptMap.put(Participant.BizRoleOfCreator.name(), "申请人的同角色人员");

        // 限于前办理人的同一部门人员
        ptMap.put(Participant.SameDeptAsPrior.name(), "限于前办理人的同一部门人员");
        // 限于前办理人的同一根部门人员
        ptMap.put(Participant.SameRootDeptAsPrior.name(), "限于前办理人的同一根部门人员");
        // 限于前办理人的同一根节点人员
        ptMap.put(Participant.SameRootNodeAsPrior.name(), "限于前办理人的同一根节点人员");
        // 限于前办理人的同一单位人员
        ptMap.put(Participant.SameBizUnitAsPrior.name(), "限于前办理人的同一单位人员");
        // 限于前办理人的直接汇报人
        ptMap.put(Participant.SameDirectLeaderAsPrior.name(), "限于前办理人的直接汇报人");
        // 限于前办理人的部门领导
        ptMap.put(Participant.SameDeptLeaderAsPrior.name(), "限于前办理人的部门领导");
        // 限于前办理人的上级领导
        ptMap.put(Participant.SameLeaderAsPrior.name(), "限于前办理人的上级领导");
        // 限于前办理人的分管领导
        ptMap.put(Participant.SameBranchLeaderAsPrior.name(), "限于前办理人的分管领导");
        // 限于前办理人的所有上级领导
        ptMap.put(Participant.SameAllLeaderAsPrior.name(), "限于前办理人的所有上级领导");
        // 限于前办理人的直接下属
        ptMap.put(Participant.SameSubordinateOfPrior.name(), "限于前办理人的直接下属");
        // 限于前办理人的所有下属
        ptMap.put(Participant.SameAllSubordinateOfPrior.name(), "限于前办理人的所有下属");

        // 限于前办理人的同业务项人员
        ptMap.put(Participant.SameBizItemAsPrior.name(), "限于前办理人的同业务项人员");
        // 限于前办理人同业务项的指定角色人员
        ptMap.put(Participant.SameRoleUserOfBizItemAsPrior.name(), "限于前办理人同业务项的指定角色人员");
        // 限于前办理人的同部门同角色人员
        ptMap.put(Participant.SameDeptAndBizRoleAsPrior.name(), "限于前办理人的同部门同角色人员");
        // 限于前办理人同部门的指定角色人员
        ptMap.put(Participant.SameRoleUserOfDeptAsPrior.name(), "限于前办理人同部门的指定角色人员");
        // 限于前办理人的上级部门人员
        ptMap.put(Participant.SameParentDeptAsPrior.name(), "限于前办理人的上级部门人员");
        // 限于前办理人上级部门的指定角色人员
        ptMap.put(Participant.SameRoleUserOfParentDeptAsPrior.name(), "限于前办理人上级部门的指定角色人员");
        // 限于前办理人根部门的指定角色人员
        ptMap.put(Participant.SameRoleUserOfRootDeptAsPrior.name(), "限于前办理人根部门的指定角色人员");
        // 限于前办理人根节点的指定角色人员
        ptMap.put(Participant.SameRoleUserOfRootNodeAsPrior.name(), "限于前办理人根节点的指定角色人员");
        // 限于前办理人的同角色人员
        ptMap.put(Participant.SameBizRoleAsPrior.name(), "限于前办理人的同角色人员");

        // 限于申请人的同一部门人员
        ptMap.put(Participant.SameDeptAsCreator.name(), "限于申请人的同一部门人员");
        // 限于申请人的同一根部门人员
        ptMap.put(Participant.SameRootDeptAsCreator.name(), "限于申请人的同一根部门人员");
        // 限于申请人的同一根节点人员
        ptMap.put(Participant.SameRootNodeAsCreator.name(), "限于申请人的同一根节点人员");
        // 限于申请人的同一单位人员
        ptMap.put(Participant.SameBizUnitAsCreator.name(), "限于申请人的同一单位人员");
        // 限于申请人的直接汇报人
        ptMap.put(Participant.SameDirectLeaderAsCreator.name(), "限于申请人的直接汇报人");
        // 限于申请人的部门领导
        ptMap.put(Participant.SameDeptLeaderAsCreator.name(), "限于申请人的部门领导");
        // 限于申请人的上级领导
        ptMap.put(Participant.SameLeaderAsCreator.name(), "限于申请人的上级领导");
        // 限于申请人的分管领导
        ptMap.put(Participant.SameBranchLeaderAsCreator.name(), "限于申请人的分管领导");
        // 限于申请人的所有上级领导
        ptMap.put(Participant.SameAllLeaderAsCreator.name(), "限于申请人的所有上级领导");


        // 限于申请人的同业务项人员
        ptMap.put(Participant.SameBizItemAsCreator.name(), "限于申请人的同业务项人员");
        // 限于申请人同业务项的指定角色人员
        ptMap.put(Participant.SameRoleUserOfBizItemAsCreator.name(), "限于申请人同业务项的指定角色人员");
        // 限于申请人的同部门同角色人员
        ptMap.put(Participant.SameDeptAndBizRoleAsCreator.name(), "限于申请人的同部门同角色人员");
        // 限于申请人同部门的指定角色人员
        ptMap.put(Participant.SameRoleUserOfDeptAsCreator.name(), "限于申请人同部门的指定角色人员");
        // 限于申请人的上级部门人员
        ptMap.put(Participant.SameParentDeptAsCreator.name(), "限于申请人的上级部门人员");
        // 限于申请人上级部门的指定角色人员
        ptMap.put(Participant.SameRoleUserOfParentDeptAsCreator.name(), "限于申请人上级部门的指定角色人员");
        // 限于申请人根部门的指定角色人员
        ptMap.put(Participant.SameRoleUserOfRootDeptAsCreator.name(), "限于申请人根部门的指定角色人员");
        // 限于申请人根节点的指定角色人员
        ptMap.put(Participant.SameRoleUserOfRootNodeAsCreator.name(), "限于申请人根节点的指定角色人员");
        // 限于申请人的同角色人员
        ptMap.put(Participant.SameBizRoleAsCreator.name(), "限于申请人的同角色人员");

        
        // // 办理人的领导
        // ptMap.put(Participant.LeaderOfUser.name(), "办理人的直接领导");
        // // 办理人的领导
        // ptMap.put(Participant.DeptLeaderOfUser.name(), "办理人的部门领导");
        // // 办理人的所有领导
        // ptMap.put(Participant.AllLeaderOfUser.name(), "办理人的所有领导");
    }

    /**
     * @param code
     * @return
     */
    public static String getName(String code) {
        String displayName = ptMap.get(code);
        return StringUtils.isBlank(displayName) ? "" : displayName;
    }

    /**
     * @return
     */
    public static Set<String> getKeySet() {
        return ptMap.keySet();
    }

    /**
     * @param key
     * @return
     */
    public static boolean containsKey(String key) {
        return ptMap.containsKey(key);
    }

}
