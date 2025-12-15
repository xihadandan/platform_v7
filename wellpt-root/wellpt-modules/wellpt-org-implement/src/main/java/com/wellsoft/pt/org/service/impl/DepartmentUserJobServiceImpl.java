package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DepartmentUserJobDao;
import com.wellsoft.pt.org.entity.DepartmentPrincipal;
import com.wellsoft.pt.org.entity.DepartmentUserJob;
import com.wellsoft.pt.org.service.DepartmentUserJobService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 部门角色
 */
@Service
@Transactional
public class DepartmentUserJobServiceImpl extends BaseServiceImpl implements DepartmentUserJobService {

    @Autowired
    DepartmentUserJobDao departmentUserJobDao;

    public DepartmentUserJob getMajor(String userUuid) {
        return departmentUserJobDao.getMajor(userUuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<DepartmentUserJob> getByDepartment(String uuid) {
        return departmentUserJobDao.getByDepartment(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByUser(String uuid) {
        this.departmentUserJobDao.deleteByUser(uuid);
    }

    @Override
    public DepartmentPrincipal get(String uuid) {
        // TODO Auto-generated method stub
        return this.dao.get(DepartmentPrincipal.class, uuid);
    }

    @Override
    public List<DepartmentUserJob> getAll() {
        // TODO Auto-generated method stub
        return departmentUserJobDao.getAllByTenantId(SpringSecurityUtils.getCurrentTenantId());
    }
}
