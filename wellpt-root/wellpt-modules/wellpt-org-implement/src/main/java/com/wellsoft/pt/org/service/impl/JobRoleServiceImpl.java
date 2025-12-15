package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.JobRoleDao;
import com.wellsoft.pt.org.entity.JobRole;
import com.wellsoft.pt.org.service.JobRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 职位角色
 */
@Service
@Transactional
public class JobRoleServiceImpl extends BaseServiceImpl implements JobRoleService {
    @Autowired
    private JobRoleDao jobRoleDao;

    @Override
    public List<JobRole> getJobRoleByJobUuid(String jobUuid) {
        // TODO Auto-generated method stub
        return jobRoleDao.getJobRoleByJobUuid(jobUuid);
    }

    @Override
    public List<JobRole> getJobRoleByRoleUuid(String roleUuid) {
        // TODO Auto-generated method stub
        return jobRoleDao.getJobRoleByRoleUuid(roleUuid);
    }

    @Override
    public void deleteJobRoleByJobUuidAndRoleUuid(String jobUuid,
                                                  String roleUuid) {
        this.jobRoleDao.deleteJobRoleByJobUuidAndRoleUuid(jobUuid, roleUuid);
    }

    @Override
    public void save(JobRole jobRole) {
        // TODO Auto-generated method stub
        jobRoleDao.saveJobRole(jobRole);
    }

    @Override
    public void delete(JobRole jobRole) {
        // TODO Auto-generated method stub
        jobRoleDao.deleteJobRole(jobRole);
    }


}
