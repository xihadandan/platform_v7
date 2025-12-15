package com.wellsoft.distributedlog.es.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.distributedlog.Constants;
import com.wellsoft.distributedlog.dto.LogDTO;
import com.wellsoft.distributedlog.es.entity.LogEntity;
import com.wellsoft.distributedlog.es.service.LogService;
import com.wellsoft.distributedlog.request.LogRequestParams;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Description: 日志数据服务类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2021年06月30日   chenq	 Create
 * </pre>
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    ElasticsearchOperations elasticsearchOperations;

    @Resource(name = "elasticsearchClient")
    RestHighLevelClient client;


    @Override
    public void bulkIndex(Map<String, List<IndexQuery>> indexMap) {
        Set<String> keys = indexMap.keySet();
        for (String k : keys) {
            elasticsearchOperations.bulkIndex(indexMap.get(k), IndexCoordinates.of(k));
        }
    }

    @Override
    public void bulkIndex(ArrayList<LogDTO> logDTOs) {
        if (!CollectionUtils.isEmpty(logDTOs)) {
            Map<String, List<IndexQuery>> map = Maps.newHashMap();
            for (LogDTO s : logDTOs) {
                LogEntity logEntity = new LogEntity();
                BeanUtils.copyProperties(s, logEntity);
                String indexName = Constants.ES_LOG_INDEX_NAME_PREFIX + DateFormatUtils.format(logEntity.getLogTime(), "yyyyMMdd");
                if (!map.containsKey(indexName)) {
                    map.put(indexName, Lists.<IndexQuery>newArrayList());
                }
                map.get(indexName).add(new IndexQueryBuilder()
                        .withObject(logEntity)
                        .build());
            }
            this.bulkIndex(map);
        }

    }

    @Override
    public boolean ping() {
        try {
            return client.ping(RequestOptions.DEFAULT);
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public Long count(LogRequestParams params) {
        NativeSearchQueryBuilder queryBuilder = buildQuery(params);
        if (queryBuilder == null) {
            return 0L;
        }
        return elasticsearchOperations.count(queryBuilder.build(), time2IndexCoordinates(params.getBeginTime(), params.getEndTime()));
    }

    private NativeSearchQueryBuilder buildQuery(LogRequestParams params) {
        if (params.getBeginTime() == null || params.getEndTime() == null) {
            return null;
        }
        NativeSearchQueryBuilder builder = new NativeSearchQueryBuilder();

        // 时间范围
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(rangeQuery("log_time").lte(params.getEndTime()).gte(params.getBeginTime()));

        if (StringUtils.isNotBlank(params.getContent())) {
            boolQueryBuilder.filter(matchQuery("content", params.getContent()));
        }
        if (params.getLogLevel() != null) {
            boolQueryBuilder.filter(termsQuery("log_level", params.getLogLevel()));
        }
        if (StringUtils.isNotBlank(params.getTraceId())) {
            boolQueryBuilder.filter(termQuery("trace_id", params.getTraceId()));
        }
        if (StringUtils.isNotBlank(params.getPreIp())) {
            boolQueryBuilder.filter(termQuery("pre_id", params.getPreIp()));
        }
        if (StringUtils.isNotBlank(params.getIp())) {
            boolQueryBuilder.filter(termQuery("ip", params.getIp()));
        }
        if (StringUtils.isNotBlank(params.getApp())) {
            boolQueryBuilder.filter(termQuery("app", params.getApp()));
        }
        if (StringUtils.isNotBlank(params.getPreApp())) {
            boolQueryBuilder.filter(termQuery("pre_app", params.getPreIp()));
        }
        builder.withFilter(boolQueryBuilder);
        return builder;
    }

    @Override
    public SearchHits<LogEntity> query(LogRequestParams params, PageRequest pageRequest) {
        NativeSearchQueryBuilder builder = buildQuery(params);
        if (builder == null) {
            return null;
        }
        builder.withPageable(pageRequest);
        builder.withSort(SortBuilders.fieldSort("log_time").order(SortOrder.DESC));
        return elasticsearchOperations.search(builder.build(), LogEntity.class,
                time2IndexCoordinates(params.getBeginTime(), params.getEndTime()));
    }

    private IndexCoordinates time2IndexCoordinates(Date beginTime, Date endTime) {
        Long minus = DateUtils.truncate(endTime, Calendar.DATE).getTime() - DateUtils.truncate(beginTime, Calendar.DATE).getTime();
        Long days = (minus / 86400000) + 1;
        List<String> indexs = Lists.newArrayListWithCapacity(days.intValue());
        for (int i = 0; i < days; i++) {
            String idx = Constants.ES_LOG_INDEX_NAME_PREFIX + DateFormatUtils.format(DateUtils.addDays(beginTime, i), Constants.LOG_INDEX_DATE_FORMATE);
            if (elasticsearchOperations.indexOps(IndexCoordinates.of(idx)).exists()) {
                indexs.add(idx);
            }
        }
        return IndexCoordinates.of(indexs.toArray(new String[]{}));
    }
}
