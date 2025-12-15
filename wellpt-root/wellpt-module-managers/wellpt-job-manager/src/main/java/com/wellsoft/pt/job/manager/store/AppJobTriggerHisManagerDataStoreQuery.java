package com.wellsoft.pt.job.manager.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.task.service.QrtzFiredTriggerHisService;
import org.springframework.stereotype.Component;

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
@Component
public class AppJobTriggerHisManagerDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Resource
    QrtzFiredTriggerHisService qrtzFiredTriggerHisService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_定时任务触发历史";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("entryId", "t1.entry_id", "ENTRY_ID", String.class);
        criteriaMetadata.add("instanceName", "t1.instance_name", "运行实例ID", String.class);
        criteriaMetadata.add("firedTime", "t1.fired_time", "触发时间", Long.class);
        criteriaMetadata.add("state", "t1.state", "状态", String.class);
        criteriaMetadata.add("executeTime", "t1.execute_time", "执行Job时间", Date.class);
        criteriaMetadata.add("finishTime", "t1.finish_time", "执行结束时间", Date.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        return qrtzFiredTriggerHisService.listQueryItemByNameSQLQuery(
                "appModuleJobTriggerHisManagerQuery", getQueryParams(queryContext),
                queryContext.getPagingInfo());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getPagingInfo().getTotalCount();
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        queryParams.put("jobName", queryContext.getQueryParams().get("jobName"));
        queryParams.put("jobGroup", SpringSecurityUtils.getCurrentTenantId());
        return queryParams;
    }

}
