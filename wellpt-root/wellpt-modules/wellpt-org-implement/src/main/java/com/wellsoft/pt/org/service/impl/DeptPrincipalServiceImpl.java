package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DeptPrincipalDao;
import com.wellsoft.pt.org.entity.DeptPrincipal;
import com.wellsoft.pt.org.service.DeptPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 部门角色
 */
@Service
@Transactional
public class DeptPrincipalServiceImpl extends BaseServiceImpl implements DeptPrincipalService {

    @Autowired
    DeptPrincipalDao deptPrincipalDao;

    /**
     * 根据部门UUID，获取部门管理员
     *
     * @param uuid
     */
    public List<DeptPrincipal> getManager(String deptUuid) {
        return deptPrincipalDao.getManager(deptUuid);
    }

    /**
     * 根据部门UUID，删除部门管理员
     *
     * @param uuid
     */
    public void deleteManager(String deptUuid) {
        this.deptPrincipalDao.deleteManager(deptUuid);
    }

    /**
     * 根据部门UUID，获取部门分管领导
     *
     * @param uuid
     */
    public List<DeptPrincipal> getBranched(String deptUuid) {
        return deptPrincipalDao.getBranched(deptUuid);
    }

    /**
     * 根据部门UUID，删除部门分管领导
     *
     * @param uuid
     */
    public void deleteBranched(String deptUuid) {
        this.deptPrincipalDao.deleteBranched(deptUuid);
    }

    /**
     * 根据部门UUID，获取部门负责人
     *
     * @param uuid
     */
    public List<DeptPrincipal> getPrincipal(String deptUuid) {
        return deptPrincipalDao.getPrincipal(deptUuid);
    }

    /**
     * 根据部门UUID，删除部门负责人
     *
     * @param uuid
     */
    public void deletePrincipal(String deptUuid) {
        this.deptPrincipalDao.deletePrincipal(deptUuid);
    }

    /**
     * 根据组织ID获得对应的部门
     */
    public List<DeptPrincipal> getPrincipalByOrgId(String userId, String jobId) {
        return deptPrincipalDao.getPrincipalByOrgId(userId, jobId);
    }


}
