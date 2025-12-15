package com.wellsoft.pt.security.core.userdetails;

import com.wellsoft.pt.cache.CacheManager;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

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
public class CompositeUserDetailsCache implements UserCache {

    private final static String USER_DETAILS_CACHE = "user";
    private CacheManager cacheManager;

    public UserDetails getUserFromCache(String username) {
        return cacheManager.getCache(USER_DETAILS_CACHE).get(username, UserDetails.class);
    }

    public void putUserInCache(UserDetails user) {
        cacheManager.getCache(USER_DETAILS_CACHE).put(user.getUsername(), user);
    }

    public void removeUserFromCache(String username) {
        cacheManager.getCache(USER_DETAILS_CACHE).evict(username);
    }

    public void removeAllUserFromCache() {
        cacheManager.getCache(USER_DETAILS_CACHE).clear();
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
}
