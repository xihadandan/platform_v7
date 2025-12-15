package com.wellsoft.pt.app.cleanup;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.cleanup.AbstractDataCleanupDataCleanupProvider;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.app.cleanup.AppProductCleanupProvider.INNER_PRODUCT_ID;

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
public class AppPageDefinitionCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "appPageDefinition";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        String appId = params.optString("appId");
        Map<String, Object> queryMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的页面
            queryMap.put("appId", appId);
        } else {
            // 不指定模块的情况下，要排除平台内置模块的或者内置系统
            queryMap.put("excludeAppIds", INNER_PRODUCT_ID);
        }

        this.nativeDao.batchExecute("delete from app_function f  where exists ("
                        + " select 1 from app_page_resource r ,app_page_definition p where r.app_function_uuid=f.uuid and " +
                        " r.app_page_uuid = p.uuid and "
                        + (queryMap.containsKey("appId") ? " p.app_id = :appId" :
                        " (p.app_id is null or ( p.app_id not in (:excludeAppIds) and ( p.app_id not like 'pt-%' and p.app_id not like 'pt_%' ))) ")
                        + ")"
                , queryMap);

        this.nativeDao.batchExecute("delete from app_page_resource r  where exists ("
                        + " select 1 from app_page_definition p where r.app_page_uuid = p.uuid and "
                        + (queryMap.containsKey("appId") ? " p.app_id = :appId" :
                        " ( p.app_id is null or ( p.app_id not in (:excludeAppIds) and ( p.app_id not like 'pt-%' and p.app_id not like 'pt_%' )))  ")
                        + ")"
                , queryMap);

        this.nativeDao.batchExecute("delete from app_widget_definition r where exists ("
                        + " select 1 from app_page_definition p where r.app_page_id = p.id and "
                        + (queryMap.containsKey("appId") ? " p.app_id = :appId" :
                        " ( p.app_id is null or ( p.app_id not in (:excludeAppIds) and ( p.app_id not like 'pt-%' and p.app_id not like 'pt_%' )))  ")
                        + ")"
                , queryMap);

        int count = this.nativeDao.batchExecute("delete from app_page_definition p where "
                        + (queryMap.containsKey("appId") ? " p.app_id = :appId" :
                        " ( p.app_id is null or ( p.app_id not in (:excludeAppIds) and ( p.app_id not like 'pt-%' and p.app_id not like 'pt_%' )))  ")
                , queryMap);

        return ExpectCleanupResult.total(count);
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        String appId = params.optString("appId");
        Map<String, Object> queryMap = Maps.newHashMap();
        if (StringUtils.isNotBlank(appId)) {
            // 删除指定模块下的页面
            queryMap.put("appId", appId);
        } else {
            // 不指定模块的情况下，要排除平台内置模块的或者内置系统
            queryMap.put("excludeAppIds", INNER_PRODUCT_ID);
        }
        List<QueryItem> items = this.nativeDao.query("select count(1) as total from app_page_definition p where "
                        + (queryMap.containsKey("appId") ? " p.app_id = :appId" :
                        " p.app_id is null or ( p.app_id not in (:excludeAppIds) and ( p.app_id not like 'pt-%' and p.app_id not like 'pt_%' ))  ")
                , queryMap, QueryItem.class);

        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
