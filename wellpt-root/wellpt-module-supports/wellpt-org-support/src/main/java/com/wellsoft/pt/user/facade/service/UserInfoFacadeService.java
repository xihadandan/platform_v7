package com.wellsoft.pt.user.facade.service;

import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.UserLoginLogEntity;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;

import java.util.List;
import java.util.Set;

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
public interface UserInfoFacadeService {
    UserDto getUserByLoginName(String loginName, String loginNameHashAlgorithmCode);

    UserDto getFullInternetUserByLoginName(String loginName, String loginNameHashAlgorithmCode);

    UserTypeEnum getUserTypeByLoginName(String loginName);

    boolean isNotStaffUser(String loginName);

    /**
     * 根据互联网登录名，获取对应所有角色uuid
     *
     * @param loginName loginName
     * @return 对应所有角色uuid
     */
    Set<String> getUserTypeAllRoles(String loginName);

    Set<String> getUserTypeRoles(UserTypeEnum type);

    /**
     * 添加用户 注册用户
     *
     * @param user 用户对象
     * @return void
     **/
    void addUser(UserDto user);

    /**
     * 修改用户
     *
     * @param user 用户对象
     * @return void
     **/
    void modifyUser(UserDto user);

    void modifyPassword(String loginName, String newPassword, String oldPassword);

    UserDto getUserByExtLogin(String extLoginName, String extLoginType);

    Set<String> getUserTypeRolesByLoginName(String loginName);

    void expiredUser(String loginName);

    void deleteByLoginName(String loginName);

    boolean isExist(String loginName);

    /**
     * 根据loginName，添加额外的法人角色
     *
     * @param loginName loginName
     */
    void addUserExtraRoleType(String loginName);

    /**
     * 根据loginName，删除额外的法人角色
     *
     * @param loginName loginName
     */
    void deleteUserExtraRoleType(String loginName);

    boolean isExistByLoginNameAndAccountType(String loginName, UserAccountEntity.Type type);

    UserAccountEntity getUserAccountByLoginName(String loginName);

    List<String> getUserRolesByUserId(String userId);

    void updateLastLoginTime(String loginName);

    void saveUserLoginLog(UserLoginLogEntity log);

    UserAcctPasswordRules getUserAcctPasswordRules();

    int updateAcctPasswordErrorNum(String loginName);

    void unlockUserAccountHaveReachedUnlockTime();

    void notifyUserAccountPasswordAreAboutToExpired();
}
