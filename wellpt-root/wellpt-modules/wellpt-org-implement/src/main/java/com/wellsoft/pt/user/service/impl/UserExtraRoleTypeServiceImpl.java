package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.user.dao.UserExtraRoleTypeDaoImpl;
import com.wellsoft.pt.user.entity.UserExtraRoleTypeEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.service.UserExtraRoleTypeService;
import com.wellsoft.pt.user.service.UserInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年11月04日   shenhb	 Create
 * </pre>
 */
@Service
public class UserExtraRoleTypeServiceImpl extends AbstractJpaServiceImpl<UserExtraRoleTypeEntity, UserExtraRoleTypeDaoImpl, String> implements UserExtraRoleTypeService {

    @Resource
    UserInfoService userInfoService;

    @Override
    public Set<UserTypeEnum> getUserExtraUserTypeEnums(String loginName) {
        List<UserExtraRoleTypeEntity> roleEntityList = this.dao.listByFieldEqValue("loginName", loginName);
        Set<UserTypeEnum> userTypeEnums = Sets.newHashSet();
        for (UserExtraRoleTypeEntity entity : roleEntityList) {
            userTypeEnums.add(entity.getUserExtraRoleType());
        }
        return userTypeEnums;
    }

    @Override
    @Transactional
    public void saveUserExtraRoleType(String loginName, UserTypeEnum type) {

        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity == null) {
            return;
        }

        Set<UserTypeEnum> userExtraUserTypeEnums = this.getUserExtraUserTypeEnums(loginName);
        if (!userExtraUserTypeEnums.contains(type)) {
            UserExtraRoleTypeEntity entity = new UserExtraRoleTypeEntity();
            entity.setLoginName(loginName);
            entity.setUserExtraRoleType(type);
            this.dao.save(entity);
        }
    }

    @Override
    @Transactional
    public void deleteUserExtraRoleType(String loginName, UserTypeEnum type) {
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity == null) {
            return;
        }

        Map<String, Object> params = Maps.newHashMap();
        params.put("login_name", loginName);
        params.put("role_type", type.ordinal());
        this.dao.deleteBySQL("delete from USER_EXTRA_ROLE_TYPE where LOGIN_NAME = :login_name and USER_EXTRA_ROLE_TYPE = :role_type", params);
    }
}
