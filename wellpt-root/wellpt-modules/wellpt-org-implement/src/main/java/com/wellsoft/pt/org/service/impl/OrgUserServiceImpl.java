package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import com.wellsoft.pt.org.dao.impl.OrgUserDaoImpl;
import com.wellsoft.pt.org.dto.OrgJobDutyDto;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.dto.OrgUserElementDto;
import com.wellsoft.pt.org.dto.OrgUserJobDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.entity.UserInfoExtEntity;
import com.wellsoft.pt.user.service.UserAccountService;
import com.wellsoft.pt.user.service.UserInfoExtService;
import com.wellsoft.pt.user.service.UserInfoService;
import com.wellsoft.pt.user.service.UserRoleService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年01月12日   chenq	 Create
 * </pre>
 */
@Service
public class OrgUserServiceImpl extends AbstractJpaServiceImpl<OrgUserEntity, OrgUserDaoImpl, Long> implements OrgUserService {

    @Resource
    OrgElementService orgElementService;

    @Resource
    OrgVersionService orgVersionService;

    @Resource
    OrgElementPathService orgElementPathService;

    @Resource
    OrgGroupService orgGroupService;

    @Resource
    OrgUserReportRelationService orgUserReportRelationService;
    @Resource
    OrgElementRoleMemberService orgElementRoleMemberService;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserAccountService userAccountService;

    @Autowired
    RoleService roleService;

    @Autowired
    OrgElementPathChainService orgElementPathChainService;

    @Autowired
    OrgElementManagementService orgElementManagementService;

    @Autowired
    OrgFacadeService orgFacadeService;

    @Autowired
    UserInfoExtService userInfoExtService;
    @Autowired
    OrgElementI18nService orgElementI18nService;

    @Override
    @Transactional
    public void deleteByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("orgVersionUuid", orgVersionUuid);
        this.dao.deleteByHQL("delete from OrgUserEntity where orgVersionUuid=:orgVersionUuid", param);
    }

    @Override
    public List<OrgUserEntity> listByOrgVersionUuid(Long orgVersionUuid) {
        return this.dao.listByFieldEqValue("orgVersionUuid", orgVersionUuid);
    }

    @Override
    public List<OrgUserEntity> listByOrgVersionUuidAndType(Long orgVersionUuid, OrgUserEntity.Type type) {
        Assert.notNull(orgVersionUuid, "组织版本UUID不能为空！");
        Assert.notNull(type, "用户类型不能为空！");

        OrgUserEntity entity = new OrgUserEntity();
        entity.setOrgVersionUuid(orgVersionUuid);
        entity.setType(type);
        return this.dao.listByEntity(entity);
    }

    @Transactional
    @Override
    public void deleteOrgUserByOrgVersionUuidAndType(String userId, Long orgVersionUuid, OrgUserEntity.Type type) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("userId", userId);
        param.put("orgVersionUuid", orgVersionUuid);
        param.put("type", type);
        this.dao.deleteByHQL("delete from OrgUserEntity where orgVersionUuid=:orgVersionUuid and userId=:userId " + (type != null ? "and type=:type" : ""), param);
    }

    @Override
    @Transactional
    public List<OrgUserEntity> addOrgUser(String userId, Long
            orgVersionUuid, List<String> orgElementIds, OrgUserEntity.Type type) {
        OrgVersionEntity versionEntity = orgVersionService.getOne(orgVersionUuid);
        if (versionEntity != null) {
            if (CollectionUtils.isNotEmpty(orgElementIds)) {
                List<OrgUserEntity> userEntities = Lists.newArrayList();
                for (String orgElementId : orgElementIds) {
                    OrgUserEntity example = new OrgUserEntity();
                    example.setType(type);
                    example.setOrgVersionUuid(orgVersionUuid);
                    example.setUserId(userId);
                    example.setOrgElementId(orgElementId);
                    List<OrgUserEntity> exists = this.dao.listByEntity(example);
                    if (CollectionUtils.isNotEmpty(exists)) {
                        continue;
                    }
                    OrgElementPathEntity orgElementPathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(orgElementId, orgVersionUuid);
                    OrgElementEntity orgElementEntity = orgElementService.getByIdAndOrgVersionUuid(orgElementId, orgVersionUuid);
                    if (orgElementPathEntity != null) {
                        OrgUserEntity userEntity = new OrgUserEntity();
                        userEntity.setType(type);
                        userEntity.setOrgElementId(orgElementId);
                        userEntity.setOrgVersionUuid(orgVersionUuid);
                        userEntity.setUserId(userId);
                        userEntity.setTenant(versionEntity.getTenant());
                        userEntity.setSystem(versionEntity.getSystem());
                        if (CollectionUtils.isNotEmpty(listByEntity(userEntity))) {
                            continue;
                        }

                        userEntity.setOrgUuid(versionEntity.getOrgUuid());

                        userEntity.setOrgUserId(IdPrefix.ORG_USER.getValue() + Separator.UNDERLINE.getValue() + DigestUtils.md5Hex(orgElementId + userId));
                        userEntity.setUserPath(orgElementPathEntity.getIdPath() + Separator.SLASH.getValue() + userId);

                        userEntity.setOrgElementType(orgElementEntity.getType());
                        userEntities.add(userEntity);
                    }
                }
                if (CollectionUtils.isNotEmpty(userEntities)) {
                    this.dao.saveAll(userEntities);
                }
                return userEntities;

            } else {
                // 判断是否已经添加到该组织内
                Map<String, Object> params = Maps.newHashMap();
                params.put("orgVersionUuid", orgVersionUuid);
                params.put("type", type);
                params.put("userId", userId);
                List<OrgUserEntity> userEntities = this.dao.listByHQL("from OrgUserEntity where " +
                        "orgVersionUuid=:orgVersionUuid and userId=:userId and type=:type and orgElementId is null", params);
                if (CollectionUtils.isEmpty(userEntities)) {
                    OrgUserEntity userEntity = new OrgUserEntity();
                    userEntity.setOrgUuid(versionEntity.getOrgUuid());
                    userEntity.setOrgVersionUuid(orgVersionUuid);
                    userEntity.setUserId(userId);
                    userEntity.setUserPath(userId);
                    userEntity.setOrgUserId(IdPrefix.ORG_USER.getValue() + Separator.UNDERLINE.getValue() + DigestUtils.md5Hex(orgVersionUuid + userId));
                    userEntity.setType(type);
                    userEntity.setTenant(versionEntity.getTenant());
                    userEntity.setSystem(versionEntity.getSystem());
                    this.dao.save(userEntity);
                    return Lists.newArrayList(userEntity);
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public List<OrgUserEntity> saveUserJobs(String userId, Long orgVersionUuid, List<String> jobIds, OrgUserEntity.Type type) {
        this.deleteOrgUserByOrgVersionUuidAndType(userId, orgVersionUuid, type);
        if (CollectionUtils.isNotEmpty(jobIds)) {
            return this.addOrgUser(userId, orgVersionUuid, jobIds, type);
        }
        return null;
    }

    /**
     * 将指定用户路径前缀的用户，更新为新的组织元素下的路径
     *
     * @param orgVersionUuid
     * @param elementPathPrefix
     * @param elementPathEntity
     */
    @Override
    @Transactional
    public void updateAllOrgUserByOrgVersionUuidAndElementPathPrefix(Long orgVersionUuid, String elementPathPrefix,
                                                                     OrgElementPathEntity elementPathEntity) {
        Assert.notNull(orgVersionUuid, "组织版本UUID不能为空！");
        Assert.hasLength(elementPathPrefix, "组织元素路径前缀不能为空！");

        String hql = "from OrgUserEntity t where t.orgVersionUuid = :orgVersionUuid and t.userPath like :elementPathPrefix || '%' and t.type = :type";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("elementPathPrefix", elementPathPrefix);
        params.put("type", OrgUserEntity.Type.MEMBER_USER);
        List<OrgUserEntity> orgUserEntities = this.dao.listByHQL(hql, params);

        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            OrgElementEntity elementEntity = null;
            if (elementPathEntity != null) {
                elementEntity = orgElementService.getOne(elementPathEntity.getOrgElementUuid());
            }
            String elementType = elementEntity != null ? elementEntity.getType() : null;
            orgUserEntities.forEach(orgUser -> {
                if (elementPathEntity != null) {
                    orgUser.setUserPath(elementPathEntity.getIdPath() + Separator.SLASH.getValue() + orgUser.getUserId());
                    orgUser.setOrgElementId(elementPathEntity.getOrgElementId());
                    orgUser.setOrgElementType(elementType);
                } else {
                    orgUser.setUserPath(orgUser.getUserId());
                    orgUser.setOrgElementId(null);
                    orgUser.setOrgElementType(null);
                }
            });
            this.saveAll(orgUserEntities);
        }
    }


    @Override
    public List<OrgUserJobDto> listUserJobs(String userId, Long orgVersionUuid) {
        List<OrgUserJobDto> jobDtos = Lists.newArrayList();
        if (orgVersionUuid == null) {
            // 查询默认组织的已发布版本的用户职位信息

        } else {
            Map<String, Object> params = Maps.newHashMap();
            params.put("orgVersionUuid", orgVersionUuid);
            params.put("userId", userId);
            params.put("type", Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER));
            List<OrgUserEntity> orgUserEntities = this.listByHQL("from OrgUserEntity where orgVersionUuid=:orgVersionUuid and userId=:userId and type in (:type) order by type asc", params);
            if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                for (OrgUserEntity userEntity : orgUserEntities) {
                    OrgUserJobDto dto = new OrgUserJobDto();
                    dto.setJobId(userEntity.getOrgElementId());
                    OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(userEntity.getOrgElementId(), orgVersionUuid);
                    dto.setJobIdPath(pathEntity.getIdPath());
                    dto.setJobNamePath(pathEntity.getCnPath());
                    dto.setJobName(pathEntity.getCnPath().substring(pathEntity.getCnPath().lastIndexOf(Separator.SLASH.getValue()) + 1));
                    dto.setOrgVersionUuid(orgVersionUuid);
                    dto.setPrimary(OrgUserEntity.Type.PRIMARY_JOB_USER.equals(userEntity.getType()));
                    jobDtos.add(dto);
                }
            }

        }
        return jobDtos;
    }

    @Override
    public List<OrgUserJobDto> listUserJobs(String userId, String[] orgVersionIds) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        List<OrgUserJobDto> jobDtos = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionIds", orgVersionIds);
        params.put("userId", userId);
        params.put("type", Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER));
        List<OrgUserEntity> orgUserEntities = this.listByHQL("from OrgUserEntity where orgVersionUuid in(select v.uuid from OrgVersionEntity v where v.id in(:orgVersionIds)) and userId=:userId and type in (:type)", params);
        List<OrgUserEntity> mainJobs = orgUserEntities.stream().filter(job -> OrgUserEntity.Type.PRIMARY_JOB_USER.equals(job.getType())).collect(Collectors.toList());
        List<OrgUserEntity> otherJobs = orgUserEntities.stream().filter(job -> OrgUserEntity.Type.SECONDARY_JOB_USER.equals(job.getType())).collect(Collectors.toList());
        orgUserEntities = Lists.newArrayList(mainJobs);
        orgUserEntities.addAll(otherJobs);
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            List<OrgElementPathEntity> pathEntities = getOrgElementPathEntity(orgUserEntities);
            for (OrgUserEntity userEntity : orgUserEntities) {
                OrgUserJobDto dto = new OrgUserJobDto();
                dto.setJobId(userEntity.getOrgElementId());
                OrgElementPathEntity pathEntity = pathEntities.stream().filter(path -> StringUtils.equals(path.getOrgElementId(), userEntity.getOrgElementId())
                        && path.getOrgVersionUuid().equals(userEntity.getOrgVersionUuid())).findFirst().orElse(null);
                if (pathEntity == null) {
                    pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(userEntity.getOrgElementId(), userEntity.getOrgVersionUuid());
                }
                dto.setJobIdPath(pathEntity.getIdPath());
                dto.setJobNamePath(pathEntity.getCnPath());
                if (!Locale.SIMPLIFIED_CHINESE.toString().equals(LocaleContextHolder.getLocale().toString())) {
                    String pathName = orgElementPathService.getLocaleOrgElementPath(pathEntity.getOrgElementUuid(), LocaleContextHolder.getLocale().toString());
                    if (StringUtils.isNotBlank(pathName)) {
                        dto.setJobNamePath(pathName);
                    }
                }
                dto.setJobName(dto.getJobNamePath().substring(pathEntity.getCnPath().lastIndexOf(Separator.SLASH.getValue()) + 1));
                dto.setOrgVersionUuid(userEntity.getOrgVersionUuid());
                dto.setPrimary(OrgUserEntity.Type.PRIMARY_JOB_USER.equals(userEntity.getType()));
                jobDtos.add(dto);
            }
        }
        return jobDtos;
    }

    /**
     * @param orgUserEntities
     * @return
     */
    private List<OrgElementPathEntity> getOrgElementPathEntity(List<OrgUserEntity> orgUserEntities) {
        Map<Long, List<String>> orgElementIdMap = Maps.newHashMap();
        orgUserEntities.stream().forEach(userEntity -> {
            Long orgVersionUuid = userEntity.getOrgVersionUuid();
            if (!orgElementIdMap.containsKey(orgVersionUuid)) {
                orgElementIdMap.put(orgVersionUuid, Lists.newArrayList());
            }
            orgElementIdMap.get(orgVersionUuid).add(userEntity.getOrgElementId());
        });
        List<OrgElementPathEntity> pathEntities = Lists.newArrayList();
        orgElementIdMap.forEach((orgVersionUuid, orgElementIds) -> {
            pathEntities.addAll(orgElementPathService.listByOrgElementIdsAndOrgVersionUuid(orgElementIds, orgVersionUuid));
        });
        return pathEntities;
    }

    @Override
    public List<OrgUserDto> listOrgUser(String userId, String[] orgVersionIds) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        return listOrgUser(Lists.newArrayList(userId), orgVersionIds);
    }

    @Override
    public List<OrgUserDto> listOrgUser(List<String> userIds, String[] orgVersionIds) {
        Assert.notEmpty(userIds, "用户ID列表不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        List<OrgUserDto> jobDtos = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionIds", orgVersionIds);
        params.put("userIds", userIds);
        List<OrgUserEntity> orgUserEntities = this.listByHQL("from OrgUserEntity where orgVersionUuid in(select v.uuid from OrgVersionEntity v where v.id in(:orgVersionIds)) and userId in(:userIds)", params);
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            List<OrgElementPathEntity> pathEntities = getOrgElementPathEntity(orgUserEntities);
            for (OrgUserEntity userEntity : orgUserEntities) {
                OrgUserDto dto = new OrgUserDto();
                BeanUtils.copyProperties(userEntity, dto);
                OrgElementPathEntity pathEntity = pathEntities.stream().filter(path -> StringUtils.equals(path.getOrgElementId(), userEntity.getOrgElementId())
                        && path.getOrgVersionUuid().equals(userEntity.getOrgVersionUuid())).findFirst().orElse(null);
                if (pathEntity == null) {
                    pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(userEntity.getOrgElementId(), userEntity.getOrgVersionUuid());
                }
                if (pathEntity == null) {
                    continue;
                }
                dto.setOrgElementIdPath(pathEntity.getIdPath());
                dto.setOrgElementCnPath(pathEntity.getCnPath());
                String[] parts = StringUtils.split(pathEntity.getCnPath(), Separator.SLASH.getValue());
                if (parts != null && parts.length > 0) {
                    dto.setOrgElementName(parts[parts.length - 1]);
                }
                dto.setOrgVersionUuid(userEntity.getOrgVersionUuid());
                jobDtos.add(dto);
            }
        }
        return jobDtos;
    }

    /**
     * 删除组织路径下的用户所有职位信息
     *
     * @param orgVersionUuid
     * @param elementPathPrefix
     */
    @Override
    @Transactional
    public void deleteAllOrgUserJobByOrgVersionUuidAndElementPathPrefix(Long orgVersionUuid, String elementPathPrefix) {
        Assert.notNull(orgVersionUuid, "组织版本UUID不能为空！");
        Assert.hasLength(elementPathPrefix, "组织元素路径前缀不能为空！");

        String hql = "delete from OrgUserEntity t where t.orgVersionUuid = :orgVersionUuid and t.userPath like :elementPathPrefix || '%' and t.type in(:types)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("elementPathPrefix", elementPathPrefix);
        params.put("types", new OrgUserEntity.Type[]{OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER});
        this.dao.updateByHQL(hql, params);
    }

    /**
     * 根据用户ID，组织版本ID列表，获取用户在组织中的关联
     *
     * @param userId
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<OrgUserEntity> listOrgUserByUserId(String userId, String... orgVersionIds) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "from OrgUserEntity t where t.userId = :userId and exists (select v.uuid from OrgVersionEntity v where v.uuid = t.orgVersionUuid and v.id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<OrgUserEntity> listOrgUserByUserIdAndOrgElementIds(String userId, List<String> orgElementIds, String... orgVersionIds) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(orgElementIds, "组织元素ID列表不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "from OrgUserEntity t where t.userId = :userId and t.orgElementId in :orgElementIds and exists (select v.uuid from OrgVersionEntity v where v.uuid = t.orgVersionUuid and v.id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgElementIds", orgElementIds);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    public List<OrgUserEntity> listByUserIdsAndOrgVersionUuidAndTypes(List<String> userIds, long orgVersionUuid, List<OrgUserEntity.Type> types) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("types", types);
        params.put("userIds", userIds);
        return this.dao.listByHQL("from OrgUserEntity where " +
                "orgVersionUuid=:orgVersionUuid and userId in (:userIds) and type in (:types) ", params);
    }

    @Override
    public List<OrgUserEntity> getByUserIdAndTypeAndOrgVersionUuid(String userId, OrgUserEntity.Type type, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("type", type);
        params.put("userId", userId);
        return this.dao.listByHQL("from OrgUserEntity where " +
                "orgVersionUuid=:orgVersionUuid and userId = :userId " + (type != null ? " and type = :type " : ""), params);
    }

    /**
     * 获取指定用户路径下的用户ID列表
     *
     * @param userPathPrefix
     * @param orgVersionUuids
     * @return
     */
    @Override
    public List<String> listUserIdByUserPathPrefix(String userPathPrefix, Long[] orgVersionUuids) {
        Assert.hasLength(userPathPrefix, "用户路径前缀不能为空！");
        Assert.notEmpty(orgVersionUuids, "组织版本UUID列表不能为空！");

        String hql = "select t.userId from OrgUserEntity t where t.userPath like :userPathPrefix || '%' and t.orgVersionUuid in(:orgVersionUuids)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userPathPrefix", userPathPrefix);
        params.put("orgVersionUuids", orgVersionUuids);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * 获取指定用户路径下的用户ID列表
     *
     * @param userPathPrefix
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> listUserIdByUserPathPrefix(String userPathPrefix, String[] orgVersionIds) {
        Assert.hasLength(userPathPrefix, "用户路径前缀不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "select t.userId from OrgUserEntity t where t.userPath like :userPathPrefix || '%' and t.orgVersionUuid in(select v.uuid from OrgVersionEntity v where v.id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userPathPrefix", userPathPrefix);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * 获取租户管理员ID列表
     *
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<String> listCurrentTenantAdminIds(String[] orgVersionIds) {
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "select t1.userId from UserInfoEntity t1, UserAccountEntity t2, OrgUserEntity t3 where t1.accountUuid = t2.uuid and t1.userId = t3.userId and t2.type = :accountType and t3.orgVersionUuid in(select uuid from OrgVersionEntity where id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("accountType", UserAccountEntity.Type.TENANT_ADMIN);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.listCharSequenceByHQL(hql, params);
    }

    /**
     * 判断用户ID是否在指定组织元素下
     *
     * @param userId
     * @param orgElementIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public boolean isInOrgElement(String userId, List<String> orgElementIds, String[] orgVersionIds) {
        Assert.hasLength(userId, "用户ID不能为空！");
        Assert.notEmpty(orgElementIds, "组织元素ID列表不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        String hql = "select count(t.uuid) from OrgUserEntity t where t.userId = :userId and t.orgElementId in(:orgElementIds) and t.orgVersionUuid in(select uuid from OrgVersionEntity where id in(:orgVersionIds))";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgElementIds", orgElementIds);
        params.put("orgVersionIds", orgVersionIds);
        return this.dao.countByHQL(hql, params) > 0;
    }

    @Override
    public boolean isJobInOrgElement(String jobId, List<String> orgElementIds, String[] orgVersionIds) {
        Assert.hasLength(jobId, "用户ID不能为空！");
        Assert.notEmpty(orgElementIds, "组织元素ID列表不能为空！");
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        for (String orgElementId : orgElementIds) {
            String hql = "select count(t.uuid) from OrgUserEntity t where t.orgElementId = :jobId and t.orgElementType = :eleType and t.userPath like '%' || :orgElementId || '%' and t.orgVersionUuid in(select uuid from OrgVersionEntity where id in(:orgVersionIds))";
            Map<String, Object> params = Maps.newHashMap();
            params.put("jobId", jobId);
            params.put("eleType", OrgElementModelEntity.ORG_JOB_ID);
            params.put("orgElementId", orgElementId);
            params.put("orgVersionIds", orgVersionIds);
            Long count = this.dao.countByHQL(hql, params);
            if (count > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取用户相关的组织元素ID
     *
     * @param userId
     * @return
     */
    @Override
    public List<String> listRelatedElementId(String userId) {
        Assert.hasLength(userId, "用户ID不能为空！");

        String hql = "select t.userPath from OrgUserEntity t where t.userId = :userId";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        List<String> userPaths = this.dao.listCharSequenceByHQL(hql, params);

        List<String> elementIds = Lists.newArrayList();
        for (String userPath : userPaths) {
            elementIds.addAll(Arrays.asList(StringUtils.split(userPath, Separator.SLASH.getValue())));
        }
        return elementIds;
    }


    @Override
    public Long countOrgElementUserByOrgVersionUuid(String orgElementId, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        OrgElementEntity elementEntity = orgElementService.getByIdAndOrgVersionUuid(orgElementId, orgVersionUuid);
        if (elementEntity != null) {
            params.put("orgElementId", elementEntity.getId());
            Long cnt = dao.countBySQL("select count(distinct r.user_id) from org_user r where r.org_version_uuid=:orgVersionUuid and   (  r.org_element_id =:orgElementId     or ( " +
                    " exists ( select 1 from ORG_ELEMENT_PATH_CHAIN c where  c.org_version_uuid = r.org_version_uuid and c.org_element_id = :orgElementId and r.org_element_id = c.sub_org_element_id  )))", params);

            return cnt;
        }
        return 0L;

    }

    @Override
    public Long countUserByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        return dao.countBySQL("select count(distinct user_id) from org_user where org_version_uuid=:orgVersionUuid ", params);
    }

    @Override
    public Long countUserHasDeptJobByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        return dao.countByHQL("select count(distinct userId) from OrgUserEntity  " +
                "where orgVersionUuid=:orgVersionUuid    and  orgElementType in ('dept','job') ", params);
    }

    @Override
    @Transactional
    public void deleteOrgUser(String userId, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("userId", userId);
        this.dao.deleteByHQL("delete from OrgUserEntity where userId=:userId " +
                (orgVersionUuid == null ? "" : "and orgVersionUuid=:orgVersionUuid"), params);
        orgElementRoleMemberService.deleteByMemberAndOrgVersionUuid(userId, orgVersionUuid);
        orgUserReportRelationService.deleteByOrgVersionUuidAndUserId(userId, orgVersionUuid);
        orgUserReportRelationService.deleteByOrgVersionUuidAndReportToUserId(userId, orgVersionUuid);
    }

    @Override
    @Transactional
    public void addUser(OrgUserElementDto userElementDto) {
        if (userElementDto.getOrgVersionUuid() != null && CollectionUtils.isNotEmpty(userElementDto.getUserIds())) {
            for (String userId : userElementDto.getUserIds()) {
                Map<String, List<String>> jobReports = Maps.newHashMap();
                if (org.apache.commons.lang3.StringUtils.isNotBlank(userElementDto.getOrgElementId())
                        && org.apache.commons.lang3.StringUtils.isBlank(userElementDto.getJobId())) {
                    // 未指定职位的情况下，作为成员用户加入
                    if (!userElementDto.getOrgElementId().startsWith(IdPrefix.JOB.getValue() + Separator.UNDERLINE.getValue())) {
                        this.addOrgUser(userId, userElementDto.getOrgVersionUuid(), Lists.newArrayList(userElementDto.getOrgElementId()), OrgUserEntity.Type.MEMBER_USER);
                    }
                    jobReports.put(userElementDto.getOrgElementId(), userElementDto.getDirectReporter());
                }
                if (org.apache.commons.lang3.StringUtils.isNotBlank(userElementDto.getJobId())) {
                    this.addOrgUser(userId, userElementDto.getOrgVersionUuid(), Lists.newArrayList(userElementDto.getJobId()), OrgUserEntity.Type.SECONDARY_JOB_USER);
                    jobReports.put(userElementDto.getJobId(), userElementDto.getDirectReporter());
                    jobReports.remove(userElementDto.getOrgElementId());
                }
                if (CollectionUtils.isNotEmpty(userElementDto.getDirectReporter())) {
                    orgUserReportRelationService.saveUserReportRelation(userId, jobReports, userElementDto.getOrgVersionUuid());
                }
            }
        }
    }

    @Override
    @Transactional
    public void removeUser(OrgUserElementDto userElementDto) {
        if (userElementDto.getOrgVersionUuid() != null && CollectionUtils.isNotEmpty(userElementDto.getUserIds())) {
            for (String userId : userElementDto.getUserIds()) {
                Map<String, Object> param = Maps.newHashMap();
                param.put("userId", userId);
                param.put("orgVersionUuid", userElementDto.getOrgVersionUuid());
                OrgUserEntity example = new OrgUserEntity();
                example.setUserId(userId);
                StringBuilder sql = new StringBuilder("delete  from OrgUserEntity o where userId=:userId and orgVersionUuid=:orgVersionUuid");
                if (userElementDto.getOrgElementId() != null) {
                    param.put("orgElementId", userElementDto.getOrgElementId());
                    sql.append(" and ( o.orgElementId=:orgElementId " +
                            "or exists ( select 1 from OrgElementPathChainEntity c where c.subOrgElementId = o.orgElementId and c.orgElementId=:orgElementId ) ) ");
                }
                dao.deleteByHQL(sql.toString(), param);
            }
        }
    }

    @Override
    public List<TreeNode> getOrgUserRolePrivilegeTree(String userId, Long orgVersionUuid) {
        // 用户自身的角色
        UserInfoEntity user = userInfoService.getByUserId(userId);
        TreeNode.TreeContextHolder.remove();
        if (user != null) {
            List<TreeNode> nodes = Lists.newArrayList();
            List<String> roles = userRoleService.getRolesByUserUuid(user.getUuid());
            if (CollectionUtils.isNotEmpty(roles)) {
                for (String r : roles) {
                    TreeNode n = roleService.getRolePrivilegeTree(r);
                    if (n != null) {
                        nodes.add(n);
                    }
                }
            }

            // 获取用户在当前版本的组织元素关联的权限
            if (orgVersionUuid != null) {
                OrgVersionEntity versionEntity = orgVersionService.getOne(orgVersionUuid);
                if (versionEntity != null) {
                    List<OrgUserEntity> orgUserEntities = this.listOrgUserByUserId(userId, versionEntity.getId());
                    if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                        for (OrgUserEntity orgUserEntity : orgUserEntities) {
                            String orgElementId = orgUserEntity.getOrgElementId();
                            if (org.apache.commons.lang.StringUtils.isNotBlank(orgElementId)) {
                                OrgElementEntity elementEntity = orgElementService.getByIdAndOrgVersionUuid(orgElementId, orgUserEntity.getOrgVersionUuid());
                                if (elementEntity != null) {
                                    List<TreeNode> elementNodes = orgElementService.getOrgElementRolePrivilegeTree(elementEntity.getUuid());
                                    if (CollectionUtils.isNotEmpty(elementNodes)) {
                                        nodes.addAll(elementNodes);
                                    }
                                }
                            }

                        }
                    }
                }
            }
            return nodes;
        }
        return null;
    }

    @Override
    @Transactional
    public void saveOrgUser(List<OrgUserEntity> orgUsers, Long orgVersionUuid, String userId) {
        this.deleteOrgUserByOrgVersionUuidAndType(userId, orgVersionUuid, null);
        // 保存组织版本跟用户的关系
        this.addOrgUser(userId, orgVersionUuid, null, OrgUserEntity.Type.MEMBER_USER);
        if (CollectionUtils.isNotEmpty(orgUsers)) {
            Map<OrgUserEntity.Type, List<String>> typeIds = Maps.newHashMap();
            for (OrgUserEntity orgUser : orgUsers) {
                if (orgUser.getType() != null && org.apache.commons.lang3.StringUtils.isNotBlank(orgUser.getOrgElementId())) {
                    if (!typeIds.containsKey(orgUser.getType())) {
                        typeIds.put(orgUser.getType(), Lists.newArrayList());
                    }
                    typeIds.get(orgUser.getType()).add(orgUser.getOrgElementId());
                }
            }
            Set<Map.Entry<OrgUserEntity.Type, List<String>>> entrySet = typeIds.entrySet();
            for (Map.Entry<OrgUserEntity.Type, List<String>> ent : entrySet) {
                this.addOrgUser(userId, orgVersionUuid, ent.getValue(), ent.getKey());
            }
        }
    }

    @Override
    public List<UserInfoEntity> getRoleRelaUsers(String roleUuid, String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("roleUuid", roleUuid);
        param.put("tenant", tenant);
        param.put("system", system);
        StringBuilder hql = new StringBuilder("from UserInfoEntity u where exists (" +
                "select 1 from UserRoleEntity ur ,OrgUserEntity ou where ur.roleUuid=:roleUuid and ur.userUuid = u.uuid and ou.userId = u.userId " +
                " and ou.tenant = :tenant " + (StringUtils.isNotBlank(system) ? " and ou.system =:system" : "") +
                ") ");
        return userInfoService.listByHQL(hql.toString(), param);
    }

    @Override
    public List<OrgUserEntity> getAllOrgUserUnderDefaultPublishedOrgVersion(String userId, String system, String tenant) {
        return this.getAllOrgUserUnderPublishedOrgVersion(userId, system, tenant, true);
    }

    @Override
    public List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, String system, String tenant) {
        // 默认组织的组织用户信息先查出排前面
        List<OrgUserEntity> orgUserEntities = this.getAllOrgUserUnderPublishedOrgVersion(userId, system, tenant, true);
        Set<Long> uuids = Sets.newHashSet();
        for (OrgUserEntity entity : orgUserEntities) {
            uuids.add(entity.getUuid());
        }
        List<OrgUserEntity> allOrgUserEntities = this.getAllOrgUserUnderPublishedOrgVersion(userId, system, tenant, false);
        for (OrgUserEntity entity : allOrgUserEntities) {
            if (uuids.add(entity.getUuid())) {
                orgUserEntities.add(entity);
            }
        }
        return orgUserEntities;
    }


    private List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, String system, String tenant, boolean onlyDefaultOrg) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        param.put("state", OrgVersionEntity.State.PUBLISHED);
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
        }
        param.put("userId", userId);
        StringBuilder hql = new StringBuilder("from OrgUserEntity u where userId = :userId and u.tenant = :tenant  " +
                (StringUtils.isNotBlank(system) ? " and system=:system " : "") +
                " and exists ( " +
                "   select 1 from OrgVersionEntity v ,OrganizationEntity o  where o.uuid = v.orgUuid" +
                " and o.enable = true and o.expired = false  " + (onlyDefaultOrg ? " and o.isDefault = true " : "") +
                " and v.uuid =u.orgVersionUuid and v.state = :state " +
                ") ");
        List<OrgUserEntity> orgUserEntities = this.listByHQL(hql.toString(), param);
        for (OrgUserEntity entity : orgUserEntities) {
            if (StringUtils.isNotBlank(entity.getOrgElementId())) {
                OrgElementEntity elementEntity = orgElementService.getByIdAndOrgVersionUuid(entity.getOrgElementId(), entity.getOrgVersionUuid());
                if (elementEntity != null) {
                    entity.setOrgElement(elementEntity);
                    OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(entity.getOrgElementId(), entity.getOrgVersionUuid());
                    entity.setOrgElementPath(pathEntity);
                    if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
                        String path = orgElementPathService.getLocaleOrgElementPath(elementEntity.getUuid(), LocaleContextHolder.getLocale().toString());
                        if (StringUtils.isNotBlank(path)) {
                            entity.getOrgElementPath().setLocalPath(path);
                        }
                        String name = orgElementService.getLocalOrgElementName(entity.getUuid(), LocaleContextHolder.getLocale().toString());
                        if (StringUtils.isNotBlank(name)) {
                            entity.getOrgElement().setLocalName(name);
                        }
                        String sname = orgElementService.getLocalOrgElementShortName(entity.getUuid(), LocaleContextHolder.getLocale().toString());
                        if (StringUtils.isNotBlank(sname)) {
                            entity.getOrgElement().setLocalShortName(sname);
                        }
                    }
                }
            }
        }
        return orgUserEntities;
    }

    public List<OrgUserEntity> getAllOrgUserUnderPublishedOrgVersion(String userId, List<OrgUserEntity.Type> types, String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("tenant", StringUtils.defaultIfBlank(tenant, SpringSecurityUtils.getCurrentTenantId()));
        param.put("state", OrgVersionEntity.State.PUBLISHED);
        if (StringUtils.isNotBlank(system)) {
            param.put("system", system);
        }
        if (CollectionUtils.isNotEmpty(types)) {
            param.put("types", types);
        }
        param.put("userId", userId);
        StringBuilder hql = new StringBuilder("from OrgUserEntity u where userId = :userId and u.tenant = :tenant" +
                (StringUtils.isNotBlank(system) ? " and system=:system " : "") +
                (CollectionUtils.isNotEmpty(types) ? " and u.type in :types" : "") +
                " and exists ( " +
                "   select 1 from OrgVersionEntity v ,OrganizationEntity o  where o.uuid = v.orgUuid" +
                " and o.enable = true and o.expired = false and " +
                " v.uuid =u.orgVersionUuid and v.state = :state " +
                ") ");
        List<OrgUserEntity> orgUserEntities = this.listByHQL(hql.toString(), param);
        for (OrgUserEntity entity : orgUserEntities) {
            if (StringUtils.isNotBlank(entity.getOrgElementId())) {
                OrgElementEntity elementEntity = orgElementService.getByIdAndOrgVersionUuid(entity.getOrgElementId(), entity.getOrgVersionUuid());
                if (elementEntity != null) {
                    entity.setOrgElement(elementEntity);
                    OrgElementPathEntity pathEntity = orgElementPathService.getByOrgElementIdAndOrgVersionUuid(entity.getOrgElementId(), entity.getOrgVersionUuid());
                    entity.setOrgElementPath(pathEntity);
                }
            }

        }
        return orgUserEntities;
    }


    @Override
    public UserDetailsVo getOrgUserDetailsByUerIdAndSystem(String userId, String system) {
        Assert.notNull(userId, "用户ID不为空");
        UserInfoEntity userInfoEntity = userInfoService.getByUserId(userId);
        MultiOrgJobDutyService multiOrgJobDutyService = ApplicationContextHolder.getBean(MultiOrgJobDutyService.class);
        if (userInfoEntity != null) {
            UserDetailsVo userDto = new UserDetailsVo();
            userDto.setUuid(userInfoEntity.getUuid());
            userDto.setUserId(userInfoEntity.getUserId());
            userDto.setUserName(userInfoEntity.getUserName());
            String userName = userInfoService.getLocaleUserNameByUserIdLocale(userInfoEntity.getUserId(), LocaleContextHolder.getLocale().toString());
            if (StringUtils.isNotBlank(userName)) {
                userDto.setUserName(userName);
            }
            userDto.setEnName(userInfoEntity.getEnName());
            userDto.setGender(userInfoEntity.getGender());
            userDto.setLoginName(userInfoEntity.getLoginName());
            userDto.setAvatar(userInfoEntity.getAvatar());
            userDto.setCeilPhoneNumber(userInfoEntity.getCeilPhoneNumber());
            userDto.setMail(userInfoEntity.getMail());
            userDto.setWorkState(userInfoEntity.getWorkState());
            List<UserInfoExtEntity> userInfoExtEntities = userInfoExtService.getByUserUuid(userInfoEntity.getUuid());
            if (CollectionUtils.isNotEmpty(userInfoExtEntities)) {
                for (UserInfoExtEntity extEntity : userInfoExtEntities) {
                    if ("idNumber".equalsIgnoreCase(extEntity.getAttrKey())) {
                        userDto.setIdNumber(extEntity.getAttrValue());
                    } else if ("familyPhoneNumber".equalsIgnoreCase(extEntity.getAttrKey())) {
                        userDto.setFamilyPhoneNumber(extEntity.getAttrValue());
                    } else if ("businessPhoneNumber".equalsIgnoreCase(extEntity.getAttrKey())) {
                        userDto.setBusinessPhoneNumber(extEntity.getAttrValue());
                    } else if ("workLocation".equalsIgnoreCase(extEntity.getAttrKey())) {
                        userDto.setWorkLocation(extEntity.getAttrValue());
                    }
                }
            }
            List<OrgUserEntity> orgUserEntities = this.getAllOrgUserUnderDefaultPublishedOrgVersion(userId, system, null);
            UserAccountEntity accountEntity = userAccountService.getByLoginName(userInfoEntity.getLoginName());
            userDto.setLastLoginTime(accountEntity.getLastLoginTime());
            if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                OrgElementPathEntity asMemberPath = null;
                OrgElementPathEntity otherJobUserPath = null;
                for (OrgUserEntity orgUserEntity : orgUserEntities) {
                    if (OrgUserEntity.Type.PRIMARY_JOB_USER.equals(orgUserEntity.getType())) {
                        // 取主职上的信息
                        userDto.setMainJobNamePath(StringUtils.defaultIfBlank(orgUserEntity.getOrgElementPath().getLocalPath(), orgUserEntity.getOrgElementPath().getCnPath()));
                        String[] paths = StringUtils.isNotBlank(orgUserEntity.getOrgElementPath().getLocalPath()) ?
                                orgUserEntity.getOrgElementPath().getLocalPath().split(Separator.SLASH.getValue()) :
                                orgUserEntity.getOrgElementPath().getCnPath().split(Separator.SLASH.getValue());
                        userDto.setMainJobName(paths[paths.length - 1]);
                        String unitId = OrgElementPathEntity.nearestIdByPrefix(orgUserEntity.getOrgElementPath().getIdPath(), IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue(), false);
                        String[] idPaths = orgUserEntity.getOrgElementPath().getIdPath().split(Separator.SLASH.getValue());
                        int i = ArrayUtils.lastIndexOf(idPaths, unitId);
                        if (i != -1) {
                            userDto.setUnitName(paths[i]);
                        }
                        String deptId = OrgElementPathEntity.nearestIdByPrefix(orgUserEntity.getOrgElementPath().getIdPath(), IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue(), false);
                        i = ArrayUtils.lastIndexOf(idPaths, deptId);
                        if (i != -1) {
                            userDto.setDeptName(paths[i]);
                        }
                        // 设置直接上级
                        userDto.setDirectorUserId(this.getDirectorUserId(orgUserEntity.getOrgElementPath().getIdPath(), orgUserEntity.getOrgVersionUuid()));
                        if (orgUserEntity.getOrgElement() != null) {
                            OrgJobDutyDto dutyDto = multiOrgJobDutyService.getJobDutyDetailsByJobIdAndOrgVersionUuid(orgUserEntity.getOrgElement().getId(), orgUserEntity.getOrgVersionUuid());
                            // 主职位职级
                            if (dutyDto != null) {
                                userDto.setDutyName(dutyDto.getDutyName());
                                List<OrgJobDutyDto.OrgJobRankDto> jobRankDtos = dutyDto.getJobRanks();
                                List<String> rankNames = Lists.newArrayList();
                                if (CollectionUtils.isNotEmpty(jobRankDtos)) {
                                    for (OrgJobDutyDto.OrgJobRankDto rankDto : jobRankDtos) {
                                        rankNames.add(rankDto.getJobRank());
                                    }
                                    userDto.setJobRankName(StringUtils.join(rankNames, Separator.SEMICOLON.getValue()));
                                }
                            }
                        }

                    } else if (OrgUserEntity.Type.SECONDARY_JOB_USER.equals(orgUserEntity.getType())) {
                        String[] paths = StringUtils.isNotBlank(orgUserEntity.getOrgElementPath().getLocalPath()) ?
                                orgUserEntity.getOrgElementPath().getLocalPath().split(Separator.SLASH.getValue()) : orgUserEntity.getOrgElementPath().getCnPath().split(Separator.SLASH.getValue());
                        userDto.getOtherJobNamePaths().add(StringUtils.defaultIfBlank(orgUserEntity.getOrgElementPath().getLocalPath(), orgUserEntity.getOrgElementPath().getCnPath()));
                        userDto.getOtherJobNames().add(paths[paths.length - 1]);
                        if (otherJobUserPath == null) {
                            otherJobUserPath = orgUserEntity.getOrgElementPath();
                        }
                    } else if (asMemberPath == null) {
                        asMemberPath = orgUserEntity.getOrgElementPath();
                    }
                }

                // 未设置主职位，则取副职所在单位、部门信息
                if (userDto.getDeptName() == null) {
                    if (otherJobUserPath != null) {
                        String deptId = OrgElementPathEntity.nearestIdByPrefix(otherJobUserPath.getIdPath(), IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue(), false);
                        if (StringUtils.isNotBlank(deptId)) {
                            String[] idPaths = otherJobUserPath.getIdPath().split(Separator.SLASH.getValue());
                            String[] paths = StringUtils.isNotBlank(otherJobUserPath.getLocalPath()) ? otherJobUserPath.getLocalPath().split(Separator.SLASH.getValue()) : otherJobUserPath.getCnPath().split(Separator.SLASH.getValue());
                            int i = ArrayUtils.lastIndexOf(idPaths, deptId);
                            if (i != -1) {
                                userDto.setDeptName(paths[i]);
                            }
                        }
                    }
                    if (asMemberPath != null && userDto.getDeptName() == null && StringUtils.isNotBlank(asMemberPath.getIdPath())) {
                        String deptId = OrgElementPathEntity.nearestIdByPrefix(asMemberPath.getIdPath(), IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue(), false);
                        if (StringUtils.isNotBlank(deptId)) {
                            String[] idPaths = asMemberPath.getIdPath().split(Separator.SLASH.getValue());
                            String path = StringUtils.defaultIfBlank(asMemberPath.getLocalPath(), asMemberPath.getCnPath());
                            if (StringUtils.isNotBlank(path)) {
                                String[] paths = path.split(Separator.SLASH.getValue());
                                int i = ArrayUtils.lastIndexOf(idPaths, deptId);
                                if (i != -1) {
                                    userDto.setDeptName(paths[i]);
                                }
                            }
                        }

                    }
                }
                if (userDto.getUnitName() == null) {
                    if (otherJobUserPath != null && StringUtils.isNotBlank(otherJobUserPath.getIdPath())) {
                        String deptId = OrgElementPathEntity.nearestIdByPrefix(otherJobUserPath.getIdPath(), IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue(), false);
                        if (StringUtils.isNotBlank(deptId)) {
                            String[] idPaths = otherJobUserPath.getIdPath().split(Separator.SLASH.getValue());
                            String path = StringUtils.defaultIfBlank(otherJobUserPath.getLocalPath(), otherJobUserPath.getCnPath());
                            if (StringUtils.isNotBlank(path)) {
                                String[] paths = path.split(Separator.SLASH.getValue());
                                int i = ArrayUtils.lastIndexOf(idPaths, deptId);
                                if (i != -1) {
                                    userDto.setUnitName(paths[i]);
                                }
                            }
                        }
                    }
                    if (asMemberPath != null && userDto.getUnitName() == null && StringUtils.isNotBlank(asMemberPath.getIdPath())) {
                        String deptId = OrgElementPathEntity.nearestIdByPrefix(asMemberPath.getIdPath(), IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue(), false);
                        if (StringUtils.isNotBlank(deptId)) {
                            String[] idPaths = asMemberPath.getIdPath().split(Separator.SLASH.getValue());
                            String path = StringUtils.defaultIfBlank(asMemberPath.getLocalPath(), asMemberPath.getCnPath());
                            if (StringUtils.isNotBlank(path)) {
                                String[] paths = path.split(Separator.SLASH.getValue());
                                int i = ArrayUtils.lastIndexOf(idPaths, deptId);
                                if (i != -1) {
                                    userDto.setUnitName(paths[i]);
                                }
                            }
                        }
                    }
                }
                if (userDto.getDirectorUserId() == null) {
                    // 设置直接上级
                    if (otherJobUserPath != null) {
                        userDto.setDirectorUserId(this.getDirectorUserId(otherJobUserPath, orgUserEntities.get(0).getOrgVersionUuid()));
                    }
                    if (asMemberPath != null && userDto.getDirectorUserId() == null) {
                        userDto.setDirectorUserId(this.getDirectorUserId(asMemberPath, orgUserEntities.get(0).getOrgVersionUuid()));
                    }
                }
                OrgVersionEntity versionEntity = orgVersionService.getOne(orgUserEntities.get(0).getOrgVersionUuid());
                userDto.setSubordinateUsers(orgFacadeService.listUserSubordinateUserIds(userId, Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER, OrgUserEntity.Type.SECONDARY_JOB_USER,
                        OrgUserEntity.Type.MEMBER_USER), versionEntity.getId()));
            } else {
                // TODO: 是否获取其主职位的组织
//                List<OrgUserEntity> primaryJobUserEntities = this.getAllOrgUserUnderPublishedOrgVersion(userId, Lists.newArrayList(OrgUserEntity.Type.PRIMARY_JOB_USER), system, SpringSecurityUtils.getCurrentTenantId());

            }
            return userDto;
        }

        return null;
    }

    @Override
    public List<OrgGroupEntity> getGroupsIncludeUser(String userId, String system) {
        List<OrgVersionEntity> orgVersionEntities = orgVersionService.listPublishedBySystemAndTenant(system, SpringSecurityUtils.getCurrentTenantId());
        List<Long> versionUuids = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orgVersionEntities)) {
            for (OrgVersionEntity ver : orgVersionEntities) {
                versionUuids.add(ver.getUuid());
            }
        }
        List<OrgElementPathEntity> pathEntities = orgElementPathService.getOrgUserElementPaths(userId, versionUuids);
        Set<String> eleIds = Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(pathEntities)) {
            for (OrgElementPathEntity pathEntity : pathEntities) {
                eleIds.addAll(Arrays.asList(pathEntity.getIdPath().split(Separator.SLASH.getValue())));
            }
        }
        eleIds.add(userId);
        return orgGroupService.listGroupsIncludeMember(eleIds);
    }

    @Override
    public Map<String, String> getUserIdNamesByOrgElementIds(List<String> ids) {
        Set<String> orgElementIds = Sets.newHashSet();
        Set<String> userIds = Sets.newHashSet();
        Set<String> groupIds = Sets.newHashSet();
        for (String id : ids) {
            if (id.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                userIds.add(id);
            } else if (id.startsWith(IdPrefix.GROUP.getValue() + Separator.UNDERLINE.getValue())) {
                groupIds.add(id);
            } else {
                orgElementIds.add(id);
            }
        }
        if (!groupIds.isEmpty()) {
            Set<String> members = orgGroupService.listMemberIdByIds(groupIds);
            if (CollectionUtils.isNotEmpty(members)) {
                for (String id : members) {
                    if (id.startsWith(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue())) {
                        userIds.add(id);
                    } else {
                        orgElementIds.add(id);
                    }
                }
            }
        }

        if (!orgElementIds.isEmpty()) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("state", OrgVersionEntity.State.PUBLISHED.ordinal());
            ListUtils.handleSubList(Lists.newArrayList(orgElementIds), 200, list -> {
                params.put("orgElementIds", list);
                List<String> uids = dao.listCharSequenceBySQL("select distinct r.user_id as user_id from org_user r " +
                        " where exists ( select 1 from org_version v where v.uuid = r.org_version_uuid and v.state =:state )  and   (  r.org_element_id in (:orgElementIds)    or ( " +
                        " exists ( select 1 from org_element_path_chain c where  c.org_version_uuid = r.org_version_uuid and c.org_element_id in (:orgElementIds)" +
                        " and r.org_element_id = c.sub_org_element_id  )))", params);
                if (CollectionUtils.isNotEmpty(uids)) {
                    userIds.addAll(uids);
                }
            });

        }

        Map<String, String> userIdNameMap = Maps.newHashMap();
        if (!userIds.isEmpty()) {
            userIdNameMap.putAll(userInfoService.getUserNamesByUserIds(userIds));
        }


        return userIdNameMap;
    }

    @Override
    public List<OrgUserEntity> getOrgUserLikeSuffixUserPath(String suffixUserPath, Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        params.put("suffixUserPath", "%" + suffixUserPath);
        return this.dao.listByHQL("from OrgUserEntity where userPath like :suffixUserPath and orgVersionUuid=:orgVersionUuid", params);
    }

    @Override
    public List<OrgUserEntity> listByUserIdsAndOrgUuidAndTypes(List<String> userIds, Long
            orgUuid, ArrayList<OrgUserEntity.Type> types) {
        OrgVersionEntity versionEntity = orgVersionService.getByStateAndOrgUuid(OrgVersionEntity.State.PUBLISHED, orgUuid);
        if (versionEntity != null) {
            return this.listByUserIdsAndOrgVersionUuidAndTypes(userIds, versionEntity.getUuid(), types);
        }
        return null;
    }

    @Override
    public Long countByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid) {
        String hql = "select count(*) from OrgUserEntity where userId = :userId and orgVersionUuid = :orgVersionUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.countByHQL(hql, params);
    }

    @Override
    @Transactional
    public void updateOrgUserPath(Map<String, String> orgElementPathMap) {
        Set<String> orgElementIds = orgElementPathMap.keySet();
        for (String id : orgElementIds) {
            OrgUserEntity example = new OrgUserEntity();
            example.setOrgElementId(id);
            List<OrgUserEntity> orgUserEntities = listByEntity(example);
            if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                for (OrgUserEntity u : orgUserEntities) {
                    u.setUserPath(orgElementPathMap.get(id) + Separator.SLASH.getValue() + u.getUserId());
                }
                saveAll(orgUserEntities);
            }
        }
    }

    @Override
    public List<OrgUserEntity> listByUserIdAndOrgVersionUuid(String userId, Long orgVersionUuid) {
        String hql = "from OrgUserEntity t where t.userId = :userId and t.orgVersionUuid=:orgVersionUuid ";
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("orgVersionUuid", orgVersionUuid);
        return this.dao.listByHQL(hql, params);
    }

    private String getDirectorUserId(OrgElementPathEntity pathEntity, Long orgVersionUuid) {
        if (pathEntity != null) {
            List<OrgElementManagementEntity> managementEntities =
                    orgElementManagementService.listByElementIds(Lists.newArrayList(pathEntity.getIdPath().split(Separator.SLASH.getValue())), new Long[]{orgVersionUuid});
            Map<String, String> managementEntityMap = Maps.newHashMap();
            for (OrgElementManagementEntity entity : managementEntities) {
                if (StringUtils.isNotBlank(entity.getDirector())) {
                    managementEntityMap.put(entity.getOrgElementId(), entity.getDirector());
                }
            }
            String[] ids = pathEntity.getIdPath().split(Separator.SLASH.getValue());
            ArrayUtils.reverse(ids);
            for (String i : ids) {
                if (managementEntityMap.containsKey(i)) {
                    return managementEntityMap.get(i);
                }
            }

        }
        return null;
    }


    private String getDirectorUserId(String idPath, Long orgVersionUuid) {
        if (idPath != null) {
            List<OrgElementManagementEntity> managementEntities =
                    orgElementManagementService.listByElementIds(Lists.newArrayList(idPath.split(Separator.SLASH.getValue())), new Long[]{orgVersionUuid});
            Map<String, String> managementEntityMap = Maps.newHashMap();
            for (OrgElementManagementEntity entity : managementEntities) {
                if (StringUtils.isNotBlank(entity.getDirector())) {
                    managementEntityMap.put(entity.getOrgElementId(), entity.getDirector());
                }
            }
            String[] ids = idPath.split(Separator.SLASH.getValue());
            ArrayUtils.reverse(ids);
            for (String i : ids) {
                if (managementEntityMap.containsKey(i)) {
                    return managementEntityMap.get(i);
                }
            }

        }
        return null;
    }

    private String getNearestElementNameByIdPrefix(OrgElementPathEntity pathEntity, String prefix, Long
            orgVersionUuid) {
        if (pathEntity != null) {
            String unitId = pathEntity.nearestIdByPrefix(pathEntity.getIdPath(), prefix, false);
            if (StringUtils.isNotBlank(unitId)) {
                OrgElementEntity ele = orgElementService.getByIdAndOrgVersionUuid(unitId, orgVersionUuid);
                if (ele != null) {
                    return ele.getName();
                }
            }
        }
        return null;
    }

    @Override
    public Long countUserBySystemAndTenant(String system, String tenant) {
        if (StringUtils.isBlank(system)) {
            Assert.notNull(system, "系统ID参数不为空");
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("tenant", tenant);
        return dao.countByHQL("select count(distinct o.userId) from OrgUserEntity o " +
                "where o.system=:system and o.tenant=:tenant and exists (" +
                " select 1 from UserAccountEntity a , UserInfoEntity e where e.accountUuid = a.uuid and e.userId = o.userId " +
                " and a.isAccountNonLocked = true" +
                ") ", params);
    }


    @Override
    public UserSystemOrgDetails getUserSystemOrgDetails(String userId, String system) {
        List<OrgUserEntity> orgUserEntities = orgFacadeService.getAllOrgUserUnderPublishedOrgVersion(userId, system, Config.DEFAULT_TENANT);
        List<OrgUserEntity> noElementOrgUser = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(orgUserEntities)) {
            UserSystemOrgDetails userSystemOrgDetails = new UserSystemOrgDetails();

            // 设置用户的系统组织信息
            Map<String, UserSystemOrgDetails.OrgDetail> map = Maps.newHashMap();
            Map<String, List<OrgUserEntity>> asDeptMember = Maps.newHashMap();
            for (OrgUserEntity orgUserEntity : orgUserEntities) {
                if (org.apache.commons.lang.StringUtils.isNotBlank(orgUserEntity.getSystem())) {
                    if (orgUserEntity.getOrgElement() != null) {
                        if (!map.containsKey(orgUserEntity.getOrgElement().getOrgVersionId())) {
                            UserSystemOrgDetails.OrgDetail orgDetail = new UserSystemOrgDetails.OrgDetail();
                            map.put(orgUserEntity.getOrgElement().getOrgVersionId(), orgDetail);
                            orgDetail.setSystem(orgUserEntity.getSystem());
                            userSystemOrgDetails.getDetails().add(orgDetail);
                        }
                        UserSystemOrgDetails.OrgDetail detail = map.get(orgUserEntity.getOrgElement().getOrgVersionId());
                        if (OrgUserEntity.Type.PRIMARY_JOB_USER.equals(orgUserEntity.getType())
                                || OrgUserEntity.Type.SECONDARY_JOB_USER.equals(orgUserEntity.getType())) {
                            OrgTreeNodeDto dto = new OrgTreeNodeDto();
                            dto.setEleId(orgUserEntity.getOrgElementId());
                            dto.setEleUuid(orgUserEntity.getOrgElement().getUuid().toString());
                            dto.setOrgVersionId(orgUserEntity.getOrgElement().getOrgVersionId());
                            dto.setName(orgUserEntity.getOrgElement().getName());
                            dto.setShortName(orgUserEntity.getOrgElement().getShortName());
                            dto.setEleNamePath(orgUserEntity.getOrgElementPath().getCnPath());
                            dto.setEleIdPath(orgUserEntity.getOrgElementPath().getIdPath());
                            if (OrgUserEntity.Type.PRIMARY_JOB_USER.equals(orgUserEntity.getType())) {
                                detail.setMainJob(dto);
                            } else {
                                detail.addOtherJobs(dto);
                            }
                        } else if (OrgUserEntity.Type.MEMBER_USER.equals(orgUserEntity.getType()) && orgUserEntity.getOrgElementId().startsWith(IdPrefix.DEPARTMENT.getValue() + Separator.UNDERLINE.getValue())) {
                            if (!asDeptMember.containsKey(orgUserEntity.getSystem())) {
                                asDeptMember.put(orgUserEntity.getSystem(), Lists.newArrayList());
                            }
                            asDeptMember.get(orgUserEntity.getSystem()).add(orgUserEntity);
                        }
                    } else {
                        noElementOrgUser.add(orgUserEntity);
                    }
                }
            }


            // 获取单位与无职位的部门
            for (UserSystemOrgDetails.OrgDetail orgDetail :
                    userSystemOrgDetails.getDetails()) {
                // 单位信息优先匹配主职位上的
                if (orgDetail.getMainJob() != null) {
                    setUnitByJob(orgDetail, orgDetail.getMainJob());
                    if (orgDetail.getUnit() != null) {
                        continue;
                    }

                }

                if (!orgDetail.getOtherJobs().isEmpty()) {
                    for (OrgTreeNodeDto node : orgDetail.getOtherJobs()) {
                        setUnitByJob(orgDetail, node);
                        if (orgDetail.getUnit() != null) {
                            break;
                        }
                    }
                    if (orgDetail.getUnit() != null) {
                        continue;
                    }
                }


                if (orgDetail.getMainDept() == null && org.springframework.util.CollectionUtils.isEmpty(orgDetail.getOtherDepts())
                        && !org.springframework.util.CollectionUtils.isEmpty(asDeptMember.get(orgDetail.getSystem()))
                ) {
                    List<OrgUserEntity> deptOrgUserList = asDeptMember.get(orgDetail.getSystem());
                    for (int i = 0, len = deptOrgUserList.size(); i < len; i++) {
                        // 获取作为成员的部门节点
                        OrgTreeNodeDto dto = new OrgTreeNodeDto();
                        OrgUserEntity orgUserEntity = deptOrgUserList.get(i);
                        dto.setEleId(orgUserEntity.getOrgElementId());
                        dto.setEleUuid(orgUserEntity.getOrgElement().getUuid().toString());
                        dto.setOrgVersionId(orgUserEntity.getOrgElement().getOrgVersionId());
                        dto.setName(orgUserEntity.getOrgElement().getName());
                        dto.setShortName(orgUserEntity.getOrgElement().getShortName());
                        dto.setEleNamePath(orgUserEntity.getOrgElementPath().getCnPath());
                        dto.setEleIdPath(orgUserEntity.getOrgElementPath().getIdPath());
                        if (i == 0) {
                            orgDetail.setMainDept(dto);
                            setUnitByJob(orgDetail, dto);
                        } else {
                            orgDetail.getOtherDepts().add(dto);
                        }
                    }


                }


            }


            if (CollectionUtils.isEmpty(userSystemOrgDetails.getDetails()) && !noElementOrgUser.isEmpty()) {
                // 取无挂靠组织节点的组织信息存入
                Set<String> inputSys = Sets.newHashSet();
                for (OrgUserEntity u : noElementOrgUser) {
                    if (inputSys.add(u.getSystem())) {
                        UserSystemOrgDetails.OrgDetail d = new UserSystemOrgDetails.OrgDetail();
                        d.setSystem(u.getSystem());
                        userSystemOrgDetails.getDetails().add(d);
                    }
                }

            }
            return userSystemOrgDetails;
        }
        return null;
    }

    @Override
    public Map<String, UserSystemOrgDetails> getUserSystemOrgDetailsMap(List<String> userIds, String system) {
        Map<String, UserSystemOrgDetails> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(userIds)) {
            for (String uid : userIds) {
                UserSystemOrgDetails details = this.getUserSystemOrgDetails(uid, system);
                if (details != null) {
                    map.put(uid, details);
                }
            }

        }
        return map;
    }

    private void setUnitByJob(UserSystemOrgDetails.OrgDetail orgDetail, OrgTreeNodeDto job) {
        String unit = OrgElementPathEntity.nearestIdByPrefix(job.getEleIdPath(), IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue(), false);
        if (unit != null) {
            OrgElementEntity unitElement = orgFacadeService.getOrgElementByIdAndOrgVersionId(unit, job.getOrgVersionId());
            if (unitElement != null) {
                OrgTreeNodeDto unitDto = new OrgTreeNodeDto();
                unitDto.setName(unitElement.getName());
                unitDto.setSystemUnitId(unitElement.getSourceId());
                unitDto.setEleIdPath(unitElement.getPathEntity().getIdPath());
                unitDto.setEleId(unitElement.getId());
                orgDetail.setUnit(unitDto);
            }
        }
    }

}
