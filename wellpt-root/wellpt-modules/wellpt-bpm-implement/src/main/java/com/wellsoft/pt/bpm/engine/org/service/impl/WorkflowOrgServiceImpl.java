/*
 * @(#)7/17/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.PropertyElement;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.dto.OrgRoleDto;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.dto.OrganizationDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 如何描述该类
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
@Service
public class WorkflowOrgServiceImpl implements WorkflowOrgService {
    @Autowired
    private OrgFacadeService orgFacadeService;

    /**
     * 获取系统可用的组织
     *
     * @param system
     * @return
     */
    @Override
    public List<OrganizationDto> listOrganizationBySystem(String system) {
        return orgFacadeService.listOrganizationBySystem(system);
    }

    /**
     * 根据组织ID获取组织信息
     *
     * @param orgId
     * @return
     */
    @Override
    public OrganizationDto getOrganizationById(String orgId) {
        return orgFacadeService.getOrganizationById(orgId);
    }

    @Override
    public List<OrganizationDto> listOrganizationByOrgUuids(List<Long> orgUuids) {
        return orgFacadeService.listOrganizationByOrgUuids(orgUuids);
    }

    /**
     * 根据组织UUID获取组织角色
     *
     * @param orgId
     * @return
     */
    @Override
    public List<OrgRoleDto> listOrgRoleByOrgId(String orgId) {
        return orgFacadeService.listOrgRoleByOrgId(orgId);
    }

    /**
     * 获取组织版本ID
     *
     * @param flowDelegate
     * @return
     */
    @Override
    public String getOrgVersionId(FlowDelegate flowDelegate) {
        String orgVersionId = null;
        String orgId = null;
        String system = RequestSystemContextPathResolver.system();
        PropertyElement propertyElement = flowDelegate.getFlow().getProperty();
        // 默认组织版本
        if (propertyElement.getIsUseDefaultOrg()) {
            OrgVersionEntity orgVersionEntity = orgFacadeService.getDefaultOrgVersionBySystem(system);
            if (orgVersionEntity != null) {
                orgVersionId = orgVersionEntity.getId();
            }
        } else {
            // 指定组织版本
            orgId = propertyElement.getOrgId();
            if (StringUtils.isNotBlank(orgId)) {
                OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgId(orgId);
                if (orgVersionEntity != null) {
                    orgVersionId = orgVersionEntity.getId();
                }
            }
        }
        return orgVersionId;
    }

    /**
     * 根据组织ID获取对应的组织版本ID
     *
     * @param orgId
     * @return
     */
    @Override
    public String getOrgVersionIdByOrgId(String orgId) {
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgId(orgId);
        return orgVersionEntity != null ? orgVersionEntity.getId() : null;
    }

    @Override
    public String getOrgVersionIdByOrgUuid(Long orgUuid) {
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        return orgVersionEntity != null ? orgVersionEntity.getId() : null;
    }

    /**
     * 根据组织版本ID获取对应的组织ID
     *
     * @param orgVersionId
     * @return
     */
    @Override
    public String getOrgIdByOrgVersionId(String orgVersionId) {
        OrganizationDto organizationDto = orgFacadeService.getOrganizationByOrgVersionId(orgVersionId);
        return organizationDto != null ? organizationDto.getId() : null;
    }

    @Override
    public String getOrgIdByBizOrgId(String bizOrgId) {
        OrganizationDto organizationDto = orgFacadeService.getOrganizationByBizOrgId(bizOrgId);
        return organizationDto != null ? organizationDto.getId() : null;
    }

    @Override
    public List<String> listOrgIdByBizOrgIds(List<String> bizOrgIds) {
        return orgFacadeService.listOrgIdByBizOrgIds(bizOrgIds);
    }

    /**
     * 根据组织版本ID，获取最新的组织版本ID
     *
     * @param orgVersionId
     * @return
     */
    @Override
    public String getLatestOrgVersionId(String orgVersionId) {
        return orgFacadeService.getLatestOrgVersionIdByOrgVersionId(orgVersionId);
    }

    /**
     * 根据组织ID列表，获取组织版本ID列表
     *
     * @param orgIds
     * @return
     */
    @Override
    public List<String> listOrgVersionIdsByOrgIds(List<String> orgIds) {
        List<String> orgVersionIds = Lists.newArrayList();
        List<OrgVersionEntity> entities = orgFacadeService.listOrgVersionByOrgIds(orgIds);
        entities.forEach(entity -> {
            orgVersionIds.add(entity.getId());
        });
        return orgVersionIds;
    }

    @Override
    public List<String> listOrgVersionIdsByOrgIdsAndSystem(List<String> orgIds, String system) {
        List<String> orgVersionIds = Lists.newArrayList();
        List<OrgVersionEntity> entities = orgFacadeService.listOrgVersionByOrgIdsAndSystem(orgIds, system);
        entities.forEach(entity -> {
            orgVersionIds.add(entity.getId());
        });
        return orgVersionIds;
    }

    @Override
    public List<OrgVersionEntity> listOrgVersionByOrgVersionIds(Set<String> orgVersionIds) {
        return orgFacadeService.listOrgVersionByOrgVersionIds(orgVersionIds);
    }

    @Override
    public Long getBizOrgUuidByBizOrgId(String bizOrgId) {
        return orgFacadeService.getBizOrgUuidByBizOrgId(bizOrgId);
    }

    @Override
    public List<BizOrganizationEntity> listBizOrganizationByBizOrgUuids(Set<Long> bizOrgUuids) {
        return orgFacadeService.listBizOrganizationByBizOrgUuids(bizOrgUuids);
    }

    /**
     * 获取用户的直接领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobDirectLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobDirectLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位的直接领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobDirectLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobDirectLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户指定职位的直接领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobDirectLeader(String userId, String jobId, String... orgVersionIds) {
        if (StringUtils.isBlank(jobId)) {
            return Collections.emptySet();
        }
        return orgFacadeService.listUserJobDirectLeader(userId, jobId, orgVersionIds);
    }

    /**
     * 根据ID获取名称
     *
     * @param id
     * @return
     */
    @Override
    public String getNameById(String id) {
        return orgFacadeService.getNameByOrgEleIds(Lists.newArrayList(id)).get(id);
    }

    /**
     * 根据ID列表获取名称
     *
     * @param ids
     * @return
     */
    @Override
    public Map<String, String> getNamesByIds(List<String> ids) {
        return orgFacadeService.getNameByOrgEleIds(ids);
    }

    /**
     * 根据ID列表，获取用户信息
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getUsersByIds(List<String> ids, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }

        Map<String, String> map = orgFacadeService.getUsersByIds(ids, orgVersionIds);
        return map;
    }

    /**
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getUsersByJobGrades(List<String> jobGrades, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(jobGrades)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getUsersByJobGrades(jobGrades, orgVersionIds);
    }

    /**
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getUsersByJobRankIds(List<String> jobRankIds, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(jobRankIds)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getUsersByJobRankIds(jobRankIds, orgVersionIds);
    }

    /**
     * 通过组织元素ID,获取该节点下的所有职位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getJobsByIds(List<String> ids, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getJobsByIds(ids, orgVersionIds);
    }

    /**
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getJobsByJobGrades(List<String> jobGrades, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(jobGrades)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getJobsByJobGrades(jobGrades, orgVersionIds);
    }

    /**
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> getJobIdsByJobGrades(List<String> jobGrades, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(jobGrades)) {
            return Collections.emptyList();
        }
        return orgFacadeService.getJobIdsByJobGrades(jobGrades, orgVersionIds);
    }

    /**
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getJobsByJobRankIds(List<String> jobRankIds, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(jobRankIds)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getJobsByJobRankIds(jobRankIds, orgVersionIds);
    }

    /**
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> getJobIdsByJobRankIds(List<String> jobRankIds, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(jobRankIds)) {
            return Collections.emptyList();
        }
        return orgFacadeService.getJobIdsByJobRankIds(jobRankIds, orgVersionIds);
    }

    /**
     * 通过组织元素ID,获取该节点下的所有部门ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getDepartmentsByIds(List<String> ids, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getDepartmentsByIds(ids, orgVersionIds);
    }

    /**
     * 通过组织元素ID,获取该节点下的所有单位ID和名称
     *
     * @param ids
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getUnitsByIds(List<String> ids, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyMap();
        }
        return orgFacadeService.getUnitsByIds(ids, orgVersionIds);
    }

    /**
     * @param ids
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByIds(List<String> ids, String... bizOrgIds) {
        return orgFacadeService.getBizOrgUsersByIds(ids, bizOrgIds);
    }

    /**
     * @param id
     * @param bizRoleId
     * @param bizOrgId
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByIdWithBizRoleId(String idWithBizRoleId, String bizOrgId) {
        return orgFacadeService.getBizOrgUsersByIdWithBizRoleId(idWithBizRoleId, bizOrgId);
    }

    /**
     * @param ids
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByIdsAndBizRoleIds(List<String> ids, List<String> bizRoleIds, String... bizOrgIds) {
        return orgFacadeService.getBizOrgUsersByIdsAndBizRoleIds(ids, bizRoleIds, bizOrgIds);
    }

    /**
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByBizRoleIds(List<String> bizRoleIds, String... bizOrgIds) {
        return orgFacadeService.getBizOrgUsersByBizRoleIds(bizRoleIds, bizOrgIds);
    }

    /**
     * @param ids
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgJobsByIds(List<String> ids, String... bizOrgIds) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param ids
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgDepartmentsByIds(List<String> ids, String... bizOrgIds) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param ids
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUnitsByIds(List<String> ids, String... bizOrgIds) {
        throw new UnsupportedOperationException();
    }

    /**
     * 获取用户所有职位的上级领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobSuperiorLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobSuperiorLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位的上级领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobSuperiorLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobSuperiorLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户指定职位的上级领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobSuperiorLeader(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobSuperiorLeader(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位的部门领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobDepartmentLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobDepartmentLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位的部门领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobDepartmentLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobDepartmentLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户指定职位的部门领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobDepartmentLeader(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobDepartmentLeader(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位的分管领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobBranchLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobBranchLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位的分管领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobBranchLeader(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobBranchLeader(userId, orgVersionIds);
    }

    /**
     * 获取用户指定职位的分管领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobBranchLeader(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobBranchLeader(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位的所有领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobLeaderOfAll(String userId, String... orgVersionIds) {
        Set<String> leaders = Sets.newLinkedHashSet();
        // 直接领导
        leaders.addAll(orgFacadeService.listUserAllJobDirectLeader(userId, orgVersionIds));
        // 所有上级负责、分管领导
        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR, OrgElementManagementEntity.LeaderType.LEADER);
        leaders.addAll(orgFacadeService.listUserAllJobLeader(userId, leaderTypes, null, true, false, orgVersionIds));
        return leaders;
    }

    /**
     * 获取用户主职位的所有领导
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobLeaderOfAll(String userId, String... orgVersionIds) {
        Set<String> leaders = Sets.newLinkedHashSet();
        // 直接领导
        leaders.addAll(orgFacadeService.listUserMainJobDirectLeader(userId, orgVersionIds));
        // 所有上级负责、分管领导
        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR, OrgElementManagementEntity.LeaderType.LEADER);
        leaders.addAll(orgFacadeService.listUserMainJobLeader(userId, leaderTypes, null, true, false, orgVersionIds));
        return leaders;
    }

    /**
     * 获取用户指定职位的所有领导
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobLeaderOfAll(String userId, String jobId, String... orgVersionIds) {
        Set<String> leaders = Sets.newLinkedHashSet();
        // 直接领导
        if (StringUtils.isNotBlank(jobId)) {
            leaders.addAll(orgFacadeService.listUserJobDirectLeader(userId, jobId, orgVersionIds));
        }
        // 所有上级负责、分管领导
        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR, OrgElementManagementEntity.LeaderType.LEADER);
        leaders.addAll(orgFacadeService.listUserJobLeader(userId, jobId, leaderTypes, null, true, false, orgVersionIds));
        return leaders;
    }

    /**
     * 获取用户所有职位所属部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobDepartmentUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobDepartmentUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位所属部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobDepartmentUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobDepartmentUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位所属部门的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobDepartmentUserId(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobDepartmentUserId(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位所属上级部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobParentDepartmentUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobParentDepartmentUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位所属上级部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobParentDepartmentUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobParentDepartmentUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户职位所属上级部门的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobParentDepartmentUserId(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobParentDepartmentUserId(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位根部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobRootDepartmentUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobRootDepartmentUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位根部门的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobRootDepartmentUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobRootDepartmentUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户指定职位根部门的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobRootDepartmentUserId(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobRootDepartmentUserId(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位根节点的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobRootNodeUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobRootNodeUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户主职位根节点的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobRootNodeUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobRootNodeUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户指定职位根节点的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobRootNodeUserId(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobRootNodeUserId(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户所有职位所在单位的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobUnitUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserAllJobUnitUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户所有职位所在单位的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobUnitUserId(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserMainJobUnitUserId(userId, orgVersionIds);
    }

    /**
     * 获取用户所有职位所在单位的所有人员ID
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobUnitUserId(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserJobUnitUserId(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户同业务角色的所有人员ID
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
//    @Override
//    public Set<String> listUserSameBizRoleUserId(String userId, String... orgVersionIds) {
//        return orgFacadeService.listUserSameBizRoleUserId(userId, orgVersionIds);
//    }

    /**
     * 获取用户指定职位类型的直接下属
     *
     * @param userId
     * @param jobTypes
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserSubordinateUserIds(String userId, List<OrgUserEntity.Type> jobTypes, String... orgVersionIds) {
        return orgFacadeService.listUserSubordinateUserIds(userId, jobTypes, orgVersionIds);
    }

    /**
     * 获取用户指定职位的直接下属
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserSubordinateUserIds(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserSubordinateUserIds(userId, jobId, orgVersionIds);
    }

    /**
     * 获取用户指定职位类型的所有下属
     *
     * @param userId
     * @param jobTypes
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllSubordinateUserIds(String userId, List<OrgUserEntity.Type> jobTypes, String... orgVersionIds) {
        return orgFacadeService.listUserAllSubordinateUserIds(userId, jobTypes, orgVersionIds);
    }

    /**
     * 获取用户指定职位的所有下属
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllSubordinateUserIds(String userId, String jobId, String... orgVersionIds) {
        return orgFacadeService.listUserAllSubordinateUserIds(userId, jobId, orgVersionIds);
    }

    /**
     * 获取组织元素下对应的组织角色人员
     *
     * @param eleId
     * @param orgRoleId
     * @param orgId
     * @return
     */
    @Override
    public Set<String> listUserIdWithOrgRoleIdAndOrgId(String eleId, String orgRoleId, String orgId) {
        return orgFacadeService.listUserIdWithOrgRoleIdAndOrgId(eleId, orgRoleId, orgId);
    }

    /**
     * 根据用户ID获取用户职位
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgUserJobDto> listUserJobs(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserJobs(userId, orgVersionIds);
    }

    @Override
    public List<OrgUserDto> listOrgUser(String userId, String... orgVersionIds) {
        return orgFacadeService.listOrgUser(userId, orgVersionIds);
    }

    @Override
    public List<OrgUserDto> listOrgUser(List<String> userIds, String... orgVersionIds) {
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        return orgFacadeService.listOrgUser(userIds, orgVersionIds);
    }

    @Override
    public List<OrgUserJobDto> listUserJobIdentity(String userId, Token token) {
        String[] orgVersionIds = OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token);
        Map<String, List<String>> orgIdBizOrgIdMap = OrgVersionUtils.getAvailableFlowOrgIdBizOrgIdMap(token);
        if (enabledBizOrg(orgIdBizOrgIdMap)) {
            List<OrgUserJobDto> orgUserJobDtos = orgFacadeService.listUserJobIdentity(userId, true, orgVersionIds);
            return filterByAvailableBizOrg(orgUserJobDtos, orgIdBizOrgIdMap, token);
        } else {
            return orgFacadeService.listUserJobIdentity(userId, false, orgVersionIds);
        }
    }

    private List<OrgUserJobDto> filterByAvailableBizOrg(List<OrgUserJobDto> orgUserJobDtos, Map<String, List<String>> orgIdBizOrgIdMap, Token token) {
        Set<Long> bizOrgUuids = orgUserJobDtos.stream().filter(orgUserJob -> orgUserJob.getBizOrgUuid() != null).flatMap(orgUserJob -> Stream.of(orgUserJob.getBizOrgUuid())).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(bizOrgUuids)) {
            return orgUserJobDtos;
        }

        List<BizOrganizationEntity> bizOrganizationEntities = this.listBizOrganizationByBizOrgUuids(bizOrgUuids);
        List<Long> orgUuids = bizOrganizationEntities.stream().map(BizOrganizationEntity::getOrgUuid).collect(Collectors.toList());
        List<OrganizationDto> organizationDtos = this.listOrganizationByOrgUuids(orgUuids);
        Map<Long, BizOrganizationEntity> bizOrgMap = ConvertUtils.convertElementToMap(bizOrganizationEntities, "uuid");
        Map<Long, OrganizationDto> orgMap = ConvertUtils.convertElementToMap(organizationDtos, "uuid");
        return orgUserJobDtos.stream().filter(orgUserJob -> {
            Long bizOrgUuid = orgUserJob.getBizOrgUuid();
            if (bizOrgUuid == null) {
                return true;
            }
            BizOrganizationEntity bizOrganizationEntity = bizOrgMap.get(bizOrgUuid);
            if (bizOrganizationEntity == null) {
                return false;
            }
            OrganizationDto organizationDto = orgMap.get(bizOrganizationEntity.getOrgUuid());
            if (organizationDto == null) {
                return false;
            }
            String orgId = organizationDto.getId();
            List<String> bizOrgIds = orgIdBizOrgIdMap.get(orgId);
            if (bizOrgIds == null) {
                return false;
            }
            return CollectionUtils.isEmpty(bizOrgIds) || bizOrgIds.contains(bizOrganizationEntity.getId());
        }).collect(Collectors.toList());
    }

    private boolean enabledBizOrg(Map<String, List<String>> bizOrgIdMap) {
        for (Map.Entry<String, List<String>> entry : bizOrgIdMap.entrySet()) {
            List<String> bizOrgIds = entry.getValue();
            if (CollectionUtils.isEmpty(bizOrgIds) || !bizOrgIds.contains("disabled")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<OrgUserJobDto> listUserJobIdentity(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserJobIdentity(userId, true, orgVersionIds);
    }

    @Override
    public List<OrgUserJobDto> listUserJobIdentity(String userId, boolean includeBizRole, String... orgVersionIds) {
        return orgFacadeService.listUserJobIdentity(userId, includeBizRole, orgVersionIds);
    }

    /**
     * 根据用户ID获取用户职级
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<MultiOrgDuty> listUserDutys(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserDutys(userId, orgVersionIds);
    }

    @Override
    public List<JobRankLevelVo> listUserJobRankLevels(String userId, String... orgVersionIds) {
        return orgFacadeService.listUserJobRankLevels(userId, orgVersionIds);
    }

    /**
     * 判断ID是否是memberOf中的成员
     *
     * @param id
     * @param memberOf
     * @param orgVersionIds
     * @return
     */
    @Override
    public boolean isMemberOf(String id, String memberOf, String... orgVersionIds) {
        return orgFacadeService.isMemberOf(id, new String[]{memberOf}, orgVersionIds);
    }

    /**
     * 判断ID是否是memberOf中的成员
     *
     * @param id
     * @param memberOf
     * @param orgVersionIds
     * @return
     */
    @Override
    public boolean isMemberOf(String id, String[] memberOf, String... orgVersionIds) {
        return orgFacadeService.isMemberOf(id, memberOf, orgVersionIds);
    }

    /**
     * 判断ID是否是memberOf中的成员
     *
     * @param id
     * @param memberOf
     * @param orgVersionIds
     * @return
     */
    @Override
    public boolean isMemberOf(String id, List<String> memberOf, String... orgVersionIds) {
        return orgFacadeService.isMemberOf(id, memberOf.toArray(new String[0]), orgVersionIds);
    }

    @Override
    public boolean isMemberOfOrg(String id, String orgId) {
        if (StringUtils.isBlank(orgId)) {
            return false;
        }
        return orgFacadeService.isMemberOfOrg(id, orgId);
    }

    @Override
    public boolean isMemberOfBizOrg(String id, List<String> memberOf, String bizOrgId) {
        return orgFacadeService.isMemberOfBizOrg(id, memberOf.toArray(new String[0]), bizOrgId);
    }

    /**
     * @param id
     * @param bizOrgId
     * @return
     */
    @Override
    public boolean isMemberOfBizOrg(String id, String bizOrgId) {
        if (StringUtils.isBlank(bizOrgId)) {
            return false;
        }
        return orgFacadeService.isMemberOfBizOrg(id, bizOrgId);
    }

    @Override
    public List<String> filterUserIdsByBizOrgId(List<String> userIds, String bizOrgId) {
        if (CollectionUtils.isEmpty(userIds) || StringUtils.isBlank(bizOrgId)) {
            return Collections.emptyList();
        }
        return orgFacadeService.filterUserIdsByBizOrgId(userIds, bizOrgId);
    }

    /**
     * 获取当前租户管理员ID
     *
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> listCurrentTenantAdminIds(String... orgVersionIds) {
        return orgFacadeService.listCurrentTenantAdminIds(orgVersionIds);
    }

    /**
     * 获得用户相关的组织信息ID
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> getUserRelatedIds(String userId) {
        return orgFacadeService.getUserRelatedIds(userId);
    }

    /**
     * @param groupIds
     * @return
     */
    @Override
    public Set<String> listMemberIdByGroupIds(List<String> groupIds) {
        return orgFacadeService.listMemberIdByGroupIds(groupIds);
    }

    /**
     * @param groupId
     * @return
     */
    @Override
    public List<OrgGroupMemberEntity> listMemberByGroupId(String groupId) {
        return orgFacadeService.listMemberByGroupId(groupId);
    }

    /**
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Set<String> listUserBizItemAndBizRoleUserId(String userId, List<String> bizRoleIds, String... bizOrgIds) {
        return orgFacadeService.listUserBizItemAndBizRoleUserId(userId, bizRoleIds, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizDepartmentUserId(String userId, String jobIdentityId, boolean sameBizRole, String... bizOrgIds) {
        return orgFacadeService.listUserBizDepartmentUserId(userId, jobIdentityId, sameBizRole, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizDepartmentAndBizRoleUserId(String userId, List<String> bizRoleIds, String... bizOrgIds) {
        return orgFacadeService.listUserBizDepartmentAndBizRoleUserId(userId, bizRoleIds, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizParentDepartmentUserId(String userId, String jobIdentityId, String... bizOrgIds) {
        return orgFacadeService.listUserBizParentDepartmentUserId(userId, jobIdentityId, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizParentDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> roleIds, String... bizOrgIds) {
        return orgFacadeService.listUserBizParentDepartmentAndBizRoleUserId(userId, jobIdentityId, roleIds, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizRootDepartmentUserId(String userId, String jobIdentityId, String... bizOrgId) {
        return orgFacadeService.listUserBizRootDepartmentUserId(userId, jobIdentityId, bizOrgId);
    }

    @Override
    public Set<String> listUserBizRootDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> roleIds, String... bizOrgIds) {
        return orgFacadeService.listUserBizRootDepartmentAndBizRoleUserId(userId, jobIdentityId, roleIds, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizRootNodeUserId(String userId, String... bizOrgIds) {
        return orgFacadeService.listUserBizRootNodeUserId(userId, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizRootNodeAndBizRoleUserId(String userId, List<String> bizRoleIds, String... bizOrgIds) {
        return orgFacadeService.listUserBizRootNodeAndBizRoleUserId(userId, bizRoleIds, bizOrgIds);
    }

    @Override
    public Set<String> listUserBizRoleUserId(String userId, String... bizOrgIds) {
        return orgFacadeService.listUserBizRoleUserId(userId, bizOrgIds);
    }

}
