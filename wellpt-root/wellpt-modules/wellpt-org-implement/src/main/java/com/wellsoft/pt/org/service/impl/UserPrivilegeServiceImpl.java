package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.UserPrivilegeDao;
import com.wellsoft.pt.org.entity.UserPrivilege;
import com.wellsoft.pt.org.service.UserPrivilegeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 用户权限
 */
@Service
@Transactional
public class UserPrivilegeServiceImpl extends BaseServiceImpl implements UserPrivilegeService {
    @Autowired
    private UserPrivilegeDao userPrivilegeDao;

    @Override
    public List<UserPrivilege> getUserPrivilegeByUserUuid(String userUuid) {
        // TODO Auto-generated method stub
        return userPrivilegeDao.getUserPrivilegeByUserUuid(userUuid);
    }

    @Override
    public List<UserPrivilege> getUserPrivilegeByPrivilegeUuid(
            String privilegeUuid) {
        // TODO Auto-generated method stub
        return userPrivilegeDao.getUserPrivilegeByPrivilegeUuid(privilegeUuid);
    }

    @Override
    public void save(UserPrivilege userPrivilege) {
        // TODO Auto-generated method stub
        userPrivilegeDao.saveUserPrivilege(userPrivilege);
    }


}
