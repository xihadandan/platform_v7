package com.wellsoft.pt.security.core.userdetails;

import com.google.common.collect.Sets;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.security.license.service.LicenseService;
import org.apache.commons.collections.CollectionUtils;

import java.util.Map;
import java.util.Set;

/**
 * Description:
 *
 * @author chenq
 * @date 2020/4/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/14    chenq		2020/4/14		Create
 * </pre>
 */
public class UserDetailsCacheHolder {

    private static boolean isDevProfile = true;
    private static Set<String> devProfileUsers = Sets.newHashSet();

    public static org.springframework.security.core.userdetails.UserDetails getUserFromCache(
            String username) {
        return ApplicationContextHolder.getBean(CompositeUserDetailsCache.class).getUserFromCache(
                username);
    }

    public static void putUserInCache(
            org.springframework.security.core.userdetails.UserDetails user) {
        checkDevProfile(user.getUsername());
        ApplicationContextHolder.getBean(CompositeUserDetailsCache.class).putUserInCache(user);
    }

    private static void checkDevProfile(String username) {
        if (isDevProfile()) {
            if (CollectionUtils.size(devProfileUsers) >= 5) {
                throw new RuntimeException("超出开发版本的5个用户数限制！");
            } else {
                devProfileUsers.add(username);
            }
        }
    }

    private static boolean isDevProfile() {
        if (!isDevProfile) {
            return isDevProfile;
        }
        LicenseService licenseService = ApplicationContextHolder.getBean(LicenseService.class);
        Map<String, Object> licenseMap = licenseService.getLicenseInfo();
        isDevProfile = !Config.ENV_PRD.equalsIgnoreCase((String) licenseMap.get("profile"));
        return isDevProfile;
    }

    public static void removeUserFromCache(String username) {
        devProfileUsers.remove(username);
        ApplicationContextHolder.getBean(CompositeUserDetailsCache.class).removeUserFromCache(
                username);
    }

    public static void removeAllUserFromCache() {
        ApplicationContextHolder.getBean(CompositeUserDetailsCache.class).removeAllUserFromCache();
    }
}
