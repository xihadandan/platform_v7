package com.wellsoft.pt.integration.trigger.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/12/21
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/12/21    chenq		2019/12/21		Create
 * </pre>
 */
@Component
public class TableTriggerDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Override
    public String getQueryName() {
        return "平台管理_数据交换_同步触发器管理";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("table_name", "table_name", "表名", String.class);
        criteriaMetadata.add("trigger_name", "trigger_name", "触发器", String.class);
        criteriaMetadata.add("status", "status", "状态", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("listTableTigInfo",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        Long total = queryContext.getPagingInfo().getTotalCount();
        return total != -1 ? total : queryContext.getNativeDao().countByNamedQuery("listTableTigInfo",
                getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        // whereSql中参数转换成大写
        String triggerName = (String) queryParams.get("trigger_name");
        if (StringUtils.isNotEmpty(triggerName)) {
            queryParams.put("trigger_name", triggerName.toUpperCase());
        }
        String tableName = (String) queryParams.get("table_name");
        if (StringUtils.isNotEmpty(tableName)) {
            queryParams.put("table_name", tableName.toUpperCase());
        }
        queryParams.put("keyword", queryContext.getKeyword());
        String whereSql = queryContext.getWhereSqlString();
        queryParams.put("whereSql", whereSql.replaceFirst("table_name", "t.table_name"));
        queryParams.put("orderBy", queryContext.getOrderString());
        queryParams.put("tableName", queryContext.getKeyword());
        return queryParams;
    }
}
