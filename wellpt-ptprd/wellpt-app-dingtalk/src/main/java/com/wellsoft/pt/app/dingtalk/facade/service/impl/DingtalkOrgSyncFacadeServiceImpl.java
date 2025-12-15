/*
 * @(#)4/18/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.facade.service.impl;

import com.dingtalk.api.response.OapiV2DepartmentGetResponse;
import com.dingtalk.api.response.OapiV2DepartmentListsubResponse;
import com.dingtalk.api.response.OapiV2UserGetResponse;
import com.dingtalk.api.response.OapiV2UserListResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.http.HttpUtil;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkDeptEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkJobEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkUserEntity;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkOrgSyncFacadeService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkDeptService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkJobService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkUserService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkEventHoler;
import com.wellsoft.pt.app.dingtalk.support.DingtalkSyncLoggerHolder;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkDepartmentVo;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.dto.OrgElementDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.entity.UserInfoExtEntity;
import com.wellsoft.pt.user.service.UserAccountService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/18/25.1	    zhulh		4/18/25		    Create
 * </pre>
 * @date 4/18/25
 */
@Service
public class DingtalkOrgSyncFacadeServiceImpl implements DingtalkOrgSyncFacadeService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private DingtalkDeptService dingtalkDeptService;

    @Autowired
    private DingtalkUserService dingtalkUserService;

    @Autowired
    private DingtalkJobService dingtalkJobService;

    @Autowired
    private MongoFileService mongoFileService;

    @Override
    @Transactional
    public void syncOrg(List<DingtalkDepartmentVo> departmentVos, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkConfigVo.DingtalkConfiguration configuration = dingtalkConfigVo.getConfiguration();
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(Long.valueOf(configuration.getOrgUuid()));
        DingtalkSyncLoggerHolder.orgVersion(orgVersionEntity);
        long count = orgFacadeService.countOrgElementByOrgVersionUuid(orgVersionEntity.getUuid());
        Map<Long, Long> departmentMap = new HashMap<>();
        Map<String, Long> userMap = new HashMap<>();
        // 存在组织元素，升级新版本
        if (count > 0) {
            orgVersionEntity = orgFacadeService.createEmptyOrgVersionFromOrgVersion(orgVersionEntity, false, true);
        }
        saveDepartmentVos(departmentVos, orgVersionEntity, null, departmentMap, userMap, dingtalkConfigVo);

        // 设置部门负责人
        saveDeptmentDirector(orgVersionEntity);
    }

    private void saveDeptmentDirector(OrgVersionEntity orgVersionEntity) {
        List<DingtalkUserEntity> dingtalkUserEntities = dingtalkUserService.listLeaderUserByOrgVersionUuid(orgVersionEntity.getUuid());

        dingtalkUserEntities.forEach(dingtalkUserEntity -> {
            orgFacadeService.saveOrgElementLeader(dingtalkUserEntity.getLeaderOrgElementUuid(), dingtalkUserEntity.getOaUserId(), null, null);
        });
    }

    private void saveDepartmentVos(List<DingtalkDepartmentVo> departmentVos, OrgVersionEntity orgVersionEntity,
                                   DingtalkDepartmentVo parentDepartmentVo, Map<Long, Long> departmentMap, Map<String, Long> userMap, DingtalkConfigVo dingtalkConfigVo) {
        for (DingtalkDepartmentVo departmentVo : departmentVos) {
            OrgElementDto orgElementDto = toOrgElementDto(departmentVo, orgVersionEntity, departmentMap);
            boolean added = StringUtils.isBlank(orgElementDto.getId());
            Long orgElementUuid = orgFacadeService.saveOrgElementDetails(orgElementDto);
            OrgElementEntity orgElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
            DingtalkDeptEntity dingtalkDeptEntity = saveDingtalkDept(departmentVo, orgElementEntity, orgVersionEntity, dingtalkConfigVo);
            if (added) {
                DingtalkSyncLoggerHolder.addDept(dingtalkDeptEntity);
            } else {
                DingtalkSyncLoggerHolder.updateDept(dingtalkDeptEntity);
            }

            departmentMap.put(departmentVo.getDepartment().getDeptId(), orgElementUuid);
            List<OapiV2UserListResponse.ListUserResponse> users = departmentVo.getUsers();
            if (CollectionUtils.isNotEmpty(users)) {
                saveDeptUsers(users, orgElementEntity, departmentVo, orgVersionEntity, userMap, dingtalkConfigVo);
            }

            if (CollectionUtils.isNotEmpty(departmentVo.getChildren())) {
                saveDepartmentVos(departmentVo.getChildren(), orgVersionEntity, departmentVo, departmentMap, userMap, dingtalkConfigVo);
            }
            dingtalkDeptService.flushSession();
            dingtalkDeptService.clearSession();
        }
    }

    private OrgElementDto toOrgElementDto(DingtalkDepartmentVo departmentNode, OrgVersionEntity orgVersionEntity, Map<Long, Long> departmentMap) {
        OapiV2DepartmentListsubResponse.DeptBaseResponse department = departmentNode.getDepartment();
        String orgElementId = dingtalkDeptService.getOrgElementIdByDeptId(department.getDeptId());

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(department.getName());
        orgElementDto.setId(orgElementId);
//        if (NumberUtils.isNumber(department.getOrder())) {
//            orgElementDto.setSeq(NumberUtils.createNumber(department.getOrder()).intValue());
//        }
        orgElementDto.setCode(department.getDeptId() + StringUtils.EMPTY);
        orgElementDto.setType(OrgElementModelEntity.ORG_DEPT_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(departmentMap.get(department.getParentId()));
        setDepartmentI18nName(orgElementDto, department.getExt());
        if (StringUtils.isNotBlank(orgElementId)) {
            Long orgElementUuid = dingtalkDeptService.getOrgElementUuidByOrgElementId(orgElementId);
            if (orgElementUuid != null) {
                orgElementDto.setRoleUuids(orgFacadeService.listOrgElementRoleUuidByUuid(orgElementUuid));
            }
        }
        return orgElementDto;
    }

    private void setDepartmentI18nName(OrgElementDto orgElementDto, String extentionJson) {
        if (StringUtils.isBlank(extentionJson)) {
            return;
        }

        DingtalkDepartmentVo.Extension extension = JsonUtils.json2Object(extentionJson, DingtalkDepartmentVo.Extension.class);
        if (extension != null) {
            List<OrgElementI18nEntity> i18ns = Lists.newArrayList();
            extension.toI18ns().forEach(item -> {
                Map<String, String> i18nValues = item.getI18nValues();
                if (MapUtils.isNotEmpty(i18nValues)) {
                    i18nValues.forEach((key, value) -> {
                        OrgElementI18nEntity i18nEntity = new OrgElementI18nEntity();
                        i18nEntity.setLocale(key);
                        i18nEntity.setDataCode(item.getName());
                        i18nEntity.setContent(value);
                        i18ns.add(i18nEntity);
                    });
                }
            });
            orgElementDto.setI18ns(i18ns);
        }
    }

    private DingtalkDeptEntity saveDingtalkDept(DingtalkDepartmentVo departmentVo, OrgElementEntity orgElementEntity,
                                                OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        OapiV2DepartmentListsubResponse.DeptBaseResponse department = departmentVo.getDepartment();
        DingtalkDeptEntity dingtalkDeptEntity = new DingtalkDeptEntity();
        dingtalkDeptEntity.setConfigUuid(dingtalkConfigVo.getUuid());
        dingtalkDeptEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        dingtalkDeptEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        dingtalkDeptEntity.setAppId(dingtalkConfigVo.getAppId());
        dingtalkDeptEntity.setOrgElementUuid(orgElementEntity.getUuid());
        dingtalkDeptEntity.setOrgElementId(orgElementEntity.getId());
        dingtalkDeptEntity.setName(department.getName());
        dingtalkDeptEntity.setDeptId(department.getDeptId());
        dingtalkDeptEntity.setParentId(department.getParentId());
        dingtalkDeptEntity.setExt(department.getExt());
        dingtalkDeptEntity.setStatus(DingtalkDeptEntity.Status.NORMAL);
        dingtalkDeptService.save(dingtalkDeptEntity);
        return dingtalkDeptEntity;
    }

    private void saveDeptUsers(List<OapiV2UserListResponse.ListUserResponse> users, OrgElementEntity orgElementEntity,
                               DingtalkDepartmentVo departmentNode, OrgVersionEntity orgVersionEntity, Map<String, Long> userMap, DingtalkConfigVo dingtalkConfigVo) {
        Long orgVersionUuid = orgVersionEntity.getUuid();
        DingtalkConfigVo.DingtalkOrgSyncOption orgSyncOption = dingtalkConfigVo.getConfiguration().getOrgSyncOption();
        for (OapiV2UserListResponse.ListUserResponse user : users) {
            Long deptId = departmentNode.getDepartment().getDeptId();
            Long deptElementUuid = orgElementEntity.getUuid();
            // 用户职位信息
            OrgElementEntity jobElementEntity = getOrCreateJobElement(user, deptId, deptElementUuid, orgVersionEntity, dingtalkConfigVo, true);
            Set<String> orgElementIds = Sets.newLinkedHashSet();
            orgElementIds.add(orgElementEntity.getId());
            if (CollectionUtils.size(user.getDeptIdList()) > 1) {
                orgElementIds.addAll(dingtalkDeptService.listOrgElementIdByDeptIdsAndOrgVersionUuid(user.getDeptIdList(), orgVersionUuid));
                if (orgSyncOption != null && orgSyncOption.isJob() && StringUtils.isNotBlank(user.getTitle())) {
                    orgElementIds.addAll(dingtalkJobService.listOrgElementIdByDeptIdsAndOrgVersionUuid(user.getTitle(), user.getDeptIdList(), orgVersionUuid));
                }
            }
            orgElementIds.removeAll(getJobDeptIds(orgElementIds));
            if (jobElementEntity != null) {
                orgElementIds.remove(orgElementEntity.getId());
                orgElementIds.add(jobElementEntity.getId());
            }

            String mobile = user.getMobile();
            UserInfoEntity userInfoEntity = orgFacadeService.getUserInfoByMobile(mobile);
            boolean added = false;
            if (userInfoEntity != null) {
                UserDto userDto = toUserDto(false, user, Lists.newArrayList(orgElementIds), orgVersionEntity, dingtalkConfigVo);
                userDto.setUuid(userInfoEntity.getUuid());
                userDto.setUserId(userInfoEntity.getUserId());
                userDto.setLoginName(userInfoEntity.getLoginName());
                String userInfoUuid = orgFacadeService.saveUser(userDto);
//                toUserInfo(user, userInfoEntity, dingtalkConfigVo);
//                orgFacadeService.saveUserInfo(userInfoEntity);
//                orgFacadeService.joinOrgUser(Lists.newArrayList(userInfoEntity.getUserId()), null, Collections.emptyList(),
//                        orgElementEntity.getId(), orgVersionUuid);
//                if (jobElementEntity != null) {
//                    orgFacadeService.joinOrgUser(Lists.newArrayList(userInfoEntity.getUserId()), null, Collections.emptyList(),
//                            jobElementEntity.getId(), orgVersionUuid);
//                }
            } else {
                added = true;
                UserDto userDto = toUserDto(true, user, Lists.newArrayList(orgElementIds), orgVersionEntity, dingtalkConfigVo);
                String userInfoUuid = orgFacadeService.saveUser(userDto);
                userInfoEntity = orgFacadeService.getUserInfoByUuid(userInfoUuid);
            }

            DingtalkUserEntity dingtalkUserEntity = null;
            if (userMap.containsKey(user.getUnionid())) {
                if (user.getLeader()) {
                    dingtalkUserEntity = updateDingtalkUserLeaderDept(user, departmentNode.getDepartment(), orgElementEntity, orgVersionEntity, dingtalkConfigVo);
                }
            } else {
                dingtalkUserEntity = saveDingtalkUser(user, userInfoEntity, departmentNode.getDepartment().getDeptId(),
                        orgElementEntity.getUuid(), orgVersionEntity, dingtalkConfigVo);
                userMap.put(user.getUnionid(), dingtalkUserEntity.getUuid());
            }

            if (dingtalkUserEntity != null) {
                if (added) {
                    DingtalkSyncLoggerHolder.addUser(dingtalkUserEntity);
                } else {
                    DingtalkSyncLoggerHolder.updateUser(dingtalkUserEntity);
                }
            }
        }
    }

    private List<String> getJobDeptIds(Set<String> orgElementIds) {
        List<String> jobDeptIds = Lists.newArrayList();
        orgElementIds.stream().filter(id -> StringUtils.startsWith(id, IdPrefix.JOB.getValue())).forEach(jobId -> {
            Long orgElementUuid = dingtalkJobService.getOrgElementUuidByOrgElementId(jobId);
            if (orgElementUuid != null) {
                String jobIdPath = orgFacadeService.getOrgElementPathByUuid(orgElementUuid);
                if (StringUtils.isNotBlank(jobIdPath)) {
                    List<String> idList = Arrays.asList(StringUtils.split(jobIdPath, Separator.SLASH.getValue()));
                    if (idList.indexOf(jobId) > 0) {
                        jobDeptIds.add(idList.get(idList.indexOf(jobId) - 1));
                    }
                }
            }
        });
        return jobDeptIds;
    }

    private DingtalkUserEntity updateDingtalkUserLeaderDept(OapiV2UserListResponse.ListUserResponse user, OapiV2DepartmentListsubResponse.DeptBaseResponse department,
                                                            OrgElementEntity orgElementEntity, OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkUserEntity dingtalkUserEntity = dingtalkUserService.getByUnionIdAndOrgVersionUuid(user.getUnionid(), orgVersionEntity.getUuid());
        if (dingtalkUserEntity == null) {
            return null;
        }

        dingtalkUserEntity.setLeader(user.getLeader());
        dingtalkUserEntity.setLeaderDeptId(department.getDeptId());
        dingtalkUserEntity.setLeaderOrgElementUuid(orgElementEntity.getUuid());
        dingtalkUserService.save(dingtalkUserEntity);
        return dingtalkUserEntity;
    }

    private OrgElementEntity getOrCreateJobElement(OapiV2UserListResponse.ListUserResponse user, Long deptId, Long deptElementUuid,
                                                   OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo, boolean log) {
        DingtalkConfigVo.DingtalkOrgSyncOption orgSyncOption = dingtalkConfigVo.getConfiguration().getOrgSyncOption();
        if (orgSyncOption == null) {
            orgSyncOption = new DingtalkConfigVo.DingtalkOrgSyncOption();
        }
        if (!orgSyncOption.isJob() || StringUtils.isBlank(user.getTitle()) || CollectionUtils.isEmpty(user.getDeptIdList())) {
            return null;
        }

        String syncJobMode = orgSyncOption.getJobMode();
        if (StringUtils.isBlank(syncJobMode)) {
            syncJobMode = "all";
        }

        List<Long> deptIdList = user.getDeptIdList();

        boolean matchSyncJob = false;
        switch (syncJobMode) {
            case "first":
                matchSyncJob = deptIdList.get(0).equals(deptId);
                break;
            case "last":
                matchSyncJob = deptIdList.get(deptIdList.size() - 1).equals(deptId);
                break;
            case "all":
                matchSyncJob = true;
                break;
        }

        if (matchSyncJob) {
            OrgElementDto jobElementDto = toJobElementDto(user, deptId, deptElementUuid, orgVersionEntity);
            Long orgElementUuid = jobElementDto.getUuid();
            if (orgElementUuid == null) {
                orgElementUuid = orgFacadeService.saveOrgElementDetails(jobElementDto);
            }
            OrgElementEntity jobElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
            if (jobElementEntity == null) {
                jobElementDto.setUuid(null);
                jobElementDto.setId(null);
                orgElementUuid = orgFacadeService.saveOrgElementDetails(jobElementDto);
                jobElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
            }
            DingtalkJobEntity dingtalkJobEntity = dingtalkJobService.getByOrgElementIdAndOrgVersionUuid(jobElementEntity.getId(), orgVersionEntity.getUuid());
            if (dingtalkJobEntity == null) {
                dingtalkJobEntity = saveDingtalkJob(user, deptId, jobElementEntity, orgVersionEntity, dingtalkConfigVo);
            }
            if (log) {
                if (jobElementDto.getUuid() == null) {
                    DingtalkSyncLoggerHolder.addJob(dingtalkJobEntity);
                } else {
                    DingtalkSyncLoggerHolder.updateJob(dingtalkJobEntity);
                }
            }
            return jobElementEntity;
        } else {
            return null;
        }
    }

    private DingtalkJobEntity saveDingtalkJob(OapiV2UserListResponse.ListUserResponse user, Long deptId,
                                              OrgElementEntity jobElementEntity, OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkJobEntity dingtalkJobEntity = new DingtalkJobEntity();
        dingtalkJobEntity.setConfigUuid(dingtalkConfigVo.getUuid());
        dingtalkJobEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        dingtalkJobEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        dingtalkJobEntity.setAppId(dingtalkConfigVo.getAppId());
        dingtalkJobEntity.setOrgElementUuid(jobElementEntity.getUuid());
        dingtalkJobEntity.setOrgElementId(jobElementEntity.getId());
        dingtalkJobEntity.setTitle(user.getTitle());
        dingtalkJobEntity.setDeptId(deptId);
        dingtalkJobService.save(dingtalkJobEntity);
        return dingtalkJobEntity;
    }

    private OrgElementDto toJobElementDto(OapiV2UserListResponse.ListUserResponse user, Long deptId, Long deptElementUuid,
                                          OrgVersionEntity orgVersionEntity) {
        String orgElementId = dingtalkJobService.getOrgElementIdByDeptIdAndTitle(deptId, user.getTitle());

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(user.getTitle());
        orgElementDto.setId(orgElementId);
        // orgElementDto.setCode(user.getJobNumber());
        orgElementDto.setType(OrgElementModelEntity.ORG_JOB_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(deptElementUuid);
        if (StringUtils.isNotBlank(orgElementId)) {
            Long orgElementUuid = dingtalkJobService.getOrgElementUuidByOrgElementId(orgElementId);
            if (orgElementUuid != null) {
                orgElementDto.setRoleUuids(orgFacadeService.listOrgElementRoleUuidByUuid(orgElementUuid));
            }
            orgElementUuid = dingtalkJobService.getOrgElementUuidByOrgElementIdAndOrgVersionUuid(orgElementId, orgVersionEntity.getUuid());
            orgElementDto.setUuid(orgElementUuid);
        }
        return orgElementDto;
    }

    private void toUserInfo(OapiV2UserListResponse.ListUserResponse user, UserInfoEntity userInfoEntity, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkConfigVo.DingtalkOrgSyncOption orgSyncOption = dingtalkConfigVo.getConfiguration().getOrgSyncOption();
        if (orgSyncOption == null) {
            orgSyncOption = new DingtalkConfigVo.DingtalkOrgSyncOption();
        }

        userInfoEntity.setUserName(user.getName());

        // 头像
        if (orgSyncOption.isUserAvatar()) {
            String avatarFileId = getAvatarFileId(user);
            userInfoEntity.setAvatar(avatarFileId);
        }
//        // 性别
//        if (orgSyncOption.isUserGender()) {
//            if (Integer.valueOf(1).equals(user.getGender())) {
//                userInfoEntity.setGender(UserInfoEntity.Gender.MALE);
//            } else if (Integer.valueOf(2).equals(user.getGender())) {
//                userInfoEntity.setGender(UserInfoEntity.Gender.FEMALE);
//            }
//        }
        // 手机号码
        if (StringUtils.startsWith(user.getMobile(), "+86")) {
            userInfoEntity.setCeilPhoneNumber(StringUtils.substring(user.getMobile(), 3));
        } else {
            userInfoEntity.setCeilPhoneNumber(user.getMobile());
        }
        // 分机号
        if (orgSyncOption.isUserTelephone()) {
            // TODO
        }
        // 邮箱
        if (orgSyncOption.isUserEmail()) {
            userInfoEntity.setMail(user.getEmail());
        }
        // 员工编号
        if (orgSyncOption.isUserNo()) {
            userInfoEntity.setUserNo(user.getJobNumber());
        }
        // 备注
        if (orgSyncOption.isUserRemark()) {
            userInfoEntity.setRemark(user.getRemark());
        }
    }

    private String getAvatarFileId(OapiV2UserListResponse.ListUserResponse user) {
        try {
            String avatar = user.getAvatar();
            if (StringUtils.isNotBlank(avatar)) {
                InputStream inputStream = HttpUtil.getInputStream(avatar);
                MongoFileEntity mongoFileEntity = mongoFileService.saveFile(user.getUserid() + ".png", inputStream);
                IOUtils.closeQuietly(inputStream);
                return mongoFileEntity.getFileID();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private UserDto toUserDto(boolean added, OapiV2UserListResponse.ListUserResponse user, List<String> orgElementIds,
                              OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkConfigVo.DingtalkOrgSyncOption orgSyncOption = dingtalkConfigVo.getConfiguration().getOrgSyncOption();
        if (orgSyncOption == null) {
            orgSyncOption = new DingtalkConfigVo.DingtalkOrgSyncOption();
        }

        UserDto userDto = new UserDto();
        // 姓名
        userDto.setUserName(user.getName());
        if (added) {
            userDto.setLoginName(generateLoginName(user));
            userDto.setPassword("123456");
        }
        // 头像
        if (orgSyncOption.isUserAvatar()) {
            String avatarFileId = getAvatarFileId(user);
            userDto.setAvatar(avatarFileId);
        }
//        // 性别
//        if (orgSyncOption.isUserGender()) {
//            if (Integer.valueOf(1).equals(user.getGender())) {
//                userDto.setGender(UserInfoEntity.Gender.MALE);
//            } else if (Integer.valueOf(2).equals(user.getGender())) {
//                userDto.setGender(UserInfoEntity.Gender.FEMALE);
//            }
//        }
        // 手机号码
        if (StringUtils.startsWith(user.getMobile(), "+86")) {
            userDto.setCeilPhoneNumber(StringUtils.substring(user.getMobile(), 3));
        } else {
            userDto.setCeilPhoneNumber(user.getMobile());
        }
        // 分机号
        if (orgSyncOption.isUserTelephone()) {
            List<UserInfoExtEntity> userInfoExtEntities = Lists.newArrayList();
            UserInfoExtEntity userInfoExtEntity = new UserInfoExtEntity();
            userInfoExtEntity.setAttrKey("businessPhoneNumber");
            userInfoExtEntity.setAttrValue(user.getTelephone());
            userInfoExtEntities.add(userInfoExtEntity);
            userDto.setUserInfoExts(userInfoExtEntities);
        }
        // 邮箱
        if (orgSyncOption.isUserEmail()) {
            userDto.setMail(user.getEmail());
        }
        // 员工编号
        if (orgSyncOption.isUserNo()) {
            userDto.setUserNo(user.getJobNumber());
        }
        // 备注
        if (orgSyncOption.isUserRemark()) {
            userDto.setRemark(user.getRemark());
        }
        userDto.setOrgVersionUuid(orgVersionEntity.getUuid());

        Map<String, List<String>> orgReportTos = Maps.newHashMap();
        List<OrgUserEntity> orgUserEntities = Lists.newArrayList();
        orgElementIds.forEach(orgElementId -> {
            orgReportTos.put(orgElementId, Collections.emptyList());
            OrgUserEntity orgUserEntity = new OrgUserEntity();
            orgUserEntity.setOrgElementId(orgElementId);
            if (StringUtils.startsWith(orgElementId, IdPrefix.JOB.getValue())) {
                orgUserEntity.setType(OrgUserEntity.Type.PRIMARY_JOB_USER);
            } else {
                orgUserEntity.setType(OrgUserEntity.Type.MEMBER_USER);
            }
            orgUserEntities.add(orgUserEntity);
        });
        userDto.setOrgReportTos(orgReportTos);
        userDto.setOrgUsers(orgUserEntities);
        return userDto;
    }

    private String generateLoginName(OapiV2UserListResponse.ListUserResponse user) {
        String userName = StringUtils.replace(StringUtils.trim(user.getName()), " ", "");
        String loginName = PinyinUtil.getLoginName(userName);
        int userCount = 0;
        UserInfoEntity userInfoEntity = orgFacadeService.getUserInfoByLoginName(loginName);
        boolean existAccountByLoginName = orgFacadeService.existAccountByLoginName(loginName);
        while (userInfoEntity != null || existAccountByLoginName) {
            String testLoginName = loginName + ++userCount;
            userInfoEntity = orgFacadeService.getUserInfoByLoginName(testLoginName);
            existAccountByLoginName = orgFacadeService.existAccountByLoginName(testLoginName);
        }
        return userCount > 0 ? loginName + userCount : loginName;
    }

    private DingtalkUserEntity saveDingtalkUser(OapiV2UserListResponse.ListUserResponse user, UserInfoEntity userInfoEntity,
                                                Long leaderDeptId, Long leaderElementUuid, OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkUserEntity dingtalkUserEntity = new DingtalkUserEntity();
        this.updateDingtalkUser(dingtalkUserEntity, user, userInfoEntity, leaderDeptId, leaderElementUuid, orgVersionEntity, dingtalkConfigVo);
        return dingtalkUserEntity;
    }

    private void updateDingtalkUser(DingtalkUserEntity dingtalkUserEntity, OapiV2UserListResponse.ListUserResponse user,
                                    UserInfoEntity userInfoEntity, Long leaderDeptId, Long leaderElementUuid, OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        dingtalkUserEntity.setConfigUuid(dingtalkConfigVo.getUuid());
        dingtalkUserEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        dingtalkUserEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        dingtalkUserEntity.setAppId(dingtalkConfigVo.getAppId());
        dingtalkUserEntity.setOaUserId(userInfoEntity.getUserId());
        dingtalkUserEntity.setUnionId(user.getUnionid());
        dingtalkUserEntity.setUserId(user.getUserid());
        dingtalkUserEntity.setName(user.getName());
        dingtalkUserEntity.setAvatar(user.getAvatar());
        dingtalkUserEntity.setMobile(user.getMobile());
        dingtalkUserEntity.setHideMobile(user.getHideMobile());
        dingtalkUserEntity.setTelephone(user.getTelephone());
        dingtalkUserEntity.setJobNumber(user.getJobNumber());
        dingtalkUserEntity.setTitle(user.getTitle());
        dingtalkUserEntity.setEmail(user.getEmail());
        dingtalkUserEntity.setLeader(user.getLeader());
        if (BooleanUtils.isTrue(user.getLeader())) {
            dingtalkUserEntity.setLeaderDeptId(leaderDeptId);
            dingtalkUserEntity.setLeaderOrgElementUuid(leaderElementUuid);
        }
        if (CollectionUtils.isNotEmpty(user.getDeptIdList())) {
            dingtalkUserEntity.setDeptIdList(StringUtils.join(user.getDeptIdList(), Separator.SEMICOLON.getValue()));
        }
        dingtalkUserEntity.setDeptOrder(user.getDeptOrder());
        if (user.getHiredDate() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(user.getHiredDate());
            dingtalkUserEntity.setHiredDate(calendar.getTime());
        }
        dingtalkUserEntity.setActive(user.getActive());
        dingtalkUserEntity.setRemark(user.getRemark());
        dingtalkUserEntity.setExtension(user.getExtension());
        dingtalkUserService.save(dingtalkUserEntity);
    }

    @Override
    @Transactional
    public void createUser(OapiV2UserGetResponse.UserGetResponse userGetResponse, DingtalkConfigVo dingtalkConfigVo) {
        updateUser(userGetResponse, dingtalkConfigVo);
    }

    @Override
    @Transactional
    public void updateUser(OapiV2UserGetResponse.UserGetResponse userGetResponse, DingtalkConfigVo dingtalkConfigVo) {
        DingtalkConfigVo.DingtalkOrgSyncOption orgSyncOption = dingtalkConfigVo.getConfiguration().getOrgSyncOption();
        Long orgUuid = Long.valueOf(dingtalkConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long orgVersionUuid = orgVersionEntity.getUuid();
        String mobile = userGetResponse.getMobile();
        DingtalkUserEntity dingtalkUserEntity = dingtalkUserService.getByUserIdAndOrgVersionUuid(userGetResponse.getUserid(), orgVersionUuid);
        UserInfoEntity userInfoEntity = null;
        if (dingtalkUserEntity != null) {
            userInfoEntity = orgFacadeService.getUserInfoByUserId(dingtalkUserEntity.getOaUserId());
        } else {
            userInfoEntity = orgFacadeService.getUserInfoByMobile(mobile);
        }

        OapiV2UserListResponse.ListUserResponse user = userGetResponse2User(userGetResponse);
        Set<String> orgElementIds = Sets.newLinkedHashSet();
        // 用户职位信息
        if (CollectionUtils.isNotEmpty(user.getDeptIdList())) {
            if (orgSyncOption != null && orgSyncOption.isJob()) {
                user.getDeptIdList().forEach(deptId -> {
                    Long deptElementUuid = dingtalkDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(deptId, orgVersionUuid);
                    OrgElementEntity jobElementEntity = getOrCreateJobElement(user, deptId, deptElementUuid, orgVersionEntity, dingtalkConfigVo, false);
                    if (jobElementEntity != null) {
                        orgElementIds.add(jobElementEntity.getId());
                    }
                });

                orgElementIds.addAll(dingtalkDeptService.listOrgElementIdByDeptIdsAndOrgVersionUuid(user.getDeptIdList(), orgVersionUuid));
                List<String> jobDeptIds = getJobDeptIds(orgElementIds);
                orgElementIds.removeAll(jobDeptIds);
            } else {
                orgElementIds.addAll(dingtalkDeptService.listOrgElementIdByDeptIdsAndOrgVersionUuid(user.getDeptIdList(), orgVersionUuid));
            }
        }

        if (userInfoEntity != null) {
            UserDto userDto = toUserDto(false, user, Lists.newArrayList(orgElementIds), orgVersionEntity, dingtalkConfigVo);
            userDto.setUuid(userInfoEntity.getUuid());
            userDto.setUserId(userInfoEntity.getUserId());
            userDto.setLoginName(userInfoEntity.getLoginName());
            orgFacadeService.saveUser(userDto);
        } else {
            UserDto userDto = toUserDto(true, user, Lists.newArrayList(orgElementIds), orgVersionEntity, dingtalkConfigVo);
            String userInfoUuid = orgFacadeService.saveUser(userDto);
            userInfoEntity = orgFacadeService.getUserInfoByUuid(userInfoUuid);
        }
        dingtalkUserService.flushSession();
        dingtalkUserService.clearSession();

        // 更新部门负责人
        DingtalkDepartmentVo.DeptDirectorLeaderInfo leaderInfo = updateUserDeptDirectorLeader(dingtalkUserEntity, userGetResponse, userInfoEntity.getUserId(), orgVersionUuid);
        Long leaderDeptId = leaderInfo.getDeptId();
        Long leaderElementUuid = leaderInfo.getOrgElementUuid();
        if (dingtalkUserEntity != null) {
            updateDingtalkUser(dingtalkUserEntity, user, userInfoEntity, leaderDeptId, leaderElementUuid, orgVersionEntity, dingtalkConfigVo);
            DingtalkEventHoler.success("updatedDingtalkUser", dingtalkUserEntity);
        } else {
            dingtalkUserEntity = saveDingtalkUser(user, userInfoEntity, leaderDeptId, leaderElementUuid, orgVersionEntity, dingtalkConfigVo);
            DingtalkEventHoler.success("createdDingtalkUser", dingtalkUserEntity);
        }
    }

    private DingtalkDepartmentVo.DeptDirectorLeaderInfo updateUserDeptDirectorLeader(DingtalkUserEntity dingtalkUserEntity,
                                                                                     OapiV2UserGetResponse.UserGetResponse userGetResponse,
                                                                                     String oaUserId, Long orgVersionUuid) {
        Long leaderDeptId = null;
        Long leaderElementUuid = null;
        // 更新前的部门
        List<String> existsOrgElementIds = Lists.newArrayList();
        if (dingtalkUserEntity != null && StringUtils.isNotBlank(dingtalkUserEntity.getDeptIdList())) {
            existsOrgElementIds.addAll(Arrays.asList(dingtalkUserEntity.getDeptIdList().split(Separator.SEMICOLON.getValue())));
        }
        existsOrgElementIds.forEach(existsOrgElementId -> {
            if (!userGetResponse.getDeptIdList().contains(Long.valueOf(existsOrgElementId))) {
                Long orgElementUuid = dingtalkDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(Long.valueOf(existsOrgElementId), orgVersionUuid);
                if (orgElementUuid != null) {
                    OrgElementManagementEntity orgElementManagement = orgFacadeService.getOrgElementManagementByUuid(orgElementUuid);
                    if (orgElementManagement == null) {
                        return;
                    }
                    Set<String> directors = Sets.newLinkedHashSet();
                    if (StringUtils.isNotBlank(orgElementManagement.getDirector())) {
                        directors.addAll(Arrays.asList(orgElementManagement.getDirector().split(Separator.SEMICOLON.getValue())));
                    }
                    directors.remove(oaUserId);
                    orgFacadeService.saveOrgElementLeader(orgElementUuid, oaUserId, null, null);
                }
            }
        });
        // 更新后的部门
        if (CollectionUtils.isNotEmpty(userGetResponse.getLeaderInDept())) {
            for (int index = 0; index < userGetResponse.getLeaderInDept().size(); index++) {
                OapiV2UserGetResponse.DeptLeader deptLeader = userGetResponse.getLeaderInDept().get(index);
                Long deptId = deptLeader.getDeptId();
                Long elementUuid = dingtalkDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(deptId, orgVersionUuid);
                if (elementUuid != null) {
                    Set<String> directors = Sets.newLinkedHashSet();
                    OrgElementManagementEntity orgElementManagement = orgFacadeService.getOrgElementManagementByUuid(elementUuid);
                    if (orgElementManagement == null) {
                        if (BooleanUtils.isTrue(deptLeader.getLeader())) {
                            orgFacadeService.saveOrgElementLeader(elementUuid, oaUserId, null, null);
                            leaderDeptId = deptId;
                            leaderElementUuid = elementUuid;
                        }
                    } else {
                        if (StringUtils.isNotBlank(orgElementManagement.getDirector())) {
                            directors.addAll(Arrays.asList(orgElementManagement.getDirector().split(Separator.SEMICOLON.getValue())));
                        }
                        if (BooleanUtils.isTrue(deptLeader.getLeader())) {
                            directors.add(oaUserId);
                            leaderDeptId = deptId;
                            leaderElementUuid = elementUuid;
                        } else {
                            directors.remove(oaUserId);
                        }
                        orgFacadeService.saveOrgElementLeader(elementUuid, StringUtils.join(directors, Separator.SEMICOLON.getValue()), null, null);
                    }
                }
            }
        }
        DingtalkDepartmentVo.DeptDirectorLeaderInfo leaderInfo = new DingtalkDepartmentVo.DeptDirectorLeaderInfo();
        leaderInfo.setDeptId(leaderDeptId);
        leaderInfo.setOrgElementUuid(leaderElementUuid);
        return leaderInfo;
    }

    private OapiV2UserListResponse.ListUserResponse userGetResponse2User(OapiV2UserGetResponse.UserGetResponse userGetResponse) {
        OapiV2UserListResponse.ListUserResponse user = new OapiV2UserListResponse.ListUserResponse();
        user.setUnionid(userGetResponse.getUnionid());
        user.setUserid(userGetResponse.getUserid());
        user.setName(userGetResponse.getName());
        user.setAvatar(userGetResponse.getAvatar());
        user.setMobile(userGetResponse.getMobile());
        user.setHideMobile(userGetResponse.getHideMobile());
        user.setTelephone(userGetResponse.getTelephone());
        user.setJobNumber(userGetResponse.getJobNumber());
        user.setTitle(userGetResponse.getTitle());
        user.setEmail(userGetResponse.getEmail());
        if (CollectionUtils.isNotEmpty(userGetResponse.getLeaderInDept())) {
            user.setLeader(userGetResponse.getLeaderInDept().stream().filter(item -> BooleanUtils.isTrue(item.getLeader())).findFirst().isPresent());
        } else {
            user.setLeader(false);
        }
        user.setDeptIdList(userGetResponse.getDeptIdList());
        if (CollectionUtils.isNotEmpty(userGetResponse.getDeptOrderList())) {
            user.setDeptOrder(userGetResponse.getDeptOrderList().get(0).getOrder());
        }
        user.setHiredDate(userGetResponse.getHiredDate());
        user.setActive(userGetResponse.getActive());
        user.setRemark(userGetResponse.getRemark());
        user.setExtension(userGetResponse.getExtension());
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(String userId, DingtalkConfigVo dingtalkConfigVo) {
        Long orgUuid = Long.valueOf(dingtalkConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        DingtalkUserEntity dingtalkUserEntity = dingtalkUserService.getByUserIdAndOrgVersionUuid(userId, orgVersionEntity.getUuid());
        if (dingtalkUserEntity == null) {
            String errorMsg = String.format("组织版本[%d]不同步，用户不存在，无法删除用户信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            DingtalkEventHoler.error(errorMsg);
            return;
        }

        orgFacadeService.removeOrgUser(Lists.newArrayList(dingtalkUserEntity.getOaUserId()), orgVersionEntity.getUuid());

        // 更新钉钉用户状态为禁用
        dingtalkUserEntity.setActive(false);
        dingtalkUserService.save(dingtalkUserEntity);
        DingtalkEventHoler.success("deletedDingtalkUser", dingtalkUserEntity);
    }

    @Override
    @Transactional
    public void activeUser(String userId, DingtalkConfigVo dingtalkConfigVo) {
        Long orgUuid = Long.valueOf(dingtalkConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        DingtalkUserEntity dingtalkUserEntity = dingtalkUserService.getByUserIdAndOrgVersionUuid(userId, orgVersionEntity.getUuid());
        if (dingtalkUserEntity == null) {
            String errorMsg = String.format("组织版本[%d]不同步，用户不存在，无法激活用户", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            DingtalkEventHoler.error(errorMsg);
            return;
        }

        UserInfoEntity userInfoEntity = orgFacadeService.getUserInfoByUserId(dingtalkUserEntity.getOaUserId());
        UserAccountEntity accountEntity = userAccountService.getByLoginName(userInfoEntity.getLoginName());
        if (accountEntity != null) {
            accountEntity.setIsAccountNonLocked(true);
            userAccountService.save(accountEntity);
        }

        // 更新钉钉用户状态为激活
        dingtalkUserEntity.setActive(true);
        dingtalkUserService.save(dingtalkUserEntity);
        DingtalkEventHoler.success("activedDingtalkUser", dingtalkUserEntity);
    }

    @Override
    @Transactional
    public void createDepartment(OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse, DingtalkConfigVo dingtalkConfigVo) {
        Long parentId = deptGetResponse.getParentId();
        Long orgUuid = Long.valueOf(dingtalkConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long parentOrgElementUuid = null;
        // 非根结点下添加部门
        if (!Long.valueOf(1).equals(parentId)) {
            parentOrgElementUuid = dingtalkDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(parentId, orgVersionEntity.getUuid());
            if (parentOrgElementUuid == null) {
                String errorMsg = String.format("组织版本[%d]不同步，上级部门不存在，无法创建部门信息", orgVersionEntity.getUuid());
                logger.error(errorMsg);
                DingtalkEventHoler.error(errorMsg);
                return;
            }
        }

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(deptGetResponse.getName());
        // orgElementDto.setId(orgElementId);
//        if (NumberUtils.isNumber(department.getOrder())) {
//            orgElementDto.setSeq(NumberUtils.createNumber(department.getOrder()).intValue());
//        }
        orgElementDto.setCode(deptGetResponse.getDeptId() + StringUtils.EMPTY);
        orgElementDto.setType(OrgElementModelEntity.ORG_DEPT_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(parentOrgElementUuid);
        setDepartmentI18nName(orgElementDto, deptGetResponse.getExtention());
        List<String> leaderUserIds = deptGetResponse.getDeptManagerUseridList();
        if (CollectionUtils.isNotEmpty(leaderUserIds)) {
            leaderUserIds = dingtalkUserService.listOaUserIdByUserIdAndOrgVersionUuid(leaderUserIds, orgVersionEntity.getUuid());
            if (CollectionUtils.isNotEmpty(leaderUserIds)) {
                OrgElementManagementEntity management = new OrgElementManagementEntity();
                management.setDirector(StringUtils.join(Sets.newLinkedHashSet(leaderUserIds), Separator.SEMICOLON.getValue()));
                orgElementDto.setManagement(management);
            }
        }
        Long orgElementUuid = orgFacadeService.saveOrgElementDetails(orgElementDto);

        // 同步钉钉部门信息
        OrgElementEntity orgElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
        DingtalkDepartmentVo departmentVo = new DingtalkDepartmentVo();
        OapiV2DepartmentListsubResponse.DeptBaseResponse department = new OapiV2DepartmentListsubResponse.DeptBaseResponse();
        department.setName(deptGetResponse.getName());
        department.setDeptId(deptGetResponse.getDeptId());
        department.setParentId(deptGetResponse.getParentId());
        department.setExt(deptGetResponse.getExtention());
        departmentVo.setDepartment(department);
        DingtalkDeptEntity dingtalkDeptEntity = saveDingtalkDept(departmentVo, orgElementEntity, orgVersionEntity, dingtalkConfigVo);

        DingtalkEventHoler.success("createdDingtalkDept", dingtalkDeptEntity);
    }

    @Override
    @Transactional
    public void updateDepartment(OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse, DingtalkConfigVo dingtalkConfigVo) {
        Long parentId = deptGetResponse.getParentId();
        Long deptId = deptGetResponse.getDeptId();
        Long orgUuid = Long.valueOf(dingtalkConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long orgElementUuid = dingtalkDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(deptId, orgVersionEntity.getUuid());
        Long parentOrgElementUuid = null;
        if (!Long.valueOf(1).equals(parentId)) {
            parentOrgElementUuid = dingtalkDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(parentId, orgVersionEntity.getUuid());
            if (parentOrgElementUuid == null || orgElementUuid == null) {
                String errorMsg = String.format("组织版本[%d]不同步，无法更新部门信息", orgVersionEntity.getUuid());
                logger.error(errorMsg);
                DingtalkEventHoler.error(errorMsg);
                return;
            }
        } else if (orgElementUuid == null) {
            String errorMsg = String.format("组织版本[%d]不同步，无法更新部门信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            DingtalkEventHoler.error(errorMsg);
            return;
        }

        OrgElementDto orgElementDto = orgFacadeService.getOrgElementDetailsByUuid(orgElementUuid);
        if (orgElementDto == null) {
            String errorMsg = String.format("组织版本[%d]中不存在部门[%s]，无法更新部门信息", orgVersionEntity.getUuid(), deptGetResponse.getName());
            logger.error(errorMsg);
            DingtalkEventHoler.error(errorMsg);
            return;
        }

        orgElementDto.setName(deptGetResponse.getName());
        // orgElementDto.setSeq(departmentEvent.getOrder());
        orgElementDto.setParentUuid(parentOrgElementUuid);

        List<String> leaderUserIds = deptGetResponse.getDeptManagerUseridList();
        OrgElementManagementEntity management = orgElementDto.getManagement();
        if (CollectionUtils.isNotEmpty(leaderUserIds)) {
            leaderUserIds = dingtalkUserService.listOaUserIdByUserIdAndOrgVersionUuid(leaderUserIds, orgVersionEntity.getUuid());
            if (CollectionUtils.isNotEmpty(leaderUserIds)) {
                if (management != null) {
                    management.setDirector(StringUtils.join(Sets.newLinkedHashSet(leaderUserIds), Separator.SEMICOLON.getValue()));
                } else {
                    management = new OrgElementManagementEntity();
                    management.setDirector(StringUtils.join(Sets.newLinkedHashSet(leaderUserIds), Separator.SEMICOLON.getValue()));
                }
            } else if (management != null) {
                management.setDirector(null);
            }
        } else if (management != null) {
            management.setDirector(null);
        }
        orgElementDto.setManagement(management);
        // 清空国际化信息记录UUID
        List<OrgElementI18nEntity> i18nEntities = orgElementDto.getI18ns();
        if (CollectionUtils.isNotEmpty(i18nEntities)) {
            List<OrgElementI18nEntity> newI18nEntities = Lists.newArrayList();
            i18nEntities.forEach(i18n -> {
                OrgElementI18nEntity i18nEntity = new OrgElementI18nEntity();
                BeanUtils.copyProperties(i18n, i18nEntity, Entity.BASE_FIELDS);
                newI18nEntities.add(i18nEntity);
            });
            orgElementDto.setI18ns(newI18nEntities);
        }
        String extentionJson = deptGetResponse.getExtention();
        if (StringUtils.isNotBlank(extentionJson)) {
            DingtalkDepartmentVo.Extension extension = JsonUtils.json2Object(extentionJson, DingtalkDepartmentVo.Extension.class);
            List<OrgElementI18nEntity> existsI18nEntities = orgElementDto.getI18ns();
            if (extension != null) {
                extension.toI18ns().forEach(item -> {
                    Map<String, String> i18nValues = item.getI18nValues();
                    if (MapUtils.isEmpty(i18nValues)) {
                        existsI18nEntities.removeAll(existsI18nEntities.stream().filter(i -> StringUtils.equals(i.getDataCode(), item.getName())).collect(Collectors.toList()));
                    } else {
                        i18nValues.forEach((key, value) -> {
                            OrgElementI18nEntity elementI18nEntity = existsI18nEntities.stream().filter(i -> StringUtils.equals(i.getDataCode(), item.getName())
                                    && StringUtils.equals(i.getLocale(), key)).findFirst().orElse(null);
                            if (elementI18nEntity != null) {
                                elementI18nEntity.setContent(value);
                            } else {
                                OrgElementI18nEntity i18nEntity = new OrgElementI18nEntity();
                                i18nEntity.setLocale(key);
                                i18nEntity.setDataCode(item.getName());
                                i18nEntity.setContent(value);
                                existsI18nEntities.add(i18nEntity);
                            }
                        });
                    }
                });
            }
        }
        orgFacadeService.saveOrgElementDetails(orgElementDto);

        // 更新钉钉部门信息
        DingtalkDeptEntity dingtalkDeptEntity = dingtalkDeptService.getByDeptIdAndOrgVersionUuid(deptId, orgVersionEntity.getUuid());
        updateDingtalkDept(deptGetResponse, dingtalkDeptEntity, orgElementDto, orgVersionEntity, dingtalkConfigVo);

        DingtalkEventHoler.success("updatedDingtalkDept", dingtalkDeptEntity);
    }

    private void updateDingtalkDept(OapiV2DepartmentGetResponse.DeptGetResponse deptGetResponse, DingtalkDeptEntity dingtalkDeptEntity,
                                    OrgElementDto orgElementDto, OrgVersionEntity orgVersionEntity, DingtalkConfigVo dingtalkConfigVo) {
        dingtalkDeptEntity.setConfigUuid(dingtalkConfigVo.getUuid());
        dingtalkDeptEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        dingtalkDeptEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        dingtalkDeptEntity.setAppId(dingtalkConfigVo.getAppId());
        dingtalkDeptEntity.setOrgElementUuid(orgElementDto.getUuid());
        dingtalkDeptEntity.setOrgElementId(orgElementDto.getId());
        dingtalkDeptEntity.setName(deptGetResponse.getName());
        dingtalkDeptEntity.setDeptId(deptGetResponse.getDeptId());
        dingtalkDeptEntity.setParentId(deptGetResponse.getParentId());
        dingtalkDeptEntity.setExt(deptGetResponse.getExtention());
        dingtalkDeptEntity.setStatus(DingtalkDeptEntity.Status.NORMAL);
        dingtalkDeptService.save(dingtalkDeptEntity);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long deptId, DingtalkConfigVo dingtalkConfigVo) {
        Long orgUuid = Long.valueOf(dingtalkConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        DingtalkDeptEntity dingtalkDeptEntity = dingtalkDeptService.getByDeptIdAndOrgVersionUuid(deptId, orgVersionEntity.getUuid());
        if (dingtalkDeptEntity == null || DingtalkDeptEntity.Status.DELETED.equals(dingtalkDeptEntity.getStatus())) {
            String errorMsg = String.format("组织版本[%d]不同步，部门不存在，无法删除部门信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            DingtalkEventHoler.error(errorMsg);
            return;
        }

        orgFacadeService.deleteOrgElementByUuid(dingtalkDeptEntity.getOrgElementUuid());

        // 更新钉钉部门状态为删除
        dingtalkDeptEntity.setStatus(DingtalkDeptEntity.Status.DELETED);
        dingtalkDeptService.save(dingtalkDeptEntity);

        DingtalkEventHoler.success("deletedDingtalkDept", dingtalkDeptEntity);
    }

    @Override
    public boolean isExistsDepartment(Long deptId) {
        String elementId = dingtalkDeptService.getOrgElementIdByDeptId(deptId);
        return StringUtils.isNotBlank(elementId);
    }

    @Override
    public DingtalkDeptEntity getDingtalkDeptById(Long deptId) {
        return dingtalkDeptService.getByDeptId(deptId);
    }

}
