package com.wellsoft.pt.log.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.log.service.ElasticSearchLogService;
import com.wellsoft.pt.log.support.ElasticSearchSysLogParams;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/6/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/6/3    chenq		2019/6/3		Create
 * </pre>
 */
//@Component(数据仓库接口已经有了，具体参见ElasticsearchLogDataDataStoreQuery.java)
public class AppSystemLogManagerDataStoreQuery extends AbstractDataStoreQueryInterface {
    @Resource
    ElasticSearchLogService elasticSearchLogService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_日志管理_系统日志";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "uuid", "UUID", String.class);
        criteriaMetadata.add("message", "message", "日志内容", String.class);
        criteriaMetadata.add("@timestamp", "@timestamp", "创建时间", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        QueryData queryData = elasticSearchLogService.querySysLogs(getQueryParams(queryContext));
        List<Map> lists = (List<Map>) queryData.getDataList();
        List<QueryItem> queryItemList = Lists.newArrayList();
        for (Map m : lists) {
            QueryItem queryItem = new QueryItem();
            queryItem.putAll(m);
            queryItemList.add(queryItem);
        }
        return queryItemList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        Long total = queryContext.getPagingInfo().getTotalCount();
        if (total == -1) {
            this.query(queryContext);
            return queryContext.getPagingInfo().getTotalCount();
        }
        return total;
    }

    /**
     * @param queryContext
     * @return
     */
    private ElasticSearchSysLogParams getQueryParams(QueryContext queryContext) {
        ElasticSearchSysLogParams queryParams = new ElasticSearchSysLogParams();
        if (queryContext.getQueryParams().get("message") != null
                && StringUtils.isNotBlank(queryContext.getQueryParams().get("message").toString())) {
            queryParams.setMessage(queryContext.getQueryParams().get("message").toString());
        }

        if (queryContext.getQueryParams().get("loglevel") != null
                && StringUtils.isNotBlank(queryContext.getQueryParams().get("loglevel").toString())) {
            queryParams.setLogLevel(queryContext.getQueryParams().get("loglevel").toString());
        }

        if (queryContext.getQueryParams().get("beginTime") != null
                && StringUtils.isNotBlank(queryContext.getQueryParams().get("beginTime").toString())) {
            queryParams
                    .setBeginTime(new Date(Long.parseLong(queryContext.getQueryParams().get("beginTime").toString())));
        }
        if (queryContext.getQueryParams().get("endTime") != null
                && StringUtils.isNotBlank(queryContext.getQueryParams().get("endTime").toString())) {
            queryParams.setEndTime(new Date(Long.parseLong(queryContext.getQueryParams().get("endTime").toString())));
        }
        queryParams.setPage(queryContext.getPagingInfo());
        return queryParams;
    }

}
