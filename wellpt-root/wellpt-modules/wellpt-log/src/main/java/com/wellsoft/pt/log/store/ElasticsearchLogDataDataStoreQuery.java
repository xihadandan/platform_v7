package com.wellsoft.pt.log.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.log.service.ElasticSearchLogService;
import com.wellsoft.pt.log.support.ElasticSearchSysLogParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
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
public class ElasticsearchLogDataDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    ElasticSearchLogService elasticSearchLogService;

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
        criteriaMetadata.add("uuid", "uuid", "uuid", String.class);
        criteriaMetadata.add("loglevel", "loglevel", "日志等级", String.class);
        criteriaMetadata.add("message", "message", "日志内容", String.class);
        criteriaMetadata.add("timestamp", "timestamp", "创建时间", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractDataStoreQueryInterface#query(QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        ElasticSearchSysLogParams params = getElasticSearchSysLogParams(queryContext);
        params.setPage(queryContext.getPagingInfo());
        QueryData queryData = elasticSearchLogService.querySysLogs(params);
        List<QueryItem> queryItemList = Lists.newArrayList();
        List<Map> list = (List<Map>) queryData.getDataList();
        for (Map m : list) {
            QueryItem temp = new QueryItem();
            temp.putAll(m);
            queryItemList.add(temp);
        }
        return queryItemList;
    }

    private ElasticSearchSysLogParams getElasticSearchSysLogParams(QueryContext queryContext) {
        ElasticSearchSysLogParams params = new ElasticSearchSysLogParams();
        Map<String, Object> queryParams = getQueryParams(queryContext);
        if (StringUtils.isNotBlank((String) queryParams.get("message"))) {
            params.setMessage((String) queryParams.get("message"));
        }
        if (StringUtils.isNotBlank((String) queryParams.get("loglevel"))) {
            params.setLogLevel((String) queryParams.get("loglevel"));
        }
        //        if (queryParams.get("createTime") != null) {
        //            String[] times = (String[]) queryParams.get("createTime");
        //            if (StringUtils.isNotBlank(times[0])) {
        //                params.setBeginTime(timeParser(times[0]));
        //            }
        //            if (StringUtils.isNotBlank(times[1])) {
        //                params.setEndTime(timeParser(times[1]));
        //            }
        //        }
        // 前端配置时间段没有正确处理
        Date start = params.getBeginTime();
        if (start == null) {
            String startStr = (String) queryParams.get("timestamp");
            if (startStr != null) {
                // 如果开始时间大于当前时间，抛出异常
                Date startDate = timeParser(startStr);
                if (startDate != null && startDate.getTime() > System.currentTimeMillis()) {
                    throw new RuntimeException("开始时间大于当前时间，参数不符合要求");
                } else {
                    params.setBeginTime(startDate);
                }
            }
        }
        Date end = params.getEndTime();
        if (end == null) {
            String endStr = (String) queryParams.get("timestamp_END");
            Date endDate = null;
            if (endStr != null) {
                endDate = timeParser(endStr);
            }
            // 结束时间大于当前时间。则为当前时间
            if (endDate != null && endDate.getTime() > System.currentTimeMillis()) {
                params.setEndTime(new Date());
            } else {
                params.setEndTime(endDate);
            }
        }
        // 添加pageInfo信息，处理空指针报错 wangrf
        if (queryContext.getPagingInfo() != null) {
            params.setPage(queryContext.getPagingInfo());
        }
        return params;
    }

    private Date timeParser(String timeStr) {
        try {
            return DateUtils.parseDate(timeStr, "yyyy-MM-dd HH:mm:ss:SSS", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
                    "yyyy-MM-dd HH", "yyyy-MM-dd", "yyyy年MM月dd日 HH时mm分ss秒SSS毫秒", "yyyy年MM月dd日 HH时mm分ss秒",
                    "yyyy年MM月dd日 HH时mm分", "yyyy年MM月dd日 HH时", "yyyy年MM月dd日");
        } catch (Exception e) {
            LOGGER.error("系统日志时间转换异常，无法转换时间字符串：" + timeStr);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        Long total = queryContext.getPagingInfo().getTotalCount();
        return total != -1 ? total : elasticSearchLogService.countSysLogs(getElasticSearchSysLogParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        return queryParams;
    }
}
