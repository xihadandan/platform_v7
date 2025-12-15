/*
 * @(#)7/17/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.org.service;

import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.dto.OrgRoleDto;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.org.entity.BizOrganizationEntity;
import com.wellsoft.pt.org.entity.OrgGroupMemberEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.entity.OrgVersionEntity;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 流程组织服务接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/17/23.1	zhulh		7/17/23		Create
 * </pre>
 * @date 7/17/23
 */
public interface WorkflowOrgService {

    /**
     * 获取系统可用的组织
     *
     * @param system
     * @return
     */
    List<OrganizationDto> listOrganizationBySystem(String system);

    /**
     * 根据组织ID获取组织信息
     *
     * @param orgId
     * @return
     */
    OrganizationDto getOrganizationById(String orgId);

    /**
     * 根据组织UUID列表获取组织信息
     *
     * @param orgUuids
     * @return
     */
    List<OrganizationDto> listOrganizationByOrgUuids(List<Long> orgUuids);

    /**
     * 根据组织UUID获取组织角色
     *
     * @param orgId
     * @return
     */
    List<OrgRoleDto> listOrgRoleByOrgId(String orgId);

    /**
     * 获取组织版本ID
     *
     * @param flowDelegate
     * @return
     */
    String getOrgVersionId(FlowDelegate flowDelegate);

    /**
     * 根据组织ID获取对应的组织版本ID
     *
     * @param orgId
     * @return
     */
    String getOrgVersionIdByOrgId(String orgId);

    /**
     * 根据组织ID获取对应的组织版本UUID
     *
     * @param orgUuid
     * @return
     */
    String getOrgVersionIdByOrgUuid(Long orgUuid);

    /**
     * 根据组织版本ID获取对应的组织ID
     *
     * @param orgVersionId
     * @return
     */
    String getOrgIdByOrgVersionId(String orgVersionId);

    /**
     * 根据业务组织ID获取对应的组织ID
     *
     * @param bizOrgId
     * @return
     */
    String getOrgIdByBizOrgId(String bizOrgId);

    /**
     * 根据业务组织ID列表获取对应的组织ID
     *
     * @param bizOrgIds
     * @return
     */
    List<String> listOrgIdByBizOrgIds(List<String> bizOrgIds);

    /**
     * 根据组织版本ID，获取最新的组织版本ID
     *
     * @param orgVersionId
     * @return
     */
    String getLatestOrgVersionId(String orgVersionId);

    /**
     * 根据组织ID列表，获取组织版本ID列表
     *
     * @param orgIds
     * @return
     */
    List<String> listOrgVersionIdsByOrgIds(List<String> orgIds);

    /**
     * 根据组织ID列表、归属系统，获取组织版本ID列表
     *
     * @param orgIds
     * @param system
     * @return
     */
    List<String> listOrgVersionIdsByOrgIdsAndSystem(List<String> orgIds, String system);

    /**
     * 根据组织ID列表，获取组织版本
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
     * 获取用户的直接领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobDirectLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位的直接领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobDirectLeader(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位的直接领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobDirectLeader(String userId, String jobId, String... orgVersionIds);

    /**
     * 根据ID获取名称
     *
     * @param id
     * @return
     */
    String getNameById(String id);

    /**
     * 根据ID列表获取名称
     *
     * @param ids
     * @return
     */
    Map<String, String> getNamesByIds(List<String> ids);

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
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    List<String> getJobIdsByJobRankIds(List<String> jobRankIds, String... orgVersionIds);

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
     * 根据ID列表，获取业务组织的用户信息
     *
     * @param ids
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUsersByIds(List<String> ids, String... bizOrgIds);

    /**
     * 根据元素ID/业务角色ID，获取业务组织的用户信息
     *
     * @param idWithBizRoleId
     * @param bizOrgId
     * @return
     */
    Map<String, String> getBizOrgUsersByIdWithBizRoleId(String idWithBizRoleId, String bizOrgId);

    /***
     * 根据ID列表、业务角色ID列表，获取业务组织的用户信息
     *
     * @param ids
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUsersByIdsAndBizRoleIds(List<String> ids, List<String> bizRoleIds, String... bizOrgIds);

    /**
     * 根据业务角色ID列表，获取业务组织的用户信息
     *
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUsersByBizRoleIds(List<String> bizRoleIds, String... bizOrgIds);

    /**
     * 通过组织元素ID,获取业务组织下的所有职位ID和名称
     *
     * @param ids
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgJobsByIds(List<String> ids, String... bizOrgIds);

    /**
     * 通过组织元素ID,获取业务组织的所有部门ID和名称
     *
     * @param ids
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgDepartmentsByIds(List<String> ids, String... bizOrgIds);

    /**
     * 通过组织元素ID,获取业务组织下的所有单位ID和名称
     *
     * @param ids
     * @param bizOrgIds
     * @return
     */
    Map<String, String> getBizOrgUnitsByIds(List<String> ids, String... bizOrgIds);

    /**
     * 获取用户所有职位的上级领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
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
     * 获取用户所有职位的所有领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserAllJobLeaderOfAll(String userId, String... orgVersionIds);

    /**
     * 获取用户主职位的所有领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserMainJobLeaderOfAll(String userId, String... orgVersionIds);

    /**
     * 获取用户指定职位的所有领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    Set<String> listUserJobLeaderOfAll(String userId, String jobId, String... orgVersionIds);

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
    // <String> listUserSameBizRoleUserId(String userId, String... orgVersionIds);

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
    List<OrgUserDto> listOrgUser(String userId, String... orgVersionIds);

    /**
     * 根据用户ID获取用户部门、职位
     *
     * @param userIds
     * @param orgVersionIds
     * @return
     */
    List<OrgUserDto> listOrgUser(List<String> userIds, String... orgVersionIds);

    /**
     * 根据用户ID获取用户身份
     *
     * @param userId
     * @param token
     * @return
     */
    List<OrgUserJobDto> listUserJobIdentity(String userId, Token token);

    /**
     * 根据用户ID获取用户身份
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<OrgUserJobDto> listUserJobIdentity(String userId, String... orgVersionIds);

    /**
     * 根据用户ID获取用户身份
     *
     * @param userId
     * @param includeBizRole
     * @param orgVersionIds
     * @return
     */
    List<OrgUserJobDto> listUserJobIdentity(String userId, boolean includeBizRole, String... orgVersionIds);

    /**
     * 根据用户ID获取用户职级
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    List<MultiOrgDuty> listUserDutys(String userId, String... orgVersionIds);

    List<JobRankLevelVo> listUserJobRankLevels(String userId, String... orgVersionIds);

    /**
     * 判断ID是否是memberOf中的成员
     *
     * @param id
     * @param memberOf
     * @param orgVersionIds
     * @return
     */
    boolean isMemberOf(String id, String memberOf, String... orgVersionIds);

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
     * 判断ID是否是memberOf中的成员
     *
     * @param id
     * @param memberOf
     * @param orgVersionIds
     * @return
     */
    boolean isMemberOf(String id, List<String> memberOf, String... orgVersionIds);

    /**
     * 判断ID是否是行政组织中的成员
     *
     * @param id
     * @param orgId
     * @return
     */
    boolean isMemberOfOrg(String id, String orgId);

    /**
     * @param id
     * @param memberOf
     * @param bizOrgId
     * @return
     */
    boolean isMemberOfBizOrg(String id, List<String> memberOf, String bizOrgId);

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
     * 获取当前租户管理员ID
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
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的同业务项人员
     *
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizItemAndBizRoleUserId(String userId, List<String> bizRoleIds, String... bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的同业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param sameBizRole
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizDepartmentUserId(String userId, String jobIdentityId, boolean sameBizRole, String... bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的同业务部门人员
     *
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizDepartmentAndBizRoleUserId(String userId, List<String> bizRoleIds, String... bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的上级业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizParentDepartmentUserId(String userId, String jobIdentityId, String... bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的上级业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param roleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizParentDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> roleIds, String... bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的根业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param bizOrgId
     * @return
     */
    Set<String> listUserBizRootDepartmentUserId(String userId, String jobIdentityId, String... bizOrgId);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的根业务部门人员
     *
     * @param userId
     * @param jobIdentityId
     * @param roleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> roleIds, String... bizOrgIds);

    /**
     * 根据用户ID、业务组织ID，获取用户的根业务节点人员
     *
     * @param userId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootNodeUserId(String userId, String... bizOrgIds);

    /**
     * 根据用户ID、业务角色ID列表、业务组织ID，获取用户的根业务节点人员
     *
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRootNodeAndBizRoleUserId(String userId, List<String> bizRoleIds, String... bizOrgIds);

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param userId
     * @param bizOrgIds
     * @return
     */
    Set<String> listUserBizRoleUserId(String userId, String... bizOrgIds);

}
