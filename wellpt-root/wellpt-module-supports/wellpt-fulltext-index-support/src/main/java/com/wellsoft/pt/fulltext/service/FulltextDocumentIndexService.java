package com.wellsoft.pt.fulltext.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.fulltext.index.FulltextDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.support.DataFiller;
import com.wellsoft.pt.fulltext.vo.DeleteDocumentIndexVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public interface FulltextDocumentIndexService {

    /**
     * 同步保存索引
     *
     * @return
     * @author baozh
     * @date 2021/9/13 11:14
     * @params documentIndex
     */
    String saveIndex(FulltextDocumentIndex documentIndex);

    /**
     * 全文检索
     *
     * @return
     * @author baozh
     * @date 2021/9/13 11:14
     * @params indexRequestParams
     */
    QueryData query(IndexRequestParams indexRequestParams);

    /**
     * @param indexRequestParams
     * @return
     */
    Map<String, Long> countCategory(IndexRequestParams indexRequestParams);

    /**
     * 根据记录id删除索引记录
     *
     * @return
     * @author baozh
     * @date 2021/9/13 11:15
     * @params documentIndex
     */
    boolean delete(DeleteDocumentIndexVo documentIndex);

    /**
     * 根据记录字段值删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    boolean deleteByFieldEq(String indexName, String fieldName, Object fieldValue);

    /**
     * 根据记录字段值逻辑删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     */
    boolean logicDeleteByFieldEq(String indexName, String fieldName, Object fieldValue);

    /**
     * 根据记录字段值恢复逻辑删除索引记录
     *
     * @param indexName
     * @param fieldName
     * @param fieldValue
     * @return
     */
    boolean restoreDeleteByFieldEq(String indexName, String fieldName, Object fieldValue);

    /**
     * 同步更新索引
     *
     * @return
     * @author baozh
     * @date 2021/9/14 14:10
     * @params *@params
     */
    boolean updateIndex(FulltextDocumentIndex documentIndex);

    /**
     * 同步批量保存
     *
     * @return
     * @author baozh
     * @date 2021/9/14 17:49
     * @params *@params
     */
    boolean batchSaveIndex(String indexName, List<FulltextDocumentIndex> documentIndexs);

    /**
     * 索引文档
     *
     * @param documentIndex
     * @return
     * @author baozh
     * @date 2021/9/14 18:49
     */
    Future<String> asyncSaveIndex(FulltextDocumentIndex documentIndex);

    /**
     * 索引文档
     *
     * @param documentIndex
     * @param dataFiller
     * @return
     * @author baozh
     * @date 2021/9/14 18:49
     */
    Future<String> asyncSaveIndex(FulltextDocumentIndex documentIndex, DataFiller dataFiller);

    /**
     * 异步删除
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/10/21 14:54
     */
    Future<String> asyncDelete(DeleteDocumentIndexVo documentIndex);

    /**
     * 批量索引文档
     *
     * @param indexName,documentIndexs
     * @return
     * @author baozh
     * @date 2021/9/14 18:49
     */
    void asyncBatchSaveIndex(String indexName, List<FulltextDocumentIndex> documentIndexs);

    /**
     * 查询数据字典索引编码名称
     *
     * @param
     * @return
     * @author baozh
     * @date 2021/9/15 10:59
     */
    List<DataDictionary> getDataDictionaryList();

    /**
     * 按字段统计数量
     *
     * @param field
     * @param indexNames
     * @return
     */
    Map<String, Long> distinctByField(String field, String... indexNames);

    Map<String, Long> distinctByFieldAndSystem(String field, String system, String... indexNames);

    long countByFieldEq(String indexName, String fieldName, Object fieldValue);
}
