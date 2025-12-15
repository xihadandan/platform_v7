package com.wellsoft.pt.fulltext.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.WellException;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.bpm.engine.util.PermissionGranularityUtils;
import com.wellsoft.pt.fulltext.annotation.Pipeline;
import com.wellsoft.pt.fulltext.facade.service.FulltextCategoryFacadeService;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.index.FulltextDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.support.CountAggregationQueryData;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.*;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.TermVector;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Description:
 * es操作帮助类
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/10   Create
 * </pre>
 */
@Component
public class ElasticsearchClientUtil {


    @Value("${elasticsearch.server.host}")
    private String elasticsearchHost;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public boolean isEnable() {
        if (StringUtils.isBlank(elasticsearchHost)) {
            return false;
        }
        return true;
    }

    /**
     * 判断索引是否存在
     *
     * @return
     * @author baozh
     * @date 2021/9/10 14:42
     * @params indexName 索引名称
     */
    public boolean exists(String indexName) {
        try {
            GetIndexRequest indexRequest = new GetIndexRequest(indexName);
            return restHighLevelClient.indices().exists(indexRequest, RequestOptions.DEFAULT);
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
    }

    /**
     * 创建索引
     *
     * @return
     * @author baozh
     * @date 2021/9/10 14:51
     * @params clazz对应结构class
     */
    public boolean create(Class clazz) throws IOException {
        return create(getIndexName(clazz), clazz);

    }

    /**
     * 创建索引
     *
     * @return
     * @author baozh
     * @date 2021/9/10 14:51
     * @params indexName索引名称 ,clazz对应结构class
     */
    public boolean create(String indexName, Class clazz) {
        CreateIndexRequest requestIndex = new CreateIndexRequest(indexName);
        requestIndex.mapping(getMapping(clazz));
        try {
            CreateIndexResponse indexResponse = restHighLevelClient.indices().create(requestIndex, RequestOptions.DEFAULT);
            if (!indexResponse.isAcknowledged()) {//索引创建失败
                return false;
            }
            //给集群一点时间拷贝副本
            TimeUnit.SECONDS.sleep(2);
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
        return true;
    }

    private String getIndexName(Class clazz) {
        Document doc = (Document) clazz.getAnnotation(Document.class);
        return doc.indexName();
    }

    private Map<String, Object> getMapping(Class clazz) {
        Map<String, Object> properties = getMapping(clazz.getDeclaredFields());

        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && !superclass.equals(Object.class)) {
            Map<String, Object> superProperties = getMapping(superclass.getDeclaredFields());
            for (Map.Entry<String, Object> entry : superProperties.entrySet()) {
                if (!properties.containsKey(entry.getKey())) {
                    properties.put(entry.getKey(), entry.getValue());
                }
            }
            superclass = superclass.getSuperclass();
        }

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("properties", properties);
        return result;
    }

    Map<String, Object> getMapping(java.lang.reflect.Field[] fields) {
        Map<String, Object> properties = new ConcurrentHashMap<String, Object>();
        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(Field.class)) {
                if (field.isAnnotationPresent(Pipeline.class)) {
                    Pipeline pipeline = field.getAnnotation(Pipeline.class);
                    if (StringUtils.equals(pipeline.value(), field.getName())) {
                        continue;
                    }
                }
                Map<String, Object> property = new ConcurrentHashMap<String, Object>();
                Field fieldAnnotation = field.getAnnotation(Field.class);
                property.put("type", fieldAnnotation.type().name().toLowerCase());
                if (!"".equals(fieldAnnotation.analyzer())) {
                    property.put("analyzer", fieldAnnotation.analyzer());
                }
                if (!"".equals(fieldAnnotation.searchAnalyzer())) {
                    property.put("search_analyzer", fieldAnnotation.searchAnalyzer());
                }
                if (TermVector.none != fieldAnnotation.termVector()) {
                    property.put("term_vector", fieldAnnotation.termVector().name());
                }
                if (fieldAnnotation.store()) {
                    properties.put(field.getName(), property);
                }
            }
        }
        return properties;
    }

    /**
     * 保存索引
     *
     * @return
     * @author baozh
     * @date 2021/9/10 14:53
     * @params indexName 索引名称 index 索引内容
     */
    public String save(String indexName, Object index) {
        try {
            IndexResponse indexResponse = restHighLevelClient.index(getIndexRequest(indexName, index), RequestOptions.DEFAULT);
            return indexResponse.getId();
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
    }

    private IndexRequest getIndexRequest(String indexName, Object index) {
        IndexRequest request = new IndexRequest(indexName);
        String indexString = JSONObject.toJSONString(index);
        JSONObject jsonObject = JSONObject.parseObject(indexString);
        String id = analysisEntity(jsonObject, index.getClass());
        String pipeline = getPipeline(index.getClass());
        request.id(id);
        request.source(jsonObject, XContentType.JSON);
        if (StringUtils.isNotBlank(pipeline)) {
            request.setPipeline(pipeline);
        }
        return request;
    }

    /**
     * 获取文件抽取理道
     *
     * @param clazz
     * @return
     */
    public static String getPipeline(Class<?> clazz) {
        String pineline = "";
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(Pipeline.class)) {
                pineline = field.getAnnotation(Pipeline.class).value();
                break;
            }
        }

        if (StringUtils.isBlank(pineline)) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null && !superclass.equals(Object.class)) {
                return getPipeline(superclass);
            }
        }
        return pineline;
    }

    /**
     * 解析Entity
     *
     * @param jsonObject 实体数据
     * @param clazz      实体类型
     * @return id
     * @author baozh
     * @date 2021/9/14 15:01
     */

    private String analysisEntity(JSONObject jsonObject, Class clazz) {
        String id = null;

        List<java.lang.reflect.Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        for (java.lang.reflect.Field field : fieldList) {
            if (field.isAnnotationPresent(Id.class)) {
                if (jsonObject.get(field.getName()) == null) {
                    id = SnowFlake.getId() + "";
                    jsonObject.put(field.getName(), id);
                } else {
                    id = jsonObject.get(field.getName()).toString();
                }
            }
            if (field.isAnnotationPresent(Field.class)) {
                if (!field.getAnnotation(Field.class).store()) {
                    jsonObject.remove(field.getName());
                }
            }
        }
        return id;
    }


    /**
     * 批量保存
     *
     * @return
     * @author baozh
     * @date 2021/9/14 14:42
     * @params *@params
     */
    public Boolean batchSave(String indexName, Object... indexs) {
        BulkRequest bulkRequest = new BulkRequest();
        for (Object index : indexs) {
            bulkRequest.add(getIndexRequest(indexName, index));
        }
        try {
            BulkResponse bulkResponse = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            if (RestStatus.OK == bulkResponse.status()) {
                return true;
            }
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
        return false;

    }

    /**
     * 方法描述
     *
     * @param indexName 索引名称
     * @param index     索引内容
     * @return
     * @author baozh
     * @date 2021/9/14 15:09
     */
    public boolean update(String indexName, Object index) {
        String indexString = JSONObject.toJSONString(index);
        JSONObject jsonObject = JSONObject.parseObject(indexString);
        String id = analysisEntity(jsonObject, index.getClass());
        UpdateRequest updateRequest = new UpdateRequest(indexName, id);
        updateRequest.doc(jsonObject);
        try {
            UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            return RestStatus.OK == updateResponse.status();
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (ElasticsearchStatusException e) {
            logger.error("elasticsearch索引状态错误", e);
            throw new WellException("索引记录异常,索引ID:" + id);
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
    }

    /**
     * 删除索引记录
     *
     * @return
     * @author baozh
     * @date 2021/9/14 15:19
     * @params *@params
     */
    public Boolean delete(String indexName, String id) {

        DeleteRequest deleteRequest = new DeleteRequest(indexName, id);
        try {
            DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
            return RestStatus.OK == deleteResponse.status();
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (ElasticsearchStatusException e) {
            logger.error("elasticsearch索引状态错误", e);
            throw new RuntimeException("索引记录异常,索引ID:" + id);
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }

    }

    /**
     * 删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     */
    public boolean deleteByFieldEq(String indexName, String fieldName, Object fieldValue) {
        Assert.hasLength(indexName, "索引名不能为空！");
        Assert.hasLength(fieldName, "字段名不能为空！");
        Assert.notNull(fieldValue, "字段值不能为空！");

        DeleteByQueryRequest deleteByQueryRequest = new DeleteByQueryRequest();
        deleteByQueryRequest.indices(indexName);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName, fieldValue));
        deleteByQueryRequest.setQuery(boolQueryBuilder);
        try {
            BulkByScrollResponse deleteResponse = restHighLevelClient.deleteByQuery(deleteByQueryRequest, RequestOptions.DEFAULT);
            return deleteResponse.getDeleted() > 0;
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (ElasticsearchStatusException e) {
            logger.error("elasticsearch索引状态错误", e);
            throw new RuntimeException("索引记录异常");
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }

    }

    /**
     * 逻辑删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     */
    public boolean logicDeleteByFieldEq(String indexName, String fieldName, Object fieldValue) {
        Assert.hasLength(indexName, "索引名不能为空！");
        Assert.hasLength(fieldName, "字段名不能为空！");
        Assert.notNull(fieldValue, "字段值不能为空！");

        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest();
        updateByQueryRequest.indices(indexName);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName, fieldValue));
        updateByQueryRequest.setQuery(boolQueryBuilder);
        Script script = new Script(ScriptType.INLINE, "painless", "ctx._source.isDelete = 1", Collections.emptyMap());
        updateByQueryRequest.setScript(script);
        try {
            BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
            return bulkByScrollResponse.getUpdated() > 0;
        } catch (IOException e) {
            logger.error("逻辑删除Elasticsearch数据异常: ", e);
            throw new RuntimeException("逻辑删除Elasticsearch数据异常");
        }
    }

    /**
     * 根据记录字段值恢复逻辑删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    public boolean restoreDeleteByFieldEq(String indexName, String fieldName, Object fieldValue) {
        Assert.hasLength(indexName, "索引名不能为空！");
        Assert.hasLength(fieldName, "字段名不能为空！");
        Assert.notNull(fieldValue, "字段值不能为空！");

        UpdateByQueryRequest updateByQueryRequest = new UpdateByQueryRequest();
        updateByQueryRequest.indices(indexName);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName, fieldValue));
        updateByQueryRequest.setQuery(boolQueryBuilder);
        Script script = new Script(ScriptType.INLINE, "painless", "ctx._source.isDelete = 0", Collections.emptyMap());
        updateByQueryRequest.setScript(script);
        try {
            BulkByScrollResponse bulkByScrollResponse = restHighLevelClient.updateByQuery(updateByQueryRequest, RequestOptions.DEFAULT);
            return bulkByScrollResponse.getUpdated() > 0;
        } catch (IOException e) {
            logger.error("恢复逻辑删除Elasticsearch的数据异常: ", e);
            throw new RuntimeException("恢复逻辑删除Elasticsearch的数据异常");
        }
    }

    /**
     * 保存索引
     *
     * @return
     * @author baozh
     * @date 2021/9/10 14:53
     * @params index 索引内容
     */
    public String save(Object index) throws IOException {
        return save(getIndexName(index.getClass()), index);
    }


    /**
     * 查询索引
     *
     * @return
     * @author baozh
     * @date 2021/9/13 17:42
     * @params *@params
     */
    public QueryData searchDocument(IndexRequestParams indexRequestParams, Class clazz, String... indexNames) {
        try {
            FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(RequestSystemContextPathResolver.system());
            SearchSourceBuilder sourceBuilder = getSearchSourceBuilder(indexRequestParams, fulltextSetting, clazz, indexNames);
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(indexNames);
            searchRequest.source(sourceBuilder);
            // 添加统计分类数量聚合到查询
            if (BooleanUtils.isTrue(indexRequestParams.getCountCategory())) {
                sourceBuilder.aggregation(categoryCodesCountAggregation());
            }
            // 添加结果去重折叠配置
            boolean collapseResult = isCollapse(Lists.newArrayList(indexNames), fulltextSetting);
            if (collapseResult) {
                sourceBuilder.collapse(getCollapseBuilder(sourceBuilder.highlighter(), fulltextSetting));
            }
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            // 封装查询到的数据
            List<Object> resultList = extractResults(searchResponse, indexRequestParams, collapseResult);

            QueryData queryData = new QueryData();
            if (BooleanUtils.isTrue(indexRequestParams.getCountCategory())) {
                CountAggregationQueryData countAggregationQueryData = new CountAggregationQueryData();
                countAggregationQueryData.setCountMap(extractCategoryCodeCount(searchResponse));
                queryData = countAggregationQueryData;
            }
            queryData.setDataList(resultList);
            queryData.setPagingInfo(indexRequestParams.getPagingInfo());
            return queryData;
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (ElasticsearchStatusException e) {
            logger.error("Elasticsearch状态异常: ", e);
            List<String> collect = Arrays.stream(indexNames).map(name -> name.replace("_document", "")).collect(Collectors.toList());
            throw new RuntimeException("Elasticsearch索引不存在，索引编码：" + StringUtils.join(collect));
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
    }

    private List<Object> extractResults(SearchResponse searchResponse, IndexRequestParams indexRequestParams, boolean collapseResult) {
        List<Object> resultList = Lists.newArrayList();
        SearchHits hits = searchResponse.getHits();
        Map<String, String> resultFieldMapping = indexRequestParams.getResultFieldMapping();
        int innerHitCount = 0;
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits) {
                if (collapseResult) {
                    SearchHits innerHits = hit.getInnerHits().get("by_index_order");
                    SearchHit[] searchInnerHits = innerHits != null ? innerHits.getHits() : null;
                    if (searchInnerHits != null && searchInnerHits.length > 0) {
                        if (searchInnerHits.length > 1) {
                            innerHitCount++;
                        }
                        hit = searchInnerHits[0];
                    }
                }

                Map<String, Object> result = hit.getSourceAsMap();
                // JSONObject jsonObj = JSONObject.parseObject(hit.getSourceAsString());
                // 设置高亮属性
                hit.getHighlightFields().entrySet().forEach(fieldEntity -> {
                    Text[] fragments = fieldEntity.getValue().getFragments();
                    if (fragments != null && fragments.length > 0) {
                        if (resultFieldMapping != null && resultFieldMapping.containsKey(fieldEntity.getKey())) {
                            result.put(resultFieldMapping.get(fieldEntity.getKey()), Objects.toString(fragments[0], StringUtils.EMPTY));
                        } else {
                            result.put(fieldEntity.getKey(), Objects.toString(fragments[0], StringUtils.EMPTY));
                        }
                    }
                });
                result.put("index", hit.getIndex().replace("_document", ""));
                resultList.add(result);
            }
            PagingInfo pagingInfo = indexRequestParams.getPagingInfo();
            pagingInfo.setTotalCount(hits.getTotalHits().value - innerHitCount);
        }
        return resultList;
    }

    public Map<String, Long> countDocument(IndexRequestParams indexRequestParams, Class<FulltextDocumentIndex> clazz, String... indexNames) {
        Map<String, Long> map = Maps.newLinkedHashMap();
        try {
            FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(RequestSystemContextPathResolver.system());
            SearchSourceBuilder sourceBuilder = getSearchSourceBuilder(indexRequestParams, fulltextSetting, clazz, indexNames);
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.indices(indexNames);
            searchRequest.source(sourceBuilder);
            // 添加统计分类数量聚合到查询
            sourceBuilder.aggregation(categoryCodesCountAggregation());
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            map.putAll(extractCategoryCodeCount(searchResponse));
            return map;
        } catch (NoNodeAvailableException e) {
            logger.error("Elasticsearch服务不可用: ", e);
            throw new RuntimeException("Elasticsearch服务不可用");
        } catch (ElasticsearchStatusException e) {
            logger.error("Elasticsearch状态异常: ", e);
            List<String> collect = Arrays.stream(indexNames).map(name -> name.replace("_document", "")).collect(Collectors.toList());
            throw new RuntimeException("Elasticsearch索引不存在，索引编码：" + StringUtils.join(collect));
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
    }

    private AggregationBuilder categoryCodesCountAggregation() {
        TermsAggregationBuilder aggregation = AggregationBuilders
                .terms("categoryCodes_count")
                .field("categoryCodes");
        return aggregation;
    }

    /**
     * @param searchResponse
     * @return
     */
    private Map<String, Long> extractCategoryCodeCount(SearchResponse searchResponse) {
        Map<String, Long> map = Maps.newLinkedHashMap();
        Terms terms = searchResponse.getAggregations().get("categoryCodes_count");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            String categoryCode = bucket.getKeyAsString();
            long docCount = bucket.getDocCount();
            map.put(categoryCode, docCount);
        }

        long sumOtherDocCount = terms.getSumOfOtherDocCounts();
        map.put("other", sumOtherDocCount);
        return map;
    }

    /**
     * 构建查询条件
     *
     * @return
     * @author baozh
     * @date 2021/9/13 17:42
     * @params *@params
     */
    private SearchSourceBuilder getSearchSourceBuilder(IndexRequestParams requestParams, FulltextSetting fulltextSetting, Class clazz, String... indexNames) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.fetchSource(null, new String[]{"attachment", "attachments.attachment"});
        Set<String> keywordFields = getKeyWordFields(clazz, indexNames);
        PagingInfo pagingInfo = requestParams.getPagingInfo();
        if (pagingInfo == null) {
            pagingInfo = new PagingInfo();
            pagingInfo.setCurrentPage(1);
            pagingInfo.setPageSize(10);
            requestParams.setPagingInfo(pagingInfo);
        }
        int currentPage = pagingInfo.getCurrentPage();
        int pageSize = pagingInfo.getPageSize();

        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        if (!StringUtils.isEmpty(requestParams.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.multiMatchQuery(requestParams.getKeyword(), keywordFields.toArray(new String[]{})));
        }
        // 分类编码
        if (StringUtils.isNotBlank(requestParams.getCategoryCode())) {
            boolQueryBuilder.must(QueryBuilders.termsQuery("categoryCodes", StringUtils.split(requestParams.getCategoryCode(), Separator.SEMICOLON.getValue())));
        } else {
            // 过滤掉没有启用的分类
            List<String> categoryUuids = ApplicationContextHolder.getBean(FulltextCategoryFacadeService.class).listUuidBySystem(RequestSystemContextPathResolver.system())
                    .stream().map(uuid -> uuid.toString()).collect(Collectors.toList());
            if (fulltextSetting != null && CollectionUtils.isNotEmpty(fulltextSetting.getScopeList())) {
                List<String> hiddenCategoryCodes = fulltextSetting.getScopeList().stream().filter(scope -> !scope.isVisible())
                        .map(scope -> scope.getValue()).collect(Collectors.toList());
                Set<String> showCategoryCodes = fulltextSetting.getScopeList().stream().filter(scope -> scope.isVisible())
                        .map(scope -> scope.getValue()).collect(Collectors.toSet());
                showCategoryCodes.addAll(categoryUuids);
                showCategoryCodes.removeAll(hiddenCategoryCodes);
                if (CollectionUtils.isNotEmpty(showCategoryCodes)) {
                    boolQueryBuilder.must(QueryBuilders.termsQuery("categoryCodes", showCategoryCodes));
                }
            }
        }
        // 阅读者
        filterCurrentUser(boolQueryBuilder);

        filterTime(boolQueryBuilder, requestParams, clazz);
        // 过滤系统，有效
        String system = RequestSystemContextPathResolver.system();
        if (StringUtils.isNotBlank(system)) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("system", system));
        }
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (MapUtils.isNotEmpty(requestParams.getFilterMap())) {
            for (Map.Entry<String, Object> entry : requestParams.getFilterMap().entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Collection) {
                    boolQueryBuilder.filter(QueryBuilders.termsQuery(entry.getKey(), (Collection<?>) value));
                } else if (value instanceof String) {
                    if (StringUtils.contains(value.toString(), Separator.SEMICOLON.getValue())) {
                        boolQueryBuilder.filter(QueryBuilders.termsQuery(entry.getKey(), StringUtils.split(value.toString(), Separator.SEMICOLON.getValue())));
                    } else {
                        boolQueryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), value));
                    }
                } else {
                    boolQueryBuilder.filter(QueryBuilders.termQuery(entry.getKey(), value));
                }
            }
        }

        // 构建完成
        sourceBuilder.query(boolQueryBuilder);
        // 分页
        sourceBuilder.from((currentPage - 1) * pageSize);
        sourceBuilder.size(pageSize);
        // 高亮
        if (!StringUtils.isEmpty(requestParams.getKeyword())) {
            String highlightColor = requestParams.getHighlightColor();
            if (StringUtils.isBlank(highlightColor)) {
                highlightColor = "#e75213";
            }
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags(String.format("<font color='%s'>", highlightColor));
            highlightBuilder.postTags("</font>");
            for (String keywordField : keywordFields) {
                highlightBuilder.field(keywordField).fragmentSize(requestParams.getFragmentSize()).numOfFragments(1);
            }
            sourceBuilder.highlighter(highlightBuilder);
        }
        // 增加排序
        if (requestParams.getOrder() != null) {
            sourceBuilder.sort(new FieldSortBuilder(requestParams.getOrder().getProperty()).order(Sort.Direction.ASC == requestParams.getOrder().getDirection() ? SortOrder.ASC : SortOrder.DESC));
        } else {
            // 跟踪相关度排序，默认排序是_score降序
            sourceBuilder.trackScores(true);
            // sourceBuilder.sort(new FieldSortBuilder("_score").order(SortOrder.DESC));
        }
        return sourceBuilder;
    }

    private boolean isCollapse(List<String> indexNames, FulltextSetting fulltextSetting) {
        if (indexNames.contains("workflow_document") && indexNames.contains("form_data_document")) {
            String resultDuplication = fulltextSetting.getResultDuplication();
            if (StringUtils.equals("flow", resultDuplication) || StringUtils.equals("form", resultDuplication)) {
                return true;
            }
        }
        return false;
    }

    private CollapseBuilder getCollapseBuilder(HighlightBuilder highlightBuilder, FulltextSetting fulltextSetting) {
        FieldSortBuilder fieldSortBuilder = new FieldSortBuilder("indexOrder");
        if (StringUtils.equals("flow", fulltextSetting.getResultDuplication())) {
            fieldSortBuilder.order(SortOrder.ASC);
        } else {
            fieldSortBuilder.order(SortOrder.DESC);
        }
        CollapseBuilder collapseBuilder = new CollapseBuilder("dataUuid")
                .setInnerHits(new InnerHitBuilder("by_index_order")
                        .setSize(2).addSort(fieldSortBuilder).setHighlightBuilder(highlightBuilder));
        return collapseBuilder;
    }

    private void filterCurrentUser(BoolQueryBuilder boolQueryBuilder) {
        Set<String> readers = Sets.newHashSet("all");
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        readers.addAll(PermissionGranularityUtils.getSids(userDetails));
        readers.addAll(userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        boolQueryBuilder.must(QueryBuilders.termsQuery("readers", readers));
    }

    private void filterTime(BoolQueryBuilder boolQueryBuilder, IndexRequestParams params, Class clazz) {
        String dateRangeField = params.getDateRangeField();
//        Set<String> timeRangeField = params.getOrder() != null && StringUtils.isNotBlank(params.getOrder().getProperty()) ? Sets.newHashSet(params.getOrder().getProperty()) : getTimeField(clazz);
//        if (params.getEndTime() != null || params.getStartTime() != null) {// 有自定义时间范围的
//            for (String tf : timeRangeField) {
//                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(tf);
//                if (params.getStartTime() != null) {
//                    rangeQueryBuilder.gte(params.getStartTime().getTime());
//                }
//                if (params.getEndTime() != null) {
//                    rangeQueryBuilder.lte(params.getEndTime().getTime());
//                }
//                if (params.getStartTime() != null || params.getEndTime() != null) {
//                    rangeQueryBuilders.add(rangeQueryBuilder);
//                }
//            }
//            if (!rangeQueryBuilders.isEmpty()) {
//                boolQueryBuilder.filter(orQuery(rangeQueryBuilders.toArray(new RangeQueryBuilder[]{})));
//            }
//            return;
//        }
        // 定义时间范围类型的
        if (params.getDateRange() != null && StringUtils.isNotBlank(dateRangeField)) {
            List<RangeQueryBuilder> rangeQueryBuilders = Lists.newArrayList();
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
                case CUSTOM:
                    RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(dateRangeField);
                    if (params.getStartTime() != null) {
                        rangeQueryBuilder.gte(params.getStartTime().getTime());
                    }
                    if (params.getEndTime() != null) {
                        rangeQueryBuilder.lte(params.getEndTime().getTime());
                    }
                    if (params.getStartTime() != null || params.getEndTime() != null) {
                        rangeQueryBuilders.add(rangeQueryBuilder);
                    }
                    break;
            }
            if (rangeQueryBuilders.isEmpty() && startTime != null) {
                RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(dateRangeField);
                rangeQueryBuilder.gte(startTime);
                rangeQueryBuilder.lte(endTime);
                rangeQueryBuilders.add(rangeQueryBuilder);
            }
            if (!rangeQueryBuilders.isEmpty()) {
                boolQueryBuilder.filter(orQuery(rangeQueryBuilders.toArray(new RangeQueryBuilder[]{})));
            }
        }
    }

    public QueryBuilder orQuery(RangeQueryBuilder... rangeQueryBuilders) {
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        for (RangeQueryBuilder rangeQueryBuilder : rangeQueryBuilders) {
            queryBuilder.should(rangeQueryBuilder);
        }
        return queryBuilder;

    }


    private Set<String> getKeyWordFields(Class clazz, String... indexNames) {
        Set<String> keywordFields = Sets.newHashSet(getKeyWordFields(clazz.getDeclaredFields()));

        Class<?> superclass = clazz.getSuperclass();
        while (superclass != null && !superclass.equals(Object.class)) {
            Set<String> superKeywordFields = getKeyWordFields(superclass.getDeclaredFields());
            keywordFields.addAll(superKeywordFields);
            superclass = superclass.getSuperclass();
        }
        keywordFields.add("attachments.attachment.content");
        if (indexNames != null && Sets.newHashSet(indexNames).contains("dms_file_document")
                && !keywordFields.contains("attachment.content")) {
            keywordFields.add("attachment.content");
        }
        return keywordFields;
    }

    private Set<String> getKeyWordFields(java.lang.reflect.Field[] fields) {
        Set<String> keywordFields = Sets.newHashSet();
        for (java.lang.reflect.Field field : fields) {
            if (field.isAnnotationPresent(Field.class)) {
                Field fieldAnnotation = field.getAnnotation(Field.class);
                if (fieldAnnotation.index() && (FieldType.Keyword == fieldAnnotation.type() || FieldType.Text == fieldAnnotation.type())) {
                    if (StringUtils.isNotBlank(fieldAnnotation.name())) {
                        keywordFields.add(fieldAnnotation.name());
                    } else {
                        keywordFields.add(field.getName());
                    }
                }
            }
        }
        return keywordFields;
    }

    private Set<String> getTimeField(Class clazz) {
        Set<String> timeField = Sets.newHashSet();
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Field.class)) {
                Field fieldAnnotation = field.getAnnotation(Field.class);
                if (fieldAnnotation.index() && FieldType.Date == fieldAnnotation.type()) {
                    timeField.add(field.getName());
                }
            }
        }
        return timeField;
    }


    public Map<String, Object> queryById(String indexName, String id) {
        GetRequest request = new GetRequest(indexName, id);
        try {
            GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
            Map<String, Object> source = documentFields.getSourceAsMap();
            return source;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SearchResponse search(SearchRequest request, RequestOptions requestOptions) {
        try {
            return restHighLevelClient.search(request, requestOptions);
        } catch (Exception e) {
            logger.error("Elasticsearch数据异常: ", e);
            throw new RuntimeException("Elasticsearch数据异常");
        }
    }

    public List<String> analyze(String keyword) {
        List<String> terms = Lists.newArrayList();
        try {
            // 构建分析请求
            AnalyzeRequest request = AnalyzeRequest.withGlobalAnalyzer("ik_smart", keyword);
            // 执行请求
            AnalyzeResponse response = restHighLevelClient.indices().analyze(request, RequestOptions.DEFAULT);

            // 处理响应
            List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
            for (AnalyzeResponse.AnalyzeToken token : tokens) {
                terms.add(token.getTerm());
            }
        } catch (Exception e) {
            logger.error("Elasticsearch分词请求异常: ", e);
        }
        return terms;
    }
}
