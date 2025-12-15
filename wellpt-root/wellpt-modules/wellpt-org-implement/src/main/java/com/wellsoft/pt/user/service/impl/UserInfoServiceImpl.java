package com.wellsoft.pt.user.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.wellsoft.context.Context;
import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.context.util.sm.SM3Util;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.service.MultiOrgJobDutyService;
import com.wellsoft.pt.multi.org.util.PwdUtils;
import com.wellsoft.pt.org.dao.UserNameI18nDao;
import com.wellsoft.pt.org.dto.UserAcctPasswordRules;
import com.wellsoft.pt.org.entity.OrgSettingEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.entity.OrgUserReportRelationEntity;
import com.wellsoft.pt.org.entity.UserRoleEntity;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.audit.facade.service.OAuth2UserFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.dao.UserInfoDaoImpl;
import com.wellsoft.pt.user.dto.FullInternetUserDto;
import com.wellsoft.pt.user.dto.UserCredentialDto;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.dto.UserExtraInfoDto;
import com.wellsoft.pt.user.entity.*;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.query.UserInfoIdNameQueryItem;
import com.wellsoft.pt.user.service.UserRoleService;
import com.wellsoft.pt.user.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
@Service
public class UserInfoServiceImpl extends AbstractJpaServiceImpl<UserInfoEntity, UserInfoDaoImpl, String>
        implements UserInfoService {
    @Autowired
    UserAccountService userAccountService;
    @Autowired
    UserCredentialService userCredentialService;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private InternetUserInfoService internetUserInfoService;
    @Autowired
    private UserExtraInfoService userExtraInfoService;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    UserRoleService userRoleService;

    @Resource
    private OrgUserService orgUserService;

    @Resource
    private MultiOrgJobDutyService multiOrgJobDutyService;

    @Resource
    private OrgElementRoleMemberService orgElementRoleMemberService;

    @Resource
    private UserInfoExtService userInfoExtService;

    @Resource
    private OrgUserReportRelationService orgUserReportRelationService;

    @Resource
    private OrgElementI18nService orgElementI18nService;

    @Resource
    private UserNameI18nDao userNameI18nDao;
    @Resource
    private OrgSettingService orgSettingService;

    @Override
    @Transactional
    public void addUserInfo(UserDto userDto) {
        UserInfoEntity existUserInfo = this.dao.getOneByFieldEq("loginName", userDto.getLoginName().toLowerCase());
        Assert.isNull(existUserInfo, "已存在的用户账号[" + userDto.getLoginName().toLowerCase() + "]");
        OAuth2UserFacadeService oAuth2UserFacadeService = null;
        if (Context.isOauth2Enable()) {
            try {
                oAuth2UserFacadeService = ApplicationContextHolder.getBean(OAuth2UserFacadeService.class);
            } catch (Exception e) {
            }
            if (oAuth2UserFacadeService != null) {
                Assert.isTrue(!oAuth2UserFacadeService.existUser(userDto.getLoginName()),
                        "oAuth2已存在的用户账号[" + userDto.getLoginName() + "]");
            }
            if (oAuth2UserFacadeService != null) {
                oAuth2UserFacadeService.addUser(new Gson().toJson(userDto));
            }
        }
        try {
            // 用户账号表保存
            UserAccountEntity accountEntity = new UserAccountEntity();
            BeanUtils.copyProperties(userDto, accountEntity);
            accountEntity.setPassword(SM3Util
                    .encrypt(accountEntity.getPassword() + "{" + accountEntity.getLoginName().toLowerCase() + "}"));
            accountEntity.setExtLoginName(userDto.getLoginName());
            accountEntity.setExtLoginType("DEFAULT_OAUTH2_PRINCIPAL");
            userAccountService.save(accountEntity);

            // 通用用户信息表保存
            UserInfoEntity infoEntity = new UserInfoEntity();
            BeanUtils.copyProperties(userDto, infoEntity);
            infoEntity.setAccountUuid(accountEntity.getUuid());
            this.dao.save(infoEntity);

            if (UserTypeEnum.INDIVIDUAL.getName().equals(userDto.getType().getName())
                    || UserTypeEnum.LEGAL_PERSION.getName().equals(userDto.getType().getName())) {
                // 互联网用户保存
                InternetUserInfoEntity internetUserInfoEntity = new InternetUserInfoEntity();
                BeanUtils.copyProperties(userDto, internetUserInfoEntity);
                internetUserInfoEntity.setType(userDto.getType().ordinal());
                internetUserInfoEntity.setAccountUuid(accountEntity.getUuid());
                internetUserInfoService.save(internetUserInfoEntity);

                // 用户信息扩展表保存
                saveUserExtraInfos(userDto.getUserExtraInfoDtos(), accountEntity.getUuid());
            }

            // this.saveUserCredential(userDto.getLoginName(),
            // userDto.getUserCredentialDtos());

        } catch (Exception e) {
            // 异常回滚统一认证服务的账号
            if (Context.isOauth2Enable() && oAuth2UserFacadeService != null) {
                oAuth2UserFacadeService.deleteUser(userDto.getLoginName());
            }
            logger.error("新增用户异常：", e);
            throw new RuntimeException("新增用户异常");
        }

    }

    @Override
    @Transactional
    public String saveUser(UserDto userDto) {
        UserInfoEntity userInfoEntity = null;
        checkUserUnique(userDto);
        if (StringUtils.isBlank(userDto.getUuid())) {
            userInfoEntity = this.dao.getOneByFieldEq("loginName", userDto.getLoginName().toLowerCase());
            if (userInfoEntity != null) {
                throw new BusinessException("用户账号已存在");
            }
            userInfoEntity = new UserInfoEntity();
            BeanUtils.copyProperties(userDto, userInfoEntity);
            userInfoEntity.setUserId(IdPrefix.USER.getValue() + Separator.UNDERLINE.getValue() + SnowFlake.getTinyId());
        } else {
            userInfoEntity = getOne(userDto.getUuid());
            BeanUtils.copyProperties(userDto, userInfoEntity,
                    ArrayUtils.addAll(userInfoEntity.BASE_FIELDS,
                            StringUtils.isBlank(userDto.getUserName()) ?
                                    new String[]{"userId", "pinYin", "accountUuid", "type", "userName"} : new String[]{"userId", "pinYin", "accountUuid", "type"}));
        }
        // 创建或者更新账号
        UserAccountEntity accountEntity = StringUtils.isBlank(userDto.getUuid()) ? new UserAccountEntity()
                : userAccountService.getOne(userInfoEntity.getAccountUuid());
        if (StringUtils.isBlank(userDto.getUuid())) {
            accountEntity.setPassword(userDto.getPassword());
            accountEntity.setLoginName(userInfoEntity.getLoginName());
            accountEntity.setPassword(PwdUtils.createSm3Password(userInfoEntity.getLoginName().toLowerCase(), accountEntity.getPassword()));
            accountEntity.setPasswordModifiedByUser(SpringSecurityUtils.getCurrentLoginName().equals(accountEntity.getLoginName()));
            accountEntity.setModifyPasswordTime(new Date());
            UserAcctPasswordRules rules = userAccountService.getUserAcctPasswordRules();
            if (rules.enabled(UserAcctPasswordRules.RuleKey.enablePasswordTimeLimit.name())) {
                accountEntity.setPasswordExpiredTime(DateUtils.addDays(accountEntity.getModifyPasswordTime(), rules.getInt(UserAcctPasswordRules.RuleKey.passwordLimitValidDay.name())));
            }
        }
        accountEntity.setEmail(userInfoEntity.getMail());
        accountEntity.setTel(userInfoEntity.getCeilPhoneNumber());
        accountEntity.setType(userDto.getAccountType() != null ? userDto.getAccountType() : (StringUtils.isNotBlank(accountEntity.getUuid()) && accountEntity.getType() != null ? accountEntity.getType() : UserAccountEntity.Type.BUSINESS_USER));
        userAccountService.save(accountEntity);
        userInfoEntity.setAccountUuid(accountEntity.getUuid());
        if (StringUtils.isBlank(userDto.getPinYin())) {
            userInfoEntity.setPinYin(PinyinUtil.getPinYinMulti(userInfoEntity.getUserName().trim(), true));
        }

        save(userInfoEntity);


        if (CollectionUtils.isNotEmpty(userDto.getUserNameI18ns())) {
            for (UserNameI18nEntity e : userDto.getUserNameI18ns()) {
                e.setUserUuid(userInfoEntity.getUuid());
                e.setUserId(userInfoEntity.getUserId());
            }
            userNameI18nDao.saveAllAfterDelete(userInfoEntity.getUuid(), userDto.getUserNameI18ns());
        } else if (StringUtils.isNotBlank(userInfoEntity.getEnName())) {
            userNameI18nDao.saveOrUpdateUserName(userInfoEntity.getEnName(), userInfoEntity.getUserId(), userInfoEntity.getUuid(), Locale.US.toString());
        }

        // 关联组织元素实例
        if (StringUtils.isBlank(userDto.getUuid())) {
            if (userDto.getBelongToOrgElement() != null) {
                orgUserService.addOrgUser(userInfoEntity.getUserId(), userDto.getOrgVersionUuid(), Lists.newArrayList(userDto.getBelongToOrgElement()), OrgUserEntity.Type.MEMBER_USER);
//                orgVersionUserObjRelaService.addRelaByOrgElementId(userInfoEntity.getUserId(), userDto.getBelongToOrgElement(), userDto.getOrgVersionUuid(), OrgVersionUserObjRelaEntity.Type.BELONG_TO);
            } else if (userDto.getOrgVersionUuid() != null) {
                orgUserService.addOrgUser(userInfoEntity.getUserId(), userDto.getOrgVersionUuid(), null, OrgUserEntity.Type.MEMBER_USER);
//                orgVersionUserObjRelaService.addRelaByOrgVersionUuid(userInfoEntity.getUserId(), userDto.getOrgVersionUuid());
            }
        }
        if (userDto.getOrgVersionUuid() != null) {

            orgUserService.saveOrgUser(userDto.getOrgUsers(), userDto.getOrgVersionUuid(), userInfoEntity.getUserId());
//        orgUserService.saveUserJobs(userInfoEntity.getUserId(), userDto.getOrgVersionUuid(), StringUtils.isNotBlank(userDto.getMainJobId()) ? Lists.newArrayList(userDto.getMainJobId()) : null, OrgUserEntity.Type.PRIMARY_JOB_USER);
//        orgUserService.saveUserJobs(userInfoEntity.getUserId(), userDto.getOrgVersionUuid(), userDto.getOtherJobIds(), OrgUserEntity.Type.SECONDARY_JOB_USER);

            orgUserReportRelationService.saveUserReportRelation(userInfoEntity.getUserId(), userDto.getOrgReportTos(), userDto.getOrgVersionUuid());

        }

        if (CollectionUtils.isNotEmpty(userDto.getUserInfoExts())) {
            for (UserInfoExtEntity ext : userDto.getUserInfoExts()) {
                ext.setUserUuid(userInfoEntity.getUuid());
                userInfoExtService.saveUserInfoExt(ext);
            }
        }
        if (userDto.getRoleUuids() == null) {
            return userInfoEntity.getUserId();
        }
        boolean roleChange = StringUtils.isBlank(userDto.getUuid()) && CollectionUtils.isNotEmpty(userDto.getRoleUuids());
        if (StringUtils.isNotBlank(userDto.getUuid())) {
            List<String> roles = userRoleService.getRolesByUserUuid(userDto.getUuid());
            roleChange = !ListUtils.subtract(roles, userDto.getRoleUuids()).isEmpty()
                    || !ListUtils.subtract(userDto.getRoleUuids(), roles).isEmpty();
        }
        if (roleChange) {
            userRoleService.deleteByUserUuid(userInfoEntity.getUuid());
            userRoleService.saveUserRole(userDto.getRoleUuids(), userInfoEntity.getUuid());
            ApplicationContextHolder.getBean(SecurityMetadataSourceService.class).loadSecurityMetadataSource();
        }
        return userInfoEntity.getUuid();
    }

    private void checkUserUnique(UserDto userDto) {
        // 判断用户数据唯一性
        OrgSettingEntity setExample = new OrgSettingEntity();
        setExample.setCategory("USER_UNIQUE_RULE");
        setExample.setEnable(true);
        setExample.setSystem("PRD_PT");
        setExample.setTenant(SpringSecurityUtils.getCurrentTenantId());
        List<OrgSettingEntity> settingEntity = orgSettingService.listByEntity(setExample);
        if (CollectionUtils.isNotEmpty(settingEntity)) {
            for (OrgSettingEntity entity : settingEntity) {
                if ("TEL_UNIQUE".equalsIgnoreCase(entity.getAttrKey()) && StringUtils.isNotBlank(userDto.getCeilPhoneNumber())) {
                    // 判断移动电话唯一性
                    UserInfoEntity example = new UserInfoEntity();
                    example.setUuid(userDto.getUuid());
                    example.setCeilPhoneNumber(userDto.getCeilPhoneNumber());
                    if (checkUserExist(example)) {
                        throw new RuntimeException("移动电话已被使用");
                    }
                } else if ("USER_ID_NUMBER_UNIQUE".equalsIgnoreCase(entity.getAttrKey()) && CollectionUtils.isNotEmpty(userDto.getUserInfoExts())) {
                    for (UserInfoExtEntity ext : userDto.getUserInfoExts()) {
                        if ("idNumber".equalsIgnoreCase(ext.getAttrKey()) && StringUtils.isNotBlank(ext.getAttrValue())) {
                            UserInfoEntity example = new UserInfoEntity();
                            example.setUuid(userDto.getUuid());
                            example.setIdNumber(ext.getAttrValue());
                            if (checkUserExist(example)) {
                                throw new RuntimeException("身份证号码已被使用");
                            }
                            break;
                        }
                    }
                } else if ("USER_NAME_UNIQUE".equalsIgnoreCase(entity.getAttrKey()) && StringUtils.isNotBlank(userDto.getUserName())) {
                    UserInfoEntity example = new UserInfoEntity();
                    example.setUuid(userDto.getUuid());
                    example.setUserName(userDto.getUserName());
                    if (checkUserExist(example)) {
                        throw new RuntimeException("姓名已被使用");
                    }
                } else if (StringUtils.isNotBlank(entity.getAttrVal())) {
                    Map<String, Object> map = JsonUtils.gson2Object(entity.getAttrVal(), Map.class);
                    if (map.containsKey("fields") && CollectionUtils.isNotEmpty((Collection) map.get("fields"))) {
                        List<String> fields = (List) map.get("fields");
                        UserInfoEntity example = new UserInfoEntity();
                        example.setUuid(userDto.getUuid());
                        for (String field : fields) {
                            Object v = null;
                            if (field.equalsIgnoreCase("idNumber")) {
                                for (UserInfoExtEntity ext : userDto.getUserInfoExts()) {
                                    if ("idNumber".equalsIgnoreCase(ext.getAttrKey()) && StringUtils.isNotBlank(ext.getAttrValue())) {
                                        v = ext.getAttrValue();
                                        break;
                                    }
                                }
                            } else {
                                v = ReflectionUtils.getFieldValue(userDto, field);
                            }
                            if (v == null || StringUtils.isBlank(v.toString())) {
                                return;
                            }
                            ReflectionUtils.setFieldValue(example, field, v);
                        }
                        if (checkUserExist(example)) {
                            Map<String, String> fieldNames = Maps.newHashMap();
                            fieldNames.put("userName", "姓名");
                            fieldNames.put("idNumber", "身份证号码");
                            fieldNames.put("ceilPhoneNumber", "移动电话号码");
                            fieldNames.put("mail", "邮箱");
                            List<String> names = Lists.newArrayList();
                            for (String f : fields) {
                                names.add(fieldNames.get(f));
                            }
                            throw new RuntimeException(StringUtils.join(names, "、") + "已被使用");
                        }

                    }
                }
            }
        }
    }


    @Override
    @Transactional
    public void modifyUserInfo(UserDto userDto) {
        UserInfoEntity userInfoEntity = this.dao.getOneByFieldEq("loginName", userDto.getLoginName().toLowerCase());
        if (userInfoEntity == null) {
            return;// throw new RuntimeException("不存在的用户");
        }
        userInfoEntity.setCeilPhoneNumber(userDto.getCeilPhoneNumber());
        userInfoEntity.setMail(userDto.getMail());
        userInfoEntity.setUserName(userDto.getUserName());
        this.dao.save(userInfoEntity);
        if (UserTypeEnum.INDIVIDUAL.getName().equals(userDto.getType().getName())
                || UserTypeEnum.LEGAL_PERSION.getName().equals(userDto.getType().getName())) {
            // 互联网用户表修改保存
            InternetUserInfoEntity internetUserInfoEntity = internetUserInfoService
                    .getDetailByAccountUuid(userInfoEntity.getAccountUuid());
            internetUserInfoEntity.setLicenseNumber(userDto.getLicenseNumber());
            internetUserInfoEntity.setLicenseType(userDto.getLicenseType());
            internetUserInfoEntity.setType(userDto.getType().ordinal());
            internetUserInfoEntity.setUnitName(userDto.getUnitName());
            internetUserInfoEntity.setAccountUuid(userInfoEntity.getAccountUuid());
            internetUserInfoService.save(internetUserInfoEntity);

            // 用户信息扩展表保存
            userExtraInfoService.deleteByAccountUuid(userInfoEntity.getAccountUuid());
            saveUserExtraInfos(userDto.getUserExtraInfoDtos(), userInfoEntity.getUuid());
        }

        // this.userCredentialService.deleteByLoginName(userDto.getLoginName());
        // this.saveUserCredential(userDto.getLoginName(),
        // userDto.getUserCredentialDtos());
    }

    @Override
    public FullInternetUserDto getFullInternetUserByAccountUuid(String accountUuid) {
        FullInternetUserDto fullInternetUserDto = new FullInternetUserDto();
        UserAccountEntity userAccountEntity = userAccountService.getOne(accountUuid);
        if (userAccountEntity == null) {
            throw new RuntimeException("不存在的用户");
        }
        BeanUtils.copyProperties(userAccountEntity, fullInternetUserDto);
        fullInternetUserDto.setPassword("");
        UserInfoEntity userInfoEntity = getUserInfoByAcctUuid(accountUuid);
        BeanUtils.copyProperties(userInfoEntity, fullInternetUserDto);

        InternetUserInfoEntity internetUserInfoEntity = internetUserInfoService.getDetailByAccountUuid(accountUuid);
        BeanUtils.copyProperties(internetUserInfoEntity, fullInternetUserDto);

        List<UserExtraInfoEntity> userExtraInfoEntities = userExtraInfoService.getListByAccountUuid(accountUuid);
        List<UserExtraInfoDto> userExtraInfoDtos = com.wellsoft.pt.jpa.util.BeanUtils
                .copyCollection(userExtraInfoEntities, UserExtraInfoDto.class);
        fullInternetUserDto.setUserExtraInfoDtos(userExtraInfoDtos);
        return fullInternetUserDto;
    }

    private void saveUserCredential(String loginName, Set<UserCredentialDto> userCredentialDtoSet) {
        if (CollectionUtils.isNotEmpty(userCredentialDtoSet)) {
            List<UserCredentialEntity> userCredentialEntities = Lists.newArrayList();
            for (UserCredentialDto credentialDto : userCredentialDtoSet) {
                userCredentialEntities
                        .add(new UserCredentialEntity(loginName, credentialDto.getType(), credentialDto.getCode()));
            }
            this.userCredentialService.saveAll(userCredentialEntities);
        }
    }

    @Override
    public UserInfoEntity getUserInfoByAcctUuid(String uuid) {
        return this.dao.getOneByFieldEq("accountUuid", uuid);
    }

    @Override
    public UserInfoEntity getByLoginName(String loginName) {
        return this.dao.getOneByFieldEq("loginName", loginName);
    }

    @Override
    public List<UserInfoEntity> listByLoginNames(List<String> loginNames) {
        List<Object> loginNamesList = Lists.newArrayList();
        CollectionUtils.addAll(loginNamesList, loginNames.iterator());
        return dao.listByFieldInValues("loginName", loginNamesList);
    }

    @Override
    @Transactional
    public void modifyUserPassword(String loginName, String newPassword, String oldPassword) {
        OAuth2UserFacadeService oAuth2UserFacadeService = null;
        if (Context.isOauth2Enable()) {
            try {
                oAuth2UserFacadeService = ApplicationContextHolder.getBean(OAuth2UserFacadeService.class);
            } catch (Exception e) {
            }
            if (oAuth2UserFacadeService != null) {
                oAuth2UserFacadeService.modifyPassword(loginName, newPassword, oldPassword);
            }
        }
        this.userAccountService.modifyUserPassword(loginName, newPassword, oldPassword);

    }

    @Override
    @Transactional
    public void expiredUser(String loginName) {
        OAuth2UserFacadeService oAuth2UserFacadeService = null;
        if (Context.isOauth2Enable()) {
            try {
                oAuth2UserFacadeService = ApplicationContextHolder.getBean(OAuth2UserFacadeService.class);
            } catch (Exception e) {
            }
            if (oAuth2UserFacadeService != null) {
                oAuth2UserFacadeService.expiredUser(loginName);
            }
        }
        this.userAccountService.expiredUser(loginName);

        List<Object> allPricipals = sessionRegistry.getAllPrincipals();
        if (CollectionUtils.isNotEmpty(allPricipals)) {
            for (Object u : allPricipals) {
                if (u instanceof UserDetails && ((UserDetails) u).getLoginName().equalsIgnoreCase(loginName)) {
                    List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(u, true);
                    if (CollectionUtils.isNotEmpty(sessionInformations)) {
                        for (SessionInformation si : sessionInformations) {
                            si.expireNow();
                        }
                    }
                }
            }
        }

    }

    @Override
    public boolean isExist(String loginName) {
        UserAccountEntity userAccountEntity = userAccountService.getByLoginName(loginName);
        if (null != userAccountEntity) {
            return true;
        }
        UserInfoEntity existUserInfo = this.dao.getOneByFieldEq("loginName", loginName);
        return null != existUserInfo;
    }

    @Override
    public Map<String, String> getInternetUserNamesByLoginNames(String[] loginNames) {
        List<UserInfoEntity> userInfoEntities = this.dao.listByFieldInValues("loginName", Lists.newArrayList(Arrays.asList(loginNames).iterator()));
        Map<String, String> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(userInfoEntities)) {
            for (UserInfoEntity userInfoEntity : userInfoEntities) {
                if ((UserTypeEnum.INDIVIDUAL.equals(userInfoEntity.getType())
                        || UserTypeEnum.LEGAL_PERSION.equals(userInfoEntity.getType()))) {
                    map.put(userInfoEntity.getLoginName(), userInfoEntity.getUserName());
                }
            }
        }
        return map;
    }

    @Override
    public Map<String, String> getUserNamesByLoginNames(String[] loginNames) {
        List<UserInfoEntity> userInfoEntities = this.dao.listByFieldInValues("loginName", Lists.newArrayList(Arrays.asList(loginNames).iterator()));
        Map<String, String> map = Maps.newHashMap();
        if (CollectionUtils.isNotEmpty(userInfoEntities)) {
            for (UserInfoEntity userInfoEntity : userInfoEntities) {
                map.put(userInfoEntity.getLoginName(), userInfoEntity.getUserName());
            }
        }
        return map;
    }

    @Override
    public Map<String, String> getUserNamesByUserIds(Collection<String> userIds) {
        Map<String, String> map = Maps.newLinkedHashMap();
        if (CollectionUtils.isEmpty(userIds)) {
            return map;
        }

        Map<String, Object> param = Maps.newHashMap();
//        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
        param.put("locale", LocaleContextHolder.getLocale().toString());
//        }
        List<UserInfoIdNameQueryItem> queryItems = Lists.newArrayList();
        com.wellsoft.context.util.collection.ListUtils.handleSubList(Lists.newArrayList(userIds), 200, list -> {
            param.put("userIds", list);
            List<UserInfoIdNameQueryItem> subItems = this.dao.listItemByNameSQLQuery("userInfoIdNameQuery", UserInfoIdNameQueryItem.class, param, null);
            if (CollectionUtils.isNotEmpty(subItems)) {
                queryItems.addAll(subItems);
            }
        });

        // 更新名称
        queryItems.forEach(item -> {
            map.put(item.getUserId(), StringUtils.defaultIfBlank(item.getiUserName(), item.getUserName()));
        });
        if (userIds.contains("U0000000000") && !map.containsKey("U0000000000")) {
            map.put("U0000000000",
                    !LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString()) ?
                            StringUtils.defaultIfBlank(AppCodeI18nMessageSource.getMessage("User.SuperAdminName", "pt-org", LocaleContextHolder.getLocale().toString()), "超级管理员") : "超级管理员");
        }
        return map;
    }


    @Override
    public UserDto getUserDetailsByUuid(String uuid, Long orgVersionUuid) {
        UserInfoEntity userInfo = getOne(uuid);
        UserDto userDto = new UserDto();
        if (userInfo != null) {
            BeanUtils.copyProperties(userInfo, userDto);
            UserAccountEntity userAccountEntity = userAccountService.getOne(userInfo.getAccountUuid());
            if (userAccountEntity != null) {
                userDto.setAccountType(userAccountEntity.getType());
            }
            userDto.setUserInfoExts(userInfoExtService.getByUserUuid(userInfo.getUuid()));
            if (orgVersionUuid != null) {
                List<OrgUserEntity> orgUserEntities = orgUserService.listByUserIdAndOrgVersionUuid(userInfo.getUserId(), orgVersionUuid);
                userDto.setOrgUsers(orgUserEntities);
                if (CollectionUtils.isNotEmpty(orgUserEntities)) {
                    for (OrgUserEntity u : orgUserEntities) {
                        if (OrgUserEntity.Type.PRIMARY_JOB_USER.equals(u.getType())) {
                            userDto.setMainJobId(u.getOrgElementId());
                            u.setJobDuty(multiOrgJobDutyService.getJobDutyDetailsByJobIdAndOrgVersionUuid(u.getOrgElementId(), orgVersionUuid));
                        } else if (OrgUserEntity.Type.SECONDARY_JOB_USER.equals(u.getType())) {
                            if (userDto.getOtherJobIds() == null) {
                                userDto.setOtherJobIds(Lists.newArrayList());
                            }
                            u.setJobDuty(multiOrgJobDutyService.getJobDutyDetailsByJobIdAndOrgVersionUuid(u.getOrgElementId(), orgVersionUuid));
                            userDto.getOtherJobIds().add(u.getOrgElementId());
                        }
                    }
                }


                // 汇报对象
                List<OrgUserReportRelationEntity> reports = orgUserReportRelationService.listByOrgVersionUuidAndUserId(orgVersionUuid, userInfo.getUserId());
                if (CollectionUtils.isNotEmpty(reports)) {
                    Map<String, List<String>> jobReport = Maps.newHashMap();
                    for (OrgUserReportRelationEntity rep : reports) {
                        if (!jobReport.containsKey(rep.getOrgElementId())) {
                            jobReport.put(rep.getOrgElementId(), Lists.newArrayList());
                        }
                        jobReport.get(rep.getOrgElementId()).add(rep.getReportToUserId());
                    }
                    userDto.setOrgReportTos(jobReport);
                }

            }
            // 用户角色
            List<String> roles = userRoleService.getRolesByUserUuid(userInfo.getUuid());
            userDto.setRoleUuids(roles);

            return userDto;
        }
        return null;
    }

    @Override
    public List<UserInfoEntity> getAllUsersByOrgVersionUuid(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        return dao.listByHQL("from UserInfoEntity u where exists ( select 1 from OrgUserEntity r " +
                "where r.orgVersionUuid = :orgVersionUuid and r.userId=u.userId )", params);
    }

    @Override
    @Transactional
    public void userQuit(String uuid, Long orgVersionUuid) {
        UserInfoEntity userInfoEntity = getOne(uuid);
        if (userInfoEntity != null) {
            orgElementRoleMemberService.deleteByMemberAndOrgVersionUuid(userInfoEntity.getUserId(), orgVersionUuid);
        }
    }

    @Override
    @Transactional
    public void resetUserPassword(String uuid) {
        this.resetUserPassword(uuid, "123456");
    }

    @Override
    @Transactional
    public void resetUserPassword(String uuid, String password) {
        UserInfoEntity userInfoEntity = getOne(uuid);
        if (userInfoEntity != null) {
            UserAccountEntity accountEntity = userAccountService.getByLoginName(userInfoEntity.getLoginName());
            //TODO: 重置初始密码可通过组织参数模块配置获取 orgSettingService
            accountEntity.setPassword(SM3Util
                    .encrypt(StringUtils.defaultIfBlank(password, "123456") + "{" + accountEntity.getLoginName().toLowerCase() + "}"));
            accountEntity.setModifyPasswordTime(new Date());
            accountEntity.setPasswordModifiedByUser(SpringSecurityUtils.getCurrentLoginName().equals(accountEntity.getLoginName()));
            accountEntity.setPasswordErrorNum(0);
            UserAcctPasswordRules rules = userAccountService.getUserAcctPasswordRules();
            if (rules.enabled(UserAcctPasswordRules.RuleKey.enablePasswordTimeLimit.name())) {
                accountEntity.setPasswordExpiredTime(DateUtils.addDays(accountEntity.getModifyPasswordTime(), rules.getInt(UserAcctPasswordRules.RuleKey.passwordLimitValidDay.name())));
            }
            userAccountService.save(accountEntity);
        }
    }

    @Override
    @Transactional
    public void deleteUser(String uuid) {
        UserInfoEntity userInfoEntity = getOne(uuid);
        if (userInfoEntity != null) {
            delete(userInfoEntity);
            UserAccountEntity accountEntity = userAccountService.getByLoginName(userInfoEntity.getLoginName());
            userAccountService.delete(accountEntity);
            orgElementRoleMemberService.deleteByMember(userInfoEntity.getUserId());
            orgUserService.deleteOrgUser(userInfoEntity.getUserId(), null);
        }
    }

    /**
     * 根据用户ID、组织元素ID、组织版本ID列表，获取用户信息
     *
     * @param userIds
     * @param eleIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public List<UserInfoEntity> listByUserIds(Set<String> userIds, Set<String> eleIds, String[] orgVersionIds) {
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        List<UserInfoEntity> userInfoEntities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfoEntities.addAll(this.dao.listByFieldInValues("userId", Arrays.asList(userIds.toArray(new String[0]))));
        }

        if (CollectionUtils.isNotEmpty(eleIds)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("eleIds", eleIds);
            params.put("orgVersionIds", orgVersionIds);
            userInfoEntities.addAll(this.listByNameHQLQuery("listUserInfoByElementIdsQuery", params));
        }
        return userInfoEntities;
    }

    /**
     * 根据用户ID、组织元素ID、组织版本ID列表，获取用户信息
     *
     * @param userIds
     * @param eleIds
     * @param orgVersionIds
     * @return
     */
    @Override
    public Map<String, String> listAsMapByUserIds(Set<String> userIds, Set<String> eleIds, String[] expectOrgVersionIds, String[] orgVersionIds) {
        Assert.notEmpty(orgVersionIds, "组织版本ID列表不能为空！");

        Map<String, String> map = Maps.newLinkedHashMap();
        List<UserInfoIdNameQueryItem> userInfoItems = Lists.newArrayList();
        // List<UserInfoEntity> userInfoEntities = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfoItems.addAll(this.listUserInfoIdNameQueryItemByUserIds(userIds, expectOrgVersionIds, null));
        }

        if (CollectionUtils.isNotEmpty(eleIds)) {
            Set<String> orgEleIds = eleIds;
            List<String> bizRoleIds = Lists.newArrayList();
            List<String> bizOrgEleIds = Lists.newArrayList();
            for (String orgEleId : orgEleIds) {
                if (StringUtils.contains(orgEleId, Separator.SLASH.getValue())) {
                    bizRoleIds.add(orgEleId);
                } else if (StringUtils.startsWith(orgEleId, IdPrefix.BIZ_PREFIX.getValue())
                        || StringUtils.startsWith(orgEleId, IdPrefix.BIZ_ORG_DIM.getValue())) {
                    bizOrgEleIds.add(orgEleId);
                }
            }
            if (CollectionUtils.isNotEmpty(bizRoleIds) || CollectionUtils.isNotEmpty(bizOrgEleIds)) {
                orgEleIds = Sets.newHashSet(orgEleIds);
                orgEleIds.removeAll(bizRoleIds);
                orgEleIds.removeAll(bizOrgEleIds);
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("orgVersionIds", orgVersionIds);
            if (CollectionUtils.isNotEmpty(orgEleIds)) {
                params.put("eleIds", orgEleIds);
                userInfoItems.addAll(this.dao.listItemByNameHQLQuery("listUserIdAndNameByElementIdsQuery", UserInfoIdNameQueryItem.class, params, null));
            }
            if (CollectionUtils.isNotEmpty(bizRoleIds)) {
                bizRoleIds.forEach(id -> {
                    String[] roleIds = StringUtils.split(id, Separator.SLASH.getValue());
                    String elementId = roleIds[0];
                    String bizRoleId = roleIds[1];
                    Map<String, Object> bizRoleQueryParams = Maps.newHashMap();
                    bizRoleQueryParams.put("eleId", elementId);
                    bizRoleQueryParams.put("bizRoleId", bizRoleId);
                    bizRoleQueryParams.put("orgVersionIds", orgVersionIds);
                    userInfoItems.addAll(this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByElementIdAndBizRoleIdQuery", UserInfoIdNameQueryItem.class, bizRoleQueryParams, null));
                });
            }
            if (CollectionUtils.isNotEmpty(bizOrgEleIds)) {
                params.put("eleIds", bizOrgEleIds);
                userInfoItems.addAll(this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByElementIdsQuery", UserInfoIdNameQueryItem.class, params, null));
            }
        }

        userInfoItems.forEach(userInfo -> {
            map.put(userInfo.getUserId(), userInfo.getUserName());
        });
        if (!LocaleContextHolder.getLocale().toString().equals(Locale.SIMPLIFIED_CHINESE.toString())) {
            Map<String, String> names = getUserNamesByUserIds(map.keySet());
            if (MapUtils.isNotEmpty(names)) {
                Set<Map.Entry<String, String>> entries = names.entrySet();
                for (Map.Entry<String, String> ent : entries) {
                    if (map.containsKey(ent.getKey()) && StringUtils.isNotBlank(ent.getValue())) {
                        map.put(ent.getKey(), ent.getValue());
                    }
                }
            }
        }

        return map;
    }

    /**
     * @param userIds
     * @return
     */
    private List<UserInfoIdNameQueryItem> listUserInfoIdNameQueryItemByUserIds(Set<String> userIds, String[] orgVersionIds, String[] bizOrgIds) {
        List<UserInfoIdNameQueryItem> userInfoItems = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        com.wellsoft.context.util.collection.ListUtils.handleSubList(Lists.newArrayList(userIds), 1000, list -> {
            params.put("userIds", list);
            params.put("orgVersionIds", orgVersionIds);
            params.put("bizOrgIds", bizOrgIds);
            List<UserInfoIdNameQueryItem> subItems = this.dao.listItemByNameSQLQuery("userInfoIdNameQuery", UserInfoIdNameQueryItem.class, params, null);

            if (CollectionUtils.isNotEmpty(subItems)) {
                userInfoItems.addAll(subItems);
            }
        });
        // userInfoEntities.addAll(this.dao.listByFieldInValues("userId", Arrays.asList(userIds.toArray(new String[0]))));
        // 按userIds排序
        if (CollectionUtils.size(userIds) > 1) {
            Map<String, Integer> orderMap = Maps.newHashMap();
            int order = 0;
            for (String userId : userIds) {
                orderMap.put(userId, order++);
            }
            Collections.sort(userInfoItems, (o1, o2) -> orderMap.get(o1.getUserId()).compareTo(orderMap.get(o2.getUserId())));
        }
        return userInfoItems;
    }

    /**
     * @param userIds
     * @param eleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> listBizOrgUserAsMapByUserIds(Set<String> userIds, Set<String> eleIds, String[] bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        Map<String, String> map = Maps.newLinkedHashMap();
        List<UserInfoIdNameQueryItem> userInfoItems = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfoItems.addAll(this.listUserInfoIdNameQueryItemByUserIds(userIds, null, bizOrgIds));
        }

        if (CollectionUtils.isNotEmpty(eleIds)) {
            Set<String> bizOrgEleIds = eleIds;
            List<String> bizRoleIds = Lists.newArrayList();
            for (String orgEleId : bizOrgEleIds) {
                if (StringUtils.contains(orgEleId, Separator.SLASH.getValue())) {
                    bizRoleIds.add(orgEleId);
                }
            }
            if (CollectionUtils.isNotEmpty(bizRoleIds)) {
                bizOrgEleIds = Sets.newHashSet(bizOrgEleIds);
                bizOrgEleIds.removeAll(bizRoleIds);
            }
            Map<String, Object> params = Maps.newHashMap();
            params.put("bizOrgIds", bizOrgIds);
            if (CollectionUtils.isNotEmpty(bizOrgEleIds)) {
                params.put("eleIds", bizOrgEleIds);
                userInfoItems.addAll(this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByElementIdsQuery", UserInfoIdNameQueryItem.class, params, null));
            }
            if (CollectionUtils.isNotEmpty(bizRoleIds)) {
                bizRoleIds.forEach(id -> {
                    String[] roleIds = StringUtils.split(id, Separator.SLASH.getValue());
                    String elementId = roleIds[0];
                    String bizRoleId = roleIds[1];
                    Map<String, Object> bizRoleQueryParams = Maps.newHashMap();
                    bizRoleQueryParams.put("eleId", elementId);
                    bizRoleQueryParams.put("bizRoleId", bizRoleId);
                    userInfoItems.addAll(this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByElementIdAndBizRoleIdQuery", UserInfoIdNameQueryItem.class, bizRoleQueryParams, null));
                });
            }
        }

        userInfoItems.forEach(userInfo -> {
            map.put(userInfo.getUserId(), userInfo.getUserName());
        });
        return map;
    }

    @Override
    public Map<String, String> listBizOrgUserAsMapByIdAndBizRoleId(String elementId, String bizRoleId, String bizOrgId) {
        Assert.hasLength(elementId, "组织元素ID不能为空！");
        Assert.hasLength(bizRoleId, "业务角色ID不能为空！");
        Assert.hasLength(bizOrgId, "业务组织ID不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("eleId", elementId);
        params.put("bizRoleId", bizRoleId);
        params.put("bizOrgId", bizOrgId);
        List<UserInfoIdNameQueryItem> userInfoItems = this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByElementIdAndBizRoleIdQuery", UserInfoIdNameQueryItem.class, params, null);

        Map<String, String> map = Maps.newLinkedHashMap();
        userInfoItems.forEach(userInfo -> {
            map.put(userInfo.getUserId(), userInfo.getUserName());
        });
        return map;
    }

    /**
     * @param userIds
     * @param eleIds
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> listBizOrgUserAsMapByUserIdsAndBizRoleIds(Set<String> userIds, Set<String> eleIds, List<String> bizRoleIds, String[] bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        Map<String, String> map = Maps.newLinkedHashMap();
        List<UserInfoIdNameQueryItem> userInfoItems = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(userIds)) {
            userInfoItems.addAll(this.listUserInfoIdNameQueryItemByUserIds(userIds, null, bizOrgIds));
        }

        if (CollectionUtils.isNotEmpty(eleIds)) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("eleIds", eleIds);
            params.put("bizRoleIds", bizRoleIds);
            params.put("bizOrgIds", bizOrgIds);
            userInfoItems.addAll(this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByElementIdsQuery", UserInfoIdNameQueryItem.class, params, null));
        }

        userInfoItems.forEach(userInfo -> {
            map.put(userInfo.getUserId(), userInfo.getUserName());
        });
        return map;
    }

    /**
     * @param bizRoleIds
     * @param bizOrgIds
     * @return
     */
    @Override
    public Map<String, String> listBizOrgUserAsMapByBizRoleIds(List<String> bizRoleIds, String[] bizOrgIds) {
        Assert.notEmpty(bizOrgIds, "业务组织ID列表不能为空！");

        Map<String, Object> params = Maps.newHashMap();
        params.put("bizRoleIds", bizRoleIds);
        params.put("bizOrgIds", bizOrgIds);
        List<UserInfoIdNameQueryItem> userInfoItems = this.dao.listItemByNameHQLQuery("listBizOrgUserIdAndNameByBizRoleIdsQuery", UserInfoIdNameQueryItem.class, params, null);

        Map<String, String> map = Maps.newLinkedHashMap();
        userInfoItems.forEach(userInfo -> {
            map.put(userInfo.getUserId(), userInfo.getUserName());
        });
        return map;
    }

    @Override
    @Transactional
    public void deleteUsers(List<String> uuid) {
        if (CollectionUtils.isNotEmpty(uuid)) {
            for (String uid : uuid) {
                deleteUser(uid);
            }
        }
    }

    @Override
    public UserInfoEntity getByUserId(String userId) {
        return dao.getOneByFieldEq("userId", userId);
    }

    @Override
    public UserInfoEntity getByMobile(String mobile) {
        Assert.hasLength(mobile, "手机号不能为空！");

        String globalMobile = mobile.startsWith("+") ? StringUtils.substring(mobile, 3) : "+" + mobile;
        Map<String, Object> params = Maps.newHashMap();
        params.put("globalMobile", globalMobile);
        params.put("mobile", mobile);
        String hql = "from UserInfoEntity u where u.ceilPhoneNumber = :mobile or u.ceilPhoneNumber = :globalMobile order by u.createTime desc";
        List<UserInfoEntity> userInfoEntities = this.dao.listByHQL(hql, params);
        return CollectionUtils.isNotEmpty(userInfoEntities) ? userInfoEntities.get(0) : null;
    }

    @Override
    @Transactional
    public void deleteUserRoleByRoleUuid(String roleUuid) {
        userRoleService.deleteByRoleUuid(roleUuid);
    }

    /**
     * 根据用户ID列表、角色UUID添加用户角色
     *
     * @param userIds
     * @param roleUuid
     */
    @Override
    @Transactional
    public void addUserRoleByIdsAndRoleUuid(List<String> userIds, String roleUuid) {
        if (CollectionUtils.isEmpty(userIds)) {
            return;
        }

        List<UserRoleEntity> roleEntities = Lists.newArrayList();
        List<UserInfoEntity> entities = this.dao.listByFieldInValues("userId", userIds);
        entities.forEach(userInfoEntity -> {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setUserUuid(userInfoEntity.getUuid());
            userRoleEntity.setRoleUuid(roleUuid);
            roleEntities.add(userRoleEntity);
        });

        if (CollectionUtils.isNotEmpty(roleEntities)) {
            userRoleService.saveAll(roleEntities);
        }
    }

    /**
     * 根据权限角色UUID获取用户
     *
     * @param roleUuid
     * @return
     */
    @Override
    public List<UserInfoEntity> listByRoleUuid(String roleUuid) {
        Assert.hasLength(roleUuid, "角色UUID不能为空！");

        String hql = "from UserInfoEntity t1 where exists(select t2.uuid from UserRoleEntity t2 where t2.roleUuid = :roleUuid and t2.userUuid = t1.uuid)";
        Map<String, Object> params = Maps.newHashMap();
        params.put("roleUuid", roleUuid);
        return this.dao.listByHQL(hql, params);
    }

    @Override
    @Transactional
    public void deleteUserRoleByRoleUuidAndUserIds(String roleUuid, List<String> userIds) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userIds", userIds);
        userRoleService.deleteUserRoleByRoleUuidAndUserUuids(roleUuid,
                dao.listCharSequenceBySQL("select uuid from user_info where user_id in (:userIds)", params));
    }

    @Override
    public Select2QueryData queryUserOptionsUnderOrgVersion(Long orgVersionUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("orgVersionUuid", orgVersionUuid);
        List<QueryItem> queryItems = dao.listQueryItemByHQL("SELECT uuid as uuid, userId as userId , userName as userName  from UserInfoEntity u where exists ( select 1 from OrgUserEntity r " +
                "where r.orgVersionUuid = :orgVersionUuid and r.userId=u.userId )", params, null);
        return new Select2QueryData(queryItems, "userId", "userName", null);
    }

    @Override
    public List<UserInfoEntity> getUsersLikeUserNameAndPinyin(String keyword, int pageSize) {
        PagingInfo pagingInfo = null;
        if (pageSize > 0) {
            pagingInfo = new PagingInfo(0, pageSize);
        }
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(keyword)) {
            params.put("keyword", "%" + keyword + "%");
        }
        return this.listByHQLAndPage("from UserInfoEntity u " + (StringUtils.isNotBlank(keyword) ? "where ( u.userName like :keyword or u.pinYin like :keyword or u.loginName like :keyword ) " : "") + " order by pinYin asc", params, pagingInfo);
    }

    @Override
    public String getUserAvatar(String userId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        List<String> avatar = this.dao.listCharSequenceBySQL("select avatar from user_info where user_id=:userId", params);
        return CollectionUtils.isNotEmpty(avatar) ? avatar.get(0) : null;
    }

    @Override
    @Transactional
    public void updateUserWorkState(String userId, String workState) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("workState", workState);
        this.dao.updateByHQL("update UserInfoEntity set workState = :workState where userId=:userId", params);
    }

    @Override
    public boolean checkPassword(String password) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("loginName", SpringSecurityUtils.getCurrentLoginName());
        params.put("password", PwdUtils.createSm3Password(SpringSecurityUtils.getCurrentLoginName(), password));
        List<QueryItem> items = userAccountService.listQueryItemBySQL("select 1 from user_account where login_name=:loginName and password =:password", params, null);
        return CollectionUtils.isNotEmpty(items);
    }

    @Override
    public List<UserInfoEntity> getUserInfosByUserId(List<String> userIds) {
        return dao.listByFieldInValues("userId", userIds);
    }

    @Override
    public List<UserInfoEntity> getTenantManagerInfo(String system, String tenant) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("system", system);
        params.put("tenant", tenant);
        params.put("type", UserAccountEntity.Type.TENANT_ADMIN);
        return this.dao.listByHQL("from UserInfoEntity u where u.system=:system and u.tenant=:tenant" +
                " and  exists ( select 1 from UserAccountEntity a where a.type =:type and a.uuid=u.accountUuid ) order by u.userNo asc", params);
    }

    @Override
    public List<UserNameI18nEntity> getUserNameI18nsByUserUuid(String userUuid) {
        return this.userNameI18nDao.listByFieldEqValue("userUuid", userUuid);
    }

    @Override
    public List<UserNameI18nEntity> getUserNameI18nsById(String userId) {
        return this.userNameI18nDao.listByFieldEqValue("userId", userId);
    }

    @Override
    public String getLocaleUserNameByUserIdLocale(String userId, String locale) {
        Assert.noNullElements(new String[]{userId, locale}, "参数不为空");
        Map<String, Object> params = Maps.newHashMap();
        params.put("userId", userId);
        params.put("locale", locale);
        List<UserNameI18nEntity> list = this.userNameI18nDao.listByHQL("from UserNameI18nEntity where locale=:locale and userId=:userId", params);
        return CollectionUtils.isNotEmpty(list) ? list.get(0).getUserName() : null;
    }

    @Override
    @Transactional
    public void saveUserReportRelation(String userId, Map<String, List<String>> orgElementIdReport, Long orgVersionUuid) {
        orgUserReportRelationService.saveUserReportRelation(userId, orgElementIdReport, orgVersionUuid);
    }

    @Override
    public boolean checkUserExist(UserInfoEntity userInfoEntity) {
        Map<String, Object> map = Maps.newHashMap();
        StringBuilder sql = new StringBuilder("from UserInfoEntity u where 1=1");
        if (StringUtils.isNotBlank(userInfoEntity.getUuid())) {
            sql.append(" and u.uuid <> :uuid ");
            map.put("uuid", userInfoEntity.getUuid());
        }
        if (StringUtils.isNotBlank(userInfoEntity.getIdNumber())) {
            sql.append(" and exists ( select 1 from UserInfoExtEntity e where e.userUuid = u.uuid and e.attrKey='idNumber' and attrValue=:idNumber) ");
            map.put("idNumber", userInfoEntity.getIdNumber());
        }
        if (StringUtils.isNotBlank(userInfoEntity.getCeilPhoneNumber())) {
            sql.append(" and u.ceilPhoneNumber = :ceilPhoneNumber");
            map.put("ceilPhoneNumber", userInfoEntity.getCeilPhoneNumber());
        }
        if (StringUtils.isNotBlank(userInfoEntity.getMail())) {
            sql.append(" and u.mail = :mail");
            map.put("mail", userInfoEntity.getMail());
        }
        if (StringUtils.isNotBlank(userInfoEntity.getUserName())) {
            sql.append(" and u.userName = :userName");
            map.put("userName", userInfoEntity.getUserName());
        }

        return this.dao.countByHQL(sql.toString(), map) > 0;
    }

    /**
     * // 用户信息扩展表保存
     *
     * @param userExtraInfoDtos 用户信息扩展对象集合
     * @param accountUuid       账号uuid
     * @return void
     **/
    private void saveUserExtraInfos(List<UserExtraInfoDto> userExtraInfoDtos, String accountUuid) {
        for (UserExtraInfoDto userExtraInfoDto : userExtraInfoDtos) {
            UserExtraInfoEntity userExtraInfoEntity = new UserExtraInfoEntity();
            BeanUtils.copyProperties(userExtraInfoDto, userExtraInfoEntity);
            userExtraInfoEntity.setAccountUuid(accountUuid);
            userExtraInfoService.save(userExtraInfoEntity);
        }
    }
}
