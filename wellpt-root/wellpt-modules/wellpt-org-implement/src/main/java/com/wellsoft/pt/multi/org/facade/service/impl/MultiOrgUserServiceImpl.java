/*
 * @(#)2017年11月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.facade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.*;
import com.wellsoft.pt.multi.org.dto.MultiOrgPwdSettingDto;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.enums.AdminSetPwdTypeEnum;
import com.wellsoft.pt.multi.org.event.AccountUserEvent;
import com.wellsoft.pt.multi.org.event.OperateAccountUserPublisher;
import com.wellsoft.pt.multi.org.facade.service.*;
import com.wellsoft.pt.multi.org.service.*;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.config.entity.MultiUserLoginSettingsEntity;
import com.wellsoft.pt.security.config.service.MultiUserLoginSettingsService;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月22日.1	zyguo		2017年11月22日		Create
 * </pre>
 * @date 2017年11月22日
 */
@Service
public class MultiOrgUserServiceImpl implements MultiOrgUserService {
    private Logger logger = LoggerFactory.getLogger(MultiOrgUserServiceImpl.class);
    @Autowired
    private MultiOrgUserAccountFacadeService multiOrgUserAccountService;
    @Autowired
    private MultiOrgService multiOrgService;
    @Autowired
    private MultiOrgVersionFacade multiOrgVersionService;
    @Autowired
    private MultiOrgUserInfoService multiOrgUserInfoService;
    @Autowired
    private MultiOrgUserTreeNodeService multiOrgUserTreeNodeService;
    @Autowired
    private MultiOrgDutyService multiOrgDutyService;
    @Autowired
    private MultiOrgUserRoleService multiOrgUserRoleService;
    @Autowired
    private MultiOrgElementRoleService multiOrgElementRoleService;
    @Autowired
    private RoleFacadeService roleFacade;
    @Autowired
    private MongoFileService mongoFileService;

    @Autowired(required = false)
    private List<DealUserChangeEvent> userChangeEventList;
    @Autowired
    private MultiOrgUserWorkInfoService multiOrgUserWorkInfoService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MultiOrgTreeNodeService multiOrgTreeNodeService;
    @Autowired
    private MultiOrgGroupMemberService multiOrgGroupMemberService;
    @Autowired
    private SecurityApiFacade securityApiFacade;

    @Autowired
    private OperateAccountUserPublisher operateAccountUserPublisher;
    @Autowired
    private UserInfoFacadeService userInfoFacadeService;
    @Autowired
    private MultiOrgPwdSettingFacadeService multiOrgPwdSettingFacadeService;
    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;
    @Autowired
    private MultiOrgElementService multiOrgElementService;

    @Autowired
    private MultiJobRankLevelService multiJobRankLevelService;

    @Autowired
    private MultiOrgJobRankService multiOrgJobRankService;

    @Autowired
    private MultiUserLoginSettingsService multiUserLoginSettingsService;

    /**
     * 添加账号 // 添加新用户
     *
     * @param vo 包含的密码不加密
     * @return
     */
    @Override
    @Transactional
    public OrgUserVo addUser(OrgUserVo vo) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(vo.getLoginNameZh())) {
            vo.setLoginNameZh(vo.getUserName());
        }
        // 创建账号
        String pwd = vo.getPassword();
        MultiOrgUserAccount newAccount = null;
        Boolean isRandomPwd = Boolean.FALSE;
        MultiOrgPwdSettingDto multiOrgPwdSettingDto = multiOrgPwdSettingFacadeService.getMultiOrgPwdSetting();
        if (StringUtils.isBlank(multiOrgPwdSettingDto.getUuid())) {
            throw new RuntimeException("密码规则数据为空，无法操作，请联系管理员保存密码规则！");
        }
        if (AdminSetPwdTypeEnum.RandomPwd.getValue().equals(multiOrgPwdSettingDto.getAdminSetPwdType())) {
            isRandomPwd = Boolean.TRUE;
            // 随机密码
            if (StringUtils.isBlank(vo.getPassword())) {
                pwd = PwdUtils.generatePwdByRoleByCkeckPwdRole(multiOrgPwdSettingDto);
                vo.setPassword(pwd);
            }
            newAccount = this.multiOrgUserAccountService.addUserAccount(vo, isRandomPwd);
        } else {
            newAccount = this.multiOrgUserAccountService.addUserAccount(vo, isRandomPwd);
        }

        vo.setId(newAccount.getId());
        // 保存个人信息
        MultiOrgUserInfo info = new MultiOrgUserInfo();
        BeanUtils.copyProperties(vo, info, IdEntity.BASE_FIELDS);
        info.setUserId(newAccount.getId());
        this.multiOrgUserInfoService.save(info);
        try {
            if (userInfoFacadeService.isExist(vo.getLoginName().toLowerCase())) {
                userInfoFacadeService.modifyUser(
                        new UserDto(vo.getLoginName(), vo.getUserName(), vo.getPassword(), UserTypeEnum.STAFF));
            } else {
                userInfoFacadeService.addUser(
                        new UserDto(vo.getLoginName(), vo.getUserName(), vo.getPassword(), UserTypeEnum.STAFF));
                // 随机密码把原密码返回给前端
                if (isRandomPwd) {
                    vo.setPassword(pwd);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("该外部账号已存在，无法新建[" + vo.getLoginName() + "]", e);
        }

        if (StringUtils.isNotBlank(vo.getPhotoUuid())) {
            // 添加头像对应的图片
            mongoFileService.pushFileToFolder(getUserPhotoFoloder(vo.getId()), vo.getPhotoUuid(), null);
        }
        // 保存职位信息
        this.addJobs(newAccount, vo);

        // 保存角色信息
        this.addUserRoles(newAccount.getId(), vo);
        // 角色信息变更 ，需要发布一下，不管是删除，还是新增，或是修改，对应的角色都需要发布下
        Set<String> roleUuids = Sets.newHashSet();
        // 新角色需要放进来
        if (StringUtils.isNotBlank(vo.getRoleUuids())) {
            roleUuids.addAll(Lists.newArrayList(vo.getRoleUuids().split(";")));
        }
        for (String roleUuid : roleUuids) {
            roleFacade.publishRoleUpdatedEvent(roleUuid);
        }

        ArrayList<MultiOrgUserAccount> newAccountList = new ArrayList<MultiOrgUserAccount>();
        newAccountList.add(newAccount);
        operateAccountUserPublisher
                .publish(AccountUserEvent.AccountUserEventSource.source().accounts(newAccountList).save());

        // 触发其他模块的相关需要处理的用户添加事件
        if (CollectionUtils.isNotEmpty(userChangeEventList)) {
            for (DealUserChangeEvent event : userChangeEventList) {
                event.dealAddUserEvent(vo);
            }
        }
        return vo;
    }

    /**
     * 校验用户登录信息是否唯一
     *
     * @param vo
     * @return
     * @author baozh
     * @date 2021/11/24 10:22
     */
    @Override
    public List<String> checkOnly(OrgUserVo vo) {
        MultiUserLoginSettingsEntity loginSettings = multiUserLoginSettingsService.getLoginSettingsEntity();
        if (loginSettings == null) {
            return null;
        }
        List<String> names = new ArrayList<>();
        names.add(vo.getLoginName());
        if (StringUtils.isBlank(vo.getLoginNameZh())) {
            vo.setLoginNameZh(vo.getUserName());
        }
        if (loginSettings.getAccountZhEnable() == 1) {
            names.add(vo.getLoginNameZh().toLowerCase());
        }
        if (loginSettings.getNameEnEnable() == 1 && StringUtils.isNotBlank(vo.getEnglishName())) {
            names.add(vo.getEnglishName().toLowerCase());
        }
        if (loginSettings.getTellEnable() == 1 && StringUtils.isNotBlank(vo.getMobilePhone())) {
            names.add(vo.getMobilePhone().toLowerCase());
        }
        if (loginSettings.getIdentifierCodeEnable() == 1 && StringUtils.isNotBlank(vo.getIdNumber())) {
            names.add(vo.getIdNumber().toLowerCase());
        }
        if (loginSettings.getEmailEnable() == 1 && StringUtils.isNotBlank(vo.getMainEmail())) {
            names.add(vo.getMainEmail().toLowerCase());
        }
        if (loginSettings.getEmpCodeEnable() == 1 && StringUtils.isNotBlank(vo.getEmployeeNumber())) {
            names.add(vo.getEmployeeNumber().toLowerCase());
        }
        Map<String, Object> param = new HashMap<>();
        param.putAll(JSONObject.parseObject(JSONObject.toJSONString(loginSettings)));
        param.put("checkValue", names);
        param.put("userUuid", vo.getUuid());
        List<QueryItem> list = multiUserLoginSettingsService.listQueryItemByNameHQLQuery("checkUserLoginNameQuery",
                param, null);
        if (list.isEmpty()) {
            return null;
        }
        return checkOnlyResult(vo, loginSettings, list);
    }

    private List<String> checkOnlyResult(OrgUserVo vo, MultiUserLoginSettingsEntity loginSettings,
                                         List<QueryItem> list) {
        List<String> result = new ArrayList<>();
        Set<String> names = new HashSet<>();
        // 收集返回数据
        for (QueryItem item : list) {
            names.add(item.getString("loginNameLowerCase"));
            if (loginSettings.getAccountZhEnable() == 1 && item.getString("loginNameZh") != null) {
                names.add(item.getString("loginNameZh").toLowerCase());
            }
            if (loginSettings.getNameEnEnable() == 1 && item.getString("englishName") != null) {
                names.add(item.getString("englishName").toLowerCase());
            }
            if (loginSettings.getTellEnable() == 1 && item.getString("mobilePhone") != null) {
                names.add(item.getString("mobilePhone").toLowerCase());
            }
            if (loginSettings.getIdentifierCodeEnable() == 1 && item.getString("idNumber") != null) {
                names.add(item.getString("idNumber").toLowerCase());
            }
            if (loginSettings.getEmailEnable() == 1 && item.getString("mainEmail") != null) {
                names.add(item.getString("mainEmail").toLowerCase());
            }
            if (loginSettings.getEmpCodeEnable() == 1 && item.getString("employeeNumber") != null) {
                names.add(item.getString("employeeNumber").toLowerCase());
            }
        }
        if (names.contains(vo.getLoginName())) {
            result.add("账号名");
        }
        if (loginSettings.getAccountZhEnable() == 1 && names.contains(vo.getLoginNameZh().toLowerCase())) {
            result.add("中文账号名");
        }
        if (loginSettings.getNameEnEnable() == 1 && vo.getEnglishName() != null
                && names.contains(vo.getEnglishName().toLowerCase())) {
            result.add("英文名");
        }
        if (loginSettings.getTellEnable() == 1 && vo.getMobilePhone() != null
                && names.contains(vo.getMobilePhone().toLowerCase())) {
            result.add("手机号");
        }
        if (loginSettings.getIdentifierCodeEnable() == 1 && vo.getIdNumber() != null
                && names.contains(vo.getIdNumber().toLowerCase())) {
            result.add("身份证号");
        }
        if (loginSettings.getEmailEnable() == 1 && vo.getMainEmail() != null
                && names.contains(vo.getMainEmail().toLowerCase())) {
            result.add("邮箱");
        }
        if (loginSettings.getEmpCodeEnable() == 1 && vo.getEmployeeNumber() != null
                && names.contains(vo.getEmployeeNumber().toLowerCase())) {
            result.add("员工编号");
        }
        return result;
    }

    private void addUserRoles(String userId, OrgUserVo vo) {
        if (StringUtils.isNotBlank(vo.getRoleUuids())) {
            this.multiOrgUserRoleService.addRoleListOfUser(userId, vo.getRoleUuids());
        }
    }

    private String getUserPhotoFoloder(String userId) {
        return userId + "_photo";
    }

    // 修改用户
    @Override
    @Transactional
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public OrgUserVo modifyUser(OrgUserVo vo) {

        if (StringUtils.isBlank(vo.getLoginNameZh())) {
            vo.setLoginNameZh(vo.getUserName());
        }
        OrgUserVo oldUser = this.getUser(vo.getUuid());

        // 修改账号信息
        MultiOrgUserAccount newAccount = this.multiOrgUserAccountService.modifyUserAccount(vo);

        // 修改个人信息
        MultiOrgUserInfo info = this.multiOrgUserInfoService.getByUserId(newAccount.getId());
        BeanUtils.copyProperties(vo, info, IdEntity.BASE_FIELDS);
        this.multiOrgUserInfoService.save(info);
        if (StringUtils.isBlank(vo.getPhotoUuid())) {
            mongoFileService.popAllFilesFromFolder(getUserPhotoFoloder(vo.getId()));
        } else {
            // 先删后加
            mongoFileService.popAllFilesFromFolder(getUserPhotoFoloder(vo.getId()));
            mongoFileService.pushFileToFolder(getUserPhotoFoloder(vo.getId()), vo.getPhotoUuid(), null);
        }

        // 修改职位信息
        this.modifyUserJob(newAccount, vo);

        // 修改角色信息, 先删后加
        this.multiOrgUserRoleService.deleteRoleListOfUser(newAccount.getId());
        this.addUserRoles(newAccount.getId(), vo);

        // 触发其他模块的相关需要处理的用户变更事件
        if (CollectionUtils.isNotEmpty(userChangeEventList)) {
            for (DealUserChangeEvent event : userChangeEventList) {
                event.dealModifyUserInfoEvent(oldUser, vo);
            }
        }

        return vo;
    }

    // 删掉旧的职位信息，然后插入新的职位信息
    private void modifyUserJob(MultiOrgUserAccount user, OrgUserVo vo) {
        // 获取当前的对应的组织版本
        List<MultiOrgVersion> versionList = this.multiOrgVersionService
                .queryCurrentActiveVersionListOfSystemUnit(user.getSystemUnitId());
        if (!CollectionUtils.isEmpty(versionList)) {
            for (MultiOrgVersion version : versionList) {
                this.multiOrgUserTreeNodeService.deleteUserJobByOrgVersion(user.getId(), version.getId());
            }
        }

        // 重新插入新职位
        this.addJobs(user, vo);
    }

    /**
     * 通过UUID，获取一个用户信息
     */
    @Override
    public OrgUserVo getUser(String uuid) {
        Assert.isTrue(StringUtils.isNotBlank(uuid), "参数不能为空");
        MultiOrgUserAccount account = this.multiOrgUserAccountService.getAccount(uuid);
        Assert.isTrue(account != null, "对应的用户不存在");
        return getUser(account, true, true, true);
    }

    /**
     * @param account
     * @param includeJobInfo
     * @param includeWorkInfo
     * @param includeRoleInfo
     * @return
     */
    private OrgUserVo getUser(MultiOrgUserAccount account, boolean includeJobInfo, boolean includeWorkInfo,
                              boolean includeRoleInfo) {
        Assert.isTrue(account != null, "对应的用户不存在");

        OrgUserVo vo = new OrgUserVo();
        // 设置账号信息
        BeanUtils.copyProperties(account, vo);
        // 设置个人信息
        MultiOrgUserInfo userInfo = this.multiOrgUserInfoService.getByUserId(account.getId());
        BeanUtils.copyProperties(userInfo, vo, IdEntity.BASE_FIELDS);

        Map<String, List<String>> eleIdStrsMap = new LinkedHashMap<>();
        // 获取职位信息
        if (includeJobInfo) {
            ArrayList<OrgUserJobDto> jobs = this.getUserJob(account);
            vo.setJobList(jobs);
            // 转化成mainJob, mainDept
            if (CollectionUtils.isNotEmpty(jobs)) {
                List<String> jobIds = Lists.newArrayList();
                List<String> jobIdPaths = Lists.newArrayList();
                List<String> jobNames = Lists.newArrayList();
                List<String> jobNamePaths = Lists.newArrayList();
                List<String> businessUnitIds = Lists.newArrayList();
                List<String> businessUnitNames = Lists.newArrayList();
                for (OrgUserJobDto userJob : jobs) {
                    if (userJob == null) {
                        continue;
                    }
                    OrgTreeNodeDto jobDto = userJob.getOrgTreeNodeDto();
                    if (null != userJob.getIsMain() && userJob.getIsMain() == 1) {
                        vo.setMainJobId(jobDto.getOrgVersionId() + "/" + jobDto.getEleId());
                        vo.setMainJobName(jobDto.getName());
                        vo.setMainJobIdPath(jobDto.getEleIdPath());
                        vo.setMainJobNamePath(jobDto.getEleNamePath());
                        MultiOrgDuty duty = multiOrgDutyService.getByJobId(jobDto.getEleId());
                        if (duty != null) {
                            vo.setDutyId(duty.getId());
                            vo.setDutyName(duty.getName());
                        }
                        String namePath = this.orgApiFacade.getEleNamePathByEleIdPath(jobDto.getEleIdPath());
                        vo.setMainJobNamePath(namePath);
                        eleIdStrsMap.put("main",
                                Lists.newArrayList(namePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)));
                        vo.setMainDepartmentId(jobDto.getOrgVersionId() + "/" + jobDto.getDeptId());
                        String deptNamePath = multiOrgService.getDepartmentNamePathByJobIdPath(jobDto.getEleIdPath(),
                                false);
                        jobDto.setDeptNamePath(deptNamePath);
                        vo.setMainDepartmentIdPath(jobDto.getDeptIdPath());
                        vo.setMainDepartmentName(jobDto.getDeptName());
                        vo.setMainDepartmentNamePath(jobDto.getDeptNamePath());

                        MultiOrgElement unitEle = this.orgApiFacade.getBusinessUnitByJobIdPath(jobDto.getEleIdPath());
                        if (unitEle != null) {// 设置业务单位信息
                            vo.setMainBusinessUnitId(unitEle.getId());
                            vo.setMainBusinessUnitName(unitEle.getName());
                        }
                    } else {
                        jobIds.add(jobDto.getOrgVersionId() + "/" + jobDto.getEleId());
                        jobIdPaths.add(jobDto.getEleIdPath());
                        jobNames.add(jobDto.getName());
                        String namePath = this.orgApiFacade.getEleNamePathByEleIdPath(jobDto.getEleIdPath());
                        eleIdStrsMap.put("other" + jobDto.getEleId(),
                                Lists.newArrayList(namePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL)));
                        jobNamePaths.add(namePath);

                        MultiOrgElement unitEle = this.orgApiFacade.getBusinessUnitByJobIdPath(jobDto.getEleIdPath());
                        if (unitEle != null) {// 设置业务单位信息
                            businessUnitIds.add(unitEle.getId());
                            businessUnitNames.add(unitEle.getName());
                        }

                    }
                }
                vo.setOtherJobIds(StringUtils.join(jobIds, ";"));
                vo.setOtherJobIdPaths(StringUtils.join(jobIdPaths, ";"));
                vo.setOtherJobNames(StringUtils.join(jobNames, ";"));
                vo.setOtherJobNamePaths(StringUtils.join(jobNamePaths, ";"));
                vo.setOtherBusinessUnitIds(StringUtils.join(businessUnitIds, ";"));
                vo.setOtherBusinessUnitNames(StringUtils.join(businessUnitNames, ";"));
            }
            // 获取工作职级职档
            List<JobRankLevelVo> relationList = multiJobRankLevelService.queryListByUserId(account.getId());
            vo.setRelationList(relationList);
        }

        // 获取角色信息
        if (includeRoleInfo) {
            List<MultiOrgUserRole> roleList = this.queryRoleListOfUser(account.getId());
            String roleUuids = ListUtils.list2StringsByField(roleList, "roleUuid");
            vo.setRoleUuids(roleUuids);
        }

        if (includeWorkInfo) {
            MultiOrgUserWorkInfo userWorkInfo = multiOrgUserWorkInfoService.getUserWorkInfo(account.getId());
            // 直属上级领导
            if (userWorkInfo != null && userWorkInfo.getDirectLeaderIds() != null) {
                String directLeaderIds = userWorkInfo.getDirectLeaderIds();
                List<String> directLeaderNamePathList = new ArrayList<>();
                for (String directLeaderId : directLeaderIds.split(Separator.SEMICOLON.getValue())) {
                    String[] split = directLeaderId.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                    String versionId = split[0];
                    String directLeaderOnlyId;
                    if (split.length > 1) {
                        directLeaderOnlyId = split[1];
                    } else {
                        directLeaderOnlyId = split[0];
                    }
                    if (directLeaderOnlyId.startsWith(IdPrefix.JOB.getValue())) {
                        OrgTreeNodeDto orgTreeNodeDto = multiOrgTreeNodeService
                                .getNodeByEleIdAndOrgVersion(directLeaderOnlyId, versionId);
                        String jobName = orgApiFacade.getNameByOrgEleId(directLeaderOnlyId);
                        if (orgTreeNodeDto == null) {
                            jobName = orgApiFacade.getNameByOrgEleId(directLeaderOnlyId);
                        } else {
                            String eleIdPath = orgTreeNodeDto.getEleIdPath();
                            jobName = orgApiFacade.getEleNamePathByEleIdPath(eleIdPath);
                            eleIdStrsMap.put("leader" + directLeaderOnlyId,
                                    Lists.newArrayList(jobName.split(MultiOrgService.PATH_SPLIT_SYSMBOL)));
                        }
                        directLeaderNamePathList.add(jobName);
                    } else if (directLeaderOnlyId.startsWith(IdPrefix.USER.getValue())) {
                        MultiOrgUserAccount directLeaderAccount = multiOrgUserAccountService
                                .getAccountByUserId(directLeaderOnlyId);
                        if (directLeaderAccount != null) {
                            directLeaderNamePathList.add(directLeaderAccount.getUserName());
                            ArrayList<OrgUserJobDto> orgUserJobDtos = this.queryUserJobByUserId(directLeaderOnlyId);
                            for (OrgUserJobDto userJob : orgUserJobDtos) {
                                OrgTreeNodeDto jobDto = userJob.getOrgTreeNodeDto();
                                if (userJob.getIsMain() == 1) {
                                    String namePath = this.orgApiFacade
                                            .getEleNamePathByEleIdPath(jobDto.getEleIdPath());
                                    String[] jobStrs = namePath.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                                    ArrayList<String> list = Lists.newArrayList(jobStrs);
                                    list.add(directLeaderAccount.getUserName());
                                    eleIdStrsMap.put("leader" + directLeaderOnlyId, list);
                                }
                            }

                        }
                    }
                }
                vo.setDirectLeaderIds(userWorkInfo.getDirectLeaderIds());
                vo.setDirectLeaderNamePaths(StringUtils.join(directLeaderNamePathList, Separator.SEMICOLON.getValue()));
            }
        }
        multiOrgTreeDialogService.eliminateDuplicateNames(eleIdStrsMap, 0);
        vo.setMainJobSmartNamePath(StringUtils.join(eleIdStrsMap.get("main"), MultiOrgService.PATH_SPLIT_SYSMBOL));
        List<String> otherJobSmartNamePathList = new ArrayList<>();
        for (String key : eleIdStrsMap.keySet()) {
            if (key.startsWith("other")) {
                otherJobSmartNamePathList
                        .add(StringUtils.join(eleIdStrsMap.get(key), MultiOrgService.PATH_SPLIT_SYSMBOL));
            }
        }
        vo.setOtherJobSmartNamePaths(StringUtils.join(otherJobSmartNamePathList, ";"));
        List<String> leaderSmartNamePathList = new ArrayList<>();
        for (String key : eleIdStrsMap.keySet()) {
            if (key.startsWith("leader" + IdPrefix.USER.getValue())) {
                List<String> list = eleIdStrsMap.get(key);
                leaderSmartNamePathList.add(list.get(list.size() - 1));
            } else if (key.startsWith("leader")) {
                leaderSmartNamePathList
                        .add(StringUtils.join(eleIdStrsMap.get(key), MultiOrgService.PATH_SPLIT_SYSMBOL));
            }
        }
        vo.setDirectLeaderSmartNamePaths(StringUtils.join(leaderSmartNamePathList, ";"));
        return vo;
    }

    @Override
    public List<MultiOrgUserRole> queryRoleListOfUser(String userId) {
        return this.multiOrgUserRoleService.queryRoleListOfUser(userId);
    }

    @Override
    public ArrayList<OrgUserJobDto> queryUserJob(String userUuid) {
        Assert.isTrue(StringUtils.isNotBlank(userUuid), "参数不能为空");
        MultiOrgUserAccount account = this.multiOrgUserAccountService.getAccount(userUuid);
        Assert.isTrue(account != null, "对应的用户不存在");
        return this.getUserJob(account);
    }

    @Override
    public ArrayList<OrgUserJobDto> queryUserJobByUserId(String userId) {
        Assert.isTrue(StringUtils.isNotBlank(userId), "参数不能为空");
        MultiOrgUserAccount account = this.multiOrgUserAccountService.getAccountByUserId(userId);
        Assert.isTrue(account != null, "对应的用户不存在");
        return this.getUserJob(account);
    }

    private ArrayList<OrgUserJobDto> getUserJob(MultiOrgUserAccount account) {
        // 先获取用户的归属单位的最新版本信息，然后获取该版本的职位信息，因为存在多形态的问题，所以返回值是个LIST
        List<MultiOrgVersion> verList = multiOrgVersionService
                .queryCurrentActiveVersionListOfSystemUnit(account.getSystemUnitId());
        if (!CollectionUtils.isEmpty(verList)) {
            ArrayList<OrgUserJobDto> list = new ArrayList<OrgUserJobDto>();
            List<MultiOrgUserTreeNode> allJobs = new ArrayList<MultiOrgUserTreeNode>();
            for (MultiOrgVersion version : verList) {
                List<MultiOrgUserTreeNode> jobs = multiOrgUserTreeNodeService.queryUserJobByOrgVersion(account.getId(),
                        version.getId());
                allJobs.addAll(jobs);
            }

            for (MultiOrgUserTreeNode j : allJobs) {
                OrgTreeNodeDto treeNode = multiOrgService.getNodeByEleIdAndOrgVersion(j.getEleId(),
                        j.getOrgVersionId());
                if (treeNode != null) {
                    OrgUserJobDto vo = new OrgUserJobDto();
                    vo.setOrgTreeNodeDto(treeNode);
                    vo.setIsMain(j.getIsMain());
                    vo.setUserId(j.getUserId());
                    list.add(vo);
                } else {
                    logger.warn("用户" + account.getId() + "对应的职位找不到对应的节点，该组织版本是否有重新导入组织？");
                }
            }
            // 获取工作信息，对list排序，根据orgUserWorkInfo的jobIds顺序排序
            MultiOrgUserWorkInfo orgUserWorkInfo = multiOrgUserWorkInfoService.getUserWorkInfo(account.getId());
            if (null == orgUserWorkInfo) {
                return list;
            }
            String jobIds = orgUserWorkInfo.getJobIds();
            if (StringUtils.isNotBlank(jobIds)) {
                String[] jobIdArr = jobIds.split(";");
                OrgUserJobDto[] newJobDto = new OrgUserJobDto[list.size()];
                for (OrgUserJobDto orgUserJobDto : list) {
                    for (int i = 0; i < jobIdArr.length; i++) {
                        if (jobIdArr[i].equals(orgUserJobDto.getOrgTreeNodeDto().getEleId())) {
                            newJobDto[i] = orgUserJobDto;
                            break;
                        }
                    }
                }
                return new ArrayList<>(Arrays.asList(newJobDto));
            }

            return list;
        }
        return null;
    }

    // 添加用户职位信息
    @SuppressWarnings("static-access")
    private void addJobs(MultiOrgUserAccount a, OrgUserVo vo) {
        ArrayList<String> jobIdList = new ArrayList<String>();
        // 添加主职
        if (StringUtils.isNotBlank(vo.getMainJobId())) {
            jobIdList.add(vo.getMainJobId());
        }
        // 添加其他的职位
        if (StringUtils.isNotBlank(vo.getOtherJobIds())) {
            String[] jobs = vo.getOtherJobIds().split(";");
            jobIdList.addAll(Arrays.asList(jobs));
        }
        // 职位ids
        List<String> jobIds = new ArrayList<>();
        // 部门ids
        List<String> deptIds = new ArrayList<String>();
        // 节点全路径
        List<String> eleIdPaths = new ArrayList<String>();

        for (int i = 0; i < jobIdList.size(); i++) {
            if (StringUtils.isBlank(jobIdList.get(i))) {
                continue;
            }
            String[] idAndVer = jobIdList.get(i).split(MultiOrgService.PATH_SPLIT_SYSMBOL);
            String versionId = idAndVer[0];
            String jobId = idAndVer[1];
            MultiOrgUserTreeNode userNode = new MultiOrgUserTreeNode();
            userNode.setUserId(a.getId());
            userNode.setEleId(jobId);
            userNode.setOrgVersionId(versionId);
            List<MultiOrgUserTreeNode> list = multiOrgUserTreeNodeService.listByEntity(userNode);
            if (CollectionUtils.isNotEmpty(list)) {
                userNode = list.get(0);// 已经存在则更新
            }
            if (i == 0) { // 如果没有设置主职，设置第一个职位为主职
                userNode.setIsMain(1);
            }
            if (!jobIds.contains(jobId)) {
                jobIds.add(jobId);
            }
            this.multiOrgUserTreeNodeService.save(userNode);

            OrgTreeNodeDto treeNodeDto = multiOrgService.getNodeOfCurrentVerisonByEleId(jobId);
            if (treeNodeDto != null) {
                String eleIdPath = treeNodeDto.getEleIdPath();
                if (StringUtils.isNotBlank(eleIdPath) && !eleIdPaths.contains(eleIdPath)) {
                    eleIdPaths.add(eleIdPath);
                    String deptId = treeNodeDto.getNearestEleIdByType(eleIdPath, "D");
                    if (StringUtils.isNotBlank(deptId) && !deptIds.contains(deptId)) {
                        deptIds.add(deptId);
                    }
                }
            }
        }

        MultiOrgUserWorkInfo entity = new MultiOrgUserWorkInfo();
        entity.setUserId(a.getId());
        MultiOrgUserWorkInfo orgUserWorkInfo = multiOrgUserWorkInfoService.getUserWorkInfo(a.getId());
        if (orgUserWorkInfo == null) {
            orgUserWorkInfo = new MultiOrgUserWorkInfo();
        }
        orgUserWorkInfo.setUserId(a.getId());
        orgUserWorkInfo.setSystemUnitId(a.getSystemUnitId());
        orgUserWorkInfo.setDeptIds(StringUtils.join(deptIds, ";"));
        orgUserWorkInfo.setJobIds(StringUtils.join(jobIds, ";"));
        orgUserWorkInfo.setEleIdPaths(StringUtils.join(eleIdPaths, ";"));

        if (StringUtils.isNotBlank(vo.getDirectLeaderIds())) {
            String[] directLeaderIdArr = vo.getDirectLeaderIds().split(Separator.SEMICOLON.getValue());
            // 去重
            orgUserWorkInfo.setDirectLeaderIds(
                    StringUtils.join(new HashSet<>(Arrays.asList(directLeaderIdArr)), Separator.SEMICOLON.getValue()));
        } else {
            orgUserWorkInfo.setDirectLeaderIds(StringUtils.EMPTY);
        }
        multiOrgUserWorkInfoService.save(orgUserWorkInfo);

        // 根据用户id删除工作职级职档
        // 可能存在超管操作用户信息
        multiJobRankLevelService.deleteByUserId(a.getId());
        // 保存工作职级职档
        List<JobRankLevelVo> relationList = vo.getRelationList();
        if (relationList != null && !relationList.isEmpty()) {
            for (JobRankLevelVo rankLevelVo : relationList) {
                MultiJobRankLevelEntity jobRankLevelEntity = new MultiJobRankLevelEntity();
                BeanUtils.copyProperties(rankLevelVo, jobRankLevelEntity);
                jobRankLevelEntity.setUserId(a.getId());
                multiJobRankLevelService.save(jobRankLevelEntity);
            }
        }
    }

    /**
     * 获取指定组织位置下的所有用户
     */
    @Override
    public List<OrgUserTreeNodeDto> queryUserByOrgTreeNode(String eleId, String orgVersionId) {
        Assert.isTrue(StringUtils.isNotBlank(eleId), "eleIdPath 不能为空");
        Assert.isTrue(StringUtils.isNotBlank(orgVersionId), "orgVersionId 不能为空");

        List<OrgUserTreeNodeDto> list = this.multiOrgUserTreeNodeService.queryUserByOrgTreeNode(eleId, orgVersionId);
        return list;
    }

    // 通过loginName获取用户信息
    @Override
    public OrgUserVo getUserByLoginNameIgnoreCase(String loginName, String loginNameHashAlgorithmCode) {
        Assert.isTrue(StringUtils.isNotBlank(loginName), "loginName不能为空");
        Assert.isTrue(StringUtils.isNotBlank(loginNameHashAlgorithmCode), "loginNameHashAlgorithmCode不能为空");
        // 中文登录修改,根据登录信息获取账户
        List<MultiOrgUserAccount> userAccounts = multiOrgUserAccountService
                .getUserAccountByLoginNameIgnoreCase(loginName);
        MultiOrgUserAccount multiOrgUserAccount = null;
        if (userAccounts == null || userAccounts.isEmpty()) {
            multiOrgUserAccount = multiOrgUserAccountService.getUserByLoginNameIgnoreCase(loginName,
                    loginNameHashAlgorithmCode);
        } else if (userAccounts.size() == 1) {
            multiOrgUserAccount = userAccounts.get(0);
        }

        if (multiOrgUserAccount == null) {
            return null;
        }
        return this.getUser(multiOrgUserAccount.getUuid());
    }

    // 通过userId获取用户信息
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public OrgUserVo getUserById(String userId) {
        return this.getUserById(userId, true, true, true);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService#getUserById(java.lang.String, boolean, boolean, boolean)
     */
    @Override
    public OrgUserVo getUserById(String userId, boolean includeJobInfo, boolean includeWorkInfo,
                                 boolean includeRoleInfo) {
        Assert.isTrue(StringUtils.isNotBlank(userId), "userId不能为空");
        if (MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId)) {
            // 平台管理员账号
            return orgApiFacade.getPTAdmin();
        } else {
            MultiOrgUserAccount a = this.multiOrgUserAccountService.getAccountByUserId(userId);
            if (a == null) {
                return null;
            }
            return this.getUser(a, includeJobInfo, includeWorkInfo, includeRoleInfo);
        }
    }

    /**
     * 批量获取用户,后面需要改进成去数据库一次性拉取
     */
    // TODO
    @Override
    public List<OrgUserVo> queryUserListByIds(List<String> ids) {
        ArrayList<OrgUserVo> list = new ArrayList<OrgUserVo>();
        if (!CollectionUtils.isEmpty(ids)) {
            for (String id : ids) {
                list.add(this.getUserById(id));
            }
        }
        return list;
    }

    /**
     * 处理组织版本升级的对应事件
     * 1,复制一份老版本的数据，保存为新版本
     */
    @Override
    @Transactional
    public boolean dealOrgUpgradeEvent(MultiOrgVersion oldVersion, MultiOrgVersion newVersion) {
        List<MultiOrgUserTreeNode> objs = this.multiOrgUserTreeNodeService.queryAllNodeByVersionId(oldVersion.getId());
        if (!CollectionUtils.isEmpty(objs)) {
            for (MultiOrgUserTreeNode node : objs) {
                MultiOrgUserTreeNode newNode = new MultiOrgUserTreeNode();
                BeanUtils.copyProperties(node, newNode, IdEntity.BASE_FIELDS);
                newNode.setOrgVersionId(newVersion.getId());
                this.multiOrgUserTreeNodeService.save(newNode);
            }
        }
        return true;
    }

    @Override
    public List<MultiOrgUserRole> queryUserListByRole(String roleUuid) {
        return this.multiOrgUserRoleService.queryUserListByRole(roleUuid);
    }

    /**
     * 角色删除，引用该角色的用户，必须跟着删除掉
     */
    @Override
    public boolean dealRoleRemoveEvent(String roleUuid) {
        this.multiOrgUserRoleService.deleteUserListOfRole(roleUuid);
        return true;
    }

    @Override
    public TreeNode getUserPrivilegeResultTree(String uuid) {
        OrgUserVo user = this.getUser(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(user.getUserName());
        treeNode.setId(TreeNode.ROOT_ID);
        if (StringUtils.isNotBlank(user.getRoleUuids())) {
            String[] roles = user.getRoleUuids().split(";");
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (String roleUuid : roles) {
                if (StringUtils.isBlank(roleUuid)) {
                    continue;
                }
                Role role = this.roleFacade.get(roleUuid);
                if (role == null || role.getSystemDef() == 1) {
                    continue;
                }
                TreeNode child = new TreeNode();
                child.setId(role.getUuid());
                child.setName(role.getName());
                children.add(child);
                this.roleFacade.buildRoleNestedRoleTree(child, role);
            }
            treeNode.setChildren(children);
        }
        return treeNode;
    }

    @Override
    public TreeNode getUserAllPrivilegeResultTree(String uuid) {
        OrgUserVo user = this.getUser(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(user.getUserName());
        treeNode.setId(TreeNode.ROOT_ID);
        Map<String, Set<String>> allRoles = this.orgApiFacade.queryAllRoleListByUser(user);
        if (allRoles != null) {
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (String roleUuid : allRoles.keySet()) {
                Role role = this.roleFacade.get(roleUuid);
                if (role == null || role.getSystemDef() == 1) {
                    continue;
                }
                TreeNode child = new TreeNode();
                child.setId(role.getUuid());
                child.setName(role.getName());
                children.add(child);
                this.roleFacade.buildRoleNestedRoleTree(child, role);
            }
            treeNode.setChildren(children);
        }
        return treeNode;
    }

    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public MultiOrgUserAccount getAccountByUserId(String userId) {
        return this.multiOrgUserAccountService.getAccountByUserId(userId);
    }

    @Override
    public MultiOrgUserAccount getAccountByUuid(String userUuid) {
        return this.multiOrgUserAccountService.getAccount(userUuid);
    }

    @Override
    public MultiOrgUserAccount getUserAccountByLoginName(String loginName) {
        return this.multiOrgUserAccountService.getUserByLoginNameIgnoreCase(loginName,
                PasswordAlgorithm.Plaintext.getValue());
    }

    @Override
    public List<MultiOrgUserAccount> queryAllAccountOfUnit(String systemUnitId) {
        return this.multiOrgUserAccountService.queryAllAccountOfUnit(systemUnitId);
    }

    @Override
    public List<OrgUserTreeNodeDto> queryUserByOrgVersion(String orgVersionId) {
        return this.multiOrgUserTreeNodeService.queryUserByOrgVersion(orgVersionId);
    }

    @Override
    public List<OrgUserJobDto> queryUserJobByOrgVersionId(String userId, String orgVersionId) {
        List<MultiOrgUserTreeNode> jobs = multiOrgUserTreeNodeService.queryUserJobByOrgVersion(userId, orgVersionId);
        List<OrgUserJobDto> list = new ArrayList<OrgUserJobDto>();
        if (!CollectionUtils.isEmpty(jobs)) {
            for (MultiOrgUserTreeNode j : jobs) {
                OrgTreeNodeDto dto = multiOrgService.getNodeByEleIdAndOrgVersion(j.getEleId(), j.getOrgVersionId());
                OrgUserJobDto vo = new OrgUserJobDto();
                vo.setOrgTreeNodeDto(dto);
                vo.setIsMain(j.getIsMain());
                vo.setUserId(j.getUserId());
                list.add(vo);
            }
        }
        return list;
    }

    // 需要把该节点下面的用户，移到对应的新节点位置下面
    @Override
    public void dealElementIdChangeEvent(String oldEleId, String newEleId, String orgVersionId) {
        this.multiOrgUserTreeNodeService.updateUserEleIdByOrgVersion(oldEleId, newEleId, orgVersionId);
    }

    // 更新用户的最后登录时间
    @Override
    public void updateLastLoginTime(String userUuid) {
        try {
            this.multiOrgUserAccountService.updateLastLoginTime(userUuid);
        } catch (Exception e) {
            logger.error("更新用户的最后登录时间 updateLastLoginTime 异常：", e);
            throw new BusinessException("网络异常");
        }

    }

    @Override
    public List<MultiOrgUserAccount> queryAllAdminIdsBySystemUnitId(String unitId) {
        return this.multiOrgUserAccountService.queryAllAdminIdsBySystemUnitId(unitId);
    }

    @Override
    public List<String> queryUserIdsLikeName(String name) {
        return this.multiOrgUserAccountService.queryUserIdsLikeName(name);
    }

    // 批量获取用户信息，userDto格式
    @Override
    public List<OrgUserDto> queryUserDtoListByIds(List<String> userIds) {
        return this.multiOrgUserAccountService.queryUserDtoListByIds(userIds);
    }

    // 添加单位管理员
    @Override
    @Transactional
    public OrgUserVo addUnitAdmin(OrgUserVo vo) throws UnsupportedEncodingException {
        vo.setType(MultiOrgUserAccount.TYPE_UNIT_ADMIN);
        return addUser(vo);
    }

    // 修改单位管理员
    @Override
    @Transactional
    public OrgUserVo modifyUnitAdmin(OrgUserVo vo) {
        vo.setType(MultiOrgUserAccount.TYPE_UNIT_ADMIN);
        OrgUserVo oldUser = this.getUser(vo.getUuid());
        vo.setMainJobId(oldUser.getMainJobId());
        vo.setOtherJobIds(oldUser.getOtherJobIds());
        vo.setRelationList(oldUser.getRelationList());

        vo.setEmployeeNumber(oldUser.getEmployeeNumber());

        String roleUuids = oldUser.getRoleUuids();
        if (StringUtils.isNotBlank(roleUuids)) {
            List<String> dbRoleIds = Lists.newArrayList(roleUuids.split(";"));
            List<String> roleIds = securityApiFacade.queryRoleIdByCurrentUserUnitId();
            dbRoleIds.removeAll(roleIds);

            if (dbRoleIds.size() > 0) {
                String roleStr = vo.getRoleUuids() == null ? "" : (vo.getRoleUuids() + ";");
                roleStr = roleStr + StringUtils.join(dbRoleIds, ";");
                vo.setRoleUuids(roleStr);
            }
        }
        return modifyUser(vo);
    }

    // 获取单位管理员，如果有多个，取第一个
    @Override
    public OrgUserVo getUnitAdmin(String unitId) {
        List<MultiOrgUserAccount> objs = this.multiOrgUserAccountService.queryAllAdminIdsBySystemUnitId(unitId);
        if (CollectionUtils.isNotEmpty(objs)) {
            return this.getUser(objs.get(0).getUuid());
        }
        return null;
    }

    @Override
    public void recomputeUserWorkInfoByUnit(String unitId) {
        Stopwatch w1 = Stopwatch.createStarted();
        // 先删除该单位的所有用户
        // this.multiOrgUserWorkInfoService.deleteAllUserByUnit(unitId);
        w1.stop();
        logger.info("recomputeUserWorkInfoByUnit.删除单位内所有用户, 耗时=" + w1.toString());
        // 然后获取该单位的所有用户的职位信息
        List<MultiOrgUserAccount> allUser = this.queryAllAccountOfUnit(unitId);
        if (CollectionUtils.isNotEmpty(allUser)) {
            List<MultiOrgUserWorkInfo> list = Lists.newArrayList();
            w1.start();
            for (MultiOrgUserAccount a : allUser) {
                ArrayList<OrgUserJobDto> job = this.getUserJob(a);
                if (CollectionUtils.isNotEmpty(job)) {
                    Set<String> deptIds = Sets.newHashSet();
                    Set<String> jobIds = Sets.newHashSet();
                    Set<String> eleIdPaths = Sets.newHashSet();
                    for (OrgUserJobDto jobDto : job) {
                        jobIds.add(jobDto.getOrgTreeNodeDto().getEleId());
                        eleIdPaths.add(jobDto.getOrgTreeNodeDto().getEleIdPath());
                        String deptId = jobDto.getOrgTreeNodeDto().getDeptId();
                        if (StringUtils.isNotBlank(deptId)) {
                            deptIds.add(deptId);
                        }
                    }
                    MultiOrgUserWorkInfo workInfo = new MultiOrgUserWorkInfo();
                    workInfo.setUserId(a.getId());
                    List<MultiOrgUserWorkInfo> workInfos = multiOrgUserWorkInfoService.listByEntity(workInfo);
                    if (CollectionUtils.isNotEmpty(workInfos)) {
                        workInfo = workInfos.get(0);
                    }
                    workInfo.setDeptIds(StringUtils.join(deptIds, ";"));
                    workInfo.setJobIds(StringUtils.join(jobIds, ";"));
                    workInfo.setEleIdPaths(StringUtils.join(eleIdPaths, ";"));
                    workInfo.setSystemUnitId(unitId);
                    list.add(workInfo);
                    if (list.size() == 100) {// 100个保存一批次
                        multiOrgUserWorkInfoService.saveAll(list);
                        list.clear();
                    }
                }
            }
            w1.stop();
            logger.info("recomputeUserWorkInfoByUnit 获取所有用户职位, 耗时=" + w1.toString());
            w1.start();
            // 批量添加
            multiOrgUserWorkInfoService.saveAll(list);
            w1.stop();
            logger.info("recomputeUserWorkInfoByUnit 批量插入用户职位, 耗时=" + w1.toString());
        }
    }

    @Override
    public void recomputeUserWorkInfoByEleId(String orgVersionId, String eleId) {
        MultiOrgUserTreeNode userTreeEntity = new MultiOrgUserTreeNode();
        userTreeEntity.setEleId(eleId);
        userTreeEntity.setOrgVersionId(orgVersionId);
        List<MultiOrgUserTreeNode> userTreeNodes = multiOrgUserTreeNodeService.listByEntity(userTreeEntity);
        for (MultiOrgUserTreeNode userTreeNode : userTreeNodes) {
            String userId = userTreeNode.getUserId();
            MultiOrgUserAccount multiOrgUserAccount = multiOrgUserAccountService.getAccountByUserId(userId);
            if (null == multiOrgUserAccount) {
                continue;
            }
            ArrayList<OrgUserJobDto> job = getUserJob(multiOrgUserAccount);
            if (CollectionUtils.isNotEmpty(job)) {
                Set<String> deptIds = Sets.newHashSet();
                Set<String> jobIds = Sets.newHashSet();
                Set<String> eleIdPaths = Sets.newHashSet();
                for (OrgUserJobDto jobDto : job) {
                    jobIds.add(jobDto.getOrgTreeNodeDto().getEleId());
                    eleIdPaths.add(jobDto.getOrgTreeNodeDto().getEleIdPath());
                    String deptId = jobDto.getOrgTreeNodeDto().getDeptId();
                    if (StringUtils.isNotBlank(deptId)) {
                        deptIds.add(deptId);
                    }
                }
                MultiOrgUserWorkInfo workInfo = new MultiOrgUserWorkInfo();
                workInfo.setUserId(multiOrgUserAccount.getId());
                List<MultiOrgUserWorkInfo> workInfos = multiOrgUserWorkInfoService.listByEntity(workInfo);
                if (CollectionUtils.isNotEmpty(workInfos)) {
                    workInfo = workInfos.get(0);
                }
                workInfo.setDeptIds(StringUtils.join(deptIds, ";"));
                workInfo.setJobIds(StringUtils.join(jobIds, ";"));
                workInfo.setEleIdPaths(StringUtils.join(eleIdPaths, ";"));
                workInfo.setSystemUnitId(multiOrgUserAccount.getSystemUnitId());
                multiOrgUserWorkInfoService.save(workInfo);
            }
        }
    }

    /**
     * 删除一个用户
     *
     * @param a
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteUser(MultiOrgUserAccount a) {
        // 删除账号
        this.multiOrgUserAccountService.deleteAccount(a);
        // 删除用户的基本信息
        this.multiOrgUserInfoService.deleteUser(a.getId());
        // 移除头像对应的图片
        mongoFileService.popAllFilesFromFolder(getUserPhotoFoloder(a.getId()));

        // 删除用户的冗余工作信息
        this.multiOrgUserWorkInfoService.deleteUser(a.getId());
        // 删除用户的组织信息
        this.multiOrgUserTreeNodeService.deleteUser(a.getId());
        // 删除用户的角色信息
        this.multiOrgUserRoleService.deleteRoleListOfUser(a.getId());
        // 角色信息变更 ，需要发布一下，不管是删除，还是新增，或是修改，对应的角色都需要发布下
        List<MultiOrgUserRole> oldRoles = this.multiOrgUserRoleService.queryRoleListOfUser(a.getId());
        if (oldRoles != null) {
            for (MultiOrgUserRole r : oldRoles) {
                roleFacade.publishRoleUpdatedEvent(r.getRoleUuid());
            }
        }
        // 删除用户的群组信息
        this.multiOrgGroupMemberService.deleteMemeber(a.getId());

        this.userInfoFacadeService.deleteByLoginName(a.getLoginName());
    }

    @Override
    public void addRoleListOfUser(String userId, String roleUuid) {
        this.multiOrgUserRoleService.addRoleListOfUser(userId, roleUuid);
    }

    @Override
    public int countUserByJob(Map<String, Object> params) {
        return this.multiOrgUserWorkInfoService.countUserByJob(params);
    }

    @Override
    public String getUserPhoto(String userId) {
        return this.multiOrgUserInfoService.getUserPhoto(userId);
    }

    @Override
    public UserNode queryUserNode(String userId) {
        if (StringUtils.isEmpty(userId)) {
            return null;
        }
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("userId", userId);
        StringBuilder hqlSb = new StringBuilder(
                "select a.id as id,a.userName as name,a.userNamePy as namePy,b.sex as iconSkin from "
                        + "MultiOrgUserAccount a,MultiOrgUserInfo b " + "where  a.id = b.userId and a.id = :userId ");
        List<UserNode> userNodes = multiOrgUserInfoService.listItemHqlQuery(hqlSb.toString(), UserNode.class, query);
        if (userNodes.size() > 0) {
            return userNodes.get(0);
        }
        return null;
    }

    @Override
    public Map<String, UserJob> gerUserJob(String userId) {
        List<String> userIds = new ArrayList<>();
        userIds.add(userId);
        return multiOrgUserTreeNodeService.gerUserJob(userIds);
    }

    @Override
    public List<MultiOrgElementRole> getElementRoles(String eleId) {
        List<MultiOrgElementRole> elementRoleList = this.multiOrgElementRoleService.queryRoleListOfElement(eleId);
        return elementRoleList;
    }

    @Override
    public MultiOrgUserWorkInfo getMultiOrgUserWorkInfo(String userId) {
        MultiOrgUserWorkInfo multiOrgUserWorkInfo = this.multiOrgUserWorkInfoService.getUserWorkInfo(userId);
        return multiOrgUserWorkInfo;
    }

    @Override
    public MultiOrgUserWorkInfo saveMultiOrgUserWorkInfo(MultiOrgUserWorkInfo multiOrgUserWorkInfo) {
        this.multiOrgUserWorkInfoService.save(multiOrgUserWorkInfo);
        return multiOrgUserWorkInfo;
    }

    @Override
    public OrgUserVo getSimpleUserInfoById(String userId) {
        if (MultiOrgUserAccount.PT_ACCOUNT_ID.equals(userId)) {
            // 平台管理员账号
            return orgApiFacade.getPTAdmin();
        } else {
            MultiOrgUserInfo userInfo = multiOrgUserInfoService.getByUserId(userId);
            OrgUserVo vo = new OrgUserVo();
            MultiOrgUserAccount account = this.multiOrgUserAccountService.getAccountByUserId(userId);
            if (account != null) {
                // 设置账号信息
                BeanUtils.copyProperties(account, vo);
            }
            if (userInfo != null) {
                // 设置个人信息
                BeanUtils.copyProperties(userInfo, vo, IdEntity.BASE_FIELDS);
                return vo;
            } else {
                return null;
            }
        }

    }

    @Override
    public void recomputeUserWorkInfoByVersions(String fromVersionId, String toVersionId) {
        StopWatch timer = new StopWatch("recomputeUserWorkInfoByVersions");
        timer.start("UPDATE");
        if (StringUtils.isNotBlank(fromVersionId)) {
            // 查询旧版本的组织人员工作节点
            List<String> userIdList = multiOrgUserTreeNodeService.listUserIdsByVersionId(fromVersionId);
            logger.info("组织版本号[" + fromVersionId + "->" + toVersionId + "]，更新用户数=[{}]", userIdList.size());
            if (CollectionUtils.isNotEmpty(userIdList)) {
                for (String userId : userIdList) {
                    MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService.getUserWorkInfo(userId);
                    if (workInfo != null && StringUtils.isNotBlank(workInfo.getEleIdPaths())
                            && workInfo.getEleIdPaths().indexOf(fromVersionId) != -1) {
                        String eleIdPaths = workInfo.getEleIdPaths();
                        String[] paths = eleIdPaths.split(Separator.SEMICOLON.getValue());
                        Set<String> jobIds = Sets.newLinkedHashSet();
                        Set<String> deptIds = Sets.newLinkedHashSet();
                        Set<String> eleIdPathSet = Sets.newLinkedHashSet();
                        for (int i = 0, len = paths.length; i < len; i++) {
                            String[] pparts = paths[i].split(Separator.SLASH.getValue());
                            if (paths[i].startsWith(fromVersionId)) {// 更新掉旧版本的职位路径
                                if (toVersionId != null) {
                                    // 查询新版本下的所有有效职位
                                    List<MultiOrgUserTreeNode> userTreeNodeList = multiOrgUserTreeNodeService
                                            .queryUserJobByOrgVersion(workInfo.getUserId(), toVersionId);// 查询目标版本下的有效职位节点
                                    if (CollectionUtils.isNotEmpty(userTreeNodeList)) {
                                        for (MultiOrgUserTreeNode userTreeNode : userTreeNodeList) {
                                            MultiOrgTreeNode jobTreeNode = multiOrgTreeNodeService.queryByVerIdEleId(
                                                    userTreeNode.getOrgVersionId(), userTreeNode.getEleId());
                                            if (jobTreeNode != null) {
                                                eleIdPathSet.add(jobTreeNode.getEleIdPath());
                                                jobIds.add(jobTreeNode.getEleId());
                                                deptIds.add(MultiOrgTreeNode.getNearestEleIdByType(
                                                        jobTreeNode.getEleIdPath(), IdPrefix.DEPARTMENT.getValue()));
                                            }
                                        }
                                    }
                                }
                            } else {
                                eleIdPathSet.add(paths[i]);
                                jobIds.add(pparts[pparts.length - 1]);
                                deptIds.add(MultiOrgTreeNode.getNearestEleIdByType(paths[i],
                                        IdPrefix.DEPARTMENT.getValue()));
                            }
                        }

                        workInfo.setEleIdPaths(StringUtils.join(eleIdPathSet, Separator.SEMICOLON.getValue()));
                        workInfo.setJobIds(StringUtils.join(jobIds, Separator.SEMICOLON.getValue()));
                        workInfo.setDeptIds(StringUtils.join(deptIds, Separator.SEMICOLON.getValue()));
                        multiOrgUserWorkInfoService.save(workInfo);
                    }

                }
            }

        } else if (StringUtils.isBlank(fromVersionId) && StringUtils.isNotBlank(toVersionId)) { // 激活版本号
            List<MultiOrgUserTreeNode> userTreeNodeList = multiOrgUserTreeNodeService
                    .queryAllNodeByVersionId(toVersionId);
            logger.info("激活组织版本号[" + toVersionId + "]，更新用户的工作信息数=[{}]", userTreeNodeList.size());
            if (CollectionUtils.isNotEmpty(userTreeNodeList)) {
                for (MultiOrgUserTreeNode userTreeNode : userTreeNodeList) {
                    MultiOrgUserWorkInfo workInfo = multiOrgUserWorkInfoService
                            .getUserWorkInfo(userTreeNode.getUserId());
                    if (workInfo == null || workInfo.getEleIdPaths() == null
                            || workInfo.getEleIdPaths().indexOf(workInfo.getEleIdPaths()) != -1) {
                        continue;
                    }
                    MultiOrgTreeNode jobTreeNode = multiOrgTreeNodeService
                            .queryByVerIdEleId(userTreeNode.getOrgVersionId(), userTreeNode.getEleId());
                    if (jobTreeNode == null) {
                        continue;
                    }
                    workInfo.setEleIdPaths(StringUtils.defaultIfBlank(workInfo.getEleIdPaths(), "") + ";"
                            + jobTreeNode.getEleIdPath());
                    workInfo.setJobIds(
                            StringUtils.defaultIfBlank(workInfo.getJobIds(), "") + ";" + jobTreeNode.getEleId());
                    workInfo.setDeptIds(StringUtils.defaultIfBlank(workInfo.getDeptIds(), "") + ";" + MultiOrgTreeNode
                            .getNearestEleIdByType(jobTreeNode.getEleIdPath(), IdPrefix.DEPARTMENT.getValue()));
                    multiOrgUserWorkInfoService.save(workInfo);
                }

            }
        }
        timer.stop();
        logger.info("组织版本号[" + fromVersionId + "->" + toVersionId + "]，更新用户的工作信息，耗时详情：{}", timer.prettyPrint());

    }

    @Override
    public HashMap<String, String> getUnforbiddenUserIdNames(List<String> userIds) {
        return this.multiOrgUserAccountService.getUnforbiddenUserIdNames(userIds);
    }

    @Override
    public void resetPwdErrorNumber(String userUuid) {
        try {
            this.multiOrgUserAccountService.pwdUnlockAccount(userUuid);
        } catch (Exception e) {
            logger.error("重置密码错误次数 resetPwdErrorNumber 异常：", e);
            throw new BusinessException("网络异常");
        }

    }

    @Override
    public List<MultiOrgUserAccount> getAccountByUsername(String username) {
        return this.multiOrgUserAccountService.getAccountByUsername(username);
    }

    @Override
    public List<MultiOrgUserAccount> getAccountsByUserIds(List<String> ids) {
        return this.multiOrgUserAccountService.getAccountsByUserIds(ids);
    }

    @Override
    public Map<String, OrgUserVo> getUserAccoutVoWithMainJobByIds(List<String> userids) {
        List<MultiOrgUserAccount> accountList = this.multiOrgUserAccountService.getAccountsByUserIds(userids);
        List<MultiOrgUserWorkInfo> workInfos = this.multiOrgUserWorkInfoService.getUserWorkInfosByUserIds(userids);
        Map<String, Set<String>> userJobIds = this.multiOrgUserTreeNodeService.getMainUserJobIdsIgnoreVersion(userids);
        final Set<String> elementIds = Sets.newHashSet();
        ImmutableMap<String, MultiOrgUserWorkInfo> workInfoMap = Maps.uniqueIndex(workInfos,
                new Function<MultiOrgUserWorkInfo, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable MultiOrgUserWorkInfo workInfo) {
                        if (StringUtils.isNotBlank(workInfo.getEleIdPaths())) {
                            String[] parts = workInfo.getEleIdPaths()
                                    .replaceAll(";", MultiOrgService.PATH_SPLIT_SYSMBOL)
                                    .split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                            elementIds.addAll(Arrays.asList(parts));
                        }
                        return workInfo.getUserId();
                    }
                });

        List<MultiOrgElement> elements = multiOrgElementService.getOrgElementsByIds(Lists.newArrayList(elementIds));
        ImmutableMap<String, MultiOrgElement> multiOrgElementMap = Maps.uniqueIndex(elements,
                new Function<MultiOrgElement, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable MultiOrgElement multiOrgElement) {
                        return multiOrgElement.getId();
                    }
                });
        Map<String, OrgUserVo> result = Maps.newHashMap();
        for (MultiOrgUserAccount account : accountList) {
            OrgUserVo vo = new OrgUserVo();
            org.springframework.beans.BeanUtils.copyProperties(account, vo);
            result.put(account.getId(), vo);
            MultiOrgUserWorkInfo wk = workInfoMap.get(account.getId());
            if (wk != null && StringUtils.isNotBlank(wk.getEleIdPaths())) {
                String[] parts = wk.getEleIdPaths().split(";");
                for (String p : parts) {
                    Set<String> jobids = userJobIds.get(account.getId());
                    for (String j : jobids) {
                        if (p.indexOf(j) != -1) {
                            // 找到主职位
                            String[] nodes = p.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                            List<String> labels = Lists.newArrayListWithCapacity(nodes.length);
                            List<String> deptIdPath = Lists.newArrayListWithCapacity(nodes.length);
                            List<String> deptNamePath = Lists.newArrayListWithCapacity(nodes.length);
                            for (String n : nodes) {
                                MultiOrgElement element = multiOrgElementMap.get(n);
                                if (element != null) {
                                    labels.add(element.getName());
                                    deptIdPath.add(element.getId());
                                    deptNamePath.add(element.getName());
                                    if (n.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                                        vo.setMainDepartmentName(element.getName());
                                        vo.setMainDepartmentId(nodes[0] + MultiOrgService.PATH_SPLIT_SYSMBOL + n);
                                        vo.setMainDepartmentIdPath(
                                                StringUtils.join(deptIdPath, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                    }
                                }
                            }
                            vo.setMainJobName(labels.get(labels.size() - 1));
                            vo.setMainJobIdPath(StringUtils.join(deptIdPath, MultiOrgService.PATH_SPLIT_SYSMBOL));
                            vo.setMainJobNamePath(StringUtils.join(labels, MultiOrgService.PATH_SPLIT_SYSMBOL));
                            vo.setMainDepartmentNamePath(StringUtils.join(deptNamePath.subList(1, deptNamePath.size()),
                                    MultiOrgService.PATH_SPLIT_SYSMBOL));
                            break;
                        }
                    }
                    if (StringUtils.isNotBlank(vo.getMainJobName())) {
                        break;
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, OrgUserVo> getUserAccoutVoWithAllJobByIds(List<String> userids) {
        // 其他职位读取
        List<MultiOrgUserAccount> accountList = this.multiOrgUserAccountService.getAccountsByUserIds(userids);
        List<MultiOrgUserWorkInfo> workInfos = this.multiOrgUserWorkInfoService.getUserWorkInfosByUserIds(userids);
        Map<String, List<OrgUserJobDto>> userJobs = this.multiOrgUserTreeNodeService
                .getAllUserJobIdsIgnoreVersion(userids);
        final Set<String> elementIds = Sets.newHashSet();
        ImmutableMap<String, MultiOrgUserWorkInfo> workInfoMap = Maps.uniqueIndex(workInfos,
                new Function<MultiOrgUserWorkInfo, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable MultiOrgUserWorkInfo workInfo) {
                        if (StringUtils.isNotBlank(workInfo.getEleIdPaths())) {
                            String[] parts = workInfo.getEleIdPaths()
                                    .replaceAll(";", MultiOrgService.PATH_SPLIT_SYSMBOL)
                                    .split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                            elementIds.addAll(Arrays.asList(parts));
                        }
                        return workInfo.getUserId();
                    }
                });

        // 所有的节点信息列表
        List<MultiOrgElement> elements = multiOrgElementService.getOrgElementsByIds(Lists.newArrayList(elementIds));
        ImmutableMap<String, MultiOrgElement> multiOrgElementMap = Maps.uniqueIndex(elements,
                new Function<MultiOrgElement, String>() {
                    @Nullable
                    @Override
                    public String apply(@Nullable MultiOrgElement multiOrgElement) {
                        return multiOrgElement.getId();
                    }
                });
        Map<String, OrgUserVo> result = Maps.newHashMap();
        for (MultiOrgUserAccount account : accountList) {
            OrgUserVo vo = new OrgUserVo();
            org.springframework.beans.BeanUtils.copyProperties(account, vo);
            result.put(account.getId(), vo);
            MultiOrgUserWorkInfo wk = workInfoMap.get(account.getId());
            if (wk != null && StringUtils.isNotBlank(wk.getEleIdPaths())) {
                String[] parts = wk.getEleIdPaths().split(";");
                for (String p : parts) {
                    List<OrgUserJobDto> jobs = userJobs.get(account.getId());
                    for (OrgUserJobDto job : jobs) {
                        if (job.getIsMain() == 1) {
                            if (p.indexOf(job.getOrgTreeNodeDto().getEleId()) != -1) {
                                // 找到主职位
                                String[] nodes = p.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                                List<String> labels = Lists.newArrayListWithCapacity(nodes.length);
                                List<String> deptIdPath = Lists.newArrayListWithCapacity(nodes.length);
                                List<String> deptNamePath = Lists.newArrayListWithCapacity(nodes.length);
                                for (String n : nodes) {
                                    MultiOrgElement element = multiOrgElementMap.get(n);
                                    if (element != null) {
                                        labels.add(element.getName());
                                        deptIdPath.add(element.getId());
                                        deptNamePath.add(element.getName());
                                        if (n.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                                            vo.setMainDepartmentName(element.getName());
                                            vo.setMainDepartmentId(nodes[0] + MultiOrgService.PATH_SPLIT_SYSMBOL + n);
                                            vo.setMainDepartmentIdPath(
                                                    StringUtils.join(deptIdPath, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                        }
                                    }
                                }
                                if (labels.size() > 0) {
                                    vo.setMainJobName(labels.get(labels.size() - 1));
                                    vo.setMainJobIdPath(
                                            StringUtils.join(deptIdPath, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                    vo.setMainJobNamePath(StringUtils.join(labels, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                    vo.setMainDepartmentNamePath(
                                            StringUtils.join(deptNamePath.subList(1, deptNamePath.size()),
                                                    MultiOrgService.PATH_SPLIT_SYSMBOL));
                                }

                                continue;
                            }
                        } else {
                            // 其他职位
                            if (p.indexOf(job.getOrgTreeNodeDto().getEleId()) != -1) {
                                String[] nodes = p.split(MultiOrgService.PATH_SPLIT_SYSMBOL);
                                List<String> labels = Lists.newArrayListWithCapacity(nodes.length);
                                List<String> deptIdPath = Lists.newArrayListWithCapacity(nodes.length);
                                List<String> deptNamePath = Lists.newArrayListWithCapacity(nodes.length);
                                for (String n : nodes) {
                                    MultiOrgElement element = multiOrgElementMap.get(n);
                                    if (element != null) {
                                        labels.add(element.getName());
                                        deptIdPath.add(element.getId());
                                        deptNamePath.add(element.getName());
                                    }
                                }
                                if (StringUtils.isBlank(vo.getOtherJobNames())) {
                                    if (labels.size() > 0) {
                                        vo.setOtherJobNames(labels.get(labels.size() - 1));
                                    }

                                } else {
                                    if (labels.size() > 0) {
                                        vo.setOtherJobNames(
                                                vo.getOtherJobNames() + ";" + labels.get(labels.size() - 1));
                                    } else {
                                        vo.setOtherJobNames(vo.getOtherJobNames());
                                    }
                                }

                                if (StringUtils.isBlank(vo.getOtherJobIdPaths())) {
                                    vo.setOtherJobIdPaths(
                                            StringUtils.join(deptIdPath, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                } else {
                                    vo.setOtherJobIdPaths(vo.getOtherJobIdPaths() + ";"
                                            + StringUtils.join(deptIdPath, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                }

                                if (StringUtils.isBlank(vo.getOtherJobNamePaths())) {
                                    vo.setOtherJobNamePaths(
                                            StringUtils.join(labels, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                } else {
                                    vo.setOtherJobNamePaths(vo.getOtherJobNamePaths() + ";"
                                            + StringUtils.join(labels, MultiOrgService.PATH_SPLIT_SYSMBOL));
                                }
                            }
                            continue;
                        }

                    }

                }
            }
        }
        return result;
    }

    @Transactional
    public void updateIdNumber(String idNumber, String userId) {
        MultiOrgUserInfo userInfo = this.multiOrgUserInfoService.getByUserId(userId);
        if (userInfo != null) {
            userInfo.setIdNumber(idNumber);
            this.multiOrgUserInfoService.save(userInfo);
        }
    }

    @Override
    public MultiOrgUserInfo getUserByIdNumber(String idNumber) {
        return this.multiOrgUserInfoService.getByIdNumber(idNumber);
    }

    @Override
    public OrgUserVo getLoginUserInfo(String loginName, String loginNameHashAlgorithmCode) {
        Stopwatch timer = Stopwatch.createStarted();
        try {
            Assert.isTrue(StringUtils.isNotBlank(loginName), "loginName不能为空");
            Assert.isTrue(StringUtils.isNotBlank(loginNameHashAlgorithmCode), "loginNameHashAlgorithmCode不能为空");
            MultiOrgUserAccount account = this.multiOrgUserAccountService.getUserByLoginNameIgnoreCase(loginName,
                    loginNameHashAlgorithmCode);
            if (account == null) {
                return null;
            }

            OrgUserVo vo = new OrgUserVo();
            // 设置账号信息
            BeanUtils.copyProperties(account, vo);
            // 设置个人信息
            MultiOrgUserInfo userInfo = this.multiOrgUserInfoService.getByUserId(account.getId());
            BeanUtils.copyProperties(userInfo, vo, IdEntity.BASE_FIELDS);

            // 获取职位信息
            MultiOrgUserWorkInfo workInfo = this.multiOrgUserWorkInfoService.getUserWorkInfo(account.getId());
            if (workInfo != null && StringUtils.isNotBlank(workInfo.getEleIdPaths())) {
                vo.setOtherJobIdPaths(workInfo.getEleIdPaths());// 登录逻辑不关心哪个是主职，所以直接放该字段
                // 仅查询主职的信息
                String[] jobids = workInfo.getJobIds().split(";");
                String[] jobPaths = workInfo.getEleIdPaths().split(";");
                List<MultiOrgUserTreeNode> jobNodes = this.multiOrgUserTreeNodeService
                        .queryMainJobByJobIdsAndUser(jobids, account.getId());
                if (CollectionUtils.isNotEmpty(jobNodes)) {
                    for (MultiOrgUserTreeNode node : jobNodes) {
                        if (ArrayUtils.indexOf(jobids, node.getEleId()) != -1) {
                            // 主职信息
                            OrgUserJobDto orgUserJobDto = new OrgUserJobDto();
                            orgUserJobDto.setUserId(node.getUserId());
                            orgUserJobDto.setIsMain(1);
                            OrgTreeNodeDto orgTreeNodeDto = new OrgTreeNodeDto();
                            BeanUtils.copyProperties(node, orgTreeNodeDto);
                            orgUserJobDto.setOrgTreeNodeDto(orgTreeNodeDto);
                            vo.setJobList(Lists.newArrayList(orgUserJobDto));
                            for (String p : jobPaths) {
                                if (p.indexOf(node.getEleId()) != -1) {
                                    orgTreeNodeDto.setEleIdPath(p);
                                    // 查询主职部门
                                    MultiOrgElement jobElement = multiOrgService.getOrgElementById(node.getEleId());
                                    if (jobElement != null) {
                                        orgTreeNodeDto.setName(jobElement.getName());
                                    }
                                    String deptNamePath = multiOrgService.getDepartmentNamePathByJobIdPath(p, false);
                                    orgTreeNodeDto.setDeptNamePath(deptNamePath);
                                    break;
                                }
                            }

                            break;
                        }
                    }
                }
            }

            // 获取角色信息
            List<String> roleUuids = this.multiOrgUserRoleService.queryRoleUuidsOfUser(account.getId());
            if (CollectionUtils.isNotEmpty(roleUuids)) {
                vo.setRoleUuids(StringUtils.join(roleUuids, ";"));
            }
            return vo;
        } catch (Exception e) {
            logger.error("获取登录用户信息异常：", e);
        } finally {
            logger.info("获取登录用户信息耗时：{}", timer.stop());
        }
        return null;
    }

    @Override
    public List<MultiOrgGroupMember> queryGroupListByMemberId(String memberId) {
        return this.multiOrgGroupMemberService.queryGroupListByMemberId(memberId);
    }

    @Override
    public Set<String> queryGroupIdsByMemeberId(Set<String> memberIds) {
        return this.multiOrgGroupMemberService.queryGroupIdsByMemeberId(memberIds);
    }

    @Override
    public List<MultiOrgUserAccount> getUserAccountByLoginNameIgnoreCase(String loginName) {
        return multiOrgUserAccountService.getUserAccountByLoginNameIgnoreCase(loginName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, timeout = 30)
    public void loginUserBusinessTransaction(String userUuid) {
        this.updateLastLoginTime(userUuid);
        this.resetPwdErrorNumber(userUuid);
    }

    /**
     * 根据手机号码，列出对应的用户信息
     *
     * @param mobilePhone
     * @return
     */
    @Override
    public List<MultiOrgUserInfo> listUserByMobilePhone(String mobilePhone) {
        return this.multiOrgUserInfoService.listUserByMobilePhone(mobilePhone);
    }

    /**
     * 根据手机号码，获取对应的用户数量
     *
     * @param mobilePhone
     * @return
     */
    @Override
    public long countUserByMobilePhone(String mobilePhone) {
        return multiOrgUserInfoService.countUserByMobilePhone(mobilePhone);
    }
}
