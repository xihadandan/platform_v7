/*
 * @(#)3/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.lark.oapi.service.contact.v3.model.Department;
import com.lark.oapi.service.contact.v3.model.User;
import com.lark.oapi.service.contact.v3.model.*;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.http.HttpUtil;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.app.feishu.entity.FeishuDeptEntity;
import com.wellsoft.pt.app.feishu.entity.FeishuUserEntity;
import com.wellsoft.pt.app.feishu.facade.service.FeishuOrgSyncFacadeService;
import com.wellsoft.pt.app.feishu.model.DepartmentNode;
import com.wellsoft.pt.app.feishu.service.FeishuDeptService;
import com.wellsoft.pt.app.feishu.service.FeishuUserService;
import com.wellsoft.pt.app.feishu.support.FeishuEventHoler;
import com.wellsoft.pt.app.feishu.support.FeishuSyncLoggerHolder;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.dto.OrgElementDto;
import com.wellsoft.pt.org.dto.OrgUserDto;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
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
 * 3/20/25.1	    zhulh		3/20/25		    Create
 * </pre>
 * @date 3/20/25
 */
@Service
public class FeishuOrgSyncFacadeServiceImpl implements FeishuOrgSyncFacadeService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private FeishuDeptService feishuDeptService;

    @Autowired
    private FeishuUserService feishuUserService;

    @Autowired
    private MongoFileService mongoFileService;

    @Override
    @Transactional
    public void syncOrg(List<DepartmentNode> departmentNodes, FeishuConfigVo feishuConfigVo) {
        FeishuConfigVo.FeishuConfiguration configuration = feishuConfigVo.getConfiguration();
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(Long.valueOf(configuration.getOrgUuid()));
        FeishuSyncLoggerHolder.orgVersion(orgVersionEntity);
        long count = orgFacadeService.countOrgElementByOrgVersionUuid(orgVersionEntity.getUuid());
        Map<String, Long> departmentMap = new HashMap<>();
        Map<String, Long> userMap = new HashMap<>();
        // 存在组织元素，升级新版本
        if (count > 0) {
            orgVersionEntity = orgFacadeService.createEmptyOrgVersionFromOrgVersion(orgVersionEntity, false, true);
        }
        saveDepartmentNodes(departmentNodes, orgVersionEntity, null, departmentMap, userMap, feishuConfigVo);

        // 设置部门负责人
        saveDeptmentDirector(orgVersionEntity);
    }

    @Override
    @Transactional
    public void createDepartment(Department department, FeishuConfigVo feishuConfigVo) {
        String openParentDepartmentId = department.getParentDepartmentId();
        Long orgUuid = Long.valueOf(feishuConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long parentOrgElementUuid = null;
        // 非根结点下添加部门
        if (!StringUtils.equals("0", openParentDepartmentId)) {
            parentOrgElementUuid = feishuDeptService.getOrgElementUuidByOpenDepartmentIdAndOrgVersionUuid(openParentDepartmentId, orgVersionEntity.getUuid());
            if (parentOrgElementUuid == null) {
                String errorMsg = String.format("组织版本[%d]不同步，上级部门不存在，无法创建部门信息", orgVersionEntity.getUuid());
                logger.error(errorMsg);
                FeishuEventHoler.error(errorMsg);
                return;
            }
        }

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(department.getName());
        //  orgElementDto.setId(orgElementId);
        // orgElementDto.setSeq(departmentEvent.getOrder());
        orgElementDto.setCode(department.getOrder() + StringUtils.EMPTY);
        orgElementDto.setType(OrgElementModelEntity.ORG_DEPT_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(parentOrgElementUuid);
        setDepartmentI18nName(orgElementDto, department);
        DepartmentLeader[] departmentLeaders = department.getLeaders();
        if (departmentLeaders != null && departmentLeaders.length > 0) {
            List<String> openLeaderUserIds = Arrays.stream(departmentLeaders).map(DepartmentLeader::getLeaderID).collect(Collectors.toList());
            List<String> leaderUserIds = feishuUserService.listOaUserIdByOpenIdAndOrgVersionUuid(openLeaderUserIds, orgVersionEntity.getUuid());
            if (CollectionUtils.isNotEmpty(leaderUserIds)) {
                OrgElementManagementEntity management = new OrgElementManagementEntity();
                management.setDirector(StringUtils.join(leaderUserIds, Separator.SEMICOLON.getValue()));
                orgElementDto.setManagement(management);
            }
        }
        Long orgElementUuid = orgFacadeService.saveOrgElementDetails(orgElementDto);

        // 同步飞书部门信息
        OrgElementEntity orgElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
        DepartmentNode departmentNode = new DepartmentNode();
        departmentNode.setDepartment(department);
        FeishuDeptEntity feishuDeptEntity = saveFeishuDept(departmentNode, orgElementEntity, orgVersionEntity, feishuConfigVo);

        FeishuEventHoler.success("createdFeishuDept", feishuDeptEntity);
    }

    @Override
    @Transactional
    public void deleteDepartment(DepartmentEvent departmentEvent, FeishuConfigVo feishuConfigVo) {
        // DepartmentEvent departmentEvent = p2DepartmentDeletedV3Data.getObject();
        String openDepartmentId = departmentEvent.getOpenDepartmentId();
        Long orgUuid = Long.valueOf(feishuConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        FeishuDeptEntity feishuDeptEntity = feishuDeptService.getByOpenDepartmentIdAndOrgVersionUuid(openDepartmentId, orgVersionEntity.getUuid());
        if (feishuDeptEntity == null) {
            String errorMsg = String.format("组织版本[%d]不同步，部门不存在，无法删除部门信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            FeishuEventHoler.error(errorMsg);
            return;
        }

        orgFacadeService.deleteOrgElementByUuid(feishuDeptEntity.getOrgElementUuid());

        // 更新飞书部门状态为删除
        // feishuDeptEntity = feishuDeptService.getByOpenDepartmentIdAndOrgVersionUuid(openDepartmentId, orgVersionEntity.getUuid());
        feishuDeptEntity.setStatus("1");
        feishuDeptService.save(feishuDeptEntity);

        FeishuEventHoler.success("deletedFeishuDept", feishuDeptEntity);
    }

    @Override
    @Transactional
    public void updateDepartment(P2DepartmentUpdatedV3Data p2DepartmentUpdatedV3Data, FeishuConfigVo feishuConfigVo) {
        DepartmentEvent departmentEvent = p2DepartmentUpdatedV3Data.getObject();
        String openParentDepartmentId = departmentEvent.getParentDepartmentId();
        String openDepartmentId = departmentEvent.getOpenDepartmentId();
        Long orgUuid = Long.valueOf(feishuConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        Long orgElementUuid = feishuDeptService.getOrgElementUuidByOpenDepartmentIdAndOrgVersionUuid(openDepartmentId, orgVersionEntity.getUuid());
        Long parentOrgElementUuid = null;
        if (!StringUtils.equals("0", openParentDepartmentId)) {
            parentOrgElementUuid = feishuDeptService.getOrgElementUuidByOpenDepartmentIdAndOrgVersionUuid(openParentDepartmentId, orgVersionEntity.getUuid());
            if (parentOrgElementUuid == null || orgElementUuid == null) {
                String errorMsg = String.format("组织版本[%d]不同步，无法更新部门信息", orgVersionEntity.getUuid());
                logger.error(errorMsg);
                FeishuEventHoler.error(errorMsg);
                return;
            }
        } else if (orgElementUuid == null) {
            String errorMsg = String.format("组织版本[%d]不同步，无法更新部门信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            FeishuEventHoler.error(errorMsg);
            return;
        }

        OrgElementDto orgElementDto = orgFacadeService.getOrgElementDetailsByUuid(orgElementUuid);
        if (orgElementDto == null) {
            String errorMsg = String.format("组织版本[%d]中不存在部门[%s]，无法更新部门信息", orgVersionEntity.getUuid(), departmentEvent.getName());
            logger.error(errorMsg);
            FeishuEventHoler.error(errorMsg);
            return;
        }

        orgElementDto.setName(departmentEvent.getName());
        // orgElementDto.setSeq(departmentEvent.getOrder());
        orgElementDto.setParentUuid(parentOrgElementUuid);

        DepartmentLeader[] departmentLeaders = departmentEvent.getLeaders();
        OrgElementManagementEntity management = orgElementDto.getManagement();
        if (departmentLeaders != null && departmentLeaders.length > 0) {
            List<String> openLeaderUserIds = Arrays.stream(departmentLeaders).map(DepartmentLeader::getLeaderID).collect(Collectors.toList());
            List<String> leaderUserIds = feishuUserService.listOaUserIdByOpenIdAndOrgVersionUuid(openLeaderUserIds, orgVersionEntity.getUuid());
            if (CollectionUtils.isNotEmpty(leaderUserIds)) {
                if (management != null) {
                    management.setDirector(StringUtils.join(leaderUserIds, Separator.SEMICOLON.getValue()));
                } else {
                    management = new OrgElementManagementEntity();
                    management.setDirector(StringUtils.join(leaderUserIds, Separator.SEMICOLON.getValue()));
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

        // 更新飞书部门信息
        FeishuDeptEntity feishuDeptEntity = feishuDeptService.getByOpenDepartmentIdAndOrgVersionUuid(openDepartmentId, orgVersionEntity.getUuid());
        updateFeishuDept(departmentEvent, feishuDeptEntity, orgElementDto, orgVersionEntity, feishuConfigVo);

        FeishuEventHoler.success("updatedFeishuDept", feishuDeptEntity);
    }

    @Override
    @Transactional
    public void createUser(UserEvent userEvent, FeishuConfigVo feishuConfigVo) {
        updateUser(userEvent, feishuConfigVo);
    }

    @Override
    @Transactional
    public void deleteUser(UserEvent userEvent, FeishuConfigVo feishuConfigVo) {
        String openId = userEvent.getOpenId();
        Long orgUuid = Long.valueOf(feishuConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        FeishuUserEntity feishuUserEntity = feishuUserService.getByOpenIdAndOrgVersionUuid(openId, orgVersionEntity.getUuid());
        if (feishuUserEntity == null || StringUtils.equals("3", feishuUserEntity.getStatus())) {
            String errorMsg = String.format("组织版本[%d]不同步，用户不存在，无法删除用户信息", orgVersionEntity.getUuid());
            logger.error(errorMsg);
            FeishuEventHoler.error(errorMsg);
            return;
        }

        orgFacadeService.removeOrgUser(Lists.newArrayList(feishuUserEntity.getOaUserId()), orgVersionEntity.getUuid());

        // 更新飞书用户状态为删除
        feishuUserEntity.setStatus("3");
        feishuUserService.save(feishuUserEntity);
        FeishuEventHoler.success("deletedFeishuUser", feishuUserEntity);
    }

    @Override
    @Transactional
    public void updateUser(UserEvent userEvent, FeishuConfigVo feishuConfigVo) {
        Long orgUuid = Long.valueOf(feishuConfigVo.getConfiguration().getOrgUuid());
        OrgVersionEntity orgVersionEntity = orgFacadeService.getOrgVersionByOrgUuid(orgUuid);
        String[] departments = userEvent.getDepartmentIds();
        List<String> orgElementIds = feishuDeptService.listOrgElementIdByOpenDepartmentIdsAndOrgVersionUuid(Lists.newArrayList(departments), orgVersionEntity.getUuid());
        User user = userEvent2User(userEvent);
        String mobile = userEvent.getMobile();
        FeishuUserEntity feishuUserEntity = feishuUserService.getByOpenIdAndOrgVersionUuid(userEvent.getOpenId(), orgVersionEntity.getUuid());
        UserInfoEntity userInfoEntity = null;
        if (feishuUserEntity != null) {
            userInfoEntity = orgFacadeService.getUserInfoByUserId(feishuUserEntity.getOaUserId());
        } else {
            userInfoEntity = orgFacadeService.getUserInfoByMobile(mobile);
        }
        if (userInfoEntity != null) {
            String userId = userInfoEntity.getUserId();
            toUserInfo(user, userInfoEntity, feishuConfigVo);
            orgFacadeService.saveUserInfo(userInfoEntity);
            List<OrgUserDto> orgUserDtos = orgFacadeService.listOrgUser(userId, new String[]{orgVersionEntity.getId()});
            List<OrgUserDto> toRemoveOrgUserDtos = orgUserDtos.stream()
                    .filter(orgUserDto -> StringUtils.isNotBlank(orgUserDto.getOrgElementId()) && !orgElementIds.contains(orgUserDto.getOrgElementId()))
                    .collect(Collectors.toList());
            toRemoveOrgUserDtos.forEach(toRemoveOrgUserDto -> {
                orgFacadeService.removeOrgUser(Lists.newArrayList(userId), toRemoveOrgUserDto.getOrgElementId(), orgVersionEntity.getUuid());
            });
            orgElementIds.forEach(orgElementId -> {
                orgFacadeService.joinOrgUser(Lists.newArrayList(userId), null, Collections.emptyList(),
                        orgElementId, orgVersionEntity.getUuid());
            });
        } else {
            UserDto userDto = toUserDto(user, orgElementIds, orgVersionEntity, feishuConfigVo);
            String userInfoUuid = orgFacadeService.saveUser(userDto);
            userInfoEntity = orgFacadeService.getUserInfoByUuid(userInfoUuid);
        }

        if (feishuUserEntity != null) {
            updateFeishuUser(feishuUserEntity, user, userInfoEntity, orgVersionEntity, feishuConfigVo);
            FeishuEventHoler.success("updatedFeishuUser", feishuUserEntity);
        } else {
            feishuUserEntity = saveFeishuUser(user, userInfoEntity, orgVersionEntity, feishuConfigVo);
            FeishuEventHoler.success("createdFeishuUser", feishuUserEntity);
        }
    }

    @Override
    public boolean isDepartmentDeleted(String openDepartmentId) {
        FeishuDeptEntity feishuDeptEntity = feishuDeptService.getByOpenDepartmentId(openDepartmentId);
        return feishuDeptEntity == null || StringUtils.equals("1", feishuDeptEntity.getStatus());
    }

    private void saveDeptmentDirector(OrgVersionEntity orgVersionEntity) {
        List<FeishuDeptEntity> feishuDeptEntities = feishuDeptService.listHasLeaderUserIdByOrgVersionUuid(orgVersionEntity.getUuid());
        if (CollectionUtils.isEmpty(feishuDeptEntities)) {
            return;
        }
        Set<String> leaderOpenIds = feishuDeptEntities.stream().flatMap(feishuDeptEntity -> {
            String leaders = feishuDeptEntity.getLeaders();
            if (StringUtils.isBlank(leaders)) {
                return Stream.empty();
            }
            return Stream.of(StringUtils.split(leaders, Separator.SEMICOLON.getValue()));
        }).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(leaderOpenIds)) {
            return;
        }
        List<FeishuUserEntity> feishuUserEntities = feishuUserService.listByOpenIdsAndOrgVersionUuid(leaderOpenIds, orgVersionEntity.getUuid());
        Map<String, String> leaderMap = Maps.newHashMap();// feishuUserEntities.stream().collect(Collectors.toMap(FeishuUserEntity::getOpenId, FeishuUserEntity::getOaUserId));
        feishuUserEntities.forEach(feishuUserEntity -> leaderMap.put(feishuUserEntity.getOpenId(), feishuUserEntity.getOaUserId()));
        feishuDeptEntities.forEach(feishuDeptEntity -> {
            String leaders = feishuDeptEntity.getLeaders();
            if (StringUtils.isBlank(leaders)) {
                return;
            }
            List<String> directorLeaders = Arrays.asList(StringUtils.split(leaders, Separator.SEMICOLON.getValue()));
            List<String> directors = Lists.newArrayList();
            directorLeaders.forEach(leader -> {
                String oaUserId = leaderMap.get(leader);
                if (StringUtils.isNotBlank(oaUserId)) {
                    directors.add(oaUserId);
                }
            });
            if (CollectionUtils.isEmpty(directors)) {
                return;
            }
            orgFacadeService.saveOrgElementLeader(feishuDeptEntity.getOrgElementUuid(), StringUtils.join(directors, Separator.SEMICOLON.getValue()), null, null);
        });
    }

    private void saveDepartmentNodes(List<DepartmentNode> departmentNodes, OrgVersionEntity orgVersionEntity,
                                     DepartmentNode parentDepartmentNode, Map<String, Long> departmentMap,
                                     Map<String, Long> userMap, FeishuConfigVo feishuConfigVo) {
        for (DepartmentNode departmentNode : departmentNodes) {
            OrgElementDto orgElementDto = toOrgElementDto(departmentNode, orgVersionEntity, departmentMap);
            boolean added = StringUtils.isBlank(orgElementDto.getId());
            Long orgElementUuid = orgFacadeService.saveOrgElementDetails(orgElementDto);
            OrgElementEntity orgElementEntity = orgFacadeService.getOrgElementByUuid(orgElementUuid);
            FeishuDeptEntity feishuDeptEntity = saveFeishuDept(departmentNode, orgElementEntity, orgVersionEntity, feishuConfigVo);
            if (added) {
                FeishuSyncLoggerHolder.addDept(feishuDeptEntity);
            } else {
                FeishuSyncLoggerHolder.updateDept(feishuDeptEntity);
            }

            departmentMap.put(departmentNode.getDepartment().getOpenDepartmentId(), orgElementUuid);
            List<User> users = departmentNode.getUsers();
            if (CollectionUtils.isNotEmpty(users)) {
                saveDeptUsers(users, orgElementEntity, departmentNode, orgVersionEntity, userMap, feishuConfigVo);
            }

            if (CollectionUtils.isNotEmpty(departmentNode.getChildren())) {
                saveDepartmentNodes(departmentNode.getChildren(), orgVersionEntity, departmentNode, departmentMap, userMap, feishuConfigVo);
            }
            feishuDeptService.flushSession();
            feishuDeptService.clearSession();
        }
    }

    private FeishuDeptEntity saveFeishuDept(DepartmentNode departmentNode, OrgElementEntity orgElementEntity, OrgVersionEntity orgVersionEntity,
                                            FeishuConfigVo feishuConfigVo) {
        Department department = departmentNode.getDepartment();
        FeishuDeptEntity feishuDeptEntity = new FeishuDeptEntity();
        feishuDeptEntity.setConfigUuid(feishuConfigVo.getUuid());
        feishuDeptEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        feishuDeptEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        feishuDeptEntity.setAppId(feishuConfigVo.getAppId());
        feishuDeptEntity.setOrgElementUuid(orgElementEntity.getUuid());
        feishuDeptEntity.setOrgElementId(orgElementEntity.getId());
        feishuDeptEntity.setName(department.getName());
        feishuDeptEntity.setParentDepartmentId(department.getParentDepartmentId());
        feishuDeptEntity.setDepartmentId(department.getDepartmentId());
        feishuDeptEntity.setOpenDepartmentId(department.getOpenDepartmentId());
        feishuDeptEntity.setLeaderUserId(department.getLeaderUserId());
        feishuDeptEntity.setDepartmentOrder(department.getOrder());
        feishuDeptEntity.setStatus((department.getStatus() != null && department.getStatus().getIsDeleted()) ? "1" : "0");
        if (department.getLeaders() != null) {
            feishuDeptEntity.setLeaders(Arrays.stream(department.getLeaders()).map(leader -> leader.getLeaderID()).collect(Collectors.joining(";")));
        }
        feishuDeptService.save(feishuDeptEntity);
        return feishuDeptEntity;
    }

    private void updateFeishuDept(DepartmentEvent departmentEvent, FeishuDeptEntity feishuDeptEntity, OrgElementDto orgElementDto,
                                  OrgVersionEntity orgVersionEntity, FeishuConfigVo feishuConfigVo) {
        feishuDeptEntity.setConfigUuid(feishuConfigVo.getUuid());
        feishuDeptEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        feishuDeptEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        feishuDeptEntity.setAppId(feishuConfigVo.getAppId());
        feishuDeptEntity.setOrgElementUuid(orgElementDto.getUuid());
        feishuDeptEntity.setOrgElementId(orgElementDto.getId());
        feishuDeptEntity.setName(departmentEvent.getName());
        feishuDeptEntity.setParentDepartmentId(departmentEvent.getParentDepartmentId());
        feishuDeptEntity.setDepartmentId(departmentEvent.getDepartmentId());
        feishuDeptEntity.setOpenDepartmentId(departmentEvent.getOpenDepartmentId());
        feishuDeptEntity.setLeaderUserId(departmentEvent.getLeaderUserId());
        feishuDeptEntity.setDepartmentOrder(departmentEvent.getOrder() + StringUtils.EMPTY);
        feishuDeptEntity.setStatus((departmentEvent.getStatus() != null && departmentEvent.getStatus().getIsDeleted()) ? "1" : "0");
        if (departmentEvent.getLeaders() != null) {
            feishuDeptEntity.setLeaders(Arrays.stream(departmentEvent.getLeaders()).map(leader -> leader.getLeaderID()).collect(Collectors.joining(";")));
        }
        feishuDeptService.save(feishuDeptEntity);
    }

    private void updateFeishuUser(FeishuUserEntity feishuUserEntity, User user, UserInfoEntity userInfoEntity,
                                  OrgVersionEntity orgVersionEntity, FeishuConfigVo feishuConfigVo) {
        feishuUserEntity.setConfigUuid(feishuConfigVo.getUuid());
        feishuUserEntity.setOrgUuid(orgVersionEntity.getOrgUuid());
        feishuUserEntity.setOrgVersionUuid(orgVersionEntity.getUuid());
        feishuUserEntity.setAppId(feishuConfigVo.getAppId());
        feishuUserEntity.setOaUserId(userInfoEntity.getUserId());
        feishuUserEntity.setUnionId(user.getUnionId());
        feishuUserEntity.setUserId(user.getUserId());
        feishuUserEntity.setOpenId(user.getOpenId());
        feishuUserEntity.setName(user.getName());
        feishuUserEntity.setEnName(user.getEnName());
        feishuUserEntity.setNickname(user.getNickname());
        feishuUserEntity.setEmail(user.getEmail());
        feishuUserEntity.setMobile(user.getMobile());
        feishuUserEntity.setMobileVisible(user.getMobileVisible());
        feishuUserEntity.setGender(user.getGender());
        if (user.getAvatar() != null) {
            feishuUserEntity.setAvatar(user.getAvatar().getAvatarOrigin());
        }
        if (user.getStatus() != null) {
            feishuUserEntity.setStatus(user.getStatus().getIsActivated() ? "1" : "0");
        }
        feishuUserEntity.setDepartmentIds(StringUtils.join(user.getDepartmentIds(), ";"));
        feishuUserEntity.setLeaderUserId(user.getLeaderUserId());
        if (user.getJoinTime() != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(user.getJoinTime());
            feishuUserEntity.setJoinTime(calendar.getTime());
        }
        feishuUserEntity.setEmployeeNo(user.getEmployeeNo());
        feishuUserEntity.setJobTitle(user.getJobTitle());
        if (user.getDepartmentPath() != null) {
            String departmentPath = Arrays.stream(user.getDepartmentPath()).map(path -> path.getDepartmentPath().getDepartmentPathName().getName()).collect(Collectors.joining(";"));
            feishuUserEntity.setDepartmentPath(departmentPath);
        }
        feishuUserService.save(feishuUserEntity);
    }

    private FeishuUserEntity saveFeishuUser(User user, UserInfoEntity userInfoEntity, OrgVersionEntity orgVersionEntity, FeishuConfigVo feishuConfigVo) {
        FeishuUserEntity feishuUserEntity = new FeishuUserEntity();
        this.updateFeishuUser(feishuUserEntity, user, userInfoEntity, orgVersionEntity, feishuConfigVo);
        return feishuUserEntity;
    }

    private void saveDeptUsers(List<User> users, OrgElementEntity orgElementEntity, DepartmentNode departmentNode,
                               OrgVersionEntity orgVersionEntity, Map<String, Long> userMap, FeishuConfigVo feishuConfigVo) {
        Long orgVersionUuid = orgVersionEntity.getUuid();
        for (User user : users) {
            String mobile = user.getMobile();
            UserInfoEntity userInfoEntity = orgFacadeService.getUserInfoByMobile(mobile);
            boolean added = false;
            if (userInfoEntity != null) {
                if (!userMap.containsKey(user.getOpenId())) {
                    toUserInfo(user, userInfoEntity, feishuConfigVo);
                    orgFacadeService.saveUserInfo(userInfoEntity);
                }
                orgFacadeService.joinOrgUser(Lists.newArrayList(userInfoEntity.getUserId()), null, Collections.emptyList(),
                        orgElementEntity.getId(), orgVersionUuid);
            } else {
                added = true;
                UserDto userDto = toUserDto(user, Lists.newArrayList(orgElementEntity.getId()), orgVersionEntity, feishuConfigVo);
                String userInfoUuid = orgFacadeService.saveUser(userDto);
                userInfoEntity = orgFacadeService.getUserInfoByUuid(userInfoUuid);
            }
            if (!userMap.containsKey(user.getOpenId())) {
                FeishuUserEntity feishuUserEntity = saveFeishuUser(user, userInfoEntity, orgVersionEntity, feishuConfigVo);
                if (added) {
                    FeishuSyncLoggerHolder.addUser(feishuUserEntity);
                } else {
                    FeishuSyncLoggerHolder.updateUser(feishuUserEntity);
                }
                userMap.put(user.getOpenId(), feishuUserEntity.getUuid());
            }
        }
    }

    private void toUserInfo(User user, UserInfoEntity userInfoEntity, FeishuConfigVo feishuConfigVo) {
        FeishuConfigVo.FeishuOrgSyncOption orgSyncOption = feishuConfigVo.getConfiguration().getOrgSyncOption();
        if (orgSyncOption == null) {
            orgSyncOption = new FeishuConfigVo.FeishuOrgSyncOption();
        }

        userInfoEntity.setUserName(user.getName());

        // 头像
        if (orgSyncOption.isUserAvatar()) {
            String avatarFileId = getAvatarFileId(user);
            userInfoEntity.setAvatar(avatarFileId);
        }
        // 性别
        if (orgSyncOption.isUserGender()) {
            if (Integer.valueOf(1).equals(user.getGender())) {
                userInfoEntity.setGender(UserInfoEntity.Gender.MALE);
            } else if (Integer.valueOf(2).equals(user.getGender())) {
                userInfoEntity.setGender(UserInfoEntity.Gender.FEMALE);
            }
        }
        // 手机号码
        if (StringUtils.startsWith(user.getMobile(), "+86")) {
            userInfoEntity.setCeilPhoneNumber(StringUtils.substring(user.getMobile(), 3));
        } else {
            userInfoEntity.setCeilPhoneNumber(user.getMobile());
        }
        // 邮箱
        if (orgSyncOption.isUserEmail()) {
            userInfoEntity.setMail(user.getEmail());
        }
        // 员工编号
        if (orgSyncOption.isUserNo()) {
            userInfoEntity.setUserNo(user.getEmployeeNo());
        }
        // 备注
        if (orgSyncOption.isUserRemark()) {
            userInfoEntity.setRemark(user.getDescription());
        }
    }

    private User userEvent2User(UserEvent userEvent) {
        User user = new User();
        BeanUtils.copyProperties(userEvent, user);
        user.setName(userEvent.getName());
        user.setAvatar(userEvent.getAvatar());
        user.setGender(userEvent.getGender());
        user.setMobile(userEvent.getMobile());
        user.setMobileVisible(userEvent.getMobileVisible());
        user.setEmail(userEvent.getEmail());
        user.setEmployeeNo(userEvent.getEmployeeNo());
        user.setEmployeeType(userEvent.getEmployeeType());
        user.setDepartmentIds(userEvent.getDepartmentIds());
        return user;
    }

    private UserDto toUserDto(User user, List<String> orgElementIds, OrgVersionEntity orgVersionEntity,
                              FeishuConfigVo feishuConfigVo) {
        FeishuConfigVo.FeishuOrgSyncOption orgSyncOption = feishuConfigVo.getConfiguration().getOrgSyncOption();
        if (orgSyncOption == null) {
            orgSyncOption = new FeishuConfigVo.FeishuOrgSyncOption();
        }
        UserDto userDto = new UserDto();
        // 姓名
        userDto.setUserName(user.getName());
        userDto.setLoginName(generateLoginName(user));
        userDto.setPassword("123456");
        // 头像
        if (orgSyncOption.isUserAvatar()) {
            String avatarFileId = getAvatarFileId(user);
            userDto.setAvatar(avatarFileId);
        }
        // 性别
        if (orgSyncOption.isUserGender()) {
            if (Integer.valueOf(1).equals(user.getGender())) {
                userDto.setGender(UserInfoEntity.Gender.MALE);
            } else if (Integer.valueOf(2).equals(user.getGender())) {
                userDto.setGender(UserInfoEntity.Gender.FEMALE);
            }
        }
        // 手机号码
        if (StringUtils.startsWith(user.getMobile(), "+86")) {
            userDto.setCeilPhoneNumber(StringUtils.substring(user.getMobile(), 3));
        } else {
            userDto.setCeilPhoneNumber(user.getMobile());
        }
        // 邮箱
        if (orgSyncOption.isUserEmail()) {
            userDto.setMail(user.getEmail());
        }
        // 员工编号
        if (orgSyncOption.isUserNo()) {
            userDto.setUserNo(user.getEmployeeNo());
        }
        // 备注
        if (orgSyncOption.isUserRemark()) {
            userDto.setRemark(user.getDescription());
        }
        userDto.setOrgVersionUuid(orgVersionEntity.getUuid());

        Map<String, List<String>> orgReportTos = Maps.newHashMap();
        List<OrgUserEntity> orgUserEntities = Lists.newArrayList();
        orgElementIds.forEach(orgElementId -> {
            orgReportTos.put(orgElementId, Collections.emptyList());
            OrgUserEntity orgUserEntity = new OrgUserEntity();
            orgUserEntity.setOrgElementId(orgElementId);
            orgUserEntity.setType(OrgUserEntity.Type.MEMBER_USER);
            orgUserEntities.add(orgUserEntity);
        });
        userDto.setOrgReportTos(orgReportTos);
        userDto.setOrgUsers(orgUserEntities);
        return userDto;
    }

    private String getAvatarFileId(User user) {
        try {
            AvatarInfo avatarInfo = user.getAvatar();
            if (avatarInfo != null && StringUtils.isNotBlank(avatarInfo.getAvatarOrigin())) {
                InputStream inputStream = HttpUtil.getInputStream(avatarInfo.getAvatarOrigin());
                MongoFileEntity mongoFileEntity = mongoFileService.saveFile(user.getOpenId() + ".png", inputStream);
                IOUtils.closeQuietly(inputStream);
                return mongoFileEntity.getFileID();
            }
        } catch (Exception e) {
        }
        return null;
    }

    private String generateLoginName(User user) {
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

    private OrgElementDto toOrgElementDto(DepartmentNode departmentNode, OrgVersionEntity orgVersionEntity, Map<String, Long> departmentMap) {
        Department department = departmentNode.getDepartment();
        String orgElementId = feishuDeptService.getOrgElementIdByOpenDepartmentId(department.getOpenDepartmentId());

        OrgElementDto orgElementDto = new OrgElementDto();
        orgElementDto.setOrgVersionUuid(orgVersionEntity.getUuid());
        orgElementDto.setOrgVersionId(orgVersionEntity.getId());
        orgElementDto.setName(department.getName());
        orgElementDto.setId(orgElementId);
//        if (NumberUtils.isNumber(department.getOrder())) {
//            orgElementDto.setSeq(NumberUtils.createNumber(department.getOrder()).intValue());
//        }
        orgElementDto.setCode(department.getOrder());
        orgElementDto.setType(OrgElementModelEntity.ORG_DEPT_ID);
        orgElementDto.setState(OrgVersionEntity.State.PUBLISHED);
        orgElementDto.setParentUuid(departmentMap.get(department.getParentDepartmentId()));
        setDepartmentI18nName(orgElementDto, department);
        if (StringUtils.isNotBlank(orgElementId)) {
            Long orgElementUuid = feishuDeptService.getOrgElementUuidByOrgElementId(orgElementId);
            if (orgElementUuid != null) {
                orgElementDto.setRoleUuids(orgFacadeService.listOrgElementRoleUuidByUuid(orgElementUuid));
            }
        }
        return orgElementDto;
    }

    private void setDepartmentI18nName(OrgElementDto orgElementDto, Department department) {
        DepartmentI18nName departmentI18nName = department.getI18nName();
        if (departmentI18nName != null) {
            List<OrgElementI18nEntity> i18ns = Lists.newArrayList();
            String zhCnName = departmentI18nName.getZhCn();
            String enUsName = departmentI18nName.getEnUs();
            String jaJpName = departmentI18nName.getJaJp();
            if (StringUtils.isNotBlank(enUsName)) {
                OrgElementI18nEntity i18nEntity = new OrgElementI18nEntity();
                i18nEntity.setLocale("zh_CN");
                i18nEntity.setDataCode("name");
                i18nEntity.setContent(zhCnName);
                i18ns.add(i18nEntity);
            }
            if (StringUtils.isNotBlank(enUsName)) {
                OrgElementI18nEntity i18nEntity = new OrgElementI18nEntity();
                i18nEntity.setLocale("en_US");
                i18nEntity.setDataCode("name");
                i18nEntity.setContent(enUsName);
                i18ns.add(i18nEntity);
            }
            if (StringUtils.isNotBlank(jaJpName)) {
                OrgElementI18nEntity i18nEntity = new OrgElementI18nEntity();
                i18nEntity.setLocale("ja_JP");
                i18nEntity.setDataCode("name");
                i18nEntity.setContent(jaJpName);
                i18ns.add(i18nEntity);
            }
            orgElementDto.setI18ns(i18ns);
        }
    }

}
