/*
 * @(#)5/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.weixin.entity.WeixinDeptEntity;
import com.wellsoft.pt.app.weixin.entity.WeixinUserEntity;
import com.wellsoft.pt.app.weixin.facade.service.WeixinOrgSyncFacadeService;
import com.wellsoft.pt.app.weixin.service.WeixinDeptService;
import com.wellsoft.pt.app.weixin.service.WeixinUserService;
import com.wellsoft.pt.app.weixin.support.WeixinEventHoler;
import com.wellsoft.pt.app.weixin.support.WeixinSyncLoggerHolder;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.app.weixin.vo.WeixinDepartmentVo;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.dto.OrgElementDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.entity.UserInfoExtEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/21/25.1	    zhulh		5/21/25		    Create
 * </pre>
 * @date 5/21/25
 */
@Service
public class WeixinOrgSyncFacadeServiceImpl extends AbstractApiFacade implements WeixinOrgSyncFacadeService {

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private WeixinDeptService weixinDeptService;

    @Autowired
    private WeixinUserService weixinUserService;

    @Override
    @Transactional
    public void syncOrg(List<WeixinDepartmentVo> departmentVos, WeixinConfigVo weixinConfigVo) {
        WeixinConfigVo.WeixinConfiguration configuration = weixinConfigVo.getConfiguration();
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(Long.valueOf(configuration.getOrgUuid()));
        WeixinSyncLoggerHolder.orgVersion(orgVersionEntity);
        long count = orgFacadeService.countOrgElementByOrgVersionUuid(orgVersionEntity.getUuid());
        Map<Long, Long> departmentMap = new HashMap<>();
        Map<String, Long> userMap = new HashMap<>();
        // 存在组织元素，升级新版本
        if (count > 0) {
            orgVersionEntity = orgFacadeService.createEmptyOrgVersionFromOrgVersion(orgVersionEntity, false, true);
        }
        saveDepartmentVos(departmentVos, orgVersionEntity, null, departmentMap, userMap, weixinConfigVo);

        // 设置部门负责人、用户直属领导
        saveDeptmentDirectorAndUserDirectLeader(orgVersionEntity, weixinConfigVo);
    }

    private void saveDeptmentDirectorAndUserDirectLeader(OrgVersionEntity orgVersionEntity, WeixinConfigVo weixinConfigVo) {
        // 部门负责人
        List<WeixinUserEntity> deptLeaderUserEntities = weixinUserService.listLeaderUserByOrgVersionUuid(orgVersionEntity.getUuid());
        deptLeaderUserEntities.forEach(weixinUserEntity -> {
            String isLeaderInDepts = weixinUserEntity.getIsLeaderInDepts();
            String departmentIds = weixinUserEntity.getDepartmentIds();
            if (StringUtils.isBlank(isLeaderInDepts) || StringUtils.isBlank(departmentIds)) {
                return;
            }
            WeixinDepartmentVo.User user = new WeixinDepartmentVo.User();
            user.setIsLeaderInDept(Arrays.asList(StringUtils.split(isLeaderInDepts, Separator.SEMICOLON.getValue()))
                    .stream().flatMap(isLeader -> Stream.of(Integer.valueOf(isLeader))).collect(Collectors.toList()));
            user.setDepartment(Arrays.asList(StringUtils.split(departmentIds, Separator.SEMICOLON.getValue()))
                    .stream().flatMap(departmentId -> Stream.of(Long.valueOf(departmentId))).collect(Collectors.toList()));
            updateUserDeptDirectorLeader(weixinUserEntity, user, weixinUserEntity.getOaUserId(), orgVersionEntity.getUuid());
        });

        // 直属领导
        List<WeixinUserEntity> weixinUserEntities = weixinUserService.listWithDirectLeaderByOrgVersionUuid(orgVersionEntity.getUuid());
        weixinUserEntities.forEach(weixinUserEntity -> {
            if (StringUtils.isNotBlank(weixinUserEntity.getDirectLeaders())) {
                List<String> directLeader = Arrays.asList(StringUtils.split(weixinUserEntity.getDirectLeaders(), Separator.SEMICOLON.getValue()));
                String orgElementId = weixinDeptService.getOrgElementIdByIdAndConfigUuid(weixinUserEntity.getMainDepartment(), weixinConfigVo.getUuid());
                List<String> directLeaderIds = weixinUserService.listOaUserIdByUserIdAndOrgVersionUuid(directLeader, orgVersionEntity.getUuid());
                Map<String, List<String>> orgReportTos = Maps.newHashMap();
                orgReportTos.put(orgElementId, directLeaderIds);
                orgFacadeService.saveUserReportRelation(weixinUserEntity.getOaUserId(), orgReportTos, orgVersionEntity.getUuid());
            }
        });
    }

    private void saveDepartmentVos(List<WeixinDepartmentVo> departmentVos, OrgVersionEntity orgVersionEntity, Object o, Map<Long, Long> departmentMap, Map<String, Long> userMap, WeixinConfigVo weixinConfigVo) {
        for (WeixinDepartmentVo departmentVo : departmentVos) {
            OrgElementEntity orgElementEntity = null;
            if (!Long.valueOf(1).equals(departmentVo.getDepartment().getId())) {
                OrgElementDto orgElementDto = toOrgElementDto(departmentVo, orgVersionEntity, departmentMap, weixinConfigVo);
                boolean added = StringUtils.isBlank(orgElementDto.getId());
                Long orgElementUuid = orgFacadeService.saveOrgElementDetails(orgElementDto);
                orgElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
                WeixinDeptEntity weixinDeptEntity = saveWeixinDept(departmentVo, orgElementEntity, orgVersionEntity, weixinConfigVo);
                if (added) {
                    WeixinSyncLoggerHolder.addDept(weixinDeptEntity);
                } else {
                    WeixinSyncLoggerHolder.updateDept(weixinDeptEntity);
                }

                departmentMap.put(departmentVo.getDepartment().getId(), orgElementUuid);
            }

            List<WeixinDepartmentVo.User> users = departmentVo.getUsers();
            if (CollectionUtils.isNotEmpty(users)) {
                saveDeptUsers(users, orgElementEntity, departmentVo, orgVersionEntity, userMap, weixinConfigVo);
            }

            if (CollectionUtils.isNotEmpty(departmentVo.getChildren())) {
                saveDepartmentVos(departmentVo.getChildren(), orgVersionEntity, departmentVo, departmentMap, userMap, weixinConfigVo);
            }
            weixinDeptService.flushSession();
            weixinDeptService.clearSession();
        }
    }

    private void saveDeptUsers(List<WeixinDepartmentVo.User> users, OrgElementEntity orgElementEntity, WeixinDepartmentVo departmentVo, OrgVersionEntity orgVersionEntity, Map<String, Long> userMap, WeixinConfigVo weixinConfigVo) {
        Long orgVersionUuid = orgVersionEntity.getUuid();
        WeixinConfigVo.WeixinOrgSyncOption orgSyncOption = weixinConfigVo.getConfiguration().getOrgSyncOption();
        for (WeixinDepartmentVo.User user : users) {
            WeixinUserEntity weixinUserEntity = weixinUserService.getByUserIdAndCorpId(user.getUserId(), weixinConfigVo.getCorpId());
            UserInfoEntity userInfoEntity = null;
            if (weixinUserEntity != null) {
                userInfoEntity = orgFacadeService.getUserInfoByUserId(weixinUserEntity.getOaUserId());
            }
            Set<String> orgElementIds = Sets.newLinkedHashSet();
            if (orgElementEntity != null) {
                orgElementIds.add(orgElementEntity.getId());
            }
            if (userMap.containsKey(user.getUserId())) {
                orgElementIds.addAll(weixinDeptService.listOrgElementIdByIdsAndOrgVersionUuid(user.getDepartment(), orgVersionUuid));
            }
            boolean added = false;
            if (userInfoEntity != null) {
                UserDto userDto = toUserDto(added, user, Lists.newArrayList(orgElementIds), orgVersionEntity, weixinConfigVo);
                userDto.setUuid(userInfoEntity.getUuid());
                userDto.setUserId(userInfoEntity.getUserId());
                userDto.setLoginName(userInfoEntity.getLoginName());
                String userInfoUuid = orgFacadeService.saveUser(userDto);
            } else {
                added = true;
                UserDto userDto = toUserDto(added, user, Lists.newArrayList(orgElementIds), orgVersionEntity, weixinConfigVo);
                String userInfoUuid = orgFacadeService.saveUser(userDto);
                userInfoEntity = orgFacadeService.getUserInfoByUuid(userInfoUuid);
            }

            if (!userMap.containsKey(user.getUserId())) {
                weixinUserEntity = saveWeixinUser(user, userInfoEntity, orgVersionEntity, weixinConfigVo);
                userMap.put(user.getUserId(), weixinUserEntity.getUuid());
            }

            if (weixinUserEntity != null) {
                if (added) {
                    WeixinSyncLoggerHolder.addUser(weixinUserEntity);
                } else {
                    WeixinSyncLoggerHolder.updateUser(weixinUserEntity);
                }
            }
        }
    }

    private WeixinUserEntity saveWeixinUser(WeixinDepartmentVo.User user, UserInfoEntity userInfoEntity, OrgVersionEntity orgVersionEntity, WeixinConfigVo weixinConfigVo) {
        WeixinUserEntity weixinUserEntity = new WeixinUserEntity();
        this.updateWeixinUser(weixinUserEntity, user, userInfoEntity, orgVersionEntity, weixinConfigVo);
        return weixinUserEntity;
    }

    private void updateWeixinUser(WeixinUserEntity weixinUserEntity, WeixinDepartmentVo.User user, UserInfoEntity userInfoEntity,
                                  OrgVersionEntity orgVersionEntity, WeixinConfigVo weixinConfigVo) {
        weixinUserEntity.setConfigUuid(weixinConfigVo.getUuid());
        weixinUserEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        weixinUserEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        weixinUserEntity.setCorpId(weixinConfigVo.getCorpId());
        weixinUserEntity.setOaUserId(userInfoEntity.getUserId());
        weixinUserEntity.setUserId(user.getUserId());
        weixinUserEntity.setName(user.getName());
        weixinUserEntity.setDepartmentIds(StringUtils.join(user.getDepartment(), Separator.SEMICOLON.getValue()));
        weixinUserEntity.setPosition(user.getPosition());
        weixinUserEntity.setStatus(user.getStatus());
        weixinUserEntity.setEnable(user.getEnable());
        weixinUserEntity.setIsLeader(user.getIsleader());
        if (user.getExtAttr() != null) {
            weixinUserEntity.setExtAttr(JsonUtils.object2Json(user.getExtAttr()));
        }
        weixinUserEntity.setHideMobile(user.getHideMobile());
        weixinUserEntity.setTelephone(user.getTelephone());
        weixinUserEntity.setOrders(StringUtils.join(user.getOrder(), Separator.SEMICOLON.getValue()));
        weixinUserEntity.setMainDepartment(user.getMainDepartment());
        weixinUserEntity.setAlias(user.getAlias());
        weixinUserEntity.setIsLeaderInDepts(StringUtils.join(user.getIsLeaderInDept(), Separator.SEMICOLON.getValue()));
        weixinUserEntity.setDirectLeaders(StringUtils.join(user.getDirectLeader(), Separator.SEMICOLON.getValue()));
        if (user.getExternalProfile() != null) {
            weixinUserEntity.setExternalProfile(JsonUtils.object2Json(user.getExternalProfile()));
        }
        weixinUserService.save(weixinUserEntity);
    }

    private UserDto toUserDto(boolean added, WeixinDepartmentVo.User user, List<String> orgElementIds, OrgVersionEntity orgVersionEntity, WeixinConfigVo weixinConfigVo) {
        WeixinConfigVo.WeixinOrgSyncOption orgSyncOption = weixinConfigVo.getConfiguration().getOrgSyncOption();
        if (orgSyncOption == null) {
            orgSyncOption = new WeixinConfigVo.WeixinOrgSyncOption();
        }

        UserDto userDto = new UserDto();
        // 姓名
        userDto.setUserName(user.getName());
        if (added) {
            userDto.setLoginName(generateLoginName(user));
            userDto.setPassword("123456");
        }
//        // 头像
//        if (orgSyncOption.isUserAvatar()) {
//            String avatarFileId = getAvatarFileId(user);
//            userDto.setAvatar(avatarFileId);
//        }
//        // 性别
//        if (orgSyncOption.isUserGender()) {
//            if (Integer.valueOf(1).equals(user.getGender())) {
//                userDto.setGender(UserInfoEntity.Gender.MALE);
//            } else if (Integer.valueOf(2).equals(user.getGender())) {
//                userDto.setGender(UserInfoEntity.Gender.FEMALE);
//            }
//        }
//        // 手机号码
//        if (StringUtils.startsWith(user.getMobile(), "+86")) {
//            userDto.setCeilPhoneNumber(StringUtils.substring(user.getMobile(), 3));
//        } else {
//            userDto.setCeilPhoneNumber(user.getMobile());
//        }
        // 分机号
        if (orgSyncOption.isUserTelephone()) {
            List<UserInfoExtEntity> userInfoExtEntities = Lists.newArrayList();
            UserInfoExtEntity userInfoExtEntity = new UserInfoExtEntity();
            userInfoExtEntity.setAttrKey("businessPhoneNumber");
            userInfoExtEntity.setAttrValue(user.getTelephone());
            userInfoExtEntities.add(userInfoExtEntity);
            userDto.setUserInfoExts(userInfoExtEntities);
        }
//        // 邮箱
//        if (orgSyncOption.isUserEmail()) {
//            userDto.setMail(user.getEmail());
//        }
//        // 员工编号
//        if (orgSyncOption.isUserNo()) {
//            userDto.setUserNo(user.getJobNumber());
//        }
//        // 备注
//        if (orgSyncOption.isUserRemark()) {
//            userDto.setRemark(user.getRemark());
//        }
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
        // 直属领导
        List<String> directLeader = user.getDirectLeader();
        if (CollectionUtils.isNotEmpty(directLeader)) {
            String orgElementId = weixinDeptService.getOrgElementIdByIdAndConfigUuid(user.getMainDepartment(), weixinConfigVo.getUuid());
            if (orgElementIds.contains(orgElementId)) {
                List<String> directLeaderIds = weixinUserService.listOaUserIdByUserIdAndOrgVersionUuid(directLeader, orgVersionEntity.getUuid());
                orgReportTos.put(orgElementId, directLeaderIds);
            }
        }
        userDto.setOrgReportTos(orgReportTos);
        userDto.setOrgUsers(orgUserEntities);
        return userDto;
    }

    private String generateLoginName(WeixinDepartmentVo.User user) {
        String userName = StringUtils.replace(StringUtils.trim(user.getName()), " ", "");
        String loginName = StringUtils.lowerCase(user.getUserId());
        if (StringUtils.isBlank(loginName)) {
            loginName = PinyinUtil.getLoginName(userName);
        }
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

    private WeixinDeptEntity saveWeixinDept(WeixinDepartmentVo departmentVo, OrgElementEntity orgElementEntity, OrgVersionEntity orgVersionEntity, WeixinConfigVo weixinConfigVo) {
        WeixinDepartmentVo.Department department = departmentVo.getDepartment();
        WeixinDeptEntity weixinDeptEntity = new WeixinDeptEntity();
        weixinDeptEntity.setConfigUuid(weixinConfigVo.getUuid());
        weixinDeptEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        weixinDeptEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        weixinDeptEntity.setCorpId(weixinConfigVo.getCorpId());
        weixinDeptEntity.setOrgElementUuid(orgElementEntity.getUuid());
        weixinDeptEntity.setOrgElementId(orgElementEntity.getId());
        weixinDeptEntity.setName(department.getName());
        weixinDeptEntity.setId(department.getId());
        weixinDeptEntity.setParentId(department.getParentId());
        weixinDeptEntity.setSortOrder(department.getOrder());
        weixinDeptEntity.setDepartmentLeaders(StringUtils.join(department.getDepartmentLeaders(), Separator.SEMICOLON.getValue()));
        weixinDeptEntity.setStatus(WeixinDeptEntity.Status.NORMAL);
        weixinDeptService.save(weixinDeptEntity);
        return weixinDeptEntity;
    }

    private OrgElementDto toOrgElementDto(WeixinDepartmentVo departmentVo, OrgVersionEntity orgVersionEntity, Map<Long, Long> departmentMap, WeixinConfigVo weixinConfigVo) {
        WeixinDepartmentVo.Department department = departmentVo.getDepartment();
        String orgElementId = weixinDeptService.getOrgElementIdByIdAndConfigUuid(department.getId(), weixinConfigVo.getUuid());

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(department.getName());
        orgElementDto.setId(orgElementId);
//        if (NumberUtils.isNumber(department.getOrder())) {
//            orgElementDto.setSeq(NumberUtils.createNumber(department.getOrder()).intValue());
//        }
        orgElementDto.setCode(department.getOrder() + StringUtils.EMPTY);
        orgElementDto.setType(OrgElementModelEntity.ORG_DEPT_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(departmentMap.get(department.getParentId()));
        // setDepartmentI18nName(orgElementDto, department.getExt());
        if (StringUtils.isNotBlank(orgElementId)) {
            Long orgElementUuid = weixinDeptService.getOrgElementUuidByOrgElementId(orgElementId);
            if (orgElementUuid != null) {
                orgElementDto.setRoleUuids(orgFacadeService.listOrgElementRoleUuidByUuid(orgElementUuid));
            }
        }
        return orgElementDto;
    }

    @Override
    @Transactional
    public void createUser(WeixinDepartmentVo.User user, WeixinConfigVo weixinConfigVo) {
        updateUser(user, weixinConfigVo);
    }

    @Override
    @Transactional
    public void updateUser(WeixinDepartmentVo.User user, WeixinConfigVo weixinConfigVo) {
        WeixinConfigVo.WeixinOrgSyncOption orgSyncOption = weixinConfigVo.getConfiguration().getOrgSyncOption();
        Long orgUuid = Long.valueOf(weixinConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long orgVersionUuid = orgVersionEntity.getUuid();
        WeixinUserEntity weixinUserEntity = weixinUserService.getByUserIdAndOrgVersionUuid(user.getUserId(), orgVersionUuid);
        UserInfoEntity userInfoEntity = null;
        if (weixinUserEntity != null) {
            userInfoEntity = orgFacadeService.getUserInfoByUserId(weixinUserEntity.getOaUserId());
        }

        Set<String> orgElementIds = Sets.newLinkedHashSet();
        // 用户职位信息
        if (CollectionUtils.isNotEmpty(user.getDepartment())) {
//            if (orgSyncOption != null && orgSyncOption.isJob()) {
//                user.getDeptIdList().forEach(deptId -> {
//                    Long deptElementUuid = weixinDeptService.getOrgElementUuidByDeptIdAndOrgVersionUuid(deptId, orgVersionUuid);
//                    OrgElementEntity jobElementEntity = getOrCreateJobElement(user, deptId, deptElementUuid, orgVersionEntity, weixinConfigVo, false);
//                    if (jobElementEntity != null) {
//                        orgElementIds.add(jobElementEntity.getId());
//                    }
//                });
//
//                orgElementIds.addAll(weixinDeptService.listOrgElementIdByDeptIdsAndOrgVersionUuid(user.getDeptIdList(), orgVersionUuid));
//                List<String> jobDeptIds = getJobDeptIds(orgElementIds);
//                orgElementIds.removeAll(jobDeptIds);
//            } else {
            orgElementIds.addAll(weixinDeptService.listOrgElementIdByIdsAndOrgVersionUuid(user.getDepartment(), orgVersionUuid));
//            }
        }

        if (userInfoEntity != null) {
            UserDto userDto = toUserDto(false, user, Lists.newArrayList(orgElementIds), orgVersionEntity, weixinConfigVo);
            userDto.setUuid(userInfoEntity.getUuid());
            userDto.setUserId(userInfoEntity.getUserId());
            userDto.setLoginName(userInfoEntity.getLoginName());
            orgFacadeService.saveUser(userDto);
        } else {
            UserDto userDto = toUserDto(true, user, Lists.newArrayList(orgElementIds), orgVersionEntity, weixinConfigVo);
            String userInfoUuid = orgFacadeService.saveUser(userDto);
            userInfoEntity = orgFacadeService.getUserInfoByUuid(userInfoUuid);
        }
        weixinUserService.flushSession();
        weixinUserService.clearSession();

        // 更新部门负责人
        updateUserDeptDirectorLeader(weixinUserEntity, user, userInfoEntity.getUserId(), orgVersionUuid);

        if (weixinUserEntity != null) {
            updateWeixinUser(weixinUserEntity, user, userInfoEntity, orgVersionEntity, weixinConfigVo);
            WeixinEventHoler.success("updatedWeixinUser", weixinUserEntity);
        } else {
            weixinUserEntity = saveWeixinUser(user, userInfoEntity, orgVersionEntity, weixinConfigVo);
            WeixinEventHoler.success("createdWeixinUser", weixinUserEntity);
        }
    }

    private void updateUserDeptDirectorLeader(WeixinUserEntity weixinUserEntity, WeixinDepartmentVo.User user, String oaUserId, Long orgVersionUuid) {
        // 更新前的部门
        List<String> existsOrgElementIds = Lists.newArrayList();
        if (weixinUserEntity != null && StringUtils.isNotBlank(weixinUserEntity.getDepartmentIds())) {
            existsOrgElementIds.addAll(Arrays.asList(weixinUserEntity.getDepartmentIds().split(Separator.SEMICOLON.getValue())));
        }
        existsOrgElementIds.forEach(existsOrgElementId -> {
            if (!user.getDepartment().contains(Long.valueOf(existsOrgElementId))) {
                Long orgElementUuid = weixinDeptService.getOrgElementUuidByIdAndOrgVersionUuid(Long.valueOf(existsOrgElementId), orgVersionUuid);
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
        if (CollectionUtils.isNotEmpty(user.getIsLeaderInDept())) {
            for (int index = 0; index < user.getIsLeaderInDept().size(); index++) {
                Long leaderDeptId = null;
                Integer leaderInDept = user.getIsLeaderInDept().get(index);
                if (Integer.valueOf(1).equals(leaderInDept)) {
                    leaderDeptId = user.getDepartment().get(index);
                }
                Long orgElementUuid = weixinDeptService.getOrgElementUuidByIdAndOrgVersionUuid(user.getDepartment().get(index), orgVersionUuid);
                if (orgElementUuid != null) {
                    Set<String> directors = Sets.newLinkedHashSet();
                    OrgElementManagementEntity orgElementManagement = orgFacadeService.getOrgElementManagementByUuid(orgElementUuid);
                    if (orgElementManagement == null) {
                        if (leaderDeptId != null) {
                            orgFacadeService.saveOrgElementLeader(orgElementUuid, oaUserId, null, null);
                        }
                    } else {
                        if (StringUtils.isNotBlank(orgElementManagement.getDirector())) {
                            directors.addAll(Arrays.asList(orgElementManagement.getDirector().split(Separator.SEMICOLON.getValue())));
                        }
                        if (leaderDeptId != null) {
                            directors.add(oaUserId);
                        } else {
                            directors.remove(oaUserId);
                        }
                        orgFacadeService.saveOrgElementLeader(orgElementUuid, StringUtils.join(directors, Separator.SEMICOLON.getValue()), null, null);
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void deleteUser(String userId, WeixinConfigVo weixinConfigVo) {
        Long orgUuid = Long.valueOf(weixinConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        WeixinUserEntity weixinUserEntity = weixinUserService.getByUserIdAndOrgVersionUuid(userId, orgVersionEntity.getUuid());
        if (weixinUserEntity == null) {
            String errorMsg = String.format("组织版本[%d]不同步，用户不存在，无法删除用户信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            WeixinEventHoler.error(errorMsg);
            return;
        }

        orgFacadeService.removeOrgUser(Lists.newArrayList(weixinUserEntity.getOaUserId()), orgVersionEntity.getUuid());

        // 更新微信用户状态为退出企业
        weixinUserEntity.setStatus(5);
        weixinUserService.save(weixinUserEntity);
        WeixinEventHoler.success("deletedWeixinUser", weixinUserEntity);
    }

    @Override
    @Transactional
    public void createDepartment(WeixinDepartmentVo.Department department, WeixinConfigVo weixinConfigVo) {
        Long parentId = department.getParentId();
        Long orgUuid = Long.valueOf(weixinConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long parentOrgElementUuid = null;
        // 非根结点下添加部门
        if (!Long.valueOf(1).equals(parentId)) {
            parentOrgElementUuid = weixinDeptService.getOrgElementUuidByIdAndOrgVersionUuid(parentId, orgVersionEntity.getUuid());
            if (parentOrgElementUuid == null) {
                String errorMsg = String.format("组织版本[%d]不同步，上级部门不存在，无法创建部门信息", orgVersionEntity.getUuid());
                logger.error(errorMsg);
                WeixinEventHoler.error(errorMsg);
                return;
            }
        }

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(department.getName());
        orgElementDto.setCode(department.getOrder() + StringUtils.EMPTY);
        orgElementDto.setType(OrgElementModelEntity.ORG_DEPT_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(parentOrgElementUuid);
        // setDepartmentI18nName(orgElementDto, deptGetResponse.getExtention());
        List<String> leaderUserIds = department.getDepartmentLeaders();
        if (CollectionUtils.isNotEmpty(leaderUserIds)) {
            leaderUserIds = weixinUserService.listOaUserIdByUserIdAndOrgVersionUuid(leaderUserIds, orgVersionEntity.getUuid());
            if (CollectionUtils.isNotEmpty(leaderUserIds)) {
                OrgElementManagementEntity management = new OrgElementManagementEntity();
                management.setDirector(StringUtils.join(Sets.newLinkedHashSet(leaderUserIds), Separator.SEMICOLON.getValue()));
                orgElementDto.setManagement(management);
            }
        }
        Long orgElementUuid = orgFacadeService.saveOrgElementDetails(orgElementDto);

        // 同步微信部门信息
        OrgElementEntity orgElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
        WeixinDepartmentVo departmentVo = new WeixinDepartmentVo();
        departmentVo.setDepartment(department);
        WeixinDeptEntity weixinDeptEntity = saveWeixinDept(departmentVo, orgElementEntity, orgVersionEntity, weixinConfigVo);

        WeixinEventHoler.success("createdWeixinDept", weixinDeptEntity);
    }

    @Override
    @Transactional
    public void updateDepartment(WeixinDepartmentVo.Department department, WeixinConfigVo weixinConfigVo) {
        Long parentId = department.getParentId();
        Long deptId = department.getId();
        Long orgUuid = Long.valueOf(weixinConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long orgElementUuid = weixinDeptService.getOrgElementUuidByIdAndOrgVersionUuid(deptId, orgVersionEntity.getUuid());
        Long parentOrgElementUuid = null;
        //if (!Long.valueOf(1).equals(parentId)) {
        parentOrgElementUuid = weixinDeptService.getOrgElementUuidByIdAndOrgVersionUuid(parentId, orgVersionEntity.getUuid());
        if (parentOrgElementUuid == null || orgElementUuid == null) {
            String errorMsg = String.format("组织版本[%d]不同步，无法更新部门信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            WeixinEventHoler.error(errorMsg);
            return;
        }
//        } else if (orgElementUuid == null) {
//            String errorMsg = String.format("组织版本[%d]不同步，无法更新部门信息", orgVersionEntity.getUuid());
//            logger.error(errorMsg);
//            WeixinEventHoler.error(errorMsg);
//            return;
//        }

        OrgElementDto orgElementDto = orgFacadeService.getOrgElementDetailsByUuid(orgElementUuid);
        if (orgElementDto == null) {
            String errorMsg = String.format("组织版本[%d]中不存在部门[%s]，无法更新部门信息", orgVersionEntity.getUuid(), department.getName());
            logger.error(errorMsg);
            WeixinEventHoler.error(errorMsg);
            return;
        }

        orgElementDto.setName(department.getName());
        orgElementDto.setParentUuid(parentOrgElementUuid);

        List<String> leaderUserIds = department.getDepartmentLeaders();
        OrgElementManagementEntity management = orgElementDto.getManagement();
        if (CollectionUtils.isNotEmpty(leaderUserIds)) {
            leaderUserIds = weixinUserService.listOaUserIdByUserIdAndOrgVersionUuid(leaderUserIds, orgVersionEntity.getUuid());
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
        orgFacadeService.saveOrgElementDetails(orgElementDto);

        // 更新微信部门信息
        WeixinDeptEntity weixinDeptEntity = weixinDeptService.getByIdAndOrgVersionUuid(deptId, orgVersionEntity.getUuid());
        updateWeixinDept(department, weixinDeptEntity, orgElementDto, orgVersionEntity, weixinConfigVo);

        WeixinEventHoler.success("updatedWeixinDept", weixinDeptEntity);
    }

    private void updateWeixinDept(WeixinDepartmentVo.Department department, WeixinDeptEntity weixinDeptEntity, OrgElementDto orgElementDto, OrgVersionEntity orgVersionEntity, WeixinConfigVo weixinConfigVo) {
        weixinDeptEntity.setConfigUuid(weixinConfigVo.getUuid());
        weixinDeptEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        weixinDeptEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        weixinDeptEntity.setCorpId(weixinConfigVo.getCorpId());
        weixinDeptEntity.setOrgElementUuid(orgElementDto.getUuid());
        weixinDeptEntity.setOrgElementId(orgElementDto.getId());
        weixinDeptEntity.setName(department.getName());
        weixinDeptEntity.setId(department.getId());
        weixinDeptEntity.setParentId(department.getParentId());
        weixinDeptEntity.setSortOrder(department.getOrder());
        weixinDeptEntity.setDepartmentLeaders(StringUtils.join(department.getDepartmentLeaders(), Separator.SEMICOLON.getValue()));
        weixinDeptEntity.setStatus(WeixinDeptEntity.Status.NORMAL);
        weixinDeptService.save(weixinDeptEntity);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long deptId, WeixinConfigVo weixinConfigVo) {
        Long orgUuid = Long.valueOf(weixinConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        WeixinDeptEntity weixinDeptEntity = weixinDeptService.getByIdAndOrgVersionUuid(deptId, orgVersionEntity.getUuid());
        if (weixinDeptEntity == null || WeixinDeptEntity.Status.DELETED.equals(weixinDeptEntity.getStatus())) {
            String errorMsg = String.format("组织版本[%d]不同步，部门不存在，无法删除部门信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            WeixinEventHoler.error(errorMsg);
            return;
        }

        orgFacadeService.deleteOrgElementByUuid(weixinDeptEntity.getOrgElementUuid());

        // 更新微信部门状态为删除
        weixinDeptEntity.setStatus(WeixinDeptEntity.Status.DELETED);
        weixinDeptService.save(weixinDeptEntity);

        WeixinEventHoler.success("deletedWeixinDept", weixinDeptEntity);
    }

}
