package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentRoleDao;
import com.wellsoft.pt.org.entity.DepartmentRole;
import com.wellsoft.pt.org.service.DepartmentRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 部门角色
 */
@Service
@Transactional
public class DepartmentRoleServiceImpl extends BaseServiceImpl implements DepartmentRoleService {
    @Autowired
    DepartmentRoleDao departmentRoleDao;


    public List<DepartmentRole> getDepartmentRoleByDepartmentUuid(String departmentUuid) {
        return departmentRoleDao.getDepartmentRoleByDepartmentUuid(departmentUuid);
    }

    public List<DepartmentRole> getDepartmentRoleByRoleUuid(String roleUuid) {
        return departmentRoleDao.getDepartmentRoleByRoleUuid(roleUuid);
    }

    public void deleteDepartmentRoleByDepartmentUuidAndRoleUuid(
            String departmentUuid, String roleUuid) {
        departmentRoleDao.deleteDepartmentRoleByDepartmentUuidAndRoleUuid(departmentUuid, roleUuid);
    }

    @Override
    public void save(DepartmentRole departmentRole) {

        departmentRoleDao.saveDepartmentRole(departmentRole);
    }

}
