package com.wellsoft.pt.user.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.user.dao.UserInfoDaoImpl;
import com.wellsoft.pt.user.dto.FullInternetUserDto;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.entity.UserNameI18nEntity;

import java.util.Collection;
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
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
public interface UserInfoService extends JpaService<UserInfoEntity, UserInfoDaoImpl, String> {

    /**
     * 添加用户 注册用户
     *
     * @param userDto 用户对象
     * @return void
     **/
    void addUserInfo(UserDto userDto);

    String saveUser(UserDto userDto);

    /**
     * 修改用户
     *
     * @param userDto 用户对象
     * @return void
     **/
    void modifyUserInfo(UserDto userDto);

    /**
     * 获取完整的互联网用户信息对象
     *
     * @param accountUuid
     * @return com.wellsoft.pt.user.dto.FullInternetUserDto
     **/
    FullInternetUserDto getFullInternetUserByAccountUuid(String accountUuid);

    UserInfoEntity getUserInfoByAcctUuid(String uuid);

    UserInfoEntity getByLoginName(String loginName);

    List<UserInfoEntity> listByLoginNames(List<String> loginNames);

    void modifyUserPassword(String loginName, String newPassword, String oldPassword);

    void expiredUser(String loginName);

    public abstract boolean isExist(String loginName);

    Map<String, String> getInternetUserNamesByLoginNames(String[] loginNames);

    Map<String, String> getUserNamesByLoginNames(String[] loginNames);

    Map<String, String> getUserNamesByUserIds(Collection<String> userIds);

    UserDto getUserDetailsByUuid(String uuid, Long orgVersionUuid);

    List<UserInfoEntity> getAllUsersByOrgVersionUuid(Long orgVersionUuid);

    void userQuit(String uuid, Long orgVersionUuid);

    void resetUserPassword(String uuid);

    void resetUserPassword(String uuid, String password);

    void deleteUser(String uuid);

    /**
     * 根据用户ID、组织元素ID、组织版本ID列表，获取用户信息
     *
     * @param userIds
     * @param eleIds
     * @param orgVersionIds
     * @return
     */
    List<UserInfoEntity> listByUserIds(Set<String> userIds, Set<String> eleIds, String[] orgVersionIds);

    /**
     * 根据用户ID、组织元素ID、组织版本ID列表，获取用户信息
     *
     * @param userIds
     * @param eleIds
     * @param orgVersionIds
     * @return
     */
    Map<String, String> listAsMapByUserIds(Set<String> userIds, Set<String> eleIds, String[] expectOrgVersionIds, String[] orgVersionIds);

    /**
     * 根据用户ID、组织元素ID、业务组织ID列表，获取用户信息
     *
     * @param userIds
     * @param eleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> listBizOrgUserAsMapByUserIds(Set<String> userIds, Set<String> eleIds, String[] bizOrgIds);

    /**
     * 组织元素ID、业务角色ID列表、业务组织ID列表，获取用户信息
     *
     * @param elementId
     * @param bizRoleId
     * @param bizOrgId
     * @return
     */
    Map<String, String> listBizOrgUserAsMapByIdAndBizRoleId(String elementId, String bizRoleId, String bizOrgId);

    /**
     * 根据用户ID、组织元素ID、业务角色ID列表、业务组织ID列表，获取用户信息
     *
     * @param userIds
     * @param eleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> listBizOrgUserAsMapByUserIdsAndBizRoleIds(Set<String> userIds, Set<String> eleIds, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据业务角色ID列表、业务组织ID列表，获取用户信息
     *
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> listBizOrgUserAsMapByBizRoleIds(List<String> bizRoleIds, String[] bizOrgIds);

    void deleteUsers(List<String> uuid);

    UserInfoEntity getByUserId(String userId);

    /**
     * 根据手机号获取用户信息
     *
     * @param mobile
     * @return
     */
    UserInfoEntity getByMobile(String mobile);

    /**
     * 根据角色UUID删除用户角色
     *
     * @param roleUuid
     */
    void deleteUserRoleByRoleUuid(String roleUuid);

    /**
     * 根据用户ID列表、角色UUID添加用户角色
     *
     * @param userIds
     * @param roleUuid
     */
    void addUserRoleByIdsAndRoleUuid(List<String> userIds, String roleUuid);

    /**
     * 根据权限角色UUID获取用户
     *
     * @param roleUuid
     * @return
     */
    List<UserInfoEntity> listByRoleUuid(String roleUuid);

    void deleteUserRoleByRoleUuidAndUserIds(String roleUuid, List<String> userIds);

    Select2QueryData queryUserOptionsUnderOrgVersion(Long orgVersionUuid);

    List<UserInfoEntity> getUsersLikeUserNameAndPinyin(String keyword, int pageSize);

    String getUserAvatar(String userId);

    void updateUserWorkState(String userId, String workState);

    boolean checkPassword(String password);

    List<UserInfoEntity> getUserInfosByUserId(List<String> userIds);

    List<UserInfoEntity> getTenantManagerInfo(String system, String tenant);

    List<UserNameI18nEntity> getUserNameI18nsByUserUuid(String userUuid);

    List<UserNameI18nEntity> getUserNameI18nsById(String userId);

    String getLocaleUserNameByUserIdLocale(String userId, String locale);

    void saveUserReportRelation(String userId, Map<String, List<String>> orgElementIdReport, Long orgVersionUuid);

    boolean checkUserExist(UserInfoEntity userInfoEntity);
}
