package com.wellsoft.pt.security.core.userdetails.impl;

import com.google.common.collect.Sets;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.core.userdetails.PlatformUserDetials;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.user.dto.UserDetailsVo;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2022年12月27日   chenq	 Create
 * </pre>
 */
@Service
public class UserDetailsAdapterServiceProviderImpl implements UserDetailsServiceProvider {
    @Resource
    UserInfoFacadeService userInfoFacadeService;

    @Resource
    OrgFacadeService orgFacadeService;

    @Autowired
    SecurityMetadataSourceService securityMetadataSourceService;

    @Override
    public String getLoginType() {
        return null;
    }

    @Override
    public UserDetails getUserDetails(String username) {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attr.getRequest();

        // 登录名获取用户，以及用户的默认职位信息
        // 用户登录无法明确当前用户的职位、部门等信息。该类信息，由每次请求动态解析当前归属的系统决定
        UserDetailsVo userDetailsVo = orgFacadeService.getUserDetailsVoByLoginName(username);
        PlatformUserDetials userDetials = new PlatformUserDetials(userDetailsVo, "", this.queryAllGrantedAuthoritiesByUserId(userDetailsVo.getUserId()));

        return userDetials;
    }


    @Override
    public Set<GrantedAuthority> queryAllGrantedAuthoritiesByUserId(String userId) {
        // 获取组织用户的组织节点关联的角色
        Set<String> roleUuids = orgFacadeService.getOrgUserRoleUuidsByUserId(userId);
        Set<GrantedAuthority> authSet = Sets.newHashSet();
        // 添加内置角色 TODO 权限细化未处理，先使用内置角色处理未授权的RUL权限
        authSet.add(new SimpleGrantedAuthority(BuildInRole.ROLE_ANONYMOUS.name()));
        authSet.add(new SimpleGrantedAuthority(BuildInRole.ROLE_USER.name()));
        for (String roleUuid : roleUuids) {
            Set<GrantedAuthority> set = securityMetadataSourceService.getUserGrantedAuthority(roleUuid);
            if (!CollectionUtils.isEmpty(set)) {
                authSet.addAll(set);
            }
        }
        return authSet;
    }
}
