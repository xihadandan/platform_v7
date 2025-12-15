package com.wellsoft.pt.app.cleanup;

import com.google.common.collect.Lists;
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
 * 2024年06月05日   chenq	 Create
 * </pre>
 */
@Service
public class AppSystemCleanupProvider extends AbstractDataCleanupDataCleanupProvider {
    @Override
    public String getType() {
        return "appSystemInfo";
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ExpectCleanupResult cleanup(Params params) {
        Map<String, Object> queryMap = Maps.newHashMap();
        String appId = params.optString("appId");
        if (StringUtils.isNotBlank(appId)) {
            queryMap.put("appId", appId);
        }
        queryMap.put("excludes", INNER_PRODUCT_ID);

        ExpectCleanupResult result = ExpectCleanupResult.total(nativeDao.batchExecute("delete from app_system_info where " +
                "system not in ( :excludes ) and ( system not like 'pt-%' and system not like 'pt_%' ) "
                + (StringUtils.isNotBlank(appId) ? " and system =:appId" : ""), queryMap));
        List<String> tables = Lists.newArrayList("app_system_login_page_def", "app_system_page_setting", "app_system_login_policy",
                "app_system_page_theme", "app_system_param");
        for (String table : tables) {
            nativeDao.batchExecute("delete from " + table + " where " +
                    "system not in ( :excludes ) and ( system not like 'pt-%' and system not like 'pt_%' ) "
                    + (StringUtils.isNotBlank(appId) ? " and system =:appId" : ""), queryMap);
        }

        return result;
    }

    @Override
    public ExpectCleanupResult expectCleanupRows(Params params) {
        Map<String, Object> queryMap = Maps.newHashMap();
        queryMap.put("excludes", INNER_PRODUCT_ID);
        String appId = params.optString("appId");
        if (StringUtils.isNotBlank(appId)) {
            queryMap.put("appId", appId);
        }
        List<QueryItem> items = nativeDao.query("select count(1) as total from app_system_info where " +
                " system not in ( :excludes ) and ( system not like 'pt-%' and system not like 'pt_%' ) " + (StringUtils.isNotBlank(appId) ? " and system =:appId" : ""), queryMap, QueryItem.class);
        return ExpectCleanupResult.total(items.get(0).getLong("total"));
    }
}
