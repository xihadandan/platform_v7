/*
 * @(#)2015-6-25 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rule.engine.suport;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-25.1	zhulh		2015-6-25		Create
 * </pre>
 * @date 2015-6-25
 */
public class CommandName {
    // 1、组织机构
    public static final String Unit = "Unit";
    // 2、文档域
    public static final String FormField = "FormField";
    // 3、办理环节
    public static final String TaskHistory = "TaskHistory";
    // 4、内置可选值(前办理人、申请人、前一环节办理人、全组织)
    public static final String Option = "Option";
    // 5、接口实现
    public static final String Interface = "Interface";
    // 所在成员
    public static final String NewFlowMemberOf = "NewFlowMemberOf";
    // 组织类型
    public static final String OrganizationOf = "OrganizationOf";
    // 1、直接领导
    public static final String LeaderOf = "LeaderOf";
    // 2、部门领导
    public static final String DeptLeaderOf = "DeptLeaderOf";
    // 3、分管领导
    public static final String BranchedLeaderOf = "BranchedLeaderOf";
    // 4、所有领导
    public static final String AllLeaderOf = "AllLeaderOf";
    // 5、部门
    public static final String DeptOf = "DeptOf";
    // 6、上级部门
    public static final String ParentDeptOf = "ParentDeptOf";
    // 7、根部门
    public static final String RootDeptOf = "RootDeptOf";
    // 8、直系部门人员
    public static final String SameDeptOf = "SameDeptOf";
    // 9、同一根部门人员
    public static final String SameRootDeptOf = "SameRootDeptOf";

    public static final String SetOperation = "SetOperation";

    public static final String End = "end";

    public static final String Blank = " ";

    public static final String LeftBracket = "(";

    public static final String RightBracket = ")";

}
