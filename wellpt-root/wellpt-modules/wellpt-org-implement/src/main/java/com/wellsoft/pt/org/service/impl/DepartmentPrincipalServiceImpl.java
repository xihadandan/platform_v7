/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentPrincipalDao;
import com.wellsoft.pt.org.entity.DepartmentPrincipal;
import com.wellsoft.pt.org.service.DepartmentPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DepartmentPrincipalServiceImpl extends BaseServiceImpl implements DepartmentPrincipalService {

    @Autowired
    DepartmentPrincipalDao departmentPrincipalDao;

    /**
     * 根据部门UUID，获取部门管理员
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getManager(String deptUuid) {
        return departmentPrincipalDao.getManager(deptUuid);
    }

    /**
     * 根据部门UUID，删除部门管理员
     *
     * @param uuid
     */
    public void deleteManager(String deptUuid) {
        this.departmentPrincipalDao.deleteManager(deptUuid);
    }

    /**
     * 根据部门UUID，获取部门分管领导
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getBranched(String deptUuid) {
        return departmentPrincipalDao.getBranched(deptUuid);
    }

    /**
     * 根据部门UUID，删除部门分管领导
     *
     * @param uuid
     */
    public void deleteBranched(String deptUuid) {
        this.departmentPrincipalDao.deleteBranched(deptUuid);
    }

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<DepartmentPrincipal> getPrincipal(String deptUuid) {
        return departmentPrincipalDao.getPrincipal(deptUuid);
    }

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid) {
        departmentPrincipalDao.deletePrincipal(deptUuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUserUuid(String userUuid) {
        this.departmentPrincipalDao.deleteByUserUuid(userUuid);
    }

    /**
     * 删除DeptPrincipal（部门负责人）中的用户
     *
     * @param userId
     */
    public void deleteByUserId(String userId) {
        this.departmentPrincipalDao.deleteByUserId(userId);
    }

    @Override
    public List<DepartmentPrincipal> getAll() {
        // TODO Auto-generated method stub
        return departmentPrincipalDao.getAll();
    }

}
