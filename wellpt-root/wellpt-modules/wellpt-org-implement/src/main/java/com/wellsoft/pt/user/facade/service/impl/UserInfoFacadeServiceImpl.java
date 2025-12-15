package com.wellsoft.pt.user.facade.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.UserLoginLogEntity;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import com.wellsoft.pt.user.service.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月11日   chenq	 Create
 * </pre>
 */
@Service
public class UserInfoFacadeServiceImpl implements UserInfoFacadeService {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    UserTypeRoleService userTypeRoleService;

    @Autowired
    UserExtraRoleTypeService userExtraRoleTypeService;

    @Autowired
    UserRoleService userRoleService;

    @Override
    public UserDto getUserByLoginName(String loginName, String loginNameHashAlgorithmCode) {
        UserAccountEntity userAccountEntity = userAccountService.getByLoginName(loginName);
        if (userAccountEntity == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        UserInfoEntity userInfoEntity = userInfoService.getUserInfoByAcctUuid(userAccountEntity.getUuid());
        BeanUtils.copyProperties(userInfoEntity, userDto);
        BeanUtils.copyProperties(userAccountEntity, userDto);
        userDto.setAccountType(userAccountEntity.getType());
        userDto.setLockCause(userAccountEntity.getLockCause());
        userDto.setUnlockTime(userAccountEntity.getUnlockTime());
        String userName = userInfoService.getLocaleUserNameByUserIdLocale(userInfoEntity.getUserId(), LocaleContextHolder.getLocale().toString());
        if (StringUtils.isNotBlank(userName)) {
            userDto.setLocalUserName(userName);
        }
        return userDto;
    }

    @Override
    public UserDto getFullInternetUserByLoginName(String loginName, String loginNameHashAlgorithmCode) {
        UserAccountEntity userAccountEntity = userAccountService.getByLoginName(loginName);
        if (userAccountEntity == null) {
            return null;
        }
        UserDto userDto = userInfoService.getFullInternetUserByAccountUuid(userAccountEntity.getUuid());

        return userDto;
    }

    @Override
    public UserTypeEnum getUserTypeByLoginName(String loginName) {
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity != null) {
            return userInfoEntity.getType();
        }
        return null;
    }

    @Override
    public boolean isNotStaffUser(String loginName) {
        return !UserTypeEnum.STAFF.equals(getUserTypeByLoginName(loginName));
    }

    @Override
    public Set<String> getUserTypeAllRoles(String loginName) {
        UserTypeEnum userTypeEnum = this.getUserTypeByLoginName(loginName);

        Set<UserTypeEnum> userExtraUserTypeEnums = userExtraRoleTypeService.getUserExtraUserTypeEnums(loginName);
        if (userTypeEnum != null) {
            userExtraUserTypeEnums.add(userTypeEnum);
        }

        Set<String> userTypeRoles = new HashSet<>();
        for (UserTypeEnum typeEnum : userExtraUserTypeEnums) {
            userTypeRoles.addAll(userTypeRoleService.getUserTypeRoles(typeEnum));
        }
        return userTypeRoles;
    }

    @Override
    public Set<String> getUserTypeRoles(UserTypeEnum type) {
        if (type == null) {
            return Collections.EMPTY_SET;
        }
        return userTypeRoleService.getUserTypeRoles(type);
    }

    @Override
    public void addUser(UserDto user) {
        this.userInfoService.addUserInfo(user);
    }

    @Override
    public void modifyUser(UserDto user) {
        this.userInfoService.modifyUserInfo(user);
    }

    @Override
    public void modifyPassword(String loginName, String newPassword, String oldPassword) {
        this.userInfoService.modifyUserPassword(loginName, newPassword, oldPassword);
    }

    @Override
    public UserDto getUserByExtLogin(String extLoginName, String extLoginType) {
        UserAccountEntity userAccountEntity = userAccountService.getByExtLoginInfo(extLoginName, extLoginType);
        if (userAccountEntity == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        UserInfoEntity userInfoEntity = userInfoService.getUserInfoByAcctUuid(userAccountEntity.getUuid());
        BeanUtils.copyProperties(userInfoEntity, userDto);
        BeanUtils.copyProperties(userAccountEntity, userDto);
        return userDto;
    }

    @Override
    public Set<String> getUserTypeRolesByLoginName(String loginName) {
        return this.getUserTypeRoles(this.getUserTypeByLoginName(loginName));
    }

    @Override
    public void expiredUser(String loginName) {
        userInfoService.expiredUser(loginName);
    }

    @Override
    public void deleteByLoginName(String loginName) {
        UserAccountEntity userAccountEntity = userAccountService.getByLoginName(loginName);
        if (userAccountEntity != null) {
            userAccountService.delete(userAccountEntity);
        }
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity != null) {
            userInfoService.delete(userInfoEntity);
        }
    }

    @Override
    public boolean isExist(String loginName) {
        return userInfoService.isExist(loginName);
    }

    @Override
    public void addUserExtraRoleType(String loginName) {
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity == null) {
            throw new BusinessException("没有该用户名");
        } else {
            if (UserTypeEnum.INDIVIDUAL.equals(userInfoEntity.getType())) {
                userExtraRoleTypeService.saveUserExtraRoleType(loginName, UserTypeEnum.LEGAL_PERSION);
            }
        }
    }

    @Override
    public void deleteUserExtraRoleType(String loginName) {
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity == null) {
            throw new BusinessException("没有该用户名");
        } else {
            if (UserTypeEnum.INDIVIDUAL.equals(userInfoEntity.getType())) {
                userExtraRoleTypeService.deleteUserExtraRoleType(loginName, UserTypeEnum.LEGAL_PERSION);
            }
        }
    }

    @Override
    public boolean isExistByLoginNameAndAccountType(String loginName, UserAccountEntity.Type type) {
        return userAccountService.existAccountByLoginName(loginName, type);
    }

    @Override
    public UserAccountEntity getUserAccountByLoginName(String loginName) {
        return userAccountService.getByLoginName(loginName);
    }

    @Override
    public List<String> getUserRolesByUserId(String userId) {
        UserInfoEntity userInfoEntity = userInfoService.getByUserId(userId);
        return userInfoEntity != null ? userRoleService.getRolesByUserUuid(userInfoEntity.getUuid()) : null;
    }

    @Override
    @Transactional
    public void updateLastLoginTime(String loginName) {
        if (StringUtils.isNotBlank(loginName)) {
            Map<String, Object> param = Maps.newHashMap();
            param.put("loginName", loginName);
            param.put("time", new Date());
            userAccountService.updateByHQL("update UserAccountEntity set lastLoginTime = :time , passwordErrorNum = 0  where loginName=:loginName", param);
        }
    }

    @Override
    public void saveUserLoginLog(UserLoginLogEntity log) {
        userAccountService.saveUserLoginLog(log);
    }

    @Override
    public UserAcctPasswordRules getUserAcctPasswordRules() {
        return userAccountService.getUserAcctPasswordRules();
    }

    @Override
    public int updateAcctPasswordErrorNum(String loginName) {
        return userAccountService.updateAcctPasswordErrorNum(loginName);
    }

    @Override
    public void unlockUserAccountHaveReachedUnlockTime() {
        userAccountService.unlockUserAccountHaveReachedUnlockTime();
    }

    @Override
    public void notifyUserAccountPasswordAreAboutToExpired() {
        userAccountService.notifyUserAccountPasswordAreAboutToExpired();
    }


}
