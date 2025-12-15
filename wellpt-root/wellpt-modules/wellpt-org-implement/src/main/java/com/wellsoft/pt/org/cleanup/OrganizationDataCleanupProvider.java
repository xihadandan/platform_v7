package com.wellsoft.pt.org.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.org.service.OrganizationService;
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
public class OrganizationDataCleanupProvider extends AbstractDataCleanupDataCleanupProvider {

    @Autowired
    OrganizationService organizationService;

    @Override
    public String getType() {
        return IexportType.Organization;
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
            // 清理系统下的组织
            total = organizationService.getDao().deleteBySQL("delete from org_organization where system=:system and tenant=:tenant", sqlParams);
            // 删除组织模型定义
            organizationService.getDao().deleteBySQL("delete from org_element_model where system=:system and tenant=:tenant " +
                    " and id not in ('unit','dept','job','classify') ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_model_def where system=:system and tenant=:tenant", sqlParams);

            // 删除组织元素实例
            organizationService.getDao().deleteBySQL("delete from org_element_management m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_ext_attr m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_path m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_path_chain m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_role_member m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_role_rela m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element_management m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_element m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_user m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_role m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_user_report_relation m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_setting m where m.system=:system and m.tenant=:tenant" +
                    " and not exists ( select 1 from org_setting o " +
                    "where o.system is null and o.tenant is null ) ", sqlParams);
            organizationService.getDao().deleteBySQL("delete from org_role m where exists ( select 1 from org_version o " +
                    "where o.system=:system and o.tenant=:tenant and o.uuid= m.org_version_uuid ) ", sqlParams);

            // 删除组织版本
            organizationService.getDao().deleteBySQL("delete from org_version where system=:system and tenant=:tenant", sqlParams);

        }
        return ExpectCleanupResult.total(total);
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
            List<QueryItem> items = organizationService.listQueryItemBySQL("select count(1) as total from ORG_ORGANIZATION where system=:system and tenant=:tenant", sqlParams, null);
            logger.info("预计删除组织数据: {}条数据 ", items.get(0).getLong("total"));
            total = items.get(0).getLong("total");
        }
        return ExpectCleanupResult.total(total);
    }
}
