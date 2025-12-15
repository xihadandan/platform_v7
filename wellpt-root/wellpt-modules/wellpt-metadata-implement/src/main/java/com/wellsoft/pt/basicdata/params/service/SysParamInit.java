package com.wellsoft.pt.basicdata.params.service;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.params.core.precompressor.PrecompressorCenter;
import com.wellsoft.pt.basicdata.params.entity.SysParamItem;
import com.wellsoft.pt.basicdata.params.facade.SystemParams;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;

/**
 * Description: 系统参数初始化，配置在web.xml中且放在spring容器初始化之后
 *
 * @author Lmw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-20.1	Lmw		2015-07-20		Create
 * </pre>
 * @date 2015-07-20
 */
@Component
public class SysParamInit implements ServletContextListener {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void init() {
        SysParamItemService sysParamItemService = ApplicationContextHolder.getBean(SysParamItemService.class);
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        try {
            if (userDetails == null) {
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT, Config.DEFAULT_TENANT);
            }

            List<SysParamItem> rlist = PrecompressorCenter.pack();
            for (SysParamItem item : rlist) {
                sysParamItemService.saveValue(item);
            }

            List<SysParamItem> list = sysParamItemService.getAll();
            Cache cache = cacheManager.getCache(ModuleID.SECURITY);
            for (SysParamItem item : list) {
                String key = item.getKey();
                // 忽略掉键为空的记录
                if (StringUtils.isEmpty(key)) {
                    continue;
                }
                String cacheKey = SystemParams.getCacheKey(key);
                String value = StringUtils.isBlank(item.getValue()) ? "" : item.getValue();
                // 放入缓存
                cache.put(cacheKey, value);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        } finally {
            if (userDetails == null) {
                IgnoreLoginUtils.logout();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {

    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        init();
    }
}
