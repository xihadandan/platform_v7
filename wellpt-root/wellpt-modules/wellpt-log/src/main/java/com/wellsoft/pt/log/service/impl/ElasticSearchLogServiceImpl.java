package com.wellsoft.pt.log.service.impl;

/**
 * es6 升级 es7
 *
 * @author baozh
 * <p>
 * import com.google.common.collect.Lists;
 * import com.wellsoft.context.jdbc.support.QueryData;
 * import com.wellsoft.context.jdbc.support.QueryItem;
 * import com.wellsoft.context.util.ApplicationContextHolder;
 * import com.wellsoft.pt.jpa.criteria.InterfaceCriteria;
 * import com.wellsoft.pt.log.LogConfiguration;
 * <p>
 * import com.wellsoft.pt.log.store.DistributedlogDataStoreRestQuery;
 * import com.wellsoft.pt.log.support.ElasticSearchSysLogParams;
 * import org.apache.commons.collections.CollectionUtils;
 * import org.apache.commons.lang3.ArrayUtils;
 * import org.apache.commons.lang3.StringUtils;
 * import org.apache.commons.lang3.time.DateFormatUtils;
 * import org.apache.commons.lang3.time.DateUtils;
 * import org.elasticsearch.index.query.BoolQueryBuilder;
 * import org.elasticsearch.search.sort.FieldSortBuilder;
 * import org.elasticsearch.search.sort.SortOrder;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.data.domain.PageRequest;
 * import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
 * import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
 * import org.springframework.data.elasticsearch.core.query.SearchQuery;
 * <p>
 * <p>
 * import javax.annotation.Resource;
 * import javax.validation.constraints.NotNull;
 * import java.util.*;
 * <p>
 * import static org.elasticsearch.index.query.QueryBuilders.*;
 **/

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.log.service.ElasticSearchLogService;
import com.wellsoft.pt.log.support.ElasticSearchSysLogParams;
import org.springframework.stereotype.Service;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/1/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/14    chenq		2019/1/14		Create
 * </pre>
 */
@Service
public class ElasticSearchLogServiceImpl implements ElasticSearchLogService {
    @Override
    public QueryData querySysLogs(ElasticSearchSysLogParams params) {
        return null;
    }

    @Override
    public long countSysLogs(ElasticSearchSysLogParams params) {
        return 0;
    }


    /**
     // 只缓存存在的索引
     private Set<String> indexSetExists = Collections.synchronizedSet(new HashSet<String>());

     @Resource private LogConfiguration logConfiguration;

     @Autowired private DistributedlogDataStoreRestQuery restQuery;

     private ElasticsearchTemplate template() {
     try {
     return ApplicationContextHolder.getBean(ElasticsearchTemplate.class);
     } catch (Exception e) {
     throw new RuntimeException("不存在的elasticsearchTemplate");
     }
     }

     @Override
     @SuppressWarnings({"rawtypes", "unchecked"})
     public QueryData querySysLogs(ElasticSearchSysLogParams params) {
     InterfaceCriteria queryContext = new InterfaceCriteria(null, null, null);
     queryContext.setPagingInfo(params.getPage());
     if (params.getBeginTime() != null) {
     queryContext.getQueryParams().put("timestamp", DateFormatUtils.format(params.getBeginTime(), "yyyy-MM-dd HH:mm:ss:SSS"));
     }
     if (params.getEndTime() != null) {
     queryContext.getQueryParams().put("timestamp_END", DateFormatUtils.format(params.getEndTime(), "yyyy-MM-dd HH:mm:ss:SSS"));
     }
     List<QueryItem> queryItems = restQuery.query(queryContext);
     QueryData queryData = new QueryData();
     queryData.setDataList(queryItems);
     return queryData;
     }

     private SearchQuery searchQuery(ElasticSearchSysLogParams params) {
     BoolQueryBuilder boolQueryBuilder = boolQuery();
     int saveDay = logConfiguration.getSaveDays();
     // 验证日期
     validateDate(params, saveDay);
     // 验证索引
     Set<String> indices = explainIndices(params.getBeginTime(), params.getEndTime(), saveDay);
     if (CollectionUtils.isEmpty(indices)) {
     return null;
     }
     boolQueryBuilder.filter(rangeQuery("@timestamp").lte(params.getEndTime()).gte(params.getBeginTime()));
     List<FieldSortBuilder> orderList = Lists.newArrayList(new FieldSortBuilder("@timestamp").order(SortOrder.DESC));
     if (ArrayUtils.isNotEmpty(params.getOrders())) {
     orderList.clear();
     for (ElasticSearchSysLogParams.Order order : params.getOrders()) {
     // 对property进行处理
     String property = order.getProperty();
     if ("timestamp".equals(property)) {
     order.setProperty("@timestamp");
     }
     orderList.add(new FieldSortBuilder(order.getProperty()).order(order.isAsc() ? SortOrder.ASC
     : SortOrder.DESC));
     }
     }
     if (StringUtils.isNotBlank(params.getMessage())) {
     boolQueryBuilder.filter(matchQuery("message", params.getMessage()));
     }
     if (StringUtils.isNotBlank(params.getLogLevel())) {
     boolQueryBuilder.filter(matchQuery("loglevel", params.getLogLevel()));
     }

     NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
     queryBuilder.withQuery(boolQueryBuilder);
     queryBuilder.withIndices(indices.toArray(new String[]{}));
     queryBuilder.withTypes("logs");
     if (CollectionUtils.isNotEmpty(orderList)) {
     for (FieldSortBuilder sortBuilder : orderList) {
     queryBuilder.withSort(sortBuilder);
     }
     }
     // ela 查询页数从0开始
     queryBuilder
     .withPageable(new PageRequest(params.getPage().getCurrentPage() - 1, params.getPage().getPageSize()));
     SearchQuery query = queryBuilder.build();

     return query;
     }

     @Override public long countSysLogs(ElasticSearchSysLogParams params) {
     SearchQuery param = searchQuery(params);
     if (param == null) {
     return 0;
     }
     return template().count(param);
     }

     private void validateDate(ElasticSearchSysLogParams params, int saveDay) {
     // 如果没有时间维度，默认查询当天的范围
     if (params.getEndTime() == null) {
     params.setEndTime(new Date());
     }
     if (params.getBeginTime() == null) {
     params.setBeginTime(DateUtils.truncate(new Date(), Calendar.DATE));
     }
     // 判断查询的时间范围
     Date nowDay = DateUtils.truncate(new Date(), Calendar.DATE);
     Date minDate = DateUtils.addDays(nowDay, -(saveDay - 1));
     // 时间不符合处理
     if (params.getBeginTime().compareTo(params.getEndTime()) > 0) {
     throw new RuntimeException("时间范围查询异常，开始时间会大于结束时间");
     }
     // 时间范围不符合
     if (minDate.compareTo(params.getEndTime()) >= 0) {
     throw new RuntimeException("服务器日志只保存最近的" + logConfiguration.getSaveDays() + "天");
     }
     // 如果start<minDate
     if (minDate.compareTo(params.getBeginTime()) > 0) {
     params.setBeginTime(minDate);
     }
     }

     /**
      * 如何描述该方法
      *
      * @param beginTime
     * @param endTime
     * @return private Set<String> explainIndices(@NotNull Date beginTime, @NotNull Date endTime, int saveDays) {
    String prefix = "wellpt-log-";
    List<String> indices = new ArrayList<String>();
    String startIndice = prefix + DateFormatUtils.format(beginTime, "yyyy.MM.dd");
    indices.add(startIndice);
    String endIndice = prefix + DateFormatUtils.format(endTime, "yyyy.MM.dd");
    if (!endIndice.equals(startIndice)) {
    for (int i = 0; i < saveDays; i++) {
    Date nextDay = DateUtils.addDays(beginTime, i + 1);
    if (DateUtils.isSameDay(nextDay, endTime)) {
    break;
    }
    String indice = prefix + DateFormatUtils.format(nextDay, "yyyy.MM.dd");
    indices.add(indice);
    }
    }
    Set<String> result = new HashSet<String>();
    // 遍历确认是否存在
    for (String indice : indices) {
    if (indicesExists(indice)) {
    result.add(indice);
    }
    }
    return result;
    }

    private boolean indicesExists(String indice) {
    if (indexSetExists.contains(indice)) {
    // 存在
    return true;
    } else {
    // 确认是否存在
    boolean flag = template().indexExists(indice);
    if (flag) {
    // 添加到缓存中
    indexSetExists.add(indice);
    return true;
    }
    }
    return false;
    }**/
}
