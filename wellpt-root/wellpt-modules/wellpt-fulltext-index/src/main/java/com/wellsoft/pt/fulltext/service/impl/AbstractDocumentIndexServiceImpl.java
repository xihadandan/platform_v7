package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.fulltext.annotation.FilterTime;
import com.wellsoft.pt.fulltext.annotation.Keyword;
import com.wellsoft.pt.fulltext.index.BaseDocumentIndex;
import com.wellsoft.pt.fulltext.index.FulltextDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.service.DocumentIndexService;
import com.wellsoft.pt.fulltext.service.FulltextDocumentIndexService;
import com.wellsoft.pt.fulltext.support.DataFiller;
import com.wellsoft.pt.fulltext.vo.DeleteDocumentIndexVo;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.Future;

/**
 * import com.wellsoft.context.jdbc.support.PagingInfo;
 * import com.wellsoft.context.jdbc.support.QueryData;
 * <p>
 * import com.wellsoft.pt.fulltext.request.IndexRequestParams;
 * import com.wellsoft.pt.fulltext.service.DocumentIndexService;
 * import com.wellsoft.pt.fulltext.support.HighlightResultMapper;
 * import org.apache.commons.collections.CollectionUtils;
 * import org.apache.commons.lang3.StringUtils;
 * import org.apache.commons.lang3.time.DateUtils;
 * import org.elasticsearch.client.transport.NoNodeAvailableException;
 * import org.elasticsearch.index.query.BoolQueryBuilder;
 * import org.elasticsearch.index.query.RangeQueryBuilder;
 * import org.elasticsearch.search.highlight.HighlightBuilder;
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.data.domain.Page;
 * import org.springframework.data.domain.PageRequest;
 * <p>
 * import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
 * import org.springframework.data.elasticsearch.core.query.IndexQuery;
 * import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
 * import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
 * import org.springframework.data.elasticsearch.core.query.SearchQuery;
 * <p>
 * <p>
 * import java.util.concurrent.Callable;
 * import java.util.concurrent.Future;
 * import java.util.concurrent.ScheduledExecutorService;
 * <p>
 * import static org.elasticsearch.index.query.QueryBuilders.*;
 **/

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年09月08日   chenq	 Create
 * </pre>
 */
public abstract class AbstractDocumentIndexServiceImpl<T extends BaseDocumentIndex> implements DocumentIndexService<T> {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    FulltextDocumentIndexService fulltextDocumentIndexService;

    /**
     * es6 升级到  es7
     *
     * @author baozh
     * * @Autowired
     * ElasticsearchTemplate elasticsearchTemplate;
     * @Autowired ScheduledExecutorService scheduledExecutorService;
     **/

    Class<T> documentIndexClazz;

    String categoryCode;

    String indexName;

    String type;

    Set<String> keywordFields = Sets.newHashSet();

    Set<String> timeField = Sets.newHashSet();

    public AbstractDocumentIndexServiceImpl() {
        super();
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                documentIndexClazz = (Class<T>) params[0];
                Document document = documentIndexClazz.getAnnotation(Document.class);
                categoryCode = document.indexName().replace(FulltextDocumentIndexServiceImpl.DOC, "");
                indexName = document.indexName();
                type = document.type();
                List<Field> fieldList = Lists.newArrayList(documentIndexClazz.getSuperclass().getDeclaredFields());
                fieldList.addAll(Arrays.asList(documentIndexClazz.getDeclaredFields()));
                for (Field field : fieldList) {
                    if (field.isAnnotationPresent(Keyword.class)) {
                        keywordFields.add(field.getName());
                    }
                    if (field.isAnnotationPresent(FilterTime.class)) {
                        timeField.add(field.getName());
                    }
                }

            }

        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }


    @Override
    public Future<String> asyncIndex(T index) {
        if (CollectionUtils.isEmpty(index.getCategoryCodes())) {
            index.setCategoryCodes(Sets.newHashSet(categoryCode));
        }
        return fulltextDocumentIndexService.asyncSaveIndex(index);
    }

    /**
     * 索引文档
     *
     * @param index
     * @param dataFiller
     * @return
     */
    @Override
    public Future<String> asyncIndex(T index, DataFiller dataFiller) {
        if (CollectionUtils.isEmpty(index.getCategoryCodes())) {
            index.setCategoryCodes(Sets.newHashSet(categoryCode));
        }
        return fulltextDocumentIndexService.asyncSaveIndex(index, dataFiller);
    }

    @Override
    public void asyncBulkIndex(List<T> indexes) {
        if (CollectionUtils.isEmpty(indexes)) {
            return;
        }
        List<FulltextDocumentIndex> list = new ArrayList<>();
        for (T index : indexes) {
            indexes.add(index);
        }
        fulltextDocumentIndexService.asyncBatchSaveIndex(indexName, list);
    }

    @Override
    public String index(T index) {
        if (CollectionUtils.isEmpty(index.getCategoryCodes())) {
            index.setCategoryCodes(Sets.newHashSet(categoryCode));
        }
        return fulltextDocumentIndexService.saveIndex(index);
    }

    @Override
    public void bulkIndex(List<T> indexes) {
        if (CollectionUtils.isEmpty(indexes)) {
            return;
        }
        List<FulltextDocumentIndex> list = new ArrayList<>();
        for (T index : indexes) {
            indexes.add(index);
        }
        String indexName = indexes.get(0).getClass().getAnnotation(Document.class).indexName();
        fulltextDocumentIndexService.batchSaveIndex(indexName, list);
    }

    @Override
    public QueryData query(IndexRequestParams params) {
        params.setCategoryCode(categoryCode);
        return fulltextDocumentIndexService.query(params);
    }

    @Override
    public long count(IndexRequestParams params) {
        return 0;
    }

    @Override
    public long countByFieldEq(String fieldName, Object fieldValue) {
        return fulltextDocumentIndexService.countByFieldEq(indexName, fieldName, fieldValue);
    }

    @Override
    public void deleteIndex(DeleteDocumentIndexVo vo) {
        vo.setIndexName(indexName);
        fulltextDocumentIndexService.asyncDelete(vo);
    }

    @Override
    public void deleteIndexByFieldEq(String fieldName, Object fieldValue) {
        fulltextDocumentIndexService.deleteByFieldEq(indexName, fieldName, fieldValue);
    }

    @Override
    public void logicDeleteIndexByFieldEq(String fieldName, Object fieldValue) {
        fulltextDocumentIndexService.logicDeleteByFieldEq(indexName, fieldName, fieldValue);
    }

    /**
     * 恢复逻辑删除的索引文档
     *
     * @param fieldName
     * @param fieldValue
     */
    @Override
    public void restoreDeleteIndexByFieldEq(String fieldName, Object fieldValue) {
        fulltextDocumentIndexService.restoreDeleteByFieldEq(indexName, fieldName, fieldValue);
    }

    @Override
    public Map<String, Long> distinctByField(String fieldName) {
        return fulltextDocumentIndexService.distinctByField(fieldName, indexName);
    }

    @Override
    public Map<String, Long> distinctByFieldAndSystem(String fieldName, String system) {
        return fulltextDocumentIndexService.distinctByFieldAndSystem(fieldName, system, indexName);
    }

    /***
     *  es6 升级到  es7
     *   @author baozh
     @Override public String index(T index) {
     IndexQueryBuilder builder = new IndexQueryBuilder();
     builder.withObject(index);
     try {
     return elasticsearchTemplate.index(builder.build());
     } catch (NoNodeAvailableException e) {
     logger.error("Elasticsearch服务不可用: ", e);
     throw new RuntimeException("Elasticsearch服务不可用");
     } catch (Exception e) {
     logger.error("Elasticsearch数据异常: ", e);
     throw new RuntimeException("Elasticsearch数据异常");
     }

     }

     @Override public void bulkIndex(List<T> indexes) {
     List<IndexQuery> indexQueries = Lists.newArrayList();
     for (T idx : indexes) {
     IndexQueryBuilder builder = new IndexQueryBuilder();
     builder.withObject(idx);
     indexQueries.add(builder.build());
     }
     try {
     elasticsearchTemplate.bulkIndex(indexQueries);
     } catch (NoNodeAvailableException e) {
     logger.error("Elasticsearch服务不可用: ", e);
     } catch (Exception e) {
     logger.error("Elasticsearch数据异常: ", e);
     }
     }

     @Override public Future<String> asyncIndex(final T index) {
     Future<String> result = scheduledExecutorService.submit(new Callable<String>() {
     @Override public String call() throws Exception {
     IndexQueryBuilder builder = new IndexQueryBuilder();
     builder.withObject(index);
     try {
     return elasticsearchTemplate.index(builder.build());
     } catch (NoNodeAvailableException e) {
     logger.error("Elasticsearch服务不可用: ", e);
     } catch (Exception e) {
     logger.error("Elasticsearch数据异常: ", e);
     }
     return null;
     }
     });
     return result;
     }

     @Override public void asyncBulkIndex(final List<T> indexes) {
     if (CollectionUtils.isEmpty(indexes)) {
     return;
     }
     scheduledExecutorService.submit(new Callable<Object>() {
     @Override public Object call() throws Exception {
     List<IndexQuery> indexQueries = Lists.newArrayList();
     for (T idx : indexes) {
     IndexQueryBuilder builder = new IndexQueryBuilder();
     builder.withObject(idx);
     indexQueries.add(builder.build());
     }
     try {
     elasticsearchTemplate.bulkIndex(indexQueries);
     } catch (NoNodeAvailableException e) {
     logger.error("Elasticsearch服务不可用: ", e);
     } catch (Exception e) {
     logger.error("Elasticsearch数据异常: ", e);
     }
     return null;
     }
     });
     }

     @Override public QueryData query(IndexRequestParams params) {
     SearchQuery searchQuery = searchQuery(params);
     QueryData queryData = new QueryData();
     if (searchQuery != null) {
     Page<T> page = null;
     try {
     page = elasticsearchTemplate.queryForPage(searchQuery, documentIndexClazz, new HighlightResultMapper());
     } catch (NoNodeAvailableException e) {
     logger.error("Elasticsearch服务不可用: ", e);
     throw new RuntimeException("Elasticsearch服务不可用");
     } catch (Exception e) {
     logger.error("Elasticsearch数据异常: ", e);
     throw new RuntimeException("Elasticsearch数据异常");
     }
     queryData.setDataList(page.getContent());
     queryData.setPagingInfo(new PagingInfo(page.getNumber(), params.getPagingInfo().getPageSize()));
     PagingInfo info = params.getPagingInfo();
     if (info != null) {
     info.setTotalCount(page.getTotalElements());
     }
     queryData.getPagingInfo().setTotalCount(page.getTotalElements());
     } else {
     queryData.setDataList(new ArrayList<>());
     PagingInfo info = params.getPagingInfo();
     if (info != null) {
     info.setTotalCount(0);
     }
     }
     return queryData;
     }

     @Override public long count(IndexRequestParams params) {
     try {
     return elasticsearchTemplate.count(searchQuery(params));
     } catch (NoNodeAvailableException e) {
     logger.error("Elasticsearch服务不可用: ", e);
     throw new RuntimeException("Elasticsearch服务不可用");
     } catch (Exception e) {
     logger.error("Elasticsearch数据异常: ", e);
     throw new RuntimeException("Elasticsearch数据异常");
     }
     }

     private SearchQuery searchQuery(IndexRequestParams params) {
     BoolQueryBuilder boolQueryBuilder = boolQuery();
     filterTime(boolQueryBuilder, params);
     boolQueryBuilder.must(multiMatchQuery(params.getKeyword(), keywordFields.toArray(new String[]{})));
     NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
     queryBuilder.withQuery(boolQueryBuilder);
     queryBuilder.withIndices(indexName);
     queryBuilder.withTypes(type);
     HighlightBuilder.Field[] hiFields = new HighlightBuilder.Field[keywordFields.size()];
     int i = 0;
     for (String kf : keywordFields) {
     hiFields[i++] = new HighlightBuilder.Field(kf).fragmentSize(params.getFragmentSize()).numOfFragments(1);
     }
     queryBuilder.withHighlightFields(hiFields);
     //order(queryBuilder, params);
     // ela 查询页数从0开始
     queryBuilder
     .withPageable(new PageRequest(params.getPagingInfo().getCurrentPage() - 1, params.getPagingInfo().getPageSize()));
     SearchQuery query = queryBuilder.build();

     return query;
     }


     private void filterTime(BoolQueryBuilder boolQueryBuilder, IndexRequestParams params) {

     Set<String> timeRangeField = params.getOrder() != null && StringUtils.isNotBlank(params.getOrder().getProperty()) ? Sets.newHashSet(params.getOrder().getProperty()) : this.timeField;
     List<RangeQueryBuilder> rangeQueryBuilders = Lists.newArrayList();
     if (params.getEndTime() != null || params.getStartTime() != null) {// 有自定义时间范围的
     for (String tf : timeRangeField) {
     RangeQueryBuilder rangeQueryBuilder = rangeQuery(tf);
     if (params.getStartTime() != null) {
     rangeQueryBuilder.gte(params.getStartTime());
     }
     if (params.getEndTime() != null) {
     rangeQueryBuilder.lte(params.getEndTime());
     }
     if (params.getStartTime() != null || params.getEndTime() != null) {
     rangeQueryBuilders.add(rangeQueryBuilder);
     }
     }
     if (!rangeQueryBuilders.isEmpty()) {
     boolQueryBuilder.filter(orQuery(rangeQueryBuilders.toArray(new RangeQueryBuilder[]{})));
     }
     return;
     }
     if (params.getDateRange() != null) { // 定义时间范围类型的
     Date startTime = null;
     Date endTime = new Date();
     switch (params.getDateRange()) {
     case ONE_DAY:
     startTime = DateUtils.addDays(endTime, -1);
     break;
     case ONE_WEEK:
     startTime = DateUtils.addWeeks(endTime, -1);
     break;
     case ONE_MONTH:
     startTime = DateUtils.addMonths(endTime, -1);
     break;
     case ONE_YEAR:
     startTime = DateUtils.addYears(endTime, -1);
     break;
     }
     for (String tf : timeRangeField) {
     RangeQueryBuilder rangeQueryBuilder = rangeQuery(tf);
     rangeQueryBuilder.gte(startTime);
     rangeQueryBuilder.lte(endTime);
     rangeQueryBuilders.add(rangeQueryBuilder);
     }
     if (!rangeQueryBuilders.isEmpty()) {
     boolQueryBuilder.filter(orQuery(rangeQueryBuilders.toArray(new RangeQueryBuilder[]{})));
     }
     }

     }**/
}
