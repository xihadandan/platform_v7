package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentPrivilegeDao;
import com.wellsoft.pt.org.entity.DepartmentPrivilege;
import com.wellsoft.pt.org.service.DepartmentPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 部门权限
 */
@Service
@Transactional
public class DepartmentPrivilegeServiceImpl extends BaseServiceImpl implements DepartmentPrivilegeService {
    @Autowired
    private DepartmentPrivilegeDao departmentPrivilegeDao;

    public List<DepartmentPrivilege> getDepartmentPrivilegeByDepartmentUuid(String departmentUuid) {
        return departmentPrivilegeDao.getDepartmentPrivilegeByDepartmentUuid(departmentUuid);
    }

    public List<DepartmentPrivilege> getDepartmentPrivilegeByPrivilegeUuid(String privilegeUuid) {
        return departmentPrivilegeDao.getDepartmentPrivilegeByPrivilegeUuid(privilegeUuid);
    }

    @Override
    public void save(DepartmentPrivilege departmentPrivilege) {
        // TODO Auto-generated method stub
        departmentPrivilegeDao.saveDepartmentPrivilege(departmentPrivilege);
    }

}
