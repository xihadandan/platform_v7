package com.wellsoft.pt.webmail.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.webmail.service.WmMailUserService;
import org.apache.commons.lang.StringUtils;
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
public class WebmailDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Autowired
    WmMailUserService wmMailUserService;

    @Override
    public String getType() {
        return "pt-webmail";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        long total = 0L;
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<String> clearTypes = (List<String>) params.get("clearTypes");
            if (clearTypes.contains("mailbox")) {
                total += wmMailUserService.getDao().deleteBySQL("delete from wm_mailbox a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            }
            if (clearTypes.contains("mailFolder")) {
                total += wmMailUserService.getDao().deleteBySQL("delete from wm_mail_folder a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            }
            if (clearTypes.contains("mailContact")) {
                total += wmMailUserService.getDao().deleteBySQL("delete from wm_mail_user_contact a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            }
            if (clearTypes.contains("mailTag")) {
                wmMailUserService.getDao().deleteBySQL("delete from wm_mail_rela_tag a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.creator=o.user_id ) ", sqlParams);
                total += wmMailUserService.getDao().deleteBySQL("delete from wm_mail_tag a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
            }
            return ExpectCleanupResult.total(total);
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
            List<String> clearTypes = (List<String>) params.get("clearTypes");
            if (clearTypes.contains("mailbox")) {
                List<QueryItem> items = wmMailUserService.listQueryItemBySQL("select count(1) as total from wm_mailbox a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams, null);
                logger.info("当前系统下的邮件统计: {}", items.get(0).getLong("total"));
                total += items.get(0).getLong("total");
            }
            if (clearTypes.contains("mailFolder")) {
                List<QueryItem> items = wmMailUserService.listQueryItemBySQL("select count(1) as total from wm_mail_folder a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams, null);
                logger.info("当前系统下的邮件文件夹统计: {}", items.get(0).getLong("total"));
                total += items.get(0).getLong("total");
            }
            if (clearTypes.contains("mailContact")) {
                List<QueryItem> items = wmMailUserService.listQueryItemBySQL("select count(1) as total from wm_mail_user_contact a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams, null);
                logger.info("当前系统下的邮件联系人统计: {}", items.get(0).getLong("total"));
                total += items.get(0).getLong("total");
            }
            if (clearTypes.contains("mailTag")) {
                List<QueryItem> items = wmMailUserService.listQueryItemBySQL("select count(1) as total from wm_mail_tag a where exists ( select 1 from org_user o" +
                        " where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams, null);
                logger.info("当前系统下的邮件自定义标签统计: {}", items.get(0).getLong("total"));
                total += items.get(0).getLong("total");
            }
            return ExpectCleanupResult.total(total);
        }
        return ExpectCleanupResult.total(total);
    }
}
