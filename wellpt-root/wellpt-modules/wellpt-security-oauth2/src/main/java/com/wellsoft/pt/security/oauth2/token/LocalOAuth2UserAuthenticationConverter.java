package com.wellsoft.pt.security.oauth2.token;

import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.LoginType;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.core.userdetails.DefaultUserDetails;
import com.wellsoft.pt.security.core.userdetails.InternetUserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsCacheHolder;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.user.dto.UserDto;
import com.wellsoft.pt.user.enums.UserTypeEnum;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class LocalOAuth2UserAuthenticationConverter extends DefaultUserAuthenticationConverter {


    private final String NICK_NAME = "nick_name";
    private final String FROM = "from";
    protected Set<String> defaultRoles = Sets.newHashSet(BuildInRole.ROLE_ANONYMOUS.name());
    private String remoteOAuth2Type = "DEFAULT_OAUTH2_PRINCIPAL";
    private String defaultTrustedRoleName = "ROLE_TRUSTED_USER";

    /**
     * 生成随机字符串
     *
     * @param num
     * @return
     */
    private static String generateRandomString(int num) {
        Object[] c = new Object[num];
        for (int i = 0; i < num; i++) {
            c[i] = RandomUtils.nextInt(0, 2) == 0 ?
                    (char) RandomUtils.nextInt(97, 123) //a-z
                    : (char) RandomUtils.nextInt(65, 91);//A-Z
        }
        return "U" + System.currentTimeMillis() + org.apache.commons.lang.StringUtils.join(c);
    }

    public void setDefaultTrustedRoleName(String defaultTrustedRoleName) {
        this.defaultTrustedRoleName = defaultTrustedRoleName;
    }

    public Map<String, ?> convertUserAuthentication(Authentication authentication) {
        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES,
                    AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;
    }

    public Authentication extractAuthentication(Map<String, ?> map) {
        if (map.containsKey(USERNAME)) {
            UserDetails userDetails = UserDetailsCacheHolder.getUserFromCache(map.get(USERNAME).toString());
            if (userDetails != null) {
                return new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
            }
            MultiOrgUserAccountFacadeService accountFacadeService = ApplicationContextHolder.getBean(
                    MultiOrgUserAccountFacadeService.class);
            UserInfoFacadeService userInfoFacadeService = ApplicationContextHolder.getBean(UserInfoFacadeService.class);
            String username = map.get(USERNAME).toString();
            String nickname = map.get(NICK_NAME).toString();
            String from = map.get(FROM).toString();
            String remoteType = remoteOAuth2Type;
            UserDto userDto = userInfoFacadeService.getUserByExtLogin(username, remoteType);
            if (userDto == null) {
//            UserDto dto = new UserDto();
//            dto.setPassword(generateRandomString(6));
//            dto.setType(UserTypeEnum.INDIVIDUAL);
//            dto.setUserName(nickname);
//            dto.setLoginName(generateRandomString(12));
//            dto.setExtLoginName(username);
//            dto.setExtLoginType(remoteType);
//            // 默认生成系统下的账号，密码随机
//            userInfoFacadeService.addUser(dto);
                userDto = new UserDto();
                userDto.setUserName(nickname);
                userDto.setLoginName(username);
                userDto.setType(UserTypeEnum.INDIVIDUAL);
                userDto.setExtLoginType(remoteType);
                userDto.setPassword("");
                userDto.setIsAccountNonExpired(true);
                userDto.setIsAccountNonLocked(true);
                userDto.setIsCredentialsNonExpired(true);
                userDto.setIsEnabled(true);
                // 互联网用户账号
                return getUsernamePasswordAuthenticationToken(userDto);
            } else {
                if (userDto.getType().equals(UserTypeEnum.STAFF)) {
                    // 员工账号
                    MultiOrgUserAccount multiOrgUserAccount = accountFacadeService.getUserByLoginNameIgnoreCase(username, null);
                    return getMultiOrgUsernamePasswordAuthenticationToken(multiOrgUserAccount.getUuid());
                } else {
                    // 互联网用户账号
                    return getUsernamePasswordAuthenticationToken(userDto);
                }
            }
        }
        //TODO: 关于客户端认证方式的权限资源
        return null;
    }

    private Authentication getUsernamePasswordAuthenticationToken(UserDto user) {
        // 获取互联网用户：按个人类型、法人类型加载不同的默认角色
        Set<GrantedAuthority> authorities = Sets.newHashSet();
        for (String r : this.defaultRoles) {
            authorities.add(new SimpleGrantedAuthority(r));
        }
        authorities.add(new SimpleGrantedAuthority(BuildInRole.ROLE_INTERNET_USER.name()));
        authorities.add(new SimpleGrantedAuthority("ROLE_TRUSTED_USER"));
        Set<String> roles = ApplicationContextHolder.getBean(UserInfoFacadeService.class).getUserTypeAllRoles(user.getLoginName());// 用户类型对应的默认角色
        RoleFacadeService roleFacadeService = ApplicationContextHolder.getBean(RoleFacadeService.class);

        for (String role : roles) {
            Set<String> roleIds = roleFacadeService.getRoleAndNetedRolesById(role);
            if (CollectionUtils.isNotEmpty(roleIds)) {
                for (String r : roleIds) {
                    authorities.add(new SimpleGrantedAuthority(r));
                }
            }
        }

        InternetUserDetails userdetails = new InternetUserDetails(user.getUserName(), user.getPassword(), user.getIsEnabled(),
                user.getIsAccountNonExpired(), user.getIsCredentialsNonExpired(), user.getIsAccountNonLocked(), authorities);
        userdetails.setLoginName(user.getLoginName());
        userdetails.setLoginNameLowerCase(user.getLoginName().toLowerCase());
        userdetails.setLoginType(LoginType.INTERNET_USER);
        UserDetailsCacheHolder.putUserInCache(userdetails);
        return new UsernamePasswordAuthenticationToken(userdetails, "N/A", authorities);
    }

    private UsernamePasswordAuthenticationToken getMultiOrgUsernamePasswordAuthenticationToken(
            String accountUuid) {
        MultiOrgUserService multiOrgUserService = ApplicationContextHolder.getBean(
                MultiOrgUserService.class);
        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        OrgUserVo user = multiOrgUserService.getUser(accountUuid);
        Map<String, Set<String>> userRoles = orgApiFacade.queryAllRoleListByUser(user);
        RoleFacadeService roleFacadeService = ApplicationContextHolder.getBean(
                RoleFacadeService.class);
        Set<GrantedAuthority> authoritiesSet = roleFacadeService.obtainGrantedAuthorities(
                userRoles);
        TenantFacadeService tenantService = ApplicationContextHolder.getBean(
                TenantFacadeService.class);
        Tenant tenant = tenantService.getByAccount(Config.DEFAULT_TENANT);
        UserDetails userDetails = new DefaultUserDetails(tenant, user
                , authoritiesSet);
        UserDetailsCacheHolder.putUserInCache(userDetails);
        return new UsernamePasswordAuthenticationToken(userDetails, "N/A", authoritiesSet);
    }


}
