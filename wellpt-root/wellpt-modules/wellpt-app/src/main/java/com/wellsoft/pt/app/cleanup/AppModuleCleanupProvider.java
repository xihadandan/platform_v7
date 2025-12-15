package com.wellsoft.pt.app.cleanup;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.collections.CollectionUtils;
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
public class AppModuleCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "appModule";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isNotEmpty(clearTypes)) {
            if (clearTypes.size() == 1) {
                queryMap.put("enabled", Integer.parseInt(clearTypes.get(0)));
            }
        } else {
            return ExpectCleanupResult.total(0);
        }
        // 删除模块下的关系数据
        nativeDao.batchExecute("delete app_module_res_group_member r where exists (" + " select 1 from app_module m , app_module_res_group a where a.uuid= r.group_uuid and  m.id = a.module_id  "
                + (queryMap.containsKey("enabled") ? " and m.enabled=:enabled " : "") + ") ", queryMap);
        nativeDao.batchExecute("delete app_module_res_group a where exists (" + " select 1 from app_module m where m.id = a.module_id "
                + (queryMap.containsKey("enabled") ? " and  m.enabled=:enabled " : "") + ") ", queryMap);
        nativeDao.batchExecute("delete app_module_res_seq a where exists (" + " select 1 from app_module m where m.id = a.module_id "
                + (queryMap.containsKey("enabled") ? " and m.enabled=:enabled " : "") + ") ", queryMap);

        List<QueryItem> items = nativeDao.query("select id,uuid from app_module where 1=1 " + (queryMap.containsKey("enabled") ? " and enabled=:enabled " : "") + " and id not like 'pt-%' and id not like 'pt_%'", queryMap, QueryItem.class);
        if (CollectionUtils.isNotEmpty(items)) {
            for (QueryItem it : items) {
                this.cleanupByTypeParams("appPageDefinition", Params.build(new String[]{"appId"}, new Object[]{it.getString("id")}));
                this.cleanupByTypeParams("formDefinition", Params.build(new String[]{"appId", "clearTypes"},
                        new Object[]{it.getString("id"), Lists.newArrayList("dropDataModel")}));
                this.cleanupByTypeParams("flowDefinition", Params.build(new String[]{"appId"}, new Object[]{it.getString("id")}));
                this.cleanupByTypeParams("basicData", Params.build(new String[]{"appId", "clearTypes"},
                        new Object[]{it.getString("id"), Lists.newArrayList("cd_data_dictionary",
                                "msg_message_template", "cd_print_template", "cd_data_store_definition", "cd_data_store_definition", "cd_serial_number")}));
            }

        }
        int count = nativeDao.batchExecute("delete from app_module where 1=1 " + (queryMap.containsKey("enabled") ? " and enabled=:enabled " : "") + " and id not like 'pt-%' and id not like 'pt_%'", queryMap);
        if (CollectionUtils.isNotEmpty(items)) {
            for (QueryItem it : items) {
                this.cleanupByTypeParams("rolePrivilegeResource", Params.build(new String[]{"appId", "clearTypes"}, new Object[]{it.getString("id"),
                        Lists.newArrayList("role", "privilege", "resource")}));
            }

        }
        // 清理功能组件
        nativeDao.batchExecute("delete app_user_widget_definition w where w.type=2 and w.app_id is not null and  not exists ( select 1 from app_module m where m.id =w.app_id )", queryMap);
        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        Map<String, Object> queryMap = Maps.newHashMap();
        List<String> clearTypes = (List<String>) params.get("clearTypes");
        if (CollectionUtils.isNotEmpty(clearTypes)) {
            if (clearTypes.size() == 1) {
                queryMap.put("enabled", Integer.parseInt(clearTypes.get(0)));
            }
        } else {
            return ExpectCleanupResult.total(0);
        }
        List<QueryItem> items = nativeDao.query("select count(1) as total from app_module where 1=1 " + (queryMap.containsKey("enabled") ? " and enabled=:enabled " : "") + " and id not like 'pt-%' and id not like 'pt_%'", queryMap, QueryItem.class);
        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
