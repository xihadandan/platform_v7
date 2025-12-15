package com.wellsoft.pt.org.facade.service;

import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.dto.*;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserInfoEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 组织门面服务，封装对其他模块提供的接口
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
public interface OrgFacadeService {

    Map<String, String> getUserNamesByUserIds(List<String> userIds);

    Set<String> getOrgUserRoleUuidsByUserId(String userId);

    UserDetailsVo getUserDetailsVoByLoginName(String loginName);

    UserDetailsVo getUserDetailsVoByUserId(String userId);

    List<UserInfoEntity> getUserInfosByUserId(List<String> userIds);

    UserInfoEntity getUserInfoByUserId(String userId);

    UserInfoEntity getUserInfoByUuid(String userInfoUuid);

    /**
     * 根据用户手机号获取用户信息
     *
     * @param mobile
     * @return
     */
    UserInfoEntity getUserInfoByMobile(String mobile);

    /**
     * 根据用户登录名获取用户信息
     *
     * @param loginName
     * @return
     */
    UserInfoEntity getUserInfoByLoginName(String loginName);

    /**
     * 是否存在的登录名的账号
     *
     * @param loginName
     * @return
     */
    boolean existAccountByLoginName(String loginName);

    /**
     * 获取系统可用的组织
     *
     * @param system
     * @return
     */
    List<OrganizationDto> listOrganizationBySystem(String system);

    /**
     * 根据组织UUID列表获取组织信息
     *
     * @param orgUuids
     * @return
     */
    List<OrganizationDto> listOrganizationByOrgUuids(List<Long> orgUuids);

    /**
     * 根据组织ID获取组织信息
     *
     * @param orgId
     * @return
     */
    OrganizationDto getOrganizationById(String orgId);

    /**
     * 根据业务组织ID获取组织信息
     *
     * @param bizOrgId
     * @return
     */
    OrganizationDto getOrganizationByBizOrgId(String bizOrgId);

    /**
     * 根据组织版本ID获取对应的组织
     *
     * @param orgVersionId
     * @return
     */
    OrganizationDto getOrganizationByOrgVersionId(String orgVersionId);

    /**
     * 根据组织ID获取组织角色
     *
     * @param orgId
     * @return
     */
    List<OrgRoleDto> listOrgRoleByOrgId(String orgId);

    /**
     * 获取系统可使用的组织版本
     *
     * @param system
     * @return
     */
    List<OrgVersionEntity> listOrgVersionBySystem(String system, String tenant);

    /**
     * 获取默认组织使用的组织版本
     *
     * @param system
     * @return
     */
    OrgVersionEntity getDefaultOrgVersionBySystem(String system);

    /**
     * 获取指定组织使用的组织版本
     *
     * @param orgId
     * @return
     */
    OrgVersionEntity getOrgVersionByOrgId(String orgId);

    /**
     * 获取指定组织使用的组织版本
     *
     * @param orgUuid
     * @return
     */
    OrgVersionEntity getOrgVersionByOrgUuid(Long orgUuid);

    /**
     * 根据组织版本ID，获取最新的组织版本ID
     *
     * @param orgVersionId
     * @return
     */
    String getLatestOrgVersionIdByOrgVersionId(String orgVersionId);


    /**
     * 根据组织ID列表，获取组织版本列表
     *
     * @param orgIds
     * @return
     */
    List<OrgVersionEntity> listOrgVersionByOrgIds(List<String> orgIds);

    /**
     * 根据组织ID列表、归属系统，获取组织版本列表
     *
     * @param orgIds
     * @param system
     * @return
     */
    List<OrgVersionEntity> listOrgVersionByOrgIdsAndSystem(List<String> orgIds, String system);

    /**
     * 根据组织版本ID列表，获取组织版本列表
     *
     * @param orgVersionIds
     * @return
     */
    List<OrgVersionEntity> listOrgVersionByOrgVersionIds(Set<String> orgVersionIds);

    /**
     * 根据业务组织ID，获取业务组织UUID
     *
     * @param bizOrgId
     * @return
     */
    Long getBizOrgUuidByBizOrgId(String bizOrgId);

    /**
     * @param bizOrgUuids
     * @return
     */
    List<BizOrganizationEntity> listBizOrganizationByBizOrgUuids(Set<Long> bizOrgUuids);

    /**
     * 根据组织元素ID列表，获取对应的名称
     *
     * @param orgEleIds
     * @return
     */
    Map<String, String> getNameByOrgEleIds(List<String> orgEleIds);

    Map<String, String> getNamePathByOrgEleIds(List<String> orgEleIds);

    /**
     * 根据用户ID、组织版本ID，获取用户的直接汇报人
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobDirectLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位的直接汇报人
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobDirectLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位的直接汇报人
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobDirectLeader(String userId, String jobId, String... orgVersionIds);

    /**
     * 根据ID列表，获取用户信息
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getUsersByIds(List<String> ids, String... orgVersionIds);

    /**
     * 根据职等列表，获取用户信息
     *
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getUsersByJobGrades(List<String> jobGrades, String... orgVersionIds);

    /**
     * 根据职级ID列表，获取用户信息
     *
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getUsersByJobRankIds(List<String> jobRankIds, String... orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有职位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getJobsByIds(List<String> ids, String... orgVersionIds);

    /**
     * 通过职等列表,获取所有职位ID和名称
     *
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getJobsByJobGrades(List<String> jobGrades, String... orgVersionIds);

    /**
     * 通过职等列表,获取所有职位ID
     *
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    List<String> getJobIdsByJobGrades(List<String> jobGrades, String... orgVersionIds);

    /**
     * 通过职级ID列表,获取所有职位ID和名称
     *
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getJobsByJobRankIds(List<String> jobRankIds, String... orgVersionIds);

    /**
     * 通过职级ID列表,获取所有职位ID
     *
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    List<String> getJobIdsByJobRankIds(List<String> jobGrades, String... orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有部门ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getDepartmentsByIds(List<String> ids, String... orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有单位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getUnitsByIds(List<String> ids, String... orgVersionIds);

    /**
     * 通过组织元素ID,获取该节点下的所有组织ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    Map<String, String> getOrgElementsByIds(List<String> ids, String... orgVersionIds);

    /**
     * 获取用户所有职位的上级领导
     *
     * @param userId
     * @param orgVersionIds
     */
    Set<String> listUserAllJobSuperiorLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位的上级领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobSuperiorLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位的上级领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobSuperiorLeader(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位的部门领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobDepartmentLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位的部门领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobDepartmentLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位的部门领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobDepartmentLeader(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位的分管领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobBranchLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位的分管领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobBranchLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位的分管领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobBranchLeader(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位的领导
     *
     * @param userId
     * @param leaderTypes
     * @param eleIdTypes
     * @param getAll
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobLeader(String userId, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                     List<String> eleIdTypes, boolean getAll, boolean includeSelf, String... orgVersionIds);

    /**
     * 获取用户主职位的领导
     *
     * @param userId
     * @param leaderTypes
     * @param eleIdTypes
     * @param getAll
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobLeader(String userId, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                      List<String> eleIdTypes, boolean getAll, boolean includeSelf, String... orgVersionIds);

    /**
     * 获取用户指定职位的领导
     *
     * @param userId
     * @param leaderTypes
     * @param eleIdTypes
     * @param getAll
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobLeader(String userId, String jobId, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                  List<String> eleIdTypes, boolean getAll, boolean includeSelf, String... orgVersionIds);

    /**
     * 获取用户所有职位所属部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobDepartmentUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位所属部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobDepartmentUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位所属部门的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobDepartmentUserId(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位所属上级部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobParentDepartmentUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位所属上级部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobParentDepartmentUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户职位所属上级部门的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobParentDepartmentUserId(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位根部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobRootDepartmentUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位根部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobRootDepartmentUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位根部门的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobRootDepartmentUserId(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位根节点的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobRootNodeUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位根节点的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobRootNodeUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位根节点的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobRootNodeUserId(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户所有职位所在单位的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobUnitUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户所有职位所在单位的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobUnitUserId(String userId, String... orgVersionIds);

    /**
     * 获取用户所有职位所在单位的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobUnitUserId(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    // Set<String> listUserSameBizRoleUserId(String userId, String... orgVersionIds);


    /**
     * 获取用户指定职位类型的直接下属
     *
     * @param userId
     * @param jobTypes
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserSubordinateUserIds(String userId, List<OrgUserEntity.Type> jobTypes, String... orgVersionIds);

    /**
     * 获取用户指定职位的直接下属
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserSubordinateUserIds(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取用户指定职位类型的所有下属
     *
     * @param userId
     * @param jobTypes
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllSubordinateUserIds(String userId, List<OrgUserEntity.Type> jobTypes, String... orgVersionIds);

    /**
     * 获取用户指定职位的所有下属
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllSubordinateUserIds(String userId, String jobId, String... orgVersionIds);

    /**
     * 获取组织元素下对应的组织角色人员
     *
     * @param eleId
     * @param orgRoleId
     * @param orgId
     * @return
     */
    Set<String> listUserIdWithOrgRoleIdAndOrgId(String eleId, String orgRoleId, String orgId);

    /**
     * 根据用户ID获取用户职位
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<OrgUserJobDto> listUserJobs(String userId, String... orgVersionIds);

    /**
     * 根据用户ID获取用户部门、职位
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<OrgUserDto> listOrgUser(String userId, String[] orgVersionIds);

    /**
     * 根据用户ID获取用户部门、职位
     *
     * @param userIds
     * @param orgVersionIds
     * @return
     */
    List<OrgUserDto> listOrgUser(List<String> userIds, String[] orgVersionIds);

    /**
     * 根据用户ID获取用户身份
     *
     * @param userId
     * @param includeBizRole
     * @param orgVersionIds
     * @return
     */
    List<OrgUserJobDto> listUserJobIdentity(String userId, boolean includeBizRole, String[] orgVersionIds);

    /**
     * 根据用户ID获取用户职级
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<MultiOrgDuty> listUserDutys(String userId, String[] orgVersionIds);

    List<JobRankLevelVo> listUserJobRankLevels(String userId, String[] orgVersionIds);

    /**
     * 判断ID是否是memberOf中的成员
     *
     * @param id
     * @param memberOf
     * @param orgVersionIds
     * @return
     */
    boolean isMemberOf(String id, String[] memberOf, String... orgVersionIds);

    /**
     * 判断ID是否是行政组织中的成员
     *
     * @param id
     * @param orgId
     * @return
     */
    boolean isMemberOfOrg(String id, String orgId);

    /**
     * 判断ID是否是memberOf中的业务组织成员
     *
     * @param id
     * @param bizOrgId
     * @return
     */
    boolean isMemberOfBizOrg(String id, String[] memberOf, String bizOrgId);

    /**
     * 判断ID是否是业务组织中的成员
     *
     * @param id
     * @param bizOrgId
     * @return
     */
    boolean isMemberOfBizOrg(String id, String bizOrgId);

    /**
     * 过滤业务组织下的用户ID
     *
     * @param userIds
     * @param bizOrgId
     * @return
     */
    List<String> filterUserIdsByBizOrgId(List<String> userIds, String bizOrgId);

    /**
     * 获取租户管理员ID列表
     *
     * @param orgVersionIds
     * @return
     */
    List<String> listCurrentTenantAdminIds(String... orgVersionIds);

    /**
     * 获得用户相关的组织信息ID
     *
     * @param userId
     * @return
     */
    Set<String> getUserRelatedIds(String userId);

    /**
     * 权限角色删除处理
     *
     * @param roleUuid
     */
    void deleteRelaRoleByRoleUuid(String roleUuid);

    /**
     * 添加权限角色成员
     *
     * @param roleUuid
     * @param memberIds
     */
    void addRelaRoleMembers(String roleUuid, List<String> memberIds);

    void deleteRoleRelaUsers(String roleUuid, List<String> userUuids);

    /**
     * 根据角色信息获取相关成员信息
     *
     * @param roleUuid
     * @return
     */
    OrgRelaRoleMembersDto getRelaRoleMembersByRoleUuid(String roleUuid);

    void deleteRelaRoleMembers(String roleUuid, List<String> memberIds);

    List<OrgUserEntity> getAllOrgUserUnderDefaultPublishedOrgVersion(String userId, String system, String tenant);

    List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, String system, String tenant);


    OrgElementEntity getOrgElementByIdVersionUuid(String id, Long orgVersionUuid);

    OrgElementEntity getOrgElementByIdAndOrgVersionId(String id, String orgVersionId);

    OrgElementEntity getOrgElementByUuid(Long orgElementUuid);

    String getOrgElementPathByUuid(Long orgElementUuid);

    String getOrgElementPathById(String orgElementId);

    boolean existSystemOrgUser(String userId, String system, String tenant);

    Map<String, String> getUserIdNamesByOrgElementIds(List<String> recipients);

    /**
     * 根据群组ID列表获取成员ID
     *
     * @param groupIds
     * @return
     */
    Set<String> listMemberIdByGroupIds(List<String> groupIds);

    /**
     * 根据群组ID获取成员列表
     *
     * @param groupId
     * @return
     */
    List<OrgGroupMemberEntity> listMemberByGroupId(String groupId);

    /**
     * 获取租户管理员
     *
     * @param system
     * @param tenant
     * @return
     */
    UserInfoEntity getTenantManagerInfo(String system, String tenant);

    /**
     * 根据ID列表，获取业务组织的用户信息
     *
     * @param ids
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUsersByIds(List<String> ids, String[] bizOrgIds);

    /**
     * 根据元素ID/业务角色ID，获取业务组织的用户信息
     *
     * @param idWithBizRoleId
     * @param bizOrgId
     * @return
     */
    Map<String, String> getBizOrgUsersByIdWithBizRoleId(String idWithBizRoleId, String bizOrgId);

    /**
     * 根据ID列表、业务角色ID列表、业务组织ID，获取业务组织的用户信息
     *
     * @param ids
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUsersByIdsAndBizRoleIds(List<String> ids, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据业务角色ID列表、业务组织ID，获取业务组织的用户信息
     *
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUsersByBizRoleIds(List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的同业务项人员
     *
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizItemAndBizRoleUserId(String userId, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param sameBizRole
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizDepartmentUserId(String userId, String jobIdentityId, boolean sameBizRole, String[] bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的业务部门人员
     *
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizDepartmentAndBizRoleUserId(String userId, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的上级业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizParentDepartmentUserId(String userId, String jobIdentityId, String[] bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的上级业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizParentDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的根业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootDepartmentUserId(String userId, String jobIdentityId, String[] bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的上级业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的业务根节点人员
     *
     * @param userId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootNodeUserId(String userId, String[] bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的业务根节点人员
     *
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootNodeAndBizRoleUserId(String userId, List<String> bizRoleIds, String[] bizOrgIds);

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param userId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRoleUserId(String userId, String[] bizOrgIds);


    List<OrgSelectProvider.Node> explainOrgElementIdsToNode(Set<String> ids);


    String getLocaleOrgElementPath(String orgElementId, Long orgVersionUuid, String locale);

    String getLocaleOrgElementPath(Long orgElementUuid, String locale);

    Map<Long, String> getLocaleOrgElementPaths(List<Long> orgElementUuids, String locale);

    List<OrgElementI18nEntity> getLocaleOrgElementI18nsByDataIds(List<String> dataId, String dataCode, String locale);

    List<OrgElementI18nEntity> getLocaleOrgElementI18nsByDataUuids(List<Long> dataUuid, String dataCode, String locale);


    List<OrgTreeNodeDto> getUserBizOrgRolesByOrgUuid(String userId, Long orgUuid);


    /**
     * 根据组织版本UUID，获取组织元素数量
     *
     * @param orgVersionUuid
     * @return
     */
    long countOrgElementByOrgVersionUuid(Long orgVersionUuid);

    /**
     * @param orgElementUuid
     * @return
     */
    OrgElementDto getOrgElementDetailsByUuid(Long orgElementUuid);

    /**
     * @param orgElementUuid
     * @return
     */
    List<String> listOrgElementRoleUuidByUuid(Long orgElementUuid);

    /**
     * @param orgElementUuid
     * @return
     */
    OrgElementManagementEntity getOrgElementManagementByUuid(Long orgElementUuid);

    /**
     * 保存组织元素
     *
     * @param orgElementDto
     * @return
     */
    Long saveOrgElementDetails(OrgElementDto orgElementDto);

    /**
     * 保存组织元素的领导
     *
     * @param orgElementUuid
     * @param director
     * @param leader
     * @param manager
     */
    void saveOrgElementLeader(Long orgElementUuid, String director, String leader, String manager);

    /**
     * 删除组织元素
     *
     * @param orgElementUuid
     */
    void deleteOrgElementByUuid(Long orgElementUuid);

    /**
     * 用户信息
     *
     * @param userDto
     */
    String saveUser(UserDto userDto);

    void saveUserInfo(UserInfoEntity userInfoEntity);

    /**
     * 保存用户汇报关系
     *
     * @param userId
     * @param orgElementIdReport
     * @param orgVersionUuid
     */
    void saveUserReportRelation(String userId, Map<String, List<String>> orgElementIdReport, Long orgVersionUuid);

    /**
     * 用户加入组织
     *
     * @param userIds
     * @param jobId
     * @param directReporter
     * @param orgElementId
     * @param orgVersionUuid
     */
    void joinOrgUser(List<String> userIds, String jobId, List<String> directReporter, String orgElementId, Long orgVersionUuid);

    /**
     * 用户移出组织
     *
     * @param userIds
     * @param orgVersionUuid
     */
    void removeOrgUser(List<String> userIds, Long orgVersionUuid);

    /**
     * 用户移出组织
     *
     * @param userIds
     * @param orgElementId
     * @param orgVersionUuid
     */
    void removeOrgUser(List<String> userIds, String orgElementId, Long orgVersionUuid);


    /**
     * 从组织版本创建空的组织版本
     *
     * @param orgVersionEntity
     * @param copyUser
     * @param publish
     * @return
     */
    OrgVersionEntity createEmptyOrgVersionFromOrgVersion(OrgVersionEntity orgVersionEntity, boolean copyUser, boolean publish);

    /**
     * 根据业务组织ID列表，获取组织ID列表
     *
     * @param bizOrgIds
     * @return
     */
    List<String> listOrgIdByBizOrgIds(List<String> bizOrgIds);

}
