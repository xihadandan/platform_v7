/*
 * @(#)2013-3-8 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.enums;

/**
 * Description: 参与者枚举类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-8.1	zhulh		2013-3-8		Create
 * </pre>
 * @date 2013-3-8
 */
public enum Participant {
    // 1、组织机构
    Unit,
    // 2、文档域
    FormField,
    // 3、办理环节
    TaskHistory,
    // 4、内置可选值(前办理人、申请人、前一环节办理人、全组织)
    Option,
    // 5、接口实现
    Interface,

    // 组织类型
    OrganizationOf,

    // 1、直接领导
    LeaderOf,
    // 2、部门领导
    DeptLeaderOf,
    // 3、分管领导
    BranchedLeaderOf,
    // 4、所有领导
    AllLeaderOf,
    // 5、部门
    DeptOf,
    // 6、上级部门
    ParentDeptOf,
    // 7、根部门
    RootDeptOf,
    // 8、直系部门人员
    SameDeptOf,
    // 9、同一根部门人员
    SameRootDeptOf,


    // 前办理人
    PriorUser,
    // 前办理人的直接汇报人
    DirectLeaderOfPriorUser,
    // 前办理人的部门领导
    DeptLeaderOfPriorUser,
    // 前办理人的上级领导
    LeaderOfPriorUser,
    // 前办理人的分管领导
    BranchedLeaderOfPriorUser,
    // 前办理人的所有上级领导
    AllLeaderOfPriorUser,
    // 前办理人的部门人员
    DeptOfPriorUser,
    // 前办理人的上级部门人员
    ParentDeptOfPriorUser,
    // 前办理人的根部门人员
    RootDeptOfPriorUser,
    // 前办理人的根节点人员
    RootNodeOfPriorUser,
    // 前办理人的单位人员
    BizUnitOfPriorUser,
    // 前办理人直接下属
    SubordinateOfPriorUser,
    // 前办理人的所有下属
    AllSubordinateOfPriorUser,
    // 前一个环节办理人
    PriorTaskUser,

    // 申请人
    Creator, // OK
    // 申请人的直接汇报人
    DirectLeaderOfCreator,
    // 申请人的部门领导
    DeptLeaderOfCreator, // OK
    // 申请人的上级领导
    LeaderOfCreator, // OK
    // 申请人的分管领导
    BranchedLeaderOfCreator, // OK
    // 申请人的所有上级领导
    AllLeaderOfCreator, // OK
    // 申请人的部门人员
    DeptOfCreator, // OK
    // 申请人的上级部门人员
    ParentDeptOfCreator, // OK
    // 申请人的根部门人员
    RootDeptOfCreator, // OK
    // 申请人的根节点人员
    RootNodeOfCreator,
    // 申请人的单位人员
    BizUnitOfCreator, // OK
    // 全组织
    // Corp, // OK
    // 申请人的同业务角色人员(已弃用)
    SameBizRoleOfCreator,

    // 业务组织
    // 前办理人
    // PriorUser,
    // 前办理人的同业务项人员
    BizItemOfPriorUser,
    // 前办理人同业务项的指定角色人员
    RoleUserOfBizItemOfPriorUser,
    // 前办理人的同部门人员
    // DeptOfPriorUser,
    // 前办理人的同部门同角色人员
    DeptAndBizRoleOfPriorUser,
    // 前办理人同部门的指定角色人员
    RoleUserOfDeptOfPriorUser,
    // 前办理人的上级部门人员
    // ParentDeptOfPriorUser,
    // 前办理人上级部门的指定角色人员
    RoleUserOfParentDeptOfPriorUser,
    // 前办理人的根部门人员
    // RootDeptOfPriorUser,
    // 前办理人根部门的指定角色人员
    RoleUserOfRootDeptOfPriorUser,
    // 前办理人的根节点人员
    // RootNodeOfPriorUser,
    // 前办理人根节点的指定角色人员
    RoleUserOfRootNodeOfPriorUser,
    // 前办理人的同角色人员
    BizRoleOfPriorUser,

    // 业务组织
    // 申请人
    // Creator,
    // 申请人的同业务项人员
    BizItemOfCreator,
    // 申请人同业务项的指定角色人员
    RoleUserOfBizItemOfCreator,
    // 申请人的同部门人员
    // DeptOfCreator,
    // 申请人的同部门同角色人员
    DeptAndBizRoleOfCreator,
    // 申请人同部门的指定角色人员
    RoleUserOfDeptOfCreator,
    // 申请人的上级部门人员
    // ParentDeptOfCreator,
    // 申请人上级部门的指定角色人员
    RoleUserOfParentDeptOfCreator,
    // 申请人的根部门人员
    // RootDeptOfCreator,
    // 申请人根部门的指定角色人员
    RoleUserOfRootDeptOfCreator,
    // 申请人的根节点人员
    // RootNodeOfCreator,
    // 申请人根节点的指定角色人员
    RoleUserOfRootNodeOfCreator,
    // 申请人的同角色人员
    BizRoleOfCreator,

    // 人员过滤
    // 限于前办理人的同一部门人员
    SameDeptAsPrior,
    // 限于前办理人的同一根部门人员
    SameRootDeptAsPrior,
    // 限于前办理人的同一根节点人员
    SameRootNodeAsPrior,
    // 限于前办理人的同一单位人员
    SameBizUnitAsPrior,
    // 限于前办理人的直接汇报人
    SameDirectLeaderAsPrior,
    // 限于前办理人的部门领导
    SameDeptLeaderAsPrior,
    // 限于前办理人的上级领导
    SameLeaderAsPrior,
    // 限于前办理人的分管领导
    SameBranchLeaderAsPrior,
    // 限于前办理人的所有上级领导
    SameAllLeaderAsPrior,
    // 限于前办理人的直接下属
    SameSubordinateOfPrior,
    // 限于前办理人的所有下属
    SameAllSubordinateOfPrior,

    // 业务组织
    // 限于前办理人的同业务项人员
    SameBizItemAsPrior,
    // 限于前办理人同业务项的指定角色人员
    SameRoleUserOfBizItemAsPrior,
    // 限于前办理人的同部门人员
    // SameDeptAsPrior
    // 限于前办理人的同部门同角色人员
    SameDeptAndBizRoleAsPrior,
    // 限于前办理人同部门的指定角色人员
    SameRoleUserOfDeptAsPrior,
    // 限于前办理人的上级部门人员
    SameParentDeptAsPrior,
    // 限于前办理人上级部门的指定角色人员
    SameRoleUserOfParentDeptAsPrior,
    // 限于前办理人的根部门人员
    // SameRootDeptAsPrior,
    // 限于前办理人根部门的指定角色人员
    SameRoleUserOfRootDeptAsPrior,
    // 限于前办理人的根节点人员
    // SameRootNodeAsPrior,
    // 限于前办理人根节点的指定角色人员
    SameRoleUserOfRootNodeAsPrior,
    // 限于前办理人的同角色人员
    SameBizRoleAsPrior,
    // 前办理人的同业务角色人员(已弃用)
    SameBizRoleOfPriorUser,

    // 限于申请人的同一部门人员
    SameDeptAsCreator,
    // 限于申请人的同一根部门人员
    SameRootDeptAsCreator,
    // 限于申请人的同一根节点人员
    SameRootNodeAsCreator,
    // 限于申请人的同一单位人员
    SameBizUnitAsCreator,
    // 限于申请人的直接汇报人
    SameDirectLeaderAsCreator,
    // 限于申请人的部门领导
    SameDeptLeaderAsCreator,
    // 限于申请人的上级领导
    SameLeaderAsCreator,
    // 限于申请人的分管领导
    SameBranchLeaderAsCreator,
    // 限于申请人的所有上级领导
    SameAllLeaderAsCreator,
    // // 指定办理人的直接领导
    // LeaderOfUser,
    // // 指定办理人的部门领导
    // DeptLeaderOfUser,
    // // 指定办理人的分管领导
    // BranchedLeaderOfUser,
    // // 指定办理人的所有领导
    // AllLeaderOfUser

    // 业务组织
    // 限于申请人的同业务项人员
    SameBizItemAsCreator,
    // 限于申请人同业务项的指定角色人员
    SameRoleUserOfBizItemAsCreator,
    // 限于申请人的同部门人员
    // SameDeptAsCreator,
    // 限于申请人的同部门同角色人员
    SameDeptAndBizRoleAsCreator,
    // 限于申请人同部门的指定角色人员
    SameRoleUserOfDeptAsCreator,
    // 限于申请人的上级部门人员
    SameParentDeptAsCreator,
    // 限于申请人上级部门的指定角色人员
    SameRoleUserOfParentDeptAsCreator,
    // 限于申请人的根部门人员
    // SameRootDeptAsCreator,
    // 限于申请人根部门的指定角色人员
    SameRoleUserOfRootDeptAsCreator,
    // 限于申请人的根节点人员
    // SameRootNodeAsCreator,
    // 限于申请人根节点的指定角色人员
    SameRoleUserOfRootNodeAsCreator,
    // 限于申请人的同角色人员
    SameBizRoleAsCreator
}
