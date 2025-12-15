package com.wellsoft.pt.org.service.impl;


import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.dao.UserRoleDao;
import com.wellsoft.pt.org.entity.UserRole;
import com.wellsoft.pt.org.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


/**
 * 用户角色
 */
//@Service
//@Transactional
@Deprecated
public class UserRoleServiceImpl extends BaseServiceImpl implements UserRoleService {
    @Autowired
    private UserRoleDao userRoleDao;

    @Override
    public List<UserRole> getUserRoleByUserUuid(String userUuid) {
        // TODO Auto-generated method stub
        return userRoleDao.getUserRoleByUserUuid(userUuid);
    }

    @Override
    public List<UserRole> getUserRoleByRoleUuid(String roleUuid) {
        // TODO Auto-generated method stub
        return userRoleDao.getUserRoleByRoleUuid(roleUuid);
    }

    @Override
    public void deleteUserRoleByUserUuidAndRoleUuid(String userUuid,
                                                    String roleUuid) {
        this.userRoleDao.deleteUserRoleByUserUuidAndRoleUuid(userUuid, roleUuid);
    }

    @Override
    public void save(UserRole userRole) {

        // TODO Auto-generated method stub
        this.userRoleDao.saveRole(userRole);
    }


}
