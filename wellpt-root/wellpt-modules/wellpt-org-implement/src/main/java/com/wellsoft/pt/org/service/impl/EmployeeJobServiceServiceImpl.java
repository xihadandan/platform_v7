package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.EmployeeJobDao;
import com.wellsoft.pt.org.entity.EmployeeJob;
import com.wellsoft.pt.org.service.EmployeeJobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 职位角色
 */
@Service
@Transactional
public class EmployeeJobServiceServiceImpl extends BaseServiceImpl implements EmployeeJobService {

    @Autowired
    EmployeeJobDao employeeJobDao;

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getByEmployee(String uuid) {
        return employeeJobDao.getByEmployee(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteByEmployee(String uuid) {
        this.employeeJobDao.deleteByEmployee(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteJob(String uuid) {
        this.employeeJobDao.deleteJob(uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getJobs(String uuid) {
        return employeeJobDao.getJobs(uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getMajorJobs(String uuid) {
        return employeeJobDao.getMajorJobs(uuid);
    }

    /**
     * @param uuid
     * @return
     */
    public List<EmployeeJob> getOtherJobs(String uuid) {
        return employeeJobDao.getOtherJobs(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteMajorByEmployee(String uuid) {
        this.employeeJobDao.deleteMajorByEmployee(uuid);
    }

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    public void deleteOtherByEmployee(String uuid) {
        this.employeeJobDao.deleteOtherByEmployee(uuid);
    }


}
