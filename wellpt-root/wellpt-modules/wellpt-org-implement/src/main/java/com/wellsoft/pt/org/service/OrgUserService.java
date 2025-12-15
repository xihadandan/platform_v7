package com.wellsoft.pt.org.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.org.dao.impl.OrgUserDaoImpl;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.dto.OrgUserElementDto;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.entity.OrgGroupEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.entity.UserInfoEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月23日   chenq	 Create
 * </pre>
 */
public interface OrgUserService extends JpaService<OrgUserEntity, OrgUserDaoImpl, Long> {

    void deleteByOrgVersionUuid(Long orgVersionUuid);

    List<OrgUserEntity> listByOrgVersionUuid(Long orgVersionUuid);

    List<OrgUserEntity> listByOrgVersionUuidAndType(Long orgVersionUuid, OrgUserEntity.Type type);

    void deleteOrgUserByOrgVersionUuidAndType(String userId, Long orgVersionUuid, OrgUserEntity.Type type);

    List<OrgUserEntity> addOrgUser(String userId, Long orgVersionUuid, List<String> orgElementIds, OrgUserEntity.Type type);

    List<OrgUserEntity> saveUserJobs(String userId, Long orgVersionUuid, List<String> jobIds, OrgUserEntity.Type relaType);

    /**
     * 将指定用户路径前缀的用户，更新为新的组织元素下的路径
     *
     * @param orgVersionUuid
     * @param elementPathPrefix
     * @param elementPathEntity
     */
    void updateAllOrgUserByOrgVersionUuidAndElementPathPrefix(Long orgVersionUuid, String elementPathPrefix, OrgElementPathEntity elementPathEntity);

    List<OrgUserJobDto> listUserJobs(String userId, Long orgVersionUuid);

    List<OrgUserJobDto> listUserJobs(String userId, String[] orgVersionIds);

    List<OrgUserDto> listOrgUser(String userId, String[] orgVersionIds);

    List<OrgUserDto> listOrgUser(List<String> userIds, String[] orgVersionIds);

    /**
     * 删除组织路径下的用户所有职位信息
     *
     * @param orgVersionUuid
     * @param elementPathPrefix
     */
    void deleteAllOrgUserJobByOrgVersionUuidAndElementPathPrefix(Long orgVersionUuid, String elementPathPrefix);

    /**
     * 根据用户ID，组织版本ID列表，获取用户职位信息
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<OrgUserEntity> listOrgUserByUserId(String userId, String... orgVersionIds);

    List<OrgUserEntity> listOrgUserByUserIdAndOrgElementIds(String userId, List<String> orgElementIds, String... orgVersionIds);

    List<OrgUserEntity> listByUserIdsAndOrgVersionUuidAndTypes(List<String> userIds, long orgVersionUuid, List<OrgUserEntity.Type> newArrayList);

    List<OrgUserEntity> getByUserIdAndTypeAndOrgVersionUuid(String userId, OrgUserEntity.Type type, Long orgVersionUuid);

    /**
     * 获取指定用户路径下的用户ID列表
     *
     * @param userPathPrefix
     * @param orgVersionUuids
     * @return
     */
    List<String> listUserIdByUserPathPrefix(String userPathPrefix, Long[] orgVersionUuids);

    /**
     * 获取指定用户路径下的用户ID列表
     *
     * @param userPathPrefix
     * @param orgVersionIds
     * @return
     */
    List<String> listUserIdByUserPathPrefix(String userPathPrefix, String[] orgVersionIds);

    /**
     * 获取租户管理员ID列表
     *
     * @param orgVersionIds
     * @return
     */
    List<String> listCurrentTenantAdminIds(String[] orgVersionIds);

    /**
     * 判断用户ID是否在指定组织元素下
     *
     * @param userId
     * @param orgElementIds
     * @param orgVersionIds
     * @return
     */
    boolean isInOrgElement(String userId, List<String> orgElementIds, String[] orgVersionIds);

    /**
     * 判断职位ID是否在指定组织元素下
     *
     * @param jobId
     * @param orgElementIds
     * @param orgVersionIds
     * @return
     */
    boolean isJobInOrgElement(String jobId, List<String> orgElementIds, String[] orgVersionIds);

    /**
     * 获取用户相关的组织元素ID
     *
     * @param userId
     * @return
     */
    List<String> listRelatedElementId(String userId);

    Long countOrgElementUserByOrgVersionUuid(String orgElementUuid, Long orgVersionUuid);

    Long countUserByOrgVersionUuid(Long orgVersionUuid);

    Long countUserHasDeptJobByOrgVersionUuid(Long orgVersionUuid);

    void deleteOrgUser(String userId, Long orgVersionUuid);

    void addUser(OrgUserElementDto orgUserAddition);

    void removeUser(OrgUserElementDto orgUserElementDto);

    List<TreeNode> getOrgUserRolePrivilegeTree(String userId, Long orgVersionUuid);

    void saveOrgUser(List<OrgUserEntity> orgUsers, Long orgVersionUuid, String userId);

    List<UserInfoEntity> getRoleRelaUsers(String roleUuid, String system, String tenant);


    List<OrgUserEntity> getAllOrgUserUnderDefaultPublishedOrgVersion(String userId, String system, String tenant);

    List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, String system, String tenant);


    List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, List<OrgUserEntity.Type> types, String system, String tenant);

    UserDetailsVo getOrgUserDetailsByUerIdAndSystem(String userId, String system);

    List<OrgGroupEntity> getGroupsIncludeUser(String userId, String system);

    Map<String, String> getUserIdNamesByOrgElementIds(List<String> orgElementIds);

    List<OrgUserEntity> getOrgUserLikeSuffixUserPath(String suffixUserPath, Long orgVersionUuid);

    List<OrgUserEntity> listByUserIdsAndOrgUuidAndTypes(List<String> userIds, Long orgUuid, ArrayList<OrgUserEntity.Type> types);

    Long countByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid);

    void updateOrgUserPath(Map<String, String> orgElementPathMap);

    List<OrgUserEntity> listByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid);

    Long countUserBySystemAndTenant(String system, String tenant);

    UserSystemOrgDetails getUserSystemOrgDetails(String userId, String system);

    Map<String, UserSystemOrgDetails> getUserSystemOrgDetailsMap(List<String> userIds, String system);

}
