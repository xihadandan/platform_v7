package com.wellsoft.pt.fulltext.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.basicdata.datadict.bean.DataDictionaryBean;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.fulltext.facade.service.FulltextSettingFacadeService;
import com.wellsoft.pt.fulltext.index.FormDataDocumentIndex;
import com.wellsoft.pt.fulltext.index.FulltextDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.service.FulltextDocumentIndexService;
import com.wellsoft.pt.fulltext.support.DataFiller;
import com.wellsoft.pt.fulltext.support.FulltextSetting;
import com.wellsoft.pt.fulltext.utils.ElasticsearchClientUtil;
import com.wellsoft.pt.fulltext.vo.DeleteDocumentIndexVo;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Description:
 * es全文检索实现
 * <pre>
 * 修改记录:
 * 修改后版本    修改人     修改日期    修改内容
 * 1.0         baozh    2021/9/13   Create
 * </pre>
 */
@Service
public class FulltextDocumentIndexServiceImpl implements FulltextDocumentIndexService {

    public static final String DOC = "_document";
    private static final String ES_INDEX_TYPE = "es_index_type";
    private static final String ES_INDEX_PARENT_TYPE = "es_index_parent_type";
    private static final String ES_INDEX_PARENT_CODE = "es_index_parent_code";
    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    ScheduledExecutorService scheduledExecutorService;
    @Autowired
    private ElasticsearchClientUtil clientUtil;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    private AtomicInteger atomic = new AtomicInteger(0);

    @Autowired
    private FulltextSettingFacadeService fulltextSettingFacadeService;

    /**
     * 索引保存方法
     *
     * @return id
     * @author baozh
     * @date 2021/9/10 10:38
     * @params index
     */
    @Override
    public String saveIndex(FulltextDocumentIndex documentIndex) {
        //1、判断索引是否存在，如果不存在先创建
//        String categoryCode = documentIndex.getCategoryCode();
//        if (StringUtils.isBlank(categoryCode)) {
//            throw new RuntimeException("Elasticsearch保存失败,分类编码：" + categoryCode);
//        }

        //es索引名称
        // String indexName = categoryCode.toLowerCase().trim() + DOC;
        String indexName = documentIndex.getClass().getAnnotation(Document.class).indexName();
        try {
            createIndex(indexName, documentIndex);
            //2、将数据保存到所引
            //将数据设置为有效
            documentIndex.setIsDelete(0);
            if (StringUtils.isBlank(documentIndex.getSystem())) {
                documentIndex.setSystem(getSystemId());
            }
//            if (!clientUtil.exists(indexName)) {
//                logger.info("Elasticsearch索引不存在: " + indexName);
//            }
            return clientUtil.save(indexName, documentIndex);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Elasticsearch连接失败,索引名称: " + indexName);
            throw new RuntimeException("Elasticsearch连接失败,索引名称：" + indexName);
        }
    }

    private String getSystemId() {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        String systemId = (String) userDetails.getExtraData("system");
        if (StringUtils.isNotBlank(systemId)) {
            return systemId;
        }
        List<String> systemIds = SpringSecurityUtils.getAccessableSystem();
        return CollectionUtils.isNotEmpty(systemIds) ? systemIds.get(0) : null;
    }

    private void createIndex(String indexName, FulltextDocumentIndex documentIndex) {
        // String indexName = categoryCode + DOC;
        if (!clientUtil.exists(indexName)) {
            //CAS操作
            while (!atomic.compareAndSet(0, 1)) {
                //有线程在创建索引，再次判断索引是否存在
                if (clientUtil.exists(indexName)) {
                    logger.info(indexName + "索引存在");
                    return;
                }
            }
            try {
                //1.1、创建索引
                boolean indexFlag = clientUtil.create(indexName, ClassUtils.getUserClass(documentIndex));
                if (!indexFlag) {
                    throw new RuntimeException("Elasticsearch创建索引失败{} error:" + indexName);
                }
                logger.info("Elasticsearch创建索引成功,索引名称: " + indexName);
                // 1.2、将索引保存到字典表
                // saveIndexDataDictionary(categoryCode);
            } finally {
                atomic.set(0);
            }

        }
    }

    private void saveIndexDataDictionary(String categoryCode) {
        //检查数据字典是否存在，不存在则添加
        DataDictionary dataDictionary = dataDictionaryService.getByCodeWithInType(categoryCode, ES_INDEX_TYPE + "_" + categoryCode);
        if (dataDictionary == null) {
            DataDictionary dataDictionaryParent = dataDictionaryService.getByCodeWithInType(ES_INDEX_PARENT_CODE, ES_INDEX_PARENT_TYPE);
            if (dataDictionaryParent == null) {
                //创建父节点
                DataDictionaryBean dataDictionaryParentNew = new DataDictionaryBean();
                dataDictionaryParentNew.setType(ES_INDEX_PARENT_TYPE);
                dataDictionaryParentNew.setCode(ES_INDEX_PARENT_CODE);
                dataDictionaryParentNew.setName("ES索引表");
                String parentUuid = dataDictionaryService.saveBean(dataDictionaryParentNew);
                dataDictionaryParentNew.setUuid(parentUuid);
                dataDictionaryParent = dataDictionaryParentNew;
            }
            DataDictionaryBean dataDictionaryBean = new DataDictionaryBean();
            dataDictionaryBean.setParentUuid(dataDictionaryParent.getUuid());
            dataDictionaryBean.setCode(categoryCode);
            dataDictionaryBean.setName(categoryCode);
            dataDictionaryBean.setType(ES_INDEX_TYPE + "_" + categoryCode);
            dataDictionaryService.saveBean(dataDictionaryBean);
        }
    }

    /**
     * 全文检索方法
     *
     * @return
     * @author baozh
     * @date 2021/9/13 15:55
     * @params indexRequestParams 请求参数
     */
    @Override
    public QueryData query(IndexRequestParams indexRequestParams) {
        QueryData queryData = new QueryData();
        queryData.setPagingInfo(indexRequestParams.getPagingInfo());
        Set<String> indexNames = extractIndexNames(indexRequestParams);
        if (indexNames.contains("dms_file_document")) {
            Map<String, String> resultFieldMapping = indexRequestParams.getResultFieldMapping();
            if (resultFieldMapping == null) {
                resultFieldMapping = Maps.newHashMap();
                indexRequestParams.setResultFieldMapping(resultFieldMapping);
            }
            if (!resultFieldMapping.containsKey("attachment.content")) {
                resultFieldMapping.put("attachment.content", "content");
            }
        }
        queryData = clientUtil.searchDocument(indexRequestParams, FulltextDocumentIndex.class, indexNames.toArray(new String[]{}));
        return queryData;
    }

    @Override
    public Map<String, Long> countCategory(IndexRequestParams indexRequestParams) {
        Set<String> indexNames = extractIndexNames(indexRequestParams);
        indexRequestParams.getPagingInfo().setPageSize(0);
        Map<String, Long> map = clientUtil.countDocument(indexRequestParams, FulltextDocumentIndex.class, indexNames.toArray(new String[]{}));
        return map;
    }

    private Set<String> extractIndexNames(IndexRequestParams indexRequestParams) {
        Set<String> indexNames = Sets.newHashSet();
        String categoryCode = indexRequestParams.getCategoryCode();
        FulltextSetting fulltextSetting = fulltextSettingFacadeService.getSettingBySystem(RequestSystemContextPathResolver.system());
        Set<String> builtInKeys = fulltextSetting.getBuiltInKeys();
        String formDataIndexName = FormDataDocumentIndex.class.getAnnotation(Document.class).indexName();
        if (StringUtils.isBlank(categoryCode)) {
            indexNames.add(formDataIndexName);
            indexNames.addAll(builtInKeys.stream().map(key -> key + DOC).collect(Collectors.toList()));
        } else {
            String[] categoryCodes = StringUtils.split(categoryCode, Separator.SEMICOLON.getValue());
            for (String code : categoryCodes) {
                if (builtInKeys.contains(code)) {
                    indexNames.add(code + DOC);
                } else {
                    indexNames.add(formDataIndexName);
                }
            }
        }
        return indexNames;
    }

    @Override
    public boolean delete(DeleteDocumentIndexVo indexVo) {
        String indexName = indexVo.getIndexName();
        String id = indexVo.getUuid();
        if (StringUtils.isBlank(indexName)) {
            throw new RuntimeException("Elasticsearch删除失败,索引不能为空，索引ID:" + id);
        }
        FulltextDocumentIndex documentIndex = new FulltextDocumentIndex();
        documentIndex.setUuid(id);
        documentIndex.setIsDelete(1);
        // 先判断数据是否存在
        Map<String, Object> resultMap = clientUtil.queryById(indexName, documentIndex.getUuid());
        if (resultMap == null) {
            return true;
        }
        return clientUtil.update(indexName, documentIndex);
        // return clientUtil.delete(indexName, id);
    }

    /**
     * 根据记录字段值删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    @Override
    public boolean deleteByFieldEq(String indexName, String fieldName, Object fieldValue) {
        return clientUtil.deleteByFieldEq(indexName, fieldName, fieldValue);
    }

    /**
     * 根据记录字段值逻辑删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     */
    @Override
    public boolean logicDeleteByFieldEq(String indexName, String fieldName, Object fieldValue) {
        return clientUtil.logicDeleteByFieldEq(indexName, fieldName, fieldValue);
    }

    /**
     * 根据记录字段值恢复逻辑删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    @Override
    public boolean restoreDeleteByFieldEq(String indexName, String fieldName, Object fieldValue) {
        return clientUtil.restoreDeleteByFieldEq(indexName, fieldName, fieldValue);
    }

    /**
     * 更新索引
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/14 15:21
     */
    @Override
    public boolean updateIndex(FulltextDocumentIndex documentIndex) {
//        String categoryCode = documentIndex.getCategoryCode();
//        if (StringUtils.isBlank(documentIndex.getUuid())) {
//            throw new RuntimeException("uuid不能为空");
//        }
//        if (StringUtils.isBlank(categoryCode)) {
//            throw new RuntimeException("Elasticsearch更新失败,分类编码为空");
//        }
        String indexName = documentIndex.getClass().getAnnotation(Document.class).indexName();
        //先判断数据是否存在
        Map<String, Object> resultMap = clientUtil.queryById(indexName, documentIndex.getUuid());
        if (resultMap == null) {
            return false;
        }
        //es索引名称
        return clientUtil.update(indexName, documentIndex);
    }

    /**
     * 批量保存
     *
     * @param indexName 索引名称，documentIndexs 内容
     * @return
     * @author baozh
     * @date 2021/9/14 17:45
     */
    @Override
    public boolean batchSaveIndex(String indexName, List<FulltextDocumentIndex> documentIndexs) {
        if (CollectionUtils.isEmpty(documentIndexs)) {
            return true;
        }
        createIndex(indexName, documentIndexs.get(0));
        String systemId = getSystemId();
        documentIndexs.forEach(index -> {
            index.setIsDelete(0);
            if (StringUtils.isBlank(index.getSystem())) {
                index.setSystem(systemId);
            }
        });
        return clientUtil.batchSave(indexName, documentIndexs.toArray(new FulltextDocumentIndex[]{}));
    }

    @Override
    public Future<String> asyncDelete(DeleteDocumentIndexVo indexVo) {
        //判断是否启用全文检索
        if (!clientUtil.isEnable()) {
            return null;
        }
        Future<String> result = scheduledExecutorService.submit(() -> {
            String indexName = indexVo.getIndexName();
            String id = indexVo.getUuid();
            if (StringUtils.isBlank(indexName)) {
                return "false";
            }
            FulltextDocumentIndex documentIndex = new FulltextDocumentIndex();
            documentIndex.setUuid(id);
            documentIndex.setIsDelete(1);
            //先判断数据是否存在
            Map<String, Object> resultMap = clientUtil.queryById(indexName, documentIndex.getUuid());
            if (resultMap == null) {
                return "false";
            }
            return clientUtil.update(indexName, documentIndex) + "";
        });
        return result;
    }

    /**
     * 异步保存索引
     *
     * @param documentIndex
     * @return
     * @author baozh
     * @date 2021/9/14 19:01
     */
    @Override
    public Future<String> asyncSaveIndex(FulltextDocumentIndex documentIndex) {
        return asyncSaveIndex(documentIndex, null);
    }

    /**
     * 索引文档
     *
     * @param documentIndex
     * @param dataFiller
     * @return
     * @author baozh
     * @date 2021/9/14 18:49
     */
    @Override
    public Future<String> asyncSaveIndex(FulltextDocumentIndex documentIndex, DataFiller dataFiller) {
        //判断是否启用全文检索
        if (!clientUtil.isEnable()) {
            return null;
        }
        if (StringUtils.isBlank(documentIndex.getSystem())) {
            documentIndex.setSystem(getSystemId());
        }
        String indexName = documentIndex.getClass().getAnnotation(Document.class).indexName();
        Future<String> result = scheduledExecutorService.submit(() -> {
            if (dataFiller != null) {
                dataFiller.filling(documentIndex);
            }
            createIndex(indexName, documentIndex);
            documentIndex.setIsDelete(0);
            // String indexName = documentIndex.getCategoryCode() + DOC;
//            Map<String, Object> resultMap = clientUtil.queryById(indexName, documentIndex.getUuid());
//            if (resultMap == null) {
//                if (documentIndex.getCreateTime() == null) {
//                    documentIndex.setCreateTime(new Date());
//                }
            return clientUtil.save(indexName, documentIndex);
//            }
//            return clientUtil.update(indexName, documentIndex) + "";
        });
        return result;
    }

    /**
     * 异步批量保存
     *
     * @param indexName,documentIndexs
     * @return
     * @author baozh
     * @date 2021/9/14 19:01
     */
    @Override
    public void asyncBatchSaveIndex(String indexName, List<FulltextDocumentIndex> documentIndexs) {
        //判断是否启用全文检索
        if (!clientUtil.isEnable()) {
            return;
        }
        if (documentIndexs == null || documentIndexs.size() == 0) {
            return;
        }
        String systemId = getSystemId();
        documentIndexs.forEach(index -> {
            index.setIsDelete(0);
            if (StringUtils.isBlank(index.getSystem())) {
                index.setSystem(systemId);
            }
        });
        scheduledExecutorService.submit(() -> {
            createIndex(indexName, documentIndexs.get(0));
            clientUtil.batchSave(indexName + DOC, documentIndexs.toArray(new FulltextDocumentIndex[]{}));
        });
    }

    @Override
    public List<DataDictionary> getDataDictionaryList() {
        DataDictionary indexParent = dataDictionaryService.getByCodeWithInType(ES_INDEX_PARENT_CODE, ES_INDEX_PARENT_TYPE);
        if (indexParent == null) {
            throw new RuntimeException("数据字典ES索引表查询为空");
        }
        List<DataDictionary> dataDictionaryList = dataDictionaryService.getDataDictionariesByParentUuid(indexParent.getUuid());
        return dataDictionaryList;
    }

    @Override
    public Map<String, Long> distinctByField(String field, String... indexNames) {
        SearchRequest request = new SearchRequest(indexNames);
        SearchSourceBuilder source = new SearchSourceBuilder()
                .size(0)
                .aggregation(AggregationBuilders
                        .terms("distinct_field")
                        .field(field));
        request.source(source);
        SearchResponse response = clientUtil.search(request, RequestOptions.DEFAULT);

        Map<String, Long> resultMap = Maps.newHashMap();
        Terms terms = response.getAggregations().get("distinct_field");
        terms.getBuckets().forEach(bucket -> {
            resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
        });
        return resultMap;
    }

    @Override
    public Map<String, Long> distinctByFieldAndSystem(String field, String system, String... indexNames) {
        SearchRequest request = new SearchRequest(indexNames);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termQuery("system", system));
        SearchSourceBuilder source = new SearchSourceBuilder()
                .size(0)
                .query(boolQueryBuilder)
                .aggregation(AggregationBuilders
                        .terms("distinct_field")
                        .field(field));
        request.source(source);
        SearchResponse response = clientUtil.search(request, RequestOptions.DEFAULT);

        Map<String, Long> resultMap = Maps.newHashMap();
        Terms terms = response.getAggregations().get("distinct_field");
        terms.getBuckets().forEach(bucket -> {
            resultMap.put(bucket.getKeyAsString(), bucket.getDocCount());
        });
        return resultMap;
    }

    @Override
    public long countByFieldEq(String indexName, String fieldName, Object fieldValue) {
        SearchRequest request = new SearchRequest(indexName);
        SearchSourceBuilder source = new SearchSourceBuilder()
                .size(0);
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        boolQueryBuilder.filter(QueryBuilders.termQuery(fieldName, fieldValue));
        source.query(boolQueryBuilder);
        request.source(source);
        SearchResponse response = clientUtil.search(request, RequestOptions.DEFAULT);
        return response.getHits().getTotalHits().value;
    }

}
