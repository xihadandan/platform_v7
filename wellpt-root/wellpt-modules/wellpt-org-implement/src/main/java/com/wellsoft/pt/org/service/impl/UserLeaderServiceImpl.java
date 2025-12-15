/*
 * @(#)2015-10-14 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.dao.UserLeaderDao;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.DeptPrincipal;
import com.wellsoft.pt.org.entity.JobPrincipal;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.UserLeaderService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
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
@Service
@Transactional(readOnly = true)
public class UserLeaderServiceImpl extends BaseServiceImpl implements UserLeaderService {

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private UserLeaderDao userLeaderDao;

    @Autowired
    private UserDao userDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLeaderService#getUserSuperiorLeaders(java.lang.String, java.lang.String)
     */
    @Override
    public List<User> getUserSuperiorLeaders(String userId, String orgId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("orgId", orgId);
        return this.nativeDao.namedQuery("getUserSuperiorLeader", values, User.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLeaderService#getUserJobReportLeaders(java.lang.String, java.lang.String)
     */
    @Override
    public List<User> getUserJobReportLeaders(String userId, String orgId) {
        // 获取汇报对象
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("excludeUserId", userId);
        values.put("userId", userId);
        values.put("orgId", orgId);
        List<JobPrincipal> jobPrincipals = this.nativeDao.namedQuery("getJobPrincipal", values, JobPrincipal.class);
        if (jobPrincipals.isEmpty()) {
            return new ArrayList<User>(0);
        }

        List<String> userIds = new ArrayList<String>(0);
        List<String> jobIds = new ArrayList<String>(0);
        for (JobPrincipal jobPrincipal : jobPrincipals) {
            String reportId = jobPrincipal.getOrgId();
            if (reportId.startsWith(IdPrefix.USER.getValue())) {// U
                userIds.add(reportId);
            } else if (reportId.startsWith("J")) { // J
                jobIds.add(reportId);
            }
        }

        // 解析汇报对象
        if (userIds.isEmpty()) {
            userIds.add("-1");
        }
        if (jobIds.isEmpty()) {
            jobIds.add("-1");
        }
        values.put("userIds", userIds);
        values.put("jobIds", jobIds);
        return this.nativeDao.namedQuery("getJobReportLeader", values, User.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLeaderService#getUserBranchedLeader(java.lang.String, java.lang.String)
     */
    @Override
    public List<User> getUserDepartmentBranchedLeaders(String userId, String orgId, boolean hasGetAll, boolean hasSelf) {
        List<String> types = new ArrayList<String>();
        types.add(DeptPrincipal.TYPE_BRANCHED);
        return getDepartmentLeaders(userId, orgId, types, hasGetAll, hasSelf);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLeaderService#getUserDepartmentPrincipal(java.lang.String, java.lang.String)
     */
    @Override
    public List<User> getUserDepartmentPrincipalLeaders(String userId, String orgId, boolean hasGetAll, boolean hasSelf) {
        List<String> types = new ArrayList<String>();
        types.add(DeptPrincipal.TYPE_PRINCIPAL);
        return getDepartmentLeaders(userId, orgId, types, hasGetAll, hasSelf);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLeaderService#getUserDirectLeaders(java.lang.String, java.lang.String)
     */
    @Override
    public List<User> getUserDirectLeaders(String userId, String orgId) {
        List<User> leaders = new ArrayList<User>();
        // 1、人员-上级领导
        leaders.addAll(getUserSuperiorLeaders(userId, orgId));
        // 2、职位-职位汇报对象
        leaders.addAll(getUserJobReportLeaders(userId, orgId));
        // 2、部门负责人
        leaders.addAll(getUserDepartmentPrincipalLeaders(userId, orgId, false, false));

        return leaders;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UserLeaderService#getUserAllLeaders(java.lang.String, java.lang.String)
     */
    @Override
    public List<User> getUserAllLeaders(String userId, String orgId) {
        List<User> leaders = new ArrayList<User>();
        // 1、分管领导
        leaders.addAll(getUserDepartmentBranchedLeaders(userId, orgId, false, false));
        // 2、部门负责人
        leaders.addAll(getUserDepartmentPrincipalLeaders(userId, orgId, true, false));
        // 3、直属领导
        leaders.addAll(getUserDirectLeaders(userId, orgId));

        return leaders;
    }

    /**
     * @param userId
     * @param orgId
     * @param types
     * @param hasGetAll
     * @param hasSelf
     * @return
     */
    private List<User> getDepartmentLeaders(String userId, String orgId, List<String> types, boolean hasGetAll,
                                            boolean hasSelf) {
        List<User> departmentPrincipals = new ArrayList<User>(0);
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        values.put("orgId", orgId);
        List<Department> departments = this.nativeDao.namedQuery("getUserDepartmentByUserIdAndOrgId", values,
                Department.class);
        for (Department department : departments) {
            departmentPrincipals.addAll(getDepartmentLeaders(userId, department.getId(), types, hasSelf));
        }

        // 获取上级部门的部门负责人
        if (hasGetAll) {
            for (Department department : departments) {
                Department parent = departmentService.get(department.getUuid()).getParent();
                while (parent != null) {
                    departmentPrincipals.addAll(getDepartmentLeaders(userId, parent.getId(), types, hasSelf));

                    parent = parent.getParent();
                }
            }
        }

        // 没有取全部的情况下，一级一级往上取
        if (departmentPrincipals.isEmpty() && !hasGetAll) {
            for (Department department : departments) {
                Department parent = departmentService.get(department.getUuid()).getParent();
                while (parent != null) {
                    departmentPrincipals.addAll(getDepartmentLeaders(userId, parent.getId(), types, hasSelf));
                    parent = parent.getParent();

                    // 取到值，结束
                    if (!departmentPrincipals.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return departmentPrincipals;
    }

    /**
     * @param departmentId
     * @param types
     * @return
     */
    private List<User> getDepartmentLeaders(String userId, String departmentId, List<String> types, boolean hasSelf) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentId", departmentId);
        values.put("types", types);
        List<DeptPrincipal> deptPrincipals = this.nativeDao.namedQuery("getDepartmentLeader", values,
                DeptPrincipal.class);
        if (deptPrincipals.isEmpty()) {
            return new ArrayList<User>(0);
        }

        List<String> userIds = new ArrayList<String>(0);
        List<String> jobIds = new ArrayList<String>(0);
        for (DeptPrincipal deptPrincipal : deptPrincipals) {
            String reportId = deptPrincipal.getOrgId();
            if (reportId.startsWith(IdPrefix.USER.getValue())) {// U
                userIds.add(reportId);
            } else if (reportId.startsWith("J")) { // J
                jobIds.add(reportId);
            }
        }

        // 解析汇报对象
        if (userIds.isEmpty()) {
            userIds.add("-1");
        }
        if (jobIds.isEmpty()) {
            jobIds.add("-1");
        }
        values.put("userIds", userIds);
        values.put("jobIds", jobIds);

        List<User> departmentPrincipals = this.nativeDao.namedQuery("getJobReportLeader", values, User.class);

        if (!hasSelf) {
            departmentPrincipals = removeByUserId(departmentPrincipals, userId);
        }

        return departmentPrincipals;
    }

    /**
     * @param departmentPrincipal
     * @param userId
     * @return
     */
    private List<User> removeByUserId(List<User> departmentPrincipals, String userId) {
        boolean containSelf = false;
        for (User user : departmentPrincipals) {
            if (StringUtils.equals(user.getId(), userId)) {
                containSelf = true;
                break;
            }
        }
        if (!containSelf) {
            return departmentPrincipals;
        }
        return new ArrayList<User>(0);
    }

}
