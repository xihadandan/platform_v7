package com.wellsoft.pt.org.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.entity.MultiOrgDuty;
import com.wellsoft.pt.multi.org.service.MultiOrgDutyService;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.org.dto.*;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.service.UserAccountService;
import com.wellsoft.pt.user.service.UserInfoService;
import com.wellsoft.pt.user.service.UserRoleService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description: 组织门面服务实现类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年11月09日   chenq	 Create
 * </pre>
 */
@Service
public class OrgFacadeServiceImpl implements OrgFacadeService {

    @Autowired
    OrganizationService organizationService;

    @Autowired
    OrgElementService orgElementService;

    @Autowired
    OrgElementModelService orgElementModelService;

    @Autowired
    OrgElementRoleRelaService orgElementRoleRelaService;

    @Autowired
    OrgElementManagementService orgElementManagementService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    OrgUserService orgUserService;

    @Autowired
    OrgGroupService orgGroupService;

    @Autowired
    OrgGroupRoleService orgGroupRoleService;

    @Autowired
    OrgVersionService orgVersionService;

    @Autowired
    OrgElementRoleMemberService orgElementRoleMemberService;

    @Autowired
    OrgElementPathService orgElementPathService;

    @Autowired
    private OrgElementI18nService orgElementI18nService;

    @Autowired
    OrgElementPathChainService orgElementPathChainService;

    @Autowired
    OrgRoleService orgRoleService;

    @Autowired
    OrgUserReportRelationService orgUserReportRelationService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    BizOrganizationService bizOrganizationService;

    @Autowired
    BizOrgElementService bizOrgElementService;

    @Autowired
    BizOrgElementMemberService bizOrgElementMemberService;

    @Autowired
    MultiOrgDutyService multiOrgDutyService;

    @Autowired
    MultiOrgJobDutyService multiOrgJobDutyService;

    @Override
    public Map<String, String> getUserNamesByUserIds(List<String> userIds) {
        return userInfoService.getUserNamesByUserIds(userIds);
    }

    @Override
    public Set<String> getOrgUserRoleUuidsByUserId(String userId) {
        // 获取用户ID对应的可用组织用户角色UUID集合
        List<QueryItem> queryItems = orgElementRoleRelaService.queryRoleAndUserPaths(userId);
        Set<String> orgElementIds = Sets.newHashSet();
        Set<String> roleUuids = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(queryItems)) {
            for (QueryItem item : queryItems) {
                orgElementIds.addAll(Arrays.asList(item.getString("userPath").split(Separator.SLASH.getValue())));
                roleUuids.add(item.getString("roleUuid"));
            }
        }

        orgElementIds.add(userId);

        List<OrgUserEntity> orgUserEntities = orgUserService.getAllOrgUserUnderPublishedOrgVersion(userId, null, null);
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            for (OrgUserEntity u : orgUserEntities) {
                if (StringUtils.isNotBlank(u.getUserPath())) {
                    orgElementIds.addAll(Arrays.asList(u.getUserPath().split(Separator.SLASH.getValue())));
                }
            }
        }
        // 查询群组成员关联的角色
        roleUuids.addAll(orgGroupRoleService.queryRoleUuidsByGroupMemberIds(Lists.newArrayList(orgElementIds)));

        // 查询业务组织关联的角色
        Set<String> memRoleUuids = bizOrgElementService.getBizOrgElementAuthRoleUuidsByUserId(userId);
        roleUuids.addAll(memRoleUuids);
        return roleUuids;
    }

    @Override
    public UserDetailsVo getUserDetailsVoByLoginName(String loginName) {
        UserInfoEntity userInfoEntity = userInfoService.getByLoginName(loginName);
        if (userInfoEntity != null) {
            return getUserDetailsVo(userInfoEntity);
        }
        return null;
    }

    /**
     * @param userInfoEntity
     * @return
     */
    private UserDetailsVo getUserDetailsVo(UserInfoEntity userInfoEntity) {
        UserDetailsVo vo = new UserDetailsVo();
        vo.setLoginName(userInfoEntity.getLoginName());
        vo.setUserName(userInfoEntity.getUserName());
        vo.setAvatar(userInfoEntity.getAvatar());
        vo.setUserId(userInfoEntity.getUserId());
        vo.setUuid(userInfoEntity.getUuid());

        UserAccountEntity accountEntity = userAccountService.getOne(userInfoEntity.getAccountUuid());
        vo.setAccountNonExpired(accountEntity.getIsAccountNonExpired());
        vo.setAccountNonLocked(accountEntity.getIsAccountNonLocked());
        vo.setCredentialsNonExpired(accountEntity.getIsCredentialsNonExpired());
        vo.setEnabled(accountEntity.getIsEnabled());
        vo.setUserType(accountEntity.getType().ordinal());
        return vo;
    }

    @Override
    public UserDetailsVo getUserDetailsVoByUserId(String userId) {
        UserInfoEntity userInfoEntity = userInfoService.getByUserId(userId);
        if (userInfoEntity != null) {
            return getUserDetailsVo(userInfoEntity);
        }
        return null;
    }

    @Override
    public List<UserInfoEntity> getUserInfosByUserId(List<String> userIds) {
        return userInfoService.getUserInfosByUserId(userIds);
    }

    @Override
    public UserInfoEntity getUserInfoByUserId(String userId) {
        return userInfoService.getByUserId(userId);
    }

    @Override
    public UserInfoEntity getUserInfoByUuid(String userInfoUuid) {
        return userInfoService.getOne(userInfoUuid);
    }

    @Override
    public UserInfoEntity getUserInfoByMobile(String mobile) {
        return userInfoService.getByMobile(mobile);
    }

    @Override
    public UserInfoEntity getUserInfoByLoginName(String loginName) {
        return userInfoService.getByLoginName(loginName);
    }

    @Override
    public boolean existAccountByLoginName(String loginName) {
        return userAccountService.existAccountByLoginName(loginName);
    }

    /**
     * 获取系统可用的组织
     *
     * @param system
     * @return
     */
    @Override
    public List<OrganizationDto> listOrganizationBySystem(String system) {
        List<OrganizationEntity> entities = organizationService.listEnabledBySystem(system);
        List<OrganizationDto> dtos = BeanUtils.copyCollection(entities, OrganizationDto.class);
        return dtos;
    }

    @Override
    public List<OrganizationDto> listOrganizationByOrgUuids(List<Long> orgUuids) {
        if (CollectionUtils.isEmpty(orgUuids)) {
            return Collections.emptyList();
        }
        List<OrganizationEntity> entities = organizationService.listByUuids(orgUuids);
        List<OrganizationDto> dtos = BeanUtils.copyCollection(entities, OrganizationDto.class);
        return dtos;
    }

    /**
     * 根据组织ID获取组织信息
     *
     * @param orgId
     * @return
     */
    @Override
    public OrganizationDto getOrganizationById(String orgId) {
        OrganizationDto dto = new OrganizationDto();
        OrganizationEntity entity = organizationService.getById(orgId);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * @param bizOrgId
     * @return
     */
    @Override
    public OrganizationDto getOrganizationByBizOrgId(String bizOrgId) {
        OrganizationDto dto = new OrganizationDto();
        OrganizationEntity entity = organizationService.getByBizOrgId(bizOrgId);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 根据组织版本ID获取对应的组织
     *
     * @param orgVersionId
     * @return
     */
    @Override
    public OrganizationDto getOrganizationByOrgVersionId(String orgVersionId) {
        OrganizationDto dto = new OrganizationDto();
        OrganizationEntity entity = organizationService.getByOrgVersionId(orgVersionId);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

    /**
     * 根据组织UUID获取组织角色
     *
     * @param orgId
     * @return
     */
    @Override
    public List<OrgRoleDto> listOrgRoleByOrgId(String orgId) {
        List<OrgRoleEntity> entities = orgRoleService.listByOrgId(orgId);
        List<OrgRoleDto> dtos = BeanUtils.copyCollection(entities, OrgRoleDto.class);
        return dtos;
    }

    /**
     * 获取可使用的组织版本
     *
     * @param system
     * @return
     */
    @Override
    public List<OrgVersionEntity> listOrgVersionBySystem(String system, String tenant) {
        return orgVersionService.listPublishedBySystemAndTenant(system, StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
    }

    /**
     * 获取默认组织使用的组织版本
     *
     * @param system
     * @return
     */
    @Override
    public OrgVersionEntity getDefaultOrgVersionBySystem(String system) {
        return orgVersionService.getDefaultOrgVersionBySystem(system);
    }

    /**
     * 获取指定组织使用的组织版本
     *
     * @param orgId
     * @return
     */
    @Override
    public OrgVersionEntity getOrgVersionByOrgId(String orgId) {
        return orgVersionService.getOrgVersionByOrgId(orgId);
    }

    /**
     * @param orgUuid
     * @return
     */
    @Override
    public OrgVersionEntity getOrgVersionByOrgUuid(Long orgUuid) {
        return orgVersionService.getOrgVersionByOrgUuid(orgUuid);
    }

    /**
     * 根据组织版本ID，获取最新的组织版本ID
     *
     * @param orgVersionId
     * @return
     */
    @Override
    public String getLatestOrgVersionIdByOrgVersionId(String orgVersionId) {
        OrgVersionEntity orgVersionEntity = orgVersionService.getLatestOrgVersionByOrgVersionId(orgVersionId);
        return orgVersionEntity != null ? orgVersionEntity.getId() : orgVersionId;
    }

    /**
     * 根据组织ID列表，获取组织版本列表
     *
     * @param orgIds
     * @return
     */
    @Override
    public List<OrgVersionEntity> listOrgVersionByOrgIds(List<String> orgIds) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return Collections.emptyList();
        }
        return orgVersionService.listByOrgIds(orgIds);
    }

    @Override
    public List<OrgVersionEntity> listOrgVersionByOrgIdsAndSystem(List<String> orgIds, String system) {
        if (CollectionUtils.isEmpty(orgIds)) {
            return Collections.emptyList();
        }
        return orgVersionService.listByOrgIdsAndSystem(orgIds, system);
    }

    @Override
    public List<OrgVersionEntity> listOrgVersionByOrgVersionIds(Set<String> orgVersionIds) {
        if (CollectionUtils.isEmpty(orgVersionIds)) {
            return Collections.emptyList();
        }
        return orgVersionService.listByIds(orgVersionIds);
    }

    @Override
    public Long getBizOrgUuidByBizOrgId(String bizOrgId) {
        return bizOrganizationService.getBizOrgUuidByBizOrgId(bizOrgId);
    }

    @Override
    public List<BizOrganizationEntity> listBizOrganizationByBizOrgUuids(Set<Long> bizOrgUuids) {
        if (CollectionUtils.isEmpty(bizOrgUuids)) {
            return Collections.emptyList();
        }
        return bizOrganizationService.listByUuids(Lists.newArrayList(bizOrgUuids));
    }

    /**
     * 根据组织元素ID列表，获取对应的名称
     *
     * @param orgEleIds
     * @return
     */
    @Override
    public Map<String, String> getNameByOrgEleIds(List<String> orgEleIds) {
        if (CollectionUtils.isEmpty(orgEleIds)) {
            return Collections.emptyMap();
        }

        Set<String> ids = Sets.newLinkedHashSet();
        for (String orgEleId : orgEleIds) {
            if (StringUtils.contains(orgEleId, Separator.SLASH.getValue())) {
                String[] idParts = StringUtils.split(orgEleId, Separator.SLASH.getValue());
                int length = idParts.length;
                // 业务角色ID
                if ((StringUtils.startsWith(idParts[length - 2], IdPrefix.BIZ_ORG_DIM.getValue())
                        || StringUtils.startsWith(idParts[length - 2], IdPrefix.BIZ_PREFIX.getValue()))
                        && !IdPrefix.hasPrefix(idParts[length - 1])) {
                    for (int i = 0; i < length - 1; i++) {
                        if (i == length - 2) {
                            ids.add(idParts[length - 2] + "/" + idParts[length - 1]);
                        } else {
                            ids.add(idParts[i]);
                        }
                    }
                } else {
                    ids.addAll(Arrays.asList(idParts));
                }
            } else {
                ids.add(orgEleId);
            }
        }

        Map<String, String> idMap = getNameByOrgEleIds(ids);

        Map<String, String> map = Maps.newLinkedHashMap();
        for (String orgEleId : orgEleIds) {
            if (StringUtils.contains(orgEleId, Separator.SLASH.getValue())) {
                List<String> idNames = Lists.newArrayList();
                String[] idParts = StringUtils.split(orgEleId, Separator.SLASH.getValue());
                int length = idParts.length;
                // 业务角色ID
                if ((StringUtils.startsWith(idParts[length - 2], IdPrefix.BIZ_ORG_DIM.getValue())
                        || StringUtils.startsWith(idParts[length - 2], IdPrefix.BIZ_PREFIX.getValue()))
                        && !IdPrefix.hasPrefix(idParts[length - 1])) {
                    for (int i = 0; i < length - 1; i++) {
                        if (i == length - 2) {
                            idNames.add(idMap.get(idParts[length - 2] + "/" + idParts[length - 1]));
                        } else {
                            idNames.add(idMap.get(idParts[i]));
                        }
                    }
                } else {
                    for (String idPart : idParts) {
                        idNames.add(idMap.get(idPart));
                    }
                }
                map.put(orgEleId, StringUtils.join(idNames, Separator.SLASH.getValue()));
            } else {
                map.put(orgEleId, idMap.get(orgEleId));
            }
        }
        return map;
    }

    private Map<String, String> getNameByOrgEleIds(Set<String> orgEleIds) {
        Map<String, String> map = Maps.newHashMap();
        List<String> eleIds = Lists.newArrayList();
        List<String> userIds = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        Set<String> bizElIds = Sets.newLinkedHashSet();
        Set<String> bizRoleIds = Sets.newLinkedHashSet();
        orgEleIds.forEach(id -> {
            String idValue = id;
            if (StringUtils.contains(id, Separator.SLASH.getValue())) {
                String[] idParts = StringUtils.split(id, Separator.SLASH.getValue());
                idValue = idParts[idParts.length - 1];
                // 业务角色ID不进行拆分处理
                if (idParts.length == 2 && (StringUtils.startsWith(idParts[0], IdPrefix.BIZ_ORG_DIM.getValue())
                        || StringUtils.startsWith(idParts[0], IdPrefix.BIZ_PREFIX.getValue()))
                        && !IdPrefix.hasPrefix(idParts[1])) {
                    idValue = id;
                    bizRoleIds.add(id);
                    return;
                }
            }
            if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
                userIds.add(id);
            } else if (StringUtils.startsWith(id, IdPrefix.GROUP.getValue())) {
                groupIds.add(id);
            } else if (StringUtils.startsWith(id, IdPrefix.BIZ_PREFIX.getValue())
                    || StringUtils.startsWith(id, IdPrefix.BIZ_ORG_DIM.getValue())) {
                bizElIds.add(id);
            } else {
                eleIds.add(id);
            }
        });
        if (CollectionUtils.isNotEmpty(userIds)) {
            map.putAll(userInfoService.getUserNamesByUserIds(userIds));
        }
        if (CollectionUtils.isNotEmpty(groupIds)) {
            map.putAll(orgGroupService.getNamesByIds(groupIds));
        }
        if (CollectionUtils.isNotEmpty(eleIds)) {
            map.putAll(orgElementService.getNamesByIds(eleIds));
        }
        if (CollectionUtils.isNotEmpty(bizElIds) || CollectionUtils.isNotEmpty(bizRoleIds)) {
            bizElIds.addAll(bizRoleIds);
            map.putAll(bizOrgElementService.getNamesByIds(bizElIds));
        }
        return map;
    }

    @Override
    public Map<String, String> getNamePathByOrgEleIds(List<String> orgEleIds) {
        Map<String, String> map = Maps.newHashMap();
        List<String> eleIds = Lists.newArrayList();
        List<String> userIds = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        Set<String> bizElIds = Sets.newLinkedHashSet();
        Set<String> bizRoleIds = Sets.newLinkedHashSet();
        orgEleIds.forEach(id -> {
            String idValue = id;
            if (StringUtils.contains(id, Separator.SLASH.getValue())) {
                String[] idParts = StringUtils.split(id, Separator.SLASH.getValue());
                idValue = idParts[idParts.length - 1];
                // 业务角色ID不进行拆分处理
                if (idParts.length == 2 && (StringUtils.startsWith(idParts[0], IdPrefix.BIZ_ORG_DIM.getValue())
                        || StringUtils.startsWith(idParts[0], IdPrefix.BIZ_PREFIX.getValue()))
                        && !IdPrefix.hasPrefix(idParts[1])) {
                    idValue = id;
                    bizRoleIds.add(id);
                    return;
                }
            }
            if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
                userIds.add(id);
            } else if (StringUtils.startsWith(id, IdPrefix.GROUP.getValue())) {
                groupIds.add(id);
            } else if (StringUtils.startsWith(id, IdPrefix.BIZ_PREFIX.getValue())
                    || StringUtils.startsWith(id, IdPrefix.BIZ_ORG_DIM.getValue())) {
                bizElIds.add(id);
            } else {
                eleIds.add(id);
            }
        });
        if (CollectionUtils.isNotEmpty(userIds)) {
            map.putAll(userInfoService.getUserNamesByUserIds(userIds));
        }
        if (CollectionUtils.isNotEmpty(groupIds)) {
            map.putAll(orgGroupService.getNamesByIds(groupIds));
        }
        if (CollectionUtils.isNotEmpty(eleIds)) {
            map.putAll(orgElementService.getNamePathsByIds(eleIds));
        }
        if (CollectionUtils.isNotEmpty(bizElIds) || CollectionUtils.isNotEmpty(bizRoleIds)) {
            bizElIds.addAll(bizRoleIds);
            map.putAll(bizOrgElementService.getNamePathsByIds(Lists.newArrayList(bizElIds)));
        }
        return map;
    }

    /**
     * 根据用户ID、组织版本ID，获取用户的直接汇报人
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobDirectLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> directLeaderIds = Sets.newLinkedHashSet();
        List<OrgUserReportRelationEntity> reportRelationEntities = orgUserReportRelationService.listByOrgVersionIdsAndUserId(requiredOrgVersionIds, userId);
        if (CollectionUtils.isNotEmpty(reportRelationEntities)) {
            reportRelationEntities.stream().forEach(entity -> {
                if (StringUtils.isNotEmpty(entity.getReportToUserId())) {
                    directLeaderIds.addAll(Arrays.asList(StringUtils.split(entity.getReportToUserId(), Separator.SEMICOLON.getValue())));
                }
            });
        }
        return directLeaderIds;
    }

    /**
     * 获取用户主职位的直接汇报人
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobDirectLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> directLeaderIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        if (CollectionUtils.isEmpty(primaryJobUsers)) {
            return directLeaderIds;
        }

        List<String> eleIds = primaryJobUsers.stream().map(jobUser -> jobUser.getOrgElementId()).collect(Collectors.toList());
        List<OrgUserReportRelationEntity> reportRelationEntities = orgUserReportRelationService.listByOrgVersionIdsAndUserIdAndOrgElementIds(requiredOrgVersionIds, userId, eleIds);
        if (CollectionUtils.isNotEmpty(reportRelationEntities)) {
            reportRelationEntities.stream().forEach(entity -> {
                if (StringUtils.isNotEmpty(entity.getReportToUserId())) {
                    directLeaderIds.addAll(Arrays.asList(StringUtils.split(entity.getReportToUserId(), Separator.SEMICOLON.getValue())));
                }
            });
        }
        return directLeaderIds;
    }

    /**
     * 获取用户指定职位的直接汇报人
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobDirectLeader(String userId, String jobId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> directLeaderIds = Sets.newLinkedHashSet();
        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserService.listOrgUserByUserIdAndOrgElementIds(userId, jobIds, requiredOrgVersionIds);
        if (CollectionUtils.isEmpty(jobUsers)) {
            return directLeaderIds;
        }

        List<String> eleIds = jobUsers.stream().map(jobUser -> jobUser.getOrgElementId()).collect(Collectors.toList());
        List<OrgUserReportRelationEntity> reportRelationEntities = orgUserReportRelationService.listByOrgVersionIdsAndUserIdAndOrgElementIds(requiredOrgVersionIds, userId, eleIds);
        if (CollectionUtils.isNotEmpty(reportRelationEntities)) {
            reportRelationEntities.stream().forEach(entity -> {
                if (StringUtils.isNotEmpty(entity.getReportToUserId())) {
                    directLeaderIds.addAll(Arrays.asList(StringUtils.split(entity.getReportToUserId(), Separator.SEMICOLON.getValue())));
                }
            });
        }
        return directLeaderIds;
    }

    /**
     * 组织版本ID为空时，获取默认的组织版本ID
     *
     * @param orgVersionIds
     * @return
     */
    private String[] getDefaultOrgVersionIdIfRequired(String[] orgVersionIds) {
        if (ArrayUtils.isNotEmpty(orgVersionIds)) {
            return orgVersionIds;
        }
        String defaultOrgVersionId = orgVersionService.getDefaultOrgVersionBySystem(RequestSystemContextPathResolver.system()).getId();
        return new String[]{defaultOrgVersionId};
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        Map<String, Set<String>> groupMap = getIdGroupMap(ids);
        Set<String> userIds = groupMap.get("userIds");
        Set<String> groupIds = groupMap.get("groupIds");
        Set<String> eleIds = groupMap.get("eleIds");

        // 解析群组成员
        if (CollectionUtils.isNotEmpty(groupIds)) {
            Set<String> groupMemberIds = orgGroupService.listMemberIdByIds(groupIds);
            groupMemberIds.forEach(id -> {
                if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
                    userIds.add(id);
                } else {
                    eleIds.add(id);
                }
            });
        }

        Map<String, String> userMap = userInfoService.listAsMapByUserIds(userIds, eleIds, orgVersionIds, requiredOrgVersionIds);
        map.putAll(userMap);
//        List<UserDto> userInfoDtos = BeanUtils.copyCollection(userInfoEntities, UserDto.class);
//        userInfoDtos.forEach(userDto -> {
//            map.put(userDto.getUserId(), userDto);
//        });
        return map;
    }

    /**
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getUsersByJobGrades(List<String> jobGrades, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<String> jobIds = getJobIdsByJobGrades(jobGrades, requiredOrgVersionIds);
        if (CollectionUtils.isEmpty(jobIds)) {
            return map;
        }

        Map<String, String> userMap = userInfoService.listAsMapByUserIds(Collections.emptySet(), Sets.newHashSet(jobIds), orgVersionIds, requiredOrgVersionIds);
        map.putAll(userMap);
        return map;
    }

    /**
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getUsersByJobRankIds(List<String> jobRankIds, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<String> jobIds = getJobIdsByJobRankIds(jobRankIds, requiredOrgVersionIds);
        if (CollectionUtils.isEmpty(jobIds)) {
            return map;
        }

        Map<String, String> userMap = userInfoService.listAsMapByUserIds(Collections.emptySet(), Sets.newHashSet(jobIds), orgVersionIds, requiredOrgVersionIds);
        map.putAll(userMap);
        return map;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<OrgElementEntity> entities = orgElementService.listJobElementInIds(ids, requiredOrgVersionIds);
        entities.forEach(entity -> map.put(entity.getId(), entity.getName()));
        return map;
    }

    /**
     * @param jobGrades
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getJobsByJobGrades(List<String> jobGrades, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<String> jobIds = getJobIdsByJobGrades(jobGrades, orgVersionIds);
        if (CollectionUtils.isEmpty(jobIds)) {
            return map;
        }

        List<OrgElementEntity> entities = orgElementService.listJobElementInIds(jobIds, requiredOrgVersionIds);
        entities.forEach(entity -> map.put(entity.getId(), entity.getName()));
        return map;
    }

    @Override
    public List<String> getJobIdsByJobGrades(List<String> jobGrades, String[] orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String system = RequestSystemContextPathResolver.system();
        List<MultiOrgDuty> multiOrgDuties = multiOrgDutyService.listBySystemOrSystemUnitId(system, systemUnitId);
        List<String> dutyIds = multiOrgDuties.stream().filter(multiOrgDuty -> {
            String jobGrade = multiOrgDuty.getJobGrade();
            if (StringUtils.isBlank(jobGrade)) {
                return false;
            }
            List<String> grades = Lists.newArrayList(StringUtils.split(jobGrade, Separator.COMMA.getValue()));
            return CollectionUtils.containsAny(grades, jobGrades);
        }).map(MultiOrgDuty::getId).collect(Collectors.toList());

        if (CollectionUtils.isEmpty(dutyIds)) {
            return Collections.emptyList();
        }

        List<String> jobIds = multiOrgJobDutyService.listJobIdByDutyIds(dutyIds, requiredOrgVersionIds);
        return jobIds;
    }

    /**
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> getJobsByJobRankIds(List<String> jobRankIds, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<String> jobIds = getJobIdsByJobRankIds(jobRankIds, requiredOrgVersionIds);
        if (CollectionUtils.isEmpty(jobIds)) {
            return map;
        }

        List<OrgElementEntity> entities = orgElementService.listJobElementInIds(jobIds, requiredOrgVersionIds);
        entities.forEach(entity -> map.put(entity.getId(), entity.getName()));
        return map;
    }

    /**
     * @param jobRankIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> getJobIdsByJobRankIds(List<String> jobRankIds, String[] orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        String system = RequestSystemContextPathResolver.system();
        List<MultiOrgDuty> multiOrgDuties = multiOrgDutyService.listBySystemOrSystemUnitId(system, systemUnitId);
        List<String> dutyIds = multiOrgDuties.stream().filter(multiOrgDuty -> {
            String rankId = multiOrgDuty.getJobRank();
            if (StringUtils.isBlank(rankId)) {
                return false;
            }
            List<String> rankIds = Lists.newArrayList(StringUtils.split(rankId, Separator.COMMA.getValue()));
            return CollectionUtils.containsAny(rankIds, jobRankIds);
        }).map(MultiOrgDuty::getId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(dutyIds)) {
            return Collections.emptyList();
        }

        List<String> jobIds = multiOrgJobDutyService.listJobIdByDutyIds(dutyIds, requiredOrgVersionIds);
        return jobIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<String> deptIds = Lists.newArrayList();
        List<String> notDeptIds = Lists.newArrayList();
        ids.forEach(id -> {
            if (StringUtils.startsWith(id, IdPrefix.DEPARTMENT.getValue())) {
                deptIds.add(id);
            } else {
                notDeptIds.add(id);
            }
        });
        List<OrgElementEntity> entities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(deptIds)) {
            entities.addAll(orgElementService.listElementByIds(deptIds, requiredOrgVersionIds));
        }
        if (CollectionUtils.isNotEmpty(notDeptIds)) {
            entities.addAll(orgElementService.listDepartmentElementInIds(notDeptIds, requiredOrgVersionIds));
        }
        entities.forEach(entity -> map.put(entity.getId(), entity.getName()));
        return map;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<String> uniIds = Lists.newArrayList();
        List<String> notUnitIds = Lists.newArrayList();
        ids.forEach(id -> {
            if (StringUtils.startsWith(id, IdPrefix.SYSTEM_UNIT.getValue())) {
                uniIds.add(id);
            } else {
                notUnitIds.add(id);
            }
        });
        List<OrgElementEntity> entities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(uniIds)) {
            entities.addAll(orgElementService.listElementByIds(uniIds, requiredOrgVersionIds));
        }
        if (CollectionUtils.isNotEmpty(notUnitIds)) {
            entities.addAll(orgElementService.listUnitElementInIds(notUnitIds, requiredOrgVersionIds));
        }
        entities.forEach(entity -> map.put(entity.getId(), entity.getName()));
        return map;
    }

    @Override
    public Map<String, String> getOrgElementsByIds(List<String> ids, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Map<String, String> map = Maps.newLinkedHashMap();
        List<OrgElementEntity> entities = orgElementService.listOrgElementInIds(ids, requiredOrgVersionIds);
        entities.forEach(entity -> map.put(entity.getId(), entity.getName()));
        return map;
    }

    /**
     * 获取用户所有职位的上级领导，找到为止
     *
     * @param userId
     * @param orgVersionIds
     */
    @Override
    public Set<String> listUserAllJobSuperiorLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR);
        return listUserAllJobLeader(userId, leaderTypes, null, false, false, requiredOrgVersionIds);
    }

    /**
     * 获取用户主职位的上级领导，找到为止
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobSuperiorLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR);
        return listUserMainJobLeader(userId, leaderTypes, null, false, false, requiredOrgVersionIds);
    }

    /**
     * 获取用户指定职位的上级领导，找到为止
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobSuperiorLeader(String userId, String jobId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR);
        return listUserJobLeader(userId, jobId, leaderTypes, null, false, false, requiredOrgVersionIds);
    }

    /**
     * 获取用户所有职位的部门领导，找到为止
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobDepartmentLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR);
        List<String> eleIdTypes = Lists.newArrayList(IdPrefix.DEPARTMENT.getValue());
        return listUserAllJobLeader(userId, leaderTypes, eleIdTypes, false, true, requiredOrgVersionIds);
    }

    /**
     * 获取用户主职位的部门领导，找到为止
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobDepartmentLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR);
        List<String> eleIdTypes = Lists.newArrayList(IdPrefix.DEPARTMENT.getValue());
        return listUserMainJobLeader(userId, leaderTypes, eleIdTypes, false, true, requiredOrgVersionIds);
    }

    /**
     * 获取用户指定职位的部门领导，找到为止
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobDepartmentLeader(String userId, String jobId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.DIRECTOR);
        List<String> eleIdTypes = Lists.newArrayList(IdPrefix.DEPARTMENT.getValue());
        return listUserJobLeader(userId, jobId, leaderTypes, eleIdTypes, false, true, requiredOrgVersionIds);
    }

    /**
     * 获取用户所有职位的分管领导，找到为止
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserAllJobBranchLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.LEADER);
        return listUserAllJobLeader(userId, leaderTypes, null, false, false, requiredOrgVersionIds);
    }

    /**
     * 获取用户主职位的分管领导，找到为止
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserMainJobBranchLeader(String userId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.LEADER);
        return listUserMainJobLeader(userId, leaderTypes, null, false, false, requiredOrgVersionIds);
    }

    /**
     * 获取用户指定职位的分管领导，找到为止
     *
     * @param userId
     * @param jobId
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobBranchLeader(String userId, String jobId, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgElementManagementEntity.LeaderType> leaderTypes = Lists.newArrayList(OrgElementManagementEntity.LeaderType.LEADER);
        return listUserJobLeader(userId, jobId, leaderTypes, null, false, false, requiredOrgVersionIds);
    }

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
    @Override
    public Set<String> listUserAllJobLeader(String userId, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                            List<String> eleIdTypes, boolean getAll, boolean includeSelf, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> leaders = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 用户职位或部门对应的上级领导（即组织路径上的管理职能类节点的负责人）
        List<OrgUserEntity> allJobUsers = orgUserEntities.stream().filter(orgUserEntity -> StringUtils.isNotBlank(orgUserEntity.getOrgElementId())).collect(Collectors.toList());
//        // 主职
//        Lists.newArrayList(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER));
//        // 副职
//        allJobUsers.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.SECONDARY_JOB_USER));
        leaders.addAll(listOrgElementLeader(userId, allJobUsers, leaderTypes, eleIdTypes, getAll, includeSelf));

//        // 用户成员，职位路径上的领导为空时，取用户成员路径的领导
//        if (CollectionUtils.isEmpty(leaders)) {
//            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
//            leaders.addAll(listOrgElementLeader(userId, memberUsers, leaderTypes, eleIdTypes, getAll, includeSelf));
//        }
        return leaders;
    }

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
    @Override
    public Set<String> listUserMainJobLeader(String userId, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                             List<String> eleIdTypes, boolean getAll, boolean includeSelf, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> leaders = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 用户职位或部门对应的上级领导（即组织路径上的管理职能类节点的负责人），找到为止
        // 主职
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        leaders.addAll(listOrgElementLeader(userId, primaryJobUsers, leaderTypes, eleIdTypes, getAll, includeSelf));

        // 用户成员，职位路径上的领导为空时，取用户成员路径的领导
        if (CollectionUtils.isEmpty(leaders)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            leaders.addAll(listOrgElementLeader(userId, memberUsers, leaderTypes, eleIdTypes, getAll, includeSelf));
        }
        return leaders;
    }

    /**
     * 获取用户指定职位的领导
     *
     * @param userId
     * @param jobId
     * @param leaderTypes
     * @param eleIdTypes
     * @param getAll
     * @param orgVersionIds
     * @return
     */
    @Override
    public Set<String> listUserJobLeader(String userId, String jobId, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                         List<String> eleIdTypes, boolean getAll, boolean includeSelf, String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> leaders = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList());
        // 用户职位或部门对应的上级领导（即组织路径上的管理职能类节点的负责人），找到为止
        leaders.addAll(listOrgElementLeader(userId, jobUsers, leaderTypes, eleIdTypes, getAll, includeSelf));

        // 用户成员，职位路径上的领导为空时，取用户成员路径的领导
        if (CollectionUtils.isEmpty(leaders)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            leaders.addAll(listOrgElementLeader(userId, memberUsers, leaderTypes, eleIdTypes, getAll, includeSelf));
        }
        return leaders;
    }

    /**
     * 用户职位或部门对应的领导（即组织路径上的管理职能类节点）
     *
     * @param orgUserEntities
     * @param leaderTypes
     * @param matchEleIdTypes
     * @param getAll
     * @return
     */
    private Set<String> listOrgElementLeader(String userId, List<OrgUserEntity> orgUserEntities, List<OrgElementManagementEntity.LeaderType> leaderTypes,
                                             List<String> matchEleIdTypes, boolean getAll, boolean includeSelf) {
        Set<String> directorSet = Sets.newLinkedHashSet();
        Set<String> elementIdSet = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            elementIdSet.addAll(Arrays.asList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue())));
            elementIdSet.remove(orgUserEntity.getUserId());
            orgVersionUuids.add(orgUserEntity.getOrgVersionUuid());
        }

        // 组织元素类型过滤
        if (CollectionUtils.isNotEmpty(matchEleIdTypes)) {
            elementIdSet = elementIdSet.stream().filter(elementId -> {
                for (String matchEleType : matchEleIdTypes) {
                    if (StringUtils.startsWith(elementId, matchEleType)) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toSet());
        }
        if (CollectionUtils.isEmpty(elementIdSet)) {
            return directorSet;
        }

        // 获取组织元素上的管理信息
        List<OrgElementManagementEntity> elementManagementEntities = orgElementManagementService.listByElementIds(elementIdSet, orgVersionUuids.toArray(new Long[0]));
        Map<String, List<OrgElementManagementEntity>> elementManagementMap = ListUtils.list2group(elementManagementEntities, "orgElementId");

        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            List<String> elementIds = Lists.newArrayList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue()));
            elementIds.remove(orgUserEntity.getUserId());
            Collections.reverse(elementIds);
            Iterator<String> iterator = elementIds.iterator();
            while (iterator.hasNext()) {
                List<OrgElementManagementEntity> managementEntities = elementManagementMap.get(iterator.next());
                if (CollectionUtils.isEmpty(managementEntities)) {
                    continue;
                }

                List<String> elementLeaders = Lists.newArrayList();
                managementEntities.forEach(entity -> {
                    leaderTypes.forEach(leaderType -> {
                        switch (leaderType) {
                            // 负责人
                            case DIRECTOR:
                                if (StringUtils.isNotBlank(entity.getDirector())) {
                                    List<String> directorLeaders = Arrays.asList(StringUtils.split(entity.getDirector(), Separator.SEMICOLON.getValue()));
                                    // 节点负责人包含自己时才取节点负责人
                                    if (includeSelf) {
                                        elementLeaders.addAll(directorLeaders);
                                    } else if (!directorLeaders.contains(userId)) {
                                        elementLeaders.addAll(directorLeaders);
                                    }
                                }
                                break;
                            // 分管领导
                            case LEADER:
                                if (StringUtils.isNotBlank(entity.getLeader())) {
                                    List<String> leaders = Arrays.asList(StringUtils.split(entity.getLeader(), Separator.SEMICOLON.getValue()));
                                    // 节点分管领导包含自己时才取节点分管领导
                                    if (includeSelf) {
                                        elementLeaders.addAll(leaders);
                                    } else if (!leaders.contains(userId)) {
                                        elementLeaders.addAll(leaders);
                                    }
                                }
                                break;
                            // 管理员
                            case MANAGER:
                                if (StringUtils.isNotBlank(entity.getOrgManager())) {
                                    List<String> managers = Arrays.asList(StringUtils.split(entity.getOrgManager(), Separator.SEMICOLON.getValue()));
                                    // 节点分管领导包含自己时才取节点分管领导
                                    if (includeSelf) {
                                        elementLeaders.addAll(managers);
                                    } else if (!managers.contains(userId)) {
                                        elementLeaders.addAll(managers);
                                    }
                                }
                                break;
                        }
                    });
                });
                if (CollectionUtils.isNotEmpty(elementLeaders)) {
                    directorSet.addAll(elementLeaders);
                    // 不向上取值时，取到为止
                    if (!getAll) {
                        break;
                    }
                }
            }
        }
        return directorSet;
    }

    /**
     * 过滤组织用户
     *
     * @param orgUserEntities
     * @param types
     * @return
     */
    private List<OrgUserEntity> filterOrgUserByTypes(List<OrgUserEntity> orgUserEntities, OrgUserEntity.Type... types) {
        List<OrgUserEntity> retEntities = orgUserEntities.stream().filter(entity -> {
            for (OrgUserEntity.Type type : types) {
                if (type.equals(entity.getType())) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
        return retEntities;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        List<OrgUserEntity> allJobUsers = orgUserEntities.stream().filter(orgUserEntity -> StringUtils.isNotBlank(orgUserEntity.getOrgElementId())).collect(Collectors.toList());
//        // 主职
//        List<OrgUserEntity> allJobUsers = Lists.newArrayList(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER));
//        // 副职
//        allJobUsers.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.SECONDARY_JOB_USER));
        departmentUserIds.addAll(listDepartmentUser(allJobUsers));

//        // 用户成员，职位路径上的人员为空时，取用户成员路径的部门人员
//        if (CollectionUtils.isEmpty(departmentUserIds)) {
//            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
//            departmentUserIds.addAll(listDepartmentUser(memberUsers));
//        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 主职
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        departmentUserIds.addAll(listDepartmentUser(primaryJobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的部门人员
        if (CollectionUtils.isEmpty(departmentUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            departmentUserIds.addAll(listDepartmentUser(memberUsers));
        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList());
        departmentUserIds.addAll(listDepartmentUser(jobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的人员
        if (CollectionUtils.isEmpty(departmentUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            departmentUserIds.addAll(listDepartmentUser(memberUsers));
        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        List<OrgUserEntity> allJobUsers = orgUserEntities.stream().filter(orgUserEntity -> StringUtils.isNotBlank(orgUserEntity.getOrgElementId())).collect(Collectors.toList());
//        // 主职
//        List<OrgUserEntity> allJobUsers = Lists.newArrayList(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER));
//        // 副职
//        allJobUsers.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.SECONDARY_JOB_USER));
        departmentUserIds.addAll(listParentDepartmentUser(allJobUsers));

//        // 用户成员，职位路径上的人员为空时，取用户成员路径的上级部门人员
//        if (CollectionUtils.isEmpty(departmentUserIds)) {
//            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
//            departmentUserIds.addAll(listParentDepartmentUser(memberUsers));
//        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 主职
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        departmentUserIds.addAll(listParentDepartmentUser(primaryJobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的上级部门人员
        if (CollectionUtils.isEmpty(departmentUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            departmentUserIds.addAll(listParentDepartmentUser(memberUsers));
        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList());
        departmentUserIds.addAll(listParentDepartmentUser(jobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的人员
        if (CollectionUtils.isEmpty(departmentUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            departmentUserIds.addAll(listParentDepartmentUser(memberUsers));
        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        List<OrgUserEntity> allJobUsers = orgUserEntities.stream().filter(orgUserEntity -> StringUtils.isNotBlank(orgUserEntity.getOrgElementId())).collect(Collectors.toList());
//        // 主职
//        List<OrgUserEntity> allJobUsers = Lists.newArrayList(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER));
//        // 副职
//        allJobUsers.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.SECONDARY_JOB_USER));
        departmentUserIds.addAll(listRootDepartmentUser(allJobUsers));

//        // 用户成员，职位路径上的人员为空时，取用户成员路径的根部门人员
//        if (CollectionUtils.isEmpty(departmentUserIds)) {
//            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
//            departmentUserIds.addAll(listRootDepartmentUser(memberUsers));
//        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 主职
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        departmentUserIds.addAll(listRootDepartmentUser(primaryJobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的根部门人员
        if (CollectionUtils.isEmpty(departmentUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            departmentUserIds.addAll(listRootDepartmentUser(memberUsers));
        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> departmentUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList());
        departmentUserIds.addAll(listRootDepartmentUser(jobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的人员
        if (CollectionUtils.isEmpty(departmentUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            departmentUserIds.addAll(listRootDepartmentUser(memberUsers));
        }
        return departmentUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> rootNodeUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        List<OrgUserEntity> allJobUsers = orgUserEntities.stream().filter(orgUserEntity -> StringUtils.isNotBlank(orgUserEntity.getOrgElementId())).collect(Collectors.toList());
//        // 主职
//        List<OrgUserEntity> allJobUsers = Lists.newArrayList(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER));
//        // 副职
//        allJobUsers.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.SECONDARY_JOB_USER));
        rootNodeUserIds.addAll(listRootNodeUser(allJobUsers));

//        // 用户成员，职位路径上的人员为空时，取用户成员路径的根部门人员
//        if (CollectionUtils.isEmpty(rootNodeUserIds)) {
//            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
//            rootNodeUserIds.addAll(listRootNodeUser(memberUsers));
//        }
        return rootNodeUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> rootNodeUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 主职
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        rootNodeUserIds.addAll(listRootNodeUser(primaryJobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的根部门人员
        if (CollectionUtils.isEmpty(rootNodeUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            rootNodeUserIds.addAll(listRootNodeUser(memberUsers));
        }
        return rootNodeUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> rootNodeUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList());
        rootNodeUserIds.addAll(listRootNodeUser(jobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的人员
        if (CollectionUtils.isEmpty(rootNodeUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            rootNodeUserIds.addAll(listRootNodeUser(memberUsers));
        }
        return rootNodeUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> unitUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        List<OrgUserEntity> allJobUsers = orgUserEntities.stream().filter(orgUserEntity -> StringUtils.isNotBlank(orgUserEntity.getOrgElementId())).collect(Collectors.toList());
//        // 主职
//        List<OrgUserEntity> allJobUsers = Lists.newArrayList(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER));
//        // 副职
//        allJobUsers.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.SECONDARY_JOB_USER));
        unitUserIds.addAll(listOrgUserUnitUserId(allJobUsers));

//        // 用户成员，职位路径上的人员为空时，取用户成员路径的根部门人员
//        if (CollectionUtils.isEmpty(unitUserIds)) {
//            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
//            unitUserIds.addAll(listOrgUserUnitUserId(memberUsers));
//        }
        return unitUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> unitUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 主职
        List<OrgUserEntity> primaryJobUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.PRIMARY_JOB_USER);
        unitUserIds.addAll(listOrgUserUnitUserId(primaryJobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的根部门人员
        if (CollectionUtils.isEmpty(unitUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            unitUserIds.addAll(listOrgUserUnitUserId(memberUsers));
        }
        return unitUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> unitUserIds = Sets.newLinkedHashSet();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);

        // 指定的职位
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        List<OrgUserEntity> jobUsers = orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList());
        unitUserIds.addAll(listOrgUserUnitUserId(jobUsers));

        // 用户成员，职位路径上的人员为空时，取用户成员路径的人员
        if (CollectionUtils.isEmpty(unitUserIds)) {
            List<OrgUserEntity> memberUsers = filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER);
            unitUserIds.addAll(listOrgUserUnitUserId(memberUsers));
        }
        return unitUserIds;
    }

    /**
     * 获取部门人员
     *
     * @param orgUserEntities
     * @return
     */
    private Set<String> listDepartmentUser(List<OrgUserEntity> orgUserEntities) {
        Set<String> departmentPaths = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        // 获取部门路径及组织版本UUID列表
        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            List<String> elementIds = Lists.newArrayList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue()));
            List<String> removeIds = Lists.newArrayList();
            Collections.reverse(elementIds);
            for (String elementId : elementIds) {
                if (StringUtils.startsWith(elementId, IdPrefix.DEPARTMENT.getValue())) {
                    break;
                } else {
                    removeIds.add(elementId);
                }
            }
            elementIds.removeAll(removeIds);
            Collections.reverse(elementIds);
            if (CollectionUtils.isNotEmpty(elementIds)) {
                departmentPaths.add(StringUtils.join(elementIds, Separator.SLASH.getValue()));
            }
            orgVersionUuids.add(orgUserEntity.getOrgVersionUuid());
        }

        // 查询部门路径下的用户
        Set<String> userIds = Sets.newLinkedHashSet();
        for (String departmentPath : departmentPaths) {
            userIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, orgVersionUuids.toArray(new Long[0])));
        }
        return userIds;
    }

    /**
     * 获取上级部门人员
     *
     * @param orgUserEntities
     * @return
     */
    private Set<String> listParentDepartmentUser(List<OrgUserEntity> orgUserEntities) {
        Set<String> departmentPaths = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        // 获取上级部门路径及组织版本UUID列表
        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            List<String> elementIds = Lists.newArrayList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue()));
            List<String> removeIds = Lists.newArrayList();
            Collections.reverse(elementIds);
            boolean searchParentDept = false;
            for (String elementId : elementIds) {
                if (StringUtils.startsWith(elementId, IdPrefix.DEPARTMENT.getValue())) {
                    if (searchParentDept) {
                        break;
                    }
                    // 开始查询上级部门
                    searchParentDept = true;
                    removeIds.add(elementId);
                } else {
                    removeIds.add(elementId);
                }
            }
            elementIds.removeAll(removeIds);
            Collections.reverse(elementIds);
            if (CollectionUtils.isNotEmpty(elementIds)) {
                departmentPaths.add(StringUtils.join(elementIds, Separator.SLASH.getValue()));
            }
            orgVersionUuids.add(orgUserEntity.getOrgVersionUuid());
        }

        // 查询部门路径下的用户
        Set<String> userIds = Sets.newLinkedHashSet();
        for (String departmentPath : departmentPaths) {
            userIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, orgVersionUuids.toArray(new Long[0])));
        }
        return userIds;
    }

    /**
     * 获取根部门人员
     *
     * @param orgUserEntities
     * @return
     */
    private Collection<String> listRootDepartmentUser(List<OrgUserEntity> orgUserEntities) {
        Set<String> departmentPaths = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        // 获取根部门路径及组织版本UUID列表
        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            List<String> elementIds = Lists.newArrayList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue()));
            List<String> newElementIds = Lists.newArrayList();
            boolean hasRootDept = false;
            for (String elementId : elementIds) {
                if (StringUtils.startsWith(elementId, IdPrefix.DEPARTMENT.getValue())) {
                    newElementIds.add(elementId);
                    hasRootDept = true;
                    break;
                } else {
                    newElementIds.add(elementId);
                }
            }
            if (hasRootDept) {
                departmentPaths.add(StringUtils.join(newElementIds, Separator.SLASH.getValue()));
            }
            orgVersionUuids.add(orgUserEntity.getOrgVersionUuid());
        }

        // 查询部门路径下的用户
        Set<String> userIds = Sets.newLinkedHashSet();
        for (String departmentPath : departmentPaths) {
            userIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, orgVersionUuids.toArray(new Long[0])));
        }
        return userIds;
    }


    /**
     * 获取根节点人员
     *
     * @param orgUserEntities
     * @return
     */
    private Set<String> listRootNodeUser(List<OrgUserEntity> orgUserEntities) {
        Set<String> rootNodePaths = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        // 获取根部门路径及组织版本UUID列表
        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            List<String> elementIds = Lists.newArrayList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue()));
            if (CollectionUtils.isNotEmpty(elementIds)) {
                rootNodePaths.add(elementIds.get(0));
            }
            orgVersionUuids.add(orgUserEntity.getOrgVersionUuid());
        }

        // 查询根节点路径下的用户
        Set<String> userIds = Sets.newLinkedHashSet();
        for (String departmentPath : rootNodePaths) {
            userIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, orgVersionUuids.toArray(new Long[0])));
        }
        return userIds;
    }

    /**
     * 获取根节点人员
     *
     * @param orgUserEntities
     * @return
     */
    private Set<String> listOrgUserUnitUserId(List<OrgUserEntity> orgUserEntities) {
        Set<String> unitPaths = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        // 获取部门路径及组织版本UUID列表
        for (OrgUserEntity orgUserEntity : orgUserEntities) {
            List<String> elementIds = Lists.newArrayList(StringUtils.split(orgUserEntity.getUserPath(), Separator.SLASH.getValue()));
            List<String> removeIds = Lists.newArrayList();
            Collections.reverse(elementIds);
            for (String elementId : elementIds) {
                if (StringUtils.startsWith(elementId, IdPrefix.SYSTEM_UNIT.getValue())) {
                    break;
                } else {
                    removeIds.add(elementId);
                }
            }
            elementIds.removeAll(removeIds);
            Collections.reverse(elementIds);
            if (CollectionUtils.isNotEmpty(elementIds)) {
                unitPaths.add(StringUtils.join(elementIds, Separator.SLASH.getValue()));
            }
            orgVersionUuids.add(orgUserEntity.getOrgVersionUuid());
        }

        // 查询单位路径下的用户
        Set<String> userIds = Sets.newLinkedHashSet();
        for (String departmentPath : unitPaths) {
            userIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, orgVersionUuids.toArray(new Long[0])));
        }
        return userIds;
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
//        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);
//
//        Set<String> memberIds = Sets.newLinkedHashSet();
//        List<OrgElementRoleMemberEntity> entities = orgElementRoleMemberService.listUserSameElementRoleMember(userId, requiredOrgVersionIds);
//        entities.forEach(entity -> {
//            if (StringUtils.isNotBlank(entity.getMember())) {
//                memberIds.add(entity.getMember());
//            }
//        });
//        return memberIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> subordinateUserIds = Sets.newLinkedHashSet();
        // 汇报关系中的下属
        subordinateUserIds.addAll(getReportToSubordinateUserIds(userId, requiredOrgVersionIds));

        // 获取作为负责人的组织路径
        List<OrgUserEntity> allOrgUserEntities = Lists.newArrayList();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);
        // 职位路径
        allOrgUserEntities.addAll(filterOrgUserByTypes(orgUserEntities, jobTypes.toArray(new OrgUserEntity.Type[0])));
        // 用户成员路径
        allOrgUserEntities.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER));

        // 作为负责人信息
        List<OrgElementManagementEntity> elementManagementEntities = orgElementManagementService.listByDirector(userId, requiredOrgVersionIds);
        if (CollectionUtils.isNotEmpty(elementManagementEntities)) {
            subordinateUserIds.addAll(listOrgUserPathUserIds(orgUserEntities, elementManagementEntities));
        }

        subordinateUserIds.remove(userId);
        return subordinateUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> subordinateUserIds = Sets.newLinkedHashSet();
        // 汇报关系中的下属
        subordinateUserIds.addAll(getReportToSubordinateUserIds(userId, requiredOrgVersionIds));

        // 获取作为负责人的组织路径
        List<OrgUserEntity> allOrgUserEntities = Lists.newArrayList();
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, requiredOrgVersionIds);
        // 指定职位路径
        List<String> jobIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobId)) {
            jobIds.addAll(Arrays.asList(StringUtils.split(jobId, Separator.SEMICOLON.getValue())));
        }
        allOrgUserEntities.addAll(orgUserEntities.stream().filter(entity -> jobIds.contains(entity.getOrgElementId())).collect(Collectors.toList()));
        // 用户成员路径
        allOrgUserEntities.addAll(filterOrgUserByTypes(orgUserEntities, OrgUserEntity.Type.MEMBER_USER));

        // 作为负责人信息
        List<OrgElementManagementEntity> elementManagementEntities = orgElementManagementService.listByDirector(userId, requiredOrgVersionIds);
        if (CollectionUtils.isNotEmpty(elementManagementEntities)) {
            subordinateUserIds.addAll(listOrgUserPathUserIds(allOrgUserEntities, elementManagementEntities));
        }

        subordinateUserIds.remove(userId);
        return subordinateUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> subordinateUserIds = Sets.newLinkedHashSet();
        // 直接汇报及部门负责人的下属
        subordinateUserIds.addAll(listUserSubordinateUserIds(userId, jobTypes, requiredOrgVersionIds));

        // 获取分管部门的分管路径
        List<String> orgElementPaths = orgElementManagementService.listOrgElementPathByLeader(userId, requiredOrgVersionIds);

        // 查询分管路径下的下属
        for (String departmentPath : orgElementPaths) {
            subordinateUserIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, requiredOrgVersionIds));
        }

        subordinateUserIds.remove(userId);
        return subordinateUserIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        Set<String> subordinateUserIds = Sets.newLinkedHashSet();
        // 直接汇报及部门负责人的下属
        subordinateUserIds.addAll(listUserSubordinateUserIds(userId, jobId, requiredOrgVersionIds));

        // 获取分管部门的分管路径
        List<String> orgElementPaths = orgElementManagementService.listOrgElementPathByLeader(userId, requiredOrgVersionIds);

        // 查询分管路径下的下属
        for (String departmentPath : orgElementPaths) {
            subordinateUserIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, requiredOrgVersionIds));
        }

        subordinateUserIds.remove(userId);
        return subordinateUserIds;
    }

    /**
     * 获取直接汇报的下属
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    private List<String> getReportToSubordinateUserIds(String userId, String[] orgVersionIds) {
        List<OrgUserReportRelationEntity> relations = orgUserReportRelationService.listByOrgVersionIdsAndReportToUserId(orgVersionIds, userId);
        List<OrgUserEntity> orgUserEntities = orgUserService.listOrgUserByUserId(userId, orgVersionIds);
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            if (relations == null) {
                relations = Lists.newArrayList();
            }
            for (OrgUserEntity ou : orgUserEntities) {
                if (OrgElementModelEntity.ORG_JOB_ID.equalsIgnoreCase(ou.getOrgElementType())) {
                    relations.addAll(orgUserReportRelationService.listByOrgVersionIdsAndReportToUserId(orgVersionIds, ou.getOrgElementId()));
                }
            }
        }
        return CollectionUtils.isNotEmpty(relations) ? relations.stream().filter(userRelaEntity -> StringUtils.isNotBlank(userRelaEntity.getUserId())).
                map(userRelaEntity -> userRelaEntity.getUserId()).collect(Collectors.toList()) : Collections.EMPTY_LIST;
    }

    /**
     * 获取用户管理节点下的人员
     *
     * @param orgUserEntities
     * @param elementManagementEntities
     * @return
     */
    private Set<String> listOrgUserPathUserIds(List<OrgUserEntity> orgUserEntities, List<OrgElementManagementEntity> elementManagementEntities) {
        Set<String> elementIdPaths = Sets.newHashSet();
        Set<Long> orgVersionUuids = Sets.newHashSet();
        // 获取用户路径中的管理路径
        for (OrgElementManagementEntity managementEntity : elementManagementEntities) {
            for (OrgUserEntity orgUserEntity : orgUserEntities) {
                if (managementEntity.getOrgVersionUuid() != null
                        && managementEntity.getOrgVersionUuid().equals(orgUserEntity.getOrgVersionUuid())
                        && StringUtils.contains(orgUserEntity.getUserPath(), managementEntity.getOrgElementId())) {
                    elementIdPaths.add(StringUtils.substringBefore(orgUserEntity.getUserPath(), managementEntity.getOrgElementId()) + managementEntity.getOrgElementId());
                    orgVersionUuids.add(managementEntity.getOrgVersionUuid());
                }
            }
        }

        // 查询路径下的用户
        Set<String> userIds = Sets.newLinkedHashSet();
        for (String departmentPath : elementIdPaths) {
            userIds.addAll(orgUserService.listUserIdByUserPathPrefix(departmentPath, orgVersionUuids.toArray(new Long[0])));
        }
        return userIds;
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
        Set<String> userIds = Sets.newLinkedHashSet();
        List<OrgElementRoleMemberEntity> entities = orgElementRoleMemberService.listByElementIdWithOrgRoleIdAndOrgId(eleId, orgRoleId, orgId);
        for (OrgElementRoleMemberEntity entity : entities) {
            if (StringUtils.isNotBlank(entity.getMember())) {
                userIds.addAll(Arrays.asList(StringUtils.split(entity.getMember(), Separator.SEMICOLON.getValue())));
            }
        }
        return userIds;
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
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        return orgUserService.listUserJobs(userId, requiredOrgVersionIds);
    }

    @Override
    public List<OrgUserDto> listOrgUser(String userId, String[] orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        return orgUserService.listOrgUser(userId, requiredOrgVersionIds);
    }

    @Override
    public List<OrgUserDto> listOrgUser(List<String> userIds, String[] orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        return orgUserService.listOrgUser(userIds, requiredOrgVersionIds);
    }

    @Override
    public List<OrgUserJobDto> listUserJobIdentity(String userId, boolean includeBizRole, String[] orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        List<OrgUserJobDto> userJobs = Lists.newArrayList();
        // 行政组织身份
        List<OrgUserDto> orgUserDtos = orgUserService.listOrgUser(userId, requiredOrgVersionIds);
        orgUserDtos.forEach(orgUserDto -> {
            OrgUserJobDto userJob = new OrgUserJobDto();
            userJob.setJobId(orgUserDto.getOrgElementId());
            userJob.setJobName(orgUserDto.getOrgElementName());
            userJob.setJobIdPath(orgUserDto.getOrgElementIdPath());
            userJob.setJobNamePath(orgUserDto.getOrgElementCnPath());
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                String jobName = orgElementService.getLocaleOrgElementName(orgUserDto.getOrgElementId(), orgUserDto.getOrgVersionUuid(), LocaleContextHolder.getLocale().toString());
                if (StringUtils.isNotBlank(jobName)) {
                    userJob.setJobName(jobName);
                }
                String localePath = orgElementPathService.getLocaleOrgElementPath(orgUserDto.getOrgElementId(), orgUserDto.getOrgVersionUuid(), LocaleContextHolder.getLocale().toString());
                if (StringUtils.isNotBlank(localePath)) {
                    userJob.setJobNamePath(localePath);
                }
            }
            userJob.setPrimary(OrgUserEntity.Type.PRIMARY_JOB_USER.equals(orgUserDto.getType()));
            userJob.setOrgVersionUuid(orgUserDto.getOrgVersionUuid());
            userJobs.add(userJob);
        });

        // 业务组织身份
        if (includeBizRole) {
            List<BizOrgElementMemberDto> bizOrgElementMemberDtos = bizOrgElementMemberService.getDetailsByMemberId(userId, RequestSystemContextPathResolver.system());
            bizOrgElementMemberDtos.forEach(dto -> {
                BizOrgElementPathEntity pathEntity = dto.getBizOrgElementPath();
                BizOrgRoleEntity roleEntity = dto.getBizOrgRole();
                if (pathEntity == null || roleEntity == null || StringUtils.isBlank(pathEntity.getIdPath())) {
                    return;
                }

                OrgUserJobDto userJob = new OrgUserJobDto();
                userJob.setJobId(dto.getBizOrgElementId() + Separator.SLASH.getValue() + dto.getBizOrgRoleId());
                userJob.setJobName(StringUtils.defaultIfBlank(roleEntity.getLocalName(), roleEntity.getName()));
                userJob.setJobIdPath(pathEntity.getIdPath() + Separator.SLASH.getValue() + dto.getBizOrgRoleId());
                userJob.setJobNamePath(StringUtils.defaultIfBlank(pathEntity.getLocalPath(), pathEntity.getCnPath()) + Separator.SLASH.getValue() + userJob.getJobName());
                userJob.setPrimary(false);
                userJob.setBizOrgUuid(dto.getBizOrgUuid());
                userJobs.add(userJob);
            });
        }

        return userJobs;
    }

    /**
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<MultiOrgDuty> listUserDutys(String userId, String[] orgVersionIds) {

        List<OrgUserJobDto> userJobs = this.listUserJobs(userId, orgVersionIds);
        List<String> jobIds = userJobs.stream().flatMap(userJob -> Stream.of(userJob.getJobId())).collect(Collectors.toList());

        List<MultiOrgDuty> multiOrgDuties = multiOrgDutyService.listByJobIds(jobIds);
        return multiOrgDuties;
    }

    @Override
    public List<JobRankLevelVo> listUserJobRankLevels(String userId, String[] orgVersionIds) {
        List<OrgUserJobDto> userJobs = this.listUserJobs(userId, orgVersionIds);
        List<String> jobIds = userJobs.stream().flatMap(userJob -> Stream.of(userJob.getJobId())).collect(Collectors.toList());
        List<JobRankLevelVo> jobRankLevelVos = Lists.newArrayList();
        jobIds.forEach(jobId -> {
            List<MultiOrgDuty> multiOrgDuties = multiOrgDutyService.listByJobIds(Lists.newArrayList(jobId));
            multiOrgDuties.forEach(multiOrgDuty -> {
                String jobRank = multiOrgDuty.getJobRank();
                String jobGrade = multiOrgDuty.getJobGrade();
                if (StringUtils.isBlank(jobRank) || StringUtils.isBlank(jobGrade)) {
                    return;
                }
                List<String> jobRankIds = Arrays.asList(StringUtils.split(jobRank, Separator.COMMA.getValue()));
                List<String> jobGradeIds = Arrays.asList(StringUtils.split(jobGrade, Separator.COMMA.getValue()));
                if (CollectionUtils.size(jobRankIds) != CollectionUtils.size(jobGradeIds)) {
                    return;
                }
                for (int index = 0; index < jobRankIds.size(); index++) {
                    JobRankLevelVo jobRankLevelVo = new JobRankLevelVo();
                    jobRankLevelVo.setJobId(jobId);
                    jobRankLevelVo.setJobRankId(jobRankIds.get(index));
                    jobRankLevelVo.setJobRank(jobRankIds.get(index));
                    jobRankLevelVo.setJobGrade(Integer.valueOf(jobGradeIds.get(index)));
                    jobRankLevelVos.add(jobRankLevelVo);
                }
            });
        });
        return jobRankLevelVos;
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
        if (memberOf == null || memberOf.length == 0) {
            return false;
        }

        List<String> members = Lists.newArrayList(memberOf);

        // 获取群组成员
        List<String> groupMembers = Lists.newArrayListWithCapacity(0);
        for (String member : memberOf) {
            if (StringUtils.startsWith(member, IdPrefix.GROUP.getValue())) {
                groupMembers.add(member);
            }
        }
        if (CollectionUtils.isNotEmpty(groupMembers)) {
            members.removeAll(groupMembers);
            members.addAll(orgGroupService.listMemberIdByIds(groupMembers));
        }

        // ID值比较
        if (members.contains(id)) {
            return true;
        }

        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);
        // 组织成员包含比较
        // 用户
        if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
            return orgUserService.isInOrgElement(id, members, requiredOrgVersionIds);
        } else if (StringUtils.startsWith(id, IdPrefix.JOB.getValue())) {
            // 职位
            return orgUserService.isJobInOrgElement(id, members, requiredOrgVersionIds);
        } else {
            // 组织元素
            return orgElementPathChainService.containsOrgElement(members, id, requiredOrgVersionIds);
        }
    }

    @Override
    public boolean isMemberOfOrg(String id, String orgId) {
        OrgVersionEntity versionEntity = getOrgVersionByOrgId(orgId);
        if (versionEntity == null) {
            return false;
        }
        Long orgVersionUuid = versionEntity.getUuid();
        if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
            return orgUserService.countByUserIdAndOrgVersionUuid(id, orgVersionUuid) > 0;
        }
        return orgElementService.countByIdAndOrgVersionUuid(id, orgVersionUuid) > 0;
    }

    @Override
    public boolean isMemberOfBizOrg(String id, String[] memberOf, String bizOrgId) {
        List<String> members = Lists.newArrayList(memberOf);

        String orgId = id;
        // 用户
        if (StringUtils.startsWith(orgId, IdPrefix.USER.getValue())) {
            return bizOrgElementMemberService.isInBizOrgElement(id, members, bizOrgId);
        } else if (StringUtils.contains(orgId, Separator.SLASH.getValue())) {
            // 业务角色
            orgId = StringUtils.substringBefore(orgId, Separator.SLASH.getValue());
            return bizOrgElementService.containsBizOrgElement(members, orgId, bizOrgId);
        } else {
            // 组织元素
            return bizOrgElementService.containsBizOrgElement(members, orgId, bizOrgId);
        }
    }

    /**
     * @param id
     * @param bizOrgId
     * @return
     */
    @Override
    public boolean isMemberOfBizOrg(String id, String bizOrgId) {
        if (StringUtils.startsWith(id, IdPrefix.USER.getValue())) {
            return bizOrgElementMemberService.isMemberOf(id, bizOrgId);
        }

        String eleId = id;
        if (StringUtils.contains(eleId, Separator.SLASH.getValue())) {
            eleId = StringUtils.substringBefore(eleId, Separator.SLASH.getValue());
        }
        return bizOrgElementService.countByIdAndBizOrgId(eleId, bizOrgId) > 0;
    }

    @Override
    public List<String> filterUserIdsByBizOrgId(List<String> userIds, String bizOrgId) {
        return bizOrgElementMemberService.listMemberIdByMemberIdsAndBizOrgId(userIds, bizOrgId);
    }

    /**
     * 获取租户管理员ID列表
     *
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> listCurrentTenantAdminIds(String... orgVersionIds) {
        String[] requiredOrgVersionIds = getDefaultOrgVersionIdIfRequired(orgVersionIds);

        return orgUserService.listCurrentTenantAdminIds(requiredOrgVersionIds);
    }

    /**
     * 获得用户相关的组织信息ID
     *
     * @param userId
     * @return
     */
    @Override
    public Set<String> getUserRelatedIds(String userId) {
        Set<String> relatedIds = Sets.newLinkedHashSet();
        // 用户相关的组织元素ID
        relatedIds.addAll(orgUserService.listRelatedElementId(userId));

        // 群组相关的群组ID
        if (CollectionUtils.isNotEmpty(relatedIds)) {
            relatedIds.addAll(orgGroupService.listRelatedGroupIdByMemberIds(relatedIds));
        }

        // 业务组织相关ID
        List<BizOrgElementMemberDto> bizOrgElementMemberDtos = bizOrgElementMemberService.getDetailsByMemberId(userId, RequestSystemContextPathResolver.system());
        if (CollectionUtils.isNotEmpty(bizOrgElementMemberDtos)) {
            bizOrgElementMemberDtos.forEach(dto -> {
                BizOrgElementPathEntity pathEntity = dto.getBizOrgElementPath();
                if (pathEntity == null || StringUtils.isBlank(pathEntity.getIdPath())) {
                    return;
                }
                relatedIds.addAll(Arrays.asList(StringUtils.split(pathEntity.getIdPath(), Separator.SLASH.getValue())));
                if (StringUtils.isNotBlank(dto.getBizOrgElementId()) && StringUtils.isNotBlank(dto.getBizOrgRoleId())) {
                    relatedIds.add(dto.getBizOrgElementId() + Separator.SLASH.getValue() + dto.getBizOrgRoleId());
                }
            });
        }

        return relatedIds;
    }

    /**
     * 权限角色删除处理
     *
     * @param roleUuid
     */
    @Override
    @Transactional
    public void deleteRelaRoleByRoleUuid(String roleUuid) {
        // 删除用户角色
        userInfoService.deleteUserRoleByRoleUuid(roleUuid);

        // 删除组织元素角色
        orgElementService.deleteElementRoleRelaByRoleUuid(roleUuid);

        // 删除群组角色
        orgGroupService.deleteGroupRoleByRoleUuid(roleUuid);
    }

    /**
     * 添加权限角色成员
     *
     * @param roleUuid
     * @param memberIds
     */
    @Override
    @Transactional
    public void addRelaRoleMembers(String roleUuid, List<String> memberIds) {
        if (CollectionUtils.isEmpty(memberIds)) {
            return;
        }

        List<String> userIds = Lists.newArrayList();
        List<String> orgEleIds = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        Map<Long, List<String>> versionOrgEleIds = Maps.newHashMap();
        Map<String, Long> versionIdUuidMap = Maps.newHashMap();
        memberIds.forEach(id -> {
            if (id.startsWith(IdPrefix.USER.getValue())) {
                userIds.add(id);
            } else if (id.startsWith(IdPrefix.GROUP.getValue())) {
                groupIds.add(id);
            } else {
                if (id.startsWith(IdPrefix.ORG_VERSION.getValue() + Separator.UNDERLINE.getValue())) {
                    String[] parts = id.split(Separator.SLASH.getValue());
                    String versionId = parts[0];
                    List<String> ids = null;
                    if (versionIdUuidMap.containsKey(versionId)) {
                        ids = versionOrgEleIds.get(versionIdUuidMap.get(versionId));
                    } else {
                        OrgVersionEntity versionEntity = orgVersionService.getById(versionId);
                        versionIdUuidMap.put(versionEntity.getId(), versionEntity.getUuid());
                        versionOrgEleIds.put(versionEntity.getUuid(), Lists.newArrayList());
                        ids = versionOrgEleIds.get(versionEntity.getUuid());
                    }
                    if (ids != null) {
                        ids.add(parts[parts.length - 1]);
                    }
                } else if (NumberUtils.isNumber(id)) {
                    OrgElementEntity entity = orgElementService.getOne(Long.parseLong(id));
                    if (entity != null) {
                        if (!versionOrgEleIds.containsKey(entity.getOrgVersionUuid())) {
                            versionOrgEleIds.put(entity.getOrgVersionUuid(), Lists.newArrayList());
                        }
                        versionOrgEleIds.get(entity.getOrgVersionUuid()).add(entity.getId());
                    }
                } else {
                    orgEleIds.add(id);// 不带版本号的组织单元实例ID
                }
            }
        });

        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfoService.addUserRoleByIdsAndRoleUuid(userIds, roleUuid);
        }
        if (MapUtils.isNotEmpty(versionOrgEleIds)) {
            Set<Map.Entry<Long, List<String>>> entries = versionOrgEleIds.entrySet();
            for (Map.Entry<Long, List<String>> en : entries) {
                orgElementService.addElementRoleRelaByIdsAndRoleUuid(en.getValue(), roleUuid, en.getKey());
            }
        }
        if (CollectionUtils.isNotEmpty(orgEleIds)) {
            orgElementService.addElementRoleRelaByIdsAndRoleUuid(orgEleIds, roleUuid, null);
        }

        if (CollectionUtils.isNotEmpty(groupIds)) {
            orgGroupService.addGroupRoleByIdsAndRoleUuid(groupIds, roleUuid);
        }
    }

    @Override
    @Transactional
    public void deleteRoleRelaUsers(String roleUuid, List<String> userUuids) {
        userInfoService.deleteUserRoleByRoleUuidAndUserIds(roleUuid, userUuids);
    }

    /**
     * 根据角色信息获取相关成员信息
     *
     * @param roleUuid
     * @return
     */
    @Override

    public OrgRelaRoleMembersDto getRelaRoleMembersByRoleUuid(String roleUuid) {
        OrgRelaRoleMembersDto roleMembersDto = new OrgRelaRoleMembersDto();
        List<UserInfoEntity> userInfoEntities = userInfoService.listByRoleUuid(roleUuid);

        List<OrgElementEntity> elementEntities = orgElementService.listByRelaRoleUuid(roleUuid);

        List<OrgGroupEntity> groupEntities = orgGroupService.listByRoleUuid(roleUuid);

        userInfoEntities.forEach(userInfoEntity -> roleMembersDto.getUsers().put(userInfoEntity.getUserId(), userInfoEntity.getUserName()));
        elementEntities.forEach(orgElementEntity -> roleMembersDto.getOrgElements().put(orgElementEntity.getId(), orgElementEntity.getName()));
        groupEntities.forEach(groupEntity -> roleMembersDto.getGroups().put(groupEntity.getId(), groupEntity.getName()));
        return roleMembersDto;
    }

    @Override
    public void deleteRelaRoleMembers(String roleUuid, List<String> memberIds) {
        if (CollectionUtils.isEmpty(memberIds)) {
            return;
        }

        List<String> userIds = Lists.newArrayList();
        List<String> orgEleIds = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        memberIds.forEach(id -> {
            if (id.startsWith(IdPrefix.USER.getValue())) {
                userIds.add(id);
            } else if (id.startsWith(IdPrefix.GROUP.getValue())) {
                groupIds.add(id);
            } else {
                orgEleIds.add(id);
            }
        });

        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfoService.deleteUserRoleByRoleUuidAndUserIds(roleUuid, userIds);
        }
        if (CollectionUtils.isNotEmpty(orgEleIds)) {
            orgElementService.deleteOrgElementRoleRelaByIdsAndRoleUuid(orgEleIds, roleUuid, null);
        }
        if (CollectionUtils.isNotEmpty(groupIds)) {
            orgGroupService.deleteGroupRoleByIdsAndRoleUuid(groupIds, roleUuid);
        }
    }

    @Override
    public List<OrgUserEntity> getAllOrgUserUnderDefaultPublishedOrgVersion(String userId, String system, String tenant) {
        return orgUserService.getAllOrgUserUnderDefaultPublishedOrgVersion(userId, system, tenant);
    }

    @Override
    public List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, String system, String tenant) {
        return orgUserService.getAllOrgUserUnderPublishedOrgVersion(userId, system, tenant);
    }

    @Override
    public OrgElementEntity getOrgElementByIdVersionUuid(String id, Long orgVersionUuid) {
        OrgElementEntity orgElementEntity = orgElementService.getByIdAndOrgVersionUuid(id, orgVersionUuid);
        if (orgElementEntity != null) {
            OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(id, orgVersionUuid);
            orgElementEntity.setPathEntity(pathEntity);
            if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                pathEntity.setLocalPath(orgElementPathService.getLocaleOrgElementPath(orgElementEntity.getUuid(), LocaleContextHolder.getLocale().toString()));
                List<OrgElementI18nEntity> i18nEntities = orgElementI18nService.getOrgElementI18nsByDataUuidAndLocale(orgElementEntity.getUuid(), LocaleContextHolder.getLocale().toString());
                if (CollectionUtils.isNotEmpty(i18nEntities)) {
                    for (OrgElementI18nEntity i : i18nEntities) {
                        if ("name".equals(i.getDataCode())) {
                            orgElementEntity.setLocalName(i.getContent());
                        } else if ("short_name".equals(i.getDataCode())) {
                            orgElementEntity.setLocalShortName(i.getContent());
                        }
                    }
                }
            }
        }

        return orgElementEntity;
    }

    @Override
    public OrgElementEntity getOrgElementByIdAndOrgVersionId(String id, String orgVersionId) {
        OrgElementEntity entity = null;
        if (StringUtils.isNotBlank(orgVersionId)) {
            OrgVersionEntity versionEntity = orgVersionService.getById(orgVersionId);
            if (versionEntity != null) {
                entity = this.getOrgElementByIdVersionUuid(id, versionEntity.getUuid());
            }
        } else {
            Map<String, Object> params = Maps.newHashMap();
            params.put("id", id);
            params.put("state", OrgVersionEntity.State.PUBLISHED);
            List<OrgElementEntity> list = orgElementService.listByHQL("from OrgElementEntity where id=:id and state=:state", params);
            if (CollectionUtils.isNotEmpty(list)) {
                entity = list.get(0);
            }
        }
        if (entity != null && !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            OrgElementI18nEntity i18nEntity = orgElementI18nService.getOrgElementI18n(entity.getUuid(), "name", LocaleContextHolder.getLocale().toString());
            if (i18nEntity != null) {
                entity.setLocalName(i18nEntity.getContent());
            }
        }
        return entity;
    }

    @Override
    public OrgElementEntity getOrgElementByUuid(Long orgElementUuid) {
        return orgElementService.getOne(orgElementUuid);
    }

    @Override
    public String getOrgElementPathByUuid(Long orgElementUuid) {
        OrgElementPathEntity pathEntity = orgElementPathService.getByOrgEleUuid(orgElementUuid);
        return pathEntity == null ? null : pathEntity.getIdPath();
    }

    @Override
    public String getOrgElementPathById(String orgElementId) {
        if (StringUtils.isBlank(orgElementId)) {
            return null;
        }

        OrgElementPathEntity pathEntity = orgElementPathService.getByOrgEleId(orgElementId);
        return pathEntity == null ? null : pathEntity.getIdPath();
    }

    @Override
    public boolean existSystemOrgUser(String userId, String system, String tenant) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("system", system);
        params.put("tenant", tenant);
        List<QueryItem> items = orgUserService.listQueryItemBySQL("select 1 from org_user where user_id=:userId and system=:system and tenant=:tenant", params, null);
        return CollectionUtils.isNotEmpty(items);
    }

    @Override
    public Map<String, String> getUserIdNamesByOrgElementIds(List<String> orgElementIds) {
        return orgUserService.getUserIdNamesByOrgElementIds(orgElementIds);
    }

    @Override
    public Set<String> listMemberIdByGroupIds(List<String> groupIds) {
        if (CollectionUtils.isEmpty(groupIds)) {
            return Collections.emptySet();
        }
        return orgGroupService.listMemberIdByIds(groupIds);
    }

    @Override
    public List<OrgGroupMemberEntity> listMemberByGroupId(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            return Collections.emptyList();
        }
        return orgGroupService.listMemberById(groupId);
    }

    @Override
    public UserInfoEntity getTenantManagerInfo(String system, String tenant) {
        List<UserInfoEntity> userInfoEntities = userInfoService.getTenantManagerInfo(system, tenant);
        return CollectionUtils.isNotEmpty(userInfoEntities) ? userInfoEntities.get(0) : null;
    }

    /**
     * @param ids
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByIds(List<String> ids, String[] bizOrgIds) {
        Map<String, String> map = Maps.newLinkedHashMap();
        Map<String, Set<String>> groupMap = getIdGroupMap(ids);
        Set<String> userIds = groupMap.get("userIds");
        Set<String> eleIds = groupMap.get("eleIds");

        Map<String, String> userMap = userInfoService.listBizOrgUserAsMapByUserIds(userIds, eleIds, bizOrgIds);
        map.putAll(userMap);
        return map;
    }

    @Override
    public Map<String, String> getBizOrgUsersByIdWithBizRoleId(String idWithBizRoleId, String bizOrgId) {
        String[] ids = StringUtils.split(idWithBizRoleId, Separator.SLASH.getValue());
        String elementId = ids[ids.length - 2];
        String roleId = ids[ids.length - 1];
        return userInfoService.listBizOrgUserAsMapByIdAndBizRoleId(elementId, roleId, bizOrgId);
    }

    /**
     * @param ids
     * @return
     */
    private Map<String, Set<String>> getIdGroupMap(List<String> ids) {
        Map<String, String> map = Maps.newLinkedHashMap();
        Set<String> userIds = Sets.newLinkedHashSet();
        Set<String> groupIds = Sets.newLinkedHashSet();
        Set<String> eleIds = Sets.newLinkedHashSet();
        ids.forEach(id -> {
            String idValue = id;
            if (StringUtils.contains(id, Separator.SLASH.getValue())) {
                String[] idParts = StringUtils.split(id, Separator.SLASH.getValue());
                idValue = idParts[idParts.length - 1];
                // 业务角色ID不进行拆分处理
                if (idParts.length == 2 && (StringUtils.startsWith(idParts[0], IdPrefix.BIZ_ORG_DIM.getValue())
                        || StringUtils.startsWith(idParts[0], IdPrefix.BIZ_PREFIX.getValue()))
                        && !IdPrefix.hasPrefix(idParts[1])) {
                    idValue = id;
                }
            }
            if (StringUtils.startsWith(idValue, IdPrefix.USER.getValue())) {
                userIds.add(idValue);
            } else if (StringUtils.startsWith(idValue, IdPrefix.GROUP.getValue())) {
                groupIds.add(idValue);
            } else {
                eleIds.add(idValue);
            }
        });

        Map<String, Set<String>> groupMap = Maps.newHashMap();
        groupMap.put("userIds", userIds);
        groupMap.put("groupIds", groupIds);
        groupMap.put("eleIds", eleIds);
        return groupMap;
    }

    /**
     * @param ids
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByIdsAndBizRoleIds(List<String> ids, List<String> bizRoleIds, String[] bizOrgIds) {
        Map<String, String> map = Maps.newLinkedHashMap();
        Map<String, Set<String>> groupMap = getIdGroupMap(ids);
        Set<String> userIds = groupMap.get("userIds");
        Set<String> eleIds = groupMap.get("eleIds");

        Map<String, String> userMap = userInfoService.listBizOrgUserAsMapByUserIdsAndBizRoleIds(userIds, eleIds, bizRoleIds, bizOrgIds);
        map.putAll(userMap);
        return map;
    }

    /**
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> getBizOrgUsersByBizRoleIds(List<String> bizRoleIds, String[] bizOrgIds) {
        Map<String, String> userMap = userInfoService.listBizOrgUserAsMapByBizRoleIds(bizRoleIds, bizOrgIds);
        return userMap;
    }

    /**
     * @param userId
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Set<String> listUserBizItemAndBizRoleUserId(String userId, List<String> bizRoleIds, String[] bizOrgIds) {
        List<String> elementIdPaths = bizOrgElementService.listIdPathsByUserId(userId, bizOrgIds);
        // List<BizOrgElementEntity> elementEntities = bizOrgElementService.listByUserId(userId, true, bizOrgIds);
        if (CollectionUtils.isEmpty(elementIdPaths)) {
            return Collections.emptySet();
        }

        List<String> eleIds = Lists.newArrayList();
        elementIdPaths.forEach(idPath -> {
            List<String> ids = Arrays.asList(StringUtils.split(idPath, Separator.SLASH.getValue()));
            Collections.reverse(ids);
            for (String eleId : ids) {
                if (StringUtils.startsWith(eleId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                    eleIds.add(eleId);
                    break;
                }
            }
        });

        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, bizRoleIds, true, false);
        return Sets.newLinkedHashSet(memberIds);
    }

    @Override
    public Set<String> listUserBizDepartmentUserId(String userId, String jobIdentityId, boolean sameBizRole, String[] bizOrgIds) {
        List<BizOrgElementEntity> elementEntities = bizOrgElementService.listByUserIdAndElementTypes(userId, Lists.newArrayList(OrgElementModelEntity.ORG_DEPT_ID), bizOrgIds);
        elementEntities = filterByJobIdentityId(elementEntities, jobIdentityId);
        if (CollectionUtils.isEmpty(elementEntities)) {
            return Collections.emptySet();
        }

        List<String> memberIds = Lists.newArrayList();
        List<String> eleIds = elementEntities.stream().map(BizOrgElementEntity::getId).collect(Collectors.toList());
        // 同业务角色
        if (sameBizRole) {
            List<BizOrgElementMemberEntity> memberEntities = bizOrgElementMemberService.listByMemberIdAndBizOrgElementIds(userId, eleIds, bizOrgIds);
            memberEntities.forEach(memberEntity -> {
                List<String> bizRoleIds = Lists.newArrayList(memberEntity.getBizOrgRoleId());
                List<String> bizOrgElementIds = Lists.newArrayList(memberEntity.getBizOrgElementId());
                memberIds.addAll(bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(bizOrgElementIds, bizRoleIds, true, false));
            });
        } else {
            memberIds.addAll(bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, null, true, false));
        }
        return Sets.newLinkedHashSet(memberIds);
    }

    private List<BizOrgElementEntity> filterByJobIdentityId(List<BizOrgElementEntity> elementEntities, String jobIdentityId) {
        if (CollectionUtils.isEmpty(elementEntities) || StringUtils.isBlank(jobIdentityId)) {
            return elementEntities;
        }

        List<String> identityEleIds = Lists.newArrayList();
        // List<String> identityRoleIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobIdentityId)) {
            Arrays.stream(StringUtils.split(jobIdentityId, Separator.SEMICOLON.getValue())).forEach(identity -> {
                String[] parts = StringUtils.split(identity, Separator.SLASH.getValue());
                identityEleIds.add(parts[0]);
//                if (parts.length > 1) {
//                    identityRoleIds.add(parts[1]);
//                }
            });
        }
        if (CollectionUtils.isNotEmpty(identityEleIds)) {
            elementEntities = elementEntities.stream().filter(bizOrgElementEntity -> {
                return identityEleIds.contains(bizOrgElementEntity.getId());
            }).collect(Collectors.toList());
        }
        return elementEntities;
    }

    private List<String> filterIdPathByJobIdentityId(List<String> idPaths, String jobIdentityId) {
        if (CollectionUtils.isEmpty(idPaths) || StringUtils.isBlank(jobIdentityId)) {
            return idPaths;
        }

        List<String> identityEleIds = Lists.newArrayList();
        // List<String> identityRoleIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(jobIdentityId)) {
            Arrays.stream(StringUtils.split(jobIdentityId, Separator.SEMICOLON.getValue())).forEach(identity -> {
                String[] parts = StringUtils.split(identity, Separator.SLASH.getValue());
                identityEleIds.add(parts[0]);
//                if (parts.length > 1) {
//                    identityRoleIds.add(parts[1]);
//                }
            });
        }
        if (CollectionUtils.isNotEmpty(identityEleIds)) {
            idPaths = idPaths.stream().filter(idPath -> {
                return identityEleIds.stream().filter(eleId -> idPath.contains(eleId)).findFirst().isPresent();
            }).collect(Collectors.toList());
        }
        return idPaths;
    }

    @Override
    public Set<String> listUserBizDepartmentAndBizRoleUserId(String userId, List<String> bizRoleIds, String[] bizOrgIds) {
        List<BizOrgElementEntity> elementEntities = bizOrgElementService.listByUserIdAndElementTypes(userId, Lists.newArrayList(OrgElementModelEntity.ORG_DEPT_ID), bizOrgIds);
        if (CollectionUtils.isEmpty(elementEntities)) {
            return Collections.emptySet();
        }

        List<String> eleIds = elementEntities.stream().map(BizOrgElementEntity::getId).collect(Collectors.toList());
        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, bizRoleIds, true, false);
        return Sets.newLinkedHashSet(memberIds);
    }

    @Override
    public Set<String> listUserBizParentDepartmentUserId(String userId, String jobIdentityId, String[] bizOrgIds) {
        List<BizOrgElementEntity> elementEntities = listUserBizParentDepartment(userId, jobIdentityId, bizOrgIds);
        if (CollectionUtils.isEmpty(elementEntities)) {
            return Collections.emptySet();
        }

        List<String> eleIds = elementEntities.stream().map(BizOrgElementEntity::getId).collect(Collectors.toList());
        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, null, true, false);
        return Sets.newLinkedHashSet(memberIds);
    }

    /**
     * @param userId
     * @param jobIdentityId
     * @param bizOrgIds
     * @return
     */
    private List<BizOrgElementEntity> listUserBizParentDepartment(String userId, String jobIdentityId, String[] bizOrgIds) {
        List<BizOrgElementEntity> elementEntities = bizOrgElementService.listParentByUserId(userId, bizOrgIds);
        elementEntities = filterByJobIdentityId(elementEntities, jobIdentityId);
        if (CollectionUtils.isEmpty(elementEntities)) {
            return Collections.emptyList();
        }

        List<BizOrgElementEntity> deptElementEntities = elementEntities.stream().filter(bizOrgElementEntity -> OrgElementModelEntity.ORG_DEPT_ID.equals(bizOrgElementEntity.getElementType())).collect(Collectors.toList());
        // 上级节点是是维度节点，取维度的上级部门节点
        List<Long> parentOfSkipDimensionElementEntities = elementEntities.stream().flatMap(bizOrgElementEntity -> {
            if (bizOrgElementEntity.getParentUuid() == null) {
                return Stream.empty();
            }
            if (bizOrgElementEntity.getIsDimension()) {
                return Stream.of(bizOrgElementEntity.getParentUuid());
            }
            return Stream.empty();
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(parentOfSkipDimensionElementEntities)) {
            List<BizOrgElementEntity> parentElementEntities = bizOrgElementService.listByUuids(parentOfSkipDimensionElementEntities);
            parentElementEntities.stream().filter(bizOrgElementEntity -> OrgElementModelEntity.ORG_DEPT_ID.equals(bizOrgElementEntity.getElementType())).forEach(deptElementEntities::add);
        }
        return deptElementEntities;
    }

    @Override
    public Set<String> listUserBizParentDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> bizRoleIds, String[] bizOrgIds) {
        List<BizOrgElementEntity> elementEntities = listUserBizParentDepartment(userId, jobIdentityId, bizOrgIds);
        if (CollectionUtils.isEmpty(elementEntities)) {
            return Collections.emptySet();
        }

        List<String> eleIds = elementEntities.stream().map(BizOrgElementEntity::getId).collect(Collectors.toList());
        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, bizRoleIds, true, false);
        return Sets.newLinkedHashSet(memberIds);
    }

    @Override
    public Set<String> listUserBizRootDepartmentUserId(String userId, String jobIdentityId, String[] bizOrgIds) {
        List<String> eleIds = getRootBizDepartmentIds(userId, jobIdentityId, bizOrgIds);
        if (CollectionUtils.isEmpty(eleIds)) {
            return Collections.emptySet();
        }

        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, null, true, false);
        return Sets.newLinkedHashSet(memberIds);
    }

    @Override
    public Set<String> listUserBizRootDepartmentAndBizRoleUserId(String userId, String jobIdentityId, List<String> bizRoleIds, String[] bizOrgIds) {
        List<String> eleIds = getRootBizDepartmentIds(userId, jobIdentityId, bizOrgIds);
        if (CollectionUtils.isEmpty(eleIds)) {
            return Collections.emptySet();
        }

        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, bizRoleIds, true, false);
        return Sets.newLinkedHashSet(memberIds);
    }

    /**
     * @param userId
     * @param bizOrgIds
     * @return
     */
    private List<String> getRootBizDepartmentIds(String userId, String jobIdentityId, String[] bizOrgIds) {
        List<String> elementIdPaths = bizOrgElementService.listIdPathsByUserId(userId, bizOrgIds);
        elementIdPaths = filterIdPathByJobIdentityId(elementIdPaths, jobIdentityId);
        if (CollectionUtils.isEmpty(elementIdPaths)) {
            return Collections.emptyList();
        }

        Set<String> idSet = elementIdPaths.stream().flatMap(path -> Arrays.stream(StringUtils.split(path, Separator.SLASH.getValue())))
                .collect(Collectors.toSet());
        List<BizOrgElementEntity> elementEntities = bizOrgElementService.listByIds(Arrays.asList(idSet.toArray(new String[0])));
        Map<String, BizOrgElementEntity> elementEntityMap = ConvertUtils.convertElementToMap(elementEntities, "id");
        List<String> eleIds = Lists.newArrayList();
        elementIdPaths.forEach(path -> {
            String[] paths = StringUtils.split(path, Separator.SLASH.getValue());
            for (String elId : paths) {
                BizOrgElementEntity element = elementEntityMap.get(elId);
                if (element != null && OrgElementModelEntity.ORG_DEPT_ID.equals(element.getElementType())) {
                    eleIds.add(elId);
                    break;
                }
            }
        });
        return eleIds;
    }

    @Override
    public Set<String> listUserBizRootNodeUserId(String userId, String[] bizOrgIds) {
        List<String> elementIdPaths = bizOrgElementService.listIdPathsByUserId(userId, bizOrgIds);
        if (CollectionUtils.isEmpty(elementIdPaths)) {
            return Collections.emptySet();
        }

        List<String> eleIds = elementIdPaths.stream().map(path -> StringUtils.split(path, Separator.SLASH.getValue())[0]).collect(Collectors.toList());
        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, null, true, true);
        return Sets.newLinkedHashSet(memberIds);
    }

    @Override
    public Set<String> listUserBizRootNodeAndBizRoleUserId(String userId, List<String> bizRoleIds, String[] bizOrgIds) {
        List<String> elementIdPaths = bizOrgElementService.listIdPathsByUserId(userId, bizOrgIds);
        if (CollectionUtils.isEmpty(elementIdPaths)) {
            return Collections.emptySet();
        }

        List<String> eleIds = elementIdPaths.stream().map(path -> StringUtils.split(path, Separator.SLASH.getValue())[0]).collect(Collectors.toList());
        List<String> memberIds = bizOrgElementMemberService.listMemberIdByBizOrgElementIdsAndBizRoleIds(eleIds, bizRoleIds, true, true);
        return Sets.newLinkedHashSet(memberIds);
    }

    @Override
    public Set<String> listUserBizRoleUserId(String userId, String[] bizOrgIds) {
        List<String> memberIds = bizOrgElementMemberService.listSameBizRoleMemberId(userId, bizOrgIds);
        return Sets.newLinkedHashSet(memberIds);
    }


    @Override
    public List<OrgSelectProvider.Node> explainOrgElementIdsToNode(Set<String> elementIds) {
        List<String> userIds = Lists.newArrayList();
        List<String> orgEleIds = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        List<OrgSelectProvider.Node> result = Lists.newArrayList();
        Map<Long, List<String>> versionOrgEleIds = Maps.newHashMap();
        Map<String, Long> versionIdUuidMap = Maps.newHashMap();
        elementIds.forEach(id -> {
            if (id.startsWith(IdPrefix.USER.getValue())) {
                userIds.add(id);
            } else if (id.startsWith(IdPrefix.GROUP.getValue())) {
                groupIds.add(id);
            } else {
                if (id.startsWith(IdPrefix.ORG_VERSION.getValue() + Separator.UNDERLINE.getValue())) {
                    String[] parts = id.split(Separator.SLASH.getValue());
                    String versionId = parts[0];
                    List<String> ids = null;
                    if (versionIdUuidMap.containsKey(versionId)) {
                        ids = versionOrgEleIds.get(versionIdUuidMap.get(versionId));
                    } else {
                        OrgVersionEntity versionEntity = orgVersionService.getById(versionId);
                        versionIdUuidMap.put(versionEntity.getId(), versionEntity.getUuid());
                        versionOrgEleIds.put(versionEntity.getUuid(), Lists.newArrayList());
                        ids = versionOrgEleIds.get(versionEntity.getUuid());
                    }
                    if (ids != null) {
                        ids.add(parts[parts.length - 1]);
                    }
                } else if (NumberUtils.isNumber(id)) {
                    OrgElementEntity entity = orgElementService.getOne(Long.parseLong(id));
                    if (entity != null) {
                        if (!versionOrgEleIds.containsKey(entity.getOrgVersionUuid())) {
                            versionOrgEleIds.put(entity.getOrgVersionUuid(), Lists.newArrayList());
                        }
                        versionOrgEleIds.get(entity.getOrgVersionUuid()).add(entity.getId());
                    }
                } else {
                    orgEleIds.add(id);// 不带版本号的组织单元实例ID
                }
            }
        });

        if (CollectionUtils.isNotEmpty(userIds)) {
            List<UserInfoEntity> userInfoEntities = getUserInfosByUserId(userIds);
            if (CollectionUtils.isNotEmpty(userInfoEntities)) {
                for (UserInfoEntity u : userInfoEntities) {
                    OrgSelectProvider.Node node = new OrgSelectProvider.Node();
                    node.setData(u);
                    node.setType("user");
                    node.setTypeName("用户");
                    node.setKey(u.getUserId());
                    node.setTitle(u.getUserName());
                    result.add(node);
                }
            }
        }
        List<OrgElementEntity> orgElementEntities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orgEleIds)) {
            orgElementEntities.addAll(orgElementService.listByIdsAndOrgVersionId(orgEleIds.toArray(new String[]{}), null));
        }
        if (MapUtils.isNotEmpty(versionOrgEleIds)) {
            Set<Map.Entry<Long, List<String>>> entries = versionOrgEleIds.entrySet();
            for (Map.Entry<Long, List<String>> en : entries) {
                orgElementEntities.addAll(orgElementService.listByIdsAndOrgVersionId(en.getValue().toArray(new String[]{}), en.getKey()));

            }
        }
        List<OrgElementModelEntity> modelEntities = orgElementModelService.listOrgElementModels(SpringSecurityUtils.getCurrentTenantId(), RequestSystemContextPathResolver.system());
        Map<String, OrgElementModelEntity> modelMap = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(modelEntities)) {
            for (OrgElementModelEntity m : modelEntities) {
                modelMap.put(m.getId(), m);
            }
        }
        if (CollectionUtils.isNotEmpty(orgElementEntities)) {
            for (OrgElementEntity elementEntity : orgElementEntities) {
                OrgSelectProvider.Node node = new OrgSelectProvider.Node();
                node.setData(elementEntity);
                node.setType(elementEntity.getType());
                node.setKey(elementEntity.getId());
                node.setTitle(elementEntity.getName());
                if (modelMap.containsKey(elementEntity.getType())) {
                    node.setTypeName(modelMap.get(elementEntity.getType()).getName());
                }
                result.add(node);
            }
        }

        if (CollectionUtils.isNotEmpty(groupIds)) {
            List<OrgGroupEntity> orgGroupEntities = orgGroupService.listByIds(groupIds);
            if (CollectionUtils.isNotEmpty(orgGroupEntities)) {
                for (OrgGroupEntity group : orgGroupEntities) {
                    OrgSelectProvider.Node node = new OrgSelectProvider.Node();
                    node.setData(group);
                    node.setType("group");
                    node.setTypeName("群组");
                    node.setKey(group.getId());
                    node.setTitle(group.getName());
                    result.add(node);
                }
            }
        }
        return result;
    }

    @Override
    public String getLocaleOrgElementPath(String orgElementId, Long orgVersionUuid, String locale) {
        return orgElementPathService.getLocaleOrgElementPath(orgElementId, orgVersionUuid, locale);

    }

    @Override
    public String getLocaleOrgElementPath(Long orgElementUuid, String locale) {
        return orgElementPathService.getLocaleOrgElementPath(orgElementUuid, locale);
    }

    @Override
    public Map<Long, String> getLocaleOrgElementPaths(List<Long> orgElementUuids, String locale) {
        return orgElementPathService.getLocaleOrgElementPaths(orgElementUuids, locale);
    }

    @Override
    public List<OrgElementI18nEntity> getLocaleOrgElementI18nsByDataIds(List<String> dataId, String dataCode, String locale) {
        return orgElementI18nService.getOrgElementI18ns(Sets.newHashSet(dataId), dataCode, locale);
    }

    @Override
    public List<OrgElementI18nEntity> getLocaleOrgElementI18nsByDataUuids(List<Long> dataUuid, String dataCode, String locale) {
        return orgElementI18nService.listOrgElementI18ns(Sets.newHashSet(dataUuid), dataCode, locale);
    }

    @Override
    public List<OrgTreeNodeDto> getUserBizOrgRolesByOrgUuid(String userId, Long orgUuid) {
        return bizOrgElementMemberService.getUserBizOrgRolesByOrgUuid(userId, orgUuid);
    }


    @Override
    public long countOrgElementByOrgVersionUuid(Long orgVersionUuid) {
        return orgElementService.countByOrgVersionUuid(orgVersionUuid);
    }

    @Override
    public OrgElementDto getOrgElementDetailsByUuid(Long orgElementUuid) {
        return orgElementService.getDetails(orgElementUuid);
    }

    @Override
    public List<String> listOrgElementRoleUuidByUuid(Long orgElementUuid) {
        return orgElementService.listRoleUuidByUuid(orgElementUuid);
    }

    @Override
    public OrgElementManagementEntity getOrgElementManagementByUuid(Long orgElementUuid) {
        return orgElementManagementService.getByOrgElementUuid(orgElementUuid);
    }

    @Override
    @Transactional
    public Long saveOrgElementDetails(OrgElementDto orgElementDto) {
        return orgElementService.saveOrgElement(orgElementDto);
    }

    @Override
    @Transactional
    public void saveOrgElementLeader(Long orgElementUuid, String director, String leader, String manager) {
        orgElementService.saveOrgElementLeader(orgElementUuid, director, leader, manager);
    }

    @Override
    @Transactional
    public void deleteOrgElementByUuid(Long orgElementUuid) {
        orgElementService.deleteOrgElement(orgElementUuid);
    }

    @Override
    @Transactional
    public String saveUser(UserDto userDto) {
        return userInfoService.saveUser(userDto);
    }

    @Override
    @Transactional
    public void saveUserInfo(UserInfoEntity userInfoEntity) {
        userInfoService.save(userInfoEntity);
    }

    @Override
    @Transactional
    public void saveUserReportRelation(String userId, Map<String, List<String>> orgElementIdReport, Long orgVersionUuid) {
        userInfoService.saveUserReportRelation(userId, orgElementIdReport, orgVersionUuid);
    }

    @Override
    @Transactional
    public void joinOrgUser(List<String> userIds, String jobId, List<String> directReporter, String orgElementId, Long orgVersionUuid) {
        OrgUserElementDto orgUserElementDto = new OrgUserElementDto();
        orgUserElementDto.setUserIds(userIds);
        orgUserElementDto.setJobId(jobId);
        orgUserElementDto.setDirectReporter(directReporter);
        orgUserElementDto.setOrgElementId(orgElementId);
        orgUserElementDto.setOrgVersionUuid(orgVersionUuid);
        orgUserService.addUser(orgUserElementDto);
    }

    @Override
    @Transactional
    public void removeOrgUser(List<String> userIds, Long orgVersionUuid) {
        this.removeOrgUser(userIds, null, orgVersionUuid);
    }

    @Override
    @Transactional
    public void removeOrgUser(List<String> userIds, String orgElementId, Long orgVersionUuid) {
        OrgUserElementDto orgUserElementDto = new OrgUserElementDto();
        orgUserElementDto.setUserIds(userIds);
        orgUserElementDto.setOrgVersionUuid(orgVersionUuid);
        orgUserElementDto.setOrgElementId(orgElementId);
        orgUserService.removeUser(orgUserElementDto);
    }

    @Override
    @Transactional
    public OrgVersionEntity createEmptyOrgVersionFromOrgVersion(OrgVersionEntity orgVersionEntity, boolean copyUser, boolean publish) {
        OrgVersionEntity newOrgVersionEntity = orgVersionService.createEmptyOrgVersionFromOrgVersion(orgVersionEntity, copyUser);
        if (publish) {
            orgVersionService.updatePublished(newOrgVersionEntity.getUuid());
            newOrgVersionEntity = orgVersionService.getOne(newOrgVersionEntity.getUuid());
        }
        return newOrgVersionEntity;
    }

    @Override
    public List<String> listOrgIdByBizOrgIds(List<String> bizOrgIds) {
        return organizationService.listIdByBizOrgIds(bizOrgIds);
    }

}
