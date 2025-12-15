package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.JobPrivilegeDao;
import com.wellsoft.pt.org.entity.JobPrivilege;
import com.wellsoft.pt.org.service.JobPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 职位权限
 */
@Service
@Transactional
public class JobPrivilegeServiceImpl extends BaseServiceImpl implements JobPrivilegeService {
    @Autowired
    private JobPrivilegeDao jobPrivilegeDao;

    @Override
    public List<JobPrivilege> getJobPrivilegeByJobUuid(String jobUuid) {
        // TODO Auto-generated method stub
        return jobPrivilegeDao.getJobPrivilegeByJobUuid(jobUuid);
    }

    @Override
    public List<JobPrivilege> getJobPrivilegeByPrivilegeUuid(
            String privilegeUuid) {
        // TODO Auto-generated method stub
        return jobPrivilegeDao.getJobPrivilegeByPrivilegeUuid(privilegeUuid);
    }


}
