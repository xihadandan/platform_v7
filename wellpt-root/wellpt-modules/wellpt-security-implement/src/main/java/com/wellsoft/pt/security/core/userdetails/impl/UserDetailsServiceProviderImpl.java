/*
 * @(#)2012-12-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.core.userdetails.impl;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.web.ServletUtils;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.common.i18n.AppCodeI18nMessageSource;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.org.entity.OrgElementEntity;
import com.wellsoft.pt.org.entity.OrgElementPathEntity;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.org.entity.UserProperty;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.security.access.LoginAuthenticationProcessingFilter;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.access.intercept.provider.UserSecurityMetadataSourceProvider;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.bean.RoleAuthority;
import com.wellsoft.pt.security.core.authentication.encoding.PasswordAlgorithm;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.core.userdetails.UserSystemOrgDetails;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.unit.service.CommonUnitService;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-26.1	zhulh		2012-12-26		Create
 * </pre>
 * @date 2012-12-26
 */
@Transactional(readOnly = true)
@Service(value = "userDetailsServiceProvider")
public class UserDetailsServiceProviderImpl extends BaseServiceImpl implements UserDetailsServiceProvider {
    @Autowired
    SecurityMetadataSourceService securityMetadataSourceService;
    MessageSourceAccessor message = SpringSecurityMessageSource.getAccessor();
    Pattern chinessPattern = Pattern.compile("[\u4e00-\u9fa5]");
    @Autowired
    private RoleService roleService;
    @Autowired
    private MultiOrgUserService multiOrgUserService;
    @Autowired
    private TenantFacadeService tenantService;
    @Autowired
    private CommonUnitService commonUnitService;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private CacheManager cacheManager;

    @Resource
    OrgFacadeService orgFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider#getLoginType()
     */
    @Override
    public String getLoginType() {
        return LoginType.USER;
    }

    /**
     * 租户功能已经取消了， 所以这里不需要这么复杂的判断方法了
     *
     * @Override public UserDetails getUserDetails(String loginName) {
     * if (StringUtils.isBlank(loginName)) {
     * throw new RuntimeException("用户名不能为空！");
     * }
     * <p>
     * String loginNameHashAlgorithmCode = PasswordAlgorithm.Plaintext.getCode();
     * String tenant = null;
     * Tenant tenantAccount = null;
     * if (RequestContextHolder.getRequestAttributes() != null) {
     * ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
     * HttpServletRequest request = attr.getRequest();
     * Object tenantValue = null;
     * Object loginNameHashAlgorithmCodeValue = request
     * .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_LNALG_CODE_KEY);
     * if (loginNameHashAlgorithmCodeValue != null
     * && StringUtils.isNotBlank(loginNameHashAlgorithmCodeValue.toString())) {
     * loginNameHashAlgorithmCode = loginNameHashAlgorithmCodeValue.toString();
     * }
     * if (OrgUtil.isLoginNameUniqueInTenant()) {
     * // 取出单位ID;
     * tenantValue = request
     * .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_UNIT_ID_KEY);
     * Object tenantValueId = request
     * .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_TENANT_ID_KEY);
     * if ((tenantValue == null || "".equals(tenantValue))) {
     * if (tenantValueId != null && StringUtils.isNotBlank(tenantValueId.toString())) {
     * tenant = tenantValueId.toString();
     * } else {
     * throw new RuntimeException("请选择单位！");
     * }
     * }
     * // 如果单位ID不为空则走新规则替换
     * if (tenantValue != null && !"".equals(tenantValue)) {
     * CommonUnit commonUnit = commonUnitService.getById(tenantValue.toString());
     * tenantAccount = tenantService.getById(commonUnit.getTenantId());
     * if (tenantAccount == null || !Tenant.STATUS_ENABLED.equals(tenantAccount.getStatus())) {
     * throw new RuntimeException("单位对应的租户ID[" + commonUnit.getTenantId() + "]不存在");
     * }
     * }
     * } else {
     * tenantValue = request.getAttribute(SwitchTenantUserFilter.SPRING_SECURITY_SWITCH_TENANT_KEY);
     * if (tenantValue == null || StringUtils.isBlank(tenantValue.toString())) {
     * User user = userService.getByLoginName(loginName);
     * if (user != null) {
     * tenantAccount = tenantService.getById(user.getTenantId());
     * }
     * } else {
     * tenant = tenantValue.toString();
     * }
     * }
     * // 登录验证码
     * Object verifyCode = request.getSession().getAttribute("verifycode");
     * Object inputVerifyCode = request
     * .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_VERIFY_CODE_KEY);
     * if (verifyCode != null
     * && (inputVerifyCode == null || !verifyCode.toString().equalsIgnoreCase(inputVerifyCode.toString()))) {
     * throw new RuntimeException("请输入正确的验证码！");
     * }
     * <p>
     * // 租户为空取当前会话租户ID，若再为空取默认租户
     * if (StringUtils.isBlank(tenant)) {
     * tenant = SpringSecurityUtils.getCurrentTenantId();
     * if (StringUtils.isBlank(tenant)) {
     * tenant = Config.DEFAULT_TENANT;// 取默认租户
     * }
     * }
     * if (tenantAccount == null) {
     * tenantAccount = tenantService.getByAccount(tenant);
     * }
     * if (tenantAccount == null || !Tenant.STATUS_ENABLED.equals(tenantAccount.getStatus())) {
     * throw new RuntimeException("租户[" + tenant + "]不存在");
     * }
     * request.setAttribute("tenantId", tenantAccount.getId());
     * }
     * if (StringUtils.isBlank(tenant)) {
     * throw new RuntimeException("租户[" + tenant + "]不存在");
     * }
     * // 忽略登录验证租户用户信息
     * UserDetails userDetails = checkAndGetUserDetails(loginName, tenantAccount, loginNameHashAlgorithmCode);
     * // 验证成功，清空session的tenant信息
     * return userDetails;
     * }
     */

    public UserDetails getUserDetails(String loginName) {
        if (StringUtils.isBlank(loginName)) {
            throw new RuntimeException("登录名不能为空！");
        }

        String loginNameHashAlgorithmCode = PasswordAlgorithm.Plaintext.getCode();
        // 取消租户的功能了，所以这里固定大家都是默认租户
        String tenant = Config.DEFAULT_TENANT;
        Tenant tenantAccount = tenantService.getById(tenant);
        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            Object loginNameHashAlgorithmCodeObj = request
                    .getAttribute(LoginAuthenticationProcessingFilter.SPRING_SECURITY_FORM_LNALG_CODE_KEY);
            if (loginNameHashAlgorithmCodeObj != null) {
                loginNameHashAlgorithmCode = loginNameHashAlgorithmCodeObj.toString();
            }

        }
        // 忽略登录验证租户用户信息
        UserDetails userDetails = checkAndGetUserDetails(loginName, tenantAccount, loginNameHashAlgorithmCode);
        // 验证成功，清空session的tenant信息
        return userDetails;

    }

    private boolean isChinese(String loginName) {
        return chinessPattern.matcher(loginName.charAt(0) + "").find();
    }

    protected UserDetails checkAndGetUserDetails(String loginName, Tenant tenantAccount,
                                                 String loginNameHashAlgorithmCode) {
        try {
            UserInfoFacadeService userInfoFacadeService = ApplicationContextHolder.getBean(UserInfoFacadeService.class);
            UserDto userDto = userInfoFacadeService.getUserByLoginName(loginName, loginNameHashAlgorithmCode);
            if (userDto == null) {
                throw new RuntimeException("Bad credentials");
            }
            if (StringUtils.isNotBlank(RequestSystemContextPathResolver.system())) {
                if (!orgFacadeService.existSystemOrgUser(userDto.getUserId(), RequestSystemContextPathResolver.system()
                        ,/* FIXME: 租户由前端登录地址决定 */ Config.DEFAULT_TENANT) && !Lists.newArrayList(UserAccountEntity.Type.TENANT_ADMIN,
                        UserAccountEntity.Type.SUPER_ADMIN).contains(userDto.getAccountType())) {
                    //TODO: 管理员由租户后续提供可以访问的系统维度
                    throw new RuntimeException("Bad credentials");
                }
            }
            OrgUserVo user = new OrgUserVo();
            user.setId(userDto.getUserId());
            user.setLoginName(loginName);
            user.setUserName(userDto.getUserName());
            user.setLocalUserName(userDto.getLocalUserName());
            user.setPhotoUuid(userDto.getAvatar());
            user.setIsLocked(userDto.getIsAccountNonLocked() ? 0 : 1);
            user.setIsForbidden(!userDto.getIsAccountNonExpired() || !userDto.getIsEnabled() ? 1 : 0);
            user.setUuid(userDto.getUuid());
            user.setPassword(userDto.getPassword());
            user.setType(userDto.getAccountType().ordinal());
            user.setUserNamePy(userDto.getPinYin());

            if (user == null) {
                throw new BadCredentialsException("Bad credentials");
            }

            if (1 == user.getIsForbidden()) {
                throw new RuntimeException(AppCodeI18nMessageSource.getMessage("User.AccountForbidden", "pt-org", LocaleContextHolder.getLocale().toString()
                        , ImmutableMap.<String, Object>builder().put("account", user.getLoginName()).build(), "账号[" + user.getLoginName() + "]已被冻结!"));
            }

            if (1 == user.getIsLocked()) {
                if (UserAccountEntity.LockCause.PASSWORD_ERROR.equals(userDto.getLockCause())) {
                    throw new RuntimeException(AppCodeI18nMessageSource.getMessage("User.AccountPasswordErrorLocked", "pt-org", LocaleContextHolder.getLocale().toString()
                            , ImmutableMap.<String, Object>builder().put("account", user.getLoginName()).put("unlocktime", DateFormatUtils.format(userDto.getUnlockTime(), "yyyy-MM-dd HH:mm")).build(), "账号" + user.getLoginName() + "因密码输入错误，已被锁定！锁定期间无法使用，"
                                    + DateUtils.formatDateTimeMin(userDto.getUnlockTime()) + "将自动解锁！"));
                } else {
                    throw new RuntimeException(AppCodeI18nMessageSource.getMessage("User.AccountForbidden", "pt-org", LocaleContextHolder.getLocale().toString()
                            , ImmutableMap.<String, Object>builder().put("account", user.getLoginName()).build(), "账号[" + user.getLoginName() + "]已被冻结!"));
                }
            }


            // 如果非管理员，且不允许登录本系统
            // if (Boolean.FALSE.equals(user.getIssys()) &&
            // !securityApiFacade.isAllowLogin(user.getId())) {
            // throw new RuntimeException("用户[" + user.getUserName() +
            // "]不允许登录本系统！");
            // }

            // 只能以证书登录
            String certifcatePropValue = orgApiFacade.getUserProperty(UserProperty.KEY_ONLY_LOGON_CERTIFICATE,
                    user.getUuid());
            if (StringUtils.isNotBlank(certifcatePropValue)) {
                if (Boolean.TRUE.equals(Boolean.valueOf(certifcatePropValue))) {
                    throw new RuntimeException("用户[" + user.getLoginName() + "]只能以证书登录本系统！");
                }
            }

            // 用户登录的IP设置
            if (RequestContextHolder.getRequestAttributes() != null) {
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attr.getRequest();
                String ipAddr = ServletUtils.getRemoteAddr(request);
                // 判断指定的用户ID是否允许从指定的IP地址登录系统，若允许返回true，否则返回false
                if (!securityApiFacade.isAllowLogin(user.getId(), ServletUtils.getRemoteAddr(request))) {
                    throw new RuntimeException("用户[" + user.getLoginName() + "]不允许从IP地址[" + ipAddr + "]登录本系统！");
                }
            }

            // 用户登录的域名设置
            if (RequestContextHolder.getRequestAttributes() != null) {
                ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = attr.getRequest();
                String domainaddr = request.getServerName();
                // 判断指定的用户ID是否允许从指定的IP地址登录系统，若允许返回true，否则返回false
                if (!securityApiFacade.isAllowDomainLogin(user.getId(), domainaddr)) {
                    throw new RuntimeException("用户[" + user.getLoginName() + "]不允许从域[" + domainaddr + "]登录本系统！");
                }
            }
            Stopwatch timer = Stopwatch.createStarted();
            Set<GrantedAuthority> grantedAuths = this.queryAllGrantedAuthoritiesByUser(user);
            if (user.getType() != null) {
                if (UserAccountEntity.Type.TENANT_ADMIN.ordinal() == user.getType()) {
                    grantedAuths.add(new SimpleGrantedAuthority(BuildInRole.ROLE_TENANT_ADMIN.name()));
                }
            }
            logger.info("登录验证获取权限耗时：{}", timer.stop());
            DefaultUserDetails userdetails = new DefaultUserDetails(tenantAccount, user, grantedAuths, getLoginType());
            setOrgDetail(userdetails);

            // 清理用户级安全缓存
            cacheManager.getCache(
                    UserSecurityMetadataSourceProvider.USER_SECURITY_CACHE_PREFIX + userdetails.getUserId().toLowerCase()).clear();
            return userdetails;

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw e;
        }

    }

    public void setOrgDetail(DefaultUserDetails userDetails) {
        List<OrgUserEntity> orgUserEntities = orgFacadeService.getAllOrgUserUnderPublishedOrgVersion(userDetails.getUserId(), null, userDetails.getTenantId());
        List<OrgUserEntity> noElementOrgUser = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(orgUserEntities)) {
            // 设置用户的系统组织信息
            Map<String, UserSystemOrgDetails.OrgDetail> map = Maps.newHashMap();
            UserSystemOrgDetails userSystemOrgDetails = new UserSystemOrgDetails();
            userDetails.setUserSystemOrgDetails(userSystemOrgDetails);
            Map<String, List<OrgUserEntity>> asDeptMember = Maps.newHashMap();
            for (OrgUserEntity orgUserEntity : orgUserEntities) {
                if (StringUtils.isNotBlank(orgUserEntity.getSystem())) {
                    if (orgUserEntity.getOrgElement() != null) {
                        if (!map.containsKey(orgUserEntity.getOrgElement().getOrgVersionId())) {
                            UserSystemOrgDetails.OrgDetail orgDetail = new UserSystemOrgDetails.OrgDetail();
                            map.put(orgUserEntity.getOrgElement().getOrgVersionId(), orgDetail);
                            orgDetail.setSystem(orgUserEntity.getSystem());
                            userSystemOrgDetails.getDetails().add(orgDetail);
                            List<OrgTreeNodeDto> bizOrgRoles = orgFacadeService.getUserBizOrgRolesByOrgUuid(orgUserEntity.getUserId(), orgUserEntity.getOrgUuid());
                            orgDetail.setBizOrgRoles(bizOrgRoles);
                        }
                        UserSystemOrgDetails.OrgDetail detail = map.get(orgUserEntity.getOrgElement().getOrgVersionId());
                        if (OrgUserEntity.Type.PRIMARY_JOB_USER.equals(orgUserEntity.getType())
                                || OrgUserEntity.Type.SECONDARY_JOB_USER.equals(orgUserEntity.getType())) {
                            OrgTreeNodeDto dto = new OrgTreeNodeDto();
                            dto.setEleId(orgUserEntity.getOrgElementId());
                            dto.setEleUuid(orgUserEntity.getOrgElement().getUuid().toString());
                            dto.setOrgVersionId(orgUserEntity.getOrgElement().getOrgVersionId());
                            dto.setName(orgUserEntity.getOrgElement().getName());
                            dto.setLocalName(orgUserEntity.getOrgElement().getLocalName());
                            dto.setShortName(orgUserEntity.getOrgElement().getShortName());
                            dto.setLocalShortName(orgUserEntity.getOrgElement().getLocalShortName());
                            dto.setEleNamePath(orgUserEntity.getOrgElementPath().getCnPath());
                            dto.setLocalEleNamePath(orgUserEntity.getOrgElementPath().getLocalPath());
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
                    userDetails.getUserSystemOrgDetails().getDetails()) {
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


                if (orgDetail.getMainDept() == null && CollectionUtils.isEmpty(orgDetail.getOtherDepts())
                        && !CollectionUtils.isEmpty(asDeptMember.get(orgDetail.getSystem()))
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
                        dto.setLocalName(orgUserEntity.getOrgElement().getLocalName());
                        dto.setShortName(orgUserEntity.getOrgElement().getShortName());
                        dto.setLocalShortName(orgUserEntity.getOrgElement().getLocalShortName());
                        dto.setEleNamePath(orgUserEntity.getOrgElementPath().getCnPath());
                        dto.setLocalEleNamePath(orgUserEntity.getOrgElementPath().getLocalPath());
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


            if (CollectionUtils.isEmpty(userDetails.getUserSystemOrgDetails().getDetails()) && !noElementOrgUser.isEmpty()) {
                // 取无挂靠组织节点的组织信息存入
                Set<String> inputSys = Sets.newHashSet();
                for (OrgUserEntity u : noElementOrgUser) {
                    if (inputSys.add(u.getSystem())) {
                        UserSystemOrgDetails.OrgDetail d = new UserSystemOrgDetails.OrgDetail();
                        d.setSystem(u.getSystem());
                        userDetails.getUserSystemOrgDetails().getDetails().add(d);
                    }
                }

            }

        }

    }

    private void setUnitByJob(UserSystemOrgDetails.OrgDetail orgDetail, OrgTreeNodeDto job) {
        String unit = OrgElementPathEntity.nearestIdByPrefix(job.getEleIdPath(), IdPrefix.SYSTEM_UNIT.getValue() + Separator.UNDERLINE.getValue(), false);
        if (unit != null) {
            OrgElementEntity unitElement = orgFacadeService.getOrgElementByIdAndOrgVersionId(unit, job.getOrgVersionId());
            if (unitElement != null) {
                OrgTreeNodeDto unitDto = new OrgTreeNodeDto();
                unitDto.setName(unitElement.getName());
                unitDto.setLocalName(unitElement.getLocalName());
                unitDto.setLocalShortName(unitElement.getLocalShortName());
                unitDto.setSystemUnitId(unitElement.getSourceId());
                unitDto.setEleIdPath(unitElement.getPathEntity().getIdPath());
                unitDto.setEleId(unitElement.getId());
                orgDetail.setUnit(unitDto);
            }
        }
    }

    /**
     * @param @param  user
     * @param @return 设定文件
     * @return Set<GrantedAuthority> 返回类型 用户权限
     * @throws
     * @Title: obtainGrantedAuthorities
     * @Description: 根据用户返回该用户的权限
     */

    protected Set<GrantedAuthority> obtainGrantedAuthorities(String systemUnitId, Map<String, Set<String>> userRoles) {
        Set<GrantedAuthority> authSet = Sets.newHashSet();
        // 添加内置角色 TODO 权限细化未处理，先使用内置角色处理未授权的RUL权限
        authSet.add(new SimpleGrantedAuthority(BuildInRole.ROLE_ANONYMOUS.name()));
        authSet.add(new SimpleGrantedAuthority(BuildInRole.ROLE_USER.name()));
        Set<String> roleUuids = userRoles.keySet();
        for (String roleUuid : roleUuids) {
            Set<GrantedAuthority> set = securityMetadataSourceService.getUserGrantedAuthority(roleUuid);
            if (!CollectionUtils.isEmpty(set)) {
                authSet.addAll(set);
            }
        }
//        if (!CollectionUtils.isEmpty(userRoles)) {
//            // Set<Role> grantedRoles = userService.getUserOrgRoles(user);
//            for (String roleUuid : userRoles.keySet()) {
//                Role role = roleService.get(roleUuid);
//                if (role != null) {
//                    Set<String> from = userRoles.get(roleUuid);
//                    if (role.getSystemDef() == 1) {
//                        authSet.add(new SimpleGrantedAuthority(role.getId()));
//                    } else {
//                        authSet.add(new RoleAuthority(role.getId(), role.getName(), from));
//                    }
//                    // 获取嵌套的角色
//                    obtainNestedRole(authSet, role);
//                }
//            }
//        }

        return authSet;
    }

    /**
     * @param authSet
     * @param role
     */
    protected void obtainNestedRole(Set<GrantedAuthority> authSet, Role role) {
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role childRole = roleService.get(nestedRole.getRoleUuid());
            Set<String> from = Sets.newHashSet("父角色：" + role.getName());
            if (childRole.getSystemDef() == 1) {
                authSet.add(new SimpleGrantedAuthority(childRole.getId()));
            } else {
                authSet.add(new RoleAuthority(childRole.getId(), childRole.getName(), from));
            }
            obtainNestedRole(authSet, childRole);
        }
    }

    private Set<GrantedAuthority> queryAllGrantedAuthoritiesByUser(OrgUserVo user) {
        Map<String, Set<String>> userRoles = orgApiFacade.queryAllRoleListByUserByJobs(user);
        return obtainGrantedAuthorities(user.getSystemUnitId(), userRoles);
    }

    @Override
    public Set<GrantedAuthority> queryAllGrantedAuthoritiesByUserId(String userId) {
        OrgUserVo user = this.orgApiFacade.getUserVoById(userId);
        return this.queryAllGrantedAuthoritiesByUser(user);
    }

}
