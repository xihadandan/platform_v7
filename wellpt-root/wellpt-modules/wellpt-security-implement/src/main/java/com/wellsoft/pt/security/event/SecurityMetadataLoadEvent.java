package com.wellsoft.pt.security.event;

import com.google.common.base.Stopwatch;
import com.wellsoft.context.config.Config;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.security.access.intercept.provider.SecurityMetadataSourceProviderFactory;
import com.wellsoft.pt.security.core.userdetails.UserDetailsServiceProvider;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Description: 容器应用启动时，主动加载权限角色等元数据到内存
 *
 * @author chenq
 * @date 2019/1/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/23    chenq		2019/1/23		Create
 * </pre>
 */
@Component
public class SecurityMetadataLoadEvent implements ApplicationListener<ContextRefreshedEvent> {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SecurityMetadataSourceProviderFactory securityMetadataSourceProviderFactory;

    @Resource
    private UserDetailsServiceProvider userDetailsServiceProvider;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        try {
            if (contextRefreshedEvent.getApplicationContext().getParent() == null) {
                Stopwatch timer = Stopwatch.createStarted();
                AppCacheUtils.clear();
                AppCacheUtils.loadAll();
                logger.info("启动应用，加载产品集成树到缓存中，耗时：{}", timer.stop());
                IgnoreLoginUtils.login(Config.DEFAULT_TENANT,
                        MultiOrgUserAccount.PT_ACCOUNT_ID);//超级管理员登录
                timer = Stopwatch.createStarted();
                securityMetadataSourceProviderFactory.getDefaultTenantSecurityMetadataSourceProvider().appStartLoadConfigAttribute();
                logger.info("启动应用，加载应用对应的权限角色元数据，耗时：{}", timer.stop());
            }
        } catch (Exception e) {
            logger.error("启动应用，加载应用对应的权限角色元数据异常：", e);
        } finally {
            IgnoreLoginUtils.logout();
        }


    }
}