package com.wellsoft.pt.org.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.service.OrgGroupService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
public class OrgGroupDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    OrgGroupService orgGroupService;

    @Override
    public String getType() {
        return IexportType.OrgGroup;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        if (StringUtils.isNotBlank(system)) {
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            orgGroupService.getDao().deleteBySQL("delete from org_group_role m where exists ( select 1 from org_group g where g.system=:system and g.tenant=:tenant and g.uuid =m.group_uuid ) ", sqlParams);
            orgGroupService.getDao().deleteBySQL("delete from org_group_owner m where exists ( select 1 from org_group g where g.system=:system and g.tenant=:tenant and g.uuid =m.group_uuid ) ", sqlParams);
            orgGroupService.getDao().deleteBySQL("delete from org_group_member m where exists ( select 1 from org_group g where g.system=:system and g.tenant=:tenant and g.uuid =m.group_uuid ) ", sqlParams);
            return ExpectCleanupResult.total(orgGroupService.getDao().deleteBySQL("delete from org_group g where g.system=:system and g.tenant=:tenant", sqlParams));
        }
        return ExpectCleanupResult.total(0);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String system = StringUtils.defaultIfBlank(params.optString("system"), RequestSystemContextPathResolver.system());
        long total = 0L;
        if (StringUtils.isNotBlank(system)) {
            // 清理系统下的组织
            Map<String, Object> sqlParams = Maps.newHashMap();
            sqlParams.put("system", system);
            sqlParams.put("tenant", SpringSecurityUtils.getCurrentTenantId());
            List<QueryItem> items = orgGroupService.listQueryItemBySQL("select count(1) as total from org_group where system=:system and tenant=:tenant", sqlParams, null);
            logger.info("预计删除组织群组数据: {}条数据 ", items.get(0).getLong("total"));
            return ExpectCleanupResult.total(items.get(0).getLong("total"));
        }
        return ExpectCleanupResult.total(total);
    }
}
