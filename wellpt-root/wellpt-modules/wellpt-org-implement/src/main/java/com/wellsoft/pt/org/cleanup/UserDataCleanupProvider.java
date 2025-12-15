package com.wellsoft.pt.org.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.service.OrgUserService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.service.UserInfoService;
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
 * 2024年04月23日   chenq	 Create
 * </pre>
 */
@Service
public class UserDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    OrgUserService orgUserService;

    @Override
    public String getType() {
        return IexportType.User;
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

            if ("removeFromSystem".equalsIgnoreCase(clearType)) {
                // 只是移除用户与当前系统的关系
                total = this.expectCleanupRows(params).getTotal();
                orgUserService.getDao().deleteBySQL("delete from org_user o where o.system=:system and o.tenant=:tenant", sqlParams);
                return ExpectCleanupResult.total(total);
            } else {
                // 删除邮箱账号相关信息
                this.cleanupByTypeParams("pt-webmail-user", Params.build(new String[]{"system"}, new Object[]{system}));
                // 删除账号
                orgUserService.getDao().deleteBySQL("delete from user_account a where exists ( select 1 from org_user o ,user_info i " +
                        "where o.system=:system and o.tenant=:tenant and i.login_name = a.login_name and i.user_id=o.user_id ) ", sqlParams);
                // 额外删除不存在用户信息的账号
                orgUserService.getDao().deleteBySQL("delete from user_account a where not exists ( select 1 from  user_info i " +
                        "where i.login_name = a.login_name   ) ", sqlParams);
                // 删除用户信息
                orgUserService.getDao().deleteBySQL("delete from user_info_ext a where exists ( select 1 from org_user o ,user_info i " +
                        "where o.system=:system and o.tenant=:tenant and i.uuid = a.user_uuid and i.user_id=o.user_id ) ", sqlParams);
                total = orgUserService.getDao().deleteBySQL("delete from user_info a where exists ( select 1 from org_user o  " +
                        "where o.system=:system and o.tenant=:tenant and a.user_id=o.user_id ) ", sqlParams);
                orgUserService.getDao().deleteBySQL("delete from org_user o where o.system=:system and o.tenant=:tenant", sqlParams);
                return ExpectCleanupResult.total(total);
            }
        }
        return ExpectCleanupResult.total(0);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        if (StringUtils.isNotBlank(system)) {
            // 清理系统下的组织
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<QueryItem> items = userInfoService.listQueryItemBySQL("select count(1) as total from user_info u where exists (" +
                    "select 1 from org_user o where o.user_id = u.user_id and o.system=:system and o.tenant=:tenant) ", sqlParams, null);
            logger.info("当前系统下的用户统计: {}", items.get(0).getLong("total"));
            return ExpectCleanupResult.total(items.get(0).getLong("total"));
        }
        return ExpectCleanupResult.total(0);
    }
}
