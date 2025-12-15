package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.DutyRoleDao;
import com.wellsoft.pt.org.entity.DutyRole;
import com.wellsoft.pt.org.service.DutyRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 职务角色
 */
@Service
@Transactional
public class DutyRoleServiceImpl extends BaseServiceImpl implements DutyRoleService {
    @Autowired
    private DutyRoleDao dutyRoleDao;


    public List<DutyRole> getDutyRoleByDutyUuid(String dutyUuid) {
        return dutyRoleDao.getDutyRoleByDutyUuid(dutyUuid);
    }

    public List<DutyRole> getDutyRoleByRoleUuid(String roleUuid) {
        return dutyRoleDao.getDutyRoleByRoleUuid(roleUuid);
    }

    public void deleteDutyRoleByDutyUuidAndRoleUuid(String dutyUuid, String roleUuid) {
        dutyRoleDao.deleteDutyRoleByDutyUuidAndRoleUuid(dutyUuid, roleUuid);
    }

    @Override
    public void save(DutyRole dutyRole) {
        // TODO Auto-generated method stub
        dutyRoleDao.saveDutyRole(dutyRole);
    }


}
