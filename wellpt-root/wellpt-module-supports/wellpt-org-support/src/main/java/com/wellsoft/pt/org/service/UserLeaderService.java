/*
 * @(#)2015-10-14 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.org.entity.User;

import java.util.List;

/**
 * 组织架构中设置领导的字段如下：
 * 1、部门-分管领导
 * 2、部门-部门负责人
 * 3、人员-上级领导
 * 4、职位-职位汇报对象
 * 由以上四种领导定义出三种类别领导，供流程定义中过滤领导使用，流程中的领导取值需结合所选择的组织树，若选择的管理型组织，则只考虑管理型组织树的领导，若选择多个组织（管理型组织和其他专业型组织），需考虑所选的多个组织的领导，若组织类型字段未设置，则默认查找所有组织的领导。
 * 1、分管领导：从人员所在的部门开始往上查找上层部门中分管领导字段中有设置的，作为分管领导，若查找到的分管领导是员工自己，需跳过，要继续查找上层部门的分管领导（需陈浩经理确认），查找到后就不再继续查找。
 * 2、部门负责人：从人员所在的部门开始往上查找上层部门的部门负责人字段，若找到的部门负责人中有包含自己，则此部门设置的部门负责人不取，再往上查找，一直往上找到组织树的最顶层，将所有查找到部门负责人中的人员集合在一起。（需陈浩经理确认）。
 * 3、直属领导：包含人员的上级领导字段中设置的人员、人员职位的汇报对象中设置的人员、人员所在部门开始往上查找，直到找到部门的部门负责人字段有设置为止，若找到的部门负责人中有包含自己，则继续往上层查找，找到后不再继续往上。
 * 4、所有领导：包含1、2、3点人员的集合。
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-14.1	zhulh		2015-10-14		Create
 * </pre>
 * @date 2015-10-14
 */
public interface UserLeaderService extends BaseService {

    /**
     * 3、人员-上级领导
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<User> getUserSuperiorLeaders(String userId, String orgId);

    /**
     * 4、职位-职位汇报对象
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<User> getUserJobReportLeaders(String userId, String orgId);

    /**
     * 1、分管领导
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<User> getUserDepartmentBranchedLeaders(String userId, String orgId, boolean hasGetAll, boolean hasSelf);

    /**
     * 2、部门负责人
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<User> getUserDepartmentPrincipalLeaders(String userId, String orgId, boolean hasGetAll, boolean hasSelf);

    /**
     * 3、直属领导
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<User> getUserDirectLeaders(String userId, String orgId);

    /**
     * 4、所有领导：包含1、2、3点人员的集合。
     *
     * @param userId
     * @param orgId
     * @return
     */
    List<User> getUserAllLeaders(String userId, String orgId);

}
