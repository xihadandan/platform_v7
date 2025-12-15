package com.wellsoft.pt.security.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
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
 * 2024年06月04日   chenq	 Create
 * </pre>
 */
@Service
public class SecurityRolePrivilegeResCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "rolePrivilegeResource";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String appId = params.optString("appId");
        int count = 0;
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的表单
            queryMap.put("appId", appId);
        } else {
            String[] excludeIds = new String[]{"PRD_PT", "system_manager"};
            queryMap.put("excludeIds", excludeIds);

        }
        if (clearTypes.contains("role")) {
            count += this.nativeDao.batchExecute("delete " + this.roleQuerySql(queryMap), queryMap);
        }
        if (clearTypes.contains("privilege")) {
            count += this.nativeDao.batchExecute("delete " + this.privilegeQuerySql(queryMap), queryMap);
        }
        if (clearTypes.contains("resource")) {
            count += this.nativeDao.batchExecute("delete " + resourceQuerySql(queryMap), queryMap);
        }
        if (clearTypes.contains("privilege")) {
            this.nativeDao.batchExecute("delete from audit_privilege_other_resource p where not exists ( select 1 from audit_privilege a where a.uuid = p.privilege_uuid ) "
                    , queryMap);
            this.nativeDao.batchExecute("delete from audit_privilege_resource p where not exists ( select 1 from audit_privilege a where a.uuid = p.privilege_uuid ) "
                    , queryMap);
            this.nativeDao.batchExecute("delete from audit_role_privilege p where not exists ( select 1 from audit_privilege a where a.uuid = p.privilege_uuid )" +
                            " or not exists (select 1 from audit_role a where a.uuid = p.role_uuid)  "
                    , queryMap);
        }
        if (clearTypes.contains("role")) {
            this.nativeDao.batchExecute("delete from audit_role_privilege p where not exists ( select 1 from audit_privilege a where a.uuid = p.privilege_uuid )" +
                            " or not exists (select 1 from audit_role a where a.uuid = p.role_uuid)  "
                    , queryMap);
            this.nativeDao.batchExecute("delete from audit_nested_role p where not exists ( select 1 from audit_role a where a.uuid = p.role_uuid ) "
                    , queryMap);
            this.nativeDao.batchExecute("delete from audit_role_nested_role p where not exists ( select 1 from audit_role a where a.uuid = p.role_uuid ) " +
                            " or not exists ( select 1 from audit_nested_role a where a.uuid = p.nested_role_uuid) "
                    , queryMap);
        }
        return ExpectCleanupResult.total(count);
    }

    private String roleQuerySql(Map<String, Object> queryMap) {
        return " from audit_role p\n" +
                " where " + (queryMap.containsKey("appId") ? " p.app_id = :appId " :
                (" ( p.app_id is null or ( p.app_id not in (:excludeIds) and p.app_id not like 'pt-%' and p.app_id not like 'pt_%' " +
                        " and not exists\n" +
                        "                (select 1\n" +
                        "                    from app_prod_version v, app_product d\n" +
                        "                   where d.id = v.prod_id\n" +
                        "                     and (d.id in (:excludeIds) or\n" +
                        "                         (d.id like 'pt-%' and d.id like 'pt_%'))\n" +
                        "                     and v.version_id = p.app_id) )) " +
                        "   and (p.system_def = 0 or\n" +
                        "       (p.system_def = 1 and (\n" +
                        // 系统生成的页面访问权限, 需要判断依赖的页面是否存在，如果页面还存在，就不能删除权限\n"
                        "        ( p.id like 'ROLE_VIEW_PAGE_%' and not exists\n" +
                        "         (select 1\n" +
                        "              from app_page_definition m\n" +
                        "             where 'ROLE_VIEW_PAGE_' || m.id = p.id))  or (p.id like 'ROLE_page_sysdef_%' and not exists\n" +
                        "                (select 1\n" +
                        "                      from app_page_definition m\n" +
                        "                     where ('ROLE_page_sysdef_' || m.id) = p.id)\n" +
                        "               \n" +
                        "               ) or p.id like 'ROLE_PAGE_SYSTEM_DEF_%'\n" +
                        "        )) )"));
    }

    private String privilegeQuerySql(Map<String, Object> queryMap) {
        return "  from audit_privilege p\n" +
                " where " + (queryMap.containsKey("appId") ? " p.app_id = :appId " :
                (" ( p.app_id is null or ( p.app_id not in (:excludeIds) and p.app_id not like 'pt-%' and p.app_id not like 'pt_%')) " +
                        "   and (p.system_def = 0 or\n" +
                        "       (p.system_def = 1 and\n" +
                        "       (\n" +
                        // 系统生成的模块访问权限, 需要判断依赖的模块是否存在，如果模块还存在，就不能删除权限
                        "        (p.code like 'PRIVILEGE_MOD_%' and not exists\n" +
                        "         (select 1 from app_module m where m.id = p.app_id)) or\n" +
                        "        (\n" +
                        // 系统生成的页面访问权限, 需要判断依赖的页面是否存在，如果页面还存在，就不能删除权限\n"
                        "         p.code like 'PRIVILEGE_PAGE_%' and not exists\n" +
                        "         (select 1\n" +
                        "              from app_page_definition m\n" +
                        "             where 'PRIVILEGE_PAGE_' || m.id = p.code)\n" +
                        "        ) )))"));
    }

    private String resourceQuerySql(Map<String, Object> queryMap) {
        return "from audit_resource p\n" +
                "         where " + (queryMap.containsKey("appId") ? " p.module_id=:appId" : ("((p.module_id is null and\n" +
                // 旧的资源在设计器暂时还有使用，等后续这些资源改造后再删除
                "               (p.uuid not in\n" +
                "               ('bcc485ad-201a-4075-8fd5-b18ab09b76c4',\n" +
                "                   'ba595678-7c58-450b-b0c3-1d955d8295df') and\n" +
                "               (p.parent_uuid <> 'ba595678-7c58-450b-b0c3-1d955d8295df' or\n" +
                "               (p.parent_uuid = 'ba595678-7c58-450b-b0c3-1d955d8295df' and\n" +
                "               p.type <> 'BUTTON'))\n" +
                "               \n" +
                "               )) or (p.module_id not in ('PRD_PT', 'system_manager') and\n" +
                "               (p.module_id not like 'pt-%' and\n" +
                "               p.module_id not like 'pt_%')))"))
                ;
    }


    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String appId = params.optString("appId");
        int count = 0;
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isEmpty(clearTypes)) {
            return ExpectCleanupResult.total(0);
        }
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的表单
            queryMap.put("appId", appId);
        } else {
            String[] excludeIds = new String[]{"PRD_PT", "system_manager"};
            queryMap.put("excludeIds", excludeIds);
        }
        List<QueryItem> items = null;
        if (clearTypes.contains("role")) {
            items = this.nativeDao.query("select count(1) as total\n" + this.roleQuerySql(queryMap), queryMap, QueryItem.class);
            count += items.get(0).getLong("total");
        }
        if (clearTypes.contains("privilege")) {
            items = this.nativeDao.query("select count(1) as total\n" + this.privilegeQuerySql(queryMap), queryMap, QueryItem.class);
            count += items.get(0).getLong("total");

        }
        if (clearTypes.contains("resource")) {
            items = this.nativeDao.query("select count(1) as total " + resourceQuerySql(queryMap), queryMap, QueryItem.class);
            count += items.get(0).getLong("total");
        }


        return ExpectCleanupResult.total(count);
    }
}
