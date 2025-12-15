package com.wellsoft.pt.webmail.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年04月25日   chenq	 Create
 * </pre>
 */
@Service
public class WebmailUserDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    WmMailUserService wmMailUserService;

    @Override
    public String getType() {
        return "pt-webmail-user";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        long total = 0L;
        if (StringUtils.isNotBlank(system)) {
            // 清理系统下的组织
            String clearType = params.optString("clearTypes");
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<String> address = wmMailUserService.getDao().listCharSequenceBySQL("select a.mail_address as mail_address from wm_mail_user a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            // 删除邮件账号
            NativeDao jamesDao = ApplicationContextHolder.getBean(NativeDao.class);
            jamesDao.setSessionFactory(
                    ApplicationContextHolder.getBean("james", SessionFactory.class),
                    null);
            Map<String, Object> jamesParams = Maps.newHashMap();
            if (CollectionUtils.isNotEmpty(address)) {
                ListUtils.handleSubList(address, 200, list -> {
                    jamesParams.put("userName", list);
                    jamesDao.batchExecute("delete from james_mail m where exists ( select 1 from james_mailbox x where x.mailbox_id =m.mailbox_id and " +
                            "x.user_name in (:userName) )", jamesParams);
                    jamesDao.batchExecute("delete from james_mailbox where user_name in (:userName)", jamesParams);
                    jamesDao.batchExecute("delete from james_user where user_name in (:userName)", jamesParams);
                });
            }
            // 邮件
            wmMailUserService.getDao().deleteBySQL("delete from wm_mailbox a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_user a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_recent_contact a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_folder a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_tag a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_rela_tag a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.creator=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mailbox_info_user a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_signature a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_revocation a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.creator=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_template a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_unfetched a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            wmMailUserService.getDao().deleteBySQL("delete from wm_mail_use_capacity a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);

        }
        return ExpectCleanupResult.total(total);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        long total = 0L;
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<QueryItem> items = wmMailUserService.listQueryItemBySQL("select a.mail_address as mail_address from wm_mail_user a where exists ( select 1 from org_user o" +
                    " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams, null);
            logger.info("当前系统下的邮件用户统计: {}", items.get(0).getLong("total"));
            return ExpectCleanupResult.total(items.get(0).getLong("total"));
        }
        return ExpectCleanupResult.total(total);
    }

    private NativeDao getMailDao() {
        NativeDao jamesDao = ApplicationContextHolder.getBean(NativeDao.class);
        jamesDao.setSessionFactory(
                ApplicationContextHolder.getBean("james", SessionFactory.class),
                null);
        return jamesDao;
    }
}
