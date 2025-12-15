package com.wellsoft.pt.fulltext.service;

import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.pt.fulltext.index.BaseDocumentIndex;
import com.wellsoft.pt.fulltext.request.IndexRequestParams;
import com.wellsoft.pt.fulltext.support.DataFiller;
import com.wellsoft.pt.fulltext.vo.DeleteDocumentIndexVo;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

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
public interface DocumentIndexService<T extends BaseDocumentIndex> {

    /**
     * 索引文档
     *
     * @param index
     * @return
     */
    Future<String> asyncIndex(T index);

    /**
     * 索引文档
     *
     * @param index
     * @return
     */
    Future<String> asyncIndex(T index, DataFiller dataFiller);

    /**
     * 批量索引文档
     *
     * @param indexes
     */
    void asyncBulkIndex(List<T> indexes);


    /**
     * 异步索引文档
     *
     * @param index
     * @return
     */
    String index(T index);

    void bulkIndex(List<T> indexes);

    /**
     * 匹配查询文档
     *
     * @param params
     * @return
     */
    QueryData query(IndexRequestParams params);

    /**
     * 统计文档
     *
     * @param params
     * @return
     */
    long count(IndexRequestParams params);

    /**
     * 统计文档
     *
     * @param fieldName
     * @param fieldValue
     * @return
     */
    long countByFieldEq(String fieldName, Object fieldValue);

    void deleteIndex(DeleteDocumentIndexVo vo);

    /**
     * 删除索引文档
     *
     * @param fieldName
     * @param fieldValue
     */
    void deleteIndexByFieldEq(String fieldName, Object fieldValue);

    /**
     * 逻辑删除索引文档
     *
     * @param fieldName
     * @param fieldValue
     */
    void logicDeleteIndexByFieldEq(String fieldName, Object fieldValue);

    /**
     * 恢复逻辑删除的索引文档
     *
     * @param fieldName
     * @param fieldValue
     */
    void restoreDeleteIndexByFieldEq(String fieldName, Object fieldValue);

    Map<String, Long> distinctByField(String fieldName);

    Map<String, Long> distinctByFieldAndSystem(String fieldName, String system);
}
